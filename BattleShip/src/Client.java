
import models.User;
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
import javax.swing.*;

import controller.ConnectUsers;
import controller.LoginController;
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
	ArrayList<User> activeUsers;
	Socket socket = null;
	private User user;
	
	LoginView lv;
	HomepageView hv;
	LoginController lc;
	ConnectUsers cu;
	
	

	
	public Client() {
		
		try {
			socket = new Socket("192.168.1.182",9898);
		    fromServer = new DataInputStream(socket.getInputStream());
		    toServer = new DataOutputStream(socket.getOutputStream());
		    fromServerObj = new ObjectInputStream(socket.getInputStream());
		    toServerObj = new ObjectOutputStream(socket.getOutputStream());
		    
			System.out.println("Success");
			LoginView l = new LoginView();
		    l.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    l.setVisible(true);
		    l.addComponentListener(new winlistenner());
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}


	}
	
	public class winlistenner implements ComponentListener {

        public void componentHidden(ComponentEvent arg0) {
            // TODO Auto-generated method stub
            System.out.print("Hided\r\n");
			user = new User(1,"Elizabeth","test",0);
//            hv = new HomepageView(user);
            try {
                toServer.writeUTF("user is here");
                toServerObj.writeObject(user);
                toServer.flush();
                
                activeUsers = new ArrayList<User>();
                int totalUsers = fromServer.readInt();
                for (int i = 0; i < totalUsers; i++) {
                	Object o = fromServerObj.readObject();
                	activeUsers.add((User)o);
                	hv = new HomepageView(user, activeUsers);
                }
            } 
            
            catch (ClassNotFoundException e) {
            	e.printStackTrace();
            }
            catch (IOException e) {
            	e.printStackTrace();
            }
            
            

            hv.setVisible(true);

        }

        public void componentMoved(ComponentEvent arg0) {
            // TODO Auto-generated method stub
            System.out.print("Moved\r\n");

        }

        public void componentResized(ComponentEvent arg0) {
            // TODO Auto-generated method stub
            System.out.print("Resized\r\n");


        }

        public void componentShown(ComponentEvent arg0) {
            // TODO Auto-generated method stub

            System.out.print("Shown\r\n");

        }

}
	
//	public void sendMyUser() {
//		user = cu.getUser();
//		toServer.writeObject(user);
//	}
	
	//thread to get updated list from server
	//one thread that is listening to output from server? 
	//server will send three types of info: 1. updated list of users; 2. user accept/ decline  3. game info 
	//once the game has started the client only needs to receive game info. therefore that can be a separate thread once the game has started 
	
	
//	public ArrayList<User> getCurrentUsers() {
//		//gets current users from server 
//		//passes them to the homepage controller and homepage view to be displayed
//		
//	}
	
//	class OpenConnectionListener implements ActionListener {
//
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			try {
//				socket = new Socket("192.168.1.182", 9898);
//			    fromServer = new DataInputStream(socket.getInputStream());
//			    toServer = new DataOutputStream(socket.getOutputStream());
//				textArea.append("connected\n");
//				// start a thread to get input from the server
//				Thread t = new Thread(new WaitForServer());
//	            t.start();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//				textArea.append("connection Failure");
//			}
//		}	  
//	  }
//	  
//	  class WaitForServer implements Runnable {
//		  
//		  public void run() {
//			  
//			  try {
//				  toServer.writeUTF("HELLO");
//				  String greeting = fromServer.readUTF();
//				  }  
//			  catch (IOException e) {
//				  e.printStackTrace();
//
//			  }
//			  
//
//			  
//			  while(true) {
//				  try {
//					  String m = fromServer.readUTF();
//					  textArea.append(m + "\n");
//				  }
//				  catch (Exception e) {
//					  e.printStackTrace();
//					  break;
//				  }
//			  }
//		  }
//	  }
	
	
	
	public static void main(String[] args) {
		Client client = new Client();
//	    client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	    client.setVisible(true);
	    
//	    new 
	}
}


