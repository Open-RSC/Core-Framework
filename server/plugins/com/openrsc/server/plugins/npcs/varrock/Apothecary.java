package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class Apothecary implements
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player p, final Npc n) {
		if (p.getQuestStage(Quests.ROMEO_N_JULIET) == 4) {
			say(p, n, "Apothecary. Father Lawrence sent me",
				"I need some Cadava potion to help Romeo and Juliet");
			npcsay(p, n, "Cadava potion. Its pretty nasty. And hard to make",
				"Wing of Rat, Tail of frog. Ear of snake and horn of dog",
				"I have all that, but I need some cadavaberries",
				"You will have to find them while I get the rest ready",
				"Bring them here when you have them. But be careful. They are nasty");
			p.updateQuestStage(Quests.ROMEO_N_JULIET, 5);
			return;
		} else if (p.getQuestStage(Quests.ROMEO_N_JULIET) == 5) {
			if (!p.getCarriedItems().hasCatalogID(ItemId.CADAVABERRIES.id())) {
				npcsay(p, n, "Keep searching for the berries",
					"They are needed for the potion");
			} else {
				npcsay(p, n, "Well done. You have the berries");
				Functions.mes(p, "You hand over the berries");
				p.getCarriedItems().remove(new Item(ItemId.CADAVABERRIES.id()));
				p.message("Which the apothecary shakes up in vial of strange liquid");
				npcsay(p, n, "Here is what you need");
				p.message("The apothecary gives you a Cadava potion");
				p.getCarriedItems().getInventory().add(new Item(ItemId.CADAVA.id()));
				p.updateQuestStage(Quests.ROMEO_N_JULIET, 6);
			}
			return;
		}
		npcsay(p, n, "I am the apothecary", "I have potions to brew. Do you need anything specific?");
		int option;
		/*if (!getServer().getConfig().WANT_EXPERIENCE_ELIXIRS)
			option = showMenu(p, n, "Can you make a strength potion?",
				"Do you know a potion to make hair fall out?",
				"Have you got any good potions to give way?");
		else
			option = showMenu(p, n, "Can you make a strength potion?",
				"Do you know a potion to make hair fall out?",
				"Have you got any good potions to give way?",
				"Do you have any experience elixir?");*/

		// Disabled experience elixir due to not being functional at this time
		option = multi(p, n, "Can you make a strength potion?",
			"Do you know a potion to make hair fall out?",
			"Have you got any good potions to give way?");

		if (option == 0) {
			if (ifheld(p, ItemId.COINS.id(), 5)
				&& p.getCarriedItems().hasCatalogID(ItemId.LIMPWURT_ROOT.id(), Optional.of(false))
				&& p.getCarriedItems().hasCatalogID(ItemId.RED_SPIDERS_EGGS.id(), Optional.of(false))) {
				say(p, n, "I have the root and spiders eggs needed to make it");
				npcsay(p, n, "Well give me them and 5 gold and I'll make you your potion");
				int sub_option = multi(p, n, "Yes ok", "No thanks");
				if (sub_option == 0) {
					int cointimes = p.getCarriedItems().getInventory().countId(ItemId.COINS.id()) / 5;
					int roottimes = p.getCarriedItems().getInventory().countId(ItemId.LIMPWURT_ROOT.id());
					int eggtimes = p.getCarriedItems().getInventory().countId(ItemId.RED_SPIDERS_EGGS.id());
					int repeattimes = Math.min(cointimes, roottimes);
					repeattimes = Math.min(eggtimes, repeattimes);
					p.setBatchEvent(new BatchEvent(p.getWorld(), p, 600, "Apothecary Brews Potion", repeattimes, false) {
						@Override
						public void action() {
							if (p.getCarriedItems().getInventory().countId(ItemId.COINS.id()) < 5) {
								p.message("You don't have enough coins");
								interrupt();
								return;
							}
							if (p.getCarriedItems().getInventory().countId(ItemId.LIMPWURT_ROOT.id()) < 1
								|| p.getCarriedItems().getInventory().countId(ItemId.RED_SPIDERS_EGGS.id()) < 1) {
								p.message("You don't have all the ingredients");
								interrupt();
								return;
							}
							p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
							p.getCarriedItems().remove(new Item(ItemId.LIMPWURT_ROOT.id()));
							p.getCarriedItems().remove(new Item(ItemId.RED_SPIDERS_EGGS.id()));
							p.message("The Apothecary brews you a potion");
							p.message("The Apothecary gives you a strength potion");
							give(p, ItemId.FULL_STRENGTH_POTION.id(), 1);
						}
					});
				}
			} else {
				npcsay(p, n,
					"Yes. But the ingredients are a little hard to find",
					"If you ever get them I will make it for you. For a cost");
				say(p, n, "So what are the ingredients?");
				npcsay(p, n,
					"You'll need to find to find the eggs of the deadly red spider",
					"And a limpwurt root",
					"Oh and you'll have to pay me 5 coins");
				say(p, n, "Ok, I'll look out for them");
			}
		} else if (option == 1) {
			npcsay(p, n, "I do indeed. I gave it to my mother. That's why I now live alone");
		} else if (option == 2) {
			if (p.getCarriedItems().hasCatalogID(ItemId.POTION.id(), Optional.of(false))) {
				npcsay(p, n, "Only that spot cream. Hope you enjoy it",
					"Yes, ok. Try this potion");
				give(p, ItemId.POTION.id(), 1);
			} else {
				int chance = DataConversions.random(0, 2);
				if (chance < 2) {
					npcsay(p, n, "Yes, ok. Try this potion");
					give(p, ItemId.POTION.id(), 1);
				} else {
					npcsay(p, n, "Sorry, charity is not my strongest point");
				}
			}
		} else if (option == 3 && p.getWorld().getServer().getConfig().WANT_EXPERIENCE_ELIXIRS) {
			npcsay(p, n, "Yes, it's my most mysterious and special elixir",
				"It has a strange taste and sure does give you a rush",
				"I would know..",
				"I sell it for 5,000gp");
			int menu = multi(p, n, "Yes please", "No thankyou");
			if (menu == 0) {
				long lastElixir = 0;
				if (p.getCache().hasKey("buy_elixir")) {
					lastElixir = p.getCache().getLong("buy_elixir");
				}
				if (System.currentTimeMillis() - lastElixir < 24 * 60 * 60 * 1000) {
					npcsay(p, n, "Wait.. it's you, I recently made an elixir for you",
						"I don't want to poison my customers",
						"You'll need to wait before I make you a new one");
					int time = (int) (86400 - ((System.currentTimeMillis() - lastElixir) / 1000));
					p.message("You need to wait: " + DataConversions.getDateFromMsec(time * 1000));
					return;
				}
				if (ifheld(p, ItemId.COINS.id(), 5000)) {
					say(p, n, "I have the 5,000 gold with me");
					p.message("you give Apothecary 5,000 gold");
					p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5000));
					Functions.mes(p, "Apothecary: starts brewing and fixes to a elixir");
					p.message("Apothecary gives you a mysterious experience elixir.");
					//TODO: Determine if elixir will be added and indexed ID if so
					//addItem(p, ItemId.EXPERIENCE_ELIXIR.id(), 1);
					p.getCache().store("buy_elixir", System.currentTimeMillis());
				} else {
					say(p, n, "Oops, I don't have enough coins");
					npcsay(p, n, "Ok. I need my money, the ingredients are hard to find");
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.APOTHECARY.id();
	}
}
