import java.awt.*;
import java.util.List;
import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;

import de.hardcode.jxinput.event.JXInputAxisEvent;
import de.hardcode.jxinput.event.JXInputButtonEvent;
//import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;



public class ArtistState {

	protected int templateNum;
	protected AffineTransform viewTransform;
	protected DesignBounds preview;
	protected boolean rulemenuHidden;
	protected List<Runnable> onMenuChange;
	protected List<Runnable> onViewTransformChange;
	protected Design currentDesign;
	protected Double zoomLevel;
	protected int menuColumn = 0;
	protected DesignTemplateLibrary library;
	protected int componentHeight, componentWidth;
	protected Point2D previewRadius;
	protected int seed;
	
	
	public void newSeed() {
		Random random = new Random();
		seed = random.nextInt();
		notifyViewTransformChange();
	}
	
	public ArtistState() {
		templateNum = 0;
		rulemenuHidden = false;
		onMenuChange = new LinkedList<Runnable>();
		onViewTransformChange = new LinkedList<Runnable>();
		resetZoomState();
		library = new DesignTemplateLibrary();
		newSeed();
	}

	public void resetZoomState() {
		viewTransform = new AffineTransform();
		double unitLength = 500*.9;
		viewTransform.scale(unitLength, unitLength);
		viewTransform.translate(0.05, 0.05);
		zoomLevel=1.0;
		notifyViewTransformChange();
	}
	
	public DesignTemplateLibrary library() {
		return library;
	}

	

	
	public int getMenuColumn() {
		return menuColumn;
	}

	public void outputToSVG() {
		
		        // Get a DOMImplementation.
		 DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

		        // Create an instance of org.w3c.dom.Document.
		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);

