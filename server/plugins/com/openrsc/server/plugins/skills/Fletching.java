package com.openrsc.server.plugins.skills;

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
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.give;

public class Fletching implements UseInvTrigger {

	private static final int[] logIds = {
			ItemId.LOGS.id(),
			ItemId.OAK_LOGS.id(),
			ItemId.WILLOW_LOGS.id(),
			ItemId.MAPLE_LOGS.id(),
			ItemId.YEW_LOGS.id(),
			ItemId.MAGIC_LOGS.id()
	};

	@Override
	public boolean blockUseInv(Player player, Item item1, Item item2) {
		int item1ID = item1.getCatalogId();
		int item2ID = item2.getCatalogId();
		if (item1ID == com.openrsc.server.constants.ItemId.FEATHER.id() && attachFeathers(player, item1, item2)) {
			return true;
		} else if (item2ID == com.openrsc.server.constants.ItemId.FEATHER.id() && attachFeathers(player, item2, item1)) {
			return true;
		} else if (item1ID == com.openrsc.server.constants.ItemId.BOW_STRING.id() && doBowString(player, item1, item2)) {
			return true;
		} else if (item2ID == com.openrsc.server.constants.ItemId.BOW_STRING.id() && doBowString(player, item2, item1)) {
			return true;
		} else if (item1ID == com.openrsc.server.constants.ItemId.HEADLESS_ARROWS.id() && doArrowHeads(player, item1, item2)) {
			return true;
		} else if (item2ID == com.openrsc.server.constants.ItemId.HEADLESS_ARROWS.id() && doArrowHeads(player, item2, item1)) {
			return true;
		} else if (item1ID == com.openrsc.server.constants.ItemId.KNIFE.id() && DataConversions.inArray(logIds, item2.getCatalogId())) {
			return true;
		} else if (item2ID == com.openrsc.server.constants.ItemId.KNIFE.id() && DataConversions.inArray(logIds, item1.getCatalogId())) {
			return true;
		} else if (item1ID == com.openrsc.server.constants.ItemId.CHISEL.id() && (item2.getCatalogId() == com.openrsc.server.constants.ItemId.QUEST_OYSTER_PEARLS.id() || item2.getCatalogId() == com.openrsc.server.constants.ItemId.OYSTER_PEARLS.id()) && doPearlCut(player, item1, item2)) {
			return true;
		} else if (item2ID == com.openrsc.server.constants.ItemId.CHISEL.id() && (item1.getCatalogId() == com.openrsc.server.constants.ItemId.QUEST_OYSTER_PEARLS.id() || item1.getCatalogId() == com.openrsc.server.constants.ItemId.OYSTER_PEARLS.id()) && doPearlCut(player, item2, item1)) {
			return true;
		} else if (item1ID == com.openrsc.server.constants.ItemId.OYSTER_PEARL_BOLT_TIPS.id() && doBoltMake(player, item2, item1)) {
			return true;
		} else if (item2ID == com.openrsc.server.constants.ItemId.OYSTER_PEARL_BOLT_TIPS.id() && doBoltMake(player, item1, item2)) {
			return true;
		}
		return false;
	}

