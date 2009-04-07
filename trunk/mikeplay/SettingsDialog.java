import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JColorChooser;


public class SettingsDialog extends JFrame {
	ArtistState artist;
	public SettingsDialog(ArtistState artist) {
		super("Fractal Settings");
		this.artist = artist;
		Container pane = getContentPane();
		pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));
		JButton button = new JButton("Pick color");
		button.setBackground(Color.green);
		final JFrame frame = this;
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JColorChooser.showDialog(frame, "Pick a color", Color.blue);
				
			}
			
		});
		pane.add(button);
		pane.add(new JButton("Test Button2"));
	}
	

	
}
