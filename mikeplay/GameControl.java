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
	
	protected double deltaX, deltaY;
	protected Timer cursorTimer;
	protected Timer zoomTimer;
	protected Timer panTimerX;
	protected Timer panTimerY;

	
	public GameControl(ArtistState artist, FractalComponent component) {
		this.component = component;
		this.artist = artist;
		initTimers();
		initGamepad();
	}

	private void initTimers() {
		cursorTimer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double xPosition = dev.getAxis(0).getValue();
				double yPosition = dev.getAxis(1).getValue();
				if(Math.abs(xPosition) < .3 && Math.abs(yPosition) < .3)
					return;
				updateCursor(xPosition, yPosition);
				
			}
		});
		
		zoomTimer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Math.abs(dev.getAxis(2).getValue())<0.2 && zoomTimer.isRunning()) 
					zoomTimer.stop();
				artist.zoomViewTransform(1-dev.getAxis(2).getValue()/10 );
				try {
					component.painter().redrawAll();
				} catch (FractalPainter.RenderingException e1) {
					System.err.println("Rendering exception while zooming");
					e1.printStackTrace();
				}
			}
		});
		
		ActionListener panTimerActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double xPosition = dev.getAxis(3).getValue();
				double yPosition = dev.getAxis(4).getValue();
				if(Math.abs(xPosition)<0.2 && panTimerX.isRunning()) 
					panTimerX.stop();
				if(Math.abs(yPosition)<0.2 && panTimerY.isRunning()) 
					panTimerY.stop();
				artist.panViewTransform(-dev.getAxis(3).getValue()/20,-dev.getAxis(4).getValue()/20 );
				if(cursorTimer.isRunning()) {
					//cursor.setLocation(xPosition/20+cursor.getX(), yPosition/20+cursor.getY());
					Point2D crosshair = artist.pointInFractalCoordinates(getCrosshair());
				    artist.updatePreview(crosshair, getCursor());
					//updateCursor(xPosition, yPosition);
				}
				try {
					component.painter().redrawAll();
				} catch (FractalPainter.RenderingException e1) {
					System.err.println("Rendering exception while zooming");
					e1.printStackTrace();
				}
				
			}
			
		};
		 
		panTimerX = new Timer(100, panTimerActionListener);
		
		panTimerY = new Timer(100, panTimerActionListener);
		
	}

	
	public Point2D getCursor() {
		Point2D crosshair = artist.pointInFractalCoordinates(getCrosshair());
	    return new Point2D.Double(crosshair.getX()+ deltaX, crosshair.getY() + deltaY );
	}
	public void initGamepad() {
		//System.out.println("Number of Dev " +JXInputManager.getNumberOfDevices());
		if(JXInputManager.getNumberOfDevices()>0) {
			dev = JXInputManager.getJXInputDevice( 0 );  //get Device
			//System.out.println(dev.getName());
			for(int i=0; i<dev.getNumberOfAxes(); i++ ) {
				Axis axis = dev.getAxis(i); //get axies
				JXInputEventManager.addListener( this, axis, 0.1 );
				//System.out.println("AxisName"+i+" "+axis.getName());
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
		
		if(ev.getAxis() == dev.getAxis(2))   //if z axis do zoom function
			onZoom();
		
		if(ev.getAxis() == dev.getAxis(3))   //if x axis rotation do pan function
			onPan();
		
		if(ev.getAxis() == dev.getAxis(4))   //if y axis rotation do pan function
			onPan();
		
		
	}

	private void onPan() {
		
			
		
		if(Math.abs(dev.getAxis(3).getValue())>0.2 && !(panTimerX.isRunning()))
			panTimerX.start();
		if(Math.abs(dev.getAxis(3).getValue())<0.2 && panTimerX.isRunning()) 
			panTimerX.stop();
		if(Math.abs(dev.getAxis(4).getValue())>0.2 && !(panTimerY.isRunning()))
			panTimerY.start();
		if(Math.abs(dev.getAxis(4).getValue())<0.2 && panTimerY.isRunning()) 
			panTimerY.stop();
		
	}

	private void onZoom() {
		
		
		if(Math.abs(dev.getAxis(2).getValue())>0.2 && !(zoomTimer.isRunning()))
			zoomTimer.start();
		if(Math.abs(dev.getAxis(2).getValue())<0.2 && zoomTimer.isRunning()) 
			zoomTimer.stop();
	}

	public void changed(JXInputButtonEvent ev) {
		System.out.println( "Button " + ev.getButton().getName() + " changed : state=" + ev.getButton().getState() );
	
		if(ev.getButton() == dev.getButton(0) && dev.getButton(0).getState()) {
			onButtonAClicked();
		}
		if(ev.getButton() == dev.getButton(1) && dev.getButton(1).getState()) {
			onButtonBClicked();
		}
		if(ev.getButton() == dev.getButton(5) && dev.getButton(5).getState()) {
			onButtonRBClicked();
		}
		if(ev.getButton() == dev.getButton(4) && dev.getButton(4).getState()) {
			onButtonRBClicked();
		}
		if(ev.getButton() == dev.getButton(7) && dev.getButton(7).getState()) {
			onButtonStartClicked();
		}
		if(ev.getButton() == dev.getButton(6) && dev.getButton(6).getState()) {
			onButtonBackClicked();
		}
		if(ev.getButton() == dev.getButton(2) && dev.getButton(2).getState()) {
			onButtonXClicked();
		}
		if(ev.getButton() == dev.getButton(3) && dev.getButton(3).getState()) {
			onButtonYClicked();
		}
		
	}

	

	private void onButtonYClicked() {
		artist.toggleRuleMenu();
		
	}

	private void onButtonXClicked() {
		artist.makeNewDesign();
		
	}

	private void onButtonBackClicked() {
		artist.writeToFile();
		
	}

	private void onButtonStartClicked() {
		artist.readFromFile();
		
	}
	

	private void onButtonAClicked() {
		if(cursorTimer.isRunning()) { //second time button a is pressed
			cursorTimer.stop();
			Point2D localPoint = artist.pointInFractalCoordinates(getCrosshair());
			artist.updatePreview(localPoint,getCursor());
			artist.getCurrentDesign().addSubdesign(artist.getPreview());
			artist.setPreview(null);
			if(localPoint.distance(artist.getPreview().getCenter()) > .0001) {
				FractalModification foo = new AddModification(artist.getCurrentDesign(),artist.getPreview(),component.painter);
				//artist.getCurrentDesign().addSubdesign(artist.getPreview());
				artist.setPreview(null);
				component.repaint();	
			}
	
		} else { //first time button a is pressed
			deltaX = deltaY = 0;
			artist.startPreview(artist.pointInFractalCoordinates(getCrosshair()));
			//cursor.setLocation(artist.getPreview().getCenter());
			//System.out.println("center"+cursor);
			cursorTimer.start();
		}
	}
	private void onButtonBClicked() {
		DesignBounds subDesign = artist.getCurrentDesign().subDesignUnder(artist.pointInFractalCoordinates(getCrosshair()));
		if(subDesign != null) {
			System.out.println("delete"+getCrosshair());
			artist.getCurrentDesign().removeSubdesign(subDesign);
			artist.notifyViewTransformChange();
		} else {
			//click on unfilled area
		}
		
		
		
	}
	private void onButtonRBClicked() {
		artist.resetZoomState();
	}
	private void updateCursor(double xPosition, double yPosition) {
		
		deltaX += xPosition/50;
		deltaY += yPosition/50;
		artist.updatePreview(artist.pointInFractalCoordinates(getCrosshair()), getCursor());
		//System.out.println("cursor"+cursor);
		//component.repaint();
		
	}

	

	public void changed(JXInputDirectionalEvent ev) {
		System.out.println( "Directional " + ev.getDirectional().getName() + " changed : direction=" + ev.getDirectional().getDirection()+"value="+ev.getDirectional().getValue()+"delta=" + ev.getDirectionDelta());
		if(ev.getDirectional().getDirection()==18000 && ev.getDirectional().getValue()==1) {
			directionalDown();
		}
		if(ev.getDirectional().getDirection()==0 && ev.getDirectional().getValue()==1) {
			directionalUp();
		}
		if(ev.getDirectional().getDirection()==9000 && ev.getDirectional().getValue()==1) {
			directionalRight();
		}
		if(ev.getDirectional().getDirection()==27000 && ev.getDirectional().getValue()==1) {
			directionalLeft();
		}
		
	}

	private void directionalLeft() {
		artist.setMenuColumn(artist.getMenuColumn() - 1);
		
	}

	private void directionalRight() {
		artist.setMenuColumn(artist.getMenuColumn() + 1);
		
	}

	private void directionalDown() {
		if(artist.getMenuColumn() == 0) {
			artist.incrementCurrentDesignCategory();
		}
		if(artist.getMenuColumn() == 1) {
			artist.incrementCurrentDesign();
		}
		if(artist.getMenuColumn() == 2) {
			artist.incrementTemplate();
		}		
		
	}

	private void directionalUp() {
		if(artist.getMenuColumn() == 0) {
			artist.decrementCurrentDesignCategory();
		}
		if(artist.getMenuColumn() == 1) {
			artist.decrementCurrentDesign();
		}
		if(artist.getMenuColumn() == 2) {
			artist.decrementTemplate();
		}
		
	}


}
