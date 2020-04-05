package com.openrsc.server.plugins.skills;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ItemHerbDef;
import com.openrsc.server.external.ItemHerbSecond;
import com.openrsc.server.external.ItemUnIdentHerbDef;
import com.openrsc.server.model.container.CarriedItems;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.util.rsc.MessageType;

import java.util.concurrent.atomic.AtomicReference;

import static com.openrsc.server.plugins.Functions.*;

public class Herblaw implements OpInvTrigger, UseInvTrigger {

	@Override
	public void onOpInv(final Item item, Player player, String command) {
		if (command.equalsIgnoreCase("Identify")) {
			handleHerbIdentify(item, player);
		}
	}

	public boolean blockOpInv(final Item i, Player p, String command) {
		return command.equalsIgnoreCase("Identify");
	}

	private boolean handleHerbIdentify(final Item item, Player player) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return false;
		}
		ItemUnIdentHerbDef herb = item.getUnIdentHerbDef(player.getWorld());
		if (herb == null) {
			return false;
		}
		if (player.getSkills().getLevel(Skills.HERBLAW) < herb.getLevelRequired()) {
			player.playerServerMessage(MessageType.QUEST, "You cannot identify this herb");
			player.playerServerMessage(MessageType.QUEST, "you need a higher herblaw level");
			return false;
		}
		if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != -1) {
			player.message("You need to complete Druidic ritual quest first");
			return false;
		}

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Herblaw Identify Herb", player.getCarriedItems().getInventory().countId(item.getCatalogId()), false) {
			@Override
			public void action() {
				Player owner = getOwner();
				if (owner.getSkills().getLevel(Skills.HERBLAW) < herb.getLevelRequired()) {
					owner.playerServerMessage(MessageType.QUEST, "You cannot identify this herb");
					owner.playerServerMessage(MessageType.QUEST, "you need a higher herblaw level");
					interrupt();
					return;
				}
				if (owner.getQuestStage(Quests.DRUIDIC_RITUAL) != -1) {
					owner.message("You need to complete Druidic ritual quest first");
					interrupt();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& owner.getFatigue() >= owner.MAX_FATIGUE) {
						owner.message("You are too tired to identify this herb");
						interrupt();
						return;
					}
				}
				ItemUnIdentHerbDef herb = item.getUnIdentHerbDef(getWorld());
				Item newItem = new Item(herb.getNewId());
				if (owner.getCarriedItems().remove(item.getCatalogId(),1,false) > -1) {
					owner.getCarriedItems().getInventory().add(newItem,true);
					owner.playerServerMessage(MessageType.QUEST, "This herb is " + newItem.getDef(getWorld()).getName());
					owner.incExp(Skills.HERBLAW, herb.getExp(), true);
				}
				owner.setBusy(false);
			}
		});
		return true;
	}

	@Override
	public void onUseInv(Player player, Item item, Item usedWith) {
		ItemHerbSecond secondDef = null;
		int itemID = item.getCatalogId();
		int usedWithID = usedWith.getCatalogId();
		CarriedItems carriedItems = player.getCarriedItems();

		// Add secondary ingredient
		if ((secondDef = player.getWorld().getServer().getEntityHandler().getItemHerbSecond(itemID, usedWithID)) != null) {
			doHerbSecond(player, item, usedWith, secondDef, false);
		} else if ((secondDef = player.getWorld().getServer().getEntityHandler().getItemHerbSecond(usedWithID, itemID)) != null) {
			doHerbSecond(player, usedWith, item, secondDef, true);

		// Grind ingredient
		} else if (itemID == ItemId.PESTLE_AND_MORTAR.id()) {
			doGrind(player, item, usedWith);
		} else if (usedWithID == ItemId.PESTLE_AND_MORTAR.id()) {
			doGrind(player, usedWith, item);

		// Add herb to vial
		} else if (itemID == ItemId.VIAL.id()) {
			doHerblaw(player, item, usedWith);
		} else if (usedWithID == ItemId.VIAL.id()) {
			doHerblaw(player, usedWith, item);

		// Ogre potion (Watchtower quest)
		} else if (itemID == ItemId.UNFINISHED_OGRE_POTION.id() && usedWithID == ItemId.GROUND_BAT_BONES.id()) {
			makeLiquid(player, usedWith, item, true);
		} else if (itemID == ItemId.GROUND_BAT_BONES.id() && usedWithID == ItemId.UNFINISHED_OGRE_POTION.id()) {
			makeLiquid(player, item, usedWith, false);
		} else if (itemID == ItemId.UNFINISHED_POTION.id() && (usedWithID == ItemId.GROUND_BAT_BONES.id() || usedWithID == ItemId.GUAM_LEAF.id())) {
			makeLiquid(player, item, usedWith, false);
		} else if (usedWithID == ItemId.UNFINISHED_POTION.id() && (itemID == ItemId.GROUND_BAT_BONES.id() || itemID == ItemId.GUAM_LEAF.id())) {
			makeLiquid(player, usedWith, item, true);

		// Explosive compound (Digsite quest)
		} else if (usedWithID == ItemId.NITROGLYCERIN.id() && itemID == ItemId.AMMONIUM_NITRATE.id()
				|| usedWithID == ItemId.AMMONIUM_NITRATE.id() && itemID == ItemId.NITROGLYCERIN.id()) {
			if (player.getSkills().getLevel(Skills.HERBLAW) < 10) {
				player.playerServerMessage(MessageType.QUEST, "You need to have a herblaw level of 10 or over to mix this liquid");
				return;
			}
			if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != -1) {
				player.message("You need to complete Druidic ritual quest first");
				return;
			}
			player.incExp(Skills.HERBLAW, 20, true);
			player.playerServerMessage(MessageType.QUEST, "You mix the nitrate powder into the liquid");
			player.message("It has produced a foul mixture");
			thinkbubble(player, new Item(ItemId.AMMONIUM_NITRATE.id()));
			carriedItems.remove(ItemId.AMMONIUM_NITRATE.id(), 1);
			carriedItems.getInventory().replace(ItemId.NITROGLYCERIN.id(), ItemId.MIXED_CHEMICALS_1.id());
		} else if (usedWithID == ItemId.GROUND_CHARCOAL.id() && itemID == ItemId.MIXED_CHEMICALS_1.id()
				|| usedWithID == ItemId.MIXED_CHEMICALS_1.id() && itemID == ItemId.GROUND_CHARCOAL.id()) {
			if (player.getSkills().getLevel(Skills.HERBLAW) < 10) {
				player.playerServerMessage(MessageType.QUEST, "You need to have a herblaw level of 10 or over to mix this liquid");
				return;
			}
			if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != -1) {
				player.message("You need to complete Druidic ritual quest first");
				return;
			}
			player.incExp(Skills.HERBLAW, 25, true);
			player.playerServerMessage(MessageType.QUEST, "You mix the charcoal into the liquid");
			player.message("It has produced an even fouler mixture");
			thinkbubble(player, new Item(ItemId.GROUND_CHARCOAL.id()));
			carriedItems.remove(ItemId.GROUND_CHARCOAL.id(), 1);
			carriedItems.getInventory().replace(ItemId.MIXED_CHEMICALS_1.id(), ItemId.MIXED_CHEMICALS_2.id());
		} else if (usedWithID == ItemId.ARCENIA_ROOT.id() && itemID == ItemId.MIXED_CHEMICALS_2.id()
				|| usedWithID == ItemId.MIXED_CHEMICALS_2.id() && itemID == ItemId.ARCENIA_ROOT.id()) {
			if (player.getSkills().getLevel(Skills.HERBLAW) < 10) {
				player.playerServerMessage(MessageType.QUEST, "You need to have a herblaw level of 10 or over to mix this liquid");
				return;
			}
			if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != -1) {
				player.message("You need to complete Druidic ritual quest first");
				return;
			}
			player.incExp(Skills.HERBLAW, 30, true);
			player.message("You mix the root into the mixture");
			player.message("You produce a potentially explosive compound...");
			thinkbubble(player, new Item(ItemId.ARCENIA_ROOT.id()));
			carriedItems.remove(ItemId.ARCENIA_ROOT.id(), 1);
			carriedItems.getInventory().replace(ItemId.MIXED_CHEMICALS_2.id(), ItemId.EXPLOSIVE_COMPOUND.id());
			say(player, null, "Excellent this looks just right");

		// Blamish oil (Heroes quest)
		} else if (usedWithID == ItemId.UNFINISHED_HARRALANDER_POTION.id() && itemID == ItemId.BLAMISH_SNAIL_SLIME.id()
				|| usedWithID == ItemId.BLAMISH_SNAIL_SLIME.id() && itemID == ItemId.UNFINISHED_HARRALANDER_POTION.id()) {
			if (player.getSkills().getLevel(Skills.HERBLAW) < 25) {
				player.playerServerMessage(MessageType.QUEST, "You need a herblaw level of 25 to make this potion");
				return;
			}
			if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != -1) {
				player.message("You need to complete Druidic ritual quest first");
				return;
			}
			player.incExp(Skills.HERBLAW, 320, true);
			player.message("You mix the slime into your potion");
			carriedItems.remove(ItemId.UNFINISHED_HARRALANDER_POTION.id(), 1);
			carriedItems.getInventory().replace(ItemId.BLAMISH_SNAIL_SLIME.id(), ItemId.BLAMISH_OIL.id());

		// Snakes weed potion (Legends quest)
		} else if (usedWithID == ItemId.SNAKES_WEED_SOLUTION.id() && itemID == ItemId.ARDRIGAL.id()
				|| usedWithID == ItemId.ARDRIGAL.id() && itemID == ItemId.SNAKES_WEED_SOLUTION.id()) {
			if (player.getSkills().getLevel(Skills.HERBLAW) < 45) {
				player.playerServerMessage(MessageType.QUEST, "You need to have a herblaw level of 45 or over to mix this potion");
				return;
			}
			if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != -1) {
				player.message("You need to complete Druidic ritual quest first");
				return;
			}
			//player needs to have learned secret from gujuo
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 0 && player.getQuestStage(Quests.LEGENDS_QUEST) < 7) {
				player.message("You're not quite sure what effect this will have.");
				player.message("You decide against experimenting.");
				return;
			}
			player.message("You add the Ardrigal to the Snakesweed Solution.");
			player.message("The mixture seems to bubble slightly with a strange effervescence...");
			carriedItems.remove(ItemId.ARDRIGAL.id(), 1);
			carriedItems.getInventory().replace(ItemId.SNAKES_WEED_SOLUTION.id(), ItemId.GUJUO_POTION.id());
		} else if (usedWithID == ItemId.ARDRIGAL_SOLUTION.id() && itemID == ItemId.SNAKE_WEED.id()
				|| usedWithID == ItemId.SNAKE_WEED.id() && itemID == ItemId.ARDRIGAL_SOLUTION.id()) {
			if (player.getSkills().getLevel(Skills.HERBLAW) < 45) {
				player.playerServerMessage(MessageType.QUEST, "You need to have a herblaw level of 45 or over to mix this potion");
				return;
			}
			if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != -1) {
				player.message("You need to complete Druidic ritual quest first");
				return;
			}
			//player needs to have learned secret from gujuo
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 0 && player.getQuestStage(Quests.LEGENDS_QUEST) < 7) {
				player.message("You're not quite sure what effect this will have.");
				player.message("You decide against experimenting.");
				return;
			}
			player.message("You add the Snake Weed to the Ardrigal solution.");
			player.message("The mixture seems to bubble slightly with a strange effervescence...");
			carriedItems.remove(ItemId.SNAKE_WEED.id(), 1);
			carriedItems.getInventory().replace(ItemId.ARDRIGAL_SOLUTION.id(), ItemId.GUJUO_POTION.id());
		}
	}

	public boolean blockUseInv(Player p, Item item, Item usedWith) {
		int itemID = item.getCatalogId();
		int usedWithID = usedWith.getCatalogId();
		if ((p.getWorld().getServer().getEntityHandler().getItemHerbSecond(itemID, usedWithID)) != null
			|| (p.getWorld().getServer().getEntityHandler().getItemHerbSecond(usedWithID, itemID)) != null) {
			return true;
		} else if (itemID == ItemId.PESTLE_AND_MORTAR.id() || usedWithID == ItemId.PESTLE_AND_MORTAR.id()) {
			return true;
		} else if (itemID == ItemId.VIAL.id() || usedWithID == ItemId.VIAL.id()) {
			return true;
		} else if (itemID == ItemId.UNFINISHED_OGRE_POTION.id() && usedWithID == ItemId.GROUND_BAT_BONES.id()
			|| itemID == ItemId.GROUND_BAT_BONES.id() && usedWithID == ItemId.UNFINISHED_OGRE_POTION.id()) {
			return true;
		} else if (itemID == ItemId.UNFINISHED_POTION.id() && (usedWithID == ItemId.GROUND_BAT_BONES.id() || usedWithID == ItemId.GUAM_LEAF.id())
			|| usedWithID == ItemId.UNFINISHED_POTION.id() && (itemID == ItemId.GROUND_BAT_BONES.id() || itemID == ItemId.GUAM_LEAF.id())) {
			return true;
		} else if (usedWithID == ItemId.NITROGLYCERIN.id() && itemID == ItemId.AMMONIUM_NITRATE.id()
				|| usedWithID == ItemId.AMMONIUM_NITRATE.id() && itemID == ItemId.NITROGLYCERIN.id()) {
			return true;
		} else if (usedWithID == ItemId.GROUND_CHARCOAL.id() && itemID == ItemId.MIXED_CHEMICALS_1.id()
				|| usedWithID == ItemId.MIXED_CHEMICALS_1.id() && itemID == ItemId.GROUND_CHARCOAL.id()) {
			return true;
		} else if (usedWithID == ItemId.ARCENIA_ROOT.id() && itemID == ItemId.MIXED_CHEMICALS_2.id()
				|| usedWithID == ItemId.MIXED_CHEMICALS_2.id() && itemID == ItemId.ARCENIA_ROOT.id()) {
			return true;
		} else if (usedWithID == ItemId.UNFINISHED_HARRALANDER_POTION.id() && itemID == ItemId.BLAMISH_SNAIL_SLIME.id()
				|| usedWithID == ItemId.BLAMISH_SNAIL_SLIME.id() && itemID == ItemId.UNFINISHED_HARRALANDER_POTION.id()) {
			return true;
		} else if (usedWithID == ItemId.SNAKES_WEED_SOLUTION.id() && itemID == ItemId.ARDRIGAL.id()
				|| usedWithID == ItemId.ARDRIGAL.id() && itemID == ItemId.SNAKES_WEED_SOLUTION.id()) {
			return true;
		} else if (usedWithID == ItemId.ARDRIGAL_SOLUTION.id() && itemID == ItemId.SNAKE_WEED.id()
				|| usedWithID == ItemId.SNAKE_WEED.id() && itemID == ItemId.ARDRIGAL_SOLUTION.id()) {
			return true;
		}
		return false;
	}

	private boolean doHerblaw(Player player, final Item vial,
							  final Item herb) {
		int vialID = vial.getCatalogId();
		int herbID = herb.getCatalogId();
		CarriedItems carriedItems = player.getCarriedItems();
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return false;
		}
		if (vialID == ItemId.VIAL.id() && herbID == ItemId.GROUND_BAT_BONES.id()) {
			player.message("You mix the ground bones into the water");
			player.message("Fizz!!!");
			say(player, null, "Oh dear, the mixture has evaporated!",
				"It's useless...");
			carriedItems.remove(vialID, 1);
			carriedItems.remove(herbID, 1);
			carriedItems.getInventory().add(new Item(ItemId.EMPTY_VIAL.id(), 1));
			return false;
		}
		if (vialID == ItemId.VIAL.id() && herbID == ItemId.JANGERBERRIES.id()) {
			player.message("You mix the berries into the water");
			carriedItems.remove(vialID, 1);
			carriedItems.remove(herbID, 1);
			carriedItems.getInventory().add(new Item(ItemId.UNFINISHED_POTION.id(), 1));
			return false;
		}
		if (vialID == ItemId.VIAL.id() && herbID == ItemId.ARDRIGAL.id()) {
			player.message("You put the ardrigal herb into the watervial.");
			player.message("You make a solution of Ardrigal.");
			carriedItems.remove(vialID, 1);
			carriedItems.remove(herbID, 1);
			carriedItems.getInventory().add(new Item(ItemId.ARDRIGAL_SOLUTION.id(), 1));
			return false;
		}
		if (vialID == ItemId.VIAL.id() && herbID == ItemId.SNAKE_WEED.id()) {
			player.message("You put the Snake Weed herb into the watervial.");
			player.message("You make a solution of Snake Weed.");
			carriedItems.remove(vialID, 1);
			carriedItems.remove(herbID, 1);
			carriedItems.getInventory().add(new Item(ItemId.SNAKES_WEED_SOLUTION.id(), 1));
			return false;
		}
		final ItemHerbDef herbDef = player.getWorld().getServer().getEntityHandler().getItemHerbDef(herbID);
		if (herbDef == null) {
			return false;
		}
		int repeatTimes = 1;
		boolean allowDuplicateEvents = true;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			repeatTimes = Math.min(player.getCarriedItems().getInventory().countId(vialID),
				player.getCarriedItems().getInventory().countId(herbID));
			allowDuplicateEvents = false;
		}
		player.setBatchEvent(new BatchEvent(player.getWorld(), player,
			player.getWorld().getServer().getConfig().GAME_TICK,
			"Herblaw Make Potion", repeatTimes, false, allowDuplicateEvents) {
			@Override
			public void action() {
				Player owner = getOwner();
				CarriedItems ownerItems = owner.getCarriedItems();
				if (owner.getSkills().getLevel(Skills.HERBLAW) < herbDef.getReqLevel()) {
					owner.playerServerMessage(MessageType.QUEST, "you need level " + herbDef.getReqLevel()
						+ " herblaw to make this potion");
					interrupt();
					return;
				}
				if (owner.getQuestStage(Quests.DRUIDIC_RITUAL) != -1) {
					owner.message("You need to complete Druidic ritual quest first");
					interrupt();
					return;
				}
				if (ownerItems.hasCatalogID(vialID)
					&& ownerItems.hasCatalogID(herbID)) {
					ownerItems.remove(vialID, 1);
					ownerItems.remove(herbID, 1);
					owner.playSound("mix");
					owner.playerServerMessage(MessageType.QUEST, "You put the " + herb.getDef(getWorld()).getName()
						+ " into the vial of water");
					ownerItems.getInventory().add(
						new Item(herbDef.getPotionId(), 1));
				} else {
					interrupt();
				}
			}
		});
		return true;
	}

	private boolean doHerbSecond(Player player, final Item second,
								 final Item unfinished, final ItemHerbSecond def, final boolean isSwapped) {
		int secondID = second.getCatalogId();
		int unfinishedID = unfinished.getCatalogId();
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return false;
		}
		if (unfinishedID != def.getUnfinishedID()) {
			return false;
		}
		final AtomicReference<Item> bubbleItem = new AtomicReference<Item>();
		bubbleItem.set(null);
		// Shaman potion constraint
		if (secondID == ItemId.JANGERBERRIES.id() && unfinishedID == ItemId.UNFINISHED_GUAM_POTION.id() &&
			(player.getQuestStage(Quests.WATCHTOWER) >= 0 && player.getQuestStage(Quests.WATCHTOWER) < 6)) {
			say(player, null, "Hmmm...perhaps I shouldn't try and mix these items together",
				"It might have unpredictable results...");
			return false;
		} else if (secondID == ItemId.JANGERBERRIES.id() && unfinishedID == ItemId.UNFINISHED_GUAM_POTION.id()) {
			if (!isSwapped) {
				bubbleItem.set(unfinished);
			} else {
				bubbleItem.set(second);
			}
		}
		int repeatTimes = 1;
		boolean allowDuplicateEvents = true;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			repeatTimes = Math.min(player.getCarriedItems().getInventory().countId(secondID),
				player.getCarriedItems().getInventory().countId(unfinishedID));
			allowDuplicateEvents = false;
		}
		player.setBatchEvent(new BatchEvent(player.getWorld(), player,
			player.getWorld().getServer().getConfig().GAME_TICK,
			"Herblaw Make Potion", repeatTimes, false, allowDuplicateEvents) {
			@Override
			public void action() {
				Player owner = getOwner();
				if (owner.getSkills().getLevel(Skills.HERBLAW) < def.getReqLevel()) {
					owner.playerServerMessage(MessageType.QUEST, "You need a herblaw level of "
						+ def.getReqLevel() + " to make this potion");
					interrupt();
					return;
				}
				if (owner.getQuestStage(Quests.DRUIDIC_RITUAL) != -1) {
					owner.message("You need to complete Druidic ritual quest first");
					interrupt();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& owner.getFatigue() >= owner.MAX_FATIGUE) {
						owner.message("You are too tired to make this potion");
						interrupt();
						return;
					}
				}
				CarriedItems carriedItems = owner.getCarriedItems();
				if (carriedItems.hasCatalogID(secondID)
					&& carriedItems.hasCatalogID(unfinishedID)) {
					if (bubbleItem.get() != null) {
						thinkbubble(owner, bubbleItem.get());
					}
					owner.playSound("mix");
					owner.playerServerMessage(MessageType.QUEST, "You mix the " + second.getDef(getWorld()).getName()
						+ " into your potion");
					carriedItems.remove(secondID, 1);
					carriedItems.remove(unfinishedID, 1);
					carriedItems.getInventory().add(new Item(def.getPotionID(), 1));
					owner.incExp(Skills.HERBLAW, def.getExp(), true);
				} else
					interrupt();
			}
		});
		return false;
	}

	private boolean makeLiquid(Player player, final Item ingredient,
							   final Item unfinishedPot, final boolean isSwapped) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return false;
		}

		int unfinishedPotID = unfinishedPot.getCatalogId();
		int ingredientID = ingredient.getCatalogId();
		CarriedItems carriedItems = player.getCarriedItems();
		if (unfinishedPotID == ItemId.UNFINISHED_POTION.id() && (ingredientID == ItemId.GROUND_BAT_BONES.id() || ingredientID == ItemId.GUAM_LEAF.id())
			|| ingredientID == ItemId.UNFINISHED_POTION.id() && (unfinishedPotID == ItemId.GROUND_BAT_BONES.id() || unfinishedPotID == ItemId.GUAM_LEAF.id())) {
			player.playerServerMessage(MessageType.QUEST, "You mix the liquid with the " + ingredient.getDef(player.getWorld()).getName().toLowerCase());
			player.message("Bang!!!");
			displayTeleportBubble(player, player.getX(), player.getY(), true);
			player.damage(8);
			say(player, null, "Ow!");
			player.playerServerMessage(MessageType.QUEST, "You mixed this ingredients incorrectly and the mixture exploded!");
			carriedItems.remove(unfinishedPotID, 1);
			carriedItems.remove(ingredientID, 1);
			carriedItems.getInventory().add(new Item(ItemId.EMPTY_VIAL.id(), 1));
			return false;
		}
		if (unfinishedPotID == ItemId.UNFINISHED_OGRE_POTION.id() && ingredientID == ItemId.GROUND_BAT_BONES.id()
			|| unfinishedPotID == ItemId.GROUND_BAT_BONES.id() && ingredientID == ItemId.UNFINISHED_OGRE_POTION.id()) {
			if (player.getSkills().getLevel(Skills.HERBLAW) < 14) {
				player.playerServerMessage(MessageType.QUEST,
					"You need to have a herblaw level of 14 or over to mix this liquid");
				return false;
			}
			if (player.getQuestStage(Quests.DRUIDIC_RITUAL) != -1) {
				player.message("You need to complete Druidic ritual quest first");
				return false;
			}
			if (player.getQuestStage(Quests.WATCHTOWER) >= 0 && player.getQuestStage(Quests.WATCHTOWER) < 6) {
				say(player, null, "Hmmm...perhaps I shouldn't try and mix these items together",
					"It might have unpredictable results...");
				return false;
			} else if (carriedItems.hasCatalogID(ingredientID)
				&& carriedItems.hasCatalogID(unfinishedPotID)) {
				if (!isSwapped) {
					thinkbubble(player, unfinishedPot);
				} else {
					thinkbubble(player, ingredient);
				}
				player.playerServerMessage(MessageType.QUEST,
					"You mix the " + ingredient.getDef(player.getWorld()).getName().toLowerCase() + " into the liquid");
				player.playerServerMessage(MessageType.QUEST, "You produce a strong potion");
				carriedItems.remove(ingredientID, 1);
				carriedItems.remove(unfinishedPotID, 1);
				carriedItems.getInventory().add(new Item(ItemId.OGRE_POTION.id(), 1));
				//the other half has been done already
				player.incExp(Skills.HERBLAW, 100, true);
			}
		}
		return false;
	}

	private boolean doGrind(Player player, final Item mortar,
							final Item item) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return false;
		}
		int newID;
		switch (ItemId.getById(item.getCatalogId())) {
			case UNICORN_HORN:
				newID = ItemId.GROUND_UNICORN_HORN.id();
				break;
			case BLUE_DRAGON_SCALE:
				newID = ItemId.GROUND_BLUE_DRAGON_SCALE.id();
				break;
			/**
			 * Quest items.
			 */
			case BAT_BONES:
				newID = ItemId.GROUND_BAT_BONES.id();
				break;
			case A_LUMP_OF_CHARCOAL:
				newID = ItemId.GROUND_CHARCOAL.id();
				player.message("You grind the charcoal to a powder");
				break;
			case CHOCOLATE_BAR:
				newID = ItemId.CHOCOLATE_DUST.id();
				break;
			/**
			 * End of Herblaw Quest Items.
			 */
			default:
				return false;
		}
		player.setBatchEvent(new BatchEvent(player.getWorld(), player,
			player.getWorld().getServer().getConfig().GAME_TICK,
			"Herblaw Grind", player.getCarriedItems().getInventory().countId(item.getCatalogId()), false) {
			@Override
			public void action() {
				if (getOwner().getCarriedItems().remove(item) > -1) {
					if (item.getCatalogId() != ItemId.A_LUMP_OF_CHARCOAL.id()) {
						getOwner().playerServerMessage(MessageType.QUEST, "You grind the " + item.getDef(getWorld()).getName()
							+ " to dust");
					}
					thinkbubble(getOwner(), new Item(ItemId.PESTLE_AND_MORTAR.id()));
					getOwner().getCarriedItems().getInventory().add(new Item(newID, 1));

				}
			}
		});
		return true;
	}
}
