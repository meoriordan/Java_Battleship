package controller;

import dao.UserDao;
import models.User;

public class RegisterController {
	
	String username;
	String password;
	UserDao ud;
	
	/*this class handles taking the username and password 
	 * entered in the view and passed through the client to the server to this controller 
	 * it attempts to create a user in the database 
	 * and sends a boolean to indicate whether the user was successfully created*/
	
	public RegisterController(String username, String password) {
		this.username = username;
		this.password = password;
		this.ud = new UserDao();
	}
	
	public boolean createUser() {
		boolean successfulCreation = ud.insertUser(username, password);
		return successfulCreation;
	}
	
//	public User retrieveUser() {
//		user = ud.retrieveUser(username, password);
//		return user;
//	}

}