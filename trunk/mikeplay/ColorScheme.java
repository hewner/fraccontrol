import java.awt.Color;


public class ColorScheme 
{
	private Color firstColor;
	private Color firstContrastColor;
	private Color secondColor;
	private Color secondContrastColor;
	public static enum ColorMode { FIRST, FIRST_CONTRAST, SECOND, SECOND_CONTRAST };
		
	public ColorScheme(Color first, 
						Color firstContrast, 
						Color second, 
						Color secondContrast) {
		this.firstColor = first;
		this.firstContrastColor = firstContrast;
		this.secondColor = second;
		this.secondContrastColor = secondContrast;
	}
	
	public static ColorMode getContrast(ColorMode color) {
		if(color == ColorMode.FIRST) return ColorMode.FIRST_CONTRAST;
		if(color == ColorMode.SECOND) return ColorMode.SECOND_CONTRAST;
		if(color == ColorMode.FIRST_CONTRAST) return ColorMode.FIRST;
		if(color == ColorMode.SECOND_CONTRAST) return ColorMode.SECOND;
		return null;
	}

	public static ColorMode getOppositeContrast(ColorMode color) {
		if(color == ColorMode.FIRST) return ColorMode.SECOND_CONTRAST;
		if(color == ColorMode.SECOND) return ColorMode.FIRST_CONTRAST;
		if(color == ColorMode.FIRST_CONTRAST) return ColorMode.SECOND;
		if(color == ColorMode.SECOND_CONTRAST) return ColorMode.FIRST;
		return null;
	}

	public static ColorMode getOpposite(ColorMode color) {
		if(color == ColorMode.FIRST) return ColorMode.SECOND;
		if(color == ColorMode.SECOND) return ColorMode.FIRST;
		if(color == ColorMode.FIRST_CONTRAST) return ColorMode.SECOND_CONTRAST;
		if(color == ColorMode.SECOND_CONTRAST) return ColorMode.FIRST_CONTRAST;
		return null;
	}
	
	public static boolean isContrast(ColorMode color) {
		return (color == ColorMode.FIRST_CONTRAST || color == ColorMode.SECOND_CONTRAST);
	}
	
	public static ColorMode first() {
		return ColorMode.FIRST;
	}

	public Color color(ColorMode color) {
		if(color == ColorMode.FIRST) return firstColor;
		if(color == ColorMode.SECOND) return secondColor;
		if(color == ColorMode.FIRST_CONTRAST) return firstContrastColor;
		if(color == ColorMode.SECOND_CONTRAST) return secondContrastColor;
		return null;
	}

	public void setColor(ColorMode color, Color newColor) {
		if(color == ColorMode.FIRST) firstColor = newColor;
		if(color == ColorMode.SECOND) secondColor = newColor;
		if(color == ColorMode.FIRST_CONTRAST) firstContrastColor = newColor;
		if(color == ColorMode.SECOND_CONTRAST) secondContrastColor = newColor;
		
	}

}

