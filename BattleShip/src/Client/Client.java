package Client;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

import javax.swing.*;

import controller.ConnectUsers;
import controller.LoginController;
import models.User;
import views.HomepageView;
import views.LoginView;


//connect to server upon opening  or button 
//view login screen upon connecting 
//view homepage screen upon authentication 
//view game screen upon connection w another user 

//thread to handle login view 
//thread to handle homepage view 
//

//client is getting information from the server, from its views of login and homepage 
//and from its controllers of login and connect users 

public class Client  {
	
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;
	
	ObjectOutputStream toServerObj = null;
	ObjectInputStream fromServerObj = null;
	
	Socket socket = null;
	
	ArrayList<User> activeUsers;
	private User user;
	
	LoginView lv;
	HomepageView hv;
	LoginController lc;
	ConnectUsers cu;
	
	String username;
	String password;

	public Client() {
		
		try {
			socket = new Socket("192.168.1.182",9898);
		    fromServer = new DataInputStream(socket.getInputStream());
		    toServer = new DataOutputStream(socket.getOutputStream());
		    
		    fromServerObj = new ObjectInputStream(socket.getInputStream());
		    toServerObj = new ObjectOutputStream(socket.getOutputStream());
		    
			lv = new LoginView(this);
		    lv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    lv.setVisible(true);
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void verifyLogin() {
		username = lv.getUserName();
		password = lv.getPassword();
		try {
			toServer.writeUTF(username);
			toServer.writeUTF(password);
			Boolean verify = fromServer.readBoolean();
			if (verify == true) {
				lv.setVisible(false);
				hv = new HomepageView();
				hv.setVisible(true);
				Object o = fromServerObj.readObject();
				user = (User) o;
				System.out.println(user.getUsername() + " " + user.getPassword() + " " + user.getTotalPoints() + " " + user.getUserID());	
			}
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
	}
}


