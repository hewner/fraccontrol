import java.awt.Graphics2D;
import java.util.Stack;

public abstract class Rule {
	private Graphics2D currentGraphics;
	protected FractalPainter painter;
	protected Stack<Graphics2D> graphicsStack;
	
	public Rule() {
		graphicsStack = new Stack<Graphics2D>();
	}
	
	protected void rule(String name) {
		Graphics2D newG = (Graphics2D) getCurrentGraphics().create();
		try {
			painter.addTask(name,newG);
		} catch (FractalPainter.RenderingException e) {
			System.err.println("RenderingException invoking rule " + name);
			System.err.println("Attempting to continue...");
		}
	}

	protected void scale(double sizex, double sizey) {
		getCurrentGraphics().scale(sizex, sizey);
	}
	
	protected void scale(double size) {
		getCurrentGraphics().scale(size, size);
	}
	
	protected void rotate(double radians) {
		getCurrentGraphics().rotate(radians);
	}
	
	protected void translate(double x, double y) {
		getCurrentGraphics().translate(x, y);
	}
	
	protected void square() {
			getCurrentGraphics().fillRect(0,0,300,300);
	}
	
	protected void push() {
		graphicsStack.push((Graphics2D) getCurrentGraphics().create());
	}
	
	protected void pop() {
		graphicsStack.pop();
	}
	
	public void doDraw(Graphics2D g) {
		graphicsStack.clear();
		graphicsStack.push(g);
		
		draw();
	}
	public abstract void draw();

	public void setPainter(FractalPainter painter) {
		this.painter = painter;
	}

	protected Graphics2D getCurrentGraphics() {
		return graphicsStack.peek();
	}
}
