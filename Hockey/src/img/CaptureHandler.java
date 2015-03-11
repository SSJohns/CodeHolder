package img;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

public class CaptureHandler
{
  public static void main(String args[]) throws AWTException, IOException {
     // capture the whole screen
     //BufferedImage screencapture = new Robot().createScreenCapture(
     //      new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()) );
     

     // Save as PNG
     // File file = new File("screencapture.png");
     // ImageIO.write(screencapture, "png", file);
  }

  
  public BufferedImage getScreen() throws HeadlessException, AWTException {
	  //Capture Screen
	  return new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()) );
  }
  
  
  public void captureRectJPEG(int x, int y, int w, int h, String name) throws AWTException, IOException {
	  //Capture Screen Region
	  BufferedImage screencapture = new Robot().createScreenCapture(new Rectangle(x, y, w, h));

	  //Save File
	  File file = new File(name);
	     ImageIO.write(screencapture, "jpg", file);
  }
  
  public BufferedImage getRect(int x, int y, int w, int h) throws AWTException {
	  return new Robot().createScreenCapture(new Rectangle(x, y, w, h));
  }
}


//To capture a window
//BufferedImage image = new Robot().createScreenCapture( 
//   new Rectangle( myframe.getX(), myframe.getY(), 
//                 myframe.getWidth(), myframe.getHeight() ) );