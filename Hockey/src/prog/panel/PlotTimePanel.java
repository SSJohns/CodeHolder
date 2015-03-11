package prog.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import stat.EventController;
import stat.PlayerController;
import stat.prim.*;



public class PlotTimePanel extends JPanelExt
{
	private static int eventNum;
	private static List<List<Long>> playerTimeList;
	private static Map<Integer, Integer> playerMap = new HashMap<Integer, Integer>();
	private int b = 30;
	private static long maxTime, minTime;
	private String xLabel = "Event", yLabel = "Total Time";
	private static int totPNum;
	private static double std;
	private double xScale = 0, yScale = 0, frac = 1;
	

		
	public PlotTimePanel(int x, int y, int w, int h)
	{		
		super(x,y,w,h, true);
				
		enable = true;

		h -= 30;
		
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		 	
		
		
		updateGraph();
		
				
		this.setLayout(null);
		this.setBounds(x, y, w, h);
		this.setBackground(Color.BLACK);
	}
	
	public void updateGraph() {
		playerMap.clear();
		

		frac = 0;
		
		totPNum = PlayerController.getTotalDisplayNumber();
		

		
		if(totPNum == 0)
			return;
		
		playerTimeList = new ArrayList<List<Long>>(totPNum);
		List<Season> seasonList = EventController.getSeasonList();
		
		
		eventNum = 0;
		for(Season curSeason : seasonList)
			for(Event curEvent : curSeason.getEventList())
				if(curEvent.getInclude())
					eventNum++;
		
		for(int i = 0; i < totPNum; i++) {
			List<Long> list = new ArrayList<Long>(1+eventNum);
			playerTimeList.add(i, list);
			
			for(int t = 0; t <= eventNum; t++)
				list.add(t, new Long(0));
		}
		
		int i = 0;
		for(Player p : PlayerController.getPlayerList())
			if(p.getDisplay())
				playerMap.put(p.getID(), i++);
		
		
		
		int e = 0;
		for(Season curSeason : seasonList) {	
			for(Event curEvent : curSeason.getEventList()) {
				if(!curEvent.getInclude())
					continue;
					
				//Increment The Number of Games For Each Player
				for(List<Long> list : playerTimeList)
					list.set(1+e, list.get(e));					
				
				for(PlayerTime pt : curEvent.getTimeList()) {
					if(!PlayerController.getPlayer(pt.getID()).getDisplay())
						continue;
						
					long totTime;
								
					i = playerMap.get(pt.getID()).intValue();
					List<Long> list = playerTimeList.get(i);
					totTime = list.get(1+e).longValue() + pt.getTime();
					list.set(1+e, new Long(totTime));					
				}
				
				e++;
			}
		}
		
		
		maxTime = -1;
		minTime = -1;
		
		long finTime, totTime = 0;
		for(i = 0; i < totPNum; i++) {
			finTime = playerTimeList.get(i).get(eventNum);
			totTime += finTime;
			
			if(maxTime == -1 || finTime > maxTime)
				maxTime = finTime;
			if(minTime == -1 || finTime < minTime)
				minTime = finTime;
		}
		
		
		double meanTime = totTime/totPNum;
		double stdSum = 0;
		
		for(i = 0; i < totPNum; i++)
			stdSum += Math.pow(playerTimeList.get(i).get(eventNum) - meanTime,2);
		
		std = Math.sqrt(stdSum/(totPNum-1));
		
		
		
		if(xScale == 0 || yScale == 0) {
			int lX, rX, tY, bY;
			lX = b;
			rX = w-b;
			tY = b;
			bY = h-b;
			xScale = (1.*rX - lX)/(eventNum);
		    yScale = (1.*bY - tY)/(maxTime+10.);
		}
		
		isStatic = false;
	}
	
	
	
	public void paintImage(Graphics g) {
		if(frac < 1) {
			frac += .1;
		}
		else
			frac = 1;
		
		int xP, yP, cX, cY;

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);	
		
		
		
		int lX, rX, tY, bY;
		lX = b;
		rX = w-b;
		tY = b;
		bY = h-b;
		
		double newXS = (1.*rX - lX)/(eventNum),
			   newYS = (1.*bY - tY)/(maxTime+10.);
		
		xScale += (newXS - xScale)/4;
		yScale += (newYS - yScale)/4;
		
		
		if(Math.abs(xScale-newXS) < .01 && Math.abs(yScale-newYS) < .01) {
			xScale = newXS;
			yScale = newYS;
			
			isStatic = true;
		}
		
		
		//Draw Background Lines
		g.setColor(new Color(.5f,.5f,.5f, .3f));
		for(int e = 1; e < eventNum; e++) {
			cX = (int) (lX + xScale*e);
			
			g.drawLine(cX,0, cX,h);
		}
		
		
		
		g.setColor(Color.BLACK);
		for(int p = 0; p < totPNum; p++)
			drawLine(g, playerTimeList.get(p), frac);
			
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, (int) (w*.3), (int) (h*.2));	
		
		g.setColor(Color.BLACK);
		g.drawString("Min: " + minTime/1000 + "s", 0,20);
		g.drawString("Max: " + maxTime/1000 + "s", 0,40);
		g.drawString("Difference: " + (maxTime - minTime)/1000 + "s", 0,60);
		g.drawString("STD: " + Math.round(std/1000) + "s", 0,80);
	}
		
		
	public void drawLine(Graphics g, List<Long> timeList, double fraction) {
		int xP, yP, cX, cY;
		int lX, rX, tY, bY;
		
		lX = b;
		rX = w-b;
		tY = b;
		bY = h-b;
		
		xP = lX;
		yP = bY;
	
		for(int e = 1; e < eventNum+1; e++) {
			cX =  (int) (lX + xScale*e);
			cY =  (int) (bY - yScale*timeList.get(e).longValue());		
			
			
			g.drawLine(xP,yP, cX,cY);
			
			xP = cX;
			yP = cY;				
		}
	}
}
