package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class Casket implements OpInvTrigger {

	@Override
	public boolean blockOpInv(Item item, Player p, String command) {
		return item.getCatalogId() == ItemId.CASKET.id();
	}

	@Override
	public void onOpInv(Item item, Player p, String command) {
		if (item.getCatalogId() == ItemId.CASKET.id()) {
			p.setBusyTimer(1300);

			int randomChanceOpen = DataConversions.random(0, 1081);

			mes(p, 1300, "you open the casket");
			p.playerServerMessage(MessageType.QUEST, "you find some treasure inside!");

			remove(p, ItemId.CASKET.id(), 1);

			// Coins, 54.11% chance
			if (randomChanceOpen <= 585) {
				// Randomly gives different coin amounts
				int randomChanceCoin = DataConversions.random(0, 6);
				if (randomChanceCoin == 0) {
					give(p, ItemId.COINS.id(), 10);
				} else if (randomChanceCoin == 1) {
					give(p, ItemId.COINS.id(), 20);
				} else if (randomChanceCoin == 2) {
					give(p, ItemId.COINS.id(), 40);
				} else if (randomChanceCoin == 3) {
					give(p, ItemId.COINS.id(), 80);
				} else if (randomChanceCoin == 4) {
					give(p, ItemId.COINS.id(), 160);
				} else if (randomChanceCoin == 5) {
					give(p, ItemId.COINS.id(), 320);
				} else {
					give(p, ItemId.COINS.id(), 640);
				}
			} else if (randomChanceOpen <= 859) {
				// Uncut sapphire, 25.34% chance
				give(p, ItemId.UNCUT_SAPPHIRE.id(), 1);
			} else if (randomChanceOpen <= 990) {
				// Uncut emerald, 12.11% chance
				give(p, ItemId.UNCUT_EMERALD.id(), 1);
			} else if (randomChanceOpen <= 1047) {
				//Uncut ruby, 5.27% chance
				give(p, ItemId.UNCUT_RUBY.id(), 1);
			} else if (randomChanceOpen <= 1064) {
				// Uncut diamond, 1.57% chance
				give(p, ItemId.UNCUT_DIAMOND.id(), 1);
			} else {
				// Tooth halves, 1.56% chance
				// Randomly give one part or the other
				int randomChanceKey = DataConversions.random(0, 1);
				if (randomChanceKey == 0) {
					give(p, ItemId.TOOTH_KEY_HALF.id(), 1);
				} else {
					give(p, ItemId.LOOP_KEY_HALF.id(), 1);
				}
			}
		}
	}
}
