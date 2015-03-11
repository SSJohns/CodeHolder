package stat;

import io.InputController;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import math.Math2D;
import prog.Controller;
import prog.gui.Hideable;
import prog.gui.RightMouseWindow;
import prog.panel.PlayerPanel;
import stat.prim.Player;
import stat.prim.Roster;
import stat.prim.Season;



public class PlayerController {
	private static List<Player> playerList = new ArrayList<Player>();
	private static int totalPlayerNum = 0, totalDisplayNum = 0;
	final static int LST_NUM = 0, LST_PHOTO = 1, LST_NAME = 2, LST_POS = 3, LST_TIME = 4, LST_TIMEP = 5, LST_TIMEBAR = 6, LST_GAMENUM = 7, LST_ID = 8, LST_TIMEGAME = 9, LST_ACTIVE = 10, LST_ICETIME = 11, LST_RANK = 12;
	static int deltaH = 64;
	
	private static BufferedImage blankPhoto = null; {
		try {
			blankPhoto = ImageIO.read(new File("src/photo.jpg"));
		}
		catch (IOException e) {
		}
	}
	
	
	
	public static void addPlayer(String number, BufferedImage photo, String first, String last, String pos) {
		playerList.add(new Player(totalPlayerNum, number, photo, first, last, pos, 0));

		//Add Blank Slot to Time, Display Time, and Event Time Lists
		TimeController.addTime(0);
		
		totalPlayerNum++;
		totalDisplayNum++;
	}
	
	public static float calcFullHeight() {
		return deltaH*totalDisplayNum;
	}
	
	public void drawPlayers(Graphics g, int dY) {
		HeaderController.findGapPos();
		
		int scrY, scrH;
		scrY = (int) PlayerPanel.getScrollY();
		scrH = PlayerPanel.getPanelHeight();
		
		//LOOP THROUGH PLAYERS
		for(Player curPlayer : playerList) {			
			if(curPlayer.getDisplay()) {
				if(dY + 64 < 0)
					continue;
				else if(dY - 64 > PlayerPanel.getPanelHeight())
					return;
					
				drawPlayer(curPlayer, g, dY);
				dY += deltaH;
			}
		}
	}
	
