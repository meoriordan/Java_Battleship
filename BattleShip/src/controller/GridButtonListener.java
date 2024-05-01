package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;

import shiplayout.ButtonGrid;

public class GridButtonListener implements ActionListener{
	private HashMap<JButton,Boolean> pushedButtons;
	private ButtonGrid bg;
//	private PlayGameClient pgc;
	private GameControllerClient gcc;
	
	public GridButtonListener(ButtonGrid bg, GameControllerClient gcc) {
		this.bg = bg;
		this.gcc = gcc;
		pushedButtons = new HashMap<JButton, Boolean>();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton jb = (JButton) e.getSource();
		jb.setBackground(Color.GRAY);
//		System.out.println("the button name is: " + jb.getText());
		int pos = Integer.valueOf(jb.getText());	
		System.out.println("the position pushed is : " + pos);
		pushedButtons.put(jb,true);
		bg.setPushedButtons(pushedButtons);
		jb.removeActionListener(this);
		gcc.getCurrentShot(pos, jb);
//		pgc.giveTurn(jb,pos);
		
	}
}
