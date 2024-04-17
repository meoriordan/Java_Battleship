package shiplayout;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.sql.*;

public class Server extends JFrame implements Runnable {	
	
	JTextArea ta;
	int clientNo = 0;
	ArrayList<User> activeUsers;
	
	public Server() {
		super("Chat Server");
		ta = new JTextArea();
		this.add(ta);
		
		activeUsers = new ArrayList<User>();

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
		    private ResultSet usersResult;
		    
		    public HandleAClient(Socket socket, int clientNum) {
		      this.socket = socket;
		      this.clientNum = clientNum;

		      
		      try {
		    	  this.inputFromClient = new DataInputStream(this.socket.getInputStream());
		    	  this.outputToClient = new DataOutputStream(this.socket.getOutputStream());
		      }
		      catch(IOException ex) {
			        ex.printStackTrace();
			        ta.append("Connection lost with client " + this.clientNum + '\n');
			      }
		    }

		    public void run() {
		    	String validUsername;
		    	String validPassword;
		    	int validUserID;
		    	int validUserPoints; 
		    	User user = null;
		      try {		
		    	  while (true) {
			    	  String username = inputFromClient.readUTF();
			    	  String password = inputFromClient.readUTF();
			    	  String message = "nope";
			    	  JavaSqlConn c = new JavaSqlConn();
					  usersResult = c.loginUser(username, password);
					  if (usersResult.next()) {
						  message = "yay";
						  validUsername = usersResult.getString("username");
						  validPassword = usersResult.getString("password");
						  validUserID = usersResult.getInt("user_id");
						  validUserPoints = usersResult.getInt("points");
						  user = new User(validUserID, validUsername, validPassword, validUserPoints);
						  activeUsers.add(user);
						  outputToClient.writeUTF(message);  
						  break;
					  } else {
						  outputToClient.writeUTF(message);  
					  }
		    	  }
		      } catch (Exception e) {
		    	  e.printStackTrace();
//		    	 probably need to remove user from active users list
		    	  ta.append("Connection lost with client " + this.clientNum + '\n');
		      }
		    }
		  }
	  
		public static void main(String[] args) {
			Server chatServer = new Server();
		    chatServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    chatServer.setVisible(true);
		}
	}
