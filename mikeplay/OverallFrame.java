import java.awt.Color;
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
		try {
			
			FractalPainter painter = new FractalPainter(600,500,artist);
			initLibrary(artist);
			Design design = new Design(Color.BLUE,artist.library().getTemplate("square"));
			new Design(Color.RED,artist.library().getTemplate("circle"));
			new Design(Color.YELLOW,artist.library().getTemplate("triangle"));
			artist.setCurrentDesign(design);
			FractalComponent fractalComponent = new FractalComponent(painter,artist); 	
			getContentPane().add(fractalComponent);
			MouseControl mouse = new MouseControl(artist);
			GameControl game = new GameControl(artist, fractalComponent);
			fractalComponent.addMouseListener(mouse);
			fractalComponent.addMouseMotionListener(mouse);
			addKeyListener(mouse);
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
		ruleMenu.setSize(300,getHeight());
		
		artist.onMenuChange( new Runnable() {
			public void run() {
				if(ruleMenu.isHidden() != artist.isRuleMenuHidden())
					ruleMenu.toggleMenu();
				repaint();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				//we want to behave as if the user had clicked "Exit"
				System.exit(0);
			}
		});
	}

	protected void initLibrary(ArtistState artist) {
		DesignTemplate squareTemplate = new DesignTemplate("square",new Rectangle2D.Double(0,0,1.0,1.0), artist.library(),2/Math.sqrt(2));
		DesignTemplate circleTemplate = new DesignTemplate("circle", new Ellipse2D.Double(0,0,1.0,1.0), artist.library(), 2);
			Polygon2D triangle = new Polygon2D();
		triangle.addPoint(0, (float).866);
		triangle.addPoint(1, (float).866); //(1, 0);
		triangle.addPoint((float) .5,0); //((float).5, (float).866);
		DesignTemplate triangleTemplate = new DesignTemplate("triangle", triangle, artist.library(), Math.sqrt(3));
		loadSVGs(artist);
	}

	protected Path findPath(SVGElement element) {
		if(element instanceof Path) {
			return (Path) element;
		}
		for(Object child : element.getChildren(null)) {
			Path path = findPath((SVGElement) child);
			if(path != null) {
				return path;
			}
		}
		return null;
	}
	
	protected void loadSVGs(ArtistState artist) {
		File svgs = new File("svgs");
		SVGUniverse uni = new SVGUniverse();
		for(String svgName : svgs.list()) {
			System.out.println(svgName);
			try {
				FileInputStream in = new FileInputStream("svgs" + File.separator + svgName);
				URI uri = uni.loadSVG(in,svgName);
				SVGRoot root = uni.getDiagram(uri).getRoot();
				Path path = findPath(root);
				new DesignTemplate(svgName,path.getShape(), artist.library(),2/Math.sqrt(2));
				new Design(Color.YELLOW,artist.library().getTemplate(svgName));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
