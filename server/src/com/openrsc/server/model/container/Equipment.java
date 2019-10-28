package com.openrsc.server.model.container;

import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;

public class Equipment {

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
				if (item != null && item.getID() == id)
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
		}
	}

	public void remove(int slot) {
		synchronized (list) {
			list[slot] = null;
			ActionSender.sendEquipmentStats(player, slot);
			player.updateWornItems(slot,
				player.getSettings().getAppearance().getSprite(slot));
		}
	}
	public int remove(int id, int amount) {
		synchronized (list) {
			for (int i = 0; i < slots; i++) {
				Item curEquip = list[i];
				if (curEquip == null || curEquip.getDef(player.getWorld()) == null)
					continue;
				ItemDefinition curEquipDef = curEquip.getDef(player.getWorld());

				if (curEquip.getID() == id) {
					int curAmount = curEquip.getAmount();
					if (!curEquipDef.isStackable() && amount > 1)
						return -1;

					if (curAmount > amount) {
						list[i].setAmount(curAmount - amount);
					} else if(curAmount < amount) {
						return -1;
					} else {
						list[i] = null;
						player.updateWornItems(curEquipDef.getWieldPosition(),
							player.getSettings().getAppearance().getSprite(curEquipDef.getWieldPosition()));
					}

					ActionSender.sendEquipmentStats(player);
					return i;
				}
			}
			return -1;
		}
	}



}
