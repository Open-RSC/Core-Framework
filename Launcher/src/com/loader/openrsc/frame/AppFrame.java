package com.loader.openrsc.frame;

import java.awt.Rectangle;
import java.awt.Color;

import java.awt.Dimension;
import javax.swing.JProgressBar;
import com.loader.openrsc.frame.elements.ControlButton;
import com.loader.openrsc.frame.elements.LaunchButton;
import com.loader.openrsc.frame.elements.LinkButton;
import com.loader.openrsc.frame.elements.NewsBox;
import com.loader.openrsc.frame.listeners.PositionListener;
import com.loader.openrsc.net.xml.XMLReader;
import com.loader.openrsc.util.Utils;

import javax.swing.JLabel;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class AppFrame extends JFrame
{
	public static AppFrame instance;
    private NewsBox box;
    private JLabel logo;
    private JLabel text;
    private JLabel subText;
    private JLabel bg;
    private LaunchButton launch;
    private JProgressBar progress;
    private JLabel status;
    private JLabel postedDate;
    private JLabel checkLabel;
    
    public AppFrame() {
        this.setPreferredSize(new Dimension(980, 560));
        this.setUndecorated(true);
        this.setTitle("Open RSC");
        this.setIconImage(Utils.getImage("icon.png").getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AppFrame.instance = this;
    }
    
    public void build() {
        (this.bg = new JLabel(Utils.getImage("background.png"))).setBounds(0, 0, 980, 560);
        
        this.add(this.bg);
        this.addLogo();
        this.addButtons();
        this.addNewsBox();
        (this.postedDate = new JLabel(XMLReader.getNews().getMessages().get(0).getSplitDate())).setBounds(131, 116, 128, 8);
        this.postedDate.setFont(Utils.getFont("OpenSans-Regular.ttf", 1, 10.0f));
        this.postedDate.setHorizontalAlignment(0);
        this.bg.add(this.postedDate);
        this.addMouseListener(new PositionListener(this));
        this.addMouseMotionListener(new PositionListener(this));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    private void addNewsBox() {
        (this.box = new NewsBox()).setBounds(36, 100, 451, 340);
        this.bg.add(this.box);
        XMLReader.init(this.box);
    }
    
    private void addLogo() {
        (this.logo = new JLabel(Utils.getImage("logo.png"))).setBounds(12, 12, 47, 47);
        this.bg.add(this.logo);
        (this.text = new JLabel("openrsc".toUpperCase())).setBounds(65, 24, 100, 15);
        this.text.setForeground(new Color(126, 141, 9));
        this.text.setFont(Utils.getFont("OpenSans-Regular.ttf", 1, 12.0f));
        this.bg.add(this.text);
        (this.subText = new JLabel("LOADER")).setBounds(65, 35, 100, 15);
        this.subText.setForeground(new Color(200, 200, 200));
        this.subText.setFont(Utils.getFont("OpenSans-Regular.ttf", 1, 10.0f));
        this.bg.add(this.subText);
        (this.status = new JLabel("Server Status: ---")).setForeground(Color.WHITE);
        this.status.setFont(Utils.getFont("OpenSans-Regular.ttf", 0, 12.0f));
        this.status.setHorizontalAlignment(4);
        this.status.setBounds(625, 74, 315, 19);
        this.bg.add(this.status);
    }
    
    public JLabel getCheckLabel() {
        return this.checkLabel;
    }

	public void setDownloadProgress(String f, float percent) {
		(this.progress = new JProgressBar(0, 100)).setBounds(27, 530 , 640, 18);
		this.progress.setForeground(new Color(0x412B0A));  //new Color(131,147,9));
		this.progress.setBackground(new Color(0x2D2E2A));
		this.progress.setFont(Utils.getFont("OpenSans-Regular.ttf", 1, 12.0f));
		this.progress.setOpaque(true);
		this.progress.setStringPainted(true);
		this.progress.setBorderPainted(false);
		this.progress.setValue((int) percent);
		this.progress.setString(f + " - " + (int) percent + "%");
		this.bg.add(this.progress);
		this.progress.repaint();

	}
	
    
    private void addButtons() {
        this.bg.add(new LinkButton("Website", new Rectangle(27, 480, 119, 40)));
        this.bg.add(new LinkButton("Forums", new Rectangle(158, 480, 119, 40)));
        this.bg.add(new LinkButton("Shop", new Rectangle(288, 480, 119, 40)));
        this.bg.add(new LinkButton("Support", new Rectangle(418, 480, 119, 40)));
        this.bg.add(new LinkButton("Register", new Rectangle(548, 480, 119, 40)));
        (this.launch = new LaunchButton()).setBounds(797, 481, 174, 69);
        this.bg.add(this.launch);
        /**
         * TODO Checkbox and force update? not needed I think.
         */
	        this.bg.add(new ControlButton(2, 958, 8, 10, 11));
	        this.bg.add(new ControlButton(1, 940, 8, 10, 11));
        /**
         * END.
         */
    }
    
    public NewsBox getBox() {
        return this.box;
    }
    
    public static AppFrame get() {
        return AppFrame.instance;
    }
    
    public JProgressBar getProgress() {
        return this.progress;
    }
    
    public LaunchButton getLaunch() {
        return this.launch;
    }
    
    public JLabel getStatus() {
        return this.status;
    }
}
