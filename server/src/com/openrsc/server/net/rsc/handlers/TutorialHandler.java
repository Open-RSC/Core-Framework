package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.NoPayloadStruct;

public class TutorialHandler implements PayloadProcessor<NoPayloadStruct, OpcodeIn> {

	@Override
	public void process(final NoPayloadStruct payload, final Player player) throws Exception {
		if (player == null) {
			return;
		}
		if (player.getLocation().onTutorialIsland()) {
			if (player.inCombat()) {
				player.message("You cannot do that whilst fighting!");
			}
			if (player.isBusy()) {
				return;
			}
			if (player.getCache().hasKey("tutorial")) {
				player.getCache().remove("tutorial");
			}
			player.teleport(120, 648, false);
			player.message("Skipped tutorial, welcome to Lumbridge");
			ActionSender.sendPlayerOnTutorial(player);
		}
	}
}
