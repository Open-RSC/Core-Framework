package com.openrsc.server.util;

import com.openrsc.server.Server;
import com.thoughtworks.xstream.XStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class PersistenceManager {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private static final XStream xstream = new XStream();

	private final Server server;

	public PersistenceManager(Server server) {
		this.server = server;

		setupAliases();
	}

	public Object load(String filename) {
		try {
			InputStream is = new FileInputStream(new File(getServer().getConfig().CONFIG_DIR, filename));
			if (filename.endsWith(".gz")) {
				is = new GZIPInputStream(is);
			}
			Object rv = xstream.fromXML(is);
			return rv;
		} catch (IOException ioe) {
			LOGGER.catching(ioe);
		}
		return null;
	}

	protected void setupAliases() {
		try {
			Properties aliases = new Properties();
			FileInputStream fis = new FileInputStream(new File(getServer().getConfig().CONFIG_DIR, "aliases.xml"));
			aliases.loadFromXML(fis);
			for (Enumeration<?> e = aliases.propertyNames(); e.hasMoreElements(); ) {
				String alias = (String) e.nextElement();
				Class<?> c = Class.forName((String) aliases.get(alias));
				xstream.alias(alias, c);
			}
		} catch (Exception ioe) {
			LOGGER.catching(ioe);
		}
	}

	public void write(String filename, Object o) {
		try {
			OutputStream os = new FileOutputStream(new File(getServer().getConfig().CONFIG_DIR, filename));
			if (filename.endsWith(".gz")) {
				os = new GZIPOutputStream(os);
			}
			xstream.toXML(o, os);
		} catch (IOException ioe) {
			LOGGER.catching(ioe);
		}
	}

	public Server getServer() {
		return server;
	}
}
