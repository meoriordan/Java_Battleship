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
	
	public HomepageView(User user, ArrayList<User> onlineUsers, Client myClient) {

			JPanel panel = new JPanel();
			
			this.user = user;
			this.onlineUsers = onlineUsers;
			this.myClient = myClient;
			
			for (User u: onlineUsers) {
				JButton x = new JButton(u.getUsername());
				panel.add(x);
				x.addActionListener(new ConnectListener());
			}
			add(panel);
			setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
//	public void updateUsers(ArrayList<User> ou) {
//		System.out.println("here in update users");
//		this.onlineUsers = ou;
//		this.repaint();
//	}
	
	
	class ConnectListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String opponent = ((JButton) e.getSource()).getText();
	        System.out.println(((JButton) e.getSource()).getText());
	        myClient.attemptConnection(opponent);
		}	  
	  }
	
}