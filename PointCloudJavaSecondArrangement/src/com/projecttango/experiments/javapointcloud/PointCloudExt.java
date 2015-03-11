package com.projecttango.experiments.javapointcloud;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.atap.tangoservice.TangoXyzIjData;
import com.projecttango.tangoutils.renderables.PointCloud;

public class PointCloudExt extends PointCloud {
	int numPts = 0;
	
	public PointCloudExt(int maxDepthPoints) {
		super(maxDepthPoints);	
	}
	
	public void UpdatePoints(TangoXyzIjData xyzIj) {
		//Create Byte Array Buffer For Storing Point Data
		byte[] buffer = new byte[xyzIj.xyzCount * 3 * 4];
		
		
		//Fill Buffer with Current Snapshot's Points
			FileInputStream fileStream = new FileInputStream(
	                xyzIj.xyzParcelFileDescriptor.getFileDescriptor());
			try {
	            fileStream.read(buffer, xyzIj.xyzParcelFileDescriptorOffset, buffer.length);
	            fileStream.close();
	        } catch (IOException e) {
	            e.printStackTrace();	        	
	            return;
	        }
		
		
		//Add Data from Buffer to Total Buffer
		/*for(int i = 0; i < curBuffer.length; i++)
			totBuffer[i] = curBuffer[i];
		for(int i = 0; i < buffer.length; i++)
			totBuffer[curBuffer.length+i] = buffer[i];*/
		//for(int i = 0; i < xyzIj.xyzCount*3*4; i++)
		//	totBuffer[i] = buffer[i];
			
		//Increment Number of Points
		//numPts += xyzIj.xyzCount;
		numPts = xyzIj.xyzCount;
		
		//Update Points to Include New Points
		UpdatePoints(buffer, numPts);		
	}
}
