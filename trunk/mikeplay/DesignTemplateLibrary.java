import java.util.*;


public class DesignTemplateLibrary {
	
	protected Map<DesignTemplate,Vector<Design>> map;
	protected LinkedList<DesignTemplate> templates;
	protected Random random;
	
	public DesignTemplateLibrary() {
		map = new HashMap<DesignTemplate,Vector<Design>>();
		templates = new LinkedList<DesignTemplate>();
		random = new Random();
	}

	public void addTemplate(DesignTemplate template) {
		map.put(template, new Vector<Design>());
		templates.add(template);
	}
	
	public void addDesign(Design d) {
		map.get(d.getTemplate()).add(d);
	}
	
	public Design getRandomDesign(DesignTemplate template) {
		Vector<Design> designs = map.get(template);
		int num = random.nextInt(designs.size());
		return designs.get(num);
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
	
	static DesignTemplateLibrary singleton = null;
	public static DesignTemplateLibrary library() {
		
		if(singleton == null) {
			singleton = new DesignTemplateLibrary();
		}
		
		return singleton;		
	}
}
