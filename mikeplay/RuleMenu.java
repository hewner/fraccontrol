import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;


public class RuleMenu extends JComponent {
	public static final int SPACING = 10;
	public static final int BOX_WIDTH =  80;
	public static final int BOX_HEIGHT = 80;
	protected ArtistState artist;
	public boolean menuHidden;
	
	public RuleMenu(ArtistState newArtist) {
		this.artist = newArtist;
		menuHidden = false;
		final RuleMenu parentThis = this;
		artist.onMenuChange( new Runnable() {
			
			public void run() {
				if(menuHidden != artist.isRuleMenuHidden()) {
					menuHidden = artist.isRuleMenuHidden();
					if(menuHidden) {
						Animation animation = new Animation(1000, new Point(0,600),parentThis);
						animation.startAnimation();
					} else {
						Animation animation = new Animation(1000, new Point(0,0),parentThis);
						animation.startAnimation();
					}
				}
				repaint();
			}
		});
	}
	
	public void setSelection(int i) {
		artist.setCurrentTemplate(i);
	}
	public void paintComponent(Graphics g) {
		int selection = artist.getCurrentTemplateNum();
		g.setColor(new Color((float).4,(float).4, (float) .4, (float) 1));
		g.fillRect(0, 0, BOX_WIDTH+2*SPACING,getHeight());		
		g.setColor(new Color((float).8,(float).8, (float) .8, (float) .5));
		g.fillRect(0, SPACING/2+(BOX_HEIGHT+SPACING)*selection, BOX_WIDTH+2*SPACING, BOX_HEIGHT+SPACING);
		g.setColor(new Color((float)0,(float)0, (float) 0, (float) 1));
		int i = 0;
		for(DesignTemplate template : artist.getTemplates()) {
			Graphics2D current = (Graphics2D) g.create();
			current.translate(SPACING, SPACING+(BOX_HEIGHT+SPACING)*i);
			current.scale(BOX_WIDTH, BOX_HEIGHT);
			current.setColor(new Color((float)0,(float)0, (float) 0, (float) 1));
			current.fillRect(0, 0, 1, 1);
			current.translate(0.05, .05);
			current.scale(0.9,0.9);
			current.setColor(Color.white);
			current.fill(template.getShape());
			current.drawString(template.getName(), 2*SPACING, 3*SPACING+(BOX_HEIGHT+SPACING)*i);
			i++;
		}
		
		
	}
	

}
