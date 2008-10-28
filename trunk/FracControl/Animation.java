import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.Timer;


public class Animation implements ActionListener {
	double startTime;
	float duration;
	Point start;
	Point end;
	JComponent comp;
	Timer timer;
	
	public Animation(float duration, Point end, JComponent comp) {
		this.duration = duration;
		this.start = comp.getLocation();
		this.end = end;
		this.comp = comp;
	}
	
	public Point getLocation() {
		
		double secs = System.currentTimeMillis() - startTime;
		double percent = secs/duration;
		if(percent > 1) {
			timer.stop();
			return end;
		}
		int xPos = (int)((end.x - start.x)*percent+start.x);
		int yPos = (int)((end.y - start.y)*percent+start.y);
		return new Point(xPos, yPos);
	}
	
	public void update() {
		//System.out.println("Updating to " + getLocation());
		comp.setLocation(getLocation());
	}
	
	public void startAnimation() {
		startTime = System.currentTimeMillis();
		timer = new Timer(100,this);
		timer.start();
	}

	public void actionPerformed(ActionEvent e) {
		update();
	}
	
}
