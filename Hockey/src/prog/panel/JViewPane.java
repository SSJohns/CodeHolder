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
import javax.swing.JViewport;


public class JViewPane extends JScrollPaneExt {

		
	public JViewPane(Component thing) {
		super(thing);
		
		this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
		this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
	}
}
