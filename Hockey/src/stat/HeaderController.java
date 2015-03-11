package stat;

import io.InputController;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import prog.Controller;
import prog.panel.PlayerPanel;
import math.Math2D;

public class HeaderController {
	private static List<Header> headerList;
	public final static int LST_NUM = 0, LST_PHOTO = 1, LST_NAME = 2, LST_POS = 3, LST_TIME = 4, LST_TIMEP = 5, LST_TIMEBAR = 6, LST_GAMENUM = 7, LST_ID = 8, LST_TIMEGAME = 9, LST_ACTIVE = 10, LST_ICETIME = 11, LST_RANK = 12;
	private static boolean isDragging;
	private static int dragDX, dragDY;
	private static int dragHeader = -1;
	private static boolean canSort;
	
	//Gap Variables
	private static float[] gaps;
	private static int gapPos = -1;
	private static int prevHeader;

	
	public HeaderController() {
		headerList = new ArrayList<Header>();
		headerList.add(new Header(LST_ID,		"ID", 		 32));
		headerList.add(new Header(LST_NUM,		"#", 		 32));
		headerList.add(new Header(LST_PHOTO, 	"Photo", 	 48));
		headerList.add(new Header(LST_NAME, 	"Name", 	128));
		headerList.add(new Header(LST_POS, 		"Pos.", 	 48));
		headerList.add(new Header(LST_TIME,	 	"Time", 	128));
		headerList.add(new Header(LST_TIMEP, 	"Time %", 	 64));
		headerList.add(new Header(LST_TIMEBAR,	"Time Bar", 128));
		headerList.add(new Header(LST_GAMENUM,	"Game #", 	 64));
		headerList.add(new Header(LST_TIMEGAME,	"Avg Time/Game",128));
		headerList.add(new Header(LST_ACTIVE,	"On Ice", 	64));
		headerList.add(new Header(LST_ICETIME,	"Ice Time", 128));
		headerList.add(new Header(LST_RANK,		"Rank", 48));
		
		gaps = new float[headerList.size()+1];
		clearGapsList();
	}
	
	
	public void drawHeaderBGs(Graphics g, double eventBrightness) {
		Header curHeader;
		float ddX = gaps[0], k, n = .85f; //.95f
		
		for(int i = 0; i < headerList.size(); i++) {
			curHeader = headerList.get(i);
			
			if(!(isDragging && dragHeader == i)) {	
				if(curHeader.num == LST_ACTIVE || curHeader.num == LST_ICETIME)
					k = (float) (eventBrightness/255.);
				else 
					k = n;
				
				g.setColor(new Color(k,k,k));
				g.fillRect(Math.round(ddX),0, curHeader.w, PlayerPanel.getPanelHeight());
	
				ddX += curHeader.w;
			}
			
			
			ddX += gaps[i+1];
		}
	}

	public static void drawHeaders(Graphics g, int drawX, int dY) {
		Header curHeader;
		//DRAW HEADERS
		float ddX = drawX;
		int dX, curNum, toB;
		int overI = -1;
		
		int mX,mY;
		mX = PlayerPanel.getLocalMouseX();
		mY = PlayerPanel.getLocalMouseY();
		boolean mD,mDP;
		mD = InputController.getLMouseDown();
		mDP = InputController.getLMouseDownPrev();
		
		
		ddX += gaps[0];
		
		for(int i = 0; i < headerList.size(); i++) {
			curHeader = headerList.get(i);
			
			if(!(isDragging && dragHeader == i)) {	
				
				toB = 230;
				
				dX = (int) ddX;
				
				g.setColor(new Color(curHeader.brightness/255,curHeader.brightness/255,curHeader.brightness/255));
				g.fillRect(Math.round(ddX),dY, curHeader.w,20);
				
				g.setColor(new Color(1f,1f,1f,1f));
				g.fillRect(Math.round(ddX),dY+4, curHeader.w,9);
				g.setColor(new Color(.2f,.2f,.2f,.05f));
				g.fillRect(Math.round(ddX),dY, curHeader.w,2);
				g.fillRect(Math.round(ddX),dY+11, curHeader.w,9);
				g.fillRect(Math.round(ddX),dY+14, curHeader.w,5);
				g.fillRect(Math.round(ddX),dY+16, curHeader.w,2);
				
				g.fillRect(Math.round(ddX),dY, 1,PlayerPanel.getPanelHeight());
				
				g.setColor(Color.BLACK);
				g.drawString(curHeader.name, Math.round(ddX),dY+17);
					
				if(Math2D.isInsideBox(mX,mY, dX,dY,dX+curHeader.w,dY+20)) {
					overI = i;
					
					if(!isDragging && !mDP && !mD)
					{
						InputController.setMouseType(Cursor.HAND_CURSOR);
						
						clearGapsList();
					
						if(!InputController.getLMouseClick() && !mD)
							g.drawRect(Math.round(ddX),dY, curHeader.w-1,20);
					}
					else if(InputController.getLMouseClick()) {
						InputController.consumeLMouseClick();
						dragHeader = i;
						
						dragDX = (mX - dX);
						dragDY = (mY - dY);
						
						canSort = true;
					}
					else {
						if(!isDragging) {
							if(mD) {
								InputController.setMouseType(Cursor.HAND_CURSOR);
		
								toB = 200;
							}
							else if(mDP && !mD) {	
								dragHeader = -1;
								curNum = curHeader.num;
								
								if(canSort) {
									PlayerPanel.resetSortAlpha();
				
									if(prevHeader != curNum) {
										PlayerController.sortPlayersBy(curNum, true);
										prevHeader = curNum;
									}
									else {
										PlayerController.sortPlayersBy(curNum, false);
										prevHeader = -1;
									}
								}
							}
						}
					}
				}
				else
					if(dragHeader == i) {
						InputController.setMouseType(Cursor.HAND_CURSOR);
						
						isDragging = true;
						canSort = false;
						
						if(i == 0)
							gaps[0] = curHeader.w;
						else
							gaps[i] = curHeader.w;						
					}
						
				curHeader.brightness += (toB - curHeader.brightness)/3;
					
				ddX += curHeader.w;
			}
			else {
				g.setColor(Color.WHITE);
				g.drawString(curHeader.name, mX-dragDX, mY-dragDY+17);
			}
				
			
			
			ddX += gaps[i+1];
		}
		
		if(isDragging && dragHeader != -1 && overI != dragHeader) {
			g.setColor(new Color(0f, 0f, 0f, .2f));
			g.drawRect(mX-dragDX,mY-dragDY, headerList.get(dragHeader).w, 20);
			
			if(!mD) {
				dragHeader = -1;
				isDragging = false;
			}
		}
	}
	
