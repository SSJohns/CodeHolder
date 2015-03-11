import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;



public class InputController implements MouseListener, KeyListener, MouseMotionListener {

	private static int localMouseX, localMouseY;
	public static int mouseX, mouseY, mouseXP, mouseYP;
	public static boolean isInReal = false;
	public static boolean keyEsc = false, keySpace = false;
	
	public static boolean didMouseClick = false, isMouseDown = false;
	
	public InputController() {
		mouseX = 0;
		mouseY = 0;
		
		Timer fw = new Timer();
		fw.schedule(new TimerTask() {
			public void run() {	
				PointerInfo a = MouseInfo.getPointerInfo();
				Point b = a.getLocation();
				
				mouseXP = mouseX;
				mouseYP = mouseY;
				
				int mX, mY;
				mX = (int) b.getX();
				mY = (int) b.getY();
				
				mouseX = mX;
				mouseY = mY;
								
				
				int fPX1, fPY1, fPX2, fPY2;
				fPX1 = 4140;
				fPY1 = 256;
				fPX2 = 4635;
				fPY2 = 633;
				
				int wX1, wY1, wX2, wY2;
				wX1 = 3500;
				wY1 = 198;
				wX2 = 4652;
				wY2 = 780;
				
				
				if(Controller.isBeamOn && Controller.isViewOn) {
					boolean inPreview, inReal, prevInReal, inWindow;
					inPreview = (mX >= fPX1 && mX <= fPX2 && mY >= fPY1 && mY <= fPY2);
					isInReal = inReal = (mX > PreviewPanel.bScrX && mX < PreviewPanel.bScrX+PreviewPanel.bScrW && mY > PreviewPanel.bScrY && mY < PreviewPanel.bScrY+PreviewPanel.bScrH);
					prevInReal = (mouseXP > PreviewPanel.bScrX && mouseXP < PreviewPanel.bScrX+PreviewPanel.bScrW && mouseYP > PreviewPanel.bScrY && mouseYP < PreviewPanel.bScrY+PreviewPanel.bScrH);
					inWindow = (mX >= wX1 && mX <= wX2 && mY >= wY1 && mY <= wY2);
					
					Robot bot;
						try {
							bot = new Robot();
						} catch (AWTException e1) {
							return;
						}
					
					if(inPreview && !prevInReal) {
						bot.mouseMove(PreviewPanel.bScrX + mX-4140, PreviewPanel.bScrY + mY-256);
						bot.mousePress(InputEvent.BUTTON1_MASK);
					    bot.mouseRelease(InputEvent.BUTTON1_MASK);
					}
					else if(prevInReal && !inReal && !inWindow) {
						bot.mouseMove(fPX1 + mX-PreviewPanel.bScrX, fPY1 + mY-PreviewPanel.bScrY);
						bot.mousePress(InputEvent.BUTTON1_MASK);
					    bot.mouseRelease(InputEvent.BUTTON1_MASK);
					}
				}
			}
		}, 0, (int) (1000/60.));
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		isMouseDown = didMouseClick = true;		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isMouseDown = didMouseClick = false;
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
		System.out.println("wtf");

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
		public static boolean getMouseClick() {
			if(didMouseClick) {
				didMouseClick = false;
				return true;
			}
			else
				return false;
		}
		public static boolean getMouseDown() {
			return isMouseDown;
		}
		public static int getLocalMouseX() {
			return localMouseX;
		}
		public static int getLocalMouseY() {
			return localMouseY-25;
		}
}
