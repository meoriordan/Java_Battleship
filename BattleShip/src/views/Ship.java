package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;

public class Ship extends Rectangle{
	private static ArrayList<String> SHIPS = new ArrayList<String>(Arrays.asList("Carrier","Battleship","Cruiser","Submarine","Destroyer"));
	private static ArrayList<Integer> LENGTHS = new ArrayList<Integer>(Arrays.asList(5,4,3,3,2));
	private String shipType;
	private int shipLength;
	private int squareSize;
	private Color shipColor;
	private int gridX;
	private int gridY;
	private int width;
	private int height;
	private Dimension d;
	
	public Ship(String type,int x, int y) {
		this.shipType = type;
		if(shipType == "Carrier") {
			shipLength = 5;
		}
		else if(shipType == "Battleship") {
			shipLength = 4;
		}
		else if(shipType == "Cruiser") {
			shipLength = 3;
		}
		else if(shipType == "Submarine") {
			shipLength = 3;
		}
		else if(shipType == "Destroyer") {
			shipLength = 2;
		}
		else {
			System.err.println("Invalid ship type. Ship type unassinged");
		}
		shipColor = Color.DARK_GRAY;
		squareSize = 100;
		width = squareSize;
		height  = squareSize*shipLength;
		this.gridX = x;
		this.gridY = y;
		setLocation(gridX,gridY);
		d = new Dimension(width,height);
		
	}
	
	public void updateSize(int newSize) {
		width = width*newSize/squareSize;
		height = height*newSize/squareSize;
		squareSize = newSize;
		d.setSize(width,height);
		this.setSize(d);
	}
	
	public void rotateShip() {
		int newWidth = height;
		height = width;
		width = newWidth;
		d.setSize(width,height);
		this.setSize(d);
	}
	
	public int getShipWidth() {
		return width;
	}
	
	public int getShipHeight() {
		return height;
	}

	@Override
	public String toString() {
		return shipType;
	}
	
	public String getShipType() {
		return shipType;
	}
}