	public void drawPlayer(Player player, Graphics g, int y) {
		Header curHeader;
		int dY = y + 15, id, gameNum, dX, mX, mY;
		String number, first, last, position;
		BufferedImage photo;
		boolean isActive;
		
		mX = PlayerPanel.getLocalMouseX();
		mY = PlayerPanel.getLocalMouseY();
		
		
		id = player.getID();
		number = player.getNumber();
		photo = player.getPhoto();
		first = player.getFirstName();
		last = player.getLastName();
		position = player.getPosition();
		gameNum = player.getGameNum();
		isActive = player.getOnIce();
		
		
		List<Header> headerList = HeaderController.getHeaderList();
		int playerWinW = PlayerPanel.getPlayerWinWidth();
		
		
		g.setColor(Color.WHITE);
		g.fillRect(0,y-1, playerWinW,1);
		g.setColor(new Color(.2f,.2f,.2f,.3f));
		g.fillRect(0,y, playerWinW,2);
		g.setColor(new Color(.2f,.2f,.2f,.7f));
		g.drawLine(0, y, playerWinW, y);
		
		g.setColor(Color.BLACK);
		
		float curW, ddX = 0;
		
		//Draw Each Column of Array (Done with For Loop so These Can Be Reordered!!)
		ddX += HeaderController.getGap(0);
	
		
		boolean isMouseOver = false;
		//boolean isMouseOver = (isSelecting && isInsideSelection(0,y,playerWinW,y+deltaH));//isInsideBox(mX,mY, 0,y,playerWinW,y+deltaH);
		
		
		if(Math2D.isInsideBox(mX,mY, 0,y,playerWinW,y+deltaH) && InputController.getRMouseClick()) {
			InputController.consumeRMouseClick();
			
			RightMouseWindow rWin = new RightMouseWindow(144);
			rWin.addModifyOption("Set On Ice", player.getModOnIce(), new Boolean(true));
			rWin.addGap();
			
			rWin.addHideOption("Hide " + first + " " + last, player);
			rWin.addDeleteOption("Delete " + first + " " + last, playerList, player);
			rWin.addGap();
			
			List<Hideable> hList = new ArrayList<Hideable>();
			for(Player p : playerList)
				hList.add((Hideable) p);
			rWin.addShowAllOption("Show All", hList);
			
			rWin.endWindow();
		}

		
		for(int i = 0; i < headerList.size() ; i++) {
			curHeader = headerList.get(i);
			curW = curHeader.w;
				
			
			if(!(HeaderController.isDragging() && HeaderController.getDragHeader() == i)) {	
				dX = Math.round(ddX);
				
				
				if(isMouseOver) {
						g.setColor(new Color(0f,0f,0f,.2f));
						g.fillRect(Math.round(ddX),y, curHeader.w,deltaH);
				}
				
				g.setColor(Color.BLACK);
				switch(curHeader.num) {			
					//Team Number
					case LST_NUM:		g.drawString("# " + number, dX, dY);
										break;
					//Photo
					case LST_PHOTO:		if(photo == null)
											g.drawImage(blankPhoto, dX, dY-10, 48, 48, null);
										else
											g.drawImage(photo, dX, dY-10, 48, 48, null);
										break;
					case LST_NAME:		g.drawString(last + ", " + first, dX, dY);
										break;
					case LST_POS:		g.drawString(position, dX, dY);
										break;
					case LST_TIME:		TimeController.drawTime(g, TimeController.getDispTime(id), dX, dY);
										break;
					case LST_TIMEP:		double timeP;
										timeP = (100.f*TimeController.getDispTime(id)/TimeController.getDispTotalTime());
										
																				
										String str;
										str = new DecimalFormat("0.00").format(timeP)+"%";
										if(timeP < 10)
											str = "  " + str;
										else if(timeP < 100)
											str = " " + str;
										
										g.drawString(str, dX, dY);
										break;
					case LST_TIMEBAR:	g.setColor(Color.WHITE);
										g.fillRect(dX+10,dY-10, 100,10);
										g.setColor(Color.BLACK);
										g.fillRect(dX+10,dY-10, (int) (100*TimeController.getDispTime(id)/TimeController.getDispTotalTime()),10);
										g.drawRect(dX+10,dY-10, 100,10);
										break;
					case LST_GAMENUM:	g.drawString(gameNum + "", dX, dY);
										break;
					case LST_ID:		g.drawString(id + "", dX, dY);
										break;
					case LST_TIMEGAME:	if(gameNum == 0)
											g.drawString("N/A", dX, dY);
										else {
											long numMillisecs2 = Math.round(TimeController.getDispTime(id)/gameNum);
											

											TimeController.drawTime(g, numMillisecs2, dX, dY);
										}
										break;
					case LST_ACTIVE:	if(isActive)
											g.drawString("1", dX, dY);
										else
											g.drawString("0", dX, dY);
										break;
					case LST_ICETIME:	TimeController.drawTime(g, TimeController.getEventTime(id), dX, dY);
										break;
										
					case LST_RANK:		g.drawString("" + PlayerRanker.rankPlayer(player), dX,dY);
										break;
				}
				ddX += curW;
			}
			else {
				int pDX, pDY;
				pDX = mX-HeaderController.getDragDX();
				pDY = mY-HeaderController.getDragDY()+dY;
				
				Graphics mG = g;
				
				mG.setColor(Color.WHITE);
				switch(curHeader.getNumber()) {	
					//Team Number
					case LST_NUM:		mG.drawString("# " + number, pDX, pDY);
										break;
					//Photo
					case LST_PHOTO:		if(photo == null)
											mG.drawImage(blankPhoto, pDX, pDY-10, 48, 48, null);
										else
											mG.drawImage(photo, pDX, pDY-10, 48, 48, null);
										break;
					case LST_NAME:		mG.drawString(last + ", " + first, pDX, pDY);
										break;
					case LST_POS:		mG.drawString(position, pDX, pDY);
										break;
					case LST_TIME:		TimeController.drawTime(mG, TimeController.getDispTime(id), pDX, pDY);
										break;
					case LST_TIMEP:		double timeP;
										timeP = (100.f*TimeController.getDispTime(id)/TimeController.getDispTotalTime());
										
										String str;
										str = new DecimalFormat("0.00").format(timeP)+"%";
										if(timeP < 10)
											str = "  " + str;
										else if(timeP < 100)
											str = " " + str;
										
										mG.drawString(str, pDX, pDY);
										break;
					case LST_TIMEBAR:	mG.setColor(Color.WHITE);
										mG.fillRect(pDX+10,pDY-10, 100,10);
										mG.setColor(Color.BLACK);
										mG.fillRect(pDX+10,pDY-10, (int) (100*TimeController.getDispTime(id)/TimeController.getDispTotalTime()),10);
										mG.drawRect(pDX+10,pDY-10, 100,10);
										break;
					case LST_GAMENUM:	mG.drawString(gameNum + "", pDX, pDY);
										break;
					case LST_ID:		mG.drawString(id + "", pDX, pDY);
										break;
					case LST_TIMEGAME:	if(gameNum == 0)
											mG.drawString("N/A", pDX, pDY);
										else {
											long numMillisecs2 = Math.round(TimeController.getDispTime(id)/gameNum);
											
											
											TimeController.drawTime(mG, numMillisecs2, pDX, pDY);
										}
										break;
				}
			}
			
			ddX += HeaderController.getGap(i+1);
		}
	}
	
