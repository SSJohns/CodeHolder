package stat.prim;

import java.awt.image.BufferedImage;

import prog.Controller;
import prog.gui.*;
import prog.panel.PlayerPanel;
import stat.PlayerController;



public class Player implements Deletable, Hideable {
	private int id, gameNum;
	private String number, first, last, position;
	private boolean  shouldDisplay = true;
	private Modifiable<Boolean> isOnIce = new Modifiable<Boolean>(new Boolean(false));
	private BufferedImage photo;
	
	public Player(int id, String number, BufferedImage photo, String first, String last, String position) {
		this.id = id;
		this.number = number;
		this.photo = photo;
		this.first = first;
		this.last = last;
		this.position = position;
	}
	
	public Player(int id, String number, BufferedImage photo, String first, String last, String position, int gameNum) {
		this.id = id;
		this.number = number;
		this.photo = photo;
		this.first = first;
		this.last = last;
		this.position = position;	
		this.gameNum = gameNum;
	}
	
	public void destroy() {
	}
	public void hide() {
		if(shouldDisplay) {
			System.out.println("Hid " + first + " " + last + ".");
			
			System.out.println(PlayerController.getTotalDisplayNumber());
			
			shouldDisplay = false;
			PlayerController.decreaseTotalDisplayNum();
			
			System.out.println(PlayerController.getTotalDisplayNumber());
			
			//Redraw Graph to Include this Player
			Controller.updateGraph();
		}
	}
	public void unhide() {
		if(!shouldDisplay) {
			shouldDisplay = true;
			PlayerController.increaseTotalDisplayNum();
			
			//Redraw Graph to Not Include this Player
			Controller.updateGraph();
		}
	}
	
	//Get and Set Functions
		public int getID() {
			return id;
		}
		
		public String getNumber() {
			return number;
		}
		
		public String getFirstName() {
			return first;
		}
		
		public String getLastName() {
			return last;
		}
		
		public String getPosition() {
			return position;
		}
		
		public BufferedImage getPhoto() {
			return photo;
		}
		
		
		
		public void incrementGameNum() {
			gameNum++;
		}
		public int getGameNum() {
			return gameNum;
		}
		
		
		
		public boolean getDisplay() {
			return shouldDisplay;
		}
		
		public void setOnIce(boolean isOnIce) {
			this.isOnIce.set(new Boolean(isOnIce));
		}
		public boolean getOnIce() {
			return isOnIce.get().booleanValue();
		}
		public Modifiable<Boolean> getModOnIce() {
			return isOnIce;
		}
}
	