package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import models.Board;
import models.Grid;
import models.User;
import shiplayout.ButtonGrid;
import shiplayout.Ship2;
import shiplayout.SnapGrid;

public class PlayGameClient {
	
	private User opponent;
	private User user;
	private SnapGrid sg;
	private ButtonGrid bg;
	private HashMap<String,ArrayList<Integer>> finalShipLocations;
	private JFrame frame;
	private Board myBoard;
	private Boolean turn;
	private Socket serverSocket;
	private static final String SAVED = "SAVED";
	private static final String LISTENING = "LISTENING";
	private static final String GRIDSEND = "GRIDSEND";
	private static final String LOST = "LOST"; //IF I receive this I won
	private static final String WIN = "WIN"; //IF I receive this I lost
	//private static final String START = "START";//start game
	private Boolean gameOver;
	//private static final String TURN = "TURN"; //give or take turn
	private int positionOnThisTurn;
	private int DELAY = 50;
	
	public PlayGameClient(User user, User opponent,int gameID,Boolean turn,Socket serverSocket) {
		this.opponent = opponent;
		this.user = user;
		this.serverSocket = serverSocket;
		this.turn = turn; //if starts as true you will take the first turn
		myBoard = new Board(gameID,user.getUsername(),opponent.getUsername());
		gameOver = false;
		positionOnThisTurn = -1;
			
		shipsView();
		
	}
	