	public void hideAllPlayers() {
		totalDisplayNum = 0;
		
		for(Player player : playerList)
			player.hide();
	}
	
	public void showPlayers(Roster roster) {		
		List<Integer> idList = roster.getIDList();
		
		//Hide All Players
		hideAllPlayers();

		//Reenable Only Those In Roster
		for(Integer id : idList)
			playerList.get(id).unhide();
	}
	
	public static void sortPlayersBy(int num, boolean increasing) {
		List<Long> timeList = TimeController.getTimeList();
		Player playerL, playerR;
		Long timeL, timeR;
		boolean didMove, shouldSwitch = false;
		int pNum = playerList.size(); 
					
		
		Controller.logMessage("Sorting players...");
		

		do {
			didMove = false;
			
			
			for(int i = 0; i < pNum-1; i++) {
				playerL = playerList.get(i);
				playerR = playerList.get(i+1);
				timeL = timeList.get(playerL.getID()).longValue();
				timeR = timeList.get(playerR.getID()).longValue();
				
				shouldSwitch = false;
				
				if(increasing) {
					switch(num) {
						case LST_NUM:		shouldSwitch = (Integer.parseInt(playerL.getNumber()) > Integer.parseInt(playerR.getNumber()));
											break;
						case LST_PHOTO:		shouldSwitch = (playerL.getPhoto() == null && playerR.getPhoto() != null);
											break;
						case LST_NAME:		shouldSwitch = (playerL.getLastName().compareTo(playerR.getLastName()) > 0);
											break;
						case LST_POS:		shouldSwitch = (playerL.getPosition().compareTo(playerR.getPosition()) > 0 || (playerL.getPosition().compareTo(playerR.getPosition()) == 0 && playerL.getLastName().compareTo(playerR.getLastName()) > 0));
											break;
						case LST_TIME:		
						case LST_TIMEP:
						case LST_TIMEBAR:	shouldSwitch = (timeL < timeR);
											break;
						case LST_GAMENUM:	shouldSwitch = (playerL.getGameNum() < playerR.getGameNum() || (playerL.getGameNum() == playerR.getGameNum() && playerL.getLastName().compareTo(playerR.getLastName()) > 0));
											break;
						case LST_ID:		shouldSwitch = (playerL.getID() > playerR.getID());
											break;
						case LST_TIMEGAME:	if(playerL.getGameNum() == 0 || playerR.getGameNum() == 0)
												shouldSwitch = playerL.getGameNum() < playerR.getGameNum();
											else
												shouldSwitch = (timeL/playerL.getGameNum() < timeR/playerR.getGameNum());
											break;
					}
				}
				else
					switch(num) {
						case LST_NUM:		shouldSwitch = (Integer.parseInt(playerL.getNumber()) < Integer.parseInt(playerR.getNumber()));
											break;
						case LST_PHOTO:		shouldSwitch = (playerR.getPhoto() == null && playerL.getPhoto() != null);
											break;
						case LST_NAME:		shouldSwitch = (playerL.getLastName().compareTo(playerR.getLastName()) < 0);
											break;
						case LST_POS:		shouldSwitch = (playerL.getPosition().compareTo(playerR.getPosition()) < 0 || (playerL.getPosition().compareTo(playerR.getPosition()) == 0 && playerL.getLastName().compareTo(playerR.getLastName()) > 0));
											break;	
						case LST_TIME:		
						case LST_TIMEP:
						case LST_TIMEBAR:	shouldSwitch = (timeL > timeR);
											break;
						case LST_GAMENUM:	shouldSwitch = (playerL.getGameNum() > playerR.getGameNum() || (playerL.getGameNum() == playerR.getGameNum() && playerL.getLastName().compareTo(playerR.getLastName()) > 0));
											break;
						case LST_ID:		shouldSwitch = (playerL.getID() < playerR.getID());
											break;
						case LST_TIMEGAME:	if(playerL.getGameNum() == 0 || playerR.getGameNum() == 0)
												shouldSwitch = playerL.getGameNum() > playerR.getGameNum();
											else
												shouldSwitch = (timeL/playerL.getGameNum() > timeR/playerR.getGameNum());
											break;
					}
				
				if(shouldSwitch) {
					playerList.set(i, playerR);
					playerList.set(i+1, playerL);
					
					didMove = true;
				}
			}
		} while(didMove);		
	}

