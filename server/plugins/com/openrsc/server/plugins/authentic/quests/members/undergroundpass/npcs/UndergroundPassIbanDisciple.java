package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassIbanDisciple implements TalkNpcTrigger, KillNpcTrigger {

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.IBAN_DISCIPLE.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.IBAN_DISCIPLE.id()) {
			if (player.getQuestStage(Quests.UNDERGROUND_PASS) == -1) {
				mes("you search the diciples remains");
				delay(3);
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

	@Override
	public void onTalkNpc(Player player, Npc n) {
		say(player, n, "hi");
		if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.ROBE_OF_ZAMORAK_TOP.id())
			|| !player.getCarriedItems().getEquipment().hasEquipped(ItemId.ROBE_OF_ZAMORAK_BOTTOM.id())) {
			npcsay(player, n, "an imposter....die scum");
			n.startCombat(player);
		} else {
			int selected = DataConversions.getRandom().nextInt(4);

			if (selected == 0) {
				// nothing
			} else if (selected == 1) {
				npcsay(player, n, "hail the great one, my lord iban", "i die for you again and again");
				say(player, n, "is that possible?");
				npcsay(player, n, "under iban anything is possible", "death is only the beginning");
			} else if (selected == 2) {
				npcsay(player, n, "som molica aniul demonte");
				say(player, n, "pardon");
				npcsay(player, n, "som molica aniul demonte");
			} else if (selected == 3) {
				npcsay(player, n, "iban is our father, our guide", "soon he will rule all life");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n)  {
		return n.getID() == NpcId.IBAN_DISCIPLE.id();
	}
}
