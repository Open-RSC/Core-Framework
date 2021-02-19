package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class CraftingCape implements OpInvTrigger {

	private boolean canTeleport(Player player) {
		if (player.getLocation().wildernessLevel() >= 30 || player.getLocation().isInFisherKingRealm()
			|| player.getLocation().isInsideGrandTreeGround()
			|| (player.getLocation().inModRoom() && !player.isAdmin())) {
			mes("A mysterious force blocks your teleport!");
			delay(3);
			mes("You can't use this teleport after level 30 wilderness");
			delay(3);
			return false;
		}
		if (player.getCarriedItems().getInventory().countId(ItemId.ANA_IN_A_BARREL.id()) > 0) {
			mes("You can't teleport while holding Ana,");
			delay(3);
			mes("It's just too difficult to concentrate.");
			delay(3);
			return false;
		}
		return true;
	}

	private void checkPlagueSample(Player player) {
		if (player.getCarriedItems().hasCatalogID(ItemId.PLAGUE_SAMPLE.id())) {
			mes("the plague sample is too delicate...");
			delay(3);
			mes("it disintegrates in the crossing");
			delay(3);
			while (player.getCarriedItems().getInventory().countId(ItemId.PLAGUE_SAMPLE.id()) > 0) {
				player.getCarriedItems().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
			}
		}
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (command.equalsIgnoreCase("Teleport") && canTeleport(player)) {
			mes("With a swish of your cape");
			delay(3);
			checkPlagueSample(player);
			if (item.getCatalogId() == ItemId.CRAFTING_CAPE.id()) {
				player.teleport(347, 599, true);
				mes("You teleport to the Crafting Guild");
				delay(3);
			}
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return (item.getCatalogId() == ItemId.CRAFTING_CAPE.id()
			&& command.equalsIgnoreCase("Teleport"));
	}
}
