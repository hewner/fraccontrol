import java.awt.*;
import java.util.List;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

//import org.apache.batik.svggen.SVGGraphics2D;
//import org.apache.batik.dom.GenericDOMImplementation;
//import org.apache.batik.svggen.SVGGraphics2D;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;



public class ArtistState {

	protected AffineTransform viewTransform;
	protected DesignBounds preview;
	protected boolean rulemenuHidden;
	protected List<ActionListener> libraryListeners;
	protected List<Runnable> onViewTransformChange;
	protected Design currentDesign;
	protected DesignTemplate currentTemplate;
	protected Double zoomLevel;
	protected DesignTemplateLibrary library;
	protected int componentHeight, componentWidth;
	protected Point2D previewRadius;
	protected int seed;
	protected FractalPainter painter;

	
	public void newSeed() {
		Random random = new Random();
		seed = random.nextInt();
		notifyViewTransformChange();
	}
	
	public ArtistState() {
		rulemenuHidden = false;
		libraryListeners = new LinkedList<ActionListener>();
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

	public void outputToSVG(File filename) {
/*		
		        // Get a DOMImplementation.
		 DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

		        // Create an instance of org.w3c.dom.Document.
		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);

		// Create an instance of the SVG Generator.
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		svgGenerator.scale(500, 500);
		LinkedList<DrawTask> toDraw = new LinkedList<DrawTask>();
		toDraw.add(new DrawTask(currentDesign, seed));
		double minScale = 1.0/300;
		System.out.println("Starting output");
		while(!toDraw.isEmpty()) {
			DrawTask current = toDraw.remove();
			current.drawBackground(svgGenerator);
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
			FileOutputStream file = new FileOutputStream(filename);
			Writer out = new OutputStreamWriter(file, "UTF-8");
			svgGenerator.stream(out, useCSS);
			System.out.println("Wrote file");
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	public void makeNewDesign(DesignTemplate template) {
		Design newD = template.addDesign();
		currentDesign = newD;
		notifyLibraryChange();
		notifyViewTransformChange();		
	}
	
	public void onViewTransformChange(Runnable callback) {
		onViewTransformChange.add(callback);
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
			//new ZoomModification(painter, zoom);
			AffineTransform previewZoom = new AffineTransform();
			previewZoom.translate(componentWidth/2,componentHeight/2);
			previewZoom.scale(zoomFactor,zoomFactor);
			previewZoom.translate(-componentWidth/2,-componentHeight/2);
			painter.previewTransform(previewZoom);
			
			
			AffineTransform zoom = new AffineTransform();
			zoom.translate(crosshairFractalCoordinates.getX(),crosshairFractalCoordinates.getY());
			
			zoom.scale(zoomFactor,zoomFactor);

			zoom.translate(-crosshairFractalCoordinates.getX(),-crosshairFractalCoordinates.getY());
			
			viewTransform.concatenate(zoom);
			
			notifyViewTransformChange();
			//System.out.println("shift="+zoomFactor/(50*zoomLevel));
			
		}
	}
	public double getZoomLevel() {
		return zoomLevel;
	}
	
	public void panViewTransform(double xPan, double yPan) {
		if(viewTransform != null) {
			AffineTransform previewTransform = new AffineTransform();
			previewTransform.translate((xPan/zoomLevel)*viewTransform.getScaleX(), (yPan/zoomLevel)*viewTransform.getScaleY());
			painter.previewTransform(previewTransform);
			viewTransform.translate(xPan/zoomLevel, yPan/zoomLevel);
			notifyViewTransformChange();
		}
	}
	
	public DesignTemplate getCurrentTemplate() {
		return currentTemplate;
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
	
	public Point2D getPreviewRadius() {
		return previewRadius;
	}
		
	public void updatePreview(Point2D center, Point2D radius) {
		preview.setToMaxSize(center, radius, currentDesign);
		previewRadius = radius;
	}
	
	public void startPreview(Point2D center) {
		preview = new DesignBounds(center, getCurrentTemplate());
		previewRadius = center;
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
	
	public void addLibraryListener(ActionListener l) {
		libraryListeners.add(l);
	}
	public void removeLibraryListener(ActionListener l) {
		libraryListeners.remove(l);
	}
	
	public void notifyLibraryChange() {
		ActionEvent e = new ActionEvent(this, 0,"Library changed");
		for(ActionListener l: libraryListeners) {
			l.actionPerformed(e);
		}
	}
	
	public void writeToFile(File file) {
		
		StateToSave toSave = new StateToSave();
		toSave.library = library();
		toSave.zoomLevel = zoomLevel;
		toSave.viewTransform = viewTransform;
		toSave.currentDesign = currentDesign;
		toSave.currentTemplate = currentTemplate;
		toSave.seed = seed;
		
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		
		try {
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(toSave);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readFromFile(File file) {
		FileInputStream fos = null;
		ObjectInputStream out = null;
		StateToSave save;
		try {
			fos = new FileInputStream(file);
			out = new ObjectInputStream(fos);
			save = (StateToSave) out.readObject();
			out.close();

			zoomLevel = save.zoomLevel;
			viewTransform = save.viewTransform;
			currentDesign = save.currentDesign;
			currentTemplate = save.currentTemplate;
			seed = save.seed;
			library = save.library;
			notifyLibraryChange();
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

	public void setCurrentTemplate(DesignTemplate template) {
		currentTemplate = template;
	}
	
}
