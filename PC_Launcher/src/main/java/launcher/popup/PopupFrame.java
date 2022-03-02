package launcher.popup;

import launcher.Fancy.MainWindow;
import launcher.Settings;
import launcher.Utils.CmdRunner;
import launcher.Utils.Logger;
import launcher.Utils.Utils;
import launcher.elements.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;

public class PopupFrame extends JLabel {
	public static PopupFrame instance;

	private static final long serialVersionUID = 8654472888657426168L;
	private JLabel POPUP_BACKGROUND;

	public static final int LAUNCHER_SETTINGS = 0;
	public static final int CLIENT_SETTINGS = 1;

	private final int CLIENT_LOGO_LARGE_X = 10;
	private final int CLIENT_LOGO_LARGE_Y = 120;
	private final int EXPLANATION_X = 145;
	private final int EXPLANATION_Y = 120;
	private final int CLIENT_CHOOSER_X = 10;
	private final int CLIENT_CHOOSER_Y = 245;

	private final int SERVER_X = 150;
	private final int SERVER_Y = 10;
	private final String preservationName = "RSC Preservation";
	private final String cabbageName = "RSC Cabbage";
	private final String O1scapeName = "2001scape";
	private final String kaleName = "RSC Kale";
	private final String openPKName = "Open PK (pre-release)";
	private final String uraniumName = "RSC Uranium";
	private final String coleslawName = "RSC Coleslaw";
	private final int PRESERVATION_IDX = 0;
	private final int CABBAGE_IDX = 1;
	private final int O1SCAPE_IDX = 2;
	// private final int KALE_IDX = ++idx;
	private final int OPENPK_IDX = 3;
	private final int URANIUM_IDX = 4;
	private final int COLESLAW_IDX = 5;
	private final String[] serverChooserNames = new String[] { preservationName, cabbageName, O1scapeName, openPKName, uraniumName, coleslawName };
	private final JComboBox serverChooser = new JComboBox(serverChooserNames);
	private int serverChooserLastIdx = 0;
	private JButton[] serverNameRSC;
	private JButton[] serverName;
	private JLabel horizontalRule;

	HashMap<String, String> clientNameTranslator = new HashMap<String, String>();

	public static ClientSettingsCard preservationSettingsCard = new ClientSettingsCard();
	public static ClientSettingsCard cabbageSettingsCard = new ClientSettingsCard();
	public static ClientSettingsCard O1scapeSettingsCard = new ClientSettingsCard();
	public static ClientSettingsCard openpkSettingsCard = new ClientSettingsCard();
	public static ClientSettingsCard uraniumSettingsCard = new ClientSettingsCard();
	public static ClientSettingsCard coleslawSettingsCard = new ClientSettingsCard();

	public PopupFrame(int popupId) {
		build(popupId);
	}

	public static PopupFrame get() {
		return PopupFrame.instance;
	}

	private void build(int popupId) {
		defineClientSettingLookup();
		ClientDescriptions.init();
		setBounds(new Rectangle(58, 51, 692, 405));
		setLayout(null);

		(this.POPUP_BACKGROUND = new JLabel()).setBounds(0, 0, 689, 405);
		POPUP_BACKGROUND.setForeground(Color.WHITE);
		POPUP_BACKGROUND.setHorizontalAlignment(SwingConstants.CENTER);
		POPUP_BACKGROUND.setFont(Utils.getFont("Helvetica.otf", 0, 12.0F));
		add(POPUP_BACKGROUND);

		int control_button_width = 10;
		int control_button_height = 11;
		ControlButton popup_minimize = new ControlButton(ControlButton.MINIMIZE, 637, 9, control_button_width, control_button_height);
		ControlButton popup_close = new ControlButton(ControlButton.CLOSE, 657, 9, control_button_width, control_button_height);
		if (Settings.undecoratedWindow) {
			POPUP_BACKGROUND.add(popup_minimize);
			POPUP_BACKGROUND.add(popup_close);
		}

		LaunchButton fleaCircusEgg = new LaunchButton("fleacircus");
		fleaCircusEgg.setBounds(0, 2, 20, 20);
		POPUP_BACKGROUND.add(fleaCircusEgg);

		// COMMON POPUP CONTENT
		int x = 28; // 144;
		int y = 366;

		LinkButton question_mark = new LinkButton("question_mark", new Rectangle(x, y, 22, 35));
		LinkButton about_our_servers = new LinkButton("about_our_servers_button", new Rectangle(x + 22 + 4, y, 160, 35));
		x += 209;

		LinkButton floppy_disk = new LinkButton("floppy_disk", new Rectangle(x, y, 35, 35));
		LinkButton apply_and_save_button  = new  LinkButton("apply_and_save_button", new Rectangle(x + 35 + 5, y, 160, 35));

		x += 206 + 12;
		LinkButton gear = new LinkButton("exit_gear", new Rectangle(x, y, 35, 35));
		LinkButton close = new LinkButton("exit_settings_button", new Rectangle(x + 35 + 4, y, 160, 35));

		POPUP_BACKGROUND.add(floppy_disk);
		POPUP_BACKGROUND.add(apply_and_save_button);
		POPUP_BACKGROUND.add(question_mark);
		POPUP_BACKGROUND.add(about_our_servers);
		POPUP_BACKGROUND.add(gear);
		POPUP_BACKGROUND.add(close);

		// different pages
		switch (popupId) {
			case LAUNCHER_SETTINGS:
				// setMessage("About our servers");
				buildLauncherSettingsPage();
				break;
			case CLIENT_SETTINGS:
				buildClientSettingsPage();
				break;
		}
		instance = this;
		MainWindow.get()._BACKGROUND.revalidate();
		MainWindow.get().pack();
		MainWindow.get()._BACKGROUND.repaint();

	}

