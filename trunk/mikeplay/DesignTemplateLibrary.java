import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.*;

import com.kitfox.svg.Path;
import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGRoot;
import com.kitfox.svg.SVGUniverse;


public class DesignTemplateLibrary implements Serializable {
	
	private static final long serialVersionUID = -7673206905863074000L;
	protected Map<DesignTemplate,Vector<Design>> map;
	protected LinkedList<DesignTemplate> templates;
	protected transient SVGUniverse uni;
	
	public DesignTemplateLibrary() {
		map = new HashMap<DesignTemplate,Vector<Design>>();
		templates = new LinkedList<DesignTemplate>();		
	}

	private SVGUniverse svgUniverse() {
		if(uni == null) {
			uni = new SVGUniverse();
		}
		return uni;
	}
	
	public void addTemplate(DesignTemplate template) {
		map.put(template, new Vector<Design>());
		templates.add(template);
	}
	
	public DesignTemplate getTemplate(String name) {
		for(DesignTemplate template : map.keySet()) {
			if(template.getName().equals(name)) {
				return template;
			}
		}
		throw new RuntimeException("Attempt to access unknown template " + name);
	}
	
	protected Path findPath(SVGElement element) {
		if(element instanceof Path) {
			return (Path) element;
		}
		for(Object child : element.getChildren(null)) {
			Path path = findPath((SVGElement) child);
			if(path != null) {
				return path;
			}
		}
		return null;
	}

	
	public void templateFromFile(File file) throws Exception {
		FileInputStream in = new FileInputStream(file);
		URI uri;
		uri = svgUniverse().loadSVG(in,file.getName());
		SVGRoot root = svgUniverse().getDiagram(uri).getRoot();
		Path path = findPath(root);
		if(path == null)
			throw new Exception("Could not find path in file");
		DesignTemplate template = new DesignTemplate(file.getName(),path.getShape(), 2/Math.sqrt(2),1);
		addTemplate(template);
	}
	
	
	public LinkedList<DesignTemplate> getTemplates() {
		return templates;
	}
	
}
