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
	
	String username;
	String password;
	
	Boolean matched = false;
	Thread userListHandle;

	public Client() {
		
		try {
			socket = new Socket("10.16.208.75",9898);
//			socket.setSoTimeout(10000);
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
			toServer.writeUTF("LOGIN");
			toServer.writeUTF(username);
			toServer.writeUTF(password);
			Boolean verify = fromServer.readBoolean();
			if (verify == true) {
				lv.setVisible(false);
				
				Object o = fromServerObj.readObject();
				user = (User) o;


				String x1 = fromServer.readUTF();
				System.out.println("x1: " + x1);
				int x = fromServer.readInt();
				activeUsers = new ArrayList<User>();
				for (int i = 0; i < x; i++) {
					Object o2 = fromServerObj.readObject();
					activeUsers.add((User) o2);
				}

				hv = new HomepageView(user, activeUsers, this);
				hv.setVisible(true);
				
				
//				String message = fromServer.readUTF();

//				System.out.println(message);
//				
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
	
	//function to register a user by sending their username and password to the client and receiving a response
	public void attemptRegistration(String username, String password) {
		System.out.println("attempting registration");
		try {
			toServer.writeUTF("REGISTER");
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
					System.out.println("HERE in client " + user.getUsername());
//					try {
//						Thread.sleep(10000);
//					}
//					catch (InterruptedException e) {
//						e.printStackTrace();
//					}
					String message = fromServer.readUTF();
  
				
					if (message.equals("UPDATE USERS")) {
						System.out.println("HERE in client");
//
						int x = fromServer.readInt();
						activeUsers = new ArrayList<User>();
						for (int i = 0; i < x; i++) {
							Object o2 = fromServerObj.readObject();
							activeUsers.add((User) o2);
						}
						hv.refreshUserList(activeUsers);
						
					} else if (message.equals("ATTEMPTING CONNECTION")) {
						String opponent = fromServer.readUTF();
						String response = hv.receivedConnectionRequest(opponent);
						System.out.println(response);
						try {
							Thread.sleep(10000);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
						toServer.writeUTF(response);
						toServer.flush();


						System.out.println("sent response");

					}
				}
				
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	public void attemptConnection(String opponent) {
		System.out.println("CHECKPOINT");
		try {
			toServer.writeUTF("CONNECT");
			toServerObj.writeObject(user);
			for (User u: activeUsers) {
				if (u.getUsername().equals(opponent)) {
					try {
						toServerObj.writeObject(u);
					} 
					catch (IOException e) {
						e.printStackTrace();
					}

				}
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
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
	}
}


