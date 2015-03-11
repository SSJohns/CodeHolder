package prog.panel;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;


public class JTabbedPaneExt extends JTabbedPane {	
	public JTabbedPaneExt() {
	}
	
	public void addTab(String name, Component thing) {
		JScrollPaneExt s = new JViewPane(thing);
		
		super.addTab(name, s);
	}
}
