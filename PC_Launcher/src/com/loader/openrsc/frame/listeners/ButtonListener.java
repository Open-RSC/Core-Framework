package com.loader.openrsc.frame.listeners;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.AppFrame;
import com.loader.openrsc.frame.elements.CheckCombo;
import com.loader.openrsc.util.ClientLauncher;
import com.loader.openrsc.util.Utils;

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

			case "openrsc": {
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

			case "minimize": {
				AppFrame.get().setState(1);
				return;
			}

			case "close": {
				System.exit(0);
				return;
			}

			case "delete": {
				// Deletes all cache files except for .txt files and .wav files

				File folder = new File(Constants.CONF_DIR);
				File[] fList = folder.listFiles();
				assert fList != null;
				for (File file : fList) {
					String extension = String.valueOf(file);
					if (!extension.endsWith(".txt")) {
						new File(String.valueOf(file)).delete();
					}
				}

				File video = new File(Constants.CONF_DIR + "/video");
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

				File spritepacks = new File(Constants.CONF_DIR + "/video/spritepacks");
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
		launch(false);
	}

	private void launch(boolean dev) {
		// Deletes the client.properties file that may persist unwanted settings between different games
		File f = new File(Constants.CONF_DIR + File.separator + "client.properties");
		f.delete();

		//update the sprite pack config file
		File configFile = new File(Constants.CONF_DIR + File.separator + "config.txt");
		configFile.delete();

		CheckCombo.store[] entries = AppFrame.get().getComboBoxState();

		//Update the config file
		if (!(entries.length == 1 && entries[0].text.equalsIgnoreCase("none"))) {
			try {
				FileWriter write = new FileWriter(configFile, true);
				PrintWriter writer = new PrintWriter(write);
				for (CheckCombo.store entry : entries)
					writer.println(entry.text + ":" + (entry.state ? 1 : 0));
				writer.close();
				write.close();
			} catch (IOException a) {
				a.printStackTrace();
			}
		}

		ClientLauncher.launchClient(dev);
	}
}
