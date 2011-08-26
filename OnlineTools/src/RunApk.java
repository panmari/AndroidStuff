// RunApk.java

import java.io.*;
import java.net.*;
import ch.aplu.util.*;

public class RunApk implements Cleanable
{
  private final static String version = "2.5";
  private final String serverToolDir = "jdroidtools";
  private final String localToolDir = ".jdroidtools";
  private final String userHome = System.getProperty("user.home");
  private final String fs = System.getProperty("file.separator");
  // Files in android server tools directory must be exlicitely 
  // enumerated here and must exist in the server directory
  private String[] toolFiles =
  {
    "a_deb.bat", "a_start.bat", "adb.exe",
    "AdbWinApi.dll", "AdbWinUsbApi.dll"
  };
  
  private String[] toolFilesLinux = { "adb" };
  private static URL iconUrl;
  private static String iconResourcePath = "res/android.png";
  private ModelessOptionPane mop = null;
  private final String TITLE = "Android Apk Installer V" + version + " (www.aplu.ch)";
  private final String os = System.getProperty("os.name");

  // ========================= Ctor =================================
  public RunApk(String[] args)
  {
    if (args.length != 2)
    {
      System.out.println(
        "RunApk Version " + version + "\n"
        + "Usage: RunApk <urlbin> package_name\n"
        + "  where <urlbin> is the full qualified url path to the\n"
        + "  Android application (without -debug, without .apk), e.g.\n"
        + "  http://clab1.phbern.ch/jOnline/archive/5/bin/Ex01");
      return;
    }

    System.out.println("RunApk V" + version);
    ClassLoader loader = getClass().getClassLoader();
    iconUrl = loader.getResource(iconResourcePath);
    String emptyMsg = "                                       "
                    + "                                       ";
    mop = new ModelessOptionPane(50, 50, emptyMsg, iconUrl);
    mop.addCleanable(this);
    mop.showTitle(TITLE);
    String binUrl = args[0].trim();  // Trim
    binUrl = binUrl.replace("\\", "/");  // Change \ to /
    if (binUrl.charAt(binUrl.length() - 1) == '/')  // Remove trailing /
      binUrl = binUrl.substring(0, binUrl.length() - 1);
    int index = 0;
    for (int i = 0; i < 4; i++)
      index = binUrl.indexOf('/', index + 1);
    String serverHome = binUrl.substring(0, index);
    System.out.println("Server home: " + serverHome);
    int lastIndex = binUrl.lastIndexOf('/', binUrl.length() - 1);
    String apkName = binUrl.substring(lastIndex + 1).trim();  // Extract apk name
    String packageName = args[1].trim();  // Get package name

    // Construct qualified filenames
    String absToolDir = userHome + fs + localToolDir;
    String absLocalApk = absToolDir + fs + apkName + ".apk";

    File fabsToolDir = new File(fs + absToolDir + fs);  // Escaped for spaces in name
    if (!fabsToolDir.exists())
      fabsToolDir.mkdir();

    mop.setText("Copying apk from server...", false);
    System.out.println("Copying from server: " + binUrl + "-debug.apk...");
    // Copy apk from server
    boolean rc = copyFile(binUrl + "-debug.apk", absLocalApk);
    if (!rc)
    {
      mop.setText("File download failed.", false);
      System.out.println("failed");
      return;
    }
    System.out.println("Successful");

    System.out.println("Copying tools from server...");
    String absToolsDir = serverHome + "/" + serverToolDir;
    //SM: change tools (a bit ugly)
    if (os.equals("Linux"))
    	toolFiles = toolFilesLinux;
    for (int i = 0; i < toolFiles.length; i++)
    {
      String srcUrl = absToolsDir + "/" + toolFiles[i];
      String dstPath = absToolDir + fs + toolFiles[i];
      copyFile(srcUrl, dstPath);
      // Adb may already be running and cannot be replaced!
      //SM: set executable
      if (os.equals("Linux"))
    	  new File(dstPath).setExecutable(true);
    }

    System.out.println("Installing...");
    mop.setText("Installing (wait for device)...", false);
    String command = "adb wait-for-device install -r " + apkName + ".apk";
    System.out.println("command: " + command + " in directory: " + fabsToolDir);

    try
    {
        if (os.equals("Linux"))
        	runCommand(fabsToolDir + fs + command, fabsToolDir);
        else runCommand(command, fabsToolDir);
    }
    catch (IOException ex)
    {
      mop.setText("Failed to install on device/emulator.\n"
        + "Check if Android SDK is properly installed", false);
      return;
    }

    System.out.println("starting");
    mop.setText("Starting...", false);
    command = "adb shell am start -n " + packageName + "/." + apkName;
    System.out.println("command: " + command + " in directory: " + fabsToolDir);
    try
    {
    	 if (os.equals("Linux"))
         	runCommand(fabsToolDir + fs + command, fabsToolDir);
         else runCommand(command, fabsToolDir);
    }
    catch (IOException ex)
    {
      mop.setText("Failed to start app.\n"
        + "Check if Android SDK is properly installed");
      return;
    }
    mop.setText("Enjoy.", false);
    delay(4000);
    System.exit(0);
  }

  // ========================= copyFile() ===========================
  private boolean copyFile(String srcUrl, String dstFile)
  // copy file 'srcUrl' to local file 'dstFile'
  // e.g. getFile("http://clab1.phbern.ch/Ex1.bin", "c:\scratch\Ex1.bin")
  {
//    System.out.println("Src: " + srcUrl + " Dst: " + dstFile);

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
      return false;
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
    return true;
  }
 
  // ========================= runCommand() ==========================
  public void runCommand(String cmd, File fwrkDir) throws IOException
  {
    /* Warning:
    How to circumvent hanging forever in Runtime.exec():
    If the program you launch produces output or expects input, 
    ensure that you process the input and output streams, even
    if you don't use them
    BUT IN THIS CASE, consuming the data may bring the emulator to hang...
     */
    Process proc = Runtime.getRuntime().exec(cmd, null, fwrkDir);
    // Consume the error stream, in order to terminate the process

    String str;
    InputStream istr = proc.getErrorStream();
    BufferedReader br = new BufferedReader(new InputStreamReader(istr));
    while ((str = br.readLine()) != null)
    {
      System.out.println(str);
      String first = str.substring(0, 1);
      first = first.toUpperCase();
      str = first + str.substring(1);
      mop.setText(str);
      if (str.substring(0, 5).equals("Error"))
        Monitor.putSleep();
      else
        break;
    }
    try
    {
      proc.waitFor();
    }
    catch (InterruptedException e)
    {
      System.out.println("Error in exec(). Process was interrupted");
    }
  }

  private void delay(long timeout)
  {
    try
    {
      Thread.sleep(timeout);
    }
    catch (InterruptedException ex)
    {
    }
  }

  public void clean()
  {
    System.exit(1);
  }

  // ========================= main =================================
  public static void main(String[] args)
  {
	String[] debugArgs = {"http://clab1.phbern.ch/jOnline/archive/2/bin/MyAndroid", 
			"online.app"};
    new RunApk(debugArgs);
  }
}
