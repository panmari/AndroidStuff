// FourInARow.java

package ch.aplu.fourinarow;

import ch.aplu.android.*;
import android.graphics.Color;
import java.util.Arrays;

public class FourInARow extends GameGrid implements GGTouchListener
{
  private int currentPlayer = 0;
  public boolean finished = false;
  Token activeToken;
  private IPlayer ComputerPlayer;
  private String moveInfo = "Drag chip and release ";
  private static final int nbHorzCells = 7;
  private static final int nbVertCells = 7;

  public FourInARow()
  {
    super(nbHorzCells, nbVertCells, 40, Color.TRANSPARENT);
  }

  public void main()
  {
    addTouchListener(this, GGTouch.release | GGTouch.drag);
    getBg().clear(Color.WHITE);
    activeToken = new Token(currentPlayer, this);
    addActor(activeToken, new Location(0, 0), Location.SOUTH);
    addActor(new BG(), new Location(3, -4)); //outside of grid, so it doesn't disturb game
    setSimulationPeriod(30);
    setTouchEnabled(true);
    doRun();
    ComputerPlayer = new DBot(1, this); //menu for choosing?
    for (Token[] column : DBot.board) //fill board with "empty" stones
      Arrays.fill(column, new Token(-1, this));
    showToast("Game started");
    setStatusText(moveInfo);
  }

  public void reset()
  {
    getBg().clear();
    removeActors(Token.class); //remove all tokens
    for (Token[] column : DBot.board) //fill board with "empty" stones
      Arrays.fill(column, new Token(-1, this));
    currentPlayer = 0; //Human player always starts (bc i'm lazy)
    showToast("Game restarted");
    setStatusText(moveInfo);
    activeToken = new Token(currentPlayer, this);
    addActor(activeToken, new Location(0, 0), Location.SOUTH);
    finished = false;
  }

  public void computerMove()
  {
    setTouchEnabled(false);
    int col = ComputerPlayer.getColumn();
    // (AP) if this column is full take the first available column
    if (getOneActorAt(new Location(col, 1)) != null)
    {
      for (int i = 0; i < nbHorzCells; i++)
      {
        if (getOneActorAt(new Location(i, 1)) == null)
        {
          col = i;
          break;
        }
      }
    }
    activeToken.setX(col);
    activeToken.setActEnabled(true);
    currentPlayer = (currentPlayer + 1) % 2; //change Player
  }

  public boolean touchEvent(GGTouch touch)
  {

    Location mouseLoc = toLocationInGrid(touch.getX(), touch.getY());
    if (touch.getEvent() == GGTouch.drag)
    {
      //move active token with mouse
      if (!finished && activeToken.getX() != mouseLoc.x)
        activeToken.setX(mouseLoc.x);
      return true;
    }

    if (finished)
    {
      reset();
      return true;
    }

    if (getOneActorAt(new Location(mouseLoc.x, 1)) == null)
    {
      //drop Token if column isn't full
      activeToken.setActEnabled(true);
      setTouchEnabled(false);
      currentPlayer = (currentPlayer + 1) % 2;
    }
    else
    {
      showToast("This column is full.");
    }

    return true;
  }

  public boolean isBoardFull()
  {
    boolean isFull = true;
    for (int i = 0; i < nbHorzCells; i++)
    {
      if (getOneActorAt(new Location(i, 1)) == null)
      {
        isFull = false;
        break;
      }
    }
    return isFull;
  }

  public int getPlayerOfTokenAt(int x, int y)
  {
    Location loc = new Location(x, y);
    if (getOneActorAt(loc) == null)
      return -1;
    else
      return ((Token)getOneActorAt(loc)).getPlayer();
  }

  // @param the location of newly inserted token
  // @return true, if four are connected through that token
  public boolean check4Win(Location loc)
  {
    int col = loc.x;
    int row = loc.y;
    return (checkVertically(col, row, 4) || checkHorizontally(col, row, 4)
      || checkDiagonally1(col, row, 4)
      || checkDiagonally2(col, row, 4));

  }

  private boolean checkDiagonally2(int col, int row, int nrOfTokens)
  {
    for (int j = 0; j < nrOfTokens; j++)
    {
      int adjacentSameTokens = 0;
      for (int i = 0; i < nrOfTokens; i++)
      {
        if ((col - i + j) >= 0 && (col - i + j) < nbHorzCells
          && (row + i - j) >= 1 && (row + i - j) < nbVertCells
          && getPlayerOfTokenAt(col - i + j, row + i - j) == getPlayerOfTokenAt(col, row))
        {
          adjacentSameTokens++;
        }
      }
      if (adjacentSameTokens >= nrOfTokens)
        return true;
    }
    return false;
  }

  private boolean checkDiagonally1(int col, int row, int nrOfTokens)
  {
    for (int j = 0; j < nrOfTokens; j++)
    {
      int adjacentSameTokens = 0;
      for (int i = 0; i < nrOfTokens; i++)
      {
        if ((col + i - j) >= 0 && (col + i - j) < nbHorzCells
          && (row + i - j) >= 1 && (row + i - j) < nbVertCells
          && getPlayerOfTokenAt(col + i - j, row + i - j) == getPlayerOfTokenAt(col, row))
        {
          adjacentSameTokens++;
        }
      }
      if (adjacentSameTokens >= nrOfTokens)
        return true;
    }
    return false;
  }

  private boolean checkHorizontally(int col, int row, int nrOfTokens)
  {
    int adjacentSameTokens = 1;
    int i = 1;
    while (col - i >= 0 && getPlayerOfTokenAt(col - i, row) == getPlayerOfTokenAt(col, row))
    {
      adjacentSameTokens++;
      i++;
    }
    i = 1;
    while (col + i < nbHorzCells && getPlayerOfTokenAt(col + i, row) == getPlayerOfTokenAt(col, row))
    {
      adjacentSameTokens++;
      i++;
    }
    return (adjacentSameTokens >= nrOfTokens);
  }

  private boolean checkVertically(int col, int row, int nrOfTokens)
  {
    int adjacentSameTokens = 1;
    int i = 1;
    while (row + i < nbVertCells && getPlayerOfTokenAt(col, row + i) == getPlayerOfTokenAt(col, row))
    {
      adjacentSameTokens++;
      i++;
    }
    return (adjacentSameTokens >= nrOfTokens);
  }
}
