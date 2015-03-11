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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

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


public class PathPanel extends JPanel 
{
	private InputController input;
	private int x, y, w, h;
	
	int curNodeNum = 0;
	
	final int BTN_NODEP = 1, BTN_LINKP = 2, BTN_NODEM = 3, BTN_PATHC = 4, BTN_NODERM = 5, BTN_LINKRM = 6, BTN_SAVE = 7, BTN_OPEN = 8;
	
	Path curPath = null;
		Node pathStartNode = null, pathEndNode = null;
	
	private Button hoverButton = null, selectedButton = null;
	int selectedID = 0;
	
	final Color COL_DESTROY = new Color(150, 0, 0);
	
	float selectX, selectY;
	
	
	List<Button> buttonList = new ArrayList<Button>();
	List<Link> globalLinkList = new ArrayList<Link>();
	List<Node> globalNodeList = new ArrayList<Node>();
	List<Path> globalPathList = new ArrayList<Path>();
	
	List<Particle> globalParticleList = new ArrayList<Particle>();
	
	Node startNode = null, moveNode = null, hoverNode = null;

	
	private int num = 0;
	
	public PathPanel(int x, int y, int w, int h)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		
		this.setLayout(null);
		this.setBounds(x, y, w, h);
		this.setBackground(Color.BLACK);
		
		int bY = 20;
		
		new Button(BTN_SAVE, 20, bY, 90,20, "Save");		new Button(BTN_OPEN, 115, bY, 90,20, "Open");		bY += 30;
		new Button(BTN_NODEP, 20,bY, 90,20, "+ Node");		new Button(BTN_NODERM, 115,bY, 90,20, "- Node");	bY += 30;
		new Button(BTN_LINKP, 20,bY, 90,20, "+ Link");		new Button(BTN_LINKRM, 115,bY, 90,20, "- Link");	bY += 30;
		new Button(BTN_NODEM, 20,bY, 90,20, "Move Node");														bY += 30;
		new Button(BTN_PATHC, 20,bY, 90,20, "Find Path");														bY += 30;
		
		this.addKeyListener(input);

