package models;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {
	
	private static int boardIDs = 1;
	
	private int boardID;
	private Grid playerGrid;
	private Grid opponentGrid;
	private int gameID;
	private int userID;
	private ArrayList<Ship> ships;
	
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
		playerGrid = new Grid(this.boardID);
		opponentGrid = new Grid(this.boardID);
		ships = new ArrayList<Ship>();
		
		populateShipTypes();
		
		for (String s: shipTypes.keySet()) {
			ships.add(new Ship(this.boardID,s,shipTypes.get(s)));
		}
	}
	
	public int getBoardId() {
		return this.boardID;
	}
	
	
	public void initializePlayerGrid() {
		//something about placing the ships on the board
		//when a ship is placed on grid, mark those values on the board
		
	}
	
	public int checkPlayerGridScore() {
		return playerGrid.getTotalPoints();
	}
	
}