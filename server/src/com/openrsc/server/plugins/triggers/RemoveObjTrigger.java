package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.struct.UnequipRequest;

public interface RemoveObjTrigger {
	void onRemoveObj(UnequipRequest request);
	/**
	 * Return true if you wish to prevent a user from unwielding an item
	 */
	boolean blockRemoveObj(UnequipRequest request);
}
