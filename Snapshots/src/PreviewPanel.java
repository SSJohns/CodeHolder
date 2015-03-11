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

import com.jhlabs.image.CropFilter;
import com.jhlabs.image.EdgeFilter;
import com.jhlabs.image.LaplaceFilter;
import com.jhlabs.image.MaskFilter;
import com.jhlabs.image.MedianFilter;
import com.jhlabs.image.OffsetFilter;
import com.jhlabs.image.RescaleFilter;
import com.jhlabs.image.RotateFilter;
import com.jhlabs.image.ThresholdFilter;


public class PreviewPanel extends JPanel 
{
	private int x, y, w, h;
	public static int tScrX, tScrY, tScrW, tScrH, bScrX, bScrY, bScrW, bScrH;
	private BufferedImage topPreview, botPreview, topMask, botMask, prevBotPreview;
	public static BufferedImage finBotMask, finTopMask, tempHolder;
	private final CaptureHandler VIDEO_FEED;
	private byte[] pixels;
	private boolean overBottom = false; 
	public static int cropX, cropY, cropW, cropH;
	
	private BufferedImage SPRITE_CURSOR;
	
	
	ArrayList<Feature> curFeatures = null;
	ArrayList<Feature> prevFeatures = null;

	
	private int num = 0;
	
	public PreviewPanel(int x, int y, int w, int h, final CaptureHandler VIDEO_FEED)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.VIDEO_FEED = VIDEO_FEED;
		
		topPreview = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		botPreview = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		prevBotPreview = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		botMask = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		topMask = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		finBotMask = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		finTopMask = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		
		this.setLayout(null);
		this.setBounds(x, y, w, h);
		this.setBackground(Color.BLACK);
		
