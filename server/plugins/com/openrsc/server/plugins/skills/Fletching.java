package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemArrowHeadDef;
import com.openrsc.server.external.ItemBowStringDef;
import com.openrsc.server.external.ItemDartTipDef;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.ItemLogCutDef;
import com.openrsc.server.model.MenuOptionListener;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.addItem;

public class Fletching implements InvUseOnItemExecutiveListener {

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		if (item1.getID() == ItemId.FEATHER.id() && attachFeathers(player, item1, item2)) {
			return true;
		} else if (item2.getID() == ItemId.FEATHER.id() && attachFeathers(player, item2, item1)) {
			return true;
		} else if (item1.getID() == ItemId.BOW_STRING.id() && doBowString(player, item1, item2)) {
			return true;
		} else if (item2.getID() == ItemId.BOW_STRING.id() && doBowString(player, item2, item1)) {
			return true;
		} else if (item1.getID() == ItemId.HEADLESS_ARROWS.id() && doArrowHeads(player, item1, item2)) {
			return true;
		} else if (item2.getID() == ItemId.HEADLESS_ARROWS.id() && doArrowHeads(player, item2, item1)) {
			return true;
		} else if (item1.getID() == ItemId.KNIFE.id() && doLogCut(player, item1, item2)) {
			return true;
		} else if (item2.getID() == ItemId.KNIFE.id() && doLogCut(player, item2, item1)) {
			return true;
		} else if (item1.getID() == ItemId.CHISEL.id() && (item2.getID() == ItemId.QUEST_OYSTER_PEARLS.id() || item2.getID() == ItemId.OYSTER_PEARLS.id()) && doPearlCut(player, item1, item2)) {
			return true;
		} else if (item2.getID() == ItemId.CHISEL.id() && (item1.getID() == ItemId.QUEST_OYSTER_PEARLS.id() || item1.getID() == ItemId.OYSTER_PEARLS.id()) && doPearlCut(player, item2, item1)) {
			return true;
		} else if (item1.getID() == ItemId.OYSTER_PEARL_BOLT_TIPS.id() && doBoltMake(player, item2, item1)) {
			return true;
		} else if (item2.getID() == ItemId.OYSTER_PEARL_BOLT_TIPS.id() && doBoltMake(player, item1, item2)) {
			return true;
		}
		return false;
	}


	private boolean attachFeathers(Player player, final Item feathers,
								   final Item item) {
		if (!Constants.GameServer.MEMBER_WORLD) {
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
		} else if ((tipDef = EntityHandler.getItemDartTipDef(item.getID())) != null) {
			itemID = tipDef.getDartID(); // Dart ID
			experience = (int) (tipDef.getExp());
		} else {
			return false;
		}

		player.message("You attach feathers to some of your "
			+ item.getDef().getName());
		final int exp = experience;
		player.setBatchEvent(new BatchEvent(player, 40, "Fletching Attach Feathers", 1000 + amount, false) {
			@Override
			public void action() {
				if (owner.getInventory().countId(feathers.getID()) < 1) {
					interrupt();
					return;
				}
				if (owner.getInventory().countId(item.getID()) < 1) {
					interrupt();
					return;
				}
				if (owner.getInventory().remove(feathers.getID(), 1) > -1
					&& owner.getInventory().remove(item.getID(), 1) > -1) {
					owner.incExp(SKILLS.FLETCHING.id(), exp, true);
					addItem(owner, itemID, 1);
				} else {
					interrupt();
				}
			}
		});
		return true;
	}

	private boolean doArrowHeads(Player player, final Item headlessArrows,
								 final Item arrowHeads) {
		if (!Constants.GameServer.MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return true;
		}
		final ItemArrowHeadDef headDef = EntityHandler
			.getItemArrowHeadDef(arrowHeads.getID());
		if (headDef == null) {
			return false;
		}

		int amount = 10;
		if (headlessArrows.getAmount() < amount) {
			amount = headlessArrows.getAmount();
		}
		if (arrowHeads.getAmount() < amount) {
			amount = arrowHeads.getAmount();
		}

		player.message("You attach "
			+ arrowHeads.getDef().getName().toLowerCase()
			+ " to some of your arrows");
		player.setBatchEvent(new BatchEvent(player, 40, "Fletching Attach Arrowheads", 1000 + amount, false) {
			@Override
			public void action() {
				if (owner.getSkills().getLevel(SKILLS.FLETCHING.id()) < headDef.getReqLevel()) {
					owner.message("You need a fletching skill of "
						+ headDef.getReqLevel() + " or above to do that");
					interrupt();
					return;
				}
				if (owner.getInventory().countId(arrowHeads.getID()) < 1) {
					interrupt();
					return;
				}
				if (owner.getInventory().countId(headlessArrows.getID()) < 1) {
					interrupt();
					return;
				}
				if (owner.getInventory().remove(headlessArrows.getID(), 1) > -1
					&& owner.getInventory().remove(arrowHeads.getID(), 1) > -1) {
					owner.incExp(SKILLS.FLETCHING.id(), headDef.getExp(), true);
					owner.getInventory().add(new Item(headDef.getArrowID(), 1));
				} else {
					interrupt();
				}
			}
		});
		return true;
	}

	private boolean doBowString(Player player, final Item bowString,
								final Item bow) {
		if (!Constants.GameServer.MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return true;
		}
		final ItemBowStringDef stringDef = EntityHandler
			.getItemBowStringDef(bow.getID());
		if (stringDef == null) {
			return false;
		}
		player.setBatchEvent(new BatchEvent(player, 600, "Fletching String Bow", Formulae
			.getRepeatTimes(player, SKILLS.FLETCHING.id()), false) {

			@Override
			public void action() {
				if (owner.getSkills().getLevel(SKILLS.FLETCHING.id()) < stringDef.getReqLevel()) {
					owner.message("You need a fletching skill of "
						+ stringDef.getReqLevel() + " or above to do that");
					interrupt();
					return;
				}
				if (owner.getInventory().countId(bow.getID()) < 1) {
					interrupt();
					return;
				}
				if (owner.getInventory().countId(bowString.getID()) < 1) {
					interrupt();
					return;
				}
				if (owner.getInventory().remove(bowString) > -1
					&& owner.getInventory().remove(bow) > -1) {
					owner.message("You add a string to the bow");
					owner.getInventory().add(new Item(stringDef.getBowID(), 1));
					owner.incExp(SKILLS.FLETCHING.id(), stringDef.getExp(), true);
				} else
					interrupt();
			}
		});
		return true;
	}

	private boolean doLogCut(final Player player, final Item knife,
							 final Item log) {
		if (!Constants.GameServer.MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return true;
		}
		final ItemLogCutDef cutDef = EntityHandler.getItemLogCutDef(log.getID());
		if (cutDef == null) {
			return false;
		}
		player.message("What would you like to make?");
		Server.getServer().getEventHandler().add(new MiniEvent(player, "Fletching Make Bow") {
			public void action() {
				String[] option = new String[]{"Make arrow shafts",
					"Make shortbow", "Make longbow"};
				String[] options = new String[]{"Make shortbow",
					"Make longbow"};
				if (log.getID() == ItemId.LOGS.id()) {
					owner.setMenuHandler(new MenuOptionListener(option) {
						public void handleReply(final int option,
												final String reply) {
							final int reqLvl, exp, itemID, amount;
							String cutMessage = null;
							switch (option) {
								case 0:
									itemID = 280;
									amount = 10;
									reqLvl = cutDef.getShaftLvl();
									exp = cutDef.getShaftExp();
									cutMessage = "You carefully cut the wood into 10 arrow shafts";
									break;
								case 1:
									itemID = cutDef.getShortbowID();
									amount = 1;
									reqLvl = cutDef.getShortbowLvl();
									exp = cutDef.getShortbowExp();
									cutMessage = "You carefully cut the wood into a shortbow";
									break;
								case 2:
									itemID = cutDef.getLongbowID();
									amount = 1;
									reqLvl = cutDef.getLongbowLvl();
									exp = cutDef.getLongbowExp();
									cutMessage = "You carefully cut the wood into a longbow";
									break;
								default:
									return;
							}
							final int requiredLvl = reqLvl;
							final int experience = exp;
							final String cutMessages = cutMessage;
							player.setBatchEvent(new BatchEvent(player, 600, "Fletching Make Bow", Formulae
								.getRepeatTimes(player, SKILLS.FLETCHING.id()), false) {


								@Override
								public void action() {
									if (owner.getSkills().getLevel(SKILLS.FLETCHING.id()) < requiredLvl) {
										owner.message("You need a fletching skill of "
											+ requiredLvl + " or above to do that");
										interrupt();
										return;
									}
									if (owner.getInventory().remove(log) > -1) {
										owner.message(cutMessages);
										addItem(owner, itemID, amount);
										owner.incExp(SKILLS.FLETCHING.id(), experience, true);
									} else
										interrupt();
								}
							});
						}
					});
					ActionSender.sendMenu(owner, option);
					return;
				}
				owner.setMenuHandler(new MenuOptionListener(options) {
					public void handleReply(final int option, final String reply) {
						final int reqLvl, exp, itemID, amount;
						String cutMessage = null;
						switch (option) {
							case 0:
								itemID = cutDef.getShortbowID();
								amount = 1;
								reqLvl = cutDef.getShortbowLvl();
								exp = cutDef.getShortbowExp();
								cutMessage = "You carefully cut the wood into a shortbow";
								break;
							case 1:
								itemID = cutDef.getLongbowID();
								amount = 1;
								reqLvl = cutDef.getLongbowLvl();
								exp = cutDef.getLongbowExp();
								cutMessage = "You carefully cut the wood into a longbow";
								break;
							default:
								return;
						}

						final int requiredLvl = reqLvl;
						final int experience = exp;
						final String cutMessages = cutMessage;
						player.setBatchEvent(new BatchEvent(player, 600, "Fletching Make Bow", Formulae
							.getRepeatTimes(player, SKILLS.FLETCHING.id()), false) {


							@Override
							public void action() {
								if (owner.getSkills().getLevel(SKILLS.FLETCHING.id()) < requiredLvl) {
									owner.message("You need a fletching skill of "
										+ requiredLvl + " or above to do that");
									interrupt();
									return;
								}
								if (owner.getInventory().remove(log) > -1) {
									owner.message(cutMessages);
									addItem(owner, itemID, amount);
									owner.incExp(SKILLS.FLETCHING.id(), experience, true);
								} else
									interrupt();
							}
						});
					}
				});
				ActionSender.sendMenu(owner, options);
			}
		});
		return true;
	}

	private boolean doPearlCut(final Player player, final Item chisel, final Item pearl) {
		if (!Constants.GameServer.MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return true;
		}

		int amount;
		if (pearl.getID() == ItemId.QUEST_OYSTER_PEARLS.id()) {
			amount = 25;
		} else if (pearl.getID() == ItemId.OYSTER_PEARLS.id()) {
			amount = 2;
		} else {
			player.message("Nothing interesting happens");
			return false;
		}

		final int amt = amount;
		final int exp = 25;
		final int pearlID = pearl.getID();
		player.setBatchEvent(new BatchEvent(player, 600, "Fletching Pearl Cut", Formulae
			.getRepeatTimes(player, SKILLS.FLETCHING.id()), false) {

			@Override
			public void action() {
				if (owner.getSkills().getLevel(SKILLS.FLETCHING.id()) < 34) {
					owner.message("You need a fletching skill of 34 to do that");
					interrupt();
					return;
				}
				if (owner.getInventory().remove(pearlID, 1) > -1) {
					owner.message("");
					owner.incExp(SKILLS.FLETCHING.id(), exp, true);
					addItem(owner, ItemId.OYSTER_PEARL_BOLT_TIPS.id(), amt);
				} else interrupt();
			}
		});
		return true;
	}

	private boolean doBoltMake(final Player player, final Item bolts, final Item tips) {
		if (!Constants.GameServer.MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return true;
		}

		if (tips.getID() != ItemId.OYSTER_PEARL_BOLT_TIPS.id()) { // not pearl tips
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

		player.setBatchEvent(new BatchEvent(player, 40, "Fletching Make Bolt", 1000 + amount, false) {
			@Override
			public void action() {
				if (owner.getSkills().getLevel(SKILLS.FLETCHING.id()) < 34) {
					owner.message("You need a fletching skill of 34 to do that");
					interrupt();
					return;
				}
				if (owner.getInventory().countId(bolt) < 1) {
					interrupt();
					return;
				}
				if (owner.getInventory().countId(tip) < 1) {
					interrupt();
					return;
				}
				if (owner.getInventory().remove(bolt, 1) > -1
					&& owner.getInventory().remove(tip, 1) > -1) {
					owner.message("");
					owner.incExp(SKILLS.FLETCHING.id(), 25, true);
					addItem(owner, ItemId.OYSTER_PEARL_BOLTS.id(), 1);
				} else interrupt();
			}
		});
		return true;
	}

}
