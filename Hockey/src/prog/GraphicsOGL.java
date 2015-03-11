package prog;


import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;


public class GraphicsOGL {
	
	public static GLCanvas createCanvas() {
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		
		GLCanvas canv = new GLCanvas(caps);
			//canv.addGLEventListener(this);
		
		return canv;
	}
	
	
}
