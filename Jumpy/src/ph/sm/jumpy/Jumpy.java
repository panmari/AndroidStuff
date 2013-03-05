// AndroidEx25a.java, Artificial horizon

package ph.sm.jumpy;

import ch.aplu.android.*;
import android.graphics.Color;
import static java.lang.Math.*;

public class Jumpy extends GameGrid
{
  private final double r = 100;
  private GGStatusBar status;

  public Jumpy()
  {
    super(WHITE);
    status = addStatusBar(30);
  }

  public void main()
  {
    GGPanel p = getPanel(-110, 110, -110, 110);
    p.setAutoRefreshEnabled(false);
    GGComboSensor comboSensor = GGComboSensor.init(this);
    while (true)
    {
      p.clear(Color.rgb(10, 40, 10));
      drawDisplay(p);
      
      float[] values = comboSensor.getOrientation(0);
      double pitch = 90 + values[4];
      double roll = values[5];
      
      double b = pitch;
      double m = tan(toRadians(roll));
      
      double x1 = (-b * m + sqrt((m * m + 1) * r * r - b * b)) / (m * m + 1);
      double y1 = m * x1 + b;
      double x2 = (-b * m - sqrt((m * m + 1) * r * r - b * b)) / (m * m + 1);
      double y2 = m * x2 + b;
      p.setPaintColor(RED);
      p.setLineWidth(2);
      p.line(x1, y1, x2, y2);
      p.setLineWidth(1);
      p.setPaintColor(WHITE);
         
      status.setText(String.format("Pitch:% 4.1f  Roll:% 4.1f", pitch, roll));
      refresh();
      delay(50);
    }
  }

  private void drawDisplay(GGPanel p)
  {
    PointD p1;
    PointD p2;
    p.circle(new PointD(0, 0), r, false);
    for (int a = 0; a < 360; a += 10)
    {
      p1 = new PointD((r - 10) * cos(toRadians(a)), (r - 10) * sin(toRadians(a)));
      p2 = new PointD(r * cos(toRadians(a)), r * sin(toRadians(a)));
      p.line(p1, p2);
    }
    
    for (int y = -5; y <= 5; y++)
    {
      p1 = new PointD(-20, 10 * y);
      p2 = new PointD(20, 10 * y);
      p.line(p1, p2);
    }
    
    p1 = new PointD(35, 3);
    p2 = new PointD(65, -3);
    p.rectangle(p1, p2, true);

    p1 = new PointD(-35, 3);
    p2 = new PointD(-65, -3);
    p.rectangle(p1, p2, true);
    
    p.circle(new PointD(0, 0), 5, true);
  }
}