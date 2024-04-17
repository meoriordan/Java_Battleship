package shiplayout;

public class User {
	private int userId;
	private String username;
	private String password;
	private int totalPoints;
	
	public User(int userId, String username, String password, int totalPoints) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.totalPoints = totalPoints;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public int getUserId() {
		return this.userId;
	}
	
}