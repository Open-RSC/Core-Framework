package com.openrsc.server.plugins.authentic.npcs.shilo;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Kaleb implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.KALEB.id()) {
			say(player, n, "Hello.");
			npcsay(player, n, "Hello Bwana,",
				"What can I do for you today?");
			int menu = multi(player, n, false, //do not send over
				"Can you tell me a bit about this place?",
				"Buy some wine : 1 Gold.",
				"Buy some Beer: 2 Gold.",
				"Buy a nights rest: 35 Gold",
				"Buy a pack of 5 Dorm tickets: 175 Gold");
			if (menu == 0) {
				say(player, n, "Can you tell me a bit about this place?");
				npcsay(player, n, "Of course Bwana, you look like a traveler!");
				say(player, n, "Yes I am actually!");
				npcsay(player, n, "Well, I am a traveller myself, and I have set up this hostel",
					"for adventurers and travellers who are weary from their journey",
					"There is a dormitory upstairs if you are tired, it costs 35 gold",
					"pieces which covers the costs of laundry and cleaning.");
			} else if (menu == 1) {
				npcsay(player, n, player.getText("KalebVeryGood"));
				if (ifheld(player, ItemId.COINS.id(), 1)) {
					player.getCarriedItems().remove(new Item(ItemId.COINS.id()));
					give(player, ItemId.WINE.id(), 1);
					player.message("You purchase a jug of wine.");
				} else {
					npcsay(player, n, "Sorry Bwana, you don't have enough money.");
				}
			} else if (menu == 2) {
				npcsay(player, n, player.getText("KalebVeryGood"));
				if (ifheld(player, ItemId.COINS.id(), 2)) {
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 2));
					give(player, ItemId.BEER.id(), 1);
					player.message("You purchase a frothy glass of beer.");
				} else {
					npcsay(player, n, "Sorry Bwana, you don't have enough money.");
				}
			} else if (menu == 3) {
				npcsay(player, n, player.getText("KalebVeryGood"));
				if (ifheld(player, ItemId.COINS.id(), 35)) {
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 35));
					give(player, ItemId.PARAMAYA_REST_TICKET.id(), 1);
					player.message("You purchase a ticket to access the dormitory.");
				} else {
					npcsay(player, n, "Sorry Bwana, you don't have enough money.");
				}
			} else if (menu == 5) {
				npcsay(player, n, player.getText("KalebVeryGood"));
				if (ifheld(player, ItemId.COINS.id(), 175)) {
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 175));
					give(player, ItemId.PARAMAYA_REST_TICKET.id(), 5);
					player.message("You purchase 5 tickets to access the dormitory.");
				} else {
					npcsay(player, n, "Sorry Bwana, you don't have enough money.");
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.KALEB.id();
	}

}
