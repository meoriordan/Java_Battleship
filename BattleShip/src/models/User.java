package models;


public class User {
	
	private int userID;
	private String username;
	private String password;
	private int totalPoints;
	
	public User(int userID, String username, String password, int totalPoints) {
		this.userID = userID;
		this.username = username;
		this.password = password;
		this.totalPoints = totalPoints;
	}
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getTotalPoints() {
		return totalPoints;
	}
	public void setTotalPoints(int totalPoints) {
		this.totalPoints = totalPoints;
	}
	
	public void winGame() {
		this.totalPoints += 1;
	}
	
	
	
}