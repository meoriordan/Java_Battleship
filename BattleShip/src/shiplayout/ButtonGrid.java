package shiplayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ButtonGrid extends JPanel{
	private static final ArrayList<String> LETTERS = new ArrayList<String>(Arrays.asList("A","B","C","D","E","F","G","H","I","J"));
	private static final ArrayList<String> NUMBERS = new ArrayList<String>(Arrays.asList("","1","2","3","4","5","6","7","8","9","10"));
	private int squareSize;
	private SnapGrid sg;
	private HashMap<JButton,Boolean> pushedButtons;
	
	public ButtonGrid(SnapGrid sg){
		this.sg = sg;
		//setSquareSize();
		pushedButtons = new HashMap<JButton,Boolean>();
		GridLayout gd = new GridLayout(11,11,0,0);
		
		//JButton button1 = new JButton("1");
		int numCount = 0;
		int letCount = 0;
		int fontSize = squareSize/2;
		int boxCount = 1;
		//Dimension d = new Dimension(squareSize,squareSize);
		for(int horiz = 0; horiz<11; horiz++) {
			for(int vert = 0; vert<11; vert++) {
				if(vert == 0 && horiz == 0) {
					JLabel textLabel = new JLabel(NUMBERS.get(numCount),SwingConstants.CENTER);
					//textLabel.setSize(d);
					textLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
					add(textLabel);
					numCount++;
				}
				if(vert == 0 && horiz >0) {
					JLabel textLabel = new JLabel(NUMBERS.get(numCount),SwingConstants.CENTER);
					//textLabel.setSize(d);
					textLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
					add(textLabel);
					numCount++;
				}
				if(horiz == 0 && vert >0) {
					JLabel textLabel = new JLabel(LETTERS.get(letCount),SwingConstants.CENTER);
					textLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
					//textLabel.setSize(d);
					add(textLabel);
					//gd.addLayoutComponent(LETTERS.get(numCount),textLabel);
					letCount++;
				}
				if(vert > 0 && horiz >0){
					JButton jb = new JButton(String.valueOf(boxCount));
					jb.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
					jb.setBackground(Color.WHITE);
					jb.addActionListener(new buttonPushListener());
					//jb.setSize(d);
					add(jb);
					pushedButtons.put(jb,false);
					//gd.addLayoutComponent(String.valueOf(boxCount),jb);
					boxCount +=1;
				}
				
			}
		}
		this.setVisible(true);
		this.setLayout(gd);
		this.setSize(getPreferredSize());
	}
	
	class buttonPushListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton jb = (JButton) e.getSource();
			jb.setBackground(Color.RED);
			pushedButtons.put(jb,true);
			jb.removeActionListener(this);
			
		}
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		setSize(getPreferredSize());
	}
	
	public Dimension getPreferredSize() {
		squareSize = sg.getSquareSize();
		int w = (int) (squareSize*12.5);
		int h = (int) (squareSize*12.5);
//		Dimension d = super.getPreferredSize();
//		Container c = getParent();
//		if (c!=null) {
//			d = c.getSize();
//		}
//		int w = (int) (2*d.getWidth()/3);
//		int h = (int) (d.getHeight());
		//int s = (w<h ? w:h)-60;
		int s = (w<h ? w:h);//-60;
		Dimension nd = new Dimension(s,s);
		//c.setSize((int)(2.5*s), s);
		return nd;
		
	}
	
	public static void main(String args[]) {
		JFrame frame = new JFrame("SNAP GRID TEST");
		SnapGrid sg = new SnapGrid();
		ButtonGrid bg = new ButtonGrid(sg);
		frame.setLayout(new BorderLayout());
		frame.add(sg,BorderLayout.EAST);
		frame.add(bg,BorderLayout.CENTER);
		frame.setSize(new Dimension(1000,1000));
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
