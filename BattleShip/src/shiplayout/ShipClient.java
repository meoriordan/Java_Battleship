package shiplayout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import models.User;
import views.HomepageView;
import views.LoginView;
import views.RegisterView;
import controller.PlayGameClient;

public class ShipClient {
	private DataOutputStream toServer = null;
	private DataInputStream fromServer = null;
	private Socket hostSocket =null;
	private static int DELAY = 20;
	private String username;
	private String opponent;
	private String password;
	private String[] options;
	private Thread userListHandle;
	private final static String ACCEPT = "Accept";
	private final static String ADDME = "ADDME";
	private final static String DENY = "Deny";
	private final static String FALSE = "FALSE";
	private final static String GAMEREQUEST = "GAMEREQUEST";
	private final static String GAMESTART = "GameStart";
	private final static String LOGIN = "Login";
	private final static String REGISTER = "REGISTER";
	private final static String REQUESTED = "Requested";
	private final static String SAVED = "SAVED";
	private final static String TRUE = "TRUE";
	private final static ArrayList<String> INVALID_USERNAMES = new ArrayList<String>(Arrays.asList(ACCEPT,ADDME,DENY,FALSE,GAMESTART,LOGIN,REGISTER,REQUESTED,SAVED,TRUE));
	private String userList;
	private User user;
	private LoginView lv;
	private HomepageView hv;
	private RegisterView rv;
	private ObjectInputStream fromServerObj = null;
	private ObjectOutputStream toServerObj = null;
	private Boolean turn;
	private ReentrantLock lock = new ReentrantLock();
	private Boolean gamePlay;	
	
	public ShipClient() {
		lv = new LoginView(this);
		options = new String[] {ACCEPT,DENY};
		turn = false;
		gamePlay = false;
	}
	
	public void gameStart() {
		try {
			toServer.writeUTF(GAMESTART);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Pair<DataOutputStream,DataInputStream> serverDataStreams = new Pair<DataOutputStream,DataInputStream>(toServer,fromServer);
		Pair<ObjectOutputStream,ObjectInputStream> serverObjStreams = new Pair<ObjectOutputStream,ObjectInputStream>(toServerObj,fromServerObj);
		PlayGameClient pgc = new PlayGameClient(user,opponent,0,turn, serverDataStreams,serverObjStreams,this);
		hv.setVisible(false);
	}
	
	public void renewHomepage() {
		hv.setVisible(true);
		gamePlay = false;
		userListHandle = new Thread(new UserListener());
		userListHandle.start();
		turn = false;
	}
	
	public void verifyLogin() {
		
		username = lv.getUserName();
		password = lv.getPassword();
		try {
			if(hostSocket == null) {
				hostSocket = new Socket("localhost",9898);
			}
			if(toServer == null) {
				toServer = new DataOutputStream(hostSocket.getOutputStream());
			}
			if(fromServer == null) {
				fromServer = new DataInputStream(hostSocket.getInputStream());
			}
			
			toServer.writeUTF(username);
			toServer.writeUTF(password);
			toServer.flush();
			String verify = fromServer.readUTF();
			
			if (verify.equals(TRUE)) {
				if(toServerObj == null) {
					toServerObj = new ObjectOutputStream(hostSocket.getOutputStream());
				}
				if(fromServerObj == null) {
					fromServerObj = new ObjectInputStream(hostSocket.getInputStream());
				}
				Object o = fromServerObj.readObject();
				user = (User) o;
				lv.setVisible(false);
				hv = new HomepageView(user, this);
				hv.setVisible(true);	
				userListHandle = new Thread(new UserListener());
				userListHandle.start();	
			}
			else {
				//FAILED LOGIN NOTIF????
			}		
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	class UserListener implements Runnable{

		@Override
		public void run() {
			try {
				hostSocket.setSoTimeout(5000);
				while(true) {
					if(gamePlay) {
						break;
					}
					Boolean gotLock = false;
					while(!gotLock) {
						if(!lock.isLocked()) {
							lock.lock();
							gotLock = true;
						}
					}
					try {
						String message = fromServer.readUTF();
						if(message.equals(GAMEREQUEST)) {
							toServer.writeUTF(REQUESTED);
							String opponentName = fromServer.readUTF();
							int n = JOptionPane.showOptionDialog(null,
								    ("New game request from "+opponentName+". Accept?"),
								    ("GAME REQUEST TO " + username),
								    JOptionPane.YES_NO_OPTION,
								    JOptionPane.QUESTION_MESSAGE,
								    null,     //do not use a custom Icon
								    options,  //the titles of buttons
								    options[0]); //default button title
							String response = options[n];
							toServer.writeUTF(response);
							toServer.flush();
							if(response.equals(ACCEPT)) {
								opponent = opponentName; 
								turn = true;
								gamePlay = true;
								gameStart();
								break;
							}
							else {
								refreshUserList();
							}
						}
						else{
							if(!INVALID_USERNAMES.contains(message)) {
								userList  = message;
							}
							refreshUserList();	
						}
					}catch(SocketTimeoutException sto) {
					}finally {
						lock.unlock();
						Thread.sleep(2000);
					}		
					
					}
			}catch(IOException | InterruptedException ioe) {
				ioe.printStackTrace();
			}
		}
		
		private void refreshUserList() {
			SwingUtilities.invokeLater(()->{
				hv.updateUserList(userList);
			});
		}
	}
	
	public void chooseOpponent(String opponentName){
		Boolean gotLock = false;
		while(!gotLock) {
			if(!lock.isLocked()) {
				lock.lock();
				gotLock = true;
			}
		}
		try {
			toServer.writeUTF(GAMEREQUEST);
			toServer.flush();
			toServer.writeUTF(opponentName);
			while(true) {
				try{
					String response = fromServer.readUTF();
					if(response.equals(ACCEPT)) {
						opponent = opponentName;
						gamePlay = true;
						gameStart();
						break;
					}else if(response.equals(DENY)){
						//ADD A DIALOG BOX TO SHOW THAT IT WAS DENIED
						break;
					}
				}catch(SocketTimeoutException ste) {
					try {
						Thread.sleep(5000);
						toServer.writeUTF(GAMEREQUEST);
						toServer.flush();
						toServer.writeUTF(opponentName);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}finally {
			lock.unlock();
		}
	}
	
	public void switchToRegistration() {
		lv.setVisible(false);
		rv = new RegisterView(this);
		rv.setVisible(true);
	}
	
	public void attemptRegistration(String username, String password) {;
		try {
			if(hostSocket == null) {
				hostSocket = new Socket("localhost",9898);
			}
			if(toServer == null) {
				toServer = new DataOutputStream(hostSocket.getOutputStream());
			}
			if(fromServer == null) {
				fromServer = new DataInputStream(hostSocket.getInputStream());
			}
			
			toServer.writeUTF(REGISTER);
			toServer.writeUTF(username);
			toServer.writeUTF(password);
			toServer.flush();
			String success;
			success = fromServer.readUTF();
			if(success.equals(TRUE)) {
				lv.setVisible(true);
				rv.setVisible(false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(()-> new ShipClient()/*.setVisible(true)*/);
	}
}