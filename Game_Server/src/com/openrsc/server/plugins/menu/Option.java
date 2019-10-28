package com.openrsc.server.plugins.menu;

public abstract class Option {
	/**
	 * Option in text
	 */
	private String option;

	/**
	 * Constructs a new option
	 *
	 * @param string - The option
	 */
	public Option(String string) {
		option = string;
	}

	/**
	 * The action that is done after player has selected an option.
	 */
	public abstract void action();

	/**
	 * Returns the option in text.
	 *
	 * @return
	 */
	public String getOption() {
		return option;
	}
}
