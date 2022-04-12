package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.CombatStyleStruct;

public class CombatStyleHandler implements PayloadProcessor<CombatStyleStruct, OpcodeIn> {

	public void process(final CombatStyleStruct payload, final Player player) throws Exception {

		int style = payload.style;
		if (style < Skills.CONTROLLED_MODE || style > Skills.DEFENSIVE_MODE) {
			player.setSuspiciousPlayer(true, "style handler style < 0 or style > 3");
			return;
		}
		player.setCombatStyle(style);
	}

}
