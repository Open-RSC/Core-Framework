package launcher.Fancy;

import launcher.Settings;
import launcher.Utils.Defaults;
import launcher.Utils.Utils;
import launcher.elements.*;
import launcher.listeners.PositionListener;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private static MainWindow instance;

    private JLabel _BACKGROUND;
    private JLabel _LOGO;
    private CheckCombo comboBox;
    private LaunchButton launch1;
    private LaunchButton launch2;
    private LaunchButton launch3;
    private LaunchButton launch4;
    private LaunchButton launch5;
	private LaunchButton launch6;
	private LaunchButton launch7;
	private LaunchButton launch8;

	public MainWindow() {
        this.setPreferredSize(new Dimension(795, 555));
        this.setUndecorated(true);
        this.setTitle(Defaults._TITLE);
        this.setIconImage(Utils.getImage("icon.png").getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainWindow.instance = this;
    }

    public static MainWindow get() {
        return MainWindow.instance;
    }

    public void build() {
        (this._BACKGROUND = new JLabel(Utils.getImage("background.png"))).setBounds(0, 0, 800, 560);
        this.add(this._BACKGROUND);
        (this._LOGO = new JLabel(Utils.getImage("openrsc_sword_logo.png"))).setBounds(265, 86, 277, 100);
        this._BACKGROUND.add(this._LOGO);
        this.addButtons();
        this.addMouseListener(new PositionListener(this));
        this.addMouseMotionListener(new PositionListener(this));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void addButtons() {
        // Link button size
        int link_button_width = 130;
        int link_button_height = 74;

        // Link buttons
        final String BUTTON1 = "Discord";
		int link_button_y = 359;
		this._BACKGROUND.add(new LinkButton(BUTTON1, new Rectangle(101, link_button_y, link_button_width, link_button_height)));

        final String BUTTON2 = "Bug Reports";
        this._BACKGROUND.add(new LinkButton(BUTTON2, new Rectangle(256, link_button_y, link_button_width, link_button_height)));

        final String BUTTON3 = "Our Wiki";
        this._BACKGROUND.add(new LinkButton(BUTTON3, new Rectangle(414, link_button_y, link_button_width, link_button_height)));

        final String BUTTON4 = "RSC Wiki";
        this._BACKGROUND.add(new LinkButton(BUTTON4, new Rectangle(567, link_button_y, link_button_width, link_button_height)));

        // Launch Server's Client buttons
        addServerButtons();

        // Control button size
        int control_button_width = 10;
        int control_button_height = 11;

        // Control buttons
        this._BACKGROUND.add(new ControlButton(1, 695, 60, control_button_width, control_button_height)); // Minimize button
        this._BACKGROUND.add(new ControlButton(2, 715, 60, control_button_width, control_button_height)); // Exit button
        this._BACKGROUND.add(new ControlButton(3, 670, 488, 15, 15)); // Delete cache button

        int robotCheckboxX = 690;
        int robotCheckboxY = 438;
        CheckboxButton robotCheckbox = new CheckboxButton("", new Rectangle(robotCheckboxX, robotCheckboxY, 50, 25));
        robotCheckbox.setSelected(Settings.showBotButtons);
        this._BACKGROUND.add(robotCheckbox);
        (this._LOGO = new JLabel(Utils.getImage("robot.png"))).setBounds(robotCheckboxX + 20, robotCheckboxY, 25, 25);
        this._BACKGROUND.add(this._LOGO);
    }

    public void toggleBotServers() {
        if (Settings.showBotButtons) {
            this._BACKGROUND.remove(this.launch1);
            this._BACKGROUND.remove(this.launch2);
			this._BACKGROUND.remove(this.launch3);
        } else {
            this._BACKGROUND.remove(this.launch4);
            this._BACKGROUND.remove(this.launch5);
			this._BACKGROUND.remove(this.launch6);
			this._BACKGROUND.remove(this.launch7);
			this._BACKGROUND.remove(this.launch8);
        }
        addServerButtons();
        Settings.saveSettings();
    }

    private void addServerButtons() {
        int preservation_x = 0;
        int cabbage_x = 0;
        int uranium_x = 0;
        int coleslaw_x = 0;
        int rscplus_x = 50;
		int apos_x = 50;
		int idlersc_x = 50;
		int launch_button_y = 218;
		int apos_y = launch_button_y - 100;
		int idlersc_y = launch_button_y - 20;

        // Launch button size
        int launch_button_width = 100;
        int launch_button_height = 100;

        if (Settings.showBotButtons) {
            uranium_x = 230;
            coleslaw_x = 477;
        } else {
        	preservation_x = 230;
            cabbage_x = 477;
        }

        if (Settings.showBotButtons) {
			String uranium = "uranium";
            (this.launch4 = new LaunchButton(uranium)).setBounds(uranium_x, launch_button_y, launch_button_width, launch_button_height);
            this._BACKGROUND.add(this.launch4);

            String coleslaw = "coleslaw";
            (this.launch5 = new LaunchButton(coleslaw)).setBounds(coleslaw_x, launch_button_y, launch_button_width, launch_button_height);
            this._BACKGROUND.add(this.launch5);

			String rscplus = "rscplus";
			(this.launch6 = new LaunchButton(rscplus)).setBounds(rscplus_x, launch_button_y - 170, launch_button_width, launch_button_height);
			this._BACKGROUND.add(this.launch6);

			String apos = "apos";
			(this.launch7 = new LaunchButton(apos)).setBounds(apos_x, apos_y, launch_button_width, launch_button_height);
			this._BACKGROUND.add(this.launch7);

			String idlersc = "idlersc";
			(this.launch8 = new LaunchButton(idlersc)).setBounds(idlersc_x, idlersc_y, launch_button_width, launch_button_height);
			this._BACKGROUND.add(this.launch8);

			this._BACKGROUND.repaint();
        } else {
            String preservation = "preservation";
            (this.launch1 = new LaunchButton(preservation)).setBounds(preservation_x, launch_button_y, launch_button_width, launch_button_height);
            this._BACKGROUND.add(this.launch1);

            String cabbage = "cabbage";
            (this.launch2 = new LaunchButton(cabbage)).setBounds(cabbage_x, launch_button_y - 3, launch_button_width, launch_button_height);
            this._BACKGROUND.add(this.launch2);

			String rscplus = "rscplus";
			(this.launch3 = new LaunchButton(rscplus)).setBounds(rscplus_x, launch_button_y - 170, launch_button_width, launch_button_height);
			this._BACKGROUND.add(this.launch3);

			this._BACKGROUND.repaint();
        }
        this._BACKGROUND.repaint();
    }
}
