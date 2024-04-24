package controller;

import models.User;


//server sends list of users that are logged in and free for a game 
//this class enables a user is picked in the view to send info to 

public class ConnectUsers {
	
	private User user;
	
	public ConnectUsers() {
		
	}
	
	
	
	public User getUser() {
		return this.user;
	}
	
	
	public void attemptConnection(User user1, User user2) {
		//client sends message to server which sends message to that client to see if they want to connect 
		//client sends the response to this function
		
		
	}
	
	
	
}