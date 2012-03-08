// BtRemoteNxt.java
// Get data from remote measuring device (NXT brick in autonomous mode)

import java.io.*;
import ch.aplu.bluetooth.*;
import javax.swing.JOptionPane;
import ch.aplu.util.Console;
import ch.aplu.util.ExitListener;

public class BtRemoteNxt implements ExitListener
{
  private final int channel = 1;  // Fixed Bluetooth channel on NXT
  private BluetoothClient bc;

  public BtRemoteNxt()
  {
    String prompt = "Enter Server's Bluetooth Name";
    String serverName;
    do
    {
      serverName = JOptionPane.showInputDialog(null, prompt);
      if (serverName == null)
        System.exit(0);
    }
    while (serverName.trim().length() == 0);

    Console c = new Console();
    c.addExitListener(this);

    bc = new BluetoothClient(serverName, channel);
    System.out.println("Trying to connect to '" + serverName +
      "' at channel " + channel);
    if (!bc.connect())
    {
      System.out.println("Connection failed");
      return;
    }

    System.out.println("Connection established");
    DataInputStream dis = new DataInputStream(bc.getInputStream());
    DataOutputStream dos = new DataOutputStream(bc.getOutputStream());
    try
    {
      while (true)
      {
        System.out.print("Request data...");
        dos.writeInt(0);
        dos.flush();

        // Blocking, throws IOException
        // when server closes connection or BluetoothClient.disconnect() is called
        int k = dis.readInt();
        System.out.println("Got value: " + k);
        Console.delay(2000);
      }
    }
    catch (IOException ex)
    {
      System.out.println("Connection lost");
    }
    bc.disconnect();
  }

  public void notifyExit()
  {
    bc.disconnect();
    System.exit(0);
  }

  public static void main(String[] args)
  {
    new BtRemoteNxt();
  }
}
