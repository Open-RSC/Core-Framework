package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassDemons implements KillNpcTrigger {

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return inArray(n.getID(), NpcId.OTHAINIAN.id(), NpcId.DOOMION.id(), NpcId.HOLTHION.id());
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (inArray(n.getID(), NpcId.OTHAINIAN.id(), NpcId.DOOMION.id(), NpcId.HOLTHION.id())) {
			if (!player.getCache().hasKey("doll_of_iban") && player.getQuestStage(Quests.UNDERGROUND_PASS) != 6) {
				player.message("the demon slumps to the floor");
				teleportPlayer(player, n);
			} else {
				teleportPlayer(player, n);
				mes("the demon slumps to the floor");
				delay(3);
				if (!player.getCarriedItems().hasCatalogID(n.getID() + 364, Optional.empty())) {
					player.message("around it's neck you find a strange looking amulet");
					give(player, n.getID() + 364, 1); // will give correct ammys for all.
				}
			}
		}
	}

	private void teleportPlayer(Player player, Npc n) {
		if (n.getID() == NpcId.OTHAINIAN.id()) {
			player.teleport(796, 3541);
		} else if (n.getID() == NpcId.DOOMION.id()) {
			player.teleport(807, 3541);
		} else if (n.getID() == NpcId.HOLTHION.id()) {
			player.teleport(807, 3528);
		}
	}
}
