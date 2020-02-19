package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.struct.EquipRequest;

public interface EquipExecutiveListener {
	/**
	 * Return true if you wish to prevent a user from wielding an item
	 */
	void blockEquip(EquipRequest request);
}
