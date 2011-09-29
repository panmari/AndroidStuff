import java.net.URL;
import java.io.*;

import ch.aplu.util.ModelessOptionPane;

public class InstallUSB {
	public enum Status {
		INSTALLING, SUCCESS, ERROR
	}

	private Status state = Status.INSTALLING;
	private final String fs = System.getProperty("file.separator");
	private final String version = "1.1";
	private URL iconUrl;
	private ModelessOptionPane mop = null;
	private final String iconResourcePath = "res/android.png";
	private final String TITLE = "Android USB configurator V" + version
			+ " (www.aplu.ch)";
	private final String os = System.getProperty("os.name");
	private final String zipName = "android_usb_Linux.zip";
	private final String scriptName = "android_usb.sh";
	private static String dlURL;

	public InstallUSB() {
		ClassLoader loader = getClass().getClassLoader();
		iconUrl = loader.getResource(iconResourcePath);
		String msg;
		if (os.equals("Linux"))
			msg = "Installing USB devices requires sudo rights.\n"
					+ "You will be asked for your sudo password...";
		else
			msg = "Installation for Linux platform only.";
		mop = new ModelessOptionPane(50, 50, msg, iconUrl);
		mop.showTitle(TITLE);
		SystemTools.delay(3000);
		if (!os.equals("Linux"))
			System.exit(0);

		File tempDir = null;
		String zipPath = "", scriptPath = "";
		
		try {
			tempDir = SystemTools.createTempDirectory();
			zipPath = tempDir + fs + zipName;
			scriptPath = tempDir + fs + scriptName;
			Downloader.copyFile(dlURL, zipPath);
			Unzipper.unzip(zipPath, tempDir);
		} catch (IOException e) {
			mop.setText("Download/Extracting failed. \nTerminating now...",
					false);
			System.out.println(e.getMessage());
			SystemTools.delay(4000);
			System.exit(0);
		}
		mop.setText("Installing now. Please wait...\n(Click cancel to quit.)",
				false);
		try {
			runCommand("gksudo " + scriptPath, tempDir);
		} catch (IOException ex) {
			System.out.println("Failed to spawn script '" + scriptPath + "'");
			return;
		}

		while (state == Status.INSTALLING) {
			SystemTools.delay(10);
		}
		if (state == Status.ERROR)
			mop.setText("Can't install.\nPossibly wrong administrator password.", false);
		else
			mop.setText("Installation successful. Please reconnect\nyour smartphone to the USB port.");
		if(SystemTools.deleteDir(tempDir))
			System.out.println("Cleanup successful");
		SystemTools.delay(6000);
		System.exit(0);
	}
	
	
	public void runCommand(String cmd, File fwrkDir) throws IOException {
		final Process proc = Runtime.getRuntime().exec(cmd, null, fwrkDir);

		// ----------------------- Input Thread ------------------------
		new Thread() {
			public void run() {
				InputStream is = proc.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String str = "";
				while (true) {
					try {
						str = br.readLine();
					} catch (IOException ex) {
						System.out.println("Exception in E readLine: \n" + ex);
						return;
					}
					if (str != null) {
						System.out.println("is: " + str);
						state = Status.SUCCESS;
					}
				}
			}
		}.start();

		// ----------------------- Error Thread ------------------------
		new Thread() {
			public void run() {
				InputStream is = proc.getErrorStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String str = "";
				while (true) {
					try {
						str = br.readLine();
					} catch (IOException ex) {
						System.out.println("Exception in E readLine: \n" + ex);
						return;
					}
					if (str != null) {
						System.out.println("es: " + str);
						state = Status.ERROR;
					}
				}
			}
		}.start();
		try {
			proc.waitFor();
		} catch (InterruptedException ex) {
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
