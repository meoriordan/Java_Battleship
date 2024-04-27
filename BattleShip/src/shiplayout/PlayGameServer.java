package shiplayout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

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
	private static final String LOST = "LOST"; //IF I receive this I won
	private static final String WIN = "WIN"; //IF I receive this I lost
	private static final String START = "START";//start game
	private Boolean gameOver;
	private static final String TURN = "TURN"; //give or take turn
	
	public PlayGameServer(Socket player1, Socket player2) throws IOException {
		primInFromPlayer1 = new DataInputStream(player1.getInputStream());
		primInFromPlayer2 = new DataInputStream(player2.getInputStream());
		primOutToPlayer1 = new DataOutputStream(player1.getOutputStream());
		primOutToPlayer2 = new DataOutputStream(player2.getOutputStream());
		
		objInFromPlayer1 = new ObjectInputStream(player1.getInputStream());
		objInFromPlayer2 = new ObjectInputStream(player2.getInputStream());
		objOutToPlayer1 = new ObjectOutputStream(player1.getOutputStream());
		objOutToPlayer2 = new ObjectOutputStream(player2.getOutputStream());
		gameOver = false;
		Thread t = new Thread(new SetGridsRunnable());
		t.start();
	}
	
	class SetGridsRunnable implements Runnable{
		
		private Boolean player1Set = false;
		private Boolean player2Set = false;;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(!player1Set || !player2Set) {
				try {
					
					String player1Message = primInFromPlayer1.readUTF();
					String player2Message = primInFromPlayer2.readUTF();
					
					if(player1Message.equals(SAVED)) {
						primOutToPlayer1.writeUTF(LISTENING);
						HashMap<String, ArrayList<Integer>> player1Map = (HashMap<String, ArrayList<Integer>>) objInFromPlayer1.readObject();
						primOutToPlayer1.flush();
						if(player1Map != null) {
							primOutToPlayer2.writeUTF(GRIDSEND);
							primOutToPlayer2.flush();
							objOutToPlayer2.writeObject(player1Map);
							objOutToPlayer2.flush();
							player2Set = true;
						}
					}
					if(player2Message.equals(SAVED)) {
						primOutToPlayer2.writeUTF(LISTENING);
						HashMap<String, ArrayList<Integer>> player2Map = (HashMap<String, ArrayList<Integer>>) objInFromPlayer2.readObject();
						primOutToPlayer2.flush();
						if(player2Map != null) {
							primOutToPlayer1.writeUTF(GRIDSEND);
							primOutToPlayer2.flush();
							objOutToPlayer2.writeObject(player2Map);
							objOutToPlayer2.flush();
							player1Set = true;
						}
					}
					
					objInFromPlayer1.close();
					objOutToPlayer1.close();
					objInFromPlayer2.close();
					objOutToPlayer2.close();
					startGamePlay();
					
				
				} catch (IOException ioe) {
					// TODO Auto-generated catch block
					ioe.printStackTrace();
				}catch(ClassNotFoundException cnfe) {
					//if objInFromPlayer does not find an object
					cnfe.printStackTrace();
				}
			}
			
		}
		
	}
	

	class GamePlayRunnable implements Runnable{
		
		private int turns = 1;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				String player1Message = primInFromPlayer1.readUTF();
				String player2Message = primInFromPlayer2.readUTF();
				while(!gameOver) {
					if(turns%2 == 1) { // Player1s turn
						int player1Guess = primInFromPlayer1.readInt();
						if(player1Guess>0) {
							primOutToPlayer1.writeUTF(SAVED);
							primOutToPlayer1.flush();
							primOutToPlayer2.writeInt(player1Guess);
							primOutToPlayer2.flush();
							player2Message = primInFromPlayer2.readUTF();
							if(player2Message.equals(LOST)) {
								primOutToPlayer2.writeUTF(WIN);
								primOutToPlayer1.writeUTF(LOST);
								gameOver = true;
							}else if(player2Message.equals(SAVED)) {
								turns++;
								continue;
							}
						}	
						
					}else {
						int player2Guess = primInFromPlayer2.readInt();
						if(player2Guess>0) {
							primOutToPlayer2.writeUTF(SAVED);
							primOutToPlayer2.flush();
							primOutToPlayer1.writeInt(player2Guess);
							primOutToPlayer1.flush();
							player1Message = primInFromPlayer1.readUTF();
							if(player1Message.equals(LOST)) {
								primOutToPlayer1.writeUTF(WIN);
								primOutToPlayer2.writeUTF(LOST);
								gameOver = true;
							}else if(player1Message.equals(SAVED)) {
								turns++;
								continue;
							}
						}
						
					}
				}
				
				primInFromPlayer1.close();
				primOutToPlayer1.close();
				primInFromPlayer2.close();
				primOutToPlayer2.close();				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				
			}
			
			
		}
		
	}
	
	public void startGamePlay() {
		Thread t = new Thread(new GamePlayRunnable());
		t.start();
	}
	
	
}
