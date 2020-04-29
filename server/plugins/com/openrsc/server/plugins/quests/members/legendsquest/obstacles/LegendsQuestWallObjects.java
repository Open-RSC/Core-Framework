package com.openrsc.server.plugins.quests.members.legendsquest.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.UseBoundTrigger;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.quests.members.legendsquest.npcs.LegendsQuestUngadulu;
import com.openrsc.server.plugins.quests.members.shilovillage.ShiloVillageUtils;
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
					mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You walk into the darkness of the magical doorway.",
						"You walk for a short way before pushing open another door.");
					if (obj.getX() == 464 && obj.getY() == 3721) {
						player.message("You appear in a large cavern like room filled with pools of water.");
						player.teleport(467, 3724);
					} else {
						mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You appear in a small walled cavern ");
						player.message("There seems to be an exit to the south east.");
						player.teleport(463, 3720);
					}
				} else {
					mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You see no way to use that...");
					player.message("Perhaps you should search it?");
				}
			} else if (click == SEARCH) {
				mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You search the wall...");
				if ((player.getCache().hasKey("ancient_wall_runes") && player.getCache().getInt("ancient_wall_runes") == 5) || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
					mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You find the word 'SMELL' marked on the wall.",
						"The outline of a door appears on the wall.");
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
					mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You find five slightly round depressions and some strange markings..",
						"There is a lot of dirt and mould growing over the markings, but you clear it out.",
						"After a while you manage to see that it is some form of message.",
						"Would you like to read it.");
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
			if (getCurrentLevel(player, Skills.AGILITY) < 50) {
				player.message("You need an agility level of 50 to jump this wall");
				player.setBusy(false);
				return;
			}
			mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You take a run at the wall...");
			if (ShiloVillageUtils.succeed(player, 50)) {
				mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You take a good run up and sail majestically over the wall.",
					"You land perfectly and stand ready for action.");
			} else {
				mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You fail to jump the wall properly and clip the wall with your leg.",
					"You're spun around mid air and hit the floor heavily.",
					"The fall knocks the wind out of you.");
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
				doWallMovePlayer(obj, player, 210, player.getWorld().getServer().getConfig().GAME_TICK * 8, false);
				player.message("You feel completely fine to walk through these flames..");
				return;
			} else {
				if (click == TOUCH) {
					mes(player, "You walk blindly into the intense heat of the supernatural flames.");
					if (DataConversions.random(0, 9) <= 3) {
						mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "The heat is so intense that it burns you.");
						player.damage((int) Math.ceil((double) player.getSkills().getLevel(Skills.HITS) / 10 + 1));
						say(player, null, "Owwww!");
					} else {
						mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "The heat is intense and just before you burn yourself,",
							"you pull your hand out of the way of the flame.");
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
							mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You look closely at the flames, they seem to form a straight wall.",
								"Something about them looks very strange, they look completely supernatural.",
								"For example, they seem to appear to come from straight out of the ground.");
							say(player, null, "Mmmm, pretty!");
							if (player.getX() >= 450 && player.getX() <= 455 && player.getY() >= 3704 && player.getY() <= 3711
								|| player.getX() == 456 && player.getY() >= 3707 && player.getY() <= 3708
								|| player.getX() == 449 && player.getY() >= 3707 && player.getY() <= 3708) {
								player.message("What would you like to do?");
								int leave = multi(player,
									"Leap out of the flaming Octagram...",
									"Attract Shamans's attention.");
								if (leave == 0) {
									mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "This is quite dangerous, but you find a suitable location to jump.");
									player.teleport(453, 3705);
									delay(player.getWorld().getServer().getConfig().GAME_TICK * 2);
									mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You take a run up...");
									int burnDegRnd = DataConversions.random(0, 5);
									if (burnDegRnd <= 2) {
										mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You sail over the tops of the flames, just getting slightly burnt by the flames...");
										player.damage(DataConversions.random(3,7));
									}
									else if (burnDegRnd <= 4) {
										mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You get severly burned as you jump across the flames...");
										player.damage(DataConversions.random(8,17));
									}
									else if (burnDegRnd <= 5) {
										mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You get severly burned as you jump across the flames...",
												"You feel very un well..");
										player.damage(DataConversions.random(18,37));
									}
									player.teleport(455, 3702);
								} else if (leave == 1) {
									Npc ungadulu = ifnearvisnpc(player, NpcId.UNGADULU.id(), 8);
									if (ungadulu == null) {
										addnpc(player.getWorld(), NpcId.UNGADULU.id(), 453, 3707);
									}
									LegendsQuestUngadulu.ungaduluWallDialogue(player, ungadulu, -1);
								}
							} else {
								mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You see a white clad figure in the midst of the flames...");
								Npc ungadulu = ifnearvisnpc(player, NpcId.UNGADULU.id(), 8);
								if (ungadulu == null) {
									addnpc(player.getWorld(), NpcId.UNGADULU.id(), 453, 3707);
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
							player.getCarriedItems().getInventory().replace(item.getCatalogId(), ItemId.BLESSED_GOLDEN_BOWL.id());
						}
					}
					player.message("You quickly walk over the doused flames.");
					doWallMovePlayer(obj, player, 206, player.getWorld().getServer().getConfig().GAME_TICK * 8, true);
					break;
				case BUCKET_OF_WATER:
				case JUG_OF_WATER:
				case BOWL_OF_WATER:
				case VIAL:
				case GOLDEN_BOWL_WITH_PURE_WATER:
					mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "The water seems to evaporate in a cloud of steam",
						"before it gets anywhere near the flames.");
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
						mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You slide the Soul-Rune into the first depression...",
							"It glows slightly and merges with the wall.",
							"The letter 'S' appears where the Soul-Rune merged with the door.");
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
						mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You slide the Mind-Rune into the second slot depression...",
							"It glows slightly and merges with the wall.",
							"The letter 'M' appears where the Mind-Rune merged with the door.");
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
						mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You slide the Earth-Rune into the third depression...",
							"It glows slightly and merges with the wall.",
							"The letter 'E' appears where the Earth-Rune merged with the door.");
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
							mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You slide the Law-Rune into the fifth depression...",
								"It glows slightly and merges with the wall.",
								"The letter 'L' appears where the Law-Rune merged with the door.");
							ancientDoorWalkThrough(player, obj);
						} else {
							mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You slide the Law-Rune into the fourth depression...",
								"It glows slightly and merges with the wall.",
								"The letter 'L' appears where the Law-Rune merged with the door.");
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
		mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You see a small door outline starting to form in the wall.",
			"And then a well formed door handle emerges, suddenly the door cracks open.");
		player.message("Would you like to go through?");
		int goThrough = multi(player,
			"Yes, I'll go through.",
			"No, I'll stay here.");
		if (goThrough == 0) {
			mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You walk into the darkness of the magical doorway.",
				"You walk for a short way before pushing open another door.");
			if (obj.getX() == 464 && obj.getY() == 3721) {
				player.message("You appear in a large cavern like room filled with pools of water.");
				player.teleport(467, 3724);
			} else {
				mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You appear in a small walled cavern ");
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
