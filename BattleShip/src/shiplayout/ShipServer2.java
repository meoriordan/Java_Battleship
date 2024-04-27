package shiplayout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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

public class ShipServer2 extends JFrame implements Runnable{
	private static Map<String,Socket> synchUsers;
	private static HashMap<Socket,Socket> opponents;
	private JTextArea ta;
	private int clientNo = 0;
	//private ServerSocket serverSocket;
	//private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
	private final static String GAMEREQUEST = "GAMEREQUEST";
	private final static String ACCEPT = "Accept";
	private final static String DENY = "Deny";
	private final static String REQUESTED = "Requested";
	private final static String GAMESTART = "GameStart";
	private final static ArrayList<String> INVALID_USERNAMES = new ArrayList<String>(Arrays.asList(ACCEPT,DENY,REQUESTED,GAMESTART,GAMEREQUEST));
	private final static int DELAY = 20;
	

	
	public ShipServer2() {
		
		super("Ship Server");//		
		HashMap<String,Socket> users = new HashMap<String,Socket>(); //username and socket
		synchUsers = Collections.synchronizedMap(users);
		ta = new JTextArea(10,10);
		JScrollPane sp = new JScrollPane(ta);
		this.add(sp);
		this.setTitle("MultiThreaded BattleShip Server");
		this.setSize(400,200);
		Thread t = new Thread(this);
		t.start();
		this.opponents = new HashMap<Socket,Socket>();
	}
	
	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(9898);
			ta.append("MultiThreaded BattleShip Server started at "+ new Date() + "\n");
			while(true) {
				Socket clientSocket = serverSocket.accept();
				clientNo++;
				ta.append("Starting thread for client " + clientNo+" at " + new Date() + "\n");
				InetAddress inetAddress= clientSocket.getInetAddress();
				ta.append("Client " + clientNo + "'s host name is " +inetAddress.getHostName()+"\n");
				ta.append("Client "+ clientNo + "'s IP Address is "+inetAddress.getHostAddress()  + "\n");
				new Thread(new HandleClientConnect(clientSocket,clientNo)).start();
			}
		}catch(IOException ioe) {
				ioe.printStackTrace();
		}
		
	}
	
	class HandleClientConnect implements Runnable {
		
		private String username = null;
		private Socket socket;
		private int clientNum; 
		DataOutputStream outputToThisClient;
		DataInputStream inputFromThisClient;
		
		public HandleClientConnect(Socket socket, int clientNum) {
			this.socket = socket;
			this.clientNum = clientNum;
			
			try {
				outputToThisClient = new DataOutputStream(socket.getOutputStream());
				inputFromThisClient = new DataInputStream(socket.getInputStream());
				while(true) {
					Thread.sleep(DELAY);
					username = inputFromThisClient.readUTF();
					if(username != null) {
						synchUsers.put(username, socket);
						postUserList();
						break;
					}
				}
			}catch(IOException | InterruptedException ioe) {
				ioe.printStackTrace();
			}
			
		}
		
		@Override
		public void run() {
			try {
				
				while(true) {
					String message = inputFromThisClient.readUTF();
					if(!INVALID_USERNAMES.contains(message)) {
						postUserList();
					}
					else if(message.equals(REQUESTED)) {
						ta.append(username + " received game request.. waiting..\n");
						wait();
					}
					else if(message.equals(GAMEREQUEST)) {
						String opponentName = inputFromThisClient.readUTF();
						ta.append(username + " requested game with " + opponentName+" waiting on response..");
						if(synchUsers.containsKey(opponentName)) {
							Socket opponentSocket = synchUsers.get(opponentName);
							DataInputStream fromOpponent = new DataInputStream(opponentSocket.getInputStream());
							DataOutputStream toOpponent = new DataOutputStream(opponentSocket.getOutputStream());
							toOpponent.writeUTF(GAMEREQUEST);
							ta.append("CHECK POINT 1\n");
							toOpponent.writeUTF(username);
							ta.append("CHECK POINT 2\n");
							removeClient(opponentName);
							ta.append("CHECK POINT 3\n");
							removeClient(username);
							ta.append("CHECK POINT 4\n");
							while(true) {
								ta.append("CHECK POINT 5\n");
								String response = fromOpponent.readUTF();
								ta.append("CHECK POINT 6\n");
								ta.append("RESPONSE " +response+"\n");
								if(response.equals(ACCEPT)) {
									outputToThisClient.writeUTF(response);
									outputToThisClient.flush();
									ta.append("Starting game between "+username+" and "+opponentName+"\n");
									opponents.put(socket, opponentSocket);
									opponents.put(opponentSocket,socket);
									notify();
									break;									
								}else if(response.equals(DENY)) {
									outputToThisClient.writeUTF(response);
									outputToThisClient.flush();
									synchUsers.put(opponentName, opponentSocket);
									synchUsers.put(username,socket);
									notify();
									postUserList();
									break;
								}
							}
						}
					}
				}
			}catch(IOException ioe){
				ta.append("IOEXCEPTION FROM THREAD: "+username+"\n");
				ioe.printStackTrace();
				removeClient(username);
				
			}catch(InterruptedException ie) {
				ta.append(username + " thread interrupted.. am i waiting?");
			}
			//catch(Exception e) {
//				ta.append("EXCEPTION FROM THREAD: "+username+"\n");
//				removeClient(username);
//	//			postUserList();
//				e.printStackTrace();
//			}
		}
	}
	
	private void postUserList() {
		for(HashMap.Entry<String,Socket> entry : synchUsers.entrySet()) {
			try {
				DataOutputStream out = new DataOutputStream(entry.getValue().getOutputStream());
				postUserList(out);
			}catch(IOException ioe) {
				removeClient(entry.getKey());
			}
		}
	}
	
	private void removeClient(String user) {
		synchUsers.remove(user);
		postUserList();
	}
	
	private void postUserList(DataOutputStream out )throws IOException {
		String userList = new String("");
		for(String username : synchUsers.keySet()) {
			userList += username + ",";
		}
		if(userList.length()>0 && userList.charAt(userList.length()-1) == ','){
			userList = userList.substring(0, userList.length()-1);
		}
		out.writeUTF(userList.toString());
		out.flush();
	}
	
	public static void main(String[] args) {
		ShipServer2 server = new ShipServer2();
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server.setVisible(true);
	}

}