	public void loadPlayers(File pFile) {
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(pFile.toPath(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println("Error opening player file.");
			return;
		}

		String id, line, num, first, last, position;
		try {
			while((line = reader.readLine()) != null) {							
				id = line.substring(0, line.indexOf(' '));
					line = line.replace(id + " ", "");
				
				num = line.substring(0, line.indexOf(' '));
					line = line.replace(num + " ", "");
			
				first = line.substring(0, line.indexOf(' '));
					line = line.replace(first + " ", "");
				
				last = line.substring(0, line.indexOf(' '));
					line = line.replace(last + " ", "");


				position = line;
				
				BufferedImage photo = null;
				File photoF = new File("Data/Images/" + id + ".png");
				
				
				if(photoF.exists())
					photo = ImageIO.read(photoF);
				
				addPlayer(num, photo, first, last, position);
			}
		} catch (NumberFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Function for Drawing the Player Toggle List
		public void drawPlayerList(Graphics g, int pLX) {
			int dX = pLX, dY = 20;
			int mX, mY;
			mX = PlayerPanel.getLocalMouseX();
			mY = PlayerPanel.getLocalMouseY();
			
			
			g.setColor(Color.BLACK);
			
			
			List<Season> seasonList = EventController.getSeasonList();
			
			
			//Draw Seasons to Enable Certain Rosters
			for(Season curSeason : seasonList) {
				g.drawString("Enable " + curSeason.getYear() + " Roster", dX + 20, dY+15);
				
				//If Mouse Over Words...
				if(Math2D.isInsideBox(mX, mY, dX+20,dY+4,dX+144,dY+16)) {
					InputController.setMouseType(Cursor.HAND_CURSOR);
		
					if(InputController.getLMouseClick()) {
						InputController.consumeLMouseClick();
						
						showPlayers(curSeason.getRoster());
					}
					else if(InputController.getRMouseClick()) {
						InputController.consumeRMouseClick();
						RightMouseWindow rmWin = new RightMouseWindow(144);
						rmWin.addOpenTextOption("Open Roster File", "/Data/player.dat");
						rmWin.endWindow();

						//rmWin.addOpenTextOption("Open Roster File", "/Data/" + seasonList.get(s).getYear() + "/roster.dat");
					}
				}
				
				dY += 20;
			}
			
			dY += 10;
			
			//Display Active List with Checkboxes
			for(Player curPlayer : playerList) {
			
				g.drawString(curPlayer.getFirstName() + " " + curPlayer.getLastName(), dX + 20, dY+15);
				
				if(Math2D.isInsideBox(mX, mY, dX+4,dY+4,dX+128,dY+16)) {
					InputController.setMouseType(Cursor.HAND_CURSOR);
					
					if(InputController.getLMouseClick()) {
						InputController.consumeLMouseClick();
						
						if(curPlayer.getDisplay())
							curPlayer.hide();
						else
							curPlayer.unhide();
					}
				}
				
				
				//If Playing, Draw Filled Rectangle
				if(curPlayer.getDisplay())
					g.fillRect(dX+4, dY+4, 12,12);
				//Otherwise, Empty Rectangle
				else
					g.drawRect(dX+4, dY+4, 12,12);			
				
				dY += 20;

			}
		}

	public static List<Player> getPlayerList() {
		return playerList;
	}

	public static int getTotalPlayerNumber() {
		return totalPlayerNum;
	}

	public static int getTotalDisplayNumber() {
		return totalDisplayNum;
	}

	public static void increaseTotalDisplayNum() {
		totalDisplayNum++;
	}
	
	public static void decreaseTotalDisplayNum() {
		totalDisplayNum--;
		
		totalDisplayNum = Math.max(0, totalDisplayNum);
	}

	public static Player getPlayer(int id) {
		Player test = playerList.get(id);
		if(test.getID() == id)
			return test;
		
		for(Player p : playerList)
			if(p.getID() == id)
				return p;
			
		return null;
	}
}
