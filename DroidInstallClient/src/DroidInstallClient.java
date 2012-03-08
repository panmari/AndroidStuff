// DroidInstallClient.java

import java.io.*;
import ch.aplu.bluetooth.*;
import javax.swing.*;
import ch.aplu.util.*;
import java.util.*;
import ch.aplu.util.ExitListener;
import com.l2fprod.common.swing.plaf.LookAndFeelAddons;

public class DroidInstallClient implements ExitListener
{
  // ------------- Inner class TransferThread ---------------
  private class TransferThread extends Thread
  {
    public void run()
    {
      System.out.println("Start sending file...");
      boolean rc = sendFile(apkFile, dos);
      if (!rc)
        System.out.println("Error while sending file");
      ch.aplu.util.Console.delay(7000);
      //    bc.disconnect();  // Client disconnects
      System.out.println("Transfer terminated. Exiting now...");
      System.exit(0);
    }
  }

  private class MyFilter extends javax.swing.filechooser.FileFilter
  {
    public boolean accept(File file)
    {
      String filename = file.getName();
      return filename.endsWith(".apk");
    }

    public String getDescription()
    {
      return "Android Apps (*.apk)";
    }
  }
  // ------------- End of inner class -----------------------
  //
  private final String VERSION = "1.1";
  private final String serviceName = "DroidInstall";
  private BluetoothClient bc;
  private DataOutputStream dos;
  private ModelessOptionPane mop;
  private File apkFile;
  private String fs = System.getProperty("file.separator");
  private String userHome = System.getProperty("user.home");
  private String propPath = userHome + fs + "BluetoothInstall.properties";
  private File propFile = new File(propPath);
  private Properties prop = new Properties();

  public DroidInstallClient()
  {
    Properties prop = loadProperties();
    String btName = "";
    if (prop != null)
    {
      btName = prop.getProperty("BluetoothName");
      if (btName == null)
        btName = "";
      else
        btName = btName.trim();
    }
    
    File defaultDir = null;
    JFileChooser fc = new JFileChooser(defaultDir);
    fc.setDialogTitle("DroidInstallClient V" + VERSION + " - Select The APK File To Transfer");
    fc.addChoosableFileFilter(new MyFilter());
    fc.showOpenDialog(null);
    apkFile = fc.getSelectedFile();
    if (apkFile == null)
      System.exit(0);
    String prompt = "Enter Server's Bluetooth Name";
    String serverName;
    do
    {
      serverName = JOptionPane.showInputDialog(null, prompt, btName);
      if (serverName == null)
        System.exit(0);
    }
    while (serverName.trim().length() == 0);
    serverName = serverName.trim();
    setProperty("BluetoothName", serverName);

    bc = new BluetoothClient(serverName, serviceName);
    bc.setVerbose(true);
    mop = new ModelessOptionPane("Trying to connect to Bluetooth device '"
      + serverName + "'.\nPlease wait a moment...");
    mop.setTitle("DroidInstallClient V" + VERSION);
    if (!bc.connect())
    {
      String msg = "Connection failed.\n"
        + "Possible reasons:\n"
        + "- DroidInstall App not available (start/restart it)\n"
        + "- Bluetooth devices not paired\n"
        + "- Wrong smartphone Bluetooth name\n"
        + "- Bluetooth blocked or not enabled (disable/enable it)";
      mop.setText(msg, true);
      return;
    }

    mop.setText("Connection established", false);
    dos = new DataOutputStream(bc.getOutputStream());
    new TransferThread().start();
  }

  public void notifyExit()
  {
    bc.disconnect();
    System.exit(0);
  }

  private boolean sendFile(File srcFile, DataOutputStream dos)
  {
    InputStream in = null;
    int size = 0;
    try
    {
      in = new FileInputStream(srcFile);
      byte[] buf = new byte[256];
      int len;
      while ((len = in.read(buf)) > 0)
      {
        dos.write(buf, 0, len);
        size += len;
        if (size % 16384 == 0)
          mop.setText("Processing...\n# bytes transferred: " + size, false);
      }
      dos.flush();
    }
    catch (IOException ex)
    {
      return false;
    }
    finally
    {
      try
      {
        in.close();
      }
      catch (Exception ex)
      {
      }
      try
      {
        dos.close();
      }
      catch (Exception ex)
      {
      }
    }
    mop.setText("Transfer finished.\nTotal # of bytes transferred: " + size, false);
    return true;
  }

  private Properties loadProperties()
  {
    // Return null, if error
    if (!propFile.exists())
    {
      try
      {
        propFile.createNewFile();
      }
      catch (IOException ex)
      {
        return null;
      }
    }
    FileInputStream fis = null;
    try
    {
      fis = new FileInputStream(propFile);
      prop.load(fis);
    }
    catch (IOException ex)
    {
      return null;
    }
    finally
    {
      try
      {
        fis.close();
      }
      catch (Exception ex)
      {
      }
    }
    return prop;
  }

  private void setProperty(String key, String value)
  {
    if (prop == null)
      return;
    prop.setProperty(key, value);
    try
    {
      FileOutputStream fos = new FileOutputStream(propFile);
      prop.store(fos, null);
      fos.close();
    }
    catch (IOException ex)
    {
      System.out.println("Can't set property " + key);
    }
  }

  public static void main(String[] args)
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      LookAndFeelAddons.setAddon(LookAndFeelAddons.getBestMatchAddonClassName());
    }
    catch (Exception e)
    {
    }
    new DroidInstallClient();
  }
}
