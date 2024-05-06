package views;

import controller.LoginController;
import models.User;
import shiplayout.ShipClient;
import controller.ConnectUsers;

import java.awt.*;
import javax.swing.*;

import Client.Client;

import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


public class HomepageView extends JFrame {
	
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 300;
	private JTextField userNameField;
	private JTextField passwordField;
	private ConnectUsers cu;
	private User user;
	private String onlineUsers;
	private ShipClient myClient;
	private JPanel panel;
	private JPanel onlineUsersPanel;
	private ArrayList<ArrayList<String>> pastGames;
	
	public HomepageView(User user, ShipClient myClient, ArrayList<ArrayList<String>> pastGames) {

			
			panel = new JPanel();
			
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			this.user = user;
			this.myClient = myClient;
			this.pastGames = pastGames;
			
			onlineUsersPanel = new JPanel();
			panel.add(onlineUsersPanel);
			JPanel panelGames = new JPanel();
			panelGames.setLayout(new BoxLayout(panelGames, BoxLayout.Y_AXIS));
			panel.add(panelGames);
			this.add(panel);
			
			JPanel p1 = new JPanel();
			p1.add(new JLabel("GAME HISTORY"));
			p1.setMaximumSize(new Dimension(500,1000));
			panelGames.add(p1);
			if (pastGames != null) {
				int count = 1;
				for (ArrayList<String> a: pastGames) {
					JPanel p = new JPanel();
					p.setMaximumSize(new Dimension(500,1000));
					p.add(new JLabel("Game " + count + ": " + a.get(0) + " against " + a.get(1) + ". " + a.get(2) + " won."));
					panelGames.add(p);
					count++;
				}
			}
			
			this.setTitle(user.getUsername());
			setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
			setVisible(true);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void updateUserList(String onlineUsers) {
		this.onlineUsers = onlineUsers;
		onlineUsersPanel.removeAll();
		for(String user: this.onlineUsers.split(",")){
			if(!user.equals(this.user.getUsername())){
				JButton userButton = new JButton(user);
				userButton.setName(user);
				userButton.addActionListener(new ConnectListener());
				onlineUsersPanel.add(userButton);
			}
		}
		
		onlineUsersPanel.revalidate();
		onlineUsersPanel.repaint();
	}
	
	
	class ConnectListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String opponent = ((JButton) e.getSource()).getText();
	        myClient.chooseOpponent(opponent);
		}	  
	  }
	
}