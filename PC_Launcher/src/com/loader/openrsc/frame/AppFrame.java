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

	private JLabel orsc_online;
	private JLabel rscc_online;
	private JLabel openpk_online;
	private JLabel rscp_online;
	private JLabel dev_online;

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
		/*JLabel subText; // Disabled unless needed for debugging purposes
		(subText = new JLabel("Version " + String.format("%8.6f", Constants.VERSION_NUMBER))).setBounds(630, 39, 170, 15);
		subText.setForeground(new Color(255, 255, 255, 220));
		subText.setFont(Utils.getFont("Helvetica.otf", 1, 10.0f));
		this.bg.add(subText);*/

		/*
		 * Open RSC
		 */
		// Online player count - spaced 16px apart
		(this.orsc_online = new JLabel("Players Online: N/A")).setForeground(Color.WHITE);
		this.orsc_online.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		int orsc_x = 110;
		int orsc_y = 290;
		this.orsc_online.setBounds(orsc_x, orsc_y + 13, 327, 15);
		this.bg.add(this.orsc_online);

		/*
		 * RSC Cabbage
		 */
		// Online player count - spaced 16px apart
		(this.rscc_online = new JLabel("Players Online: N/A")).setForeground(Color.WHITE);
		this.rscc_online.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		int rscc_x = 360;
		int rscc_y = 290;
		this.rscc_online.setBounds(rscc_x, rscc_y + 13, 327, 15);
		this.bg.add(this.rscc_online);

		/*
		 * Open PK
		 */
		// Online player count - spaced 16px apart
		(this.openpk_online = new JLabel("Players Online: N/A")).setForeground(Color.WHITE);
		this.openpk_online.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		int openpk_x = 593;
		int openpk_y = 290;
		this.openpk_online.setBounds(openpk_x, openpk_y + 13, 327, 15);
		this.bg.add(this.openpk_online);

		/*
		 * RSC Preservation
		 */
		// Online player count - spaced 16px apart
		(this.rscp_online = new JLabel("Players Online: N/A")).setForeground(Color.WHITE);
		this.rscp_online.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		int rscp_x = 600;
		int rscp_y = 94;
		this.rscp_online.setBounds(rscp_x, rscp_y + 12, 327, 15);
		//this.bg.add(this.rscp_online); // Disabled on purpose

		/*
		 * Dev World
		 */
		// Online player count - spaced 16px apart
		(this.dev_online = new JLabel("Players Online: N/A")).setForeground(Color.WHITE);
		this.dev_online.setFont(Utils.getFont("Helvetica.otf", 0, 11.0f));
		int dev_x = 600;
		int dev_y = 110;
		this.dev_online.setBounds(dev_x, dev_y + 12, 327, 15);
		//this.bg.add(this.dev_online); // Disabled on purpose

		/*
		 * Sprite pack
		 */
		// Sprite pack
		/*JLabel sprite_pack = new JLabel("Available sprite packs"); // Disabled on purpose
		sprite_pack.setFont(Utils.getFont("Helvetica.otf", 1, 13.0f));
		sprite_pack.setForeground(Color.WHITE);
		sprite_pack.setBounds(585, 116, 150, 15);
		this.bg.add(sprite_pack);*/

		comboBox = new CheckCombo();
		//comboBox.combo.setBounds(585, 132, 150, 30);

		this.bg.add(comboBox.combo);
		(this.progress = new JProgressBar(0, 100)).setBounds(125, 477, 540, 28);
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
		int link_button_width = 102;
		int link_button_height = 74;
		this.bg.add(new LinkButton(Constants.BUTTON1, new Rectangle(115, 357, link_button_width, link_button_height)));
		this.bg.add(new LinkButton(Constants.BUTTON2, new Rectangle(270, 357, link_button_width, link_button_height)));
		this.bg.add(new LinkButton(Constants.BUTTON3, new Rectangle(428, 357, link_button_width, link_button_height)));
		this.bg.add(new LinkButton(Constants.BUTTON4, new Rectangle(581, 357, link_button_width, link_button_height)));

		// Launch button section
		(this.launch = new LaunchButton()).setBounds(297, 209, 198, 146);
		this.bg.add(this.launch);

		// Control button section
		this.bg.add(new ControlButton(1, 720, 40, 10, 11)); // Minimize button
		this.bg.add(new ControlButton(2, 738, 40, 10, 11)); // Exit button +18px x

		// Radio button section
		ButtonGroup group = new ButtonGroup();
		orscRadioButton = new RadioButton(new Rectangle(183, 226, 105, 105));
		rsccRadioButton = new RadioButton(new Rectangle(496, 222, 105, 105));
		openpkRadioButton = new RadioButton(new Rectangle(607, 225, 105, 105));
		//rscpRadioButton = new RadioButton(new Rectangle(612, 226, 105, 105));
		//devRadioButton = new RadioButton(new Rectangle(612, 226, 105, 105));

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

	public JLabel getrsccOnline() {
		return this.rscc_online;
	}

	public JLabel getorscOnline() {
		return this.orsc_online;
	}

	public JLabel getopenpkOnline() {
		return this.openpk_online;
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

	public void unlockGameSelection() {
		rsccRadioButton.setEnabled(true);
		orscRadioButton.setEnabled(true);
		openpkRadioButton.setEnabled(true);
		rscpRadioButton.setEnabled(true);
		devRadioButton.setEnabled(true);
	}
}
