import java.awt.geom.AffineTransform;
import java.io.Serializable;


public class StateToSave implements Serializable{

	private static final long serialVersionUID = 512085084405315862L;
	public DesignTemplateLibrary library;
	public Double zoomLevel;
	public AffineTransform viewTransform;
	public Design currentDesign;
	public DesignTemplate currentTemplate;
	public int seed;
}
