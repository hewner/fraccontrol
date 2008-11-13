import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.Timer;


public class FractalComponent extends JComponent {
	
	protected FractalPainter painter;
	protected ArtistState artist;
	
	public FractalComponent(FractalPainter p, ArtistState artist) {
		this.artist = artist;
		painter = p;
		painter.addDesign("start", artist.getCurrentDesign());
		painter.setStartRule("start");
		Design design = new Design(Color.GREEN,DesignTemplateLibrary.library().getTemplate("square"));
		painter.addDesign("sub", design);

		ActionListener repaintListener  = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
			}			
		};
		
		artist.onViewTransformChange(new Runnable() {
			public void run() {
				try {
					painter.redrawAll();
				} catch (FractalPainter.RenderingException e) {
					System.err.println("Error after view transform change");
					e.printStackTrace();
				}
			}
		});
		Timer timer = new Timer(100,repaintListener);
		timer.start();
	}
	public void setBounds(int x, int y, int width, int height) {
		System.out.println("W: " + width + " H: " + height );
		try {
			if(width != getWidth() || height != getHeight()) {
				painter.startDrawingWithSize(width, height);
			}
		} catch (FractalPainter.RenderingException e) {
			throw new RuntimeException("Resize caused RenderingException " + e.toString());
		}
		super.setBounds(x,y,width,height);
	}
	public void paintComponent(Graphics graph) {
		painter.drawCurrentImage(graph);
		artist.drawPreview((Graphics2D) graph, painter);
		//draw crosshair
		graph.setColor(Color.white);
		Point ch = artist.getCrosshair(this);
		
		graph.drawLine(ch.x, ch.y-5, ch.x, ch.y+5); //vertical line
		graph.drawLine(ch.x-5, ch.y, ch.x+5, ch.y); //horizontal line
		
		

	}
	
	public FractalPainter painter() {
		return painter;
	}
}
