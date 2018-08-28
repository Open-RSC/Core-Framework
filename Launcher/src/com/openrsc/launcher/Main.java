package com.openrsc.launcher;

import com.openrsc.launcher.rss.Feed;
import com.openrsc.launcher.rss.FeedMessage;
import com.openrsc.launcher.rss.RSSFeedParser;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class Main
{
  private static Main singleton;
  private JFrame frame;
  private JButton playButton;
  private JProgressBar progressBar;
  private JLabel statusText;
  private CacheUpdater cacheUpdater;
  private HTMLEditorKit kit;
  private JTextPane textPane_1;
  private JScrollPane scrollPane_1;
  private JTextPane statusLog;
  private JCheckBox chckbxLinksAtBottom;
  private ClassLoader loader;
  private Class<?> mainClass;
  private JTextPane textPane;
  private JScrollPane scrollPane;
  private JLabel lblHavingProblemsJoin;
  private JLabel lblUpdateLog;
  private JLabel label_2;
  private JLabel lblFollowUs;
  private JLabel lblOnSocialMedia;
  
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
       this.frame.getContentPane().setBackground(Color.black);
       this.frame.setResizable(false);
      
       this.kit = new HTMLEditorKit();
       StyleSheet styleSheet = this.kit.getStyleSheet();
       styleSheet.addRule("body,html {background-color: #111;\tfont-size: 8px;font-family: 'Lucida Grande', 'Lucida Sans Unicode', Arial, Verdana, sans-serif;height: 100%;}");
       styleSheet.addRule("a {color: #1C81B8;text-decoration: none;font-size: 8px;font-family: 'Lucida Grande', 'Lucida Sans Unicode', Arial, Verdana, sans-serif;}");
       styleSheet.addRule("p {color: #b1b1b1;margin: 0 0 10px; font-size: 8px;font-family: 'Lucida Grande', 'Lucida Sans Unicode', Arial, Verdana, sans-serif;}");
       styleSheet.addRule("img {max-width:10%;max-height:10%}");
       styleSheet.addRule("h3 {font-family: 'Cinzel', serif;font-weight: 700;color: #e1bb34;}");
      
       this.scrollPane = new JScrollPane();
       this.scrollPane.setViewportBorder(null);
       this.scrollPane.setBounds(10, 257, 381, 175);
       this.frame.getContentPane().add(this.scrollPane);
      
       this.textPane = new JTextPane();
       this.textPane.setForeground(Color.WHITE);
       this.textPane.setBackground(new Color(34, 34, 34));
       this.textPane.setEditable(false);
       this.textPane.setContentType("text/html");
      
       this.textPane.setEditorKit(this.kit);
       this.scrollPane.setViewportView(this.textPane);
      
       this.scrollPane_1 = new JScrollPane();
       this.scrollPane_1.setViewportBorder(null);
       this.scrollPane_1.setBounds(403, 257, 381, 175);
       this.frame.getContentPane().add(this.scrollPane_1);
      
       this.textPane_1 = new JTextPane();
       this.textPane_1.setForeground(Color.WHITE);
       this.textPane_1.setEditable(false);
       this.textPane_1.setContentType("text/html");
       this.textPane_1.setBackground(new Color(34, 34, 34));
      
       this.textPane_1.setEditorKit(this.kit);
       this.scrollPane_1.setViewportView(this.textPane_1);
      
       JScrollPane scrollPane_2 = new JScrollPane();
       scrollPane_2.setBounds(10, 465, 648, 55);
       this.frame.getContentPane().add(scrollPane_2);
      
       this.statusLog = new JTextPane();
       this.statusLog.setForeground(new Color(255, 255, 255));
       this.statusLog.setBackground(new Color(34, 34, 34));
       scrollPane_2.setViewportView(this.statusLog);
      
       this.lblFollowUs = new JLabel("Follow us");
       this.lblFollowUs.setFont(font);
       this.lblFollowUs.setForeground(Color.WHITE);
       this.lblFollowUs.setBounds(26, 2, 75, 14);
       this.frame.getContentPane().add(this.lblFollowUs);
      
       this.lblOnSocialMedia = new JLabel("on social media:");
       this.lblOnSocialMedia.setForeground(Color.WHITE);
       this.lblOnSocialMedia.setFont(font);
       this.lblOnSocialMedia.setBounds(9, 12, 120, 20);
       this.frame.getContentPane().add(this.lblOnSocialMedia);
      
       JLabel lblNewLabel_1 = new JLabel("Latest News & Updates");
       lblNewLabel_1.setForeground(Color.WHITE);
       lblNewLabel_1.setBounds(10, 230, 239, 26);
       lblNewLabel_1.setFont(bold);
       this.frame.getContentPane().add(lblNewLabel_1);
      
       this.lblUpdateLog = new JLabel("Update log:");
       this.lblUpdateLog.setForeground(Color.WHITE);
       this.lblUpdateLog.setFont(bold);
       this.lblUpdateLog.setBounds(403, 230, 239, 26);
       this.frame.getContentPane().add(this.lblUpdateLog);
      
       this.lblHavingProblemsJoin = new JLabel("Join our Discord for support");
       this.lblHavingProblemsJoin.setForeground(Color.WHITE);
       this.lblHavingProblemsJoin.setFont(font);
       this.lblHavingProblemsJoin.setBounds(448, 9, 210, 20);
       this.frame.getContentPane().add(this.lblHavingProblemsJoin);
      
       JLabel lblStatusLog = new JLabel("Status log");
       lblStatusLog.setForeground(Color.WHITE);
      
       lblStatusLog.setBounds(10, 443, 103, 18);
       lblStatusLog.setFont(bold);
       this.frame.getContentPane().add(lblStatusLog);
      
       JLabel discordButton = new JLabel();
       discordButton.setIcon(new ImageIcon(Main.class.getResource("/resources/discord.jpg")));
       discordButton.setBounds(668, 6, 110, 26);
       discordButton.addMouseListener(new LinkListener("https://discord.gg/94vVKND"));
       this.frame.getContentPane().add(discordButton);
      
       JLabel youtubeButton = new JLabel("");
       youtubeButton.setIcon(new ImageIcon(Main.class.getResource("/resources/youtube.png")));
       youtubeButton.setBounds(168, 1, 35, 35);
       youtubeButton.addMouseListener(new LinkListener("http://localhost"));
       this.frame.getContentPane().add(youtubeButton);
      
       JLabel facebookButton = new JLabel("New label");
       facebookButton.setIcon(new ImageIcon(Main.class.getResource("/resources/facebook.png")));
       facebookButton.setBounds(131, 1, 35, 35);
       facebookButton.addMouseListener(new LinkListener("http://localhost"));
       this.frame.getContentPane().add(facebookButton);
      
       this.progressBar = new JProgressBar(0, 100);
       this.progressBar.setFont(font);
       this.progressBar.setForeground(new Color(204, 0, 0));
       this.progressBar.setBackground(Color.BLACK);
       this.progressBar.setValue(0);
       this.progressBar.setStringPainted(true);
       this.progressBar.setBorderPainted(false);
       this.progressBar.setBounds(9, 542, 649, 18);
       this.frame.getContentPane().add(this.progressBar);
      
       this.statusText = new JLabel("");
       this.statusText.setBackground(Color.WHITE);
       this.statusText.setHorizontalAlignment(0);
       this.statusText.setForeground(Color.WHITE);
       this.statusText.setFont(font);
       this.statusText.setBounds(190, 523, 297, 18);
       this.frame.getContentPane().add(this.statusText);
      
       this.playButton = new JButton(new ImageIcon(Main.class.getResource("/resources/play.png")));
       this.playButton.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
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
      
       JLabel label_1 = new JLabel("");
       label_1.setIcon(new ImageIcon(Main.class.getResource("/resources/header.png")));
       label_1.setBounds(0, 0, 798, 35);
       this.frame.getContentPane().add(label_1);
      
       this.label_2 = new JLabel("");
       this.label_2.setIcon(new ImageIcon(Main.class.getResource("/resources/header.png")));
       this.label_2.setBounds(0, 221, 800, 35);
       this.frame.getContentPane().add(this.label_2);
      
       JLabel label = new JLabel("");
       label.setIcon(new ImageIcon(Main.class.getResource("/resources/background.png")));
       label.setBounds(0, 36, 798, 220);
       this.frame.getContentPane().add(label);
      
       this.chckbxLinksAtBottom = new JCheckBox("Quick-links on Client");
       this.chckbxLinksAtBottom.setFont(new Font("Tahoma", 0, 10));
       this.chckbxLinksAtBottom.setForeground(Color.WHITE);
       this.chckbxLinksAtBottom.setBackground(Color.BLACK);
       this.chckbxLinksAtBottom.setSelected(true);
       this.chckbxLinksAtBottom.setBounds(664, 475, 124, 26);
       this.frame.getContentPane().add(this.chckbxLinksAtBottom);
       fetchNews();
    } catch (Exception e) {
       e.printStackTrace();
    }
    
     this.frame.setVisible(true);
     setStatusText("Please wait - Fetching new files...");
  }
  

  public static Feed getNews()
  {
     RSSFeedParser parser = new RSSFeedParser("https://www.rscrevolution.com/extern.php?action=feed&type=rss&fid=2&show=10&order=posted");
     Feed feed = parser.readFeed();
     return feed;
  }
  
  public static Feed getUpdateLogs() {
     RSSFeedParser parser = new RSSFeedParser("https://www.rscrevolution.com/extern.php?action=feed&type=rss&fid=3&show=10&order=posted");
     Feed feed = parser.readFeed();
     return feed;
  }
  
  private void fetchNews() {
     Feed feed = getNews();
     if (feed == null) {
       return;
    }

     String newsText = "<html><body>";
              FeedMessage msg;
     for (Iterator localIterator = feed.getMessages().iterator(); localIterator.hasNext();) { msg = (FeedMessage)localIterator.next();
       newsText = newsText + "<a href='" + msg.getLink() + "'>" + msg.getTitle() + " - " + msg.getSplitDate() + "</a>";
       newsText = newsText + "<p>" + msg.getDescription().trim().substring(0, 300) + "...<a href='" + msg.getLink() + "'>Read more</a></p>";
       newsText = newsText + "<hr>"; }
    
     newsText = newsText + "</body></html>";
     this.textPane.setDocument(this.kit.createDefaultDocument());
     this.textPane.setText(newsText);
    
     String updateText = "";
     for (FeedMessage message : getUpdateLogs().getMessages()) {
       updateText = updateText + "<a href='" + message.getLink() + "'>" + message.getTitle() + " - " + message.getSplitDate() + "</a>";
       updateText = updateText + "<p>" + message.getDescription().trim().substring(0, Math.min(300, message.getDescription().length())) + "...<a href='" + message.getLink() + "'>Read more</a></p>";
       updateText = updateText + "<hr>";
    }
     this.textPane_1.setDocument(this.kit.createDefaultDocument());
     this.textPane_1.setText(updateText);
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
    
     JFrame gameFrame = new JFrame("Open RSC");
     gameFrame.setIconImage(getResource("RuneScape.png"));
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
       jmenubar.setBackground(Color.BLACK);
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
     label.setForeground(Color.white); //Font color
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
     this.progressBar.setBackground(Color.black);
     this.progressBar.setValue((int)percent);
     this.progressBar.setString((int)percent + "%");
     this.frame.repaint();
  }
  
  public void setDownloadProgress(String f, float percent) {
     this.progressBar.setBackground(Color.black);
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