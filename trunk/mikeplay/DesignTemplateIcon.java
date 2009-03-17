import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;


public class DesignTemplateIcon implements Icon {

	private DesignTemplate template;
	private Design design;
	private int size;
	
	public DesignTemplateIcon(DesignTemplate template, int size) {
		if(template == null) {
			System.out.println("Die");
		}
		this.template = template;
		this.size = size;
	}
	
	public DesignTemplateIcon(Design design, int size) {
		this.design = design;
		this.size = size;
		this.template = design.template;
	}

	public int getIconHeight() {
		return size;
	}

	public int getIconWidth() {
		return size;
	}

	public void paintIcon(Component comp, Graphics g, int x, int y) {
		Graphics2D newG = (Graphics2D) g.create();
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
		RenderingHints.VALUE_ANTIALIAS_ON);
		newG.setRenderingHints(rh);
		newG.translate(x, y);
		newG.scale(size, size);
		newG.translate(.05, .05);
		newG.scale(.9,.9);
		newG.setColor(Color.darkGray);
		newG.setStroke(new BasicStroke((float) .005));
		template.drawFillShape(newG);
		newG.setColor(Color.lightGray);
		template.drawLineShape(newG);
		if(design != null) {
			for(DesignBounds sub : design.getSubdesigns()) {
				Graphics2D temp = (Graphics2D) newG.create();
				sub.transformGraphics(temp);
				sub.draw(temp);
			}
		}
		
	}

}
