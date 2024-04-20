import java.util.ArrayList;
import java.util.HashMap;

public class Game {
	
	private int gameID;
	private User user0;
	private User user1;
	private Boolean winner;
	private int score0;
	private int score1;
	private Board board0;
	private Board board1;
	private ArrayList<Ship> ships0;
	private ArrayList<Ship> ships1;
	
	public Game(int gameID, User user0, User user1) {
		this.gameID = gameID;
		this.user0 = user0;
		this.user1 = user1;
		score0 = 0;
		score1 = 0;
		board0 = new Board(user0);
		board1 = new Board(user1);
		
		ships0 = new ArrayList<Ship>;
		ships1 = new ArrayList<Ship>;
		
		HashMap<String, Integer> shipTypes = new HashMap<String, Integer>();
		shipTypes.put("",0);
		
		
	}
	
	public void takeTurn(User u) {
		//something to do with boards
		//user places peg on grid where there is not already a peg (this happens on both boards)
		//check if game is won 
		
		
	}
		
				
				
		
				
		
		
		
				
	}
	
	
	
	
}