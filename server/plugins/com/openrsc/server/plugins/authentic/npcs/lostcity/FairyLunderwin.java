package com.openrsc.server.plugins.authentic.npcs.lostcity;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class FairyLunderwin implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.FAIRY_LUNDERWIN.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.FAIRY_LUNDERWIN.id()) {
			npcsay(player, n, "I am buying cabbage, we have no such thing where I come from",
				"I pay hansomly for this wounderous object",
				"Would 100 gold coins per cabbage be a fair price?");
			if (player.getCarriedItems().hasCatalogID(ItemId.CABBAGE.id(), Optional.of(false))
				|| player.getCarriedItems().hasCatalogID(ItemId.SPECIAL_DEFENSE_CABBAGE.id(), Optional.of(false))) {
				int menu = multi(player, n, false, //do not send over
					"Yes, I will sell you all my cabbages",
					"No, I will keep my cabbbages");
				if (menu == 0) {
					say(player, n, "Yes, I will sell you all my cabbages");
					while (player.getCarriedItems().hasCatalogID(ItemId.CABBAGE.id(), Optional.of(false))
						|| player.getCarriedItems().hasCatalogID(ItemId.SPECIAL_DEFENSE_CABBAGE.id(), Optional.of(false))) {
						mes("You sell a cabbage");
						delay();
						if (player.getCarriedItems().hasCatalogID(ItemId.CABBAGE.id(), Optional.of(false))) {
							player.getCarriedItems().remove(new Item(ItemId.CABBAGE.id(), 1));
						} else if (player.getCarriedItems().hasCatalogID(ItemId.SPECIAL_DEFENSE_CABBAGE.id(), Optional.of(false))) {
							player.getCarriedItems().remove(new Item(ItemId.SPECIAL_DEFENSE_CABBAGE.id(), 1));
						}
						give(player, ItemId.COINS.id(), 100);
					}
					npcsay(player, n, "Good doing buisness with you");
				} else if (menu == 1) {
					say(player, n, "No, I will keep my cabbages");
				}
			} else {
				say(player, n, "Alas I have no cabbages either");
			}
		}
	}
}