		try {
			(new File("Resources/Junk/")).mkdir();
			SPRITE_CURSOR = ImageIO.read(new File("Resources/cursor.png") );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		
			SPRITE_CURSOR = null;
		}
	}
	
	
	
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, w, h);
		
		//Draw Feed, Centered
			double pnlRatio, capRatio;
			int dX, dY, dW, dH;
			
			pnlRatio = (double) w/h;
			capRatio = (double) tScrW/tScrH;
			
			
			dY = 0;
			dH = h;
			
			dW = (int) (dH*capRatio);
			dX = 0;
			
			if(Controller.isTopMask)
				g.drawImage(finTopMask, dX, dY, dX+dW, dY+dH, 0, 0, tScrW, tScrH, this);
			else
				g.drawImage(topPreview, dX, dY, dX+dW, dY+dH, 0, 0, tScrW, tScrH, this);
			
			
			//g.drawImage(RoomMapper.roomImage, 0, 0, 400, 400, this);
			//g.drawImage(tempHolder, 0, 0, this);
			
			g.setColor(Color.WHITE);
			//g.drawString(String.valueOf(RoomMapper.direction), 0, 20);
			
			
			//Shift Over Drawing Coordinates
				capRatio = (double) bScrW/bScrH;
				
				dY = 0;
				dH = h;
				dW = (int) (dH*capRatio);
				dX = w-dW;
				
			
			//Draw Bottom Mask if Button Enabled, Otherwise, Normal Bottom
		
				if(Controller.isBotMask)
				{
					g.drawImage(finBotMask, dX, dY, dX+dW, dY+dH, 0, 0, bScrW, bScrH, this);
					g.setColor(Color.WHITE);
					g.drawRect(dX + cropX, cropY, cropW, cropH);
				}
				else
					g.drawImage(botPreview, dX, dY, dX+dW, dY+dH, 0, 0, bScrW, bScrH, this);
			
			
			if(InputController.isInReal)
			{
				int fPX1, fPY1, fPX2, fPY2;
				fPX1 = 4140;
				fPY1 = 256;
				fPX2 = 4635;
				fPY2 = 633;
				
				int mX, mY;
				mX = dX + (int) ((InputController.mouseX - PreviewPanel.bScrX)*((fPX2 - fPX1)/((double) PreviewPanel.bScrW)));
				mY = (int) ((InputController.mouseY - PreviewPanel.bScrY)*((fPY2 - fPY1)/((double) PreviewPanel.bScrH)));
				
				
				g.drawImage(SPRITE_CURSOR, mX, mY, this);
				//g.fillOval(dX, dY, dW, dH);
			}
			
			
	}
	
	
	
	public void setScreenRegion(int x, int y, int w, int h) {
		tScrX = x;
		tScrY = y;
		tScrW = w;
		tScrH = h;
 	}
	
	public void findScreenRegion()
	{
		boolean success = false;
		
		/*try {
	        String line;
	        Process p = Runtime.getRuntime().exec("ps -e");
	        BufferedReader input =
	                new BufferedReader(new InputStreamReader(p.getInputStream()));
	        while ((line = input.readLine()) != null) {
	        	if(line.contains("bin/beam"))
	        	{
	        		success = true;
	        		break;
	        	}
	        }
	        
	        input.close();
	        
	        Controller.isBeamOpen = success;
	        if(!success)
	        	return;
	    } catch (Exception err) {
	        err.printStackTrace();
	    }*/
		
		Controller.isBeamOpen = true;
		
		
		int cX, cY, cW, lX, rX, tY, bY, bLX, bRX, bBY, bTY;
		
		lX = tScrX;
		rX = tScrX+tScrW;
		tY = tScrY;
		bY = tScrY+tScrH;
		
		
		BufferedImage screen;
		try {
			screen = VIDEO_FEED.getScreen();
		} catch (HeadlessException | AWTException e) {
			return;
		}
		
		
		cY = 700;
		cW = screen.getWidth();
		int pixel[], newPixel[];
		
		
		//FIND LEFT SIDE
		cX = 5;
			newPixel = getRGB(screen, cX, cY, cW);
			do
			{
				pixel = newPixel;
		
				cX++;		
				
				//Beam Window Open, but Bot Not Active!!
				if(cX > cW/2)
				{
					Controller.isBeamOn = false;
					return;
				}
				
				newPixel = getRGB(screen, cX, cY, cW);
	
			} while(nearColor(pixel, newPixel, 4));
		lX = cX;

		//FIND RIGHT SIDE
		cX = screen.getWidth()-5;
			newPixel = getRGB(screen, cX, cY, cW);
			do
			{
				pixel = newPixel;
		
				cX--;		
				newPixel = getRGB(screen, cX, cY, cW);
	
			} while(nearColor(pixel, newPixel, 4));
		rX = cX;
		
		//FIND TOP
		cX = 700;
		cY = 100;
		newPixel = getRGB(screen, cX, cY, cW);
			do
			{
				pixel = newPixel;
		
				cY++;		
				newPixel = getRGB(screen, cX, cY, cW);
	
			} while(mostlyBlue(newPixel, 40));
		tY = cY;
		
		//FIND BOTTOM
		cX = lX+5;
		cY = screen.getHeight()-5;
			newPixel = getRGB(screen, cX, cY, cW);
			do
			{
				pixel = newPixel;
		
				cY--;		
				newPixel = getRGB(screen, cX, cY, cW);
	
			} while(nearColor(pixel, newPixel, 4));
		bY = cY;
		
		
		//FIND LEFT SIDE
		cX = 5;
		cY = bY+1;
			newPixel = getRGB(screen, cX, cY, cW);
			do
			{
				pixel = newPixel;
		
				cX++;		
				newPixel = getRGB(screen, cX, cY, cW);
	
			} while(nearColor(pixel, newPixel, 4));
		bLX = cX;

		//FIND RIGHT SIDE
		cX = screen.getWidth()-5;
			newPixel = getRGB(screen, cX, cY, cW);
			do
			{
				pixel = newPixel;
		
				cX--;		
				newPixel = getRGB(screen, cX, cY, cW);
	
			} while(nearColor(pixel, newPixel, 4));
		bRX = cX;
		
		
		Controller.isBeamOn = true;
		
		
		Color DARK_BLUE = new Color(.1255f, .4235f, .7176f);
		
		

		
				
		tScrX = lX;
		tScrY = tY;
		tScrW = rX-lX;
		tScrH = bY-tY;
		
		bScrX = bLX;
		bScrY = bY;
		bScrW = bRX - bLX;
		bScrH = screen.getHeight() - bY;
		
		cropX = (int) (PreviewPanel.bScrW/3.);
		cropY = (int) (PreviewPanel.bScrH/10.);
		cropW = (int) (PreviewPanel.bScrW/3.);
		cropH = (int) (PreviewPanel.bScrH/4.);
	}
	
	public void nearGray(float R, float G, float B, float amt)
	{
		
	}
	
	public boolean nearColor(int R, int G, int B, int cR, int cG, int cB, int amt)
	{
		boolean nearR, nearG, nearB;
		
		nearR = (Math.abs(R - cR) < amt);
		nearG = (Math.abs(G - cG) < amt);
		nearB = (Math.abs(B - cB) < amt);
		
		return (nearR && nearG && nearB);
	}
	
	public boolean nearColor(int color1[], int color2[], int amt)
	{
		boolean nearR, nearG, nearB;
		
		nearR = (Math.abs(color1[0] - color2[0]) < amt);
		nearG = (Math.abs(color1[1] - color2[1]) < amt);
		nearB = (Math.abs(color1[2] - color2[2]) < amt);
		
		return (nearR && nearG && nearB);
	}
	
	public boolean mostlyBlue(int color[], int amt)
	{		
		return (color[2] > color[0]+amt && color[2] > color[1]+amt);
	}
	
	
	
	private int[] getRGB(BufferedImage image, int x, int y, int w) {
		//int rgb[] = new int[3];
		
		//int pixel = (x + w*y) * 3;
	    
		//rgb[2] = ((int) pixels[pixel] & 0xff); // blue
		//rgb[1] = (((int) pixels[pixel + 1] & 0xff) << 8); // green
		//rgb[0] += (((int) pixels[pixel + 2] & 0xff) << 16); // red
		

		return image.getRaster().getPixel(x, y, new int[3]);//rgb;
	}
	
	
	private Color getColor(BufferedImage image, int x, int y, int w) {
		int rgb[] = new int[3];
		
		int pixel = (x + w*y) * 3;
	    
		rgb[2] = ((int) pixels[pixel] & 0xff); // blue
		rgb[1] = (((int) pixels[pixel + 1] & 0xff) << 8); // green
		rgb[0] += (((int) pixels[pixel + 2] & 0xff) << 16); // red
		

		return new Color((float) (rgb[0]/255.), (float) (rgb[1]/255.), (float) (rgb[2]/255.));
	}
	
	
	public void getMask()
	{			
		MedianFilter lFilt = new MedianFilter();
		ThresholdFilter tFilt = new ThresholdFilter(100);
		EdgeFilter eFilt = new EdgeFilter();

		if(Controller.isTopMask)
		{		
			topMask.flush();
			try {
				topMask = VIDEO_FEED.getRect(tScrX, tScrY, tScrW, tScrH);
				
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			//RescaleFilter rFilt = new RescaleFilter(.5f);
			//rFilt.filter(topMask, topMask);
			
			//lFilt.filter(topMask, topMask);
			eFilt.filter(topMask, topMask); 
			tFilt.filter(topMask, topMask);
			
			finTopMask = topMask;
		}

		if(Controller.isBotMask)
		{
			botMask.flush();
			try {
				botMask = VIDEO_FEED.getRect(bScrX, bScrY, bScrW, bScrH);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			BufferedImage greenMask;
			greenMask = onlyGreen(botMask);
			
		
			//boolean canSeeCharger;
			//greenMask = fillCharger(greenMask, botPreview);
			
			
			lFilt.filter(botMask, botMask);
			eFilt.filter(botMask, botMask);
			tFilt.filter(botMask, botMask);
			
			mergeImages(botMask, greenMask, finBotMask);
		}
	}
	
	
	public BufferedImage onlyGreen(BufferedImage image1)
	{
	    int rgb1[];
	    int imw, imh;
	    	imw = image1.getWidth();
	    	imh = image1.getHeight();

		//for(int i = 0; i < len; i++)
		//{
		//    rgb3[i] = ((rgb1[i] >> (2 - channel1)) & 0xff);// - ((rgb2[i] >> (2 - channel2)) & 0xff);
		//}
		
		BufferedImage output = new BufferedImage(imw, imh, BufferedImage.TYPE_INT_ARGB);
		//output.setRGB(0, 0, imw, imh, rgb3, 0, imw);
		
		int[] inPixels1 = new int[imw], inPixels2 = new int[imw];
		
		WritableRaster img1Raster = image1.getRaster(),
					   outRaster = output.getRaster();
		
		for ( int y = 0; y < imh; y++ ) {
			rgb1 = (int []) img1Raster.getDataElements(0, y, imw, 1, inPixels1);
	    	
			// We try to avoid calling getRGB on images as it causes them to become unmanaged, causing horrible performance problems.
			for ( int x = 0; x < imw; x++ )
			{
				int o;
				int a = rgb1[x] & 0xff000000;
				int r1 = (rgb1[x] >> 16) & 0xff;
				int g1 = (rgb1[x] >> 8) & 0xff;
				int b1 = (rgb1[x] & 0xff);
				
				o = Math.max(0, g1 - r1 - b1);
				if(o > 100)
					o = 255;
				else
					o = 0;
				
				//o = r1;//>> 8;
				
				inPixels1[x] = a | (0 << 16) | (o << 8) | 0;
			}
			outRaster.setDataElements( 0, y, imw, 1, inPixels1);
		}
		
		return output;
    }
	
	
	public BufferedImage fillCharger(BufferedImage imageEdge, BufferedImage imageReal)
	{
	    int rgbe[], rgbr[];
	    int imw, imh;
	    	imw = imageEdge.getWidth();
	    	imh = imageEdge.getHeight();

		//for(int i = 0; i < len; i++)
		//{
		//    rgb3[i] = ((rgb1[i] >> (2 - channel1)) & 0xff);// - ((rgb2[i] >> (2 - channel2)) & 0xff);
		//}
		
		BufferedImage output = new BufferedImage(imw, imh, BufferedImage.TYPE_INT_ARGB);
		//output.setRGB(0, 0, imw, imh, rgb3, 0, imw);
		
		int[] inPixels1 = new int[imw], inPixels2 = new int[imw];
		
		WritableRaster imgEdgeRaster = imageEdge.getRaster(),
					   imgRealRaster = imageReal.getRaster(),
					   outRaster = output.getRaster();
		
		boolean isInside, prevGreen = false, isGreen = false;
		byte check = 0;
		int numInside = 0, numLine;
		
		for ( int y = 0; y < imh; y++ ) {
			isInside = false;
			numLine = 0;
			
			rgbe = (int []) imgEdgeRaster.getDataElements(0, y, imw, 1, inPixels1);
			rgbr = (int []) imgRealRaster.getDataElements(0, y, imw, 1, inPixels2);

	    	
			// We try to avoid calling getRGB on images as it causes them to become unmanaged, causing horrible performance problems.
			for ( int x = 0; x < imw; x++ ) {
				if(check == 0) {
					int re = (rgbe[x] >> 16) & 0xff, rr = (rgbr[x] >> 16) & 0xff;
					int ge = (rgbe[x] >> 8) & 0xff,  gr = (rgbr[x] >> 8) & 0xff;
					int be = (rgbe[x] & 0xff),       br = (rgbr[x] & 0xff);
					
					isGreen = (ge > 200 && re < 200 && be < 200);
					
					if(isGreen && !prevGreen)
							isInside = !isInside;
					
					if(rr > 200 && gr > 200 && br > 200)
						numLine++;
					
					prevGreen = isGreen;
				}
				else if(check == 1)
				{
					int re = (rgbe[x] >> 16) & 0xff, ge = (rgbe[x] >> 8) & 0xff, be = (rgbe[x] & 0xff);
					
					isGreen = (ge > 200 && re < 200 && be < 200);
					
					if(isGreen && !prevGreen)
							isInside = !isInside;
					
					if(isInside)
						inPixels1[x] = (rgbe[x] & 0xff000000) | (255 << 16) | (255 << 8) | 255;
					
					prevGreen = isGreen;
				}
				else if(check == 2) {
					int g = (rgbe[x] >> 8) & 0xff;
					
					if(g > 200)
						inPixels1[x] = (rgbe[x] & 0xff000000) | (0 << 16) | (0 << 8) | 0;
				}
			}
			
			if(check == 0) {
				if(!isInside && numLine > 32)
					check = 1;
				else
					check = 2;
				
				y--;
			}
			else if(check == 1 || check == 2) {
				outRaster.setDataElements( 0, y, imw, 1, inPixels1);
				check = 0;
			}
		}
		
		return output;
    }
	
	
	public BufferedImage subtractImages(BufferedImage image1, BufferedImage image2, int channel1, int channel2)
	{
	    int rgb1[], rgb2[], rgb3[], len;
	    int imw, imh;
	    	imw = image1.getWidth();
	    	imh = image1.getHeight();

		//for(int i = 0; i < len; i++)
		//{
		//    rgb3[i] = ((rgb1[i] >> (2 - channel1)) & 0xff);// - ((rgb2[i] >> (2 - channel2)) & 0xff);
		//}
		
		BufferedImage output = new BufferedImage(imw, imh, BufferedImage.TYPE_INT_ARGB);
		//output.setRGB(0, 0, imw, imh, rgb3, 0, imw);
		
		int[] inPixels1 = new int[imw], inPixels2 = new int[imw];
		
		WritableRaster img1Raster = image1.getRaster(),
					   img2Raster = image2.getRaster(),
					   outRaster = output.getRaster();
		
		for ( int y = 0; y < imh; y++ ) {
			rgb1 = (int []) img1Raster.getDataElements(0, y, imw, 1, inPixels1);
	    	rgb2 = (int []) img2Raster.getDataElements(0, y, imw, 1, inPixels2);
	    	
			// We try to avoid calling getRGB on images as it causes them to become unmanaged, causing horrible performance problems.
			for ( int x = 0; x < imw; x++ )
			{
				int c1, c2, o;
				int a = rgb1[x] & 0xff000000;
				int r1 = (rgb1[x] >> 16) & 0xff;
				int g1 = (rgb1[x] >> 8) & 0xff;
				int b1 = (rgb1[x] & 0xff);
				int r2 = (rgb2[x] >> 16) & 0xff;
				int g2 = (rgb2[x] >> 8) & 0xff;
				int b2 = (rgb2[x] & 0xff);
				
				switch(channel1)
				{
					case 0:	c1 = r1;
							break;
					case 1: c1 = g1;
							break;
					case 2: c1 = b1;
							break;
					default: c1 = 0;
				}
				switch(channel2)
				{
					case 0:	c2 = r2;
							break;
					case 1: c2 = g2;
							break;
					case 2: c2 = b2;
							break;
					default: c2 = 0;
				}
				
				o = Math.max(0, c1 - c2);
				
				//o = r1;//>> 8;
				
				inPixels1[x] = a | (o << 16) | (o << 8) | o;
			}
			outRaster.setDataElements( 0, y, imw, 1, inPixels1);
        }
		
		
		
		return output;
	}
	
	
	public double calcVDistanceDifference(BufferedImage prevImage, BufferedImage curImage) {
		double outAngle = 0, startDis = -5, deltaDis = .5;
		List<Integer> errorList = new ArrayList<Integer>();
		
		CropFilter cFilt = new CropFilter((int)(bScrW*.515), (int)(bScrH*.2), (int)(bScrW*.08), (int)(bScrH*.1));
		
		BufferedImage prevCrop, tempImage, tempCrop;
		prevCrop = cFilt.filter(prevImage, null);
		
		//tempHolder = prevCrop;
		
		//REALLY, REALLY SLOW
		//Calculate Error At Different Angles
		for(double i = startDis; i < 5; i += deltaDis)
		{
			AffineTransform tx = new AffineTransform();
	        tx.translate(0, i);
	        
	        AffineTransformOp op;
	        if(i == Math.round(i))
	        	op = new AffineTransformOp(tx,AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	        else
	        	op = new AffineTransformOp(tx,AffineTransformOp.TYPE_BILINEAR);
	        tempImage = op.filter(curImage, null);//(sourse,destination)


			//tempImage = rFilt.filter(curImage, null);
			tempCrop = cFilt.filter(tempImage, null);
			
			int diff = calcImageDifference(prevCrop, tempCrop);
			errorList.add(new Integer(diff));
			
			if(diff == -1)
				return 0;
		}
		
		//Find Minimum Error Angle
		int err = errorList.get(0).intValue(), curErr;
		double minDis = startDis, curDis;
		for(int i = 1; i < errorList.size(); i++)
		{
			curErr = errorList.get(i).intValue();
			curDis = startDis + i*deltaDis;
			
			if(curErr < err)
			{
				err = curErr;
				minDis = curDis;
			}
		}
		
		
		//System.out.println(minDis);

		
			
		return minDis;
	}
	
	public double calcAngleDifference(BufferedImage prevImage, BufferedImage curImage) {
		double outAngle = 0, startAngle = -6, endAngle = 6, deltaAngle = .5	; //Delta Angle Can't be Smaller :P
		int erNum = (int) ((endAngle - startAngle)/deltaAngle);
		
		int errors[] = new int[erNum];
		double angles[] = new double[erNum];
		//List<Integer> errorList = new ArrayList<Integer>();
		
		CropFilter cFilt = new CropFilter((int)(bScrW*.6), (int)(bScrH*.05), (int)(bScrW*.2), (int)(bScrH*.3));
		
		BufferedImage prevCrop, tempImage, tempCrop;
		prevCrop = cFilt.filter(prevImage, null);
		
		tempHolder = prevCrop;
		
		int k = 0;
		
		//Calculate Error At Different Angles
		for(double i = startAngle; i < endAngle; i += deltaAngle)
		{
			//RotateFilter rFilt = new RotateFilter((int) i);
			
			AffineTransform tx = new AffineTransform();
	        tx.rotate(i/180*3.14159, bScrW*.5, bScrH*.666);

	        AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_NEAREST_NEIGHBOR); //WAYYYYY FASTER THAN BILINEAR
	        tempImage = op.filter(curImage, null);


			//tempImage = rFilt.filter(curImage, null);
			tempCrop = cFilt.filter(tempImage, null);
			
			int diff = calcImageDifference(prevCrop, tempCrop);
			errors[k] = diff; 
			angles[k] = i;
			
			//errorList.add(new Integer(diff));
					
			k++;
			
			if(diff == -1)
				return 0;
		}
		
		//System.out.println(" ");
		
		int tempErr;
		double tempAng;
		boolean didMove;
		do {
			didMove = false;
			
			for(int i = 0; i < erNum-1; i++)
				if(errors[i] > errors[i+1]) {
					tempErr = errors[i];
					tempAng = angles[i];
					
					errors[i] = errors[i+1];
					errors[i+1] = tempErr;
					angles[i] = angles[i+1];
					angles[i+1] = tempAng;
					
					didMove = true;
				}
			
		} while(didMove);
		
		
		//double minAngle;
		//minAngle = minErrorSolver(angles[0],errors[0], angles[1],errors[1], angles[2],errors[2]);
		
		return -(angles[0]*(370)/360);
		
		//return Sifter.getAngleDifference(curList, prevList, bScrW*(.6 - .5), bScrH*(.666 - .05));
	}
	
	
	public int calcImageDifference(BufferedImage image1, BufferedImage image2) {
	    int rgb1[], rgb2[], rgb3[], totDiff = 0;
	    int imw, imh;
	    	imw = image1.getWidth();
	    	imh = image1.getHeight();
	    	
	    if(imw != image2.getWidth())
	    	return -1;

		//for(int i = 0; i < len; i++)
		//{
		//    rgb3[i] = ((rgb1[i] >> (2 - channel1)) & 0xff);// - ((rgb2[i] >> (2 - channel2)) & 0xff);
		//}
		
		
		int[] inPixels1 = new int[imw], inPixels2 = new int[imw];
		
		WritableRaster img1Raster = image1.getRaster(),
					   img2Raster = image2.getRaster();
		
		int c1, c2, minE;
		
		for ( int y = 0; y < imh; y += 4) {
			rgb1 = (int []) img1Raster.getDataElements(0, y, imw, 1, inPixels1);
	    	rgb2 = (int []) img2Raster.getDataElements(0, y, imw, 1, inPixels2);
	    	
			// We try to avoid calling getRGB on images as it causes them to become unmanaged, causing horrible performance problems.
			for (int x = 0; x < imw; x += 4)
			{
				c1 = (rgb1[x] >> 16) & 0xff + (rgb1[x] >> 8) & 0xff + rgb1[x] & 0xff;
				c2 = (rgb2[x] >> 16) & 0xff + (rgb2[x] >> 8) & 0xff + rgb2[x] & 0xff;
				
				//minE = Math.min(Math.abs(((rgb1[x] >> 16) & 0xff) - ((rgb2[x] >> 16) & 0xff)), Math.abs(((rgb1[x] >> 8) & 0xff) - ((rgb2[x] >> 8) & 0xff)));
				
				//if(minE > 8)
				//	totDiff += minE;
				
				if(c1 > c2)
					totDiff += c1 - c2;
				else
					totDiff += c2 - c1;
				
				//if(Math.abs(c1 - c2) > 20)
				//	totDiff += 1;
			}
        }
		
		
		
		return totDiff;
	}
	
	
	public double minErrorSolver(double x1,double y1, double x2,double y2, double x3, double y3) {
		double 	y32 = y3 - y2,
				y12 = y1 - y2,
				x12 = x1 - x2,
				x32 = x3 - x2,
				x122 = (x1*x1 - x2*x2),
				x322 = (x3*x3 - x2*x2);
		double b = (y32*x122 - y12*x322)/(x32*x122 - x12*x322);
		
		double	y13 = y1 - y3,
				x132 = (x1*x1 - x3*x3),
				x13 = x1 - x3;
		double a = (y13 - b*x13)/x132;
		
		
		double o = -b/(2*a);
		
		if(Double.isNaN(o) || Double.isInfinite(o))
			return 0;
		
		System.out.println(o);
		return o;
	}
	
	
	public BufferedImage mergeImages(BufferedImage image1, BufferedImage image2, BufferedImage output)
	{
	    int rgb1[], rgb2[], rgb3[], len;
	    int imw, imh;
	    	imw = image1.getWidth();
	    	imh = image1.getHeight();

		//for(int i = 0; i < len; i++)
		//{
		//    rgb3[i] = ((rgb1[i] >> (2 - channel1)) & 0xff);// - ((rgb2[i] >> (2 - channel2)) & 0xff);
		//}
		
		//output.setRGB(0, 0, imw, imh, rgb3, 0, imw);
		
		int[] inPixels1 = new int[imw], inPixels2 = new int[imw];
		
		WritableRaster img1Raster = image1.getRaster(),
					   img2Raster = image2.getRaster(),
					   outRaster = output.getRaster();
		
		for ( int y = 0; y < imh; y++ ) {
			rgb1 = (int []) img1Raster.getDataElements(0, y, imw, 1, inPixels1);
	    	rgb2 = (int []) img2Raster.getDataElements(0, y, imw, 1, inPixels2);
	    	
			// We try to avoid calling getRGB on images as it causes them to become unmanaged, causing horrible performance problems.
			for ( int x = 0; x < imw; x++ )
			{
				int r, g, b;
				int a = rgb1[x] & 0xff000000;
				int r1 = (rgb1[x] >> 16) & 0xff;
				int g1 = (rgb1[x] >> 8) & 0xff;
				int b1 = (rgb1[x] & 0xff);
				int r2 = (rgb2[x] >> 16) & 0xff;
				int g2 = (rgb2[x] >> 8) & 0xff;
				int b2 = (rgb2[x] & 0xff);
				
				r = Math.min(r1 + r2, 255);
				g = Math.min(g1 + g2, 255);
				b = Math.min(b1 + b2, 255);
				
				//o = r1;//>> 8;
				
				inPixels1[x] = a | (r << 16) | (g << 8) | b;
			}
			outRaster.setDataElements( 0, y, imw, 1, inPixels1);
        }
		
		
		
		return output;
	}
	
	
	public void startThreads()
	{
		//Create Thread For Updating Feed, Runs at 60 FPS
		Timer fw = new Timer();
		fw.schedule(new TimerTask() {
			public void run() {	
				repaint();
								
				
				if(Controller.isBeamOn && Controller.isViewOn)
				{
					if(!Controller.isTopMask)
					{
						topPreview.flush();
						
						try {
							topPreview = VIDEO_FEED.getRect(tScrX, tScrY, tScrW, tScrH);
						} catch (AWTException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(!Controller.isBotMask)
					{
						prevBotPreview.flush();
						prevBotPreview = botPreview;
						prevFeatures = curFeatures;
						
						
						//CropFilter cFilt = new CropFilter((int)(bScrW*.6), (int)(bScrH*.05), (int)(bScrW*.3), (int)(bScrH*.3));
		
						
						try {
 							botPreview = VIDEO_FEED.getRect(bScrX, bScrY, bScrW, bScrH);
 							
 							//tempHolder = cFilt.filter(botPreview, null);
 							
 							//PImage pPreview = new PImage(tempHolder);
 							
 							//curFeatures = Sifter.runSift(pPreview, "curImage");
 							
 							
 							//CURRENTLY, A LOT OF ERROR
 							
 							//Determine Angle Difference Between Images, Add to Mapper Angle
 							double dDir = 0;
 							//if(curFeatures != null && prevFeatures != null)
 							dDir = calcAngleDifference(prevBotPreview, botPreview);
 							RoomMapper.direction += dDir;
 							
 							//System.out.println(dDir);
 							
 							if(RoomMapper.direction < 0)
 								RoomMapper.direction += 360;
 							else if(RoomMapper.direction > 360)
 								RoomMapper.direction -= 360;
 							
 							if(Math.abs(dDir) < .5)
 							{
								
	 							double dis = -calcVDistanceDifference(prevBotPreview, botPreview), rad = RoomMapper.direction/180*Math.PI;
	 							RoomMapper.x += dis*Math.cos(rad)*RoomMapper.PIXEL_LENGTH;
	 							RoomMapper.y -= dis*Math.sin(rad)*RoomMapper.PIXEL_LENGTH;
 							}

 							
						} catch (AWTException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}, 0, (int) (1000./60));
		
		
		Timer masker = new Timer();
		masker.schedule(new TimerTask() {
			public void run() {	
				if(Controller.isViewOn && Controller.isBeamOn)
					getMask();
			}
		}, 0, (int) (1000/30.));
		
		
		Timer regionFinder = new Timer();
		regionFinder.schedule(new TimerTask() {
			public void run() {	
				if(!InputController.isInReal)
					findScreenRegion();
			}
		}, 0, (int) (1000/2.));
	}
}
