


public class Board {
	
	private int boardID;
	private Grid playerGrid;
	private Grid opponentGrid;
	private int gameID;
	private int userID;
	
	public Board(int boardID, int gameID, int userID) {
		this.boardID = boardID;
		this.gameID = gameID;
		this.userID = userID;
		playerGrid = new Grid(this.boardID);
		opponentGrid = new Grid(this.boardID);
	}
	
	
	public void initializePlayerGrid() {
		//something about placing the ships on the board
		//when a ship is placed on grid, mark those values on the board
		
	}
	
	
	
	
	
	
}