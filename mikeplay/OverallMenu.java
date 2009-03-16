import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class OverallMenu extends JMenuBar {
	ArtistState artist;
	public OverallMenu(ArtistState newArtist) {
		JMenu fileMenu = new JMenu("File");
		JMenuItem load = new JMenuItem("Load fractal");
		this.artist = newArtist;
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
		JMenu designMenu = new JMenu("Designs");
		final JMenuItem changeDesign = new JMenuItem("Change Design");
		changeDesign.setIcon(new DesignTemplateIcon(artist.getCurrentDesign(),25));
		artist.onMenuChange(new Runnable() {
			public void run() {
				changeDesign.setIcon(new DesignTemplateIcon(artist.getCurrentDesign(),25));
			}
		});
		designMenu.add(changeDesign);
		JMenu brushMenu = new JMenu("Tools");
		brushMenu.setIcon(new DesignTemplateIcon(artist.getCurrentTemplate(),25));
		ButtonGroup group = new ButtonGroup();
		int templateNum = 0;
		for(DesignTemplate template : artist.getTemplates()) {
			Icon icon = new DesignTemplateIcon(template,30);
			JMenuItem item = new JCheckBoxMenuItem(icon);
			group.add(item);
			item.addActionListener(new TemplateChanger(templateNum, template, brushMenu));
			templateNum++;
			brushMenu.add(item);			
		}
		
		add(fileMenu);
		add(designMenu);
		add(brushMenu);
		
	}
	
	private class TemplateChanger implements ActionListener {
		
		private int templateNum;
		private DesignTemplate template;
		private JMenu menu;
		
		public TemplateChanger(int templateNum, DesignTemplate template, JMenu menu) {
			this.templateNum = templateNum;
			this.template = template;
			this.menu = menu;
		}
		public void actionPerformed(ActionEvent e) {
			artist.setCurrentTemplate(templateNum);
			menu.setIcon(new DesignTemplateIcon(template,25));
		}
		
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
