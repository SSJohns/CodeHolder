package prog.panel;

import io.InputController;
import io.OutputController;

import java.awt.AWTException;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import math.Math2D;
import prog.Controller;
import prog.gui.*;
import stat.EventController;
import stat.Header;
import stat.HeaderController;
import stat.PlayerController;
import stat.TimeController;
import stat.prim.*;


public class PlayerPanel extends JPanelExt {
	private static PlayerPanel pPnl;
	private InputController input;
	private TimeController timeCon;

		
	private Button hoverButton = null, selectedButton = null;
	int selectedID = 0;
	
	private static double scrY = 0;
	
		
	
	
	
	//Scrollbar Variables
	boolean moveBar = false;
	int barX, barY = 20, barW = 16, barH = 64;
	static int playerWinW;
	float barB = .2f;
	
	int mX, mY;
	int fLMDC = 0;
	boolean lMCP = false, rMCP;
	int dragDX, dragDY, dragHeader = -1, overI;
	boolean isDragging = false;
	
	int toBarY = 0;
	private double barYD = 0;
	boolean isAutoScrolling = false;
	
	int eventBrightness = 100;
	
	private static float sortAlpha = 0;
	
	

	public static boolean isInEvent = false, isEventActive = false;
	
	
	int pLX, evX;
	
	
	
	List<Button> buttonList = new ArrayList<Button>();
		
	

	
	private PlayerController playerCon;
	private EventController eventCon;
	private HeaderController headerCon;
	
	

	
	

	
	
	public PlayerPanel(int x, int y, int w, int h)
	{
		super(x, y, w, h, true);
		
		pPnl = this;
		
		enable = true;
		isStatic = false;
		
		
		playerCon = new PlayerController();
		timeCon = new TimeController(PlayerController.getPlayerList());
		headerCon = new HeaderController();
		eventCon = new EventController();
		
		
		barX = playerWinW = calcPlayerWindowWidth();
		pLX = playerWinW+barW;
		evX = pLX + 196;
		
		
		
		//Load Players In
		playerCon.loadPlayers(new File("Data/player.dat"));		
		eventCon.loadEvents();
		

		this.addKeyListener(input);	
	}
	

	
	//Function for Determining if the Displayed Players Fill the Panel
	public boolean doPlayersFillScreen() {
		return PlayerController.calcFullHeight() > h-20;
	}
	
	
	
	
	


	
	public class Drawable {
		public void draw(Graphics g) {
		}
	}

	
	public class Button extends Drawable{
		int id, x, y, w, h;
		double brightness = 0;
		String text;
		
		public Button(int id, int x, int y, int w, int h, String text) {
			super();
			
			this.id = id;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			
			this.text = text;
			
			buttonList.add(this);
		}
		
		public void draw(Graphics g) {
			if(selectedButton == this)
				brightness += (180 - brightness)/3;
			else if(hoverButton == this)
				brightness += (150 - brightness)/10;
			else
				brightness += (80 - brightness)/10;
			
			g.setColor(new Color((int)brightness, (int)brightness, (int)brightness));
			g.fillRect(x, y, w, h);
			
			g.setColor(Color.BLACK);
			g.drawRect(x, y, w, h);
			
			g.setColor(Color.WHITE);
			g.drawString(text, x+10, y+h -4);
		}
	}

	
	
	
	public void paintImage(Graphics g) {
		boolean shouldStatic = false;
		
		
		g.setColor(Color.WHITE);
			g.fillRect(0, 0, w, h);
		//g.setColor(new Color(.9333f, .9333f, .9333f, .3f));
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, w, h);
		
		
		headerCon.drawHeaderBGs(g, eventBrightness);
		
		timeCon.incDispTimeList(3.);
		

		
		//Set Current Container, so Correct Mouse Position is Used
		InputController.setCurrentContainer(curContainer);
		scrY = curContainer.getVerticalScrollBar().getValue();

		
		mX = getLocalMouseX();
		mY = getLocalMouseY() - mY;
	
		
		fLMDC = InputController.getFastLDoubleClick();
		
		
		
		
		int toEB;
		if(isInEvent) {
			if(isEventActive)
				toEB = 255;
			else
				toEB = 230;
		}
		else
			toEB = 100;
		eventBrightness += (toEB - eventBrightness)/5.;
		
				
		
		
		
		playerCon.drawPlayers(g, 20 + (int) -(((1.*barY-20)/(h-barH-20))*(PlayerController.calcFullHeight()-h+20)));

		
		for(int i = 0; i < buttonList.size(); i++)
			buttonList.get(i).draw(g);
			
		g.setColor(Color.BLACK);
		g.drawRect(0,0, w-1	,h-1);
		
		
		if(h != (int) PlayerController.calcFullHeight()+20)
			setHeight((int) PlayerController.calcFullHeight()+20);
		playerCon.drawPlayerList(g, pLX);
		
		
		
		
		//Draw Flash When Resorted
		g.setColor(new Color(1f,1f,1f, sortAlpha));
		g.fillRect(0, 20, playerWinW, h);
		sortAlpha += (0 - sortAlpha)/4;
		
		InputController.setCurrentContainer(null);
		
		isStatic = shouldStatic;
	}
	
	

	public void cloneListIntoList(List listDest, List listSrc) {
		System.out.println("Initial Size: " + listDest.size());
		listDest.clear();
		
		int s = listSrc.size();
		for(int i = 0; i < s; i++)
			listDest.add(listSrc.get(i));
		
		System.out.println("Final Size: " + listDest.size());
	}
	
	public int calcPlayerWindowWidth() {
		return HeaderController.calcHeadersWidth();
	}
	
	public void startEvent() {
		isInEvent = true;
		isEventActive = false;

		timeCon.pauseEvent();
	}
	
	


	public void autoscrollToBottom() {
		barYD = barY;
		toBarY = h - barH;
		isAutoScrolling = true;
	}

	public void reversePlaying() {
		isEventActive = !isEventActive;
		
		if(isEventActive)
			timeCon.resumeEvent();
		else
			timeCon.pauseEvent();
	}
	
	public static void resetSortAlpha() {
		sortAlpha = 1;
	}



	public static int getPanelHeight() {
		return pPnl.h;
	}



	public static int getPlayerWinWidth() {
		return playerWinW;	
	}
	
	public static int getLocalMouseX() {	
		return InputController.getLocalMouseX()-pPnl.x;
	}
	
	public static int getLocalMouseY() {
		return InputController.getLocalMouseY()-pPnl.y;
	}
	
	
	
	public static boolean saveTextFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(Paths.get("").toAbsolutePath().toFile());
		
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "Text Files", "*.txt");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showSaveDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	File outFile = chooser.getSelectedFile();
	    	
	    	String fileName = outFile.getName();
	    	if(!fileName.endsWith(".txt"))
	    		fileName = fileName + ".txt";
	    		
	    	saveTextFile(fileName);
	    	
	    	return true;
	    }
	    
	    return false;
	}
	
	public static boolean saveTextFile(String fileName) {
		try {
			PrintWriter writer;
			writer = new PrintWriter(fileName, "UTF-8");
			
			List<Player> pList = PlayerController.getPlayerList();
			for(Player p : pList) {
				writer.print(p.getLastName() + ", " + p.getFirstName());
				
				writer.println();
			}
			
			writer.close();
			return true;
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static double getScrollY() {
		return scrY;
	}
	
	public static void unstatic() {
		pPnl.setStatic(false);
	}
}
