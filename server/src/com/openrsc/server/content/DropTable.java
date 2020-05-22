package com.openrsc.server.content;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class DropTable {

	private static final Logger LOGGER = LogManager.getLogger();


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

	public int getTotalWeight() {
		return totalWeight;
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

	public Item rollItem(boolean ringOfWealth, Player owner) {
		DropTable rollTable = ringOfWealth ? modifyTable(this) : this;

		int hit = DataConversions.random(0, rollTable.totalWeight);
		int sum = 0;
		for (Drop drop : rollTable.drops) {
			if (drop.weight == 0) continue;
			if (drop.id == ItemId.UNHOLY_SYMBOL_MOULD.id()) {
				if (owner.wantUnholySymbols()) {
					continue;
				}
			}

			sum += drop.weight;
			if (sum >= hit) {
				if (drop.type == dropType.NOTHING || drop.id == ItemId.NOTHING.id()) {
					return null;
				}
				if (owner.getWorld().getServer().getEntityHandler().getItemDef(drop.id).isMembersOnly()
					&& !owner.getWorld().getServer().getConfig().MEMBER_WORLD) {
					continue; // Members only item on a free world
				}
				else if (drop.type == dropType.ITEM) {
					if (ringOfWealth && owner != null && owner instanceof Player) {
						((Player) owner).playerServerMessage(MessageType.QUEST, "@ora@Your ring of wealth shines brightly!");
					}
					if (owner.getWorld().getServer().getConfig().VALUABLE_DROP_MESSAGES) {
						checkValuableDrop(drop.id, drop.amount, drop.weight, rollTable.totalWeight, owner);
					}
					return new Item(drop.id, drop.amount, drop.noted);
				} else if (drop.type == dropType.TABLE) {
					return drop.table.rollItem(ringOfWealth, owner);
				}
			}
		}
		return null;
	}

	public void dropInvariableItems(Player owner, Mob dropping) {
		int total = 0;
		int weightTotal = 0;
		for (Drop drop : drops) {
			total = weightTotal = total + drop.weight;
			if (drop.weight == 0 && drop.id != ItemId.NOTHING.id()) {

				// If Ring of Avarice (custom) is equipped, and the item is a stack,
				// we will award the item with slightly different logic.
				if (handleRingOfAvarice(owner, new Item(drop.id, drop.amount))) continue;

				// Otherwise, create a normal GroundItem.
				GroundItem groundItem = new GroundItem(owner.getWorld(), drop.id, dropping.getX(), dropping.getY(), drop.amount, owner);
				groundItem.setAttribute("npcdrop", true);
				owner.getWorld().registerItem(groundItem);
			}
		}
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

	public static boolean handleRingOfAvarice(final Player player, final Item item) {
		try {
			int slot = -1;
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.RING_OF_AVARICE.id())) {
				ItemDefinition itemDef = player.getWorld().getServer().getEntityHandler().getItemDef(item.getCatalogId());
				if (itemDef != null && itemDef.isStackable()) {
					if (player.getCarriedItems().getInventory().hasInInventory(item.getCatalogId())) {
						player.getCarriedItems().getInventory().add(item);
						return true;
					} else if (player.getConfig().WANT_EQUIPMENT_TAB && (slot = player.getCarriedItems().getEquipment().searchEquipmentForItem(item.getCatalogId())) != -1) {
						Item equipped = player.getCarriedItems().getEquipment().get(slot);
						equipped.changeAmount(player.getWorld().getServer().getDatabase(), item.getAmount());
						return true;
					} else {
						if (player.getCarriedItems().getInventory().getFreeSlots() > 0) {
							player.getCarriedItems().getInventory().add(item);
							return true;
						} else {
							player.message("Your ring of Avarice tried to activate, but your inventory was full.");
							return false;
						}
					}
				}
			}
		} catch (GameDatabaseException ex) {
			LOGGER.error(ex.getMessage());
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
			} else {
				owner.message("@red@Valuable drop: " + temp.getDef(owner.getWorld()).getName() + " (" +
					(temp.getDef(owner.getWorld()).getDefaultPrice()) + " coins)");
			}
		}
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
