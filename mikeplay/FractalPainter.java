import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class FractalPainter {
	protected BufferedImage image;
	
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
		this.width = width;
		this.height = height;
	}
	
	public void startDrawing() throws RenderingException {
		startDrawingWithSize(width, height);
	}
	
	public void startDrawingWithSize(int width, int height) throws RenderingException {
		//System.out.println("width " + width + " height " + height);
		if(width <= 0) throw new RenderingException("Width " + width);
		if(height <= 0) throw new RenderingException("Height " + height);
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
		
	protected LinkedList<DrawTask> toDraw;
	protected class PaintThread extends Thread {
		
		public boolean shouldStop = false;
		public FractalPainter painter;
		
		public PaintThread(Design design, Graphics2D g, FractalPainter painter) {
			toDraw = new LinkedList<DrawTask>();
			addTask(new DrawTask(g, design, 1, Color.white));
			this.painter = painter;
		}

		public void shouldStop() {
			shouldStop = true;
		}
		public void run() {
			int numberDrawn = 0;
			double minScale = .5/(width > height ? height : width);
			while(!toDraw.isEmpty() && !shouldStop) {
				DrawTask current = toDraw.remove();
				Shape background = current.design.getTemplate().getShape();
				if(!background.intersects(current.g.getClipBounds())) {
					//this shape is not on screen
					continue;
				}
				current.g.setStroke(new BasicStroke((float) .005));
				current.g.setColor(current.backgroundColor);
				current.g.fill(background);
				if(current.bigColor == current.backgroundColor) {
					current.g.setColor(current.smallColor);
					current.g.draw(background);
				}
				numberDrawn++;
				for(DesignBounds sub : current.design.getSubdesigns()) {
					Graphics2D newG = (Graphics2D) current.g.create();
					AffineTransform newT = new AffineTransform(sub.transform());
					AffineTransform oldT = newG.getTransform();
					newT.preConcatenate(oldT);
					newG.setTransform(newT);
					newG.getTransform().preConcatenate(sub.transform());
						
					Design subDesign = artist.library().getRandomDesign(sub.getTemplate());
					double newScale = sub.getScale()*current.absoluteScale*artist.getZoomLevel();
					if(newScale >= minScale) {
						DrawTask task = new DrawTask(newG, artist.library().getRandomDesign(sub.getTemplate()), sub, current);
						addTask(task);
					} else {
						//System.out.println("Stopping recurse too small");
					}

				}
				
			}
			if(!shouldStop) {
				//System.out.println("Finished drawing " + numberDrawn + " shapes.");
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
		
		thread = new PaintThread(artist.getCurrentDesign(), g, this);
		thread.start();
	}
	
	public void drawCurrentImage(Graphics g) {
			g.drawImage(image, 0,0,Color.BLACK, null);
	}

	public static final int maxRules = 30000;
	public synchronized void  addTask(DrawTask task) {
		if(toDraw.size() < maxRules) {
			toDraw.add(task);
		} else {
			System.err.println("Exceeded toDraw max!");
		}
	}
	
	public boolean isTooSmall(Graphics2D g) {
		return(g.getTransform().getScaleX() < .25);
	}
	
}
