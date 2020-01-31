package com.loader.openrsc.frame;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.elements.*;
import com.loader.openrsc.frame.listeners.PositionListener;
import com.loader.openrsc.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.Random;

public class AppFrame extends JFrame {
	private static AppFrame instance;
	private JLabel bg;
	private LaunchButton launch;
	private JProgressBar progress;
	private JLabel checkLabel;

	// ORSC section
	private JLabel orsc_status;
	private JLabel orsc_online;

	// RSCC section
	private JLabel rscc_status;
	private JLabel rscc_online;

	// Open PK section
	private JLabel openpk_status;
	private JLabel openpk_online;

	// RSC Preservation section
	private JLabel rscp_status;
	private JLabel rscp_online;

	// Dev World section
	private JLabel dev_status;

	private CheckCombo comboBox;

	private RadioButton rsccRadioButton;
	private RadioButton orscRadioButton;
	private RadioButton openpkRadioButton;
	private RadioButton rscpRadioButton;
	private RadioButton devRadioButton;

	public AppFrame() {
		this.setPreferredSize(new Dimension(795, 555));
		this.setUndecorated(true);
		this.setTitle(Constants.ORSC_GAME_NAME + " Game Launcher");
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
		(this.bg = new JLabel(Utils.getImage("background.png"))).setBounds(0, 0, 800, 560);

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
		// Version text
		JLabel subText;
		(subText = new JLabel("Version " + String.format("%8.6f", Constants.VERSION_NUMBER))).setBounds(335, 490, 170, 15);
		subText.setForeground(new Color(255, 255, 255, 220));
		subText.setFont(Utils.getFont("Helvetica.otf", 1, 10.0f));
		this.bg.add(subText);

		/*
		 * Open RSC
		 */
		// Server status check - spaced 12px apart
		(this.orsc_status = new JLabel(Constants.ORSC_GAME_NAME + ": N/A")).setForeground(Color.WHITE);
		this.orsc_status.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		int orsc_x = 110;
		int orsc_y = 290;
		this.orsc_status.setBounds(orsc_x, orsc_y, 327, 15);
		this.bg.add(this.orsc_status);

		// Online player count - spaced 16px apart
		(this.orsc_online = new JLabel("Players Online: N/A")).setForeground(Color.WHITE);
		this.orsc_online.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		this.orsc_online.setBounds(orsc_x, orsc_y + 13, 327, 15);
		this.bg.add(this.orsc_online);

		/*
		 * RSC Cabbage
		 */
		// Server status check - spaced 12px apart
		(this.rscc_status = new JLabel(Constants.RSCC_GAME_NAME + ": N/A")).setForeground(Color.WHITE);
		this.rscc_status.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		int rscc_x = 360;
		int rscc_y = 290;
		this.rscc_status.setBounds(rscc_x, rscc_y, 327, 15);
		this.bg.add(this.rscc_status);

		// Online player count - spaced 16px apart
		(this.rscc_online = new JLabel("Players Online: N/A")).setForeground(Color.WHITE);
		this.rscc_online.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		this.rscc_online.setBounds(rscc_x, rscc_y + 13, 327, 15);
		this.bg.add(this.rscc_online);

		/*
		 * Open PK
		 */
		// Server status check - spaced 12px apart
		(this.openpk_status = new JLabel(Constants.OPENPK_GAME_NAME + ": N/A")).setForeground(Color.WHITE);
		this.openpk_status.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		int openpk_x = 593;
		int openpk_y = 290; //
		this.openpk_status.setBounds(openpk_x, openpk_y, 327, 15);
		this.bg.add(this.openpk_status);

		// Online player count - spaced 16px apart
		(this.openpk_online = new JLabel("Players Online: N/A")).setForeground(Color.WHITE);
		this.openpk_online.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		this.openpk_online.setBounds(openpk_x, openpk_y + 13, 327, 15);
		this.bg.add(this.openpk_online);

		/*
		 * RSC Preservation
		 */
		// Server status check - spaced 12px apart
		(this.rscp_status = new JLabel(Constants.RSCP_GAME_NAME + ": N/A")).setForeground(Color.WHITE);
		this.rscp_status.setFont(Utils.getFont("Helvetica.otf", 0, 12.0f));
		int rscp_x = 600;
		int rscp_y = 94;
		this.rscp_status.setBounds(rscp_x, rscp_y + 12, 327, 15);
		//this.bg.add(this.rscp_status); // Disabled on purpose

		/*
		 * Dev World
		 */
		// Server status check - spaced 12px apart
		(this.dev_status = new JLabel(Constants.DEV_GAME_NAME + ": N/A")).setForeground(Color.WHITE);
		this.dev_status.setFont(Utils.getFont("Helvetica.otf", 0, 12.0f));
		int dev_x = 600;
		int dev_y = 110;
		this.dev_status.setBounds(dev_x, dev_y + 12, 327, 15);
		//this.bg.add(this.dev_status); // Disabled on purpose

		/*
		 * Sprite pack
		 */
		// Sprite pack
		JLabel sprite_pack = new JLabel("Available sprite packs");
		sprite_pack.setFont(Utils.getFont("Helvetica.otf", 1, 13.0f));
		sprite_pack.setForeground(Color.WHITE);
		sprite_pack.setBounds(585, 116, 150, 15);
		this.bg.add(sprite_pack);

		comboBox = new CheckCombo();
		comboBox.combo.setBounds(585, 132, 150, 30);

		this.bg.add(comboBox.combo);
		(this.progress = new JProgressBar(0, 100)).setBounds(58, 453, 508, 18);
		this.progress.setBackground(new Color(45, 46, 42));
		this.progress.setOpaque(true);
		this.progress.setStringPainted(true);
		this.progress.setBorderPainted(false);
		this.bg.add(this.progress);
	}


