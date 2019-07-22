package com.openrsc.server.model.container;

import com.openrsc.server.model.entity.player.Player;

import java.util.ArrayList;

public class Equipment {

	//Number of equipment slots the player has
	public static final int slots = 13;
	public Item[] list = new Item[slots];
	private Player player = null;


	public Equipment(Player p) {
		this.player = p;
		for (int i = 0; i < slots; i ++)
			list[i] = null;
	}

	public int getWeaponAim() {
		int total = 1;
		for (Item item : list)
			total += item == null ? 0 : item.getDef().getWeaponAimBonus();
		return total;
	}

	public int getWeaponPower() {
		int total = 1;
		for (Item item : list)
			total += item == null ? 0 : item.getDef().getWeaponPowerBonus();
		return total;
	}

	public int getArmour() {
		int total = 1;
		for (Item item : list)
			total += item == null ? 0 : item.getDef().getArmourBonus();
		return total;
	}

	public int equipCount() {
		int total = 0;
		for (Item item : list) {
			if (item != null)
				total++;
		}
		return total;
	}

	public int hasEquipped(int id) {
		Item item;
		for (int i = 0; i < slots; i++) {
			item = list[i];
			if (item != null && item.getID() == id)
				return i;
		}
		return -1;
	}

	public Item getAmmoItem() {
		return list[12];
	}


}
