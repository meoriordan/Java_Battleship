package models;


import dao.UserDao;

public class Game {
	
	private static int gameIDs;
	
	private int gameID;
	private User user0;
	private User user1;
	private int winner = 0;
	private Boolean gameOver;
	private UserDao userDao;

	
	public Game(User user0, User user1) {
		this.gameID = gameIDs ++;
		this.user0 = user0;
		this.user1 = user1;
		this.userDao = new UserDao();
	}
	
	public void gameOver(User winner) {
		userDao.insertGame(user0.getUsername(), user1.getUsername(), winner.getUsername());
		userDao.updatePoints(winner);
//		this.winner = winner.getUserID();
		
	}
	
	public int getWinner() {
		return winner;
	}
	
	
	
	

	
	
	
	
}