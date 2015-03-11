import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;


public class RoomMapper {
	private static final int ROOM_SIZE = 512;
	public static double x = ROOM_SIZE/2, y = ROOM_SIZE/2, direction = 0, size = 2, turnSpeed = .3;
	public static BufferedImage roomImage = new BufferedImage(ROOM_SIZE, ROOM_SIZE, BufferedImage.TYPE_INT_RGB);
	
	public static double BEAM_LENGTH = 25/12., BEAM_WIDTH = 18/12., //length in ft
						  PIXEL_LENGTH = 1,
						  scale = 25;
	
	private Graphics roomGraphics = roomImage.createGraphics();
	{
		roomGraphics.setColor(Color.BLACK);
		roomGraphics.fillRect(0, 0, ROOM_SIZE, ROOM_SIZE);
		roomGraphics.setColor(Color.WHITE);
	}
	
	public RoomMapper()
	{
		Timer mp = new Timer();
		mp.schedule(new TimerTask() {
		    public void run() {
		    	updatePosition();
		    	
		    	PIXEL_LENGTH = 5*BEAM_LENGTH/(PreviewPanel.bScrH*.3);   //# pixels in beam lengthwise / beam length in ft
		    }}, 0, (int)(1000/60.));
	}
	
	private void updatePosition()
	{
		CommandOutputter.BeamPilot pil = CommandOutputter.pilot;
		

		//roomGraphics.setColor(Color.BLACK);
		//roomGraphics.clearRect(0,0, ROOM_SIZE, ROOM_SIZE);
		
		roomGraphics.setColor(Color.WHITE);
		
		
		int bX, bY, bLX, bLY;
		bX = (int)(ROOM_SIZE/2 + scale*((x-BEAM_WIDTH/2) - ROOM_SIZE/2));
		bY = (int)(ROOM_SIZE/2 + scale*((y-BEAM_WIDTH/2) - ROOM_SIZE/2));
		
		roomGraphics.fillOval(bX, bY, (int)(BEAM_WIDTH*scale), (int)(BEAM_WIDTH*scale));
		
		int cX, cY;
		cX = bX + (int)(BEAM_WIDTH*scale/2);
		cY = bY + (int)(BEAM_WIDTH*scale/2);
		
		//roomGraphics.drawLine(cX,cY,    cX + (int) (100*Math.cos(direction/180*Math.PI)),cY - (int)(100*Math.sin(direction/180*Math.PI)));
		//roomGraphics.drawLine((int) x, (int) y, (int) (x + 100*Math.cos(direction/180*Math.PI)), (int) (y - 100*Math.sin(direction/180*Math.PI)));
	}
}
