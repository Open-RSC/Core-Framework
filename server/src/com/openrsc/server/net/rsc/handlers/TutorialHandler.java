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
		player.skipTutorial();
	}
}
