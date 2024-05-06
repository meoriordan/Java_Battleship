package dao;

import java.sql.DriverManager;

import models.User;

import java.sql.*;


//create new user - DONE
//verify user - DONE
//insert completed game 
//get users games (optional/ later)


public class UserDao {
	
	String url;
	Connection conn;
	
	private static final String 
	INSERT = "INSERT INTO USERS (username, password, totalPoints) VALUES (?,?,?)";
	
	private static final String 
	FIND_BY_USERNAME_PASSWORD = "SELECT * FROM users WHERE username = ? AND password = ?";
	
	private static final String 
	FIND_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
	
	private static final String 
	GAME_OVER = "INSERT INTO games (user0_id, user1_id, winner) VALUES (?,?,?)";
	
	private static final String 
	UPDATE_POINTS = "UPDATE USERS SET totalPoints = ? WHERE user_id = ?";
	
	
	
	public UserDao() {
		try {
			url = "jdbc:sqlite:/Users/elizabethoriordan/battleship.db";
			conn = DriverManager.getConnection(url);
		}
		catch (SQLException e ) {
			e.printStackTrace();
		}
		
		Statement statement = null;
		try {
			statement = conn.createStatement();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean insertUser(String username, String password) {
		try {
			PreparedStatement pstmt = conn.prepareStatement(INSERT);
			
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setInt(3, 0);
			
			int count = pstmt.executeUpdate();
			
			conn.close();
			
			if (count == 1) {
				return true;
			}
			else {
				return false;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public boolean findUser(String username, String password) {
		try {
			PreparedStatement pstmt = conn.prepareStatement(FIND_BY_USERNAME_PASSWORD);
			
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			
			ResultSet rset = pstmt.executeQuery();
			
			if (!rset.next()) {
				return false;
			}
			else {
				return true;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public User retrieveUser(String username, String password) {
		User user = null;
		try {
			PreparedStatement pstmt = conn.prepareStatement(FIND_BY_USERNAME_PASSWORD);
			
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			
			ResultSet rset = pstmt.executeQuery();
			user = new User(rset.getInt(1), username, password, rset.getInt(4));	
			conn.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return user;

	}
	
	public boolean insertGame(String user0, String user1, String winner) {
		try {
					
			PreparedStatement pstmt1 = conn.prepareStatement(FIND_BY_USERNAME);
			
			pstmt1.setString(1,user0);
			
			ResultSet rset1 = pstmt1.executeQuery();
			
			int user0ID = rset1.getInt(1);
			
			PreparedStatement pstmt2 = conn.prepareStatement(FIND_BY_USERNAME);
			
			pstmt1.setString(1, user1);
			
			ResultSet rset2 = pstmt1.executeQuery();
			
			int user1ID = rset2.getInt(1);	
			
			int winnerID;
			
			if (winner.equals(user0)) {
				winnerID = user0ID;
			} else {
				winnerID = user1ID;
			}
			
			PreparedStatement pstmt = conn.prepareStatement(GAME_OVER);
			
			pstmt.setInt(1, user0ID);
			pstmt.setInt(2, user1ID);
			pstmt.setInt(3, winnerID);
			
			int count = pstmt.executeUpdate();
			
			if (count == 1) {
				return true;
			}
			else {
				return false;
			}			
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean updatePoints(User u) {
		try {
			
			PreparedStatement pstmt1 = conn.prepareStatement(FIND_BY_USERNAME);
			
			pstmt1.setString(1,u.getUsername());
			
			ResultSet rset = pstmt1.executeQuery();
			
			int userID = rset.getInt(1);
			int totalPoints = rset.getInt(4);
			
			PreparedStatement pstmt = conn.prepareStatement(UPDATE_POINTS);
			
			totalPoints += 1;
			pstmt.setInt(1,userID);
			pstmt.setInt(2, totalPoints);
			
			int count = pstmt.executeUpdate();
	
			if (count == 1) {
				return true;
			} 
			else {
				return false;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
	
	




//
//	
//	public void createUser(String username, String password) {
//		try {
//			Statement statement = conn.createStatement();
//			int x = statement.executeUpdate("INSERT INTO users (username, password) VALUES " + "('" + username + "'" + ",'" + password + "')");
//			System.out.println(x);	
//		}
//		catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		
//	}
//	
//	public ResultSet loginUser(String username, String password) {
//		ResultSet resultSet = null;
//		try {
//			Statement statement = conn.createStatement();
//			resultSet = statement.executeQuery("SELECT * FROM users WHERE username = '" + username + "' and password  = '" + password + "';");
//		}
//		catch (SQLException e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
//		return resultSet;
//
//		
//	}
//	
//	public static void main(String[] args) throws SQLException {
//		String url = "jdbc:sqlite:/Users/elizabethoriordan/battleship.db";
//		Connection conn = DriverManager.getConnection(url);
//		
//		   System.out.println("Database connected");
//
//		    // Create a statement
//		    Statement statement = null;
//			try {
//				statement = conn.createStatement();
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.exit(0);
//			}
//
//		    // Execute a statement
//		    ResultSet resultSet = null;
//			try {
//				resultSet = statement.executeQuery
//				  ("select * from users;'");
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				System.exit(0);
//			}
//
//		    // Iterate through the result and print the student names
//		    try {
//				while (resultSet.next())
//				  System.out.println(resultSet.getString(1) + "\t" +
//				    resultSet.getString(2));
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.exit(0);
//			}
//
//		    // Close the connection
//		    try {
//				conn.close();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				System.exit(0);
//			}
//		  }
//		}

