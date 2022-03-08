package com.openrsc.server.plugins.shared;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.PlayerLoginTrigger;

public class PlayerLogin implements PlayerLoginTrigger {
	@Override
	public void onPlayerLogin(Player player) {
		// nothing to do
	}

	@Override
	public boolean blockPlayerLogin(Player player) {
		return false;
	}
}
