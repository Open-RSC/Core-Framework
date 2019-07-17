package com.openrsc.server.plugins.quests.members.legendsquest.obstacles;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.InvUseOnWallObjectListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnWallObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.plugins.quests.members.legendsquest.npcs.LegendsQuestUngadulu;
import com.openrsc.server.plugins.quests.members.shilovillage.ShiloVillageUtils;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.createGroundItem;
import static com.openrsc.server.plugins.Functions.doWallMovePlayer;
import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.spawnNpc;

public class LegendsQuestWallObjects implements WallObjectActionListener, WallObjectActionExecutiveListener, InvUseOnWallObjectListener, InvUseOnWallObjectExecutiveListener {

	public static final int FLAME_WALL = 210;
	public static final int RUT = 206;
	public static final int ANCIENT_WALL = 212;
	public static final int RUINED_WALL = 211;

	/**
	 * Right click options
	 **/
	public static final int TOUCH = 0;
	public static final int INVESTIGATE = 1;
	public static final int USE = 0;
	public static final int SEARCH = 1;

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		return inArray(obj.getID(), FLAME_WALL, RUT, ANCIENT_WALL, RUINED_WALL);
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == ANCIENT_WALL) {
			if (click == USE) {
				if ((p.getCache().hasKey("ancient_wall_runes") && p.getCache().getInt("ancient_wall_runes") == 5) || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
					message(p, 1300, "You walk into the darkness of the magical doorway.",
						"You walk for a short way before pushing open another door.");
					if (obj.getX() == 464 && obj.getY() == 3721) {
						p.message("You appear in a large cavern like room filled with pools of water.");
						p.teleport(467, 3724);
					} else {
						message(p, 1300, "You appear in a small walled cavern ");
						p.message("There seems to be an exit to the south east.");
						p.teleport(463, 3720);
					}
				} else {
					message(p, 1300, "You see no way to use that...");
					p.message("Perhaps you should search it?");
				}
			} else if (click == SEARCH) {
				message(p, 1300, "You search the wall...");
				if ((p.getCache().hasKey("ancient_wall_runes") && p.getCache().getInt("ancient_wall_runes") == 5) || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
					message(p, 1300, "You find the word 'SMELL' marked on the wall.",
						"The outline of a door appears on the wall.");
					p.message("What would you like to do?.");
					int option = showMenu(p,
						"Read the message on the wall.",
						"Investigate the outline of the door.");
					if (option == 0) {
						ActionSender.sendBox(p, "Place the five in order to pass % %or your life will dwindle until the last% %All five are stones of magical power% %Place them wrong and your fate will sour% %First is of the spirit of man or beast% %Second is the place where thoughts are born% %Third is the soil from which good things grow% %Four and five are the rules all men should know% %All put together make the word of a basic sense% %And from perspective help make maps from indifference.", true);
					} else if (option == 1) {
						ancientDoorWalkThrough(p, obj);
					}
				} else {
					message(p, 1300, "You find five slightly round depressions and some strange markings..",
						"There is a lot of dirt and mould growing over the markings, but you clear it out.",
						"After a while you manage to see that it is some form of message.",
						"Would you like to read it.");
					int menu = showMenu(p,
						"Yes, I'll read it.",
						"No, I won't read it.");
					if (menu == 0) {
						ActionSender.sendBox(p, "Place the five in order to pass % %or your life will dwindle until the last% %All five are stones of magical power% %Place them wrong and your fate will sour% %First is of the spirit of man or beast% %Second is the place where thoughts are born% %Third is the soil from which good things grow% %Four and five are the rules all men should know% %All put together make the word of a basic sense% %And from perspective help make maps from indifference.", true);
					} else if (menu == 1) {
						p.message("You decide against reading the message.");
					}
				}
			}
		}
		else if (obj.getID() == RUINED_WALL) {
			if (getCurrentLevel(p, SKILLS.AGILITY.id()) < 50) {
				p.message("You need an agility level of 50 to jump this wall");
				p.setBusy(false);
				return;
			}
			message(p, 1300, "You take a run at the wall...");
			if (ShiloVillageUtils.succeed(p, 50)) {
				message(p, 1300, "You take a good run up and sail majestically over the wall.",
					"You land perfectly and stand ready for action.");
			} else {
				message(p, 1300, "You fail to jump the wall properly and clip the wall with your leg.",
					"You're spun around mid air and hit the floor heavily.",
					"The fall knocks the wind out of you.");
				p.damage(6);
				playerTalk(p, null, "Ughhh!");
			}
			if (p.getY() >= 3729) {
				p.teleport(457, 3727);
			} else {
				p.teleport(455, 3729);
			}
		}
		else if (obj.getID() == FLAME_WALL) {
			if (hasItem(p, ItemId.MAGICAL_FIRE_PASS.id())) {
				doWallMovePlayer(obj, p, 210, 5000, false);
				p.message("You feel completely fine to walk through these flames..");
				return;
			} else {
				if (click == TOUCH) {
					message(p, "You walk blindly into the intense heat of the supernatural flames.");
					if (DataConversions.random(0, 9) <= 3) {
						message(p, 1300, "The heat is so intense that it burns you.");
						p.damage((int) Math.ceil((double) p.getSkills().getLevel(SKILLS.HITS.id()) / 10 + 1));
						playerTalk(p, null, "Owwww!");
					} else {
						message(p, 1300, "The heat is intense and just before you burn yourself,",
							"you pull your hand out of the way of the flame.");
						playerTalk(p, null, "Whew!");
					}
				} else if (click == INVESTIGATE) {
					switch (p.getQuestStage(Constants.Quests.LEGENDS_QUEST)) {
						case 2:
						case 3:
						case 4:
						case 5:
						case 6:
						case 7:
						case 8:
						case 9:
						case 10:
						case 11:
						case -1:
							message(p, 1300, "You look closely at the flames, they seem to form a straight wall.",
								"Something about them looks very strange, they look completely supernatural.",
								"For example, they seem to appear to come from straight out of the ground.");
							playerTalk(p, null, "Mmmm, pretty!");
							if (p.getX() >= 450 && p.getX() <= 455 && p.getY() >= 3704 && p.getY() <= 3711
								|| p.getX() == 456 && p.getY() >= 3707 && p.getY() <= 3708
								|| p.getX() == 449 && p.getY() >= 3707 && p.getY() <= 3708) {
								p.message("What would you like to do?");
								int leave = showMenu(p,
									"Leap out of the flaming Octagram...",
									"Attract Shamans's attention.");
								if (leave == 0) {
									message(p, 1300, "This is quite dangerous, but you find a suitable location to jump.");
									p.teleport(453, 3705);
									sleep(1300);
									message(p, 1300, "You take a run up...");
									int burnDegRnd = DataConversions.random(0, 5);
									if (burnDegRnd <= 2) {
										message(p, 1300, "You sail over the tops of the flames, just getting slightly burnt by the flames...");
										p.damage(DataConversions.random(3,7));
									}
									else if (burnDegRnd <= 4) {
										message(p, 1300, "You get severly burned as you jump across the flames...");
										p.damage(DataConversions.random(8,17));
									}
									else if (burnDegRnd <= 5) {
										message(p, 1300, "You get severly burned as you jump across the flames...",
												"You feel very un well..");
										p.damage(DataConversions.random(18,37));
									}
									p.teleport(455, 3702);
								} else if (leave == 1) {
									Npc ungadulu = getNearestNpc(p, NpcId.UNGADULU.id(), 8);
									if (ungadulu == null) {
										spawnNpc(NpcId.UNGADULU.id(), 453, 3707);
									}
									LegendsQuestUngadulu.ungaduluWallDialogue(p, ungadulu, -1);
								}
							} else {
								message(p, 1300, "You see a white clad figure in the midst of the flames...");
								Npc ungadulu = getNearestNpc(p, NpcId.UNGADULU.id(), 8);
								if (ungadulu == null) {
									spawnNpc(NpcId.UNGADULU.id(), 453, 3707);
								}
								LegendsQuestUngadulu.ungaduluWallDialogue(p, ungadulu, -1);
							}
							break;
					}
				}
			}
		}
		if (obj.getID() == RUT) {
			p.message("This looks like a rut has been etched into the ground.");
		}
	}

	@Override
	public boolean blockInvUseOnWallObject(GameObject obj, Item item, Player p) {
		return obj.getID() == FLAME_WALL || obj.getID() == ANCIENT_WALL;
	}

	@Override
	public void onInvUseOnWallObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == FLAME_WALL) {
			switch (ItemId.getById(item.getID())) {
				case BLESSED_GOLDEN_BOWL_WITH_PURE_WATER:
					p.message("You splash some pure water on the flames");
					if (!p.getCache().hasKey("douse_flames")) {
						p.getCache().set("douse_flames", 1);
					} else {
						int pourCount = p.getCache().getInt("douse_flames");
						p.getCache().set("douse_flames", pourCount + 1);
						if (pourCount >= 4) {
							p.getCache().remove("douse_flames");
							p.message("The pure water in the golden bowl has run out...");
							p.getInventory().replace(item.getID(), ItemId.BLESSED_GOLDEN_BOWL.id());
						}
					}
					p.message("You quickly walk over the doused flames.");
					doWallMovePlayer(obj, p, 206, 5000, true);
					break;
				case BUCKET_OF_WATER:
				case JUG_OF_WATER:
				case BOWL_OF_WATER:
				case VIAL:
				case GOLDEN_BOWL_WITH_PURE_WATER:
					message(p, 1300, "The water seems to evaporate in a cloud of steam",
						"before it gets anywhere near the flames.");
					break;
				default:
					p.message("Nothing interesting happens");
					break;
			}
		}
		else if (obj.getID() == ANCIENT_WALL) {
			switch (ItemId.getById(item.getID())) {
				case AIR_RUNE:
				case FIRE_RUNE:
				case BLOOD_RUNE:
				case WATER_RUNE:
				case COSMIC_RUNE:
				case NATURE_RUNE:
				case DEATH_RUNE:
				case BODY_RUNE:
				case LIFE_RUNE:
				case CHAOS_RUNE:
					if ((p.getCache().hasKey("ancient_wall_runes") && p.getCache().getInt("ancient_wall_runes") == 5) || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
						ancientDoorWalkThrough(p, obj);
					} else {
						runesFail(p, item);
					}
					break;
				case SOUL_RUNE:
					if ((p.getCache().hasKey("ancient_wall_runes") && p.getCache().getInt("ancient_wall_runes") == 5) || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
						ancientDoorWalkThrough(p, obj);
					} else if (!p.getCache().hasKey("ancient_wall_runes")) {
						removeItem(p, ItemId.SOUL_RUNE.id(), 1);
						message(p, 1300, "You slide the Soul-Rune into the first depression...",
							"It glows slightly and merges with the wall.",
							"The letter 'S' appears where the Soul-Rune merged with the door.");
						p.getCache().put("ancient_wall_runes", 1);
					} else {
						runesFail(p, item);
					}
					break;
				case MIND_RUNE:
					if ((p.getCache().hasKey("ancient_wall_runes") && p.getCache().getInt("ancient_wall_runes") == 5) || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
						ancientDoorWalkThrough(p, obj);
					} else if (p.getCache().hasKey("ancient_wall_runes") && p.getCache().getInt("ancient_wall_runes") == 1) {
						removeItem(p, ItemId.MIND_RUNE.id(), 1);
						message(p, 1300, "You slide the Mind-Rune into the second slot depression...",
							"It glows slightly and merges with the wall.",
							"The letter 'M' appears where the Mind-Rune merged with the door.");
						p.getCache().put("ancient_wall_runes", 2);
					} else {
						runesFail(p, item);
					}
					break;
				case EARTH_RUNE:
					if ((p.getCache().hasKey("ancient_wall_runes") && p.getCache().getInt("ancient_wall_runes") == 5) || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
						ancientDoorWalkThrough(p, obj);
					} else if (p.getCache().hasKey("ancient_wall_runes") && p.getCache().getInt("ancient_wall_runes") == 2) {
						removeItem(p, ItemId.EARTH_RUNE.id(), 1);
						message(p, 1300, "You slide the Earth-Rune into the third depression...",
							"It glows slightly and merges with the wall.",
							"The letter 'E' appears where the Earth-Rune merged with the door.");
						p.getCache().put("ancient_wall_runes", 3);
					} else {
						runesFail(p, item);
					}
					break;
				case LAW_RUNE:
					if ((p.getCache().hasKey("ancient_wall_runes") && p.getCache().getInt("ancient_wall_runes") == 5) || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == -1) {
						ancientDoorWalkThrough(p, obj);
					} else if (p.getCache().hasKey("ancient_wall_runes") && (p.getCache().getInt("ancient_wall_runes") == 3 || p.getCache().getInt("ancient_wall_runes") == 4)) {
						int getRuneCount = p.getCache().getInt("ancient_wall_runes");
						removeItem(p, ItemId.LAW_RUNE.id(), 1);
						if (getRuneCount == 4) {
							p.getCache().put("ancient_wall_runes", 5);
							message(p, 1300, "You slide the Law-Rune into the fifth depression...",
								"It glows slightly and merges with the wall.",
								"The letter 'L' appears where the Law-Rune merged with the door.");
							ancientDoorWalkThrough(p, obj);
						} else {
							message(p, 1300, "You slide the Law-Rune into the fourth depression...",
								"It glows slightly and merges with the wall.",
								"The letter 'L' appears where the Law-Rune merged with the door.");
							p.getCache().put("ancient_wall_runes", getRuneCount + 1);
						}
					} else {
						runesFail(p, item);
					}
					break;
				default:
					p.message("Nothing interesting happens");
					break;
			}
		}
	}

	private void ancientDoorWalkThrough(Player p, GameObject obj) {
		message(p, 1300, "You see a small door outline starting to form in the wall.",
			"And then a well formed door handle emerges, suddenly the door cracks open.");
		p.message("Would you like to go through?");
		int goThrough = showMenu(p,
			"Yes, I'll go through.",
			"No, I'll stay here.");
		if (goThrough == 0) {
			message(p, 1300, "You walk into the darkness of the magical doorway.",
				"You walk for a short way before pushing open another door.");
			if (obj.getX() == 464 && obj.getY() == 3721) {
				p.message("You appear in a large cavern like room filled with pools of water.");
				p.teleport(467, 3724);
			} else {
				message(p, 1300, "You appear in a small walled cavern ");
				p.message("There seems to be an exit to the south east.");
				p.teleport(463, 3720);
			}
		} else if (goThrough == 1) {
			p.message("You decide to stay where you are.");
		}
	}

	private void runesFail(Player p, Item item) {
		p.message("The rune stone burns red hot in your hand, you drop it to the floor.");
		p.damage(DataConversions.random(1, 5));
		removeItem(p, item.getID(), 1);
		createGroundItem(item.getID(), 1, p.getX(), p.getY(), p);
	}
}
