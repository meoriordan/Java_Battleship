package controller;

import models.User;
import models.Game;


public class GameController {
	
	private User user1;
	private User user2;
	private Game game;
	private Boolean gameOver = false;
	
	
	public GameController(User u1, User u2) {
		this.user1 = u1;
		this.user2 = u2;
		this.game = new Game(this.user1, this.user2);
	}
	
	public void setBoards(int[] p1, int[] p2) {
		game.getBoard1().getPlayerGrid().setPositions(p1);
		game.getBoard2().getPlayerGrid().setPositions(p2);
	}
	
	public Boolean takeTurn(User u, int shot) {
		return game.takeTurnGame(u, shot);
	}
	
	
}

