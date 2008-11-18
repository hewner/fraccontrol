import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class Design implements Serializable {
	
	private static final long serialVersionUID = -1428638605128121687L;
	protected Color background;
	protected List<DesignBounds> subDesigns;
	protected transient Area subDesignArea;
	protected DesignTemplate template;
	
	public Design(Color background, DesignTemplate t) {
		this.background = background;
		subDesigns = new LinkedList<DesignBounds>();
		subDesignArea = new Area();
		template = t;
		t.getLibrary().addDesign(this);
	}
	
	public List<DesignBounds> getSubdesigns() {
		return subDesigns;
	}
	
	public void drawBackground(Graphics2D g) {
		g.setColor(background);
		Shape rect = template.getShape(); 
		if(!rect.intersects(g.getClipBounds())) {
			//this shape is not on screen
			return;
		}
		g.fill(rect);
	}
	
	public void addSubdesign(DesignBounds shape) {
		setRightScale(shape);
		subDesignArea.add(shape.computeArea());
		subDesigns.add(shape);
	}
	
	protected Area subDesignArea() {
		if(subDesignArea == null) {
			subDesignArea = new Area();
			for(DesignBounds sub : subDesigns) {
				subDesignArea.add(sub.computeArea());
			}
		}
		return subDesignArea;
	}
	
	public boolean fits(DesignBounds shape) {
		Area overall = new Area(template.getShape());
		Area area = shape.computeArea();
		area.subtract(overall);
		if(!area.isEmpty()) return false;
		area = shape.computeArea();
		area.intersect(subDesignArea());
		if(!area.isEmpty()) return false;
		return true;
	}
	
	public void setRightScale(DesignBounds shape) {
		
		if(fits(shape)) {
			return;
		}
		double smallestFailure = shape.getScale();
		double largestSuccess = 0;
		for(int i = 0; i < 20; i++) {
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
