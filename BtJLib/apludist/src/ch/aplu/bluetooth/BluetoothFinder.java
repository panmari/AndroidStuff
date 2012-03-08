// BluetoothFinder.java

/*
This software is part of the Aplu Bluetooth Library.
It is Open Source Free Software, so you may
- run the code for any purpose
- study how the code works and adapt it to your needs
- integrate all or parts of the code in your own programs
- redistribute copies of the code
- improve the code and release your improvements to the public
However the use of the code is entirely your responsibility.
 */

package ch.aplu.bluetooth;

import javax.bluetooth.*;
import java.util.*;
import java.io.*;

/**
 * Class to perform an inquiry for Bluetooth devices and their services.<br><br>
 * Strategy for fast connection to a device with given device name:<br>
 *   - Invoke static methods searchPreknownDevice() and searchCachedDevice()<br>
 *   - If fails, perform an extended search by constructing a BluetoothFinder instance
 *     with uuids = 0 (no service search)<br>
 *   - Retrieve Bluetooth address with RemoteDevice.getBluetoothAddress()<br>
 *   - Declare a connection URL string using the known service protocol, e.g.
 *     connectionUrl = "btspp://" + address + ":1;authenticate=false;encrypt=false;master=false"<br>
 *   - Open a stream connection conn = (StreamConnection)Connector.open(connectionUrl)<br>
 *   - For data transfer open input and/or output streams with
 *     conn.openInputStream(), conn.openOutputStream()<br><br>
 * 
 * After an extending search, the callback method BluetoothResponser.notifyBluetoothDeviceSearch()
 * is called, where device search information can be retrieved. If a service search is performed, the
 * callback methode BluetoothResponder.notifyBluetoothServiceSearch() is called, where service search
 * information can be retrieved.
 */
public class BluetoothFinder implements DiscoveryListener
{
  // --------------- Inner class SearchThread ---------------------------
  private class SearchThread extends Thread
  {
    private BluetoothFinder bf;
    private int transID;

    private SearchThread(BluetoothFinder bf)
    {
      this.bf = bf;
    }

    public void run()
    {
      BtDeviceInfo di = (BtDeviceInfo)(deviceTable.elementAt(devIndex));
      RemoteDevice dev = di.getRemoteDevice();
      javax.bluetooth.UUID[] uuidSet = new javax.bluetooth.UUID[uuids.length];
      for (int k = 0; k < uuids.length; k++)
      {
        uuidSet[k] = new javax.bluetooth.UUID(uuids[k]);
        if (isVerbose)
          VerboseWriter.out.println("Search for uuid " + uuids[k] + "...");
      }
      searchForService(dev, attrSet, uuidSet);
      final int period = 1000; // ms
      final int maxTime = 60000;  // ms
      int time = 0;
      serviceSearchDone = false;
      while (!serviceSearchDone && time < maxTime)
      {
        try
        {
          Thread.currentThread().sleep(period);
        }
        catch (InterruptedException ex)
        {}
        time += period;
      }
      if (serviceSearchDone)
      {
       if (isVerbose)
         VerboseWriter.out.println("Service thread terminated");
      }
      else
      {
        String btName = getDeviceName(dev);
        String msg = "Timeout while searching services for device " + btName;
        if (isVerbose)
          VerboseWriter.out.println(msg);
        serviceSearchCompleted(transID, SERVICE_SEARCH_ERROR);
      }
    }

    // ================= Search services of a single device ========
    private void searchForService(RemoteDevice dev, int[] attrSet, javax.bluetooth.UUID[] uuidSet)
    {
      if (isVerbose)
        VerboseWriter.out.println("\nSearching services of device: " + getDeviceName(dev));

      // non-blocking search
      try
      {
        transID = agent.searchServices(attrSet, uuidSet, dev, bf);
      }
      catch (BluetoothStateException ex)
      {
        String msg = "Bluetooth service search failed.";
        if (isVerbose)
          VerboseWriter.out.println(msg);
      }
      if (isVerbose)
        VerboseWriter.out.println("searchServices() returned transID: " + transID);
    }
  }
  // ------------------- End of inner class -------------------------

