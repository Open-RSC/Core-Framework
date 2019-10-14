package com.openrsc.server.plugins.skills;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ItemCraftingDef;
import com.openrsc.server.external.ItemGemDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.concurrent.atomic.AtomicReference;

import static com.openrsc.server.plugins.Functions.*;

public class Crafting implements InvUseOnItemListener,
	InvUseOnItemExecutiveListener, InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener {

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

	@Override
	public void onInvUseOnItem(Player player, Item item1, Item item2) {
		if (item1.getID() == ItemId.CHISEL.id()) {
			doCutGem(player, item1, item2);
		} else if (item2.getID() == ItemId.CHISEL.id()) {
			doCutGem(player, item2, item1);
		} else if (item1.getID() == ItemId.GLASSBLOWING_PIPE.id()) {
			doGlassBlowing(player, item1, item2);
		} else if (item2.getID() == ItemId.GLASSBLOWING_PIPE.id()) {
			doGlassBlowing(player, item2, item1);
		} else if (item1.getID() == ItemId.NEEDLE.id()) {
			makeLeather(player, item1, item2);
		} else if (item2.getID() == ItemId.NEEDLE.id()) {
			makeLeather(player, item2, item1);
		} else if (item1.getID() == ItemId.BALL_OF_WOOL.id()) {
			useWool(player, item1, item2);
		} else if (item2.getID() == ItemId.BALL_OF_WOOL.id()) {
			useWool(player, item2, item1);
		} else if ((item1.getID() == ItemId.BUCKET_OF_WATER.id() || item1.getID() == ItemId.JUG_OF_WATER.id() || item1.getID() == ItemId.BOWL_OF_WATER.id()) && useWater(player, item1, item2)) {
			return;
		} else if ((item2.getID() == ItemId.BUCKET_OF_WATER.id() || item2.getID() == ItemId.JUG_OF_WATER.id() || item2.getID() == ItemId.BOWL_OF_WATER.id()) && useWater(player, item2, item1)) {
			return;
		} else if (item1.getID() == ItemId.MOLTEN_GLASS.id() && item2.getID() == ItemId.LENS_MOULD.id() || item1.getID() == ItemId.LENS_MOULD.id() && item2.getID() == ItemId.MOLTEN_GLASS.id()) {
			if (getCurrentLevel(player, Skills.CRAFTING) < 10) {
				player.message("You need a crafting level of 10 to make the lens");
				return;
			}
			if (player.getInventory().remove(new Item(ItemId.MOLTEN_GLASS.id())) > -1) {
				player.message("You pour the molten glass into the mould");
				player.message("And clasp it together");
				player.message("It produces a small convex glass disc");
				player.getInventory().add(new Item(ItemId.LENS.id()));
			}
			return;
		}
		player.message("Nothing interesting happens");
	}

	@Override
	public void onInvUseOnObject(GameObject obj, final Item item, final Player player) {

		if (!craftingChecks(obj, item, player)) return;

		beginCrafting(item, player);
	}

	private boolean craftingChecks(final GameObject obj, final Item item, final Player player) {
		// allowed item on crafting game objects
		if (!craftingTypeChecks(obj, item, player)) return false;

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

		if (item.getID() == ItemId.SODA_ASH.id() || item.getID() == ItemId.SAND.id()) { // Soda Ash or Sand (Glass)
			if (player.getInventory().countId(ItemId.SODA_ASH.id()) < 1) {
				player.playerServerMessage(MessageType.QUEST, "You need some soda ash to make glass");
				return false;
			} else if (player.getInventory().countId(ItemId.SAND.id()) < 1) {
				player.playerServerMessage(MessageType.QUEST, "You need some sand to make glass");
				return false;
			}
		}

		return true;
	}

	private boolean craftingTypeChecks(final GameObject obj, final Item item, final Player player) {
		return ((obj.getID() == 118 || obj.getID() == 813) && DataConversions.inArray(itemsFurnance, item.getID()))
				|| (obj.getID() == 178 && DataConversions.inArray(itemsOven, item.getID()))
				|| (obj.getID() == 179 && item.getID() == ItemId.SOFT_CLAY.id());
	}

	private void beginCrafting(final Item item, final Player player) {
		if (item.getID() == ItemId.SODA_ASH.id() || item.getID() == ItemId.SAND.id()) {
			doGlassMaking(item, player);
			return;
		} else if (DataConversions.inArray(itemsOven, item.getID())) {
			doPotteryFiring(item, player);
			return;
		}

		player.message("What would you like to make?");

		if (item.getID() == ItemId.GOLD_BAR.id() || item.getID() == ItemId.GOLD_BAR_FAMILYCREST.id()) {
			doGoldJewelry(item, player);
		} else if (item.getID() == ItemId.SILVER_BAR.id()) {
			doSilverJewelry(item, player);
		} else if (item.getID() == ItemId.SOFT_CLAY.id()) {
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
		int type = showMenu(player, options);
		if (player.isBusy() || type < 0 || type > 2) {
			return;
		}
		reply.set(options[type]);

		final int[] moulds = {
				ItemId.RING_MOULD.id(),
				ItemId.NECKLACE_MOULD.id(),
				ItemId.AMULET_MOULD.id(),
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
		if (player.getInventory().countId(moulds[type]) < 1) {
			player.message("You need a " + player.getWorld().getServer().getEntityHandler().getItemDef(moulds[type]).getName() + " to make a " + reply.get());
			return;
		}
		player.message("What type of " + reply.get() + " would you like to make?");

		int gem = showMenu(player, options);

		if (player.isBusy() || gem < 0 || gem > (player.getWorld().getServer().getConfig().MEMBER_WORLD ? 5 + (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB ? 1 : 0) : 4)) {
			return;
		}
		reply.set(options[gem]);

		ItemCraftingDef def = player.getWorld().getServer().getEntityHandler().getCraftingDef((gem * 3) + type);
		if (def == null) {
			// No definition found
			player.message("Nothing interesting happens");
			return;
		}

		if (def.itemID == ItemId.NOTHING.id())
		{
			player.message("You have no reason to make that item.");
			return;
		}

		int retrytimes = player.getInventory().countId(item.getID());
		if (gem != 0) {
			retrytimes = player.getInventory().countId(gems[gem]) < retrytimes ? player.getInventory().countId(gems[gem]) : retrytimes;
		}
		if (retrytimes <= 0) {
			if (gem != 0 && player.getInventory().countId(gems[gem]) < 1) {
				player.message("You don't have a " + reply.get() + ".");
			}
			return;
		}

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 1200, "Craft Gold Jewelry", retrytimes, false) {
			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.CRAFTING) < def.getReqLevel()) {
					getOwner().playerServerMessage(MessageType.QUEST, "You need a crafting skill of level " + def.getReqLevel() + " to make this");
					interrupt();
					return;
				}
				if (gem != 0 && getOwner().getInventory().countId(gems[gem]) < 1) {
					getOwner().message("You don't have a " + reply.get() + ".");
					interrupt();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().message("You are too tired to craft");
						interrupt();
						return;
					}
				}
				if (getOwner().getInventory().remove(item) > -1 && (gem == 0 || getOwner().getInventory().remove(gems[gem], 1) > -1)) {
					showBubble(getOwner(), item);
					Item result;
					if (item.getID() == ItemId.GOLD_BAR_FAMILYCREST.id() && gem == 3 && type == 0) {
						result = new Item(ItemId.RUBY_RING_FAMILYCREST.id(), 1);
					} else if (item.getID() == ItemId.GOLD_BAR_FAMILYCREST.id() && gem == 3 && type == 1) {
						result = new Item(ItemId.RUBY_NECKLACE_FAMILYCREST.id(), 1);
					} else {
						result = new Item(def.getItemID(), 1);
					}
					getOwner().playerServerMessage(MessageType.QUEST, "You make a " + result.getDef(getWorld()).getName());
					getOwner().getInventory().add(result);
					getOwner().incExp(Skills.CRAFTING, def.getExp(), true);
				} else {
					getOwner().message("You don't have a " + reply.get() + ".");
					interrupt();
				}
			}
		});
	}

	private void doSilverJewelry(final Item item, final Player player) {
		AtomicReference<String> reply = new AtomicReference<String>();

		// select type
		String[] options = new String[]{
				"Holy Symbol of Saradomin",
				"Unholy symbol of Zamorak"
		};
		int type = showMenu(player, options);
		if (player.isBusy() || type < 0 || type > 1) {
			return;
		}
		reply.set(options[type]);

		int[] moulds = {
				ItemId.HOLY_SYMBOL_MOULD.id(),
				ItemId.UNHOLY_SYMBOL_MOULD.id(),
		};
		final int[] results = {
				ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(),
				ItemId.UNSTRUNG_UNHOLY_SYMBOL_OF_ZAMORAK.id()
		};
		if (player.getInventory().countId(moulds[type]) < 1) {
			player.message("You need a " + player.getWorld().getServer().getEntityHandler().getItemDef(moulds[type]).getName() + " to make a " + reply.get() + "!");
			return;
		}

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 1200, "Craft Silver Jewelry", player.getInventory().countId(item.getID()), false) {
			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.CRAFTING) < 16) {
					getOwner().playerServerMessage(MessageType.QUEST, "You need a crafting skill of level 16 to make this");
					interrupt();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().message("You are too tired to craft");
						interrupt();
						return;
					}
				}
				if (getOwner().getInventory().remove(item) > -1) {
					showBubble(getOwner(), item);
					Item result = new Item(results[type]);
					getOwner().playerServerMessage(MessageType.QUEST, "You make a " + result.getDef(getWorld()).getName());
					getOwner().getInventory().add(result);
					getOwner().incExp(Skills.CRAFTING, 200, true);
				} else {
					interrupt();
				}
			}
		});
	}

	private void doPotteryMolding(final Item item, final Player player) {
		String[] options = new String[]{"Pie dish", "Pot", "Bowl"};
		int type = showMenu(player, options);
		if (player.isBusy() || type < 0 || type > 2) {
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

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Craft Clay", player.getInventory().countId(item.getID()), false) {
			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.CRAFTING) < reqLvl) {
					getOwner().playerServerMessage(MessageType.QUEST, "You need to have a crafting of level " + reqLvl + " or higher to make " + msg.get());
					interrupt();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().message("You are too tired to craft");
						interrupt();
						return;
					}
				}
				if (getOwner().getInventory().remove(item) > -1) {
					showBubble(getOwner(), item);
					getOwner().playerServerMessage(MessageType.QUEST, "you make the clay into a " + potteryItemName(result.getDef(getWorld()).getName()));
					getOwner().getInventory().add(result);
					getOwner().incExp(Skills.CRAFTING, exp, true);
				} else {
					interrupt();
				}
			}
		});
	}

	private void doPotteryFiring(final Item item, final Player player) {
		int reqLvl, xp;
		Item result;
		AtomicReference<String> msg = new AtomicReference<String>();
		switch (ItemId.getById(item.getID())) {
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
		final boolean fail = Formulae.crackPot(reqLvl, player.getSkills().getLevel(Skills.CRAFTING));

		showBubble(player, item);
		String potteryItem = potteryItemName(item.getDef(player.getWorld()).getName());
		player.playerServerMessage(MessageType.QUEST, "You put the " + potteryItem + " in the oven");
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 1800, "Craft Clay", player.getInventory().countId(item.getID()), false) {
			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.CRAFTING) < reqLvl) {
					getOwner().playerServerMessage(MessageType.QUEST, "You need to have a crafting of level " + reqLvl + " or higher to make " + msg.get());
					interrupt();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().message("You are too tired to craft");
						interrupt();
						return;
					}
				}
				showBubble(getOwner(), item);
				if (getOwner().getInventory().remove(item) > -1) {
					if (fail) {
						getOwner().playerServerMessage(MessageType.QUEST, "The " // TODO: Check if is authentic message
							+ potteryItem + " cracks in the oven, you throw it away.");
					} else {
						getOwner().playerServerMessage(MessageType.QUEST, "the "
							+ potteryItem + " hardens in the oven");
						getWorld().getServer().getGameEventHandler().add(new SingleEvent(getWorld(), getOwner(), 1800, "Remove Clay From Oven") {
							@Override
							public void action() {
								getOwner().playerServerMessage(MessageType.QUEST, "You remove a "
									+ result.getDef(getWorld()).getName().toLowerCase()
									+ " from the oven");
								getOwner().getInventory().add(result);
								getOwner().incExp(Skills.CRAFTING, exp, true);
							}

						});
					}
				} else {
					interrupt();
				}
			}
		});
	}

	private void doGlassMaking(final Item item, final Player player) {
		int otherItem = item.getID() == ItemId.SAND.id() ? ItemId.SODA_ASH.id() : ItemId.SAND.id();
		int repeatTimes = player.getInventory().countId(item.getID());
		repeatTimes = player.getInventory().countId(otherItem) < repeatTimes ? player.getInventory().countId(otherItem) : repeatTimes;

		showBubble(player, item);
		player.playerServerMessage(MessageType.QUEST, "you heat the sand and soda ash in the furnace to make glass");
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Craft Molten Glass", repeatTimes, false) {
			public void action() {
				if (getOwner().getInventory().countId(otherItem) < 1 ||
						getOwner().getInventory().countId(item.getID()) < 1) {
					interrupt();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().message("You are too tired to craft");
						interrupt();
						return;
					}
				}
				if (getOwner().getInventory().remove(otherItem, 1) > -1
						&& getOwner().getInventory().remove(item) > -1) {
					getOwner().getInventory().add(new Item(ItemId.MOLTEN_GLASS.id(), 1));
					getOwner().getInventory().add(new Item(ItemId.BUCKET.id(), 1));
					getOwner().incExp(Skills.CRAFTING, 80, true);
				} else {
					interrupt();
					return;
				}

				if (!isCompleted()) {
					showBubble(getOwner(), item);
					getOwner().playerServerMessage(MessageType.QUEST, "you heat the sand and soda ash in the furnace to make glass");
				}
			}
		});
	}

	private void doCutGem(Player player, final Item chisel, final Item gem) {
		final ItemGemDef gemDef = player.getWorld().getServer().getEntityHandler().getItemGemDef(gem.getID());
		if (gemDef == null) {
			return;
		}

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Cut Gem", player.getInventory().countId(gem.getID()), false) {
			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.CRAFTING) < gemDef.getReqLevel()) {
					boolean pluralize = gemDef.getGemID() <= ItemId.UNCUT_DRAGONSTONE.id();
					getOwner().playerServerMessage(MessageType.QUEST,
						"you need a crafting level of " + gemDef.getReqLevel()
							+ " to cut " + (gem.getDef(getWorld()).getName().contains("ruby") ? "rubies" : gem.getDef(getWorld()).getName().replaceFirst("(?i)uncut ", "") + (pluralize ? "s" : "")));
					interrupt();
					return;
				}
				if (getOwner().getInventory().remove(gem.getID(), 1, false) > -1) {
					Item cutGem = new Item(gemDef.getGemID(), 1);
					// Jade, Opal and red topaz fail handler - 25% chance to fail

					if (DataConversions.inArray(gemsThatFail, gem.getID()) &&
						Formulae.smashGem(gem.getID(), gemDef.getReqLevel(), getOwner().getSkills().getLevel(Skills.CRAFTING))) {
						getOwner().message("You miss hit the chisel and smash the " + cutGem.getDef(getWorld()).getName() + " to pieces!");
						getOwner().getInventory().add(new Item(ItemId.CRUSHED_GEMSTONE.id()));

						if (gem.getID() == ItemId.UNCUT_RED_TOPAZ.id()) {
							getOwner().incExp(Skills.CRAFTING, 25, true);
						} else if (gem.getID() == ItemId.UNCUT_JADE.id()) {
							getOwner().incExp(Skills.CRAFTING, 20, true);
						} else {
							getOwner().incExp(Skills.CRAFTING, 15, true);
						}
					} else {
						getOwner().getInventory().add(cutGem, true);
						getOwner().message("You cut the " + cutGem.getDef(getWorld()).getName().toLowerCase());
						getOwner().playSound("chisel");
						getOwner().incExp(Skills.CRAFTING, gemDef.getExp(), true);
					}
				}
				else {
					interrupt();
				}
			}
		});
	}

	private void doGlassBlowing(Player player, final Item pipe, final Item glass) {
		if (glass.getID() != ItemId.MOLTEN_GLASS.id()) {
			return;
		}
		player.message("what would you like to make?");

		String[] options = new String[]{
				"Vial",
				"orb",
				"Beer glass"
		};

		int type = showMenu(player, options);
		if (player.isBusy() || type < 0 || type > 2) {
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

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Craft Glass Blowing", player.getInventory().countId(glass.getID()), false) {
			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.CRAFTING) < reqLvl) {
					getOwner().message(
						"You need a crafting level of " + reqLvl + " to make " + resultGen);
					interrupt();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().message("You are too tired to craft");
						interrupt();
						return;
					}
				}
				if (getOwner().getInventory().remove(glass) > -1) {
					getOwner().playerServerMessage(MessageType.QUEST, "You make a " + result.getDef(getWorld()).getName());
					getOwner().getInventory().add(result);
					getOwner().incExp(Skills.CRAFTING, exp, true);
				}
			}
		});
	}

	private void makeLeather(Player player, final Item needle, final Item leather) {
		if (leather.getID() != ItemId.LEATHER.id()) {
			return;
		}
		if (player.getInventory().countId(ItemId.THREAD.id()) < 1) {
			player.message("You need some thread to make anything out of leather");
			return;
		}

		String[] options = new String[]{
				"Armour",
				"Gloves",
				"Boots",
				"Cancel"
		};

		int type = showMenu(player, options);
		if (player.isBusy() || type < 0 || type > 3) {
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
			default:
				return;
		}

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Craft Leather", player.getInventory().countId(leather.getID()), false) {
			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.CRAFTING) < reqLvl) {
					getOwner().playerServerMessage(MessageType.QUEST, "You need to have a crafting of level " + reqLvl + " or higher to make " + result.getDef(player.getWorld()).getName());
					interrupt();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().message("You are too tired to craft");
						interrupt();
						return;
					}
				}
				if (getOwner().getInventory().remove(leather) > -1) {
					getOwner().message("You make some " + result.getDef(getWorld()).getName());
					getOwner().getInventory().add(result);
					getOwner().incExp(Skills.CRAFTING, exp, true);
					//a reel of thread accounts for 5 uses
					if (!getOwner().getCache().hasKey("part_reel_thread")) {
						getOwner().getCache().set("part_reel_thread", 1);
					} else {
						int parts = getOwner().getCache().getInt("part_reel_thread");
						if (parts >= 4) {
							getOwner().message("You use up one of your reels of thread");
							getOwner().getInventory().remove(ItemId.THREAD.id(), 1);
							getOwner().getCache().remove("part_reel_thread");
						} else {
							getOwner().getCache().put("part_reel_thread", parts + 1);
						}
					}
				}
			}
		});
	}

	private void useWool(Player player, final Item woolBall, final Item item) {
		int newID;
		switch (ItemId.getById(item.getID())) {
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
		int woolAmount = player.getInventory().countId(woolBall.getID());
		int amuletAmount = player.getInventory().countId(item.getID());

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Craft String Amulet",
			woolAmount < amuletAmount ? woolAmount : amuletAmount, false) {

			public void action() {
				if (getOwner().getInventory().countId(item.getID()) <= 0 || getOwner().getInventory().countId(ItemId.BALL_OF_WOOL.id()) <= 0) {
					interrupt();
					return;
				}
				if (getOwner().getInventory().remove(woolBall) > -1 && getOwner().getInventory().remove(item) > -1) {
					getOwner().message("You put some string on your " + item.getDef(getWorld()).getName().toLowerCase());
					getOwner().getInventory().add(new Item(newId, 1));
				} else {
					interrupt();
					return;
				}
			}
		});
	}

	private boolean useWater(Player player, final Item water, final Item item) {
		int jugID = Formulae.getEmptyJug(water.getID());
		if (jugID == -1) { // This shouldn't happen
			return false;
		}
		// Clay and water is not bowl of water
		if (item.getID() == ItemId.CLAY.id() && water.getID() != ItemId.BOWL_OF_WATER.id()) {
			if (player.getInventory().remove(water) > -1
				&& player.getInventory().remove(item) > -1) {
				message(player, 1200, "You mix the clay and water");
				player.message("You now have some soft workable clay");
				player.getInventory().add(new Item(jugID, 1));
				player.getInventory().add(new Item(ItemId.SOFT_CLAY.id(), 1));
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
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		ItemGemDef gemDef = player.getWorld().getServer().getEntityHandler().getItemGemDef(item1.getID());
		ItemGemDef gemDef2 = player.getWorld().getServer().getEntityHandler().getItemGemDef(item2.getID());
		if (item1.getID() == ItemId.CHISEL.id() && (gemDef != null || gemDef2 != null)) {
			return true;
		} else if (item2.getID() == ItemId.CHISEL.id() && (gemDef != null || gemDef2 != null)) {
			return true;
		} else if (item1.getID() == ItemId.GLASSBLOWING_PIPE.id()) {
			return true;
		} else if (item2.getID() == ItemId.GLASSBLOWING_PIPE.id()) {
			return true;
		} else if (item1.getID() == ItemId.NEEDLE.id()) {
			return true;
		} else if (item2.getID() == ItemId.NEEDLE.id()) {
			return true;
		} else if (item1.getID() == ItemId.BALL_OF_WOOL.id()) {
			return true;
		} else if (item2.getID() == ItemId.BALL_OF_WOOL.id()) {
			return true;
		} else if ((item1.getID() == ItemId.BUCKET_OF_WATER.id() || item1.getID() == ItemId.JUG_OF_WATER.id()) && item2.getID() == ItemId.CLAY.id()) {
			return true;
		} else if ((item2.getID() == ItemId.BUCKET_OF_WATER.id() || item2.getID() == ItemId.JUG_OF_WATER.id()) && item1.getID() == ItemId.CLAY.id()) {
			return true;
		} else
			return item1.getID() == ItemId.MOLTEN_GLASS.id() && item2.getID() == ItemId.LENS_MOULD.id() || item1.getID() == ItemId.LENS_MOULD.id() && item2.getID() == ItemId.MOLTEN_GLASS.id();
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		return craftingTypeChecks(obj, item, player);
	}
}
