package prog.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import prog.panel.PlayerPanel;
import stat.EventController;
import stat.prim.Event;

public class JMenuBarExt extends JMenuBar implements ActionListener {
	private JMenu fileMenu, editMenu, eventMenu, helpMenu;
	private JMenuItem saveAsText, generateRandom, clearGenerated;
	
	
	
	public JMenuBarExt() {
		fileMenu = new JMenu("File");
			//fileMenu.getAccessibleContext().setAccessibleDescription("Save/Load Data, Set Options, Etc.");
			saveAsText = new JMenuItem("Save as Text File");
			saveAsText.addActionListener(this);
				fileMenu.add(saveAsText);
		
			add(fileMenu);
			
		editMenu = new JMenu("Edit");
		
			add(editMenu);
		
		eventMenu = new JMenu("Event");
			generateRandom = new JMenuItem("Generate Random Event");
			generateRandom.addActionListener(this);
				eventMenu.add(generateRandom);
		
			clearGenerated = new JMenuItem("Clear Generated Events");
			clearGenerated.addActionListener(this);
				eventMenu.add(clearGenerated);
			add(eventMenu);
			
		helpMenu = new JMenu("Help");
			add(helpMenu);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == saveAsText)
			PlayerPanel.saveTextFile();
		if(e.getSource() == generateRandom)
			Event.generateRandomEvent();
		if(e.getSource() == clearGenerated)
			Event.clearGenerated();
	}
}
