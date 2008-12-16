import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class OverallMenu extends JMenuBar {
	ArtistState artist;
	public OverallMenu(ArtistState artist) {
		JMenu fileMenu = new JMenu("File");
		JMenuItem load = new JMenuItem("Load fractal");
		this.artist = artist;
		fileMenu.add(load);
		final OverallMenu parent = this;
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doLoad();
			}
		});
		JMenuItem save = new JMenuItem("Save fractal");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doSave();
			}
		});		
		fileMenu.add(save);
		JMenuItem export = new JMenuItem("Export to svg");
		fileMenu.add(export);
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doExport();
			}
		});
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);	
			}
		});
		fileMenu.add(exit);
		add(fileMenu);
	}
	
	private void doExport() {
	    JFileChooser fc = new JFileChooser();
	    
	    if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
	    	artist.outputToSVG(fc.getSelectedFile());
	    }	
	}

	private void doSave() {
	    JFileChooser fc = new JFileChooser();
	    
	    if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
	    	artist.writeToFile(fc.getSelectedFile());
	    }	
	}

	private void doLoad() {
	    JFileChooser fc = new JFileChooser();
	    
	    if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	    	artist.readFromFile(fc.getSelectedFile());
	    }		
	}
}
