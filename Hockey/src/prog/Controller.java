package prog;

import io.InputController;
import io.OutputController;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import prog.gui.JMenuBarExt;
import prog.panel.*;
import prog.win.*;



public class Controller extends JFrame implements ActionListener
{
	private static Controller main;
	
	private static JMenuBarExt menuBar;
	private InputController input;
	private static MainPanel pnlMain;
	private static PlayerPanel pnlPlayers;
	private static EventExplorerPanel pnlEventExp;
	private static LogPanel pnlLog;
	private static PlotTimePanel pnlPlot;
	private static ActivePanel pnlAct;
	private static BarGraphPanel pnlBar;
	private JTabbedPaneExt tbPlot;
	
	

	//TOP
		private JButton btnOne, btnTwo, btnNewEvent, btnPlayEvent;
	
	//VIEW ONE
		private JButton btnAddPlayer, btnPlotTimes, btnGenEvent;
		private PreviewPanel pnlPreview;
	JLabel label;
	
	final static Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

	//1150
	private final static int WINDOW_WIDTH = SCREEN_SIZE.width, WINDOW_HEIGHT = SCREEN_SIZE.height, TOP_BAR = 30, TOP_BORDER = 16, BOTTOM_BORDER = 32, BORDER = 15, LOG_HEIGHT = 256,
			ACTIVE_WIDTH = 1024;
	private int prevW, prevH;
	{
		prevW = WINDOW_WIDTH - (BORDER + 80 + BORDER + ACTIVE_WIDTH + 2*BORDER);		
		prevH = WINDOW_HEIGHT - (TOP_BAR + TOP_BORDER + BORDER + LOG_HEIGHT + BORDER + BOTTOM_BORDER);
	}
	
	int numPic = 0;
	
	
	//Initialization!!
	public static void main(String args[]) throws AWTException
	{		
		Controller con = new Controller();
		con.setTitle("Hockey Tool");
		

		//con.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		con.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Simulate Click to Fix Window Issue
		OutputController.click(0,0);
	}
	
	
	
