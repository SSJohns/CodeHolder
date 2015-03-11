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


public class JScrollPaneExt extends JScrollPane {
	private static List<JScrollPaneExt> scrList = new ArrayList<JScrollPaneExt>();
	private int scrY;
	private boolean canPaint = true;
	
	
	public JScrollPaneExt(Component thing) {
		super(thing);
		
		scrList.add(this);
		this.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
	}
}
