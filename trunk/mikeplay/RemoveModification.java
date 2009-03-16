import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;


public class RemoveModification implements FractalModification
{
	private Graphics2D g;
	
	public RemoveModification(Design removeFrom, DesignBounds subdesignToRemove, FractalPainter painter) {
		removeFrom.removeSubdesign(subdesignToRemove);
		try {
			painter.redrawAll();
		} catch (FractalPainter.RenderingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*g = painter.getGraphics();
		List<DrawTask> designsToChange = painter.cachedInstancesOf(removeFrom);
		LinkedList<DrawTask> undrawTasks = new LinkedList<DrawTask>();
		for(DrawTask task : designsToChange) {
			DrawTask removed = task.removeSubtaskAfterward(subdesignToRemove);
			undrawTasks.add(removed);
		}
		painter.getThread().addAll(this, undrawTasks);*/
	}

	public void doDraw(DrawTask current) {
		current.drawErase(g);
	}

}