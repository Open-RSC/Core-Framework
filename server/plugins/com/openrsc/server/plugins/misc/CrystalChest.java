package com.openrsc.server.plugins.misc;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.showBubble;

import java.util.ArrayList;

public class CrystalChest implements InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener {

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		return item.getID() == ItemId.CRYSTAL_KEY.id() && obj.getID() == 248;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
		showBubble(player, item);
		message(player, "You use the key to unlock the chest");
		if (player.getInventory().remove(item) > -1) {
			ArrayList<Item> loot = new ArrayList<Item>();
			loot.add(new Item(ItemId.UNCUT_DRAGONSTONE.id(), 1));
			int percent = DataConversions.random(0, 10000);
			if (percent < 26) {
				loot.add(new Item(ItemId.RUNE_PLATE_MAIL_LEGS.id(), 1));
			} else if (percent < 132) {
				loot.add(new Item(ItemId.ADAMANTITE_SQUARE_SHIELD.id(), 1));
			} else if (percent < 407) {
				loot.add(new Item(ItemId.IRON_ORE_CERTIFICATE.id(), 30));
			} else if (percent < 733) {
				loot.add(new Item(ItemId.TOOTH_KEY_HALF.id(), 1));
				loot.add(new Item(ItemId.COINS.id(), 750));
			} else if (percent < 1084) {
				loot.add(new Item(ItemId.RUNITE_BAR.id(), 3));
			} else if (percent < 1451) {
				loot.add(new Item(ItemId.LOOP_KEY_HALF.id(), 1));
				loot.add(new Item(ItemId.COINS.id(), 750));
			} else if (percent < 1874) {
				loot.add(new Item(ItemId.RUBY.id(), 2));
				loot.add(new Item(ItemId.DIAMOND.id(), 2));
			} else if (percent < 2529) {
				loot.add(new Item(ItemId.COAL_CERTIFICATE.id(), 20));
			} else if (percent < 3302) {
				loot.add(new Item(ItemId.FIRE_RUNE.id(), 50));
				loot.add(new Item(ItemId.WATER_RUNE.id(), 50));
				loot.add(new Item(ItemId.AIR_RUNE.id(), 50));
				loot.add(new Item(ItemId.EARTH_RUNE.id(), 50));
				loot.add(new Item(ItemId.MIND_RUNE.id(), 50));
				loot.add(new Item(ItemId.BODY_RUNE.id(), 50));
				loot.add(new Item(ItemId.DEATH_RUNE.id(), 10));
				loot.add(new Item(ItemId.NATURE_RUNE.id(), 10));
				loot.add(new Item(ItemId.CHAOS_RUNE.id(), 10));
				loot.add(new Item(ItemId.LAW_RUNE.id(), 10));
				loot.add(new Item(ItemId.COSMIC_RUNE.id(), 10));
			} else if (percent < 4359) {
				loot.add(new Item(ItemId.RAW_SWORDFISH_CERTIFICATE.id(), 1));
				loot.add(new Item(ItemId.COINS.id(), 1000));
			} else if (percent < 8328) {
				loot.add(new Item(ItemId.SPINACH_ROLL.id(), 1));
				loot.add(new Item(ItemId.COINS.id(), 2000));
			}
			for (Item i : loot) {
				if (i.getAmount() > 1 && !i.getDef().isStackable()) {
					for (int x = 0; x < i.getAmount(); x++) {
						player.getInventory().add(new Item(i.getID(), 1));
					}
				} else {
					player.getInventory().add(i);
				}
			}
		}
	}

}
