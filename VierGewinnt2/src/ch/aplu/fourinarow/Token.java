// Token.java

package ch.aplu.fourinarow;

import android.graphics.Point;
import ch.aplu.android.*;
import ch.aplu.util.Monitor;

public class Token extends Actor
{
  private int player, nb;
  private FourInARow gg;
  private int cellSizeFactor;

  public Token(int player, FourInARow gg)
  {
    super(false, "token", 2);
    this.player = player;
    this.gg = gg;
    setActEnabled(false);
    show(player); // 0 = yellow , 1 = red
    cellSizeFactor = gg.getCellSize()/6;
  }

  public void act()
  {
    Location nextLoc = new Location(getX(), getY() + 1);
    if (gameGrid.getOneActorAt(nextLoc) == null && isMoveValid())
    {
      if (nb == 6)
      {
        nb = 0;
        setLocationOffset(new Point(0, 0));
        move();
      }
      else
        setLocationOffset(new Point(0, nb*cellSizeFactor ));
      nb++;
    }
    else
    { 
      //token has arrived
      setActEnabled(false);
      IPlayer.board[getX()][Math.abs(getY() - 6)] = this; //put into table for computers move
      if (gg.check4Win(getLocation()))
      {
        gg.showToast(player == 0 ? "You won!" : "You lost!");
        gg.showToast("Click anywhere to play again.");
        gg.finished = true;
        gg.refresh();
        Monitor.putSleep(2000); // wait for 2 seconds
      }
      else if (gg.isBoardFull())
      {
        gg.showToast("It's a draw!");
        gg.showToast("Click anywhere to play again.");
        gg.finished = true;
        gg.refresh();
        Monitor.putSleep(2000); // wait for 2 seconds
      }
      else
      {
        // make new Token:
        gg.activeToken = new Token((player + 1) % 2, gg);
        gg.addActor(gg.activeToken, new Location(getX(), 0),
          Location.SOUTH);
      }
      gg.setTouchEnabled(true);
      if (this.player == 0 && !gg.finished) // if this was human -> computer move
        gg.computerMove();
    }
  }

  public int getPlayer()
  {
    return player;
  }
}
