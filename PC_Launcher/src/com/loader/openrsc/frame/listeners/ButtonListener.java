package com.loader.openrsc.frame.listeners;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.AppFrame;
import com.loader.openrsc.frame.elements.CheckCombo;
import com.loader.openrsc.util.ClientLauncher;
import com.loader.openrsc.util.Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

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
				Utils.openWebpage("https://openrsc.com/wiki");
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
				int port = 43594;
				set(ip, port);
				launch();
				return;
			}
			case "cabbage": {
				String ip = "game.openrsc.com";
				int port = 43595;
				set(ip, port);
				launch();
				return;
			}

			case "preservation": {
				String ip = "game.openrsc.com";
				int port = 43596;
				set(ip, port);
				launch();
				return;
			}

			case "openpk": {
				String ip = "game.openrsc.com";
				int port = 43597;
				set(ip, port);
				launch();
				return;
			}

			case "wk": {
				String ip = "game.openrsc.com";
				int port = 43598;
				set(ip, port);
				launch();
				return;
			}

			case "dev": {
				String ip = "game.openrsc.com";
				int port = 43599;
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
				Path f = Paths.get(Constants.CONF_DIR);
				try {
					Files.walk(f)
						.sorted(Comparator.reverseOrder())
						.map(Path::toFile)
						.forEach(File::delete);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}

			default:
				break;
		}
		System.out.println(action);
	}

	private void set(String ip, int port) {
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

		ClientLauncher.launchClient();
	}
}
