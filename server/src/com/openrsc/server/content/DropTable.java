package com.openrsc.server.content;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.database.impl.mysql.queries.logging.LiveFeedLog;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class DropTable {

	private static final Logger LOGGER = LogManager.getLogger();


	ArrayList<Drop> drops;
	ArrayList<Accessor> accessors;
	int totalWeight;
	String description;
	boolean rare;

	private static int RING_OF_WEALTH_BOOST_NUMERATOR = 1;
	private static int RING_OF_WEALTH_BOOST_DENOMINATOR = 128;

	public DropTable() {
		this("", false);
	}

	public DropTable(String description) {
		this(description, false);
	}

	public DropTable(String description, boolean rare) {
		drops = new ArrayList<>();
		accessors = new ArrayList<>();
		totalWeight = 0;
		this.rare = rare;

		this.description = description;
	}

	@Override
	public String toString() {
		return "DropTable{" +
			"drops=" + drops +
			", totalWeight=" + totalWeight +
			", description='" + description + '\'' +
			'}';
	}

	public DropTable clone() {
		return clone("");
	}

	public DropTable clone(String description) {
		DropTable clonedDropTable = new DropTable(description, this.rare);
		for (Drop drop : drops) {
			if (drop.type == dropType.NOTHING) {
				clonedDropTable.addEmptyDrop(drop.weight);
			}
			else if (drop.type == dropType.ITEM) {
				clonedDropTable.addItemDrop(drop.id, drop.amount, drop.weight, drop.noted);
			}
			else if (drop.type == dropType.TABLE) {
				clonedDropTable.addTableDrop(drop.table, drop.weight);
			}
		}
		return clonedDropTable;
	}

	public int getTotalWeight() {
		return totalWeight;
	}

	public String getDescription() {
		return description;
	}

	public void addEmptyDrop(int weight) {
		if (weight < 0) {
			LOGGER.error("The drop table for \"" + this.description + "\" doesn't add up as expected!!!");
			System.exit(0);
		}
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

	public void removeItemDrop(Item item) {
		Iterator<Drop> iter = drops.iterator();
		while (iter.hasNext()) {
			Drop drop = iter.next();
			if (drop.id == item.getCatalogId() && drop.amount == item.getAmount()) {
				iter.remove();
			}
		}
	}

	public ArrayList<Item> rollItem(boolean ringOfWealth, Player owner) {
		DropTable rollTable = this;
		int hit = DataConversions.random(0, rollTable.totalWeight - 1);
		int sum = 0;
		ArrayList<Item> items = new ArrayList<>();
		for (Drop drop : rollTable.drops) {
			sum += drop.weight;
			if (sum > hit) {
				// If it's not a reroll, and the user is wearing a ring of wealth,
				// We let them roll once more for a second chance at goodies.
				if (drop.type == dropType.NOTHING) {
					if (ringOfWealth) {
						items.addAll(rollItem(false, owner));
					}
					break;
				}
				else if (drop.type == dropType.ITEM) {
					if (drop.weight == 0) continue;
					if (owner.getWorld().getServer().getEntityHandler().getItemDef(drop.id).isMembersOnly()
						&& !owner.getWorld().getServer().getConfig().MEMBER_WORLD) {
						continue; // Members only item on a free world
					}
					if (drop.id == ItemId.UNHOLY_SYMBOL_MOULD.id()) {
						if (owner.wantUnholySymbols()) {
							continue;
						}
					}
					if (owner.getWorld().getServer().getConfig().VALUABLE_DROP_MESSAGES) {
						checkValuableDrop(drop.id, drop.amount, drop.weight, rollTable.totalWeight, owner);
					}
					items.add(new Item(drop.id, drop.amount, drop.noted));
					break;
				} else if (drop.type == dropType.TABLE) {
					DropTable newTable = drop.table.clone();

					ArrayList<Item> invariableItemsToAdd = newTable.invariableItems(owner);
					items.addAll(invariableItemsToAdd);

					// We need to check if no "always drop" items were added.
					// If there weren't, and the totalWeight is 0, that means
					// that the new drop table ONLY contains additional drop tables.
					// This is probably only a special case for Chaos Druids.
					boolean onlyTables = invariableItemsToAdd.isEmpty() && newTable.getTotalWeight() == 0;

					if (newTable.getTotalWeight() > 0) {
						ArrayList<Item> itemsToAdd = newTable.rollItem(false, owner);
						if (itemsToAdd.size() > 0 && ringOfWealth && drop.table.rare) {
							owner.playerServerMessage(MessageType.QUEST, "@ora@Your ring of wealth shines brightly!");
							owner.playSound("foundgem");
						}
						items.addAll(itemsToAdd);
					} else if (onlyTables) {
						for (Drop table : newTable.drops) {
							if (table.type == dropType.TABLE)
							{
								items.addAll(table.table.rollItem(ringOfWealth, owner));
							}
						}
					}

					break;
				}
			}
		}
		return items;
	}

	public ArrayList<Item> invariableItems(Player owner) {
		int total = 0;
		ArrayList<Item> items = new ArrayList<>();
		Iterator<Drop> it = drops.iterator();
		while (it.hasNext()) {
			Drop drop = it.next();
			total = total + drop.weight;
			if (drop.weight == 0 && drop.id != ItemId.NOTHING.id()) {

				Item item = new Item(drop.id, drop.amount, drop.noted);

				// Remove from the table once it's dropped.
				it.remove();

				// If Ring of Avarice (custom) is equipped, and the item is a stack,
				// we will award the item with slightly different logic.
				if (handleRingOfAvarice(owner, item)) continue;

				items.add(item);
			}
		}
		return items;
	}

	public static boolean handleRingOfAvarice(final Player player, final Item item) {
		int slot = -1;
		if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.RING_OF_AVARICE.id())) {
			ItemDefinition itemDef = player.getWorld().getServer().getEntityHandler().getItemDef(item.getCatalogId());
			if (itemDef != null && itemDef.isStackable()) {
				if (player.getCarriedItems().getInventory().hasInInventory(item.getCatalogId())) {
					player.getCarriedItems().getInventory().add(item);
					return true;
				} else if (player.getConfig().WANT_EQUIPMENT_TAB && (slot = player.getCarriedItems().getEquipment().searchEquipmentForItem(item.getCatalogId())) != -1) {
					Item equipped = player.getCarriedItems().getEquipment().get(slot);
					equipped.changeAmount(item.getAmount());
					return true;
				} else {
					if (player.getCarriedItems().getInventory().getFreeSlots() > 0) {
						player.getCarriedItems().getInventory().add(item);
						return true;
					} else {
						player.message("Your ring of Avarice tried to activate, but your inventory was full.");
					}
				}
			}
		}
		return false;
	}

	private void checkValuableDrop(int dropID, int amount, int weight, int weightTotal, Player owner) {
		// Check if we have a "valuable drop" (configurable)
		Item temp = new Item(dropID);
		double currentRatio = (double) weight / (double) weightTotal;
		if (dropID != com.openrsc.server.constants.ItemId.NOTHING.id() &&
			amount > 0 &&
			(
				currentRatio > owner.getWorld().getServer().getConfig().VALUABLE_DROP_RATIO ||
					(
						owner.getWorld().getServer().getConfig().VALUABLE_DROP_EXTRAS &&
							owner.getWorld().getServer().getConfig().valuableDrops.contains(temp.getDef(owner.getWorld()).getName())
					)
			)
		) {
			if (amount > 1) {
				owner.message("@red@Valuable drop: " + amount + " x " + temp.getDef(owner.getWorld()).getName() + " (" +
					(temp.getDef(owner.getWorld()).getDefaultPrice() * amount) + " coins)");
				owner.getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(owner, "has obtained " + amount
					+ " x " + temp.getDef(owner.getWorld()).getName() + "!"));
			} else {
				owner.message("@red@Valuable drop: " + temp.getDef(owner.getWorld()).getName() + " (" +
					(temp.getDef(owner.getWorld()).getDefaultPrice()) + " coins)");
				owner.getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(owner, "has obtained a "
					+ temp.getDef(owner.getWorld()).getName() + "!"));
			}
		}
	}

	public enum dropType {
		NOTHING,
		ITEM,
		TABLE;
	}

	public static class Accessor {
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
				return hit <= numerator;
			}
		}
		return false;
	}

	private static class Drop {
		DropTable table = null;
		dropType type;
		int id = -1;
		int amount = 0;
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

		@Override
		public String toString() {
			return "Drop{" +
				"table=" + table +
				", type=" + type +
				", id=" + id +
				", amount=" + amount +
				", weight=" + weight +
				", noted=" + noted +
				'}';
		}
	}
}
