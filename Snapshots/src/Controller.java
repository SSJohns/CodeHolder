import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;



public class Controller extends JFrame implements ActionListener
{
	private CaptureHandler cap;
	private CommandOutputter com;
	private InputController input;
	private RoomMapper mapper;
	private PathPanel pnlPath;

	//TOP
		private JButton btnOne, btnTwo;
		private byte curView = 1;
	
	//VIEW ONE
		private JButton btnSnap;
		private JToggleButton tglbtnView, tglbtnCapture, tglbtnPilot, tglbtnTopMask, tglbtnBotMask;
		private JPanel panel;
		private PreviewPanel pnlPreview;
		private ImageFolderPanel pnlImages;
		public static boolean isViewOn = false, isPiloting = false, isTopMask = false, isBotMask = false, isBeamOpen = false, isBeamOn = false;
	JLabel label;
	
	private Timer fw;
	
	//1150
	private final static int WINDOW_WIDTH = 1150, WINDOW_HEIGHT = 580, TOP_BORDER = 30, BORDER = 15;
	private int prevW, prevH;
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
		prevW = 800 - (BORDER + 80 + 2*BORDER);
		prevH = (int)(prevW*(((double) screenSize.height)/screenSize.width));
		prevW = WINDOW_WIDTH - (BORDER + 80 + 2*BORDER);
	}
	
	int numPic = 0;
	
	
	//Initialization!!
	public static void main(String args[]) throws AWTException
	{
		Controller con = new Controller();
		con.setTitle("BeamPro Control Tool");
		con.setBounds(3500, 200, WINDOW_WIDTH, WINDOW_HEIGHT);
		//con.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		con.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		con.setVisible(true);
		
		
		//NOT WORKING
		
		File path = new File("Img");
		if(!path.exists())
			path.mkdir();
		else if(path.list().length > 0)
		{
			int folderN = 0;
			File renamePath;
			

			
			do
			{
				renamePath = new File("ImgBkp" + String.valueOf(folderN));
				folderN++; 
			} while(renamePath.exists());
			
			path.renameTo(renamePath);
			
			File newPath = new File("Img");
			newPath.mkdir();
				File[] fList = newPath.listFiles();
				for(int i = 0; i < fList.length; i++)
					fList[i].delete();
				
		}
		else
			System.out.println(path.getAbsolutePath());
	}
	
	
	
	public Controller() throws AWTException
	{
		input = new InputController();
			this.addMouseListener(input);
			this.addMouseMotionListener(input);
		cap = new CaptureHandler();
		com = new CommandOutputter();
		mapper = new RoomMapper();
		
		
		//Create Main, Background Panel
		panel = new JPanel();
			panel.setLayout(null);
		label = new JLabel();					
			panel.add(label);
			
		int curBtnY = TOP_BORDER;

		btnOne = new JButton("1");
			btnOne.setBounds(15, curBtnY, 18, 18);
			btnOne.addActionListener(this);
			panel.add(btnOne);
		btnTwo = new JButton("2");
			btnTwo.setBounds(15+18+5, curBtnY, 18, 18);
			btnTwo.addActionListener(this);
			panel.add(btnTwo);
			
		curBtnY += 18+20;
		
		tglbtnView = new JToggleButton("Enable View");
			tglbtnView.setBounds(15, curBtnY, 80, 18);
			tglbtnView.addActionListener(this);
			panel.add(tglbtnView);
		curBtnY += 18+30;
		
		//Create Button for Taking Snapshots
		btnSnap = new JButton("Snapshot");		
			btnSnap.setBounds(15, curBtnY, 80, 18);
			btnSnap.addActionListener(this);
			panel.add(btnSnap);
			curBtnY += 18+15;
			
		//Create Button for Capturing Many Snapshots
		tglbtnCapture = new JToggleButton("Capture");
			tglbtnCapture.setBounds(15, curBtnY, 80, 18);
			tglbtnCapture.addActionListener(this);
			panel.add(tglbtnCapture);
			curBtnY += 18+30;
			
		tglbtnPilot = new JToggleButton("Pilot!");		
			tglbtnPilot.setBounds(15, curBtnY, 80, 18);
			tglbtnPilot.addActionListener(this);
			panel.add(tglbtnPilot);
			curBtnY += 18+15;
			
		//Create Preview Panel for Drawing Screen Region to Window
		pnlPreview = new PreviewPanel(15+80+15,TOP_BORDER,prevW,prevH, cap);
			pnlPreview.findScreenRegion();
			//pnlPreview.setScreenRegion(618, 123, 1326, 990);
			pnlPreview.startThreads();
			panel.add(pnlPreview);
			
			JLabel lblPreview = new JLabel("Top Window");
			lblPreview.setBounds(15+80+15, TOP_BORDER-25, 80, TOP_BORDER);
			panel.add(lblPreview);
			
			tglbtnTopMask = new JToggleButton("Mask");		
			tglbtnTopMask.setBounds(15+80+15, TOP_BORDER+prevH+10, 80, 18);
			tglbtnTopMask.addActionListener(this);
			panel.add(tglbtnTopMask);
			
			tglbtnBotMask = new JToggleButton("Mask");		
			tglbtnBotMask.setBounds(15+80+15 + prevW/2, TOP_BORDER+prevH+10, 80, 18);
			tglbtnBotMask.addActionListener(this);
			panel.add(tglbtnBotMask);
			
		
		pnlImages = new ImageFolderPanel(BORDER,TOP_BORDER+prevH+TOP_BORDER, WINDOW_WIDTH - 2*BORDER, WINDOW_HEIGHT - (TOP_BORDER+prevH+TOP_BORDER + 2*BORDER), new File("Img"));
			panel.add(pnlImages);
			
		pnlPath = new PathPanel(15+80+15,TOP_BORDER,prevW,prevH);
			panel.add(pnlPath);
			
		hideAll();
		unhide(1);
						
		
		this.add(panel);
	}
	
	public void hideAll() {
		tglbtnView.setVisible(false);
		btnSnap.setVisible(false);
		tglbtnCapture.setVisible(false);
		tglbtnPilot.setVisible(false);
		pnlPreview.setVisible(false);
		tglbtnBotMask.setVisible(false);
		tglbtnTopMask.setVisible(false);
		pnlImages.setVisible(false);
		pnlPath.setVisible(false);
	}
	
	public void unhide(int view) {
		if(view == 1) {
			tglbtnView.setVisible(true);
			btnSnap.setVisible(true);
			tglbtnCapture.setVisible(true);
			tglbtnPilot.setVisible(true);
			pnlPreview.setVisible(true);
			tglbtnBotMask.setVisible(true);
			tglbtnTopMask.setVisible(true);
			pnlImages.setVisible(true);
		}
		
		if(view == 2) {
			pnlPath.setVisible(true);
		}
	}
	


	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == btnOne) {
			curView = 1;
			
			hideAll();
			unhide(1);
		}
		
		if(e.getSource() == btnTwo) {
			curView = 2;
			
			hideAll();
			unhide(2);
		}
		
		if(curView == 1) {
			if(e.getSource() == btnSnap)
				try 
				{
					cap.captureRectJPEG(618, 123, 1326, 990, "Img/feedImg" + String.valueOf(numPic) + ".jpg");
					numPic++;
				} 
				catch (AWTException | IOException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			if(e.getSource() == tglbtnPilot)
			{
				if(tglbtnPilot.isSelected()) {
					if(isBeamOn && isViewOn) {
						isPiloting = true;
						com.startPiloting();
					}
					else
						tglbtnPilot.setSelected(false);
				}
				else {
					isPiloting = false;
					com.stopPiloting();
				}
			}
			
			if(e.getSource() == tglbtnView)
				isViewOn = !isViewOn;
			
			if(e.getSource() == tglbtnTopMask)
				isTopMask = !isTopMask;
			
			if(e.getSource() == tglbtnBotMask)
				isBotMask = !isBotMask;
			
			//Begin Capturing Video Feed
			if(e.getSource() == tglbtnCapture)
			{
				if(tglbtnCapture.isSelected()) {
					if(isBeamOn && isViewOn) {
						//Create Thread For Updating Feed, Runs at 60 FPS
						fw = new Timer();
						fw.schedule(new TimerTask() {
							public void run() {	
								try {
									cap.captureRectJPEG(618, 123, 1326, 990, "Img/feedImg" + String.valueOf(numPic) + ".jpg");
								} catch (AWTException | IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								numPic++;
							}
						}, 0, (int) (1000));
					}
					else
						tglbtnCapture.setSelected(false);
				}
				else
					fw.cancel();
			}
		}
	}
}
