package com.openrsc.server.plugins.defaults;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.GameServer;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.Server;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.doDoor;
import static com.openrsc.server.plugins.Functions.doGate;
import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.replaceObjectDelayed;
import static com.openrsc.server.plugins.Functions.showBubble;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.spawnNpc;

/**
 * Does default action to unhandled(non-quest related) doors.
 *
 * @author n0m
 */
public class DoorAction {

	public boolean blockWallObjectAction(final GameObject obj,
										 final Integer click, final Player player) {

		if (obj.getDoorDef().name.toLowerCase().contains("door")
			|| obj.getDoorDef().name.equalsIgnoreCase("door")
			|| obj.getDoorDef().name.equalsIgnoreCase("odd looking wall")
			|| obj.getDoorDef().name.equalsIgnoreCase("doorframe")) {
			return true;
		}

		// Tutorial Doors
		if (obj.getID() == 75 && obj.getX() == 222 && obj.getY() == 743) {
			return true;
		} else if (obj.getID() == 76 && obj.getX() == 224 && obj.getY() == 737) {
			return true;
		} else if (obj.getID() == 77 && obj.getX() == 220 && obj.getY() == 727) {
			return true;
		} else if (obj.getID() == 78 && obj.getX() == 212 && obj.getY() == 729) {
			return true;
		} else if (obj.getID() == 80 && obj.getX() == 206 && obj.getY() == 730) {
			return true;
		} else if (obj.getID() == 81 && obj.getX() == 201 && obj.getY() == 734) {
			return true;
		} else if (obj.getID() == 82 && obj.getX() == 198 && obj.getY() == 746) {
			return true;
		} else if (obj.getID() == 83 && obj.getX() == 204 && obj.getY() == 752) {
			return true;
		} else if (obj.getID() == 84 && obj.getX() == 209 && obj.getY() == 754) {
			return true;
		} else if (obj.getID() == 85 && obj.getX() == 217 && obj.getY() == 760) {
			return true;
		} else if (obj.getID() == 88 && obj.getX() == 222 && obj.getY() == 760) {
			return true;
		} else if (obj.getID() == 89 && obj.getX() == 226 && obj.getY() == 760) {
			return true;
		} else if (obj.getID() == 90 && obj.getX() == 230 && obj.getY() == 759) {
			return true;
		}

		return false;
	}

