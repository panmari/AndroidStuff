
import java.net.URL;
import java.io.*;

import ch.aplu.util.ModelessOptionPane;

public class InstallUSB
{
  public enum Status
  {
    INSTALLING, SUCCESS, ERROR
  }
  private Status state = Status.INSTALLING;
  private final String fs = System.getProperty("file.separator");
  private final String version = "0.90";
  private URL iconUrl;
  private final String tempDir = System.getProperty("java.io.tmpdir");
  private ModelessOptionPane mop = null;
  private final String iconResourcePath = "res/android.png";
  private final String TITLE = "Android USB configurator V" + version
    + " (www.aplu.ch)";
  private final String os = System.getProperty("os.name");
  private final String zipPath = tempDir + fs + "android_usb_Linux.zip";
  private final String scriptPath = tempDir + fs + "android_usb.sh";
  private static String dlURL; 

  public InstallUSB()
  {
    ClassLoader loader = getClass().getClassLoader();
    iconUrl = loader.getResource(iconResourcePath);
    String msg;
    if (os.equals("Linux"))
      msg = "Installing USB devices requires administrator rights.\n"
        + "You will be asked for the administrator password...";
    else
      msg = "Installation for Linux platform only.";
    mop = new ModelessOptionPane(50, 50, msg, iconUrl);
    mop.showTitle(TITLE);
    delay(3000);
    if (!os.equals("Linux"))
      System.exit(0);

    try {
    	Downloader.copyFile(dlURL, zipPath);
		Unzipper.unzip(zipPath, tempDir);
	} catch (IOException e) {
		 mop.setText("Download/Extracting failed. \nTerminating now...", false);
		 System.out.println(e.getMessage());
	     delay(4000);
	     System.exit(0);
	}
    if (!new File(scriptPath).exists())
    {
      mop.setText("Can't find shell script to execute.\nTerminating now...", false);
      delay(4000);
      System.exit(0);
    }
    mop.setText("Installing now. Please wait...\n(Click cancel to quit.)", false);
    try
    {
      runCommand("gksudo " + scriptPath, new File(tempDir));
    }
    catch (IOException ex)
    {
      System.out.println("Failed to spawn script '" + scriptPath + "'");
      return;
    }
    while (state == Status.INSTALLING)
    {
      delay(10);
    }
    if (state == Status.ERROR)
      mop.setText("Can't install.\nPossibly wrong administrator password.", false);
    else
      mop.setText("Installation successful. Please reconnect\nyour smartphone to the USB port.");
    delay(6000);
    System.exit(0);
  }

  // ========================= runCommand() ==========================
  public void runCommand(String cmd, File fwrkDir) throws IOException
  {
    final Process proc = Runtime.getRuntime().exec(cmd, null, fwrkDir);

    // ----------------------- Input Thread ------------------------
    new Thread()
    {
      public void run()
      {
        InputStream is = proc.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String str = "";
        while (true)
        {
          try
          {
            str = br.readLine();
          }
          catch (IOException ex)
          {
            System.out.println("Exception in E readLine: \n" + ex);
            return;
          }
          if (str != null)
          {
            System.out.println("is: " + str);
            state = Status.SUCCESS;
          }
        }
      }
    }.start();

    // ----------------------- Error Thread ------------------------
    new Thread()
    {
      public void run()
      {
        InputStream is = proc.getErrorStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String str = "";
        while (true)
        {
          try
          {
            str = br.readLine();
          }
          catch (IOException ex)
          {
            System.out.println("Exception in E readLine: \n" + ex);
            return;
          }
          if (str != null)
          {
            System.out.println("es: " + str);
            state = Status.ERROR;
          }
        }
      }
    }.start();
    try
    {
      proc.waitFor();
    }
    catch (InterruptedException ex)
    {
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

	public static void main(String[] args) {
		/*
		 * if (args.length != 1) {
		 * System.out.println("Provide one argument: Url to the zip file");
		 * return; }
		 */
		// zipUrl = args[0];
		// SM debug:
		dlURL = "http://clab1.phbern.ch/jOnline/jdroidtoolsLinux/android_usb_Linux.zip";
		new InstallUSB();
	}
}
