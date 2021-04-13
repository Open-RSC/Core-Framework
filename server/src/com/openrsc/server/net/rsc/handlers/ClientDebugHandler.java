package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.DebugInfoStruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientDebugHandler implements PayloadProcessor<DebugInfoStruct, OpcodeIn> {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void process(DebugInfoStruct payload, Player player) throws Exception {
		LOGGER.debug("Player '" + player.getUsername() + "' @ " + player.getCurrentIP() + " sent");
		LOGGER.debug(payload.infoString);
	}
}
