package controller;

import views.ShipView;
import Client.Client;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import controller.PlayGameClient.setGridHandler;
import javax.swing.JButton;

public class GameControllerClient {
	
	private HashMap<String,ArrayList<Integer>> finalShipLocations;
	private ShipView sv;
	private Client c;
	private int currentShot = -1;
	private JButton currentButton;
	
	public GameControllerClient(ShipView shipview, Client client) {
		this.sv = shipview;
		this.c = client;
	}
	

	
	
	
	public void setFinalShipLocations(HashMap<String,ArrayList<Integer>> finalShipLocations) {
		this.finalShipLocations = finalShipLocations;
		System.out.println("FINAL: "  +finalShipLocations);
		sv.addButtonGrid();
		int[] positions = new int[101];
		for (HashMap.Entry<String, ArrayList<Integer>> entry : finalShipLocations.entrySet()) {
			for (int i: entry.getValue()) {
				positions[i] = -1;
			}
			System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
		}
		c.getFinalShipLocations(positions);
	}
	
	public void takeTurn() {
		sv.enableButtonGrid();
	}
	
	public void getCurrentShot(int cs, JButton jb) {
		this.currentShot = cs;		
		System.out.println("now its in gcc");
		this.currentButton = jb;
//		c.setCurrentShot(this.currentShot);
	}
	
	public void updateButton(Boolean result) {
		if (!result) {
			System.out.println("miss :(" + currentButton.getText());
			currentButton.setForeground(Color.white);
			sv.repaint();
		} else {
			System.out.println("hit! :)" + currentButton.getText() );
			currentButton.setForeground(Color.red);
			sv.repaint();
		}
		currentShot = -1;

//		sv.disableButtonGrid();
	}
	
	public int checkCurrentShot() {
		return this.currentShot;
	}
	
	
	
	
	
	
}