	private boolean attachFeathers(Player player, final Item feathers,
								   final Item attachment) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return true;
		}

		// Amount is less than 10 if we do not have enough.
		int amount = 10;
		if (feathers.getAmount() < amount) {
			amount = feathers.getAmount();
		}
		if (attachment.getAmount() < amount) {
			amount = attachment.getAmount();
		}

		// Determine EXP based on amount + item
		final int itemID;
		int experience = 4;
		ItemDartTipDef tipDef = null;
		int attachmentID = attachment.getCatalogId();
		if (attachmentID == ItemId.ARROW_SHAFTS.id()) {
			itemID = ItemId.HEADLESS_ARROWS.id();
		} else if ((tipDef = player.getWorld().getServer().getEntityHandler().getItemDartTipDef(attachmentID)) != null) {
			itemID = tipDef.getDartID(); // Dart ID
			experience = tipDef.getExp();
		} else {
			return false;
		}

		player.message("You attach feathers to some of your "
			+ attachment.getDef(player.getWorld()).getName());
		final int exp = experience;
		int retryTimes = 1001; // 1 + 1000 for authentic behaviour
		boolean allowDuplicateEvents = true;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			retryTimes = 5;
			allowDuplicateEvents = false;
		}

		player.setBatchEvent(new BatchEvent(player.getWorld(), player,
				player.getWorld().getServer().getConfig().GAME_TICK, "Fletching Attach Feathers",
				retryTimes, false, allowDuplicateEvents) {
			@Override
			public void action() {
				ServerConfiguration config = getWorld().getServer().getConfig();
				Player owner = getOwner();
				CarriedItems ci = owner.getCarriedItems();
				int featherID = feathers.getCatalogId();
				for (int i = 0; i < 10; ++i) {
					if (ci.getInventory().countId(featherID) < 1
						|| ci.getInventory().countId(attachmentID) < 1) {
						interrupt();
						return;
					}
					if (config.WANT_FATIGUE) {
						if (owner.getFatigue() >= owner.MAX_FATIGUE) {
							if (config.STOP_SKILLING_FATIGUED >= 2) {
								owner.message("You are too tired to gain experience, get some rest!");
								interrupt();
								return;
							}
						}

					}
					Item item = ci.getInventory().get(
						ci.getInventory().getLastIndexById(featherID));
					Item featherToRemove = new Item(featherID, 1, false, item.getItemId());
					item = ci.getInventory().get(
						ci.getInventory().getLastIndexById(attachmentID));
					Item attachmentToRemove = new Item(attachmentID, 1, false, item.getItemId());
					if (ci.remove(featherToRemove, true) > -1
						&& ci.remove(attachmentToRemove, true) > -1) {
						ci.getInventory().add(new Item(itemID, 1), true);
						owner.incExp(Skills.FLETCHING, exp, true);
						// ActionSender.sendInventory(owner);
					} else {
						interrupt();
					}
				}
			}
		});
		return true;
	}

	private boolean doArrowHeads(Player player, final Item headlessArrows,
								 final Item arrowHeads) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return true;
		}
		final ItemArrowHeadDef headDef = player.getWorld().getServer().getEntityHandler()
			.getItemArrowHeadDef(arrowHeads.getCatalogId());
		if (headDef == null) {
			return false;
		}

		if (player.getSkills().getLevel(Skills.FLETCHING) < headDef.getReqLevel()) {
			player.message("You need a fletching skill of "
				+ headDef.getReqLevel() + " or above to do that");
			return true;
		}

		player.message("You attach "
			+ arrowHeads.getDef(player.getWorld()).getName().toLowerCase()
			+ " to some of your arrows");
		int retryTimes = 1001; // 1 + 1000 for authentic behaviour
		boolean allowDuplicateEvents = true;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			retryTimes = 5;
			allowDuplicateEvents = false;
		}
		player.setBatchEvent(new BatchEvent(player.getWorld(), player,
			player.getWorld().getServer().getConfig().GAME_TICK, "Fletching Attach Arrowheads",
			retryTimes, false, allowDuplicateEvents) {
			@Override
			public void action() {
				ServerConfiguration config = getWorld().getServer().getConfig();
				Player owner = getOwner();
				CarriedItems ci = owner.getCarriedItems();
				int arrowHeadsID = arrowHeads.getCatalogId();
				int headlessArrowsID = headlessArrows.getCatalogId();
				for (int i = 0; i < 10; ++i) {
					if (owner.getSkills().getLevel(Skills.FLETCHING) < headDef.getReqLevel()) {
						owner.message("You need a fletching skill of "
							+ headDef.getReqLevel() + " or above to do that");
						interrupt();
						return;
					}
					if (ci.getInventory().countId(arrowHeadsID) < 1
						|| ci.getInventory().countId(headlessArrowsID) < 1) {
						interrupt();
						return;
					}
					if (config.WANT_FATIGUE) {
						if (owner.getFatigue() >= owner.MAX_FATIGUE) {
							if (config.STOP_SKILLING_FATIGUED >= 2) {
								owner.message("You are too tired to gain experience, get some rest!");
								interrupt();
								return;
							}
						}

					}
					Item item = ci.getInventory().get(
						ci.getInventory().getLastIndexById(headlessArrowsID));
					Item headlessToRemove = new Item(headlessArrowsID, 1, false, item.getItemId());
					item = ci.getInventory().get(
						ci.getInventory().getLastIndexById(arrowHeadsID));
					Item arrowHeadsToRemove = new Item(arrowHeadsID, 1, false, item.getItemId());
					if (ci.remove(headlessToRemove, true) > -1
						&& ci.remove(arrowHeadsToRemove, true) > -1) {
						//Successful make attempt
						int skillCapeMultiplier = SkillCapes.shouldActivate(owner, ItemId.FLETCHING_CAPE) ? 2 : 1;
						ci.getInventory().add(new Item(headDef.getArrowID(), skillCapeMultiplier));
						owner.incExp(Skills.FLETCHING, headDef.getExp() * skillCapeMultiplier, true);
						ActionSender.sendInventory(owner);
					} else {
						interrupt();
						return;
					}
				}
			}
		});
		return true;
	}

	private boolean doBowString(Player player, final Item bowString,
								final Item bow) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return true;
		}
		final ItemBowStringDef stringDef = player.getWorld().getServer().getEntityHandler()
			.getItemBowStringDef(bow.getCatalogId());
		if (stringDef == null) {
			return false;
		}
		int bowtimes = player.getCarriedItems().getInventory().countId(bow.getCatalogId());
		int stringtimes = player.getCarriedItems().getInventory().countId(bowString.getCatalogId());

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Fletching String Bow",
			bowtimes < stringtimes ? bowtimes : stringtimes, false) {
			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.FLETCHING) < stringDef.getReqLevel()) {
					getOwner().message("You need a fletching skill of "
						+ stringDef.getReqLevel() + " or above to do that");
					interrupt();
					return;
				}
				if (getOwner().getCarriedItems().getInventory().countId(bow.getCatalogId()) < 1
						|| getOwner().getCarriedItems().getInventory().countId(bowString.getCatalogId()) < 1) {
					interrupt();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().message("You are too tired to train");
						interrupt();
						return;
					}
				}
				if (getOwner().getCarriedItems().remove(bowString) > -1
					&& getOwner().getCarriedItems().remove(bow) > -1) {
					getOwner().message("You add a string to the bow");
					getOwner().getCarriedItems().getInventory().add(new Item(stringDef.getBowID(), 1));
					getOwner().incExp(Skills.FLETCHING, stringDef.getExp(), true);
				} else
					interrupt();
			}
		});
		return true;
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

		int type = Functions.multi(player, options);
		if (player.isBusy() || type < 0 || type > options.length) {
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
			amount = 10;
			reqLvl = cutDef.getShaftLvl();
			exp = cutDef.getShaftExp();
			cutMessage = "You carefully cut the wood into 10 arrow shafts";
			break;
		case 1:
			id = cutDef.getShortbowID();
			amount = 1;
			reqLvl = cutDef.getShortbowLvl();
			exp = cutDef.getShortbowExp();
			cutMessage = "You carefully cut the wood into a shortbow";
			break;
		case 2:
			id = cutDef.getLongbowID();
			amount = 1;
			reqLvl = cutDef.getLongbowLvl();
			exp = cutDef.getLongbowExp();
			cutMessage = "You carefully cut the wood into a longbow";
			break;
		}
		final int requiredLvl = reqLvl;
		final int experience = exp;
		final int itemID = id;
		final int amt = amount;
		final String cutMessages = cutMessage;

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Fletching Make Bow",
				player.getCarriedItems().getInventory().countId(log.getCatalogId()), false) {
			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.FLETCHING) < requiredLvl) {
					getOwner().message("You need a fletching skill of "
							+ requiredLvl + " or above to do that");
					interrupt();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().message("You are too tired to train");
						interrupt();
						return;
					}
				}
				if (getOwner().getCarriedItems().remove(log) > -1) {
					getOwner().message(cutMessages);
					give(getOwner(), itemID, amt);
					getOwner().incExp(Skills.FLETCHING, experience, true);
				} else
					interrupt();
			}
		});
	}

	private boolean doPearlCut(final Player player, final Item chisel, final Item pearl) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return true;
		}

		int amount;
		if (pearl.getCatalogId() == com.openrsc.server.constants.ItemId.QUEST_OYSTER_PEARLS.id()) {
			amount = 25;
		} else if (pearl.getCatalogId() == com.openrsc.server.constants.ItemId.OYSTER_PEARLS.id()) {
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
					interrupt();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().message("You are too tired to train");
						interrupt();
						return;
					}
				}
				if (getOwner().getCarriedItems().remove(pearlID, 1) > -1) {
					getOwner().message("you chisel the pearls into small bolt tips");
					give(getOwner(), com.openrsc.server.constants.ItemId.OYSTER_PEARL_BOLT_TIPS.id(), amt);
					getOwner().incExp(Skills.FLETCHING, exp, true);
				} else interrupt();
			}
		});
		return true;
	}

	private boolean doBoltMake(final Player player, final Item bolts, final Item tips) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return true;
		}

		if (tips.getCatalogId() != com.openrsc.server.constants.ItemId.OYSTER_PEARL_BOLT_TIPS.id()) { // not pearl tips
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
						interrupt();
						return;
					}
					if (ci.getInventory().countId(bolt) < 1
						|| ci.getInventory().countId(tip) < 1) {
						interrupt();
						return;
					}
					if (config.WANT_FATIGUE) {
						if (owner.getFatigue() >= owner.MAX_FATIGUE) {
							if (config.STOP_SKILLING_FATIGUED >= 2) {
								owner.message("You are too tired to gain experience, get some rest!");
								interrupt();
								return;
							}
						}

					}
					if (ci.remove(bolt, 1) > -1
						&& ci.remove(tip, 1) > -1) {
						//Successful bolt make
						int skillCapeMultiplier = SkillCapes.shouldActivate(owner, ItemId.FLETCHING_CAPE) ? 2 : 1;
						ci.getInventory().add(new Item(ItemId.OYSTER_PEARL_BOLTS.id(), skillCapeMultiplier));
						owner.incExp(Skills.FLETCHING, 25 * skillCapeMultiplier, true);
					} else interrupt();
				}
			}
		});
		return true;
	}

	@Override
	public void onUseInv(Player player, Item item1, Item item2) {
		if (item1.getCatalogId() == com.openrsc.server.constants.ItemId.KNIFE.id() && DataConversions.inArray(logIds, item2.getCatalogId())) {
			doLogCut(player, item1, item2);
		} else if (item2.getCatalogId() == com.openrsc.server.constants.ItemId.KNIFE.id() && DataConversions.inArray(logIds, item1.getCatalogId())) {
			doLogCut(player, item2, item1);
		}
	}

}
