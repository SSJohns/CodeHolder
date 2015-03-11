package prog.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import stat.TimeController;
import math.Math2D;


public class LogPanel extends JPanelExt {
	private List<String> messageList = new ArrayList<String>();
	private List<Date> timeList = new ArrayList<Date>();
	
	private DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss:SS");
	private int minH;

	
	public LogPanel(int x, int y, int w, int h) {
		super(x,y,w,h, true);
		this.minH = h;
		
		enable = true;
	}
	
	
	
	public void paintImage(Graphics g) {
		
		//Clear Background to White
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);
		
		
		int dX, dY;
		dX = 0;
		dY = 15;
		
		//Draw Text Log
		for(int i = messageList.size()-1; i >= 0; i--) {
			//Draw Background Lines
			if(Math2D.isEven(i)) {
				g.setColor(new Color(.9f,.9f,.9f));
				g.fillRect(dX, dY-15, w, 20);
			}
			
			//Draw Current Message
			g.setColor(Color.BLACK);
			g.drawString((i+1) + ")", dX+5, dY);
			g.drawString(messageList.get(i), dX+40, dY);

			//Draw Time of Message
			g.drawString(dateFormat.format(timeList.get(i)), dX+w - 150, dY);

			//Move Drawing Point Down
			dY += 20;
		}
		
		isStatic = true;
	}
	
	public void addMessage(String message) {
		//Add Message To List
		messageList.add(message);
		
		//Add Current Time to List
		Calendar cal = Calendar.getInstance();
		timeList.add(cal.getTime());
		
		//Update Size of Window With New Messages
		setHeight(Math.max(minH, 15 + 10 + (messageList.size()-1)*20));
		
		
		isStatic = false;
	}
}
