package com.openrsc.server.plugins.authentic.skills.fletching;

import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.external.ItemArrowHeadDef;
import com.openrsc.server.external.ItemBowStringDef;
import com.openrsc.server.external.ItemDartTipDef;
import com.openrsc.server.external.ItemLogCutDef;
import com.openrsc.server.model.container.CarriedItems;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Fletching implements UseInvTrigger {

	private static final int[] attachmentIds = {
		ItemId.ARROW_SHAFTS.id(),
		ItemId.BRONZE_DART_TIPS.id(),
		ItemId.IRON_DART_TIPS.id(),
		ItemId.STEEL_DART_TIPS.id(),
		ItemId.MITHRIL_DART_TIPS.id(),
		ItemId.ADAMANTITE_DART_TIPS.id(),
		ItemId.RUNE_DART_TIPS.id(),
	};

	private static final int[] unstrungBows = {
		ItemId.UNSTRUNG_SHORTBOW.id(),
		ItemId.UNSTRUNG_LONGBOW.id(),
		ItemId.UNSTRUNG_OAK_SHORTBOW.id(),
		ItemId.UNSTRUNG_OAK_LONGBOW.id(),
		ItemId.UNSTRUNG_WILLOW_SHORTBOW.id(),
		ItemId.UNSTRUNG_WILLOW_LONGBOW.id(),
		ItemId.UNSTRUNG_MAPLE_SHORTBOW.id(),
		ItemId.UNSTRUNG_MAPLE_LONGBOW.id(),
		ItemId.UNSTRUNG_YEW_SHORTBOW.id(),
		ItemId.UNSTRUNG_YEW_LONGBOW.id(),
		ItemId.UNSTRUNG_MAGIC_SHORTBOW.id(),
		ItemId.UNSTRUNG_MAGIC_LONGBOW.id()
	};

	private static final int[] arrowHeads = {
		ItemId.BRONZE_ARROW_HEADS.id(),
		ItemId.IRON_ARROW_HEADS.id(),
		ItemId.STEEL_ARROW_HEADS.id(),
		ItemId.MITHRIL_ARROW_HEADS.id(),
		ItemId.ADAMANTITE_ARROW_HEADS.id(),
		ItemId.RUNE_ARROW_HEADS.id(),
	};

	private static final int[] logIds = {
		ItemId.LOGS.id(),
		ItemId.OAK_LOGS.id(),
		ItemId.WILLOW_LOGS.id(),
		ItemId.MAPLE_LOGS.id(),
		ItemId.YEW_LOGS.id(),
		ItemId.MAGIC_LOGS.id()
	};

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		int item1ID = item1.getCatalogId();
		int item2ID = item2.getCatalogId();

		// Adding feathers to shafts / darts.
		if (item1ID == ItemId.FEATHER.id() && DataConversions.inArray(attachmentIds, item2.getCatalogId())) {
			return true;
		} else if (item2ID == ItemId.FEATHER.id() && DataConversions.inArray(attachmentIds, item1.getCatalogId())) {
			return true;

			// Adding bow strings to unstrung bows.
		} else if (item1ID == ItemId.BOW_STRING.id() && DataConversions.inArray(unstrungBows, item2.getCatalogId())) {
			return true;
		} else if (item2ID == ItemId.BOW_STRING.id() && DataConversions.inArray(unstrungBows, item1.getCatalogId())) {
			return true;

			// Add arrow heads to headless arrows.
		} else if (item1ID == ItemId.HEADLESS_ARROWS.id() && DataConversions.inArray(arrowHeads, item2.getCatalogId())) {
			return true;
		} else if (item2ID == ItemId.HEADLESS_ARROWS.id() && DataConversions.inArray(arrowHeads, item1.getCatalogId())) {
			return true;

			// Use knife on logs.
		} else if (item1ID == ItemId.KNIFE.id() && DataConversions.inArray(logIds, item2.getCatalogId()) && Skill.FLETCHING.id() != Skill.NONE.id()) {
			return true;
		} else if (item2ID == ItemId.KNIFE.id() && DataConversions.inArray(logIds, item1.getCatalogId()) && Skill.FLETCHING.id() != Skill.NONE.id()) {
			return true;

			// Cut oyster pearls.
		} else if (item1ID == ItemId.CHISEL.id() && (item2.getCatalogId() == ItemId.QUEST_OYSTER_PEARLS.id()
			|| item2.getCatalogId() == ItemId.OYSTER_PEARLS.id())) {
			return true;
		} else if (item2ID == ItemId.CHISEL.id() && (item1.getCatalogId() == ItemId.QUEST_OYSTER_PEARLS.id()
			|| item1.getCatalogId() == ItemId.OYSTER_PEARLS.id())) {
			return true;

			// Add oyster pearl bolt tips to bolts.
		} else if (item1ID == ItemId.OYSTER_PEARL_BOLT_TIPS.id() && item2ID == ItemId.CROSSBOW_BOLTS.id()) {
			return true;
		} else if (item2ID == ItemId.OYSTER_PEARL_BOLT_TIPS.id() && item1ID == ItemId.CROSSBOW_BOLTS.id()) {
			return true;
		}

		return false;
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		int item1ID = item1.getCatalogId();
		int item2ID = item2.getCatalogId();

		// Adding feathers to shafts / darts.
		if (item1ID == ItemId.FEATHER.id() && DataConversions.inArray(attachmentIds, item2.getCatalogId())) {
			attachFeathers(player, item1, item2);
		} else if (item2ID == ItemId.FEATHER.id() && DataConversions.inArray(attachmentIds, item1.getCatalogId())) {
			attachFeathers(player, item2, item1);

			// Adding bow strings to unstrung bows.
		} else if (item1ID == ItemId.BOW_STRING.id() && DataConversions.inArray(unstrungBows, item2.getCatalogId())) {
			doBowString(player, item1, item2);
		} else if (item2ID == ItemId.BOW_STRING.id() && DataConversions.inArray(unstrungBows, item1.getCatalogId())) {
			doBowString(player, item2, item1);

			// Add arrow heads to headless arrows.
		} else if (item1ID == ItemId.HEADLESS_ARROWS.id() && DataConversions.inArray(arrowHeads, item2.getCatalogId())) {
			doArrowHeads(player, item1, item2);
		} else if (item2ID == ItemId.HEADLESS_ARROWS.id() && DataConversions.inArray(arrowHeads, item1.getCatalogId())) {
			doArrowHeads(player, item2, item1);

			// Use knife on logs.
		} else if (item1ID == ItemId.KNIFE.id() && DataConversions.inArray(logIds, item2.getCatalogId()) && Skill.FLETCHING.id() != Skill.NONE.id()) {
			doLogCut(player, item1, item2);
		} else if (item2ID == ItemId.KNIFE.id() && DataConversions.inArray(logIds, item1.getCatalogId()) && Skill.FLETCHING.id() != Skill.NONE.id()) {
			doLogCut(player, item2, item1);

			// Cut oyster pearls.
		} else if (item1ID == ItemId.CHISEL.id() && (item2.getCatalogId() == ItemId.QUEST_OYSTER_PEARLS.id() || item2.getCatalogId() == ItemId.OYSTER_PEARLS.id())) {
			doPearlCut(player, item1, item2);
		} else if (item2ID == ItemId.CHISEL.id() && (item1.getCatalogId() == ItemId.QUEST_OYSTER_PEARLS.id() || item1.getCatalogId() == ItemId.OYSTER_PEARLS.id())) {
			doPearlCut(player, item2, item1);

			// Add oyster pearl bolt tips to bolts.
		} else if (item1ID == ItemId.OYSTER_PEARL_BOLT_TIPS.id() && item2ID == ItemId.CROSSBOW_BOLTS.id()) {
			doBoltMake(player, item2, item1);
		} else if (item2ID == ItemId.OYSTER_PEARL_BOLT_TIPS.id() && item1ID == ItemId.CROSSBOW_BOLTS.id()) {
			doBoltMake(player, item1, item2);
		}
	}

	private void attachFeathers(Player player, final Item feathers, final Item attachment) {
		if (!config().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}

		// Determine EXP based on amount + item
		final int resultID;
		int experience = 4;
		ItemDartTipDef tipDef = null;
		if (attachment.getCatalogId() == ItemId.ARROW_SHAFTS.id()) {
			resultID = ItemId.HEADLESS_ARROWS.id();
		} else if ((tipDef = player.getWorld().getServer().getEntityHandler().getItemDartTipDef(attachment.getCatalogId())) != null) {
			resultID = tipDef.getDartID(); // Dart ID
			experience = tipDef.getExp();
		} else {
			return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = 5;
		}

		startbatch(repeat);
		batchFeathers(player, feathers, attachment, resultID, experience);
	}

	private void batchFeathers(Player player, Item feathers, Item attachment, int resultID, int experience) {
		player.message("You attach feathers to some of your "
			+ attachment.getDef(player.getWorld()).getName());

		ServerConfiguration config = config();
		CarriedItems ci = player.getCarriedItems();
		feathers = ci.getInventory().get(
			ci.getInventory().getLastIndexById(feathers.getCatalogId(), Optional.of(false))
		);
		attachment = ci.getInventory().get(
			ci.getInventory().getLastIndexById(attachment.getCatalogId(), Optional.of(false))
		);
		if (feathers == null || attachment == null) return;
		int loopAmount = Math.min(10, feathers.getAmount());
		loopAmount = Math.min(loopAmount, attachment.getAmount());
		boolean authenticClientUpdates = !config().CUSTOM_IMPROVEMENTS;
		int timesLooped = 0;
		for (int i = 0; i < loopAmount; ++i) {
			if (checkFatigue(player)) {
				return;
			}

			ci.remove(new Item(feathers.getCatalogId(), 1), authenticClientUpdates);
			ci.remove(new Item(attachment.getCatalogId(), 1), authenticClientUpdates);
			ci.getInventory().add(new Item(resultID), authenticClientUpdates);
			if (authenticClientUpdates) {
				player.incExp(Skill.FLETCHING.id(), experience, true);
			}
			timesLooped++;
		}
		if (!authenticClientUpdates) {
			ActionSender.sendInventory(player);
			player.incExp(Skill.FLETCHING.id(), experience * timesLooped, true);
		}
		delay();

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			batchFeathers(player, feathers, attachment, resultID, experience);
		}
	}

	private void doArrowHeads(Player player, final Item headlessArrows, final Item arrowHeads) {
		if (!config().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}
		final ItemArrowHeadDef headDef = player.getWorld().getServer().getEntityHandler()
			.getItemArrowHeadDef(arrowHeads.getCatalogId());
		if (headDef == null) {
			return;
		}

		if (player.getSkills().getLevel(Skill.FLETCHING.id()) < headDef.getReqLevel()) {
			player.message("You need a fletching skill of "
				+ headDef.getReqLevel() + " or above to do that");
			return;
		}

		player.message("You attach "
			+ arrowHeads.getDef(player.getWorld()).getName().toLowerCase()
			+ " to some of your arrows");
		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = 5;
		}

		startbatch(repeat);
		batchArrowheads(player, headlessArrows, arrowHeads, headDef);
	}

	private void batchArrowheads(Player player, Item headlessArrows, Item arrowHeads, ItemArrowHeadDef headDef) {
		if (!canReceive(player, new Item(headDef.getArrowID()))) {
			player.message("Your client does not support the desired object");
			return;
		}

		CarriedItems ci = player.getCarriedItems();
		headlessArrows = ci.getInventory().get(
			ci.getInventory().getLastIndexById(headlessArrows.getCatalogId(), Optional.of(false))
		);
		arrowHeads = ci.getInventory().get(
			ci.getInventory().getLastIndexById(arrowHeads.getCatalogId(), Optional.of(false))
		);
		if (headlessArrows == null || arrowHeads == null) return;
		int loopAmount = Math.min(10, headlessArrows.getAmount());
		loopAmount = Math.min(loopAmount, arrowHeads.getAmount());
		int skillCapeMultiplier = SkillCapes.shouldActivate(player, ItemId.FLETCHING_CAPE) ? 2 : 1;
		boolean authenticClientUpdates = !config().CUSTOM_IMPROVEMENTS;
		int timesLooped = 0;
		for (int i = 0; i < loopAmount; ++i) {
			if (player.getSkills().getLevel(Skill.FLETCHING.id()) < headDef.getReqLevel()) {
				player.message("You need a fletching skill of "
					+ headDef.getReqLevel() + " or above to do that");
				return;
			}
			if (checkFatigue(player)) {
				return;
			}

			ci.remove(new Item(headlessArrows.getCatalogId(), 1), authenticClientUpdates);
			ci.remove(new Item(arrowHeads.getCatalogId(), 1), authenticClientUpdates);
			ci.getInventory().add(new Item(headDef.getArrowID(), skillCapeMultiplier), authenticClientUpdates);

			if (authenticClientUpdates) {
				player.incExp(Skill.FLETCHING.id(), headDef.getExp() * skillCapeMultiplier, true);
			}
			timesLooped++;
		}
		if (!authenticClientUpdates) {
			ActionSender.sendInventory(player);
			player.incExp(Skill.FLETCHING.id(), headDef.getExp() * skillCapeMultiplier * timesLooped, true);
		}
		delay();

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			batchArrowheads(player, headlessArrows, arrowHeads, headDef);
		}
	}

	private void doBowString(Player player, final Item bowString, final Item bow) {
		if (!config().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}
		final ItemBowStringDef stringDef = player.getWorld().getServer().getEntityHandler()
			.getItemBowStringDef(bow.getCatalogId());
		if (stringDef == null) {
			return;
		}
		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			int bowtimes = player.getCarriedItems().getInventory().countId(bow.getCatalogId(), Optional.of(false));
			int stringtimes = player.getCarriedItems().getInventory().countId(bowString.getCatalogId(), Optional.of(false));
			repeat = Math.min(bowtimes, stringtimes);
		}

		startbatch(repeat);
		batchStringing(player, bow, bowString, stringDef);
	}

	private void batchStringing(Player player, Item bow, Item bowString, ItemBowStringDef stringDef) {
		if (!canReceive(player, new Item(stringDef.getBowID()))) {
			player.message("Your client does not support the desired object");
			return;
		}
		if (player.getSkills().getLevel(Skill.FLETCHING.id()) < stringDef.getReqLevel()) {
			player.message("You need a fletching skill of "
				+ stringDef.getReqLevel() + " or above to do that");
			return;
		}
		if (checkFatigue(player)) {
			return;
		}
		bow = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(bow.getCatalogId(), Optional.of(false))
		);
		bowString = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(bowString.getCatalogId(), Optional.of(false))
		);
		if (bow == null || bowString == null) return;

		player.getCarriedItems().remove(bowString);
		player.getCarriedItems().remove(bow);
		player.message("You add a string to the bow");
		player.getCarriedItems().getInventory().add(new Item(stringDef.getBowID(), 1));
		player.incExp(Skill.FLETCHING.id(), stringDef.getExp(), true);
		delay();

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay(2);
			batchStringing(player, bow, bowString, stringDef);
		}
	}

	private void doLogCut(final Player player, final Item knife,
						  final Item log) {
		if (!config().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}
		final ItemLogCutDef cutDef = player.getWorld().getServer().getEntityHandler().getItemLogCutDef(log.getCatalogId());
		if (cutDef == null) {
			return;
		}

		boolean logConfig = config().MORE_SHAFTS_PER_BETTER_LOG;

		player.message("What would you like to make?");

		String[] options = logConfig ? new String[]{"Make arrow shafts", "Make shortbow", "Make longbow"} :
			(log.getCatalogId() == ItemId.LOGS.id() ? new String[]{"Make arrow shafts",
				"Make shortbow", "Make longbow"} : new String[]{"Make shortbow", "Make longbow"});

		int type = multi(player, options);
		if (type < 0 || type > options.length) {
			return;
		}

		if (options.length == 2 && type >= 0) type += 1;

		int reqLvl, exp, amount;
		reqLvl = exp = amount = 0;
		int id = ItemId.NOTHING.id();
		String cutMessage = null;
		switch (type) {
			case 0:
				id = ItemId.ARROW_SHAFTS.id();
				reqLvl = cutDef.getShaftLvl();
				exp = cutDef.getShaftExp();
				cutMessage = "You carefully cut the wood into " + getNumberOfShafts(player, log.getCatalogId())
					+ " arrow shafts";
				break;
			case 1:
				id = cutDef.getShortbowID();
				reqLvl = cutDef.getShortbowLvl();
				exp = cutDef.getShortbowExp();
				cutMessage = "You carefully cut the wood into a shortbow";
				break;
			case 2:
				id = cutDef.getLongbowID();
				reqLvl = cutDef.getLongbowLvl();
				exp = cutDef.getLongbowExp();
				cutMessage = "You carefully cut the wood into a longbow";
				break;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(log.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchLogCutting(player, log, id, reqLvl, exp, cutMessage);
	}

	private void batchLogCutting(Player player, Item log, int id, int reqLvl, int exp, String cutMessage) {
		if (!canReceive(player, new Item(id))) {
			player.message("Your client does not support the desired object");
			return;
		}
		if (player.getSkills().getLevel(Skill.FLETCHING.id()) < reqLvl) {
			player.message("You need a fletching skill of " + reqLvl + " or above to do that");
			return;
		}
		if (checkFatigue(player)) {
			return;
		}
		log = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(log.getCatalogId(), Optional.of(false))
		);
		if (log == null) return;
		if (player.getCarriedItems().remove(log) > -1) {
			player.message(cutMessage);
			give(player, id, id == ItemId.ARROW_SHAFTS.id() ? getNumberOfShafts(player, log.getCatalogId()) : 1);
			player.incExp(Skill.FLETCHING.id(), exp, true);
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay(2);
			batchLogCutting(player, log, id, reqLvl, exp, cutMessage);
		}
	}

	private int getNumberOfShafts(final Player player, final int logId) {

		if (!config().MORE_SHAFTS_PER_BETTER_LOG) return 10;
		for (int i = 0; i < logIds.length; ++i) {
			if (logId == logIds[i]) {
				return 10 + (i * 5);
			}
		}
		return 10;
	}

	private void doPearlCut(final Player player, final Item chisel, final Item pearl) {
		if (!config().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}

		int amount;
		if (pearl.getCatalogId() == ItemId.QUEST_OYSTER_PEARLS.id()) {
			amount = 25;
		} else if (pearl.getCatalogId() == ItemId.OYSTER_PEARLS.id()) {
			amount = 2;
		} else {
			player.message("Nothing interesting happens");
			return;
		}

		final int amt = amount;
		final int exp = 25;
		final int pearlID = pearl.getCatalogId();

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(pearlID, Optional.of(false));
		}

		startbatch(repeat);
		batchPearlCutting(player, pearl, amount);
	}

	private void batchPearlCutting(Player player, Item pearl, int amount) {
		if (player.getSkills().getLevel(Skill.FLETCHING.id()) < 34) {
			player.message("You need a fletching skill of 34 to do that");
			return;
		}
		if (checkFatigue(player)) {
			return;
		}

		pearl = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(pearl.getCatalogId(), Optional.of(false))
		);
		if (pearl == null) return;

		player.getCarriedItems().remove(new Item(pearl.getCatalogId()));
		player.message("you chisel the pearls into small bolt tips");
		give(player, ItemId.OYSTER_PEARL_BOLT_TIPS.id(), amount);
		player.incExp(Skill.FLETCHING.id(), 100, true);
		delay();

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchPearlCutting(player, pearl, amount);
		}
	}

	private void doBoltMake(final Player player, final Item bolts, final Item tips) {
		if (!config().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}

		if (tips.getCatalogId() != ItemId.OYSTER_PEARL_BOLT_TIPS.id()) { // not pearl tips
			player.message("Nothing interesting happens");
			return;
		}

		int repeat = 1; // 1 + 1000 for authentic behaviour
		if (config().BATCH_PROGRESSION) {
			repeat = 5;
		}
		startbatch(repeat);
		batchBolts(player, bolts, tips);
	}

	private void batchBolts(Player player, Item bolts, Item tips) {
		ServerConfiguration config = config();
		CarriedItems ci = player.getCarriedItems();
		bolts = ci.getInventory().get(
			ci.getInventory().getLastIndexById(bolts.getCatalogId(), Optional.of(false))
		);
		tips = ci.getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(tips.getCatalogId(), Optional.of(false))
		);
		if (bolts == null || tips == null) return;
		int loopCount = Math.min(10, bolts.getAmount());
		loopCount = Math.min(loopCount, tips.getAmount());
		int skillCapeMultiplier = SkillCapes.shouldActivate(player, ItemId.FLETCHING_CAPE) ? 2 : 1;
		int timesLooped = 0;
		boolean authenticClientUpdates = !config().CUSTOM_IMPROVEMENTS;
		for (int i = 0; i < loopCount; ++i) {
			if (player.getSkills().getLevel(Skill.FLETCHING.id()) < 34) {
				player.message("You need a fletching skill of 34 to do that");
				return;
			}
			if (checkFatigue(player)) {
				return;
			}
			ci.remove(new Item(bolts.getCatalogId(), 1), authenticClientUpdates);
			ci.remove(new Item(tips.getCatalogId(), 1), authenticClientUpdates);
			ci.getInventory().add(new Item(ItemId.OYSTER_PEARL_BOLTS.id(), skillCapeMultiplier), authenticClientUpdates);
			if (authenticClientUpdates) {
				player.incExp(Skill.FLETCHING.id(), 25 * skillCapeMultiplier, true);
			}
			timesLooped++;
		}
		if (!authenticClientUpdates) {
			ActionSender.sendInventory(player);
			player.incExp(Skill.FLETCHING.id(), 25 * skillCapeMultiplier * timesLooped, true);
		}

		delay();

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			batchBolts(player, bolts, tips);
		}
	}

	private boolean checkFatigue(Player player) {
		if (config().WANT_FATIGUE) {
			if (player.getFatigue() >= player.MAX_FATIGUE) {
				if (config().STOP_SKILLING_FATIGUED >= 2) {
					player.message("You are too tired to train");
					return true;
				}
			}
		}
		return false;
	}
}
