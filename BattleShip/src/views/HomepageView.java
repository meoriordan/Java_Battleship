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
	public static final int DEFAULT_HEIGHT = 500;
	private JTextField userNameField;
	private JTextField passwordField;
	private ConnectUsers cu;
	private User user;
	private String onlineUsers;
	private ShipClient myClient;
	private JPanel panel;
	
	public HomepageView(User user, ShipClient myClient) {

			panel = new JPanel();
			
			this.user = user;
			this.myClient = myClient;
			add(panel);
			setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
			setVisible(true);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void updateUserList(String onlineUsers) {
		this.onlineUsers = onlineUsers;
		panel.removeAll();
		for(String user: this.onlineUsers.split(",")){
			if(!user.equals(this.user.getUsername())){
				JButton userButton = new JButton(user);
				userButton.setName(user);
				userButton.addActionListener(new ConnectListener());
				panel.add(userButton);
			}
		}
		
		panel.revalidate();
		panel.repaint();
	}
	
	
	class ConnectListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String opponent = ((JButton) e.getSource()).getText();
	        myClient.chooseOpponent(opponent);
		}	  
	  }
	
}