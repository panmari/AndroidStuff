// BtChatServer.java

import java.io.*;
import javax.bluetooth.*;
import ch.aplu.bluetooth.*;

public class BtChatServer extends BtChatQuit
  implements BtListener
{
  private String serviceName;
  private BluetoothServer bs;
  private BufferedReader netIn = null;
  private PrintWriter netOut = null;
  private String line = null;
  private BtChatPanel p;
  private boolean isVerbose;

  public BtChatServer(String serviceName, String msg, boolean isVerbose)
  {
    this.serviceName = serviceName;
    this.isVerbose = isVerbose;
    p = new BtChatPanel(this, "Bluetooth Chat (Server) - Service: " + serviceName);
    bs = new BluetoothServer(serviceName, this, isVerbose);
    p.appendLine(msg);
  }

  public void notifyConnection(RemoteDevice rd, InputStream is, OutputStream os)
  {
    p.enableEntry(true);
    netIn = new BufferedReader(new InputStreamReader(is));
    netOut = new PrintWriter(new OutputStreamWriter(os));
    p.setOutput(netOut);
    try
    {
      p.appendLine("Connected to " + rd.getFriendlyName(false));
    }
    catch (Exception ex)
    {
      if (isVerbose)
        System.out.println("Got exception while retrieving friendly name");
      return;
    }

    try
    {
      while (true)
      {
        if (isVerbose)
          System.out.println("Call blocking readLine()");
        line = netIn.readLine(); // Blocking
        if (isVerbose)
          System.out.println("Returned from blocking readLine(). Got: " + line);
        if (line == null) // Connection lost, end of stream detected
          throw new IOException();
        p.appendLine(line);
      }
    }
    catch (IOException ex)
    {
      if (isVerbose)
        System.out.println("Got IOException");
      p.appendLine("Connection lost.\nListening as server");
    }
    bs.close();
    p.enableEntry(false);
  }

  public void quit()
  {
    bs.cancel();
    System.exit(0);
  }

  public static void main(String args[])
  {
    if (args.length != 2)
    {
      System.out.println("Cmd line args: BtChatServer serviceName [yes/no]\n" +
        "where yes/no turns verbose on/off");
      return;
    }

    boolean verbose = false;
    if (args[1].equals("yes"))
      verbose = true;

    new BtChatServer(args[0], "Waiting for a client", verbose);
  }
}
