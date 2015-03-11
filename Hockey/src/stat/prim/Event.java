package stat.prim;

import io.InputController;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import math.Math2D;
import prog.Controller;
import prog.gui.Deletable;
import prog.gui.RightMouseWindow;
import prog.panel.PlayerPanel;
import stat.EventController;
import stat.PlayerController;
import stat.TimeController;

public class Event implements Deletable {
	private static List<Event> eventList = new ArrayList<Event>();
	private Season parent;
	
	private String name, date;
	private long totalTime;
	private List<PlayerTime> timeList;
	private boolean didWin, shouldInclude = false, isValid, isReal;
	
	
	
	public Event(Season parent,  String name, String date, boolean didWin, long totalTime, List<PlayerTime> timeList, boolean isValid, boolean isReal) {
		this.parent = parent;		
		this.name = name;
		this.date = date;
		this.didWin = didWin;
		this.totalTime = totalTime;
		this.timeList = timeList;
		this.isValid = isValid;
		this.isReal = isReal;
		
		eventList.add(this);
	}
	
	//INSTANCE FUNCTIONS
		//PARENT FUNCTIONS
			public void destroy() {
				remove();
				eventList.remove(this);
				parent.removeEvent(this);
			}
		
		//ACCESSOR FUNCTIONS
			public String getName() {
				return name;
			}
			public String getDate() {
				return date;
			}
			public long getTotalTime() {
				return totalTime;
			}
			public List<PlayerTime> getTimeList() {
				return timeList;
			}
			public void setInclude(boolean shouldInclude) {
				this.shouldInclude = shouldInclude;
			}
			public boolean getInclude() {
				return shouldInclude;
			}
			public boolean getValid() {
				return isValid;
			}
			public boolean getIsReal() {
				return isReal;
			}
	
	public void include() {
		if(!shouldInclude) {
			shouldInclude = true;
			
			TimeController.includeEvent(this);
		}
	}
	public void remove() {
		if(shouldInclude) {
			shouldInclude = false;
			
			TimeController.removeEvent(this);
		}
	}
	
	public void draw(Graphics g, int dX, int dY) {
		int mX, mY;
		
		mX = PlayerPanel.getLocalMouseX();
		mY = PlayerPanel.getLocalMouseY();
				
		
		
		g.setColor(Color.BLACK);
		
		if(shouldInclude)
			g.fillRect(dX+4, dY+4, 12,12);
		else
			g.drawRect(dX+4, dY+4, 12,12);
		
		if(!isValid)
		g.setColor(Color.RED);
			g.drawString(date, dX+20, dY+15);
		
		if(Math2D.isInsideBox(mX, mY, dX+4,dY+4,dX+128,dY+16)) {
			InputController.setMouseType(Cursor.HAND_CURSOR);
			
			if(InputController.getLMouseClick()) {
				InputController.consumeLMouseClick();
				
				if(shouldInclude)
					remove();
				else
					include();
			}
			else if(InputController.getRMouseClick()) {
				InputController.consumeRMouseClick();
				
				RightMouseWindow rWin = new RightMouseWindow(144);
				rWin.addDeleteOption("Delete " + date, parent.getEventList(), this);
				rWin.endWindow();
			}
		}
	}


	public boolean getWin() {
		return didWin;
	}
	
	
	
	//GLOBAL FUNCTIONS
		public static void clearGenerated() {	
			List<Event> destroyList = new ArrayList<Event>();
			
			for(Event e : eventList)
				if(!e.isReal)
					destroyList.add(e);
			
			for(Event e : destroyList)
				e.destroy();
		}
		
		public static void generateRandomEvent() {
			long time = (long) (Math.random()*62452411);
			
			List<PlayerTime> timeList = new ArrayList<PlayerTime>();
			for(Player curPlayer : PlayerController.getPlayerList())
				timeList.add(new PlayerTime(curPlayer.getID(), (long)(Math.random()*time)));
			
			
			boolean rndBool = (Math.random() < 0.5);
			
			
			Season.getRandom().addEvent("" + "G", "", rndBool, time, timeList, false);
			Controller.logMessage("Generated random event.");
		}
}