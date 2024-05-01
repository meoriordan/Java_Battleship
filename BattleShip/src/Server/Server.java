package Server;

import controller.LoginController;
import controller.RegisterController;
import controller.GameController;
import models.User;
import models.Game;

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
		        		this.user = lc.retrieveUser();
		        		toClientObj.writeObject(user);
			        	Server.ch.add(this);
			        	updateActiveUsers();	
		        	}
		    	}
		    	catch (IOException e) {
		    		e.printStackTrace();
		    	}
		    }
		    
		    
		    public void takeAPause() {
		    	try {
			    	Thread.sleep(30000);
		    	}
		    	catch (InterruptedException e){
		    		e.printStackTrace();
		    	}
		    }
		    
		    
		    
		    public void run() {
				Boolean endThisThread = false;
		    	try {
		    		while (true) {
		    			if (endThisThread) {
		    				break;
		    			}
		    			if (user != null) {
			    			ta.append("waiting for an action: " + user.getUsername() + "\n");
		    			}
		    			
		    			String action = inputFromClient.readUTF();
		    			ta.append("heres my action: " + action);
		    			
		    			if (action.equals("REGISTER")) {
		    				attemptRegistration();
		    			}
		    			else if (action.equals("LOGIN")) {
		    				attemptLogin();
		    			} 
		    			else if (action.equals("STARTING GAME NOW")) {
		    				endThisThread = true;
		    			}
		    			else if (action.equals("CONNECT")) { 
		    				
		    				Object o1;
		    				String opponent;
		    				String response;
		    				try {
//			    				ta.append("\nCONNECTING");
			    				outputToClient.writeUTF("CONNECTION ACK");
					    		o1 = fromClientObj.readObject();
					    		User u1 = (User) o1;
					    		opponent = inputFromClient.readUTF();
//					    		ta.append("GOT THE OPPONENT INFO\n");
					    		
	
					    		
					    		Socket opponentSocket = null;
					    		DataOutputStream opponentOutput = null;
					    		DataInputStream opponentInput = null;
					    		ObjectOutputStream opponentObjOutput = null;
					    		ObjectInputStream opponentObjInput = null;
					    		User opponentUser = null;
					    		
					    		for (HandleAClient h: Server.ch) {
					    			if (h.user.getUsername().equals(opponent)) {
//							    		ta.append("GOT THE OPPONENT \n");
					    				opponentSocket = h.socket;
					    				opponentOutput = h.outputToClient;
					    				opponentInput  = h.inputFromClient;	
					    				opponentObjOutput = h.toClientObj;
					    				opponentObjInput = h.fromClientObj;
					    				opponentUser = h.user;
//					    				h.takeAPause();
					    			}
					    		}
			    				opponentOutput.writeUTF("ATTEMPTING CONNECTION");
			    				opponentOutput.writeUTF(u1.getUsername());
			    				outputToClient.writeUTF("accept");
		    					PlayAGame play = new PlayAGame(this.socket, this.user, opponentSocket, opponentUser, this.inputFromClient,this.outputToClient, opponentInput, opponentOutput,this.fromClientObj, this.toClientObj, opponentObjInput, opponentObjOutput);
		    					Thread t2 = new Thread(play);
		    					t2.start();
		    					endThisThread = true;
//			    				response = opponentInput.readUTF();
//			    				System.out.println(response);
//			    				ta.append("RESPONSE: " + response);
					    							    		
		    				}
		    				catch (IOException e) {
		    					e.printStackTrace();
		    				}
		    				catch (ClassNotFoundException e) {
		    					e.printStackTrace();
		    				}
		    			}
		    		}
		    	} catch (IOException e) {
			    		e.printStackTrace();
			    	}
			    	System.out.println("bye bye thread");
//			    				outputToClient.writeUTF("GOT CONNECTON REQUEST");

//					    		

//					    		
//			    				opponentOutput.writeUTF("ATTEMPTING CONNECTION");
//			    				opponentOutput.writeUTF(u1.getUsername());
//			    				response = opponentInput.readUTF();
//			    				ta.append("RESPONSE: " + response);
//			    				
//			    				for (int i = 0; i < 100; i++) {
//			    					outputToClient.writeUTF(response);
//			    					outputToClient.flush();	
//			    				}
//
//			    				
//			    				if (response.equals("accept")) {
//
//			    					ta.append("\nplaying!");

//			    				}
//		    				} 


//		    			} else if (action.equals("considering request")) {
//		    				try {
//			    				Thread.sleep(30000);
//			    				endThisThread = true;
//		    				}
//		    				catch (InterruptedException e) {
//		    					e.printStackTrace();
//		    				}
//		    			}	
//		    		}
//		    	}


		    }
	  
	  class PlayAGame implements Runnable {
		  
		  private DataInputStream inputFromClient1;
		  private DataOutputStream outputToClient1;
		  private DataInputStream inputFromClient2;
		  private DataOutputStream outputToClient2;
		  
		  private ObjectInputStream inputObjFromClient1;
		  private ObjectOutputStream outputObjToClient1;
		  
		  private ObjectInputStream inputObjFromClient2;
		  private ObjectOutputStream outputObjToClient2;
		  
		  private Socket socket1;
		  private Socket socket2;
		  
		  private User user1;
		  private User user2;
		  
		  private GameController gc;
		  private Game g;
		  
		  int[] positions1;
		  int[] positions2;
		  
		  public PlayAGame(Socket s1, User u1, Socket s2, User u2, DataInputStream id1, DataOutputStream od1, DataInputStream id2, DataOutputStream od2, ObjectInputStream oi1, ObjectOutputStream oo1, ObjectInputStream oi2, ObjectOutputStream oo2) {
			  
			  System.out.println("RUNNING GAME"); 

			  this.socket1 = s1;
			  this.socket2 = s2;
			  
			  this.user1 = u1;
			  this.user2 = u2;
			  
			  inputFromClient1 = id1;
			  outputToClient1 = od1;
			  inputFromClient2 = id2;
			  outputToClient2 = od2;
			  
			  inputObjFromClient1 = oi1;
			  outputObjToClient1 = oo1;
			  inputObjFromClient2 = oi2;
			  outputObjToClient2 = oo2;
			  
			  gc = new GameController(user1, user2);
			  
		  }
		  
		  public void run() {
			  
			  try {
				  Object op1 = inputObjFromClient1.readObject();
				  Object op2 = inputObjFromClient2.readObject();

				  positions1 = (int[]) op1;
				  positions2 = (int[]) op2;
				  
				  ta.append("GOT MY BOARDS");
				  
			  }
			  catch (IOException e) {
				  e.printStackTrace();
			  } 
			  catch (ClassNotFoundException e1) {
				  e1.printStackTrace();
			  }
			  
			  gc.setBoards(positions1, positions2);
			  
			  int turn = 0;
			  
			  while(true) {
				  
				  ta.append("we're off!");
				  ta.append("current turn: " + turn);
				  if  (turn % 2 == 0) {
					  try {
						  outputToClient2.writeUTF("YOUR TURN");
						  outputToClient1.writeUTF("wait for now");
						  int shot = inputFromClient2.readInt();
						  ta.append(user2.getUsername() + " chose " + shot);
						  boolean shotMade = gc.takeTurn(user2,shot);
						  ta.append("writing to client now" );
						  outputToClient2.writeBoolean(shotMade);
						  if (shotMade) {
							  outputToClient1.writeUTF("HIT MADE");
							  outputToClient1.writeInt(shot);
						  }
					  }
					  catch (IOException e) {
						  e.printStackTrace();
					  }
					 
//					  turn++;
					  //send message to client 
					  //get value from client 2
					  //pass value to game controller 
					  //get hit or miss back from controller 
					  //send hit or miss back to player 
					  //send message to client 
				  } 
				  else {
					  try {
						  outputToClient1.writeUTF("YOUR TURN");
						  outputToClient2.writeUTF("wait for now");
						  int shot = inputFromClient1.readInt();
						  ta.append(user1.getUsername() + " chose " + shot);
						  boolean shotMade = gc.takeTurn(user1,shot);
						  ta.append("writing to client now" );
						  outputToClient1.writeBoolean(shotMade);
					  }
					  catch (IOException e) {
						  e.printStackTrace();
					  }
					  //send message to client 
					  //get value from client 2
					  //pass value to game controller 
					  //get hit or miss back from controller 
					  //send hit or miss back to player 
					  //send message to client 		  
				  }
				  turn+=1;
			  }
		  }

	  }
	  }
	  
	    
	  

	public static void main(String[] args) {
		Server server = new Server();
	    server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    server.setVisible(true);
	}
}


