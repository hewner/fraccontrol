import java.awt.geom.AffineTransform;
import java.io.Serializable;


public class StateToSave implements Serializable{

	private static final long serialVersionUID = -71033833302266553L;
	public DesignTemplateLibrary library;
	public Double zoomLevel;
	public AffineTransform viewTransform;
	public Design currentDesign;
}
