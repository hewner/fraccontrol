import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

class DrawTask {
	private Design design;
	private double absoluteScale;
	//private Color bigColor;
	//private Color smallColor;
	protected ColorScheme.ColorMode backgroundColor;
	
	private int uniqueName;
	private Vector<DrawTask> subtasks;
	protected DesignBounds sub;
	private AffineTransform transform;
	
	
	public DrawTask(Design design, int seed) {
		this.design = design;
		this.absoluteScale = 1; 
		this.uniqueName = seed;
		transform = new AffineTransform();
		setColorScheme(null);
	}
	
	public DrawTask(DesignBounds sub, DrawTask parent) {
		this.sub = sub;
		transform = new AffineTransform(sub.transform());
		AffineTransform oldT = parent.transform;
		transform.preConcatenate(oldT);
		setColorScheme(parent);
		this.uniqueName = (parent.uniqueName + "|" + sub.getDesignNumber()).hashCode();
		
		Vector<Design> possibleDesigns = sub.getTemplate().getDesigns();
		int num = Math.abs(uniqueName) % possibleDesigns.size();
		this.design = possibleDesigns.get(num);
		this.absoluteScale = parent.absoluteScale*sub.getScale();
	}

	public boolean setColorScheme(DrawTask parent) {
		ColorScheme.ColorMode oldScheme = backgroundColor;
		if(parent == null) {
			this.backgroundColor = ColorScheme.first();
		} else {
			if (parent.design.isBig(sub)) {
				this.backgroundColor = ColorScheme.getContrast(parent.backgroundColor);
			} else {
				this.backgroundColor = ColorScheme.getOppositeContrast(parent.backgroundColor);
			}
		}		
		return oldScheme != backgroundColor;
	}
	
	public void drawBackground(Graphics2D g, ColorScheme scheme) {
		Graphics2D myG = (Graphics2D) g.create();
		myG.transform(transform);
		myG.setColor(scheme.color(backgroundColor));
		design.getTemplate().drawFillShape(myG);
		if(!ColorScheme.isContrast(backgroundColor)) {
			myG.setColor(scheme.color(ColorScheme.getOpposite(backgroundColor)));
			myG.setStroke(new BasicStroke((float) .005));
			design.getTemplate().drawLineShape(myG);
		}
	}
	
	public void addSubtaskAfterward(DrawTask sub) {
		if(subtasks != null) {
			subtasks.add(sub);
		}
	}
	
	public DrawTask removeSubtaskAfterward(DesignBounds bounds) {
		if(subtasks != null) {
			Iterator<DrawTask> i = subtasks.iterator();
			while(i.hasNext()) {
				DrawTask subtask = i.next();
				if(subtask.sub == bounds) {
					i.remove();
					return subtask;
				}
			}
			throw new RuntimeException("DesignBounds not found in task");
		}
		return null;
	}
	
	public Vector<DrawTask> getSubtasks() {
		if(subtasks != null) return subtasks;
		subtasks = new Vector<DrawTask>();
		for(DesignBounds bounds : design.getSubdesigns()) {
			subtasks.add(new DrawTask(bounds,this));
		}
		return subtasks;
	}
	
	public boolean isInClipBounds(Graphics2D g) {
		Graphics2D myG = (Graphics2D) g.create();
		myG.transform(transform);
		return design.getTemplate().getBounds().intersects(myG.getClipBounds());
	}
	
	public double getAbsoluteArea() {
		return absoluteScale*design.getTemplate().getScaleMultiplier();
	}

	public Design getDesign() {
		return design;
	}
	
}