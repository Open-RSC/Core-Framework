package com.openrsc.server.plugins.listeners.action;

public interface StartupListener {
	/**
	 * Called when the server starts up
	 */
	void onStartup();
	boolean blockStartup();
}
