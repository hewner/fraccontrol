import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.*;

import de.hardcode.jxinput.event.JXInputAxisEvent;
import de.hardcode.jxinput.event.JXInputButtonEvent;


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
	//protected FractalComponent component;
	
	public ArtistState() {
		//this.component = component;
		templateNum = 0;
		rulemenuHidden = false;
		onMenuChange = new LinkedList<Runnable>();
		onViewTransformChange = new LinkedList<Runnable>();
		viewTransform = new AffineTransform();
		double unitLength = 500*.9;
		viewTransform.scale(unitLength, unitLength);
		viewTransform.translate(0.05, 0.05);
		zoomLevel=1.0;
	}
	
	public int getMenuColumn() {
		return menuColumn;
	}

	public void makeNewDesign() {
		Design newD = new Design(Color.GREEN,currentDesign.getTemplate());
		currentDesign = newD;
		notifyViewTransformChange();
		notifyMenuChange();
	}
	
	public void decrementCurrentDesignCategory() {
		DesignTemplate previous = null;
		for(DesignTemplate template : DesignTemplateLibrary.library().getTemplates()) {
			if(template == currentDesign.getTemplate()) {
				if(previous != null) {
					currentDesign = DesignTemplateLibrary.library().getDesignsForTemplate(previous).firstElement();
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
		for(Design design : DesignTemplateLibrary.library().getDesignsForTemplate(currentDesign.getTemplate())) {
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
		for(Design design : DesignTemplateLibrary.library().getDesignsForTemplate(currentDesign.getTemplate())) {
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
		for(DesignTemplate template : DesignTemplateLibrary.library().getTemplates()) {
			if(found) {
				currentDesign = DesignTemplateLibrary.library().getDesignsForTemplate(template).firstElement();
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
			zoomLevel = zoomLevel*(1+zoomFactor/10);
			//System.out.println("zoom=" +zoomLevel);
			viewTransform.scale(1+zoomFactor/10,1+zoomFactor/10);
			viewTransform.translate(-zoomFactor/(40*zoomLevel), -zoomFactor/(40*zoomLevel));
			notifyViewTransformChange();
			//System.out.println("shift="+zoomFactor/(50*zoomLevel));
			
		}
	}
	public double getZoomLevel() {
		return zoomLevel;
	}
	
	public void panViewTransform(double xPan, double yPan) {
		if(viewTransform != null) {
			viewTransform.translate(xPan, yPan);
			notifyViewTransformChange();
			
		}
	}
	
	
	public void incrementTemplate() {
		setCurrentTemplate(templateNum + 1);
	}
	
	public void decrementTemplate() {
		setCurrentTemplate(templateNum - 1);
	}
	
	public DesignTemplate getCurrentTemplate() {
		return DesignTemplateLibrary.library().getTemplates().get(getCurrentTemplateNum());
	}
	
	public int getTemplateCount() {
		return DesignTemplateLibrary.library().getTemplates().size();
	}
	
	public List<DesignTemplate> getTemplates() {
		return DesignTemplateLibrary.library().getTemplates();
	}
	
	public DesignBounds getPreview() {
		return preview;
	}
	
	private void updatePreviewForRadius(Point2D localPoint) {
		
		double dis = preview.getCenter().distance(localPoint);
		double scale = 2*dis/1.4142;
		
		preview.setScale(scale);
		preview.setRotation(Math.atan2(preview.getCenter().getY()-localPoint.getY(), preview.getCenter().getX()-localPoint.getX()));
		System.out.println(preview.getCenter());
	}
	
	public void updatePreview(Point2D center, Point2D radius) {
		System.out.println(center + "  " + radius);
		updatePreviewForRadius(radius);
		ensurePreviewDoesNotOverlap();
	}
	
	public void drawPreview(Graphics2D g, FractalPainter painter) {
		if(preview != null) {
			Graphics2D newG = (Graphics2D) g.create();
			newG.transform(viewTransform());
			preview.transformGraphics(newG);
			newG.setColor(Color.PINK);
			preview.draw(newG);
		}
	}
	
	public void startPreview(Point2D center) {
		preview = new DesignBounds(center, getCurrentTemplate());
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
	
}