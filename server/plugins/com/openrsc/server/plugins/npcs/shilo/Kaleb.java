package com.openrsc.server.plugins.npcs.shilo;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class Kaleb implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.KALEB.id()) {
			say(p, n, "Hello.");
			npcsay(p, n, "Hello Bwana,",
				"What can I do for you today?");
			int menu = multi(p, n, false, //do not send over
				"Can you tell me a bit about this place?",
				"Buy some wine: 1 Gold.",
				"Buy some Beer: 2 Gold.",
				"Buy a nights rest: 35 Gold",
				"Buy a pack of 5 Dorm tickets: 175 Gold");
			if (menu == 0) {
				say(p, n, "Can you tell me a bit about this place?");
				npcsay(p, n, "Of course Bwana, you look like a traveler!");
				say(p, n, "Yes I am actually!");
				npcsay(p, n, "Well, I am a traveller myself, and I have set up this hostel",
					"for adventurers and travellers who are weary from their journey",
					"There is a dormitory upstairs if you are tired, it costs 35 gold",
					"pieces which covers the costs of laundry and cleaning.");
			} else if (menu == 1) {
				npcsay(p, n, "Very good " + (p.isMale() ? "sir" : "madam") + "!");
				if (ifheld(p, ItemId.COINS.id(), 1)) {
					remove(p, ItemId.COINS.id(), 1);
					give(p, ItemId.WINE.id(), 1);
					p.message("You purchase a jug of wine.");
				} else {
					npcsay(p, n, "Sorry Bwana, you don't have enough money.");
				}
			} else if (menu == 2) {
				npcsay(p, n, "Very good " + (p.isMale() ? "sir" : "madam") + "!");
				if (ifheld(p, ItemId.COINS.id(), 2)) {
					remove(p, ItemId.COINS.id(), 2);
					give(p, ItemId.BEER.id(), 1);
					p.message("You purchase a frothy glass of beer.");
				} else {
					npcsay(p, n, "Sorry Bwana, you don't have enough money.");
				}
			} else if (menu == 3) {
				npcsay(p, n, "Very good " + (p.isMale() ? "sir" : "madam") + "!");
				if (ifheld(p, ItemId.COINS.id(), 35)) {
					remove(p, ItemId.COINS.id(), 35);
					give(p, ItemId.PARAMAYA_REST_TICKET.id(), 1);
					p.message("You purchase a ticket to access the dormitory.");
				} else {
					npcsay(p, n, "Sorry Bwana, you don't have enough money.");
				}
			} else if (menu == 5) {
				npcsay(p, n, "Very good " + (p.isMale() ? "sir" : "madam") + "!");
				if (ifheld(p, ItemId.COINS.id(), 175)) {
					remove(p, ItemId.COINS.id(), 175);
					give(p, ItemId.PARAMAYA_REST_TICKET.id(), 5);
					p.message("You purchase 5 tickets to access the dormitory.");
				} else {
					npcsay(p, n, "Sorry Bwana, you don't have enough money.");
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.KALEB.id();
	}

}
