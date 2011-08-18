// InstallEmul.java
// Installs Android Emulator

import java.util.*;
import java.util.zip.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class InstallEmul
{
  private String VERSION = "2.0";
  private String userHome = System.getProperty("user.home");
  private final String fs = System.getProperty("file.separator");
  private static String zipUrl; 
  private final String iconResourcePath = "res/android.png";
  //private ProgressPane //mop = null;
  private static URL iconUrl;
  private final int zipFileSize = 58000000; // in bytes
  private final String os = System.getProperty("os.name");

  public InstallEmul()
  {
    String tmpDir = System.getProperty("java.io.tmpdir");
    final String zipPath;
    if (os.equals("Linux")) {
    	//change download path:
    	 zipUrl = zipUrl.replace(".zip", "_linux.zip");
    	 //Linux braucht hier noch ein fs:
    	 zipPath = tmpDir + fs + "jdroidemul.zip";
    }
    else zipPath = tmpDir + "jdroidemul.zip";
    
    ClassLoader loader = getClass().getClassLoader();
    iconUrl = loader.getResource(iconResourcePath);
    String emptyMsg = "                                                     "
      + "                                             ";
    //mop = new ProgressPane(50, 50, emptyMsg, iconUrl);
    //mop.setTitle("Android Emulator Installer V" + VERSION + " (www.aplu.ch)");

    String emulFolder = userHome + fs + ".jdroidemul";
    if (!createFolder(emulFolder))
    {
      //mop.setText("Can't create folder " + emulFolder);
      return;
    }

    String msg = "Downloading distribution zip.  Be patient...";
    //mop.setText(msg, false);
    System.out.println("Downloading distribution zip " + zipUrl + "...");
    Thread t = new Thread()
    {
      public void run()
      {
        copyFile(zipUrl, zipPath);
      }
    };
    t.start();
    try
    {
      t.join();
    }
    catch (InterruptedException ex)
    {}
    System.out.println("Done. Installing now...");
    //mop.setText("Download finished. Installing now...", false);
    try
    {
      unzip(zipPath, emulFolder);
    }
    catch (IOException ex)
    {
      System.out.println("Installation failed");
      //mop.setText("Installation failed", false);
      return;
    }

    String folder = userHome + fs + ".android";
    if (!createFolder(folder))
    {
      //mop.setText("Can't create folder " + folder);
      return;
    }

    folder = userHome + fs + ".android" + fs + "avd";
    if (!createFolder(folder))
    {
      //mop.setText("Can't create folder " + folder);
      return;
    }

    folder = userHome + fs + ".android" + fs
      + "avd" + fs + "Slim-Emulator.avd";
    if (!createFolder(folder))
    {
      //mop.setText("Can't create folder " + folder);
      return;
    }

    // Create Slim-Emulator.ini
    // SM: only needed on Linux
    String slimEmulator_ini = userHome + fs + ".android"
      + fs + "avd" + fs + "Slim-Emulator.ini";

    System.out.println("Creating " + slimEmulator_ini);
    PrintWriter out = null;
    try
    {
      out = new PrintWriter(
        new BufferedWriter(new FileWriter(slimEmulator_ini)));
      out.println("target=android-8");
      out.println("path=" + userHome + fs + ".android" + fs + "avd"
        + fs + "Slim-Emulator.avd");
    }
    catch (IOException ex)
    {
      System.out.println("Can't create " + slimEmulator_ini);
    }

    try
    {
      out.close();
    }
    catch (Exception ex)
    {
    }

    // Create config.ini
    String config_ini = userHome + fs + ".android"
      + fs + "avd" + fs + "Slim-Emulator.avd" + fs + "config.ini";

    System.out.println("Creating " + config_ini);
    out = null;
    try
    {
      out = new PrintWriter(
        new BufferedWriter(new FileWriter(config_ini)));
      out.println("hw.lcd.density=160");
      out.println("skin.name=HVGA");
      out.println("skin.path=platforms" + fs + "android-8" + fs
        + "skins" + fs + "HVGA");
      out.println("hw.cpu.arch=arm");
      out.println("abi.type=armeabi");
      out.println("vm.heapSize=24");
      out.println("snapshot.present=true");
      out.println("image.sysdir.1=platforms" + fs + "android-8" + fs
        + "images" + fs);
    }
    catch (IOException ex)
    {
      System.out.println("Can't create " + slimEmulator_ini);
    }

    try
    {
      out.close();
    }
    catch (Exception ex)
    {
    }
    System.out.println("Done");
    //mop.setText("Installation successful...", false);
    delay(2000);
  }

  private boolean createFolder(String folder)
  {
    File folderFile = new File(folder);
    if (!folderFile.exists())
    {
      System.out.print("Need to create  " + folder + "...");
      boolean rc = folderFile.mkdir();
      if (rc)
        System.out.println("ok");
      else
      {
        System.out.println("failed");
        return false;
      }
    }
    else
      System.out.println("Use existing folder  " + folder);
    return true;
  }

  public void unzip(String zipFileName, String destFolder) throws IOException
  // Precondition: root must contain at least one file 
  {
    System.out.println("Unzipping " + zipFileName);
    ZipFile zipFile = null;
    InputStream inputStream = null;

    File inputFile = new File(zipFileName);
    try
    {
      // Wrap the input file with a ZipFile to iterate through
      // its contents
      zipFile = new ZipFile(inputFile);
      Enumeration<? extends ZipEntry> oEnum = zipFile.entries();
      while (oEnum.hasMoreElements())
      {
        ZipEntry zipEntry = oEnum.nextElement();
        if (zipEntry.isDirectory())
        {
          File destDirFile = new File(destFolder + fs + zipEntry.getName());
          destDirFile.mkdirs();
        }
        else
        {
          File destFile = new File(destFolder + fs + zipEntry.getName());
          inputStream = zipFile.getInputStream(zipEntry);
          write(inputStream, destFile);
          //set executable permission on Linux:
          if (os.equals("Linux")) {
	          if (zipEntry.getName().contains("emulator") ||
	        		  zipEntry.getName().contains("adb"))
	        	  destFile.setExecutable(true);
          }
        }
      }
    }
    catch (IOException ioException)
    {
      throw ioException;
    }
    finally
    {
      try
      {
        if (zipFile != null)
          zipFile.close();
        if (inputStream != null)
          inputStream.close();
      }
      catch (IOException ex)
      {
        System.out.println("Can't cleanup unzipper");
      }
    }
  }

  public static void write(InputStream inputStream, File fileToWrite)
    throws IOException
  {
    BufferedInputStream buffInputStream = new BufferedInputStream(inputStream);
    FileOutputStream fos = new FileOutputStream(fileToWrite);
    BufferedOutputStream bos = new BufferedOutputStream(fos);

    int byteData;
    while ((byteData = buffInputStream.read()) != -1)
      bos.write((byte)byteData);
    bos.close();
    fos.close();
    buffInputStream.close();
  }

  // ========================= copyFile() ===========================
  private void copyFile(String srcUrl, String dstFile)
  // copy file 'srcUrl' to local file 'dstFile'
  // e.g. getFile("http://clab1.phbern.ch/Ex1.bin", "c:\scratch\Ex1.bin")
  {
       System.out.println("Src: " + srcUrl + " Dst: " + dstFile);

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
      int value = 0;
      int size = zipFileSize; // bytes
      while ((count = inp.read(buff)) != -1)
      {
        out.write(buff, 0, count);
        value += count;
        //mop.setBarValue((int)(100.0 * value / size));
      }
    }
    catch (IOException ex)
    {
      System.out.println("Error while copying " + srcUrl);
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

  private void delay(long timeout)
  {
    try
    {
      Thread.currentThread().sleep(timeout);
    }
    catch (InterruptedException ex)
    {
    }
  }

  public static void main(String[] args)
  {
	  /*
    if (args.length != 1)
    {
      System.out.println("Provide one argument: Url to the zip file");
      return;
    }
    */
    //zipUrl = args[0];
    //SM debug:
    zipUrl = "http://clab1.phbern.ch/jOnline/jdroidemul/jdroidemul.zip";
    new InstallEmul();
  }
}
