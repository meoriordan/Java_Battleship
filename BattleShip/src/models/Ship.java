

public class Ship {
	
	private static int shipIDs;
	
	private int shipID;
	private int boardID;
	private int[] startPos;
	private int[] endPos;
	private final String shipType;
	private final int length;
	private boolean inPlay;
	
	public Ship(int boardID, String shipType, int length) {
		this.boardID = boardID;
		this.shipType = shipType;
		this.length = length;
		this.inPlay = true;
	}
	
	
	public int getShipID() {
		return shipID;
	}
	public void setShipID(int shipID) {
		this.shipID = shipID;
	}
	public int getBoardID() {
		return boardID;
	}
	public void setBoardID(int boardID) {
		this.boardID = boardID;
	}
	public int[] getstartPos() {
		return startPos;
	}
	public void setStartPos(int[] startPos) {
		this.startPos = startPos;
	}
	public String getShipType() {
		return shipType;
	}
	public void setShipType(String shipType) {
		this.shipType = shipType;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public boolean isInPlay() {
		return inPlay;
	}
	public void setInPlay(boolean inPlay) {
		this.inPlay = inPlay;
	}
	

	
}