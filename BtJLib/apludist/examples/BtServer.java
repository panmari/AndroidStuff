// BtServer.java
// Data exchange with BtClient.java

import java.io.*;
import javax.bluetooth.*;
import ch.aplu.bluetooth.*;
import javax.swing.JOptionPane;

public class BtServer implements BtListener
{
  private final String serviceName = "BtServer";
  private BluetoothServer bs;

  public BtServer()
  {
    bs = new BluetoothServer(serviceName, this, false);
    System.out.println("Waiting for a client exposing service '" + 
                        serviceName + "'");
    int rc;
    do
    {
      rc = JOptionPane.
        showConfirmDialog(null, "Stopping the server?", 
                          "BtServer", JOptionPane.YES_NO_OPTION);
    }
    while (rc != JOptionPane.YES_OPTION);
    bs.close();  // Announce termination to client
    bs.cancel(); // No more client to wait for
    System.exit(0);
  }

  // Callback when a connection is established
  public void notifyConnection(RemoteDevice rd, 
                               InputStream is, OutputStream os)
  {
    // Retrieve streams
    DataInputStream dis = new DataInputStream(is);
    DataOutputStream dos = new DataOutputStream(os);

    try
    {
      while (true)
      {
        // Wait for data
        int n = dis.readInt();
        System.out.println("Got: " + n);

        // Reply inverse
        dos.writeInt(-n);
        dos.flush();  // Don't forget to flush
      }
    }
    catch (IOException ex)
    {
      bs.close();
    }
    System.out.println("Transfer finished.\n" +
                       "Waiting for the next client");
  }
  
  public static void main(String[] args)
  {
    new BtServer();
  }
}
