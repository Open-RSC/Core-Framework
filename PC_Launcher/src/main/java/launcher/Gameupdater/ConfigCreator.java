package launcher.Gameupdater;

import launcher.Utils.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.Properties;


public class ConfigCreator {
	public static void createPropsConfig(File config, Properties props) {
		if (!config.exists()) {
			if (config.getParentFile() != null) {
				try {
					Files.createDirectories(config.getParentFile().toPath());
				} catch (IOException e) {
					config.getParentFile().mkdirs();
					e.printStackTrace();
				}
			}
		}

		try {
			FileOutputStream out = new FileOutputStream(config);
			props.store(out, null);
			out.close();
		} catch (IOException e) {
			Logger.Error("Could not save world config!");
			e.printStackTrace();
		}
	}

	public static void createPreservationConfig(File config) {
		Properties props = new Properties();
		props.put("rsa_pub_key", "7112866275597968156550007489163685737528267584779959617759901583041864787078477876689003422509099353805015177703670715380710894892460637136582066351659813");
		props.put("port", "43596");
		props.put("rsa_exponent", "65537");
		props.put("url", "game.openrsc.com");
		props.put("name", "RSC Preservation");
		props.put("servertype", "1");
		props.put("hiscores_url", "https\\://rsc.vet/player/preservation/%USERNAME%");

		createPropsConfig(config, props);
	}

	public static void createUraniumConfig(File config) {
		Properties props = new Properties();
		props.put("rsa_pub_key", "7112866275597968156550007489163685737528267584779959617759901583041864787078477876689003422509099353805015177703670715380710894892460637136582066351659813");
		props.put("port", "43235");
		props.put("rsa_exponent", "65537");
		props.put("url", "game.openrsc.com");
		props.put("name", "RSC Uranium");
		props.put("servertype", "1");
		props.put("hiscores_url", "https\\://rsc.vet/player/uranium/%USERNAME%");

		createPropsConfig(config, props);
	}

	public static void create2001scapeConfig(File config) {
		Properties props = new Properties();
		props.put("rsa_pub_key", "7112866275597968156550007489163685737528267584779959617759901583041864787078477876689003422509099353805015177703670715380710894892460637136582066351659813");
		props.put("port", "43593");
		props.put("rsa_exponent", "65537");
		props.put("url", "game.openrsc.com");
		props.put("name", "2001scape");
		props.put("servertype", "1");
		props.put("hiscores_url", "https\\://rsc.vet/player/2001scape/%USERNAME%");

		createPropsConfig(config, props);
	}
}
