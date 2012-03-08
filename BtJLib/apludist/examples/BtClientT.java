// BtClientT.java
// Data exchange with BtServer.java
// Same as BtClient.java, but uses a transfer thread

import java.io.*;
import ch.aplu.bluetooth.*;
import javax.swing.JOptionPane;

public class BtClientT
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

  public BtClientT()
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

    int rc;
    do
    {
      rc = JOptionPane.
        showConfirmDialog(null, "Stopping the client?", 
                          "BtClient", JOptionPane.YES_NO_OPTION);
    }
    while (rc != JOptionPane.YES_OPTION);
    bc.disconnect();
    System.exit(0);
  }

  public static void main(String[] args)
  {
    new BtClientT();
  }
}
