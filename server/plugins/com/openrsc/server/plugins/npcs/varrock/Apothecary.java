package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class Apothecary implements
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		if (player.getQuestStage(Quests.ROMEO_N_JULIET) == 4) {
			say(player, n, "Apothecary. Father Lawrence sent me",
				"I need some Cadava potion to help Romeo and Juliet");
			npcsay(player, n, "Cadava potion. Its pretty nasty. And hard to make",
				"Wing of Rat, Tail of frog. Ear of snake and horn of dog",
				"I have all that, but I need some cadavaberries",
				"You will have to find them while I get the rest ready",
				"Bring them here when you have them. But be careful. They are nasty");
			player.updateQuestStage(Quests.ROMEO_N_JULIET, 5);
			return;
		} else if (player.getQuestStage(Quests.ROMEO_N_JULIET) == 5) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.CADAVABERRIES.id())) {
				npcsay(player, n, "Keep searching for the berries",
					"They are needed for the potion");
			} else {
				npcsay(player, n, "Well done. You have the berries");
				mes("You hand over the berries");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.CADAVABERRIES.id()));
				player.message("Which the apothecary shakes up in vial of strange liquid");
				npcsay(player, n, "Here is what you need");
				player.message("The apothecary gives you a Cadava potion");
				player.getCarriedItems().getInventory().add(new Item(ItemId.CADAVA.id()));
				player.updateQuestStage(Quests.ROMEO_N_JULIET, 6);
			}
			return;
		}
		npcsay(player, n, "I am the apothecary", "I have potions to brew. Do you need anything specific?");
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
		option = multi(player, n, "Can you make a strength potion?",
			"Do you know a potion to make hair fall out?",
			"Have you got any good potions to give way?");

		if (option == 0) {
			if (ifheld(player, ItemId.COINS.id(), 5)
				&& player.getCarriedItems().hasCatalogID(ItemId.LIMPWURT_ROOT.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.RED_SPIDERS_EGGS.id(), Optional.of(false))) {
				say(player, n, "I have the root and spiders eggs needed to make it");
				npcsay(player, n, "Well give me them and 5 gold and I'll make you your potion");
				int sub_option = multi(player, n, "Yes ok", "No thanks");
				if (sub_option == 0) {
					int cointimes = player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) / 5;
					int roottimes = player.getCarriedItems().getInventory().countId(ItemId.LIMPWURT_ROOT.id());
					int eggtimes = player.getCarriedItems().getInventory().countId(ItemId.RED_SPIDERS_EGGS.id());
					int repeat = Math.min(cointimes, roottimes);
					repeat = Math.min(eggtimes, repeat);
					startbatch(repeat);
					batchPotion(player);
				}
			} else {
				npcsay(player, n,
					"Yes. But the ingredients are a little hard to find",
					"If you ever get them I will make it for you. For a cost");
				say(player, n, "So what are the ingredients?");
				npcsay(player, n,
					"You'll need to find to find the eggs of the deadly red spider",
					"And a limpwurt root",
					"Oh and you'll have to pay me 5 coins");
				say(player, n, "Ok, I'll look out for them");
			}
		} else if (option == 1) {
			npcsay(player, n, "I do indeed. I gave it to my mother. That's why I now live alone");
		} else if (option == 2) {
			if (player.getCarriedItems().hasCatalogID(ItemId.POTION.id(), Optional.of(false))) {
				npcsay(player, n, "Only that spot cream. Hope you enjoy it",
					"Yes, ok. Try this potion");
				give(player, ItemId.POTION.id(), 1);
			} else {
				int chance = DataConversions.random(0, 2);
				if (chance < 2) {
					npcsay(player, n, "Yes, ok. Try this potion");
					give(player, ItemId.POTION.id(), 1);
				} else {
					npcsay(player, n, "Sorry, charity is not my strong point");
				}
			}
		} else if (option == 3 && config().WANT_EXPERIENCE_ELIXIRS) {
			npcsay(player, n, "Yes, it's my most mysterious and special elixir",
				"It has a strange taste and sure does give you a rush",
				"I would know..",
				"I sell it for 5,000gp");
			int menu = multi(player, n, "Yes please", "No thankyou");
			if (menu == 0) {
				long lastElixir = 0;
				if (player.getCache().hasKey("buy_elixir")) {
					lastElixir = player.getCache().getLong("buy_elixir");
				}
				if (System.currentTimeMillis() - lastElixir < 24 * 60 * 60 * 1000) {
					npcsay(player, n, "Wait.. it's you, I recently made an elixir for you",
						"I don't want to poison my customers",
						"You'll need to wait before I make you a new one");
					int time = (int) (86400 - ((System.currentTimeMillis() - lastElixir) / 1000));
					player.message("You need to wait: " + DataConversions.getDateFromMsec(time * 1000));
					return;
				}
				if (ifheld(player, ItemId.COINS.id(), 5000)) {
					say(player, n, "I have the 5,000 gold with me");
					player.message("you give Apothecary 5,000 gold");
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5000));
					mes("Apothecary: starts brewing and fixes to a elixir");
					delay(3);
					player.message("Apothecary gives you a mysterious experience elixir.");
					//TODO: Determine if elixir will be added and indexed ID if so
					//addItem(p, ItemId.EXPERIENCE_ELIXIR.id(), 1);
					player.getCache().store("buy_elixir", System.currentTimeMillis());
				} else {
					say(player, n, "Oops, I don't have enough coins");
					npcsay(player, n, "Ok. I need my money, the ingredients are hard to find");
				}
			}
		}
	}

	private void batchPotion(Player player) {
		if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) < 5) {
			player.message("You don't have enough coins");
			return;
		}
		if (player.getCarriedItems().getInventory().countId(ItemId.LIMPWURT_ROOT.id()) < 1
			|| player.getCarriedItems().getInventory().countId(ItemId.RED_SPIDERS_EGGS.id()) < 1) {
			player.message("You don't have all the ingredients");
			return;
		}
		player.message("You give a limpwurt root some red spiders eggs and 5 coins to the apothecary");
		player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
		player.getCarriedItems().remove(new Item(ItemId.LIMPWURT_ROOT.id()));
		player.getCarriedItems().remove(new Item(ItemId.RED_SPIDERS_EGGS.id()));
		delay(3);
		player.message("The Apothecary brews up a potion");
		delay(4);
		player.message("The Apothecary gives you a strength potion");
		give(player, ItemId.FULL_STRENGTH_POTION.id(), 1);
		delay();

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !ifbatchcompleted()) {
			batchPotion(player);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.APOTHECARY.id();
	}
}
