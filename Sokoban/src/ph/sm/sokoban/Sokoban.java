// Sokoban.java

package ph.sm.sokoban;

import ch.aplu.android.*;
import android.graphics.*;

public class Sokoban extends GameGrid implements GGSoftButtonListener
{
  private final static SokobanGrid grid = new SokobanGrid(0); // 0, 1, or 2
  private final static int nbHorzCells = grid.getNbHorzCells();
  private final static int nbVertCells = grid.getNbVertCells();
  private final int borderColor = Color.rgb(255, 0, 0);
  private SokobanStone[] stones = new SokobanStone[grid.getNbStones()];
  private SokobanTarget[] targets = new SokobanTarget[grid.getNbStones()];
  private SokobanActor sok;
  private boolean isFinished = false;

  public Sokoban()
  {
    super(nbHorzCells, nbVertCells, cellZoom(20), Color.LTGRAY);
    addSoftButton(0, "Up");
    addSoftButton(1, "Dn");
    addSoftButton(2, "Lt");
    addSoftButton(3, "Rt");
  }

  public void main()
  {
    GGBackground bg = getBg();
    drawBoard(bg);
    drawActors();
    addSoftButtonListener(this);
  }

  private void drawActors()
  {
    int stoneIndex = 0;
    int targetIndex = 0;

    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a == 5) // Sokoban actor
        {
          sok = new SokobanActor();
          addActor(sok, location);
        }
        if (a == 3) // Stones
        {
          stones[stoneIndex] = new SokobanStone();
          addActor(stones[stoneIndex], location);
          stoneIndex++;
        }
        if (a == 4) // Targets
        {
          targets[targetIndex] = new SokobanTarget();
          addActor(targets[targetIndex], location);
          targetIndex++;
        }
      }
    }
    setPaintOrder(SokobanTarget.class);
  }

  private void drawBoard(GGBackground bg)
  {
    bg.clear(Color.WHITE);
    bg.setPaintColor(Color.DKGRAY);
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a == 0) // outside
          bg.fillCell(location, Color.LTGRAY);
        if (a == 2)  // Border
          bg.fillCell(location, borderColor);
      }
    }
  }

  public void buttonPressed(int buttonNb){}
  public void buttonReleased(int buttonNb){}
  public void buttonClicked(int buttonNb){}

  public void buttonRepeated(int buttonNb)
  {
    if (isFinished)
      return;
    Location next = null;
    switch (buttonNb)
    {
      case 2:
        next = sok.getLocation().getNeighbourLocation(Location.WEST);
        sok.setDirection(Location.WEST);
        break;
      case 0:
        next = sok.getLocation().getNeighbourLocation(Location.NORTH);
        sok.setDirection(Location.NORTH);
        break;
      case 3:
        next = sok.getLocation().getNeighbourLocation(Location.EAST);
        sok.setDirection(Location.EAST);
        break;
      case 1:
        next = sok.getLocation().getNeighbourLocation(Location.SOUTH);
        sok.setDirection(Location.SOUTH);
        break;
    }
    if (next != null && canMove(next))
    {
      sok.setLocation(next);
    }
    refresh();
  }

  private boolean canMove(Location location)
  {
    // Test if try to move into border
    int c = getBg().getColor(location);
    if (c == borderColor)
      return false;
    else // Test if there is a stone
    {
      SokobanStone stone = (SokobanStone)getOneActorAt(location, SokobanStone.class);
      if (stone != null)
      {
        // Try to move the stone
        stone.setDirection(sok.getDirection());
        if (moveStone(stone))
          return true;
        else
          return false;
      }
    }
    return true;
  }

  private boolean moveStone(SokobanStone stone)
  {
    Location next = stone.getNextMoveLocation();
    // Test if try to move into border
    int c = getBg().getColor(next);
    if (c == borderColor)
      return false;

    // Test if there is another stone
    SokobanStone neighbourStone =
      (SokobanStone)getOneActorAt(next, SokobanStone.class);
    if (neighbourStone != null)
      return false;

    // Move the stone
    stone.setLocation(next);

    // Check if we are at a target
    if (getOneActorAt(next, SokobanTarget.class) != null)
      stone.show(1);
    else
      stone.show(0);
    return true;
  }
}
// --------- SokobanActors-------------
class SokobanActor extends Actor
{
  public SokobanActor()
  {
    super(true, "sokoban");  // Rotatable
  }
}

