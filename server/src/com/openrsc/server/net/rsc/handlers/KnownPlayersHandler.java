package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.KnownPlayersStruct;

public class KnownPlayersHandler implements PayloadProcessor<KnownPlayersStruct, OpcodeIn> {
	@Override
	public void process(KnownPlayersStruct payload, Player player) throws Exception {
		player.knownPlayersCount = payload.playerCount;
		for (int i = 0; i < payload.playerCount; i++) {
			player.knownPlayerPids[i] = payload.playerServerAppearanceId[i];
			player.knownPlayerAppearanceIds[i] = payload.playerServerAppearanceId[i];
		}
	}
}
