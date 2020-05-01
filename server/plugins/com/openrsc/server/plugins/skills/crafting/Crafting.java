package com.openrsc.server.plugins.skills.crafting;

import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.GenericLog;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ItemCraftingDef;
import com.openrsc.server.external.ItemGemDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.CarriedItems;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static com.openrsc.server.plugins.Functions.*;

public class Crafting implements UseInvTrigger,
	UseLocTrigger {

	/**
	 * World instance
	 */
	private int[] gemsThatFail = new int[]{
		ItemId.UNCUT_RED_TOPAZ.id(),
		ItemId.UNCUT_JADE.id(),
		ItemId.UNCUT_OPAL.id(),
	};
	private int[] itemsFurnance = new int[]{
		ItemId.SILVER_BAR.id(),
		ItemId.GOLD_BAR.id(),
		ItemId.SODA_ASH.id(),
		ItemId.SAND.id(),
		ItemId.GOLD_BAR_FAMILYCREST.id(),
	};
	private int[] itemsOven = new int[]{
		ItemId.UNFIRED_POT.id(),
		ItemId.UNFIRED_PIE_DISH.id(),
		ItemId.UNFIRED_BOWL.id()
	};

	final int[] gold_moulds = {
		ItemId.RING_MOULD.id(),
		ItemId.NECKLACE_MOULD.id(),
		ItemId.AMULET_MOULD.id(),
	};

	final int[] silver_moulds = {
		ItemId.HOLY_SYMBOL_MOULD.id(),
		ItemId.UNHOLY_SYMBOL_MOULD.id(),
	};

	final int[] gems = {
		ItemId.NOTHING.id(),
		ItemId.SAPPHIRE.id(),
		ItemId.EMERALD.id(),
		ItemId.RUBY.id(),
		ItemId.DIAMOND.id(),
		ItemId.DRAGONSTONE.id(),
		ItemId.OPAL.id(),
	};

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		int item1ID = item1.getCatalogId();
		int item2ID = item2.getCatalogId();
		CarriedItems carriedItems = player.getCarriedItems();
		Inventory inventory = carriedItems.getInventory();
		if (item1ID == ItemId.CHISEL.id()) {
			doCutGem(player, item1, item2);
		} else if (item2ID == ItemId.CHISEL.id()) {
			doCutGem(player, item2, item1);
		} else if (item1ID == ItemId.GLASSBLOWING_PIPE.id()) {
			doGlassBlowing(player, item1, item2);
		} else if (item2ID == ItemId.GLASSBLOWING_PIPE.id()) {
			doGlassBlowing(player, item2, item1);
		} else if (item1ID == ItemId.NEEDLE.id()) {
			if (item2ID == ItemId.TEDDY_HEAD.id() || item2ID == ItemId.TEDDY_BOTTOM.id()) {
				if (inventory.hasInInventory(ItemId.TEDDY_HEAD.id())
					&& inventory.hasInInventory(ItemId.TEDDY_BOTTOM.id())
					&& inventory.hasInInventory(ItemId.THREAD.id())) {
					if (getCurrentLevel(player, Skills.CRAFTING) >= 15) {
						carriedItems.remove(new Item(ItemId.TEDDY_HEAD.id()));
						carriedItems.remove(new Item(ItemId.TEDDY_BOTTOM.id()));
						carriedItems.remove(new Item(ItemId.THREAD.id()));
						carriedItems.getInventory().add(new Item(ItemId.TEDDY.id()));
						player.message("You stitch together the teddy parts");
					} else
						player.message("You need level 15 crafting to fix the teddy");
				} else
					player.message("You need the two teddy halves and some thread");
			} else {
				makeLeather(player, item1, item2);
			}
		} else if (item2ID == ItemId.NEEDLE.id()) {
			if (item1ID == ItemId.TEDDY_HEAD.id() || item1ID == ItemId.TEDDY_BOTTOM.id()) {
				if (inventory.hasInInventory(ItemId.TEDDY_HEAD.id())
					&& inventory.hasInInventory(ItemId.TEDDY_BOTTOM.id())
					&& inventory.hasInInventory(ItemId.THREAD.id())) {
					if (getCurrentLevel(player, Skills.CRAFTING) >= 15) {
						carriedItems.remove(new Item(ItemId.TEDDY_HEAD.id()));
						carriedItems.remove(new Item(ItemId.TEDDY_BOTTOM.id()));
						carriedItems.remove(new Item(ItemId.THREAD.id()));
						carriedItems.getInventory().add(new Item(ItemId.TEDDY.id(), 1));
						player.message("You stitch together the teddy parts");
					} else
						player.message("You need level 15 crafting to fix the teddy");
				} else
					player.message("You need the two teddy halves and some thread");
			} else {
				makeLeather(player, item2, item1);
			}
		} else if (item1ID == ItemId.BALL_OF_WOOL.id()) {
			useWool(player, item1, item2);
		} else if (item2ID == ItemId.BALL_OF_WOOL.id()) {
			useWool(player, item2, item1);
		} else if ((item1ID == ItemId.BUCKET_OF_WATER.id() || item1ID == ItemId.JUG_OF_WATER.id() || item1ID == ItemId.BOWL_OF_WATER.id()) && useWater(player, item1, item2)) {
			return;
		} else if ((item2ID == ItemId.BUCKET_OF_WATER.id() || item2ID == ItemId.JUG_OF_WATER.id() || item2ID == ItemId.BOWL_OF_WATER.id()) && useWater(player, item2, item1)) {
			return;
		} else if (item1ID == ItemId.MOLTEN_GLASS.id() && item2ID == ItemId.LENS_MOULD.id() || item1ID == ItemId.LENS_MOULD.id() && item2ID == ItemId.MOLTEN_GLASS.id()) {
			if (getCurrentLevel(player, Skills.CRAFTING) < 10) {
				player.message("You need a crafting level of 10 to make the lens");
				return;
			}
			if (carriedItems.remove(new Item(ItemId.MOLTEN_GLASS.id())) > -1) {
				player.message("You pour the molten glass into the mould");
				player.message("And clasp it together");
				player.message("It produces a small convex glass disc");
				inventory.add(new Item(ItemId.LENS.id()));
			}
		} else {
			player.message("Nothing interesting happens");
		}
	}

	@Override
	public void onUseLoc(final Player player, GameObject obj, final Item item) {

		if (!craftingChecks(obj, item, player)) return;

		beginCrafting(item, player);
	}

	private boolean craftingChecks(final GameObject obj, final Item item, final Player player) {
		// allowed item on crafting game objects
		if (!craftingTypeChecks(obj, item, player)) return false;

		if (item.getItemStatus().getNoted()) return false;

		if (obj.getLocation().equals(Point.location(399, 840))) {
			// furnace in shilo village
			if ((player.getLocation().getY() == 841 && !player.withinRange(obj, 2)) && !player.withinRange90Deg(obj, 2)) {
				return false;
			}
		} else {
			// some furnaces the player is 2 spaces away
			if (!player.withinRange(obj, 1) && !player.withinRange90Deg(obj, 2)) {
				return false;
			}
		}

		if (item.getCatalogId() == ItemId.SODA_ASH.id() || item.getCatalogId() == ItemId.SAND.id()) { // Soda Ash or Sand (Glass)
			if (player.getCarriedItems().getInventory().countId(ItemId.SODA_ASH.id()) < 1) {
				player.playerServerMessage(MessageType.QUEST, "You need some soda ash to make glass");
				return false;
			} else if (player.getCarriedItems().getInventory().countId(ItemId.SAND.id()) < 1) {
				player.playerServerMessage(MessageType.QUEST, "You need some sand to make glass");
				return false;
			}
		}

		return true;
	}

	private boolean craftingTypeChecks(final GameObject obj, final Item item, final Player player) {
		return ((obj.getID() == 118 || obj.getID() == 813) && DataConversions.inArray(itemsFurnance, item.getCatalogId()))
				|| (obj.getID() == 178 && DataConversions.inArray(itemsOven, item.getCatalogId()))
				|| (obj.getID() == 179 && item.getCatalogId() == ItemId.SOFT_CLAY.id());
	}

	private void beginCrafting(final Item item, final Player player) {
		if (item.getCatalogId() == ItemId.SODA_ASH.id() || item.getCatalogId() == ItemId.SAND.id()) {
			doGlassMaking(item, player);
			return;
		} else if (DataConversions.inArray(itemsOven, item.getCatalogId())) {
			doPotteryFiring(item, player);
			return;
		}

		player.message("What would you like to make?");

		if (item.getCatalogId() == ItemId.GOLD_BAR.id() || item.getCatalogId() == ItemId.GOLD_BAR_FAMILYCREST.id()) {
			doGoldJewelry(item, player);
		} else if (item.getCatalogId() == ItemId.SILVER_BAR.id()) {
			doSilverJewelry(item, player);
		} else if (item.getCatalogId() == ItemId.SOFT_CLAY.id()) {
			doPotteryMolding(item, player);
		}
	}

	private void doGoldJewelry(final Item item, final Player player) {
		AtomicReference<String> reply = new AtomicReference<String>();

		// select type
		String[] options = new String[]{
			"Ring",
			"Necklace",
			"Amulet"
		};
		int type = multi(player, options);
		if (type < 0 || type > 2) {
			return;
		}
		reply.set(options[type]);

		// select gem
		options = new String[]{
			"Gold",
			"Sapphire",
			"Emerald",
			"Ruby",
			"Diamond"
		};
		if (player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
				options = new String[]{
					"Gold",
					"Sapphire",
					"Emerald",
					"Ruby",
					"Diamond",
					"Dragonstone",
					"Opal"
				};
			} else {
				options = new String[]{
					"Gold",
					"Sapphire",
					"Emerald",
					"Ruby",
					"Diamond",
					"Dragonstone"
				};
			}
		}
		if (player.getCarriedItems().getInventory().countId(gold_moulds[type]) < 1) {
			player.message("You need a " + player.getWorld().getServer().getEntityHandler().getItemDef(gold_moulds[type]).getName() + " to make a " + reply.get());
			return;
		}
		player.message("What type of " + reply.get() + " would you like to make?");

		int gem = multi(player, options);

		if (gem < 0 || gem > (player.getWorld().getServer().getConfig().MEMBER_WORLD ? 5 + (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB ? 1 : 0) : 4)) {
			return;
		}
		reply.set(options[gem]);

		ItemCraftingDef def = player.getWorld().getServer().getEntityHandler().getCraftingDef((gem * 3) + type);
		if (def == null) {
			// No definition found
			player.message("Nothing interesting happens");
			return;
		}

		if (def.itemID == ItemId.NOTHING.id()) {
			player.message("You have no reason to make that item.");
			return;
		}

		int retrytimes = player.getCarriedItems().getInventory().countId(item.getCatalogId());

		//Perfect gold bars shouldn't be batched
		if (item.getCatalogId() == ItemId.GOLD_BAR_FAMILYCREST.id()) {
			retrytimes = 1;
		} else {
			if (gem != 0) {
				retrytimes = Math.min(player.getCarriedItems().getInventory().countId(gems[gem]), retrytimes);
			}
			if (retrytimes <= 0) {
				if (gem != -1 && player.getCarriedItems().getInventory().countId(gems[gem]) < 1) {
					player.message("You don't have a " + reply.get() + ".");
				}
				return;
			}
		}

		batchGoldJewelry(player, item, def, gem, gems, type, reply, retrytimes);
	}

	private void batchGoldJewelry(Player player, Item item, ItemCraftingDef def, int gem, int[] gems, int type, AtomicReference<String> reply, int repeat) {
		if (player.getSkills().getLevel(Skills.CRAFTING) < def.getReqLevel()) {
			player.playerServerMessage(MessageType.QUEST, "You need a crafting skill of level " + def.getReqLevel() + " to make this");
			return;
		}
		if (gem != 0 && player.getCarriedItems().getInventory().countId(gems[gem]) < 1) {
			player.message("You don't have a " + reply.get() + ".");
			return;
		}
		if (player.getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (player.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to craft");
				return;
			}
		}
		if (player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false)) <= 0
			|| (gem > 0 && player.getCarriedItems().getInventory().countId(gems[gem], Optional.of(false)) <= 0)) {
			player.message("You don't have a " + reply.get() + ".");
			return;
		}

		// Remove items
		thinkbubble(player, item);
		player.getCarriedItems().remove(item);
		if (gem > 0) {
			player.getCarriedItems().remove(new Item(gems[gem]));
		}
		delay(player.getWorld().getServer().getConfig().GAME_TICK * 2);

		Item result;
		if (item.getCatalogId() == ItemId.GOLD_BAR_FAMILYCREST.id() && gem == 3 && type == 0) {
			result = new Item(ItemId.RUBY_RING_FAMILYCREST.id(), 1);
		} else if (item.getCatalogId() == ItemId.GOLD_BAR_FAMILYCREST.id() && gem == 3 && type == 1) {
			result = new Item(ItemId.RUBY_NECKLACE_FAMILYCREST.id(), 1);
		} else {
			result = new Item(def.getItemID(), 1);
		}
		player.playerServerMessage(MessageType.QUEST, "You make a " + result.getDef(player.getWorld()).getName());
		player.getCarriedItems().getInventory().add(result);
		player.incExp(Skills.CRAFTING, def.getExp(), true);

		// Repeat
		if (player.hasMoved()) return;
		repeat--;
		if(repeat > 0) {
			item = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId())
			);
			delay(player.getWorld().getServer().getConfig().GAME_TICK);
			batchGoldJewelry(player, item, def, gem, gems, type, reply, repeat);
		}
	}

	private void doSilverJewelry(final Item item, final Player player) {
		AtomicReference<String> reply = new AtomicReference<String>();

		// select type
		String[] options = new String[]{
			"Holy Symbol of Saradomin",
			"Unholy symbol of Zamorak"
		};
		int type = multi(player, options);
		if (type < 0 || type > 1) {
			return;
		}
		reply.set(options[type]);

		final int[] results = {
			ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(),
			ItemId.UNSTRUNG_UNHOLY_SYMBOL_OF_ZAMORAK.id()
		};
		if (player.getCarriedItems().getInventory().countId(silver_moulds[type]) <= 0) {
			player.message("You need a " + player.getWorld().getServer().getEntityHandler().getItemDef(silver_moulds[type]).getName() + " to make a " + reply.get() + "!");
			return;
		}

		int repeat = 1;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
		}
		batchSilverJewelry(player, item, results, type, reply, repeat);
	}

	private void batchSilverJewelry(Player player, Item item, int[] results, int type, AtomicReference<String> reply, int repeat) {
		if (player.getSkills().getLevel(Skills.CRAFTING) < 16) {
			player.playerServerMessage(MessageType.QUEST, "You need a crafting skill of level 16 to make this");
			return;
		}
		if (player.getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (player.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to craft");
				return;
			}
		}

		if (player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false)) <= 0) {
			player.message("You need a " + player.getWorld().getServer().getEntityHandler().getItemDef(silver_moulds[type]).getName() + " to make a " + reply.get() + "!");
			return;
		}

		thinkbubble(player, item);
		player.getCarriedItems().remove(item);
		delay(player.getWorld().getServer().getConfig().GAME_TICK * 2);

		Item result = new Item(results[type]);
		player.playerServerMessage(MessageType.QUEST, "You make a " + result.getDef(player.getWorld()).getName());
		player.getCarriedItems().getInventory().add(result);
		player.incExp(Skills.CRAFTING, 200, true);

		// Repeat
		if (player.hasMoved()) return;
		repeat--;
		if (repeat > 0) {
			item = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId())
			);
			delay(player.getWorld().getServer().getConfig().GAME_TICK);
			batchSilverJewelry(player, item, results, type, reply, repeat);
		}
	}

	private void doPotteryMolding(final Item item, final Player player) {
		String[] options = new String[]{"Pie dish", "Pot", "Bowl"};
		int type = multi(player, options);
		if (type < 0 || type > 2) {
			return;
		}

		int reqLvl, exp;
		Item result;
		AtomicReference<String> msg = new AtomicReference<String>();
		switch (type) {
			case 1:
				result = new Item(ItemId.UNFIRED_POT.id(), 1);
				reqLvl = 1;
				exp = 25;
				// should not use this, as pot is made at level 1
				msg.set("a pot");
				break;
			case 0:
				result = new Item(ItemId.UNFIRED_PIE_DISH.id(), 1);
				reqLvl = 4;
				exp = 60;
				msg.set("pie dishes");
				break;
			case 2:
				result = new Item(ItemId.UNFIRED_BOWL.id(), 1);
				reqLvl = 7;
				exp = 40;
				msg.set("a bowl");
				break;
			default:
				player.message("Nothing interesting happens");
				return;
		}

		int repeat = 1;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
		}

		batchPotteryMoulding(player, item, reqLvl, result, msg, exp, repeat);
	}

	private void batchPotteryMoulding(Player player, Item item, int reqLvl, Item result, AtomicReference<String> msg, int exp, int repeat) {

		if (player.getSkills().getLevel(Skills.CRAFTING) < reqLvl) {
			player.playerServerMessage(MessageType.QUEST, "You need to have a crafting of level " + reqLvl + " or higher to make " + msg.get());
			return;
		}
		if (player.getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (player.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to craft");
				return;
			}
		}

		if (player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false)) <= 0) {
			return;
		}

		delay(player.getWorld().getServer().getConfig().GAME_TICK);
		player.getCarriedItems().remove(item);
		thinkbubble(player, item);
		player.playerServerMessage(MessageType.QUEST, "you make the clay into a " + potteryItemName(result.getDef(player.getWorld()).getName()));
		player.getCarriedItems().getInventory().add(result);
		player.incExp(Skills.CRAFTING, exp, true);

		// Repeat
		if (player.hasMoved()) return;
		repeat--;
		if (repeat > 0) {
			item = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
			);
			delay(player.getWorld().getServer().getConfig().GAME_TICK);
			batchPotteryMoulding(player, item, reqLvl, result, msg, exp, repeat);
		}
	}

	private void doPotteryFiring(final Item item, final Player player) {
		int reqLvl, xp;
		Item result;
		AtomicReference<String> msg = new AtomicReference<String>();
		switch (ItemId.getById(item.getCatalogId())) {
			case UNFIRED_POT:
				result = new Item(ItemId.POT.id(), 1);
				reqLvl = 1;
				xp = 25;
				// should not use this, as pot is made at level 1
				msg.set("a pot");
				break;
			case UNFIRED_PIE_DISH:
				result = new Item(ItemId.PIE_DISH.id(), 1);
				reqLvl = 4;
				xp = 40;
				msg.set("pie dishes");
				break;
			case UNFIRED_BOWL:
				result = new Item(ItemId.BOWL.id(), 1);
				reqLvl = 7;
				xp = 60;
				msg.set("a bowl");
				break;
			default:
				player.message("Nothing interesting happens");
				return;
		}

		final int exp = xp;

		int repeat = 1;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
		}
		batchPotteryFiring(player, item, reqLvl, result, msg, exp, repeat);
	}

	private void batchPotteryFiring(Player player, Item item, int reqLvl, Item result, AtomicReference<String> msg, int exp, int repeat) {
		if (player.getSkills().getLevel(Skills.CRAFTING) < reqLvl) {
			player.playerServerMessage(MessageType.QUEST, "You need to have a crafting of level " + reqLvl + " or higher to make " + msg.get());
			return;
		}
		if (player.getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (player.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to craft");
				return;
			}
		}

		if (player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false)) <= 0) {
			return;
		}

		thinkbubble(player, item);
		String potteryItem = potteryItemName(item.getDef(player.getWorld()).getName());
		player.playerServerMessage(MessageType.QUEST, "You put the " + potteryItem + " in the oven");
		player.getCarriedItems().remove(item);
		delay(player.getWorld().getServer().getConfig().GAME_TICK * 3);

		if (Formulae.crackPot(reqLvl, player.getSkills().getLevel(Skills.CRAFTING))) {
			player.playerServerMessage(MessageType.QUEST, "The " // TODO: Check if is authentic message
				+ potteryItem + " cracks in the oven, you throw it away.");
		} else {
			player.playerServerMessage(MessageType.QUEST, "the "
				+ potteryItem + " hardens in the oven");

			delay(player.getWorld().getServer().getConfig().GAME_TICK * 3);

			player.playerServerMessage(MessageType.QUEST, "You remove a "
				+ result.getDef(player.getWorld()).getName().toLowerCase()
				+ " from the oven");
			player.getCarriedItems().getInventory().add(result);
			player.incExp(Skills.CRAFTING, exp, true);
		}

		// Repeat
		if (player.hasMoved()) return;
		repeat--;
		if (repeat > 0) {
			item = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
			);
			delay(player.getWorld().getServer().getConfig().GAME_TICK);
			batchPotteryFiring(player, item, reqLvl, result, msg, exp, repeat);
		}
	}

	private void doGlassMaking(final Item item, final Player player) {
		int otherItem = item.getCatalogId() == ItemId.SAND.id() ? ItemId.SODA_ASH.id() : ItemId.SAND.id();
		int repeat = 1;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId());
			repeat = Math.min(player.getCarriedItems().getInventory().countId(otherItem), repeat);
		}

		batchGlassMaking(player, item, otherItem, repeat);
	}

	private void batchGlassMaking(Player player, Item item, int otherItem, int repeat) {
		thinkbubble(player, item);
		player.playerServerMessage(MessageType.QUEST, "you heat the sand and soda ash in the furnace to make glass");
		Inventory inventory = player.getCarriedItems().getInventory();
		if (inventory.countId(otherItem) < 1 ||
			inventory.countId(item.getCatalogId()) < 1) {
			return;
		}
		if (player.getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (player.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to craft");
				return;
			}
		}
		player.getCarriedItems().remove(new Item(otherItem));
		player.getCarriedItems().remove(item);
		delay(player.getWorld().getServer().getConfig().GAME_TICK);
		inventory.add(new Item(ItemId.MOLTEN_GLASS.id(), 1));
		inventory.add(new Item(ItemId.BUCKET.id(), 1));
		player.incExp(Skills.CRAFTING, 80, true);

		// Repeat
		if (player.hasMoved()) return;
		repeat--;
		if (repeat > 0) {
			item = inventory.get(
				inventory.getLastIndexById(item.getCatalogId(), Optional.of(false))
			);
			delay(player.getWorld().getServer().getConfig().GAME_TICK);
			batchGlassMaking(player, item, otherItem, repeat);
		}
	}

	private void doGlassBlowing(Player player, final Item pipe, final Item glass) {
		if (glass.getCatalogId() != ItemId.MOLTEN_GLASS.id()) {
			return;
		}
		player.message("what would you like to make?");

		String[] options = new String[]{
			"Vial",
			"orb",
			"Beer glass"
		};

		int type = multi(player, options);
		if (type < 0 || type > 2) {
			return;
		}

		Item result;
		int reqLvl, exp;
		String resultGen;
		switch (type) {
			case 0:
				result = new Item(ItemId.EMPTY_VIAL.id(), 1);
				reqLvl = 33;
				exp = 140;
				resultGen = "vials";
				break;
			case 1:
				result = new Item(ItemId.UNPOWERED_ORB.id(), 1);
				reqLvl = 46;
				exp = 210;
				resultGen = "orbs";
				break;
			case 2:
				result = new Item(ItemId.BEER_GLASS.id(), 1);
				reqLvl = 1;
				exp = 70;
				// should not use this, as beer glass is made at level 1
				resultGen = "beer glasses";
				break;
			default:
				return;
		}

		int repeat = 1;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(glass.getCatalogId(), Optional.of(false));
		}
		batchGlassBlowing(player, glass, result, reqLvl, exp, resultGen, repeat);
	}

	private void batchGlassBlowing(Player player, Item glass, Item result, int reqLvl, int exp, String resultGen, int repeat) {
		Inventory inventory = player.getCarriedItems().getInventory();
		ServerConfiguration config = player.getWorld().getServer().getConfig();
		if (player.getSkills().getLevel(Skills.CRAFTING) < reqLvl) {
			player.message(
				"You need a crafting level of " + reqLvl + " to make " + resultGen);
			return;
		}
		if (config.WANT_FATIGUE) {
			if (config.STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to craft");
				return;
			}
		}

		if (player.getCarriedItems().getInventory().countId(glass.getCatalogId(), Optional.of(false)) <= 0) {
			return;
		}

		player.getCarriedItems().remove(glass);
		delay(player.getWorld().getServer().getConfig().GAME_TICK);
		String message = "You make a " + result.getDef(player.getWorld()).getName();

		// Special handling for vials
		if (result.getCatalogId() == ItemId.EMPTY_VIAL.id()) {
			if (config.WANT_CUSTOM_QUESTS) {
				int amnt = 0;
				double breakChance = 91.66667 - getCurrentLevel(player, Skills.CRAFTING) / 1.32;
				for (int loop = 0; loop < 6; ++loop) {
					double hit = new Random().nextDouble() * 99;
					if (hit > breakChance) {
						amnt++;
					}
				}
				message = "You make " + amnt + " vial" + (amnt != 1 ? "s" : "");
				result.getItemStatus().setAmount(amnt);
				if (player.getLocation().inBounds(418, 559, 421, 563)) {
					try {
						result.setNoted(player.getWorld().getServer().getDatabase(), true);
					} catch (GameDatabaseException e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}

		player.playerServerMessage(MessageType.QUEST, message);

		if (!result.getDef(player.getWorld()).isStackable() && result.getAmount() > 1) {
			int owedGlassware = result.getAmount() - 1;
			int space = inventory.getFreeSlots();
			while (owedGlassware > 0) {
				if (space > 0) {
					inventory.add(result);
					--space;
				} else {
					player.getWorld().registerItem(
						new GroundItem(player.getWorld(), result.getCatalogId(), player.getX(), player.getY(), 1, player),
						player.getWorld().getServer().getConfig().GAME_TICK * 150); // TODO: Delay on this?
					player.getWorld().getServer().getGameLogger().addQuery(new GenericLog(player.getWorld(), player.getUsername() + " dropped(inventory full) "
						+ result.getCatalogId() + " x" + "1" + " at " + player.getLocation().toString()));
				}
				--owedGlassware;
			}
		}

		inventory.add(result);
		player.incExp(Skills.CRAFTING, exp, true);

		// Repeat
		if (player.hasMoved()) return;
		repeat--;
		if (repeat > 0) {
			delay(player.getWorld().getServer().getConfig().GAME_TICK * 2);
			glass = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(glass.getCatalogId(), Optional.of(false))
			);
			batchGlassBlowing(player, glass, result, reqLvl, exp, resultGen, repeat);
		}
	}

	private void doCutGem(Player player, final Item chisel, final Item gem) {
		final ItemGemDef gemDef = player.getWorld().getServer().getEntityHandler().getItemGemDef(gem.getCatalogId());
		if (gemDef == null) {
			if (gem.getCatalogId() == ItemId.KING_BLACK_DRAGON_SCALE.id()) {
				if (getCurrentLevel(player, Skills.CRAFTING) < 90) {
					player.message("You need 90 crafting to split the scales");
					return;
				}
				if (player.getCarriedItems().remove(new Item(ItemId.KING_BLACK_DRAGON_SCALE.id(),1)) > -1) {
					player.message("You chip the massive scale into 5 pieces");
					give(player, ItemId.CHIPPED_DRAGON_SCALE.id(), 5);
					player.incExp(Skills.CRAFTING,player.getWorld().getServer().getConfig().GAME_TICK * 2,true);
				}
			} else
				player.message("Nothing interesting happens");
			return;
		}

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Cut Gem", player.getCarriedItems().getInventory().countId(gem.getCatalogId(), Optional.of(false)), false) {
			@Override
			public void action() {
				Player owner = getOwner();
				if (owner.getSkills().getLevel(Skills.CRAFTING) < gemDef.getReqLevel()) {
					boolean pluralize = gemDef.getGemID() <= ItemId.UNCUT_DRAGONSTONE.id();
					owner.playerServerMessage(MessageType.QUEST,
						"you need a crafting level of " + gemDef.getReqLevel()
							+ " to cut " + (gem.getDef(getWorld()).getName().contains("ruby") ? "rubies" : gem.getDef(getWorld()).getName().replaceFirst("(?i)uncut ", "") + (pluralize ? "s" : "")));
					interruptBatch();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& owner.getFatigue() >= owner.MAX_FATIGUE) {
						owner.message("You are too tired to craft");
						interruptBatch();
						return;
					}
				}
				Item item = owner.getCarriedItems().getInventory().get(
					owner.getCarriedItems().getInventory().getLastIndexById(gem.getCatalogId(), Optional.of(false)));
				if (item.getItemStatus().getNoted()) return;
				if (owner.getCarriedItems().remove(item) > -1) {
					Item cutGem = new Item(gemDef.getGemID(), 1);
					// Jade, Opal and red topaz fail handler - 25% chance to fail

					if (DataConversions.inArray(gemsThatFail, gem.getCatalogId()) &&
						Formulae.smashGem(gem.getCatalogId(), gemDef.getReqLevel(), owner.getSkills().getLevel(Skills.CRAFTING))) {
						owner.message("You miss hit the chisel and smash the " + cutGem.getDef(getWorld()).getName() + " to pieces!");
						owner.getCarriedItems().getInventory().add(new Item(ItemId.CRUSHED_GEMSTONE.id()));

						if (gem.getCatalogId() == ItemId.UNCUT_RED_TOPAZ.id()) {
							owner.incExp(Skills.CRAFTING, 25, true);
						} else if (gem.getCatalogId() == ItemId.UNCUT_JADE.id()) {
							owner.incExp(Skills.CRAFTING, 20, true);
						} else {
							owner.incExp(Skills.CRAFTING, 15, true);
						}
					} else {
						owner.getCarriedItems().getInventory().add(cutGem, true);
						owner.message("You cut the " + cutGem.getDef(getWorld()).getName().toLowerCase());
						owner.playSound("chisel");
						owner.incExp(Skills.CRAFTING, gemDef.getExp(), true);
					}
				}
				else {
					interruptBatch();
				}
			}
		});
	}

	private void makeLeather(Player player, final Item needle, final Item leather) {
		if (leather.getCatalogId() != ItemId.LEATHER.id()) {
			player.message("Nothing interesting happens");
			return;
		}

		if (player.getCarriedItems().getInventory().countId(ItemId.THREAD.id()) < 1) {
			player.message("You need some thread to make anything out of leather");
			return;
		}

		final boolean customLeather = player.getWorld().getServer().getConfig().WANT_CUSTOM_LEATHER;

		String[] options = customLeather ?
			new String[]{
				"Armour",
				"Gloves",
				"Boots",
				"More...",
				"Cancel"
			}
			:
			new String[]{
				"Armour",
				"Gloves",
				"Boots",
				"Cancel"
			};

		int type = multi(player, options);
		if (type < 0 || type > (customLeather ? 4 : 3)) {
			return;
		}

		Item result;
		int reqLvl, exp;
		switch (type) {
			case 0:
				result = new Item(ItemId.LEATHER_ARMOUR.id(), 1);
				reqLvl = 14;
				exp = 100;
				break;
			case 1:
				result = new Item(ItemId.LEATHER_GLOVES.id(), 1);
				reqLvl = 1;
				exp = 55;
				break;
			case 2:
				result = new Item(ItemId.BOOTS.id(), 1);
				reqLvl = 7;
				exp = 65;
				break;
			case 3:
				if(!customLeather) {
					return;
				}

				String[] customMenu = new String[]{
					"Chaps",
					"Top",
					"Skirt",
					"Cancel"
				};

				int customType = multi(player, customMenu);
				if(customType < 0 || customType > 3)
					return;

				switch(customType) {
					case 0:
						result = new Item(ItemId.LEATHER_CHAPS.id(), 1);
						reqLvl = 10;
						exp = 80;
						break;
					case 1:
						result = new Item(ItemId.LEATHER_TOP.id(), 1);
						reqLvl = 14;
						exp = 100;
						break;
					case 2:
						result = new Item(ItemId.LEATHER_SKIRT.id(), 1);
						reqLvl = 10;
						exp = 80;
						break;
					default:
						return;
				}
				break;
			default:
				return;
		}

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Craft Leather", player.getCarriedItems().getInventory().countId(leather.getCatalogId()), false) {
			@Override
			public void action() {
				Player owner = getOwner();
				if (owner.getSkills().getLevel(Skills.CRAFTING) < reqLvl) {
					owner.playerServerMessage(MessageType.QUEST, "You need to have a crafting of level " + reqLvl + " or higher to make " + result.getDef(player.getWorld()).getName());
					interruptBatch();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& owner.getFatigue() >= owner.MAX_FATIGUE) {
						owner.message("You are too tired to craft");
						interruptBatch();
						return;
					}
				}
				if (owner.getCarriedItems().remove(leather) > -1) {
					owner.message("You make some " + result.getDef(getWorld()).getName());
					owner.getCarriedItems().getInventory().add(result);
					owner.incExp(Skills.CRAFTING, exp, true);
					//a reel of thread accounts for 5 uses
					if (!owner.getCache().hasKey("part_reel_thread")) {
						owner.getCache().set("part_reel_thread", 1);
					} else {
						int parts = owner.getCache().getInt("part_reel_thread");
						if (parts >= 4) {
							owner.message("You use up one of your reels of thread");
							owner.getCarriedItems().remove(new Item(ItemId.THREAD.id()));
							owner.getCache().remove("part_reel_thread");
						} else {
							owner.getCache().put("part_reel_thread", parts + 1);
						}
					}
				}
			}
		});
	}

	private void useWool(Player player, final Item woolBall, final Item item) {
		int newID;
		switch (ItemId.getById(item.getCatalogId())) {
			case UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN:
				newID = ItemId.UNBLESSED_HOLY_SYMBOL.id();
				break;
			case UNSTRUNG_UNHOLY_SYMBOL_OF_ZAMORAK:
				newID = ItemId.UNBLESSED_UNHOLY_SYMBOL_OF_ZAMORAK.id();
				break;
			case UNSTRUNG_GOLD_AMULET:
				newID = ItemId.GOLD_AMULET.id();
				break;
			case UNSTRUNG_SAPPHIRE_AMULET:
				newID = ItemId.SAPPHIRE_AMULET.id();
				break;
			case UNSTRUNG_EMERALD_AMULET:
				newID = ItemId.EMERALD_AMULET.id();
				break;
			case UNSTRUNG_RUBY_AMULET:
				newID = ItemId.RUBY_AMULET.id();
				break;
			case UNSTRUNG_DIAMOND_AMULET:
				newID = ItemId.DIAMOND_AMULET.id();
				break;
			case UNSTRUNG_DRAGONSTONE_AMULET:
				newID = ItemId.UNENCHANTED_DRAGONSTONE_AMULET.id();
				break;
			default:
				return;
		}
		final int newId = newID;
		int woolAmount = player.getCarriedItems().getInventory().countId(woolBall.getCatalogId());
		int amuletAmount = player.getCarriedItems().getInventory().countId(item.getCatalogId());

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Craft String Amulet",
			Math.min(woolAmount, amuletAmount), false) {

			public void action() {
				Player owner = getOwner();
				if (owner.getCarriedItems().getInventory().countId(item.getCatalogId()) <= 0 || owner.getCarriedItems().getInventory().countId(ItemId.BALL_OF_WOOL.id()) <= 0) {
					interruptBatch();
					return;
				}
				if (owner.getCarriedItems().remove(woolBall) > -1 && owner.getCarriedItems().remove(item) > -1) {
					owner.message("You put some string on your " + item.getDef(getWorld()).getName().toLowerCase());
					owner.getCarriedItems().getInventory().add(new Item(newId));
				} else {
					interruptBatch();
					return;
				}
			}
		});
	}

	private boolean useWater(Player player, final Item water, final Item item) {
		int jugID = Formulae.getEmptyJug(water.getCatalogId());
		if (jugID == -1) { // This shouldn't happen
			return false;
		}
		// Clay and water is not bowl of water
		if (item.getCatalogId() == ItemId.CLAY.id() && water.getCatalogId() != ItemId.BOWL_OF_WATER.id()) {
			if (player.getCarriedItems().remove(water) > -1
				&& player.getCarriedItems().remove(item) > -1) {
				player.getCarriedItems().getInventory().add(new Item(jugID));
				player.getCarriedItems().getInventory().add(new Item(ItemId.SOFT_CLAY.id()));
				mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You mix the clay and water");
				player.message("You now have some soft workable clay");
			}
		} else {
			return false;
		}
		return true;
	}

	private String potteryItemName(String rawName) {
		String uncapName = rawName.toLowerCase();
		if (uncapName.startsWith("unfired ")) {
			return uncapName.substring(8);
		}
		return uncapName;
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		ItemGemDef gemDef = player.getWorld().getServer().getEntityHandler().getItemGemDef(item1.getCatalogId());
		ItemGemDef gemDef2 = player.getWorld().getServer().getEntityHandler().getItemGemDef(item2.getCatalogId());
		int item1ID = item1.getCatalogId();
		int item2ID = item2.getCatalogId();
		if (item1ID == ItemId.CHISEL.id() && (gemDef != null || gemDef2 != null)) {
			return true;
		} else if (item2ID == ItemId.CHISEL.id() && (gemDef != null || gemDef2 != null)) {
			return true;
		} else if (item1ID == ItemId.CHISEL.id() && item2ID == ItemId.KING_BLACK_DRAGON_SCALE.id()) {
			return true;
		} else if (item2ID == ItemId.CHISEL.id() && item1ID == ItemId.KING_BLACK_DRAGON_SCALE.id()) {
			return true;
		} else if (item1ID == ItemId.GLASSBLOWING_PIPE.id()) {
			return true;
		} else if (item2ID == ItemId.GLASSBLOWING_PIPE.id()) {
			return true;
		} else if (item1ID == ItemId.NEEDLE.id()) {
			return true;
		} else if (item2ID == ItemId.NEEDLE.id()) {
			return true;
		} else if (item1ID == ItemId.BALL_OF_WOOL.id()) {
			return true;
		} else if (item2ID == ItemId.BALL_OF_WOOL.id()) {
			return true;
		} else if ((item1ID == ItemId.BUCKET_OF_WATER.id() || item1ID == ItemId.JUG_OF_WATER.id()) && item2ID == ItemId.CLAY.id()) {
			return true;
		} else if ((item2ID == ItemId.BUCKET_OF_WATER.id() || item2ID == ItemId.JUG_OF_WATER.id()) && item1ID == ItemId.CLAY.id()) {
			return true;
		} else
			return item1ID == ItemId.MOLTEN_GLASS.id() && item2ID == ItemId.LENS_MOULD.id() || item1ID == ItemId.LENS_MOULD.id() && item2ID == ItemId.MOLTEN_GLASS.id();
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return craftingTypeChecks(obj, item, player);
	}
}
