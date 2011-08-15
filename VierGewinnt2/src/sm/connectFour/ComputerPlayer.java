package sm.connectFour;

public abstract class ComputerPlayer {

	private final  boolean debug = true;
	protected boolean firstMove = true;
	protected int thisPlayer;
	protected int[][] board;
	protected int xMax, yMax;
	protected ArrayManager am;

	public ComputerPlayer(ArrayManager am, int nbPlayer) {
		this.am = am;
		this.thisPlayer = nbPlayer;
		this.xMax = am.getxMax();
		this.yMax = am.getyMax();
	}
	
	/**
	 * @return the column the token is placed
	 */
	abstract public int getColumn();
	
	public abstract String getNameAndDescription();

	@ForTestingOnly
	public void updateBoard() {
		board = am.getBoardCopy();
	}
	
	@ForTestingOnly
	public int getLines(int length, int player) {
		return (getHorizontalLines(length, player) + 
				getVerticalLines(length, player) + 
				getDiagonalLines(length, player));
	}

	private int getHorizontalLines(int length, int player) {
		int total_hits = 0;
		if (length > 4 || length < 2)
			return 0;

		for (int y = 0; y < yMax; y++) {
			for (int x = 0; x <= xMax - length; x++) {
				int hit = 1;
				for (int t = 1; t < length; t++) {
					if (board[x + t][y] != board[x][y]
							|| board[x + t][y] != player) {
						hit = 0;
						break;
					}
				}
				total_hits += hit;
			}
		}
		return total_hits;
	}

	private int getVerticalLines(int length, int player) {
		int total_hits = 0;

		if (length > 4 || length < 2)
			return 0;

		for (int x = 0; x < xMax; x++) {
			for (int y = 0; y <= yMax - length; y++) {
				int hit = 1;
				for (int t = 1; t < length; t++) {
					if (board[x][y + t] != board[x][y]
							|| board[x][y + t] != player) {
						hit = 0;
						break;
					}
				}
				total_hits += hit;
			}
		}
		return total_hits;
	}

	private int getDiagonalLines(int length, int player) {
		int total_hits = 0;

		for (int x = 0; x <= (xMax - length); x++) {
			for (int y = 0; y <= (yMax - length); y++) {
				int hit = 1;
				for (int t = 1; t < length; t++) {
					if ((board[x + t][y + t] != board[x][y])
							|| (board[x + t][y + t] != player))
						hit = 0;
				}
				total_hits += hit;
			}
		}

		for (int x = xMax - 1; x >= length - 1; x--) {
			for (int y = 0; y <= (yMax - length); y++) {
				int hit = 1;
				for (int t = 1; t < length; t++) {
					if ((board[x - t][y + t] != board[x][y])
							|| (board[x - t][y + t] != player))
						hit = 0;
				}
				total_hits += hit;
			}
		}
		return total_hits;
	}
	
	protected boolean isBoardFull() {
		for (int x = 0; x < xMax; x++)
			if (board[x][yMax-1] == am.getNoTokenRepresentation())
				return false;
		return false;
	}
	
	@ForTestingOnly
	public boolean isBoardEmpty() {
		for (int x = 0; x < xMax; x++)
			if (board[x][0] != am.getNoTokenRepresentation())
				return false;
		return true;
	}

	protected boolean insertToken(int player, int x) {
		int y = 0;
		while (y < yMax) {
			if (board[x][y] == am.getNoTokenRepresentation()) {
				board[x][y] = player;
				return true;
			}
			y++;
		}
		return false;
	}

	protected void removeTopmostToken(int column) {
		int y = yMax - 1;
		while (y >= 0) {
			if (board[column][y] != am.getNoTokenRepresentation()) {
				board[column][y] = am.getNoTokenRepresentation();
				return;
			}
			y--;
		}
	}
	
	protected int other(int player) {
		return (player + 1) % 2;
	}
	
   	protected void debugInfo(String info) {
		if (debug) System.out.println(info);		
	}
   	
   	protected void reset() {
   		firstMove = true;
   	}

}
