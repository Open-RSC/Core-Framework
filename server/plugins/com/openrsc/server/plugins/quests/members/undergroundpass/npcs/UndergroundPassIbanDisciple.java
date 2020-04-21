package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassIbanDisciple implements KillNpcTrigger {

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
		return n.getID() == NpcId.IBAN_DISCIPLE.id();
	}

	@Override
	public void onKillNpc(Player p, Npc n) {
		if (n.getID() == NpcId.IBAN_DISCIPLE.id()) {
			n.killedBy(p);
			if (p.getQuestStage(Quests.UNDERGROUND_PASS) == -1) {
				mes(p, "you search the diciples remains");
				if (!p.getCarriedItems().hasCatalogID(ItemId.STAFF_OF_IBAN.id(), Optional.empty())
					&& !p.getCarriedItems().hasCatalogID(ItemId.STAFF_OF_IBAN_BROKEN.id(), Optional.empty())) {
					p.message("and find a staff of iban");
					give(p, ItemId.STAFF_OF_IBAN_BROKEN.id(), 1);
				} else {
					p.message("but find nothing");
				}
			} else {
				addobject(ItemId.ROBE_OF_ZAMORAK_TOP.id(), 1, p.getX(), p.getY(), p);
				addobject(ItemId.ROBE_OF_ZAMORAK_BOTTOM.id(), 1, p.getX(), p.getY(), p);
			}
		}
	}
}
