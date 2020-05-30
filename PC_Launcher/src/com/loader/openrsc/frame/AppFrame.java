package com.loader.openrsc.frame;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.elements.*;
import com.loader.openrsc.frame.listeners.PositionListener;
import com.loader.openrsc.util.Utils;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {
	private static AppFrame instance;
	private JLabel bg;
	private JLabel logo;
	private JProgressBar progress;

	private JLabel orsc_online;
	private JLabel rscc_online;
	private JLabel rscp_online;
	private JLabel dev_online;

	private CheckCombo comboBox;
	private LaunchButton launch1;
	private LaunchButton launch2;
	private LaunchButton launch3;
	private LaunchButton launch4;
	private LaunchButton launch5;

	private final int preservation_x = 113;
	private final int openrsc_x = 165;
	private final int cabbage_x = 295;
	private final int openpk_x = 420;
	private final int dev_x = 535;
	private final int launch_button_y = 218;
	private final int link_button_y = 359;

	public AppFrame() {
		this.setPreferredSize(new Dimension(795, 555));
		this.setUndecorated(true);
		this.setTitle(Constants.Title);
		this.setIconImage(Utils.getImage("icon.png").getImage());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		AppFrame.instance = this;
	}

	public static AppFrame get() {
		return AppFrame.instance;
	}

	public void build() {
		(this.bg = new JLabel(Utils.getImage("background.png"))).setBounds(0, 0, 800, 560);
		this.add(this.bg);
		(this.logo = new JLabel(Utils.getImage("rslogo.png"))).setBounds(242, 86, 312, 100);
		this.bg.add(this.logo);
		this.addGameSelection();
		this.addButtons();
		this.addMouseListener(new PositionListener(this));
		this.addMouseMotionListener(new PositionListener(this));
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void setDownloadProgress(String f, float percent) {
		if (percent >= 90) this.progress.setForeground(new Color(0, 153, 0));
		else if (percent >= 80 && percent < 90) this.progress.setForeground(new Color(91, 153, 0));
		else if (percent >= 70 && percent < 80) this.progress.setForeground(new Color(130, 153, 0));
		else if (percent >= 60 && percent < 70) this.progress.setForeground(new Color(153, 147, 0));
		else if (percent >= 50 && percent < 60) this.progress.setForeground(new Color(153, 122, 0));
		else if (percent >= 40 && percent < 50) this.progress.setForeground(new Color(153, 102, 0));
		else if (percent >= 30 && percent < 40) this.progress.setForeground(new Color(153, 63, 0));
		else if (percent >= 20 && percent < 30) this.progress.setForeground(new Color(153, 43, 0));
		else this.progress.setForeground(new Color(153, 0, 0));
		this.progress.setValue((int) percent);
		this.progress.setString(f + " - " + (int) percent + "%");
		this.progress.repaint();
	}

	public void addButtons() {
		// Link button size
		int link_button_width = 130;
		int link_button_height = 74;

		// Link buttons
		final String BUTTON1 = "Discord";
		this.bg.add(new LinkButton(BUTTON1, new Rectangle(101, link_button_y, link_button_width, link_button_height)));

		final String BUTTON2 = "Bug Reports";
		this.bg.add(new LinkButton(BUTTON2, new Rectangle(256, link_button_y, link_button_width, link_button_height)));

		final String BUTTON3 = "Our Wiki";
		this.bg.add(new LinkButton(BUTTON3, new Rectangle(414, link_button_y, link_button_width, link_button_height)));

		final String BUTTON4 = "RSC Wiki";
		this.bg.add(new LinkButton(BUTTON4, new Rectangle(567, link_button_y, link_button_width, link_button_height)));


		// Launch button size
		int launch_button_width = 100;
		int launch_button_height = 100;

		// Launch buttons
		String preservation = "preservation";
		//(this.launch4 = new LaunchButton(openpk)).setBounds(preservation_x, 209, launch_button_width, launch_button_height);
		//this.bg.add(this.launch4);

		String openrsc = "openrsc";
		(this.launch1 = new LaunchButton(openrsc)).setBounds(openrsc_x, launch_button_y, launch_button_width, launch_button_height);
		this.bg.add(this.launch1);

		String cabbage = "cabbage";
		(this.launch2 = new LaunchButton(cabbage)).setBounds(cabbage_x, launch_button_y - 3, launch_button_width, launch_button_height);
		this.bg.add(this.launch2);

		String dev = "dev";
		(this.launch5 = new LaunchButton(dev)).setBounds(dev_x, launch_button_y, launch_button_width, launch_button_height);
		this.bg.add(this.launch5);


		// Control button size
		int control_button_width = 10;
		int control_button_height = 11;

		// Control buttons
		this.bg.add(new ControlButton(1, 695, 60, control_button_width, control_button_height)); // Minimize button
		this.bg.add(new ControlButton(2, 715, 60, control_button_width, control_button_height)); // Exit button
		this.bg.add(new ControlButton(3, 670, 488, 15, 15)); // Delete cache button
	}

	private void addGameSelection() {
		// Version text
		/*JLabel subText; // Disabled unless needed for debugging purposes
		(subText = new JLabel("Version " + String.format("%8.6f", Constants.VERSION_NUMBER))).setBounds(630, 39, 170, 15);
		subText.setForeground(new Color(255, 255, 255, 220));
		subText.setFont(Utils.getFont("Helvetica.otf", 1, 10.0f));
		this.bg.add(subText);*/

		/*
		 * RSC Preservation
		 */
		// Online player count
		(this.rscp_online = new JLabel("Players Online: 0")).setForeground(Color.WHITE);
		this.rscp_online.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		this.rscp_online.setBounds(preservation_x + 10, launch_button_y + 95, 327, 15);
		//this.bg.add(this.rscp_online); // Disabled on purpose

		/*
		 * Open RSC
		 */
		// Online player count
		(this.orsc_online = new JLabel("Players Online: 0")).setForeground(Color.WHITE);
		this.orsc_online.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		this.orsc_online.setBounds(openrsc_x + 10, launch_button_y + 95, 327, 15);
		this.bg.add(this.orsc_online);

		/*
		 * RSC Cabbage
		 */
		// Online player count
		(this.rscc_online = new JLabel("Players Online: 0")).setForeground(Color.WHITE);
		this.rscc_online.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		this.rscc_online.setBounds(cabbage_x + 10, launch_button_y + 95, 327, 15);
		this.bg.add(this.rscc_online);

		comboBox = new CheckCombo();
		//comboBox.combo.setBounds(585, 132, 150, 30);

		this.bg.add(comboBox.combo);
		(this.progress = new JProgressBar(0, 100)).setBounds(125, 488, 540, 18);
		this.progress.setBackground(new Color(45, 46, 42));
		this.progress.setOpaque(true);
		this.progress.setStringPainted(true);
		this.progress.setBorderPainted(false);
		this.bg.add(this.progress);
	}

	public JProgressBar getProgress() {
		return this.progress;
	}

	public LaunchButton getLaunchopenrsc() {
		return this.launch1;
	}

	public LaunchButton getLaunchcabbage() {
		return this.launch2;
	}

	public LaunchButton getLaunchpreservation() {
		return this.launch4;
	}

	public LaunchButton getLaunchdev() {
		return this.launch5;
	}

	public JLabel getrsccOnline() {
		return this.rscc_online;
	}

	public JLabel getorscOnline() {
		return this.orsc_online;
	}

	public JLabel getdevOnline() {
		return this.dev_online;
	}

	public JLabel getrscpOnline() {
		return this.rscp_online;
	}

	// Spritepack combobox
	public CheckCombo getSpriteCombo() {
		return this.comboBox;
	}

	public CheckCombo.store[] getComboBoxState() {
		int entryCount = comboBox.combo.getItemCount();
		CheckCombo.store[] items = new CheckCombo.store[entryCount];
		JComboBox entry = comboBox.combo;
		for (int p = 0; p < entryCount; p++) {
			items[p] = (CheckCombo.store) entry.getItemAt(p);
		}

		return items;
	}
}
