package views;

import controller.LoginController;


import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

//this controller lives on the server side 
//it receives the data from the login view and handles authenticating this info with the database 



public class LoginView extends JFrame {
	
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 500;
	private JTextField userNameField;
	private JTextField passwordField;
	private LoginController lc;
	
	public LoginView() {
			userNameField = new JTextField("",10);
			passwordField = new JPasswordField("", 10);
			JPanel panel = new JPanel();		
			panel.add(new JLabel("User name: "));
			panel.add(userNameField);
			panel.add(new JLabel("Password: "));
			panel.add(passwordField);
			JButton loginButton = new JButton("Login");
			loginButton.addActionListener(new LoginListener());
			panel.add(loginButton);

			add(panel);
			setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	public LoginController getLoginController() {
		return lc;
	}
	
	
	
	class LoginListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean loggedIn = false;
			System.out.println("test");
			String username = userNameField.getText().trim();
			String password = passwordField.getText().trim();
			
			System.out.println("username: " + username + "password: " + password);

			if (username.equals("") || password.equals("")) {
				return;
			}  
			
			else {
				lc = new LoginController(username, password, LoginView.this);
				boolean loginSuccess = lc.verifyInfo();
//				if (loginSuccess) {
//					LoginView.this.setVisible(false);
//				}	
			}
			
			
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

}