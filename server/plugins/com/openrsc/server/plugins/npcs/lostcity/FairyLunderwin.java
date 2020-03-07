package com.openrsc.server.plugins.npcs.lostcity;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class FairyLunderwin implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.FAIRY_LUNDERWIN.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.FAIRY_LUNDERWIN.id()) {
			npcsay(p, n, "I am buying cabbage, we have no such thing where I come from",
				"I pay hansomly for this wounderous object",
				"Would 100 gold coins per cabbage be a fair price?");
			if (p.getCarriedItems().hasCatalogID(ItemId.CABBAGE.id(), Optional.of(false))
				|| p.getCarriedItems().hasCatalogID(ItemId.SPECIAL_DEFENSE_CABBAGE.id(), Optional.of(false))) {
				int menu = multi(p, n, false, //do not send over
					"Yes, I will sell you all my cabbages",
					"No, I will keep my cabbbages");
				if (menu == 0) {
					say(p, n, "Yes, I will sell you all my cabbages");
					while (p.getCarriedItems().hasCatalogID(ItemId.CABBAGE.id(), Optional.of(false))
						|| p.getCarriedItems().hasCatalogID(ItemId.SPECIAL_DEFENSE_CABBAGE.id(), Optional.of(false))) {
						mes(p, 60, "You sell a cabbage");
						if (p.getCarriedItems().hasCatalogID(ItemId.CABBAGE.id(), Optional.of(false))) {
							remove(p, ItemId.CABBAGE.id(), 1);
						} else if (p.getCarriedItems().hasCatalogID(ItemId.SPECIAL_DEFENSE_CABBAGE.id(), Optional.of(false))) {
							remove(p, ItemId.SPECIAL_DEFENSE_CABBAGE.id(), 1);
						}
						give(p, ItemId.COINS.id(), 100);
					}
					npcsay(p, n, "Good doing buisness with you");
				} else if (menu == 1) {
					say(p, n, "No, I will keep my cabbages");
				}
			} else {
				say(p, n, "Alas I have no cabbages either");
			}
		}
	}
}
