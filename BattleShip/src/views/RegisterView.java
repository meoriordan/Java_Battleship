package views;

import controller.LoginController;
import shiplayout.ShipClient;
import Client.Client;

import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;


public class RegisterView extends JFrame {
	
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 500;
	private JTextField userNameField;
	private JTextField passwordField;
	String username;
	String password;
	private ShipClient myClient;
	
	public RegisterView(ShipClient c) {
		
			userNameField = new JTextField("",10);
			passwordField = new JPasswordField("", 10);
			this.myClient = c;
			
			JPanel panel = new JPanel();		
			panel.add(new JLabel("User name: "));
			panel.add(userNameField);
			panel.add(new JLabel("Password: "));
			panel.add(passwordField);
			
			JButton registerButton = new JButton("Register");
			registerButton.addActionListener(new RegisterListener());
			panel.add(registerButton);

			add(panel);
			setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
			setVisible(true);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	class RegisterListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			username = userNameField.getText().trim();
			password = passwordField.getText().trim();
						
			if (username.equals("") || password.equals("")) {
				return;
			} 
			
			myClient.attemptRegistration(username, password);
		}		
	}
	
	public String getUserName() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}		
}

