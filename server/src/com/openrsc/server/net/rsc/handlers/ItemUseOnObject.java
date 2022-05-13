package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToObjectAction;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.ItemOnObjectStruct;
import com.openrsc.server.plugins.triggers.UseBoundTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

public class ItemUseOnObject implements PayloadProcessor<ItemOnObjectStruct, OpcodeIn> {

	private void handleDoor(final Player player, final Point location,
							final GameObject object, final int dir, final Item item) {
		player.setWalkToAction(new WalkToObjectAction(player, object) {
			public void executeInternal() {
				getPlayer().resetPath();
				GameObject obj = getPlayer().getViewArea().getWallObjectWithDir(
					object.getLocation(), object.getDirection());
				if (getPlayer().isBusy() || getPlayer().isRanging()
					|| !getPlayer().getCarriedItems().hasCatalogID(item.getCatalogId()) || obj == null
					|| !obj.equals(object)) {
					return;
				}
				getPlayer().resetAll();

				if (item.getDef(getPlayer().getWorld()).isMembersOnly()
					&& !getPlayer().getConfig().MEMBER_WORLD) {
					getPlayer().message(getPlayer().MEMBER_MESSAGE);
					return;
				}
				if (getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(
						UseBoundTrigger.class,
						getPlayer(),
						new Object[]{getPlayer(), object, item}, this)) {
					return;
				}
			}
		});
	}

	private void handleObject(final Player player, final Point location,
							  final GameObject object, final Item item) {
		if ((object.getID() == 226 || object.getID() == 232) && player.withinRange(object, 2)) {
			player.resetPath();
			player.resetAll();
			if (player.getWorld().getServer().getPluginHandler().handlePlugin(
					UseLocTrigger.class, player, new Object[]{player, object, item}))
				return;
		}
		player.setWalkToAction(new WalkToObjectAction(player, object) {
			public void executeInternal() {
				getPlayer().resetPath();
				GameObject obj = getPlayer().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				if (obj == null || getPlayer().isBusy() || getPlayer().isRanging()
					|| !getPlayer().getCarriedItems().getInventory().contains(item)
					|| !getPlayer().atObject(object) || obj == null) {
					return;
				}
				getPlayer().resetAll();

				if (item.getDef(getPlayer().getWorld()).isMembersOnly()
					&& !getPlayer().getConfig().MEMBER_WORLD) {
					getPlayer().message(getPlayer().MEMBER_MESSAGE);
					return;
				}

				if (getPlayer().getWorld().getServer().getPluginHandler()
					.handlePlugin(UseLocTrigger.class, getPlayer(),
						new Object[]{getPlayer(), (GameObject) object, item}, this))
					return;
			}
		});
	}

	public void process(ItemOnObjectStruct payload, Player player) throws Exception {
		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			return;
		}

		if (player.getDuel().isDueling()) {
			return;
		}

		if (player.isBusy()) {
			player.resetPath();// sendSound
			return;
		}
		OpcodeIn pID = payload.getOpcode();
		player.resetAll();
		GameObject object;
		Item item;
		OpcodeIn packetOne = OpcodeIn.USE_WITH_BOUNDARY;
		OpcodeIn packetTwo = OpcodeIn.USE_ITEM_ON_SCENERY;

		if (pID == packetOne) { // Use Item on Boundary
			object = player.getViewArea().getWallObjectWithDir(Point.location(payload.coordObject.getX(), payload.coordObject.getY()), payload.direction);
			if (object == null) {
				player.setSuspiciousPlayer(true, "item on null door");
				player.resetPath();
				return;
			}
			int dir = object.getDirection();
			int slotID = payload.slotID;
			if (player.getConfig().WANT_EQUIPMENT_TAB && slotID == -1)
			{
				// they used an item from their equipment slot
				if (player.isUsingCustomClient()) {
					int itemID = payload.itemID;
					int realSlot = player.getCarriedItems().getEquipment().searchEquipmentForItem(itemID);
					if (realSlot == -1)
						return;
					item = player.getCarriedItems().getEquipment().get(realSlot);
					if (item == null)
						return;
				} else {
					player.message("only custom clients can use items from the equipment slots.");
					return;
				}
			} else
				item = player.getCarriedItems().getInventory().get(slotID);
			if (object.getType() == 0 || item == null || item.getItemStatus().getNoted()) { // This
				player.setSuspiciousPlayer(true, "item on null equipment slot or something");
				return;
			}
			handleDoor(player, object.getLocation(), object, dir, item);
		} else if (pID == packetTwo) { // Use Item on Scenery
			object = player.getViewArea().getGameObject(Point.location(payload.coordObject.getX(), payload.coordObject.getY()));
			if (object == null) {
				player.setSuspiciousPlayer(true, "item on null GameObject");
				player.resetPath();
				return;
			}
			int slotID = payload.slotID;
			if (player.getConfig().WANT_EQUIPMENT_TAB && slotID > Inventory.MAX_SIZE) {
				item = player.getCarriedItems().getEquipment().get(slotID - Inventory.MAX_SIZE);
			} else
				item = player.getCarriedItems().getInventory().get(slotID);
			if (object.getType() == 1 || item == null) { // This
				player.setSuspiciousPlayer(true, "null item or object");
				return;
			}

			// Currently, using notes on scenery is not supported.
			// However, we do allow it for the custom Seers Party Chest
			if (item.getItemStatus().getNoted() && !((object.getID() == 18 || object.getID() == 17) && object.getLocation().isInSeersPartyHall())) {
				player.message("Nothing interesting happens");
				return;
			}

			handleObject(player, object.getLocation(), object, item);
		}
	}

}
