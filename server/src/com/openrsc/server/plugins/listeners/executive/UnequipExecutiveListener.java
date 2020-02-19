package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.struct.UnequipRequest;

public interface UnequipExecutiveListener {
	/**
	 * Return true if you wish to prevent a user from unwielding an item
	 */
	boolean blockUnequip(UnequipRequest request);
}
