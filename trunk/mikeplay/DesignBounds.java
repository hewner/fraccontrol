import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;


public class DesignBounds implements Serializable, Comparable {

	private static final long serialVersionUID = -8416997179686702686L;
	protected AffineTransform trans;
	protected double scale, rotation;
	protected Point2D center;
	protected DesignTemplate template;
	protected long designNumber;
	
	public DesignBounds(Point2D center, double scale, double rotation, DesignTemplate t) {
		this.center = center;
		this.scale = scale;
		this.rotation = rotation;
		this.template = t;
		this.designNumber = getNewDesignNumber();
	}
	
	public long getDesignNumber() {
		return designNumber;
	}
	
	public DesignTemplate getTemplate() {
		return template;
	}
	
	public DesignBounds(Point2D center, DesignTemplate t) {
		this(center,0,0,t);
	}
	
	public void transformGraphics(Graphics2D g) {
		g.transform(transform());
	}
	
	public void draw(Graphics2D g) {
		if(scale != 0) {
			template.drawFillShape(g);
		}
	}
	
	public void setScale(double scale) {
		this.scale = scale;
		trans = null;
	}
	
	public void setRotation(double rot) {
		this.rotation = rot;
		trans = null;
	}
	
	public Point2D getCenter() {
		return center;
	}
	
	public void setCenter(Point2D pos) {
		this.center = pos;
		trans = null;
	}
	
	public AffineTransform transform() {
		if(trans == null) {
			trans = new AffineTransform();
			trans.translate(center.getX(), center.getY());
			trans.rotate(Math.PI*3/4 + rotation);
			trans.translate(width()/-2, height()/-2);
			trans.scale(scale,scale);
		}
		return trans;
	}
	
	public Area computeArea() {
		Area area = template.getArea();
		area.transform(transform());
		return area;
	}
	
	public double width() {
		return scale;
	}
	
	public double height() {
		return scale;
	}

	public double getScale() {
		return scale;
	}

	public int compareTo(Object o) {
		DesignBounds other = (DesignBounds) o;
		return Double.compare(scale, other.scale);
	}

	private static long currentDesignNumber = 0;
	private static long getNewDesignNumber() {
		return currentDesignNumber++;
	}
	
}
