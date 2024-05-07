package shiplayout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


import controller.LoginController;
import controller.RegisterController;
import models.User;

public class ShipServer extends JFrame implements Runnable{
	private static Map<HandleClientConnect, HandleClientConnect> opponents;
	private static Map<String,HandleClientConnect> threadMap;
	private JTextArea ta;
	private int clientNo = 0;
	private final static String ACCEPT = "Accept";//
	private final static String DENY = "Deny";//
	private final static String FALSE = "FALSE";//
	private final static String GAMEREQUEST = "GAMEREQUEST";//
	private final static String GAMESTART = "GameStart";//
	private final static String LOGIN = "Login";//
	private final static String REGISTER = "REGISTER";//
	private final static String REQUESTED = "Requested";//
	private final static String SAVED = "SAVED";//
	private final static String TRUE = "TRUE";//
	private ServerSocket serverSocket;
	private final static ArrayList<String> INVALID_USERNAMES = new ArrayList<String>(Arrays.asList(ACCEPT,DENY,LOGIN,REQUESTED,GAMESTART,GAMEREQUEST,REGISTER,SAVED,TRUE,FALSE));
	private final static int DELAY = 20;
	
	public ShipServer() {
		
		super("Ship Server");//		
		ta = new JTextArea(10,10);
		JScrollPane sp = new JScrollPane(ta);
		this.add(sp);
		this.setTitle("MultiThreaded BattleShip Server");
		this.setSize(400,200);
		Thread t = new Thread(this);
		t.start();
		HashMap<HandleClientConnect,HandleClientConnect> opps = new HashMap<HandleClientConnect,HandleClientConnect>();
		this.opponents = Collections.synchronizedMap(opps);
		HashMap<String,HandleClientConnect> threads = new HashMap<String,HandleClientConnect>();
		threadMap = Collections.synchronizedMap(threads);
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(9898);
			ta.append("MultiThreaded BattleShip Server started at "+ new Date() + "\n");
			while(true) {
				Socket clientSocket = serverSocket.accept();
				clientNo++;
				ta.append("Starting thread for client " + clientNo+" at " + new Date() + "\n");
				InetAddress inetAddress= clientSocket.getInetAddress();
				ta.append("Client " + clientNo + "'s host name is " +inetAddress.getHostName()+"\n");
				ta.append("Client "+ clientNo + "'s IP Address is "+inetAddress.getHostAddress()  + "\n");
				Thread thread = new Thread(new HandleClientConnect(clientSocket,clientNo));
				thread.start();
			}
		}catch(IOException ioe) {
				ioe.printStackTrace();
		}
	}
	
	class HandleClientConnect implements Runnable {
		
		private String username = null;
		private Socket clientSocket;
		DataOutputStream outputToThisClient;
		DataInputStream inputFromThisClient;
		ObjectOutputStream outputToClientObj;
		ObjectInputStream inputFromClientObj;
		HandleClientConnect opponentHCC;
		private User user;
		private Boolean gamePlay;
		private int clientNum;
		private PlayGameServer pgs = null;
		
		public HandleClientConnect(Socket clientSocket, int clientNum) {
			this.clientSocket = clientSocket;
			this.clientNum = clientNum;
			gamePlay = false;
			
			try {
				outputToThisClient = new DataOutputStream(clientSocket.getOutputStream());
				inputFromThisClient = new DataInputStream(clientSocket.getInputStream());
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
		public String getUsername() {
			return this.username;
		}
		
		public User getUser() {
			return user;
		}
		
		public DataOutputStream getDataOutput() {
			return outputToThisClient;
		}
		
		public DataInputStream getDataInput() {
			return inputFromThisClient;
		}
		
		public ObjectOutputStream getObjOutput() {
			return outputToClientObj;
		}
		
		public ObjectInputStream getObjInput() {
			return inputFromClientObj;
		}
		
		public void setGamePlay(Boolean gamePlay) {
			this.gamePlay = gamePlay;
		}

		public void gameStart(HandleClientConnect opponentHCC){
			this.opponentHCC = opponentHCC;
			opponentHCC.setGamePlay(true);
			gamePlay = true;
			try {
				PlayGameServer pgs = new PlayGameServer(this,opponentHCC);
				this.pgs = pgs;
				ta.append("GAME START IN SERVER: "+username+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void gameEnd() {
			gamePlay = false;
    		threadMap.put(username, this);
			opponentHCC.setGamePlay(false);
			threadMap.put(opponentHCC.getUsername(), opponentHCC);
			this.opponentHCC = null;
    		postUserList();
		}
		
		public void attemptLogin() {
			try {
	        	String username = inputFromThisClient.readUTF();
	        	String password = inputFromThisClient.readUTF();

	        	LoginController lc = new LoginController(username, password);
	        	boolean verify = lc.verifyInfo();
	        	String out = (verify ? TRUE : FALSE);
	        	outputToThisClient.writeUTF(out);
	        	outputToThisClient.flush();
	        	
	        	if (verify == true) {   
	        		user = lc.retrieveUser();
	        		outputToClientObj = new ObjectOutputStream(clientSocket.getOutputStream());
	        		inputFromClientObj = new ObjectInputStream(clientSocket.getInputStream());
	        		outputToClientObj.writeObject(user);
	        		outputToThisClient.flush();
	        		threadMap.put(username, this);
	        		this.username = username;	
		        	ArrayList<ArrayList<String>> pastGames = lc.retrieveGames();
		        	outputToClientObj.writeObject(pastGames);
		        	postUserList();
		        	lc = null;
	        	}
			}
	    	catch (IOException e) {
	    		e.printStackTrace();
	    	} 
	    }
		
		public void attemptRegistration() {
			try {
				String username = inputFromThisClient.readUTF();
				String password = inputFromThisClient.readUTF();
				RegisterController rv = new RegisterController(username,password);
				boolean creationSuccessful = rv.createUser();
				outputToThisClient.writeUTF(creationSuccessful ? TRUE : FALSE);
				outputToThisClient.flush();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void attemptGameRequest() {
			
			String opponentName;
			try {
				opponentName = inputFromThisClient.readUTF();
				ta.append(username + " requested game with " + opponentName+" waiting on response..\n");
				if(threadMap.containsKey(opponentName)) {
					HandleClientConnect opponentHCC = threadMap.get(opponentName);
					DataInputStream fromOpponent = opponentHCC.getDataInput();
					DataOutputStream toOpponent = opponentHCC.getDataOutput();	
					toOpponent.writeUTF(GAMEREQUEST);
					toOpponent.writeUTF(username);
					removeClient(opponentName);
					removeClient(username);
					while(true) {
						String response = fromOpponent.readUTF();
						if(response.equals(ACCEPT)) {
							outputToThisClient.writeUTF(response);
							outputToThisClient.flush();
							ta.append("Starting game between "+username+" and "+opponentName+"\n");
							opponents.put(this, opponentHCC);
							opponents.put(opponentHCC,this);
							gamePlay = true;
							gameStart(opponentHCC);
							break;									
						}else if(response.equals(DENY)) {
							outputToThisClient.writeUTF(response);
							outputToThisClient.flush();
							threadMap.put(opponentName, threadMap.get(opponentName));
							notify();
							postUserList();
							break;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
		@Override
		public void run() {
			try {
				clientSocket.setSoTimeout(30000);
				while(true) {
					if(!gamePlay) {
						try {
							String message = inputFromThisClient.readUTF();
						    if(message.equals(LOGIN)) {
								attemptLogin();								
							}
							else if(message.equals(REGISTER)) {
								attemptRegistration();
							}
							else if(message.equals(REQUESTED)) {
								ta.append(username + " received game request.. waiting..\n");
								wait();
							}
							else if(message.equals(GAMEREQUEST)) {
								attemptGameRequest();
							}
							else if(message.equals(GAMESTART)) {
								gamePlay = true;
							}
						}catch(SocketTimeoutException ste) {
						}
					}
				}
			}catch(SocketTimeoutException ste) {}
			catch(IOException ioe){
				ta.append("IOEXCEPTION FROM THREAD: "+username+"\n");
				ioe.printStackTrace();
				removeClient(username);
				
			}catch(InterruptedException ie) {
				ta.append(username + " thread interrupted..");
			}
		}
	}
	
	private void postUserList() {
		for(HashMap.Entry<String,HandleClientConnect> entry : threadMap.entrySet()) {
			try {
				DataOutputStream out = entry.getValue().getDataOutput();
				postUserList(out);
			}catch(IOException ioe) {
				removeClient(entry.getKey());
			}
		}
	}
	
	private void removeClient(String username) {
		threadMap.remove(username);
		postUserList();
	}
	
	private void postUserList(DataOutputStream out)throws IOException {
		String userList = new String("");
		for(String username : threadMap.keySet()) {
			userList += username + ",";
		}
		if(userList.length()>0 && userList.charAt(userList.length()-1) == ','){
			userList = userList.substring(0, userList.length()-1);
		}
		out.writeUTF(userList.toString());
		out.flush();
	}
	
	public static void main(String[] args) {
		ShipServer server = new ShipServer();
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server.setVisible(true);
	}

}
