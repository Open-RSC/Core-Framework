package com.openrsc.server.util.languages;

import com.openrsc.server.Server;
import com.openrsc.server.model.entity.player.Player;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;

public class I18NService {
	Server server;

	// males never get misgendered in authentic rsc, so a fixed locale for them isn't necessary
	Locale enUK;
	Locale enUKMale;
	Locale enUKFemale;
	Locale enUKFemaleNoMisgender;
	Locale enUKGenderNeutral;

	ResourceBundle baseAuthenticBundle;
	ResourceBundle baseMaleBundle;
	ResourceBundle baseFemaleBundle;
	ResourceBundle baseFemaleFixedBundle;
	ResourceBundle baseGenderNeutralBundle;
	ResourceBundle customBundle;
	ResourceBundle customMaleBundle;
	ResourceBundle customFemaleBundle;
	ResourceBundle customGenderNeutralBundle;

	public I18NService(final Server server) {
		this.server = server;
		init();
	}

	private void init() {
		enUK = new Locale("en", "UK");
		enUKMale = new Locale("en", "UK", "male"); // only messages for the player that depend on their gender
		enUKFemale = new Locale("en", "UK", "female"); // only messages for the player that depend on their gender
		enUKFemaleNoMisgender = new Locale("en", "UK", "female_no_misgender"); // only messages for the player that depend on their gender
		enUKGenderNeutral = new Locale("en", "UK", "gender_neutral"); // only messages for the player that depend on their gender

		try {
			File file = new File(server.getConfig().CONFIG_DIR, "languages");
			URL[] urls = {file.toURI().toURL()};
			ClassLoader loader = new URLClassLoader(urls);
			baseAuthenticBundle = ResourceBundle.getBundle("AuthenticMessages", enUK, loader);
			baseMaleBundle = ResourceBundle.getBundle("AuthenticMessages", enUKMale, loader);
			baseFemaleBundle = ResourceBundle.getBundle("AuthenticMessages", enUKFemale, loader);
			baseFemaleFixedBundle = ResourceBundle.getBundle("AuthenticMessages", enUKFemaleNoMisgender, loader);
			baseGenderNeutralBundle = ResourceBundle.getBundle("AuthenticMessages", enUKGenderNeutral, loader);

			customBundle = ResourceBundle.getBundle("CustomMessages", enUK, loader);
			customMaleBundle = ResourceBundle.getBundle("CustomMessages", enUKMale, loader);
			customFemaleBundle = ResourceBundle.getBundle("CustomMessages", enUKFemale, loader);
			customGenderNeutralBundle = ResourceBundle.getBundle("CustomMessages", enUKGenderNeutral, loader);

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		}
	}

	public String getText(String key) {
		return getText(key, null);
	}

	public String getText(String key, Player player) {
		return getMez(key, player)
			.substring(2)
			.replaceAll("%USERNAME%", player.getUsername())
			.replaceAll("&nbsp;", " ");
	}

	public int getType(String key) {
		return getType(key, null);
	}
	public int getType(String key, Player player) {
		try {
			int type = Integer.parseInt(getMez(key, player).substring(0,1));
			return type == 9 ? 0 : type;
		} catch (Exception ex) {
			return 0;
		}
	}

	public String getMez(String key, Player player) {
		if (null == player) {
			return getMezFromBase(key);
		}

		try {
			return resolveBundle(player, false).getString(key);
		} catch (MissingResourceException ex) {
			try {
				return resolveBundle(player, true).getString(key);
			} catch (MissingResourceException ex2) {
				return getMezFromBase(key);
			}
		}
	}

	public String getMezFromBase(String key) {
		try {
			return baseAuthenticBundle.getString(key);
		} catch (MissingResourceException ex3) {
			try {
				return customBundle.getString(key);
			} catch (MissingResourceException ex4) {
				return "0;Cabbage: " + key;
			}
		}
	}

	private ResourceBundle resolveBundle(Player player, boolean custom) {
		PreferredLanguage language = player.getPreferredLanguage();
		if (language == PreferredLanguage.NONE_SET) {
			language = pickLikelyLanguage(player);
		}
		if (custom) {
			switch (language) {
				case ENGLISH_UK_MALE:
					return baseMaleBundle;
				case ENGLISH_UK_FEMALE:
					return baseFemaleBundle;
				case ENGLISH_UK_FEMALE_FIXED:
					return baseFemaleFixedBundle;
				case ENGLISH_UK_GENDER_NEUTRAL:
					return baseGenderNeutralBundle;
			}
			return baseGenderNeutralBundle;
		} else {
			switch (language) {
				case ENGLISH_UK_MALE:
					return customMaleBundle;
				case ENGLISH_UK_FEMALE:
				case ENGLISH_UK_FEMALE_FIXED:
					return customFemaleBundle;
				case ENGLISH_UK_GENDER_NEUTRAL:
					return customGenderNeutralBundle;
			}
			return customGenderNeutralBundle;
		}
	}

	private PreferredLanguage pickLikelyLanguage(Player player) {
		if (player.isMale()) {
			return PreferredLanguage.ENGLISH_UK_MALE;
		}
		if (player.getConfig().WANT_CUSTOM_SPRITES) {
			return PreferredLanguage.ENGLISH_UK_FEMALE_FIXED;
		}
		return PreferredLanguage.ENGLISH_UK_FEMALE;
	}
}
