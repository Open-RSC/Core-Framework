package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassDemons implements PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener {

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return inArray(n.getID(), NpcId.OTHAINIAN.id(), NpcId.DOOMION.id(), NpcId.HOLTHION.id());
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (inArray(n.getID(), NpcId.OTHAINIAN.id(), NpcId.DOOMION.id(), NpcId.HOLTHION.id())) {
			n.killedBy(p);
			if (!p.getCache().hasKey("doll_of_iban") && p.getQuestStage(Quests.UNDERGROUND_PASS) != 6) {
				p.message("the demon slumps to the floor");
				teleportPlayer(p, n);
			} else {
				teleportPlayer(p, n);
				message(p, "the demon slumps to the floor");
				if (!hasItem(p, n.getID() + 364)) {
					p.message("around it's neck you find a strange looking amulet");
					addItem(p, n.getID() + 364, 1); // will give correct ammys for all.
				}
			}
		}
	}

	private void teleportPlayer(Player p, Npc n) {
		if (n.getID() == NpcId.OTHAINIAN.id()) {
			p.teleport(796, 3541);
		} else if (n.getID() == NpcId.DOOMION.id()) {
			p.teleport(807, 3541);
		} else if (n.getID() == NpcId.HOLTHION.id()) {
			p.teleport(807, 3528);
		}
	}
}
