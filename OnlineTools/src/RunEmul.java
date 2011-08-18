// RunEmul.java
// Starts the Android Emulator

import java.io.*;
import java.net.*;
import ch.aplu.util.*;

public class RunEmul
{
  private String VERSION = "2.0";
  private String userHome = System.getProperty("user.home");
  private final String fs = System.getProperty("file.separator");
  private final String iconResourcePath = "res/android.png";
  private static URL iconUrl;
  private ModelessOptionPane mop = null;

  public RunEmul()
  {
    ClassLoader loader = getClass().getClassLoader();
    iconUrl = loader.getResource(iconResourcePath);
    String emptyMsg = "                                                                   ";
    mop = new ModelessOptionPane(50, 50, emptyMsg, iconUrl);  
    mop.setTitle("Android Emulator Starter V" + VERSION + " (www.aplu.ch)");
    mop.setText("Starting emulator now...", false);
    String startFolder = userHome + fs + ".jdroidemul" + fs + "tools";
    String cmd = "cmd /c " + startFolder + fs + "a_startemul.bat";
    System.out.println("exec: " + cmd);
    try
    {
      Runtime.getRuntime().exec(cmd, null, new File(startFolder));
    }
    catch (IOException ex)
    {
      mop.setText("Failed to start the emulator.\n(Please install our Slim-Emulator)", false);
      System.out.println("Failed to start emulator");
      return;
    }
    delay(10000);
    System.exit(0);
  }

  private void delay(long timeout)
  {
    try
    {
      Thread.currentThread().sleep(timeout);
    }
    catch (InterruptedException ex)
    {
    }
  }

  public static void main(String[] args)
  {
    new RunEmul();
  }
}
