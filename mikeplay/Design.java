import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;


public class Design {
	
	Color background;
	List<Subdesign> subDesigns;
	
	protected class Subdesign {
		public String name;
		public AffineTransform trans;
		public Subdesign(String name, AffineTransform trans) {
			this.name = name;
			this.trans = trans;
		}
	}
	
	public static Shape rect = new Rectangle2D.Double(0,0,1.0,1.0);
	public Shape getShadow() {
		return rect;
	}
	
	public Design(Color background) {
		this.background = background;
		subDesigns = new LinkedList<Subdesign>();
	}
	
	public void draw(Graphics2D g, FractalPainter painter) {
		g.setColor(background);
		Shape rect = getShadow(); 
		if(!rect.intersects(g.getClipBounds())) {
			//this shape is not on screen
			return;
		}
		g.fill(rect);
		Graphics2D newG;
		for(Subdesign sub : subDesigns) {
			newG = (Graphics2D) g.create();
			AffineTransform newT = new AffineTransform(sub.trans);
			AffineTransform oldT = newG.getTransform();
			newT.preConcatenate(oldT);
			newG.setTransform(newT);
			newG.getTransform().preConcatenate(sub.trans);
			try {
				painter.addTask(sub.name, newG);
			} catch (FractalPainter.RenderingException e) {
				System.err.println("Caught rendering exception for task: " + sub.name);
				System.err.println("Attempting to continue...");
			}
			
		}
	}
	
	public void addSubdesign(String name, AffineTransform trans) {
		subDesigns.add(new Subdesign(name, trans));
	}

	public void addSubdesign(String string, Point2D localPoint) {
		AffineTransform transform = new AffineTransform();
		transform.translate(localPoint.getX(), localPoint.getY());
		transform.scale(0.5, 0.5);
		addSubdesign(string,transform);
		
	}
	
	public void transformSubdesign(Shape shape, AffineTransform trans) {
		//AffineTransform transform = new AffineTransform();
		//transform.translate(localPoint.getX(), localPoint.getY());
		//transform.scale(0.5, 0.5);
		//return tra;
	}
}
