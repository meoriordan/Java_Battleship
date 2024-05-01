package models;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {
	
	private static int gameIDs;
	
	private int gameID;
	private User user1;
	private User user2;
	private int winner;
	private Board board1;
	private Board board2;
	private Boolean gameOver;

	
	public Game(User user1, User user2) {
		this.gameID = gameIDs ++;
		this.user1 = user1;
		this.user2 = user2;
		board1 = new Board(this.gameID, user1.getUserID());
		board2 = new Board(this.gameID, user2.getUserID());
		
		//Set boards as each others opponent
		//board0.setOpponent(board1.getPlayerGrid());
	//	board1.setOpponent(board0.getPlayerGrid());
		
	}
	
//	public void startGame() {
//		setBoards();
//		playGame();
//	}
	
	public Board getBoard1() {
		return board1;
	}
	
	public Board getBoard2() {
		return board2;
	}
	
	public Boolean takeTurnGame(User u, int pos) {
		if (u.getUsername().equals(user1.getUsername())) {
			return board2.checkHit(pos);
		} else {
			return board1.checkHit(pos);
		}
	}
	
	
	
//	private void setBoards() {
//		//code for boards getting initial positions of ships
//		board0.initializePlayerGrid();
//	//	board1.initializePlayerGrid();
//	}
	

	
//	public int playGame() {
//		
//		int turnsTaken = 0;
//		
//		while (!gameOver) {
//			
//			if (turnsTaken % 2 == 0 ) {
//				takeTurn(user1);
//				turnsTaken += 1;
//			}
//			else {
//				takeTurn(user1);
//				turnsTaken += 1;
//			}   
//			
//			if (board1.checkPlayerGridScore() == 17) {
//				winner = 1;
//				user2.winGame();
//				gameOver = true;
//			}
//			
//			if (board2.checkPlayerGridScore() == 17) {
//				winner = 0;
//				user1.winGame();
//				gameOver = true;
//			}
//		
//		}
//		
//		return 
//			
//	}
	
	public void takeTurn(User u, int shot) {
		//user board is activated 
		//user selects positions 
		//position is passed to server which is passed to controller 
		//controller sends position to game to see if the opponent has ship there 
		//returns true or false to the player 
		//returns whether game is continuing or ending to the client 
		
		//maybe user passes it's selection here and this function updates the boards?
		//something to do with boards
		//user places peg on grid where there is not already a peg (this happens on both boards)
		//check if game is won 	
	}
	
	
	
	
}