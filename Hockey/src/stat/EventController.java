package stat;

import io.InputController;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import math.Math2D;
import prog.Controller;
import prog.gui.RightMouseWindow;
import prog.panel.PlayerPanel;
import stat.prim.Event;
import stat.prim.PlayerTime;
import stat.prim.Roster;
import stat.prim.Season;

public class EventController {
	private static List<Season> seasonList = new ArrayList<Season>();
	
	public static Season getSeason(int year) {
		return seasonList.get(year-2014);
	}
	
	public static List<Season> getSeasonList() {
		return seasonList;
	}
	
	
	public void loadEvents() {
		File dataDir = new File("Data"), curFile, curEventFile;
		File dataFiles[] = dataDir.listFiles(), seasonFiles[];

		List<Event> curEventList;
		List<Integer> rosterList;
				
		if(dataFiles.length == 0)
			return;
		
		long totTime;
		List<PlayerTime> tList;
		BufferedReader reader;
		
		for(int d = 0; d < dataFiles.length; d++) {		
			curFile = dataFiles[d];
			
			if(curFile.isDirectory() && curFile.getName().compareTo("Images") != 0) {			
				seasonFiles = curFile.listFiles();
				
				curEventList = new ArrayList<Event>();
				rosterList = new ArrayList<Integer>();
				
				
				Season newS = new Season(Integer.parseInt(curFile.getName()), null, new Roster(rosterList));
				
				for(int f = 0; f < seasonFiles.length; f++)	{	
					curEventFile = seasonFiles[f];
					
					Controller.logMessage("Loading file \"" + curEventFile.getName() + "\"...");
					
					int type = -1;
					if(curEventFile.getName().contains("P"))
						type = 0;
					else if(curEventFile.getName().contains("G"))
						type = 1;
					
					try {
						reader = Files.newBufferedReader(curEventFile.toPath(), StandardCharsets.UTF_8);
					
						if(curEventFile.getName().compareTo("roster.dat") == 0) {
							String line;

								while((line = reader.readLine()) != null)
										rosterList.add(new Integer(Integer.parseInt(line)));
								
								reader.close();
						}
						else {
							totTime = Long.parseLong(reader.readLine());
							tList = new ArrayList<PlayerTime>();
												
							
							String str, line;
							int id;
							long time;
							boolean isValid = true;
							
							while((line = reader.readLine()) != null) {							
								str = line.substring(0, line.indexOf(' '));
									id = Integer.parseInt(str);
									line = line.replace(str + " ", "");
								
								time = Long.parseLong(line);
								
								if(time > totTime)
									isValid = false;
				
								tList.add(new PlayerTime(id, time));
								if(type == 1)
									PlayerController.getPlayerList().get(id).incrementGameNum();
							}
							
							curEventList.add(new Event(newS, "", curEventFile.getName(), true, totTime, tList, isValid, true));
							
							reader.close();
						}
					} catch (NumberFormatException | IOException e) {
						e.printStackTrace();	
					}
					
					//parent.pnlLog.addMessage("         Loaded file successfully.");
				}
				
				newS.setEventList(curEventList);
				
				EventController.getSeasonList().add(newS);
				includeSeason(newS);
			}
		}
	}
	
	public void includeSeason(Season s) {
		for(Event e : s.getEventList())
			e.include();
	}
	
	
	
	public static void drawEvents(Graphics g, int evX) {
		int dX = evX, dY = 20, mX, mY;
		List<Event> curEventList;
		
		mX = PlayerPanel.getLocalMouseX();
		mY = PlayerPanel.getLocalMouseY();
		
		
		for(Season curSeason : seasonList) {
			g.setColor(Color.BLACK);
			curEventList = curSeason.getEventList();
			
			g.drawString(curSeason.getYear() + " Season", dX, dY+15);
			
			if(Math2D.isInsideBox(mX, mY, dX+4,dY+4,dX+128,dY+16)) {
				InputController.setMouseType(Cursor.HAND_CURSOR);
				
				if(InputController.getLMouseClick()) {
					InputController.consumeLMouseClick();
					
					curSeason.setOpen(!curSeason.getOpen());
				}
				else if(InputController.getRMouseClick()) {
					InputController.consumeRMouseClick();
					RightMouseWindow rmWin = new RightMouseWindow(144);
					
					rmWin.addOpenDirOption("Open \"" + curSeason.getYear() + "\" in Finder", "/Data/" + curSeason.getYear());
					rmWin.endWindow();
				}
			}
			
			
			dY +=20;
			
			if(curSeason.getOpen())
				for(Event curEvent : curEventList) {
					curEvent.draw(g, dX + 32, dY);
					
					dY += 20;
				}
		}
	}

	
}
