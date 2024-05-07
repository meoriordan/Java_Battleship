package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;

import views.Ship;
import views.SnapGrid;

public class SaveButtonListener implements ActionListener {
	private SnapGrid sg;
	private PlayGameClient pgc;
	
	public SaveButtonListener(SnapGrid sg, PlayGameClient pgc) {
		this.sg = sg;
		this.pgc = pgc;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		HashMap<String,ArrayList<Integer>> finalShipLocations = sg.getFinalShipLocations();
		pgc.setFinalShipLocations(finalShipLocations);			
		JButton jb = (JButton)e.getSource();	
		sg.remove(jb);
	}
}
