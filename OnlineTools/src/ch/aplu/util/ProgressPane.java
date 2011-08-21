// ProgressPane.java

package ch.aplu.util;


import javax.swing.*;
import java.awt.event.*;
import java.net.URL;
import java.awt.*;

/**
 * Modeless message dialog using Swing JOptionPane.
 * Useful to show text and/or images while the application continues to run.<br><br>
 * All Swing methods are invoked in the EDT.
 */
public class ProgressPane
{
  // ---------------- Inner class SetText ----------------
  private class SetText implements Runnable
  // Used in order to call Swing methods from EDT only
  {
    private String text;
    private boolean adjust;

    private SetText(String text, boolean adjust)
    {
      this.text = text;
      this.adjust = adjust;
    }

    public void run()
    {
      optionPane.setMessage(text);
      if (adjust)
        dlg.pack();  // Adapt size to new message
    }
  }
  // ---------------- End of inner class -----------------
  //
  private JDialog dlg;
  private JOptionPane optionPane;
  private JButton jButton = null;
  private static Cleanable cleanable = null;
  private Dimension size = null;
  private boolean isDecorated = true;
  private boolean isVisible;
  private Frame owner = null;
  private JProgressBar progressBar;

  /**
   * URL to display the exclamation icon.
   */
  public final static URL ICON_EXCLAMATION = null;

  /**
   * General constructor that show a modeless message dialog at given
   * position (upper left corner) containing given text and given
   * icon image (gif or jpg). iconUrl is the URL for the icon resource.
   * The iconUrl is normally retrieved by calling<br><br>
   * <code>
   * ClassLoader loader = getClass().getClassLoader();<br>
   * URL iconUrl = loader.getResource(iconResource);
   * </code><br><br>
   * where iconResource is a '/'-separated path name that identifies the resource.
   * If the resource is not found or iconUrl is set to ICON_EXCLAMATION, the exclamation icon is displayed.
   * When the titlebar's close button is hit, the application will
   * terminate by calling System.exit(0).
   * To change this behaviour, register the cleanable interface.<br><br>
   * If buttonText is not null and not empty a single button is displayed with
   * the given text. To get a click event, register an ActionListener. <br><br>
   * All methods run in the Event Dispatch Thread (EDT).
   *
  */
  public ProgressPane(int ulx, int uly, String text, URL iconUrl,
                            String buttonText)
  {
    init(false, ulx, uly, text, iconUrl, true, buttonText);
  }

  /**
   * Same as ModelessOptionPane(ulx, uly, text, iconUrl, buttonText), but with
   * no button.
   */
  public ProgressPane(int ulx, int uly, String text, URL iconUrl)
  {
    init(false, ulx, uly,text, iconUrl, true, null);
  }

  /**
   * Same as general constructor, but with no icon and no button.
   */
  public ProgressPane(int ulx, int uly, String text)
  {
    init(false, ulx, uly, text, null, false, null);
  }

 /**
   * Same as general constructor, but with no text, no icon and no button,
   * given dimension and selectable decoration. Useful for undecorated
   * status bars. If an owner is given, the owner window is brought
   * to front when the status bar is clicked and vice versa.
   * (The z-order position of the pane is the same as the owner.)
   */
  public ProgressPane(Frame owner, int ulx, int uly, Dimension size, boolean isDecorated)
  {
    this.owner = owner;
    this.size = size;
    this.isDecorated = isDecorated;
    init(false, ulx, uly, null, null, false, null);
  }

  /**
   * Same as general constructor, but with no button and
   * dialog centered in middle of the screen.
   */
  public ProgressPane(String text, URL iconUrl)
  {
    init(true, 0, 0, text, iconUrl, true, null);
  }

  /**
   * Same as general constructor, but dialog centered in middle of the screen.
   */
  public ProgressPane(String text, URL iconUrl, String buttonText)
  {
    init(true, 0, 0, text, iconUrl, true, buttonText);
  }

  /**
   * Same as general constructor, but with no icon and no button and
   * dialog centered in middle of screen.
   */
  public ProgressPane(String text)
  {
    init(true, 0, 0, text, null, false, null);
  }

  private void init(final boolean centered, final int ulx, final int uly,
                    final String text, final URL iconUrl,
                    final boolean isIcon, final String buttonText)

