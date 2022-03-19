package com.openrsc.server.plugins.shared;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.PlayerLogoutTrigger;

public class PlayerLogout implements PlayerLogoutTrigger {
	@Override
	public void onPlayerLogout(Player player) {
		// nothing to do
	}

	@Override
	public boolean blockPlayerLogout(Player player) {
		return false;
	}
}
