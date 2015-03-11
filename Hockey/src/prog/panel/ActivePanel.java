package prog.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import math.Math2D;
import prog.Controller;
import stat.PlayerController;
import stat.prim.Player;

public class ActivePanel extends JPanelExt {
	List<Face> faceList = new ArrayList<Face>();
	
	public ActivePanel(int x, int y, int w, int h) {
		super(x, y, w, h, true);
		
		List<Player> playerList = PlayerController.getPlayerList();
		for(Player p : playerList)
			faceList.add(new Face(false, p));
	}
	
	public void paintImage(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);	
		
		for(Face f : faceList)
			f.draw(g);
	}
	
	private class Face {
		private final static float BENCH_X = .25f, BENCH_Y = .35f, ACTIVE_X = .75f, ACTIVE_Y = .35f;
		private final static int SIZE = 32;
		private float x, y, toX = 0, toY = 0;
		private boolean isMoving = false, prevPlaying, isPlaying;
		private Player me;
		
		public Face(boolean isPlaying, Player me) {
			this.isPlaying = isPlaying;
			this.me = me;
			
			if(isPlaying) {
				x = ACTIVE_X*w;
				y = ACTIVE_Y*h;
			}
			else {
				x = BENCH_X*w;
				y = BENCH_Y*h;
			}
			
			double dis, dir;
			dis = 32;
			dir = Math.random()*1080;
			
			x += Math2D.lengthdirX(dis, dir);
			y += Math2D.lengthdirY(dis, dir);
		}
		
		public void draw(Graphics g) {		
			isPlaying = me.getOnIce();
			
			if(isPlaying && !prevPlaying) {
				toX = ACTIVE_X*w;
				toY = ACTIVE_Y*h;
				isMoving = true;
			}
			else if(prevPlaying && !isPlaying) {
				toX = BENCH_X*w;
				toY = ACTIVE_Y*h;
				isMoving = true;
			}
			
			if(isMoving) {
				if(Math2D.pointDistance(x, y, toX, toY) > .01) {
					x += (toX - x)/5;
					y += (toY - y)/5;
				}
				else {
					x = toX;
					y = toY;
					isMoving = false;
				}
			}
			else {
				int colNum = 0;
				
				//COLLISIONS
				for(Face f : faceList) {
					if(collide(f))
						colNum++;
					
					//if(colNum > 2)
					//	break;
				}
			}
			
			
			BufferedImage facePic = me.getPhoto();
			g.drawImage(facePic, (int) (x-SIZE/2), (int) (y-SIZE/2), (int) SIZE, (int) SIZE, null);
			
			prevPlaying = isPlaying;
		}
		
		public boolean collide(Face f) {		
			if(f == this)
				return false;
			
			double dis;
			dis = Math2D.pointDistance(x, y, f.x, f.y);
			
			if(dis > SIZE)
				return false;
			
			double dir;			
			dir = Math.random()*360;//Math2D.pointDirection(x, y, f.x, f.y);
			dis = SIZE - dis;
				if(f.isMoving) {
					x -= Math2D.lengthdirX(dis, dir);
					y -= Math2D.lengthdirY(dis, dir);
				}
				else {
					x -= Math2D.lengthdirX(dis/2, dir);
					y -= Math2D.lengthdirY(dis/2, dir);
					f.x += Math2D.lengthdirX(dis/2, dir);
					f.y += Math2D.lengthdirY(dis/2, dir);
				}
			
			return true;
		}
	}
}
