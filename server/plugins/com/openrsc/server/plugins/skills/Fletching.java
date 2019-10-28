package com.openrsc.server.plugins.skills;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ItemArrowHeadDef;
import com.openrsc.server.external.ItemBowStringDef;
import com.openrsc.server.external.ItemDartTipDef;
import com.openrsc.server.external.ItemLogCutDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.showMenu;

public class Fletching implements InvUseOnItemExecutiveListener, InvUseOnItemListener {

	private static final int[] logIds = {
			ItemId.LOGS.id(),
			ItemId.OAK_LOGS.id(),
			ItemId.WILLOW_LOGS.id(),
			ItemId.MAPLE_LOGS.id(),
			ItemId.YEW_LOGS.id(),
			ItemId.MAGIC_LOGS.id()
	};

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		if (item1.getID() == com.openrsc.server.constants.ItemId.FEATHER.id() && attachFeathers(player, item1, item2)) {
			return true;
		} else if (item2.getID() == com.openrsc.server.constants.ItemId.FEATHER.id() && attachFeathers(player, item2, item1)) {
			return true;
		} else if (item1.getID() == com.openrsc.server.constants.ItemId.BOW_STRING.id() && doBowString(player, item1, item2)) {
			return true;
		} else if (item2.getID() == com.openrsc.server.constants.ItemId.BOW_STRING.id() && doBowString(player, item2, item1)) {
			return true;
		} else if (item1.getID() == com.openrsc.server.constants.ItemId.HEADLESS_ARROWS.id() && doArrowHeads(player, item1, item2)) {
			return true;
		} else if (item2.getID() == com.openrsc.server.constants.ItemId.HEADLESS_ARROWS.id() && doArrowHeads(player, item2, item1)) {
			return true;
		} else if (item1.getID() == com.openrsc.server.constants.ItemId.KNIFE.id() && DataConversions.inArray(logIds, item2.getID())) {
			return true;
		} else if (item2.getID() == com.openrsc.server.constants.ItemId.KNIFE.id() && DataConversions.inArray(logIds, item1.getID())) {
			return true;
		} else if (item1.getID() == com.openrsc.server.constants.ItemId.CHISEL.id() && (item2.getID() == com.openrsc.server.constants.ItemId.QUEST_OYSTER_PEARLS.id() || item2.getID() == com.openrsc.server.constants.ItemId.OYSTER_PEARLS.id()) && doPearlCut(player, item1, item2)) {
			return true;
		} else if (item2.getID() == com.openrsc.server.constants.ItemId.CHISEL.id() && (item1.getID() == com.openrsc.server.constants.ItemId.QUEST_OYSTER_PEARLS.id() || item1.getID() == com.openrsc.server.constants.ItemId.OYSTER_PEARLS.id()) && doPearlCut(player, item2, item1)) {
			return true;
		} else if (item1.getID() == com.openrsc.server.constants.ItemId.OYSTER_PEARL_BOLT_TIPS.id() && doBoltMake(player, item2, item1)) {
			return true;
		} else if (item2.getID() == com.openrsc.server.constants.ItemId.OYSTER_PEARL_BOLT_TIPS.id() && doBoltMake(player, item1, item2)) {
			return true;
		}
		return false;
	}

	private boolean attachFeathers(Player player, final Item feathers,
								   final Item item) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return true;
		}

		// Amount is less than 10 if we do not have enough.
		int amount = 10;
		if (feathers.getAmount() < amount) {
			amount = feathers.getAmount();
		}
		if (item.getAmount() < amount) {
			amount = item.getAmount();
		}

		// Determine EXP based on amount + item
		final int itemID;
		int experience = 4;
		ItemDartTipDef tipDef = null;
		if (item.getID() == ItemId.ARROW_SHAFTS.id()) {
			itemID = ItemId.HEADLESS_ARROWS.id();
		} else if ((tipDef = player.getWorld().getServer().getEntityHandler().getItemDartTipDef(item.getID())) != null) {
			itemID = tipDef.getDartID(); // Dart ID
			experience = tipDef.getExp();
		} else {
			return false;
		}

		player.message("You attach feathers to some of your "
			+ item.getDef(player.getWorld()).getName());
		final int exp = experience;
		int retrytimes = player.getWorld().getServer().getConfig().BATCH_PROGRESSION ? Formulae.getRepeatTimes(player, Skills.FLETCHING) : 1000 + amount;
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 40, "Fletching Attach Feathers", retrytimes, false) {
			@Override
			public void action() {
				if (getOwner().getInventory().countId(feathers.getID()) < 1
						|| getOwner().getInventory().countId(item.getID()) < 1) {
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
				if (getOwner().getInventory().remove(feathers.getID(), 1) > -1
					&& getOwner().getInventory().remove(item.getID(), 1) > -1) {
					addItem(getOwner(), itemID, 1);
					getOwner().incExp(Skills.FLETCHING, exp, true);
				} else {
					interrupt();
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
			.getItemArrowHeadDef(arrowHeads.getID());
		if (headDef == null) {
			return false;
		}

		if (player.getSkills().getLevel(Skills.FLETCHING) < headDef.getReqLevel()) {
			player.message("You need a fletching skill of "
				+ headDef.getReqLevel() + " or above to do that");
			return true;
		}

		int amount = 10;
		if (headlessArrows.getAmount() < amount) {
			amount = headlessArrows.getAmount();
		}
		if (arrowHeads.getAmount() < amount) {
			amount = arrowHeads.getAmount();
		}

		player.message("You attach "
			+ arrowHeads.getDef(player.getWorld()).getName().toLowerCase()
			+ " to some of your arrows");
		int retrytimes = player.getWorld().getServer().getConfig().BATCH_PROGRESSION ? Formulae.getRepeatTimes(player, Skills.FLETCHING) : 1000 + amount;
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 40, "Fletching Attach Arrowheads", retrytimes, false) {
			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.FLETCHING) < headDef.getReqLevel()) {
					getOwner().message("You need a fletching skill of "
						+ headDef.getReqLevel() + " or above to do that");
					interrupt();
					return;
				}
				if (getOwner().getInventory().countId(arrowHeads.getID()) < 1
						|| getOwner().getInventory().countId(headlessArrows.getID()) < 1) {
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
				if (getOwner().getInventory().remove(headlessArrows.getID(), 1) > -1
					&& getOwner().getInventory().remove(arrowHeads.getID(), 1) > -1) {
					getOwner().getInventory().add(new Item(headDef.getArrowID(), 1));
					getOwner().incExp(Skills.FLETCHING, headDef.getExp(), true);
				} else {
					interrupt();
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
			.getItemBowStringDef(bow.getID());
		if (stringDef == null) {
			return false;
		}
		int bowtimes = player.getInventory().countId(bow.getID());
		int stringtimes = player.getInventory().countId(bowString.getID());

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Fletching String Bow",
			bowtimes < stringtimes ? bowtimes : stringtimes, false) {
			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.FLETCHING) < stringDef.getReqLevel()) {
					getOwner().message("You need a fletching skill of "
						+ stringDef.getReqLevel() + " or above to do that");
					interrupt();
					return;
				}
				if (getOwner().getInventory().countId(bow.getID()) < 1
						|| getOwner().getInventory().countId(bowString.getID()) < 1) {
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
				if (getOwner().getInventory().remove(bowString) > -1
					&& getOwner().getInventory().remove(bow) > -1) {
					getOwner().message("You add a string to the bow");
					getOwner().getInventory().add(new Item(stringDef.getBowID(), 1));
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
		final ItemLogCutDef cutDef = player.getWorld().getServer().getEntityHandler().getItemLogCutDef(log.getID());
		if (cutDef == null) {
			return;
		}
		player.message("What would you like to make?");

		String[] options = log.getID() == ItemId.LOGS.id() ? new String[]{"Make arrow shafts",
				"Make shortbow", "Make longbow"} : new String[]{"Make shortbow", "Make longbow"};

		int type = showMenu(player, options);
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

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Fletching Make Bow",
				player.getInventory().countId(log.getID()), false) {
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
				if (getOwner().getInventory().remove(log) > -1) {
					getOwner().message(cutMessages);
					addItem(getOwner(), itemID, amt);
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
		if (pearl.getID() == com.openrsc.server.constants.ItemId.QUEST_OYSTER_PEARLS.id()) {
			amount = 25;
		} else if (pearl.getID() == com.openrsc.server.constants.ItemId.OYSTER_PEARLS.id()) {
			amount = 2;
		} else {
			player.message("Nothing interesting happens");
			return false;
		}

		final int amt = amount;
		final int exp = 25;
		final int pearlID = pearl.getID();
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Fletching Pearl Cut",
			player.getInventory().countId(pearlID), false) {
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
				if (getOwner().getInventory().remove(pearlID, 1) > -1) {
					getOwner().message("");
					addItem(getOwner(), com.openrsc.server.constants.ItemId.OYSTER_PEARL_BOLT_TIPS.id(), amt);
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

		if (tips.getID() != com.openrsc.server.constants.ItemId.OYSTER_PEARL_BOLT_TIPS.id()) { // not pearl tips
			player.message("Nothing interesting happens");
			return false;
		}

		int bolt = bolts.getID();
		int tip = tips.getID();
		int amount = 10;
		if (player.getInventory().countId(bolt) < amount)
			amount = player.getInventory().countId(bolt);
		if (player.getInventory().countId(tip) < amount)
			amount = player.getInventory().countId(tip);
		int retrytimes = player.getWorld().getServer().getConfig().BATCH_PROGRESSION ? Formulae.getRepeatTimes(player, Skills.FLETCHING) : 1000 + amount;
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 40, "Fletching Make Bolt", retrytimes, false) {
			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.FLETCHING) < 34) {
					getOwner().message("You need a fletching skill of 34 to do that");
					interrupt();
					return;
				}
				if (getOwner().getInventory().countId(bolt) < 1
						|| getOwner().getInventory().countId(tip) < 1) {
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
				if (getOwner().getInventory().remove(bolt, 1) > -1
					&& getOwner().getInventory().remove(tip, 1) > -1) {
					getOwner().message("");
					addItem(getOwner(), ItemId.OYSTER_PEARL_BOLTS.id(), 1);
					getOwner().incExp(Skills.FLETCHING, 25, true);
				} else interrupt();
			}
		});
		return true;
	}

	@Override
	public void onInvUseOnItem(Player player, Item item1, Item item2) {
		if (item1.getID() == com.openrsc.server.constants.ItemId.KNIFE.id() && DataConversions.inArray(logIds, item2.getID())) {
			doLogCut(player, item1, item2);
		} else if (item2.getID() == com.openrsc.server.constants.ItemId.KNIFE.id() && DataConversions.inArray(logIds, item1.getID())) {
			doLogCut(player, item2, item1);
		}
	}

}
