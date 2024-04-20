import java.util.ArrayList;
import java.util.HashMap;

public class Game {
	
	private static int gameIDs;
	
	private int gameID;
	private User user0;
	private User user1;
	private int winner;
	private int score0;
	private int score1;
	private Board board0;
	private Board board1;
	private ArrayList<Ship> ships0;
	private ArrayList<Ship> ships1;
	private Boolean gameOver;
	
	HashMap<String, Integer> shipTypes = new HashMap<String, Integer>();
	

	private void populateShipTypes() {
		shipTypes.put("Carrier",5);
		shipTypes.put("Battleship",4);
		shipTypes.put("Cruiser",3);
		shipTypes.put("Submarine",3);
		shipTypes.put("Destroyer",2);
	}

	
	public Game(User user0, User user1) {
		this.gameID = gameIDs ++;
		this.user0 = user0;
		this.user1 = user1;
		score0 = 0;
		score1 = 0;
		board0 = new Board(this.gameID, user0.getUserID());
		board1 = new Board(this.gameID, user1.getUserID());
		
		ships0 = new ArrayList<Ship>();
		ships1 = new ArrayList<Ship>();
		
		populateShipTypes();
		
		for (String s: shipTypes.keySet()) {
			ships0.add(new Ship(board0.getBoardId(),s,shipTypes.get(s)));
			ships1.add(new Ship(board1.getBoardId(),s,shipTypes.get(s)));
		}
		
		
	}
	
	public void takeTurn(User u) {
		//something to do with boards
		//user places peg on grid where there is not already a peg (this happens on both boards)
		//check if game is won 	
		
	}
	
	public void playGame() {
		
		int turnsTaken = 0;
		
		while (!gameOver) {
			
			if (turnsTaken % 2 == 0 ) {
				takeTurn(user0);
				turnsTaken += 1;
			}
			else {
				takeTurn(user1);
				turnsTaken += 1;
			}
			
			if (board0.checkPlayerGridScore() == 17) {
				winner = 1;
				user1.winGame();
				gameOver = true;
			}
			
			if (board1.checkPlayerGridScore() == 17) {
				winner = 0;
				user0.winGame();
				gameOver = true;
		}
		
		
	}
		
				
				
		
				
		
		
		
				
	}
	
	
	
	
}