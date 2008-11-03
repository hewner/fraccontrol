import java.awt.Color;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.Timer;


public class OverallFrame extends JFrame {
	
	public OverallFrame() {
		super("FracControl");
		setSize(600,500);
		try {
			FractalPainter painter = new FractalPainter(600,500);
			Design design = new Design(Color.BLUE);
			getContentPane().add(new FractalComponent(painter,design));
			painter.startDrawing();

		} catch (FractalPainter.RenderingException e) {
			System.err.println("Initial setup caused a rendering exception");
			System.err.println(e.toString());
			e.printStackTrace();
			System.exit(-1);
		}
		final RuleMenu ruleMenu = new RuleMenu();
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
