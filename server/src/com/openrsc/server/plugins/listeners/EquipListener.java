package com.openrsc.server.plugins.listeners;

import com.openrsc.server.model.struct.EquipRequest;

public interface EquipListener {

	void onEquip(EquipRequest request);

	/**
	 * Return true if you wish to prevent a user from wielding an item
	 */
	void blockEquip(EquipRequest request);
}
