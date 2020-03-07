package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.struct.EquipRequest;

public interface EquipListener {

	void onEquip(EquipRequest request);

	/**
	 * Return true if you wish to prevent a user from wielding an item
	 */
	void blockEquip(EquipRequest request);
}
