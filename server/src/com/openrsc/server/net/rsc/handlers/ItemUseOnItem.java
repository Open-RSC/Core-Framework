package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.ItemOnItemStruct;
import com.openrsc.server.plugins.triggers.UseInvTrigger;

public final class ItemUseOnItem implements PayloadProcessor<ItemOnItemStruct, OpcodeIn> {

	public void process(ItemOnItemStruct payload, Player player) throws Exception {
		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			return;
		}

		if (player.getDuel().isDueling()) {
			return;
		}

		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final int itemIndex1 = payload.slotIndex1;
		final int itemIndex2 = payload.slotIndex2;
		Item item1 = player.getCarriedItems().getInventory().get(itemIndex1);
		Item item2 = player.getCarriedItems().getInventory().get(itemIndex2);

		if (item1 == null || item2 == null) {
			player.setSuspiciousPlayer(true, "use item on item has null item1 or item2");
			return;
		}
		if (itemIndex1 == itemIndex2) {
			player.message("Nothing interesting happens");
			return;
		}

		if (item1.getNoted() || item2.getNoted()) {
			player.message("Nothing interesting happens");
			return;
		}

		if (player.getConfig().WANT_EQUIPMENT_TAB && (itemIndex1 > Inventory.MAX_SIZE || itemIndex2 > Inventory.MAX_SIZE)) {
			player.message("Please unequip your item and try again.");
			return;
		}
		if (item1.getDef(player.getWorld()).isMembersOnly() || item2.getDef(player.getWorld()).isMembersOnly()) {
			if (!player.getConfig().MEMBER_WORLD) {
				player.sendMemberErrorMessage();
				return;
			}
		}

		// Services.lookup(DatabaseManager.class).addQuery(new
		// GenericLog(player.getUsername() + " used item " + item1 + " on item "
		// + item2 + " at " + player.getLocation()));

		player.getWorld().getServer().getPluginHandler().handlePlugin(UseInvTrigger.class, player, new Object[]{player, itemIndex1, item1, item2});
	}
}
