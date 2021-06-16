package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.TargetPositionStruct;

public class BlinkHandler implements PayloadProcessor<TargetPositionStruct, OpcodeIn> {

	@Override
	public void process(TargetPositionStruct payload, Player player) throws Exception {
		int coordX = payload.coordinate.getX();
		int coordY = payload.coordinate.getY();
		if (player.isMod()) {
			player.teleport(coordX, coordY);
			if (player.getPossessing() != null) {
				player.resetFollowing();
			}
		} else {
			player.setSuspiciousPlayer(true, "non mod player tried to blink");
		}
	}

}
