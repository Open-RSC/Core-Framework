package com.openrsc.server.content;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;

public class DropTable {
	ArrayList<Drop> drops;
	ArrayList<Accessor> accessors;
	int totalWeight;
	private static int RING_OF_WEALTH_BOOST_NUMERATOR = 1;
	private static int RING_OF_WEALTH_BOOST_DENOMINATOR = 128;


	public DropTable() {
		drops = new ArrayList<>();
		accessors = new ArrayList<>();
		totalWeight = 0;
	}

	public void addEmptyDrop(int weight) {
		drops.add(new Drop(ItemId.NOTHING.id(), 0, weight, dropType.NOTHING));
		this.totalWeight += weight;
	}

	public void addItemDrop(int itemID, int amount, int weight) {
		drops.add(new Drop(itemID, amount, weight, dropType.ITEM));
		this.totalWeight += weight;
	}

	public void addTableDrop(DropTable table, int weight) {
		drops.add(new Drop(table, weight));
		this.totalWeight += weight;
	}

	public void addAccessor(int id, int numerator, int denominator) {
		accessors.add(new Accessor(id, numerator, denominator));
	}

	public Item rollItem(boolean ringOfWealth, Player p) {
		DropTable rollTable;
		if (ringOfWealth) {
			rollTable = modifyTable(this);
		} else
			rollTable = this;

		int hit = DataConversions.random(1, rollTable.totalWeight);
		int sum = 0;
		for (Drop drop : rollTable.drops) {
			sum += drop.weight;
			if (sum >= hit) {
				if (drop.type == dropType.NOTHING)
					return null;
				else if (drop.type == dropType.ITEM) {
					if (ringOfWealth)
						p.message("Your ring of wealth shines brightly!");
					return new Item(drop.id, drop.amount);
				} else if (drop.type == dropType.TABLE) {
					return drop.table.rollItem(ringOfWealth, p);
				}
			}
		}
		return null;
	}

	//removes the empty slots from a table
	private DropTable modifyTable(DropTable table) {
		DropTable modifiedTable = new DropTable();
		for (Drop drop : table.drops) {
			if (drop.type == dropType.NOTHING)
				continue;
			else if (drop.type == dropType.ITEM) {
				modifiedTable.addItemDrop(drop.id, drop.amount, drop.weight);
			} else if (drop.type == dropType.TABLE) {
				modifiedTable.addTableDrop(drop.table, drop.weight);
			}
		}
		return modifiedTable;
	}

	public enum dropType {
		NOTHING,
		ITEM,
		TABLE;
	}

	public class Accessor {
		int id;
		int numerator;
		int denominator;

		public Accessor(int id, int numerator, int denominator) {
			this.id = id;
			this.numerator = numerator;
			this.denominator = denominator;
		}
	}

	public boolean rollAccess(int id, boolean ringOfWealth) {
		int numerator, denominator;
		for (Accessor mob : accessors) {
			if (mob.id == id) {
				numerator = ringOfWealth ? (RING_OF_WEALTH_BOOST_NUMERATOR * mob.denominator) + (RING_OF_WEALTH_BOOST_DENOMINATOR * mob.numerator) : mob.numerator;
				denominator = ringOfWealth ? RING_OF_WEALTH_BOOST_DENOMINATOR * mob.denominator : mob.denominator;
				int hit = DataConversions.random(1, denominator);
				if (hit <= numerator) {
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	private class Drop {
		DropTable table = null;
		dropType type;
		int id;
		int amount;
		int weight;

		private Drop(int itemID, int amount, int weight, dropType type) {
			this.id = itemID;
			this.amount = amount;
			this.weight = weight;
			this.type = type;
		}

		private Drop(DropTable table, int weight) {
			this.type = dropType.TABLE;
			this.weight = weight;
			this.table = table;
		}
	}
}
