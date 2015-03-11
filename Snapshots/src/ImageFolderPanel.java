import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ImageFolderPanel extends JPanel {
	
	private int x, y, w, h;
	private final File FOLDER_PATH;

	private List<Integer> timerList = new ArrayList<Integer>();
	private List<BufferedImage> imageList = new ArrayList<BufferedImage>();
	private List<File> fileList = new ArrayList<File>();
	private List<Float> floatList = new ArrayList<Float>();
	
	private int TIMER_MAX = 3;
	
	private int feedImgNum = 0;
	
	public ImageFolderPanel(int x, int y, int w, int h, final File FOLDER_PATH)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.FOLDER_PATH = FOLDER_PATH;
		
		
		this.setLayout(null);
		this.setBounds(x, y, w, h);
		this.setBackground(Color.BLACK);
		
		
	
		//Create Thread For Updating Folder List, Draw at 1 FPS
		startThreads();
	}
	
	
	
	public void paint(Graphics g) {
		g.setColor(new Color(.9333f, .9333f, .9333f));
		g.fillRect(0, 0, w, h);
		
		int top = 10;
		g.setColor(Color.DARK_GRAY);
		g.fillRoundRect(0, top, w, h-top, 20, 20);
		
	
		int dX, dY, oW, dW, dH, border = 3, spacing = 8;
		
		dX = border;
		
		//Draw Feed, Centered
		int imW, imH;

		//Don't Bother Drawing if No Files or Images are Loaded
		if(timerList.size() == 0 && imageList.size() == 0 && fileList.size() == 0)
			return;		
	
		
		for(int i = 0; i < timerList.size()-imageList.size(); i++)
		{
			imH = 990;
			imW = 1326;
			
			dH = h - 2*border;
			dW = (int) (((double) dH)/imH*imW);
			dY = (int) (h/2. - dH/2.);
			
			g.setColor(new Color(.8f, .8f, .8f, .2f));
			g.fillRoundRect(dX,dY, dW,dH, 6, 6);
			g.setColor(new Color(.8f, .8f, .8f, .3f));
			g.drawRoundRect(dX,dY, dW,dH, 6, 6);
			
			g.setColor(Color.BLACK);
			g.drawString("Loading...", dX+1, dY+dH+1);
			g.setColor(Color.WHITE);
			g.drawString("Loading...", dX, dY+dH);
			
			dX += dW + spacing;
			
			if(dX > w)
				break;
		}
		
		g.setColor(Color.WHITE);
		float imS;
		int oX;
		for(int i = imageList.size()-1; i >= 0; i--)
		{			
			BufferedImage curImg = imageList.get(i);
			
			imW = curImg.getWidth();
			imH = curImg.getHeight();
			imS = floatList.get(i).floatValue();
			
			
			dH = h - 2*border;
			oW = (int) (((double) dH)/imH*imW);
			
			if(imS < 1)
			{
				imS += (1 - imS)/5;
				
				if(imS > .99)
					imS = 1;
				
				dY = (int) (h/2. - dH/2.);

				g.setColor(new Color(.8f, .8f, .8f, .2f));
				g.fillRoundRect(dX,dY, oW,dH, 6, 6);
				g.setColor(new Color(.8f, .8f, .8f, .3f));
				g.drawRoundRect(dX,dY, oW,dH, 6, 6);
				
				g.setColor(Color.BLACK);
				g.drawString("Loading...", dX+1, dY+dH+1);
				g.setColor(Color.WHITE);
				g.drawString("Loading...", dX, dY+dH);
			}
			floatList.set(i, new Float(imS));
			
			String numStr = fileList.get(i).getName().toString();
			//	numStr.replace(".jpg","");
			//	numStr.replace("feedImg","");
			
			
			dW = (int) (oW*imS);
			dH = (int) (dH*imS);
			
			oX = dX;
			dX = (int) (oX + oW/2. - dW/2.);
			dY = (int) (h/2. - dH/2.);
			
			g.drawImage(curImg, dX, dY, dX+dW, dY+dH, 0, 0, imW, imH, this);
			
			g.setColor(Color.BLACK);
			g.drawString(numStr, dX+1, dY+dH+1);
			g.setColor(Color.WHITE);
			g.drawString(numStr, dX, dY+dH);
						
			dX = oX + oW + spacing;
						
			
			if(dX > w)
				break;
		}
	}
	
	
	public void startThreads()
	{
		Timer folderWatcher = new Timer(), repainter = new Timer();
		folderWatcher.schedule(new TimerTask() {
			public void run() {	
				File[] tempFileList = FOLDER_PATH.listFiles();
				boolean success = true;
				
				
				//If No Files Are Present in the Folder, Scrap the Files Currently Loaded
				if(tempFileList == null) {
					imageList.clear();
					fileList.clear();
					timerList.clear();
				}
				else if(tempFileList.length == 0)
					timerList.clear();
				else if(fileList.size() != tempFileList.length) {
					while(timerList.size() < tempFileList.length)
						timerList.add(new Integer(TIMER_MAX));
					
					
					
					
					for(int i = fileList.size(); i < tempFileList.length; i++) {
						//Ensure the Program WAITS to Get New Files
							//(It was running too fast before, and causing all sorts of errors.)
						Integer curTimer = timerList.get(i);
						if(curTimer > -1) {
							timerList.set(i, new Integer(curTimer.intValue() - 1));
							continue;
						}
						
						int k = tempFileList.length;
						
						while(k > 0) {						
							try {
								k--;
								
								if(k < 0)
									break;
								
								File imFile = tempFileList[k];
								boolean didRead = false;
																
								
								if(!fileList.contains(imFile)) {
									if(imFile.exists())	{
										if(imFile.getName().contains(String.valueOf(feedImgNum))) {
											imageList.add(ImageIO.read(imFile));
											fileList.add(imFile);
											floatList.add(new Float(0f));
											
											feedImgNum++;
											break;
										}
									}
								}
								else
									continue;
								
								if(!didRead) {
									success = false;
									continue;
								}
							} catch (IOException e) {
								System.out.println("Crap");
								e.printStackTrace();
								
								success = false;
								break;
							}
						}
					}
				}
			}
		}, 0, (int) (1000./2));
	
		repainter.schedule(new TimerTask() {
			public void run() {
				repaint();
			}
		}, 0, (int) (1000./60));
	}
}
