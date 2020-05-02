package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Skillcape implements OpInvTrigger {

	private boolean canTeleport(Player player) {
		if (player.getLocation().wildernessLevel() >= 30 || player.getLocation().isInFisherKingRealm()
			|| player.getLocation().isInsideGrandTreeGround()
			|| (player.getLocation().inModRoom() && !player.isAdmin())) {
			mes(player, "A mysterious force blocks your teleport!");
			mes(player, "You can't use this teleport after level 30 wilderness");
			return false;
		}
		if (player.getCarriedItems().getInventory().countId(ItemId.ANA_IN_A_BARREL.id()) > 0) {
			mes(player, "You can't teleport while holding Ana,",
				"It's just too difficult to concentrate.");
			return false;
		}
		return true;
	}

	private void checkPlagueSample(Player player) {
		if (player.getCarriedItems().hasCatalogID(ItemId.PLAGUE_SAMPLE.id())) {
			mes(player,"the plague sample is too delicate...");
			mes(player, "it disintegrates in the crossing");
			while (player.getCarriedItems().getInventory().countId(ItemId.PLAGUE_SAMPLE.id()) > 0) {
				player.getCarriedItems().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
			}
		}
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (command.equalsIgnoreCase("Teleport") && canTeleport(player)) {
			mes(player, "With a swish of your cape");
			checkPlagueSample(player);
			if (item.getCatalogId() == ItemId.CRAFTING_CAPE.id()) {
				player.teleport(347, 599, true);
				mes(player, "You teleport to the Crafting Guild");
			}
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		System.out.println("Ge");
		return (item.getCatalogId() == ItemId.CRAFTING_CAPE.id()
			&& command.equalsIgnoreCase("Teleport"));
	}
}
