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



public class BarGraphPanel extends JPanelExt
{
	private static List<Long> playerTimeList;
	private static Map<Integer, Integer> playerMap = new HashMap<Integer, Integer>();
	private int b = 30;
	private static long maxTime, minTime, totTime;
	private String xLabel = "Event", yLabel = "Total Time";
	private static int totPNum;
	private static double std;
	private double xScale = 0, yScale = 0, frac = 1;

		
	public BarGraphPanel(int x, int y, int w, int h)
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
		
		frac = 0;
		
		totPNum = PlayerController.getTotalDisplayNumber();
		
		System.out.println(totPNum);
		
		if(totPNum == 0)
			return;
		
		playerTimeList = new ArrayList<Long>(totPNum);
		List<Season> seasonList = EventController.getSeasonList();
		
		
		for(int i = 0; i < totPNum; i++)
			playerTimeList.add(new Long(0));
		
		
		int n = 0;
		for(Player p : PlayerController.getPlayerList())
			if(p.getDisplay()) {
				playerMap.put(new Integer(p.getID()), n);
				
				n++;
			}
				
		for(Season curSeason : seasonList) {	
			for(Event curEvent : curSeason.getEventList()) {
				if(!curEvent.getInclude())
					continue;
				
				for(PlayerTime pt : curEvent.getTimeList()) {
					if(!PlayerController.getPlayer(pt.getID()).getDisplay())
						continue;
						
								
					int i = playerMap.get(new Integer(pt.getID())).intValue();
					long time = playerTimeList.get(i).longValue();
					time += pt.getTime();
					playerTimeList.set(i, time);					
				}
			}
		}
		
		
		maxTime = -1;
		minTime = -1;
		
		long finTime, totMTime = 0;
		for(int i = 0; i < totPNum; i++) {
			finTime = playerTimeList.get(i);
			totMTime += finTime;
			
			if(maxTime == -1 || finTime > maxTime)
				maxTime = finTime;
			if(minTime == -1 || finTime < minTime)
				minTime = finTime;
		}
		
		/*
		double meanTime = totTime/totPNum;
		double stdSum = 0;
		
		for(i = 0; i < totPNum; i++)
			stdSum += Math.pow(playerTimeList.get(i).get(eventNum) - meanTime,2);
		
		std = Math.sqrt(stdSum/(totPNum-1));
		*/
		
		
		if(xScale == 0 || yScale == 0) {
			int lX, rX, tY, bY;
			lX = b;
			rX = w-b;
			tY = b;
			bY = h-b;
			xScale = (1.*rX - lX)/(totPNum+1);
		    yScale = (1.*bY - tY)/(maxTime+10.);
		}
		
		isStatic = false;
	}
	
	
	
	public void paintImage(Graphics g) {		
		
		if(frac < 1)
			frac += .1;
		else
			frac = 1;
		
		int lX, rX, tY, bY;
		lX = b;
		rX = w-b;
		tY = b;
		bY = h-b;
		
		double newXS, newYS;
		newXS = (1.*rX - lX)/(totPNum+1);
		newYS = (1.*bY - tY)/(maxTime+10.);
		
		xScale += (newXS - xScale)/4;
	    yScale += (newYS - yScale)/4;
	    
	    if(Math.abs(xScale-newXS) < .01 && Math.abs(yScale-newYS) < .01) {
			xScale = newXS;
			yScale = newYS;
			
			isStatic = true;
		}
	    

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);	
		
		
		
		g.setColor(Color.BLACK);
		for(int p = 0; p < totPNum; p++)
			drawBar(g, p, .5, playerTimeList.get(p), frac);
			
		
		
		g.setColor(Color.BLACK);
		g.drawString("Min: " + minTime/1000 + "s", 0,20);
		g.drawString("Max: " + maxTime/1000 + "s", 0,40);
		g.drawString("Difference: " + (maxTime - minTime)/1000 + "s", 0,60);
		g.drawString("STD: " + Math.round(std/1000) + "s", 0,80);
	}
		
		
	public void drawBar(Graphics g, double dX, double dW, Long time, double fraction) {
		int uX, uY, uW, uH;
		int lX, rX, tY, bY;
		
		lX = b;
		rX = w-b;
		tY = b;
		bY = h-b;
		
		
		uX = (int) (lX + xScale*(dX-dW/2));
		uY = (int) (bY - (time*yScale));
		uW = (int) (dW*xScale);
		uH = (int) (time*yScale);
		
		g.fillRect(uX, uY, uW, uH);
	}
}
