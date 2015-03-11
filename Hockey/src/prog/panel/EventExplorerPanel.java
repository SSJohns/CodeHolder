package prog.panel;

import io.InputController;

import java.awt.Color;
import java.awt.Graphics;

import prog.Controller;
import stat.EventController;

public class EventExplorerPanel extends JPanelExt {
	
	
	public EventExplorerPanel(int x, int y, int w, int h) {
		super(x, y, w, h, true);
	}
	
	public void paintImage(Graphics g) {
		InputController.setCurrentContainer(curContainer);
		
		g.setColor(Color.WHITE);
		g.fillRect(0,0, w,h);
		EventController.drawEvents(g, 20);
		InputController.setCurrentContainer(null);
	}
}