	public JLabel getCheckLabel() {
		return this.checkLabel;
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

	private void addButtons() {
		// Link button section
		int link_button_x = 58;
		int link_button_y = 410;
		this.bg.add(new LinkButton(Constants.BUTTON1, new Rectangle(link_button_x, link_button_y, 119, 40)));
		this.bg.add(new LinkButton(Constants.BUTTON2, new Rectangle(link_button_x + 130, link_button_y, 119, 40)));
		this.bg.add(new LinkButton(Constants.BUTTON3, new Rectangle(link_button_x + 260, link_button_y, 119, 40)));
		this.bg.add(new LinkButton(Constants.BUTTON4, new Rectangle(link_button_x + 390, link_button_y, 119, 40)));

		// Launch button section
		(this.launch = new LaunchButton()).setBounds(578, 410, 158, 60);
		this.bg.add(this.launch);

		// Control button section
		this.bg.add(new ControlButton(1, 720, 40, 10, 11)); // Minimize button
		this.bg.add(new ControlButton(2, 738, 40, 10, 11)); // Exit button +18px x

		// Radio button section
		ButtonGroup group = new ButtonGroup();
		orscRadioButton = new RadioButton(new Rectangle(127, 313, 140, 40));
		rsccRadioButton = new RadioButton(new Rectangle(385, 313, 140, 40));
		openpkRadioButton = new RadioButton(new Rectangle(612, 313, 260, 40));
		rscpRadioButton = new RadioButton(new Rectangle(612, 313, 260, 40));
		devRadioButton = new RadioButton(new Rectangle(612, 313, 140, 40));

		rsccRadioButton.setSelected(true); // First radio button is selected by default as launcher will overwrite "Cache/ip.txt" anyway at launch

		group.add(orscRadioButton);
		group.add(rsccRadioButton);
		group.add(openpkRadioButton);
		//group.add(rscpRadioButton);
		//group.add(devRadioButton);

		for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements(); ) {
			RadioButton button = (RadioButton) buttons.nextElement();
			button.setEnabled(false);
			this.bg.add(button);
		}
	}

	public JProgressBar getProgress() {
		return this.progress;
	}

	public LaunchButton getLaunch() {
		return this.launch;
	}

	// RSCC section
	public JLabel getrsccStatus() {
		return this.rscc_status;
	}

	public JLabel getrsccOnline() {
		return this.rscc_online;
	}

	// ORSC section
	public JLabel getorscStatus() {
		return this.orsc_status;
	}

	public JLabel getorscOnline() {
		return this.orsc_online;
	}

	// Open PK section
	public JLabel getopenpkStatus() {
		return this.openpk_status;
	}

	public JLabel getopenpkOnline() {
		return this.openpk_online;
	}

	// RSCP section
	public JLabel getrscpStatus() {
		return this.rscp_status;
	}

	public JLabel getrscpOnline() {
		return this.rscp_online;
	}

	// Dev World section
	public JLabel getdevStatus() {
		return this.dev_status;
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

	public void unlockGameSelection() {
		rsccRadioButton.setEnabled(true);
		orscRadioButton.setEnabled(true);
		openpkRadioButton.setEnabled(true);
		rscpRadioButton.setEnabled(true);
		devRadioButton.setEnabled(true);
	}
}
