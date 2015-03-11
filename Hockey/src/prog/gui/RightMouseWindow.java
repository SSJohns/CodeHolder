package prog.gui;

import io.InputController;
import io.OutputController;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import prog.panel.PlayerPanel;
import math.Math2D;


public class RightMouseWindow {
	private static List<RightMouseWindow> rmwList = new ArrayList<RightMouseWindow>();
	
	int x, y, w, h;
	List<ListItem> myList = new ArrayList<ListItem>();
	public final static int TYPE_DELETE = 0;
	boolean destroy = false;
	private BufferedImage img;
	private float alpha = 1f;
	
	
	public RightMouseWindow(int w) {
		this.x = InputController.getAbsoluteMouseX();
		this.y = InputController.getAbsoluteMouseY();
		this.w = w;
		
		for(RightMouseWindow curRMW : rmwList) {
			curRMW.destroy = true;
		}
		
		rmwList.add(this);
	}
	
	
	private class ListItem {
		int type;
		String name;
		Callable<Object> func = null;
		
		public ListItem() {
			myList.add(this);
		}
	}
	
	private class Option extends ListItem {
		public Option(String name, Callable func) {
			super();
			
			this.name = name;
			this.func = func;
			h += 20;
		}
	}
	
	private class Gap extends ListItem {
		public Gap() {
			super();
			
			name = "";
			h += 10;
		}
	}
	
	
	private class Deleter implements Callable {
		List list;
		Deletable obj;
		
		public Deleter(List list, Deletable obj) {
			this.list = list;
			this.obj = obj;
		}
		
		public Deletable call() throws Exception {
			obj.destroy();
			list.remove(obj);
			return null;
		}	
	}
	
	private class Hider implements Callable {
		Hideable obj;
		
		public Hider(Hideable obj) {
			this.obj = obj;
		}
		
		public Hideable call() throws Exception {
			obj.hide();
			return null;
		}
	}
	
	private class ShowAller implements Callable {
		List<Hideable> lst;
		
		public ShowAller(List<Hideable> lst) {
			this.lst = lst;
		}
		
		public Hideable call() throws Exception {
			for(Hideable obj : lst)
				obj.unhide();
			return null;
		}
	}
	
	private class Opener implements Callable {
		String dirName;
		
		public Opener(String dirName) {
			this.dirName = dirName;
		}
		
		public Object call() throws Exception {
			OutputController.openLocalDirectory(dirName);
			return null;
		}
	}
	
	private class FileOpener implements Callable {
		String fileName;
		
		public FileOpener(String fileName) {
			this.fileName = fileName;
		}
		
		public Object call() throws Exception {
			OutputController.openLocalText(fileName);
			return null;
		}
	}
	
	private class Modifier<K> implements Callable {
		Modifiable<K> var;
		K value;
		
		public Modifier(Modifiable<K> var, K value) {
			this.var = var;
			this.value = value;
		}
		
		public Object call() throws Exception {
			var.set(value);
			return null;
		}
	}
	
	
	
	
	//RIGHT MOUSE WINDOW BUILDING FUNCTIONS
		public void addGap() {
			new Gap();
		}
		
		public void addDeleteOption(String name, List list, Deletable obj) {
			new Option(name, new Deleter(list, obj));
		}
		
		public void addShowAllOption(String name, List<Hideable> list) {
			new Option(name, new ShowAller(list));
		}
		
		public void addHideOption(String name, Hideable obj) {
			new Option(name, new Hider(obj));
		}
		
		public void addOpenDirOption(String name, String dirName) {
			new Option(name, new Opener(dirName));
		}
		
		public void addOpenTextOption(String name, String fileName) {
			new Option(name, new FileOpener(fileName));
		}	
		
		public <K> void addModifyOption(String name, Modifiable<K> var, K value) {
			new Option(name, new Modifier<K>(var, value));
		}	
		
		public void endWindow() {
			img = new BufferedImage(w+1, h+1, BufferedImage.TYPE_INT_ARGB);
	
			Graphics g = img.createGraphics();
			
			
			
			int dY = 0;
			g.setColor(Color.WHITE);
			g.fillRect(0,0, w, h);
			g.setColor(Color.BLACK);
			for(int i = 0; i < myList.size(); i++) {				
				if(myList.get(i).name == "") {
					g.drawLine(0,dY+5,  w,dY+5);
					dY += 10;
					continue;
				}
				
				//g2d.drawRect(dX, dY, w, 20);
				g.drawString(myList.get(i).name, 3, dY + 17);
				
				
				dY += 20;
			}
			
			g.drawRect(0, 0, w,h);
		}
	
	
	public void draw(Graphics2D g2d, boolean mC) {
		int dX = x, dY = y;
		int mX, mY;
		
		mX = InputController.getLocalMouseX();
		mY = InputController.getLocalMouseY();
		
		
		if(!destroy) {			
			g2d.setColor(new Color(0f,0f,0f, .5f));
			g2d.fillRect(dX + 5, dY+4, w,h);
			
			g2d.setColor(Color.WHITE);
			g2d.fillRect(dX, dY, w,h);
			
			g2d.setColor(Color.BLACK);
			for(int i = 0; i < myList.size(); i++) {				
				if(myList.get(i).name == "") {
					g2d.drawLine(dX,dY+5,  dX+w,dY+5);
					dY += 10;
					continue;
				}
				
				//g2d.drawRect(dX, dY, w, 20);
				g2d.drawString(myList.get(i).name, dX+3, dY + 17);
				
				
				if(Math2D.isInsideBox(mX,mY,  dX,dY,dX+w,dY+20)) {
					InputController.setMouseType(Cursor.HAND_CURSOR);
					
					if(mC)
						try {
							myList.get(i).func.call();
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
				
				dY += 20;
			}
			
			g2d.drawRect(dX, y, w,h);
		}
		else {			
			alpha += (0 - alpha)/5f;
			
			if(alpha < .001f)
				alpha = 0f;
				
			
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2d.drawImage(img, dX,dY, null);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		}
	}

	
	public static boolean isMouseOverAny(int mX, int mY) {
		for(RightMouseWindow curRMW : rmwList)
			if(curRMW.isMouseOver(mX, mY))
				return true;
		
		return false;
	}
	
	public boolean isMouseOver(int mX, int mY) {
		return Math2D.isInsideBox(mX,mY,  x,y,x+w,y+myList.size()*20);
	}
	
	public boolean shouldDestroy() {
		return destroy;
	}
	
	
	//GLOBAL FUNCTIONS
		public static void drawAll(Graphics2D g2d, boolean didMC) {
			RightMouseWindow rmv = null;
			
			for(RightMouseWindow curRMW : rmwList) {	
				curRMW.draw(g2d, didMC);
				
				if(didMC)
					curRMW.destroy = true;
				
				if(curRMW.alpha == 0)
					rmv = curRMW;
			}
			
			if(rmv != null)
				rmwList.remove(rmv);
		}

		public static boolean isMouseOverAny() {
			return isMouseOverAny(InputController.getLocalMouseX(), InputController.getLocalMouseY());
		}
}
