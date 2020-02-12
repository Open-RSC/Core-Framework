package com.openrsc.server.model;

import com.openrsc.server.model.entity.player.Player;

public class MenuOptionListener {
	/**
	 * Array of possible options that can be chosen
	 */
	protected String[] options;
	private Player owner;

	/**
	 * Creates a new MenuHandler with the given options
	 */
	public MenuOptionListener(final String[] options) {
		this.options = options;
	}

	/**
	 * Gets the appropriate option string
	 */
	public final String getOption(final int index) {
		if (index < 0 || index >= options.length) {
			return null;
		}
		return options[index];
	}

	public final String[] getOptions() {
		return options;
	}

	/**
	 * Abstract method for handling the reply
	 */
	public final void handleReply(final int option, final String reply) {
		getOwner().setOption(option);
	}

	/**
	 * Set the Player this MenuHandler is responsible for
	 */
	public final void setOwner(final Player owner) {
		this.owner = owner;
	}

	/**
	 * The Player this handler is responsible for
	 */
	public Player getOwner() {
		return owner;
	}
}
