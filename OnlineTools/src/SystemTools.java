import java.io.File;
import java.io.IOException;

public class SystemTools {

	public static void delay(long timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException ex) {
		}
	}

	public static File createTempDirectory() throws IOException {
		final File temp;

		temp = File.createTempFile("android_usb_install_", Integer.toString((int)(Math.random()*1000)));

		if (!(temp.delete())) {
			throw new IOException("Could not delete temp file: "
					+ temp.getAbsolutePath());
		}

		if (!(temp.mkdir())) {
			throw new IOException("Could not create temp directory: "
					+ temp.getAbsolutePath());
		}

		return (temp);
	}
	
	/**
	 * Deletes recursively a directory.
	 * @return true, if success
	 */
	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}

}
