package com.openrsc.server.plugins.skills;

import static com.openrsc.server.plugins.Functions.showBubble;
import static com.openrsc.server.plugins.Functions.message;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemCraftingDef;
import com.openrsc.server.external.ItemGemDef;
import com.openrsc.server.model.MenuOptionListener;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

public class Crafting implements InvUseOnItemListener,
InvUseOnItemExecutiveListener, InvUseOnObjectListener,
InvUseOnObjectExecutiveListener {

	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	@Override
	public void onInvUseOnObject(GameObject obj, final Item item,
			Player owner) {
		switch (obj.getID()) {
		case 118:
		case 813: // Furnace
			if (item.getID() == 172 || item.getID() == 691) {
				Server.getServer().getEventHandler().add(new MiniEvent(owner) {
					public void action() {
						owner.message(
								"What would you like to make?");
						String[] options = new String[] { "Ring", "Necklace",
						"Amulet" };
						owner.setMenuHandler(new MenuOptionListener(options) {
							public void handleReply(int option, String reply) {
								if (owner.isBusy() || option < 0 || option > 2) {
									return;
								}
								final int[] moulds = { 293, 295, 294 };
								final int[] gems = { -1, 164, 163, 162, 161,
										523 };
								String[] options = { "Gold", "Sapphire",
										"Emerald", "Ruby", "Diamond" };
								if (Constants.GameServer.MEMBER_WORLD) {
									options = new String[] { "Gold",
											"Sapphire", "Emerald", "Ruby",
											"Diamond", "Dragonstone" };
								}
								final int craftType = option;
								if (owner.getInventory().countId(
										moulds[craftType]) < 1) {
									owner.message(
											"You need a "
													+ EntityHandler.getItemDef(
															moulds[craftType])
													.getName()
													+ " to make a " + reply);
									return;
								}
								owner.message(
										"What type of " + reply
										+ " would you like to make?");
								owner.setMenuHandler(new MenuOptionListener(options) {
									public void handleReply(final int option,
											final String reply) {
										owner.setBatchEvent(new BatchEvent(
												owner, 1400,
												Formulae.getRepeatTimes(owner, 12)) {
											public void action() {
												if (option < 0 || option > (Constants.GameServer.MEMBER_WORLD ? 5
														: 4)) {
													owner.checkAndInterruptBatchEvent();
													return;
												}
												if (owner.getFatigue() >= 7500) {
													owner.message("You are too tired to craft");
													interrupt();
													return;
												}
												if (option != 0
														&& owner.getInventory()
														.countId(
																gems[option]) < 1) {
													owner.message("You don't have a "
															+ reply
															+ ".");
													owner.checkAndInterruptBatchEvent();
													return;
												}
												ItemCraftingDef def = EntityHandler
														.getCraftingDef((option * 3)
																+ craftType);
												if (def == null) {
													owner.message(
															"Nothing interesting happens");
													owner.checkAndInterruptBatchEvent();
													return;
												}
												if (owner.getSkills().getLevel(12) < def
														.getReqLevel()) {
													owner.message("You need a crafting skill level of "
															+ def.getReqLevel()
															+ " to make this");
													owner.checkAndInterruptBatchEvent();
													return;
												}
												if (owner.getInventory()
														.remove(item) > -1
														&& (option == 0 || owner
														.getInventory()
														.remove(gems[option],
																1) > -1)) {
													showBubble(owner, item);
													Item result = null;
													if (item.getID() == 691
															&& option == 3
															&& craftType == 0) { 
														result = new Item(
																692, 1);
													} else if (item.getID() == 691
															&& option == 3
															&& craftType == 1) {
														result = new Item(
																693, 1);
													} else {
														result = new Item(
																def.getItemID(),
																1);
													}
													owner.message(
															"You make a "
																	+ result.getDef()
																	.getName());
													owner.getInventory().add(
															result);
													owner.incExp(12,
															def.getExp(), true);
												} else {
													owner.message("You don't have a "
															+ reply
															+ ".");
													owner.checkAndInterruptBatchEvent();
												}
											}
										});
									}
								});
								ActionSender.sendMenu(owner, options);
							}
						});
						ActionSender.sendMenu(owner, options);
					}
				});
				return;
			}
			if (item.getID() == 384) { // Silver Bar (Crafting)
				Server.getServer().getEventHandler().add(new MiniEvent(owner) {
					public void action() {
						owner.message(
								"What would you like to make?");
						String[] options = new String[] { "Holy Symbol of Saradomin", "Unholy symbol of Zamorak" };
						owner.setMenuHandler(new MenuOptionListener(options) {
							public void handleReply(final int option,
									String reply) {
								if (owner.isBusy() || option < 0 || option > 1) {
									return;
								}
								int[] moulds = { 386, 1026 };
								final int[] results = { 44, 1027 };
								if (owner.getInventory()
										.countId(moulds[option]) < 1) {
									owner.message(
											"You need a "
													+ EntityHandler.getItemDef(
															moulds[option])
													.getName()
													+ " to make a " + reply + "!");
									return;
								}

								owner.setBatchEvent(new BatchEvent(owner, 1400,
										Formulae.getRepeatTimes(owner, 12)) {

									@Override
									public void action() {
										if (owner.getSkills().getLevel(12) < 16) {
											owner.message("You need a crafting skill of level 16 to make this");
											interrupt();
											return;
										}
										if (owner.getInventory().remove(item) > -1) {
											showBubble(owner, item);
											Item result = new Item(
													results[option]);
											owner.message("You make a "
													+ result.getDef()
													.getName());
											owner.getInventory().add(result);
											owner.incExp(12, 200, true);
										} else {
											interrupt();
										}
									}

								});

							}
						});
						ActionSender.sendMenu(owner, options);
					}
				});
				return;
			} else if (item.getID() == 625) { // Sand (Glass)
				if (owner.getInventory().countId(624) < 1) {
					owner.message(
							"You need some soda ash to make glass");
					return;
				}
				owner.setBusy(true);
				showBubble(owner, item);
				owner.message("you heat the sand and soda ash in the furnace to make glass");
				Server.getServer().getEventHandler()
				.add(new ShortEvent(owner) {
					public void action() {
						if (owner.getInventory().remove(624, 1) > -1
								&& owner.getInventory().remove(item) > -1) {
							owner.getInventory().add(
									new Item(623, 1));
							owner.getInventory().add(
									new Item(21, 1));
							owner.incExp(12, 80, true);
						}
						owner.setBusy(false);
					}
				});
				return;
			}
			break;
		}
		return;
	}

	@Override
	public void onInvUseOnItem(Player player, Item item1, Item item2) {
		if (item1.getID() == 167 && doCutGem(player, item1, item2)) {
			return;
		} else if (item2.getID() == 167 && doCutGem(player, item2, item1)) {
			return;
		} else if (item1.getID() == 621 && doGlassBlowing(player, item1, item2)) {
			return;
		} else if (item2.getID() == 621 && doGlassBlowing(player, item2, item1)) {
			return;
		}
		if (item1.getID() == 39 && makeLeather(player, item1, item2)) {
			return;
		} else if (item2.getID() == 39 && makeLeather(player, item2, item1)) {
			return;
		} else if (item1.getID() == 207 && useWool(player, item1, item2)) {
			return;
		} else if (item2.getID() == 207 && useWool(player, item2, item1)) {
			return;
		} else if ((item1.getID() == 50 || item1.getID() == 141 || item1
				.getID() == 342) && useWater(player, item1, item2)) {
			return;
		} else if ((item2.getID() == 50 || item2.getID() == 141 || item2
				.getID() == 342) && useWater(player, item2, item1)) {
			return;
		} else if (item1.getID() == 623 && item2.getID() == 1017
				|| item1.getID() == 1017 && item2.getID() == 623) {
			if (player.getSkills().getLevel(12) < 10) {
				player.message(
						"You need a crafting level of 10 to make the lens");
				return;
			}
			if (player.getInventory().remove(new Item(623)) > -1) {
				player.message("You pour the molten glass into the mould");
				player.message("And clasp it together");
				player.message("It produces a small convex glass disc");
				player.getInventory().add(new Item(1018));
			}
			return;
		}
		return;
	}

	private boolean doCutGem(Player player, final Item chisel,
			final Item gem) {
		final ItemGemDef gemDef = EntityHandler.getItemGemDef(gem.getID());
		if (gemDef == null) {
			return false;
		}

		player.setBatchEvent(new BatchEvent(player, 650, Formulae.getRepeatTimes(player, 12)) {
			@Override
			public void action() {
				if (owner.getSkills().getLevel(12) < gemDef.getReqLevel()) {
					owner.message(
							"You need a crafting level of " + gemDef.getReqLevel()
							+ " to cut " + gem.getDef().getName().toLowerCase().replace("uncut ", "") + "s");
					interrupt();
					return;
				}
				if (owner.getInventory().remove(gem) > -1) {
					Item cutGem = new Item(gemDef.getGemID(), 1);
					/** Jade, Opal and red topaz fail handler - 25% chance to fail **/
					if((gem.getID() == 889 || gem.getID() == 890 || gem.getID() == 891) && DataConversions.random(0, 3) == 2) {
						owner.message("You miss hit the chisel and smash the " + cutGem.getDef().getName() + " to pieces!");
						owner.getInventory().add(new Item(915));
						owner.incExp(12, (gem.getID() == 889 ? 25 : gem.getID() == 890 ? 20 : 15), true);
					} else {
						owner.message("You cut the " + cutGem.getDef().getName().toLowerCase());
						owner.playSound("chisel");
						owner.getInventory().add(cutGem);
						owner.incExp(12, gemDef.getExp(), true);
					}
				} else {
					interrupt();
				}
			}
		});
		return true;
	}

	private boolean doGlassBlowing(Player player, final Item pipe,
			final Item glass) {
		if (glass.getID() != 623) {
			return false;
		}
		player.message("what would you like to make?");
		Server.getServer().getEventHandler().add(new MiniEvent(player) {
			public void action() {
				String[] options = new String[] { "Vial", "orb", "Beer glass" };
				owner.setMenuHandler(new MenuOptionListener(options) {
					public void handleReply(final int option, final String reply) {
						Item result;
						int reqLvl, exp;
						switch (option) {
						case 0:
							result = new Item(465, 1);
							reqLvl = 33;
							exp = 140;
							break;
						case 1:
							result = new Item(611, 1);
							reqLvl = 46;
							exp = 210;
							break;
						case 2:
							result = new Item(620, 1);
							reqLvl = 3;
							exp = 70;
							break;
						default:
							return;
						}
						if (owner.getSkills().getLevel(12) < reqLvl) {
							owner.message(
									"You need a crafting level of " + reqLvl
									+ " to make a "
									+ result.getDef().getName() + ".");
							return;
						}
						if (owner.getInventory().remove(glass) > -1) {
							owner.message(
									"You make a " + result.getDef().getName());
							owner.getInventory().add(result);
							owner.incExp(12, exp, true);
						}
					}
				});
				ActionSender.sendMenu(owner, options);
			}
		});
		return true;
	}

	private boolean makeLeather(Player player, final Item needle,
			final Item leather) {
		if (leather.getID() != 148) {
			return false;
		}
		if (player.getInventory().countId(43) < 1) {
			player.message(
					"You need some thread to make anything out of leather");
			return true;
		}
		if (DataConversions.random(0, 5) == 0) {
			player.getInventory().remove(43, 1);
		}
		Server.getServer().getEventHandler().add(new MiniEvent(player) {
			public void action() {
				String[] options = new String[] { "Armour", "Gloves", "Boots",
				"Cancel" };
				owner.setMenuHandler(new MenuOptionListener(options) {
					public void handleReply(final int option, final String reply) {
						Item result;
						int reqLvl, exp;
						switch (option) {
						case 0:
							result = new Item(15, 1);
							reqLvl = 14;
							exp = 100;
							break;
						case 1:
							result = new Item(16, 1);
							reqLvl = 1;
							exp = 55;
							break;
						case 2:
							result = new Item(17, 1);
							reqLvl = 7;
							exp = 65;
							break;
						default:
							return;
						}
						if (owner.getSkills().getLevel(12) < reqLvl) {
							owner.message(
									"You need a crafting level of " + reqLvl
									+ " to make "
									+ result.getDef().getName() + ".");
							return;
						}
						if (owner.getInventory().remove(leather) > -1) {
							owner.message(
									"You make some "
											+ result.getDef().getName());
							owner.getInventory().add(result);
							owner.incExp(12, exp, true);
						}
					}
				});
				ActionSender.sendMenu(owner, options);
			}
		});
		return true;
	}

	private boolean useWool(Player player, final Item woolBall,
			final Item item) {
		int newID;
		switch (item.getID()) {
		case 44: // Holy Symbol of saradomin
			newID = 45;
			break;
		case 1027: // Unholy Symbol of Zamorak
			newID = 1028;
			break;
		case 296: // Gold Amulet
			newID = 301;
			break;
		case 297: // Sapphire Amulet
			newID = 302;
			break;
		case 298: // Emerald Amulet
			newID = 303;
			break;
		case 299: // Ruby Amulet
			newID = 304;
			break;
		case 300: // Diamond Amulet
			newID = 305;
			break;
		case 524: // Dragonstone Amulet
			newID = 610;
			break;
		default:
			return false;
		}
		final int newId = newID;
		player.setBatchEvent(new BatchEvent(player, 650,
				Formulae.getRepeatTimes(player,12)) {
			@Override
			public void action() {
				if(owner.getInventory().countId(item.getID()) <= 0 || owner.getInventory().countId(207) <= 0) {
					interrupt();
					return;
				}
				if (owner.getInventory().remove(woolBall) > -1 && owner.getInventory().remove(item) > -1) {
					owner.message("You put some string on your " + item.getDef().getName().toLowerCase());
					owner.getInventory().add(new Item(newId, 1));
				} else {
					interrupt();
				}
			}
		});
		return true;
	}

	private boolean useWater(Player player, final Item water,
			final Item item) {
		int jugID = Formulae.getEmptyJug(water.getID());
		if (jugID == -1) { // This shouldn't happen
			return false;
		}
		switch (item.getID()) {
		case 149: // Clay
			if (player.getInventory().remove(water) > -1
					&& player.getInventory().remove(item) > -1) {
				message(player, 1200, "You mix the clay and water");
				player.message("You now have some soft workable clay");
				player.getInventory().add(new Item(jugID, 1));
				player.getInventory().add(new Item(243, 1));
			}
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		if (item1.getID() == 167 && doCutGem(player, item1, item2)) {
			return true;
		} else if (item2.getID() == 167 && doCutGem(player, item2, item1)) {
			return true;
		} else if (item1.getID() == 621) {
			return true;
		} else if (item2.getID() == 621) {
			return true;
		} else if (item1.getID() == 39) {
			return true;
		} else if (item2.getID() == 39) {
			return true;
		} else if (item1.getID() == 207) {
			return true;
		} else if (item2.getID() == 207) {
			return true;
		} else if ((item1.getID() == 50 || item1.getID() == 141 || item1
				.getID() == 342) && item2.getID() == 149) {
			return true;
		} else if ((item2.getID() == 50 || item2.getID() == 141 || item2
				.getID() == 342) && item1.getID() == 149) {
			return true;
		} else if (item1.getID() == 623 && item2.getID() == 1017
				|| item1.getID() == 1017 && item2.getID() == 623) {
			return true;
		}

		return false;
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		if ((obj.getID() == 118 || obj.getID() == 813)
				&& (item.getID() == 384 || item.getID() == 172 || item.getID() == 625)
				|| item.getID() == 691) {
			return true;
		}
		return false;
	}
}
