package models;

import java.util.ArrayList;

public class Grid {
	
	private int[] positions;
	int boardID;
	
	public Grid(int boardID) {
		this.boardID = boardID;
		positions = new int[101];
		
		//initialize all positions to zero
		for(int i = 1; i<=100;i++) {
			positions[i] = 0;
		}
	}
	
	public int[] getPositions() {
		return positions;
	}
	
	public void setPositions(int[] pos) {
		for (int i = 0; i < 101; i++) {
			positions[i] = pos[i];
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
		for (int i = 0; i < 100; i++) {
			totalPoints += i;
		}
		return totalPoints;
	}	
	
}