	private void buildLauncherSettingsPage() {
		int x = 65;
		int y = 35;

		JLabel header = new JLabel("Advanced Settings");
		header.setFont(MainWindow.helvetica24b);
		header.setBorder(new EmptyBorder(0,0,0,0));
		int headerHeight = header.getFontMetrics(MainWindow.helvetica24b).getHeight();
		header.setBounds(x, y, header.getPreferredSize().width, headerHeight);
		header.setForeground(MainWindow.yellow);
		POPUP_BACKGROUND.add(header);
		y += headerHeight + 10;

		// TODO: divider

		// undecorated window
		int labelWidth = makeCheckboxLabel("<html>Custom Window Chrome (aka Undecorated Window) <font color=red>(requires restart)</font></html>", x + 22, y + 4, POPUP_BACKGROUND);
		CheckboxButton undecorated = new CheckboxButton("undecorated_checkbox", new Rectangle(x, y, 25 + labelWidth, 25));
		undecorated.setSelected(Settings.undecoratedWindowSave);
		POPUP_BACKGROUND.add(undecorated);
		y += 40;

		// auto-update
		labelWidth = makeCheckboxLabel("<html>Automatically update launcher on start <font color=red>(requires restart)</font></html>", x + 22, y + 4, POPUP_BACKGROUND);
		CheckboxButton autoupdate = new CheckboxButton("autoupdate_checkbox", new Rectangle(x, y, 25 + labelWidth, 25));
		autoupdate.setSelected(Settings.autoUpdate);
		POPUP_BACKGROUND.add(autoupdate);
		y += 40;

		// toggle pre-release servers on
		labelWidth = makeCheckboxLabel("<html>Show pre-release servers on main page</html>", x + 22, y + 4, POPUP_BACKGROUND);
		CheckboxButton showPrerelease = new CheckboxButton("show_prerelease_checkbox", new Rectangle(x, y, 25 + labelWidth + 21, 25));
		showPrerelease.setSelected(Settings.showPrerelease);
		POPUP_BACKGROUND.add(showPrerelease);

		JLabel dukeConstruction = new JLabel(Utils.getImage("dukeconstruction.gif"));
		dukeConstruction.setBounds(x + 25 + labelWidth, y, 21, 24);
		dukeConstruction.setFocusable(false);
		dukeConstruction.setBorder(BorderFactory.createEmptyBorder());
		POPUP_BACKGROUND.add(dukeConstruction);
		y += 40;

		// IP

		// PORT

		// RSA KEY

		// EXPONENT

	}

