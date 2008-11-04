import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;


public class RuleMenu extends JComponent implements KeyListener {
	public static final int SPACING = 10;
	public static final int BOX_WIDTH = 180;
	public static final int BOX_HEIGHT = 80;
	protected int selection = 0;

	public RuleMenu() {
		addKeyListener(this);
	}
	
	public void setSelection(int i) {
		selection = i;
	}
	public void paintComponent(Graphics g) {
		g.setColor(new Color((float).4,(float).4, (float) .4, (float) .9));
		g.fillRect(0, 0, getWidth(),getHeight());		
		g.setColor(new Color((float).8,(float).8, (float) .8, (float) .5));
		g.fillRect(0, SPACING/2+(BOX_HEIGHT+SPACING)*selection, getWidth(), BOX_HEIGHT+SPACING);
		g.setColor(new Color((float)0,(float)0, (float) 0, (float) 1));
		int i = 0;
		for(DesignTemplate template : DesignTemplateLibrary.library().getTemplates()) {
			g.setColor(new Color((float)0,(float)0, (float) 0, (float) 1));
			g.fillRect(SPACING, SPACING+(BOX_HEIGHT+SPACING)*i, BOX_WIDTH, BOX_HEIGHT);
			g.setColor(Color.white);
			g.drawString(template.getName(), 2*SPACING, 3*SPACING+(BOX_HEIGHT+SPACING)*i);
			i++;
		}

	}
	
	public int maxSelection() {
		return DesignTemplateLibrary.library().getTemplates().size() - 1;
		
	}
	
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void keyTyped(KeyEvent e) {
		if(e.getKeyChar() == 'a' && selection > 0) {
			selection--;
			repaint();
		}
		if(e.getKeyChar() == 'z' && selection < maxSelection()) {
			selection++;
			repaint();
		}
		if(e.getKeyChar() == 'q') {
			Animation animation = new Animation(1000, new Point(0,600),this);
			animation.startAnimation();
		}
		if(e.getKeyChar() == 'w') {
			Animation animation = new Animation(1000, new Point(0,0),this);
			animation.startAnimation();
		}		

	}
}
