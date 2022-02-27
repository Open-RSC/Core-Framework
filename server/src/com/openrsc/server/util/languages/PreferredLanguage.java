package com.openrsc.server.util.languages;

import java.util.HashMap;
import java.util.Map;

public enum PreferredLanguage {
	NONE_SET ("None"),
	ENGLISH_UK_MALE("en_UK_male"),
	ENGLISH_UK_FEMALE("en_UK_female"),
	ENGLISH_UK_FEMALE_FIXED("en_UK_female_no_misgender"),
	ENGLISH_UK_GENDER_NEUTRAL("en_UK_gender_neutral"),
	ENGLISH_UK_FIXED_GRAMMAR_MALE("en_UK_male_proper"),
	ENGLISH_UK_FIXED_GRAMMAR_FEMALE("en_UK_female_proper"),
	ENGLISH_UK_FIXED_GRAMMAR_FEMALE_FIXED("en_UK_female_no_misgender_proper"),
	ENGLISH_UK_FIXED_GRAMMAR_GENDER_NEUTRAL("en_UK_gender_neutral_proper");

	private String localeName;

	PreferredLanguage(String localeName) {
		this.localeName = localeName;
	}

	public String getLocaleName() {
		return this.localeName;
	}

	private static final Map<String, PreferredLanguage> byLocale = new HashMap<String, PreferredLanguage>();

	static {
		for (PreferredLanguage lang : PreferredLanguage.values()) {
			if (byLocale.put(lang.getLocaleName(), lang) != null) {
				throw new IllegalArgumentException("duplicate locale: " + lang.getLocaleName());
			}
		}
	}

	public static PreferredLanguage getByLocaleName(String localeName) {
		return byLocale.getOrDefault(localeName, NONE_SET);
	}
}
