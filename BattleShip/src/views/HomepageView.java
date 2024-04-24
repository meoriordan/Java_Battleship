package views;

import controller.LoginController;
import controller.ConnectUsers;
import models.User;

import java.awt.*;
import javax.swing.*;

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
	
	public HomepageView(User user, ArrayList<User> onlineUsers) {
			JPanel panel = new JPanel();
			
			this.user = user;
			this.onlineUsers = onlineUsers;
			
//			for (shiplayout.User u: onlineUsers) {
//				panel.add(new JLabel(u.getUsername()));
//			}
			
			for (User u: onlineUsers) {
//				String x = u.getUsername();
				JButton x = new JButton(u.getUsername());
				panel.add(x);
				x.addActionListener(new LoginListener());
			}
//			panel.add(new JLabel("UsER NAME: "));
//			panel.add(new JLabel("Password: "));
//			panel.add(new JLabel(user.getUsername()));
			JButton loginButton = new JButton("Login");
			loginButton.addActionListener(new LoginListener());
			panel.add(loginButton);

			add(panel);
			setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	public void setOnlineUsers(ArrayList<User> ou) {
		this.onlineUsers = ou;
	}
	
	
	class LoginListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean loggedIn = false;
			System.out.println("test");
//			String username = userNameField.getText().trim();
//			String password = passwordField.getText().trim();
			
//			System.out.println("username: " + username + "password: " + password);

//			if (username.equals("") || password.equals("")) {
//				return;
			}  
			
//			else {
//				LoginController l1 = new LoginController(username, password, LoginView.this);
//				boolean loginSuccess = l1.verifyInfo();
//				if (loginSuccess) {
//					LoginView.this.setVisible(false);
//				}	
//			}
			
			
			//login will pass info to the controller which handles the authentication
//			try {
//			    toServer.writeUTF(username);
//			    toServer.writeUTF(password);
//			    String success = fromServer.readUTF();
//			    System.out.println("Success = " + success);
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
			
		}
		  
	  }

