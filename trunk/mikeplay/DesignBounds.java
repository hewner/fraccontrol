import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;


public class DesignBounds implements Serializable {

	private static final long serialVersionUID = -8416997179686702686L;
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
			
			g.scale(.1,.1);
			g.setColor(Color.black);
			g. drawLine((int) center.getX(), (int) center.getY(), (int) template.getShape().getBounds().getWidth() , (int) template.getShape().getBounds().getHeight());
			g.scale(10,10);
			
			
			
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
