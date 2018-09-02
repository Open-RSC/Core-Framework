package com.openrsc.server.plugins.skills;


public class LootItem implements Comparable<LootItem> {
	private int chance;
	private final int id;
	private final int amount;

	public LootItem(int id, int amount, int chance) {
		this.id = id;
		this.amount = amount;
		this.chance = chance;
	}

	public int getChance() {
		return chance;
	}

	@Override
	public int compareTo(LootItem arg0) {
		if (getChance() > arg0.getChance())
			return 1;
		else
			return -1;
	}

	public int getAmount() {
		return amount;
	}

	public int getId() {
		return id;
	}
}
