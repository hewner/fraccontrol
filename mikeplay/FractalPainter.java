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


public class FractalPainter implements FractalModification {
	protected BufferedImage image;
	protected Graphics2D g;
	
	protected int width, height;
	protected ArtistState artist;
	protected Map<Design,LinkedList<DrawTask>> designToTask;
	PaintThread thread;
	
	private AffineTransform previewedTransform;
	private BufferedImage previewedOriginal;
	private BufferedImage newImage;
	private long renderTimer;
	
	
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
		renderTimer = System.currentTimeMillis();
		newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = newImage.createGraphics();
		graphics.setClip(0,0,width,height);
		startDrawing(graphics);
	}
	
	public void redrawAll() throws RenderingException {
		startDrawingWithSize(width,height);
	}
	
	protected void startDrawing(Graphics2D g) throws RenderingException {
		if(thread != null) {			
			thread.shouldStop();
			while(thread.isAlive()) {} //ensure we have only 1 thread processing
		}
		this.g = g;
		designToTask = new HashMap<Design,LinkedList<DrawTask>>();
		g.setColor(Color.WHITE);
		g.transform(artist.viewTransform());
		
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHints(rh);
		
		DrawTask root = new DrawTask(artist.getCurrentDesign(), artist.getSeed());
		thread = new PaintThread(root, this);
	}
	
	public void previewTransform(AffineTransform transform) {
		BufferedImage preview = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = preview.createGraphics();
		if(previewedOriginal != null) {
			previewedTransform.concatenate(transform);
		} else {
			previewedTransform = transform;
			previewedOriginal = image;
		}
		g.transform(previewedTransform);
		g.drawImage(previewedOriginal, 0,0,Color.BLACK, null);
		image = preview;
	}
	
	public void drawCurrentImage(Graphics g) {
		if(newImage != null) {
			long millisElasped = System.currentTimeMillis() - renderTimer;
			System.out.println(millisElasped);
			if(millisElasped > 100) {
				image = newImage;
				previewedOriginal = null;
                previewedTransform = null;
				newImage = null;
			}
		}
		g.drawImage(image, 0,0,Color.BLACK, null);
	}
	
	private synchronized void addToTaskCache(DrawTask task) {
		LinkedList<DrawTask> list = designToTask.get(task.getDesign());
		if(list == null) {
			list = new LinkedList<DrawTask>();
			designToTask.put(task.getDesign(), list);
		}
		list.add(task);
	}

	public synchronized List<DrawTask> cachedInstancesOf(Design design) {
		return (List<DrawTask>) designToTask.get(design).clone();
	}
	
	public boolean shouldDraw(Graphics2D g, DrawTask current) {
		double minScale = 1.0/(width > height ? height : width);
		return current.isInClipBounds(g) && current.getAbsoluteArea()*artist.getZoomLevel() >= minScale;
	}

	public PaintThread getThread() {
		return thread;
	}
	
	public Graphics2D getGraphics() {
		return g;
	}
	
	public void doDraw(DrawTask current) {
		if(!shouldDraw(g, current)) return;
		current.drawBackground(g);
		addToTaskCache(current);
		List<DrawTask> subtasks = current.getSubtasks(); 
		//System.out.println(subtasks.size() + " subtasks found.");
		for(DrawTask subTask : current.getSubtasks()) {
			
			thread.addTask(this, subTask);
		}
	}
}
