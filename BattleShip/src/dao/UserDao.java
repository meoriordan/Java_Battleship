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
	GAME_OVER = "INSERT INTO GAMES () VALUES ()";
	
	
	public UserDao() {
		try {
			url = "jdbc:sqlite:C:/sqlite/battleship.db";
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
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return user;

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

