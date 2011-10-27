package ch.mazzzy.nimAndroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.aplu.android.Actor;
import ch.aplu.android.GameGrid;


public class ComputerPlayer {
	
	protected int[] pearlArrangement;
	private final int dualMax = 4;
	private int vertCells;
	protected GameGrid gg;
	protected boolean misere;
	private boolean changeStrat;

	public ComputerPlayer(GameGrid pearlGG, boolean misere) {
		this.gg = pearlGG;
		this.vertCells = gg.getNbVertCells();
		this.misere = misere;
	}
	
	/**
	 * updates the Pearl Arrangement. Only the row taken from
	 * and the amount (+1/-1 usually) is necessary.
	 * 
	 * The Position in the array
	 * corresponds to the getY of the Pearls.
	 * @param row getY() of the removed Pearls
	 * @param amount Amount of Pearls removed there
	 */
	public void updatePearlArrangement(int row, int amount) {
		pearlArrangement[row] += amount;
	}
	
	
	/**
	 * Initiates a computer move. The int[] pearlArrangement has 
	 * to represent the board exactly when calling this method. 
	 * If not, this algorithm may cause an exception.
	 */
	public void makeMove() {
		int nbToRemove = 0;
		int[] tgPearls = new int[vertCells];
		
		int removeRow = adaptToMisere();
		if (removeRow != -1) {
			//this part is needed for adapting to misère mode
			System.arraycopy(pearlArrangement, 0, tgPearls, 0, vertCells);
			tgPearls[removeRow] = 0;
			if (isUSituation(tgPearls)) 
				nbToRemove = pearlArrangement[removeRow];
			else nbToRemove = pearlArrangement[removeRow] - 1;
		} else if (!isUSituation(pearlArrangement)) {
			// if optimal Strategy is not possible, do something random.
			ArrayList<Actor> pearls = gg.getActors(Pearl.class);
			System.out.println("Doing something random");
			// from a random (not empty!) row
			Collections.shuffle(pearls);
			removeRow = pearls.get(0).getY();
			// take a random amount (at least 1)
			nbToRemove = (int) ((pearlArrangement[removeRow] - 1) * Math.random() + 1);
		} else {
			// list for saving all possible solutions
			List<int[]> solutions = new ArrayList<int[]>();
			// Try all possible situations and add them to solutions if they're good.
			for (int y = 0; y < vertCells; y++) {
				System.arraycopy(pearlArrangement, 0, tgPearls, 0, vertCells);
				for (int i = 0; i < pearlArrangement[y]; i++) {
					tgPearls = makeSituation(tgPearls, y);
					if (isUSituation(tgPearls) == false) {
						solutions.add((new int[] { y, i + 1 }));
					}
				}
			}
			// choose a random solution
			Collections.shuffle(solutions);
			removeRow = solutions.get(0)[0];
			nbToRemove = solutions.get(0)[1];
			System.out.println("Number of solutions: " + solutions.size());
		}
		removePearls(removeRow, nbToRemove);
	}
	
	/**
	 * 
	 * @return -1 if the strategy doesn't change or 
	 * 			the row of the column you have to remove pearls from
	 * 			if you have to change strategy.
	 * 			
	 */
	private int adaptToMisere() {
		if (changeStrat || !misere)
			return -1;
		boolean oneHeapBiggerTwo = false;
		int bigHeap = -1;
		for (int heap = 0; heap < vertCells; heap++) {
			if (pearlArrangement[heap] > 1) {
				bigHeap = heap;
				if (oneHeapBiggerTwo)
					return -1;
				oneHeapBiggerTwo = true;
			}
		}
		System.out.println("changing strategy for misere!");
		changeStrat = true;
		return bigHeap;
	}
	
	/**
	 * Removes pearls on the board for real. Also updates the
	 * Array representation of the board itself.
	 * @param removeRow The row pearls are removed from
	 * @param nbToRemove The number of pearls removed
	 */
	private void removePearls(int removeRow, int nbToRemove) {
		updatePearlArrangement(removeRow, -nbToRemove);

		List<Actor> removeCandidates = new ArrayList<Actor>();

		for (Actor p: gg.getActors(Pearl.class)) {
			if (p.getY() == removeRow)
				removeCandidates.add(p);
		}
		Collections.shuffle(removeCandidates);
		while (nbToRemove > 0) {
			Actor removedPearl = removeCandidates.remove(0);
			removedPearl.removeSelf();
			nbToRemove--;
		}
	}

	/**
	 *  For debugging only
	 *  Gives a pretty string representation of the given int array
	 * @return A string containing all values of k in order
	 */
	private String toString(int[] k) {
		String output = "";
		for (int i = 0; i < k.length; i++)
			output = (output + k[i] + ", ");
		return output;
	}

	/**
	 *  Removes a match from a situation given and returns the same
	 *  int array. Beware, the situation
	 *  @param sit The original situation
	 * 	@param row The row where a match should be removed from
	 */
	private int[] makeSituation(int[] sit, int row) {
		sit[row] = sit[row] - 1;
		return sit;
	}

	/**
	 * Check out http://de.wikipedia.org/wiki/Nim-Spiel to see what 
	 * a U-situation is (called "C-Stellung" there).
	 * @param sit A game situation
	 * @return true, if this situation is u.
	 */
	private Boolean isUSituation(int[] sit) {
		int[] allDuals = new int[dualMax];
		int[] oneDual = new int[dualMax];
		for (int y = 0; y < vertCells; y++) {
			oneDual = toDual(sit[y]);
			for (int i = 0; i < allDuals.length; i++) {
				allDuals[i] = allDuals[i] + oneDual[i];
			}
		}
		for (int i = 0; i < allDuals.length; i++) {
			if (allDuals[i] % 2 == 1)
				return true;
		}
		return false;
	}

	/**
	 * Gives an integer back as binary (as array of integer) only works for
	 * input < 16, or arraylength "dualMax" must be changed
	 */
	private int[] toDual(int input) {
		assert(input < 16);
		int[] output = new int[dualMax]; // 4 dualstellen
		int x = 0;
		while (input != 0) {
			output[x] = input % 2;
			input = input / 2;
			x++;
		}
		return output;
	}
	
	public void reset() {
		pearlArrangement = new int[vertCells];
		changeStrat = false;
	}
}
