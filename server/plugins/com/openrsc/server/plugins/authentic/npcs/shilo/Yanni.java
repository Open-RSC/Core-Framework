package com.openrsc.server.plugins.authentic.npcs.shilo;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Yanni implements TalkNpcTrigger, UseNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.YANNI.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		int countItemsInterest = 0;
		if (n.getID() == NpcId.YANNI.id()) {
			say(player, n, "Hello there!");
			npcsay(player, n, "Greetings Bwana!",
					"My name is Yanni and I buy and sell antiques ",
					"and other interesting items.",
					"If you have any interesting items that you might",
					"want to sell me, please let me see them and I'll",
					"offer you a fair price.",
					"Would you like me to have a look at your items",
					"and give you a quote?");
			int menu = multi(player, n,
					"Yes please!",
					"Maybe some other time?");
			if (menu == 0) {
				npcsay(player, n, "Great Bwana!");
				if (player.getCarriedItems().hasCatalogID(ItemId.BONE_KEY.id(), Optional.of(false))) {
					npcsay(player, n, "I'll give you 100 Gold for the Bone Key.");
					countItemsInterest++;
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.STONE_PLAQUE.id(), Optional.of(false))) {
					npcsay(player, n, "I'll give you 100 Gold for the Stone-Plaque.");
					countItemsInterest++;
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.TATTERED_SCROLL.id(), Optional.of(false))) {
					npcsay(player, n, "I'll give you 100 Gold for your tattered scroll");
					countItemsInterest++;
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.CRUMPLED_SCROLL.id(), Optional.of(false))) {
					npcsay(player, n, "I'll give you 100 Gold for your crumpled scroll");
					countItemsInterest++;
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.BERVIRIUS_TOMB_NOTES.id(), Optional.of(false))) {
					npcsay(player, n, "I'll give you 100 Gold for your Bervirius Tomb Notes.");
					countItemsInterest++;
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.LOCATING_CRYSTAL.id(), Optional.of(false))) {
					npcsay(player, n, "WOW! I'll give you 500 Gold for your Locating Crystal!");
					countItemsInterest++;
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.BEADS_OF_THE_DEAD.id(), Optional.of(false))) {
					npcsay(player, n, "Great I'll give you 1000 Gold for your Beads of the Dead.");
					countItemsInterest++;
				}
				if (countItemsInterest > 0) {
					if (countItemsInterest > 1) {
						npcsay(player, n, "Those are the items I am interested in Bwana.");
					} else {
						npcsay(player, n, "And that's the only item I am interested in.");
					}
					npcsay(player, n, "If you want to sell me those items, simply show them to me.");
				} else {
					npcsay(player, n, "Sorry Bwana, you have nothing I am interested in.");
				}

			} else if (menu == 1) {
				npcsay(player, n, "Sure thing.",
						"Have a nice day Bwana.");
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.YANNI.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (npc.getID() == NpcId.YANNI.id()) {
			switch (ItemId.getById(item.getCatalogId())) {
			case BONE_KEY:
				npcsay(player, npc, "Great item, here's 100 Gold for it.");
				player.getCarriedItems().remove(new Item(ItemId.BONE_KEY.id()));
				give(player, ItemId.COINS.id(), 100);
				player.message("You sell the Bone Key.");
				break;
			case STONE_PLAQUE:
				npcsay(player, npc, "Great item, here's 100 Gold for it.");
				player.getCarriedItems().remove(new Item(ItemId.STONE_PLAQUE.id()));
				give(player, ItemId.COINS.id(), 100);
				player.message("You sell the Stone Plaque.");
				break;
			case TATTERED_SCROLL:
				npcsay(player, npc, "Great item, here's 100 Gold for it.");
				player.getCarriedItems().remove(new Item(ItemId.TATTERED_SCROLL.id()));
				give(player, ItemId.COINS.id(), 100);
				player.message("You sell the Tattered Scroll.");
				break;
			case CRUMPLED_SCROLL:
				npcsay(player, npc, "Great item, here's 100 Gold for it.");
				player.getCarriedItems().remove(new Item(ItemId.CRUMPLED_SCROLL.id()));
				give(player, ItemId.COINS.id(), 100);
				player.message("You sell the crumpled Scroll.");
				break;
			case BERVIRIUS_TOMB_NOTES:
				npcsay(player, npc, "Great item, here's 100 Gold for it.");
				player.getCarriedItems().remove(new Item(ItemId.BERVIRIUS_TOMB_NOTES.id()));
				give(player, ItemId.COINS.id(), 100);
				player.message("You sell the Bervirius Tomb Notes.");
				break;
			case LOCATING_CRYSTAL:
				npcsay(player, npc, "Great item, here's 500 Gold for it.");
				player.getCarriedItems().remove(new Item(ItemId.LOCATING_CRYSTAL.id()));
				give(player, ItemId.COINS.id(), 500);
				player.message("You sell the Locating Crystal.");
				break;
			case BEADS_OF_THE_DEAD:
				npcsay(player, npc, "Great item, here's 1000 Gold for it.");
				player.getCarriedItems().remove(new Item(ItemId.BEADS_OF_THE_DEAD.id()));
				give(player, ItemId.COINS.id(), 1000);
				player.message("You sell Beads of the Dead.");
				break;
			default:
				player.message("Nothing interesting happens");
				break;
			}
		}
	}
}
