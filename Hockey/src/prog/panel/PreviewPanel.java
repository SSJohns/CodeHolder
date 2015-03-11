package prog.panel;

import java.awt.AWTException;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import processing.core.PImage;
import mpi.cbg.fly.Feature;



public class PreviewPanel extends JPanel 
{
	private int x, y, w, h;
	
	private BufferedImage SPRITE_CURSOR;
	

		
	public PreviewPanel(int x, int y, int w, int h)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;

		
		this.setLayout(null);
		this.setBounds(x, y, w, h);
		this.setBackground(Color.BLACK);
	}
	
	
	
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, w, h);	
	}
	
	
	
	public void startThreads()
	{
		//Create Thread For Updating Feed, Runs at 60 FPS
		Timer fw = new Timer();
		fw.schedule(new TimerTask() {
			public void run() {	
				repaint();

			}
		}, 0, (int) (1000./60));
	}
}