		startThreads();
	}
	
	
	private class Drawable {
		public Drawable() {
		}
		
		public void draw(Graphics g) {
		}
	}
	
	public class Particle extends Drawable {
		public Particle() {
			globalParticleList.add(this);
		}
	}
	
	public class LinearParticle extends Particle {
		float x, y, xV, yV, xA, yA;
		float speed = 1;
		
		public LinearParticle(float x, float y, float xV, float yV, float xA, float yA) {
			super();
			
			this.x = x;
			this.y = y;
			this.xV = xV;
			this.yV = yV;
			this.xA = xA;
			this.yA = yA;			
		}
		
		public void draw(Graphics g) {
			xA += speed*xA;
			yV += speed*yA;
			
			x += speed*xV;
			y += speed*yV;
			
			if((x < 0 || x > w) || (y < 0 || y > h))
				globalParticleList.remove(this);
			
			int iX, iY;
			iX = Math.round(x);
			iY = Math.round(y);
			
			g.setColor(Color.BLACK);
			g.drawRect(iX, iY, 1, 1);
		}
	}
	
	public class SlowmoLinearParticle extends LinearParticle {
		float frac;
		
		public SlowmoLinearParticle(float x, float y, float xV, float yV, float xA, float yA, float speed, float frac) {
			super(x, y, xV, yV, xA, yA);

			this.speed = speed;
			this.frac = frac;
		}
		
		public void draw(Graphics g) {
			speed += (1 - speed)/frac;
			
			super.draw(g);
		}
	}
	
	public class Node extends Drawable {
		float x, y, sizeDir = 0;
		float realX, realY;
		float s = 14;
		int id;
		List<Link> linkList = new ArrayList<Link>();
		List<Node> neighborList = new ArrayList<Node>();
		boolean mightDestroy = false;
		
		public Node(float x, float y) {
			super();
			
			realX = this.x = x;
			realY = this.y = y;
			this.id = curNodeNum++;
			
			globalNodeList.add(this);
		}
		
		public void draw(Graphics g) {
			int aX = 0, aY = 0;
			
			
			if(pathStartNode == this)
				g.setColor(Color.BLUE);
			else if(moveNode == this)
				g.setColor(new Color(128, 128, 128));
			else
			{
				g.setColor(Color.BLACK);
				
				if(hoverNode == this) {
					if(selectedID == BTN_NODERM) {
						g.setColor(COL_DESTROY);
						
						s = 14;
						
						aX = (int) Math.round(1*(2*Math.random() - 1));
						aY = (int) Math.round(1*(2*Math.random() - 1));
					}
					else if(selectedID == BTN_NODEM) {
						sizeDir += 12;
						if(sizeDir > 360)
							sizeDir -= 360;
						
						s += (14 + 1.5*Math.sin(sizeDir/180*Math.PI) - s)/1.5;
					}
				}
				else {
					sizeDir = 0;
					s += (14 - s)/1.25;
				}
			}
			
			x = realX + aX;
			y = realY + aY;
			
			int dX, dY, w, h;
			dX = Math.round(x - s/2);
			dY = Math.round(y - s/2);
			w = Math.round(x + s/2) - dX;
			h = Math.round(y + s/2) - dY;
			
			g.fillOval(dX, dY, w, h);

			g.setColor(Color.WHITE);
			g.fillOval(dX+3,dY+3, w-6,h-6);
		}
		
		public void setStability(boolean isStable) {
			mightDestroy = !isStable;
			
			for(Link curLink : hoverNode.linkList)
				curLink.setStability(isStable);
		}
		
		public void destroy() {
			globalNodeList.remove(this);
			
			Node curN;
			while(neighborList.size() > 0) {
				curN = neighborList.get(0);
				
				curN.neighborList.remove(this);
				neighborList.remove(curN);
			}
			
			Link curLink;
			while(linkList.size() > 0) {
				curLink = linkList.get(0);
				
				if(curLink != null)
					curLink.destroy(true);
				else
					linkList.remove(null);
			}
			
			
			int num = 30;
			double ang;
			float xN, yN, x, y, v, xV, yV;
			for(int i = 0; i < num; i++) {
				for(int r = 0; r < 2; r++) {
					ang = 2*Math.PI*i/num;
					
					xN = (float) Math.cos(ang);
					yN = (float) Math.sin(ang);
					
					x = xN*(6 - r);
					y = yN*(6 - r);
					
					v = 3;
					xV = v*xN + (float)(Math.random() - .5);
					yV = v*yN + (float)(Math.random() - .5);
					
					new LinearParticle(realX+x, realY+y, xV, yV, 0f, .1f);
				}
			}
		}
		
		public void setPosition(float x, float y) {
			realX = this.x = x;
			realY = this.y = y;
			
			for(int i = 0; i < linkList.size(); i++)
				linkList.get(i).recalcLength();
		}
	}
	
	public class Button extends Drawable{
		int id, x, y, w, h;
		double brightness = 0;
		String text;
		
		public Button(int id, int x, int y, int w, int h, String text) {
			super();
			
			this.id = id;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			
			this.text = text;
			
			buttonList.add(this);
		}
		
		public void draw(Graphics g) {
			if(selectedButton == this)
				brightness += (180 - brightness)/3;
			else if(hoverButton == this)
				brightness += (150 - brightness)/10;
			else
				brightness += (80 - brightness)/10;
			
			g.setColor(new Color((int)brightness, (int)brightness, (int)brightness));
			g.fillRect(x, y, w, h);
			
			g.setColor(Color.BLACK);
			g.drawRect(x, y, w, h);
			
			g.setColor(Color.WHITE);
			g.drawString(text, x+10, y+h -4);
		}
	}
	
	public class Link extends Drawable{
		final Node NODE_A, NODE_B;
		Color col = Color.BLACK;
		public Path parent = null;
		public double length;
		boolean mightDestroy = false;
		
		public Link(Node nodeA, Node nodeB) {
			NODE_A = nodeA;
			NODE_B = nodeB;
			length = calcNodeDistance(nodeA, nodeB);
			
			globalLinkList.add(this);
			
			nodeA.neighborList.add(nodeB);
			nodeB.neighborList.add(nodeA);

			nodeA.linkList.add(this);
			nodeB.linkList.add(this);
		}
		
		public Link(Node nodeA, Node nodeB, boolean list) {
			NODE_A = nodeA;
			NODE_B = nodeB;
			length = calcNodeDistance(nodeA, nodeB);
			
			if(list) {
				globalLinkList.add(this);
				nodeA.neighborList.add(nodeB);
				nodeB.neighborList.add(nodeA);
			}
			
			nodeA.linkList.add(this);
			nodeB.linkList.add(this);
		}
		
		public void destroy(boolean destroyParent) {
			globalLinkList.remove(this);
			
			NODE_A.linkList.remove(this);
			NODE_B.linkList.remove(this);
			
			if(parent != null) {
				parent.LINK_LIST.remove(this);

				if(destroyParent)
					parent.destroy();
				
				parent = null;
			}
		}
		
		public void setStability(boolean isStable) {
			mightDestroy = !isStable;
		}
		
		public CoordPair findPointWithLength(double partLength) {
			double pX = 0, pY = 0;
			
			if(partLength <= 0) {
				pX = NODE_A.x;
				pY = NODE_A.y;
			}
			else if(partLength >= length) {
				pX = NODE_B.x;
				pY = NODE_B.y;
			}
			else {	
				double frac = partLength/length;
				
				pX = NODE_A.x + (NODE_B.x - NODE_A.x)*frac;
				pY = NODE_A.y + (NODE_B.y - NODE_A.y)*frac;
			}
			
			return new CoordPair(pX, pY);
		}
		
		public void recalcLength() {
			if(parent != null)
				parent.length -= length;
			
			length = calcNodeDistance(NODE_A, NODE_B);
			
			if(parent != null)
				parent.length += length;
		}
		
		public void draw(Graphics g) {
			g.setColor(col);
			
			
			if(mightDestroy)
				g.setColor(COL_DESTROY);
			
			
			g.drawLine((int)NODE_A.x, (int)NODE_A.y, (int)NODE_B.x, (int)NODE_B.y);
		}
	}
	
	public class CoordPair {
		public final double X, Y;
		
		public CoordPair(double x, double y) {
			X = x;
			Y = y;
		}
	}

	
	public class Path extends Drawable {
		final List<Link> LINK_LIST;
		public double length;
		final Node START_NODE, END_NODE;
		double partLength = 0, numLength = 30;
		
		public Path(List<Link> LINK_LIST) {
			this.LINK_LIST = LINK_LIST; 
			
			double totalLength = 0;
			
			for(int i = 0; i < LINK_LIST.size(); i++) {
				LINK_LIST.get(i).col = Color.BLUE;
				LINK_LIST.get(i).parent = this;
				totalLength += LINK_LIST.get(i).length;
			}
			
			length = totalLength;
			
			START_NODE = LINK_LIST.get(0).NODE_A;
			END_NODE = LINK_LIST.get(LINK_LIST.size()-1).NODE_B;
			
			
			globalPathList.add(this);
		}
		
		public void destroy() {
			globalPathList.remove(this);
			
			Link curLink;
			while(LINK_LIST.size() > 0) {
				curLink = LINK_LIST.get(0);
				
				if(curLink != null)
					curLink.destroy(false);
				else
					LINK_LIST.remove(null);
			}
		}
		
		public void draw(Graphics g) {
			partLength += 1.5;
			if(partLength > length)
				partLength -= length;
			else if(partLength > numLength)
				partLength -= numLength;
			
			
			for(int i = 0; i < LINK_LIST.size(); i++)
				LINK_LIST.get(i).draw(g);
			
			
			double curLength = partLength;
			while(curLength < length) {
				CoordPair point = findPointWithLength(curLength);
				g.setColor(Color.BLACK);
				g.drawOval((int)(point.X - 2), (int)(point.Y - 2), 4, 4);
				
				curLength += numLength;
			}
		}
		
		public CoordPair findPointWithLength(double segLength) {
			double pX = 0, pY = 0;
			
			if(segLength <= 0) {
				pX = START_NODE.x;
				pY = START_NODE.y;
			}
			else if(segLength >= length) {
				pX = END_NODE.x;
				pY = END_NODE.y;
			}
			else {
				int s = LINK_LIST.size();
				Link curLink;
				
				for(int i = 0; i < s; i++) {
					curLink = LINK_LIST.get(i);
					
					if(segLength > curLink.length)
						segLength -= curLink.length;
					else
						return curLink.findPointWithLength(segLength);
				}
			}
			
			return new CoordPair(pX, pY);
		}
	}
	
	
	public void draw(Graphics g) {
		for(int i = 0; i < globalNodeList.size(); i++)
			globalNodeList.get(i).draw(g);
		
		for(int i = 0; i < globalLinkList.size(); i++)
			globalLinkList.get(i).draw(g);
		
		for(int i = 0; i < globalPathList.size(); i++)
			globalPathList.get(i).draw(g);
		
		for(int i = 0; i < globalParticleList.size(); i++)
			globalParticleList.get(i).draw(g);
		
		
		if(InputController.getMouseDown()) {
			int sX, sY, sW, sH;
			sX = Math.round(selectX);
			sY = Math.round(selectY);
			sW = Math.round(InputController.getLocalMouseX()-x - selectX);
			sH = Math.round(InputController.getLocalMouseY()-y - selectX);
			
			g.drawRect(sX, sY, sW, sH);
		}
		
		
		for(int i = 0; i < buttonList.size(); i++)
			buttonList.get(i).draw(g);
	}
	
	
	
	//NODE FUNCTIONS
		public Node nearestNode(int x, int y) {
			double minDis = 1000000, curDis;
			Node minNode = null, curNode;
			
			for(int i = 0; i < globalNodeList.size(); i++) {
				curNode = globalNodeList.get(i);
				curDis = Math.sqrt(Math.pow(x - curNode.x,2) + Math.pow(y - curNode.y,2));
				
				if(curDis < minDis) {
					minNode = curNode;
					minDis = curDis;
				}
			}
			
			return minNode;
		}
		
		public Node overNode(int x, int y, int r) {
			double minDis = 1000000, curDis;
			Node minNode = null, curNode;
			
			for(int i = 0; i < globalNodeList.size(); i++) {
				curNode = globalNodeList.get(i);
				curDis = Math.sqrt(Math.pow(x - curNode.x,2) + Math.pow(y - curNode.y,2));
				
				if(curDis < minDis) {
					minNode = curNode;
					minDis = curDis;
				}
			}
			
			if(minDis > r)
				minNode = null;
			
			return minNode;
		}
		public double calcNodeDistance(Node nodeA, Node nodeB) {
			return Math.sqrt(Math.pow(nodeA.x - nodeB.x, 2) + Math.pow(nodeA.y - nodeB.y, 2));
		}
	
	
	
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);
		
		int mX, mY;
		boolean mC, mD;
		mX = InputController.getLocalMouseX()-x;
		mY = InputController.getLocalMouseY()-y;
		mC = InputController.getMouseClick();
		mD = InputController.getMouseDown();
		
		if(mC) {
			selectX = mX;
			selectY = mY;
		}
		
		if(hoverNode != null)
			hoverNode.setStability(true);
		hoverNode = null;
		
		
		//If Mouse is in Window...
		if(mX > 0 && mY > 0 && mX < w && mY < h) {
			if(InputController.keySpace) {
				x += InputController.mouseX - InputController.mouseXP;
				y += InputController.mouseY - InputController.mouseYP;
			}
			else {
				//Loop Through Buttons
				Button curButton;
				int bX, bY, bW, bH;
				hoverButton = null;
				
				for(int i = 0; i < buttonList.size(); i++) {
					curButton = buttonList.get(i);
					bX = curButton.x;
					bY = curButton.y;
					bW = curButton.w;
					bH = curButton.h;
					
					//If Mouse Over A Button
					if(mX > bX && mY > bY && mX < bX+bW && mY < bY+bH) {
						if(mC) {
							mC = false;
						
							if(curButton.id == BTN_SAVE) {
								saveFile();
							}
							else if(curButton.id == BTN_OPEN) {
								openFile();
							}
							else {
								selectedButton = curButton;
								selectedID = curButton.id;
							}
						}
						else
							hoverButton = curButton;
						
						break;
					}
				}
				
				//If Mouse Was Not Over a Button, Create a Node
				if(selectedID == BTN_NODEP) {
					if(mC) {
					
						new Node(mX,mY);
						mC = false;
					}
				}
				else if(selectedID == BTN_LINKP) {		
					if(mC) {
						startNode = overNode(mX, mY, 10);
					}
					else if(mD) {
						
					}
					else {
						Node endNode = overNode(mX, mY, 10);
						
						if(endNode != null && endNode != startNode && startNode != null)
							new Link(startNode, endNode);
						
						startNode = null;
					}
					
					if(startNode != null)
					{			
						int x1, y1;
						x1 = (int)startNode.x;
						y1 = (int)startNode.y;
						
						g.setColor(Color.BLACK);
						g.drawLine(x1, y1, mX, mY);					
					}
				}
				//BUTTON 3: MOVING NODES
				///////////////////////////////////////////////
				else if(selectedID == BTN_NODEM) {		
					if(moveNode == null) {
						hoverNode = overNode(mX, mY, 10);
					
						if(mC)
							moveNode = hoverNode;
					}
					else if(mD)
						moveNode.setPosition(mX, mY);
					else
						moveNode = null;
				}
				//BUTTON 4: CREATING PATHS
				if(selectedID == BTN_PATHC) {
					if(mC) {
						if(pathStartNode == null)
							pathStartNode = overNode(mX, mY, 10);
						else
							pathEndNode = overNode(mX, mY, 10);
						
						if(pathStartNode != null && pathEndNode != null) {
							curPath = findPathAStar(pathStartNode, pathEndNode);
							
							pathStartNode = null;
							pathEndNode = null;
						}
					}
				}
				else if(selectedID == BTN_NODERM) {
					hoverNode = overNode(mX, mY, 10);
					
					if(hoverNode != null)
					{
						hoverNode.setStability(false);
						
						if(mC)
							hoverNode.destroy();
					}
				}
			}
		}
		
		draw(g);
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
	
	public void saveFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(Paths.get("").toAbsolutePath().toFile());
		
		
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "Map Data Files", "mdt");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showSaveDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	File outFile = chooser.getSelectedFile();
	    	
	    	PrintWriter writer;
			try {
				writer = new PrintWriter(outFile.getName());
			} catch (FileNotFoundException e) {
				return;
			}

	    	Node curNode;
	    	for(int i = 0; i < globalNodeList.size(); i++) {
	    		curNode = globalNodeList.get(i);
	    		
	    		if(curNode != null)
	    			writer.println("node" + i + "(" + curNode.x +"," + curNode.y + ")");
	    	}
	    	
	    	Link curLink;
	    	for(int i = 0; i < globalLinkList.size(); i++) {
	    		curLink = globalLinkList.get(i);
	    		
	    		if(curLink != null)
	    			writer.println("link" + i + "(" + globalNodeList.indexOf(curLink.NODE_A) +"," + globalNodeList.indexOf(curLink.NODE_B) + ")");
	    	}
	    	
	    	writer.close();
	    }
	}
	
	public void openFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(Paths.get("").toAbsolutePath().toFile());
		
		
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "Map Data Files", "mdt");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	File outFile = chooser.getSelectedFile();
	    	
	    	BufferedReader reader;
			try {
				reader = Files.newBufferedReader(outFile.toPath(), StandardCharsets.UTF_8);
			} catch (IOException e) {
				System.out.println("Error Opening.");
				return;
			}
			
			Node curNode;
			for(int i = 0; i < globalNodeList.size(); i++) {
				curNode = globalNodeList.get(i);
				if(curNode != null)
					curNode.destroy();
	    	}
			globalNodeList.clear();
			
			curNodeNum = 0;

			String line, type, str;
			int id;
			float num1, num2;
			try {
				while((line = reader.readLine()) != null) {
					type = line.substring(0, 4);
									
					if(type.equals("node") || type.equals("link")) {
						line = line.replace(type, "");
						
						str = line.substring(0, line.indexOf('('));
						id = Integer.parseInt(str);
						line = line.replace(str + "(", "");
					
						str = line.substring(0, line.indexOf(','));
						num1 = Float.parseFloat(str);
						line = line.replace(str + ",", "");

						str = line.substring(0, line.indexOf(')'));
						num2 = Float.parseFloat(str);												
						
						
						if(type.equals("node"))
							new Node(num1, num2);
						else
							new Link(globalNodeList.get((int)num1), globalNodeList.get((int)num2));
					}
				}
				
				reader.close();
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
 	
	    	
	    }
	}
	
	public Path findPathAStar(Node startNode, Node endNode) {
		if(startNode == null || endNode == null || startNode == endNode)
			return null;
		
		System.out.println("");
		System.out.println("Searching...");
		
		List<Node> closedList = new ArrayList<Node>(), nList;
		List<Link> linkList = new ArrayList<Link>();
		Node curNode, curNeighbor, minNode = null;
		
		double curCost, minCost;
		
		//Find Nodes
		
		boolean success;
		success = attemptToEnd(startNode, startNode, endNode, null, closedList);
		
		
		if(success) {
			int s;
			s = closedList.size();
			
			for(int i = 0; i < s-1; i++) {
				linkList.add(new Link(closedList.get(i), closedList.get(i+1), false));
			}
			
			linkList.add(new Link(closedList.get(s-1), endNode, false));
			
			
			/*System.out.println("Searching for Start from Endpoint...");
			
			//Work Backwards to Find Path!
			curNode = endNode;
			
			
			
			
			
			attemptToStart(endNode, endNode, startNode, null, closedList, linkList);
		
			System.out.println("Success!");
			System.out.println("Length: " + linkList.size());
			
			Collections.reverse(linkList);*/
			return new Path(linkList);
		}
		else
		{
			System.out.println("No path exists.");
			return null;
		}
	}
	
	public boolean attemptToEnd(Node startNode, Node curNode, Node endNode, Node parentNode, List<Node> closedList) {
		Node curNeighbor;
		double curCost;
		List<Node> copyList = new ArrayList<Node>(closedList), nList, nNearList = new ArrayList<Node>();
		List<Double> costList = new ArrayList<Double>();		
		
		
		copyList.add(curNode);
		System.out.println("At Node # " + curNode.id + "...");
		
		
		nList = curNode.neighborList;
		
		//Get Number of Neighbors
		int s = nList.size();
		if(s == 0 || (s == 1 && curNode != startNode)) //If No Neighbors (?) or One Neighbor, Must Be a Dead-End
			return false;
			
		for(int n = 0; n < s; n++) {
			curNeighbor = nList.get(n);
				
			if(curNeighbor == endNode) {
				System.out.println("Found endpoint!!");
				cloneListIntoList(closedList, copyList);
				return true;
			}
				
			if(copyList.contains(curNeighbor) || curNeighbor == parentNode)
				continue;
				
			curCost = calcNodeDistance(curNode, curNeighbor) + Math.abs(endNode.x - curNeighbor.x) + Math.abs(endNode.y - curNeighbor.y);
				
				
			nNearList.add(curNeighbor);
			costList.add(new Double(curCost));
		}
		
		
		if(costList.size() == 0) {
			System.out.println("Dead-end.");
			return false;
		}
		
		sortListsWithFirst(costList, nNearList);
		
		
		boolean success;
		for(int n = 0; n < costList.size(); n++) {
			success = attemptToEnd(startNode, nNearList.get(n), endNode, curNode, copyList);
			
			if(success) {
				cloneListIntoList(closedList, copyList);
				return true;
			}
		}
		
		
		return false;
	}
	
	public boolean attemptToStart(Node endNode, Node curNode, Node startNode, Node parentNode, List<Node> closedList, List<Link> linkList) {
		Node curNeighbor;
		double curCost;
		List<Node> copyList = new ArrayList<Node>(closedList), nList, nNearList = new ArrayList<Node>();
		List<Double> costList = new ArrayList<Double>();	
		List<Link> copyLinkList = new ArrayList<Link>(linkList);
		
		
		copyList.remove(curNode);
		System.out.println("At Node # " + curNode.id + "...");
		
		
		nList = curNode.neighborList;
		
		//Get Number of Neighbors
		int s = nList.size();
		if(s == 0 || (s == 1 && curNode != endNode)) //If No Neighbors (?) or One Neighbor, Must Be a Dead-End
			return false;
			
		for(int n = 0; n < s; n++) {
			curNeighbor = nList.get(n);
				
			if(curNeighbor == startNode) {
				copyLinkList.add(new Link(startNode, curNode, false));
				
				cloneListIntoList(closedList, copyList);
				cloneListIntoList(linkList, copyLinkList);
				return true;
			}
				
			if(!copyList.contains(curNeighbor) || curNeighbor == parentNode)
				continue;
				
			curCost = calcNodeDistance(curNode, curNeighbor) + Math.abs(startNode.x - curNeighbor.x) + Math.abs(startNode.y - curNeighbor.y);
				
				
			nNearList.add(curNeighbor);
			costList.add(new Double(curCost));
		}
		
		
		if(costList.size() == 0) {
			System.out.println("Dead-end.");
			return false;
		}
		
		sortListsWithFirst(costList, nNearList);
		
		
		boolean success;
		for(int n = 0; n < costList.size(); n++) {
			success = attemptToStart(endNode, nNearList.get(n), startNode, curNode, copyList, copyLinkList);
			
			if(success) {
				copyLinkList.add(new Link(nNearList.get(n), curNode, false));
				
				cloneListIntoList(closedList, copyList);
				cloneListIntoList(linkList, copyLinkList);
				return true;
			}
		}
		
		
		return false;
	}
	
	
	public List<Node> getOpenList() {
		List<Node> openList = new ArrayList<Node>();
		
		for(int i = 0; i < globalNodeList.size(); i++)
			openList.add(globalNodeList.get(i));
		
		return openList;
	}
	
	public void cloneListIntoList(List listDest, List listSrc) {
		System.out.println("Initial Size: " + listDest.size());
		listDest.clear();
		
		int s = listSrc.size();
		for(int i = 0; i < s; i++)
			listDest.add(listSrc.get(i));
		
		System.out.println("Final Size: " + listDest.size());
	}
	
	public void sortListsWithFirst(List<Double> listNum, List listObj) {
		int s = listNum.size();
		boolean didMove;
		Double leftN, rightN;
		Object leftO, rightO;
		
		
		if(s == 0 || s == 1)
			return;
			
		
		do 
		{
			didMove = false;
			
			for(int i = 0; i < s-1; i++) {
				leftN = listNum.get(i);
				rightN = listNum.get(i+1);
				
				if(leftN.doubleValue() > rightN.doubleValue()) {
					listNum.set(i, rightN);
					listNum.set(i+1, leftN);
					
					
					leftO = listObj.get(i);
					rightO = listObj.get(i+1);
					
					listObj.set(i, leftO);
					listObj.set(i+1, rightO);
					
					didMove = true;
				}
			}
			
			dispDoubleList(listNum);
		} while(didMove);
	}
	
	public void dispDoubleList(List<Double> listNum) {
		int s = listNum.size();
		for(int i = 0; i < s; i++)
			System.out.print(listNum.get(i).toString() + " ");
			
		System.out.print("\n");
	}
}
