package views;

import controller.LoginController;
import models.User;
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
	public static final int DEFAULT_HEIGHT = 500;
	private JTextField userNameField;
	private JTextField passwordField;
	private ConnectUsers cu;
	private User user;
	private ArrayList<User> onlineUsers;
	private Client myClient;
	JPanel panel;
	
	public HomepageView(User user, ArrayList<User> onlineUsers, Client myClient) {

			panel = new JPanel();
			
	
			
			this.user = user;
			this.onlineUsers = onlineUsers;
			this.myClient = myClient;
			setTitle(user.getUsername());
			
			for (User u: onlineUsers) {
				if (u.getUsername().equals(user.getUsername())) {
					continue;
				}
				else {
					JButton x = new JButton(u.getUsername());
					panel.add(x);
					x.addActionListener(new ConnectListener());
				}
			}
			add(panel);
			setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	
	public void refreshUserList(ArrayList<User> onlineUsers) {
		panel.removeAll();
		for (User u: onlineUsers) {
			if (u.getUsername().equals(user.getUsername())) {
				continue;
			}
			else {
				JButton x = new JButton(u.getUsername());
				panel.add(x);
				x.addActionListener(new ConnectListener());
			}
		}
		panel.revalidate();
		panel.repaint();
		
		
	}
	
	
	
	class ConnectListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String opponent = ((JButton) e.getSource()).getText();
//	        System.out.println(((JButton) e.getSource()).getText());
	        myClient.attemptConnection(opponent);
		}	  
	  }
	
	public String receivedConnectionRequest(String opponentName) {
		String[] options = new String[] {"accept","deny"};
		String username = user.getUsername();
		int n = JOptionPane.showOptionDialog(null,
			    ("New game request from "+opponentName+". Accept?"),
			    ("GAME REQUSET TO " + username),
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE,
			    null,     //do not use a custom Icon
			    options,  //the titles of buttons
			    options[0]); //default button title
		String response = options[n];
//		System.out.println("USER CHOSE "+response);
		return response;
	}

	
}