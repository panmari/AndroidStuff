package sm.connectFour;

public class MMBot extends ComputerPlayer {

	private final int searchDepth = 9;
	private final int VALUE_QUAD = 10000, VALUE_TRIPPLE = 100, 
			VALUE_PAIR = 20, VALUE_MIDDLE = 1;
	//columns left of the array get evaluated first:
	private final int[] columnPreference = {3, 2, 4, 5, 1, 0, 6};
	private int solution;
	private long nrEvaluatedSituations;

	public MMBot(ArrayManager am, int nbPlayer) {
		super(am, nbPlayer);
	}

	@Override
	public int getColumn() {
		if (firstMove) {
			firstMove = false;
			return randomMiddleColumn();
		}
		else {
		debugInfo("Start: \n" + am.getStringBoard(board));
		int value = maxValue(searchDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
		debugInfo("Evaluated " + nrEvaluatedSituations + 
				" best is: " + solution + " with worth: " + value);
		nrEvaluatedSituations = 0;
		return solution;
		}
	}

	private int maxValue(int depth, int alpha, int beta) {
		int newAlpha;
		for (int x: columnPreference) {
			if (insertToken(thisPlayer, x)) {
				if (depth <= 0 || isGameOver()) {
					newAlpha = evaluateSituation(thisPlayer);
				} else newAlpha = minValue(depth - 1, alpha, beta);
				removeTopmostToken(x);
				if (newAlpha > alpha) { //maximizing
					alpha = newAlpha;
					if (depth == searchDepth)
						solution = x;
				}
				if (newAlpha >= beta) //beta cut-off
					return beta;
			}
		}
		return alpha;
	}
	
	private int minValue(int depth, int alpha, int beta) {
		int newBeta;
		for (int x: columnPreference) {
			if(insertToken(other(thisPlayer), x)){
				if (depth <= 0 || isGameOver())
					newBeta = evaluateSituation(thisPlayer);
				else newBeta = maxValue(depth - 1, alpha, beta);
				removeTopmostToken(x);
				if (newBeta < beta) //minimizing
					beta = newBeta;
				if (newBeta <= alpha) //alpha cut-off
					return alpha; 
			}
		}
		return beta;
	}
	
	private int evaluateSituation(int player) {
		int result = 0;
		nrEvaluatedSituations++;
		
		if (getLines(4, other(player)) > 0)
			return (-1) * VALUE_QUAD;

		if (getLines(4, player) > 0)
			return VALUE_QUAD;
		
		for (int x = 2; x <= 4; x++)
			for (int y = 0; y < yMax; y++) 
				if (board[x][y] == player)
					result += VALUE_MIDDLE ;
				else if (board[x][y] == other(player))
					result -= VALUE_MIDDLE;
		
		result += VALUE_TRIPPLE * getLines(3, player);
		result += VALUE_PAIR * getLines(2, player);

		result -= VALUE_TRIPPLE * getLines(3, other(player));
		result -= VALUE_PAIR * getLines(2, other(player));
		//debugInfo(am.getStringBoard(board) + player + " <-- player | value --> " + result);
		return result;
	}
	
	private boolean isGameOver() {
		return getLines(4, thisPlayer) > 0 || getLines(4, other(thisPlayer)) > 0 || isBoardFull();
	}

	private int randomMiddleColumn() {
		return (int) (Math.random()*3) - 1 + (int) (xMax/2);
	}

	@Override
	public String getNameAndDescription() {
		return "MMBot - computes best outcome with minmax-algorithm.";
	}
}
