import java.awt.Shape;
import java.io.Serializable;


public class DesignTemplate implements Serializable{

	private static final long serialVersionUID = -954256991033058432L;
	protected String name;
	protected Shape shape;
	protected DesignTemplateLibrary library;
	
	public DesignTemplate(String name, Shape shape, DesignTemplateLibrary library) {
		this.name = name;
		this.shape = shape;
		this.library = library;
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
	
}
