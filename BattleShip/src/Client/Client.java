package Client;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

import javax.swing.*;

import controller.ConnectUsers;
import controller.LoginController;
import models.User;
import views.HomepageView;
import views.LoginView;
import views.RegisterView;
import views.ShipView;


//connect to server upon opening
//view login screen upon connecting 
//view homepage screen upon authentication 
//view game screen upon connection w another user 


//client is getting information from the server & from its views (login, homepage, game) 


public class Client  {
	
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;
	
	ObjectOutputStream toServerObj = null;
	ObjectInputStream fromServerObj = null;
	
	Socket socket = null;
	
	ArrayList<User> activeUsers = null;
	private User user;
	
	LoginView lv;
	HomepageView hv;
	RegisterView rv;
	ShipView sv;
	
	String username;
	String password;
	
	Boolean matched = false;
	Thread userListHandle;

	public Client() {
		
		try {
			socket = new Socket("192.168.1.182",9898);
		    fromServer = new DataInputStream(socket.getInputStream());
		    toServer = new DataOutputStream(socket.getOutputStream());
		    
		    fromServerObj = new ObjectInputStream(socket.getInputStream());
		    toServerObj = new ObjectOutputStream(socket.getOutputStream());
		    
			lv = new LoginView(this);
		    lv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    lv.setVisible(true);
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	//function to verify login info by sending to server and receiving a response
	public void verifyLogin() {
		
		username = lv.getUserName();
		password = lv.getPassword();
		
		try {
			toServer.writeUTF("LOGIN"); //server function message
			toServer.writeUTF(username);
			toServer.writeUTF(password);
			Boolean verify = fromServer.readBoolean();
			if (verify == true) {
				lv.setVisible(false);
				
				Object o = fromServerObj.readObject();
				user = (User) o;

				String x1 = fromServer.readUTF(); //client function message
				int x = fromServer.readInt();
				activeUsers = new ArrayList<User>();
				for (int i = 0; i < x; i++) {
					Object o2 = fromServerObj.readObject();
					activeUsers.add((User) o2);
				}

				hv = new HomepageView(user, activeUsers, this);
				hv.setVisible(true);
			    hv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						
				userListHandle = new Thread(new UpdatesFromServer());
				userListHandle.start();
			}
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//when a user choose register switch to that screen
	public void switchToRegistration() {
		lv.setVisible(false);
		rv = new RegisterView(this);
		rv.setVisible(true);
	}
	
	//function to register a user by sending their username and password to the server and receiving a response
	public void attemptRegistration(String username, String password) {
		try {
			toServer.writeUTF("REGISTER"); //server function message
			toServer.writeUTF(username);
			toServer.writeUTF(password);
			Boolean success;
			success = fromServer.readBoolean(); 
			if (success) {
				lv.setVisible(true);
				rv.setVisible(false);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class UpdatesFromServer implements Runnable {
		
		public void run() {
			try {
				while(true) {
					String message = fromServer.readUTF(); //client function message
					System.out.println(message + "message in the main loop :(");
					if (message.equals("UPDATE USERS")) { //client function message
						updateUsers();					
					} else if (message.equals("ATTEMPTING CONNECTION")) { //client function message
						toServer.writeUTF("considering request");
						String opponent = fromServer.readUTF();
						String response = hv.receivedConnectionRequest(opponent);
						toServer.writeUTF(response);
						toServer.flush();
						startGame(opponent);
					} 
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void attemptConnection(String opponent) {
		try {
			toServer.writeUTF("CONNECT");
			toServerObj.writeObject(user);
				try {
					toServer.writeUTF(opponent);
					System.out.println("RIGHT HERE" + user.getUsername());
					toServer.writeUTF("ha ha ha ");
					
					String response = fromServer.readUTF();
					if (response.equals("accept")) {
						System.out.println("GAME ACCEPTED!");
						startGame(opponent);
					}
				} 
				catch (IOException e) {
					e.printStackTrace();
				}

		}
		catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	public void updateUsers() {
		try {
			int x = fromServer.readInt();
			activeUsers = new ArrayList<User>();
			for (int i = 0; i < x; i++) {
				Object o2 = fromServerObj.readObject();
				activeUsers.add((User) o2);
			}
			hv.refreshUserList(activeUsers);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void startGame(String opponent) {
		hv.setVisible(false);
		sv = new ShipView();
		sv.setVisible(true);
	}
	
	public static void main(String[] args) {
		Client client = new Client();
	}
}


