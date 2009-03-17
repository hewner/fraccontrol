import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class OverallMenu extends JMenuBar {
	ArtistState artist;
	JMenu designMenu;
	JMenu brushMenu;
	
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
		JMenuItem importTemplate = new JMenuItem("Import template from SVG");
		fileMenu.add(importTemplate);
		importTemplate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doImportFromSVG();
			}
		});
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);	
			}
		});
		fileMenu.add(exit);
		

		designMenu = new JMenu("Designs");
		generateDesignMenu();
		
		brushMenu = new JMenu("Tools");
		generateBrushMenu();
		
		add(fileMenu);
		add(designMenu);
		add(brushMenu);
		artist.library().addListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateDesignMenu();
				generateBrushMenu();
			}			
		});
	}

	protected void generateBrushMenu() {
		brushMenu.removeAll();
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
	}
	
	protected void generateDesignMenu() {
		designMenu.removeAll();
		for(DesignTemplate template : artist.getTemplates()) {
			Icon icon = new DesignTemplateIcon(template,30);
			JMenu item = new JMenu("");
			item.setIcon(icon);
			//item.addActionListener(new TemplateChanger(templateNum, template, brushMenu));
			//templateNum++;
			designMenu.add(item);
			for(Design design : template.getDesigns()) {
				JMenuItem designMenuItem = new JMenuItem(new DesignTemplateIcon(design,30));
				item.add(designMenuItem);
				DesignChanger listener = new DesignChanger(designMenuItem,design);
				design.addActionListener(listener);
				designMenuItem.addActionListener(listener);
			}
			JMenuItem newItem = new JMenuItem("New...");
			newItem.addActionListener(new NewDesign(template));
			item.add(newItem);
		}
	}
	
	private class NewDesign implements ActionListener {
		protected DesignTemplate template;
		
		public NewDesign(DesignTemplate template) {
			this.template = template;
		}
		public void actionPerformed(ActionEvent e) {
			artist.makeNewDesign(template);
			generateDesignMenu();
			generateBrushMenu();
			
		}
	}
	
	private class DesignChanger implements ActionListener{

		protected JMenuItem menu;
		protected Design design;
		
		public DesignChanger(JMenuItem menu, Design design) {
			this.menu = menu;
			this.design = design;
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof Design) {
				menu.setIcon(new DesignTemplateIcon(design,30));
				menu.repaint();
			}
			if (e.getSource() == menu) {
				artist.setCurrentDesign(design);
			}
			
		}
		
		
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

	private void doImportFromSVG() {
	    JFileChooser fc = new JFileChooser();
	    
	    if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
	    	try {
				artist.library().templateFromFile(fc.getSelectedFile());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error loading file: " + e, "Cannot load file", JOptionPane.ERROR_MESSAGE);
			}
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
