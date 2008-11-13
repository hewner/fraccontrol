import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.*;

import de.hardcode.jxinput.event.JXInputAxisEvent;
import de.hardcode.jxinput.event.JXInputButtonEvent;


public class ArtistState {
	protected int templateNum;
	protected AffineTransform viewTransform;
	protected DesignBounds preview;
	protected FractalComponent component;
	
	public ArtistState() {
		//this.component = component;
		templateNum = 0;
	}
	
	public void setCurrentTemplate(int template) {
		templateNum = template;
	}
	
	public int getCurrentTemplateNum() {
		return templateNum;
	}
	
	public AffineTransform getViewTransform() {
		return viewTransform;
	}
	
	public AffineTransform viewTransform(double unitLength) {
		if(viewTransform == null) {
			viewTransform = new AffineTransform();
			viewTransform.scale(unitLength, unitLength);
			viewTransform.translate(0.05, 0.05);
		}
		return viewTransform;
	}
	
	public void zoomViewTransform(double zoomFactor) {
		if(viewTransform != null) {
			viewTransform.scale(zoomFactor, zoomFactor);
			//System.out.println("zomming"+zoomFactor);
		}
	}
	
	public void panViewTransform(double xPan, double yPan) {
		if(viewTransform != null) {
			viewTransform.translate(xPan, yPan);
			
		}
	}
	
	
	public void incrementTemplate() {
		if(templateNum != getTemplateCount() - 1) {
			templateNum++;
		}
	}
	
	public void decrementTemplate() {
		if(templateNum != 0) {
			templateNum--;
		}
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
	
	public void drawPreview(Graphics2D g, FractalPainter painter) {
		if(preview != null) {
			Graphics2D newG = (Graphics2D) g.create();
			newG.transform(painter.viewTransform());
			preview.transformGraphics(newG);
			newG.setColor(Color.PINK);
			preview.draw(newG);
		}
	}
	public void updatePreviewShape(MouseEvent e, FractalComponent component) {
		AffineTransform viewTrans = component.painter().viewTransform();
		//TODO right now we're making the assumption that the 
		//current selected design is also the outermost design
		try {
			Point2D localPoint = viewTrans.inverseTransform(e.getPoint(),null);
			if(preview == null) {
				preview = new DesignBounds(localPoint, getCurrentTemplate());
				return;
			}
			double dis = preview.getCenter().distance(localPoint);
			double scale = 2*dis/1.4142;
			preview.setScale(scale);
			preview.setRotation(Math.atan2(preview.getCenter().getY()-localPoint.getY(), preview.getCenter().getX()-localPoint.getX())); 			
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			preview = null;
		}
	}

	public void setPreview(DesignBounds dB) {
		preview = dB;
		
	}

	public void updatePreviewShapeGame(JXInputAxisEvent e, FractalComponent component) {
		AffineTransform viewTrans = component.painter().viewTransform();
		//TODO right now we're making the assumption that the 
		//current selected design is also the outermost design
		try {
			Point2D localPoint = viewTrans.inverseTransform(getCrosshair(component),null);
			if(preview == null) {
				preview = new DesignBounds(localPoint, getCurrentTemplate());
				return;
			}
			double dis = e.getAxis().getValue();
			double scale = 2*dis/1.4142;
			preview.setScale(scale);
			//preview.setRotation(Math.atan2(preview.getCenter().getY()-localPoint.getY(), preview.getCenter().getX()-localPoint.getX())); 			
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			preview = null;
		}
		
	}
	
	public void updatePreviewShapeGameButton(JXInputButtonEvent e, FractalComponent component) {
		AffineTransform viewTrans = component.painter().viewTransform();
		//TODO right now we're making the assumption that the 
		//current selected design is also the outermost design
		try {
			Point2D localPoint = viewTrans.inverseTransform(getCrosshair(component),null);
			if(preview == null) {
				preview = new DesignBounds(localPoint, getCurrentTemplate());
				return;
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			preview = null;
		}
		
	}
	
	private Point getCrosshair(FractalComponent component) {
		int width = component.getWidth();
		int height = component.getHeight();
				
		Point center = new Point(width/2, height/2);
		
		return center; 
	}

	public void updatePreviewShapeGameRotate(JXInputAxisEvent ev, FractalComponent component) {
		AffineTransform viewTrans = component.painter().viewTransform();
		//TODO right now we're making the assumption that the 
		//current selected design is also the outermost design
		try {
			Point2D localPoint = viewTrans.inverseTransform(getCrosshair(component),null);
			if(preview == null) {
				preview = new DesignBounds(localPoint, getCurrentTemplate());
				return;
			}
			
			preview.setRotation(ev.getDelta()); 			
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			preview = null;
		}
		
	}
	
}
