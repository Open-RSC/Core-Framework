package com.loader.openrsc.frame;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.elements.ControlButton;
import com.loader.openrsc.frame.elements.LaunchButton;
import com.loader.openrsc.frame.elements.LinkButton;
import com.loader.openrsc.frame.elements.NewsBox;
import com.loader.openrsc.frame.listeners.PositionListener;
import com.loader.openrsc.net.xml.XMLReader;
import com.loader.openrsc.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

@SuppressWarnings("serial")
public class AppFrame extends JFrame {
    public static AppFrame instance;
    private NewsBox box;
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
        this.setTitle(Constants.GAME_NAME);
        this.setIconImage(Utils.getImage("icon.png").getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AppFrame.instance = this;
    }

    public static AppFrame get() {
        return AppFrame.instance;
    }

    public void build() {
        Random rand = new Random();
        int value = rand.nextInt(3);
        if (value == 0) {
            (this.bg = new JLabel(Utils.getImage("background.png"))).setBounds(0, 0, 980, 560);
        } else if (value == 1) {
            (this.bg = new JLabel(Utils.getImage("background2.png"))).setBounds(0, 0, 980, 560);
        } else {
            (this.bg = new JLabel(Utils.getImage("background3.png"))).setBounds(0, 0, 980, 560);
        }

        this.add(this.bg);
        this.addLogo();
        this.addButtons();
        /*this.addNewsBox();
        (this.postedDate = new JLabel(XMLReader.getNews().getMessages().get(0).getSplitDate())).setBounds(131, 116, 128, 8);
        this.postedDate.setFont(Utils.getFont("Exo-Regular.otf", 1, 8.0f));
        this.postedDate.setHorizontalAlignment(0);
        this.bg.add(this.postedDate);*/
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
        (this.text = new JLabel(Constants.GAME_NAME.toUpperCase())).setBounds(30, 24, 100, 15);
        this.text.setForeground(new Color(255, 223, 0));
        this.text.setFont(Utils.getFont("Exo-Regular.otf", 1, 14.0f));
        this.bg.add(this.text);
        (this.subText = new JLabel("Game Launcher")).setBounds(30, 35, 100, 15);
        this.subText.setForeground(new Color(200, 200, 200));
        this.subText.setFont(Utils.getFont("Exo-Regular.otf", 1, 12.0f));
        this.bg.add(this.subText);
        (this.status = new JLabel("Server Status: ---")).setForeground(Color.WHITE);
        this.status.setFont(Utils.getFont("Exo-Regular.otf", 0, 12.0f));
        this.status.setHorizontalAlignment(4);
        this.status.setBounds(625, 74, 315, 19);
        this.bg.add(this.status);
    }

    public JLabel getCheckLabel() {
        return this.checkLabel;
    }

    public void setDownloadProgress(String f, float percent) {
        (this.progress = new JProgressBar(0, 100)).setBounds(27, 530, 640, 18);
        if (percent >= 90) this.progress.setForeground(new Color(0, 153, 0));
        else if (percent >= 80 && percent < 90) this.progress.setForeground(new Color(91, 153, 0));
        else if (percent >= 70 && percent < 80) this.progress.setForeground(new Color(130, 153, 0));
        else if (percent >= 60 && percent < 70) this.progress.setForeground(new Color(153, 147, 0));
        else if (percent >= 50 && percent < 60) this.progress.setForeground(new Color(153, 122, 0));
        else if (percent >= 40 && percent < 50) this.progress.setForeground(new Color(153, 102, 0));
        else if (percent >= 30 && percent < 40) this.progress.setForeground(new Color(153, 63, 0));
        else if (percent >= 20 && percent < 30) this.progress.setForeground(new Color(153, 43, 0));
        else this.progress.setForeground(new Color(153, 0, 0));
        this.progress.setBackground(new Color(45, 46, 42));
        this.progress.setFont(Utils.getFont("Exo-Regular.otf", 1, 11.0f));
        this.progress.setOpaque(true);
        this.progress.setStringPainted(true);
        this.progress.setBorderPainted(false);
        this.progress.setValue((int) percent);
        this.progress.setString(f + " - " + (int) percent + "%");
        this.bg.add(this.progress);
        this.progress.repaint();
    }

    private void addButtons() {
        this.bg.add(new LinkButton("News", new Rectangle(27, 480, 119, 40)));
        this.bg.add(new LinkButton("Bug Reports", new Rectangle(158, 480, 119, 40)));
        this.bg.add(new LinkButton("Discord", new Rectangle(288, 480, 119, 40)));
        this.bg.add(new LinkButton("GitHub", new Rectangle(418, 480, 119, 40)));
        this.bg.add(new LinkButton("FAQ", new Rectangle(548, 480, 119, 40)));
        (this.launch = new LaunchButton()).setBounds(797, 481, 174, 69);
        this.bg.add(this.launch);
        this.bg.add(new ControlButton(2, 958, 8, 10, 11));
        this.bg.add(new ControlButton(1, 940, 8, 10, 11));
    }

    public NewsBox getBox() {
        return this.box;
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
