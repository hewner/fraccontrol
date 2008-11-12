import java.awt.geom.AffineTransform;
import java.util.*;


public class ArtistState {
	protected int templateNum;
	protected AffineTransform viewTransform;
	
	public ArtistState() {
		templateNum = 0;
	}
	
	public void setCurrentTemplate(int template) {
		templateNum = template;
	}
	
	public int getCurrentTemplateNum() {
		return templateNum;
	}
	
	public AffineTransform getViewTransform() {
		return viewTransform;
	}
	
	public AffineTransform viewTransform(double unitLength) {
		if(viewTransform == null) {
			viewTransform = new AffineTransform();
			viewTransform.scale(unitLength, unitLength);
			viewTransform.translate(0.05, 0.05);
		}
		return viewTransform;
	}
	
	public void incrementTemplate() {
		if(templateNum != getTemplateCount() - 1) {
			templateNum++;
		}
	}
	
	public void decrementTemplate() {
		if(templateNum != 0) {
			templateNum--;
		}
	}
	
	public DesignTemplate getCurrentTemplate() {
		return DesignTemplateLibrary.library().getTemplates().get(getCurrentTemplateNum());
	}
	
	public int getTemplateCount() {
		return DesignTemplateLibrary.library().getTemplates().size();
	}
	
	public List<DesignTemplate> getTemplates() {
		return DesignTemplateLibrary.library().getTemplates();
	}
	
}
