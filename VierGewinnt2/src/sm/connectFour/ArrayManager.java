package sm.connectFour;

/**
 * Is responsible for the correct transition of the GameGrid into an Array which can be used
 * by a computerplayer to compute it's next move. Does nothing else but managing the board.
 * All game or strategic related data has to be computed in the computerPlayer!
 */
public class ArrayManager {
	
	private final int noToken = 2;
	private int[][] boardArray;
	private int xMax, yMax;
	
	public ArrayManager(int xMax, int yMax) {
		this.xMax = xMax;
		this.yMax = yMax;
		boardArray = new int[xMax][yMax];
		initializeBoardArray();
	}
	
	public void addToken(int x, int y, int player) {
		assert (player == 1 || player == 0);
		this.boardArray[x][y] = player;
	}
	

	private void initializeBoardArray() {
		for (int x = 0; x < xMax; x++)
			for (int y = 0; y < yMax; y++)
				boardArray[x][y] = noToken;
	}
	
	public int[][] getBoardArray() {
		return boardArray;
	}
	
	/**
	 * TODO: check for invalid board changes!
	 */
	private void invariant() {
		
	}
	
	/**
	 * For debugging purposes only
	 * Prints the given array in the console
	 * @param board
	 */
	public String getStringBoard(int[][] board) {
		String boardString = "";
		for (int y = yMax - 1; y >= 0; y--) {
			boardString += "|";
			for (int x = 0; x < xMax; x++) {
				if (board[x][y] == noToken)
					boardString += " |";
				else boardString += board[x][y] + "|";
			}
			boardString += "\n";
		}
		return boardString;
	}

	public void reset() {
		initializeBoardArray();
	}
	
	public int getNoTokenRepresentation() {
		return noToken;
	}
	
	public int getxMax() {
		return xMax;
	}
	
	public int getyMax() {
		return yMax;
	}
	
	@ForTestingOnly
	public int[][] getBoardCopy() {
		int[][] boardCopy = new int [xMax][yMax];
		for (int x = 0; x < xMax; x++)
			for (int y = 0; y < yMax; y++)
				boardCopy[x][y] = boardArray[x][y];
		return boardCopy;
	}

}
