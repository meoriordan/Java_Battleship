package shiplayout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ShipServer extends JFrame implements Runnable{
	private static HashMap<String,Socket> users;
	private static HashMap<Socket,Socket> opponents;
	private JTextArea ta;
	private int clientNo = 0;
	//private ServerSocket serverSocket;
	private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
	private final static String GAMEREQUEST = "GAMEREQUEST";
	private final static String ACCEPT = "Accept";
	private final static String DENY = "Deny";
	private final static String REQUESTED = "Requested";
	private final static String GAMESTART = "GameStart";
	

	
	public ShipServer() {
		
		super("Ship Server");
		
//		try {
//			this.serverSocket = new ServerSocket(port);
//		}catch(IOException e) {
//			e.printStackTrace();
//		}
//		
		users = new HashMap<String,Socket>(); //username and socket
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
				executorService.submit(()->{
					try {
						handleClientConnect(clientSocket);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}
		}catch(IOException ioe) {
				ioe.printStackTrace();
		}
		
	}
	
	private void handleClientConnect(Socket clientSocket) throws InterruptedException {
		String username = null;
		try {
			DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
			
			username = dataIn.readUTF();
			users.put(username, clientSocket);
			postUserList();
			
			while(true) {
				String message = dataIn.readUTF();
//				if(message.equals("getUsers")){//change equals
//					postUserList(dataOut);
//				}
//				else 
				if(message.equals(REQUESTED)) {
					wait();
				}
				
				if(message.equals(GAMEREQUEST)) {
					//startGame and end this thread
					String opponentName = dataIn.readUTF();
					if(users.containsKey(opponentName)) {
						Socket opponentSocket = users.get(opponentName);
						DataInputStream inOpponent = new DataInputStream(opponentSocket.getInputStream());
						DataOutputStream outOpponent = new DataOutputStream(opponentSocket.getOutputStream());
						outOpponent.writeUTF(GAMEREQUEST);
						outOpponent.flush();
						ta.append("CHECK POINT 1\n");
						outOpponent.writeUTF(username);
						ta.append("CHECK POINT 2\n");
						removeClient(opponentName);
						ta.append("CHECK POINT 3\n");
						removeClient(username);
						ta.append("CHECK POINT 4\n");
						while(true) {
							ta.append("CHECK POINT 5\n");
							String response = inOpponent.readUTF();
							ta.append("CHECK POINT 6\n");
							ta.append("RESPONSE " +response+"\n");
							if(response.equals(ACCEPT)) {
								dataOut.writeUTF(response);
								dataOut.flush();
								ta.append("Starting game between "+username+" and "+opponentName+"\n");
								opponents.put(clientSocket, opponentSocket);
								opponents.put(opponentSocket,clientSocket);
								notify();
								break;
								
							}else if(response.equals(DENY)) {
								dataOut.writeUTF(response);
								dataOut.flush();
								users.put(opponentName, opponentSocket);
								users.put(username,clientSocket);
								notify();
								break;
							}
						}
//						users.put(opponentName, opponentSocket);
//						users.put(username,clientSocket);
						postUserList();	
					}
				}
			}
		}catch(IOException ioe) {
			ioe.printStackTrace();
			removeClient(username);
			postUserList();
		}
	}
	
	private void postUserList() {
		for(HashMap.Entry<String,Socket> entry : users.entrySet()) {
			try {
				DataOutputStream out = new DataOutputStream(entry.getValue().getOutputStream());
				postUserList(out);
			}catch(IOException ioe) {
				removeClient(entry.getKey());
			}
		}
	}
	
	private void removeClient(String user) {
		users.remove(user);
		postUserList();
	}
	
	private void postUserList(DataOutputStream out )throws IOException {
		String userList = new String("");
		for(String username : users.keySet()) {
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
