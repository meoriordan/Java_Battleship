package shiplayout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import shiplayout.ShipServer.HandleClientConnect;

public class PlayGameServer {
	private Socket player1Socket;
	private Socket player2Socket;
	private DataInputStream primInFromPlayer1;
	private DataOutputStream primOutToPlayer1;
	private DataInputStream primInFromPlayer2; 
	private DataOutputStream primOutToPlayer2;
	private ObjectInputStream objInFromPlayer1;
	private ObjectOutputStream objOutToPlayer1;
	private ObjectInputStream objInFromPlayer2;
	private ObjectOutputStream objOutToPlayer2;
	private static final String SAVED = "SAVED";
	private static final String LISTENING = "LISTENING";
	private static final String GRIDSEND = "GRIDSEND";
	private static final String SENDGRID = "SENDGRID";
	private static final String START = "START";//start game
	private static final String GOTGRID = "GOTGRID";
	private Boolean gameOver;
	private static final String TURN = "TURN"; //give or take turn
	private static final int WIN = 1000;
	private static final int LOST = -1000;
	private static final int SAVE = 0;
	private String player1;
	private String player2;
	private HashMap<String, ArrayList<Integer>> player1Map;
	private HashMap<String, ArrayList<Integer>> player2Map;
	
	public PlayGameServer(HandleClientConnect player1,HandleClientConnect player2) throws IOException {
		primInFromPlayer1 = player1.getDataInput();
		primInFromPlayer2 = player2.getDataInput();
		primOutToPlayer1 = player1.getDataOutput();
		primOutToPlayer2 = player2.getDataOutput();
		
		objInFromPlayer1 = player1.getObjInput();
		objInFromPlayer2 = player2.getObjInput();
		objOutToPlayer1 = player1.getObjOutput();
		objOutToPlayer2 = player2.getObjOutput();
		
		this.player1 = player1.getUsername();
		this.player2 = player2.getUsername();
		
		gameOver = false;
		JFrame jf = new JFrame("GAME SERVER");
		jf.setVisible(true);
		jf.setSize(400,400);
		jf.setTitle(this.player1 + " vs. " +this.player2);
		
		Thread g1 = new Thread(new GetGridsRunnable(this.player1));
		g1.start();
		Thread g2 = new Thread(new GetGridsRunnable(this.player2));
		g2.start();
		
		while(true) {
			if(!g1.isAlive() && !g2.isAlive()) {
				break;
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Thread s1 = new Thread(new SetGridsRunnable(this.player1,player1Map));
		s1.start();
		Thread s2 = new Thread(new SetGridsRunnable(this.player2,player2Map));
		s2.start();
		
		while(true) {
			if(!s1.isAlive() && !s2.isAlive()) {
				break;
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		startGamePlay();
	}
	
	class SetGridsRunnable implements Runnable{
		private String player;
		private Boolean sentToPlayer;
		private DataInputStream primInFromPlayer; 
		private DataOutputStream primOutToPlayer;
		private ObjectInputStream objInFromPlayer;
		private ObjectOutputStream objOutToPlayer;
		private int playerNum;
		private HashMap<String, ArrayList<Integer>> playerMap;
		public SetGridsRunnable(String player,HashMap<String, ArrayList<Integer>> playerMap) {
			this.player = player;
			this.playerMap = playerMap;
			if(player1.equals(player)) {
				primInFromPlayer = primInFromPlayer2;
				primOutToPlayer = primOutToPlayer2;
				objInFromPlayer = objInFromPlayer2;
				objOutToPlayer = objOutToPlayer2;	
				playerNum = 2;
			}
			else if(player2.equals(player)) {
				primInFromPlayer = primInFromPlayer1;
				primOutToPlayer = primOutToPlayer1;
				objInFromPlayer = objInFromPlayer1;
				objOutToPlayer = objOutToPlayer1;	
				playerNum =1;
			}
			sentToPlayer = false;
		}
		
		@Override
		public void run() {
			while(!sentToPlayer) {
				String playerMessage;
				try {
					playerMessage = primInFromPlayer.readUTF();
					if(playerMessage.equals(SENDGRID)) {
						primOutToPlayer.writeUTF(GRIDSEND);
						primOutToPlayer.flush();
						objOutToPlayer.writeObject(playerMap);
					}
					if(playerMessage.equals(GOTGRID)) {
						sentToPlayer = true;
						break;
					}
				} catch(SocketException se) {
					break;
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}
	}
	
	class GetGridsRunnable implements Runnable{
		private String player;
		private Boolean gridReceived;
		private HashMap<String, ArrayList<Integer>> playerMap;
		private DataInputStream primInFromPlayer; 
		private DataOutputStream primOutToPlayer;
		private ObjectInputStream objInFromPlayer;
		private ObjectOutputStream objOutToPlayer;
		
		public GetGridsRunnable(String player) {
			playerMap = null;
			this.player =player;
			gridReceived = false;
			if(player1.equals(player)) {
				primInFromPlayer = primInFromPlayer1;
				primOutToPlayer = primOutToPlayer1;
				objInFromPlayer = objInFromPlayer1;
				objOutToPlayer = objOutToPlayer1;	
			}
			else if(player2.equals(player)) {
				primInFromPlayer = primInFromPlayer2;
				primOutToPlayer = primOutToPlayer2;
				objInFromPlayer = objInFromPlayer2;
				objOutToPlayer = objOutToPlayer2;	
			}
		}

		@Override
		public void run() {
			while(!gridReceived) {
				try {
					String playerMessage = primInFromPlayer.readUTF();

					if(playerMessage.equals(SAVED)) {
						primOutToPlayer.writeUTF(LISTENING);
						primOutToPlayer.flush();
						playerMap = (HashMap<String, ArrayList<Integer>>) objInFromPlayer.readObject();
						if(playerMap  != null) {
							gridReceived = true;
							primOutToPlayer.writeUTF(GOTGRID);
							primOutToPlayer.flush();
							if(player.equals(player1)) {
								player1Map = playerMap;
							}
							else {
								player2Map = playerMap;
							}
							break;
						}
					}	
				} catch (SocketException se) {
					System.out.println("Socket Exception: Connection Reset...Player 1");
					break;
				}catch(ClassNotFoundException cnfe) {
					cnfe.printStackTrace();
				}catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	class GamePlayRunnable implements Runnable{
		
		private int turns = 1;

		@Override
		public void run() {
				while(!gameOver) {
					try {
						if(turns%2 == 0) { // Player1s turn
							int player1Guess = primInFromPlayer1.readInt();
							if(player1Guess>0) {
								primOutToPlayer2.writeInt(player1Guess);
								primOutToPlayer2.flush();
								while(true) {
									try {
										int player2Message = primInFromPlayer2.readInt();
										if(player2Message == LOST) {
											primOutToPlayer2.writeInt(WIN);
											primOutToPlayer1.writeInt(LOST);
											gameOver = true;
											break;
										}else if(player2Message == SAVE) {
											primOutToPlayer2.writeInt(SAVE);
											primOutToPlayer1.writeInt(SAVE);
											turns++;
											break;
										}
									}catch(SocketTimeoutException ste) {
									}
								}
							}	
						}else {
							int player2Guess = primInFromPlayer2.readInt();
							if(player2Guess>0) {
								primOutToPlayer1.writeInt(player2Guess);
								primOutToPlayer1.flush();
								while(true) {
									try {
										int player1Message = primInFromPlayer1.readInt();
										if(player1Message == LOST) {
											primOutToPlayer1.writeInt(WIN);
											primOutToPlayer2.writeInt(LOST);
											gameOver = true;
											break;
										}else if(player1Message == SAVE) {
											primOutToPlayer1.writeInt(SAVE);
											primOutToPlayer2.writeInt(SAVE);
											turns++;
											break;
										}
									}catch(SocketTimeoutException ste) {
									}
								}
							}
						}
				}catch(SocketTimeoutException ste) {
				}catch(SocketException se) {
					se.printStackTrace();
					System.out.println("SOCKET CLOSED UNEXPECTEDLY RESTART");
					break;
				}
				catch(IOException ioe) {
					ioe.printStackTrace();
					break;
				}			
				}
		}	
	}
	
	public void startGamePlay() {
		Thread t = new Thread(new GamePlayRunnable());
		t.start();
	}
}
