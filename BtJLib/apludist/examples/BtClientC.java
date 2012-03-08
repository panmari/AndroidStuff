// BtClientC.java
// Data exchange with BtServer.java
// Same as BtClientT.java, but uses ch.aplu.util.Console

import java.io.*;
import ch.aplu.bluetooth.*;
import javax.swing.JOptionPane;
import ch.aplu.util.Console;
import ch.aplu.util.ExitListener;

public class BtClientC implements ExitListener
{
  // ------------- Inner class TransferThread ---------------
  private class TransferThread extends Thread
  {
    public void run()
    {
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
        System.out.println("readInt() unblocked");
      }
      bc.disconnect();
      System.out.println("Transfer thread terminated");
    }
  }
  // ------------- End of inner class -----------------------

  private final String serviceName = "BtServer";
  private BluetoothClient bc;
  private DataInputStream dis;
  private DataOutputStream dos;

  public BtClientC()
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
    
    bc = new BluetoothClient(serverName, serviceName);
    System.out.println("Trying to connect to '" + serverName + 
                       "' using service '" + serviceName + "'");
    if (!bc.connect())
    {
      System.out.println("Connection failed");
      return;
    }
    
    System.out.println("Connection established");
    dis = new DataInputStream(bc.getInputStream());
    dos = new DataOutputStream(bc.getOutputStream());

    new TransferThread().start();
  }
  
  public void notifyExit()
  {
    bc.disconnect();
    System.exit(0);
  }

  public static void main(String[] args)
  {
    new BtClientC();
  }
}
