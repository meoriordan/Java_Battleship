package dao;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;

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
	
	private static final String 
	GET_GAMES = "SELECT u1.username as user1name, u2.username as user2name, "
			+ " CASE WHEN g.winner = user0_id then u1.username "
			+ "		WHEN g.winner = user1_id then u2.username "
			+ " else null end as winnername FROM games g "
			+ " join users u1 on g.user0_id = u1.user_id"
			+ " join users u2 on g.user1_id = u2.user_id WHERE user0_id = ? or user1_id = ? ;"; 
	
	
	
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
//			conn.close();
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
	
	public ArrayList<ArrayList<String>> getGames(int userID) {
		ArrayList<ArrayList<String>> pastGames = null;
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(GET_GAMES);
			
			pstmt.setInt(1, userID);
			pstmt.setInt(2, userID);	
			
			ResultSet rset = pstmt.executeQuery();
			
			if (rset.isBeforeFirst()) {    
				pastGames = new ArrayList<ArrayList<String>>();
				
				while (rset.next()) {
					pastGames.add(new ArrayList<String>(Arrays.asList(rset.getString(1), rset.getString(2), rset.getString(3))));
				}
			}			
			conn.close();
			return pastGames;

		}
		catch (SQLException e) {
			e.printStackTrace();
			return pastGames;
		}		
	}
}
	
