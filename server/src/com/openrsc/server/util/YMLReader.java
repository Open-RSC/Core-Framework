package com.openrsc.server.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class YMLReader {
	private static final Logger LOGGER = LogManager.getLogger();

	// Holds the key and attribute for each setting.
	private class Setting {
		private String key, attribute;

		// Create a new setting by providing a key and an attribute.
		private Setting(String key, String attribute) {
			this.key = key;
			this.attribute = attribute;
		}

		public String getAttribute() {
			return attribute;
		}

		public String getKey() {
			return key;
		}
	}

	private List<Setting> settings;

	public YMLReader() {
		settings = new ArrayList<Setting>();
	}

	public void loadFromYML(String fileName) throws IOException {
		List<String> lines = Collections.emptyList();

		// Try to open the file. If it fails, we pass the error back up
		// to the ServerConfiguration class.
		try {
			lines = Files.readAllLines(Paths.get(fileName));
		}

		catch (IOException e) {
			throw e;
		}

		for (String line : lines) {
			// Handle comments
			if (line.contains("#")) {
				// Handle comment-only lines
				if (line.split("#").length < 2)
					continue;

				// Handle commented out properties
				if (line.indexOf('#') < line.indexOf(':'))
					continue;

				// Handle in-line comments
				String[] sublines = line.split("#");
				for (String subline : sublines) {
					if (subline.contains(":")) {
						line = subline;
						break;
					}
				}
			}

			String[] elems = line.split(":");

			// Trim the whitespace
			for (int i = 0; i < elems.length; ++i) { elems[i] = elems[i].trim(); }

			switch (elems.length) {
				case 2:
					// Handle keys that have null for their attribute
					// But we still add it to the settings anyways.
					if (elems[1].equalsIgnoreCase("null")) {
						LOGGER.info(fileName + ": Key \"" + elems[0] +
							"\" has null value.");
						settings.add(new Setting(elems[0], elems[1]));
					}
					// Handle normal lines
					else {
						// Check if the key exists in the settings list
						if (!(keyExists(elems[0]))) {
							settings.add(new Setting(elems[0], elems[1]));
						}
						else {
							LOGGER.info(fileName + ": Duplicate key: " + elems[0]);
						}
					}
					break;
				case 3:
					// Handles the line with the server port (it contains an extra colon)
					settings.add(new Setting(elems[0], (elems[1] + ":" + elems[2])));
					break;
			}
		}
	}

	// Grabs the attribute of a specific setting given a key.
	// If no setting is found with the given key, return null (might change, could break stuff).
	public String getAttribute(String key) {
		try
		{
			return settings.stream().
				filter(setting -> setting.getKey().equalsIgnoreCase(key)).
				findFirst().
				get().
				getAttribute();
		}

		catch (NoSuchElementException e)
		{
			return "NOT_HERE";
		}
	}

	// Returns true if there is already a setting with the
	// key provided.
	public boolean keyExists(String key) {
		return settings.stream().
			anyMatch(setting -> setting.getKey().equalsIgnoreCase(key));
	}
}

