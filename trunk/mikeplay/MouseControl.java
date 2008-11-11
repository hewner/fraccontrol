import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class MouseControl implements MouseListener, MouseMotionListener {
	
	protected DesignBounds preview;
	protected ArtistState artist;

	public MouseControl(ArtistState artist) {
		this.artist = artist;
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
	
	public void mouseClicked(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) { }

	public void mouseDragged(MouseEvent e) {
		FractalComponent component = (FractalComponent) e.getSource();
		updatePreviewShape(e, component);
		component.currentDesign().transformSubdesign(preview);
		component.repaint();
	}

	private void updatePreviewShape(MouseEvent e, FractalComponent component) {
		AffineTransform viewTrans = component.painter().viewTransform();
		//TODO right now we're making the assumption that the 
		//current selected design is also the outermost design
		try {
			Point2D localPoint = viewTrans.inverseTransform(e.getPoint(),null);
			if(preview == null) {
				preview = new DesignBounds(localPoint, artist.getCurrentTemplate());
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

	public void mouseReleased(MouseEvent e) {
		FractalComponent component = (FractalComponent) e.getSource();
		updatePreviewShape(e, component);
		component.currentDesign().addSubdesign(preview);
		preview = null;
		try {
			component.painter().redrawAll();
		} catch (FractalPainter.RenderingException e1) {
			System.err.println("Rendering exception adding new subcomponent");
			e1.printStackTrace();
		}

	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
