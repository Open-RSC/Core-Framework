package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassKalrag implements KillNpcTrigger {

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
		return n.getID() == NpcId.KALRAG.id();
	}

	@Override
	public void onKillNpc(Player p, Npc n) {
		if (n.getID() == NpcId.KALRAG.id()) {
			n.killedBy(p);
			mes(p, "kalrag slumps to the floor",
				"poison flows from the corpse over the soil");
			if (!p.getCache().hasKey("poison_on_doll") && p.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				if (p.getCarriedItems().hasCatalogID(ItemId.A_DOLL_OF_IBAN.id(), Optional.of(false))) {
					mes(p, "you smear the doll of iban in the poisoned blood");
					p.message("it smells horrific");
					p.getCache().store("poison_on_doll", true);
				} else {
					mes(p, "it quikly seeps away into the earth");
					p.message("you dare not collect any without ibans doll");
				}
			}
		}
	}
}
