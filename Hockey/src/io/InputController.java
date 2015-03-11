package io;


import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JScrollPane;

import prog.Controller;
import prog.panel.JScrollPaneExt;



public class InputController implements MouseListener, KeyListener, MouseMotionListener {
	public static InputController input;
	
	private static int localMouseX, localMouseY, globalMouseX, globalMouseY, lMouseX, lMouseY, mouseType;
	public static boolean isInReal = false;
	public static boolean keyEsc = false, keySpace = false;
	

	private long FLDClickTime = 0;
	private static int didFLDClick = 0;
	private static boolean 	didLMouseClick = false, isLMouseDown = false, didLMouseClickPrev = false, isLMouseDownPrev = false,
							didRMouseClick = false, isRMouseDown = false;
	private static boolean didLMouseClickC = false, didRMouseClickC = false;
	private static boolean 	didLMouseClickB = false, isLMouseDownB = false,
							didRMouseClickB = false, isRMouseDownB = false;
	
	static JScrollPaneExt currentContainer;
		
	public InputController() {
		input = this;
		
		Timer update = new Timer();
		update.schedule(new TimerTask() {
			public void run() {			
				if(didFLDClick == 1) {
					if(System.currentTimeMillis() - FLDClickTime > 80) {
						didFLDClick = -1;
						//Controller.logMessage("N");
					}
				}
				else if(didFLDClick == 2)
					if(System.currentTimeMillis() - FLDClickTime > 100)
						didFLDClick = -1;
			}
		}, 0, (int) (1000./20));
		
		Timer mw = new Timer();
		mw.schedule(new TimerTask() {
			public void run() {	
				PointerInfo a = MouseInfo.getPointerInfo();
				Point b = a.getLocation();
				
				globalMouseX = (int) b.getX();
				globalMouseY = (int) b.getY();
				
				Point wPt = Controller.getMain().getLocationOnScreen();
				lMouseX = globalMouseX - wPt.x;
				lMouseY = globalMouseY - wPt.y;
			}
		}, 0, (int) (1000./20));
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		localMouseX = e.getX();
		localMouseY = e.getY();
	
		if(e.getButton() == MouseEvent.BUTTON1) {
			isLMouseDownB = didLMouseClickB = true;
			
			if(didFLDClick == 0) {		
				didFLDClick = 1;
				FLDClickTime = System.currentTimeMillis();
			}
			else if(didFLDClick == 2)
				didFLDClick = 3;
		}
		else if(e.getButton() == MouseEvent.BUTTON3)
			isRMouseDownB = didRMouseClickB = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		localMouseX = e.getX();
		localMouseY = e.getY();
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			isLMouseDownB = didLMouseClickB = false;
			
			if(didFLDClick == 1) {
				didFLDClick = 2;
			}
		}
		else if(e.getButton() == MouseEvent.BUTTON3)
			isRMouseDownB = didRMouseClickB = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			keyEsc = true;
		else if(e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			keySpace = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			keyEsc = false;
		else if(e.getKeyCode() == KeyEvent.VK_SPACE)
			keySpace = false;
	}

	
	
	
	@Override
	public void mouseDragged(MouseEvent e) {
		localMouseX = e.getX();
		localMouseY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		localMouseX = e.getX();
		localMouseY = e.getY();
	}
	
	
	
	//GET AND SET
		public static void restoreLMouseClick() {
			//didLMouseClick = true;
		}
		public static void consumeLMouseClick() {
			didLMouseClickC = false;
		}
		public static void consumeRMouseClick() {
			didRMouseClickC = false;
		}
	
		
		public static boolean getLMouseClickPrev() {
			return didLMouseClickPrev;
		}
		public static boolean getLMouseDownPrev() {
			return isLMouseDownPrev;
		}
		
		public static boolean getLMouseClick() {
			return didLMouseClickC;
		}
		public static boolean getLMouseDown() {
			return isLMouseDown;
		}
		public static int getFastLDoubleClick() {
			if(didFLDClick == 3) {
				didFLDClick = 0;
				return 3;
			}
			else if(didFLDClick == -1) {
				didFLDClick = 0;
				return -1;
			}
			else
				return didFLDClick;
		}
		
		public static boolean getRMouseClick() {
			return didRMouseClickC;
		}
		public static boolean getRMouseDown() {
			return isRMouseDown;
		}
		
		public static int getLocalMouseX() {
			int add = 0;
			
			if(currentContainer != null)
				add += -currentContainer.getX();
			
			return lMouseX + add;
		}
		public static int getLocalMouseY() {
			int add = 0;
			
			if(currentContainer != null)
				add += -currentContainer.getY() + currentContainer.getVerticalScrollBar().getValue();
			
			return lMouseY-25 - 20 + add;
		}
		
		public static int getAbsoluteMouseX() {
			return lMouseX;
		}
		public static int getAbsoluteMouseY() {
			return lMouseY-25 - 20;
		}
		
		
		public static void setMouseType(int type) {
			mouseType = type;
		}
		
		public static int getMouseType() {
			return mouseType;
		}

		public static void updatePrevious() {
			isLMouseDownPrev = isLMouseDown;
			didLMouseClickPrev = didLMouseClick;
			
			//isLMouseDown = false;
			didLMouseClick = false;
		}

		public static void flushBuffer() {
			if(didLMouseClickB)
				didLMouseClick = didLMouseClickC = didLMouseClickB;
			if(didRMouseClickB)
				didRMouseClick = didRMouseClickC = didRMouseClickB;
			isLMouseDown = isLMouseDownB;
			isRMouseDown = isRMouseDownB;
			
			didLMouseClickB = false;
			didRMouseClickB = false;
		}

		public static void setCurrentContainer(JScrollPaneExt curPane) {
			currentContainer = curPane;
		}
}
