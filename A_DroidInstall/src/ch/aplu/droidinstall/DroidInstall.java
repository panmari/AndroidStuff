// DroidInstall.java
// Bluetooth server waiting for a client communicates with Bluetooth server
// to install APK
/* Permissions needed:
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.INSTALL_PACKAGES" />
*/

package ch.aplu.droidinstall;

import ch.aplu.android.*;
import android.bluetooth.*;
import java.io.*;
import android.os.Environment;
import android.net.Uri;
import android.content.*;
import android.graphics.Color;

public class DroidInstall extends GameGrid
  implements BluetoothConnectionListener
{
  private class TransactionThread extends Thread
  {
    private InputStream is;

    TransactionThread(InputStream is)
    {
      this.is = is;
    }

    public void run()
    {
      L.i("Transaction thread started");
      while (isRunning)
      {
        isTimeout = true;
        delay(transactionCheckDelay);
        showStatus4("# bytes transferred: " + size);
        if (isTimeout)
        {
          L.i("Transaction thread: Timeout detected");
          isRunning = false;
          try
          {
            is.close();  // Will close the channel too
          }
          catch (Exception ex)
          {
          }
        }
      }
      L.i("Transaction thread terminated");
    }
  }
  //
  private final String VERSION = "1.2";
  private final int transactionCheckDelay = 1000; // 1 s
  private BluetoothServer server;
  private final String serviceName = "DroidInstall";
  private File jDroidDir;
  private volatile boolean isRunning;
  private volatile boolean isTimeout = false;
  private final String apkName = "JDroid.apk";
  private String localName;
  private int size;
  private int screenSize;
  private Actor ta0 = null;
  private Actor ta1 = null;
  private Actor ta2 = null;
  private Actor ta3 = null;
  private Actor ta4 = null;
  private Hourglass hourglass;
  private boolean isDone;
  int fontSize;

  public DroidInstall ()
  {
    super(Color.WHITE);
    setScreenOrientation(PORTRAIT);
  }

  public void main()
  {
    screenSize = getNbHorzCells();
    fontSize =  screenSize / 20;
    localName = getBluetoothAdapter().getName();
    getBg().clear(Color.BLUE);
    showStatus0("DroidInstall V" + VERSION);
    showStatus1("(www.aplu.ch)");
    L.i("screenSize: " + screenSize);
    hourglass = new Hourglass();
    hourglass.hide();
    setSimulationPeriod(100);
    addActor(hourglass, new Location(screenSize / 2, screenSize - 80));
    File sdroot = Environment.getExternalStorageDirectory();
    jDroidDir = new File(sdroot, "JDROID");
    if (!jDroidDir.exists())
    {
      showToast("Must create " + jDroidDir);
      boolean rc = jDroidDir.mkdir();
      if (!rc)
      {
        showToast("Directory creation failed.");
        return;
      }
    }
    showStatus2("My name: '" + localName + "'. Waiting for client...");
    server = new BluetoothServer(serviceName, this);
    isDone = false;
    while (!isDone)
      delay(1000);
    L.i("isDone = true");
    doPause();
    hourglass.hide();
    playDisconnectMelody();
    showStatus3("Data in '" + jDroidDir + apkName + "'");
    showStatus4("Starting installation now...");
    delay(4000);   
    installApk();
  }

  private void showStatus0(String msg)
  {
    if (ta0 != null)
      removeActor(ta0);
    ta0 =
      new TextActor(false, msg, Color.WHITE, Color.TRANSPARENT, 2 * fontSize);
    addActor(ta0, new Location(10, 40));
  }

  private void showStatus1(String msg)
  {
    if (ta1 != null)
      removeActor(ta1);
    ta1 =
      new TextActor(false, msg, Color.WHITE, Color.TRANSPARENT, fontSize);
    addActor(ta1, new Location(10, 40 + 2 * fontSize));
  }

  private void showStatus2(String msg)
  {
    if (ta2 != null)
      removeActor(ta2);
    ta2 =
      new TextActor(false, msg, Color.GREEN, Color.TRANSPARENT, fontSize);
    addActor(ta2, new Location(10, 40 + 4 * fontSize));
  }

  private void showStatus3(String msg)
  {
    if (ta3 != null)
      removeActor(ta3);
    ta3 =
      new TextActor(false, msg, Color.GREEN, Color.TRANSPARENT, fontSize);
    addActor(ta3, new Location(10, 40 + 6 * fontSize));
  }

  private void showStatus4(String msg)
  {
    if (ta4 != null)
      removeActor(ta4);
    ta4 =
      new TextActor(false, msg, Color.YELLOW, Color.TRANSPARENT, fontSize);
    addActor(ta4, new Location(10, 40 + 8 * fontSize));
  }

  private void installApk()
  {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.fromFile(
      new File(jDroidDir, apkName)), "application/vnd.android.package-archive");
    startActivity(intent);
    //   finish();
  }

  private void playDisconnectMelody()
  {
    playTone(900, 100);
    delay(400);
    playTone(900, 100);
    delay(400);
  }

  private void playConnectMelody()
  {
    playTone(600, 100);
    delay(400);
    playTone(600, 100);
    delay(400);
  }

  public void notifyConnection(final BluetoothDevice device, final InputStream is, OutputStream os)
  {
    // Executed by server thread. Blocks it until transfer is finished
    // and clients closes the connection.
    playConnectMelody();
    showStatus2("Connection to " + device.getName() + " established");

    final File dstFile = new File(jDroidDir, apkName);
    showStatus3("Receiving data now...");
    hourglass.show();
    doRun();
    isRunning = true;
    size = 0;
    new TransactionThread(is).start();

    writeFile(is, dstFile);
    isDone = true;
    L.i("returning from writefile");
    server.abort();  // Will terminate pending wait for next client
  }

  private void writeFile(InputStream is, File dstFile)
  {
    OutputStream out = null;
    try
    {
      out = new FileOutputStream(dstFile);

      byte[] buf = new byte[16384];
      int len;
      while ((len = is.read(buf)) > 0)
      {
        out.write(buf, 0, len);
        size += len;
        isTimeout = false;
        out.flush();
      }
    }
    catch (IOException ex)
    {
      L.i("Exception in copyFile() " + ex.getMessage());
    }
    finally
    {
      L.i("# of bytes transferred: " + size);
      try
      {
        out.close();  // is closed by transaction thread
      }
      catch (Exception ex)
      {
      }
      L.i("Stream closed");
    }
  }
}
