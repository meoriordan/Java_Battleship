import java.util.ArrayList;

public class Grid {
	
	private int[] positions;
	int boardID;
	
	public Grid(int boardID) {
		this.boardID = boardID;
		positions = new int[100];
	}
	
	public void setValue(int value, int position) {
		positions[position] = value;
	}
	
	
}