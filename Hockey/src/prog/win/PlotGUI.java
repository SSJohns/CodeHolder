package prog.win;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import prog.Controller;
import prog.panel.*;



public class PlotGUI extends JFrame implements ActionListener
{
	private JPanel panel;
	private Controller parent;
	
	private final static int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 500;

	int numPic = 0;
	
	

	
	
	public PlotGUI(Controller parent, int type)
	{
		this.parent = parent;
		
		setTitle("Time Plotter");
		//con.setBounds(300, 200, WINDOW_WIDTH, WINDOW_HEIGHT);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		
		
		PlayerPanel pnl = parent.getPlayerPanel();
		
		//Create Main, Background Panel
		panel = new PlotTimePanel(0,0,WINDOW_WIDTH,WINDOW_HEIGHT-25);
			panel.setLayout(null);
						
		
		this.add(panel);
	}
	
	public void actionPerformed(ActionEvent e)
	{
	}
}
