import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.Timer;


public class FractalComponent extends JComponent {
	
	protected FractalPainter painter;
	public FractalComponent(FractalPainter p) {
		painter = p;
		ActionListener repaintListener  = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
			}			
		};
		Timer timer = new Timer(1000,repaintListener);
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
	}
}
