// ApkBlueInstaller.java
/*
Usage: No parameter: Local apk will transfer
Usage: DroidTransfer <urlbin>
where <urlbin> is the full qualified url path to the
Android application (without .apk), e.g.
http://clab1.phbern.ch/jOnline/archive/0/bin/MyAndroid
 */

import java.io.*;
import java.net.*;
import ch.aplu.bluetooth.*;
import javax.swing.*;
import ch.aplu.util.*;
import java.util.*;
import ch.aplu.util.ExitListener;
import com.l2fprod.common.swing.plaf.LookAndFeelAddons;
import javax.bluetooth.RemoteDevice;

public class ApkBlueInstaller implements ExitListener
{
  // ------------- Inner class TimeoutThread ------------
  private class TimeoutThread extends Thread
  {
    private DataOutputStream dos;

    TimeoutThread(DataOutputStream dos)
    {
      this.dos = dos;
    }

    public void run()
    {
      System.out.println("Timeout thread started");
      while (isRunning)
      {
        isTimeout = true;
        ch.aplu.util.Console.delay(transactionCheckDelay);
        if (isTimeout)
        {
          System.out.println("Timeout thread: Timeout detected");
          isRunning = false;
          try
          {
            if (!isTransferSuccessful)
            // If transfer successful, let the server disconnect, when it has
            // finished
            {
              System.out.println("Disconnecting client now...");
              bc.disconnect();
            }
          }
          catch (Exception ex)
          {
          }
        }
      }
      System.out.println("Timeout thread terminated");
    }
  }

  // ------------- Inner class TransferThread ---------------
  private class TransferThread extends Thread
  {
    public void run()
    {
      System.out.println("Start sending file...");
      boolean rc = sendFile(apkFile, dos);
      if (!rc)
        System.out.println("Error while sending file");
      isTransferSuccessful = rc;
      System.out.println("isDone = true");
      isDone = true;
    }
  }

  // ------------- Inner class MyFilter -------------------
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
  // ------------- End of inner classes ---------------------
  //
  private final boolean debug = false;
  private final String VERSION = "1.40";
  private final int transactionCheckDelay = 4000; // in ms
  private final String fs = System.getProperty("file.separator");
  private final String serviceName = "DroidInstall";
  private final String localToolDir = ".jdroidtools";
  private String userHome = System.getProperty("user.home");
  private String absToolDir = userHome + fs + localToolDir;
  private volatile boolean isDone = false;
  private volatile boolean isRunning;
  private volatile boolean isTimeout = false;
  private volatile boolean isTransferSuccessful;
  private int size;
//  private int nbRetry = 0;
//  private final int nbRetryMax = 1;
  private BluetoothClient bc;
  private DataOutputStream dos;
  private ModelessOptionPane mop;
  private File apkFile;
  private String propPath = userHome + fs + "ApkBlueInstaller.properties";
  private File propFile = new File(propPath);
  private Properties prop = new Properties();

  public ApkBlueInstaller(String urlStr)
  {
    // Get server name from properties
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
    
    // Get apk file
    if (debug)
      apkFile = new File("c:/scratch/A.apk");
    else
    {
      if (urlStr.equals(""))  // Local apk
      {
        File defaultDir = null;
        JFileChooser fc = new JFileChooser(defaultDir);
        fc.setDialogTitle("ApkBlueInstaller V" + VERSION + " - Select The APK File To Transfer");
        fc.addChoosableFileFilter(new MyFilter());
        fc.showOpenDialog(null);
        apkFile = fc.getSelectedFile();
        if (apkFile == null)
          System.exit(0);
      }
      else // Server apk
      {
        // Construct qualified filenames
        String absLocalApk = absToolDir + fs + "JDroid.apk";

        File fabsToolDir = new File(fs + absToolDir + fs);  // Escaped for spaces in name
        if (!fabsToolDir.exists())
          fabsToolDir.mkdir();

        String src = urlStr + ".apk";
        System.out.println("Copying from " + src);
        System.out.println("to " + absLocalApk);
        copyFile(src, absLocalApk);
        apkFile = new File(absLocalApk);
      }
    }

    // Ask for server name
    String serverName;
    if (!debug)
    {
      String prompt = "Start DroidInstall App First!\nEnter Smartphone Bluetooth Name:";
      do
      {
        serverName = JOptionPane.showInputDialog(null, prompt, btName);
        if (serverName == null)
          System.exit(0);
      }
      while (serverName.trim().length() == 0);
      serverName = serverName.trim();
    }
    else
      serverName = "aplusam";
    setProperty("BluetoothName", serverName);

    mop = new ModelessOptionPane("Trying to connect to Bluetooth device '"
      + serverName + "'.\nPlease wait a moment...");
    mop.setTitle("ApkBlueInstaller V" + VERSION + " (www.aplu.ch)");
   
    // Try to get device from paired devices database
    //NEW: HACKICKS WAY TO GET REMOTE DEVICE
    RemoteDevice rd = new MyRemoteDevice("549B12E61FF8");
    //BluetoothFinder.searchPreknownDevice(serverName);
    
    if (rd == null)
    {  
      mop.setText("Sorry. Device '"
        + serverName + "' not paired.\nPlease perform Bluetooth device pairing!", false);
      return;
    }  
    else
      mop.setText("Trying to connect to Bluetooth device '"
        + serverName + "'.\nPlease wait a moment...\n" +
        "Device pairing OK. Searching service now...", false);

    bc = new BluetoothClient(rd, serviceName);
    bc.setVerbose(true);
    
    // Connect with timeout
    if (!bc.connect(50))
    {
      String msg = "Connection failed.\n"
        + "Possible reasons:\n"
        + "- DroidInstall App not available (start/restart it)\n"
        + "- Bluetooth devices not paired\n"
        + "- Wrong Smartphone Bluetooth name\n"
        + "- Bluetooth blocked or not enabled (disable/enable it)";
      mop.setText(msg, true);
      return;
    }
    
    mop.setText("Connection established", false);
    dos = new DataOutputStream(bc.getOutputStream());
    new TransferThread().start();

    while (!isDone)
    {
      ch.aplu.util.Console.delay(100);
    }
    if (!isTransferSuccessful)
      mop.setText("Transfer failed for unknown reason.\nPlease restart transfer");
    // Here we may implement a retry operation without Bluetooth search
    // using the address and channel retrieved before
    else
    {
      mop.setText("Transfer finished.\nTotal # of bytes transferred: " + size +
         "\nTerminating in a moment...", false);
      ch.aplu.util.Console.delay(7000);
      System.out.println("Transfer terminated. Exiting now...");
      System.exit(0);
    }
  }

  public void notifyExit()
  {
    bc.disconnect();
    System.exit(0);
  }

  private boolean sendFile(File srcFile, DataOutputStream dos)
  {
    InputStream in = null;
    isRunning = true;
    new TimeoutThread(dos).start();
    size = 0;
    try
    {
      in = new FileInputStream(srcFile);
      byte[] buf = new byte[256];
      int len;
      while ((len = in.read(buf)) > 0)
      {
        dos.write(buf, 0, len);
        isTimeout = false;
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
    String url = "";
    if (args.length == 1)
      url = args[0];
    new ApkBlueInstaller(url.trim());
  }
}