		// Create an instance of the SVG Generator.
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		svgGenerator.scale(500, 500);
		LinkedList<DrawTask> toDraw = new LinkedList<DrawTask>();
		toDraw.add(new DrawTask(svgGenerator,currentDesign, seed));
		double minScale = 1.0/300;
		System.out.println("Starting output");
		while(!toDraw.isEmpty()) {
			DrawTask current = toDraw.remove();
			current.drawBackground();
			for(DrawTask subTask : current.getSubtasks()) {
				if(subTask.getAbsoluteArea() >= minScale) {
					System.out.println(subTask.getAbsoluteArea());
					toDraw.add(subTask);
				} else {
					//System.out.println("Stopping recurse too small");
				}

			}
			
		}

		
		// Finally, stream out SVG to the standard output using
		// UTF-8 encoding.
		boolean useCSS = true; // we want to use CSS style attributes
		try {
			FileOutputStream file = new FileOutputStream("mytest.svg");
			Writer out = new OutputStreamWriter(file, "UTF-8");
			svgGenerator.stream(out, useCSS);
			System.out.println("Wrote file");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void makeNewDesign() {
		Design newD = new Design(Color.GREEN,currentDesign.getTemplate());
		currentDesign = newD;
		notifyViewTransformChange();
		notifyMenuChange();
	}
	
	public void decrementCurrentDesignCategory() {
		DesignTemplate previous = null;
		for(DesignTemplate template : library().getTemplates()) {
			if(template == currentDesign.getTemplate()) {
				if(previous != null) {
					currentDesign = previous.getDesigns().firstElement();
					notifyViewTransformChange();
					notifyMenuChange();
				}
				return;
			}
			previous = template;
		}
	}

	public void decrementCurrentDesign() {
		Design previous = null;
		for(Design design : currentDesign.getTemplate().getDesigns()) {
			if(design == currentDesign) {
				if(previous != null) {
					currentDesign = previous;
					notifyViewTransformChange();
					notifyMenuChange();
				}
				return;
			}
			previous = design;
		}		
	}
	
	public void incrementCurrentDesign() {
		boolean found = false;
		for(Design design : currentDesign.getTemplate().getDesigns()) {
			if(found) {
				currentDesign = design;
				notifyViewTransformChange();
				notifyMenuChange();
				return;
			}
			if(design == currentDesign) {
				found = true;
			}
		}
	}

	
	public void incrementCurrentDesignCategory() {
		boolean found = false;
		for(DesignTemplate template : library().getTemplates()) {
			if(found) {
				currentDesign = template.getDesigns().firstElement();
				notifyViewTransformChange();
				notifyMenuChange();
				return;
			}
			if(template == currentDesign.getTemplate()) {
				found = true;
			}
		}
	}
	
	public void setMenuColumn(int column) {
		if(column >= 0 && column < 3) {
			menuColumn = column;
			notifyMenuChange();
		}
	}
	
	public void onMenuChange(Runnable callback) {
		onMenuChange.add(callback);
	}
	
	public void onViewTransformChange(Runnable callback) {
		onViewTransformChange.add(callback);
	}
	public void notifyMenuChange() {
		for(Runnable callback : onMenuChange) {
			callback.run();
		}
		
	}
	public void notifyViewTransformChange() {
		for(Runnable callback : onViewTransformChange) {
			callback.run();
		}		
	}
	
	public void setCurrentDesign(Design d) {
		currentDesign = d;
		notifyViewTransformChange();
	}
	
	public boolean isRuleMenuHidden() {
		return rulemenuHidden;
	}
	
	public void toggleRuleMenu() {
		rulemenuHidden = !rulemenuHidden;
		notifyMenuChange();
	}
	
	public void setCurrentTemplate(int template) {
		if(template < getTemplateCount() &&
			template >= 0) {
			templateNum = template;
			notifyMenuChange();
		}
	}
	
	public int getCurrentTemplateNum() {
		return templateNum;
	}
	
	public AffineTransform getViewTransform() {
		return viewTransform;
	}
	
	public AffineTransform viewTransform() {
		return viewTransform;
	}
	
	public void zoomViewTransform(double zoomFactor) {
		if(viewTransform != null) {
			zoomLevel = zoomLevel*(zoomFactor);
			//System.out.println("zoomlevel=" +zoomLevel);
			Point2D crosshair = new Point2D.Double(componentWidth/2,componentHeight/2);
			Point2D crosshairFractalCoordinates = pointInFractalCoordinates(crosshair);
			
			viewTransform.translate(crosshairFractalCoordinates.getX(),crosshairFractalCoordinates.getY());
			viewTransform.scale(zoomFactor,zoomFactor);
			viewTransform.translate(-crosshairFractalCoordinates.getX(),-crosshairFractalCoordinates.getY());
			notifyViewTransformChange();
			//System.out.println("shift="+zoomFactor/(50*zoomLevel));
			
		}
	}
	public double getZoomLevel() {
		return zoomLevel;
	}
	
	public void panViewTransform(double xPan, double yPan) {
		if(viewTransform != null) {
			viewTransform.translate(xPan/zoomLevel, yPan/zoomLevel);
			notifyViewTransformChange();
			//System.out.println("shift="+xPan);
		}
	}
	
	
	public void incrementTemplate() {
		setCurrentTemplate(templateNum + 1);
	}
	
	public void decrementTemplate() {
		setCurrentTemplate(templateNum - 1);
	}
	
	public DesignTemplate getCurrentTemplate() {
		return library().getTemplates().get(getCurrentTemplateNum());
	}
	
	public int getTemplateCount() {
		return library().getTemplates().size();
	}
	
	public List<DesignTemplate> getTemplates() {
		return library().getTemplates();
	}
	
	public DesignBounds getPreview() {
		return preview;
	}
	
	private void updatePreviewForRadius(Point2D localPoint) {
		
		double dis = preview.getCenter().distance(localPoint);
		double scale =  preview.getTemplate().getShapeScaleFactor()*dis;    //2*dis/1.4142;
		
		preview.setScale(scale);
		preview.setRotation(Math.atan2(preview.getCenter().getY()-localPoint.getY(), preview.getCenter().getX()-localPoint.getX()));
	}
	
	public void updatePreview(Point2D center, Point2D radius) {
		preview.setCenter(center);
		updatePreviewForRadius(radius);
		ensurePreviewDoesNotOverlap();
		previewRadius = radius;
	}
	
	public void drawPreview(Graphics2D g, FractalPainter painter) {
		if(preview != null) {
			Graphics2D newG = (Graphics2D) g.create();
			AffineTransform tran = viewTransform();
			Point2D radius = tran.transform(previewRadius,null);
			Shape shape = new Ellipse2D.Double(radius.getX() - 5, radius.getY()-5, 10, 10);
			newG.setColor(Color.GREEN);
			newG.fill(shape);
			
			newG.transform(viewTransform());
			preview.transformGraphics(newG);
			newG.setColor(Color.PINK);
			preview.draw(newG);
		}
	}
	
	public void startPreview(Point2D center) {
		preview = new DesignBounds(center, getCurrentTemplate());
		previewRadius = center;
	}

	public void ensurePreviewDoesNotOverlap() {
		currentDesign.transformSubdesign(getPreview());
	}
	
	public Point2D pointInFractalCoordinates(Point2D point) {
		 
		Point2D localPoint = null;
		try {
			localPoint = viewTransform().inverseTransform(point,null);
		} catch (NoninvertibleTransformException e) {
			System.err.println("Error converting point to local coordinates");
			e.printStackTrace();
		}
		
		return localPoint;
	}

	public void setPreview(DesignBounds dB) {
		preview = dB;
		
	}


	public Design getCurrentDesign() {
		return currentDesign;
	}
	
	public void writeToFile() {
		
		StateToSave toSave = new StateToSave();
		toSave.library = library();
		toSave.zoomLevel = zoomLevel;
		toSave.viewTransform = viewTransform;
		toSave.currentDesign = currentDesign;
		toSave.seed = seed;
		
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		
		try {
			fos = new FileOutputStream("test.dat");
			out = new ObjectOutputStream(fos);
			out.writeObject(toSave);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readFromFile() {
		FileInputStream fos = null;
		ObjectInputStream out = null;
		StateToSave save;
		try {
			fos = new FileInputStream("test.dat");
			out = new ObjectInputStream(fos);
			save = (StateToSave) out.readObject();
			out.close();
			library = save.library;
			zoomLevel = save.zoomLevel;
			viewTransform = save.viewTransform;
			currentDesign = save.currentDesign;
			seed = save.seed;
			templateNum = 0;
			notifyMenuChange();
			notifyViewTransformChange();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	public void setSize(int width, int height) {
		this.componentWidth = width;
		this.componentHeight = height;
		
	}

	public int getSeed() {
		return seed;
	}
	
}
