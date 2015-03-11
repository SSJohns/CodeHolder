


/**
 * Sifter.java
 *
 * Part of the library for the class 10-615 Art That Learns taught in
 * Carnegie Mellon University on Spring 2009 semester
 *
 */


// The Java SIFT library
import mpi.cbg.fly.*;
// Processing library
import processing.core.*;

// Java Libraries
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Class that handles the SIFT wrappers. SIFT is explained in: 
 * <a href="http://en.wikipedia.org/wiki/Scale-invariant_feature_transform">Scale Invariant Feature Transform</a>
 * 
 * Java SIFT code is taken from <a href="http://fly.mpi-cbg.de/~saalfeld/javasift.html>Stephan Saalfeld</a>'s
 * page and stripped down to be callable from other Java programs without needing ImageJ.
 * 
 * 
 * 
 * @author Barkin Aygun 
 *  
 *   
 * @see <a href="http://dev.processing.org/reference/core/javadoc/processing/core/PApplet.html">
 * processing.core.PApplet</a>
 * 
 * @usage Library
 */
public class Sifter {
	
	// steps
	private static int steps = 3;
	// initial sigma
	private static float initial_sigma = .8f;
	// feature descriptor size
	private static int fdsize = 4;
	// feature descriptor orientation bins
	private static int fdbins = 8;
	// size restrictions for scale octaves, use octaves < max_size and > min_size only
	private static int min_size = 64;
	private static int max_size = 1024;

	/**
	 * Set true to double the size of the image by linear interpolation to
	 * ( with * 2 + 1 ) * ( height * 2 + 1 ).  Thus we can start identifying
	 * DoG extrema with $\sigma = INITIAL_SIGMA / 2$ like proposed by
	 * \citet{Lowe04}.
	 * 
	 * This is useful for images scmaller than 1000px per side only. 
	 */ 
	//private static boolean upscale = false;
	//private static float scale = 1.0f;
	
	/**
	 * For using PImages directly from Processing for SIFT
	 * @param image	PImage to be converted
	 * @return FloatArray2D
	 */
	private static FloatArray2D ConvertImage(PImage image) {
		// Set image to grayscale and load pixels for reading
		int count = 0;
		FloatArray2D result;
		PImage bwimage = image;
		bwimage.filter(PImage.GRAY);
		bwimage.loadPixels();
		
		result = new FloatArray2D(bwimage.width, bwimage.height);
		for (int x = 0; x < bwimage.height; x++) {
			for (int y = 0; y < bwimage.width; y++) {
				result.data[count] = bwimage.pixels[count++] / 255.0f;
			}
		}
		
		return result;
	}
	
	/**
	 * Runs sift using default parameters on a given PImage
	 * @param image PImage to run Sift on
	 * @return Vector<Feature> Set of features found on the image
	 * 
	 * @see Feature
	 */
	public static ArrayList<Feature> runSift(PImage image, String imageId) {
		return runSift(image, steps, initial_sigma, fdsize, fdbins, min_size, max_size, imageId);
	}
	
	/**
	 * Runs sift on a given PImage with given parameters
	 * 
	 * @param image PImage to run Sift on
	 * @param steps Number of steps
	 * @param initial_sigma Initial Blur Factor
	 * @param fdsize Feature Descriptor Size
	 * @param fdbins Number of Feature Descriptor Bins
	 * @param min_size Minimum size for a feature
	 * @param max_size Maximum size for a feature
	 * @return Vector of Features
	 * 
	 * @see Feature
	 */
	public static ArrayList<Feature> runSift(PImage image, int steps, float initial_sigma,
									int fdsize, int fdbins, int min_size, int max_size, String imageId) {
		Vector<mpi.cbg.fly.Feature> fs;
		FloatArray2DSIFT sift = new FloatArray2DSIFT( fdsize, fdbins );
		FloatArray2D fa = ConvertImage(image);
		Filter.enhance(fa, 1.0f);
		
		fa = Filter.computeGaussianFastMirror(fa, (float )Math.sqrt(initial_sigma * initial_sigma - 0.25) );
		
		sift.init(fa, steps, initial_sigma, min_size, max_size);
		fs = sift.run(max_size);
		
		ArrayList<Feature> fs1 = new ArrayList<Feature>(fs.size());
		for (mpi.cbg.fly.Feature f : fs) {
			Feature tempf = new Feature(f.scale, f.orientation, f.location, f.descriptor);
			fs1.add(tempf);
		}
		
		return fs1;
	}
	
	public static int[] matchFeatures(ArrayList<Feature> set_one, ArrayList<Feature> set_two, float threshold) {
		int[] matches = new int[set_one.size()];
		int i = 0;
		for (Feature f : set_one) {
			matches[i++] = closestMatch(f, set_two, threshold);
		}
		return matches;		
	}
	
	public static int closestMatch(Feature f1, ArrayList<Feature> possible_matches, float threshold) {
		float maxMatch = 0;
		float secondMax = 0;
		float desDist = 0;
		int maxIndex = 0;
		int curIndex = 0;
		for (Feature f : possible_matches) {
			desDist = f1.descriptorDistance(f);
			//if (desDist < threshold) {curIndex++; continue;};
			if (desDist > maxMatch) {
				secondMax = maxMatch;
				maxIndex = curIndex;
				maxMatch = desDist;
			}
			curIndex++;
		}
		//System.out.print(maxMatch + " against " + secondMax);
		if (maxMatch > 0.8f && maxMatch * 0.8f < secondMax) {
			//System.out.println(" succeeds");
			return maxIndex;
		}
		else {
			//System.out.println(" fails");
			return -1;
		}
	}
	
	
	
	
	public static double getAngleDifference(ArrayList<Feature> set_one, ArrayList<Feature> set_two, double xPos, double yPos) {

		int f1X = 0, f1Y = 0, f2X = 0, f2Y = 0;
		int fNum = 0;
		
		int ind;
		for (Feature f : set_one) {
			ind = closestMatch(f, set_two, 0);
			
			if(ind != -1) {
				f1X += f.location[0];
				f1Y += f.location[1];
				f2X += set_two.get(ind).location[0];
				f2Y += set_two.get(ind).location[1];
				
				fNum++;
			}
		}
		
		System.out.println(fNum);
		
		if(fNum == 0)
			return 0;
		
		f1X /= fNum;
		f1Y /= fNum;
		f2X /= fNum;
		f2Y /= fNum;
		
		
		return (Math.atan2(yPos - f1Y, xPos + f1X) - Math.atan2(yPos - f2Y, xPos + f2X))/3.14159*180;
	}
}
