
import controller.LoginController;
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

public class Server extends JFrame implements Runnable {
	
	JTextArea ta;
	ArrayList<User> activeUsers;

    private int clientNo = 0;
	
	public Server() {
		super("Server");
		activeUsers = new ArrayList<User>();
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

		    
		    public void run() {
		      try {
		    	  
		        while (true) {
//		        	ta.append("this is a test");
//		        	ta.append(inputFromClient.readUTF());
		        	String username = inputFromClient.readUTF();
		        	String password = inputFromClient.readUTF();
		        	LoginController lc = new LoginController(username, password);
		        	boolean verify = lc.verifyInfo();
//		        	ta.append(String.valueOf(verify));
		        	outputToClient.writeBoolean(verify);
//		        	if (verify == true) {
//			        	int totalUsers = activeUsers.size();
//			        	outputToClient.writeInt(totalUsers);
//			        	for (User u: activeUsers) {
//			        		toClientObj.writeObject(u);
//			        	}
//			        	activeUsers.add(user);
//			        	ta.append(user.getUsername());
//			        	break;
//		        	}
		        }
		      }
		      catch(IOException ex) {
		    	  for (User u: activeUsers) {
		    		  if (u.equals(user)) {
		    			  activeUsers.remove(u);
		    		  }
		    	  }
		    	  
		        ex.printStackTrace();
		        ta.append("Connection lost with client " + this.clientNum + '\n');
		      }
		      
		      //blah blah blah code for getting connection info, checking with that user, and reporting back to the first user 
		      //maybe the server stores a hash map of socket + users; going through the list of active users it checks to find the one this user wants to connect with 
		      //and once it finds that one sends the message there 
		      
		      //once the game is started the server also needs to take these two users and start the game with them 
		    }
		  }
	
	public static void main(String[] args) {
		Server server = new Server();
	    server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    server.setVisible(true);
	}
}


