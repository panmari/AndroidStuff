// BtChatClient.java

import java.io.*;
import ch.aplu.bluetooth.*;
import ch.aplu.util.*;

public class BtChatClient extends BtChatQuit
{
  private String serverName;
  private int channel;
  private String serviceName;
  protected BluetoothClient bc;
  private BufferedReader netIn = null;
  private PrintWriter netOut = null;
  private String line = null;
  private BtChatPanel p = null;
  private boolean isVerbose;
  private boolean terminate;
  private ModelessOptionPane mop;

  /**
   * Create instance but do not yet connect.
   */
  public BtChatClient(boolean verbose)
  {
    isVerbose = verbose;
    if (isVerbose)
      System.out.println("Verbose enabled");
  }

  /**
   * Create instance and try to connect to given server using given channel.
   */
  public BtChatClient(String serverName, int channel, boolean verbose)
  {
    isVerbose = verbose;
    if (isVerbose)
      System.out.println("Verbose enabled");
    connect(serverName, channel, false);
  }

  /**
   * Create instance and try to connect to given server using given serviceName.
   */
  public BtChatClient(String serverName, String serviceName, boolean verbose)
  {
    isVerbose = verbose;
    if (isVerbose)
      System.out.println("Verbose enabled");
    connect(serverName, serviceName, false);
  }

  public boolean connect(String serverName, String serviceName, boolean terminate)
  {
    this.terminate = terminate;
    return doConnect(serverName, serviceName, -1);
  }

  public boolean connect(String serverName, int channel, boolean terminate)
  {
    this.terminate = terminate;
    return doConnect(serverName, null, channel);
  }

  private boolean doConnect(String serverName, String serviceName, int channel)
  {
    this.serverName = serverName;
    this.serviceName = serviceName;
    this.channel = channel;
 
    mop = new ModelessOptionPane("Trying to connect to '" + serverName + "'. Please wait...");
    mop.setTitle("BtChat (www.aplu.ch)");
    
    if (channel == -1)  // serviceName given
    {
      if (isVerbose)
        System.out.println("Trying to connect to server " + serverName +
          " using service " + serviceName);
      bc = new BluetoothClient(serverName, serviceName);
    }
    else // channel given
    {
      if (isVerbose)
        System.out.println("Trying to connect to server " + serverName +
          " using channel " + channel);
      bc = new BluetoothClient(serverName, channel);
    }
    bc.setVerbose(isVerbose);
    if (!bc.connect())
    {
      if (isVerbose)
        System.out.println("Connection to " + serverName + " failed.");
      mop.dispose();
      return false;
    }
    mop.dispose();
    execute();
    VerboseWriter.close();
    return true;
  }

  private void execute()
  {
    netIn = new BufferedReader(
      new InputStreamReader(bc.getInputStream()));
    netOut = new PrintWriter(
      new OutputStreamWriter(bc.getOutputStream()));

    p = new BtChatPanel(this, "Bluetooth Chat (Client)");
    p.setOutput(netOut);
    p.appendLine("Connection to '" + serverName + "' established");

    p.enableEntry(true);

    try
    {
      while (true)
      {
        if (isVerbose)
          System.out.println("Call blocking readline()");
        line = netIn.readLine(); // Blocking
        if (line == null)  // Connection lost, end of stream detected
          throw new IOException();
        if (isVerbose)
          System.out.println("Returned from blocking readline()");
        p.appendLine(line);
      }
    }
    catch (IOException ex)
    {
      if (isVerbose)
        System.out.println("Got IOException");
      bc.disconnect();
      p.appendLine("Connection lost");
      p.enableEntry(false);
    }
    if (terminate)
    {
      bc.disconnect();
      p.dispose();
    }
  }

  protected void quit()
  {
    bc.disconnect();
    VerboseWriter.close();
    System.exit(0);
  }

  public static void main(String[] args)
  {
    if (args.length != 3)
    {
      System.out.println("Cmd line args: BtChatClient serverName serviceName [yes/no]\n" +
        "           or: BtChatClient server channel [yes/no]\n" +
        "where yes/no turns verbose on/off");
      return;
    }
    boolean verbose = false;
    if (args[2].equals("yes"))
      verbose = true;

    int channel;
    try
    {
      channel = Integer.parseInt(args[1]);
      new BtChatClient(args[0], channel, verbose);
    }
    catch (NumberFormatException ex)  // not an integer
    {
      new BtChatClient(args[0], args[1], verbose);
    }
  }
}
