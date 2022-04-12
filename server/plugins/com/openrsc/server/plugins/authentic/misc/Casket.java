package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class Casket implements OpInvTrigger {

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.CASKET.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.CASKET.id()) {

			int randomChanceOpen = DataConversions.random(0, 1081);

			// Only 3 additional casket opens in replays,
			// 40gp twice from Tylerbeg, 1 uncut sapphire from Luis

			mes("you open the casket");
			delay(2);
			if (player.getCarriedItems().remove(new Item(ItemId.CASKET.id())) == -1) return;

			player.playerServerMessage(MessageType.QUEST, "you find some treasure inside!");

			// Coins, 54.11% chance
			if (randomChanceOpen <= 585) {
				// Randomly gives different coin amounts
				int randomChanceCoin = DataConversions.random(0, 6);
				if (randomChanceCoin == 0) {
					give(player, ItemId.COINS.id(), 10);
				} else if (randomChanceCoin == 1) {
					give(player, ItemId.COINS.id(), 20);
				} else if (randomChanceCoin == 2) {
					give(player, ItemId.COINS.id(), 40);
				} else if (randomChanceCoin == 3) {
					give(player, ItemId.COINS.id(), 80);
				} else if (randomChanceCoin == 4) {
					give(player, ItemId.COINS.id(), 160);
				} else if (randomChanceCoin == 5) {
					give(player, ItemId.COINS.id(), 320);
				} else {
					give(player, ItemId.COINS.id(), 640);
				}
			} else if (randomChanceOpen <= 859) {
				// Uncut sapphire, 25.34% chance
				give(player, ItemId.UNCUT_SAPPHIRE.id(), 1);
			} else if (randomChanceOpen <= 990) {
				// Uncut emerald, 12.11% chance
				give(player, ItemId.UNCUT_EMERALD.id(), 1);
			} else if (randomChanceOpen <= 1047) {
				//Uncut ruby, 5.27% chance
				give(player, ItemId.UNCUT_RUBY.id(), 1);
			} else if (randomChanceOpen <= 1064) {
				// Uncut diamond, 1.57% chance
				give(player, ItemId.UNCUT_DIAMOND.id(), 1);
			} else {
				// Tooth halves, 1.56% chance
				// Randomly give one part or the other
				int randomChanceKey = DataConversions.random(0, 1);
				if (randomChanceKey == 0) {
					give(player, ItemId.TOOTH_KEY_HALF.id(), 1);
				} else {
					give(player, ItemId.LOOP_KEY_HALF.id(), 1);
				}
			}
		}
	}
}
