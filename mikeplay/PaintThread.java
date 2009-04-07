import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

class PaintThread implements Runnable {
		
		private static final int maxRules = 30000; 
		private boolean shouldStop = false;
		private class ModAndTask implements Comparable<ModAndTask> {
			public FractalModification mod;
			public DrawTask task;
			public ModAndTask(FractalModification mod, DrawTask task) {
				this.mod = mod;
				this.task = task;
			}
			public int compareTo(ModAndTask arg0) {
				double myArea = task.getAbsoluteArea();
				double otherArea = arg0.task.getAbsoluteArea();
				//we want larger things first
				return Double.compare(otherArea, myArea);			
			}

		}
		private PriorityQueue<ModAndTask> toDraw;
		private Thread thread;
		
		public PaintThread(DrawTask root, FractalPainter painter) {
			toDraw = new PriorityQueue<ModAndTask>();
			addTask(painter, root);
		}

		public synchronized void shouldStop() {
			shouldStop = true;
		}
		
		public void run() {
			int numberDrawn = 0;
			while(shouldRun()) {
				ModAndTask temp = removeTask();
				DrawTask current = temp.task;
				FractalModification modification = temp.mod;
				modification.doDraw(current);
				numberDrawn++;
	
			}
			if(isEmpty()) {
				System.out.println(this + " finished drawing " + numberDrawn + " shapes.");
			}
		}
		
		public synchronized boolean isEmpty() {
			return toDraw.isEmpty();
		}
		
		public synchronized boolean shouldRun() {
			return !shouldStop && !isEmpty();
		}
		
		public synchronized void addAll(FractalModification mod, List<DrawTask> tasks) {
			for(DrawTask task : tasks) {
				addTask(mod,task);
			}
		}
		
		public synchronized void  addTask(FractalModification mod, DrawTask task) {
			if(toDraw.size() < maxRules) {
				toDraw.add(new ModAndTask(mod, task));
				if((thread == null || !thread.isAlive()) && !shouldStop) {
					thread = new Thread(this);
					thread.start();
				}
			} else {
				System.err.println("Exceeded toDraw max!");
			}
		}
		
		private synchronized ModAndTask removeTask() {
			return toDraw.remove();
		}
		
		public synchronized boolean isAlive() {
			return thread.isAlive();
		}
		
	}