	public void onWallObjectAction(final GameObject obj, final Integer click,
								   final Player p) {

		// Door's lock needs to be picked, or needs a key.
		if (blockInvUseOnWallObject(obj, null, p)) {
			p.message("The door is locked");
			return;
		}

		// Tutorial Doors
		if (obj.getID() == 75 && obj.getX() == 222 && obj.getY() == 743) {
			if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") >= 10) {
				doDoor(obj, p);
			} else {
				p.message("You should speak to a guide before going through this door");
			}
		} else if (obj.getID() == 76 && obj.getX() == 224 && obj.getY() == 737) {
			if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") >= 15) {
				doDoor(obj, p);
			} else {
				p.message("Speak to the controls guide before going through this door");
			}
		} else if (obj.getID() == 77 && obj.getX() == 220 && obj.getY() == 727) {
			if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") >= 25) {
				doDoor(obj, p);
			} else {
				p.message("Speak to the combat instructor before going through this door");
			}
		} else if (obj.getID() == 78 && obj.getX() == 212 && obj.getY() == 729) {
			if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") >= 35) {
				doDoor(obj, p);
			} else {
				p.message("You should speak to a cooking instructor before going through this door");
			}
		} else if (obj.getID() == 80 && obj.getX() == 206 && obj.getY() == 730) {
			if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") >= 40) {
				doDoor(obj, p);
			} else {
				p.message("You should speak to a finance advisor before going through this door");
			}
		} else if (obj.getID() == 81 && obj.getX() == 201 && obj.getY() == 734) {
			if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") >= 45) {
				doDoor(obj, p);
			} else {
				p.message("You should speak to the fishing instructor before going through this door");
			}
		} else if (obj.getID() == 82 && obj.getX() == 198 && obj.getY() == 746) {
			if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") >= 55) {
				doDoor(obj, p);
			} else {
				p.message("You should speak to the mining instructor before going through this door");
			}
		} else if (obj.getID() == 83 && obj.getX() == 204 && obj.getY() == 752) {
			if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") >= 60) {
				doDoor(obj, p);
			} else {
				p.message("You should speak to a bank assistant before going through this door");
			}
		} else if (obj.getID() == 84 && obj.getX() == 209 && obj.getY() == 754) {
			if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") >= 65) {
				doDoor(obj, p);
			} else {
				p.message("You should speak to the quest advisor before going through this door");
			}
		} else if (obj.getID() == 85 && obj.getX() == 217 && obj.getY() == 760) {
			if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") >= 70) {
				doDoor(obj, p);
			} else {
				p.message("You should speak to the wilderness guide before going through this door");
			}
		} else if (obj.getID() == 88 && obj.getX() == 222 && obj.getY() == 760) {
			if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") >= 80) {
				doDoor(obj, p);
			} else {
				p.message("You should speak to a magic instructor before going through this door");
			}
		} else if (obj.getID() == 89 && obj.getX() == 226 && obj.getY() == 760) {
			if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") >= 90) {
				doDoor(obj, p);
			} else {
				p.message("You should speak to a fatigue expert before going through this door");
			}
		} else if (obj.getID() == 90 && obj.getX() == 230 && obj.getY() == 759) {
			if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") >= 100) {
				doDoor(obj, p);
			} else {
				p.message("You should speak to the community instructor before going through this door");
			}
		}


		switch (obj.getID()) {

			case 54: // Dragon Slayer: Door out of maze near entrance
				if (p.getX() == 339) {
					doDoor(obj, p);
				} else {
					p.message("this door is locked");
				}
				break;

			case 154: // Grand Tree: main door (outside)
				message(p, 1900, "you open the door");
				p.teleport(703, 455);
				p.message("and walk through");
				break;

			case 153: // Grand Tree: main door (inside)
				message(p, 1900, "you open the door");
				p.teleport(416, 165);
				p.message("and walk through");
				break;

			case 161: // Karamja: shipyard gate (401, 762)
				if (p.getQuestStage(Constants.Quests.GRAND_TREE) >= 8) {
					if (p.getX() >= 407) {
						doDoor(obj, p);
						p.message("you open the door");
						p.message("and walk through");
					} else {
						p.message("the door is locked");
					}
				}
				break;

			case 74: // Hero's Guild: main door
				if (p.getQuestStage(Constants.Quests.HEROS_QUEST) == -1) {
					doDoor(obj, p);
				} else {
					Npc achetties = getNearestNpc(p, NpcId.ACHETTIES.id(), 10);
					switch (p.getQuestStage(Constants.Quests.HEROS_QUEST)) {
						case 0:
							achetties.initializeTalkScript(p);
							break;
						case 1:
						case 2:
							npcTalk(p, achetties,
								"Greetings welcome to the hero's guild",
								"How goes thy quest?");
							if (hasItem(p, ItemId.RED_FIREBIRD_FEATHER.id()) && hasItem(p, ItemId.MASTER_THIEF_ARMBAND.id()) && hasItem(p, ItemId.LAVA_EEL.id())) {
								playerTalk(p, achetties, "I have all the things needed");
								p.sendQuestComplete(Constants.Quests.HEROS_QUEST);
							} else {
								playerTalk(p, achetties,
									"It's tough, I've not done it yet");
								npcTalk(p,
									achetties,
									"Remember you need the feather of an Entrana firebird",
									"A master thief armband",
									"And a cooked lava eel");
								int opt2 = showMenu(p, achetties, false, //do not send over
									"Any hints on getting the armband?",
									"Any hints on getting the feather?",
									"Any hints on getting the eel?",
									"I'll start looking for all those things then");
								if (opt2 == 0) {
									playerTalk(p, achetties, "Any hints on getting the thieves armband?");
									npcTalk(p, achetties,
										"I'm sure you have relevant contacts to find out about that");
								} else if (opt2 == 1) {
									playerTalk(p, achetties, "Any hints on getting the feather?");
									npcTalk(p, achetties,
										"Not really - Entrana firebirds live on Entrana");
								} else if (opt2 == 2) {
									playerTalk(p, achetties, "Any hints on getting the eel?");
									npcTalk(p, achetties,
										"Maybe go and find someone who knows a lot about fishing?");
								} else if (opt2 == 3) {
									playerTalk(p, achetties, "I'll start looking for all those things then");
								}
							}
							break;

					}
				}
				break;

			case 20:
				replaceGameObject(obj, p, 1, true);
				break;

			case 1:
				replaceGameObject(obj, p, 2, false);
				break;

			case 2:
				replaceGameObject(obj, p, 1, true);
				break;

			case 176: // Alkharid: Shantay Pass jail door
				p.message("The door opens.");
				p.message(""); // strange.
				doDoor(obj, p);
				break;

			/** TEMPLE OF IKOV DOORS **/
			case 109: //
				if (p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) >= 1 || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -1 || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -2) {
					p.message("You go through the door");
					doDoor(obj, p);
				} else {
					message(p, "The door doesn't open",
						"No one seems to be in");
				}
				break;

			case 108: // Temple of Ikov: Jail door near Fire Warrior (546, 3302)
				if (p.getCache().hasKey("killedLesarkus") || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -1 || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -2) {
					p.message("You go through the door");
					doDoor(obj, p);
				} else {
					message(p, "The fire warrior's eyes glow",
						"The fire warrior glares at the door",
						"The door handle is too hot to handle");
				}
				break;

			case 107: // Temple of Ikov: Door deeper into tunnel (545, 3307)
				if (p.getCache().hasKey("completeLever") || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -1 || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -2) {
					p.message("You go through the door");
					doDoor(obj, p);
				} else {
					p.message("The door won't open");
				}
				break;

			case 106: // Temple of Ikov: Door to Ice Spiders (536, 3349)
				if (p.getCache().hasKey("openSpiderDoor") || p.getX() >= 536 || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -1 || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -2) {
					p.message("You go through the door");
					doDoor(obj, p);
				} else {
					p.message("The door won't open");
				}
				break;

			case 104: // Temple of Ikov: First door (533, 3342)
				if (p.getInventory().wielding(ItemId.PENDANT_OF_LUCIEN.id()) || p.getY() >= 3335 && p.getY() <= 3341) {
					p.message("You go through the door");
					doDoor(obj, p);
				} else {
					message(p, "As you reach to open the door",
						"A great terror comes over you",
						"You decide you'll not open this door today");
				}
				break;

			case 105: // Temple of Ikov: Bridge door (546, 3328)
				if (p.getInventory().wielding(ItemId.BOOTS_OF_LIGHTFOOTEDNESS.id()) || p.getX() >= 546) {
					p.message("You go through the door");
					doDoor(obj, p);
				} else {
					p.message("Your weight is too much for the bridge to hold");
					p.teleport(544, 3330);
					sleep(650);
					p.message("You fall through the bridge");
					sleep(1000);
					p.message("The lava singes you");
					p.damage(DataConversions.roundUp(p.getSkills().getLevel(Skills.HITPOINTS) / 5));
				}
				break;

			case 9:
				replaceGameObject(obj, p, 8, false);
				break;

			case 8:
				replaceGameObject(obj, p, 9, true);
				break;

			case 94:
			case 23:
				p.message("The door is locked");
				break;

			case 113: // Fight Arena (621, 699), (603, 717)
				boolean stop = false;
				Npc guard = null;
				if (p.getInventory().wielding(ItemId.KHAZARD_HELMET.id())
					&& p.getInventory().wielding(ItemId.KHAZARD_CHAINMAIL.id())) {
					doDoor(obj, p);
				} else if (obj.getX() == 603 && p.getX() < obj.getX()) {
					guard = getNearestNpc(p, NpcId.GUARD_KHAZARD_BYPRISONER.id(), 8);
					stop = true;
				} else if (obj.getY() == 699 && p.getY() < obj.getY()) {
					guard = getNearestNpc(p, NpcId.GUARD_KHAZARD_BRIBABLE.id(), 8);
					stop = true;
				} else {
					//inside building does not matter to exit
					doDoor(obj, p);
				}
				if (stop && guard != null) {
					if (p.getQuestStage(Constants.Quests.FIGHT_ARENA) == 0) {
						npcTalk(p, guard, "you there! halt!",
								"this is General Khazard's private lodgings",
								"what's your business here?");
						playerTalk(p, guard, "I'm looking for the 'Servil' prisoners");
						npcTalk(p, guard, "wait until tomorrow, then you can",
								"see them butchered in the arena haha",
								"Now OUT and don't come back!");
					} else {
						npcTalk(p, guard, "you there! halt!",
								"this is General Khazard's private lodgings",
								"now leave and don't return!");
					}
				}
				break;

			case 114: // Fight Arena (615, 715), (619, 711)
				if (p.getCache().hasKey("freed_servil")
					|| p.getQuestStage(Constants.Quests.FIGHT_ARENA) == -1
					|| p.getQuestStage(Constants.Quests.FIGHT_ARENA) == 3) {
					doDoor(obj, p);
					return;
				}
				Npc guardArenaEntrance = getNearestNpc(p, NpcId.GUARD_KHAZARD_ARENA_ENTRANCE.id(), 8);
				if (guardArenaEntrance != null) {
					npcTalk(p, guardArenaEntrance, "and where do you think you're going?",
						"only General Khazard decides who fights in the arena",
						"so get out of here");
				}
				break;

			case 120: // Plague city / Biohazard - unsure the purpose of this door
				if (p.getX() > 624) {
					Npc mourner = getNearestNpc(p, NpcId.MOURNER_WESTARDOUGNE.id(), 8);
					p.message("The door won't open");
					p.message("You notice a black cross on the door");
					if (mourner != null) {
						npcTalk(p, mourner, "I'd stand away from there",
							"That black cross means that house has been touched by the plague");
					}
				}
				break;

			case 122: // Plague City
				if (p.getQuestStage(Constants.Quests.PLAGUE_CITY) >= 6
					|| p.getQuestStage(Constants.Quests.PLAGUE_CITY) == -1) {
					if (p.getY() >= 569) {
						doDoor(obj, p);
						p.message("You go through the door");
					} else {
						doDoor(obj, p);
						p.message("You go through the door");
					}
					return;
				}
				Npc ted = getNearestNpc(p, NpcId.TED_REHNISON.id(), 8);
				if (ted != null) {
					p.message("The door won't open");
					npcTalk(p, ted, "Go away we don't want any");
					if (p.getY() >= 569) {
						if (removeItem(p, ItemId.PLAGUE_CITY_BOOK.id(), 1)) {
							playerTalk(p, ted,
								"I have come to return a book from Jethick");
							npcTalk(p, ted, "Ok I guess you can come in then");
							doDoor(obj, p);
							p.updateQuestStage(Constants.Quests.PLAGUE_CITY, 6);
						}
					}
				}
				break;

			case 123: // Plague City
				Npc mourner = getNearestNpc(p, NpcId.MOURNER_WESTARDOUGNE.id(), 8);
				if (p.getQuestStage(Constants.Quests.PLAGUE_CITY) == 11
					|| p.getQuestStage(Constants.Quests.PLAGUE_CITY) == -1) {
					doDoor(obj, p);
					return;
				}
				if (p.getY() <= 605 || p.getY() >= 612) {
					p.message("The door won't open");
					p.message("You notice a black cross on the door");
					if (mourner != null) {
						npcTalk(p, mourner, "I'd stand away from there",
							"That black cross means that house has been touched by the plague");
						if (hasItem(p, ItemId.WARRANT.id())) {
							playerTalk(p, mourner,
								"I have a warrant from Bravek to enter here");
							npcTalk(p, mourner, "this is highly irregular",
								"Please wait while I speak to the head mourner");
							p.message("You wait until the mourner's back is turned and sneak into the building");
							doDoor(obj, p);
							return;
						}
						if (p.getQuestStage(Constants.Quests.PLAGUE_CITY) == 7) {
							int menu = showMenu(p, mourner, false, //do not send over
								"but I think a kidnap victim is in here",
								"I fear not a mere plague",
								"thanks for the warning");
							if (menu == 0) {
								playerTalk(p, mourner, "But I think a kidnap victim is in here");
								npcTalk(p, mourner, "Sounds unlikely",
									"Even kidnappers wouldn't go in there",
									"even if someone is in there",
									"They're probably dead by now");
								int menu2 = showMenu(p, mourner, "Good point",
									"I want to check anyway");
								if (menu2 == 0) {
									// NOTHING
								} else if (menu2 == 1) {
									npcTalk(p, mourner, "You don't have clearance to go in there");
									playerTalk(p, mourner, "How do I get clearance?");
									npcTalk(p,
										mourner,
										"Well you'd need to apply to the head mourner",
										"Or I suppose Bravek the city warder",
										"I wouldn't get your hopes up though");
									p.updateQuestStage(Constants.Quests.PLAGUE_CITY, 8);
								}
							} else if (menu == 1) {
								playerTalk(p, mourner, "I fear not a mere plague");
								npcTalk(p, mourner, "that's irrelevant",
									"You don't have clearance to go in there");
								playerTalk(p, mourner, "How do I get clearance?");
								npcTalk(p,
									mourner,
									"Well you'd need to apply to the head mourner",
									"Or I suppose Bravek the city warder",
									"I wouldn't get your hopes up though");
								p.updateQuestStage(Constants.Quests.PLAGUE_CITY, 8);

							} else if (menu == 2) {
								playerTalk(p, mourner, "thanks for the warning");
							}
						}
					}
				} else {
					doDoor(obj, p);
				}
				break;

			case 121: // Plague City
				Npc Bravek = getNearestNpc(p, NpcId.BRAVEK.id(), 8);
				if (p.getQuestStage(Constants.Quests.PLAGUE_CITY) >= 9
					|| p.getQuestStage(Constants.Quests.PLAGUE_CITY) == -1) {
					doDoor(obj, p);
					return;
				}
				if (p.getX() >= 648) {
					if (Bravek != null) {
						npcTalk(p, Bravek, "Go away,I'm busy", "I'm", "um",
								"In a meeting");
					}
					p.message("The door won't open");
				} else {
					doDoor(obj, p);
				}
				break;

			case 115:
				doDoor(obj, p);
				break;

			/** Guild Doors */
			case 112: // Fishing Guild Door
				if (obj.getX() != 586 || obj.getY() != 524) {
					break;
				}
				if (p.getY() > 523) {
					if (getCurrentLevel(p, Skills.FISHING) < 68) {
						p.setBusy(true);
						Npc masterFisher = World.getWorld().getNpc(NpcId.MASTER_FISHER.id(), 582, 588,
							524, 527);
						if (masterFisher != null) {
							npcTalk(p, masterFisher, "Hello only the top fishers are allowed in here");
						}
						Server.getServer().getEventHandler().add(
							new ShortEvent(p, "Fishing Guild Door") {
								public void action() {
									p.setBusy(false);
									p.message(
										"You need a fishing level of 68 to enter");
								}
							});
					} else {
						doDoor(obj, p);
					}
				} else {
					doDoor(obj, p);
				}
				break;

			case 55: // Mining Guild Door
				if (obj.getX() != 268 || obj.getY() != 3381) {
					break;
				}
				if (getCurrentLevel(p, Skills.MINING) < 60) {
					Npc dwarf = World.getWorld().getNpc(NpcId.DWARF_MINING_GUILD.id(), 265, 270, 3379, 3380);
					if (dwarf != null) {
						npcTalk(p, dwarf, "Sorry only the top miners are allowed in there");
					}
					sleep(600);
					p.message("You need a mining of level 60 to enter");
				} else {
					doDoor(obj, p);
				}
				break;

			case 68: // Crafting Guild Door
				if (obj.getX() != 347 || obj.getY() != 601) {
					return;
				}
				if (getCurrentLevel(p, Skills.CRAFTING) < 40) {
					p.setBusy(true);
					Npc master = World.getWorld().getNpc(NpcId.MASTER_CRAFTER.id(), 341, 349, 599, 612);
					if (master != null) {
						npcTalk(p, master, "Sorry only experienced craftsmen are allowed in here");
					}
					sleep(600);
					p.setBusy(false);
					p.message("You need a crafting level of 40 to enter the guild");
				} else if (!p.getInventory().wielding(ItemId.BROWN_APRON.id())) {
					Npc master = World.getWorld().getNpc(NpcId.MASTER_CRAFTER.id(), 341, 349, 599, 612);
					if (master != null) {
						npcTalk(p, master, "Where's your brown apron?",
							"You can't come in here unless you're wearing a brown apron");
					}
				} else {
					doDoor(obj, p);
				}
				break;

			case 43: // Cooking Guild Door
				if (obj.getX() != 179 || obj.getY() != 488) {
					break;
				}
				if (getCurrentLevel(p, Skills.COOKING) < 32) {
					Npc chef = World.getWorld().getNpc(NpcId.HEAD_CHEF.id(), 176, 181, 480, 487);
					if (chef != null) {
						npcTalk(p, chef, "Sorry. Only the finest chefs are allowed in here");
					}
					sleep(600);
					p.message("You need a cooking level of 32 to enter");
				} else if (!p.getInventory().wielding(ItemId.CHEFS_HAT.id())) {
					Npc chef = World.getWorld().getNpc(NpcId.HEAD_CHEF.id(), 176, 181, 480, 487);
					if (chef != null) {
						npcTalk(p, chef, "Where's your chef's hat",
							"You can't come in here unless you're wearing a chef's hat");
					}
				} else {
					doDoor(obj, p);
				}
				break;

			case 146: // Magic Guild Door
				if (obj.getX() != 599 || obj.getY() != 757) {
					break;
				}
				if (getCurrentLevel(p, Skills.MAGIC) < 66) {
					Npc wizard = World.getWorld().getNpc(NpcId.HEAD_WIZARD.id(), 596, 597, 755, 758);
					if (wizard != null) {
						npcTalk(p, wizard, "You need a magic level of 66 to get in here",
							"The magical energy in here is unsafe for those below that level");
					}
				} else {
					doDoor(obj, p);
				}
				break;

			case 22: // Odd looking wall (545, 3283) & (219, 3282)
				p.playSound("secretdoor");
				doDoor(obj, p, -1);
				p.message("You just went through a secret door");
				break;

			case 38: // Black Knight Guard Door
				if (obj.getX() != 271 || obj.getY() != 441) {
					return;
				}
				if (p.getX() <= 270) {
					if (!p.getInventory().wielding(ItemId.IRON_CHAIN_MAIL_BODY.id())
						|| !p.getInventory().wielding(ItemId.MEDIUM_BRONZE_HELMET.id())) {
						p.message(
							"Only guards are allowed in there!");
						return;
					}
					doDoor(obj, p);
				}
				break;

			case 36: // Draynor mansion front door
				if (obj.getX() != 210 || obj.getY() != 553) {
					return;
				}
				if (p.getY() >= 553) {
					doDoor(obj, p);
				} else {
					p.message("The door won't open");
				}
				break;

			case 37: // Draynor mansion back door
				if (obj.getX() != 199 || obj.getY() != 551) {
					return;
				}
				doDoor(obj, p);
				p.message("You go through the door");
				break;

			case 30: // Locked Doors
				p.message("The door is locked");
				break;

			case 44: // champs guild door
				if (obj.getX() != 150 && obj.getY() != 554) {// champs guild door
					return;
				}
				if (p.getQuestPoints() < 32) {
					final Npc champy = getNearestNpc(p, NpcId.GUILDMASTER.id(), 20);
					if (champy != null) {
						npcTalk(p, champy,
							"You have not proven yourself worthy to enter here yet");
						message(p,
							"The door won't open - you need at least 32 quest points");
					}
					return;
				}
				doDoor(obj, p);
				break;

			case 67: // Lost City Market Door (117, 3539), (116, 3537) NPC: 221
				if (p.getLocation().getX() == 115 || p.getLocation().getY() == 3539) {
					Npc n = World.getWorld().getNpc(NpcId.DOORMAN.id(), 105, 116, 3536, 3547);
					if (n != null) {
						npcTalk(p, n,
							"You cannot go through this door without paying the trading tax");
						playerTalk(p, n, "What do I need to pay?");
						npcTalk(p, n, "One diamond");
						int m = showMenu(p, n, "Okay", "A diamond, are you crazy?",
							"I haven't brought my diamonds with me");
						if (m == 0) {
							if (!hasItem(p, ItemId.DIAMOND.id())) {
								playerTalk(p, n,
									"I haven't brought my diamonds with me");
							} else {
								p.message("You give the doorman a diamond");
								removeItem(p, ItemId.DIAMOND.id(), 1);
								doDoor(obj, p);
							}
						} else if (m == 1) {
							npcTalk(p, n, "Nope those are the rules");
						}
					}
					break;
				}
				if (p.getLocation().getX() == 116 || p.getLocation().getY() == 3538) {
					Npc n = World.getWorld().getNpc(NpcId.DOORMAN.id(), 117, 125, 3531, 3538);

					if (n != null) {
						npcTalk(p, n,
							"You cannot go through this door without paying the trading tax");
						playerTalk(p, n, "What do I need to pay?");
						npcTalk(p, n, "One diamond");
						int m = showMenu(p, n, "Okay", "A diamond, are you crazy?",
							"I haven't brought my diamonds with me");
						if (m == 0) {
							if (!hasItem(p, ItemId.DIAMOND.id())) {
								playerTalk(p, n,
									"I haven't brought my diamonds with me");
							} else {
								p.message("You give the doorman a diamond");
								removeItem(p, ItemId.DIAMOND.id(), 1);
								doDoor(obj, p);
							}
						} else if (m == 1) {
							npcTalk(p, n, "Nope those are the rules");
						}
					}
				}
				break;

			case 150: // Fight Arena: Ogre Cage (?)
				doDoor(obj, p);
				break;

			case 138: // Biohazard
				if (!p.getCache().hasKey("rotten_apples") && p.getQuestStage(Constants.Quests.BIOHAZARD) == 4) {
					message(p, "the door is locked",
						"inside you can hear the mourners eating",
						"you need to distract them from their stew");
				} else if (p.getCache().hasKey("rotten_apples") || p.getQuestStage(Constants.Quests.BIOHAZARD) == 5) {
					if (p.getY() <= 572) {
						doDoor(obj, p);
						p.playerServerMessage(MessageType.QUEST, "you open the door");
						p.playerServerMessage(MessageType.QUEST, "You go through the door");
						return;
					}
					Npc DOOR_MOURNER = getNearestNpc(p, NpcId.MOURNER_DOOR.id(), 10);
					if (DOOR_MOURNER != null) {
						if (p.getInventory().wielding(ItemId.DOCTORS_GOWN.id())) {
							npcTalk(p, DOOR_MOURNER, "in you go doc");
							doDoor(obj, p);
							p.playerServerMessage(MessageType.QUEST, "You go through the door");
						} else {
							npcTalk(p, DOOR_MOURNER, "keep away from there");
							playerTalk(p, DOOR_MOURNER, "why?");
							npcTalk(p, DOOR_MOURNER, "several mourners are ill with food poisoning",
								"we're waiting for a doctor");
						}
					}
				} else if ((p.getQuestStage(Constants.Quests.BIOHAZARD) > 5 || p.getQuestStage(Constants.Quests.BIOHAZARD) == -1) &&
					p.getInventory().wielding(802)) {
					doDoor(obj, p);
				} else {
					p.message("the door is locked");
				}
				break;

			case 141: // Biohazard
				if (p.getY() >= 1513) {
					doDoor(obj, p);
					p.playerServerMessage(MessageType.QUEST, "You go through the door");
					return;
				}
				if (p.getInventory().wielding(ItemId.DOCTORS_GOWN.id())) {
					doDoor(obj, p);
					p.playerServerMessage(MessageType.QUEST, "You go through the door");
				} else {
					p.message("the mourner is refusing to open the door");
				}
				break;

			case 145: // Biohazard
				if (p.getQuestStage(Constants.Quests.BIOHAZARD) == 7 || p.getQuestStage(Constants.Quests.BIOHAZARD) == 8 || p.getQuestStage(Constants.Quests.BIOHAZARD) == 9 || p.getQuestStage(Constants.Quests.BIOHAZARD) == -1) {
					if (p.getX() <= 82) {
						p.playerServerMessage(MessageType.QUEST, "You go through the door");
						doDoor(obj, p);
						return;
					}
					if (p.getInventory().wielding(ItemId.PRIEST_ROBE.id()) && p.getInventory().wielding(ItemId.PRIEST_GOWN.id())) {
						p.message("guidors wife allows you to go in");
						p.playerServerMessage(MessageType.QUEST, "You go through the door");
						doDoor(obj, p);
					} else {
						p.message("guildors wife refuses to let you enter");
					}
				} else {
					p.message("the door is locked");
				}
				break;
		}
	}

	public boolean blockInvUseOnWallObject(GameObject obj, Item item,
										   Player player) {

		/* Ernest the Chicken */
		if (obj.getID() == 35 && obj.getY() == 545) {
			return true;
		}
		if (obj.getID() == 110) {
			return true;
		}
		/* Dragon Slayer Maze Doors */
		if (obj.getID() >= 48 && obj.getID() <= 53 || obj.getID() == 60) {
			return true;
		}
		/* Shield of arrav */
		if (obj.getID() == 20) {
			return true;
		}
		/* witches house door */
		if (obj.getID() == 69) {
			return true;
		}
		/* varrocks shortcut to edgeville dung*/
		if (obj.getID() == 23) {
			return true;
		}
		/* taverly dungeon door near jailer */
		if ((obj.getID() == 83 && obj.getX() == 360 && obj.getY() == 3428)
			|| (obj.getID() == 83 && obj.getX() == 360
			&& obj.getY() == 3425)) {
			return true;
		}
		/* Door to enter blue dragons in taverly dungeon */
		if (obj.getID() == 84 && obj.getX() == 355 && obj.getY() == 3353) {
			return true;
		}

		return false;
	}

	/**
	 * Handle unlocking doors with keys here.
	 */

	public void onInvUseOnWallObject(GameObject obj, Item item, Player player) {
		int keyItem = ItemId.NOTHING.id();
		boolean remove = false;
		boolean showsBubble = false;
		switch (obj.getID()) {
			//Brass key
			case 23:
				keyItem = ItemId.BRASS_KEY.id();
				showsBubble = true;
				break;
			/* Ernest the Chicken */
			case 35:
				keyItem = ItemId.CLOSET_KEY.id();
				showsBubble = true;
				break;
			/* Shield of Arrav - Phoenix Door */
			case 20:
				keyItem = ItemId.PHOENIX_GANG_WEAPON_KEY.id();
				showsBubble = true;
				break;
			/* End of shield of arrav */

			case 69: // Witches house
				keyItem = ItemId.FRONT_DOOR_KEY.id();
				if (player.getCache().hasKey("witch_spawned")) {
					player.getCache().remove("witch_spawned");
				}
				showsBubble = false;
				break;
			//Jail door in Taverley Dungeon
			case 83:
				keyItem = ItemId.JAIL_KEYS.id();
				showsBubble = true;
				break;
			//Dusty key
			case 84:
				keyItem = ItemId.DUSTY_KEY.id();
				showsBubble = true;
				break;

			/* Dragon Slayer maze doors */
			case 48: /* Red door */
				keyItem = ItemId.RED_KEY.id();
				remove = true;
				showsBubble = false;
				break;
			case 49: /* Orange door */
				keyItem = ItemId.ORANGE_KEY.id();
				remove = true;
				showsBubble = false;
				break;
			case 50: /* Yellow door */
				keyItem = ItemId.YELLOW_KEY.id();
				remove = true;
				showsBubble = false;
				break;
			case 51: /* Blue door */
				keyItem = ItemId.BLUE_KEY.id();
				remove = true;
				showsBubble = false;
				break;
			case 53: /* Magenta door */
				keyItem = ItemId.MAGENTA_KEY.id();
				remove = true;
				showsBubble = false;
				break;
			case 52: /* Black door */
				keyItem = ItemId.BLACK_KEY.id();
				remove = true;
				showsBubble = false;
				break;
			case 60: /* Maze entrace */
				keyItem = ItemId.MAZE_KEY.id();
				remove = false;
				showsBubble = false;
				break;
			/* End of dragon slayer maze */
			// Temple of Ikov
			case 110:
				keyItem = ItemId.SHINY_KEY.id();
				remove = false;
				showsBubble = true;
				break;
		}
		if (player.getInventory().hasItemId(keyItem) && item.getID() == keyItem) {
			if (keyItem == ItemId.FRONT_DOOR_KEY.id() && player.getQuestStage(Quests.WITCHS_HOUSE) == 0) {
				playerTalk(player, null, "It'd be rude to break into this house");
				return;
			}
			if (showsBubble) {
				showBubble(player, item);
			}
			player.message("you unlock the door");
			doDoor(obj, player);
			player.message("you go through the door");
			if (remove) {
				player.message("Your " + item.getDef().getName().toLowerCase() + " has gone!");
				player.getInventory().remove(keyItem, 1);
				if (obj.getID() == 52) {
					player.getCache().store("melzar_unlocked", true);
				}
			}
		} else {
			player.message("Nothing interesting happens");
		}
	}

	public boolean blockObjectAction(GameObject obj, String command,
									 Player player) {
		if (obj.getGameObjectDef().getName().toLowerCase().contains("gate")
			|| obj.getGameObjectDef().getName().toLowerCase().contains("door")) {
			if (command.equalsIgnoreCase("close")
				|| command.equalsIgnoreCase("open")) {
				return true;
			}
		}
		return false;
	}

	public void onObjectAction(GameObject obj, String command, Player player) {
		if (obj.getGameObjectDef().getName().toLowerCase().contains("gate")) {
			handleGates(obj, player);
		} else if (obj.getGameObjectDef().getName().toLowerCase().contains("door")) {
			handleObjectDoor(obj, player);
		}
	}

	private void handleObjectDoor(GameObject obj, Player player) {

		if (blockInvUseOnWallObject(obj, null, player)) {
			player.message("the door is locked");
			return;
		}

		switch (obj.getID()) {
			case 142: // Black Knight Big Door
				player.message("the doors are locked");
				break;
			/* Regular Doors */
			case 18:
				replaceGameObject(17, true, player, obj);
				break;
			case 17:
				replaceGameObject(18, false, player, obj);
				break;
			case 58:
				replaceGameObject(57, false, player, obj);
				break;

			case 57: // Brimhaven Gate (434, 682)
				if (obj.getX() == 434 && obj.getY() == 682) {
					if (!Constants.GameServer.MEMBER_WORLD) {
						player.message(
							"You need to be a member to use this gate");
						return;
					}
				}
				replaceGameObject(58, true, player, obj);
				break;

			case 63:
				replaceGameObject(64, false, player, obj);
				break;

			case 64:
				if (obj.getX() == 467 && obj.getY() == 518) {
					player.message("The doors are locked");
					break;
				} else if (obj.getX() == 558 && obj.getY() == 587) {
					player.message("The doors are locked");
					break;
				} else {
					replaceGameObject(63, true, player, obj);
				}
				break;

			case 79:
				replaceGameObject(78, false, player, obj);
				break;

			case 78:
				replaceGameObject(79, true, player, obj);
				break;

			case 135:
				replaceGameObject(136, true, player, obj);
				break;

			case 136:
				replaceGameObject(135, false, player, obj);
				break;

			default:
				player.message(
					"Nothing interesting happens");
		}
	}

	private void handleGates(GameObject obj, Player player) {
		boolean members = false;

		switch (obj.getID()) {
			case 60:
				replaceGameObject(59, true, player, obj);
				return;

			case 59:
				replaceGameObject(60, false, player, obj);
				return;

			case 57:
				replaceGameObject(obj, player, 58, true);
				return;

			case 58:
				replaceGameObject(obj, player, 57, false);
				return;

			/** Gnome glider Karamja gate **/
			case 660: // (387, 760)
				if (obj.getX() != 387 || obj.getY() != 760) {
					return;
				}
				player.message("you open the gate");
				doGate(player, obj, 357);
				sleep(1000);
				player.message("and walk through");
				return;

			case 356: // McGrouber's Wood / Woodcutting Guild Gate (560, 472)
				if (obj.getX() != 560 || obj.getY() != 472) {
					return;
				}
				if (player.getY() <= 472) {
					if (Constants.GameServer.WANT_WOODCUTTING_GUILD) {
						doGate(player, obj);
					} else { // deny exit if not woodcut guild
						player.playerServerMessage(MessageType.QUEST, "the gate is locked");
					}
				} else {
					if (Constants.GameServer.WANT_WOODCUTTING_GUILD) {
						if (getCurrentLevel(player, Skills.WOODCUT) < 70) {
							player.setBusy(true);
							final Npc forester = World.getWorld().getNpc(NpcId.FORESTER.id(), 562, 565,
								468, 472);
							if (forester != null) {
								npcTalk(player, forester, "Hello only the top woodcutters are allowed in here");
							}
							Server.getServer().getEventHandler().add(
								new ShortEvent(player, "Woodcutting Guild Door") {
									public void action() {
										owner.setBusy(false);
										owner.message(
											"You need a woodcutting level of 70 to enter");
									}
								});
						} else {
							doGate(player, obj);
						}
					} else { // Deny Entry
						final Npc forester = World.getWorld().getNpc(NpcId.FORESTER.id(), 562, 565,
							468, 472);
						if (forester != null) {
							npcTalk(player, forester, "Hey you can't come through here", "This is private land");
							sleep(1200);
							player.playerServerMessage(MessageType.QUEST, "You will need to find another way in");
						} else {
							player.playerServerMessage(MessageType.QUEST, "You will need to find another way in");
							sleep(1200);
							player.playerServerMessage(MessageType.QUEST, "the gate is locked");
						}
					}
				}
				return;

			case 712: // Shilo inside gate
				if (obj.getX() != 394 || obj.getY() != 851) {
					return;
				}
				if (player.getX() >= 394) {
					message(player, "The gate opens smoothly");
					player.teleport(381, 851);
					player.message("You make your way out of Shilo Village.");
				} else {
					player.message("The gate won't open");
				}
				return;

			case 611: // Shilo outside gate
				if (obj.getX() != 388 || obj.getY() != 851) {
					return;
				}
				if (player.getQuestStage(Constants.Quests.SHILO_VILLAGE) == -1) {
					player.teleport(395, 851);
					player.message("You open the gates and make your way through into");
					player.message("the village.");
					return;
				}
				message(player, "The gate feels very cold to your touch!");
				player.message("Are you sure you want to go through?");
				int menu = showMenu(player,
					"Yes, I am fearless!",
					"No, actually, I have a bad feeling about this!");
				if (menu == 0) {
					replaceObjectDelayed(obj, 3000, 612);
					if (player.getX() >= 388) {
						message(player, "The gates open very slowly.");
						player.teleport(387, 852);
						player.message("You manage to drag your battered body back through the gates.");
					} else {
						message(player, "The gates open very slowly...");
						player.teleport(389, 852);
						message(player, "As soon as the gates open, the Zombies grab you and start dragging you inside!");
						player.teleport(391, 852);
						playerTalk(player, null, "Oh no, I'm done for!");
					}
				} else if (menu == 1) {
					message(player, "You drag your quivering body  away from the gates.");
					player.message("You look around, but you don't think anyone saw you.");
				}
				return;

			case 513: // eastern Varrock gate for family crest or biohazard or just wanna go in :)
				if (obj.getX() != 93 || obj.getY() != 521) {
					return;
				}
				if (!Constants.GameServer.MEMBER_WORLD) {
					player.message(
						"You need to be a member to use this gate");
					return;
				}
				if (player.getX() >= 94) {
					if (player.getQuestStage(Constants.Quests.BIOHAZARD) == 7) {
						Npc guard = getNearestNpc(player, NpcId.GUARD_VARROCKGATE.id(), 10);
						if (guard != null) {
							npcTalk(player, guard, "Halt. I need to conduct a search on you",
								"There have been reports of a someone bringing a virus into Varrock");
						}
						if (hasItem(player, ItemId.ETHENEA.id())) {
							while (player.getInventory().remove(new Item(ItemId.ETHENEA.id())) != -1) ;
							player.message("He takes the vial of ethenea from you");
						}
						if (hasItem(player, ItemId.SULPHURIC_BROLINE.id())) {
							while (player.getInventory().remove(new Item(ItemId.SULPHURIC_BROLINE.id())) != -1) ;
							player.message("He takes the vial of sulphuric broline from you");
						}
						if (hasItem(player, ItemId.LIQUID_HONEY.id())) {
							while (player.getInventory().remove(new Item(ItemId.LIQUID_HONEY.id())) != -1) ;
							player.message("He takes the vial of liquid honey from you");
						}
					}
					player.teleport(92, 522, false);
				} else {
					doGate(player, obj, 514);
				}
				player.message("you open the gate and pass through");
				return;

			case 93: // Red dragon gate (140, 180)
				members = true;
				break;

			case 137: // Members Gate near Doric (341, 487)
				members = true;
				break;

			case 347: //
				members = true;
				break;

			case 138: // Members Gate near Crafting Guild (343, 581)
				members = true;
				break;

			case 254: // Members Gate near Brimhaven (434, 682)
				members = true;
				break;

			case 563: // King Lathas Training Area (660, 551)
				Npc lathasGuard = getNearestNpc(player, NpcId.GUARD_GATE_TRAINING_CAMP.id(), 10);
				if (player.getQuestStage(Constants.Quests.BIOHAZARD) == -1) {
					if (player.getY() <= 551) {
						doGate(player, obj);
					} else {
						if (lathasGuard != null) {
							npcTalk(player, lathasGuard, "the king has granted you access to this training area",
								"make good use of it, soon all you strength will be needed");
						}
						doGate(player, obj);
					}
					player.message("you open the gate");
				} else {
					npcTalk(player, lathasGuard, "this is a restricted area",
						"you can only enter under the authority of king lathas");
				}
				return;

			case 626: // Gnome Stronghold Gate (703, 531)
				if (player.getY() != 532 && player.getQuestStage(Constants.Quests.GRAND_TREE) == 8) {
					Npc n = getNearestNpc(player, NpcId.GNOME_GUARD.id(), 15);
					if (n != null) {
						npcTalk(player, n, "halt human");
						playerTalk(player, n, "what?, why?");
						npcTalk(player, n, "from order of the head tree guardian...",
							"..you cannot leave");
						playerTalk(player, n, "that's crazy, why?");
						npcTalk(player, n, "humans are planning to attack our stronghold",
							"you could be a spy");
						playerTalk(player, n, "that's ridiculous");
						npcTalk(player, n, "maybe, but that's the orders, I'm sorry");
						message(player, "the gnome refuses to open the gate");
					} else {
						Npc newN = spawnNpc(NpcId.GNOME_GUARD.id(), 705, 530, 30000);
						npcTalk(player, newN, "halt human");
						playerTalk(player, newN, "what?, why?");
						npcTalk(player, newN, "from order of the head tree guardian...",
							"..you cannot leave");
						playerTalk(player, newN, "that's crazy, why?");
						npcTalk(player, newN, "humans are planning to attack our stronghold",
							"you could be a spy");
						playerTalk(player, newN, "that's ridiculous");
						npcTalk(player, newN, "maybe, but that's the orders, I'm sorry");
						message(player, "the gnome refuses to open the gate");
						newN.remove();
					}
					return;

				} else if (player.getY() >= 532 && player.getQuestStage(Constants.Quests.GRAND_TREE) == 10) {
					Npc n = getNearestNpc(player, NpcId.GNOME_GUARD.id(), 15);
					npcTalk(player, n, "i'm afraid that we have orders not to let you in");
					playerTalk(player, n, "orders from who?");
					npcTalk(player, n, "the head tree guardian, he say's you're a spy");
					playerTalk(player, n, "glough!");
					npcTalk(player, n, "i'm sorry but you'll have to leave");
					return;
				}
				members = true;
				break;

			case 305: // Members Gate in Edgeville Dungeon (196, 1266)
				members = true;
				break;

			case 1089: // Members Gate near Dig Site (59, 573)
				members = true;
				break;

			case 508: // Lesser Cage Gate
				members = false;
				break;

			case 319:
				// Members Gate near King Black Dragon ladder (285, 185)
				members = true;
				break;

			default:
				player.message(
					"Nothing interesting happens");

				return;
		}
		if (members && !GameServer.MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}
		player.message("you go through the gate");
		doGate(player, obj);
		/*if(player.getY() >= 141 && obj.getID() == 347) { // Not authentic
			// Unwield so they cannot be stationary with it
			player.unwieldMembersItems();
		}*/
	}

	private void replaceGameObject(final int newID, final boolean open,
								   final Player p, final GameObject object) {
		World.getWorld().replaceGameObject(object,
			new GameObject(object.getLocation(), newID, object
				.getDirection(), object.getType()));
		p.playSound(open ? "opendoor" : "closedoor");
	}

	private void replaceGameObject(GameObject obj, Player owner, int newID,
								   boolean open) {
		if (!owner.cantConsume()) {
			owner.setConsumeTimer(1000);
			if (open) {
				owner.message("The " + (obj.getGameObjectDef().getName().equalsIgnoreCase("gate") ? "gate" : "door") + " swings open");
			} else {
				owner.message("The " + (obj.getGameObjectDef().getName().equalsIgnoreCase("gate") ? "gate" : "door") + " creaks shut");
				owner.setBusyTimer(650);
			}
			owner.playSound(open ? "opendoor" : "closedoor");
			World.getWorld().replaceGameObject(obj, new GameObject(obj.getLocation(), newID, obj.getDirection(), obj.getType()));
		}
	}
}
