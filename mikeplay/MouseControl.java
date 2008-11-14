import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class MouseControl implements MouseListener, MouseMotionListener, KeyListener  {
	
	
	protected ArtistState artist;

	public MouseControl(ArtistState artist) {
		this.artist = artist;
	}
	
	public void mouseClicked(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) { }

	
	public void mouseDragged(MouseEvent e) {
		FractalComponent component = (FractalComponent) e.getSource();
		Point2D localPoint = artist.pointInFractalCoordinates(e.getPoint());
		artist.updatePreview(artist.getPreview().getCenter(),  localPoint);
		artist.ensurePreviewDoesNotOverlap();
		component.repaint();
	}

	

	public void mouseReleased(MouseEvent e) {
		FractalComponent component = (FractalComponent) e.getSource();
		Point2D localPoint = artist.pointInFractalCoordinates(e.getPoint());
		artist.updatePreview(artist.getPreview().getCenter(), localPoint);
		artist.getCurrentDesign().addSubdesign(artist.getPreview());
		artist.setPreview(null);
		try {
			component.painter().redrawAll();
		} catch (FractalPainter.RenderingException e1) {
			System.err.println("Rendering exception adding new subcomponent");
			e1.printStackTrace();
		}

	}

	public void mousePressed(MouseEvent e) {
		artist.startPreview(artist.pointInFractalCoordinates(e.getPoint()));
	}

	public void mouseMoved(MouseEvent e) { }
	
	public void keyPressed(KeyEvent e) { }
	
	public void keyReleased(KeyEvent e) { }
	
	public void keyTyped(KeyEvent e) {
	
		if(e.getKeyChar() == 'a') {
			artist.decrementTemplate();
		}
		if(e.getKeyChar() == 'z') {
			artist.incrementTemplate();
		}
		if(e.getKeyChar() == 'q') {
			artist.toggleRuleMenu();
		}
		if(e.getKeyChar() == 'l') {
			artist.panViewTransform(-.05, 0);
		}
		if(e.getKeyChar() == 'h') {
			artist.panViewTransform(0.05, 0);
		}
		if(e.getKeyChar() == 'j') {
			artist.panViewTransform(0, -.05);
		}
		if(e.getKeyChar() == 'k') {
			artist.panViewTransform(0, .05);
		}

	}

}
