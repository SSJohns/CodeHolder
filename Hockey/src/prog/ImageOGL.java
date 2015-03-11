package prog;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import javax.media.opengl.GL;

public class ImageOGL {
	private static ComponentColorModel colorModel; {
		colorModel = new ComponentColorModel (ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8,8,8,8}, true, false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);			
	}
	
	private WritableRaster raster;
	private BufferedImage img;
	
	
	public ImageOGL(int w, int h) {
		raster = Raster.createInterleavedRaster (DataBuffer.TYPE_BYTE, w, h, 4, null);
		img = new BufferedImage (colorModel, raster, false, null);
		
			Graphics2D g = img.createGraphics();
			AffineTransform gt = new AffineTransform();
			gt.translate (0, h);
			gt.scale (1, -1d);
			g.transform (gt);
			g.drawImage (img, null, null); //i?
	}
	
	public void draw(GL gl) {
		DataBufferByte imgBuf = (DataBufferByte) raster.getDataBuffer();
			byte[] dukeRGBA = imgBuf.getData();
			
		gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable (GL.GL_BLEND);
		
		/*gl.glColor3f(0.0f, 0.5f, 0.0f);
		gl.glRecti (0, 300, 100, 330);
		gl.glColor3f (0.0f, 0.0f, 0.0f);
		gl.glRasterPos2i (10, 300);
		gl.glDrawPixels (dukeWidth, dukeHeight,
		                 gl.GL_RGBA, gl.GL_UNSIGNED_BYTE,
		                 dukeRGBA);*/
	}
}
