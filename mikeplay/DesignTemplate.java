import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Vector;


public class DesignTemplate implements Serializable{

	private static final long serialVersionUID = -954256991033058432L;
	protected String name;
	protected Shape shape;
	private double shapeScaleFactor;
	protected Vector<Design> designs;
	
	public DesignTemplate(String name, Shape shape, double shapeScaleFactor) {
		this.name = name;
		this.shape = shape;
		this.shapeScaleFactor = shapeScaleFactor;
		designs = new Vector<Design>();
	}

	public String getName() {
		return name;
	}
	
	public Vector<Design> getDesigns() {
		return designs;
	}
	
	public void addDesign(Design design) {
		designs.add(design);
	}
	
	public Area getArea() {
		Area area = new Area(shape);
		AffineTransform trans = new AffineTransform();
		Rectangle2D rect = shape.getBounds2D();
		trans.scale(1/rect.getWidth(), 1/rect.getHeight());
		area.transform(trans);
		return area;
	}
	
	private Shape getShape() {
		return shape;
	}

	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(0,0,1,1);
	}
	
	public double getShapeScaleFactor() {
		return shapeScaleFactor;
	}

	public void drawLineShape(Graphics2D g) {
		Rectangle2D rect = shape.getBounds2D();
		Graphics2D newG = (Graphics2D) g.create();
		newG.scale(1/rect.getWidth(), 1/rect.getHeight());
		newG.draw(shape);
	}
	
	public void drawFillShape(Graphics2D g) {
		Rectangle2D rect = shape.getBounds2D();
		Graphics2D newG = (Graphics2D) g.create();
		newG.scale(1/rect.getWidth(), 1/rect.getHeight());
		newG.fill(shape);
	}
	
}
