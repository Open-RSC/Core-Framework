package com.openrsc.server.plugins.shared;

import com.openrsc.server.constants.AppearanceId;
import com.openrsc.server.database.impl.mysql.queries.logging.GenericLog;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.DropObjTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DropObject {
	public static void batchDrop(Player player, Item item, Boolean fromInventory, int amountToDrop, int totalToDrop, int invIndex) {
		Item searchItem;
		boolean found = false;
		if (fromInventory) {
			if (invIndex >= 0 && invIndex < player.getCarriedItems().getInventory().size()) {
				// search inventory using specified index
				searchItem = player.getCarriedItems().getInventory().get(invIndex);
				if (searchItem.equals(item)) {
					item = searchItem;
					found = true;
				}
			}
			if (!found) {
				// Grab the last item by the ID we are trying to drop when batching.
				item = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(item.getNoted()))
				);
			}
		}
		else {
			item = player.getCarriedItems().getEquipment().get(
				player.getCarriedItems().getEquipment().searchEquipmentForItem(item.getCatalogId())
			);
		}

		if (item == null) {
			player.message("You don't have the entered amount to drop");
			return;
		}

		int removingThisIteration = 1;
		if (fromInventory) {
			// Stacks or notes need to check their amount compared to the amount to drop.
			if (item.getAmount() > 1) {
				removingThisIteration = Math.min(amountToDrop, item.getAmount());
			}
			if (item.getItemId() != -1) {
				player.getCarriedItems().remove(new Item(item.getCatalogId(), removingThisIteration, item.getNoted(), item.getItemId()));
			} else {
				player.getCarriedItems().remove(new Item(item.getCatalogId(), removingThisIteration, item.getNoted()));
			}
			amountToDrop -= removingThisIteration;
		} else {
			int slot = player.getCarriedItems().getEquipment().searchEquipmentForItem(item.getCatalogId());
			if (slot == -1) return;

			// Always remove all when from equipment.
			removingThisIteration = item.getAmount();
			player.getCarriedItems().getEquipment().remove(item, removingThisIteration);
			ActionSender.sendEquipmentStats(player);

			final ItemDefinition itemDef = item.getDef(player.getWorld());
			final AppearanceId appearance = AppearanceId.getById(itemDef.getAppearanceId());
			if (itemDef.getWieldPosition() < 12 ||
				(itemDef.getWieldPosition() == AppearanceId.SLOT_MORPHING_RING && appearance.id() != AppearanceId.NOTHING.id())) {
				player.updateWornItems(itemDef.getWieldPosition(),
					player.getSettings().getAppearance().getSprite(itemDef.getWieldPosition()));
			}
			amountToDrop = 0;
		}

		GroundItem groundItem = new GroundItem(player.getWorld(), item.getCatalogId(), player.getX(), player.getY(), removingThisIteration, player, item.getNoted());
		ActionSender.sendSound(player, "dropobject");

		if (player.getWorld().getPlayer(DataConversions.usernameToHash(player.getUsername())) == null) {
			return;
		}

		player.getWorld().registerItem(groundItem, config().GAME_TICK * 300);
		player.getWorld().getServer().getGameLogger().addQuery(new GenericLog(player.getWorld(), player.getUsername() + " dropped " + item.getDef(player.getWorld()).getName() + " x"
			+ DataConversions.numberFormat(groundItem.getAmount()) + " at " + player.getLocation().toString()));

		// Display the Dropping x/y message only if we want batching,
		// we're dropping more than one item, and the item isn't a stack.
		if (config().BATCH_PROGRESSION && totalToDrop > 1 && removingThisIteration == 1) {
			player.message("Dropping " + (totalToDrop - amountToDrop) + "/" + totalToDrop
				+ " " + player.getWorld().getServer().getEntityHandler().getItemDef(item.getCatalogId()).getName());
		}

		// Repeat
		if (!ifinterrupted() && amountToDrop > 0) {
			delay();
			batchDrop(player, item, fromInventory, amountToDrop, totalToDrop, -1);
		}
	}
}
