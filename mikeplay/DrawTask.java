import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Random;
import java.util.Vector;

class DrawTask {
	private Design design;
	private double absoluteScale;
	private Color bigColor;
	private Color smallColor;
	private Color backgroundColor;
	private static Color lBlue = new Color((float) .1,(float).1,(float).5);
	private static Color dBlue = new Color((float) .0,(float).0,(float).2);
	private int uniqueName;
	private Vector<DrawTask> subtasks;
	private DesignBounds sub;
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
		Color oldBigColor = bigColor;
		if(parent == null) {
			this.bigColor = Color.white;
			this.smallColor = lBlue;
			this.backgroundColor = bigColor;
		} else {
			if (parent.design.isBig(sub)) {
				this.bigColor = parent.bigColor;
				this.smallColor = parent.smallColor;
			} else {
				this.bigColor = parent.smallColor;
				this.smallColor = parent.bigColor;
			}
			if(parent.backgroundColor != parent.bigColor) {
				//double logColor = 1 + Math.log(absoluteScale)/10;
				//this.color = new Color((float) (1 - logColor), (float) 1, (float) logColor, (float) 1);
				this.backgroundColor = this.bigColor;
			} else {
				if(bigColor == lBlue) {
					this.backgroundColor = dBlue;
				} else {
				this.backgroundColor = Color.black;
				}
			
			}
		}
		return oldBigColor != bigColor;		
	}
	
	public void drawBackground(Graphics2D g) {
		Graphics2D myG = (Graphics2D) g.create();
		myG.transform(transform);
		myG.setStroke(new BasicStroke((float) .005));
		myG.setColor(backgroundColor);
		design.getTemplate().drawFillShape(myG);
		if(bigColor == backgroundColor) {
			myG.setColor(smallColor);
			design.getTemplate().drawLineShape(myG);
		}			
	}
	
	public void addSubtaskAfterward(DrawTask sub) {
		if(subtasks != null) {
			subtasks.add(sub);
		}
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