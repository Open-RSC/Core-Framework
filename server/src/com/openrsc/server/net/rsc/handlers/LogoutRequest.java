package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.UnregisterForcefulness;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.NoPayloadStruct;
import com.openrsc.server.util.rsc.MessageType;

public class LogoutRequest implements PayloadProcessor<NoPayloadStruct, OpcodeIn> {
	public void process(NoPayloadStruct payload, Player player) throws Exception {
		if (player.canLogout()) {
			player.unregister(UnregisterForcefulness.FAIL_IN_COMBAT, "Player requested log out");
		} else {
			ActionSender.sendCantLogout(player);
			if (player.getDenyAllLogoutRequests()) {
				player.playerServerMessage(MessageType.QUEST, "Type @or2@::stayin@whi@ if you would actually like to log out.");
			}
		}
	}
}
