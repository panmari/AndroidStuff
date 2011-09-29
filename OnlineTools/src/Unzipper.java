import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Unzipper {
	public static void unzip(String zipFileName, String destFolder)
			throws IOException
	// Precondition: root must contain at least one file
	{
		String os = System.getProperty("os.name");
		String fs = System.getProperty("file.separator");

		System.out.println("Unzipping " + zipFileName);
		ZipFile zipFile = null;
		InputStream inputStream = null;

		File inputFile = new File(zipFileName);
		try {
			// Wrap the input file with a ZipFile to iterate through
			// its contents
			zipFile = new ZipFile(inputFile);
			Enumeration<? extends ZipEntry> oEnum = zipFile.entries();
			while (oEnum.hasMoreElements()) {
				ZipEntry zipEntry = oEnum.nextElement();
				if (zipEntry.isDirectory()) {
					File destDirFile = new File(destFolder + fs
							+ zipEntry.getName());
					destDirFile.mkdirs();
				} else {
					File destFile = new File(destFolder + fs
							+ zipEntry.getName());
					inputStream = zipFile.getInputStream(zipEntry);
					write(inputStream, destFile);
					// set executable permission on Linux:
					destFile.setExecutable(true);
				}
			}
		} catch (IOException ioException) {
			throw ioException;
		} finally {
			try {
				if (zipFile != null)
					zipFile.close();
				if (inputStream != null)
					inputStream.close();
			} catch (IOException ex) {
				System.out.println("Can't cleanup unzipper");
			}
		}
	}

	public static void write(InputStream inputStream, File fileToWrite)
			throws IOException {
		BufferedInputStream buffInputStream = new BufferedInputStream(
				inputStream);
		FileOutputStream fos = new FileOutputStream(fileToWrite);
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		int byteData;
		while ((byteData = buffInputStream.read()) != -1)
			bos.write((byte) byteData);
		bos.close();
		fos.close();
		buffInputStream.close();
	}
}