class SokobanTarget extends Actor
{
  public SokobanTarget()
  {
    super("target");
  }
}

class SokobanStone extends Actor
{
  public SokobanStone()
  {
    super("sokobanstone", 2);
  }
}

// -----  SokobanGrid.java
class SokobanGrid
{
  private final static int nbHorzCells = 19;
  private final static int nbVertCells = 11;
  private static int[][] a = new int[nbHorzCells][nbVertCells];
  private static int nbStones = 0;

  private final static String soko_0 =
    "    xxxxx          " + // 0 (19)
    "    x...x          " + // 1
    "    x*..x          " + // 2
    "  xxx..*xx         " + // 3
    "  x..*.*.x         " + // 4
    "xxx.x.xx.x   xxxxxx" + // 5
    "x...x.xx.xxxxx..oox" + // 6
    "x.*..*..........oox" + // 7
    "xxxxx.xxx.xAxx..oox" + // 8
    "    x.....xxxxxxxxx" + // 9
    "    xxxxxxx        ";  //10
  private final static int nbHorzCells_0 = 19;
  private final static int nbVertCells_0 = 11;

  private final static String soko_1 =
    "xxxxxxxxxxxx  " + // 0  (14)
    "xoo..x.....xxx" + // 1
    "xoo..x.*..*..x" + // 2
    "xoo..x*xxxx..x" + // 3
    "xoo....A.xx..x" + // 4
    "xoo..x.x..*.xx" + // 5
    "xxxxxx.xx*.*.x" + // 6
    "  x.*..*.*.*.x" + // 7
    "  x....x.....x" + // 8
    "  xxxxxxxxxxxx"; // 9
  private final static int nbHorzCells_1 = 14;
  private final static int nbVertCells_1 = 10;

  private final static String soko_2 =
  "        xxxxxxxx " + // 0  (17)
  "        x.....Ax " + // 1
  "        x.*x*.xx " + // 2
  "        x.*..*x  " + // 3
  "        xx*.*.x  " + // 4
  "xxxxxxxxx.*.x.xxx" + // 5
  "xoooo..xx.*..*..x" + // 6
  "xxooo....*..*...x" + // 7
  "xoooo..xxxxxxxxxx" + // 8
  "xxxxxxxx         ";  // 9
  private final static int nbHorzCells_2 = 17;
  private final static int nbVertCells_2 = 10;

  private final static String[] sokoModel =
  {
    soko_0, soko_1, soko_2
  };

  private final static int[] nbHorzCellsModel =
  {
    nbHorzCells_0, nbHorzCells_1, nbHorzCells_2
  };

  private final static int[] nbVertCellsModel =
  {
    nbVertCells_0, nbVertCells_1, nbVertCells_2
  };

  private static int model;

  public SokobanGrid(int model)
  {
    this.model = model;

    // Copy structure into integer array
    for (int k = 0; k < nbVertCellsModel[model]; k++)
    {
      for (int i = 0; i < nbHorzCellsModel[model]; i++)
      {
        switch (sokoModel[model].charAt(nbHorzCellsModel[model] * k + i))
        {
          case ' ':
            a[i][k] = 0;  // Empty outside
            break;
          case '.':
            a[i][k] = 1;  // Empty inside
            break;
          case 'x':
            a[i][k] = 2;  // Border
            break;
          case '*':
            a[i][k] = 3;  // Stones
            nbStones++;
            break;
          case 'o':
            a[i][k] = 4;  // Target positions
            break;
          case 'A':
            a[i][k] = 5;  // Sokoban actor
            break;
        }
      }
    }
  }

  public static int getNbHorzCells()
  {
    return nbHorzCellsModel[model];
  }

  public static int getNbVertCells()
  {
    return nbVertCellsModel[model];
  }

  public static int getNbStones()
  {
    return nbStones;
  }

  public static int getCell(Location location)
  {
    return a[location.x][location.y];
  }
}