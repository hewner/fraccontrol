import java.awt.Color;


public class ColorScheme 
{
	Color first;
	Color firstContrast;
	Color second;
	Color secondContrast;
	public static enum ColorMode { FIRST, FIRST_CONTRAST, SECOND, SECOND_CONTRAST };
		
	public ColorScheme(Color first, 
						Color firstContrast, 
						Color second, 
						Color secondContrast) {
		this.first = first;
		this.firstContrast = firstContrast;
		this.second = second;
		this.secondContrast = secondContrast;
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
		if(color == ColorMode.FIRST) return first;
		if(color == ColorMode.SECOND) return second;
		if(color == ColorMode.FIRST_CONTRAST) return firstContrast;
		if(color == ColorMode.SECOND_CONTRAST) return secondContrast;
		return null;
	}

}