  private BluetoothResponder bluetoothResponder = null;
  private DiscoveryAgent agent;
  private boolean isVerbose = false;
  private String deviceName;
  private int devIndex = 0;
  private int[] uuids;
  private int[] attrSet;
  private volatile boolean serviceSearchDone;
  private SearchThread st;
  volatile int nbCalls = 1;

  // Table of found devices, stored as DeviceInfo, which are
  //  pairs of the form {RemoteDevice, DeviceClass}
  private Vector deviceTable = new Vector();

  // Table of found services, stored as ServiceInfo, which are
  // pairs of the form {RemoteDevice, ServiceRecord}
  private Vector serviceTable = new Vector();

  /**
   * Same as Bluetoothfinder(int[] uuids, boolean isVerbose), but search
   * only for the given deviceName. If deviceName is empty or null, the search
   * is done for all devices.
   */
  public BluetoothFinder(String deviceName, int[] uuids, boolean isVerbose, BluetoothResponder responder)
  {
    bluetoothResponder = responder;
    if (deviceName == null)
      this.deviceName = "";
    else
      this.deviceName = deviceName;
    this.uuids = uuids;
    attrSet = new int[6];
    attrSet[0] = 0x0000;
    attrSet[1] = 0x0001;
    attrSet[2] = 0x0002;
    attrSet[3] = 0x0003;
    attrSet[4] = 0x0004;
    attrSet[5] = 0x0100;
    this.isVerbose = isVerbose;
    init();
  }

  /**
   * Initiates extensive device and service inquiry with given UUIP integer values.
   * Typical 16-bit integer UUIDs of common services
   *   SDP     0x0001<br>
   *   RFCOMM  0x0003<br>
   *   OBEX    0x0008<br>
   *   HTTP    0x000C<br>
   *   L2CAP   0x0100<br>
   * If uuids = null, no service search is performed.
   * Searches only services with attribute IDs given in attrSet.
   * Set isVerbose = true to enable status information via VerboseWriter.
   * Response will trigger the callback methods declared
   * in the given BluetoothResponder.
   * @see ch.aplu.bluetooth.BluetoothResponder
   * @see javax.bluetooth.UUID
   * @see VerboseWriter
   */
  public BluetoothFinder(int[] uuids, int[] attrSet,
                         boolean isVerbose, BluetoothResponder responder)
  {
    this("", uuids, attrSet, isVerbose, responder);
  }

  /**
   * Initiates device and service search with given UUIP integer values.
   * Typical 16-bit integer UUIDs of common services<br>
   *   SDP     0x0001<br>
   *   RFCOMM  0x0003<br>
   *   OBEX    0x0008<br>
   *   HTTP    0x000C<br>
   *   L2CAP   0x0100<br>
   * If uuids = null, no service search is performed.
   * Set isVerbose = true to enable status information via VerboseWriter.
   * Only the following service attributes are retrieved:<br>
   *   ServiceRecordHandle    (id: 0x0000)<br>
   *   ServiceClassIDlist     (id: 0x0001)<br>
   *   ServiceRecordState     (id: 0x0002)<br>
   *   ServiceID              (id: 0x0003)<br>
   *   ProtocolDescriptorList (id: 0x0004)<br>
   *   Service Name           (id: 0x0100)<br>
   * Response will trigger the callback methods declared
   * in the given BluetoothResponder.
   * @see ch.aplu.bluetooth.BluetoothResponder
   * @see javax.bluetooth.UUID
   * @see VerboseWriter
   */
  public BluetoothFinder(int[] uuids, boolean isVerbose, BluetoothResponder responder)
  {
    this("", uuids, isVerbose, responder);
  }

/**
   * Same as Bluetoothfinder(int[] uuids, int[] attrSet, boolean isVerbose), but search
   * only for the given deviceName. If deviceName is empty or null, the search
   * is done for all devices.
   */
  public BluetoothFinder(String deviceName, int[] uuids, int[] attrSet,
                         boolean isVerbose, BluetoothResponder responder)
  {
    bluetoothResponder = responder;
    if (deviceName == null)
      this.deviceName = "";
    else
      this.deviceName = deviceName;
    this.uuids = uuids;
    this.attrSet = attrSet;
    this.isVerbose = isVerbose;
    init();
  }

