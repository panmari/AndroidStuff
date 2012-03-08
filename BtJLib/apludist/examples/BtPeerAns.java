// BtPeerAns.java
// Answer Machine, use BtPeerInq or BtPeerInqGidlet as 'Inquiry Machine'

import javax.swing.JOptionPane;
import ch.aplu.bluetooth.*;
import ch.aplu.util.*;

public class BtPeerAns implements BtPeerListener, ExitListener
{
  private final boolean isVerbose = true;
  private BluetoothPeer bp;
  private final String serviceName = "WordInvert";
 
  public BtPeerAns()
  {
    String prompt = "Bluetooth Name?";
    String partnerName;
    do
    {
      partnerName = JOptionPane.showInputDialog(null, prompt);
      if (partnerName == null)
        System.exit(0);
    }
    while (partnerName.trim().length() == 0);
    
    Console c = new Console();
    c.addExitListener(this);

    System.out.println("Trying to connect to '" + partnerName + 
                       "' with service '" + serviceName + "'");
    bp = new BluetoothPeer(partnerName, serviceName, this, isVerbose);
  }

  public void receiveDataBlock(int[] data)
  {
    int size = data.length;
    int[] reply = new int[size];
    for (int i = 0; i < size; i++)
      reply[size - 1 - i] = data[i]; // Reverse word
    bp.sendDataBlock(reply);  // and send it back

    // Show what we did
    String in = "";
    String out = "";
    for (int i = 0; i < size; i++)
      in = in + (char)data[i];
    for (int i = 0; i < size; i++)
      out = out + (char)reply[i];
    System.out.println("Received: " + in + " - Replied: " + out);
  }

  public void notifyConnection(boolean connected)
  {
    if (connected)
      System.out.println("Connection established.");
    else
      System.out.println("Connection lost.");
  }

  // Called when the close button is hit
  public void notifyExit()
  {
    if (bp != null)
    {
      bp.releaseConnection();
      System.exit(0);
    }
  }

  public static void main(String[] args)
  {
    new BtPeerAns();
  }
}


