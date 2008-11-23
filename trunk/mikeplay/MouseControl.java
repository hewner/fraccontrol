
import java.awt.event.*;
import java.awt.geom.*;


public class MouseControl implements MouseListener, MouseMotionListener, KeyListener  {
	
	
	protected ArtistState artist;

	public MouseControl(ArtistState artist) {
		this.artist = artist;
	}
	
	public void mouseClicked(MouseEvent e) {
		Point2D localPoint = localPoint(e);
		DesignBounds subDesign = artist.getCurrentDesign().subDesignUnder(localPoint);
		if(subDesign != null) {
			if(e.getClickCount() == 2) {
				artist.getCurrentDesign().removeSubdesign(subDesign);
				artist.notifyViewTransformChange();
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
			System.out.println("artist.updatePreview(newCenter,  localPoint);");
			artist.updatePreview(artist.getPreview().getCenter(),  localPoint);
		}
		component.repaint();
	}

	

	public void mouseReleased(MouseEvent e) {
		FractalComponent component = (FractalComponent) e.getSource();
		Point2D localPoint = localPoint(e);
		artist.updatePreview(artist.getPreview().getCenter(), localPoint);
		artist.getCurrentDesign().addSubdesign(artist.getPreview());
		artist.setPreview(null);
		try {
			component.painter().redrawAll();
		} catch (FractalPainter.RenderingException e1) {
			System.err.println("Rendering exception adding new subcomponent");
			e1.printStackTrace();
		}
		artist.notifyViewTransformChange();

	}

	public void mousePressed(MouseEvent e) {
		artist.startPreview(localPoint(e));
	}

	public void mouseMoved(MouseEvent e) { }
	
	public void keyPressed(KeyEvent e) { }
	
	public void keyReleased(KeyEvent e) { }
	
	public void keyTyped(KeyEvent e) {
	
		if(e.getKeyChar() == 'a') {
			if(artist.getMenuColumn() == 0) {
				artist.decrementCurrentDesignCategory();
			}
			if(artist.getMenuColumn() == 1) {
				artist.decrementCurrentDesign();
			}
			if(artist.getMenuColumn() == 2) {
				artist.decrementTemplate();
			}
		}
		if(e.getKeyChar() == 'i') {
			artist.makeNewDesign();
		}
		if(e.getKeyChar() == 'z') {
			if(artist.getMenuColumn() == 0) {
				artist.incrementCurrentDesignCategory();
			}
			if(artist.getMenuColumn() == 1) {
				artist.incrementCurrentDesign();
			}
			if(artist.getMenuColumn() == 2) {
				artist.incrementTemplate();
			}		
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
		if(e.getKeyChar() == 'x') {
			artist.zoomViewTransform(1.05);
		}
		if(e.getKeyChar() == 'c') {
			artist.zoomViewTransform(0.95);
		}
		if(e.getKeyChar() == 'v') {
			artist.resetZoomState();
		}
		if(e.getKeyChar() == 'p') {
			artist.setMenuColumn(artist.getMenuColumn() + 1);
		}
		if(e.getKeyChar() == 'o') {
			artist.setMenuColumn(artist.getMenuColumn() - 1);
		}
		if(e.getKeyChar() == 'u') {
			artist.writeToFile();
		}
		if(e.getKeyChar() == 'y') {
			artist.readFromFile();
		}

	}

}
