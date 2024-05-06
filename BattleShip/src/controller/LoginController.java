package controller;

import java.util.ArrayList;
import views.HomepageView;
import views.LoginView;
import dao.UserDao;
import models.User;

public class LoginController {
	
	String username;
	String password;
	User user;
	UserDao ud;
	
	/*this class handles taking the username and password 
	 * entered in the view and passed through the client to the server to this controller 
	 * it verifies the existence of this username and password in the database 
	 * and sends user an object back if authentication is successful*/
	
	public LoginController(String username, String password) {
		this.username = username;
		this.password = password;
		this.ud = new UserDao();
	}
	
	public boolean verifyInfo() {
		boolean successfulLogin = ud.findUser(username, password);
		return successfulLogin;
	}
	
	public User retrieveUser() {
		this.user = ud.retrieveUser(username, password);
		return this.user;
	}
	
	public ArrayList<ArrayList<String>> retrieveGames() {
		retrieveUser();
		ArrayList<ArrayList<String>> games = ud.getGames(user.getUserID());
		return games;
	}

}