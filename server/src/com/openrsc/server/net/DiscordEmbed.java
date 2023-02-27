package com.openrsc.server.net;

import org.apache.logging.log4j.core.util.JsonUtils;

public class DiscordEmbed {
	String title;
	String content;
	String color;
	String description;

	public DiscordEmbed(String title, String content, String color, String description) {
		final StringBuilder contentSb = new StringBuilder();
		final StringBuilder titleSb = new StringBuilder();
		final StringBuilder colorSb = new StringBuilder();
		final StringBuilder descriptionSb = new StringBuilder();

		JsonUtils.quoteAsString(content, contentSb);
		JsonUtils.quoteAsString(title, titleSb);
		JsonUtils.quoteAsString(color, colorSb);
		JsonUtils.quoteAsString(description, descriptionSb);

		this.title = titleSb.toString();
		this.content = contentSb.toString();
		this.color = colorSb.toString();
		this.description = descriptionSb.toString();
	}
}
