package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToPointAction;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.ItemOnGroundItemStruct;
import com.openrsc.server.plugins.triggers.UseObjTrigger;

public class ItemUseOnGroundItem implements PayloadProcessor<ItemOnGroundItemStruct, OpcodeIn> {

	public void process(ItemOnGroundItemStruct payload, Player player) throws Exception {
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
		Point location = Point.location(payload.groundItemCoord.getX(), payload.groundItemCoord.getY());

		int groundItemId;
		int inventorySlot;

		groundItemId = payload.groundItemId;
		inventorySlot = payload.slotIndex;
		if (player.getConfig().WANT_EQUIPMENT_TAB && inventorySlot > Inventory.MAX_SIZE) {
			player.message("Please unequip your item and try again.");
			return;
		}
		final Item myItem = player.getCarriedItems().getInventory().get(inventorySlot);
		if (myItem == null)
			return;

		final GroundItem gItem = player.getRegion().getItem(groundItemId, location, player);

		if (gItem == null) {
			player.setSuspiciousPlayer(true, "item use on ground item null item");
			player.resetPath();
			return;
		}

		if (myItem.getItemStatus().getNoted() || gItem.getNoted()) {
			player.message("Nothing interesting happens");
			return;
		}

		boolean firemaking = myItem.getCatalogId() == ItemId.TINDERBOX.id();
		player.setWalkToAction(new WalkToPointAction(player,
			gItem.getLocation(), firemaking ? 0 : 1) {
			public void executeInternal() {
				if (getPlayer().isBusy()
					|| getPlayer().isRanging()
					|| getPlayer().getRegion().getItem(groundItemId, getLocation(), getPlayer()) == null
					|| !getPlayer().canReach(gItem) ) {
					return;
				}
				if (myItem == null || gItem == null)
					return;

				if ((myItem.getDef(getPlayer().getWorld()).isMembersOnly() || gItem.getDef()
					.isMembersOnly())
					&& !getPlayer().getConfig().MEMBER_WORLD) {
					getPlayer().message(getPlayer().MEMBER_MESSAGE);
					return;
				}

				getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(UseObjTrigger.class, getPlayer(), new Object[]{getPlayer(), gItem, myItem}, this);
			}
		});

	}

}
