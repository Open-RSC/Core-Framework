package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.struct.UnequipRequest;

public interface UnequipListener {
	void onUnequip(UnequipRequest request);
	/**
	 * Return true if you wish to prevent a user from unwielding an item
	 */
	boolean blockUnequip(UnequipRequest request);
}
