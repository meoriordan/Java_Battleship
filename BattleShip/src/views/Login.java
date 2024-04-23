package views;


import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

//client should maintain the login and socket information 
//if logged out, client should see login screen 
//once client has logged in, they can see the home screen



public class Login extends JFrame {
	
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 500;
	private JTextField userNameField;
	private JTextField passwordField;
//	private Socket socket = null;
//	DataOutputStream toServer = null;
//	DataInputStream fromServer = null;
	
	public Login(Socket socket, DataInputStream fromServer, DataOutputStream toServer) {
//			this.socket = socket;
//			this.fromServer = fromServer;
//			this.toServer = toServer;
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
	
	
	class LoginListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean loggedIn = false;
			System.out.println("test");
			String username = userNameField.getText().trim();
			String password = passwordField.getText().trim();
			
			if (username.equals("") || password.equals("")) {
				return;
			} 
			
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