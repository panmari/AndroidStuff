package ch.aplu.bluetooth;

public class MyRemoteDevice extends javax.bluetooth.RemoteDevice {
	/**
	 * @param macAdress the MAC-Adress of the device, in plain numbers without ":"
	 */
	public MyRemoteDevice(String macAdress) {
		super(macAdress);
	}
}
