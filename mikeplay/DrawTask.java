import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

class DrawTask {
	public Graphics2D g;
	public Design design;
	public double absoluteScale;
	public Color bigColor;
	public Color smallColor;
	public Color backgroundColor;
	public static Color lBlue = new Color((float) .1,(float).1,(float).5);
	public static Color dBlue = new Color((float) .0,(float).0,(float).2);
	public DrawTask(Graphics2D g, Design design, double absoluteScale, Color color) {
		this.g = g;
		this.design = design;
		this.absoluteScale = absoluteScale;
		this.bigColor = Color.white;
		this.smallColor = lBlue;
		this.backgroundColor = bigColor;
		
	}

	public DrawTask(Graphics2D g, Design design, DesignBounds sub, DrawTask parent) {
		this.g = g;
		this.design = design;
		this.absoluteScale = parent.absoluteScale*sub.getScale();
		if (parent.design.isBig(sub)) {
			this.bigColor = parent.bigColor;
			this.smallColor = parent.smallColor;
		} else {
			this.bigColor = parent.smallColor;
			this.smallColor = parent.bigColor;
		}
		if(parent.backgroundColor != parent.bigColor) {
			double logColor = 1 + Math.log(absoluteScale)/10;
			//this.color = new Color((float) (1 - logColor), (float) 1, (float) logColor, (float) 1);
			this.backgroundColor = this.bigColor;
		} else {
			if(parent.bigColor == lBlue) {
				this.backgroundColor = dBlue;
			} else {
				this.backgroundColor = Color.black;
			}
			
		}
	}
}
