package com.loader.openrsc.frame;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.elements.ControlButton;
import com.loader.openrsc.frame.elements.LaunchButton;
import com.loader.openrsc.frame.elements.LinkButton;
import com.loader.openrsc.frame.elements.RadioButton;
import com.loader.openrsc.frame.listeners.PositionListener;
import com.loader.openrsc.util.Utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class AppFrame extends JFrame {
	private static AppFrame instance;
	private JLabel bg;
	private LaunchButton launch;
	private JProgressBar progress;
	private JLabel checkLabel;

	// ORSC section
	private JLabel orsc_status;
	private JLabel orsc_online;
	private JLabel orsc_logins48;
	int orsc_x = 220;
	int orsc_y = 135;

	// RSCC section
	private JLabel rscc_status;
	private JLabel rscc_online;
	private JLabel rscc_logins48;
	int rscc_x = 220;
	int rscc_y = 180;

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
		// Header text
		JLabel text;
		(text = new JLabel(Constants.Title)).setBounds(250, 19, 600, 45);
		text.setForeground(new Color(255, 255, 255, 220));
		text.setFont(Utils.getFont("Exo-Regular.otf", 1, 24.0f));
		this.bg.add(text);

		// Version text
		JLabel subText;
		(subText = new JLabel("Version " + String.format("%8.6f", Constants.VERSION_NUMBER))).setBounds(662, 56, 170, 15);
		subText.setForeground(new Color(255, 255, 255, 220));
		subText.setFont(Utils.getFont("Exo-Regular.otf", 1, 10.0f));
		this.bg.add(subText);

		/*
		 * Open RSC section
		 */
		// Server status check - spaced 12px apart
		(this.orsc_status = new JLabel("Server Status: checking...")).setForeground(Color.WHITE);
		this.orsc_status.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		this.orsc_status.setBounds(orsc_x, orsc_y, 327, 15);
		this.bg.add(this.orsc_status);

		// Online player count - spaced 16px apart
		(this.orsc_online = new JLabel("Players Online: checking...")).setForeground(Color.WHITE);
		this.orsc_online.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		this.orsc_online.setBounds(orsc_x, orsc_y + 12, 327, 15);
		this.bg.add(this.orsc_online);

		// Logged in the last 48 hours - spaced 16px apart
		(this.orsc_logins48 = new JLabel("Online Last 48 Hours: checking...")).setForeground(Color.WHITE);
		this.orsc_logins48.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		this.orsc_logins48.setBounds(orsc_x, orsc_y + 24, 327, 15);
		this.bg.add(this.orsc_logins48);

		/*
		 * RSC Cabbage section
		 */
		// Server status check - spaced 12px apart
		(this.rscc_status = new JLabel("Server Status: checking...")).setForeground(Color.WHITE);
		this.rscc_status.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		this.rscc_status.setBounds(rscc_x, rscc_y, 327, 15);
		this.bg.add(this.rscc_status);

		// Online player count - spaced 16px apart
		(this.rscc_online = new JLabel("Players Online: checking...")).setForeground(Color.WHITE);
		this.rscc_online.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		this.rscc_online.setBounds(rscc_x, rscc_y + 12, 327, 15);
		this.bg.add(this.rscc_online);

		// Logged in the last 48 hours - spaced 16px apart
		(this.rscc_logins48 = new JLabel("Online Last 48 Hours: checking...")).setForeground(Color.WHITE);
		this.rscc_logins48.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		this.rscc_logins48.setBounds(rscc_x, rscc_y + 24, 327, 15);
		this.bg.add(this.rscc_logins48);
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
		// Link buttons
		this.bg.add(new LinkButton("RSC Wiki", new Rectangle(27, 480, 119, 40)));
		this.bg.add(new LinkButton("Bug Reports", new Rectangle(158, 480, 119, 40)));
		this.bg.add(new LinkButton("Bot Reports", new Rectangle(288, 480, 119, 40)));
		this.bg.add(new LinkButton("Discord", new Rectangle(418, 480, 119, 40)));

		// Launch button
		(this.launch = new LaunchButton()).setBounds(617, 477, 174, 69);
		this.bg.add(this.launch);

		// Control buttons
		this.bg.add(new ControlButton(1, 755, 8, 10, 11)); // Minimize button
		this.bg.add(new ControlButton(2, 773, 8, 10, 11)); // Exit button +18px x

		// Radio buttons
		this.bg.add(new RadioButton("Open RSC", new Rectangle(80, orsc_y, 119, 40)));
		this.bg.add(new RadioButton("RSC Cabbage", new Rectangle(80, rscc_y, 119, 40)));
	}

	public JProgressBar getProgress() {
		return this.progress;
	}

	public LaunchButton getLaunch() {
		return this.launch;
	}

	// ORSC section
	public JLabel getorscStatus() {
		return this.orsc_status;
	}

	public JLabel getorscOnline() {
		return this.orsc_online;
	}

	public JLabel getorscLogins48() {
		return this.orsc_logins48;
	}

	// RSCC section
	public JLabel getrsccStatus() {
		return this.rscc_status;
	}

	public JLabel getrsccOnline() {
		return this.rscc_online;
	}

	public JLabel getrsccLogins48() {
		return this.rscc_logins48;
	}
}
