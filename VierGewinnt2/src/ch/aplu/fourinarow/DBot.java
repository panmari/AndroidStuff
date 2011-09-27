// DBot.java

package ch.aplu.fourinarow;

import ch.aplu.android.*;
import java.util.*;

public class DBot extends IPlayer
{
  public DBot(int thisPlayer, GameGrid gg)
  {
    super(gg);
    this.thisPlayer = thisPlayer;
    this.enemyPlayer = (thisPlayer + 1) % 2;
  }

  public int getColumn()
  {
    ArrayList<Integer> possibleSolutions = new ArrayList<Integer>();
    ArrayList<Integer> veryBadIdeas = new ArrayList<Integer>();
    int topRow = board[0].length - 1;
    int column;
    int row;

    if (isBoardEmpty())
    {
      if (debug)
        System.out.println("me first, me choose middle!"); //debug
      return 4;
    }

    // Can I win in this turn?
    for (column = 0; column < board.length; column++)
    {
      row = insertToken(column);
      // if column is full, row = -1:
      if (row != -1 && checkXInARow(column, row, 4, thisPlayer, board))
      {
        if (debug)
          System.out.println("Found something that makes me win: "
            + (column + 1)); // debug
        return column;
      }
    }

    // Can enemy win in his next turn?
    for (column = 0; column < board.length; column++)
    {
      row = insertToken(column);
      // if column is full, row == -1:
      if (row != -1 && checkXInARow(column, row, 4, enemyPlayer, board))
      {
        if (debug)
          System.out.println("Found something that makes enemy win: "
            + (column + 1)); // debug
        return column;
      }
    }

    // stay defensive! try to destroy enemies chances to win:
    // put all these possibilities into ArrayList: possibleSolutions
    for (column = 0; column < 7; column++)
    {
      row = insertToken(column);

      if (row != -1 && checkXInARow(column, row, 3, enemyPlayer, board))
      {
        if (debug)
          System.out.println("Found something good: " + (column + 1)); // debug
        possibleSolutions.add(column);
      }

      if (row != -1 && checkXInARow(column, row, 2, enemyPlayer, board))
      {
        if (debug)
          System.out.println("Found something (maybe) valuable: "
            + (column + 1)); // debug
        possibleSolutions.add(column);
      }
    }

    // does any solution enable my enemy to win next turn?
    Iterator<Integer> posSolu = possibleSolutions.iterator();
    int possibleColumn;
    while (posSolu.hasNext())
    {
      possibleColumn = posSolu.next();
      int nextRow = insertToken(possibleColumn) + 1;
      if (nextRow <= topRow
        && checkXInARow(possibleColumn, nextRow, 4, enemyPlayer, board))
      {
        posSolu.remove();
        if (debug)
          System.out.println("removed solutionzzz, left is: "
            + possibleSolutions);
      }
    }

    // prefer solutions in the middle of field:
    int nrOfSolutions = possibleSolutions.size();
    for (int i = 0; i < nrOfSolutions; i++)
    {
      if (possibleSolutions.get(i) > 1 && possibleSolutions.get(i) < 5)
        possibleSolutions.add(possibleSolutions.get(i));
    }

    // if there are any solutions left, return a random one
    // One column may be in there multiple times
    // -> it's a better move -> it's probability is higher!
    if (!possibleSolutions.isEmpty())
    {
      Collections.shuffle(possibleSolutions);
      return (int)possibleSolutions.get(0);
    }


    // add illegal moves to veryBadIdeas:
    for (int col = 0; col < 7; col++)
    {
      if (board[col][topRow].getPlayer() != -1)
        veryBadIdeas.add(col);
      else
      { // add moves that enable my enemy to win to veryBadIdeas
        int nextRow = insertToken(col) + 1;
        if (nextRow <= topRow
          && checkXInARow(col, nextRow, 4, enemyPlayer, board))
          veryBadIdeas.add(col);
      }
    }
    if (debug)
      System.out.println("Found very bad ideas: " + veryBadIdeas);

    Random grn = new Random();
    // if there are only bad ideas, choose a random valid column:
    if (veryBadIdeas.size() == 7)
    {
      do
      {
        // values 2,3,4 are more probable than 0,1 or 5,6
        // because stones in the middle are more valuable
        column = grn.nextInt(4) + grn.nextInt(4);
        // column = grn.nextInt(7); // then try random position
      }
      while (board[column][topRow].getPlayer() != -1);
      if (debug)
        System.out.println("Computer found too many bad ideas! Choosing:  "
          + (column + 1));
      return column;
    }
    else
    {
      do
      {
        column = grn.nextInt(4) + grn.nextInt(4);
      }
      while (veryBadIdeas.contains(column));
    }
    if (debug)
      System.out.println("me " + thisPlayer + " Found some very bad ideas"
        + veryBadIdeas + ", but that should work: " + (column + 1));
    return column;
  }
}
