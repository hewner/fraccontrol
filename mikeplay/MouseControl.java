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
	
	public AffineTransform previewTrans;
	public Point2D initialPoint;
	
	public void drawPreview(Graphics2D g, FractalPainter painter) {
		if(previewTrans != null) {
			Graphics2D newG = (Graphics2D) g.create();
			g.transform(painter.viewTransform());
			g.transform(previewTrans);
			g.setColor(Color.PINK);
			g.fill(new Rectangle2D.Double(0,0,1.0,1.0));
		}
	}
	
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseDragged(MouseEvent e) {
		FractalComponent component = (FractalComponent) e.getSource();
		AffineTransform viewTrans = component.painter().viewTransform();
		//TODO right now we're making the assumption that the 
		//current selected design is also the outermost design
		try {
			Point2D localPoint = viewTrans.inverseTransform(e.getPoint(),null);
			if(initialPoint == null) {
				initialPoint = localPoint;
				return;
			}
			double dis = initialPoint.distance(localPoint);
			double scale = 2*dis/1.4142;
			previewTrans = new AffineTransform();
			
			previewTrans.translate(initialPoint.getX(), initialPoint.getY());
			previewTrans.rotate(Math.PI*3/4 + Math.atan2(initialPoint.getY()-localPoint.getY(), initialPoint.getX()-localPoint.getX()));
			previewTrans.translate(scale/-2, scale/-2);
			previewTrans.scale(scale, scale);
						
			component.currentDesign().transformSubdesign(new Rectangle2D.Double(0,0,1.0,1.0),previewTrans);
			//component.currentDesign().addSubdesign("sub",localPoint);
			//component.painter().redrawAll();
			component.repaint();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
