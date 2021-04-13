package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.NoPayloadStruct;

public class Heartbeat implements PayloadProcessor<NoPayloadStruct, OpcodeIn> {

	public void process(NoPayloadStruct payload, Player player) throws Exception {
		// Instead of handling the heartbeat packet here, every packet
		// that comes into the server updates the time-since-last-seen-player

		// player.addToPacketQueue(...) updates it.
	}
}
