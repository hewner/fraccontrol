import java.awt.Graphics2D;
import java.io.Serializable;

public class DesignInstance implements Serializable {

	private static final long serialVersionUID = -6912448217614364296L;
	protected Design baseDesign;
	public DesignInstance(Design design) {
		baseDesign = design;
	}

	public void draw(Graphics2D g, FractalPainter painter) {
		baseDesign.drawBackground(g);
	}
}
