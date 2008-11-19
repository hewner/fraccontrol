import java.awt.Shape;
import java.io.Serializable;


public class DesignTemplate implements Serializable{

	private static final long serialVersionUID = -954256991033058432L;
	protected String name;
	protected Shape shape;
	protected DesignTemplateLibrary library;
	private double shapeScaleFactor;
	
	public DesignTemplate(String name, Shape shape, DesignTemplateLibrary library, double shapeScaleFactor) {
		this.name = name;
		this.shape = shape;
		this.library = library;
		this.shapeScaleFactor = shapeScaleFactor;
		library.addTemplate(this);
	}

	public String getName() {
		return name;
	}
	
	public Shape getShape() {
		return shape;
	}

	public DesignTemplateLibrary getLibrary() {
		return library;
	}

	public double getShapeScaleFactor() {
		return shapeScaleFactor;
	}
	
}
