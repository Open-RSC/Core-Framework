package com.openrsc.server.plugins.custom.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class ItemDurability implements OpInvTrigger {
	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return (item.getCatalogId() == ItemId.RING_OF_RECOIL.id() || item.getCatalogId() == ItemId.RING_OF_FORGING.id()
			|| item.getCatalogId() == ItemId.DWARVEN_RING.id())
			&& (command.equalsIgnoreCase("check") || command.equalsIgnoreCase("break"));
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (command.equalsIgnoreCase("check")) {
			int charges;
			int totalCharges;
			if (item.getCatalogId() == ItemId.RING_OF_RECOIL.id()) {
				totalCharges = config().RING_OF_RECOIL_LIMIT;
				charges = player.getCache().hasKey("ringofrecoil")
					? totalCharges - player.getCache().getInt("ringofrecoil")
					: totalCharges;
				player.message("Your Ring of Recoil has " + charges + "/" +
					totalCharges + " charges remaining.");
			} else if (item.getCatalogId() == ItemId.RING_OF_FORGING.id()) {
				totalCharges = config().RING_OF_FORGING_USES;
				charges = player.getCache().hasKey("ringofforging")
					? totalCharges - player.getCache().getInt("ringofforging")
					: totalCharges;
				player.message("Your Ring of Forging has " + charges + "/" +
					totalCharges + " charges remaining.");
			} else if (item.getCatalogId() == ItemId.DWARVEN_RING.id()) {
				totalCharges = config().DWARVEN_RING_USES;
				charges = player.getCache().hasKey("dwarvenring")
					? totalCharges - player.getCache().getInt("dwarvenring")
					: totalCharges;
				player.message("Your Dwarven Ring has " + charges + "/" +
					totalCharges + " charges remaining.");
			}
		} else if (command.equalsIgnoreCase("break")) {
			player.message("Are you sure you want to break your " + item.getDef(player.getWorld()).getName() + "?");
			delay();
			int choice = multi(player, "Yes", "No");
			if (choice != 0) return;
			if (item.getCatalogId() == ItemId.RING_OF_RECOIL.id()) {
				player.getCache().remove("ringofrecoil");
				player.getCarriedItems().shatter(item);
			} else if (item.getCatalogId() == ItemId.RING_OF_FORGING.id()) {
				player.getCache().remove("ringofforging");
				player.getCarriedItems().shatter(item);
			} else if (item.getCatalogId() == ItemId.DWARVEN_RING.id()) {
				player.getCache().remove("dwarvenring");
				player.getCarriedItems().shatter(item);
			}
		}
	}
}
