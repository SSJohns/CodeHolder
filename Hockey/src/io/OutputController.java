package io;
import java.awt.AWTException;
import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;

import prog.Controller;


public class OutputController {	
	public static void click(double x, double y) {
		OutputController temp = new OutputController();
		
		(temp.new MouseHoldSim((int) x, (int) y)).start();
	}
	
	public static void openLocalDirectory(String dirName) {
		String workingDir = System.getProperty("user.dir");
		
		try {
			Runtime.getRuntime().exec("open " + workingDir + dirName);
			Controller.getMain().toFront();
			Controller.getMain().setState(Frame.NORMAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void openLocalText(String fileName) {
		String workingDir = System.getProperty("user.dir");
		
		try {
			Runtime.getRuntime().exec("/Applications/TextEdit.app/Contents/MacOS/TextEdit " + workingDir + fileName);
			Controller.getMain().toFront();
			Controller.getMain().setState(Frame.NORMAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private class MouseHoldSim extends Thread {
		private int x, y;
		
		public MouseHoldSim(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
	    public void run() {
		    Robot bot;
			try {
				bot = new Robot();
			} catch (AWTException e1) {
				return;
			}
			
			PointerInfo a = MouseInfo.getPointerInfo();
			Point b = a.getLocation();
			int mX, mY;
			mX = (int) b.getX();
			mY = (int) b.getY();
			
			
		    bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

			try {
				sleep(1);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		    bot.mouseMove((int) x, (int) y);    
		    
		    bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		    bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		    		    

		    try {
				sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    
		    bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		    bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		    bot.mouseMove(mX, mY);
	    }
	}
}
