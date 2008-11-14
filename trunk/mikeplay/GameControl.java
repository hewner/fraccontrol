import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.Timer;

import de.hardcode.jxinput.Axis;
import de.hardcode.jxinput.Button;
import de.hardcode.jxinput.Directional;
import de.hardcode.jxinput.JXInputDevice;
import de.hardcode.jxinput.JXInputManager;
import de.hardcode.jxinput.event.JXInputAxisEvent;
import de.hardcode.jxinput.event.JXInputAxisEventListener;
import de.hardcode.jxinput.event.JXInputButtonEvent;
import de.hardcode.jxinput.event.JXInputButtonEventListener;
import de.hardcode.jxinput.event.JXInputDirectionalEvent;
import de.hardcode.jxinput.event.JXInputDirectionalEventListener;
import de.hardcode.jxinput.event.JXInputEventManager;


public class GameControl implements JXInputAxisEventListener,
		JXInputButtonEventListener, JXInputDirectionalEventListener {
	
	protected JXInputDevice dev;
	protected DesignBounds preview;
	protected ArtistState artist;
	protected FractalComponent component;
	protected Point2D cursor;
	protected Timer cursorTimer;
	protected Timer zoomTimer;
	protected Timer panTimer;

	
	public GameControl(ArtistState artist, FractalComponent component) {
		this.component = component;
		this.artist = artist;
		initGamepad();
	}

	public void initGamepad() {
		//System.out.println("Number of Dev " +JXInputManager.getNumberOfDevices());
		if(JXInputManager.getNumberOfDevices()>0) {
			dev = JXInputManager.getJXInputDevice( 0 );  //get Device
			//System.out.println(dev.getName());
			for(int i=0; i<dev.getNumberOfAxes(); i++ ) {
				Axis axis = dev.getAxis(i); //get axies
				JXInputEventManager.addListener( this, axis, 0.1 );
				System.out.println("AxisName"+i+" "+axis.getName());
			}
			
			for(int i=0; i<dev.getNumberOfButtons() ; i++ ) {
				Button button = dev.getButton(i);
				JXInputEventManager.addListener( this, button );
			}
			
			for(int i=0; i<dev.getNumberOfDirectionals() ; i++ ) {
				Directional dir  = dev.getDirectional(i);
				JXInputEventManager.addListener( this, dir );
			}
			
			
			
			JXInputEventManager.setTriggerIntervall( 20 );
		}
	}
	
	
	
	public Point2D getCrosshair() {

		double width = component.getWidth();
		double height = component.getHeight();
					
		Point2D center = new Point.Double(width/2, height/2);
			return center;
		//return artist.pointInFractalCoordinates(center);

	}


	public void changed(JXInputAxisEvent ev) {
		//System.out.println( "Axis " + ev.getAxis().getName() + " changed : value=" + ev.getAxis().getValue() + ", event causing delta=" + ev.getDelta() );
		
		if(ev.getAxis() == dev.getAxis(0)) { //if x axis do scale function
			//positionPadChanged();
		}
		
		if(ev.getAxis() == dev.getAxis(1)) { //if y axis do rotate function
			//positionPadChanged();
		}
		
		if(ev.getAxis() == dev.getAxis(2)) {  //if z axis do zoom function
			onZoom();
				
				
		}
		
		if(ev.getAxis() == dev.getAxis(3)) {  //if x axis rotation do pan function
			if(ev.getAxis().getValue()>0 && ev.getDelta()>0)
				artist.panViewTransform(ev.getDelta()/10, 0);
			if(ev.getAxis().getValue()<0 && ev.getDelta()<0)
				artist.panViewTransform(ev.getDelta()/10, 0);
			try {
				component.painter().redrawAll();
			} catch (FractalPainter.RenderingException e1) {
				System.err.println("Rendering exception while zoomin");
				e1.printStackTrace();
			}
		}
		
		if(ev.getAxis() == dev.getAxis(4)) {  //if y axis rotation do pan function
			if(ev.getAxis().getValue()>0 && ev.getDelta()>0)
				artist.panViewTransform(0, ev.getDelta());
			if(ev.getAxis().getValue()<0 && ev.getDelta()<0)
				artist.panViewTransform(0, ev.getDelta());
			try {
				component.painter().redrawAll();
			} catch (FractalPainter.RenderingException e1) {
				System.err.println("Rendering exception while zoomin");
				e1.printStackTrace();
			}
		}
		
	}


	

	private void onZoom() {
		
		if(zoomTimer == null) {
			zoomTimer = new Timer(100, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					artist.zoomViewTransform(1+dev.getAxis(2).getValue()/10 );
					try {
						component.painter().redrawAll();
					} catch (FractalPainter.RenderingException e1) {
						System.err.println("Rendering exception while zooming");
						e1.printStackTrace();
					}
				}
			});
		}	
		if(Math.abs(dev.getAxis(2).getValue())>0.1 && !(zoomTimer.isRunning()))
			zoomTimer.start();
		
		if(Math.abs(dev.getAxis(2).getValue())<0.1 && zoomTimer.isRunning()) 
			zoomTimer.stop();
		
	}

	public void changed(JXInputButtonEvent ev) {
		System.out.println( "Button " + ev.getButton().getName() + " changed : state=" + ev.getButton().getState() );
	
		if(ev.getButton() == dev.getButton(0)) {
			if(ev.getButton().getState()) { //button down
				onButtonADown();
				
			} else { //button up
				onButtonAUp();
			}
		}
		
	}

	private void onButtonAUp() {

		cursorTimer.stop();
		artist.updatePreview(getCrosshair(),cursor);
		artist.getCurrentDesign().addSubdesign(artist.getPreview());
		artist.setPreview(null);
		try {
			component.painter().redrawAll();
		} catch (FractalPainter.RenderingException e1) {
			System.err.println("Rendering exception adding new subcomponent");
			e1.printStackTrace();
		}
		
		
	}

	private void onButtonADown() {
		artist.startPreview(artist.pointInFractalCoordinates(getCrosshair()));
		cursorTimer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateCursor();
			}
		});
		cursor = new Point2D.Double(artist.getPreview().getCenter().getX(),artist.getPreview().getCenter().getY());
		cursorTimer.start();
	}
	
	private void updateCursor() {

		double xPosition = dev.getAxis(0).getValue();
		double yPosition = dev.getAxis(1).getValue();
		if(Math.abs(xPosition) < .2 && Math.abs(yPosition) < .2)
			return;
		cursor.setLocation(xPosition/50+cursor.getX(), yPosition/50+cursor.getY());
		artist.updatePreview(getCrosshair(), cursor);
		
		component.repaint();
		
	}

	public void changed(JXInputDirectionalEvent ev) {
		System.out.println( "Directional " + ev.getDirectional().getName() + " changed : direction=" + ev.getDirectional().getDirection()+"value="+ev.getDirectional().getValue()+"delta=" + ev.getDirectionDelta());
		if(ev.getDirectional().getDirection()==18000 && ev.getDirectional().getValue()==1) {
			artist.incrementTemplate();
		}
		if(ev.getDirectional().getDirection()==0 && ev.getDirectional().getValue()==1) {
			artist.decrementTemplate();
		}
		
	}


}
