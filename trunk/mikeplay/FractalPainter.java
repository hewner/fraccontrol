import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class FractalPainter {
	protected BufferedImage image;
	
	protected int width, height;
	protected ArtistState artist;
	protected Map<Design,LinkedList<DrawTask>> designToTask;
	PaintThread thread;
	
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
	
	protected class PaintThread implements Runnable {
		
		public boolean shouldStop = false;
		private LinkedList<DrawTask> toDraw;
		private Thread thread;
		
		public PaintThread(Design design, Graphics2D g, FractalPainter painter) {
			toDraw = new LinkedList<DrawTask>();
			addTask(new DrawTask(g, design, artist.getSeed()));
		}

		public synchronized void shouldStop() {
			shouldStop = true;
		}
		
		public void run() {
			int numberDrawn = 0;
			double minScale = .5/(width > height ? height : width);
			while(shouldRun()) {
				DrawTask current = removeTask();
				if(!current.isInClipBounds()) {
					//this shape is not on screen
					continue;
				}
				current.drawBackground();
				addToTaskCache(current);
				numberDrawn++;
				System.out.println(current.getSubtasks().size() + " subtasks found.");
				for(DrawTask subTask : current.getSubtasks()) {
					if(subTask.getAbsoluteScale()*artist.getZoomLevel() >= minScale) {
						addTask(subTask);
					} else {
						//System.out.println("Stopping recurse too small");
					}
				}
	
			}
			if(isEmpty()) {
				System.out.println(this + " finished drawing " + numberDrawn + " shapes.");
			}
		}
		
		public synchronized boolean isEmpty() {
			return toDraw.isEmpty();
		}
		
		public synchronized boolean shouldRun() {
			return !shouldStop && !isEmpty();
		}
		
		public synchronized void addAll(List<DrawTask> tasks) {
			if(toDraw.size() + tasks.size() < maxRules) {
				toDraw.addAll(tasks);
				if((thread == null || !thread.isAlive()) && !shouldStop) {
					thread = new Thread(this);
					thread.start();
				}
			} else {
				System.err.println("Exceeded toDraw max!");
			}
		}
		
		public synchronized void  addTask(DrawTask task) {
			if(toDraw.size() < maxRules) {
				toDraw.add(task);
				if((thread == null || !thread.isAlive()) && !shouldStop) {
					thread = new Thread(this);
					thread.start();
				}
			} else {
				System.err.println("Exceeded toDraw max!");
			}
		}
		
		public synchronized DrawTask removeTask() {
			return toDraw.remove();
		}
		
		public synchronized boolean isAlive() {
			return thread.isAlive();
		}
		
	}
	
	protected void transformView(AffineTransform trans) throws RenderingException {
		artist.viewTransform().preConcatenate(trans);
		redrawAll();
	}
	
	
	protected void startDrawing(Graphics2D g) throws RenderingException {
		if(thread != null) {			
			thread.shouldStop();
			while(thread.isAlive()) {} //ensure we have only 1 thread processing
		}
		designToTask = new HashMap<Design,LinkedList<DrawTask>>();
		g.setColor(Color.WHITE);
		g.transform(artist.viewTransform());
		
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHints(rh);
		
		thread = new PaintThread(artist.getCurrentDesign(), g, this);
	}
	
	public void drawCurrentImage(Graphics g) {
			g.drawImage(image, 0,0,Color.BLACK, null);
	}

	public static final int maxRules = 30000;
	
	
	private void addToTaskCache(DrawTask task) {
		LinkedList<DrawTask> list = designToTask.get(task.getDesign());
		if(list == null) {
			list = new LinkedList<DrawTask>();
			designToTask.put(task.getDesign(), list);
			//task.getDesign().addChangeListener(designChange);
		}
		list.add(task);
	}
	
	public boolean isTooSmall(Graphics2D g) {
		return(g.getTransform().getScaleX() < .25);
	}
	
}
