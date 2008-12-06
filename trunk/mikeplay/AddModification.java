import java.util.LinkedList;
import java.util.List;


public class AddModification implements FractalModification{
	
	FractalPainter painter;
	
	public AddModification(Design addTo, DesignBounds subdesignToAdd, FractalPainter painter) {
		addTo.addSubdesign(subdesignToAdd);
		this.painter = painter;
		List<DrawTask> designsToChange = painter.cachedInstancesOf(addTo);
		LinkedList<DrawTask> drawTasks = new LinkedList<DrawTask>();
		for(DrawTask task : designsToChange) {
			for(DrawTask subtask : task.getSubtasks()) {
				if(subtask.setColorScheme(task)) {
					drawTasks.add(subtask);
				}
			}
			DrawTask newTask = new DrawTask(subdesignToAdd,task);
			drawTasks.add(newTask);
			task.addSubtaskAfterward(newTask);
		}
		painter.getThread().addAll(this, drawTasks);
	}

	public void doDraw(DrawTask current) {
		//System.out.println(current + "ZZZZZ" + painter);
		painter.doDraw(current);
	}

}