	public static void clearGapsList() {
		for(int i = 0; i < gaps.length; i++)
			gaps[i] = 0;
	}

	public static float getGap(int num) {
		return gaps[num];
	}
	
	public static void setGap(int num, float newGap) {
		gaps[num] = newGap;
	}
	
	public static void addGap(int num, float add) {
		gaps[num] += add;
	}


	public static void findGapPos() {
		gapPos = -1;
		int gapL=-1, gapR=-1, mX, mY;
		boolean mD, mDP;
		mX = PlayerPanel.getLocalMouseX();
		mY = PlayerPanel.getLocalMouseY();
		mD = InputController.getLMouseDown();		
		mDP = InputController.getLMouseDownPrev();

		
		//FIND GAP SPOT
		if(isDragging && dragHeader != -1) {
			float dX;
			dX = Math.round(gaps[0]);
			
			for(int i = 0; i < headerList.size() ; i++) {
				Header curHeader = headerList.get(i);
				
				if(!(isDragging && dragHeader == i)) {
					if(gapL == -1) {
						if(mX > dX+curHeader.w/2)
							gapL = i;
					} 
					else if(gapR == -1)
						if(mX < dX+curHeader.w/2)
							gapR = i;
						
					dX += curHeader.w;
				}
				
				dX += Math.round(gaps[i+1]);
			}
			
			
			if(gapPos == -1) {
				//If There is No Gap Leftward of the Mouse, Must Be First Gap
				if(gapL == -1)
					gapPos = 0;
				//If There is No Gap Rightward of the Mouse, Must be Last Gap
				else if(gapR == -1)
					gapPos = gaps.length-1;
				//Otherwise, Use GapR
				else
					gapPos = gapR;
			}
			
			//If Gap Is In a Certain Spot, Subtract 1 As Fix
			if(gapPos == dragHeader+1)
				gapPos--;
			
			
			//Shrink Gaps Other than the Opening One
			float curMinus, totMinus = 0;
			for(int i = 0; i < gaps.length; i++)
				if(i != gapPos && gaps[i] > 0) {
					curMinus = (0 - gaps[i])/3;
					gaps[i] += curMinus;
					
					totMinus -= curMinus;
				}
			
			
			//Add Total Subtracted From Other Gaps to Opening Gap (Want Constant Width)
			gaps[gapPos] += totMinus;
			
			
			//If Mouse Was Previously Dragged and Now Isn't, Drop Header into Place
			if(mDP && !mD) {
				Header dragCopy = headerList.get(dragHeader);
				headerList.remove(dragCopy);
				
				int pos = gapPos;
				
				if(pos <= headerList.size()) {
					if(pos > dragHeader+1)
						pos--;
					headerList.add(pos, dragCopy);
				}
				else
					headerList.add(dragCopy);
				
				dragHeader = -1;
				isDragging = false;
				
				//Set All Gaps to 0
				HeaderController.clearGapsList();
			}
		}
	}


	public static int calcHeadersWidth() {
		int w = 0;
		
		//Add The Width of Each Individual Header
		for(int i = 0; i < headerList.size(); i++)
			w += headerList.get(i).getWidth();
		
		//Return the Total Width
		return w;
	}


	public static List<Header> getHeaderList() {
		return headerList;
	}


	public static int getDragDY() {
		return dragDY;
	}


	public static int getDragDX() {
		return dragDX;
	}


	public static boolean isDragging() {
		return isDragging;
	}


	public static int getDragHeader() {
		return dragHeader;
	}
}
