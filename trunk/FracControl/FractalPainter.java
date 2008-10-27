import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class FractalPainter {
	protected BufferedImage image;
	protected Map<String, Rule> rules;
	protected String startRule;
	
	public class RenderingException extends Exception {
		public RenderingException(String error) {
			super("RenderingException: " + error);
		}
	}
	
	public FractalPainter()
	{
		rules = new HashMap<String,Rule>();
	}
	
	public void startDrawingWithSize(int width, int height) throws RenderingException {
		System.out.println("width " + width + " height " + height);
		if(width <= 0) throw new RenderingException("Width " + width);
		if(height <= 0) throw new RenderingException("Height " + height);
		if(startRule == null) throw new RenderingException("No start rule!");
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		startDrawing(graphics);
	}
	protected class Pair {
		public Graphics2D g;
		public Rule rule;
		public Pair(Graphics2D g, Rule rule) {
			this.g = g;
			this.rule = rule;
		}
	}
	
	protected LinkedList<Pair> toDraw;
	protected class PaintThread extends Thread {
		
		public boolean shouldStop = false;
		public PaintThread(Rule rule, Graphics2D g) {
			toDraw = new LinkedList<Pair>();
			toDraw.add(new Pair(g, rule));
		}
/*		public void drawSpiral(Graphics2D g2) {
			for(int i = 0; i < 1000; i++) {
				g2.fillRect(0, 0, 12, 20);
				g2.scale(.995, .995);
				g2.rotate(.05);
				g2.translate(12,0);
				if(i % 50 == 49) {
					Graphics2D newBranch = (Graphics2D) g2.create();
					newBranch.rotate(Math.PI);
					newBranch.translate(0, -20);
					toDraw.add(newBranch);
				}
			
			}			
		}*/
		public void shouldStop() {
			shouldStop = true;
		}
		public void run() {
			while(!toDraw.isEmpty() && !shouldStop) {
				Pair current = toDraw.remove();
				current.rule.doDraw(current.g);
			}		
		}
		
	}
	PaintThread thread;
	protected void startDrawing(Graphics2D g) throws RenderingException {
		if(thread != null) {			
			thread.shouldStop();
			while(thread.isAlive()) {} //ensure we have only 1 thread processing
		}
		g.setColor(Color.WHITE);
		g.translate(100, 100);
		
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHints(rh);
		
		thread = new PaintThread(getRule(startRule), g);
		thread.start();
	}
	
	public synchronized Rule getRule(String name) throws RenderingException {
		if(!rules.containsKey(name)) {
			throw new RenderingException("Reference to unknown rule " + name);
		}
		return rules.get(name);
	}
	
	public void drawCurrentImage(Graphics g) {
			g.drawImage(image, 0,0,Color.BLACK, null);
	}

	public void addRule(String name, Rule rule) {
		rule.setPainter(this);
		rules.put(name, rule);
	}

	public void setStartRule(String name) {
		startRule = name;
		
	}

	public static final int maxRules = 100;
	public synchronized void  addTask(String name, Graphics2D newG) throws RenderingException {
		if(toDraw.size() < maxRules) {
			toDraw.add(new Pair(newG, getRule(name)));
		} else {
			System.err.println("Exceeded toDraw max!");
		}
	}
}
