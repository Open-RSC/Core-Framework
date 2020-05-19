package com.openrsc.server.content;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

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
		drops.add(new Drop(ItemId.NOTHING.id(), 0, weight, false, dropType.NOTHING));
		this.totalWeight += weight;
	}

	public void addItemDrop(int itemID, int amount, int weight) {
		addItemDrop(itemID, amount, weight, false);
	}

	public void addItemDrop(int itemID, int amount, int weight, boolean noted) {
		drops.add(new Drop(itemID, amount, weight, noted, dropType.ITEM));
		this.totalWeight += weight;
	}

	public void addTableDrop(DropTable table, int weight) {
		drops.add(new Drop(table, weight));
		this.totalWeight += weight;
	}

	public void addAccessor(int id, int numerator, int denominator) {
		accessors.add(new Accessor(id, numerator, denominator));
	}

	public Item rollItem(boolean ringOfWealth, Mob owner) {
		DropTable rollTable = ringOfWealth ? modifyTable(this) : this;

		int hit = DataConversions.random(1, rollTable.totalWeight);
		int sum = 0;
		for (Drop drop : rollTable.drops) {
			sum += drop.weight;
			if (sum >= hit) {
				if (drop.type == dropType.NOTHING)
					return null;
				else if (drop.type == dropType.ITEM) {
					if (ringOfWealth && owner != null && owner instanceof Player)
						((Player) owner).playerServerMessage(MessageType.QUEST, "@ora@Your ring of wealth shines brightly!");
					return new Item(drop.id, drop.amount, drop.noted);
				} else if (drop.type == dropType.TABLE) {
					return drop.table.rollItem(ringOfWealth, owner);
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
				modifiedTable.addItemDrop(drop.id, drop.amount, drop.weight, drop.noted);
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

	public DropTable clone() {
		DropTable clonedDropTable = new DropTable();
		for (Drop drop : drops) {
			if (drop.type == dropType.ITEM) {
				clonedDropTable.addItemDrop(drop.id, drop.amount, drop.weight, drop.noted);
			} else if (drop.type == dropType.TABLE) {
				clonedDropTable.addTableDrop(drop.table, drop.weight);
			}
		}
		return clonedDropTable;
	}

	private class Drop {
		DropTable table = null;
		dropType type;
		int id;
		int amount;
		int weight;
		boolean noted;

		private Drop(int itemID, int amount, int weight, boolean noted, dropType type) {
			this.id = itemID;
			this.amount = amount;
			this.weight = weight;
			this.noted = noted;
			this.type = type;
		}

		private Drop(DropTable table, int weight) {
			this.type = dropType.TABLE;
			this.weight = weight;
			this.table = table;
		}
	}
}
