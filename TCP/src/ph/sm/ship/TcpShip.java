// TcpShip.java

package ph.sm.ship;

import android.graphics.Color;
import ch.aplu.android.*;
import ch.aplu.tcp.*;
import ch.aplu.util.Monitor;


public class TcpShip extends GameGrid implements GGTouchListener, TcpNodeListener
{
  private String roomID = "";
  private String sessionID = "awq";
  private final String myNodeName = "captain";
  private TcpNode node = new TcpNode();
  private boolean isMyMove = false;
  private final int nbShips = 10;
  private Location loc;
  private int myScore = 0;
  private int enemyScore = 0;
  protected GGStatusBar status;

  private interface Command
  {
    int GAME_START = 0;
    int SHIP_MISSED = 1;
    int SHIP_HIT = 2;
  }
    
  public TcpShip()
  {
    super(6, 6, 60, RED);
    status = addStatusBar(50);
    // setScreenOrientation(GGNavigationListener.ScreenOrientation.FIXED);    
  }
  
  public void main()
  {
    setTitle("TCP BattleShip");
    init();
  }
  
  public void init()
  {
    for (int i = 0; i < nbShips; i++)
      addActor(new Ship(), getRandomEmptyLocation());
    addTouchListener(this, GGTouch.click);    
    refresh();
    node.addTcpNodeListener(this);
    status.setText("Connecting to  relay...");
    refresh();
    connect(10);
    if (node.getNodeState() == TcpNodeState.CONNECTED)
    {
      status.setText("Connection established.");
    }
    else
      status.setText("Connection failed");
    refresh();
  }
  
  private void connect(int timeout)
  {
    while (roomID.length() < 3)
      roomID = requestEntry("Enter unique game room name (more than 2 characters):");   
    sessionID = sessionID + roomID;
    node.connect(sessionID, myNodeName);
    
    int connectingTime = 0;
    while (node.getNodeState() != TcpNodeState.CONNECTED && connectingTime < timeout) {
    	connectingTime++;
    	Monitor.putSleep(1000);
    }
  }
  
  private String requestEntry(String prompt)
  {
    return GGInputDialog.show("TcpShip" , prompt, "");
  }
  
  public void nodeStateChanged(TcpNodeState state)
  {
    if (state == TcpNodeState.DISCONNECTED)
      status.setText("Connection broken.");
  }
  
  public boolean touchEvent(GGTouch tap)
  {
    if (!isMyMove)
      return true;
    loc = toLocationInGrid(tap.getX(), tap.getY());
    node.sendMessage("" + loc.x + loc.y); // send string
    status.setText("Wait enemy bomb!");
    isMyMove = false;
    return true;
  }
  
  public void messageReceived(String sender, String text)
  {
    int x = text.charAt(0) - 48; // We get ASCII code of number
    int y = text.charAt(1) - 48;
    
    if (x == 9)  // Got command
    {
      switch (y)
      {
        case TcpShip.Command.GAME_START:
          isMyMove = false;
          status.setText("Wait! Enemy will shoot first.");
          break;
        case TcpShip.Command.SHIP_HIT:
          addActor(new Actor("checkgreen"), loc);
          myScore++;
          if (myScore == nbShips)
            gameOver(true);
          break;
        case TcpShip.Command.SHIP_MISSED:
          addActor(new Actor("checkred"), loc);
          break;
      }
    }
    else // Got coordinates
    {
      Location loc = new Location(x, y);
      Actor actor = getOneActorAt(loc, Ship.class);
      if (actor != null)
      {
        actor.removeSelf();
        addActor(new Actor("explosion"), loc);
        node.sendMessage("9" + TcpShip.Command.SHIP_HIT);
        enemyScore++;
        if (enemyScore == nbShips)
        {
          gameOver(false);
          return;
        }
      }
      else
        node.sendMessage("9" + TcpShip.Command.SHIP_MISSED);

      status.setText("Shoot now! Score: " + myScore + " (" + enemyScore + ")");
      isMyMove = true;
    }
  }

  private void gameOver(boolean isWinner)
  {
    isMyMove = false;
    removeAllActors();
    if (isWinner)
      status.setText("You won!");  
    else
      status.setText("You lost!");  
  }

  
  public void statusReceived(String text)
  {
    if (text.contains("In session:--- (0)"))  // we are first player
    {
      status.setText("Connected. Wait for partner.");
    }
    if (text.contains("In session:--- (1)"))  // we are second player
    {
      node.sendMessage("9" + TcpShip.Command.GAME_START);
      status.setText("It is you to play");
      isMyMove = true;  // Second player starts
    }
  }  
}

class Ship extends Actor
{
  public Ship()
  {
    super("boat");
  }
}