import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Version without progress bar
 *
 */
public class Downloader {
	public static void copyFile(String srcUrl, String dstFile) throws IOException
	// copy file 'srcUrl' to local file 'dstFile'
	// e.g. getFile("http://clab1.phbern.ch/Ex1.bin", "c:\scratch\Ex1.bin")
	{
		System.out.println("Src: " + srcUrl + " Dst: " + dstFile);

		File fdstFile = new File(dstFile);
		if (fdstFile.exists())
			fdstFile.delete();

		InputStream inp = null;
		FileOutputStream out = null;

		try {
			URL url = new URL(srcUrl);
			inp = url.openStream();
			out = new FileOutputStream(dstFile);

			byte[] buff = new byte[8192];
			int count;
			while ((count = inp.read(buff)) != -1) {
				out.write(buff, 0, count);
			}
		} catch (IOException ex) {
			throw ex; //don't handle it here
		} finally {
			try {
				if (inp != null)
					inp.close();
				if (out != null)
					out.close();
			} catch (IOException ex) {
			}
		}
	}
}
