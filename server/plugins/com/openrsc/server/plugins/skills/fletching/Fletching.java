package com.openrsc.server.plugins.skills.fletching;

import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.event.custom.BatchEvent;
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

import java.util.ArrayList;
import java.util.HashMap;
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
		if (item1ID == ItemId.FEATHER.id() && DataConversions.inArray(attachmentIds, item2.getCatalogId())) {// attachFeathers(player, item1, item2)) {
			return true;
		} else if (item2ID == ItemId.FEATHER.id() && DataConversions.inArray(attachmentIds, item1.getCatalogId())) {// attachFeathers(player, item2, item1)) {
			return true;

		// Adding bow strings to unstrung bows.
		} else if (item1ID == ItemId.BOW_STRING.id() && DataConversions.inArray(unstrungBows, item2.getCatalogId())) {// doBowString(player, item1, item2)) {
			return true;
		} else if (item2ID == ItemId.BOW_STRING.id() && DataConversions.inArray(unstrungBows, item1.getCatalogId())) {// doBowString(player, item2, item1)) {
			return true;

		// Add arrow heads to headless arrows.
		} else if (item1ID == ItemId.HEADLESS_ARROWS.id() && DataConversions.inArray(arrowHeads, item2.getCatalogId())) {// doArrowHeads(player, item1, item2)) {
			return true;
		} else if (item2ID == ItemId.HEADLESS_ARROWS.id() && DataConversions.inArray(arrowHeads, item1.getCatalogId())) {// doArrowHeads(player, item2, item1)) {
			return true;

		// Use knife on logs.
		} else if (item1ID == ItemId.KNIFE.id() && DataConversions.inArray(logIds, item2.getCatalogId())) {
			return true;
		} else if (item2ID == ItemId.KNIFE.id() && DataConversions.inArray(logIds, item1.getCatalogId())) {
			return true;

		// Cut oyster pearls.
		} else if (item1ID == ItemId.CHISEL.id() && (item2.getCatalogId() == ItemId.QUEST_OYSTER_PEARLS.id()
			|| item2.getCatalogId() == ItemId.OYSTER_PEARLS.id())) {// && doPearlCut(player, item1, item2)) {
			return true;
		} else if (item2ID == ItemId.CHISEL.id() && (item1.getCatalogId() == ItemId.QUEST_OYSTER_PEARLS.id()
			|| item1.getCatalogId() == ItemId.OYSTER_PEARLS.id())) {// && doPearlCut(player, item2, item1)) {
			return true;

		// Add oyster pearl bolt tips to bolts.
		} else if (item1ID == ItemId.OYSTER_PEARL_BOLT_TIPS.id() && item2ID == ItemId.CROSSBOW_BOLTS.id()) {// && doBoltMake(player, item2, item1)) {
			return true;
		} else if (item2ID == ItemId.OYSTER_PEARL_BOLT_TIPS.id() && item1ID == ItemId.CROSSBOW_BOLTS.id()) {// && doBoltMake(player, item1, item2)) {
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
		} else if (item1ID == ItemId.KNIFE.id() && DataConversions.inArray(logIds, item2.getCatalogId())) {
			doLogCut(player, item1, item2);
		} else if (item2ID == ItemId.KNIFE.id() && DataConversions.inArray(logIds, item1.getCatalogId())) {
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
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
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
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			repeat = 5;
		}

		batchFeathers(player, feathers, attachment, resultID, experience, repeat);
	}

	private void batchFeathers(Player player, Item feathers, Item attachment, int resultID, int experience, int repeat) {
		player.message("You attach feathers to some of your "
			+ attachment.getDef(player.getWorld()).getName());

		ServerConfiguration config = player.getWorld().getServer().getConfig();
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
		for (int i = 0; i < loopAmount; ++i) {

			if (checkFatigue(player)) {
				return;
			}

			ci.remove(new Item(feathers.getCatalogId(), 1));
			ci.remove(new Item(attachment.getCatalogId(), 1));
			ci.getInventory().add(new Item(resultID));
			player.incExp(Skills.FLETCHING, experience, true);
		}

		// Repeat
		if (player.hasMoved()) return;
		repeat--;
		if (repeat > 0) {
			delay(player.getWorld().getServer().getConfig().GAME_TICK);
			batchFeathers(player, feathers, attachment, resultID, experience, repeat);
		}
	}

	private void doArrowHeads(Player player, final Item headlessArrows, final Item arrowHeads) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}
		final ItemArrowHeadDef headDef = player.getWorld().getServer().getEntityHandler()
			.getItemArrowHeadDef(arrowHeads.getCatalogId());
		if (headDef == null) {
			return;
		}

		if (player.getSkills().getLevel(Skills.FLETCHING) < headDef.getReqLevel()) {
			player.message("You need a fletching skill of "
				+ headDef.getReqLevel() + " or above to do that");
			return;
		}

		player.message("You attach "
			+ arrowHeads.getDef(player.getWorld()).getName().toLowerCase()
			+ " to some of your arrows");
		int repeat = 1;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			repeat = 5;
		}

		batchArrowheads(player, headlessArrows, arrowHeads, headDef, repeat);
	}

	private void batchArrowheads(Player player, Item headlessArrows, Item arrowHeads, ItemArrowHeadDef headDef, int repeat) {
		ServerConfiguration config = player.getWorld().getServer().getConfig();
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
		for (int i = 0; i < loopAmount; ++i) {
			if (player.getSkills().getLevel(Skills.FLETCHING) < headDef.getReqLevel()) {
				player.message("You need a fletching skill of "
					+ headDef.getReqLevel() + " or above to do that");
				return;
			}
			if (checkFatigue(player)) {
				return;
			}

			ci.remove(new Item(headlessArrows.getCatalogId(), 1));
			ci.remove(new Item(arrowHeads.getCatalogId(), 1));

			int skillCapeMultiplier = SkillCapes.shouldActivate(player, ItemId.FLETCHING_CAPE) ? 2 : 1;
			ci.getInventory().add(new Item(headDef.getArrowID(), skillCapeMultiplier));
			player.incExp(Skills.FLETCHING, headDef.getExp() * skillCapeMultiplier, true);
		}

		// Repeat
		if (player.hasMoved()) return;
		repeat--;
		if (repeat > 0) {
			delay(player.getWorld().getServer().getConfig().GAME_TICK);
			batchArrowheads(player, headlessArrows, arrowHeads, headDef, repeat);
		}
	}

	private void doBowString(Player player, final Item bowString, final Item bow) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}
		final ItemBowStringDef stringDef = player.getWorld().getServer().getEntityHandler()
			.getItemBowStringDef(bow.getCatalogId());
		if (stringDef == null) {
			return;
		}
		int repeat = 1;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			int bowtimes = player.getCarriedItems().getInventory().countId(bow.getCatalogId());
			int stringtimes = player.getCarriedItems().getInventory().countId(bowString.getCatalogId());
			repeat = Math.min(bowtimes, stringtimes);
		}

		batchStringing(player, bow, bowString, stringDef, repeat);
	}

	private void batchStringing(Player player, Item bow, Item bowString, ItemBowStringDef stringDef, int repeat) {
		if (player.getSkills().getLevel(Skills.FLETCHING) < stringDef.getReqLevel()) {
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
		player.incExp(Skills.FLETCHING, stringDef.getExp(), true);
		delay(player.getWorld().getServer().getConfig().GAME_TICK);

		// Repeat
		if (player.hasMoved()) return;
		repeat--;
		if (repeat > 0) {
			delay(player.getWorld().getServer().getConfig().GAME_TICK * 2);
			batchStringing(player, bow, bowString, stringDef, repeat);
		}
	}

	private void doLogCut(final Player player, final Item knife,
							 final Item log) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}
		final ItemLogCutDef cutDef = player.getWorld().getServer().getEntityHandler().getItemLogCutDef(log.getCatalogId());
		if (cutDef == null) {
			return;
		}
		player.message("What would you like to make?");

		String[] options = log.getCatalogId() == ItemId.LOGS.id() ? new String[]{"Make arrow shafts",
			"Make shortbow", "Make longbow"} : new String[]{"Make shortbow", "Make longbow"};

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
				cutMessage = "You carefully cut the wood into 10 arrow shafts";
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
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(log.getCatalogId());
		}

		batchLogCutting(player, log, id, reqLvl, exp, cutMessage, repeat);
	}

	private void batchLogCutting(Player player, Item log, int id, int reqLvl, int exp, String cutMessage, int repeat) {
		if (player.getSkills().getLevel(Skills.FLETCHING) < reqLvl) {
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
			give(player, id, id == ItemId.ARROW_SHAFTS.id() ? 10 : 1);
			player.incExp(Skills.FLETCHING, exp, true);
			delay(player.getWorld().getServer().getConfig().GAME_TICK);
		}

		// Repeat
		if (player.hasMoved()) return;
		repeat--;
		if (repeat > 0) {
			delay(player.getWorld().getServer().getConfig().GAME_TICK * 2);
			batchLogCutting(player, log, id, reqLvl, exp, cutMessage, repeat);
		}
	}

	private boolean doPearlCut(final Player player, final Item chisel, final Item pearl) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return true;
		}

		int amount;
		if (pearl.getCatalogId() == ItemId.QUEST_OYSTER_PEARLS.id()) {
			amount = 25;
		} else if (pearl.getCatalogId() == ItemId.OYSTER_PEARLS.id()) {
			amount = 2;
		} else {
			player.message("Nothing interesting happens");
			return false;
		}

		final int amt = amount;
		final int exp = 25;
		final int pearlID = pearl.getCatalogId();
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Fletching Pearl Cut",
			player.getCarriedItems().getInventory().countId(pearlID), false) {
			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.FLETCHING) < 34) {
					getOwner().message("You need a fletching skill of 34 to do that");
					interruptBatch();
					return;
				}
				if (checkFatigue(player)) {
					return;
				}
				if (getOwner().getCarriedItems().remove(new Item(pearlID)) > -1) {
					getOwner().message("you chisel the pearls into small bolt tips");
					give(getOwner(), ItemId.OYSTER_PEARL_BOLT_TIPS.id(), amt);
					getOwner().incExp(Skills.FLETCHING, exp, true);
				} else interruptBatch();
			}
		});
		return true;
	}

	private boolean doBoltMake(final Player player, final Item bolts, final Item tips) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return true;
		}

		if (tips.getCatalogId() != ItemId.OYSTER_PEARL_BOLT_TIPS.id()) { // not pearl tips
			player.message("Nothing interesting happens");
			return false;
		}

		int bolt = bolts.getCatalogId();
		int tip = tips.getCatalogId();
		int amount = 10;
		if (player.getCarriedItems().getInventory().countId(bolt) < amount)
			amount = player.getCarriedItems().getInventory().countId(bolt);
		if (player.getCarriedItems().getInventory().countId(tip) < amount)
			amount = player.getCarriedItems().getInventory().countId(tip);
		int retryTimes = 1001; // 1 + 1000 for authentic behaviour
		boolean allowDuplicateEvents = true;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			retryTimes = 5;
			allowDuplicateEvents = false;
		}
		player.setBatchEvent(new BatchEvent(player.getWorld(), player,
			player.getWorld().getServer().getConfig().GAME_TICK, "Fletching Make Bolt",
			retryTimes, false, allowDuplicateEvents) {
			@Override
			public void action() {
				ServerConfiguration config = getWorld().getServer().getConfig();
				Player owner = getOwner();
				CarriedItems ci = owner.getCarriedItems();
				for (int i = 0; i < 10; ++i) {
					if (owner.getSkills().getLevel(Skills.FLETCHING) < 34) {
						owner.message("You need a fletching skill of 34 to do that");
						interruptBatch();
						return;
					}
					if (ci.getInventory().countId(bolt) < 1
						|| ci.getInventory().countId(tip) < 1) {
						interruptBatch();
						return;
					}
					if (checkFatigue(player)) {
						return;
					}
					if (ci.remove(new Item(bolt)) > -1
						&& ci.remove(new Item(tip)) > -1) {
						//Successful bolt make
						int skillCapeMultiplier = SkillCapes.shouldActivate(owner, ItemId.FLETCHING_CAPE) ? 2 : 1;
						ci.getInventory().add(new Item(ItemId.OYSTER_PEARL_BOLTS.id(), skillCapeMultiplier));
						owner.incExp(Skills.FLETCHING, 25 * skillCapeMultiplier, true);
					} else interruptBatch();
				}
			}
		});
		return true;
	}

	private boolean checkFatigue(Player player) {
		if (player.getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (player.getFatigue() >= player.MAX_FATIGUE) {
				if (player.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2) {
					player.message("You are too tired to train");
					return true;
				}
			}
		}
		return false;
	}
}
