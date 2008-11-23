import java.io.Serializable;
import java.util.*;


public class DesignTemplateLibrary implements Serializable {
	
	private static final long serialVersionUID = -7673206905863074000L;
	protected Map<DesignTemplate,Vector<Design>> map;
	protected LinkedList<DesignTemplate> templates;
	
	public DesignTemplateLibrary() {
		map = new HashMap<DesignTemplate,Vector<Design>>();
		templates = new LinkedList<DesignTemplate>();
	}

	public void addTemplate(DesignTemplate template) {
		map.put(template, new Vector<Design>());
		templates.add(template);
	}
	
	public void addDesign(Design d) {
		map.get(d.getTemplate()).add(d);
	}
	
	public Vector<Design> getDesignsForTemplate(DesignTemplate template) {
		return map.get(template);
	}
	
	public DesignTemplate getTemplate(String name) {
		for(DesignTemplate template : map.keySet()) {
			if(template.getName().equals(name)) {
				return template;
			}
		}
		throw new RuntimeException("Attempt to access unknown template " + name);
	}
	
	public LinkedList<DesignTemplate> getTemplates() {
		return templates;
	}
	
}