	private void buildClientSettingsPage() {
		setupCard(preservationSettingsCard,
			new String[] { ClientSettingsCard.WINRUNE, ClientSettingsCard.RSCPLUS, ClientSettingsCard.WEBCLIENT, ClientSettingsCard.OPENRSC },
			Settings.preferredClientPreservation,
			MainWindow.get().preservationCard.logo,
			!Settings.showBotButtons,
			ClientDescriptions.preservationClientDescriptions,
			false);

		setupCard(cabbageSettingsCard,
			new String[] { ClientSettingsCard.OPENRSC },
			Settings.preferredClientCabbage,
			MainWindow.get().cabbageCard.logo,
			false,
			ClientDescriptions.cabbageClientDescriptions,
			false);

		setupCard(O1scapeSettingsCard,
			new String[] { ClientSettingsCard.MUD38, ClientSettingsCard.RSCTIMES, ClientSettingsCard.WEBCLIENT },
			Settings.preferredClient2001scape,
			MainWindow.get().O1ScapeCard.logo,
			false,
			ClientDescriptions.O1scapeClientDescriptions,
			false);

		setupCard(openpkSettingsCard,
			new String[] { ClientSettingsCard.OPENRSC },
			Settings.preferredClientOpenpk,
			MainWindow.get().openPkCard.logo,
			false,
			ClientDescriptions.openpkClientDescriptions,
			true);

		setupCard(uraniumSettingsCard,
			new String[] { ClientSettingsCard.WINRUNE, ClientSettingsCard.APOSBOT, ClientSettingsCard.RSCPLUS, ClientSettingsCard.WEBCLIENT, ClientSettingsCard.OPENRSC },
			Settings.preferredClientUranium,
			MainWindow.get().uraniumCard.logo,
			Settings.showBotButtons,
			ClientDescriptions.uraniumClientDescriptions,
			false);

		setupCard(coleslawSettingsCard,
			new String[] { ClientSettingsCard.IDLERSC, ClientSettingsCard.OPENRSC },
			Settings.preferredClientColeslaw,
			MainWindow.get().coleslawCard.logo,
			false,
			ClientDescriptions.coleslawClientDescriptions,
			false);

		// SET UP CONSTANT UI ELEMENTS
		final int SERVER_CHOOSER_WIDTH = 200;
		serverChooser.setMinimumSize(new Dimension(SERVER_CHOOSER_WIDTH, 28));
		serverChooser.setMaximumSize(new Dimension(SERVER_CHOOSER_WIDTH, 28));
		serverChooser.setPreferredSize(new Dimension(SERVER_CHOOSER_WIDTH, 28));
		serverChooser.setAlignmentY((float) 0.75);

		serverChooser.setBounds(SERVER_X + 110, SERVER_Y + 65, SERVER_CHOOSER_WIDTH, 28);
		POPUP_BACKGROUND.add(serverChooser);

		(horizontalRule = new JLabel(Utils.getImage("horizontal.rule.png"))).setBounds(SERVER_X + 110, SERVER_Y + 50, 208, 3);
		POPUP_BACKGROUND.add(horizontalRule);

		serverNameRSC = new JButton[serverChooserNames.length];
		serverNameRSC[PRESERVATION_IDX] = MainWindow.get().preservationCard.getRscText();
		serverNameRSC[CABBAGE_IDX] = MainWindow.get().cabbageCard.getRscText();
		serverNameRSC[O1SCAPE_IDX] = MainWindow.get().O1ScapeCard.getRscText();
		// serverNameRSC[KALE_IDX] = MainWindow.get().kaleCard.getRscText();
		serverNameRSC[OPENPK_IDX] = MainWindow.get().openPkCard.getRscText();
		serverNameRSC[URANIUM_IDX] = MainWindow.get().uraniumCard.getRscText();
		serverNameRSC[COLESLAW_IDX] = MainWindow.get().coleslawCard.getRscText();

		serverName = new JButton[serverChooserNames.length];
		serverName[PRESERVATION_IDX] = MainWindow.get().preservationCard.getServerName();
		serverName[CABBAGE_IDX] = MainWindow.get().cabbageCard.getServerName();
		serverName[O1SCAPE_IDX] = MainWindow.get().O1ScapeCard.getServerName();
		// serverName[KALE_IDX] = MainWindow.get().kaleCard.getServerName();
		serverName[OPENPK_IDX] = MainWindow.get().openPkCard.getServerName();
		serverName[URANIUM_IDX] = MainWindow.get().uraniumCard.getServerName();
		serverName[COLESLAW_IDX] = MainWindow.get().coleslawCard.getServerName();

		for (int i = 0; i < serverName.length; i++) {
			Rectangle origNameBounds = serverName[i].getBounds();
			Rectangle origRSCBounds = serverNameRSC[i].getBounds();
			if (i == O1SCAPE_IDX) {
				origNameBounds.x += (SERVER_X + 110) - origRSCBounds.x;
				origRSCBounds.x = SERVER_X + 110;
				origNameBounds.y += (SERVER_Y + 20) - origRSCBounds.y;
				origRSCBounds.y = SERVER_Y + 20;
			} else if (i == OPENPK_IDX) {
				origRSCBounds.x += (SERVER_X + 110) - origNameBounds.x;
				origNameBounds.x = SERVER_X + 110;
				origRSCBounds.y += (SERVER_Y + 20) - origNameBounds.y;
				origNameBounds.y = SERVER_Y + 20;
			} else {
				origRSCBounds.x = SERVER_X + 110;
				origNameBounds.x = origRSCBounds.x;
				origRSCBounds.y += (SERVER_Y + 20) - origNameBounds.y;
				origNameBounds.y = SERVER_Y + 20;
			}
			serverNameRSC[i].setBounds(origRSCBounds);
			serverName[i].setBounds(origNameBounds);
		}

		// show Preservation or Uranium at first
		if (Settings.showBotButtons) {
			POPUP_BACKGROUND.add(serverNameRSC[URANIUM_IDX]);
			POPUP_BACKGROUND.add(serverName[URANIUM_IDX]);
			POPUP_BACKGROUND.add(uraniumSettingsCard.logo);
			POPUP_BACKGROUND.setComponentZOrder(uraniumSettingsCard.logo, 5);
			POPUP_BACKGROUND.add(uraniumSettingsCard.clientLogoSmall);
			POPUP_BACKGROUND.setComponentZOrder(uraniumSettingsCard.clientLogoSmall, 3);
			serverChooser.setSelectedIndex(URANIUM_IDX);
			serverChooserLastIdx = URANIUM_IDX;
		} else {
			POPUP_BACKGROUND.add(serverNameRSC[PRESERVATION_IDX]);
			POPUP_BACKGROUND.add(serverName[PRESERVATION_IDX]);
			POPUP_BACKGROUND.add(preservationSettingsCard.logo);
			POPUP_BACKGROUND.setComponentZOrder(preservationSettingsCard.logo, 5);
			POPUP_BACKGROUND.add(preservationSettingsCard.clientLogoSmall);
			POPUP_BACKGROUND.setComponentZOrder(preservationSettingsCard.clientLogoSmall, 3);
			serverChooser.setSelectedIndex(PRESERVATION_IDX);
			serverChooserLastIdx = PRESERVATION_IDX;
		}

		serverChooser.addActionListener(arg0 -> {
			serverChooserActionListener();
		});
	}

