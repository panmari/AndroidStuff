// BtPeerInq.java
// Inquiry Machine, use BtPeerAns as 'Answer Machine'

import javax.swing.*;
import ch.aplu.bluetooth.*;
import ch.aplu.util.*;

public class BtPeerInq implements BtPeerListener
{
  private BluetoothPeer bp;
  private final String partnerName = "NB1";
  private final String serviceName = "WordInvert";
  private ModelessOptionPane mop;
  private String prompt = "Enter a word";
  private volatile boolean isConnected = false;

  public BtPeerInq(boolean isVerbose)
  {
    mop = new ModelessOptionPane("Trying to connect to '" + partnerName + "'" +
      " using service '" + serviceName + "'\nPlease wait...", null);
    mop.setTitle("Word Inverser (Inquiry Machine)");

    bp = new BluetoothPeer(partnerName, serviceName, this, isVerbose);
    if (!bp.isConnected())  // We are a server, wait for a connection
      Monitor.putSleep();
    isConnected = true;
    execute();
  }

  private void execute()
  {
    prompt = "Enter a line of text";
    String value;
    while (true)
    {
      do
      {
        value = JOptionPane.showInputDialog(null, prompt);
        if (!isConnected)
          prompt = "Connection lost";
        if (value == null)
        {
          bp.releaseConnection();
          return;
        }
      }
      while (value.trim().length() == 0);
      if (isConnected)
      {
        int size = value.length();
        int[] data = new int[size];
        for (int i = 0; i < size; i++)
          data[i] = value.charAt(i);
        bp.sendDataBlock(data);
        Monitor.putSleep(); // Wait for reply
      }
    }
  }
  
  public void receiveDataBlock(int[] data)
  {
    String s = "";
    int size = data.length;
    for (int i = 0; i < size; i++)
      s = s + (char)data[i];
    prompt = "Reply: " + s;
    Monitor.wakeUp();
  }

  public void notifyConnection(boolean connected)
  {
    if (connected)
    {
      mop.setText("Connection established.", false);
      delay(2000);
      mop.dispose();
      Monitor.wakeUp();
    }
    else
    {
      isConnected = false;
      bp.releaseConnection();
    }
  }

  private void delay(int time)
  {
    try
    {
      Thread.currentThread().sleep(time);
    }
    catch (InterruptedException ex)
    {
    }
  }

  public static void main(String[] args)
  {
    new BtPeerInq(true); // Verbose mode
  }
}


