
import java.awt.event.*;
import java.awt.geom.*;


public class MouseControl implements MouseListener, MouseMotionListener, KeyListener  {
	
	
	protected ArtistState artist;
	protected FractalPainter painter;

	public MouseControl(ArtistState artist, FractalPainter painter) {
		this.artist = artist;
		this.painter = painter;
	}
	
	public void mouseClicked(MouseEvent e) {
		Point2D localPoint = localPoint(e);
		DesignBounds subDesign = artist.getCurrentDesign().subDesignUnder(localPoint);
		if(subDesign != null) {
			if(e.getClickCount() == 2) {
				new RemoveModification(artist.getCurrentDesign(), subDesign,painter);
				//artist.getCurrentDesign().removeSubdesign(subDesign);
				//artist.notifyViewTransformChange();
			}
		} else {
			//click on unfilled area
		}
	}
	
	
	private Point2D localPoint(MouseEvent e) {
		return artist.pointInFractalCoordinates(e.getPoint());
	}

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) { }

	public Point2D moveStart;
	public void mouseDragged(MouseEvent e) {
		FractalComponent component = (FractalComponent) e.getSource();
		Point2D localPoint = localPoint(e);
		if(e.isShiftDown()) {
			if(moveStart == null) {
				moveStart = localPoint;
			} else {
				double deltaX = localPoint.getX() - moveStart.getX();
				double deltaY = localPoint.getY() - moveStart.getY();
				Point2D newCenter = new Point2D.Double(
						artist.getPreview().getCenter().getX() + deltaX,
						artist.getPreview().getCenter().getY() + deltaY);
				moveStart = localPoint;
				if(newCenter.getX() > 1 || newCenter.getX() < 0 || newCenter.getY() > 1 || newCenter.getY() < 0) {
					return;
				}
				
				artist.updatePreview(newCenter,  localPoint);
				System.out.println("Center " + newCenter);
			}
		} else {
			moveStart = null;
			artist.updatePreview(artist.getPreview().getCenter(),  localPoint);
		}
		component.repaint();
	}

	

	public void mouseReleased(MouseEvent e) {
		FractalComponent component = (FractalComponent) e.getSource();
		Point2D localPoint = localPoint(e);
		artist.updatePreview(artist.getPreview().getCenter(), localPoint);
		if(localPoint.distance(artist.getPreview().getCenter()) > .0001) {
			AddModification foo = new AddModification(artist.getCurrentDesign(),artist.getPreview(),painter);
			//artist.getCurrentDesign().addSubdesign(artist.getPreview());
			artist.setPreview(null);
			component.repaint();	
		}
		
		//artist.notifyViewTransformChange();
	}

	public void mousePressed(MouseEvent e) {
		artist.startPreview(localPoint(e));
	}

	public void mouseMoved(MouseEvent e) { }
	
	public void keyPressed(KeyEvent e) { }
	
	public void keyReleased(KeyEvent e) { }
	
	public void keyTyped(KeyEvent e) {
	
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
		if(e.getKeyChar() == 'x') {
			artist.zoomViewTransform(1.05);
		}
		if(e.getKeyChar() == 'c') {
			artist.zoomViewTransform(0.95);
		}
		if(e.getKeyChar() == 'v') {
			artist.resetZoomState();
		}
		if(e.getKeyChar() == 'n') {
			artist.newSeed();
		}
	}

}
