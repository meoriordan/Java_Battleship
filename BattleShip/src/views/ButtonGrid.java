package views;

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

import controller.GridButtonListener;
import models.Grid;

public class ButtonGrid extends JPanel{
	private static final ArrayList<String> LETTERS = new ArrayList<String>(Arrays.asList("A","B","C","D","E","F","G","H","I","J"));
	private static final ArrayList<String> NUMBERS = new ArrayList<String>(Arrays.asList("","1","2","3","4","5","6","7","8","9","10"));
	private int squareSize;
	private SnapGrid sg;
	private HashMap<JButton,Boolean> pushedButtons;
	private HashMap<Integer,JButton> buttonMap;
	
	public ButtonGrid(SnapGrid sg){
		this.sg = sg;
		pushedButtons = new HashMap<JButton,Boolean>();
		buttonMap = new HashMap<Integer,JButton>();
		GridLayout gd = new GridLayout(11,11,0,0);
		int numCount = 0;
		int letCount = 0;
		int fontSize = 16;
		int boxCount = 1;
		for(int horiz = 0; horiz<11; horiz++) {
			for(int vert = 0; vert<11; vert++) {
				if(vert == 0 && horiz == 0) {
					JLabel textLabel = new JLabel(NUMBERS.get(numCount),SwingConstants.CENTER);
					textLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
					add(textLabel);
					numCount++;
				}
				if(vert == 0 && horiz >0) {
					JLabel textLabel = new JLabel(NUMBERS.get(numCount),SwingConstants.CENTER);
					textLabel.setFont(new Font("Arial",Font.BOLD,fontSize));
					textLabel.setForeground(Color.black);
					textLabel.setVisible(true);
					textLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
					add(textLabel);
					numCount++;
				}
				if(horiz == 0 && vert >0) {
					JLabel textLabel = new JLabel(LETTERS.get(letCount),SwingConstants.CENTER);
					textLabel.setFont(new Font("Arial",Font.BOLD,fontSize));
					textLabel.setForeground(Color.black);
					textLabel.setVisible(true);
					textLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
					add(textLabel);
					letCount++;
				}
				if(vert > 0 && horiz >0){
					JButton jb = new JButton();
					jb.setName(String.valueOf(boxCount));
					jb.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
					jb.setBackground(Color.WHITE);
					add(jb);
					pushedButtons.put(jb,false);
					buttonMap.put(boxCount,jb);
					boxCount +=1;
				}
			}
		}
		this.setVisible(true);
		this.setLayout(gd);
		this.setSize(getPreferredSize());
	}
	
	public void setPushedButtons(HashMap<JButton,Boolean> pushedButtons){
		this.pushedButtons = pushedButtons;
	}
	
	public HashMap<JButton,Boolean> getPushedButtons() {
		return this.pushedButtons;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setFont(new Font("Arial",Font.BOLD,sg.getSquareSize()/2));
		setSize(getPreferredSize());
	}

	public Dimension getPreferredSize() {
		squareSize = sg.getSquareSize();
		int w = (int) (squareSize*12.5);
		int h = (int) (squareSize*12.5);
		int s = (w<h ? w:h);
		Dimension nd = new Dimension(s,s);
		return nd;
	}
	
	public void disableButtonGrid() {
		for(JButton button : pushedButtons.keySet()) {
			button.setEnabled(false);
		}
		for(HashMap.Entry<JButton,Boolean> button : pushedButtons.entrySet()) {
			if(!button.getValue()) { 
				button.getKey().setBackground(Color.WHITE);
			}
		}
	}
	
	public void enableButtonGrid() {
		for(HashMap.Entry<JButton,Boolean> button : pushedButtons.entrySet()) {
			if(!button.getValue()) { 
				button.getKey().setEnabled(true);
				button.getKey().setBackground(Color.lightGray);
			}
		}
	}
	
	public void addButtonListener(int i,GridButtonListener gbl) {
		JButton button = buttonMap.get(i);
		button.addActionListener(gbl);
	}
}
