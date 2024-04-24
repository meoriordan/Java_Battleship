package controller;

import views.HomepageView;
import views.LoginView;
import models.User;


public class LoginController {
	
	String username;
	String password;
	LoginView lv;
	HomepageView hv;
	User user;
	
	/*this class handles taking the username and password 
	 * entered in the view and passes them to the model to authenticate the user 
	 * 
	 * if successful it received a user object that it can pass to other controller functions*/
	 
	public LoginController(String username, String password, LoginView lv) {
		this.username = username;
		this.password = password;
		this.lv = lv;
	}
	
	public boolean verifyInfo() {
		if (username.equals("Elizabeth")) {
			lv.setVisible(false);
//			user = new User(1,"Elizabeth","test",0);
//			HomepageView hv = new HomepageView(user);
//			hv.setVisible(true);
			return true;
		} 
		else {
			return false;
		}
	}
	
	
	
	
	
	
}