import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class FractalPainter {
	protected BufferedImage image;
	protected Map<String, Design> designs;
	protected String startDesign;
	
	protected int width, height;
	protected ArtistState artist;
	
	public class RenderingException extends Exception {
		public RenderingException(String error) {
			super("RenderingException: " + error);
		}
	}
	
	public FractalPainter(int width, int height, ArtistState artist)
	{
		this.artist = artist;
		designs = new HashMap<String,Design>();
		this.width = width;
		this.height = height;
	}
	
	public void startDrawing() throws RenderingException {
		startDrawingWithSize(width, height);
	}
	
	public void startDrawingWithSize(int width, int height) throws RenderingException {
		System.out.println("width " + width + " height " + height);
		if(width <= 0) throw new RenderingException("Width " + width);
		if(height <= 0) throw new RenderingException("Height " + height);
		if(startDesign == null) throw new RenderingException("No start rule!");
		this.width = width;
		this.height = height;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setClip(0,0,width,height);
		startDrawing(graphics);
	}
	
	public void redrawAll() throws RenderingException {
		startDrawingWithSize(width,height);
	}
	
	protected class Pair {
		public Graphics2D g;
		public Design design;
		public Pair(Graphics2D g, Design design) {
			this.g = g;
			this.design = design;
		}
	}
	
	protected LinkedList<Pair> toDraw;
	protected class PaintThread extends Thread {
		
		public boolean shouldStop = false;
		public FractalPainter painter;
		
		public PaintThread(Design design, Graphics2D g, FractalPainter painter) {
			toDraw = new LinkedList<Pair>();
			toDraw.add(new Pair(g, design));
			this.painter = painter;
		}

		public void shouldStop() {
			shouldStop = true;
		}
		public void run() {
			int numberDrawn = 0;
			while(!toDraw.isEmpty() && !shouldStop) {
				Pair current = toDraw.remove();
				if(false && isTooSmall(current.g)) {
					System.out.println("Stopping recurse.  Too small.");
				} else {
					current.design.draw(current.g,painter);
					numberDrawn++;
				}
			}
			if(!shouldStop) {
				System.out.println("Finished drawing " + numberDrawn + " shapes.");
			}
		}
		
	}
	PaintThread thread;
	
	protected void transformView(AffineTransform trans) throws RenderingException {
		artist.viewTransform().preConcatenate(trans);
		redrawAll();
	}
	
	protected void startDrawing(Graphics2D g) throws RenderingException {
		if(thread != null) {			
			thread.shouldStop();
			while(thread.isAlive()) {} //ensure we have only 1 thread processing
		}
		g.setColor(Color.WHITE);
		g.transform(artist.viewTransform());
		
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHints(rh);
		
		thread = new PaintThread(getDesign(startDesign), g, this);
		thread.start();
	}
	
	public synchronized Design getDesign(String name) throws RenderingException {
		if(!designs.containsKey(name)) {
			throw new RenderingException("Reference to unknown design " + name);
		}
		return designs.get(name);
	}
	
	public void drawCurrentImage(Graphics g) {
			g.drawImage(image, 0,0,Color.BLACK, null);
	}

	public void addDesign(String name, Design design) {
		designs.put(name, design);
	}

	public void setStartRule(String name) {
		startDesign = name;
		
	}

	public static final int maxRules = 10000;
	public synchronized void  addTask(Design design, Graphics2D newG) {
		if(toDraw.size() < maxRules) {
			toDraw.add(new Pair(newG, design));
		} else {
			System.err.println("Exceeded toDraw max!");
		}
	}
	
	public boolean isTooSmall(Graphics2D g) {
		return(g.getTransform().getScaleX() < .25);
	}
	
}
