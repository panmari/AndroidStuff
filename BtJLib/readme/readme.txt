BtJLib Library
Version 1.04, February 25, 2012
==================================================================

See http://www.aplu.ch for most recent information.

History:
-------
V1.01 - Jan 2007: - First official release, all basic features implemented
V1.02 - Jun 2010: - Modified: check for connection status in
                    BluetoothPeer.releaseConnection()
                  - Modified:  join() to join(2000) in BluetoothServer.cancel()
                    (avoids hanging when thread cannot be terminated)
V1.03 - Jul 2010  - BluetoothPeer constructor with empty or null Bluetooth name
                    starts immediately as server
                  - Removed BluetoothPeer.getLocalName(), replaced by
                    static BluetoothFinder.getLocalBluetoothName() and
                    static BluetoothFinder.getLocalBluetoothAddres()
                  - Added BluetoothPeer.isClient(), BluetoothPeer.isServer() 
V1.04 - Feb 2012  - All device and service names are trimmed now

Installation
------------

1. Download the latest version of the BtJLib library.
   Unpack the ZIP archive in any folder. The following subdirectories
   are created:
    - lib (contains BtJLib.jar)
    - doc (contains JavaDoc)
    - applications (contains sample applications)
    - src (source files)

2. Within your favorite IDE add BtJLib.jar to the external libraries 
   of your Java project.

3. Try to compile and run some of the examples subdirectory of the  distribution.

4. Consult the JavaDoc by opening index.html in the doc subdirectory. 

5. Study the Example pages at http:/www.aplu.ch (Bluetooth Package)

For any help or suggestions send an e-mail to support@aplu.ch or post an article
to the forum at http://www.aplu.ch/forum.

Enjoy!
