package launcher.Fancy;

import launcher.Gameupdater.ProgressBar;
import launcher.Settings;
import launcher.Utils.Defaults;
import launcher.Utils.Logger;
import launcher.Utils.Utils;
import launcher.Utils.WorldPopulations;
import launcher.elements.*;
import launcher.listeners.ButtonListener;
import launcher.listeners.PositionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

import static java.awt.Cursor.getPredefinedCursor;

public class MainWindow extends JFrame {
    private static MainWindow instance;

    public JLabel _BACKGROUND;

    public final MainWindowButtons buttons = new MainWindowButtons();
	public final ServerCard preservationCard = new ServerCard();
	public final ServerCard cabbageCard = new ServerCard();
	public final ServerCard O1ScapeCard = new ServerCard();
	public final ServerCard openPkCard = new ServerCard();
	public final ServerCard kaleCard = new ServerCard();
    public final ServerCard uraniumCard = new ServerCard();
    public final ServerCard coleslawCard = new ServerCard();

	// flea circus fonts
	public static final Font helvetica13 = new Font("Helvetica", Font.PLAIN, 13);
	private final Font helvetica90 = new Font("Helvetica", Font.PLAIN, 90);
	public static final Font helvetica36 = new Font("Helvetica", Font.PLAIN, 36);
	public static final Font helvetica50 = new Font("Helvetica", Font.PLAIN, 50);

	// mudclient177 fonts
	private final Font timesRoman15 = new Font("TimesRoman", Font.PLAIN, 15);
	private final Font helvetica13b = new Font("Helvetica", Font.BOLD, 13);
	private final Font helvetica12 = new Font("Helvetica", Font.PLAIN, 12);
	private final Font helvetica16b = new Font("Helvetica", Font.BOLD, 16);
	private final Font helvetica12b = new Font("Helvetica", Font.BOLD, 12);

	// common font (flea + mc177)
	public static final Font helvetica20b = new Font("Helvetica", Font.BOLD, 20);

	// i need this
	public static final Font helvetica24b = new Font("Helvetica", Font.BOLD, 24);

	public boolean offline_world_count = false;

	public static final Color yellow = new Color(255, 200, 0);
	public static final Color white = new Color(255, 255,  255);
	public static final Color linkColor = new Color(0, 180, 0);

