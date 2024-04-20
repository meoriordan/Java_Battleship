


public class Board {
	
	private static int boardIDs = 1;
	
	private int boardID;
	private Grid playerGrid;
	private Grid opponentGrid;
	private int gameID;
	private int userID;
	
	public Board(int gameID, int userID) {
		this.boardID = boardIDs++;
		this.gameID = gameID;
		this.userID = userID;
		playerGrid = new Grid(this.boardID);
		opponentGrid = new Grid(this.boardID);
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