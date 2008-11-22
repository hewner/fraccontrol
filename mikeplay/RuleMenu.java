import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComponent;


public class RuleMenu extends JComponent {

	protected ArtistState artist;
	
	public RuleMenu(ArtistState newArtist) {
		this.artist = newArtist;
		menuHidden = false;
	}

	public void setSelection(int i) {
		artist.setCurrentTemplate(i);
	}
	
	public int numberOfChoices(int column) {
		if(column == 0 || column == 2)
			return artist.getTemplateCount();
		if(column == 1)
			return getDesigns().size();
		return -1;
	}
	
	public void drawChoice(int choice, int column, Graphics2D g) {
		g.setColor(Color.white);
		if(column == 0 || column == 2) {
			artist.getTemplates().get(choice).drawFillShape(g);
		}
		if(column == 1) {
			Design design = getDesigns().get(choice);
			design.getTemplate().drawFillShape(g);
			g.setColor(Color.gray);
			for(DesignBounds sub : design.getSubdesigns()) {
				Graphics2D temp = (Graphics2D) g.create();
				sub.transformGraphics(temp);
				sub.draw(temp);
			}
		}
	}
	
	public int getSelection(int col) {
		if(col == 0) {
			LinkedList<DesignTemplate> list = artist.library().getTemplates();
			int i = 0;
			for(DesignTemplate template : list ) {
				if(artist.getCurrentDesign().getTemplate() == template)
					return i;
				i++;
			}
		}
		if(col == 1) {
			Vector<Design> designs = getDesigns();
			int i = 0;
			for(Design design : designs) {
				if(artist.currentDesign == design)
					return i;
				i++;
			}
		}
		if(col == 2)
			return artist.getCurrentTemplateNum();
		return -1;
	}

	private Vector<Design> getDesigns() {
		return artist.library().getDesignsForTemplate(artist.currentDesign.getTemplate());
	}	

	public static final int SPACING = 10;
	public static final int BOX_WIDTH =  80;
	public static final int BOX_HEIGHT = 80;
	protected boolean menuHidden;

	public boolean isHidden() {
		return menuHidden;
	}
	
	public void hideMenu() {
		menuHidden = true;
		Animation animation = new Animation(1000, new Point(0,600),this);
		animation.startAnimation();
	}
	
	public void showMenu() {
		menuHidden = false;
		Animation animation = new Animation(1000, new Point(0,0),this);
		animation.startAnimation();
	}

	public void toggleMenu() {
		if(menuHidden)
			showMenu();
		else
			hideMenu();
	}
	
	public void paintComponent(Graphics g) {
		
		g.setColor(new Color((float).4,(float).4, (float) .4, (float) .9));
		g.fillRect(0, 0, BOX_WIDTH*3+4*SPACING,getHeight());
		g.setColor(new Color((float).4,(float).4, (float) .9, (float) 1));
		g.fillRect(SPACING/2+artist.getMenuColumn()*(SPACING+BOX_WIDTH), 0, BOX_WIDTH+SPACING,getHeight());
		
		for(int column = 0; column < 3; column++) {
			int selection = getSelection(column);
			g.setColor(new Color((float).8,(float).8, (float) .8, (float) .5));
			g.fillRect(SPACING/2+(BOX_HEIGHT+SPACING)*column,
					SPACING/2+(BOX_HEIGHT+SPACING)*selection,
					BOX_WIDTH+SPACING,
					BOX_HEIGHT+SPACING);
			g.setColor(new Color((float)0,(float)0, (float) 0, (float) 1));

			for(int i = 0; i < numberOfChoices(column); i++) {
				Graphics2D current = (Graphics2D) g.create();
				current.translate(SPACING+column*(BOX_WIDTH+SPACING), SPACING+(BOX_HEIGHT+SPACING)*i);
				current.scale(BOX_WIDTH, BOX_HEIGHT);
				current.setColor(new Color((float)0,(float)0, (float) 0, (float) 1));
				current.fillRect(0, 0, 1, 1);
				current.translate(0.05, .05);
				current.scale(0.9,0.9);
				drawChoice(i,column, current);
			}
		}		
	}
	
}
