package com.openrsc.server.plugins.npcs.lostcity;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class FairyLunderwin implements TalkToNpcListener,
	TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.FAIRY_LUNDERWIN.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.FAIRY_LUNDERWIN.id()) {
			npcTalk(p, n, "I am buying cabbage, we have no such thing where I come from",
				"I pay hansomly for this wounderous object",
				"Would 100 gold coins per cabbage be a fair price?");
			if (p.getCarriedItems().hasCatalogID(ItemId.CABBAGE.id(), Optional.of(false))
				|| p.getCarriedItems().hasCatalogID(ItemId.SPECIAL_DEFENSE_CABBAGE.id(), Optional.of(false))) {
				int menu = showMenu(p, n, false, //do not send over
					"Yes, I will sell you all my cabbages",
					"No, I will keep my cabbbages");
				if (menu == 0) {
					playerTalk(p, n, "Yes, I will sell you all my cabbages");
					while (p.getCarriedItems().hasCatalogID(ItemId.CABBAGE.id(), Optional.of(false))
						|| p.getCarriedItems().hasCatalogID(ItemId.SPECIAL_DEFENSE_CABBAGE.id(), Optional.of(false))) {
						message(p, 60, "You sell a cabbage");
						if (p.getCarriedItems().hasCatalogID(ItemId.CABBAGE.id(), Optional.of(false))) {
							removeItem(p, ItemId.CABBAGE.id(), 1);
						} else if (p.getCarriedItems().hasCatalogID(ItemId.SPECIAL_DEFENSE_CABBAGE.id(), Optional.of(false))) {
							removeItem(p, ItemId.SPECIAL_DEFENSE_CABBAGE.id(), 1);
						}
						addItem(p, ItemId.COINS.id(), 100);
					}
					npcTalk(p, n, "Good doing buisness with you");
				} else if (menu == 1) {
					playerTalk(p, n, "No, I will keep my cabbages");
				}
			} else {
				playerTalk(p, n, "Alas I have no cabbages either");
			}
		}
	}
}
