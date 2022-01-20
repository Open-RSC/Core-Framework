package com.openrsc.server.plugins.authentic.quests.members.legendsquest.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.authentic.quests.members.legendsquest.npcs.LegendsQuestUngadulu;
import com.openrsc.server.plugins.authentic.quests.members.shilovillage.ShiloVillageUtils;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.triggers.UseBoundTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestWallObjects implements OpBoundTrigger, UseBoundTrigger {

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
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return inArray(obj.getID(), FLAME_WALL, RUT, ANCIENT_WALL, RUINED_WALL);
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == ANCIENT_WALL) {
			if (click == USE) {
				if ((player.getCache().hasKey("ancient_wall_runes") && player.getCache().getInt("ancient_wall_runes") == 5) || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
					mes("You walk into the darkness of the magical doorway.");
					delay(2);
					mes("You walk for a short way before pushing open another door.");
					delay(2);
					if (obj.getX() == 464 && obj.getY() == 3721) {
						player.message("You appear in a large cavern like room filled with pools of water.");
						player.teleport(467, 3724);
					} else {
						mes("You appear in a small walled cavern ");
						delay(2);
						player.message("There seems to be an exit to the south east.");
						player.teleport(463, 3720);
					}
				} else {
					mes("You see no way to use that...");
					delay(2);
					player.message("Perhaps you should search it?");
				}
			} else if (click == SEARCH) {
				mes("You search the wall...");
				delay(2);
				if ((player.getCache().hasKey("ancient_wall_runes") && player.getCache().getInt("ancient_wall_runes") == 5) || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
					mes("You find the word 'SMELL' marked on the wall.");
					delay(2);
					mes("The outline of a door appears on the wall.");
					delay(2);
					player.message("What would you like to do?.");
					int option = multi(player,
						"Read the message on the wall.",
						"Investigate the outline of the door.");
					if (option == 0) {
						ActionSender.sendBox(player, "Place the five in order to pass % %or your life will dwindle until the last% %All five are stones of magical power% %Place them wrong and your fate will sour% %First is of the spirit of man or beast% %Second is the place where thoughts are born% %Third is the soil from which good things grow% %Four and five are the rules all men should know% %All put together make the word of a basic sense% %And from perspective help make maps from indifference.", true);
					} else if (option == 1) {
						ancientDoorWalkThrough(player, obj);
					}
				} else {
					mes("You find five slightly round depressions and some strange markings..");
					delay(2);
					mes("There is a lot of dirt and mould growing over the markings, but you clear it out.");
					delay(2);
					mes("After a while you manage to see that it is some form of message.");
					delay(2);
					mes("Would you like to read it.");
					delay(2);
					int menu = multi(player,
						"Yes, I'll read it.",
						"No, I won't read it.");
					if (menu == 0) {
						ActionSender.sendBox(player, "Place the five in order to pass % %or your life will dwindle until the last% %All five are stones of magical power% %Place them wrong and your fate will sour% %First is of the spirit of man or beast% %Second is the place where thoughts are born% %Third is the soil from which good things grow% %Four and five are the rules all men should know% %All put together make the word of a basic sense% %And from perspective help make maps from indifference.", true);
					} else if (menu == 1) {
						player.message("You decide against reading the message.");
					}
				}
			}
		}
		else if (obj.getID() == RUINED_WALL) {
			if (getCurrentLevel(player, Skill.AGILITY.id()) < 50) {
				player.message("You need an agility level of 50 to jump this wall");
				return;
			}
			mes("You take a run at the wall...");
			delay(2);
			if (ShiloVillageUtils.succeed(player, 50)) {
				mes("You take a good run up and sail majestically over the wall.");
				delay(2);
				mes("You land perfectly and stand ready for action.");
				delay(2);
			} else {
				mes("You fail to jump the wall properly and clip the wall with your leg.");
				delay(2);
				mes("You're spun around mid air and hit the floor heavily.");
				delay(2);
				mes("The fall knocks the wind out of you.");
				delay(2);
				player.damage(6);
				say(player, null, "Ughhh!");
			}
			if (player.getY() >= 3729) {
				player.teleport(457, 3727);
			} else {
				player.teleport(455, 3729);
			}
		}
		else if (obj.getID() == FLAME_WALL) {
			if (player.getCarriedItems().hasCatalogID(ItemId.MAGICAL_FIRE_PASS.id(), Optional.of(false))) {
				doWallMovePlayer(obj, player, 210, config().GAME_TICK * 8, false);
				player.message("You feel completely fine to walk through these flames..");
				return;
			} else {
				if (click == TOUCH) {
					mes("You walk blindly into the intense heat of the supernatural flames.");
					delay(3);
					if (DataConversions.random(0, 9) <= 3) {
						mes("The heat is so intense that it burns you.");
						delay(2);
						player.damage((int) Math.ceil((double) player.getSkills().getLevel(Skill.HITS.id()) / 10 + 1));
						say(player, null, "Owwww!");
					} else {
						mes("The heat is intense and just before you burn yourself,");
						delay(2);
						mes("you pull your hand out of the way of the flame.");
						delay(2);
						say(player, null, "Whew!");
					}
				} else if (click == INVESTIGATE) {
					switch (player.getQuestStage(Quests.LEGENDS_QUEST)) {
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
							mes("You look closely at the flames, they seem to form a straight wall.");
							delay(2);
							mes("Something about them looks very strange, they look completely supernatural.");
							delay(2);
							mes("For example, they seem to appear to come from straight out of the ground.");
							delay(2);
							say(player, null, "Mmmm, pretty!");
							if (player.getX() >= 450 && player.getX() <= 455 && player.getY() >= 3704 && player.getY() <= 3711
								|| player.getX() == 456 && player.getY() >= 3707 && player.getY() <= 3708
								|| player.getX() == 449 && player.getY() >= 3707 && player.getY() <= 3708) {
								player.message("What would you like to do?");
								int leave = multi(player,
									"Leap out of the flaming Octagram...",
									"Attract Shamans's attention.");
								if (leave == 0) {
									mes("This is quite dangerous, but you find a suitable location to jump.");
									delay(2);
									player.teleport(453, 3705);
									delay(2);
									mes("You take a run up...");
									delay(2);
									int burnDegRnd = DataConversions.random(0, 5);
									if (burnDegRnd <= 2) {
										mes("You sail over the tops of the flames, just getting slightly burnt by the flames...");
										delay(2);
										player.damage(DataConversions.random(3,7));
									}
									else if (burnDegRnd <= 4) {
										mes("You get severly burned as you jump across the flames...");
										delay(2);
										player.damage(DataConversions.random(8,17));
									}
									else if (burnDegRnd <= 5) {
										mes("You get severly burned as you jump across the flames...");
										delay(2);
										mes("You feel very un well..");
										delay(2);
										player.damage(DataConversions.random(18,37));
									}
									player.teleport(455, 3702);
								} else if (leave == 1) {
									Npc ungadulu = ifnearvisnpc(player, NpcId.UNGADULU.id(), 8);
									if (ungadulu == null) {
										addnpc(player.getWorld(), NpcId.UNGADULU.id(), 453, 3707, 60000 * 5);
									}
									LegendsQuestUngadulu.ungaduluWallDialogue(player, ungadulu, -1);
								}
							} else {
								mes("You see a white clad figure in the midst of the flames...");
								delay(2);
								Npc ungadulu = ifnearvisnpc(player, NpcId.UNGADULU.id(), 8);
								if (ungadulu == null) {
									addnpc(player.getWorld(), NpcId.UNGADULU.id(), 453, 3707, 60000 * 5);
								}
								LegendsQuestUngadulu.ungaduluWallDialogue(player, ungadulu, -1);
							}
							break;
					}
				}
			}
		}
		if (obj.getID() == RUT) {
			player.message("This looks like a rut has been etched into the ground.");
		}
	}

	@Override
	public boolean blockUseBound(Player player, GameObject obj, Item item) {
		return obj.getID() == FLAME_WALL || obj.getID() == ANCIENT_WALL;
	}

	@Override
	public void onUseBound(Player player, GameObject obj, Item item) {
		if (obj.getID() == FLAME_WALL) {
			switch (ItemId.getById(item.getCatalogId())) {
				case BLESSED_GOLDEN_BOWL_WITH_PURE_WATER:
					player.message("You splash some pure water on the flames");
					if (!player.getCache().hasKey("douse_flames")) {
						player.getCache().set("douse_flames", 1);
					} else {
						int pourCount = player.getCache().getInt("douse_flames");
						player.getCache().set("douse_flames", pourCount + 1);
						if (pourCount >= 4) {
							player.getCache().remove("douse_flames");
							player.message("The pure water in the golden bowl has run out...");
							player.getCarriedItems().remove(new Item(item.getCatalogId()));
							player.getCarriedItems().getInventory().add(new Item(ItemId.BLESSED_GOLDEN_BOWL.id()));
						}
					}
					player.message("You quickly walk over the doused flames.");
					doWallMovePlayer(obj, player, 206, config().GAME_TICK * 8, true);
					break;
				case BUCKET_OF_WATER:
				case JUG_OF_WATER:
				case BOWL_OF_WATER:
				case VIAL:
				case GOLDEN_BOWL_WITH_PURE_WATER:
					mes("The water seems to evaporate in a cloud of steam");
					delay(2);
					mes("before it gets anywhere near the flames.");
					delay(2);
					break;
				default:
					player.message("Nothing interesting happens");
					break;
			}
		}
		else if (obj.getID() == ANCIENT_WALL) {
			switch (ItemId.getById(item.getCatalogId())) {
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
					if ((player.getCache().hasKey("ancient_wall_runes") && player.getCache().getInt("ancient_wall_runes") == 5) || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
						ancientDoorWalkThrough(player, obj);
					} else {
						runesFail(player, item);
					}
					break;
				case SOUL_RUNE:
					if ((player.getCache().hasKey("ancient_wall_runes") && player.getCache().getInt("ancient_wall_runes") == 5) || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
						ancientDoorWalkThrough(player, obj);
					} else if (!player.getCache().hasKey("ancient_wall_runes")) {
						player.getCarriedItems().remove(new Item(ItemId.SOUL_RUNE.id()));
						mes("You slide the Soul-Rune into the first depression...");
						delay(2);
						mes("It glows slightly and merges with the wall.");
						delay(2);
						mes("The letter 'S' appears where the Soul-Rune merged with the door.");
						delay(2);
						player.getCache().put("ancient_wall_runes", 1);
					} else {
						runesFail(player, item);
					}
					break;
				case MIND_RUNE:
					if ((player.getCache().hasKey("ancient_wall_runes") && player.getCache().getInt("ancient_wall_runes") == 5) || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
						ancientDoorWalkThrough(player, obj);
					} else if (player.getCache().hasKey("ancient_wall_runes") && player.getCache().getInt("ancient_wall_runes") == 1) {
						player.getCarriedItems().remove(new Item(ItemId.MIND_RUNE.id()));
						mes("You slide the Mind-Rune into the second slot depression...");
						delay(2);
						mes("It glows slightly and merges with the wall.");
						delay(2);
						mes("The letter 'M' appears where the Mind-Rune merged with the door.");
						delay(2);
						player.getCache().put("ancient_wall_runes", 2);
					} else {
						runesFail(player, item);
					}
					break;
				case EARTH_RUNE:
					if ((player.getCache().hasKey("ancient_wall_runes") && player.getCache().getInt("ancient_wall_runes") == 5) || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
						ancientDoorWalkThrough(player, obj);
					} else if (player.getCache().hasKey("ancient_wall_runes") && player.getCache().getInt("ancient_wall_runes") == 2) {
						player.getCarriedItems().remove(new Item(ItemId.EARTH_RUNE.id()));
						mes("You slide the Earth-Rune into the third depression...");
						delay(2);
						mes("It glows slightly and merges with the wall.");
						delay(2);
						mes("The letter 'E' appears where the Earth-Rune merged with the door.");
						delay(2);
						player.getCache().put("ancient_wall_runes", 3);
					} else {
						runesFail(player, item);
					}
					break;
				case LAW_RUNE:
					if ((player.getCache().hasKey("ancient_wall_runes") && player.getCache().getInt("ancient_wall_runes") == 5) || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
						ancientDoorWalkThrough(player, obj);
					} else if (player.getCache().hasKey("ancient_wall_runes") && (player.getCache().getInt("ancient_wall_runes") == 3 || player.getCache().getInt("ancient_wall_runes") == 4)) {
						int getRuneCount = player.getCache().getInt("ancient_wall_runes");
						player.getCarriedItems().remove(new Item(ItemId.LAW_RUNE.id()));
						if (getRuneCount == 4) {
							player.getCache().put("ancient_wall_runes", 5);
							mes("You slide the Law-Rune into the fifth depression...");
							delay(2);
							mes("It glows slightly and merges with the wall.");
							delay(2);
							mes("The letter 'L' appears where the Law-Rune merged with the door.");
							delay(2);
							ancientDoorWalkThrough(player, obj);
						} else {
							mes("You slide the Law-Rune into the fourth depression...");
							delay(2);
							mes("It glows slightly and merges with the wall.");
							delay(2);
							mes("The letter 'L' appears where the Law-Rune merged with the door.");
							delay(2);
							player.getCache().put("ancient_wall_runes", getRuneCount + 1);
						}
					} else {
						runesFail(player, item);
					}
					break;
				default:
					player.message("Nothing interesting happens");
					break;
			}
		}
	}

	private void ancientDoorWalkThrough(Player player, GameObject obj) {
		mes("You see a small door outline starting to form in the wall.");
		delay(2);
		mes("And then a well formed door handle emerges, suddenly the door cracks open.");
		delay(2);
		player.message("Would you like to go through?");
		int goThrough = multi(player,
			"Yes, I'll go through.",
			"No, I'll stay here.");
		if (goThrough == 0) {
			mes("You walk into the darkness of the magical doorway.");
			delay(2);
			mes("You walk for a short way before pushing open another door.");
			delay(2);
			if (obj.getX() == 464 && obj.getY() == 3721) {
				player.message("You appear in a large cavern like room filled with pools of water.");
				player.teleport(467, 3724);
			} else {
				mes("You appear in a small walled cavern ");
				delay(2);
				player.message("There seems to be an exit to the south east.");
				player.teleport(463, 3720);
			}
		} else if (goThrough == 1) {
			player.message("You decide to stay where you are.");
		}
	}

	private void runesFail(Player player, Item item) {
		player.message("The rune stone burns red hot in your hand, you drop it to the floor.");
		player.damage(DataConversions.random(1, 5));
		player.getCarriedItems().remove(new Item(item.getCatalogId()));
		addobject(item.getCatalogId(), 1, player.getX(), player.getY(), player);
	}
}
