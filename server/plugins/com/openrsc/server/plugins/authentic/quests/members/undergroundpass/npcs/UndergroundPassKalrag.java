package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.npcs;

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
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.KALRAG.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.KALRAG.id()) {
			mes("kalrag slumps to the floor");
			delay(3);
			mes("poison flows from the corpse over the soil");
			delay(3);
			if (!player.getCache().hasKey("poison_on_doll") && player.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				if (player.getCarriedItems().hasCatalogID(ItemId.A_DOLL_OF_IBAN.id(), Optional.of(false))) {
					mes("you smear the doll of iban in the poisoned blood");
					delay(3);
					player.message("it smells horrific");
					player.getCache().store("poison_on_doll", true);
				} else {
					mes("it quikly seeps away into the earth");
					delay(3);
					player.message("you dare not collect any without ibans doll");
				}
			}
		}
	}
}
