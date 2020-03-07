package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.struct.EquipRequest;

public interface WearObjTrigger {

	void onWearObj(EquipRequest request);

	/**
	 * Return true if you wish to prevent a user from wielding an item
	 */
	void blockWearObj(EquipRequest request);
}
