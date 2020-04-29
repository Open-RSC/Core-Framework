package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.UnequipRequest;

public interface RemoveObjTrigger {
	void onRemoveObj(Player player, Integer invIndex, UnequipRequest request);
	/**
	 * Return true if you wish to prevent a user from unwielding an item
	 */
	boolean blockRemoveObj(Player player, Integer invIndex, UnequipRequest request);
}
