// BtChatPanel.java
// GUI used for BtServer/BtClient

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class BtChatPanel extends JFrame 
  implements KeyListener
{
  private BtChatQuit btChatQuit;
  private PrintWriter netOut;
  private String line = "";
  private JPanel contentPane;
  private JTextArea inputArea = new JTextArea(10, 40);
  private JScrollPane inputScrollPane =
    new JScrollPane(
      inputArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

  private JTextArea outputArea = new JTextArea(10, 40);
  private JScrollPane outputScrollPane =
    new JScrollPane(
      outputArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

  public BtChatPanel(BtChatQuit btChatQuit, String title)
  {
    super(title);
    this.btChatQuit = btChatQuit;
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    init();
    pack();
    setVisible(true);
  }

  private void init()
  {
    contentPane = (JPanel)getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(outputScrollPane, BorderLayout.NORTH);
    contentPane.add(inputScrollPane, BorderLayout.SOUTH);
    inputArea.setEditable(false);
    outputArea.addKeyListener(this);
    enableEntry(false);
    setLocation(20, 20);
  }

  public void appendLine(String text)
  {
    inputArea.append(text);
    inputArea.append("\n");
    scrollToEnd(inputArea);
  }

  public void setOutput(PrintWriter netOut)
  {
    this.netOut = netOut;
  }

  public void enableEntry(boolean b)
  {
    outputArea.setEnabled(b);
    if (b)
      outputArea.requestFocus();
  }

  private void scrollToEnd(JTextArea ta)
  {
    try
    {
      ta.setCaretPosition(
        ta.getLineEndOffset(ta.getLineCount() - 1));
    }
    catch (BadLocationException ex)
    {
      System.out.println(ex);
    }
  }

  public void keyPressed(KeyEvent evt)
  {
    char ch;
    if (evt.getKeyCode() == 8 && line.length() > 0)
      line = line.substring(0, line.length() - 1);
    else
    {
      if (evt.getKeyCode() == 0 || evt.getKeyCode() > 31)
      {
        ch = evt.getKeyChar();
        line += ch;
      }
    }

    if (evt.getKeyCode() == '\n')
    {
      netOut.println(line);
      netOut.flush();
      line = "";
    }
  }

  public void keyReleased(KeyEvent evt) {}

  public void keyTyped(KeyEvent evt) {}

  protected void processWindowEvent(WindowEvent evt)
  {
    super.processWindowEvent(evt);
    if (evt.getID() == WindowEvent.WINDOW_CLOSING)
      btChatQuit.quit();
  }  
 
}
