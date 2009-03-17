import java.awt.Color;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import javax.naming.spi.DirectoryManager;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;

import org.apache.batik.ext.awt.geom.Polygon2D;

import com.kitfox.svg.Path;
import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGRoot;
import com.kitfox.svg.SVGUniverse;

public class OverallFrame extends JFrame {
	
	public OverallFrame() {
		super("FracControl");
		
		setSize(600,500);
		final ArtistState artist = new ArtistState();
		FractalPainter painter = new FractalPainter(600,500,artist);
		try {
			initLibrary(artist);
			artist.setCurrentDesign(artist.library().getTemplates().getFirst().getDesigns().firstElement());
			FractalComponent fractalComponent = new FractalComponent(painter,artist); 	
			getContentPane().add(fractalComponent);
			MouseControl mouse = new MouseControl(artist, painter);
			GameControl game = new GameControl(artist, fractalComponent);
			fractalComponent.addMouseListener(mouse);
			fractalComponent.addMouseMotionListener(mouse);
			addKeyListener(mouse);
			painter.startDrawing();
			//TODO: Fix this crap
			artist.painter = painter;
		} catch (FractalPainter.RenderingException e) {
			System.err.println("Initial setup caused a rendering exception");
			System.err.println(e.toString());
			e.printStackTrace();
			System.exit(-1);
		}
		JMenuBar bar = new OverallMenu(artist);
		setJMenuBar(bar);
	}

	protected void initLibrary(ArtistState artist) {
		DesignTemplate squareTemplate = new DesignTemplate("square",new Rectangle2D.Double(0,0,1.0,1.0), 2/Math.sqrt(2),1);
		DesignTemplate circleTemplate = new DesignTemplate("circle", new Ellipse2D.Double(0,0,1.0,1.0), 2, 0.25*Math.PI);
		artist.library().addTemplate(squareTemplate);
		artist.library().addTemplate(circleTemplate);
			Polygon2D triangle = new Polygon2D();
		triangle.addPoint(0, (float).866);
		triangle.addPoint(1, (float).866); //(1, 0);
		triangle.addPoint((float) .5,0); //((float).5, (float).866);
		DesignTemplate triangleTemplate = new DesignTemplate("triangle", triangle, Math.sqrt(3),.866*0.5);
		artist.library().addTemplate(triangleTemplate);
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
