package com.openrsc.server.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

// Author: Ryan

public class YMLReader {
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
			// Handle normal lines
			if (elems.length == 2)
				settings.add(new Setting(elems[0].trim(), elems[1].trim()));

				// Handle the line with the server port (it contains an extra colon)
			else if (elems.length == 3)
				settings.add(new Setting(elems[0].trim(), (elems[1].trim() + ":" + elems[2].trim())));
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
			return "null";
		}
	}
}

