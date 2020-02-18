package com.openrsc.server.model.container;

import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Equipment {

	/**
	 * The asynchronous logger
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	//Number of equipment slots the player has
	public static final int slots = 14;
	private final Item[] list = new Item[slots];
	private Player player = null;


	public Equipment(Player p) {
		synchronized (list) {
			this.player = p;
			for (int i = 0; i < slots; i ++)
				list[i] = null;
		}
	}

	public Item[] getList() {
		return this.list;
	}

	public int getWeaponAim() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef(player.getWorld()).getWeaponAimBonus();
			return total;
		}
	}

	public int getWeaponPower() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef(player.getWorld()).getWeaponPowerBonus();
			return total;
		}
	}

	public int getArmour() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef(player.getWorld()).getArmourBonus();
			return total;
		}
	}

	public int getMagic() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef(player.getWorld()).getMagicBonus();
			return total;
		}
	}

	public int getPrayer() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef(player.getWorld()).getPrayerBonus();
			return total;
		}
	}

	public int equipCount() {
		synchronized (list) {
			int total = 0;
			for (Item item : list) {
				if (item != null)
					total++;
			}
			return total;
		}
	}

	public int hasEquipped(int id) {
		synchronized (list) {
			Item item;
			for (int i = 0; i < slots; i++) {
				item = list[i];
				if (item != null && item.getCatalogId() == id)
					return i;
			}
			return -1;
		}
	}

	public Item getAmmoItem() {
		synchronized (list) {
			return list[12];
		}
	}

	public void clearList() {
		synchronized (list) {
			for (int i = 0; i < list.length; i++) {
				list[i] = null;
			}
		}
	}

	public Item get(int index) {
		synchronized (list) {
			if (index < 0 || index >= slots) {
				return null;
			}
			return list[index];
		}
	}

	public void equip(int slot, int itemID, int amount) { this.equip(slot, new Item(itemID, amount)); }

	public void equip(int slot, Item item) {
		synchronized (list) {
			list[slot] = item;
			//Update the DB
			try {
				player.getWorld().getServer().getDatabase().querySavePlayerEquipmentAdd(player, item);
			} catch (GameDatabaseException ex) {
				LOGGER.error(ex.getMessage());
			}
		}
	}

	public void remove(int slot) {
		synchronized (list) {
			try {
				player.getWorld().getServer().getDatabase().querySavePlayerEquipmentDelete(player, list[slot]);
			} catch (GameDatabaseException ex) {
				LOGGER.error(ex.getMessage());
			}
			list[slot] = null;
			ActionSender.sendEquipmentStats(player, slot);
			player.updateWornItems(slot,
				player.getSettings().getAppearance().getSprite(slot));
		}
	}
	public int remove(int id, int amount) {
		synchronized (list) {
			for (int i = 0; i < slots; i++) {
				int actionTaken = -1;
				//-1: no action
				// 0: update quantity
				// 1: remove item
				Item curEquip = list[i];
				if (curEquip == null || curEquip.getDef(player.getWorld()) == null)
					continue;
				ItemDefinition curEquipDef = curEquip.getDef(player.getWorld());

				if (curEquip.getCatalogId() == id) {
					int curAmount = curEquip.getAmount();
					if (!curEquipDef.isStackable() && amount > 1)
						return -1;

					if (curAmount > amount) {
						list[i].setAmount(curAmount - amount);
						actionTaken = 0;
					} else if(curAmount < amount) {
						return -1;
					} else {
						actionTaken = 1;
						list[i] = null;
						player.updateWornItems(curEquipDef.getWieldPosition(),
							player.getSettings().getAppearance().getSprite(curEquipDef.getWieldPosition()));
					}
					//Update the DB
					try {
						if (actionTaken == 0) {
							player.getWorld().getServer().getDatabase().querySavePlayerItemUpdateAmount(player, list[i]);
						} else if (actionTaken == 1) {
							player.getWorld().getServer().getDatabase().querySavePlayerEquipmentDelete(player, curEquip);
						}
					} catch (GameDatabaseException ex) {
						LOGGER.error(ex.getMessage());
					}
					ActionSender.sendEquipmentStats(player);
					return i;
				}
			}
			return -1;
		}
	}



}
