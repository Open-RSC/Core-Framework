package com.openrsc.server.plugins.authentic.skills.crafting;

import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.external.ItemCraftingDef;
import com.openrsc.server.external.ItemGemDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.CarriedItems;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang.StringUtils;

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

	public final static int[] gold_moulds = {
		ItemId.RING_MOULD.id(),
		ItemId.NECKLACE_MOULD.id(),
		ItemId.AMULET_MOULD.id(),
	};

	public final static int[] silver_moulds = {
		ItemId.HOLY_SYMBOL_MOULD.id(),
		ItemId.UNHOLY_SYMBOL_MOULD.id(),
	};

	public final static int[] gems = {
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
					if (getCurrentLevel(player, Skill.CRAFTING.id()) < 15) {
						player.message("You need level 15 crafting to fix the teddy");
						return;
					}

					int stage = player.getCache().hasKey("miniquest_dwarf_youth_rescue") ? player.getCache().getInt("miniquest_dwarf_youth_rescue") : -1;
					if (stage < 1) {
						player.message("I'd better get these parts back to the kid");
						return;
					}
					carriedItems.remove(new Item(ItemId.TEDDY_HEAD.id()));
					carriedItems.remove(new Item(ItemId.TEDDY_BOTTOM.id()));
					carriedItems.remove(new Item(ItemId.THREAD.id()));
					carriedItems.getInventory().add(new Item(ItemId.TEDDY.id()));
					player.message("You stitch together the teddy parts");

				} else {
					player.message("You need the two teddy halves and some thread");
				}
			} else {
				makeLeather(player, item1, item2);
			}
		} else if (item2ID == ItemId.NEEDLE.id()) {
			if (item1ID == ItemId.TEDDY_HEAD.id() || item1ID == ItemId.TEDDY_BOTTOM.id()) {
				if (inventory.hasInInventory(ItemId.TEDDY_HEAD.id())
					&& inventory.hasInInventory(ItemId.TEDDY_BOTTOM.id())
					&& inventory.hasInInventory(ItemId.THREAD.id())) {
					if (getCurrentLevel(player, Skill.CRAFTING.id()) < 15) {
						player.message("You need level 15 crafting to fix the teddy");
						return;
					}

					int stage = player.getCache().hasKey("miniquest_dwarf_youth_rescue") ? player.getCache().getInt("miniquest_dwarf_youth_rescue") : -1;
					if (stage < 1) {
						player.message("I'd better get these parts back to the kid");
						return;
					}
					carriedItems.remove(new Item(ItemId.TEDDY_HEAD.id()));
					carriedItems.remove(new Item(ItemId.TEDDY_BOTTOM.id()));
					carriedItems.remove(new Item(ItemId.THREAD.id()));
					carriedItems.getInventory().add(new Item(ItemId.TEDDY.id()));
					player.message("You stitch together the teddy parts");

				} else {
					player.message("You need the two teddy halves and some thread");
				}
			} else {
				makeLeather(player, item2, item1);
			}
		} else if (item1ID == ItemId.BALL_OF_WOOL.id()) {
			useWool(player, item1, item2);
		} else if (item2ID == ItemId.BALL_OF_WOOL.id()) {
			useWool(player, item2, item1);
		} else if ((item1ID == ItemId.BUCKET_OF_WATER.id() || item1ID == ItemId.JUG_OF_WATER.id()) && item2ID == ItemId.CLAY.id()) {
			useWater(player, item1, item2);
		} else if ((item2ID == ItemId.BUCKET_OF_WATER.id() || item2ID == ItemId.JUG_OF_WATER.id()) && item1ID == ItemId.CLAY.id()) {
			useWater(player, item2, item1);
		} else if (item1ID == ItemId.MOLTEN_GLASS.id() && item2ID == ItemId.LENS_MOULD.id() || item1ID == ItemId.LENS_MOULD.id() && item2ID == ItemId.MOLTEN_GLASS.id()) {
			if (getQuestStage(player, Quests.OBSERVATORY_QUEST) >= 0 && getQuestStage(player, Quests.OBSERVATORY_QUEST) < 5) {
				say(player, null, "Perhaps I should speak to the professor first");
				return;
			}
			if (getCurrentLevel(player, Skill.CRAFTING.id()) < 10) {
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
			if (player.getCarriedItems().getInventory().countId(ItemId.SODA_ASH.id(), Optional.of(false)) < 1) {
				player.playerServerMessage(MessageType.QUEST, "You need some soda ash to make glass");
				return false;
			} else if (player.getCarriedItems().getInventory().countId(ItemId.SAND.id(), Optional.of(false)) < 1) {
				player.playerServerMessage(MessageType.QUEST, "You need some sand to make glass");
				return false;
			}
		}

		return true;
	}

	private boolean craftingTypeChecks(final GameObject obj, final Item item, final Player player) {
		boolean furnace = obj.getID() == 118 || obj.getID() == 813;
		boolean furnaceItem = DataConversions.inArray(itemsFurnance, item.getCatalogId());
		boolean jewelryBar = item.getCatalogId() == ItemId.SILVER_BAR.id() || item.getCatalogId() == ItemId.GOLD_BAR.id();
		boolean wantBetterJewelryCrafting = player.getConfig().WANT_BETTER_JEWELRY_CRAFTING && !player.getQolOptOut();
		boolean potteryOven = obj.getID() == 178;
		boolean potteryItem = DataConversions.inArray(itemsOven, item.getCatalogId());
		boolean spinningWheel = obj.getID() == 179;
		boolean softClay = item.getCatalogId() == ItemId.SOFT_CLAY.id();

		// Checks to make sure you're using the right item with the right object.
		// If WANT_BETTER_JEWELRY_CRAFTING is true, we'll disallow jewelry crafting so it
		// can be handled in the custom class.
		return (furnace && furnaceItem && !(jewelryBar && wantBetterJewelryCrafting))
			|| (potteryOven && potteryItem)
			|| (spinningWheel && softClay);
	}

	private void beginCrafting(final Item item, final Player player) {
		if (item.getCatalogId() == ItemId.SODA_ASH.id() || item.getCatalogId() == ItemId.SAND.id()) {
			doGlassMaking(item, player);
			return;
		} else if (DataConversions.inArray(itemsOven, item.getCatalogId())) {
			doPotteryFiring(item, player);
			return;
		}

		player.playerServerMessage(MessageType.QUEST,"what would you like to make");

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
			"ring",
			"Necklace",
			"amulet"
		};

        thinkbubble(new Item(ItemId.GOLD_BAR.id())); // bubble will be displayed after menu
		int type = multi(player, options);
		if (type < 0 || type > 2) {
			return;
		}
		reply.set(options[type]);

		boolean noGemInIt = false;
        if (!config().WANT_EQUIPMENT_TAB) { // TODO: this is not a very good way to detect other than Cabbage server config
            player.playerServerMessage(MessageType.QUEST,
                "Would you like to put a gem in the " + options[type].toLowerCase() + "?");
            options = new String[]{
                "Yes",
                "No"
            };
            noGemInIt = multi(player, options) == 1;
        }

		// select gem
		options = new String[]{
			"Sapphire",
			"Emerald",
			"Ruby",
			"Diamond"
		};
		if (config().MEMBER_WORLD) {
			if (config().WANT_EQUIPMENT_TAB) { // TODO: this is not a very good way to detect Cabbage server config
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
					"Sapphire",
					"Emerald",
					"Ruby",
					"Diamond",
					"dragonstone"
				};

				// Dragonstone should be capitalized only when making a Necklace
				if (type == 1) {
                    options[4] = "Dragonstone";
                }
			}
		}
		if (player.getCarriedItems().getInventory().countId(gold_moulds[type], Optional.of(false)) < 1) {
			player.playerServerMessage(MessageType.QUEST,"You need a " + player.getWorld().getServer().getEntityHandler().getItemDef(gold_moulds[type]).getName() + " to make a " + reply.get());
			return;
		}
        int gem = 0;
		if (!noGemInIt) {
            player.playerServerMessage(MessageType.QUEST, "what sort of gem do you want to put in the " + reply.get() + "?");
            gem = multi(player, options);

            if (gem < 0 || gem > options.length)
                return;

            reply.set(options[gem]);

            if (options.length < 6) {
                gem++; // translate past the "Gold" gem
            }
        } else {
            reply.set("Gold");
        }

		ItemCraftingDef def = player.getWorld().getServer().getEntityHandler().getCraftingDef((gem * 3) + type);
		if (def == null) {
			// No definition found
			player.message("Nothing interesting happens");
			return;
		}

		if (def.itemID == ItemId.NOTHING.id()) {
		    // not an authentic message
			player.message("You have no reason to make that item.");
			return;
		}

		int repeat = 1;

		// Perfect gold bars shouldn't be batched
		if (config().BATCH_PROGRESSION) {
			if (item.getCatalogId() != ItemId.GOLD_BAR_FAMILYCREST.id()) {
				if (gem > 0) {
					repeat = Math.min(
						player.getCarriedItems().getInventory().countId(gems[gem], Optional.of(false)),
						player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false))
					);
				} else {
					repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
				}
			}
		}

		startbatch(repeat);
		batchGoldJewelry(player, item, def, gem, gems, type, reply);
	}

	private void batchGoldJewelry(Player player, Item item, ItemCraftingDef def, int gem, int[] gems, int type, AtomicReference<String> reply) {
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < def.getReqLevel()) {
			player.playerServerMessage(MessageType.QUEST, "You need a crafting skill of level " + def.getReqLevel() + " to make this");
			return;
		}
		if (checkFatigue(player)) return;

        // Get last gold bar in inventory.
        Item goldBar = player.getCarriedItems().getInventory().get(
            player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
        );
        if (goldBar == null) {
            // this message is inauthentic; authentically can't happen b/c there's no batching
            player.message("You don't have a " + reply.get());
            return;
        }

        Item result;
        if (goldBar.getCatalogId() == ItemId.GOLD_BAR_FAMILYCREST.id() && gem == 3 && type == 0) {
            result = new Item(ItemId.RUBY_RING_FAMILYCREST.id(), 1);
        } else if (goldBar.getCatalogId() == ItemId.GOLD_BAR_FAMILYCREST.id() && gem == 3 && type == 1) {
            result = new Item(ItemId.RUBY_NECKLACE_FAMILYCREST.id(), 1);
        } else {
            result = new Item(def.getItemID(), 1);
        }

        String resultString = "You make ";
        String connector = "a ";
        String itemName = result.getDef(player.getWorld()).getName().toLowerCase();
        if (itemName.contains("emerald")) {
            itemName = StringUtils.capitalize(itemName);
            connector = "an ";
        }

        // Get last gem in inventory.
		Item gemItem;
		if (gem != 0) {
			gemItem = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(gems[gem], Optional.of(false))
			);
			if (gemItem == null) {
			    String cut = "cut ";

			    if (def.getItemID() == ItemId.UNSTRUNG_DRAGONSTONE_AMULET.id()) {
			        cut = ""; // there may be others where this string is omitted, but no evidence currently known
                }
                String gemName = reply.get().toLowerCase();
			    if (gemName.contains("emerald")) {
			        gemName = "Emerald";
                }

                player.playerServerMessage(MessageType.QUEST, "You do not have a " + cut + gemName + " to make a " + itemName);
				return;
			}
		}

        if (itemName.contains("sapphire")) {
            itemName = StringUtils.capitalize(itemName);
        }

		// Remove items
        delay();
        player.playerServerMessage(MessageType.QUEST, resultString + connector + itemName);
		player.getCarriedItems().remove(goldBar);
		if (gem > 0) {
			player.getCarriedItems().remove(new Item(gems[gem]));
		}
		player.getCarriedItems().getInventory().add(result);
		player.incExp(Skill.CRAFTING.id(), def.getExp(), true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchGoldJewelry(player, item, def, gem, gems, type, reply);
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
		if (player.getCarriedItems().getInventory().countId(silver_moulds[type], Optional.of(false)) <= 0) {
			player.message("You need a " + player.getWorld().getServer().getEntityHandler().getItemDef(silver_moulds[type]).getName() + " to make a " + reply.get() + "!");
			return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchSilverJewelry(player, item, results, type, reply);
	}

	private void batchSilverJewelry(Player player, Item item, int[] results, int type, AtomicReference<String> reply) {
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < 16) {
			player.playerServerMessage(MessageType.QUEST, "You need a crafting skill of level 16 to make this");
			return;
		}
		if (checkFatigue(player)) return;

		Item silverMould = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(silver_moulds[type], Optional.of(false))
		);
		if (silverMould == null) {
			player.message("You need a " + player.getWorld().getServer().getEntityHandler().getItemDef(silver_moulds[type]).getName() + " to make a " + reply.get() + "!");
			return;
		}

		Item silver = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (silver == null) return;

		thinkbubble(silver);
		player.getCarriedItems().remove(silver);
		delay(2);

		Item result = new Item(results[type]);
		player.playerServerMessage(MessageType.QUEST, "You make a " + result.getDef(player.getWorld()).getName());
		player.getCarriedItems().getInventory().add(result);
		player.incExp(Skill.CRAFTING.id(), 200, true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchSilverJewelry(player, item, results, type, reply);
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
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchPotteryMoulding(player, item, reqLvl, result, msg, exp);
	}

	private void batchPotteryMoulding(Player player, Item item, int reqLvl, Item result, AtomicReference<String> msg, int exp) {
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < reqLvl) {
			player.playerServerMessage(MessageType.QUEST, "You need to have a crafting of level " + reqLvl + " or higher to make " + msg.get());
			return;
		}
		if (checkFatigue(player)) return;

		Item softClay = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (softClay == null) return;

		delay();
		player.getCarriedItems().remove(softClay);
		thinkbubble(softClay);
		player.playerServerMessage(MessageType.QUEST, "you make the clay into a " + potteryItemName(result.getDef(player.getWorld()).getName()));
		player.getCarriedItems().getInventory().add(result);
		player.incExp(Skill.CRAFTING.id(), exp, true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchPotteryMoulding(player, item, reqLvl, result, msg, exp);
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
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchPotteryFiring(player, item, reqLvl, result, msg, exp);
	}

	private void batchPotteryFiring(Player player, Item item, int reqLvl, Item result, AtomicReference<String> msg, int exp) {
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < reqLvl) {
			player.playerServerMessage(MessageType.QUEST, "You need to have a crafting of level " + reqLvl + " or higher to make " + msg.get());
			return;
		}
		if (checkFatigue(player)) return;

		Item unfiredClay = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (unfiredClay == null) return;

		thinkbubble(unfiredClay);
		String potteryItem = potteryItemName(item.getDef(player.getWorld()).getName());
		player.playerServerMessage(MessageType.QUEST, "You put the " + potteryItem + " in the oven");
		player.getCarriedItems().remove(unfiredClay);
		delay(3);

		if (Formulae.crackPot(reqLvl, player.getSkills().getLevel(Skill.CRAFTING.id()))) {
			player.playerServerMessage(MessageType.QUEST, "The " // TODO: Check if is authentic message
				+ potteryItem + " cracks in the oven, you throw it away.");
		} else {
			player.playerServerMessage(MessageType.QUEST, "the "
				+ potteryItem + " hardens in the oven");

			delay(3);

			player.playerServerMessage(MessageType.QUEST, "You remove a "
				+ result.getDef(player.getWorld()).getName().toLowerCase()
				+ " from the oven");
			player.getCarriedItems().getInventory().add(result);
			player.incExp(Skill.CRAFTING.id(), exp, true);
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchPotteryFiring(player, item, reqLvl, result, msg, exp);
		}
	}

	private void doGlassMaking(final Item item, final Player player) {
		int otherItem = item.getCatalogId() == ItemId.SAND.id() ? ItemId.SODA_ASH.id() : ItemId.SAND.id();
		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
			repeat = Math.min(player.getCarriedItems().getInventory().countId(otherItem, Optional.of(false)), repeat);
		}

		startbatch(repeat);
		batchGlassMaking(player, item, otherItem);
	}

	private void batchGlassMaking(Player player, Item item, int otherItem) {
		if (checkFatigue(player)) return;

		Inventory inventory = player.getCarriedItems().getInventory();
		Item item1 = inventory.get(
			inventory.getLastIndexById(otherItem, Optional.of(false))
		);
		Item item2 = inventory.get(
			inventory.getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (item1 == null || item2 == null)	return;

		thinkbubble(item2);
		player.playerServerMessage(MessageType.QUEST, "you heat the sand and soda ash in the furnace to make glass");
		player.getCarriedItems().remove(item1);
		player.getCarriedItems().remove(item2);
		delay();
		inventory.add(new Item(ItemId.MOLTEN_GLASS.id(), 1));
		inventory.add(new Item(ItemId.BUCKET.id(), 1));
		player.incExp(Skill.CRAFTING.id(), 80, true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchGlassMaking(player, item, otherItem);
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
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(glass.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchGlassBlowing(player, glass, result, reqLvl, exp, resultGen);
	}

	private void batchGlassBlowing(Player player, Item glass, Item result, int reqLvl, int exp, String resultGen) {
		Inventory inventory = player.getCarriedItems().getInventory();
		ServerConfiguration config = config();
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < reqLvl) {
			player.message(
				"You need a crafting level of " + reqLvl + " to make " + resultGen);
			return;
		}
		if (checkFatigue(player)) return;

		glass = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(glass.getCatalogId(), Optional.of(false))
		);
		if (glass == null) return;

		player.getCarriedItems().remove(glass);
		delay();
		String message = "You make a " + result.getDef(player.getWorld()).getName();

		// Special handling for vials
		int amount = 1;
		if (result.getCatalogId() == ItemId.EMPTY_VIAL.id()) {
			if (config.WANT_CUSTOM_QUESTS) {
				double breakChance = 91.66667 - getCurrentLevel(player, Skill.CRAFTING.id()) / 1.32;
				for (int loop = 0; loop < 5; ++loop) {
					double hit = new Random().nextDouble() * 99;
					if (hit > breakChance) {
						amount++;
					}
				}
				message = "You make " + amount + " vial" + (amount != 1 ? "s" : "");
				if (player.getLocation().inBounds(418, 559, 421, 563)) {
					result.getItemStatus().setNoted(true);
				}
			}
		}

		player.playerServerMessage(MessageType.QUEST, message);

		if (result.getNoted()) {
			result.getItemStatus().setAmount(amount);
			inventory.add(result);
		}
		else {
			for (int i = 0; i < amount; i++) {
				inventory.add(result);
			}
		}

		player.incExp(Skill.CRAFTING.id(), exp, true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay(2);
			batchGlassBlowing(player, glass, result, reqLvl, exp, resultGen);
		}
	}

	private void doCutGem(Player player, final Item chisel, final Item gem) {
		final ItemGemDef gemDef = player.getWorld().getServer().getEntityHandler().getItemGemDef(gem.getCatalogId());
		if (gemDef == null) {
			if (gem.getCatalogId() == ItemId.KING_BLACK_DRAGON_SCALE.id()) {
				if (getCurrentLevel(player, Skill.CRAFTING.id()) < 90) {
					player.message("You need 90 crafting to split the scales");
					return;
				}
				if (player.getCarriedItems().remove(new Item(ItemId.KING_BLACK_DRAGON_SCALE.id(), 1)) > -1) {
					player.message("You chip the massive scale into 5 pieces");
					give(player, ItemId.CHIPPED_DRAGON_SCALE.id(), 5);
					player.incExp(Skill.CRAFTING.id(), config().GAME_TICK * 2, true);
				}
			} else {
				player.message("Nothing interesting happens");
			}
			return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(gem.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchGemCutting(player, gem, gemDef);
	}

	private void batchGemCutting(Player player, Item gem, ItemGemDef gemDef) {
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < gemDef.getReqLevel()) {
			boolean pluralize = gemDef.getGemID() <= ItemId.UNCUT_DRAGONSTONE.id();
			player.playerServerMessage(MessageType.QUEST,
				"you need a crafting level of " + gemDef.getReqLevel()
					+ " to cut " + (gem.getDef(player.getWorld()).getName().contains("ruby") ? "rubies" : gem.getDef(player.getWorld()).getName().replaceFirst("(?i)uncut ", "") + (pluralize ? "s" : "")));
			return;
		}
		if (checkFatigue(player)) return;

		Item item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(gem.getCatalogId(), Optional.of(false)));
		if (item == null) return;

		player.getCarriedItems().remove(item);
		delay();
		Item cutGem = new Item(gemDef.getGemID(), 1);
		// Jade, Opal and red topaz fail handler - 25% chance to fail

		if (DataConversions.inArray(gemsThatFail, gem.getCatalogId()) &&
			Formulae.smashGem(gem.getCatalogId(), gemDef.getReqLevel(), player.getSkills().getLevel(Skill.CRAFTING.id()))) {
			player.message("You miss hit the chisel and smash the " + cutGem.getDef(player.getWorld()).getName() + " to pieces!");
			player.getCarriedItems().getInventory().add(new Item(ItemId.CRUSHED_GEMSTONE.id()));

			if (gem.getCatalogId() == ItemId.UNCUT_RED_TOPAZ.id()) {
				player.incExp(Skill.CRAFTING.id(), 25, true);
			} else if (gem.getCatalogId() == ItemId.UNCUT_JADE.id()) {
				player.incExp(Skill.CRAFTING.id(), 20, true);
			} else {
				player.incExp(Skill.CRAFTING.id(), 15, true);
			}
		} else {
			player.getCarriedItems().getInventory().add(cutGem, true);
			String gemName = cutGem.getDef(player.getWorld()).getName();
            if (!DataConversions.inArray(gemsThatFail, gem.getCatalogId())) {
                gemName.toLowerCase();
            } else {
                if (gemName.equals("red topaz")) {
                    gemName = "Red Topaz";
                } else {
                    gemName = StringUtils.capitalize(gemName);
                }
            }
			player.message("You cut the " + gemName);
			player.playSound("chisel");
			player.incExp(Skill.CRAFTING.id(), gemDef.getExp(), true);
		}

		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay(2);
			batchGemCutting(player, gem, gemDef);
		}
	}

	private void makeLeather(Player player, final Item needle, final Item leather) {
		if (leather.getCatalogId() != ItemId.LEATHER.id()) {
			player.message("Nothing interesting happens");
			return;
		}

		if (player.getCarriedItems().getInventory().countId(ItemId.THREAD.id(), Optional.of(false)) < 1) {
			player.message("You need some thread to make anything out of leather");
			return;
		}

		final boolean customLeather = config().WANT_CUSTOM_LEATHER;

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
				if (!customLeather) {
					return;
				}

				String[] customMenu = new String[]{
					"Chaps",
					"Top",
					"Skirt",
					"Cancel"
				};

				int customType = multi(player, customMenu);
				if (customType < 0 || customType > 3)
					return;

				switch (customType) {
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

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(leather.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchLeather(player, leather, result, reqLvl, exp);
	}

	private void batchLeather(Player player, Item leather, Item result, int reqLvl, int exp) {
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < reqLvl) {
			player.playerServerMessage(MessageType.QUEST, "You need to have a crafting of level " + reqLvl + " or higher to make " + result.getDef(player.getWorld()).getName());
			return;
		}
		if (checkFatigue(player)) return;

		Item item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(leather.getCatalogId(), Optional.of(false))
		);
		if (item == null) return;

		player.getCarriedItems().remove(item);
		delay();
		player.message("You make some " + result.getDef(player.getWorld()).getName());
		player.getCarriedItems().getInventory().add(result);
		player.incExp(Skill.CRAFTING.id(), exp, true);
		// A reel of thread accounts for 5 uses
		if (!player.getCache().hasKey("part_reel_thread")) {
			player.getCache().set("part_reel_thread", 1);
		} else {
			int parts = player.getCache().getInt("part_reel_thread");
			if (parts >= 4) {
				player.message("You use up one of your reels of thread");
				player.getCache().remove("part_reel_thread");
				player.getCarriedItems().remove(new Item(ItemId.THREAD.id()));
				if(player.getCarriedItems().getInventory().countId(ItemId.THREAD.id(), Optional.of(false)) <= 0) {
					return;
				}
			} else {
				player.getCache().put("part_reel_thread", parts + 1);
			}
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchLeather(player, leather, result, reqLvl, exp);
		}
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
		int woolAmount = player.getCarriedItems().getInventory().countId(woolBall.getCatalogId(), Optional.of(false));
		int amuletAmount = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = Math.min(woolAmount, amuletAmount);
		}

		startbatch(repeat);
		batchString(player, item, woolBall, newID);
	}

	private void batchString(Player player, Item item, Item woolBall, int newID) {
		item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		woolBall = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(woolBall.getCatalogId(), Optional.of(false))
		);
		if (item == null || woolBall == null) return;

		player.getCarriedItems().remove(woolBall);
		player.getCarriedItems().remove(item);
		player.message("You put some string on your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().getInventory().add(new Item(newID));
		delay();

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchString(player, item, woolBall, newID);
		}
	}

	private void useWater(Player player, Item water, Item item) {
		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = Math.min(player.getCarriedItems().getInventory().countId(water.getCatalogId(), Optional.of(false)),
				player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false)));
		}

		startbatch(repeat);
		batchWaterClay(player, water, item);
	}

	private void batchWaterClay(Player player, Item water, Item item) {
		int jugID = Formulae.getEmptyJug(water.getCatalogId());
		if (jugID == -1) return;

		water = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(water.getCatalogId(), Optional.of(false))
		);
		item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (water == null || item == null) return;
		player.getCarriedItems().remove(water);
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(jugID));
		player.getCarriedItems().getInventory().add(new Item(ItemId.SOFT_CLAY.id()));
		player.message("You mix the clay and water");
		delay(2);
		player.message("You now have some soft workable clay");

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchWaterClay(player, water, item);
		}
	}

	private String potteryItemName(String rawName) {
		String uncapName = rawName.toLowerCase();
		if (uncapName.startsWith("unfired ")) {
			return uncapName.substring(8);
		}
		return uncapName;
	}

	private boolean checkFatigue(Player player) {
		if (config().WANT_FATIGUE
				&& config().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
			player.message("You are too tired to craft");
			return true;
		}
		return false;
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
