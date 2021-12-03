package launcher.Utils;

import launcher.Gameupdater.Downloader;
import launcher.Gameupdater.Updater;
import launcher.Settings;
import launcher.elements.ClientSettingsCard;
import launcher.popup.PopupFrame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ClientLauncher {
	public static void launchClientForServer(String serverName) {
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
						setOpenRSCClientEndpoint(ip, port);
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
						launchWinRune(ip, port, "2003");
						break;
					case Settings.WEBCLIENT:
					case ClientSettingsCard.WEBCLIENT:
						Utils.openWebpage("http://game.openrsc.com/play/preservation/members");
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
						setOpenRSCClientEndpoint(ip, port);
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
						launchWinRune(ip, port, "2003");
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
						Utils.openWebpage("http://game.openrsc.com/play/uranium/members");
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
						try {
							Updater.updateWinRune();
						} catch (IOException e) {
							e.printStackTrace();
						}
						launchWinRune(ip, port, "2001");
						break;
					// TODO: implement rsctimes client
					case Settings.RSCTIMES:
					case ClientSettingsCard.RSCTIMES:
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
					default:
						setOpenRSCClientEndpoint(ip, port);
						launchOpenRSCClient();
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
			fileout = new FileOutputStream("Cache" + File.separator + "ip.txt");
			OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
			outputWriter.write(ip);
			outputWriter.close();
		} catch (Exception ignored) {
		}
		try {
			fileout = new FileOutputStream("Cache" + File.separator + "port.txt");
			OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
			outputWriter.write(port);
			outputWriter.close();
		} catch (Exception ignored) {
		}
	}

	private static void launchOpenRSCClient() {
		if (Downloader.currently_updating) {
			Logger.Info("Currently updating the client, please wait!"); // TODO: popup an error
			return;
		}

		// Deletes the client.properties file that may persist unwanted settings between different games
		File f = new File(Defaults._DEFAULT_CONFIG_DIR + File.separator + "client.properties");
		f.delete();

		// Update the sprite pack config file
		File configFile = new File(Defaults._DEFAULT_CONFIG_DIR + File.separator + "config.txt");
		configFile.delete();

		f = new File(Defaults._DEFAULT_CONFIG_DIR + File.separator
			+ Defaults._CLIENT_FILENAME + ".jar");
		Utils.execCmd(new String[]{"java", "-jar", f.getAbsolutePath()}, false);
	}

	public static void launchRSCPlus() {
		File rscplusJar = new File(Defaults._DEFAULT_CONFIG_DIR + File.separator + "extras" + File.separator + "rscplus" + File.separator + "rscplus.jar");
		Utils.execCmd(new String[]{"java", "-jar", rscplusJar.getAbsolutePath()}, rscplusJar.getParentFile());
	}

	public static void launchFleaCircus() {
		File fleaCircusDir = new File(Defaults._DEFAULT_CONFIG_DIR + File.separator + "extras" + File.separator + "fleacircus");
		Utils.execCmd(new String[]{"java", "-cp", fleaCircusDir.getAbsolutePath(), "fleas"}, fleaCircusDir);
	}

	public static void launchAPOS() {
		File aposbotJar = new File(Defaults._DEFAULT_CONFIG_DIR + File.separator + "extras" + File.separator + "apos" + File.separator + "APOS-master" + File.separator + "bot.jar");
		File aposDir = new File(Defaults._DEFAULT_CONFIG_DIR + File.separator + "extras" + File.separator + "apos" + File.separator + "APOS-master");

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
		File winruneJar = new File(Defaults._DEFAULT_CONFIG_DIR + File.separator + "extras" + File.separator + "winrune" + File.separator + "WinRune-master" + File.separator + "rune.jar");
		Utils.execCmd(new String[]{"java", "-jar", winruneJar.getAbsolutePath(), "members=true", "address=" + ip, "port=" + port, "version=" + version, "rsaExponent=65537", "rsaModulus=" + rsaKey}, winruneJar.getParentFile());
	}

	public static void launchIdleRSC() {
		File idlerscJar = new File(Defaults._DEFAULT_CONFIG_DIR + File.separator + "extras" + File.separator + "idlersc" + File.separator + "IdleRSC-master" + File.separator + "IdleRSC.jar");
		Utils.execCmd(new String[]{"java", "-jar", idlerscJar.getAbsolutePath()}, idlerscJar.getParentFile());
	}

	private static void exit() {
		System.exit(0);
	}
}
