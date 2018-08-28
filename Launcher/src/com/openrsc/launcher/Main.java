package com.openrsc.launcher;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;

public class Main {
  private static Main singleton;
  private JFrame frame;
  private JButton playButton;
  private JProgressBar progressBar;
  private JLabel statusText;
  private CacheUpdater cacheUpdater;
  private JTextPane statusLog;
  private JCheckBox chckbxLinksAtBottom;
  private ClassLoader loader;
  private Class<?> mainClass;
  
  public static void main(String[] args)
  {
     singleton = new Main();
     singleton.init();
  }
  
  private void init() {
     this.cacheUpdater = new CacheUpdater();
     this.cacheUpdater.update();
     fetchGameClient();
  }
  
  public Main()
  {
    try
    {
       Font font = Font.createFont(0, getFontAsStream("Cinzel-Regular.otf")).deriveFont(12.0F);
       Font bold = Font.createFont(0, getFontAsStream("Cinzel-Bold.ttf")).deriveFont(14.0F);
      
       this.frame = new JFrame("Open RSC Launcher");
       this.frame.setBounds(100, 100, 800, 600);
       this.frame.setDefaultCloseOperation(3);
       this.frame.getContentPane().setLayout(null);
       this.frame.getContentPane().setBackground(Color.WHITE);
       this.frame.setResizable(false);
      
       this.statusLog = new JTextPane();
       this.statusLog.setForeground(new Color(255, 255, 255));
       this.statusLog.setBackground(new Color(34, 34, 34));
            
       this.progressBar = new JProgressBar(0, 100);
       this.progressBar.setFont(font);
       this.progressBar.setForeground(new Color(204, 0, 0));
       this.progressBar.setBackground(Color.WHITE);
       this.progressBar.setValue(0);
       this.progressBar.setStringPainted(true);
       this.progressBar.setBorderPainted(false);
       this.progressBar.setBounds(9, 542, 649, 18);
       this.frame.getContentPane().add(this.progressBar);
      
       this.statusText = new JLabel("");
       this.statusText.setBackground(Color.WHITE);
       this.statusText.setHorizontalAlignment(0);
       this.statusText.setForeground(Color.BLACK);
       this.statusText.setFont(bold);
       this.statusText.setBounds(190, 523, 297, 18);
       this.frame.getContentPane().add(this.statusText);
      
       this.playButton = new JButton(new ImageIcon(Main.class.getResource("/resources/play.png")));
       this.playButton.setBorder(javax.swing.BorderFactory.createLineBorder(Color.WHITE));
       this.playButton.setEnabled(false);
       this.playButton.setDisabledIcon(new ImageIcon(
         getResource("play_disabled.png")));
       this.playButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent arg0) {
           Main.this.launchClient();
        }
       });
       this.playButton.addMouseListener(new java.awt.event.MouseAdapter()
      {
        public void mouseEntered(MouseEvent e) {
           if (Main.this.playButton.isEnabled()) {
             Main.this.frame.setCursor(Cursor.getPredefinedCursor(12));
          }
        }
        
        public void mouseExited(MouseEvent e) {
           if (Main.this.playButton.isEnabled()) {
             Main.this.frame.setCursor(Cursor.getPredefinedCursor(0));
          }
        }
       });
       this.playButton.setBounds(668, 505, 110, 55);
       this.frame.getContentPane().add(this.playButton);
      
       this.chckbxLinksAtBottom = new JCheckBox("Quick-links on Client");
       this.chckbxLinksAtBottom.setFont(new Font("Tahoma", 0, 10));
       this.chckbxLinksAtBottom.setForeground(Color.BLACK);
       this.chckbxLinksAtBottom.setBackground(Color.WHITE);
       this.chckbxLinksAtBottom.setSelected(false);
       this.chckbxLinksAtBottom.setBounds(664, 475, 124, 26);
       this.frame.getContentPane().add(this.chckbxLinksAtBottom);
    } catch (Exception e) {
       e.printStackTrace();
    }
    
     this.frame.setVisible(true);
     setStatusText("Please wait - Fetching new files...");
  }
  
  class LinkListener extends java.awt.event.MouseAdapter
  {
    private String link;
    
    public LinkListener(String link) {
       this.link = link;
    }
    
    public void mouseClicked(MouseEvent arg0) {
      try {
         Desktop desktop = Desktop.getDesktop();
         desktop.browse(new java.net.URI(this.link));
      } catch (Exception e) {
         e.printStackTrace();
         Main.this.showErrorPopup("Couldn't open link! " + e.toString());
      }
    }
    
    public void mouseEntered(MouseEvent e)
    {
       e.getComponent().setCursor(Cursor.getPredefinedCursor(12));
    }
    
    public void mouseExited(MouseEvent e) {
       e.getComponent().setCursor(Cursor.getPredefinedCursor(0));
    }
  }

  public void start()
    throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
  {
     Applet applet = (Applet)Applet.class.cast(this.mainClass.getConstructor(new Class[0]).newInstance(new Object[0]));
    
     JFrame gameFrame = new JFrame("Open RSC Launcher");
     gameFrame.setIconImage(getResource("favicon.png"));
     gameFrame.getContentPane().setLayout(new BorderLayout());
     gameFrame.setResizable(true);
     gameFrame.setDefaultCloseOperation(3);
     gameFrame.setLocationRelativeTo(null);
    
     JPanel gamePanel = new JPanel();
    
     gamePanel.setLayout(new BorderLayout());
     gamePanel.add(applet);
     gamePanel.setPreferredSize(new java.awt.Dimension(512, 344));
     if (this.chckbxLinksAtBottom.isSelected()) {
       JMenuBar jmenubar = new JMenuBar();
      
       jmenubar.setBorder(null);
       jmenubar.setLayout(new java.awt.FlowLayout(3, 50, 5));
       jmenubar.setBackground(Color.WHITE);
       jmenubar.add(makeLinkLabel("Website/Forums", "http://localhost"));
       jmenubar.add(makeLinkLabel("Discord(Chat/Support)", "https://discord.gg/94vVKND"));
      
       gameFrame.getContentPane().add(jmenubar, "South");
    }
     gameFrame.getContentPane().add(gamePanel, "Center");
    
     gameFrame.pack();
     gameFrame.setVisible(true);
    
     applet.init();
     applet.start();
    
     getFrame().dispose();
     setFrame(gameFrame);
  }
  
  public JLabel makeLinkLabel(String text, String link) {
     JLabel label = new JLabel(text);
     label.setForeground(Color.BLACK);
     label.setSize(100, 20);
     label.addMouseListener(new LinkListener(link));
     return label;
  }
  
  public void fetchClient()
    throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, java.net.MalformedURLException, ClassNotFoundException
  {
     this.loader = new java.net.URLClassLoader(new java.net.URL[] { new java.net.URL(Config.CLIENT_URL) });
     this.mainClass = Class.forName("rsc.RSCFrame", true, this.loader);
    
     setProgress(100.0F);
  }
  
  private void fetchGameClient() {
     setStatusText("Fetching client...");
    try {
       fetchClient();
       setStatusText("Game ready to launch!");
       this.playButton.setEnabled(true);
    } catch (Exception e) {
       e.printStackTrace();
       showErrorPopup("Error fetching client: " + e.toString());
       setStatusText("Error with updating. Please retry!");
    }
  }
  
  private void showErrorPopup(String a)
  {
     javax.swing.JOptionPane.showMessageDialog(null, a, "Launcher error!", 2);
  }
  
  private void launchClient()
  {
    try {
       start();
    } catch (Exception e) {
       e.printStackTrace();
       showErrorPopup("Error running client: " + e.toString());
    }
  }
  
  public JButton getPlayButton() {
     return this.playButton;
  }
  
  public JProgressBar getProgressBar() {
     return this.progressBar;
  }
  
  public JFrame getFrame() {
     return this.frame;
  }
  
  public void setStatusText(String string) {
     this.statusText.setText(string);
     this.frame.repaint();
  }
  
  public void setProgress(float percent) {
     this.progressBar.setBackground(Color.WHITE);
     this.progressBar.setValue((int)percent);
     this.progressBar.setString((int)percent + "%");
     this.frame.repaint();
  }
  
  public void setDownloadProgress(String f, float percent) {
     this.progressBar.setBackground(Color.WHITE);
     this.progressBar.setValue((int)percent);
     this.progressBar.setString(f + " - " + (int)percent + "%");
     this.frame.repaint();
  }
  
  public static Main getSingleton() {
     return singleton;
  }
  
  public void println(String s) {
     this.statusLog.setText(this.statusLog.getText() + s + "\n");
  }
  
  public void setFrame(JFrame gameFrame) {
     this.frame = gameFrame;
     this.frame.setResizable(true);
  }
  
   public java.io.InputStream getFontAsStream(String name) { java.io.InputStream input = getClass().getResourceAsStream("/resources/" + name);
     return input;
  }
  
   public java.awt.Image getResource(String name) { return java.awt.Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/" + name)); }
}