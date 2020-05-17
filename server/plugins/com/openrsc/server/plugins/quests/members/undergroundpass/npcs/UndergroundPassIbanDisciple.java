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
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.IBAN_DISCIPLE.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.IBAN_DISCIPLE.id()) {
			if (player.getQuestStage(Quests.UNDERGROUND_PASS) == -1) {
				mes("you search the diciples remains");
				if (!player.getCarriedItems().hasCatalogID(ItemId.STAFF_OF_IBAN.id(), Optional.empty())
					&& !player.getCarriedItems().hasCatalogID(ItemId.STAFF_OF_IBAN_BROKEN.id(), Optional.empty())) {
					player.message("and find a staff of iban");
					give(player, ItemId.STAFF_OF_IBAN_BROKEN.id(), 1);
				} else {
					player.message("but find nothing");
				}
			} else {
				addobject(ItemId.ROBE_OF_ZAMORAK_TOP.id(), 1, player.getX(), player.getY(), player);
				addobject(ItemId.ROBE_OF_ZAMORAK_BOTTOM.id(), 1, player.getX(), player.getY(), player);
			}
		}
	}
}
