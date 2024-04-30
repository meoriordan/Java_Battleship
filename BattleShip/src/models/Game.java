package models;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {
	
	private static int gameIDs;
	
	private int gameID;
	private User user0;
	private User user1;
	private int winner;
//	private int score0;
//	private int score1;
	private Board board0;
	private Board board1;
	private Boolean gameOver;

	
	public Game(User user0, User user1) {
		this.gameID = gameIDs ++;
		this.user0 = user0;
		this.user1 = user1;
//		score0 = 0;
//		score1 = 0;
		board0 = new Board(this.gameID, user0.getUserID());
		board1 = new Board(this.gameID, user1.getUserID());
		
		//Set boards as each others opponent
		//board0.setOpponent(board1.getPlayerGrid());
	//	board1.setOpponent(board0.getPlayerGrid());
		
	}
	
	public void startGame() {
		setBoards();
		playGame();
	}
	
	private void setBoards() {
		//code for boards getting initial positions of ships
		board0.initializePlayerGrid();
	//	board1.initializePlayerGrid();
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
	
	public void takeTurn(User u) {
		//maybe user passes it's selection here and this function updates the boards?
		//something to do with boards
		//user places peg on grid where there is not already a peg (this happens on both boards)
		//check if game is won 	
	}
	
	
	
	
}