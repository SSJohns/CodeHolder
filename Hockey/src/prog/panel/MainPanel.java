package prog.panel;

import io.InputController;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;

import math.Math2D;
import prog.Controller;
import prog.gui.RightMouseWindow;
import stat.HeaderController;


public class MainPanel extends JPanelExt {
	float selectX, selectY;
	boolean isSelecting = false;
	int sX, sY, sW, sH;
	
	
	public MainPanel(final int x, final int y, final int w, final int h) {
		super(x, y, w, h, false);
		
		
		Timer paintTimer = new Timer();
		paintTimer.schedule(new TimerTask() {
			public void run() {				
				repaint();
			}
		}, 0, (int) (1000./60));
	}

	public void paint(Graphics g) {	
		InputController.updatePrevious();
		InputController.flushBuffer();
		
		int mX, mY;
		mX = InputController.getAbsoluteMouseX();
		mY = InputController.getAbsoluteMouseY();
		
		boolean didLMC;
		didLMC = InputController.getLMouseClick();
		
		
		InputController.setMouseType(Cursor.DEFAULT_CURSOR);
		
		
		
		if(didLMC)
			if(RightMouseWindow.isMouseOverAny())
				InputController.consumeLMouseClick();
		
		Stopwatch.start();
		
		JPanelExt.updateAll();
		super.paintComponents(g);
		Stopwatch.stop();

		
		Graphics2D g2d = (Graphics2D) g;
		
		
		
		Point p = Controller.getPanelPlayers().getPosition();
		HeaderController.drawHeaders(g, (int) p.getX(), (int) p.getY());

		
		
		//Selection Stuff
			if(InputController.getLMouseClick()) {
				InputController.consumeLMouseClick();
				
				selectX = mX;
				selectY = mY;
				
				isSelecting = true;
			}
			
			
			//Draw Selection Rectangle
			if(InputController.getLMouseDown()) {
				if(isSelecting) {
					if(mX > selectX) {
						sX = Math.round(selectX);
						sW = Math.round(mX - selectX);
					}
					else {
						sX = Math.round(mX);
						sW = Math.round(selectX - mX);
					}
					if(mY > selectY) {
						sY = Math.round(selectY);			
						sH = Math.round(mY - selectY);
					}
					else {
						sY = Math.round(mY);			
						sH = Math.round(selectY - mY);
					}
					
					g.setColor(new Color(0f,0f,0f,.1f));
					g.fillRoundRect(sX, sY, sW, sH, 3,3);
					g.setColor(new Color(0f,0f,0f,.3f));
					g.drawRoundRect(sX+1, sY+1, sW-2, sH-2, 3,3);
					g.setColor(new Color(0f,0f,0f,.8f));				
					g.drawRoundRect(sX, sY, sW, sH, 3,3);
				}
			}
			else
				isSelecting = false;
		
		
		
		//Draw Right Mouse Windows
		RightMouseWindow.drawAll(g2d, didLMC);

		
		Controller.getMain().setCursor(InputController.getMouseType());
		
	}
	
	
	
	public boolean isInsideSelection(int x1, int y1, int x2, int y2) {
		int sX1 = sX, sY1 = sY, sX2 = sX+sW, sY2 = sY+sH;
		boolean b1, b2, b3, b4;
		
		b1 = Math2D.isInsideBox(sX1,sY1, x1,y1,x2,y2);
		b2 = Math2D.isInsideBox(sX2,sY1, x1,y1,x2,y2);
		b3 = Math2D.isInsideBox(sX1,sY2, x1,y1,x2,y2);
		b4 = Math2D.isInsideBox(sX2,sY2, x1,y1,x2,y2);
		
		return (b1 || b2 || b3 || b4 || (x1 < sX2 && sX1 < x2 && y1 < sY2 && sY1 < y2));
	}
}
