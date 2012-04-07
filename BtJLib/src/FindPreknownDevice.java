import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import ch.aplu.bluetooth.BluetoothFinder;


public class FindPreknownDevice {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String serverName = "phPhone";
		RemoteDevice rd = BluetoothFinder.searchPreknownDevice(serverName);
		try {
			System.out.println("Found and connected to: " + rd.getFriendlyName(true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
