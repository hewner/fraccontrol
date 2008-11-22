import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;


public class DesignTemplate implements Serializable{

	private static final long serialVersionUID = -954256991033058432L;
	protected String name;
	protected Shape shape;
	protected DesignTemplateLibrary library;
	private double shapeScaleFactor;
	
	public DesignTemplate(String name, Shape shape, DesignTemplateLibrary library, double shapeScaleFactor) {
		this.name = name;
		this.shape = shape;
		this.library = library;
		this.shapeScaleFactor = shapeScaleFactor;
		library.addTemplate(this);
	}

	public String getName() {
		return name;
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

	public DesignTemplateLibrary getLibrary() {
		return library;
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
