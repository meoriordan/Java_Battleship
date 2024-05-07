package views;

import controller.LoginController;
import shiplayout.ShipClient;

import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class LoginView extends JFrame {
	
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 300;
	private JTextField userNameField;
	private JTextField passwordField;
	String username;
	String password;
	private ShipClient myClient;
	private final static String ACCEPT = "Accept";
	private final static String ADDME = "ADDME";
	private final static String DENY = "Deny";
	private final static String FALSE = "FALSE";
	private final static String GAMEREQUEST = "GAMEREQUEST";
	private final static String GAMESTART = "GameStart";
	private final static String LOGIN = "Login";
	private final static String REGISTER = "REGISTER";
	private final static String REQUESTED = "Requested";
	private final static String SAVED = "SAVED";
	private final static String TRUE = "TRUE";
	private final static ArrayList<String> INVALID_USERNAMES = new ArrayList<String>(Arrays.asList(ACCEPT,ADDME,DENY,FALSE,GAMESTART,LOGIN,REGISTER,REQUESTED,SAVED,TRUE));

	public LoginView(ShipClient c) {
		
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
			userNameField = new JTextField("",10);
			passwordField = new JPasswordField("", 10);
			this.myClient = c;
			
			JPanel panel = new JPanel();		
			panel.add(new JLabel("User name: "));
			panel.add(userNameField);
			panel.add(new JLabel("Password: "));
			panel.add(passwordField);
			
			JButton loginButton = new JButton("Login");
			loginButton.addActionListener(new LoginListener());
			panel.add(loginButton);
			
			JButton registerButton = new JButton("Register");
			registerButton.addActionListener(new RegisterListener());
			panel.add(registerButton);
			

			add(panel);
			setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
			setVisible(true);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	class LoginListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			username = userNameField.getText().trim();
			password = passwordField.getText().trim();

			for(String invalidString: INVALID_USERNAMES) {
				if(username.equals(invalidString)) {
					return;
				}
			}			
			if (username.equals("") || password.equals("")) {
				return;
			} 
			myClient.verifyLogin();
		}		
	}
	
	public String getUserName() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	class RegisterListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			myClient.switchToRegistration();	
		}		
	}
}

