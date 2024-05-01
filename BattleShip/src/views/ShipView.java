package views;

import Client.Client;
import models.User;
import controller.GameControllerClient;
import controller.GridButtonListener;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import controller.PlayGameClient;
import controller.SaveButtonListener;
import shiplayout.ButtonGrid;
import shiplayout.SnapGrid;



public class ShipView extends JFrame {
	
		private User user;
		private String opponent;
		private Client client;
		private GameControllerClient gcc;
		private ButtonGrid bg;
		SnapGrid sg;
	
		public ShipView(User user, String opponent, Client client) {
			this.user = user;
			this.opponent = opponent;
			this.client = client;
			this.gcc = new GameControllerClient(this, client);
			setTitle(user.getUsername() + " vs." + opponent);
			sg = new SnapGrid();
			sg.addSaveButtonListener(new SaveButtonListener(sg,gcc));
			setLayout(new BorderLayout());
			add(sg,BorderLayout.CENTER); 
			setSize(new Dimension(1000,1000));
			setVisible(true);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		
		public void addButtonGrid() {
			bg = new ButtonGrid(sg);
			this.remove(sg);
			this.add(sg,BorderLayout.EAST);
			this.add(bg,BorderLayout.WEST);
		
			for(int i = 1; i <= 100 ; i ++) {
				bg.addButtonListener(i,new GridButtonListener(bg, gcc));
			}
			bg.disableButtonGrid();
		}
		
		public void enableButtonGrid() {
			bg.enableButtonGrid();
		}
		
		public void disableButtonGrid() {
			bg.disableButtonGrid();
		}
		
		public GameControllerClient getGCC() {
			return this.gcc;
		}


//public static void main (String[] args) {
//	ShipView sv = new ShipView();
//    sv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    sv.setVisible(true);
//}

}