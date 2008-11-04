import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;


public class Design {
	
	protected Color background;
	protected List<Subdesign> subDesigns;
	protected Area subDesignArea;
	protected DesignTemplate template;
	
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
	
	public Design(Color background, DesignTemplate t) {
		this.background = background;
		subDesigns = new LinkedList<Subdesign>();
		subDesignArea = new Area();
		template = t;
		DesignTemplateLibrary.library().addDesign(this);
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
	
	public void addSubdesign(DesignBounds shape) {
		setRightScale(shape);
		Subdesign sub = new Subdesign("sub", shape.transform());
		subDesignArea.add(shape.computeArea());
		subDesigns.add(sub);
	}
	
	public boolean fits(DesignBounds shape) {
		Area overall = new Area(new Rectangle2D.Double(0,0,1.0,1.0));
		Area area = shape.computeArea();
		area.subtract(overall);
		if(!area.isEmpty()) return false;
		area = shape.computeArea();
		area.intersect(subDesignArea);
		if(!area.isEmpty()) return false;
		return true;
	}
	
	public void setRightScale(DesignBounds shape) {
		
		if(fits(shape)) {
			return;
		}
		double smallestFailure = shape.getScale();
		double largestSuccess = 0;
		for(int i = 0; i < 10; i++) {
			double newScale = (smallestFailure + largestSuccess)/2;
			shape.setScale(newScale);
			if(fits(shape)) {
				largestSuccess = newScale;
			} else {
				smallestFailure = newScale;
			}
		}
		shape.setScale(largestSuccess);
	}
	
	public void transformSubdesign(DesignBounds design) {
		setRightScale(design);
	}
	
	public DesignTemplate getTemplate() {
		return template;
	}
}
