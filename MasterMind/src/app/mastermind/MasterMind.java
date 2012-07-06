// MasterMind.java

package app.mastermind;

import ch.aplu.android.*;
import android.graphics.*;

import java.util.ArrayList;


// -----------class ActiveRowMarker -----------
public class MasterMind extends GameGrid implements GGTouchListener
{
  private int[] secretCode = new int[4];
  private int currentRow;
  private boolean roundFinished;
  private ActiveRowMarker marker;
  GGTextField status;
  private int placedPegs = 0;
  
  public MasterMind()
  {
    super(5, 8, 0, Color.TRANSPARENT, "mastermind", false);
  }

  public void main()
  {
    this.addTouchListener(this, GGTouch.click);
    getBg().setPaintColor(Color.RED);
    getBg().clear(Color.WHITE);
    reset();
    doRun();
  }


  public boolean touchEvent(GGTouch touch)
  {
    if (roundFinished)
    {
      reset();
      return true;
    } 
    
    Location loc = toLocationInGrid(touch.getX(), touch.getY());

    if (placedPegs == 4 && loc.x == 0 && loc.y == currentRow)
    { // click on evalButton -> evaluate
      int[] guess = new int[4];
      for (int i = 0; i < 4; i++)
        guess[i] = getOneActorAt(new Location(1 + i, currentRow)).getIdVisible();
      evaluateGuess(guess);
    }

    if (loc.y == currentRow && loc.x > 0 && loc.x < 5)
    {
      if (getOneActorAt(loc) == null)
      {
        this.addActor(new Peg(), loc);
        placedPegs++;
        if (placedPegs == 4)
        { // show evaluate button
          addActor(new EvaluateButton(), new Location(0, currentRow));
        }
      }
      else
      {  
        getOneActorAt(loc).showNextSprite(); // -> next color
      }
      
    }
    refresh();
    return true;
  }

  public void reset()
  {
    double resizeFactor = this.getCellSize()/58.0;
    setAutoZoom(resizeFactor);
    removeAllActors();
    currentRow = this.getNbVertCells() - 1; 
    roundFinished = false;
    for (int i = 0; i < secretCode.length; i++)
      secretCode[i] = (int)(Math.random() * Peg.NbColors);
    marker = new ActiveRowMarker();
    addActor(marker, new Location(0, currentRow));
    refresh();
  }

  private void evaluateGuess(int[] guess)
  {
    int blackPegs = 0, whitePegs = 0;
    for (int i = 0; i < 4; i++)
      if (guess[i] == secretCode[i])
        blackPegs++;
    ArrayList<Integer> alreadyProcessed = new ArrayList<Integer>();
    for (int color : secretCode)
      for (int j = 0; j < 4; j++)
        if (color == guess[j] && !alreadyProcessed.contains(j))
        {
          alreadyProcessed.add(j);
          whitePegs++;
          break;
        }
    whitePegs -= blackPegs;
    showTips(whitePegs, blackPegs);

    if (blackPegs == 4) // got right combination
      finishRound("Correct!");
    else
      currentRow--; //go to next column for next try

    if (currentRow == 0) //no more guesses left
      finishRound("Pattern not found!");

    marker.setLocation(new Location(0, currentRow));
    placedPegs = 0;
    removeActors(EvaluateButton.class);
  }

  private void finishRound(String reason)
  {
    showToast(reason);
    setStatusText("Click to play again");
    removeActor(marker);
    showSolution();
    roundFinished = true;
  }
  

  private void showTips(int whitePegs, int blackPegs)
  {
    for (int i = 0; i < 4; i++)
    {
      if (blackPegs > 0)
      {
        EvalPeg ep = new EvalPeg(0);
        addActor(ep, new Location(0, currentRow));
        ep.turn(90 * i);
        blackPegs--;
      }
      else if (whitePegs > 0)
      {
        EvalPeg ep = new EvalPeg(1);
        addActor(ep, new Location(0, currentRow));
        ep.turn(90 * i);
        whitePegs--;
      }
    }
  }

  private void showSolution()
  {
    int x = 1;
    for (int spriteNr : secretCode)
    {
      Peg peg = new Peg();
      peg.show(spriteNr);
      addActor(peg, new Location(x, 0));
      x++;
    }
  }

  private String printArray(int[] a)
  {
    String result = "";
    for (int b : a)
      result += b + ", ";
    return result;
  }
}

class ActiveRowMarker extends Actor
{	
	public ActiveRowMarker() {
		super("activerowmarker");
	}
}

// ----------class EvalPeg -----------------
class EvalPeg extends Actor
{
  public EvalPeg(int sprite)
  {
    // sprite 0 = black, sprite 1 = white
    super(true, "epeg", 2);
    show(sprite);
  }
}

// -----------class EvaluateButton------------
class EvaluateButton extends Actor
{
  public EvaluateButton()
  {
    super("ebutton");
  }
}

//-------- class Peg-------------------------
class Peg extends Actor
{
  public static final int NbColors = 6;
  public Peg()
  {
    super("peg", NbColors);
  }
} 

