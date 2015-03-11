package prog.panel;

import io.InputController;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import prog.Controller;

public class JPanelExt extends JPanel {
	private static List<JPanelExt> pnlList = new ArrayList<JPanelExt>();
	protected int x, y, w, h;
	protected JScrollPaneExt curContainer = null;
	
	private BufferedImage img;
	private Graphics imgGraphics;
	private boolean update;
	
	GraphicsConfiguration config;
	
	protected boolean enable = false;
	protected boolean isStatic = true, doDraw = true;

	
	
	public JPanelExt(int x, int y, int w, int h, boolean update) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.update = update;	
		
		
		addMouseListener(InputController.input);
		addMouseMotionListener(InputController.input);
		
		
		
		this.setDoubleBuffered(true);
		
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice device = env.getDefaultScreenDevice();
	    config = device.getDefaultConfiguration();

	    
		
		img = config.createCompatibleImage(w, h, Transparency.OPAQUE);
		imgGraphics = img.getGraphics();
		
		img.setAccelerationPriority(1);
				
		
		this.setLayout(null);
		this.setBounds(x, y, w, h);
		//Set Internal Size
				setPreferredSize(new Dimension(w, h));
				
		pnlList.add(this);
	}
	
	public void setHeight(int newH) {
		//Change Size of Panel
		h = newH;
		setPreferredSize(new Dimension(w, h));
		
		//img = config.createCompatibleImage(w, h, Transparency.OPAQUE);
		//imgGraphics = img.getGraphics();
		
		//Revalidate Any Containers Holding This (To Update Scrollbars, For Instance)
		revalidate();
	}
	
	public void setContainer(JScrollPaneExt pane) {
		curContainer = pane;
	}
	
	public void update() {
		if(enable)
			paintImage(imgGraphics);
	}
	
	public void paintImage(Graphics g) {
	}
	
	public static void updateAll() {
		for(JPanelExt p : pnlList)
			if(p.update)
				if(!p.isStatic) {
					p.update();
					p.doDraw = true;
				}
	}
	
	public void setStatic(boolean value) {
		isStatic = value;
	}
	
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		if(enable && doDraw)
			g2d.drawImage(img, 0,0, null);
		
		doDraw = false;
	}
	
	public Point getPosition() {
		if(curContainer == null)
			return new Point(x, y);
		else
			return new Point(curContainer.getX()+x, curContainer.getY()+y);
	}
}
