
import controller.LoginController;
import controller.RegisterController;
import controller.GameController;
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
						h.outputToClient.writeUTF("UPDATE USERS");
			    		h.outputToClient.writeInt(Server.ch.size());
			    		for (HandleAClient h2: Server.ch) {
			    			h.toClientObj.writeObject(h2.user);
			    		}
			    	}    	
		    	} 
		    	catch (IOException e) {
		    		e.printStackTrace();
		    	}

		    }
		    
		    public void attemptRegistration() {
		    	try {
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
		    
//		    public void attemptConnection() {
//		    	try {
//		    		Object o1 = fromClientObj.readObject();
//		    		Object o2 = fromClientObj.readObject();
//		    		User u1 = (User) o1;
//		    		User u2 = (User) o2;
//		    		
//		    		String response;
//		    		HandleAClient opponent = null;
//		    		for (HandleAClient h: Server.ch) {
//		    			if (h.user.getUsername().equals(u2.getUsername())) {
//		    				opponent = h;
//		    			}
//		    		}
//		    		
//    				ta.append("ATTEMPTING connection with" + u2.getUsername());
//    				opponent.outputToClient.writeUTF("ATTEMPTING CONNECTION");
//    				opponent.outputToClient.writeUTF(u1.getUsername());
//    				ta.append("\nresponse is coming from " + opponent.user.getUsername());
////    				ta.append("waiting for response1");
////    				ta.append("waiting for response2");
////    				ta.append("waiting for response3");
//    				ta.append("waiting waiting  response");
//    				opponent.outputToClient.writeUTF("OK TESTING 1234");
//    				ta.append(opponent.inputFromClient.readUTF());
////    				response = opponent.inputFromClient.readUTF();
//    				
//    				ta.append("got response");
//    				
////		    		ta.append("THE RESPONSE IS:  "+ response);
//		    		
//		    	} 
//		    	catch (IOException e) {
//		    		e.printStackTrace();	
//		    	}
//		    	catch (ClassNotFoundException e) {
//		    		e.printStackTrace();
//		    	}
//		    }
		    
		    
		    public void run() {
		    	try {
		    		while (true) {
		    			
		    			String action = inputFromClient.readUTF();
		    			ta.append("heres my action: " + action);
		    			if (action.equals("REGISTER")) {
		    				attemptRegistration();
		    			}
		    			else if (action.equals("LOGIN")) {
		    				attemptLogin();
		    			} 
		    			else if (action.equals("CONNECT")) { 
		    				try {
			    				ta.append("\nCONNECTING");
					    		Object o1 = fromClientObj.readObject();
					    		String opponent = inputFromClient.readUTF();
					    		User u1 = (User) o1;
//					    		User u2 = (User) o2; 
//					    		
					    		String response;
					    		Socket opponentSocket = null;
					    		DataOutputStream opponentOutput = null;
					    		DataInputStream opponentInput = null;
					    		for (HandleAClient h: Server.ch) {
					    			if (h.user.getUsername().equals(opponent)) {
							    		ta.append("GOT THE OPPONENT \n");
					    				opponentSocket = h.socket;
					    				opponentOutput = h.outputToClient;
					    				opponentInput  = h.inputFromClient;		
					    			}
					    		}
					    		
			    				opponentOutput.writeUTF("ATTEMPTING CONNECTION");
			    				opponentOutput.writeUTF(u1.getUsername());
			    				response = opponentInput.readUTF();
			    				ta.append("RESPONSE: " + response);
			    				
			    				if (response.equals("accept")) {
//			    					PlayAGame play = new PlayAGame(this.socket, this.user, opponent.socket, opponent.user);
//			    					Thread t2 = new Thread(play);
//			    					t2.start();

//				    				try {
//					    				Thread.sleep(10000);
//				    				}
//				    				catch (InterruptedException e) {
//				    					e.printStackTrace();
//				    				}
//			    					for 
				    				ta.append(inputFromClient.readUTF());
				    				
			    					for (int i = 0; i < 100; i++) {
				    					outputToClient.writeUTF(response);
				    					outputToClient.flush();
			    					}
//			    					ta.append("sent");
			    				}
		    				} 
		    				catch (IOException e) {
		    					e.printStackTrace();
		    				}
		    				catch (ClassNotFoundException e) {
		    					e.printStackTrace();
		    				}

		    			} else if (action.equals("considering request")) {
		    				try {
			    				Thread.sleep(30000);
		    				}
		    				catch (InterruptedException e) {
		    					e.printStackTrace();
		    				}
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
	  
	  class PlayAGame implements Runnable {
		  
		  private DataInputStream inputFromClient1;
		  private DataOutputStream outputToClient1;
		  private DataInputStream inputFromClient2;
		  private DataOutputStream outputToClient2;
		  
		  private Socket socket1;
		  private Socket socket2;
		  
		  private User user1;
		  private User user2;
		  
		  private GameController gc;
		  
		  public PlayAGame(Socket s1, User u1, Socket s2, User u2) {
			  try {
		    	  this.inputFromClient1 = new DataInputStream(s1.getInputStream());
		    	  this.outputToClient1 = new DataOutputStream(s1.getOutputStream());  
		    	  this.inputFromClient1 = new DataInputStream(s2.getInputStream());
		    	  this.outputToClient1 = new DataOutputStream(s2.getOutputStream());	  
			  } 
			  catch (IOException e) {
				  e.printStackTrace();
			  }
			  
			  this.socket1 = s1;
			  this.socket2 = s2;
			  
			  this.user1 = u1;
			  this.user2 = u2;
			  
			  gc = new GameController(user1, user2);

		  }
		  
		  public void run() {
			  //initialize boards 
			  
			  //play game 
			  
			  
			  
		  }
		  
		  
	  }
	  
	  
	  

	public static void main(String[] args) {
		Server server = new Server();
	    server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    server.setVisible(true);
	}
}


