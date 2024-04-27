package shiplayout;

import java.sql.DriverManager;
import java.sql.*;


public class JavaSqlConn {
	String url;
	Connection conn;
	
	
	public JavaSqlConn() {
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
	
	public void createUser(String username, String password) {
		try {
			Statement statement = conn.createStatement();
			int x = statement.executeUpdate("INSERT INTO users (username, password) VALUES " + "('" + username + "'" + ",'" + password + "')");
			System.out.println(x);	
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		
	}
	
	public ResultSet loginUser(String username, String password) {
		boolean loginSuccess = false;;
		ResultSet resultSet = null;
		try {
			Statement statement = conn.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM users WHERE username = '" + username + "' and password  = '" + password + "';");
		}
		catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return resultSet;

		
	}
	
	public static void main(String[] args) throws SQLException {
		String url = "jdbc:sqlite:/Users/elizabethoriordan/battleship.db";
		Connection conn = DriverManager.getConnection(url);
		
		   System.out.println("Database connected");

		    // Create a statement
		    Statement statement = null;
			try {
				statement = conn.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(0);
			}

		    // Execute a statement
		    ResultSet resultSet = null;
			try {
				resultSet = statement.executeQuery
				  ("select * from users;'");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}

		    // Iterate through the result and print the student names
		    try {
				while (resultSet.next())
				  System.out.println(resultSet.getString(1) + "\t" +
				    resultSet.getString(2));
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(0);
			}

		    // Close the connection
		    try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
		  }
		}

