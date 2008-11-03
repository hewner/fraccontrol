import java.awt.Graphics2D;


public class DesignInstance {
	protected Design baseDesign;
	public DesignInstance(Design design) {
		baseDesign = design;
	}

	public void draw(Graphics2D g, FractalPainter painter) {
		baseDesign.draw(g, painter);
	}
}
