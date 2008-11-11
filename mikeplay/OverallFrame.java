import java.awt.Color;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.Timer;


public class OverallFrame extends JFrame {
	
	public OverallFrame() {
		super("FracControl");
		
		setSize(600,500);
		ArtistState artist = new ArtistState();
		try {
			
			FractalPainter painter = new FractalPainter(600,500);
			initLibrary();
			Design design = new Design(Color.BLUE,DesignTemplateLibrary.library().getTemplate("square"));
			new Design(Color.RED,DesignTemplateLibrary.library().getTemplate("circle"));
			new Design(Color.YELLOW,DesignTemplateLibrary.library().getTemplate("triangle"));
			getContentPane().add(new FractalComponent(painter,design,artist));
			painter.startDrawing();

		} catch (FractalPainter.RenderingException e) {
			System.err.println("Initial setup caused a rendering exception");
			System.err.println(e.toString());
			e.printStackTrace();
			System.exit(-1);
		}
		final RuleMenu ruleMenu = new RuleMenu(artist);
		getLayeredPane().setLayer(ruleMenu, JLayeredPane.PALETTE_LAYER);
		getLayeredPane().add(ruleMenu);
		addKeyListener(ruleMenu);
		ruleMenu.setSize(200,getHeight());
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				//we want to behave as if the user had clicked "Exit"
				System.exit(0);
			}
		});
}

	protected void initLibrary() {
		DesignTemplate squareTemplate = new DesignTemplate("square",new Rectangle2D.Double(0,0,1.0,1.0));
		DesignTemplate circleTemplate = new DesignTemplate("circle", new Ellipse2D.Double(0,0,1.0,1.0));
		DesignTemplate triangleTemplate = new DesignTemplate("triangle", new Rectangle2D.Double(0,0,1.0,0.1));
		DesignTemplateLibrary.library().addTemplate(squareTemplate);
		DesignTemplateLibrary.library().addTemplate(circleTemplate);
		DesignTemplateLibrary.library().addTemplate(triangleTemplate);
		
	}

	private static void createAndShowGUI() {
		OverallFrame app = new OverallFrame();
 
        app.setVisible(true);  
		
	}
	
	
	
	public static void main(String[] args) {
	    //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

	}
	
}