	public Controller() throws AWTException
	{
		main = this;
		
		setBounds(300, 200, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		menuBar = new JMenuBarExt();
			setJMenuBar(menuBar);
		setVisible(true);
		
		input = new InputController();
		
		
		//Create Main, Background Panel
		pnlMain = new MainPanel(0,0,WINDOW_WIDTH,WINDOW_HEIGHT);
			pnlMain.setLayout(null);
		label = new JLabel();					
			pnlMain.add(label);
			
		
		
			
			
			
		int curBtnY = TOP_BORDER;
			
		curBtnY += 18+20;

			
			
		tbPlot = new JTabbedPaneExt();
			tbPlot.setBounds(15+80+15+prevW+15,TOP_BORDER,WINDOW_WIDTH-(15+80+15+prevW+15+15),prevH/2);

			pnlPlot = new PlotTimePanel(0,0,WINDOW_WIDTH-(15+80+15+prevW+15+15),prevH/2);
				tbPlot.addTab("Time Plot", pnlPlot);
				
			pnlBar = new BarGraphPanel(0,0,WINDOW_WIDTH-(15+80+15+prevW+15+15),prevH/2);
				tbPlot.addTab("Bar Graph", pnlBar);
		
			pnlMain.add(tbPlot);
			
		
			//BORDER,TOP_BORDER+prevH+BORDER, WINDOW_WIDTH - 2*BORDER, LOG_HEIGHT
		pnlLog = new LogPanel(0,0, WINDOW_WIDTH - 2*BORDER, LOG_HEIGHT);
		JScrollPaneExt scrLog = new JScrollPaneExt(pnlLog);
			scrLog.setBounds(BORDER,TOP_BORDER+prevH+BORDER, WINDOW_WIDTH - 2*BORDER, LOG_HEIGHT);
			scrLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrLog.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			pnlMain.add(scrLog);
			
		
		curBtnY = TOP_BORDER + 18+20;
		
		btnAddPlayer = new JButton("Add Player");
			btnAddPlayer.setBounds(15, curBtnY, 80, 18);
			btnAddPlayer.addActionListener(this);
			pnlMain.add(btnAddPlayer);
			
		curBtnY += 18+20;
			
		btnPlotTimes = new JButton("Plot Times");
		btnPlotTimes.setBounds(15, curBtnY, 80, 18);
		btnPlotTimes.addActionListener(this);
		pnlMain.add(btnPlotTimes);
		
		curBtnY += 18+20;
		
		btnGenEvent = new JButton("Generate Event");
		btnGenEvent.setBounds(15, curBtnY, 80, 18);
		btnGenEvent.addActionListener(this);
		pnlMain.add(btnGenEvent);
		
		curBtnY += 18+20;
		
		btnNewEvent = new JButton("New Event");
		btnNewEvent.setBounds(15, curBtnY, 80, 18);
		btnNewEvent.addActionListener(this);
		pnlMain.add(btnNewEvent);
		
		curBtnY += 18+20;
		
		btnPlayEvent = new JButton("Play Event");
		btnPlayEvent.setBounds(15, curBtnY, 80, 18);
		btnPlayEvent.addActionListener(this);
		pnlMain.add(btnPlayEvent);

		
		
		pnlEventExp = new EventExplorerPanel(0,0,200,prevH);
			pnlEventExp.setBounds(0,0,prevW,prevH);
		JScrollPaneExt scrEv = new JScrollPaneExt(pnlEventExp);
			scrEv.setBounds(15+80+15 + 1200,TOP_BORDER,200,prevH);
			/*scrEv.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrEv.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);*/
			pnlMain.add(scrEv);
			
			pnlEventExp.setContainer(scrEv);
		
		
		//this, 15+80+15,TOP_BORDER,prevW,prevH
		pnlPlayers = new PlayerPanel(0,0,prevW,prevH);
			pnlPlayers.setBounds(0,0,prevW,prevH);
		JScrollPaneExt scrPlayers = new JScrollPaneExt(pnlPlayers);
			scrPlayers.setBounds(15+80+15,TOP_BORDER,prevW-200,prevH);
			/*scrPlayers.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrPlayers.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);*/
			pnlMain.add(scrPlayers);
			
			pnlPlayers.setContainer(scrPlayers);
			
		pnlAct = new ActivePanel(15+80+15+prevW+15,TOP_BORDER+prevH/2,WINDOW_WIDTH-(15+80+15+prevW+15+15),prevH/2);
			pnlMain.add(pnlAct);

		
		this.add(pnlMain);
	}
	


	public void actionPerformed(ActionEvent e)
	{		
		if(e.getSource() == btnAddPlayer) {
			new NewPlayerGUI(this);
		}
		else if(e.getSource() == btnPlotTimes) {
			new PlotGUI(this, 0);
		}
		else if(e.getSource() == btnNewEvent) {
			pnlPlayers.startEvent();
		}
		else if(e.getSource() == btnPlayEvent) {
			pnlPlayers.reversePlaying();
		}
		else if(e.getSource() == btnGenEvent) {
			new GenRandomEventGUI(this);
			pnlPlot.updateGraph();
		}
	}



	public static void logMessage(String string) {
		pnlLog.addMessage(string);
	}
	
	public static void updateGraph() {
		pnlPlot.updateGraph();
		pnlBar.updateGraph();
	}

	public PlayerPanel getPlayerPanel() {
		return pnlPlayers;
	}



	public static Controller getMain() {
		return main;
	}
	
	public static MainPanel getPanelMain() {
		return pnlMain;
	}
	
	public void repaintPanels() {
		pnlPlayers.repaint();
		pnlLog.repaint();
	}


	public static PlayerPanel getPanelPlayers() {
		return pnlPlayers;
	}
}
