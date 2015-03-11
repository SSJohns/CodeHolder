package stat;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import prog.Controller;
import stat.prim.*;

public class TimeController {
	private static List<Double> dispTimeList;
	private long totEventTime, curEventTime, prevEventTime;
	private Timer update = new Timer();
	private List<Player> playerList;
	private static List<Long> eventTimeList, timeList;
	private boolean isPaused = true;
	private static long dispTotalTime = 1, totalTime = 1;
	
	
	public TimeController(List<Player> playerList) {
		this.playerList = playerList;
		
		timeList = new ArrayList<Long>();
		eventTimeList = new ArrayList<Long>();
		dispTimeList = new ArrayList<Double>();
	}


	public void startEvent() {
		//Reset Time Variables
		totEventTime = 0;
	}
	
	public void pauseEvent() {
		isPaused = true;
		update.cancel();
		update.purge();
	}
	
	public void incDispTimeList(double inc) {
		double curValue;
		long toValue;
		
		for(int i = 0; i < dispTimeList.size(); i++) {
			curValue = dispTimeList.get(i).doubleValue();
			toValue = timeList.get(i).longValue();
			
			curValue += (toValue - curValue)/inc;
			
			dispTimeList.set(i, new Double(curValue));
		}
		
		dispTotalTime += (totalTime - dispTotalTime)/inc;
	}
	
	public void resumeEvent() {
		isPaused = false;
		
		//Reset Current Event Time
		curEventTime = System.currentTimeMillis();
		
		//Start Thread
		update = new Timer();
		update.schedule(new TimerTask() {
			public void run() {	
				updatePlayerTime();
			}
		}, 0, (int) (1000./20));
	}
	
	//Function for Updating Player Times
	public void updatePlayerTime() {
		int id;
		long deltaTime, oriTime;
		Long curPlayerTime;
		
		//Update Times
		prevEventTime = curEventTime;
		curEventTime = System.currentTimeMillis();
		
		//Calculate Difference in Times
		deltaTime = curEventTime - prevEventTime;
		
		//Loop through Each Player, Add Delta Time
		for(Player curPlayer : playerList) {
			//Do not Add to Player's Time if Not on Ice
			if(!curPlayer.getOnIce())
				continue;
				
			
			id = curPlayer.getID();
				
			//Get Current Length of Time Player has Played
			curPlayerTime = eventTimeList.get(id);
			oriTime = curPlayerTime.longValue();
			
			//Add to Event Time
			eventTimeList.set(id, new Long(oriTime + deltaTime));
		}
	}
	
	
	
	//TIME ARITHMETIC FUNCTIONS
	public static void includeEvent(Event e) {
		int id;
		long long1, long2;
		
		
		totalTime += e.getTotalTime();
		List<PlayerTime> addList = e.getTimeList();
		
		//Loop Through, Add Each in AddList to AddToList
		for(int i = 0; i < addList.size(); i++) {
			id = addList.get(i).getID();
			long2 = addList.get(i).getTime();
			long1 = timeList.get(id).longValue();
			
			timeList.set(id, new Long(long1 + long2));
		}
		
		Controller.updateGraph();
	}
	public static void removeEvent(Event e) {
		int id;
		long long1, long2;
		
		totalTime -= e.getTotalTime();
		List<PlayerTime> addList = e.getTimeList();
		
		//Loop Through, Subtract Each in Add List from AddToList
		for(int i = 0; i < addList.size(); i++) {
			id = addList.get(i).getID();
			long2 = addList.get(i).getTime();
			long1 = timeList.get(id).longValue();
			
			timeList.set(id, new Long(long1 - long2));
		}
		
		Controller.updateGraph();
	}


	//Overloaded drawTime() Functions
	public static void drawTime(Graphics g, Long time, int dX, int dY) {
		drawTime(g, time.longValue(), dX, dY);
	}
	public static void drawTime(Graphics g, Double time, int dX, int dY) {
		drawTime(g, Math.round(time.doubleValue()), dX, dY);
	}
	
	//Main drawTime() Function
	public static void drawTime(Graphics g, long numMillisecs, int dX, int dY) {
		int numHours, numMinutes, numSecs, numM;
		
		//Get Number of Each Unit of Time
		numHours = (int) Math.round(numMillisecs/1000/60/60);
		numMinutes = (int) Math.round((numMillisecs-numHours*1000*60*60)/1000/60);
		numSecs = (int) Math.round((numMillisecs-numHours*1000*60*60-numMinutes*1000*60)/1000);	
		numM = (int) Math.round(numMillisecs-numHours*1000*60*60-numMinutes*1000*60-numSecs*1000);	
		
		//Format Strings
		String strH = "", strM = "", strS = "", strMS = "";
		if(numHours < 10)
			strH = "0";
		if(numMinutes < 10)
			strM = "0";
		if(numSecs < 10)
			strS = "0";
		if(numM < 10)
			strMS = "00";
		else if(numM < 100)
			strMS = "0";
		strH += numHours;
		strM += numMinutes;
		strS += numSecs;
		strMS += numM;
	
		//Draw Time
		g.drawString(strH + ":" + strM + ":" + strS + ":" + strMS, dX, dY);
	}


	public static List<Long> getTimeList() {
		return timeList;
	}


	public static void addTime(long t) {
		timeList.add(new Long(t));
		dispTimeList.add(new Double(t));
		eventTimeList.add(new Long(t));
	}


	public static double getDispTime(int id) {
		return dispTimeList.get(id).doubleValue();
	}


	public static long getDispTotalTime() {
		return dispTotalTime;
	}


	public static long getEventTime(int id) {
		return eventTimeList.get(id).longValue();
	}
}
