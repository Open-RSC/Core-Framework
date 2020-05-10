package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.EquipRequest;

public interface WearObjTrigger {

	void onWearObj(Player player, Integer invIndex, EquipRequest request);

	/**
	 * Return true if you wish to prevent a user from wielding an item
	 */
	boolean blockWearObj(Player player, Integer invIndex, EquipRequest request);
}
