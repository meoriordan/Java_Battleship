package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;

import shiplayout.Ship2;
import shiplayout.SnapGrid;

public class SaveButtonListener implements ActionListener {
	private SnapGrid sg;
//	private PlayGameClient pgc;
	private GameControllerClient gcc;
	
	public SaveButtonListener(SnapGrid sg, GameControllerClient gcc) {
		this.sg = sg;
		this.gcc = gcc;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		HashMap<String,ArrayList<Integer>> finalShipLocations = sg.getFinalShipLocations();
		JButton jb = (JButton)e.getSource();
		sg.remove(jb);
		gcc.setFinalShipLocations(finalShipLocations);		
	}
}
