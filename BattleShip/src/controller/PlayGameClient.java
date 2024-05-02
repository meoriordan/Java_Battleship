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
import java.net.SocketTimeoutException;
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
import shiplayout.Pair;
import shiplayout.Ship;
import shiplayout.ShipClient;
import shiplayout.SnapGrid;

public class PlayGameClient {
	
	private String opponent;
	private User user;
	private SnapGrid sg;
	private ButtonGrid bg;
	private HashMap<String,ArrayList<Integer>> finalShipLocations;
	private JFrame frame;
	private Board myBoard;
	private Boolean turn;
	private static final String SAVED = "SAVED";
	private static final String LISTENING = "LISTENING";
	private static final String GRIDSEND = "GRIDSEND";
	private static final String SENDGRID = "SENDGRID";
	private static final String GOTGRID = "GOTGRID";
	private Boolean gameOver;
	private int positionOnThisTurn;
	private int DELAY = 50;
	private DataOutputStream primToServer ;
	private DataInputStream primFromServer ;
	private ObjectOutputStream objectToServer; 
	private ObjectInputStream objectFromServer;
	private ShipClient sc;
	private static final int WIN = 1000;
	private static final int LOST = -1000;
	private static final int SAVE = 0;
	private Boolean ongoingTurn;
	
	public PlayGameClient(User user, String opponent,int gameID,Boolean turn,Pair<DataOutputStream,DataInputStream> dataStreams,Pair<ObjectOutputStream,ObjectInputStream> objStreams,ShipClient sc) {
		this.opponent = opponent;
		this.user = user;
		this.turn = turn;
		myBoard = new Board(gameID,user.getUsername(),opponent);
		gameOver = false;
		positionOnThisTurn = -1;
		this.sc = sc;
		
		primToServer = dataStreams.getA();
	    primFromServer = dataStreams.getB();
		objectToServer = objStreams.getA();
		objectFromServer = objStreams.getB();
			
		shipsView();	
	}
	
	private void shipsView() {
		frame = new JFrame((user.getUsername() + " vs." + opponent));
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
		addButtonGrid();
		myBoard.setFinalShipLocations(finalShipLocations);
		Thread t = new Thread(new setGridHandler());
		t.start();
	}
	
	class setGridHandler implements Runnable{

		@Override
		public void run() {
			try {
				primToServer.writeUTF(SAVED);
				primToServer.flush();
				
				Boolean sent = false;
				Boolean received = false;
				while(true) {
					try {
						String message = primFromServer.readUTF();
						if(sent && received) {
							break;
						}
						else if(message.equals(LISTENING)) {
							objectToServer.writeObject(finalShipLocations);
							objectToServer.flush();
						}
						else if(message.equals(GRIDSEND)){
							sent = true;
							HashMap<String, ArrayList<Integer>> opponentEntries = (HashMap<String, ArrayList<Integer>>) objectFromServer.readObject();
							if(opponentEntries != null) {
								received = true;
								myBoard.initializeOpponentGrid(opponentEntries);
								primToServer.writeUTF(GOTGRID);
								break;
							}
							
						}else if(message.equals(GOTGRID)) {	
							sent = true;
						}
					}catch(SocketTimeoutException ste) {
						if(!sent) {
							primToServer.writeUTF(SAVED);
							primToServer.flush();
						}else if(received) {
							primToServer.writeUTF(GOTGRID);
							primToServer.flush();
						}
						else {
							primToServer.writeUTF(SENDGRID);
							primToServer.flush();
						}
					}
				}
				startGamePlay();
			} catch(SocketTimeoutException ste) {}
			catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
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
				int message = -1;
				ongoingTurn = false;
				while(!gameOver) {
					try {
						if(turn) {
							takeTurn(ongoingTurn);
							ongoingTurn = true;
							while(true) {
								
								if(positionOnThisTurn > 0) { 
									primToServer.writeInt(positionOnThisTurn);
									primToServer.flush();
									message = primFromServer.readInt();
									if(myBoard.checkOpponentGridScore() >= 17) {
										gameOver = true;
										ongoingTurn = false;
										winGame();
										break;
									}
									if(message == SAVE) { 
										turn = false;
										ongoingTurn = false;
										positionOnThisTurn = -1;
										break;
									}
								}else {
									try {
										Thread.sleep(DELAY);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							
							}
						}
						else {
							while(true) {
								int opponentGuess = primFromServer.readInt();
								if(opponentGuess > 0 && opponentGuess <= 100) {
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
										e.printStackTrace();
									}
								}
							}
							if(myBoard.checkPlayerGridScore()>=17) {
								while(true) {
									primToServer.writeInt(LOST); //I LOST
									primToServer.flush();
									message = primFromServer.readInt();
									if(message == WIN) {
										loseGame();
										break;
									}
								}
								gameOver = true;
							}else {
								primToServer.writeInt(SAVE);
								primToServer.flush();
							}
						}
					}catch(SocketTimeoutException ste) {
						ste.printStackTrace();
					}catch(IOException ioe) {
						ioe.printStackTrace();
						break;
					}
				}
		}
	}
	
	public void endGame() {
		sc.renewHomepage();
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
		frame.setSize(2*frame.getWidth(),frame.getHeight());
		frame.add(sg,BorderLayout.EAST);
		frame.add(bg,BorderLayout.WEST);
	
		for(int i = 1; i <= 100 ; i ++) {
			bg.addButtonListener(i,new GridButtonListener(bg,this));
		}
		bg.disableButtonGrid();
	}
	
	public void takeTurn(Boolean ongoing) {
		if(!ongoing) {
			bg.enableButtonGrid();	
		}
	}
	
	public void giveTurn(JButton button, int position) {
		positionOnThisTurn = position;
		bg.disableButtonGrid();
		if(myBoard.opponentHitOrMiss(position)==1){
			button.setBackground(Color.RED);
		}
	}
}