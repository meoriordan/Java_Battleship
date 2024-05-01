package models;

import java.util.ArrayList;
import java.util.HashMap;

//import shiplayout.Ship;
import shiplayout.Ship2;
import shiplayout.SnapGrid;

public class Board {
	
	private static int boardIDs = 1;
	
	private int boardID;
	private Grid playerGrid;
	private Grid opponentGrid;
	private String opponentName;
	private int gameID;
	private String username;
	private int userID;
	//private boolean savedShipLocation;
	private HashMap<String,ArrayList<Integer>> finalShipLocations;
	private ArrayList<Ship> ships;
	//private SnapGrid sg;
	
	HashMap<String, Integer> shipTypes = new HashMap<String, Integer>();
	

	private void populateShipTypes() {
		shipTypes.put("Carrier",5);
		shipTypes.put("Battleship",4);
		shipTypes.put("Cruiser",3);
		shipTypes.put("Submarine",3);
		shipTypes.put("Destroyer",2);
	}

	
	public Board(int gameID, int userID) {
		this.boardID = boardIDs++;
		this.gameID = gameID;
		this.userID = userID;
//		this.username = username;
//		this.opponentName = opponentName;
		//savedShipLocation = false;
		playerGrid = new Grid(this.boardID);
		opponentGrid = new Grid(this.boardID);
//		sg = new SnapGrid(); //UI Component 
//		Thread t = new Thread(new HandleShipPlacement());
//		t.start();
		//opponentGrid = new Grid(this.boardID);
		ships = new ArrayList<Ship>();
		
		populateShipTypes();
		
		for (String s: shipTypes.keySet()) {
			ships.add(new Ship(this.boardID,s,shipTypes.get(s)));
		}
	}
	
	public Boolean checkHit(int pos) {
		int value = playerGrid.getPosValue(pos);
		if (value == -1) {
			return true;
		} 
		else {
			return false;
		}
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
		//something about placing the ships on the board
		//when a ship is placed on grid, mark those values on the board
		for(HashMap.Entry<String,ArrayList<Integer>> entry: finalShipLocations.entrySet()) {
			for(Integer pos: entry.getValue()) {
				playerGrid.setValue(-1, pos);
			}
		}
		
	}
	
	public int opponentHitOrMiss(int box) {
		//will return 0 for miss or 1 for hit
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
	
}