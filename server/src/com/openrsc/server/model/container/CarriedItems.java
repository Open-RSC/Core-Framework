package com.openrsc.server.model.container;

import com.openrsc.server.model.entity.player.Player;

import java.util.Optional;

/** CarriedItems: A wrapper for inventory and equipment items.
 *
 *  This class is to be used in place of directly interacting with
 *  the Equipment or Inventory classes. There are explicit cases in
 *  which you would want to interact with only those classes, for
 *  custom content. Otherwise, always use the functions here.
 */
public class CarriedItems {

	private Inventory inventory; // List of items in the inventory container
	private Equipment equipment; // List of items in the equipment container
	private final Player player; // Player owning the inventory/equipment

	public CarriedItems(Player player) {
		this.player = player;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public Equipment getEquipment() {
		return this.equipment;
	}

	/** CarriedItems::hasCatalogID
	 *  Searches both the inventory and equipment for a specific
	 *  catalog ID. Can specify if notes are allowed.
	 */
	public boolean hasCatalogID(final int catalogID) {
		return this.hasCatalogID(catalogID, Optional.of(false));
	}

	public boolean hasCatalogID(final int catalogID, final Optional<Boolean> noted) {
		// noted not specified, check both normal and noted counterparts
		if (!noted.isPresent()) {
			if (getInventory().hasCatalogID(catalogID))
				return true;
			else
				return getEquipment().hasCatalogID(catalogID);
		} else {
			boolean isNoted = noted.get();
			if (getInventory().hasCatalogID(catalogID, isNoted))
				return true;
			else
				return getEquipment().hasCatalogID(catalogID);
		}

	}

	/** CarriedItems::shatter
	 * Searches for and destroys the provided item.
	 * For use with things like rings that break after certain number of uses.
	 * @param item The item to shatter
	 */
	public void shatter(Item item) {
		Item itemToShatter = getEquipment().get(
			getEquipment().searchEquipmentForItem(item.getCatalogId())
		);
		if (player.getConfig().WANT_EQUIPMENT_TAB && itemToShatter != null) {
			player.getCarriedItems().getEquipment().remove(itemToShatter, 1);
		} else {
			itemToShatter = getInventory().get(
				getInventory().getLastIndexById(item.getCatalogId())
			);
			if (itemToShatter == null) return;
			remove(itemToShatter);
		}
		player.message("Your " + player.getWorld().getServer().getEntityHandler().getItemDef(itemToShatter.getCatalogId()).getName() + " shatters");
	}

	/** CarriedItems::remove
	 *  Searches and removes from the inventory the last existing
	 *  items that matches our supplied item argument. Will first
	 *  search the inventory, and then the equipment if not found.
	 */
	public int remove(Item item) {
		return remove(item, true);
	}

	// TODO: Add parameter allowNoted
	public int remove(Item item, boolean updateClient) {
		// If the item id isn't assigned, first attempt to get it from the inventory.
		Item toRemove = item;
		if (item.getItemId() == -1) {
			toRemove = getInventory().get(
				getInventory().getLastIndexById(item.getCatalogId(), Optional.of(item.getNoted()))
			);
			if (toRemove != null) {
				item = new Item(toRemove.getCatalogId(), item.getAmount(), toRemove.getNoted(), toRemove.getItemId());
			}
		}

		// If we don't find it in the inventory, attempt to get it from equipment.
		if (toRemove != null) {
			return getInventory().remove(item, updateClient);
		}
		else {
			toRemove = getEquipment().get(
				getEquipment().searchEquipmentForItem(item.getCatalogId())
			);
			if (toRemove != null && toRemove.getAmount() >= item.getAmount()) {
				item = new Item(toRemove.getCatalogId(), item.getAmount(), toRemove.getNoted(), toRemove.getItemId());
				return getEquipment().remove(item, item.getAmount());
			}
		}
		return -1;
	}
}
