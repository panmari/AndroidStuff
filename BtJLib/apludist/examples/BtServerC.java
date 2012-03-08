// BtServerC.java
// Similar to BtServer.java, but using a ch.aplu.util.Console
// Data exchange with BtClient.java

import java.io.*;
import javax.bluetooth.*;
import ch.aplu.bluetooth.*;
import ch.aplu.util.Console;
import ch.aplu.util.ExitListener;

public class BtServerC implements BtListener, ExitListener
{
  private final String serviceName = "BtServer";
  private BluetoothServer bs;
  private int nbConnections = 0;
  private Console c = new Console();

  public BtServerC()
  {
    bs = new BluetoothServer(serviceName, this, false);
    c.addExitListener(this);
    c.println("Waiting for connection offering service '" + serviceName + "'");
  }

  public void notifyConnection(RemoteDevice rd, InputStream is, OutputStream os)
  {
    nbConnections++;
    c.println("# calls: " + nbConnections);
    try
    {
      c.println("Connection established.");
      c.println("Name: " + rd.getFriendlyName(false));
      c.println("Address " + rd.getBluetoothAddress());
    }
    catch (IOException ex)
    {
      c.println("Got exception\nwhile retrieving client info");
      return;
    }

    // Retrieve streams
    DataInputStream dis = new DataInputStream(is);
    DataOutputStream dos = new DataOutputStream(os);

    try
    {
      while (true)
      {
        // Wait for data
        int n = dis.readInt();
        c.println("Got: " + n);

        // Reply inverse
        dos.writeInt(-n);
        dos.flush();  // Don't forget to flush
      }
    }
    catch (IOException ex)
    {
      bs.close();
    }
    c.println("Transfer finished.\nWaiting for next connection");
  }
  
  // Callback when close button is hit
  public void notifyExit()
  {
    bs.close();  // Announce termination to client
    bs.cancel(); // No more client to wait for
    System.exit(0);
  }
  
  public static void main(String[] args)
  {
    new BtServerC();
  }
}


