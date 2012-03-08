// BluetoothInstall.java
// Copy apk from server.
// Name assumed xxx-debug.apk

import ch.aplu.bluetooth.*;
import ch.aplu.util.*;
import ch.aplu.util.ExitListener;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;
import com.l2fprod.common.swing.plaf.LookAndFeelAddons;

public class BluetoothInstall implements ExitListener
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
  // ------------- End of inner class -----------------------
  //
  private final static String version = "1.1 - Feb 2012";
  private final String serviceName = "DroidInstall";
  private BluetoothClient bc;
  private DataOutputStream dos;
  private ModelessOptionPane mop;
  private File apkFile;
  private String fs = System.getProperty("file.separator");
  private final String localToolDir = ".jdroidtools";
  private String userHome = System.getProperty("user.home");
  private String absToolDir = userHome + fs + localToolDir;
  private String propPath = absToolDir + fs + "BluetoothInstall.properties";
  private File propFile = new File(propPath);
  private Properties prop = new Properties();

  public BluetoothInstall(String[] args)
  {
    if (args.length != 1)
    {
      System.out.println(
        "DroidTransfer Version " + version + "\n"
        + "Usage: DroidTransfer <urlbin>\n"
        + "   where <urlbin> is the full qualified url path to the\n"
        + "     Android application (without -debug, without .apk), e.g.\n"
        + "     http://clab1.phbern.ch/jonline/archive/0/bin/MyAndroid");
      return;
    }

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

    String binUrl = args[0].trim();
    int lastIndex = binUrl.lastIndexOf('/', binUrl.length() - 1);
    String apkName = binUrl.substring(lastIndex + 1).trim();


    // Construct qualified filenames
    String absLocalApk = absToolDir + fs + "JDroid.apk";

    File fabsToolDir = new File(fs + absToolDir + fs);  // Escaped for spaces in name
    if (!fabsToolDir.exists())
      fabsToolDir.mkdir();

    String src = binUrl + "-debug.apk";
    System.out.println("Copying from " + src);
    System.out.println("to " + absLocalApk);
    copyFile(src, absLocalApk);

    apkFile = new File(absLocalApk);
    String prompt = "Start DroidInstall App First!\nEnter Smartphone Bluetooth Name:";
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
      + serverName + "'\nPlease wait a moment...");
    mop.setTitle("DroidTransfer V" + version);
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

  // ========================= copyFile() ===========================
  private void copyFile(String srcUrl, String dstFile)
  // copy file 'srcUrl' to local file 'dstFile'
  // e.g. getFile("http://clab1.phbern.ch/Ex1.bin", "c:\scratch\Ex1.bin")
  {
    //   System.out.println("Src: " + srcUrl + " Dst: " + dstFile);

    File fdstFile = new File(dstFile);
    if (fdstFile.exists())
      fdstFile.delete();

    InputStream inp = null;
    FileOutputStream out = null;

    try
    {
      URL url = new URL(srcUrl);
      inp = url.openStream();
      out = new FileOutputStream(dstFile);

      byte[] buff = new byte[8192];
      int count;
      while ((count = inp.read(buff)) != -1)
        out.write(buff, 0, count);
    }
    catch (IOException ex)
    {
      JOptionPane.showMessageDialog(null,
        "Error while copying apk file from server");
      System.exit(1);
    }
    finally
    {
      try
      {
        if (inp != null)
          inp.close();
        if (out != null)
          out.close();
      }
      catch (IOException ex)
      {
      }
    }
  }

  private boolean deleteFiles(File dir)
  {
    String absoluteToolDir = dir.getAbsolutePath();
    if (!dir.exists())
      return true;

    boolean success = true;
    if (dir.isDirectory())
    {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++)
      {
        File fChildren = new File(absoluteToolDir + fs + children[i]);
        if (!fChildren.isDirectory())
        {
          success = fChildren.delete();
          System.out.println("Deleting " + fChildren + "... "
            + (success ? "ok" : "failed"));
        }
      }
    }
    return success;
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
    //SM for debug:
    String[] debugArgs = {"http://clab1.phbern.ch/jOnline/archive/0/bin/AndroidEx2" };
    new BluetoothInstall(debugArgs);
  }
}
