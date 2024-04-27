
import controller.LoginController;
import controller.RegisterController;
import models.User;

import java.awt.BorderLayout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.swing.*;


/*a single client can: 
 * register
 * login 
 * attempt to connect with another user 
 * be contacted by another user to play
 * play a game with another user
 */

public class Server extends JFrame implements Runnable {
	
	JTextArea ta;
	ArrayList<User> activeUsers;
	ArrayList<Socket> activeSockets;
    static ArrayList<HandleAClient> ch = new ArrayList<>();

    private int clientNo = 0;
	
	public Server() {
		super("Server");
		activeUsers = new ArrayList<User>();
		activeSockets = new ArrayList<Socket>();
		ta = new JTextArea();
		this.add(ta);

	    setSize(400, 200);
	    Thread t = new Thread(this);
	    t.start();
	}
	
	public void run() {
	      try {
	          ServerSocket serverSocket = new ServerSocket(9898);
	 
	          while (true) {
	          	  
	            ta.append("Waiting for incoming connection....\n"); 
	  	        Socket socket = serverSocket.accept();
	  	        clientNo++;
	            ta.append("Starting thread for client " + clientNo +
	                    " at " + new Date() + '\n');

	            InetAddress inetAddress = socket.getInetAddress();
	            ta.append("Client " + clientNo + "'s host name is "
                    + inetAddress.getHostName() + "\n");
	            ta.append("Client " + clientNo + "'s IP Address is "
                    + inetAddress.getHostAddress() + "\n");
	  	        
	            HandleAClient hac = new HandleAClient(socket, clientNo);
	            Thread t = new Thread(hac);
	            t.start();	  	        
	            }      
	        }
	        catch(IOException ex) {
	          ex.printStackTrace();
	        } 
	}
	
	  class HandleAClient implements Runnable {
		    private Socket socket; 
		    private int clientNum;
		    
		    private DataInputStream inputFromClient;
		    private DataOutputStream outputToClient;
		    
			private ObjectOutputStream toClientObj = null;
			private ObjectInputStream fromClientObj = null;
			
		    private User user;
		    
		    public HandleAClient(Socket socket, int clientNum) {
		    	
		      this.socket = socket;
		      this.clientNum = clientNum;
		      
		      try {
		    	  this.inputFromClient = new DataInputStream(this.socket.getInputStream());
		    	  this.outputToClient = new DataOutputStream(this.socket.getOutputStream());
		    	  this.toClientObj = new ObjectOutputStream(this.socket.getOutputStream());
		    	  this.fromClientObj = new ObjectInputStream(this.socket.getInputStream());
		      }
		      catch(IOException ex) {
			        ex.printStackTrace();
			        ta.append("Connection lost with client " + this.clientNum + '\n');
			      }
		    }
		    
		
		    public void updateActiveUsers() {
		    	try {
			    	for (HandleAClient h: Server.ch) {
//			    		ta.append("x" + Server.ch.size());
//						DataOutputStream out = new DataOutputStream(h.socket.getOutputStream());
						h.outputToClient.writeUTF("UPDATE USERS");
//			    		h.outputToClient.flush();
			    		ta.append(h.user.getUsername());
			    		h.outputToClient.writeInt(Server.ch.size());
			    		for (HandleAClient h2: Server.ch) {
			    			h.toClientObj.writeObject(h2.user);
			    		}
//			    		h.toClientObj.writeObject(h.user);
			    	}    	
		    	} 
		    	catch (IOException e) {
		    		e.printStackTrace();
		    	}

		    }
		    
		    public void attemptRegistration() {
		    	try {
		    		ta.append("registration occurring");
		    		String username = inputFromClient.readUTF();
		    		String password = inputFromClient.readUTF();
		    		RegisterController rv = new RegisterController(username, password);
		    		boolean creationSuccessful = rv.createUser();
		    		outputToClient.writeBoolean(creationSuccessful);
		    	}
		    	catch (IOException e ) {
		    		e.printStackTrace();
		    	}
		    }
		    
