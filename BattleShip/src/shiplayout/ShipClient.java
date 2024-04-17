package shiplayout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class ShipClient extends JFrame{
	private DataOutputStream toServer = null;
	private DataInputStream fromServer = null;
	private JPanel activeUserPanel;
	private HashMap<String,JButton> activeUserButtons;
	private Socket hostSocket;
	private static int DELAY = 20;
	private String username;
	private String opponent;
	private JButton openButton;
	private Thread userListHandle;
	private final static String GAMEREQUEST = "GAMEREQUEST";
	private String[] options;
	private final static String ACCEPT = "Accept";
	private final static String DENY = "Deny";
	private final static String REQUESTED = "Requested";
	private final static String GAMESTART = "GameStart";
	private final static ArrayList<String> INVALID_USERNAMES = new ArrayList<String>(Arrays.asList(ACCEPT,DENY,REQUESTED,GAMESTART,GAMEREQUEST));
	private JScrollPane scrollPane;
	//private final Lock inputStreamLock = new ReentrantLock();
	private boolean buttonPressed;
	private String userList;
	
	
	public ShipClient() {
		activeUserPanel = new JPanel(new GridLayout(0,1,5,5));
		activeUserButtons = new HashMap<String,JButton>();
		buttonPressed = false;
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300,400);
		setLocationRelativeTo(null);
		
		scrollPane = new JScrollPane(activeUserPanel);
		add(scrollPane,BorderLayout.CENTER);
		
		username = JOptionPane.showInputDialog("Enter username: ");
		while(true) {
			if(!INVALID_USERNAMES.contains(username)) {//update to not be any of the hard strings
				JOptionPane.showMessageDialog(null, "Welcome "+username);
				break;
			}else {
				username = JOptionPane.showInputDialog("Choose a different username: ");
			}
		}
		
		
		openButton = new JButton("Log in");
		openButton.addActionListener(new OpenConnectionListener());
		openButton.setSize(100,40);
		add(openButton,BorderLayout.SOUTH);
		
		setTitle("Player: "+username);
		
		//GAMEREQUEST OPTIONS
		options = new String[] {ACCEPT,DENY};
		
		
		
	}
	
	public void gameStart() {
		activeUserPanel.removeAll();
		activeUserButtons.clear();
		remove(activeUserPanel);
		remove(scrollPane);
		setTitle(username + " vs. "+opponent);
		SnapGrid sg = new SnapGrid();
		ButtonGrid bg = new ButtonGrid(sg);
		setLayout(new BorderLayout());
		add(sg,BorderLayout.EAST);
		add(bg,BorderLayout.CENTER);
		setSize(new Dimension(1000,1000));
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	
	class OpenConnectionListener implements ActionListener{
		
//		public void refreshList() {
//			activeUsers.clear();
//		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				hostSocket = new Socket("localhost",9898);
				toServer = new DataOutputStream(hostSocket.getOutputStream());
				fromServer = new DataInputStream(hostSocket.getInputStream());
				toServer.writeUTF(username);
				toServer.flush();
				//openButton.setName("Refresh");
				openButton.removeActionListener(this);
				remove(openButton);
				revalidate();
				repaint();
				
			}catch(IOException ioe) {
				ioe.printStackTrace();
				System.out.println("CONNECTION FAILED");
			}
			userListHandle = new Thread(new UserListener());
			userListHandle.start();
			
		}
		
		
	}
	
	class UserListener implements Runnable{

		@Override
		public void run() {
			try {
				
				while(true) {
					if(!buttonPressed) {
						String message = fromServer.readUTF();
						if(message.equals(GAMEREQUEST)) {
							toServer.writeUTF(REQUESTED);
							String opponentName = fromServer.readUTF();
							//toServer = new DataOutputStream(hostSocket.getOutputStream());
							int n = JOptionPane.showOptionDialog(null,
								    ("New game request from "+opponentName+". Accept?"),
								    ("GAME REQUSET TO " + username),
								    JOptionPane.YES_NO_OPTION,
								    JOptionPane.QUESTION_MESSAGE,
								    null,     //do not use a custom Icon
								    options,  //the titles of buttons
								    options[0]); //default button title
							String response = options[n];
							System.out.println("USER CHOSE "+response);
							toServer.writeUTF(response);
							toServer.flush();
							if(response.equals(ACCEPT)) {
								opponent = opponentName; 
								gameStart();
								break;
							}
							else {
								refreshUserList();
							}
						}
						else{
							if(!INVALID_USERNAMES.contains(message)) {
								userList  = message;
							}
							refreshUserList();	
						}
					}
					
				}
				
				System.out.println("USER LIST THREAD ENDED: " +username);
			}catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
		}
		
		private void refreshUserList() {
			SwingUtilities.invokeLater(()->{
				activeUserPanel.removeAll();
				activeUserButtons.clear();
				
				for(String user: userList.split(",")) {
					if(!user.equals(username)) {
						JButton userButton = new JButton(user);
						userButton.setName(user);
						userButton.addActionListener(new ChooseOpponentListener());
						activeUserPanel.add(userButton);
						activeUserButtons.put(user, userButton);
					}
				}
				
				activeUserPanel.revalidate();
				activeUserPanel.repaint();
			});
		}
		
	}
	
	class ChooseOpponentListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton opponentButton = (JButton) e.getSource();
			String opponentName = opponentButton.getName();
			System.out.println("Game Request sent to "+opponentName);
			buttonPressed = true;
			try {
				toServer.writeUTF(GAMEREQUEST);
				toServer.flush();
				toServer.writeUTF(opponentName);
				while(true) {
					String response = fromServer.readUTF();
					if(response.equals(ACCEPT)) {
						opponent = opponentName;
						System.out.println("User "+opponentName + " accepted! Commencing game...");
						buttonPressed = false;
						gameStart();
						break;//and do other stuff like switch frames
					}else if(response.equals(DENY)){
						System.out.println("User "+opponentName + " denied your request. Try a different opponent.");
						buttonPressed = false;
						//refreshUserList();
						break;
					}
				}
				//userListHandle.notify();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(()-> new ShipClient().setVisible(true));
	}
}