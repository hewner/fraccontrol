import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;


public class MouseControl implements MouseListener, MouseMotionListener {
	
	public Design previewDesign;
	public AffineTransform previewTransform;
	
	public void drawPreview(Graphics2D g, FractalPainter p) {
		
	}
	
	public void mouseClicked(MouseEvent e) {
		FractalComponent component = (FractalComponent) e.getSource();
		AffineTransform viewTrans = component.painter().viewTransform();
		//TODO right now we're making the assumption that the 
		//current selected design is also the outermost design
		try {
			Point2D localPoint = viewTrans.inverseTransform(e.getPoint(),null);
			component.currentDesign().addSubdesign("sub",localPoint);
			component.painter().redrawAll();
			component.repaint();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseDragged(MouseEvent e) {
		System.out.println("Drag");
		
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
