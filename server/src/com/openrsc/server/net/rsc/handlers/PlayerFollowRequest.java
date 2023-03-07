package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.TargetMobStruct;

public final class PlayerFollowRequest implements PayloadProcessor<TargetMobStruct, OpcodeIn> {

	public void process(TargetMobStruct payload, Player player) throws Exception {

		Player affectedPlayer = player.getWorld().getPlayer(payload.serverIndex);
		if (affectedPlayer == null) {
			player.setSuspiciousPlayer(true, "tried following null player");
			return;
		}
		if (player.inCombat()) {
			return;
		}

		if (player.getDuel().isDueling()) {
			return;
		}

		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		if (!affectedPlayer.canBeReattacked())
			return;
		player.resetAll();
		player.setFollowing(affectedPlayer, 1);
		player.message("Following " + affectedPlayer.getUsername());
	}
}
