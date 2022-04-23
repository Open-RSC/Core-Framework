package com.openrsc.server.plugins.authentic.skills.mining;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.SceneryId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.content.EnchantedCrowns;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.external.GameObjectDef;
import com.openrsc.server.external.ObjectMiningDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class Mining implements OpLocTrigger, UseLocTrigger {

	public static int getAxe(Player player) {
		int lvl = player.getSkills().getLevel(Skill.MINING.id());
		for (int i = 0; i < Formulae.miningAxeIDs.length; i++) {
			if (player.getCarriedItems().getEquipment().hasCatalogID(Formulae.miningAxeIDs[i])) {
				return Formulae.miningAxeIDs[i];
			}
			if (player.getCarriedItems().getInventory().countId(Formulae.miningAxeIDs[i], Optional.of(false)) > 0) {
				if (lvl >= Formulae.miningAxeLvls[i]) {
					return Formulae.miningAxeIDs[i];
				}
			}
		}
		return -1;
	}

	@Override
	public void onOpLoc(Player player, final GameObject object, String command) {
		if ((command.equals("mine") || command.equals("prospect"))
			&& object.getID() != 588 && object.getID() != 1227) {
			if (command.equals("mine") && player.getConfig().GATHER_TOOL_ON_SCENERY) {
				player.playerServerMessage(MessageType.QUEST, "You need to use the pickaxe on the rock to mine it");
				return;
			}
			handleMiningEntry(player, object, command);
		}
	}

	private void handleMiningEntry(Player player, final GameObject object, String command) {
		if (object.getID() == 269) {
			if (command.equalsIgnoreCase("mine")) {
				if (player.getCarriedItems().hasCatalogID(getAxe(player), Optional.of(false))) {
					if (getCurrentLevel(player, Skill.MINING.id()) >= 50) {
						player.message("you manage to dig a way through the rockslide");
						if (player.getX() <= 425) {
							player.teleport(428, 438);
						} else {
							player.teleport(425, 438);
						}
					} else {
						player.playerServerMessage(MessageType.QUEST, "You need a mining level of 50 to clear the rockslide");
					}
				} else {
					player.playerServerMessage(MessageType.QUEST, "you need a pickaxe to clear the rockslide");
				}
			} else if (command.equalsIgnoreCase("prospect")) {
				player.playerServerMessage(MessageType.QUEST, "these rocks contain nothing interesting");
				player.playerServerMessage(MessageType.QUEST, "they are just in the way");
			}
		} else if (object.getID() == 770) {
			if (player.getCarriedItems().hasCatalogID(getAxe(player), Optional.of(false))) {
				mes("you mine the rock");
				delay(3);
				mes("and break of several large chunks");
				delay(3);
				give(player, ItemId.ROCKS.id(), 1);
			} else {
				player.message("you need a pickaxe to mine this rock");
			}
		} else if (object.getID() == 1026) { // watchtower - rock of dalgroth
			if (command.equalsIgnoreCase("mine")) {
				if (player.getQuestStage(Quests.WATCHTOWER) == 9) {
					if (!player.getCarriedItems().hasCatalogID(getAxe(player), Optional.of(false))) {
						player.playerServerMessage(MessageType.QUEST, "You need a pickaxe to mine the rock");
						return;
					}
					if (getCurrentLevel(player, Skill.MINING.id()) < 40) {
						player.playerServerMessage(MessageType.QUEST, "You need a mining level of 40 to mine this crystal out");
						return;
					}
					if (player.getCarriedItems().hasCatalogID(ItemId.POWERING_CRYSTAL4.id(), Optional.empty())) {
						say(player, null, "I already have this crystal",
							"There is no benefit to getting another");
						return;
					}
					player.playSound("mine");
					// special bronze pick bubble for rock of dalgroth - see wiki
					thinkbubble(new Item(ItemId.BRONZE_PICKAXE.id()));
					player.message("You have a swing at the rock!");
					player.playerServerMessage(MessageType.QUEST, "You swing your pick at the rock...");
					player.message("A crack appears in the rock and you prize a crystal out");
					give(player, ItemId.POWERING_CRYSTAL4.id(), 1);
				} else {
					say(player, null, "I can't touch it...",
						"Perhaps it is linked with the shaman some way ?");
				}
			} else if (command.equalsIgnoreCase("prospect")) {
				player.playSound("prospect");
				player.playerServerMessage(MessageType.QUEST, "You examine the rock for ores...");
				player.playerServerMessage(MessageType.QUEST, "This rock contains a crystal!");
			}
		} else {
			handleMining(object, player, player.click);
		}
	}

	private void handleMining(final GameObject rock, Player player, int click) {

		if (!player.withinRange(rock, 1)) {
			return;
		}

		final ObjectMiningDef def = player.getWorld().getServer().getEntityHandler().getObjectMiningDef(rock.getID());
		final int axeId = getAxe(player);
		int repeat = 1;
		final int mineLvl = player.getSkills().getLevel(Skill.MINING.id());
		final int mineXP = player.getSkills().getExperience(Skill.MINING.id());
		int reqlvl = 1;
		switch (ItemId.getById(axeId)) {
			case IRON_PICKAXE:
				repeat = 2;
				break;
			case STEEL_PICKAXE:
				repeat = 3;
				reqlvl = 6;
				break;
			case MITHRIL_PICKAXE:
				repeat = 5;
				reqlvl = 21;
				break;
			case ADAMANTITE_PICKAXE:
				repeat = 8;
				reqlvl = 31;
				break;
			case RUNE_PICKAXE:
				repeat = 12;
				reqlvl = 41;
				break;
		}

		if (player.click == 1) {
			player.playSound("prospect");
			player.playerServerMessage(MessageType.QUEST, "You examine the rock for ores...");
			delay(3);
			if (rock.getID() == 496) {
				// Tutorial Island rock handler
				mes("This rock contains " + new Item(def.getOreId()).getDef(player.getWorld()).getName(),
						"Sometimes you won't find the ore but trying again may find it",
						"If a rock contains a high level ore",
						"You will not find it until you increase your mining level");
				if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 49)
					player.getCache().set("tutorial", 50);
			} else {
				if (def == null || def.getRespawnTime() < 1) {
					player.playerServerMessage(MessageType.QUEST, "You fail to find anything interesting");
				}
				// Before the fatigue system (13 November 2002) it was possible to fail prospecting
				// which could happen based on "some chance" when the player had the level to mine the rock
				// and always failed when the player did not meet the level to mine the rock
				// here we set it as config option
				else if (player.getConfig().CAN_PROSPECT_FAIL
					&& (DataConversions.random(0, 3) != 1 || reqlvl > mineLvl)) {
					player.playerServerMessage(MessageType.QUEST, "You fail to find any ore in the rock");
				} else {
					player.playerServerMessage(MessageType.QUEST, "This rock contains " + new Item(def.getOreId()).getDef(player.getWorld()).getName());
				}
			}
			return;
		}

		if (axeId < 0 || reqlvl > mineLvl) {
			mes("You need a pickaxe to mine this rock");
			delay(3);
			mes("You do not have a pickaxe which you have the mining level to use");
			delay(3);
			return;
		}
		if (player.click == 0 && (def == null || (def.getRespawnTime() < 1 && rock.getID() != 496) || (def.getOreId() == 315 && player.getQuestStage(Quests.FAMILY_CREST) < 6))) {
			player.playSound("mine");
			int pickBubbleId = player.getClientLimitations().supportsTypedPickaxes ? ItemId.IRON_PICKAXE.id() : ItemId.BRONZE_PICKAXE.id();
			thinkbubble(new Item(pickBubbleId)); // authentic to only show the original pickaxe sprite
			player.playerServerMessage(MessageType.QUEST, "You swing your pick at the rock...");
			delay(3);
			player.playerServerMessage(MessageType.QUEST, "There is currently no ore available in this rock");
			return;
		}
		if (config().STOP_SKILLING_FATIGUED >= 1
			&& player.getFatigue() >= player.MAX_FATIGUE) {
			// authentically on fatigued, shows pickaxe that would have been used
			thinkbubble(new Item(axeId));
			player.playerServerMessage(MessageType.QUEST, "You are too tired to mine this rock");
			return;
		}
		if (rock.getID() == 496 && mineXP >= 210) {
			player.message("Thats enough mining for now");
			return;
		}

		if(config().BATCH_PROGRESSION) {
			repeat = Formulae.getRepeatTimes(player, Skill.MINING.id());
		}

		startbatch(repeat);
		batchMining(player, rock, def, axeId, mineLvl);
	}

	private void batchMining(Player player, GameObject rock, ObjectMiningDef def, int axeId, int mineLvl) {
		player.playSound("mine");
		int pickBubbleId = player.getClientLimitations().supportsTypedPickaxes ? ItemId.IRON_PICKAXE.id() : ItemId.BRONZE_PICKAXE.id();
		thinkbubble(new Item(pickBubbleId)); // authentic to only show the original pickaxe sprite
		player.playerServerMessage(MessageType.QUEST, "You swing your pick at the rock...");
		delay(3);

		final Item ore = new Item(def.getOreId());
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				// authentically on fatigued, shows pickaxe that would have been used
				thinkbubble(new Item(axeId));
				player.playerServerMessage(MessageType.QUEST, "You are too tired to mine this rock");
				return;
			}
		}
		if (getOre(def, player.getSkills().getLevel(Skill.MINING.id()), axeId) && mineLvl >= def.getReqLevel()) {
			if (DataConversions.random(1, 200) <= (player.getCarriedItems().getEquipment().hasEquipped(ItemId.CHARGED_DRAGONSTONE_AMULET.id()) ? 2 : 1)) {
				player.playSound("foundgem");
				Item gem = new Item(getGem(), 1);
				player.getCarriedItems().getInventory().add(gem);
				player.playerServerMessage(MessageType.QUEST, "You just found a" + gem.getDef(player.getWorld()).getName().toLowerCase().replaceAll("uncut", "") + "!");
				return;
			} else {
				GameObject obj = player.getViewArea().getGameObject(rock.getID(), rock.getX(), rock.getY());
				if (!player.getConfig().SHARED_GATHERING_RESOURCES || obj != null) {
					// Successful mining attempt
					// It is authentic to allow multiple players to get the rock if they have already started mining it.
					// In retro mechanic, if other player had depleted it you would not get it
					// In both cases if there is no ore in the rock, there will be no retry
					if (SkillCapes.shouldActivate(player, ItemId.MINING_CAPE)) {
						thinkbubble(new Item(ItemId.MINING_CAPE.id(), 1));
						player.playerServerMessage(MessageType.QUEST, "You manage to obtain two " + ore.getDef(player.getWorld()).getName().toLowerCase());
						if (ore.getCatalogId() == ItemId.CLAY.id()
							&& EnchantedCrowns.shouldActivate(player, ItemId.CROWN_OF_DEW)) {
							player.playerServerMessage(MessageType.QUEST, "Your crown shines and the clay softens");
							give(player, ItemId.SOFT_CLAY.id(), 1);
							EnchantedCrowns.useCharge(player, ItemId.CROWN_OF_DEW);
						} else {
							give(player, ore.getCatalogId(), 1);
						}
						player.incExp(Skill.MINING.id(), def.getExp() * 2, true);
						give(player, ore.getCatalogId(), 1);
					} else {
						if (ore.getCatalogId() == ItemId.CLAY.id()
							&& EnchantedCrowns.shouldActivate(player, ItemId.CROWN_OF_DEW)) {
							player.playerServerMessage(MessageType.QUEST, "Your crown shines and the clay softens");
							player.getCarriedItems().getInventory().add(new Item(ItemId.SOFT_CLAY.id(), 1));
							EnchantedCrowns.useCharge(player, ItemId.CROWN_OF_DEW);
						} else {
							player.getCarriedItems().getInventory().add(ore);
						}
						player.playerServerMessage(MessageType.QUEST, "You manage to obtain some " + ore.getDef(player.getWorld()).getName().toLowerCase());
						player.incExp(Skill.MINING.id(), def.getExp(), true);

						if (EnchantedCrowns.shouldActivate(player, ItemId.CROWN_OF_THE_ITEMS)) {
							player.playerServerMessage(MessageType.QUEST, "Your crown shines and an extra item appears on the ground");
							player.getWorld().registerItem(
								new GroundItem(player.getWorld(), ore.getCatalogId(), player.getX(), player.getY(), 1, player), player.getConfig().GAME_TICK * 50);
							EnchantedCrowns.useCharge(player, ItemId.CROWN_OF_THE_ITEMS);
						}
					}
				} else {
					player.playerServerMessage(MessageType.QUEST, "You only succeed in scratching the rock");
				}
				if (rock.getID() == 496 && player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 51) {
					player.getCache().set("tutorial", 52);
				}
				if (!config().MINING_ROCKS_EXTENDED || DataConversions.random(1, 100) <= def.getDepletion()) {
					if (def.getRespawnTime() > 0) {
						changeloc(rock, def.getRespawnTime() * 1000, SceneryId.ROCK_GENERIC.id());
					}
					return;
				}
			}
		} else {
			if (rock.getID() == 496) {
				player.playerServerMessage(MessageType.QUEST, "You fail to make any real impact on the rock");
			} else {
				player.playerServerMessage(MessageType.QUEST, "You only succeed in scratching the rock");
				if (!isbatchcomplete()) {
					GameObject checkObj = player.getViewArea().getGameObject(rock.getID(), rock.getX(), rock.getY());
					if (checkObj == null) {
						return;
					}
				}
			}
		}

		GameObject obj = player.getViewArea().getGameObject(rock.getID(), rock.getX(), rock.getY());
		if(obj == null) {
			// There is no more ore in the rock, end batch
			stopbatch();
			return;
		}

		// Repeat
		updatebatch();
		boolean customBatch = config().BATCH_PROGRESSION;
		if (!isbatchcomplete()) {
			if (!customBatch || !ifinterrupted()) {
				batchMining(player, rock, def, axeId, mineLvl);
			}
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return (command.equals("mine") || command.equals("prospect"))
			&& obj.getID() != 588 && obj.getID() != 1227;
	}

	/**
	 * Returns a gem ID
	 */
	public int getGem() {
		int rand = DataConversions.random(0, 100);
		if (rand < 10) {
			return ItemId.UNCUT_DIAMOND.id();
		} else if (rand < 30) {
			return ItemId.UNCUT_RUBY.id();
		} else if (rand < 60) {
			return ItemId.UNCUT_EMERALD.id();
		} else {
			return ItemId.UNCUT_SAPPHIRE.id();
		}
	}

	private int calcAxeBonus(int axeId) {
		//If server doesn't use batching, pickaxe shouldn't improve gathering chance
		if (!config().BATCH_PROGRESSION) {
			return 0;
		}
		int bonus = 0;
		switch (ItemId.getById(axeId)) {
			case BRONZE_PICKAXE:
				bonus = 0;
				break;
			case IRON_PICKAXE:
				bonus = 1;
				break;
			case STEEL_PICKAXE:
				bonus = 2;
				break;
			case MITHRIL_PICKAXE:
				bonus = 4;
				break;
			case ADAMANTITE_PICKAXE:
				bonus = 8;
				break;
			case RUNE_PICKAXE:
				bonus = 16;
				break;
		}
		return bonus;
	}

	/**
	 * Should we can get an ore from the rock?
	 */
	private boolean getOre(ObjectMiningDef def, int miningLevel, int axeId) {
		return Formulae.calcGatheringSuccessfulLegacy(def.getReqLevel(), miningLevel, calcAxeBonus(axeId));
	}

	@Override
	public void onUseLoc(Player player, GameObject object, Item item) {
		final GameObjectDef def = player.getWorld().getServer().getEntityHandler().getGameObjectDef(object.getID());
		if (inArray(item.getCatalogId(), Formulae.miningAxeIDs) && (player.getConfig().GATHER_TOOL_ON_SCENERY || !player.getClientLimitations().supportsClickMine)
			&& def != null && def.command1.equalsIgnoreCase("mine") && object.getID() != 588 && object.getID() != 1227) {
			player.click = 0;
			handleMiningEntry(player, object, "mine");
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		final GameObjectDef def = player.getWorld().getServer().getEntityHandler().getGameObjectDef(obj.getID());
		return (inArray(item.getCatalogId(), Formulae.miningAxeIDs) && (player.getConfig().GATHER_TOOL_ON_SCENERY || !player.getClientLimitations().supportsClickMine)
			&& def != null && def.command1.equalsIgnoreCase("mine") && obj.getID() != 588 && obj.getID() != 1227);
	}
}
