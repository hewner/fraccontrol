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
		
		DrawTask root = new DrawTask(g, artist.getCurrentDesign(), artist.getSeed());
		thread = new PaintThread(root, this);
	}
	
	public void drawCurrentImage(Graphics g) {
			g.drawImage(image, 0,0,Color.BLACK, null);
	}
	
	private void addToTaskCache(DrawTask task) {
		LinkedList<DrawTask> list = designToTask.get(task.getDesign());
		if(list == null) {
			list = new LinkedList<DrawTask>();
			designToTask.put(task.getDesign(), list);
		}
		list.add(task);
	}

	public List<DrawTask> cachedInstancesOf(Design design) {
		return designToTask.get(design);
	}
	
	public boolean shouldDraw(DrawTask current) {
		double minScale = .5/(width > height ? height : width);
		return current.isInClipBounds() && current.getAbsoluteScale()*artist.getZoomLevel() >= minScale;
	}

	public PaintThread getThread() {
		return thread;
	}
	
	public void doDraw(DrawTask current) {
		if(!shouldDraw(current)) return;
		current.drawBackground();
		addToTaskCache(current);
		List<DrawTask> subtasks = current.getSubtasks(); 
		//System.out.println(subtasks.size() + " subtasks found.");
		for(DrawTask subTask : current.getSubtasks()) {
			
			thread.addTask(this, subTask);
		}
	}
}
