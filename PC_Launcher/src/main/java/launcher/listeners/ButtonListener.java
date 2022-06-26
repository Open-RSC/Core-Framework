package launcher.listeners;

import launcher.Fancy.MainWindow;
import launcher.Gameupdater.Updater;
import launcher.Launcher;
import launcher.Main;
import launcher.Settings;
import launcher.Utils.ClientLauncher;
import launcher.Utils.Defaults;
import launcher.Utils.Logger;
import launcher.Utils.Utils;
import launcher.popup.PopupFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ButtonListener implements ActionListener {
	public static PopupFrame settingsFrame;
	public static PopupFrame launcherFrame;
	@Override
	public void actionPerformed(final ActionEvent event) {
		final String action = event.getActionCommand().toLowerCase();
		switch (action) {
			case "openrsc_sword_logo": {
				Utils.openWebpage("https://openrsc.com");
				return;
			}

			case "bots": {
				Settings.showBotButtons = !Settings.showBotButtons;
				MainWindow.get().toggleBotServers();
				MainWindow.get().buttons.robotCheckbox.setSelected(Settings.showBotButtons);
				return;
			}

			case "uranium_wiki": {
				Utils.openWebpage("https://rsc.vet/wiki/index.php?title=RSC_Uranium");
				return;
			}
			case "preservation_wiki": {
				// Utils.openWebpage("https://classic.runescape.wiki");
				// Goes to a warning nag screen on how to edit the RSC wiki, to not put PS info in it.
				Utils.openWebpage("https://rsc.vet/wiki/index.php?title=RSC_Preservation");
				return;
			}
			case "coleslaw_wiki": {
				Utils.openWebpage("https://rsc.vet/wiki/index.php?title=RSC_Coleslaw");
				return;
			}
			case "cabbage_wiki": {
				Utils.openWebpage("https://rsc.vet/wiki/index.php?title=RSC_Cabbage");
				return;
			}
			case "2001scape_wiki": {
				Utils.openWebpage("https://rsc.vet/wiki/index.php?title=2001scape");
				return;
			}
			case "kale_wiki": {
				Utils.openWebpage("https://rsc.vet/wiki/index.php?title=RSC_Kale");
				return;
			}
			case "openpk_wiki": {
				Utils.openWebpage("https://rsc.vet/wiki/index.php?title=Open_PK");
				return;
			}

			case "openrsc-forums": {
				Utils.openWebpage("https://rsc.vet/board");
				return;
			}

			case "reddit": {
				Utils.openWebpage("https://old.reddit.com/r/rsc");
				return;
			}

			case "bugs":
			case "cockroach": { // bug report
				Utils.openWebpage("https://gitlab.com/open-runescape-classic/core/-/issues");
				return;
			}
			case "chat":
			case "libera": {
				// could have used ircs://, but it's more likely helpful to show users the web client
				// and let experienced IRC users just connect using their own client manually
				// Utils.openWebpage("ircs://irc.libera.chat:6697/#openrsc");
				Utils.openWebpage("https://web.libera.chat/#openrsc");
				return;
			}
			case "discord": {
				Utils.openWebpage("https://discord.gg/ABdFCqn");
				return;
			}

			case "preservation_hiscores": {
				Utils.openWebpage("https://rsc.vet/hiscores/preservation");
				return;
			}

			case "cabbage_hiscores": {
				Utils.openWebpage("https://rsc.vet/hiscores/cabbage");
				return;
			}

			case "uranium_hiscores": {
				Utils.openWebpage("https://rsc.vet/hiscores/uranium");
				return;
			}

			case "coleslaw_hiscores": {
				Utils.openWebpage("https://rsc.vet/hiscores/coleslaw");
				return;
			}

			case "2001scape_hiscores": {
				Utils.openWebpage("https://rsc.vet/hiscores/2001scape");
				return;
			}

			case "openpk_hiscores": {
				Utils.openWebpage("https://rsc.vet/hiscores/openpk");
				return;
			}

			case "kale_hiscores": {
				Utils.openWebpage("https://rsc.vet/hiscores/kale");
				return;
			}

			case "preservation":
			case "cabbage":
			case "uranium":
			case "coleslaw":
			case "2001scape":
			case "openpk":
			case "kale": {
				ClientLauncher.launchClientForServer(action);
				return;
			}

			case "fleacircus": {
				try {
					try {
						Updater.updateFleaCircus();
					} catch (IOException e) {
						e.printStackTrace();
					}
					ClientLauncher.launchFleaCircus();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}

			case "robot_checkbox": {
				Settings.showBotButtons = !Settings.showBotButtons;
				MainWindow.get().toggleBotServers();
				return;
			}

			case "undecorated_checkbox": {
				Settings.undecoratedWindowSave = !Settings.undecoratedWindowSave;
				return;
			}

			case "autoupdate_checkbox": {
				Settings.autoUpdate = !Settings.autoUpdate;
				return;
			}

			case "show_prerelease_checkbox": {
				Settings.showPrerelease = !Settings.showPrerelease;
				return;
			}

			case "minimize": {
				MainWindow.get().setState(1);
				return;
			}
			case "close": {
				System.exit(0);
				return;
			}

			case "delete": {
				// Deletes all cache files except for .txt files and .wav files

				File folder = new File(Main.configFileLocation);
				File[] fList = folder.listFiles();
				assert fList != null;
				for (File file : fList) {
					String extension = String.valueOf(file);
					if (!extension.endsWith(".txt")) {
						new File(String.valueOf(file)).delete();
					}
				}

				File video = new File(Main.configFileLocation + "/video");
				File[] vList = video.listFiles();
				assert vList != null;
				for (File file : vList) {
					String extension = String.valueOf(file);
					if (extension.endsWith(".orsc")) {
						new File(String.valueOf(file)).delete();
					}
					if (extension.endsWith(".osar")) {
						new File(String.valueOf(file)).delete();
					}
				}

				File spritepacks = new File(Main.configFileLocation + "/video/spritepacks");
				File[] sList = spritepacks.listFiles();
				assert sList != null;
				for (File file : sList) {
					String extension = String.valueOf(file);
					if (extension.endsWith(".osar")) {
						new File(String.valueOf(file)).delete();
					}
				}

				//  re-download openrsc client
				Launcher.updater.updateOpenRSCClient();
				return;
			}

			case "gear":
			case "client_settings_button": {
				if (null != settingsFrame)
					settingsFrame.setVisible(false);
				if (null != launcherFrame)
					launcherFrame.setVisible(false);
				settingsFrame = new PopupFrame(PopupFrame.CLIENT_SETTINGS);
				settingsFrame.showFrame();
				return;
			}

			case "advanced_settings_button": {
				if (null != settingsFrame)
					settingsFrame.setVisible(false);
				if (null != launcherFrame)
					launcherFrame.setVisible(false);
				launcherFrame = new PopupFrame(PopupFrame.LAUNCHER_SETTINGS);
				launcherFrame.showFrame();
				return;
			}

			case "question_mark":
			case "about_our_servers_button": {
				Utils.openWebpage("https://rsc.vet/wiki/index.php?title=Open_RuneScape_Classic_Wiki");
				return;
			}

			case "floppy_disk":
			case "apply_and_save_button": {
				PopupFrame.get().saveClientSelectionsToSettings();
				return;
			}

			case "closepopup":
			case "exit_gear":
			case "exit_settings_button": {
				PopupFrame.get().hideFrame();
				return;
			}

			case "rune-large": {
				Utils.openWebpage("https://github.com/RSCPlus/WinRune");
				return;
			}

			case "rscplus-large": {
				Utils.openWebpage("https://github.com/RSCPlus/rscplus");
				return;
			}

			case "rsctimes-large": {
				Utils.openWebpage("https://github.com/RSCPlus/rsctimes");
				return;
			}

			case "openrsc-large": {
				Utils.openWebpage("https://gitlab.com/open-runescape-classic/core/-/tree/develop/Client_Base");
				return;
			}

			case "aposbot-large": {
				Utils.openWebpage("https://gitlab.com/open-runescape-classic/APOS");
				return;
			}

			case "mudclient38-large": {
				Utils.openWebpage("https://github.com/RSCPlus/mudclient38-recreated");
				return;
			}

			case "idlersc-large": {
				Utils.openWebpage("https://gitlab.com/idlersc/idlersc");
				return;
			}

			case "webbrowser-large": {
				Utils.openWebpage("https://github.com/2003scape/mudclient177-deob-teavm");
				return;
			}

			default:
				break;
		}
		Logger.Error("unhandled button: " + action);
	}
}