	private JLabel updateSmallClientIcon(ActionEvent arg0, LaunchButton launchButton, String[] jComboBoxItems) {
		JComboBox clientChooser = (JComboBox) arg0.getSource();
		return MainWindow.get().defineClientLogo(launchButton, jComboBoxItems[clientChooser.getSelectedIndex()]);
	}

	private LinkButton updateLargeClientIcon(ActionEvent arg0, LaunchButton launchButton, String[] jComboBoxItems) {
		JComboBox clientChooser = (JComboBox) arg0.getSource();
		return defineClientLogoButton(launchButton, clientNameTranslator.get(jComboBoxItems[clientChooser.getSelectedIndex()]));
	}

	private void defineClientSettingLookup() {
		this.clientNameTranslator.put(Settings.RSCPLUS, ClientSettingsCard.RSCPLUS);
		this.clientNameTranslator.put(Settings.WINRUNE, ClientSettingsCard.WINRUNE);
		this.clientNameTranslator.put(Settings.OPENRSC, ClientSettingsCard.OPENRSC);
		this.clientNameTranslator.put(Settings.MUD38, ClientSettingsCard.MUD38);
		this.clientNameTranslator.put(Settings.IDLERSC, ClientSettingsCard.IDLERSC);
		this.clientNameTranslator.put(Settings.APOSBOT, ClientSettingsCard.APOSBOT);
		this.clientNameTranslator.put(Settings.RSCTIMES, ClientSettingsCard.RSCTIMES);
		this.clientNameTranslator.put(Settings.WEBCLIENT, ClientSettingsCard.WEBCLIENT);

		this.clientNameTranslator.put(ClientSettingsCard.RSCPLUS, Settings.RSCPLUS);
		this.clientNameTranslator.put(ClientSettingsCard.WINRUNE, Settings.WINRUNE);
		this.clientNameTranslator.put(ClientSettingsCard.OPENRSC, Settings.OPENRSC);
		this.clientNameTranslator.put(ClientSettingsCard.MUD38, Settings.MUD38);
		this.clientNameTranslator.put(ClientSettingsCard.IDLERSC, Settings.IDLERSC);
		this.clientNameTranslator.put(ClientSettingsCard.APOSBOT, Settings.APOSBOT);
		this.clientNameTranslator.put(ClientSettingsCard.RSCTIMES, Settings.RSCTIMES);
		this.clientNameTranslator.put(ClientSettingsCard.WEBCLIENT, Settings.WEBCLIENT);
	}

