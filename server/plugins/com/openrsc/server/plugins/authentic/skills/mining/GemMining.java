package com.openrsc.server.plugins.authentic.skills.mining;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.SceneryId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.content.EnchantedCrowns;
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

import static com.openrsc.server.plugins.Functions.*;

public class GemMining implements OpLocTrigger, UseLocTrigger {

	private static final int GEM_ROCK = 588;

	private static final int[] gemWeightsWithoutDragonstone = {64, 32, 16, 8, 3, 3, 2};
	private static final int[] gemWeightsWithDragonstone = {60, 30, 15, 9, 5, 5, 4};
	private static final int[] gemIds = {
		ItemId.UNCUT_OPAL.id(),
		ItemId.UNCUT_JADE.id(),
		ItemId.UNCUT_RED_TOPAZ.id(),
		ItemId.UNCUT_SAPPHIRE.id(),
		ItemId.UNCUT_EMERALD.id(),
		ItemId.UNCUT_RUBY.id(),
		ItemId.UNCUT_DIAMOND.id()
	};

	private void handleGemRockMining(final GameObject obj, Player player, int click) {
		final ObjectMiningDef def = player.getWorld().getServer().getEntityHandler().getObjectMiningDef(obj.getID());
		final int axeId = Mining.getAxe(player);
		final int mineLvl = player.getSkills().getLevel(Skill.MINING.id());
		int repeat = 1;
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

		/*if (player.isBusy()) {
			return;
		}*/

		if (!player.withinRange(obj, 1)) {
			return;
		}

		if (player.click == 1) {
			player.playSound("prospect");
			player.playerServerMessage(MessageType.QUEST, "You examine the rock for ores...");
			delay(3);
			if (obj.getID() != GEM_ROCK) {
				player.playerServerMessage(MessageType.QUEST, "You fail to find anything interesting");
			}
			// Before the fatigue system (13 November 2002) it was possible to fail prospecting
			// which could happen based on "some chance" when the player had the level to mine the rock
			// and always failed when the player did not meet the level to mine the rock
			// here we set it as config option
			else if (player.getConfig().CAN_PROSPECT_FAIL
				&& (DataConversions.random(0, 3) != 1 || reqlvl > mineLvl)) {
				player.playerServerMessage(MessageType.QUEST, "You fail to find any ore in the rock");
			}
			else {
				player.playerServerMessage(MessageType.QUEST, "This rock contains gems");
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

		if (player.click == 0 && (obj.getID() != GEM_ROCK)) {
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

		if (config().BATCH_PROGRESSION) {
			repeat = Formulae.getRepeatTimes(player, Skill.MINING.id());
		}

		startbatch(repeat);
		batchMining(player, obj, axeId, mineLvl);
	}

	private void batchMining(Player player, GameObject obj, int axeId, int mineLvl) {
		player.playSound("mine");
		int pickBubbleId = player.getClientLimitations().supportsTypedPickaxes ? ItemId.IRON_PICKAXE.id() : ItemId.BRONZE_PICKAXE.id();
		thinkbubble(new Item(pickBubbleId));
		player.playerServerMessage(MessageType.QUEST, "You have a swing at the rock!");
		delay(3);
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				// authentically on fatigued, shows pickaxe that would have been used
				thinkbubble(new Item(axeId));
				player.playerServerMessage(MessageType.QUEST, "You are too tired to mine this rock");
				return;
			}
		}
		if (getGem(player, 40, player.getSkills().getLevel(Skill.MINING.id()), axeId) && mineLvl >= 40) { // always 40 required mining.
			Item gem = new Item(getGemFormula(player.getCarriedItems().getEquipment().hasEquipped(ItemId.CHARGED_DRAGONSTONE_AMULET.id())), 1);
			//check if there is still gem at the rock
			GameObject object = player.getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
			if (!player.getConfig().SHARED_GATHERING_RESOURCES || object != null) {
				player.message(minedString(gem.getCatalogId()));
				player.incExp(Skill.MINING.id(), 260, true); // always 65XP
				player.getCarriedItems().getInventory().add(gem);

				if (EnchantedCrowns.shouldActivate(player, ItemId.CROWN_OF_THE_ITEMS)) {
					player.playerServerMessage(MessageType.QUEST, "Your crown shines and an extra item appears on the ground");
					player.getWorld().registerItem(
						new GroundItem(player.getWorld(), gem.getCatalogId(), player.getX(), player.getY(), 1, player), player.getConfig().GAME_TICK * 50);
					EnchantedCrowns.useCharge(player, ItemId.CROWN_OF_THE_ITEMS);
				}
			} else {
				player.playerServerMessage(MessageType.QUEST, "You only succeed in scratching the rock");
			}

			if (!config().MINING_ROCKS_EXTENDED || DataConversions.random(1, 100) <= 39) {
				if (object != null && object.getID() == obj.getID()) {
					changeloc(obj, 120 * 1000, SceneryId.ROCK_GENERIC.id()); // 2 minute respawn time
				}
				return;
			}
		} else {
			player.playerServerMessage(MessageType.QUEST, "You only succeed in scratching the rock");
			if (!isbatchcomplete()) {
				GameObject checkObj = player.getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
				if (checkObj == null) {
					return;
				}
			}
		}

		GameObject objRock = player.getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
		if(objRock == null) {
			// There is no more ore in the rock, end batch
			stopbatch();
			return;
		}

		// Repeat
		updatebatch();
		boolean customBatch = config().BATCH_PROGRESSION;
		if (!isbatchcomplete()) {
			if (!customBatch || !ifinterrupted()) {
				batchMining(player, obj, axeId, mineLvl);
			}
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == GEM_ROCK && (command.equals("mine") || command.equals("prospect"));
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == GEM_ROCK && (command.equals("mine") || command.equals("prospect"))) {
			if (command.equals("mine") && player.getConfig().GATHER_TOOL_ON_SCENERY) {
				player.playerServerMessage(MessageType.QUEST, "You need to use the pickaxe on the rock to mine it");
				return;
			}
			handleGemRockMining(obj, player, player.click);
		}
	}

	private int calcAxeBonus(int axeId) { // No evidence wielding different pickaxes gives a bonus, only more swings
		/*switch (axeId) {
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
		}*/
		return 0;
	}

	private boolean getGem(Player player, int req, int miningLevel, int axeId) {
		return Formulae.calcGatheringSuccessfulLegacy(req, miningLevel, calcAxeBonus(axeId));
	}

	/**
	 * Returns a gem ID
	 */
	private int getGemFormula(boolean dragonstoneAmmy) {
		return dragonstoneAmmy ?
			Formulae.weightedRandomChoice(gemIds, gemWeightsWithDragonstone) :
			Formulae.weightedRandomChoice(gemIds, gemWeightsWithoutDragonstone);
	}

	private String minedString(int gemID) {
		if (gemID == ItemId.UNCUT_OPAL.id()) {
			return "You just mined an Opal!";
		} else if (gemID == ItemId.UNCUT_JADE.id()) {
			return "You just mined a piece of Jade!";
		} else if (gemID == ItemId.UNCUT_RED_TOPAZ.id()) {
			return "You just mined a Red Topaz!";
		} else if (gemID == ItemId.UNCUT_SAPPHIRE.id()) {
			return "You just found a sapphire!";
		} else if (gemID == ItemId.UNCUT_EMERALD.id()) {
			return "You just found an emerald!";
		} else if (gemID == ItemId.UNCUT_RUBY.id()) {
			return "You just found a ruby!";
		} else if (gemID == ItemId.UNCUT_DIAMOND.id()) {
			return "You just found a diamond!";
		}
		return null;
	}

	@Override
	public void onUseLoc(Player player, GameObject object, Item item) {
		if (inArray(item.getCatalogId(), Formulae.miningAxeIDs) && (player.getConfig().GATHER_TOOL_ON_SCENERY || !player.getClientLimitations().supportsClickMine)
			&& object.getID() == GEM_ROCK) {
			player.click = 0;
			handleGemRockMining(object, player, 0);
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (inArray(item.getCatalogId(), Formulae.miningAxeIDs) && (player.getConfig().GATHER_TOOL_ON_SCENERY || !player.getClientLimitations().supportsClickMine)
			&& obj.getID() == GEM_ROCK);
	}
}
