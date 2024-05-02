package models;

import java.util.ArrayList;

public class Grid {
	
	private int[] positions;
	int boardID;
	
	public Grid(int boardID) {
		this.boardID = boardID;
		positions = new int[101];
		
		//initialize all positions to zero
		for(int i = 0; i<=100;i++) {
			positions[i] = 0;
		}
	}
	
	public void setValue(int value, int position) {
		positions[position] = value;
	}
	
	public int getPosValue(int pos) { //because we will only want to change this value if the current value is -1
		return positions[pos];
	}
	
	public int getTotalPoints() {
		int totalPoints = 0;
		for (int i = 0; i <= 100; i++) {
			totalPoints += positions[i];
		}
		return totalPoints;
	}	
	
	public String toString() {
		String printVal = "";
		for(int i:positions) {
			printVal = printVal + i+ "   ";
		}
		return printVal;
	}
}