package models;

import java.util.ArrayList;
import java.util.HashMap;

import shiplayout.Ship;
import shiplayout.Ship2;
import shiplayout.SnapGrid;

public class Board {
	
	private static int boardIDs = 1;
	
	private int boardID;
	private Grid playerGrid;
	private Grid opponentGrid;
	private int opponentID;
	private int gameID;
	private int userID;
	//private boolean savedShipLocation;
	private HashMap<Ship2,ArrayList<Integer>> finalShipLocations;
	private ArrayList<Ship> ships;
	private SnapGrid sg;
	
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
		//savedShipLocation = false;
		playerGrid = new Grid(this.boardID);
		sg = new SnapGrid(); //UI Component 
		Thread t = new Thread(new HandleShipPlacement());
		t.start();
		//opponentGrid = new Grid(this.boardID);
		ships = new ArrayList<Ship>();
		
		populateShipTypes();
		
		for (String s: shipTypes.keySet()) {
			ships.add(new Ship(this.boardID,s,shipTypes.get(s)));
		}
	}
	
	class HandleShipPlacement implements Runnable{

		@Override
		public void run() {
			while(!sg.isSaved()) {
				
			}//I somehow need to send a message to my opponent when I have saved my board ie ready to start game.
			
			finalShipLocations = sg.getFinalShipLocations();
			initializePlayerGrid();
			
		}
		
	}
	
	public void setOpponent( Grid opponentGrid) {
		this.opponentGrid = opponentGrid;
	}
	
	public Grid getPlayerGrid() {
		return playerGrid;
	}
	
	public int getBoardId() {
		return this.boardID;
	}
	
	
	public void initializePlayerGrid() {
		//something about placing the ships on the board
		//when a ship is placed on grid, mark those values on the board
		for(HashMap.Entry<Ship2,ArrayList<Integer>> entry: finalShipLocations.entrySet()) {
			for(Integer pos: entry.getValue()) {
				playerGrid.setValue(-1, pos);
			}
		}
		
	}
	
	public int checkPlayerGridScore() {
		return playerGrid.getTotalPoints();
	}
	
}