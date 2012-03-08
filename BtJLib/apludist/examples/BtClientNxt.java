// BtClientNxt.java
// Data exchange with BtClient.java running on a Lego NXT brick (autonomous mode)

import java.io.*;
import ch.aplu.bluetooth.*;
import javax.swing.JOptionPane;
import ch.aplu.util.Console;
import ch.aplu.util.ExitListener;

public class BtClientNxt implements ExitListener
{
  private final int channel = 1;  // Fixed Bluetooth channel on NXT
  private BluetoothClient bc;

  public BtClientNxt()
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
      for (int n = 1; n <= 100; n++)
      {
        System.out.println("Send value: " + n);
        dos.writeInt(n);
        dos.flush();

        // Blocking, throws IOException
        // when server closes connection or BluetoothClient.disconnect() is called
        int k = dis.readInt();
        System.out.println("Reply value: " + k);
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
    new BtClientNxt();
  }
}
