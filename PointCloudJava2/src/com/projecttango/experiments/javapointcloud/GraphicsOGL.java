package com.projecttango.experiments.javapointcloud;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.widget.TextView;

public class GraphicsOGL {
	private static List<TextView> textList = new ArrayList<TextView>();
	
	public static void clearText() {
		textList.clear();
	}
	
	public static void setOrtho(GL10 gl, int w, int h, float near, float far) {
		gl.glViewport(0, 0, w, h);
		gl.glMatrixMode(gl.GL_PROJECTION);
		gl.glLoadIdentity();
		
		gl.glOrthof(0,  w,  0,  h,  near, far);
		gl.glMatrixMode(gl.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glDisable(gl.GL_LIGHTING);
	}    
	
	public static void setPerspective(GL10 gl, int w, int h, float near, float far, float[] projMatrix, float fov) {
		gl.glMatrixMode(gl.GL_PROJECTION);
		gl.glLoadIdentity();
		
		float mCameraAspect = (float) w/ h;
        Matrix.perspectiveM(projMatrix, 0, fov, mCameraAspect,
                near, far);
		
		gl.glMatrixMode(gl.GL_MODELVIEW);
		gl.glLoadIdentity();
	}  
	
	public static void drawText(GL10 gl, int x, int y, int size, String string) {
		int len, i;
		len = string.length();
		
		
		TextView t = new TextView(null);
		t.setText(string);
		
		textList.add(t);
		
		//t.draw(gl);
				
		/*for (i = 0; i < len; i++)
			drawChar(gl, x+size*i, y, size, string.charAt(i));*/
	}
	
	/*public static void drawChar(GL10 gl, int x, int y, int size, char c) {
		int sides = 4;
	       RegularPolygon p = new RegularPolygon(0,0,0, // x,y,z of the origin
	                     1,  // Length of the radius
	                     sides); // how many sides
	                     
	      //Allocate and fill the vertex buffer
	       mFVertexBuffer = p.getVertexBuffer();
	      
	      //Allocate and fill the index buffer
	       mIndexBuffer = p.getIndexBuffer();
	       numOfIndecies = p.getNumberOfIndecies();
	      
	      //set the buffers to their begining
	       mFVertexBuffer.position(0);
	       mIndexBuffer.position(0);
	 
	       //set the color
	       gl.glColor4f(1.0f, 0, 0, 0.5f);
	      
	      //set the vertex buffer
	       gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
	      
	      //draw
	       gl.glDrawElements(GL10.GL_TRIANGLES, this.numOfIndecies,
	                GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
	}*/
}
