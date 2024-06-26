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
	private PlayGameClient pgc;
	
	public GridButtonListener(ButtonGrid bg, PlayGameClient pgc) {
		this.bg = bg;
		this.pgc = pgc;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton jb = (JButton) e.getSource();
		jb.setBackground(Color.GRAY);
		int pos = Integer.valueOf(jb.getName());		
		pushedButtons.put(jb,true);
		bg.setPushedButtons(pushedButtons);
		jb.removeActionListener(this);
		pgc.giveTurn(jb,pos);
		
	}
}
