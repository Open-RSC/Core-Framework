package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.struct.UnequipRequest;

public interface UnequipListener {
	public void onUnequip(UnequipRequest request);
}
