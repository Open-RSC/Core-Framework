package com.openrsc.server.plugins.listeners;

public interface StartupListener {
	/**
	 * Called when the server starts up
	 */
	void onStartup();
	boolean blockStartup();
}