		    public void attemptLogin() {
		    	try {
		        	String username = inputFromClient.readUTF();
		        	String password = inputFromClient.readUTF();

		        	LoginController lc = new LoginController(username, password);
		        	boolean verify = lc.verifyInfo();
		        	outputToClient.writeBoolean(verify);
		        	
		        	if (verify == true) {   

		        		user = lc.retrieveUser();
		        		toClientObj.writeObject(user);
			        	Server.ch.add(this);
			        	updateActiveUsers();
			        	
		        	}
		    	}
		    	catch (IOException e) {
		    		e.printStackTrace();
		    	}

	        	

		    	
		    }
		    
		    public void attemptConnection() {
		    	try {
		    		Object o1 = fromClientObj.readObject();
		    		Object o2 = fromClientObj.readObject();
		    		User u1 = (User) o1;
		    		User u2 = (User) o2;
//		    		ta.append("got objects");
		    		String response = null;
		    		HandleAClient opponent = null;
		    		for (HandleAClient h: Server.ch) {
		    			if (h.user.getUsername().equals(u2.getUsername())) {
		    				ta.append("ATTEMPTING connection with" + u2.getUsername());
		    				h.outputToClient.writeUTF("ATTEMPTING CONNECTION");
		    				h.outputToClient.writeUTF(u1.getUsername());
		    				ta.append("waiting for response");
		    				response = h.inputFromClient.readUTF();
		    				ta.append("got response");


		    			}
		    		}
		    		ta.append("here");
//    				response = opponent.inputFromClient.readUTF();
    				
		    		ta.append("THE RESPONSE IS:  "+ response);
		    		
		    	} 
		    	catch (IOException e) {
		    		e.printStackTrace();	
		    	}
		    	catch (ClassNotFoundException e) {
		    		e.printStackTrace();
		    	}
		    }
		    
		    
		    public void run() {
		    	try {
		    		while (true) {
		    			String action = inputFromClient.readUTF();
		    			if (action.equals("REGISTER")) {
		    				attemptRegistration();
		    			}
		    			else if (action.equals("LOGIN")) {
		    				attemptLogin();
		    				ta.append("send");
//		    				outputToClient.writeUTF("testing123");
		    			} 
		    			else if (action.equals("CONNECT")) {
		    				ta.append("CONNECTING");
		    				attemptConnection();
		    				//connection function called
		    			} 
		    			
		    		}
		    	}
		    	catch (IOException e) {
		    		e.printStackTrace();
		    	}
		    }

		    
//		    public void run() {
//		      try {  
//		        while (true) {
//		        	String username = inputFromClient.readUTF();
//		        	String password = inputFromClient.readUTF();
//		        	LoginController lc = new LoginController(username, password);
//		        	boolean verify = lc.verifyInfo();
//		        	outputToClient.writeBoolean(verify);
//		        	
//		        	if (verify == true) {   
//		        		user = lc.retrieveUser();
//		        		toClientObj.writeObject(user);
//			        	Server.ch.add(this);
//			        	updateActiveUsers();
//			        	ta.append(user.getUsername());
//			        	break;
//		        	}
//		        }
//		        	
//	        	while (true) {
//	        		try {
//		        		Object o = fromClientObj.readObject();
//		        		User u = (User) o;
//		        		ta.append("THIS IS THE OPPONENT" + u.getUsername());
//		        		attemptConnection(this.user, u);
//	        		} 
//	        		catch (ClassNotFoundException e) {
//	        			e.printStackTrace();
//	        		}
//	        	}       
//		      }
//		      catch(IOException ex) {
//		    	  for (User u: activeUsers) {
//		    		  if (u.equals(user)) {
//		    			  activeUsers.remove(u);
//		    		  }
//		    	  }  
//		        ex.printStackTrace();
//		        ta.append("Connection lost with client " + this.clientNum + '\n');
//		      }
//		    }
		  }
	  

	public static void main(String[] args) {
		Server server = new Server();
	    server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    server.setVisible(true);
	}
}


