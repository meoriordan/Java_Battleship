package views;

import controller.LoginController;

import Client.Client;

import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;


public class LoginView extends JFrame {
	
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 500;
	private JTextField userNameField;
	private JTextField passwordField;
	private LoginController lc;
	String username;
	String password;
	private HashMap<String, String> hm;
	private Client myClient;
	
	public LoginView(Client c) {
		
			userNameField = new JTextField("",10);
			passwordField = new JPasswordField("", 10);
			hm = new HashMap<String, String>();
			this.myClient = c;
			
			JPanel panel = new JPanel();		
			panel.add(new JLabel("User name: "));
			panel.add(userNameField);
			panel.add(new JLabel("Password: "));
			panel.add(passwordField);
			
			JButton loginButton = new JButton("Login");
			LoginListener ll = new LoginListener();
			loginButton.addActionListener(new LoginListener());
			panel.add(loginButton);

			add(panel);
			setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	
	class LoginListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			username = userNameField.getText().trim();
			password = passwordField.getText().trim();
			
			System.out.println("USERNAME: " + username + "PASSWORD : " + password);
			
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
			
		}