	private void setIndexFromSave(JComboBox clientChooser, String preferredClient) {
		String searchMe = clientNameTranslator.get(preferredClient);
		for (int i = 0; i < clientChooser.getItemCount(); i++) {
			if (clientChooser.getItemAt(i).equals(searchMe)) {
				clientChooser.setSelectedIndex(i);
				return;
			}
		}
	}
	public void hideFrame() {
		setVisible(false);
		MainWindow.get().addButtons();
		MainWindow.get().addServerButtons();
	}

	public void showFrame() {
		MainWindow.get().removeAllButtons();
		setVisible(true);
		MainWindow.get()._BACKGROUND.add(this);
		MainWindow.get().pack(); // needed so that dropdown boxes show the arrow on the right.
		MainWindow.get()._BACKGROUND.repaint();
	}

	public void setVisible(boolean visible) {
		POPUP_BACKGROUND.setVisible(visible);
		repaint();
		MainWindow.get().pack(); // needed so that dropdown boxes show the arrow on the right.
		MainWindow.get()._BACKGROUND.repaint();
	}

	public void clientChooserAction(ActionEvent actionEvent, ClientSettingsCard serverCard) {
		POPUP_BACKGROUND.remove(serverCard.clientLogoSmall);
		serverCard.clientLogoSmall =
			updateSmallClientIcon(actionEvent, serverCard.logo, serverCard.clientNames);
		POPUP_BACKGROUND.add(serverCard.clientLogoSmall);
		POPUP_BACKGROUND.setComponentZOrder(serverCard.clientLogoSmall, 3);

		POPUP_BACKGROUND.remove(serverCard.explanationTexts[serverCard.activeExplanation]);
		serverCard.activeExplanation = serverCard.clientChooser.getSelectedIndex();
		Dimension newSize = serverCard.explanationTexts[serverCard.activeExplanation].getPreferredSize();
		serverCard.explanationTexts[serverCard.clientChooser.getSelectedIndex()].setBounds(
			EXPLANATION_X, EXPLANATION_Y, (int)newSize.getWidth(), (int)newSize.getHeight());
		POPUP_BACKGROUND.add(serverCard.explanationTexts[serverCard.activeExplanation]);

		POPUP_BACKGROUND.remove(serverCard.clientLogoLarge);
		serverCard.clientLogoLarge =
			updateLargeClientIcon(actionEvent, serverCard.logo, serverCard.clientNames);
		POPUP_BACKGROUND.add(serverCard.clientLogoLarge);

		POPUP_BACKGROUND.repaint();
	}

