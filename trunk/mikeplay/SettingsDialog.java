import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JColorChooser;
import javax.swing.JLabel;


public class SettingsDialog extends JFrame {
	ArtistState artist;
	final SettingsDialog parentThis = this;
	
	public SettingsDialog(ArtistState artist) {
		super("Fractal Settings");
		setSize(200,300);
		this.artist = artist;
		Container pane = getContentPane();
		pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));
		for(ColorScheme.ColorMode i : ColorScheme.ColorMode.values()) {
			JLabel label = new JLabel(i.toString());
			JButton button = new JButton("");
			button.setBackground(artist.colorScheme().color(i));
			button.addActionListener(new ColorUpdater(i,button));
			pane.add(label);
			pane.add(button);
		}
		
	}
	
	private class ColorUpdater implements ActionListener
	{
		ColorScheme.ColorMode mode;
		JButton button;
		public ColorUpdater(ColorScheme.ColorMode mode, JButton button) {
			this.mode = mode;
			this.button = button;
		}
		
		public void actionPerformed(ActionEvent e) {
			Color result = JColorChooser.showDialog(parentThis, "Color for " + mode, artist.colorScheme().color(mode));
			button.setBackground(result);
			artist.colorScheme().setColor(mode, result);
			artist.notifyViewTransformChange();
			
		}
	}
}
