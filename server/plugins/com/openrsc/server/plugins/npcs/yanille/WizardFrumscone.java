package com.openrsc.server.plugins.npcs.yanille;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.npcsay;

import com.openrsc.server.constants.NpcId;

public class WizardFrumscone implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.WIZARD_FRUMSCONE.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.WIZARD_FRUMSCONE.id()) {
			npcsay(p, n, "Do you like my magic zombies",
				"Feel free to kill them",
				"Theres plenty more where these came from");
		}
	}
}
