import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import ch.aplu.util.Cleanable;
import ch.aplu.util.ModelessOptionPane;
import ch.aplu.util.Monitor;

public class udevConfig implements Cleanable {

	private static final String fs = System.getProperty("file.separator");
	private static final String tempDir = System.getProperty("java.io.tmpdir"); 
	private final static String version = "BETA";
	private static URL iconUrl;
	private ModelessOptionPane mop = null;
	private static String iconResourcePath = "res/android.png";
	private final String TITLE = "Android Udev configurator V" + version
			+ " (www.aplu.ch)";
	private final String os = System.getProperty("os.name");
	private final String ruleName = "95-android.rules";
	private final String udevPath = "/etc/udev/rules.d";
	// Different Vendor IDs in the following order:
	// HTCold, HTCnew, Samsung, Motorola, LG, SonyEricson
	private final String[] vendorIDs = { "0bb4", "18d1", "04e8", "22b8",
			"1004", "0fce" };

	public udevConfig() {
		ClassLoader loader = getClass().getClassLoader();
		iconUrl = loader.getResource(iconResourcePath);
		String emptyMsg = "                                       "
				+ "                                       ";
		mop = new ModelessOptionPane(50, 50, emptyMsg, iconUrl);
		mop.addCleanable(this);
		mop.showTitle(TITLE);
		String rules = "";
		for (int i = 0; i < vendorIDs.length; i++) {
			rules += "SUBSYSTEM==\"usb\", SYSFS{idVendor}==\"" + vendorIDs[i]
					+ "\", MODE=\"0666\"";
			if (i != vendorIDs.length - 1)
				rules += "\n"; // add newline if not last item
		}

		String tempFile = tempDir + fs + ruleName;
		String finalFile = udevPath + fs + ruleName;
		writeToFile(tempFile, rules);
		mop.setText("Please enter your password when prompted");
		String[] cmds = { "gksudo mv " + tempFile + " " + finalFile,
				"gksudo chown root:root " + finalFile,
				"gksudo restart udev;"};
		try {
			runCommand(cmds, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mop.setText("Enjoy.", false);
		delay(4000);
		System.exit(0);
	}

	public void runCommand(String[] cmds, File fwrkDir) throws IOException {
		/*
		 * Warning: How to circumvent hanging forever in Runtime.exec(): If the
		 * program you launch produces output or expects input, ensure that you
		 * process the input and output streams, even if you don't use them BUT
		 * IN THIS CASE, consuming the data may bring the emulator to hang...
		 */
		
		for (String cmd: cmds) {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(cmd, null, fwrkDir);
			// Consume the error stream, in order to terminate the process
	
			String str;
			InputStream istr = proc.getErrorStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(istr));
			while ((str = br.readLine()) != null) {
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
			try {
				proc.waitFor();
			} catch (InterruptedException e) {
				System.out.println("Error in exec(). Process was interrupted");
			}
		}
	}

	private void writeToFile(String filePath, String msg) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(filePath);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(msg);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		new udevConfig();
	}

	private void delay(long timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException ex) {
		}
	}

	@Override
	public void clean() {
		System.exit(1);
	}
}
