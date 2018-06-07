package org.openrsc.server.model;

public abstract class MenuHandler {
	protected Player owner;
	protected String[] options;

	public MenuHandler(String[] options) {
		this.options = options;
	}

	public final void setOwner(Player owner) {
		this.owner = owner;
	}

	public final String getOption(int index) {
		if (index < 0 || index >= options.length)
			return null;
		return options[index];
	}
	
	public final String[] getOptions() {
		return options;
	}

	public abstract void handleReply(int option, String reply);
	
	public /*abstract*/ void onMenuCancelled() { /* default no-op */ }
}