import java.awt.Shape;


public class DesignTemplate {
	protected String name;
	protected Shape shape;
	
	public DesignTemplate(String name, Shape shape) {
		this.name = name;
		this.shape = shape;
	}

	public String getName() {
		return name;
	}
	
	public Shape getShape() {
		return shape;
	}

}
