package com.openrsc.server.plugins.shared;

import com.openrsc.server.plugins.triggers.StartupTrigger;

public class ServerStartup implements StartupTrigger {
	@Override
	public void onStartup() {
		// nothing to do here
	}

	@Override
	public boolean blockStartup() {
		return false;
	}
}
