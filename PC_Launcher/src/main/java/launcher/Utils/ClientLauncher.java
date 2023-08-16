package launcher.Utils;

import launcher.Gameupdater.Downloader;
import launcher.Gameupdater.Updater;
import launcher.Main;
import launcher.Settings;
import launcher.elements.ClientSettingsCard;
import launcher.popup.PopupFrame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.JOptionPane;

public class ClientLauncher {
	public static void launchClientForServer(String serverName) {
    if (Downloader.currently_updating) {
      JOptionPane.showMessageDialog(null, "Currently updating the client, please wait!");
      return;
    }
		switch (serverName) {
			case "preservation": {
				String ip = "game.openrsc.com";
				String port = "43596";
				String client;
				try {
					if (PopupFrame.get().isVisible()) {
						client = PopupFrame.preservationSettingsCard.clientNames[PopupFrame.preservationSettingsCard.clientChooser.getSelectedIndex()];
					} else {
						client = Settings.preferredClientPreservation;
					}
				} catch (Exception e) {
					client = Settings.preferredClientPreservation;
				}
				switch (client) {
					case Settings.RSCPLUS:
					case ClientSettingsCard.RSCPLUS:
						try {
							Updater.updateRSCPlus();
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					case Settings.OPENRSC:
					case ClientSettingsCard.OPENRSC:
						setOpenRSCClientEndpoint(ip, "43602");
						launchOpenRSCClient();
						break;
					default:
					case Settings.WINRUNE:
					case ClientSettingsCard.WINRUNE:
						try {
							Updater.updateWinRune();
						} catch (IOException e) {
							e.printStackTrace();
						}
						launchWinRune(ip, "43600", "2003");
						break;
					case Settings.WEBCLIENT:
					case ClientSettingsCard.WEBCLIENT:
						Utils.openWebpage("https://rsc.vet/play/preservation/members");
						break;
				}
				return;
			}
			case "cabbage": {
				// the only compatible client
				setOpenRSCClientEndpoint("game.openrsc.com", "43595");
				launchOpenRSCClient();
				return;
			}

			case "uranium": {
				String ip = "game.openrsc.com";
				String port = "43235";
				String client;
				try {
					if (PopupFrame.get().isVisible()) {
						client = PopupFrame.uraniumSettingsCard.clientNames[PopupFrame.uraniumSettingsCard.clientChooser.getSelectedIndex()];
					} else {
						client = Settings.preferredClientUranium;
					}
				} catch (Exception e) {
					client = Settings.preferredClientUranium;
				}
				switch (client) {
					case Settings.RSCPLUS:
					case ClientSettingsCard.RSCPLUS:
						try {
							Updater.updateRSCPlus();
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					case Settings.OPENRSC:
					case ClientSettingsCard.OPENRSC:
						setOpenRSCClientEndpoint(ip, "43601");
						launchOpenRSCClient();
						break;
					default:
					case Settings.WINRUNE:
					case ClientSettingsCard.WINRUNE:
						try {
							Updater.updateWinRune();
						} catch (IOException e) {
							e.printStackTrace();
						}
						launchWinRune(ip, "43605", "2003");
						break;
					case Settings.APOSBOT:
					case ClientSettingsCard.APOSBOT:
						try {
							Updater.updateAPOS();
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					case Settings.WEBCLIENT:
					case ClientSettingsCard.WEBCLIENT:
						Utils.openWebpage("https://rsc.vet/play/uranium/members");
						break;
				}
				return;
			}

			case "coleslaw": {
				String client;
				try {
					if (PopupFrame.get().isVisible()) {
						client = PopupFrame.coleslawSettingsCard.clientNames[PopupFrame.coleslawSettingsCard.clientChooser.getSelectedIndex()];
					} else {
						client = Settings.preferredClientColeslaw;
					}
				} catch (Exception e) {
					client = Settings.preferredClientColeslaw;
				}
				switch (client) {
					case Settings.IDLERSC:
					case ClientSettingsCard.IDLERSC:
						try {
							Updater.updateIdleRSC();
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					case Settings.OPENRSC:
					case ClientSettingsCard.OPENRSC:
					default:
						setOpenRSCClientEndpoint("game.openrsc.com", "43599");
						launchOpenRSCClient();
						break;
				}
				return;
			}

			case "2001scape": {
				String ip = "game.openrsc.com";
				String port = "43593";
				String client;
				try {
					if (PopupFrame.get().isVisible()) {
						client = PopupFrame.O1scapeSettingsCard.clientNames[PopupFrame.O1scapeSettingsCard.clientChooser.getSelectedIndex()];
					} else {
						client = Settings.preferredClient2001scape;
					}
				} catch (Exception e) {
					client = Settings.preferredClient2001scape;
				}
				switch (client) {
					case Settings.MUD38:
					case ClientSettingsCard.MUD38:
					default:
						try {
							Updater.updateWinRune();
						} catch (IOException e) {
							e.printStackTrace();
						}
						launchWinRune(ip, port, "2001");
						break;
					case Settings.RSCTIMES:
					case ClientSettingsCard.RSCTIMES:
						try {
							Updater.updateRSCTimes();
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					case Settings.WEBCLIENT:
					case ClientSettingsCard.WEBCLIENT:
						Utils.openWebpage("https://rsc.vet/play/2001scape/members");
						break;
				}
				return;
			}

			case "openpk": {
				// the only compatible client
				setOpenRSCClientEndpoint("game.openrsc.com", "43597");
				launchOpenRSCClient();
				return;
			}
		}
	}

	private static void setOpenRSCClientEndpoint(String ip, String port) {
		// Sets the IP and port
		FileOutputStream fileout;
		try {
			fileout = new FileOutputStream(Main.configFileLocation + File.separator + "ip.txt");
			OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
			outputWriter.write(ip);
			outputWriter.close();
		} catch (Exception e) {
      Logger.Error("Error setting ip.txt: " + e.getMessage());
		}
		try {
			fileout = new FileOutputStream(Main.configFileLocation + File.separator + "port.txt");
			OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
			outputWriter.write(port);
			outputWriter.close();
		} catch (Exception e) {
      Logger.Error("Error setting port.txt: " + e.getMessage());
		}
	}

	private static void launchOpenRSCClient() {
		// Deletes the client.properties file that may persist unwanted settings between different games
		File f = new File(Main.configFileLocation + File.separator + "client.properties");
		f.delete();

		// Update the sprite pack config file
		File configFile = new File(Main.configFileLocation + File.separator + "config.txt");
		configFile.delete();

		File openRscClientJar = new File(Main.configFileLocation + File.separator
			+ Defaults._CLIENT_FILENAME + ".jar");
		Utils.execCmd(new String[]{"java", "-jar", openRscClientJar.getAbsolutePath()}, openRscClientJar.getParentFile());
	}

	public static void launchRSCPlus() {
		File rscplusJar = new File(Main.configFileLocation + File.separator + "extras" + File.separator + "rscplus" + File.separator + "rscplus.jar");
		Utils.execCmd(new String[]{"java", "-jar", rscplusJar.getAbsolutePath()}, rscplusJar.getParentFile());
	}

	public static void launchRSCTimes() {
		File rsctimesJar = new File(Main.configFileLocation + File.separator + "extras" + File.separator + "rsctimes" + File.separator + "rsctimes.jar");
		Utils.execCmd(new String[]{"java", "-jar", rsctimesJar.getAbsolutePath()}, rsctimesJar.getParentFile());
	}

	public static void launchFleaCircus() {
		File fleaCircusDir = new File(Main.configFileLocation + File.separator + "extras" + File.separator + "fleacircus");
		Utils.execCmd(new String[]{"java", "-cp", fleaCircusDir.getAbsolutePath(), "fleas"}, fleaCircusDir);
	}

	public static void launchAPOS() {
		File aposbotJar = new File(Main.configFileLocation + File.separator + "extras" + File.separator + "apos" + File.separator + "APOS-master" + File.separator + "bot.jar");
		File aposDir = new File(Main.configFileLocation + File.separator + "extras" + File.separator + "apos" + File.separator + "APOS-master");

		// Compile all java script files within the Scripts folder
		try {
			Utils.execCmd(new String[]{"javac", "-cp", "bot.jar;./lib/rsclassic.jar;", "./Scripts/*.java"}, aposDir);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Execute APOS
		Utils.execCmd(new String[]{"java", "-jar", aposbotJar.getAbsolutePath()}, aposbotJar.getParentFile());
	}

	public static void launchWinRune(String ip, String port, String version) {
		final String rsaKey = "7112866275597968156550007489163685737528267584779959617759901583041864787078477876689003422509099353805015177703670715380710894892460637136582066351659813";
		File winruneJar = new File(Main.configFileLocation + File.separator + "extras" + File.separator + "winrune" + File.separator + "WinRune-master" + File.separator + "rune.jar");
		Utils.execCmd(new String[]{"java", "-jar", winruneJar.getAbsolutePath(), "members=true", "address=" + ip, "port=" + port, "version=" + version, "rsaExponent=65537", "rsaModulus=" + rsaKey}, winruneJar.getParentFile());
	}

	public static void launchIdleRSC() {
		File idlerscJar = new File(Main.configFileLocation + File.separator + "extras" + File.separator + "idlersc" + File.separator + "IdleRSC-master" + File.separator + "IdleRSC.jar");
		Utils.execCmd(new String[]{"java", "-jar", idlerscJar.getAbsolutePath()}, idlerscJar.getParentFile());
	}

	private static void exit() {
		System.exit(0);
	}
}