	public void setupCard(ClientSettingsCard serverCard, String[] clientNames, String preferredClient, LaunchButton logo, boolean active, HashMap<String, JLabel> clientDescriptions, boolean isPrerelease) {
		final int CLIENT_CHOOSER_WIDTH = 120;
		serverCard.clientNames = clientNames;
		serverCard.clientChooser = new JComboBox(serverCard.clientNames);
		serverCard.clientChooser.setMinimumSize(new Dimension(CLIENT_CHOOSER_WIDTH, 28));
		serverCard.clientChooser.setMaximumSize(new Dimension(CLIENT_CHOOSER_WIDTH, 28));
		serverCard.clientChooser.setPreferredSize(new Dimension(CLIENT_CHOOSER_WIDTH, 28));
		serverCard.clientChooser.setAlignmentY((float) 0.75);
		setIndexFromSave(serverCard.clientChooser, preferredClient);

		serverCard.logo = logo;
		serverCard.logo.setBounds(SERVER_X, SERVER_Y, 100, 100);
		serverCard.clientLogoSmall = MainWindow.get().defineClientLogo(serverCard.logo, serverCard.clientChooser.getSelectedItem().toString());

		serverCard.clientLogoLarge = defineClientLogoButton(serverCard.logo, serverCard.clientChooser.getSelectedItem().toString());
		serverCard.clientChooser.setBounds(CLIENT_CHOOSER_X, CLIENT_CHOOSER_Y, CLIENT_CHOOSER_WIDTH, 28);

		serverCard.explanationTexts = new JLabel[serverCard.clientNames.length];
		for (int i = 0; i < serverCard.clientNames.length; i++) {
			serverCard.explanationTexts[i] = clientDescriptions.getOrDefault(serverCard.clientNames[i], ClientDescriptions.unknownClient);
		}

		serverCard.activeExplanation = serverCard.clientChooser.getSelectedIndex();
		Dimension size = serverCard.explanationTexts[serverCard.activeExplanation].getPreferredSize();
		serverCard.explanationTexts[serverCard.activeExplanation].setBounds(
			EXPLANATION_X, EXPLANATION_Y, (int)size.getWidth(), (int)size.getHeight());

		serverCard.clientChooser.addActionListener(arg0 -> {
			clientChooserAction(arg0, serverCard);
		});

		if (active) {
			POPUP_BACKGROUND.add(serverCard.clientLogoLarge);
			POPUP_BACKGROUND.add(serverCard.clientChooser);
			POPUP_BACKGROUND.add(serverCard.explanationTexts[serverCard.activeExplanation]);
		}

		serverCard.isPrerelease = isPrerelease;
		if (isPrerelease) {
			serverCard.constructionLogo = new JLabel(Utils.getImage("undercon.gif"));
			serverCard.constructionLogo.setBounds(SERVER_X, SERVER_Y + 62, 38, 38);
			serverCard.constructionLogo.setFocusable(false);
			serverCard.constructionLogo.setBorder(BorderFactory.createEmptyBorder());
		}
	}

	private void serverChooserActionListener() {
		POPUP_BACKGROUND.remove(serverName[serverChooserLastIdx]);
		POPUP_BACKGROUND.remove(serverNameRSC[serverChooserLastIdx]);
		switch (serverChooserLastIdx) {
			case PRESERVATION_IDX:
				POPUP_BACKGROUND = preservationSettingsCard.removeAll(POPUP_BACKGROUND);
				break;
			case CABBAGE_IDX:
				POPUP_BACKGROUND = cabbageSettingsCard.removeAll(POPUP_BACKGROUND);
				break;
			case O1SCAPE_IDX:
				POPUP_BACKGROUND = O1scapeSettingsCard.removeAll(POPUP_BACKGROUND);
				break;
			case OPENPK_IDX:
				POPUP_BACKGROUND = openpkSettingsCard.removeAll(POPUP_BACKGROUND);
				break;
			case URANIUM_IDX:
				POPUP_BACKGROUND = uraniumSettingsCard.removeAll(POPUP_BACKGROUND);
				break;
			case COLESLAW_IDX:
				POPUP_BACKGROUND = coleslawSettingsCard.removeAll(POPUP_BACKGROUND);
				break;
			default:
				Logger.Error("Unimplemented server in serverChooserListener remove section");
				break;
		}

		POPUP_BACKGROUND.add(serverName[serverChooser.getSelectedIndex()]);
		POPUP_BACKGROUND.add(serverNameRSC[serverChooser.getSelectedIndex()]);
		switch (serverChooser.getSelectedIndex()) {
			case PRESERVATION_IDX:
				POPUP_BACKGROUND = preservationSettingsCard.addAll(POPUP_BACKGROUND);
				break;
			case CABBAGE_IDX:
				POPUP_BACKGROUND = cabbageSettingsCard.addAll(POPUP_BACKGROUND);
				break;
			case O1SCAPE_IDX:
				POPUP_BACKGROUND = O1scapeSettingsCard.addAll(POPUP_BACKGROUND);
				break;
			case OPENPK_IDX:
				POPUP_BACKGROUND = openpkSettingsCard.addAll(POPUP_BACKGROUND);
				break;
			case URANIUM_IDX:
				POPUP_BACKGROUND = uraniumSettingsCard.addAll(POPUP_BACKGROUND);
				break;
			case COLESLAW_IDX:
				POPUP_BACKGROUND = coleslawSettingsCard.addAll(POPUP_BACKGROUND);
				break;
			default:
				Logger.Error("Unimplemented server in serverChooserListener add section");
				break;
		}

		serverChooserLastIdx = serverChooser.getSelectedIndex();
		POPUP_BACKGROUND.revalidate();
		POPUP_BACKGROUND.repaint();
	}

