// BtChat.java
// First I try to be a client, if fails, I am a server

import javax.swing.*;
import ch.aplu.util.Console;

public class BtChat
{
  private final String serviceName = "ChatServer";
  private final boolean isVerbose = true;

  public BtChat(String partnerName, boolean isServer)
  {
    if (isServer)
      new BtChatServer("ChatServer", "Listening as server", isVerbose);
    else
    {
      BtChatClient bcc = new BtChatClient(isVerbose);
      bcc.connect(partnerName, serviceName, true);  // Blocking while I am client
      if (isVerbose)
        System.out.println("Verbose: Connection failed. Starting as server");
      new BtChatServer("ChatServer", "No client connected.\nListening as server", isVerbose);
    }
  }

  public static void main(String[] args)
  {
    if (args.length > 0)
      Console.init();
    String prompt = "Enter Bluetooth name of your chat partner.\n(Empty will listen as server)";
    String value = JOptionPane.showInputDialog(null, prompt);
    if (value == null)
      System.exit(0);
    if (value.trim().length() == 0)
      new BtChat(value, true);
    else
      new BtChat(value, false);
  }
}