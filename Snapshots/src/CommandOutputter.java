import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import com.jhlabs.image.CropFilter;
import com.jhlabs.math.ImageFunction2D;


public class CommandOutputter {
	Thread curAction = null;
	public static BeamPilot pilot = null;

	
	public void startPiloting() {
		if(curAction != null)
			curAction.stop();
		
		//curAction = //new MouseHoldSim(x, y, time);
		//curAction.start();
		
		pilot = new BeamPilot();
		pilot.go();
	}
	
	public void stopPiloting() {
		if(curAction != null)
			curAction.stop();
		
		stopMoving();
		
		pilot.stop();
		pilot = null;
	}
	
	public void click(double x, double y, int time) throws AWTException {
		if(curAction != null)
			curAction.stop();
		
		curAction = new MouseHoldSim((int) x, (int) y, time);
		curAction.start();
	}
	
	public void stopMoving()
	{
		Robot bot;
		try {
			bot = new Robot();
		} catch (AWTException e1) {
			return;
		}
		
	    bot.mouseRelease(InputEvent.BUTTON1_MASK);

		
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		int mX, mY;
		mX = (int) b.getX();
		mY = (int) b.getY();
		
		
	    bot.mouseMove((int) (PreviewPanel.bScrX + PreviewPanel.bScrW/2.), (int) (PreviewPanel.bScrY + PreviewPanel.bScrH/2. - 20));    
		
		bot.mousePress(InputEvent.BUTTON1_MASK);
	    bot.mouseRelease(InputEvent.BUTTON1_MASK);
	    
	    bot.mouseMove(mX, mY);
	}
	
	public void moveForward(int time)
	{
		curAction = new MouseHoldSim((int) (PreviewPanel.bScrX + PreviewPanel.bScrW/2.), (int) (PreviewPanel.bScrY + PreviewPanel.bScrH/2. - 60), time);
		curAction.start();
	}
	
	public void turnRight(int time)
	{
		curAction = new MouseHoldSim((int) (PreviewPanel.bScrX + PreviewPanel.bScrW/2. + 90), (int) (PreviewPanel.bScrY + PreviewPanel.bScrH/2. + 60), time);
		curAction.start();
	}
	
	private class MouseHoldSim extends Thread {
		private int x, y, time;
		
		public MouseHoldSim(int x, int y, int time)
		{
			this.x = x;
			this.y = y;
			this.time = time;
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
			
			
			try {
				sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		    bot.mouseMove((int) x, (int) y-10);    
		    
		    bot.mousePress(InputEvent.BUTTON1_MASK);
		    bot.mouseRelease(InputEvent.BUTTON1_MASK);
		    
		    try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    bot.mouseMove((int) x, (int) y);    
		    
		    

		    try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    
		    bot.mousePress(InputEvent.BUTTON1_MASK);
	    }
	}
	
	public class BeamPilot{
		public boolean isDriving = false, isTurning = false, isForward = false, isParking;
		Timer steer, watchWall;
		
		public BeamPilot()
		{
		}
		
		public void tryForward()
		{
			moveForward(3000);
		}
		
		public void tryTurning()
		{
			turnRight(3000);
		}
		
		public void go()
		{
			isDriving = true;
			isForward = true;
			
			tryTurning();
			
			steer = new Timer();
			steer.schedule(new TimerTask() {
			    public void run() {
				    if(curAction == null)
				    {
				    	if(isForward)
					    	tryForward();
				    	else if(isTurning)
				    		tryTurning();
			    	}
			    }
			}, 0, (int) (1000./10));
		
			watchWall = new Timer();
			watchWall.schedule(new TimerTask() {
			    public void run() {
				    BufferedImage useImg;
				    boolean hasWhite;
				    
				    //Crop to Area Above Vehicle
				    useImg = (new CropFilter(PreviewPanel.cropX, PreviewPanel.cropY, PreviewPanel.cropW, PreviewPanel.cropH)).filter(PreviewPanel.finBotMask, null);
				    
				    int sum = 0;
				    int rgb[] = (int []) useImg.getRaster().getDataElements(0, 0, useImg.getWidth(), useImg.getHeight(), null);
				    for(int i = 0; i < rgb.length; i++)
					    sum += ((rgb[i] >> 16) & 0xff)/255;
				    
				    //System.out.println(sum);
				    
				    
				    hasWhite = (sum > 500);
				    
				    if(InputController.keyEsc)
				    {
				    	if(curAction != null)
				    		curAction.stop();
				    	
				    	curAction = null;
				    	
				    	stop();
				    	stopMoving();
				    	
				    	Controller.isPiloting = false;
				    }
				    else if(hasWhite)
				    {
				    	if(isForward)
				    	{
					    	//System.out.println("abort!!!");
					    	
					    	stopMoving();
					    	
					    	isForward = false;
					    	isTurning = true;
					    	
					    	if(curAction != null)
					    		curAction.stop();
					    	
					    	curAction = null;
				    	}
				    }
				    else if(sum < 100) //Needs to be REALLY empty, The Lines!!
				    {
				    	if(isTurning)
				    	{
				    		stopMoving();
				    		
				    		isTurning = false;
				    		isForward = true;
				    		
				    		if(curAction != null)
				    			curAction.stop();
				    		curAction = null;
				    	}
				    }
			    }
			}, 0, (int) (1000./10));
	    }
		
		public void stop()
		{
			steer.cancel();
			watchWall.cancel();
			
			steer.purge();
			watchWall.purge();
		}
	}
}