	public MainWindow() {
		if (Settings.undecoratedWindow) {
			this.setPreferredSize(new Dimension(794, 560));
			this.setMinimumSize(new Dimension(794, 560));
		} else {
			this.setPreferredSize(new Dimension(820, 600));
			this.setMinimumSize(new Dimension(820, 600));
		}
        this.setUndecorated(Settings.undecoratedWindow);
        this.setTitle(Defaults._TITLE);
        this.setIconImage(Utils.getImage("icon.png").getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainWindow.instance = this;
    }

    public static MainWindow get() {
        return MainWindow.instance;
    }

    public void build() {
		final JPanel containerPanel = new JPanel(new GridBagLayout());
        (this._BACKGROUND = new JLabel(Utils.getImage("background.png"))).setBounds(0, 0, 794, 560);
		containerPanel.add(this._BACKGROUND);
		containerPanel.setBorder(new EmptyBorder(0,0,0,0));
		containerPanel.setBackground(Color.BLACK);
		this.setContentPane(containerPanel);
        this.defineButtons();
        this.addConstantUIElements();
		this.addButtons();
		this.addServerButtons();
		if (Settings.undecoratedWindow) {
			this.addMouseListener(new PositionListener(this));
			this.addMouseMotionListener(new PositionListener(this));
		}
		this.addComponentListener(
			new ComponentListener() {
				@Override
				public void componentResized(ComponentEvent e) {
					// This is so pack() doesn't resize the window when called if the user has resized.
					e.getComponent().setPreferredSize(new Dimension(e.getComponent().getWidth(), e.getComponent().getHeight()));
				}

				@Override
				public void componentMoved(ComponentEvent e) {
				}

				@Override
				public void componentShown(ComponentEvent e) {
				}

				@Override
				public void componentHidden(ComponentEvent e) {
				}
			}
		);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void defineButtons() {
        // Link button size
        int link_button_width = 25;
        int link_button_height = 25;
        int link_button_spacing = 10;

        // Link buttons
		int ext_links_x = 120; // 64;
		int ext_links_y = 493; // 435;

		buttons.chat_header = new LinkButton("chat", new Rectangle(ext_links_x + 3, ext_links_y - 25, 55, link_button_height));
		buttons.libera = new LinkButton("libera", new Rectangle(ext_links_x, ext_links_y, link_button_width, link_button_height));
		ext_links_x += link_button_width + link_button_spacing;
		buttons.discord = new LinkButton("discord", new Rectangle(ext_links_x, ext_links_y, link_button_width, link_button_height));
		ext_links_x += 90 + link_button_spacing + (link_button_width + link_button_spacing) * 2;
        buttons.cockroach = new LinkButton("cockroach", new Rectangle(ext_links_x, ext_links_y, link_button_width, link_button_height));
		ext_links_x -= (link_button_width + link_button_spacing) / 2;
		buttons.bugs_header = new LinkButton("bugs", new Rectangle(ext_links_x, ext_links_y - 25, 55, link_button_height));
		ext_links_x -= 95 + link_button_spacing;
		buttons.forums_header = new LinkButton("forums", new Rectangle(ext_links_x, ext_links_y - 25, 80, link_button_height));
		ext_links_x += 9;
		buttons.openrsc_forums = new LinkButton("openrsc-forums", new Rectangle(ext_links_x, ext_links_y, link_button_width, link_button_height));
		ext_links_x += link_button_width + link_button_spacing;
		buttons.reddit = new LinkButton("reddit", new Rectangle(ext_links_x, ext_links_y, link_button_width, link_button_height));

		(buttons.fleaCircusEgg = new LaunchButton("fleacircus")).setBounds(58, 53, 20, 20);

		// Control button size
        int control_button_width = 10;
        int control_button_height = 11;

        // Control buttons
        buttons.main_minimize = new ControlButton(ControlButton.MINIMIZE, 695, 60, control_button_width, control_button_height);
        buttons.main_close = new ControlButton(ControlButton.CLOSE, 715, 60, control_button_width, control_button_height);
        buttons.delete_cache = new ControlButton(ControlButton.DELETE_CACHE, 670, 488, 15, 15);

        int robotCheckboxX = 680;
        int robotCheckboxY = 438;
        buttons.robotCheckbox = new CheckboxButton("robot_checkbox", new Rectangle(robotCheckboxX, robotCheckboxY, 50, 25));
        buttons.robotCheckbox.setSelected(Settings.showBotButtons);
        (buttons.robotIcon = new JLabel(Utils.getImage("robot.png"))).setBounds(robotCheckboxX + 20, robotCheckboxY, 25, 25);
		buttons.bots_header = new LinkButton("bots", new Rectangle(robotCheckboxX - 3, robotCheckboxY - 27, 60, 25));

		int link_buttons_x = 202;
		int link_buttons_y = 432;
		buttons.question_mark = new LinkButton("question_mark", new Rectangle(link_buttons_x, link_buttons_y - 15, 22, 35));
		buttons.about_our_servers = new LinkButton("about_our_servers_button", new Rectangle(link_buttons_x + 26, link_buttons_y - 15, 160, 35));

		buttons.gear = new LinkButton("gear", new Rectangle(link_buttons_x + 206, link_buttons_y - 15, 35, 35));
		buttons.client_settings = new LinkButton("client_settings_button", new Rectangle(link_buttons_x + 206 + 35 + 4, link_buttons_y - 15, 160, 35));

		// Launcher version number shown on the settings page
		buttons.version_number = new JLabel("<html>Launcher Version:<br/>" + String.format("%8.6f", Defaults._CURRENT_VERSION) + "</html>");
		buttons.version_number.setFont(helvetica12);
		buttons.version_number.setBorder(new EmptyBorder(0,0,0,0));
		buttons.version_number.setBounds(570, 480, buttons.version_number.getPreferredSize().width, buttons.version_number.getFontMetrics(helvetica12).getHeight() * 2 + 2);
		buttons.version_number.setForeground(yellow);

		buttons.advanced_settings_button = new LinkButton("advanced_settings_button", new Rectangle(430, 485, 120, 25));
    }

    public void addButtons() {
		ProgressBar.setVisible(true);
		this._BACKGROUND = this.buttons.addAll(this._BACKGROUND);
	}

	public void addConstantUIElements() {
		this._BACKGROUND = this.buttons.addConstantUIElements(this._BACKGROUND);
	}

    public void toggleBotServers() {
		this._BACKGROUND.remove(buttons.openrsc_logo);
        if (Settings.showBotButtons) {
			this._BACKGROUND = this.preservationCard.removeAll(this._BACKGROUND);
			this._BACKGROUND = this.cabbageCard.removeAll(this._BACKGROUND);
			this._BACKGROUND = this.O1ScapeCard.removeAll(this._BACKGROUND);
			this._BACKGROUND = this.openPkCard.removeAll(this._BACKGROUND);
			this._BACKGROUND = this.kaleCard.removeAll(this._BACKGROUND);
        } else {
			this._BACKGROUND = this.uraniumCard.removeAll(this._BACKGROUND);
			this._BACKGROUND = this.coleslawCard.removeAll(this._BACKGROUND);
        }
        addServerButtons();
        Settings.saveSettings();
    }

    public void removeAllButtons() {
		if (!Settings.showBotButtons) {
			this._BACKGROUND = this.preservationCard.removeAll(this._BACKGROUND);
			this._BACKGROUND = this.cabbageCard.removeAll(this._BACKGROUND);
			this._BACKGROUND = this.O1ScapeCard.removeAll(this._BACKGROUND);
			this._BACKGROUND = this.openPkCard.removeAll(this._BACKGROUND);
			this._BACKGROUND = this.kaleCard.removeAll(this._BACKGROUND);
		} else {
			this._BACKGROUND = this.uraniumCard.removeAll(this._BACKGROUND);
			this._BACKGROUND = this.coleslawCard.removeAll(this._BACKGROUND);
		}
		this._BACKGROUND = this.buttons.removeAll(this._BACKGROUND);
		ProgressBar.setVisible(false);
		this._BACKGROUND.repaint();
	}

	public void updateWorldTotalTexts() {
		if (Settings.showBotButtons) {
			this.uraniumCard.onlineText.setText(WorldPopulations.worldOnlineTexts[WorldPopulations.URANIUM]);
			this.coleslawCard.onlineText.setText(WorldPopulations.worldOnlineTexts[WorldPopulations.COLESLAW]);
			updateTextBounds(this.uraniumCard.onlineText);
			updateTextBounds(this.coleslawCard.onlineText);
		} else {
			this.preservationCard.onlineText.setText(WorldPopulations.worldOnlineTexts[WorldPopulations.PRESERVATION]);
			this.cabbageCard.onlineText.setText(WorldPopulations.worldOnlineTexts[WorldPopulations.CABBAGE]);
			this.O1ScapeCard.onlineText.setText(WorldPopulations.worldOnlineTexts[WorldPopulations.TWOTHOUSANDONESCAPE]);
			this.openPkCard.onlineText.setText(WorldPopulations.worldOnlineTexts[WorldPopulations.OPENPK]);
			// this.kaleCard.onlineText.setText(WorldPopulations.worldOnlineTexts[WorldPopulations.KALE]);
			updateTextBounds(this.preservationCard.onlineText);
			updateTextBounds(this.cabbageCard.onlineText);
			updateTextBounds(this.O1ScapeCard.onlineText);
			updateTextBounds(this.openPkCard.onlineText);
			// updateTextBounds(this.kaleCard.onlineText);
		}
		this._BACKGROUND.repaint();
	}

	private JLabel updateTextBounds(JLabel jlabel) {
		final Rectangle oldBounds = jlabel.getBounds();
		jlabel.setBounds(oldBounds.x, oldBounds.y, jlabel.getPreferredSize().width, oldBounds.height);
		return jlabel;
	}

	public void addServerButtons() {
		// Launch button size
		int launch_button_width = 100;
		int launch_button_height = 100;

		// x coords
        int preservation_x, uranium_x, O1scape_x;
        int cabbage_x, coleslaw_x, openPk_x, kale_x;

		preservation_x = uranium_x = O1scape_x = 69;
		cabbage_x = coleslaw_x = openPk_x = kale_x = 410;

		// y coords
		int launch_button_row1_y = 75;
		int launch_button_row2_y = launch_button_row1_y + 100 + 10;
		int launch_button_row3_y = launch_button_row2_y + 100 + 10;

        if (!show2001ScapeCard() && !showOpenPk()) {
        	launch_button_row1_y += 20;
        	launch_button_row2_y += 40;
        	launch_button_row3_y += 40;
		} else {
        	launch_button_row1_y -= 11;
		}
		int launch_button_row2_bots_y = launch_button_row2_y;


		buttons.openrsc_logo = new LinkButton("openrsc_sword_logo", new Rectangle(265, launch_button_row1_y, 277, 100));

		String world_total_placeholder_text = "Fetching...";

		/**
		 * RSC Uranium Card
		 */
		(this.uraniumCard.logo = new LaunchButton("uranium")).setBounds(uranium_x, launch_button_row2_bots_y, launch_button_width, launch_button_height);
		this.uraniumCard.clientLogo = defineClientLogo(this.uraniumCard.logo);

		int x, y;
		x = uranium_x + 110;
		y = launch_button_row2_bots_y;

		this.uraniumCard.rscText = makeButton("RSC", x, y, helvetica16b, yellow, Settings.showBotButtons);
		y += this.uraniumCard.rscText.getHeight() - 3;

		this.uraniumCard.serverName = makeButton("Uranium", x, y, helvetica24b, yellow, Settings.showBotButtons);
		y += this.uraniumCard.serverName.getHeight() + 3;

		addLinkMouseListener(this.uraniumCard.serverName, this.uraniumCard.rscText, this.uraniumCard.logo, yellow, "uranium");

		(this.uraniumCard.horizontalRule = new JLabel(Utils.getImage("horizontal.rule.png"))).setBounds(x, y, 208, 3);
		y += 10;

		(this.uraniumCard.wikiLogo = new LaunchButton("uranium_wiki")).setBounds(x, y, 90, 32);

		x += 40;
		this.uraniumCard.wikiText = drawString("Wiki", x, y + 5, helvetica20b, white, Settings.showBotButtons);

		x += 60;
		(this.uraniumCard.playersLogo = new JLabel(Utils.getImage("robot.png"))).setBounds(x, y, 25, 25);


		x += 35;
		this.uraniumCard.onlineText = drawString(world_total_placeholder_text, x, y, helvetica12, white, Settings.showBotButtons);
		y += this.uraniumCard.onlineText.getHeight();

		this.uraniumCard.hiscore = makeButton("Hiscores", x, y, helvetica12, linkColor, Settings.showBotButtons);
		addLinkMouseListener(this.uraniumCard.hiscore, linkColor, "uranium_hiscores");

		/**
		 * RSC Coleslaw Card
		 */
		(this.coleslawCard.logo = new LaunchButton("coleslaw")).setBounds(coleslaw_x, launch_button_row2_bots_y - 3, launch_button_width, launch_button_height);
		this.coleslawCard.clientLogo = defineClientLogo(this.coleslawCard.logo);

		x = coleslaw_x + 110;
		y = launch_button_row2_bots_y;

		this.coleslawCard.rscText = makeButton("RSC", x, y, helvetica16b, yellow, Settings.showBotButtons);
		y += this.coleslawCard.rscText.getHeight() - 3;

		this.coleslawCard.serverName = makeButton("Coleslaw", x, y, helvetica24b, yellow, Settings.showBotButtons);
		y += this.coleslawCard.serverName.getHeight() + 3;

		addLinkMouseListener(this.coleslawCard.serverName, this.coleslawCard.rscText, this.coleslawCard.logo, yellow, "coleslaw");

		(this.coleslawCard.horizontalRule = new JLabel(Utils.getImage("horizontal.rule.png"))).setBounds(x, y, 208, 3);

		y += 10;

		(this.coleslawCard.wikiLogo = new LaunchButton("coleslaw_wiki")).setBounds(x, y, 90, 32);

		x += 40;
		this.coleslawCard.wikiText = drawString("Wiki", x, y + 5, helvetica20b, white, Settings.showBotButtons);
		x += 60;
		(this.coleslawCard.playersLogo = new JLabel(Utils.getImage("robot.png"))).setBounds(x, y, 25, 25);


		x += 35;
		this.coleslawCard.onlineText = drawString(world_total_placeholder_text, x, y, helvetica12, white, Settings.showBotButtons);
		y += this.coleslawCard.onlineText.getHeight();

		this.coleslawCard.hiscore = makeButton("Hiscores", x, y, helvetica12, linkColor, Settings.showBotButtons);
		addLinkMouseListener(this.coleslawCard.hiscore, linkColor, "coleslaw_hiscores");

		/**
		 * RSC Preservation Card
		 */
		(this.preservationCard.logo = new LaunchButton("preservation")).setBounds(preservation_x, launch_button_row2_y, launch_button_width, launch_button_height);
		this.preservationCard.clientLogo = defineClientLogo(this.preservationCard.logo);

		x = preservation_x + 110;
		y = launch_button_row2_y;

		this.preservationCard.rscText = makeButton("RSC", x, y, helvetica16b, yellow, !Settings.showBotButtons);
		y += this.preservationCard.rscText.getHeight() - 3;

		this.preservationCard.serverName = makeButton("Preservation", x, y, helvetica24b, yellow, !Settings.showBotButtons);
		y += this.preservationCard.serverName.getHeight() + 3;

		addLinkMouseListener(this.preservationCard.serverName, this.preservationCard.rscText, this.preservationCard.logo, yellow, "preservation");

		(this.preservationCard.horizontalRule = new JLabel(Utils.getImage("horizontal.rule.png"))).setBounds(x, y, 208, 3);
		y += 10;

		(this.preservationCard.wikiLogo = new LaunchButton("preservation_wiki")).setBounds(x, y, 90, 32);

		x += 40;
		this.preservationCard.wikiText = drawString("Wiki", x, y + 5, helvetica20b, white, !Settings.showBotButtons);

		x += 60;
		(this.preservationCard.playersLogo = new JLabel(Utils.getImage("human.png"))).setBounds(x, y, 26, 32);

		x += 35;
		this.preservationCard.onlineText = drawString(world_total_placeholder_text, x, y, helvetica12, white, !Settings.showBotButtons);
		y += this.preservationCard.onlineText.getHeight();

		this.preservationCard.hiscore = makeButton("Hiscores", x, y, helvetica12, linkColor, !Settings.showBotButtons);
		addLinkMouseListener(this.preservationCard.hiscore, linkColor, "preservation_hiscores");

		/**
		 * RSC Cabbage Card
		 */
		(this.cabbageCard.logo = new LaunchButton("cabbage")).setBounds(cabbage_x, launch_button_row2_y - 3, launch_button_width, launch_button_height);
		this.cabbageCard.clientLogo = defineClientLogo(this.cabbageCard.logo);

		x = cabbage_x + 110;
		y = launch_button_row2_y;

		this.cabbageCard.rscText = makeButton("RSC", x, y, helvetica16b, yellow, !Settings.showBotButtons);
		y += this.cabbageCard.rscText.getHeight() - 3;

		this.cabbageCard.serverName = makeButton("Cabbage", x, y, helvetica24b, yellow, !Settings.showBotButtons);
		y += this.cabbageCard.serverName.getHeight() + 3;

		addLinkMouseListener(this.cabbageCard.serverName, this.cabbageCard.rscText, this.cabbageCard.logo, yellow, "cabbage");

		(this.cabbageCard.horizontalRule = new JLabel(Utils.getImage("horizontal.rule.png"))).setBounds(x, y, 208, 3);
		y += 10;

		(this.cabbageCard.wikiLogo = new LaunchButton("cabbage_wiki")).setBounds(x, y, 90, 32);

		x += 40;
		this.cabbageCard.wikiText = drawString("Wiki", x, y + 5, helvetica20b, white, !Settings.showBotButtons);
		x += 60;
		(this.cabbageCard.playersLogo = new JLabel(Utils.getImage("human.png"))).setBounds(x, y, 26, 32);

		x += 35;
		this.cabbageCard.onlineText = drawString(world_total_placeholder_text, x, y, helvetica12, white, !Settings.showBotButtons);
		y += this.cabbageCard.onlineText.getHeight();

		this.cabbageCard.hiscore = makeButton("Hiscores", x, y, helvetica12, linkColor, !Settings.showBotButtons);
		addLinkMouseListener(this.cabbageCard.hiscore, linkColor, "cabbage_hiscores");

		/**
		 * 2001Scape Card
		 */
		(this.O1ScapeCard.logo = new LaunchButton("2001scape")).setBounds(O1scape_x, launch_button_row3_y - 3, launch_button_width, launch_button_height);
		this.O1ScapeCard.clientLogo = defineClientLogo(this.O1ScapeCard.logo);

		x = O1scape_x + 110;
		y = launch_button_row3_y;

		y += this.cabbageCard.rscText.getHeight() - 3;
		this.O1ScapeCard.rscText = makeButton("2001", x, y, helvetica24b, yellow, show2001ScapeCard());
		this.O1ScapeCard.serverName = makeButton("Scape", x + this.O1ScapeCard.rscText.getWidth(), y + (this.cabbageCard.serverName.getHeight() - 2 - this.cabbageCard.rscText.getHeight()), helvetica16b, yellow, show2001ScapeCard());

		y += this.O1ScapeCard.rscText.getHeight() + 3;

		addLinkMouseListener(this.O1ScapeCard.serverName, this.O1ScapeCard.rscText, this.O1ScapeCard.logo, yellow, "2001scape");

		(this.O1ScapeCard.horizontalRule = new JLabel(Utils.getImage("horizontal.rule.png"))).setBounds(x, y, 208, 3);
		y += 10;

		(this.O1ScapeCard.wikiLogo = new LaunchButton("2001scape_wiki")).setBounds(x, y, 90, 32);

		x += 40;
		this.O1ScapeCard.wikiText = drawString("Wiki", x, y + 5, helvetica20b, white, show2001ScapeCard());
		x += 60;
		(this.O1ScapeCard.playersLogo = new JLabel(Utils.getImage("human.png"))).setBounds(x, y, 26, 32);

		x += 35;
		this.O1ScapeCard.onlineText = drawString(world_total_placeholder_text, x, y, helvetica12, white, show2001ScapeCard());
		y += this.O1ScapeCard.onlineText.getHeight();

		this.O1ScapeCard.hiscore = makeButton("Hiscores", x, y, helvetica12, linkColor, show2001ScapeCard());
		addLinkMouseListener(this.O1ScapeCard.hiscore, linkColor, "2001scape_hiscores");

		/**
		 * OpenPK Card
		 */
		(this.openPkCard.logo = new LaunchButton("openpk")).setBounds(openPk_x, launch_button_row3_y - 3, launch_button_width, launch_button_height);
		this.openPkCard.clientLogo = defineClientLogo(this.openPkCard.logo);

		x = openPk_x + 40;
		y = launch_button_row3_y;

		this.openPkCard.comingSoonPlaceholder = drawString("More to come soon...", x, y + 40, helvetica20b, yellow, !showOpenPk() && !Settings.showBotButtons);
		x += 70;

		y += this.cabbageCard.rscText.getHeight() - 3;
		this.openPkCard.serverName = makeButton("Open PK ", x, y, helvetica24b, yellow, showOpenPk());
		this.openPkCard.rscText = makeButton("(alpha)", x + this.openPkCard.serverName.getWidth(), y + (this.cabbageCard.serverName.getHeight() - 2 - this.cabbageCard.rscText.getHeight()), helvetica16b, yellow, showOpenPk());

		this.openPkCard.underConstruction = new JLabel(Utils.getImage("undercon.gif"));
		this.openPkCard.underConstruction.setBounds(openPk_x, launch_button_row3_y + 62, 38, 38);
		this.openPkCard.underConstruction.setFocusable(false);
		this.openPkCard.underConstruction.setBorder(BorderFactory.createEmptyBorder());

		y += this.openPkCard.serverName.getHeight() + 3;

		addLinkMouseListener(this.openPkCard.serverName, this.openPkCard.rscText, this.openPkCard.logo, yellow, "openpk");

		(this.openPkCard.horizontalRule = new JLabel(Utils.getImage("horizontal.rule.png"))).setBounds(x, y, 208, 3);
		y += 10;

		(this.openPkCard.wikiLogo = new LaunchButton("openpk_wiki")).setBounds(x, y, 90, 32);

		x += 40;
		this.openPkCard.wikiText = drawString("Wiki", x, y + 5, helvetica20b, white, showOpenPk());
		x += 60;
		(this.openPkCard.playersLogo = new JLabel(Utils.getImage("human.png"))).setBounds(x, y, 26, 32);

		x += 35;
		this.openPkCard.onlineText = drawString(world_total_placeholder_text, x, y, helvetica12, white, showOpenPk());
		y += this.openPkCard.onlineText.getHeight();

		this.openPkCard.hiscore = makeButton("Hiscores", x, y, helvetica12, linkColor, showOpenPk());
		addLinkMouseListener(this.openPkCard.hiscore, linkColor, "openpk_hiscores");

		this._BACKGROUND.add(buttons.openrsc_logo);
		if (Settings.showBotButtons) {
			this._BACKGROUND.add(this.uraniumCard.logo);
			this._BACKGROUND.setComponentZOrder(this.uraniumCard.logo, 5);
			this._BACKGROUND.add(this.uraniumCard.clientLogo);
			this._BACKGROUND.setComponentZOrder(this.uraniumCard.clientLogo, 3);
			this._BACKGROUND.add(this.uraniumCard.horizontalRule);
			this._BACKGROUND.add(this.uraniumCard.wikiLogo);
			this._BACKGROUND.setComponentZOrder(this.uraniumCard.wikiLogo, 2);
			this._BACKGROUND.add(this.uraniumCard.playersLogo);
			this._BACKGROUND.add(this.coleslawCard.logo);
			this._BACKGROUND.setComponentZOrder(this.coleslawCard.logo, 5);
			this._BACKGROUND.add(this.coleslawCard.clientLogo);
			this._BACKGROUND.setComponentZOrder(this.coleslawCard.clientLogo, 3);
			this._BACKGROUND.add(this.coleslawCard.horizontalRule);
			this._BACKGROUND.add(this.coleslawCard.wikiLogo);
			this._BACKGROUND.setComponentZOrder(this.coleslawCard.wikiLogo, 2);
			this._BACKGROUND.add(this.coleslawCard.playersLogo);
		} else {
			this._BACKGROUND.add(this.preservationCard.logo);
			this._BACKGROUND.setComponentZOrder(this.preservationCard.logo, 5);
			this._BACKGROUND.add(this.preservationCard.clientLogo);
			this._BACKGROUND.setComponentZOrder(this.preservationCard.clientLogo, 3);
			this._BACKGROUND.add(this.preservationCard.horizontalRule);
			this._BACKGROUND.add(this.preservationCard.wikiLogo);
			this._BACKGROUND.setComponentZOrder(this.preservationCard.wikiLogo, 2);
			this._BACKGROUND.add(this.preservationCard.playersLogo);
			this._BACKGROUND.add(this.cabbageCard.logo);
			this._BACKGROUND.setComponentZOrder(this.cabbageCard.logo, 5);
			this._BACKGROUND.add(this.cabbageCard.clientLogo);
			this._BACKGROUND.setComponentZOrder(this.cabbageCard.clientLogo, 3);
			this._BACKGROUND.add(this.cabbageCard.horizontalRule);
			this._BACKGROUND.add(this.cabbageCard.wikiLogo);
			this._BACKGROUND.setComponentZOrder(this.cabbageCard.wikiLogo, 2);
			this._BACKGROUND.add(this.cabbageCard.playersLogo);
			if (show2001ScapeCard()) {
				this._BACKGROUND.add(this.O1ScapeCard.logo);
				this._BACKGROUND.setComponentZOrder(this.O1ScapeCard.logo, 5);
				this._BACKGROUND.add(this.O1ScapeCard.clientLogo);
				this._BACKGROUND.setComponentZOrder(this.O1ScapeCard.clientLogo, 3);
				this._BACKGROUND.add(this.O1ScapeCard.horizontalRule);
				this._BACKGROUND.add(this.O1ScapeCard.wikiLogo);
				this._BACKGROUND.setComponentZOrder(this.O1ScapeCard.wikiLogo, 2);
				this._BACKGROUND.add(this.O1ScapeCard.playersLogo);
			}
			if (showOpenPk()) {
				this._BACKGROUND.add(this.openPkCard.logo);
				this._BACKGROUND.setComponentZOrder(this.openPkCard.logo, 5);
				this._BACKGROUND.add(this.openPkCard.clientLogo);
				this._BACKGROUND.setComponentZOrder(this.openPkCard.clientLogo, 3);
				this._BACKGROUND.add(this.openPkCard.horizontalRule);
				this._BACKGROUND.add(this.openPkCard.wikiLogo);
				this._BACKGROUND.setComponentZOrder(this.openPkCard.wikiLogo, 2);
				this._BACKGROUND.add(this.openPkCard.playersLogo);
				this._BACKGROUND.add(this.openPkCard.underConstruction);
				this._BACKGROUND.setComponentZOrder(this.openPkCard.underConstruction, 2);
			}
		}
		WorldPopulations.updateWorldPopulations();
		this._BACKGROUND.repaint();
    }

    private void addLinkMouseListener(JButton jlabel, Color color, String actionCommand) {
		addLinkMouseListener(jlabel, null, null, color, actionCommand);
	}

	private void addLinkMouseListener(JButton primary, JButton secondary, LaunchButton ternary, Color color, String actionCommand) {
		MouseListener bigMouseListener = new MouseListener() {
			final Icon normal = null == ternary ? null : ternary.getIcon();
			final Icon rollover = null == ternary ? null : ternary.getRolloverIcon();

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
        // Move the button down a bit when clicked
        primary.setLocation(primary.getX(), primary.getY() + 1);
        if (null != secondary) {
          secondary.setLocation(secondary.getX(), secondary.getY() + 1);
        }
        if (null != ternary) {
          ternary.setLocation(ternary.getX(), ternary.getY() + 1);
        }
      }

			@Override
			public void mouseReleased(MouseEvent e) {
        // Move the button back up when released
        primary.setLocation(primary.getX(), primary.getY() - 1);
        if (null != secondary) {
          secondary.setLocation(secondary.getX(), secondary.getY() - 1);
        }
        if (null != ternary) {
          ternary.setLocation(ternary.getX(), ternary.getY() - 1);
        }
      }

			@Override
			public void mouseEntered(MouseEvent e) {
				primary.setForeground(color.brighter());
				if (null != secondary) {
					secondary.setForeground(color.brighter());
				}
				if (null != ternary) {
					ternary.setIcon(rollover);
				}
				setCursor(getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				primary.setForeground(color);
				if (null  != secondary) {
					secondary.setForeground(color);
				}
				if (null != ternary) {
					ternary.setIcon(normal);
				}
				setCursor(getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		};

		primary.addMouseListener(bigMouseListener);
		primary.setActionCommand(actionCommand);
		primary.addActionListener(new ButtonListener());
		if (null != secondary) {
			secondary.addMouseListener(bigMouseListener);
			secondary.setActionCommand(actionCommand);
			secondary.addActionListener(new ButtonListener());
		}
		if (null != ternary) {
			ternary.addMouseListener(bigMouseListener);
		}
	}

	private JLabel defineClientLogo(LaunchButton logo) {
		return defineClientLogo(logo, null);
	}

	public JLabel defineClientLogo(LaunchButton logo, String preferredClient) {
		String server = logo.getActionCommand();

		int	x = logo.getX() + 68;
		int	y = logo.getY() + 68;

		if (null == preferredClient) {
			switch (server) {
				case "preservation":
					preferredClient = Settings.preferredClientPreservation;
					break;
				case "cabbage":
					preferredClient = Settings.preferredClientCabbage;
					break;
				case "2001scape":
					preferredClient = Settings.preferredClient2001scape;
					break;
				case "kale":
					preferredClient = Settings.preferredClientKale;
					break;
				case "openpk":
					preferredClient = Settings.preferredClientOpenpk;
					break;
				case "uranium":
					preferredClient = Settings.preferredClientUranium;
					break;
				case "coleslaw":
					preferredClient = Settings.preferredClientColeslaw;
					break;
			}
		}

		if (null == preferredClient) {
			Logger.Error("No client defined for server " + server);
			preferredClient = "notdefined";
		}
		ImageIcon icon = null;

		switch (preferredClient) {
			// these are listed in chronological order of compatibility with openrsc server
			case Settings.OPENRSC:
			case ClientSettingsCard.OPENRSC:
				icon = Utils.getImage("openrsc-client-small.png");
				break;
			case Settings.RSCPLUS:
			case ClientSettingsCard.RSCPLUS:
				icon = Utils.getImage("rscplus-small.png");
				break;
			case Settings.APOSBOT:
			case ClientSettingsCard.APOSBOT:
				icon = Utils.getImage("aposbot-small.png");
				break;
			case Settings.IDLERSC:
			case ClientSettingsCard.IDLERSC:
				icon = Utils.getImage("idlersc-small.png");
				break;
			case Settings.WINRUNE:
			case ClientSettingsCard.WINRUNE:
				icon = Utils.getImage("rune.png");
				break;
			case Settings.MUD38:
			case ClientSettingsCard.MUD38:
				icon = Utils.getImage("mudclient38-small.png");
				break;
			case Settings.RSCTIMES:
			case ClientSettingsCard.RSCTIMES:
				icon = Utils.getImage("rsctimes-small.png");
				break;
			case Settings.WEBCLIENT:
			case ClientSettingsCard.WEBCLIENT:
				icon = Utils.getImage("webbrowser-small.png");
				break;
			default:
				icon = Utils.getImage("question_mark-small.png");
				break;
		}

		JLabel clientLogo = new JLabel(icon);
		clientLogo.setBounds(x, y, 32, 32);
		return clientLogo;
	}
	private JLabel drawString(String text, int x, int y, Font font, Color color, boolean add) {
		JLabel a = new JLabel(text);
		a.setFont(font);
		a.setBounds(x, y, a.getPreferredSize().width, a.getFontMetrics(font).getHeight());
		a.setForeground(color);
		if (add)
			this._BACKGROUND.add(a);
		return a;
	}

	public JButton makeButton(String text, int x, int y, Font font, Color color, boolean add) {
		JButton a = new JButton(text);
		a.setFont(font);
		a.setBorder(new EmptyBorder(0,0,0,0));
		a.setBounds(x, y, a.getPreferredSize().width, a.getFontMetrics(font).getHeight());
		a.setForeground(color);
		a.setBorderPainted(false);
		a.setContentAreaFilled(false);
		if (add)
			this._BACKGROUND.add(a);
		return a;
	}

	private boolean show2001ScapeCard() {
		return !Settings.showBotButtons;
	}

	private boolean showOpenPk() {
		return !Settings.showBotButtons && Settings.showPrerelease;
	}

}
