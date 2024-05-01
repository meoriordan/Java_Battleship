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

import Server.Server;
import controller.GameControllerClient;
//import controller.PlayGameClient.setGridHandler;
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
	
	int[] positions;
	int currentShot = -1;
	
	GameControllerClient gcc;
	
	String username;
	String password;
	
	Boolean matched = false;
	Thread userListHandle;
	
	String opponent = null;

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
		
		boolean gettingUpdates = true;
		
		public void run() {
			try {
				while(true) {
					if (!gettingUpdates) {
						break;
					}
					
					if (user != null) {
						System.out.println(user.getUsername() + "awaiting message: ");
					}
					
					String message = fromServer.readUTF(); //client function message
					
					System.out.println(message + " ::: message" + user.getUsername());
					
					if (message.equals("UPDATE USERS")) { //client function message
						updateUsers();		
						
					} else if (message.equals("CONNECTION ACK")) {
						System.out.println("i can proceed with my connection");
						toServerObj.writeObject(user);
						toServer.writeUTF(opponent);
					}
					
					else if (message.equals("ATTEMPTING CONNECTION")) { //client function message
						toServer.writeUTF("considering request");
						String opponent = fromServer.readUTF();
						String response = hv.receivedConnectionRequest(opponent);
						System.out.println("my response is + " + response);
						toServer.writeUTF(response);
						toServer.flush();
						gettingUpdates = false;
						toServer.writeUTF("STARTING GAME NOW");
						startGame(opponent);
						
					} else if (message.equals("accept")) {
						System.out.println("GAME ACCEPTED!");
						gettingUpdates = false;
						startGame(opponent);
						
					}
					else if (message.equals("GOT CONNECTION REQUEST")) {

						continue;
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
			this.opponent = opponent;
//			toServerObj.writeObject(user);
//			toServer.writeUTF(opponent);

//				try {
//					toServer.writeUTF(opponent);
//					System.out.println("RIGHT HERE" + user.getUsername());
//					this.opponent = opponent;
//				} 
//				catch (IOException e) {
//					e.printStackTrace();
//				}

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
		sv = new ShipView(user, opponent, this);
		sv.setVisible(true);
	}
	
	public void getFinalShipLocations(int[] positions) {
		this.positions = positions; 
		try {
			toServerObj.writeObject(positions);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		Thread t = new Thread(new PlayGame(sv.getGCC()));
		t.start();
		
	}
	
	public void setCurrentShot(int cs) {
		this.currentShot = cs;
		System.out.println("cs is now " + cs);
	}
	
	class PlayGame implements Runnable {
		
		GameControllerClient gcc;
//		int currentShot;
		

		
		public PlayGame(GameControllerClient gcc) {
			this.gcc = gcc;
//			currentShot = -1;
		}
		
			String message = null;
			
			public void run() {
				while(true) {
					try {
						message = fromServer.readUTF();
						System.out.println("first message of the game: " + message);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					
					if (message.equals("YOUR TURN")) {
						gcc.takeTurn();
						while(currentShot == -1) {
							currentShot = gcc.checkCurrentShot();
							try {
							Thread.sleep(1000);
							}
							catch (InterruptedException e) {
								e.printStackTrace();
							}
						} 
						try {
							System.out.println("sending shot" );
							toServer.writeInt(currentShot);
							System.out.println("watiting here" );
							Boolean result = fromServer.readBoolean();
							System.out.println("the result of this shot is: " + result);
							gcc.updateButton(result);
							currentShot = -1;
							message = null;
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					} else if (message.equals("HIT MADE")) {
						try {
							int hitMade = fromServer.readInt();
							System.out.println("i was hit :( at pos " + hitMade);
							message = null;
							currentShot = -1;
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
	}
					


	public static void main(String[] args) {
		Client client = new Client();
	}
}