  private void init()
  {
    try
    {
      LocalDevice local = LocalDevice.getLocalDevice();
      agent = local.getDiscoveryAgent();

      if (isVerbose)
        if (deviceName.equals(""))
          VerboseWriter.out.println("Searching for all devices...");
        else
          VerboseWriter.out.println("Searching for device " + deviceName + "...");

      agent.startInquiry(DiscoveryAgent.GIAC, this); // non-blocking, wait for callback
    }
    catch (Exception ex)
    {
      VerboseWriter.out.println(ex);
      return;
    }
  }

  // ================ Callback when device is found ====================
  /**
   * For internal use only.
   */
  public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass)
  {
    String devName = getDeviceName(remoteDevice);
    if (isVerbose)
    {
      VerboseWriter.out.println("Device found. Name: " + devName);
      VerboseWriter.out.println("Bluetooth address: " + remoteDevice.getBluetoothAddress());
    }
    int majorDC = deviceClass.getMajorDeviceClass();
    int minorDC = deviceClass.getMinorDeviceClass();
    if (isVerbose)
      VerboseWriter.out.println("Major Device Class: " + majorDC +
                "; Minor Device Class: " + minorDC);

    if (deviceName.equals("")) // All devices requested
      deviceTable.addElement(new BtDeviceInfo(remoteDevice, deviceClass));
    else
    {
      if (deviceName.equals(devName))  // Only one device requested
      {
        deviceTable.addElement(new BtDeviceInfo(remoteDevice, deviceClass));
        // and cancel further inquiry
        agent.cancelInquiry(this);
        if (isVerbose)
          VerboseWriter.out.println("Given device name found. Further inquiry canceled");
      }
    }
  }

  // ================ Callback when device search is complete ==========
  // Show results and start service search
  /**
   * For internal use only.
   */
  public void inquiryCompleted(int inqType)
  {
    showInquiryCode(inqType);
    if (!(inqType == INQUIRY_COMPLETED || inqType == INQUIRY_TERMINATED))
      return;

    if (isVerbose)
      VerboseWriter.out.println("# of devices found: " +  deviceTable.size());

    // Invoke notification
    if (bluetoothResponder != null)
      bluetoothResponder.notifyBluetoothDeviceSearch(deviceTable);

    if (uuids == null)
      return;  // No service search

    if (deviceTable.size() > 0)
    {
      if (isVerbose)
        VerboseWriter.out.println("\nSearching for services...");

       // Start the services search
       st = new SearchThread(this);
       st.start();
    }
  }

 // ================ Show response code of device search ===========
   private void showInquiryCode(int inqCode)
  {
    if (!isVerbose)
      return;
    if (inqCode == INQUIRY_COMPLETED)
      VerboseWriter.out.println("Device search completed");
    else
      if(inqCode == INQUIRY_TERMINATED)
        VerboseWriter.out.println("Device search terminated");
      else
        if (inqCode == INQUIRY_ERROR)
          VerboseWriter.out.println("Device search error");
        else
          VerboseWriter.out.println("Unknown device search status: " + inqCode);
  }

  // ================ Callback when service is found ====================
  /**
   * For internal use only.
   */
  public void servicesDiscovered(int transID, ServiceRecord[] serviceRecords)
  {
    if (isVerbose)
    {
       VerboseWriter.out.println("Service found. Transaction ID " + transID);
       VerboseWriter.out.println("Length of service record: " + serviceRecords.length);
    }

    for (int i = 0; i < serviceRecords.length; i++)
    {
      if (isVerbose)
        VerboseWriter.out.println("Retrieving record # " + i + "...");

      if (serviceRecords[i] != null)
      {
        // Store in service table
        RemoteDevice dev = serviceRecords[i].getHostDevice();
        serviceTable.addElement(new BtServiceInfo(dev, serviceRecords[i]));

        if (isVerbose)
        {
          // Get the service connection URL
          String serviceUrl = serviceRecords[i].getConnectionURL(0, false);
          VerboseWriter.out.println("Connection URL: " + serviceUrl);

          // Get the service record's name
          String serviceName;
          DataElement serviceNameElem = serviceRecords[i].getAttributeValue(0x0100);
          if (serviceNameElem != null)
            serviceName = (String)serviceNameElem.getValue();
          else
            serviceName = "not found";
          VerboseWriter.out.println("Service name: " + serviceName);
        }
      }
    }
  }

  // ================ Callback when service search is complete ==========
  /**
   * For internal use only.
   */
  public void serviceSearchCompleted(int transID, int respCode)
  {
    showResponseCode(transID, respCode);

    serviceSearchDone = true;
    st.interrupt();
    try
    {
      st.join();
    }
    catch (InterruptedException ex)
    {}

    devIndex++;
    if (devIndex < deviceTable.size())
    {
      // Wait a while, necessary on some mobile phones
      try
      {
        Thread.currentThread().sleep(1000);
      }
      catch (InterruptedException ex)
      {}
      st = new SearchThread(this);
      st.start();
    }
    else // end of all service requests
      // Invoke notification
      if (bluetoothResponder != null)
         bluetoothResponder.notifyBluetoothServiceSearch(serviceTable);
  }

  // ================ Show response code of service search ===========
  private void showResponseCode(int transID, int respCode)
  {
    if (!isVerbose)
      return;
    VerboseWriter.out.print("Trans ID " + transID + ". ");
    if (respCode == SERVICE_SEARCH_ERROR)
      VerboseWriter.out.println("Service search error");
    else if (respCode == SERVICE_SEARCH_COMPLETED)
      VerboseWriter.out.println("Service search completed");
    else if (respCode == SERVICE_SEARCH_TERMINATED)
      VerboseWriter.out.println("Service search terminated");
    else if (respCode == SERVICE_SEARCH_NO_RECORDS)
      VerboseWriter.out.println("Service search: No records found");
    else if (respCode == SERVICE_SEARCH_DEVICE_NOT_REACHABLE)
      VerboseWriter.out.println("Service search: Device not reachable");
    else
      VerboseWriter.out.println("Unknown service search status. Code: " + respCode);
  }


  // ==================== Helper methods ================

  /**
   * Returns device name from given device using RemoteDevice.getFriendlyName().
   * Returns null if fails.
   */
  public static String getDeviceName(RemoteDevice dev)
  {
    String s = "";
    try
    {
      s = dev.getFriendlyName(false);
    }
    catch (IOException ex)
    {
      return null;
    }
    return s;
  }

  /**
   * Returns service name from given service record.
   * Returns null, if fails.
   */
  public static String getServiceName(ServiceRecord serviceRecord)
  {
    DataElement serviceNameElem = serviceRecord.getAttributeValue(0x0100);
    if (serviceNameElem == null)
      return null;
    return (String)serviceNameElem.getValue();
  }

  /**
   * Returns the local discovery agent.
   * Returns null, if fails.
   */
  public static DiscoveryAgent getDiscoveryAgent()
  {
    try
    {
      LocalDevice dev = LocalDevice.getLocalDevice();
      return dev.getDiscoveryAgent();
    }
    catch (BluetoothStateException ex)
    {
      return null;
    }
  }

  /**
   * Searches for device with given name in the 'preknown' database, and return it.
   * Returns null, if not found or search fails.
   */
  public static RemoteDevice searchPreknownDevice(String deviceName)
  {
    DiscoveryAgent agent = BluetoothFinder.getDiscoveryAgent();
    RemoteDevice[] devs = agent.retrieveDevices(DiscoveryAgent.PREKNOWN);
    if (devs == null || devs.length == 0)
      return null;
    else
    {
      for (int i = 0; i < devs.length; i++)
      {
        String devName = BluetoothFinder.getDeviceName(devs[i]);
        if (deviceName.equals(devName))
          return devs[i];
      }
    }
    return null;
  }

  /**
   * Searches for device with given name in the 'cached' database, and return it.
   * Returns null, if not found or search fails.
   */
  public static RemoteDevice searchCachedDevice(String deviceName)
  {
    DiscoveryAgent agent = BluetoothFinder.getDiscoveryAgent();
    RemoteDevice[] devs = agent.retrieveDevices(DiscoveryAgent.CACHED);
    if (devs == null || devs.length == 0)
      return null;
    else
    {
      for (int i = 0; i < devs.length; i++)
      {
        String devName = BluetoothFinder.getDeviceName(devs[i]);
        if (deviceName.equals(devName))
          return devs[i];
      }
    }
    return null;
  }
}