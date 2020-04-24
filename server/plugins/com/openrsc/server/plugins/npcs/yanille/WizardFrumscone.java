package com.openrsc.server.plugins.npcs.yanille;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class WizardFrumscone implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.WIZARD_FRUMSCONE.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.WIZARD_FRUMSCONE.id()) {
			npcsay(player, n, "Do you like my magic zombies",
				"Feel free to kill them",
				"Theres plenty more where these came from");
		}
	}
}
