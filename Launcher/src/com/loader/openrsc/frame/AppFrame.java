package com.loader.openrsc.frame;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.elements.ControlButton;
import com.loader.openrsc.frame.elements.LaunchButton;
import com.loader.openrsc.frame.elements.LinkButton;
import com.loader.openrsc.frame.listeners.PositionListener;
import com.loader.openrsc.util.Utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import static javax.swing.SwingConstants.LEFT;

public class AppFrame extends JFrame {
	private static AppFrame instance;
	private JLabel bg;
	private LaunchButton launch;
	private JProgressBar progress;
	private JLabel status;
	private JLabel checkLabel;
	private JLabel online;
	private JLabel logins48;
	private JLabel registrationstoday;

	public AppFrame() {
		this.setPreferredSize(new Dimension(795, 555));
		this.setUndecorated(true);
		this.setTitle(Constants.ORSC_GAME_NAME);
		this.setIconImage(Utils.getImage("icon.png").getImage());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		AppFrame.instance = this;
	}

	public static AppFrame get() {
		return AppFrame.instance;
	}

	public void build() {
		Random rand = new Random();
		int value = rand.nextInt(6);
		if (value == 0) {
			(this.bg = new JLabel(Utils.getImage("background.png"))).setBounds(0, 0, 800, 560);
		} else if (value == 1) {
			(this.bg = new JLabel(Utils.getImage("background2.png"))).setBounds(0, 0, 800, 560);
		} else if (value == 2) {
			(this.bg = new JLabel(Utils.getImage("background3.png"))).setBounds(0, 0, 800, 560);
		} else if (value == 3) {
			(this.bg = new JLabel(Utils.getImage("background4.png"))).setBounds(0, 0, 800, 560);
		} else if (value == 4) {
			(this.bg = new JLabel(Utils.getImage("background5.png"))).setBounds(0, 0, 800, 560);
		} else if (value == 5) {
			(this.bg = new JLabel(Utils.getImage("background6.png"))).setBounds(0, 0, 800, 560);
		} else if (value == 6) {
			(this.bg = new JLabel(Utils.getImage("background7.png"))).setBounds(0, 0, 800, 560);
		}

		this.add(this.bg);
		this.addGameSelection();
		this.addButtons();
		this.addMouseListener(new PositionListener(this));
		this.addMouseMotionListener(new PositionListener(this));
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void addGameSelection() {
		// Title
		JLabel text;
		(text = new JLabel(Constants.Title)).setBounds(170, 19, 600, 45);
		text.setForeground(new Color(255, 255, 255, 220));
		text.setFont(Utils.getFont("Exo-Regular.otf", 1, 38.0f));
		this.bg.add(text);

		// Version
		JLabel subText;
		(subText = new JLabel("Version " + String.format("%8.6f", Constants.VERSION_NUMBER))).setBounds(662, 57, 170, 15);
		subText.setForeground(new Color(255, 255, 255, 220));
		subText.setFont(Utils.getFont("Exo-Regular.otf", 1, 10.0f));
		this.bg.add(subText);

		/*
		 * ORSC
		 */
		// Server status check - spaced 12px apart
		(this.status = new JLabel("Server Status: ---")).setForeground(Color.WHITE);
		this.status.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		this.status.setHorizontalAlignment(LEFT);
		this.status.setBounds(230, 160, 327, 15);
		this.bg.add(this.status);

		// Online player count - spaced 16px apart
		(this.online = new JLabel("Players Online: ---")).setForeground(Color.WHITE);
		this.online.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		this.online.setHorizontalAlignment(LEFT);
		this.online.setBounds(230, 172, 327, 15);
		this.bg.add(this.online);

		// Logged in the last 48 hours - spaced 16px apart
		(this.logins48 = new JLabel("Online Last 48 Hours: ---")).setForeground(Color.WHITE);
		this.logins48.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		this.logins48.setHorizontalAlignment(LEFT);
		this.logins48.setBounds(230, 184, 327, 15);
		this.bg.add(this.logins48);
	}

	public JLabel getCheckLabel() {
		return this.checkLabel;
	}

	public void setDownloadProgress(String f, float percent) {
		(this.progress = new JProgressBar(0, 100)).setBounds(27, 530, 508, 18);
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
		this.bg.add(new LinkButton("RSC Wiki", new Rectangle(27, 480, 119, 40)));
		this.bg.add(new LinkButton("Bug Reports", new Rectangle(158, 480, 119, 40)));
		this.bg.add(new LinkButton("Bot Reports", new Rectangle(288, 480, 119, 40)));
		this.bg.add(new LinkButton("Discord", new Rectangle(418, 480, 119, 40)));
		(this.launch = new LaunchButton()).setBounds(617, 481, 174, 69);
		this.bg.add(this.launch);
		this.bg.add(new ControlButton(2, 778, 8, 10, 11)); // Exit
		this.bg.add(new ControlButton(1, 760, 8, 10, 11)); // Minimize
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

	public JLabel getOnline() {
		return this.online;
	}

	public JLabel getLogins48() {
		return this.logins48;
	}
}
