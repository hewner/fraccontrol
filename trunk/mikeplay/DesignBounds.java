import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class DesignBounds {
	protected AffineTransform trans;
	protected double scale, rotation;
	protected Point2D center;
	protected DesignTemplate template;

	public DesignBounds(Point2D center, double scale, double rotation, DesignTemplate t) {
		this.center = center;
		this.scale = scale;
		this.rotation = rotation;
		this.template = t;
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
			g.fill(template.getShape());
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
		Area area = new Area(template.getShape());
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

}
