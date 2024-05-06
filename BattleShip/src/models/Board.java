package models;

import java.util.ArrayList;
import java.util.HashMap;
import shiplayout.Ship;
import shiplayout.SnapGrid;

public class Board {
	
	private static int boardIDs = 1;
	
	private int boardID;
	private Grid playerGrid;
	private Grid opponentGrid;
	private String opponentName;
	private int gameID;
	private String username;
	private HashMap<String,ArrayList<Integer>> finalShipLocations;
	
	HashMap<String, Integer> shipTypes = new HashMap<String, Integer>();
	
	private void populateShipTypes() {
		shipTypes.put("Carrier",5);
		shipTypes.put("Battleship",4);
		shipTypes.put("Cruiser",3);
		shipTypes.put("Submarine",3);
		shipTypes.put("Destroyer",2);
	}

	
	public Board(int gameID, String username,String opponentName) {
		this.boardID = boardIDs++;
		this.gameID = gameID;
		this.username = username;
		this.opponentName = opponentName;
		playerGrid = new Grid(this.boardID);
		opponentGrid = new Grid(this.boardID);
		populateShipTypes();
	}
	
	public void initializeOpponentGrid(HashMap<String,ArrayList<Integer>> opponentShipLocations) {
		for(HashMap.Entry<String,ArrayList<Integer>> entry: opponentShipLocations.entrySet()) {
			for(Integer pos: entry.getValue()) {
				opponentGrid.setValue(-1, pos);
			}
		}
	}
	
	public Grid getPlayerGrid() {
		return playerGrid;
	}
	
	public int getBoardId() {
		return this.boardID;
	}
	
	public void setFinalShipLocations(HashMap<String,ArrayList<Integer>> fsl) {
		this.finalShipLocations = fsl;
		initializePlayerGrid();
	}
	
	
	public void initializePlayerGrid() {
		for(HashMap.Entry<String,ArrayList<Integer>> entry: finalShipLocations.entrySet()) {
			for(Integer pos: entry.getValue()) {
				playerGrid.setValue(-1, pos);
			}
		}
	}
	
	public int opponentHitOrMiss(int box) {
		int value = opponentGrid.getPosValue(box);
		if(value == -1) {
			opponentGrid.setValue(1, box);
			return 1;
		}
		return 0;
	}
	
	public int thisHitOrMiss(int box) {
		int value = playerGrid.getPosValue(box);
		if(value == -1) {
			playerGrid.setValue(1, box);
			return 1;
		}
		return 0;
	}
	
	public int checkPlayerGridScore() {
		return playerGrid.getTotalPoints();
	}
	
	public int checkOpponentGridScore() {
		return opponentGrid.getTotalPoints();
	}
	
}