	public void saveClientSelectionsToSettings() {
		Settings.preferredClientPreservation = clientNameTranslator.getOrDefault(
			preservationSettingsCard.clientNames[preservationSettingsCard.clientChooser.getSelectedIndex()],
			Settings.preferredClientPreservation
		);
		Settings.preferredClientCabbage = clientNameTranslator.getOrDefault(
			cabbageSettingsCard.clientNames[cabbageSettingsCard.clientChooser.getSelectedIndex()],
			Settings.preferredClientCabbage
		);
		Settings.preferredClient2001scape = clientNameTranslator.getOrDefault(
			O1scapeSettingsCard.clientNames[O1scapeSettingsCard.clientChooser.getSelectedIndex()],
			Settings.preferredClient2001scape
		);
		Settings.preferredClientOpenpk = clientNameTranslator.getOrDefault(
			openpkSettingsCard.clientNames[openpkSettingsCard.clientChooser.getSelectedIndex()],
			Settings.preferredClientOpenpk
		);

		// TODO: RSC KALE

		Settings.preferredClientUranium = clientNameTranslator.getOrDefault(
			uraniumSettingsCard.clientNames[uraniumSettingsCard.clientChooser.getSelectedIndex()],
			Settings.preferredClientUranium
		);

		Settings.preferredClientColeslaw = clientNameTranslator.getOrDefault(
			coleslawSettingsCard.clientNames[coleslawSettingsCard.clientChooser.getSelectedIndex()],
			Settings.preferredClientColeslaw
		);

		Settings.saveSettings();

		spawnSavedText(100);
	}

	private void spawnSavedText(int animationFrame) {
		Thread t = new Thread(new SavedIndicatorThread(animationFrame, POPUP_BACKGROUND));
		t.start();
	}

	public LinkButton defineClientLogoButton(LaunchButton logo, String preferredClient) {
		String server = logo.getActionCommand();

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
		String icon = null;

		switch (preferredClient) {
			// these are listed in chronological order of compatibility with openrsc server
			case Settings.OPENRSC:
			case ClientSettingsCard.OPENRSC:
				icon = "openrsc-client-large";
				break;
			case Settings.RSCPLUS:
			case ClientSettingsCard.RSCPLUS:
				icon = "rscplus-large";
				break;
			case Settings.APOSBOT:
			case ClientSettingsCard.APOSBOT:
				icon = "aposbot-large";
				break;
			case Settings.IDLERSC:
			case ClientSettingsCard.IDLERSC:
				icon = "idlersc-large";
				break;
			case Settings.WINRUNE:
			case ClientSettingsCard.WINRUNE:
				icon = "rune-large";
				break;
			case Settings.MUD38:
			case ClientSettingsCard.MUD38:
				icon = "mudclient38-large";
				break;
			case Settings.RSCTIMES:
			case ClientSettingsCard.RSCTIMES:
				icon ="rsctimes-large";
				break;
			case Settings.WEBCLIENT:
			case ClientSettingsCard.WEBCLIENT:
				icon = "webbrowser-large";
				break;
			default:
				icon = "question_mark-small";
				break;
		}

		return new LinkButton(icon, new Rectangle(CLIENT_LOGO_LARGE_X, CLIENT_LOGO_LARGE_Y, 120, 120));
	}

	private static int makeCheckboxLabel(String text, int x, int y, JLabel POPUP_BACKGROUND) {
		JLabel undecoratedLabel = new JLabel(text);
		undecoratedLabel.setFont(MainWindow.helvetica13);
		undecoratedLabel.setBorder(new EmptyBorder(0,0,0,0));
		int undecoratedLabelHeight = undecoratedLabel.getFontMetrics(MainWindow.helvetica13).getHeight();
		undecoratedLabel.setBounds(x, y, undecoratedLabel.getPreferredSize().width, undecoratedLabelHeight);
		undecoratedLabel.setForeground(MainWindow.white);
		POPUP_BACKGROUND.add(undecoratedLabel);
		return undecoratedLabel.getPreferredSize().width;
	}
}

