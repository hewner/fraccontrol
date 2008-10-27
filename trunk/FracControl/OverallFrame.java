import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;


public class OverallFrame extends JFrame {
	
	public OverallFrame() {
		super("FracControl");
		setSize(600,500);
		try {
			FractalPainter painter = new FractalPainter();
			getContentPane().add(new FractalComponent(painter));
			
			painter.addRule("double", new Rule() {
				public void draw() {
					rule("square");
					rule("gasket");
					
				}
			});
			
			painter.addRule("gasket", new Rule() {
				public void draw() {
					rule("little square");
					translate(-25, 50);
					rule("gasket");
					translate(50,0);
					rule("gasket");
				}
			});
			painter.addRule("little square", new Rule() {
				public void draw() {
					scale(.1);
					square();
				}
			});
			painter.addRule("square", new Rule() {
				public void draw() {
                    push();
					scale(.05,.1);
					square();
					pop();
					translate(20,0);
					rotate(.05);
					scale(.995);
					rule("square");
				}				
			});
			painter.setStartRule("double");
			if(false) painter.startDrawingWithSize(600, 500);
		} catch (FractalPainter.RenderingException e) {
			System.err.println("Initial setup caused a rendering exception");
			System.err.println(e.toString());
			e.printStackTrace();
			System.exit(-1);
		}
		RuleMenu ruleMenu = new RuleMenu();
		getLayeredPane().setLayer(ruleMenu, JLayeredPane.PALETTE_LAYER);
		//getLayeredPane().add(ruleMenu);
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
