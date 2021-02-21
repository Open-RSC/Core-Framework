package launcher.listeners;

import launcher.Utils.ClientLauncher;
import launcher.Utils.Defaults;
import launcher.Fancy.MainWindow;
import launcher.Utils.Utils;
import launcher.Gameupdater.Updater;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class ButtonListener implements ActionListener {
	@Override
	public void actionPerformed(final ActionEvent event) {
		final String action = event.getActionCommand().toLowerCase();
		switch (action) {
			case "rsc wiki": {
				Utils.openWebpage("https://classic.runescape.wiki");
				return;
			}
			case "our wiki": {
				Utils.openWebpage("https://runescapeclassic.dev/wiki");
				return;
			}
			case "bug reports": {
				Utils.openWebpage("https://orsc.dev/open-rsc/Game/issues");
				return;
			}
			case "discord": {
				Utils.openWebpage("https://discord.gg/94vVKND");
				return;
			}

			case "preservation": {
				String ip = "game.openrsc.com";
				String port = "43596";
				set(ip, port);
				launch();
				return;
			}
			case "cabbage": {
				String ip = "game.openrsc.com";
				String port = "43595";
				set(ip, port);
				launch();
				return;
			}

			case "uranium": {
				String ip = "game.openrsc.com";
				String port = "43235";
				set(ip, port);
				launch();
				return;
			}

			case "coleslaw": {
				String ip = "game.openrsc.com";
				String port = "43599";
				set(ip, port);
				launch();
				return;
			}

			case "rscplus": {
				try {
					Updater.updateRSCPlus();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}

			case "apos": {
				try {
					Updater.updateAPOS();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}

			case "idlersc": {
				try {
					Updater.updateIdleRSC();
				} catch (IOException e) {
					e.printStackTrace();
				}
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

				File folder = new File(Defaults._DEFAULT_CONFIG_DIR);
				File[] fList = folder.listFiles();
				assert fList != null;
				for (File file : fList) {
					String extension = String.valueOf(file);
					if (!extension.endsWith(".txt")) {
						new File(String.valueOf(file)).delete();
					}
				}

				File video = new File(Defaults._DEFAULT_CONFIG_DIR + "/video");
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

				File spritepacks = new File(Defaults._DEFAULT_CONFIG_DIR + "/video/spritepacks");
				File[] sList = spritepacks.listFiles();
				assert sList != null;
				for (File file : sList) {
					String extension = String.valueOf(file);
					if (extension.endsWith(".osar")) {
						new File(String.valueOf(file)).delete();
					}
				}

				System.exit(0);
			}

			default:
				break;
		}
		System.out.println(action);
	}

	private void set(String ip, String port) {
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

	private void launch() {
		// Deletes the client.properties file that may persist unwanted settings between different games
		File f = new File(Defaults._DEFAULT_CONFIG_DIR + File.separator + "client.properties");
		f.delete();

		//update the sprite pack config file
		File configFile = new File(Defaults._DEFAULT_CONFIG_DIR + File.separator + "config.txt");
		configFile.delete();

		ClientLauncher.launchClient();
	}
}
