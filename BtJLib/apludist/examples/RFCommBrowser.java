// RFCommBrowser.java

import javax.bluetooth.*;
import java.util.*;
import ch.aplu.bluetooth.*;
import ch.aplu.util.*;

public class RFCommBrowser implements BluetoothResponder
{
  private Console c = new Console();
  private BluetoothFinder btf;

  public RFCommBrowser()
  {
    int uuid_RFCOMM = 0x0003;
    int[] uuids = {uuid_RFCOMM};
    c.println("Searching for Bluetooth devices. Please wait...");
    btf = new BluetoothFinder(uuids, false, this);
  }

  public void notifyBluetoothDeviceSearch(Vector deviceTable)
  {
    // Vector elements: DeviceInfo {RemoteDevice, DeviceClass}
    c.println("\nDevice report:");
    if (deviceTable.size() > 0)
    {
      for (int i = 0; i < deviceTable.size(); i++)
      {
        BtDeviceInfo di = (BtDeviceInfo)deviceTable.elementAt(i);
        RemoteDevice dev = di.getRemoteDevice();
        String deviceName = btf.getDeviceName(dev);
        DeviceClass dc = di.getDeviceClass();
        int majorDC = dc.getMajorDeviceClass();
        int minorDC = dc.getMinorDeviceClass();
        c.println("Device found. Name: " + deviceName);
        c.println("  Major Device Class: " + majorDC);
        c.println("  Minor Device Class: " + minorDC);
      }
      c.println("\nSearching for RFCOMM services. Please wait...");
    }
    else
      c.println("No devices found");
  }

  public void notifyBluetoothServiceSearch(Vector serviceTable)
  {
    // Vector elements: ServiceInfo {RemoteDevice, ServiceRecord}
    c.println("\nService report:");
    if (serviceTable.size() > 0)
    {
      for (int i = 0; i < serviceTable.size(); i++)
      {
        BtServiceInfo si = (BtServiceInfo)serviceTable.elementAt(i);
        RemoteDevice dev = si.getRemoteDevice();
        ServiceRecord sr = si.getServiceRecord();

        // Show host name
        String deviceName = btf.getDeviceName(dev);
        c.println("Service of host: " + deviceName);

        // Show service name
        String serviceName;
        DataElement de = sr.getAttributeValue(0x0100);
        if (de != null)
          serviceName = (String)de.getValue();
        else
          serviceName = "not found";
        c.println("  Service name: " + serviceName);

        // Show connection URL
        String serviceUrl = sr.getConnectionURL(0, false);
        c.println("  Connection URL: " + serviceUrl);
        c.println();
      }
      c.println("\nAll done");
    }
    else
      c.println("No services found");
  }
  
  public static void main(String[] args)
  {
    new RFCommBrowser();
  }
}
