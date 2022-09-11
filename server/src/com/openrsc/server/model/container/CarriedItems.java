package com.openrsc.server.model.container;

import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import java.util.ArrayList;
import java.util.List;
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

		// If we don't find it in the inventory with required amount, attempt to get it from equipment.
		if (toRemove != null && getInventory().countId(toRemove.getCatalogId(), Optional.of(toRemove.getNoted())) >= item.getAmount()) {
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

	public boolean remove(final Item... items)
	{
		return remove(items, true);
	}

	/**
	 * Removes multiple items from inventory/equipment if and only if all items can be removed.
	 *
	 * @param items items to be removed.
	 * @param updateClient whether to update the player's client or not.
	 * @return true if all items were removed otherwise returns false.
	 */
	public boolean remove(final Item[] items, final boolean updateClient)
	{
		if (items.length == 0)
		{
			// Nothing to remove
			return false;
		}

		final List<Item> inventoryItems = new ArrayList<>(items.length);
		List<Item> equipmentItems = null;

		if (player.getConfig().WANT_EQUIPMENT_TAB)
		{
			equipmentItems = new ArrayList<>(items.length);
		}

		synchronized (inventory.getItems())
		{
			synchronized (equipment.getList())
			{
				for (final Item item : items)
				{
					int idx = inventory.getLastIndexById(item.getCatalogId(), Optional.of(item.getNoted()));

					if (idx != -1)
					{
						// Matching item found in inventory
						final Item invItem = inventory.get(idx);
						final ItemDefinition itemDef = invItem.getDef(player.getWorld());

						if (itemDef == null)
						{
							return false;
						}

						if ((itemDef.isStackable() || invItem.getNoted()) && invItem.getAmount() < item.getAmount())
						{
							// Insufficient quantity
							return false;
						}

						// Create copy of inventory item with amount to remove
						final Item removeItem = new Item(invItem.getCatalogId(), item.getAmount(), invItem.getNoted(), invItem.getItemId());

						inventoryItems.add(removeItem);
						continue;
					}

					if (equipmentItems == null)
					{
						return false;
					}

					idx = equipment.searchEquipmentForItem(item.getCatalogId());

					if (idx == -1)
					{
						return false;
					}

					// Matching item found in Equipment
					final Item equipItem = equipment.get(idx);
					final ItemDefinition itemDef = equipItem.getDef(player.getWorld());

					if (itemDef == null)
					{
						return false;
					}

					if (itemDef.isStackable() && equipItem.getAmount() < item.getAmount())
					{
						// Insufficient quantity
						return false;
					}

					// Create copy of equipment item with amount to remove
					final Item removeItem = new Item(equipItem.getCatalogId(), item.getAmount(), equipItem.getNoted(), equipItem.getItemId());

					equipmentItems.add(removeItem);
				}

				// Matching items were found in inventory/equipment for all items to be removed.

				for (final Item item : inventoryItems)
				{
					inventory.remove(item, updateClient);
				}

				if (equipmentItems != null)
				{
					for (final Item item : equipmentItems)
					{
						equipment.remove(item, item.getAmount(), updateClient);
					}
				}
			}
		}

		return true;
	}
}
