package com.openrsc.server.plugins.triggers;

public interface StartupTrigger {
	/**
	 * Called when the server starts up
	 */
	void onStartup();
	boolean blockStartup();
}
