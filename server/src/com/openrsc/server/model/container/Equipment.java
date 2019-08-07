package com.openrsc.server.model.container;

import com.openrsc.server.model.entity.player.Player;

import java.util.ArrayList;

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
				total += item == null ? 0 : item.getDef().getWeaponAimBonus();
			return total;
		}
	}

	public int getWeaponPower() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef().getWeaponPowerBonus();
			return total;
		}
	}

	public int getArmour() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef().getArmourBonus();
			return total;
		}
	}

	public int getMagic() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef().getMagicBonus();
			return total;
		}
	}

	public int getPrayer() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef().getPrayerBonus();
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


}