  {
    if (EventQueue.isDispatchThread())
      doInit(centered, ulx, uly,text, iconUrl, isIcon, buttonText);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            doInit(centered, ulx, uly, text, iconUrl, isIcon, buttonText);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  private void doInit(boolean centered, int ulx, int uly,
                      String text, URL iconUrl, boolean isIcon,
                      String buttonText)
  {
    ImageIcon icon;
    if (iconUrl == null)
      icon = null;  // Will display defaut icon
    else
      icon = new ImageIcon(iconUrl);
    dlg = new JDialog(owner)
    {
      protected void processWindowEvent(WindowEvent e)
      {
        if (e.getID() == WindowEvent.WINDOW_CLOSING)
        {
          if (cleanable != null)
            cleanable.clean();
          else
            System.exit(0);
        }

        if (e.getID() == WindowEvent.WINDOW_ACTIVATED)
        {
          if (owner != null)
            owner.toFront();
        }
        if (e.getID() == WindowEvent.WINDOW_DEACTIVATED)
        {
        }
      }
    };
    
    progressBar = new JProgressBar(0, 100);
    progressBar.setValue(0);
    progressBar.setStringPainted(true);
    
    optionPane = new JOptionPane(text);
    if (!isDecorated)
    {
      dlg.setUndecorated(true);
      optionPane.setBorder(BorderFactory.createLineBorder(Color.black));
    }
    optionPane.setOptionType(JOptionPane.DEFAULT_OPTION);
    optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
    if (buttonText != null && buttonText.length() != 0)
    {
      jButton = new JButton(buttonText);
      Object[] options = new Object[]
      {
        jButton
      };
      optionPane.setOptions(options);
    }
    else
      optionPane.setOptions(new Object[] {});
    optionPane.setInitialSelectionValue(null);
    if (isIcon)
      optionPane.setIcon(icon);
    else
      optionPane.setIcon(new ImageIcon("...."));  // Not existing, so no icon displayed

    JPanel panel = new JPanel();
//    panel.add(optionPane, BoxLayout.Y_AXIS);
//    panel.add(progressBar, BoxLayout.Y_AXIS);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(optionPane);
    panel.add(progressBar);
    dlg.add(panel);
    dlg.pack();
    if (size != null)
      dlg.setSize(size);
    if (centered)
    {
      int wWidth = dlg.getWidth();
      int wHeight = dlg.getHeight();
      Fullscreen fs = new Fullscreen();
      ulx = (int)((fs.getWidth() - wWidth) / 2.0);
      uly = (int)((fs.getHeight() - wHeight) / 2.0);
    }
    dlg.setResizable(false);
    dlg.setLocation(ulx, uly);
    dlg.setVisible(true);
  }

  void resize(Point location, int width)
  {
    // Must set it invisible, otherwise the optionPane is not resized (why?)
    dlg.setVisible(false);
    dlg.setLocation(location);
    dlg.setSize(width, size.height);
    dlg.setVisible(true);
  }

  public void setBarValue(final int value)
  {
    if (SwingUtilities.isEventDispatchThread())
      progressBar.setValue(value);
    else
    {
      try
      {
        SwingUtilities.invokeAndWait(
          new Runnable()
          {
            public void run()
            {
              progressBar.setValue(value);
            }
          });
      }
      catch (Exception ex) {}
    }
  }
  
  public void setBarVisible(final boolean visible)
  {
    if (SwingUtilities.isEventDispatchThread())
      progressBar.setVisible(visible);
    else
    {
      try
      {
        SwingUtilities.invokeAndWait(
          new Runnable()
          {
            public void run()
            {
              progressBar.setVisible(visible);
            }
          });
      }
      catch (Exception ex) {}
    }
  }
  
  /**
   * Register a ActionListener to get a notification when the button is hit.
   */
  public void addActionListener(ActionListener listener)
  {
    if (jButton != null)
      jButton.addActionListener(listener);
  }

  /**
   * Display the given text.
   * Adjust size of dialog to length of text, unless the size is given in
   * constructor.
   */
  public void setText(String text)
  {
    if (size == null)
      setText(text, true);
    else
      setText(text, false);
  }

  /**
   * Same as setText() but select whether to adjust size of dialog.
   */
  public void setText(String text, boolean adjust)
  {
    if (SwingUtilities.isEventDispatchThread())
    {
      optionPane.setMessage(text);
      if (adjust)
        dlg.pack();  // Adapt size to new message
    }
    else
    {
      try
      {
        SwingUtilities.invokeAndWait(new SetText(text, adjust));
      }
      catch (Exception ex) {}
    }
  }

  /**
   * Show the given title in the title bar.
   */
  public void showTitle(final String title)
  {
    if (SwingUtilities.isEventDispatchThread())
      dlg.setTitle(title);
    else
    {
      try
      {
        SwingUtilities.invokeAndWait(
          new Runnable()
          {
            public void run()
            {
              dlg.setTitle(title);
            }
          });
      }
      catch (Exception ex) {}
    }
  }

  /**
   * Same as showTitle().
   */
  public void setTitle(String title)
  {
    showTitle(title);
  }

  /**
   * Enable/disable the button (if any).
   */
  public void setButtonEnabled(final boolean enable)
  {
    if (jButton == null)
      return;
    if (SwingUtilities.isEventDispatchThread())
      jButton.setEnabled(enable);
    else
    {
      try
      {
        SwingUtilities.invokeAndWait(
          new Runnable()
          {
            public void run()
            {
              jButton.setEnabled(enable);
            }
          });
      }
      catch (Exception ex) {}
    }
  }

  /**
   * Register a class with a method clean() that will be called when
   * the title bar's close button is hit.
   */
  public void addCleanable(Cleanable cl)
  {
    cleanable = cl;
  }

  /**
   * Dispose the dialog
   */
  public void dispose()
  {
    if (EventQueue.isDispatchThread())
      dlg.dispose();
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            dlg.dispose();
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

  /**
   * Return the dialog.
   */
   public JDialog getDialog()
   {
     return dlg;
   }

   /**
    * Show/hide the dialog.
    */
    public void setVisible(final boolean visible)
    {
    if (EventQueue.isDispatchThread())
      dlg.setVisible(visible);
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            dlg.setVisible(visible);
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  }

 /**
  * Return true, if dialog is visible; otherwise false.
  */
  public boolean isVisible()
  {
    if (EventQueue.isDispatchThread())
      isVisible = isVisible();
    else
    {
      try
      {
        EventQueue.invokeAndWait(new Runnable()
        {

          public void run()
          {
            isVisible = dlg.isVisible();
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
    return isVisible;
  }

  /**
   * Bring the dialog to the front.
   */
  public void toFront()
  {
    dlg.toFront();
  }

  /**
   * Request the focus.
   */
  public void requestFocus()
  {
    dlg.requestFocus();
  }
}

