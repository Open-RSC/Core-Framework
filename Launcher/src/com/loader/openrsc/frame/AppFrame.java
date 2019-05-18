package com.loader.openrsc.frame;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.elements.ControlButton;
import com.loader.openrsc.frame.elements.LaunchButton;
import com.loader.openrsc.frame.elements.LinkButton;
import com.loader.openrsc.frame.elements.LinkText;
import com.loader.openrsc.frame.elements.RadioButton;
import com.loader.openrsc.frame.listeners.PositionListener;
import com.loader.openrsc.util.Utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Random;

import javax.swing.ButtonGroup;
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

	// RSCC section
	private JLabel rscc_status;
	private JLabel rscc_online;
	private JLabel rscc_logins48;

	// Open PK section
	private JLabel openpk_status;

	// RSC Preservation section
	private JLabel rscp_status;

	// Dev World section
	private JLabel dev_status;

	// Localhost section
	private JLabel local_status;

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
		this.addGameDescriptions();
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
		(subText = new JLabel("Version " + String.format("%8.6f", Constants.VERSION_NUMBER))).setBounds(650, 54, 170, 15);
		subText.setForeground(new Color(255, 255, 255, 220));
		subText.setFont(Utils.getFont("Exo-Regular.otf", 1, 10.0f));
		this.bg.add(subText);

		/*
		 * Open RSC
		 */
		// Server status check - spaced 12px apart
		(this.orsc_status = new JLabel(Constants.ORSC_GAME_NAME + ": N/A")).setForeground(Color.WHITE);
		this.orsc_status.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		int orsc_x = 630;
		int orsc_y = 142;
		this.orsc_status.setBounds(orsc_x, orsc_y, 327, 15);
		this.bg.add(this.orsc_status);

		// Online player count - spaced 16px apart
		(this.orsc_online = new JLabel("Players Online: N/A")).setForeground(Color.WHITE);
		this.orsc_online.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		this.orsc_online.setBounds(orsc_x, orsc_y + 13, 327, 15);
		this.bg.add(this.orsc_online);

		// Logged in the last 48 hours - spaced 16px apart
		(this.orsc_logins48 = new JLabel("Online Last 48 Hours: N/A")).setForeground(Color.WHITE);
		this.orsc_logins48.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		this.orsc_logins48.setBounds(orsc_x, orsc_y + 26, 327, 15);
		this.bg.add(this.orsc_logins48);

		/*
		 * RSC Cabbage
		 */
		// Server status check - spaced 12px apart
		(this.rscc_status = new JLabel(Constants.RSCC_GAME_NAME + ": N/A")).setForeground(Color.WHITE);
		this.rscc_status.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		int rscc_x = 630;
		int rscc_y = orsc_y + 52; // 192
		this.rscc_status.setBounds(rscc_x, rscc_y, 327, 15);
		this.bg.add(this.rscc_status);

		// Online player count - spaced 16px apart
		(this.rscc_online = new JLabel("Players Online: N/A")).setForeground(Color.WHITE);
		this.rscc_online.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		this.rscc_online.setBounds(rscc_x, rscc_y + 13, 327, 15);
		this.bg.add(this.rscc_online);

		// Logged in the last 48 hours - spaced 16px apart
		(this.rscc_logins48 = new JLabel("Online Last 48 Hours: N/A")).setForeground(Color.WHITE);
		this.rscc_logins48.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		this.rscc_logins48.setBounds(rscc_x, rscc_y + 26, 327, 15);
		this.bg.add(this.rscc_logins48);

		/*
		 * Open PK
		 */
		// Server status check - spaced 12px apart
		(this.openpk_status = new JLabel(Constants.OPENPK_GAME_NAME + ": N/A")).setForeground(Color.WHITE);
		this.openpk_status.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		int openpk_x = 630;
		int openpk_y = rscc_y + 39; //
		this.openpk_status.setBounds(openpk_x, openpk_y + 12, 327, 15);
		this.bg.add(this.openpk_status);

		/*
		 * RSC Preservation
		 */
		// Server status check - spaced 12px apart
		(this.rscp_status = new JLabel(Constants.RSCP_GAME_NAME + ": N/A")).setForeground(Color.WHITE);
		this.rscp_status.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		int rscp_x = 630;
		int rscp_y = openpk_y + 26; //
		this.rscp_status.setBounds(rscp_x, rscp_y + 12, 327, 15);
		this.bg.add(this.rscp_status);

		/*
		 * Dev World
		 */
		// Server status check - spaced 12px apart
		(this.dev_status = new JLabel(Constants.DEV_GAME_NAME + ": N/A")).setForeground(Color.WHITE);
		this.dev_status.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		int dev_x = 630;
		int dev_y = rscp_y + 26; //
		this.dev_status.setBounds(dev_x, dev_y + 12, 327, 15);
		this.bg.add(this.dev_status);

		/*
		 * Localhost
		 */
		// Server status check - spaced 12px apart
		(this.local_status = new JLabel(Constants.LOCALHOST_GAME_NAME + ": N/A")).setForeground(Color.WHITE);
		this.local_status.setFont(Utils.getFont("Exo-Regular.otf", 0, 11.0f));
		int local_x = 630;
		int local_y = dev_y + 26; //
		this.local_status.setBounds(local_x, local_y + 12, 327, 15);
		this.bg.add(this.local_status);
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
		// Link button section
		int link_button_x = 28;
		int link_button_y = 480;
		this.bg.add(new LinkButton(Constants.BUTTON1, new Rectangle(link_button_x, link_button_y, 119, 40)));
		this.bg.add(new LinkButton(Constants.BUTTON2, new Rectangle(link_button_x + 130, link_button_y, 119, 40)));
		this.bg.add(new LinkButton(Constants.BUTTON3, new Rectangle(link_button_x + 260, link_button_y, 119, 40)));
		this.bg.add(new LinkButton(Constants.BUTTON4, new Rectangle(link_button_x + 390, link_button_y, 119, 40)));

		JLabel friends;
		(friends = new JLabel("Great 3rd party RSC private servers to also check out:")).setBounds(70, 375, 600, 25);
		friends.setForeground(new Color(255, 255, 255, 220));
		friends.setFont(Utils.getFont("Exo-Regular.otf", 1, 17.0f));
		this.bg.add(friends);

		int friend_link_x = 75;
		int friend_link_y = 400;
		this.bg.add(new LinkText("RSC Arch Angel", new Rectangle(friend_link_x, friend_link_y, 140, 20)));
		//this.bg.add(new LinkText("RSC Dawn", new Rectangle(friend_link_x + 145, friend_link_y, 150, 20)));
		this.bg.add(new LinkText("RSC Revolution", new Rectangle(friend_link_x + (150), friend_link_y, 150, 20)));

		JLabel spacer1;
		(spacer1 = new JLabel("|")).setBounds(friend_link_x + 150, 400, 10, 20);
		spacer1.setForeground(new Color(255, 0, 0, 220));
		spacer1.setFont(Utils.getFont("Exo-Regular.otf", 1, 12.0f));
		this.bg.add(spacer1);

		/*JLabel spacer2;
		(spacer2 = new JLabel("|")).setBounds(friend_link_x + 285, 400, 10, 20);
		spacer2.setForeground(new Color(255, 0, 0, 220));
		spacer2.setFont(Utils.getFont("Exo-Regular.otf", 1, 12.0f));
		this.bg.add(spacer2);*/

		// Launch button section
		(this.launch = new LaunchButton()).setBounds(617, 477, 174, 69);
		this.bg.add(this.launch);

		// Control button section
		this.bg.add(new ControlButton(1, 755, 8, 10, 11)); // Minimize button
		this.bg.add(new ControlButton(2, 773, 8, 10, 11)); // Exit button +18px x

		// Radio button section
		int orsc_radio_x = 30;
		int orsc_radio_y = 142;
		int rscc_radio_y = orsc_radio_y + 28;
		int openpk_radio_y = rscc_radio_y + 28;
		int rscp_radio_y = openpk_radio_y + 28;
		int dev_radio_y = rscp_radio_y + 28;
		int local_radio_y = dev_radio_y + 28;

		ButtonGroup group = new ButtonGroup();
		RadioButton orscRadioButton = new RadioButton(Constants.ORSC_GAME_NAME, new Rectangle(orsc_radio_x, orsc_radio_y, 140, 40));
		RadioButton rsccRadioButton = new RadioButton(Constants.RSCC_GAME_NAME, new Rectangle(orsc_radio_x, rscc_radio_y, 140, 40));
		RadioButton openpkRadioButton = new RadioButton(Constants.OPENPK_GAME_NAME, new Rectangle(orsc_radio_x, openpk_radio_y, 260, 40));
		RadioButton rscpRadioButton = new RadioButton(Constants.RSCP_GAME_NAME, new Rectangle(orsc_radio_x, rscp_radio_y, 260, 40));
		RadioButton devRadioButton = new RadioButton(Constants.DEV_GAME_NAME, new Rectangle(orsc_radio_x, dev_radio_y, 140, 40));
		RadioButton localRadioButton = new RadioButton(Constants.LOCALHOST_GAME_NAME, new Rectangle(orsc_radio_x, local_radio_y, 140, 40));

		orscRadioButton.setSelected(true); // First radio button is selected by default as launcher will overwrite "Cache/ip.txt" anyway at launch

		group.add(orscRadioButton);
		group.add(rsccRadioButton);
		group.add(openpkRadioButton);
		group.add(rscpRadioButton);
		group.add(devRadioButton);
		group.add(localRadioButton);

		this.bg.add(orscRadioButton);
		this.bg.add(rsccRadioButton);
		this.bg.add(openpkRadioButton);
		this.bg.add(rscpRadioButton);
		this.bg.add(devRadioButton);
		this.bg.add(localRadioButton);
	}

	private void addGameDescriptions() {
		int orsc_x = 143;
		int orsc_y = 152;
		int rscc_x = 165;
		int rscc_y = orsc_y + 29; // 188
		int openpk_y = rscc_y + 29; //
		int rscp_y = openpk_y + 29; //
		int openpk_x = 185;
		int rscp_x = 240;
		int dev_x = 150;
		int dev_y = rscp_y + 27; // 215
		int local_x = 165;
		int local_y = dev_y + 29; // 242

		// Open RSC
		JLabel orscDescription;
		(orscDescription = new JLabel("-> 1x xp rate, bank notes, quick banking, near-authentic")).setBounds(orsc_x, orsc_y, 600, 18);
		orscDescription.setForeground(new Color(255, 255, 255, 220));
		orscDescription.setFont(Utils.getFont("Exo-Regular.otf", 0, 13.0f));
		this.bg.add(orscDescription);

		// RSC Cabbage
		JLabel rsccDescription;
		(rsccDescription = new JLabel("-> 5x xp rate, runs 30% faster, auction house, batched skills, etc")).setBounds(rscc_x, rscc_y, 600, 18);
		rsccDescription.setForeground(new Color(255, 255, 255, 220));
		rsccDescription.setFont(Utils.getFont("Exo-Regular.otf", 0, 13.0f));
		this.bg.add(rsccDescription);

		// Open PK
		JLabel openpkDescription;
		(openpkDescription = new JLabel("-> Stork PK remake")).setBounds(openpk_x, openpk_y, 600, 18);
		openpkDescription.setForeground(new Color(255, 255, 255, 220));
		openpkDescription.setFont(Utils.getFont("Exo-Regular.otf", 0, 13.0f));
		this.bg.add(openpkDescription);

		// RSC Preservation
		JLabel rscpDescription;
		(rscpDescription = new JLabel("-> authentic RSC")).setBounds(rscp_x, rscp_y, 600, 18);
		rscpDescription.setForeground(new Color(255, 255, 255, 220));
		rscpDescription.setFont(Utils.getFont("Exo-Regular.otf", 0, 13.0f));
		this.bg.add(rscpDescription);

		// Dev World
		JLabel devDescription;
		(devDescription = new JLabel("-> experimental world for testing the latest code")).setBounds(dev_x, dev_y, 600, 18);
		devDescription.setForeground(new Color(255, 255, 255, 220));
		devDescription.setFont(Utils.getFont("Exo-Regular.otf", 0, 13.0f));
		this.bg.add(devDescription);

		// Single Player
		JLabel localhostDescription;
		(localhostDescription = new JLabel("-> requires first starting the single player server")).setBounds(local_x, local_y, 600, 18);
		localhostDescription.setForeground(new Color(255, 255, 255, 220));
		localhostDescription.setFont(Utils.getFont("Exo-Regular.otf", 0, 13.0f));
		this.bg.add(localhostDescription);
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

	// Open PK section
	public JLabel getopenpkStatus() {
		return this.openpk_status;
	}

	// RSCP section
	public JLabel getrscpStatus() {
		return this.rscp_status;
	}

	// Dev World section
	public JLabel getdevStatus() {
		return this.dev_status;
	}

	// Localhost section
	public JLabel getlocalStatus() {
		return this.local_status;
	}
}
