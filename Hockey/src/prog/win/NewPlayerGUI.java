package prog.win;


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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import prog.Controller;
import prog.panel.PlayerPanel;
import stat.PlayerController;
import stat.prim.Player;



public class NewPlayerGUI extends JFrame implements ActionListener
{
	//VIEW ONE
		private JButton btnAdd;
		private JFormattedTextField fldNumber;
		private JTextField fldFirst, fldLast, fldPos;
		private JPanel panel;
		private Controller parent;
	JLabel label;
	
	private Timer fw;
	
	//1150
	private final static int WINDOW_WIDTH = 300, WINDOW_HEIGHT = 200, TOP_BORDER = 30, BORDER = 15;
	private int prevW, prevH;
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
		prevW = 800 - (BORDER + 80 + 2*BORDER);
		prevH = (int)(prevW*(((double) screenSize.height)/screenSize.width));
		prevW = WINDOW_WIDTH - (BORDER + 80 + 2*BORDER);
	}
	
	int numPic = 0;
	
	

	
	
	public NewPlayerGUI(Controller parent)
	{
		this.parent = parent;
		
		setTitle("Add New Player");
		//con.setBounds(300, 200, WINDOW_WIDTH, WINDOW_HEIGHT);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		
		
		//Create Main, Background Panel
		panel = new JPanel();
			panel.setLayout(null);
		label = new JLabel();					
			panel.add(label);
			
		int curBtnY = TOP_BORDER;
		
		int btnY = 15, dH = 30;
		
		addLabel(15, btnY-6, "Number: ");
		

		NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(1);
	    formatter.setMaximum(100);
	    // If you want the value to be committed on each keystroke instead of focus lost
	    formatter.setCommitsOnValidEdit(true);
	    formatter.setAllowsInvalid(true);
		fldNumber = new JFormattedTextField(formatter);
			fldNumber.setBounds(90, btnY, 80, 18);
			fldNumber.addActionListener(this);
			panel.add(fldNumber);
					
		btnY += dH;
			
		addLabel(15, btnY-6, "First Name: ");
		fldFirst = addTextField(90, btnY);
			btnY += dH;
		
		addLabel(15, btnY-6, "Last Name: ");
		fldLast = addTextField(90, btnY);
			btnY += dH;
			
		addLabel(15, btnY-6, "Position: ");
		fldPos = addTextField(90, btnY);
			btnY += dH;
		
		btnAdd = new JButton("Add Player");
		btnAdd.setBounds(5, btnY, 250, 18);
		btnAdd.addActionListener(this);
		panel.add(btnAdd);
						
		
		this.add(panel);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == btnAdd) {
			PlayerPanel pnl = parent.getPlayerPanel();
			
			String num, first, last, pos;
			num = fldNumber.getText();
			first = fldFirst.getText();
			last = fldLast.getText();
			pos = fldPos.getText();
			
			if(num.trim().length() == 0 || first.trim().length() == 0 || last.trim().length() == 0 || pos.trim().length() == 0)
				return;
				
			//Add Player to Player Panel
			PlayerController.addPlayer(num,null,first,last,pos);
			pnl.autoscrollToBottom();
			
			//Close Window
			dispose();
		}
	}
	
	public void addLabel(int x, int y, String text) {
		JLabel lbl = new JLabel(text);
		lbl.setBounds(x,y, 80, TOP_BORDER);
		panel.add(lbl);
	}
	
	public JTextField addTextField(int x, int y) {
		JTextField fld = new JTextField();
		fld.setBounds(x,y, 80, 18);
		panel.add(fld);
		
		return fld;
	}
}