	private void shipsView() {
		frame = new JFrame((user.getUsername() + " vs." + opponent.getUsername()));
		sg = new SnapGrid();
		sg.addSaveButtonListener(new SaveButtonListener(sg,this));
		frame.setLayout(new BorderLayout());
		frame.add(sg,BorderLayout.CENTER); 
		frame.setSize(new Dimension(1000,1000));
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void setFinalShipLocations(HashMap<String,ArrayList<Integer>> finalShipLocations) {
		this.finalShipLocations = finalShipLocations;
		System.out.println("FINAL: "  +finalShipLocations);
		addButtonGrid();
		myBoard.setFinalShipLocations(finalShipLocations);
		//send finalShipLocations over server to opponent
		Thread t = new Thread(new setGridHandler());
		t.start();
		
	}
	
	class setGridHandler implements Runnable{

		@Override
		public void run() {
			try {
				DataOutputStream primToServer = new DataOutputStream(serverSocket.getOutputStream());
				DataInputStream primFromServer = new DataInputStream(serverSocket.getInputStream());
				ObjectOutputStream objectToServer = new ObjectOutputStream(serverSocket.getOutputStream());
				ObjectInputStream objectFromServer = new ObjectInputStream(serverSocket.getInputStream());
				primToServer.writeUTF(SAVED);
				
				Boolean sent = false;
				Boolean received = false;
				
				while(true) {
					
					String message = primFromServer.readUTF();
					
					if(message.equals(LISTENING)) {
						objectToServer.writeObject(finalShipLocations);
						objectToServer.flush();
						sent = true;
					}
					else if(message.equals(GRIDSEND)){
						HashMap<String, ArrayList<Integer>> opponentEntries = (HashMap<String, ArrayList<Integer>>) objectFromServer.readObject();
						if(opponentEntries != null) {
							received = true;
							myBoard.initializeOpponentGrid(opponentEntries);
						}
					}
					else if(!sent){
						primToServer.writeUTF(SAVED);
					}
					else if(sent & received) {
						
						break;
					}	
				}
				
				startGamePlay();
				objectToServer.close();
				objectFromServer.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				//IF the object received cannot be cast to HashMap<String,<ArrayList<Integer>>
				e.printStackTrace();
			}
		}
	}
	
	public void startGamePlay() {
		Thread t = new Thread(new GamePlayHandler());
		t.start();
	}
	
	class GamePlayHandler implements Runnable{

		@Override
		public void run() {
			try {
				DataOutputStream primToServer = new DataOutputStream(serverSocket.getOutputStream());
				DataInputStream primFromServer = new DataInputStream(serverSocket.getInputStream());
				String message = primFromServer.readUTF();
				while(!gameOver) {
					
					if(message.equals(WIN)) {
						gameOver = true;
						continue;
					}
					else if(message.equals(LOST)) {
						gameOver = true;
						winGame();
						continue;
					}
					else if(turn) {
						takeTurn();
						while(true) {
							
							if(positionOnThisTurn > 0) { //idk how else to wait until the player hits a button
								primToServer.writeInt(positionOnThisTurn);
								primToServer.flush();
								if(message.equals(SAVED)) { //make the server acknowledge receipt of the guess
									turn = false;
									positionOnThisTurn = -1;
									break;
								}
							}else {
								try {
									Thread.sleep(DELAY);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							message = primFromServer.readUTF();
						}
					}
					else {
						while(true) {
							int opponentGuess = primFromServer.readInt();
							if(opponentGuess > 0) {
								int hit = myBoard.thisHitOrMiss(opponentGuess);
								if(hit == 1) {
									sg.addHit(opponentGuess);
								}
								turn = true;
								break;
							}
							else {
								try {
									Thread.sleep(DELAY);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						if(myBoard.checkPlayerGridScore()==17) {
							while(true) {
								primToServer.writeUTF(LOST); //I LOST
								primToServer.flush();
								message = primFromServer.readUTF();
								if(message.equals(WIN)) {
									break; // acknowledgement that the server knows the game is over
								}
							}
							gameOver = true;
						}else {
							primToServer.writeUTF(SAVED);
							primToServer.flush();
						}
						message = primFromServer.readUTF();
					}
				}
				primToServer.close();
				primFromServer.close();
				//endGame();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void endGame() {
		//something about telling the original client to put back up the list of players!
		frame.dispose();
	}
	public void winGame() {
		user.winGame();
		JDialog dialog = new JDialog();
		dialog.setTitle(user.getUsername() + ", you won!");
		dialog.setLayout(new FlowLayout());
		
		
		
		JLabel label = new JLabel("You won!");
		dialog.add(label);
		
		JButton okButton = new JButton("OK");
		okButton.setSize(80,50);
		okButton.addActionListener(e -> {dialog.dispose();endGame();});
		dialog.add(okButton);
		
		dialog.setSize(400,100);
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		
	}
	
	public void loseGame(){
		JDialog dialog = new JDialog();
		dialog.setTitle(user.getUsername() + ", you lost.");
		dialog.setLayout(new FlowLayout());
		
		JLabel label = new JLabel("You lost.");
		dialog.add(label);
			
		
		JButton okButton = new JButton("OK");
		okButton.setSize(80,50);
		okButton.addActionListener(e -> {dialog.dispose();endGame();});
		dialog.add(okButton);
		
		dialog.setSize(400,100);
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	
	}
	
	public void addButtonGrid() {
		bg = new ButtonGrid(sg);
		frame.remove(sg);
		frame.add(sg,BorderLayout.EAST);
		frame.add(bg,BorderLayout.WEST);
	
		for(int i = 1; i <= 100 ; i ++) {
			bg.addButtonListener(i,new GridButtonListener(bg,this));
		}
		bg.disableButtonGrid();
	}
	
	public void takeTurn() {
		bg.enableButtonGrid();	
	}
	
	public void giveTurn(JButton button, int position) {
		positionOnThisTurn = position;
		bg.disableButtonGrid();
		if(myBoard.opponentHitOrMiss(position)==1){
			button.setBackground(Color.RED);
		}
	}
	
	/*this class handles starting the game between two users who 
	 * were connected in ConnectUsers and facilitates the game between them 
	 * including starting the game, passing user input through board clicks back to the game 
	 * and sending the view a message when the game is over*/

	 
	public static void main(String args[]) throws UnknownHostException, IOException {
	//	PlayGameClient pgc = new PlayGameClient(new User(1,"Odette","pass123",0),new User(2,"Elizabeth","pass321",0),1,true);
	
	}
	
}