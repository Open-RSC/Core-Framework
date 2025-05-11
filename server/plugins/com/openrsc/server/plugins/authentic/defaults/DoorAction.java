package com.openrsc.server.plugins.authentic.defaults;

import com.openrsc.server.constants.*;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.custom.misc.WoodcuttingGuild;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;


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
		if (obj.getID() == BoundaryId.DOOR_CONTINUE_START_GUIDE.id() && obj.getX() == 222 && obj.getY() == 743) {
			return true;
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_CONTROLS_GUIDE.id() && obj.getX() == 224 && obj.getY() == 737) {
			return true;
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_COMBAT_INSTRUCTOR.id() && obj.getX() == 220 && obj.getY() == 727) {
			return true;
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_COOKING_INSTRUCTOR.id() && obj.getX() == 212 && obj.getY() == 729) {
			return true;
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_FINANCIAL_ADVISOR.id() && obj.getX() == 206 && obj.getY() == 730) {
			return true;
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_FISHING_INSTRUCTOR.id() && obj.getX() == 201 && obj.getY() == 734) {
			return true;
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_MINING_INSTRUCTOR.id() && obj.getX() == 198 && obj.getY() == 746) {
			return true;
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_BANK_ASSISTANT.id() && obj.getX() == 204 && obj.getY() == 752) {
			return true;
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_QUEST_ADVISOR.id() && obj.getX() == 209 && obj.getY() == 754) {
			return true;
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_WILDERNESS_GUIDE.id() && obj.getX() == 217 && obj.getY() == 760) {
			return true;
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_MAGIC_INSTRUCTOR.id() && obj.getX() == 222 && obj.getY() == 760) {
			return true;
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_FATIGUE_EXPERT.id() && obj.getX() == 226 && obj.getY() == 760) {
			return true;
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_COMMUNITY_INSTRUCTOR.id() && obj.getX() == 230 && obj.getY() == 759) {
			return true;
		}

		return false;
	}

	public void onWallObjectAction(final GameObject obj, final Integer click,
								   final Player player) {

		// Door's lock needs to be picked, or needs a key.
		if (blockInvUseOnWallObject(obj, null, player)) {
			player.message("The door is locked");
			return;
		}

		// Tutorial Doors
		if (obj.getID() == BoundaryId.DOOR_CONTINUE_START_GUIDE.id() && obj.getX() == 222 && obj.getY() == 743) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 10) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to a guide before going through this door");
			}
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_CONTROLS_GUIDE.id() && obj.getX() == 224 && obj.getY() == 737) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 15) {
				doDoor(obj, player);
			} else {
				player.message("Speak to the controls guide before going through this door");
			}
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_COMBAT_INSTRUCTOR.id() && obj.getX() == 220 && obj.getY() == 727) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 25) {
				doDoor(obj, player);
			} else {
				player.message("Speak to the combat instructor before going through this door");
			}
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_COOKING_INSTRUCTOR.id() && obj.getX() == 212 && obj.getY() == 729) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 35) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to a cooking instructor before going through this door");
			}
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_FINANCIAL_ADVISOR.id() && obj.getX() == 206 && obj.getY() == 730) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 40) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to a finance advisor before going through this door");
			}
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_FISHING_INSTRUCTOR.id() && obj.getX() == 201 && obj.getY() == 734) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 45) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to the fishing instructor before going through this door");
			}
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_MINING_INSTRUCTOR.id() && obj.getX() == 198 && obj.getY() == 746) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 55) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to the mining instructor before going through this door");
			}
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_BANK_ASSISTANT.id() && obj.getX() == 204 && obj.getY() == 752) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 60) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to a bank assistant before going through this door");
			}
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_QUEST_ADVISOR.id() && obj.getX() == 209 && obj.getY() == 754) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 65) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to the quest advisor before going through this door");
			}
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_WILDERNESS_GUIDE.id() && obj.getX() == 217 && obj.getY() == 760) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 70) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to the wilderness guide before going through this door");
			}
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_MAGIC_INSTRUCTOR.id() && obj.getX() == 222 && obj.getY() == 760) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 80) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to a magic instructor before going through this door");
			}
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_FATIGUE_EXPERT.id() && obj.getX() == 226 && obj.getY() == 760) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 90) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to a fatigue expert before going through this door");
			}
		} else if (obj.getID() == BoundaryId.DOOR_CONTINUE_COMMUNITY_INSTRUCTOR.id() && obj.getX() == 230 && obj.getY() == 759) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 100) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to the community instructor before going through this door");
			}
		}


		switch (BoundaryId.getById(obj.getID())) {

			case DOOR_MELZAR_BASEMENT_FROM_OUTSIDE_OR_EXIT: // Dragon Slayer: Door out of maze near entrance
				// + escape doors of basement
				// all are openable from east (inside room) and locked on west side (outside room)
				if (player.getX() == obj.getX() - 1) {
					doDoor(obj, player);
				} else {
					player.message("this door is locked");
				}
				break;

			case DOOR_EXIT_GRAND_TREE: // Grand Tree: main door (outside)
				mes("you open the door");
				delay(3);
				player.teleport(703, 455);
				player.message("and walk through");
				break;

			case DOOR_ENTER_GRAND_TREE: // Grand Tree: main door (inside)
				mes("you open the door");
				delay(3);
				player.teleport(416, 165);
				player.message("and walk through");
				break;

			case DOOR_SHIPYARD_FOREMAN: // Karamja: shipyard gate (401, 762)
				if (player.getQuestStage(Quests.GRAND_TREE) >= 8) {
					if (player.getX() >= 407) {
						doDoor(obj, player);
						player.message("you open the door");
						player.message("and walk through");
					} else {
						player.message("the door is locked");
					}
				}
				break;

			case DOOR_HEROS_GUILD: // Hero's Guild: main door
				if (player.getQuestStage(Quests.HEROS_QUEST) == -1) {
					doDoor(obj, player);
				} else {
					Npc achetties = ifnearvisnpc(player, NpcId.ACHETTIES.id(), 10);
					switch (player.getQuestStage(Quests.HEROS_QUEST)) {
						case 0:
							achetties.initializeTalkScript(player);
							break;
						case 1:
						case 2:
							npcsay(player, achetties,
								"Greetings welcome to the hero's guild",
								"How goes thy quest?");
							if (player.getCarriedItems().hasCatalogID(ItemId.RED_FIREBIRD_FEATHER.id(), Optional.of(false))
								&& player.getCarriedItems().hasCatalogID(ItemId.MASTER_THIEF_ARMBAND.id(), Optional.of(false))
								&& player.getCarriedItems().hasCatalogID(ItemId.LAVA_EEL.id(), Optional.of(false))) {
								say(player, achetties, "I have all the things needed");
								player.sendQuestComplete(Quests.HEROS_QUEST);
							} else {
								say(player, achetties,
									"It's tough, I've not done it yet");
								npcsay(player,
									achetties,
									"Remember you need the feather of an Entrana firebird",
									"A master thief armband",
									"And a cooked lava eel");
								int opt2 = multi(player, achetties, false, //do not send over
									"Any hints on getting the armband?",
									"Any hints on getting the feather?",
									"Any hints on getting the eel?",
									"I'll start looking for all those things then");
								if (opt2 == 0) {
									say(player, achetties, "Any hints on getting the thieves armband?");
									npcsay(player, achetties,
										"I'm sure you have relevant contacts to find out about that");
								} else if (opt2 == 1) {
									say(player, achetties, "Any hints on getting the feather?");
									npcsay(player, achetties,
										"Not really - Entrana firebirds live on Entrana");
								} else if (opt2 == 2) {
									say(player, achetties, "Any hints on getting the eel?");
									npcsay(player, achetties,
										"Maybe go and find someone who knows a lot about fishing?");
								} else if (opt2 == 3) {
									say(player, achetties, "I'll start looking for all those things then");
								}
							}
							break;

					}
				}
				break;

			case DOOR_PHOENIX_WEAPON_KEY:
				replaceGameObject(obj, player, BoundaryId.DOORFRAME.id(), true);
				break;

			case DOORFRAME:
				replaceGameObject(obj, player, BoundaryId.DOOR.id(), false);
				break;

			case DOOR:
				replaceGameObject(obj, player, BoundaryId.DOORFRAME.id(), true);
				break;

			case JAIL_DOOR_SHANTAY_PASS: // Alkharid: Shantay Pass jail door
				if (!player.getCache().hasKey("shantay_jail")) {
					player.message("The door opens.");
					player.message(""); // strange.
					doDoor(obj, player);
				} else {
					player.message("This door is locked.");
					if (player.getX() >= 66) {
						// on the side of the jail
						Npc shantay = ifnearvisnpc(player, NpcId.SHANTAY.id(), 8);
						if (shantay != null) {
							player.message("Shantay saunters over to talk with you.");
							npcsay(player, shantay, "If you want to be let out, you have to pay a fine of five gold.",
								"Do you want to pay now?");
							int pay = multi(player, shantay, "Yes, Ok.", "No thanks, you're not having my money.");
							if (pay == 0) {
								npcsay(player, shantay,
									"Good, I see that you have come to your senses.");
								if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 5) {
									mes("You hand over five gold pieces to Shantay.");
									delay(3);
									npcsay(player, shantay,
										"Great Effendi, now please try to keep the peace.");
									mes("Shantay unlocks the door to the cell.");
									delay(3);
									player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
									player.getCache().remove("shantay_jail");
								} else {
									npcsay(player,
										shantay,
										"You don't have that kind of cash on you I see.",
										"But perhaps you have some in your bank?",
										"You can transfer some money from your bank and pay the fine.",
										"or you will be sent to a maximum security prison in Port Sarim.",
										"Which is it going to be?");
									int menu8 = multi(player, shantay, false, //do not send over
										"I'll pay the fine.",
										"I'm not paying the fine!");
									if (menu8 == 0) {
										say(player, shantay, "I'll pay the fine.");
										if (player.isIronMan(2)) {
											player.message("As an Ultimate Ironman, you cannot use the bank.");
											return;
										}
										npcsay(player, shantay,
											"Ok then..., you'll need access to your bank.");
										player.setAccessingBank(true);
										ActionSender.showBank(player);
										player.getCache().remove("shantay_jail");
									} else if (menu8 == 1) {
										say(player, shantay, "No thanks, you're not having my money.");
										sendToPortSarim(player, shantay, 1);
									}
								}
							} else if (pay == 1) {
								npcsay(player,
									shantay,
									"You have a choice.",
									"You can either pay five gold pieces or...",
									"You can be transported to a maximum security prison in Port Sarim.",
									"Will you pay the five gold pieces?");
								int menu7 = multi(player, shantay, "Yes, Ok.", "No, do your worst!");
								if (menu7 == 0) {
									npcsay(player, shantay,
										"Good, I see that you have come to your senses.");
									if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 5) {
										mes("You hand over five gold pieces to Shantay.");
										delay(3);
										npcsay(player, shantay,
											"Great Effendi, now please try to keep the peace.");
										mes("Shantay unlocks the door to the cell.");
										delay(3);
										player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
										player.getCache().remove("shantay_jail");
									} else {
										npcsay(player,
											shantay,
											"You don't have that kind of cash on you I see.",
											"But perhaps you have some in your bank?",
											"You can transfer some money from your bank and pay the fine.",
											"or you will be sent to a maximum security prison in Port Sarim.",
											"Which is it going to be?");
										int menu8 = multi(player, shantay, false, //do not send over
											"I'll pay the fine.",
											"I'm not paying the fine!");
										if (menu8 == 0) {
											say(player, shantay, "I'll pay the fine.");
											if (player.isIronMan(2)) {
												player.message("As an Ultimate Ironman, you cannot use the bank.");
												return;
											}
											npcsay(player, shantay,
												"Ok then..., you'll need access to your bank.");
											player.setAccessingBank(true);
											ActionSender.showBank(player);
											player.getCache().remove("shantay_jail");
										} else if (menu8 == 1) {
											say(player, shantay, "No thanks, you're not having my money.");
											sendToPortSarim(player, shantay, 1);
										}
									}
								} else if (menu7 == 1) {
									sendToPortSarim(player, shantay, 0);
								}
							}
						}
					}
				}
				break;

			/** TEMPLE OF IKOV DOORS **/
			case DOOR_LUCIEN_HIDEOUT: //
				if (player.getQuestStage(Quests.TEMPLE_OF_IKOV) >= 1 || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -1 || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -2) {
					player.message("You go through the door");
					doDoor(obj, player);
				} else {
					mes("The door doesn't open");
					delay(3);
					mes("No one seems to be in");
					delay(3);
				}
				break;

			case DOOR_TEMPLE_IKOV_FIRE_WARRIOR: // Temple of Ikov: Jail door near Fire Warrior (546, 3302)
				if (player.getCache().hasKey("killedLesarkus") || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -1 || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -2) {
					player.message("You go through the door");
					doDoor(obj, player);
				} else {
					mes("The fire warrior's eyes glow");
					delay(3);
					mes("The fire warrior glares at the door");
					delay(3);
					mes("The door handle is too hot to handle");
					delay(3);
				}
				break;

			case DOOR_TEMPLE_IKOV_LEVER_PULL: // Temple of Ikov: Door deeper into tunnel (545, 3307)
				if (player.getCache().hasKey("completeLever") || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -1 || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -2) {
					player.message("You go through the door");
					doDoor(obj, player);
				} else {
					player.message("The door won't open");
				}
				break;

			case DOOR_TEMPLE_IKOV_ICE_ARROW: // Temple of Ikov: Door to Ice Spiders (536, 3349)
				if (player.getCache().hasKey("openSpiderDoor") || player.getX() >= 536 || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -1 || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -2) {
					player.message("You go through the door");
					doDoor(obj, player);
				} else {
					player.message("The door won't open");
				}
				break;

			case DOOR_TEMPLE_IKOV_FEAR_ROOM: // Temple of Ikov: First door (533, 3342)
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.PENDANT_OF_LUCIEN.id()) || player.getY() >= 3335 && player.getY() <= 3341) {
					player.message("You go through the door");
					doDoor(obj, player);
				} else {
					mes("As you reach to open the door");
					delay(3);
					mes("A great terror comes over you");
					delay(3);
					mes("You decide you'll not open this door today");
					delay(3);
				}
				break;

			case DOOR_TEMPLE_IKOV_LEVER_PIECE: // Temple of Ikov: Bridge door (546, 3328)
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.BOOTS_OF_LIGHTFOOTEDNESS.id()) || player.getX() >= 546) {
					player.message("You go through the door");
					doDoor(obj, player);
				} else {
					player.message("Your weight is too much for the bridge to hold");
					player.teleport(544, 3330);
					delay();
					player.message("You fall through the bridge");
					delay(2);
					player.message("The lava singes you");
					player.damage(DataConversions.roundUp(player.getSkills().getLevel(Skill.HITS.id()) / 5));
				}
				break;

			case DOORFRAME_GRAY_BRICKS:
				replaceGameObject(obj, player, BoundaryId.DOOR_GRAY_BRICKS.id(), false);
				break;

			case DOOR_GRAY_BRICKS:
				replaceGameObject(obj, player, BoundaryId.DOORFRAME_GRAY_BRICKS.id(), true);
				break;

			case DOOR_PICK_LOCK:
			case DOOR_BRASS_KEY:
				player.message("The door is locked");
				break;

			case DOOR_KHAZARD_HOUSE: // Fight Arena (621, 699), (603, 717)
				boolean stop = false;
				Npc guard = null;
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.KHAZARD_HELMET.id())
					&& player.getCarriedItems().getEquipment().hasEquipped(ItemId.KHAZARD_CHAINMAIL.id())) {
					doDoor(obj, player);
				} else if (obj.getX() == 603 && player.getX() < obj.getX()) {
					guard = ifnearvisnpc(player, NpcId.GUARD_KHAZARD_BYPRISONER.id(), 8);
					stop = true;
				} else if (obj.getY() == 699 && player.getY() < obj.getY()) {
					guard = ifnearvisnpc(player, NpcId.GUARD_KHAZARD_BRIBABLE.id(), 8);
					stop = true;
				} else {
					//inside building does not matter to exit
					doDoor(obj, player);
				}
				if (stop && guard != null) {
					if (player.getQuestStage(Quests.FIGHT_ARENA) == 0) {
						npcsay(player, guard, "you there! halt!",
								"this is General Khazard's private lodgings",
								"what's your business here?");
						say(player, guard, "I'm looking for the 'Servil' prisoners");
						npcsay(player, guard, "wait until tomorrow, then you can",
								"see them butchered in the arena haha",
								"Now OUT and don't come back!");
					} else {
						npcsay(player, guard, "you there! halt!",
								"this is General Khazard's private lodgings",
								"now leave and don't return!");
					}
				}
				break;

			case DOOR_KHAZARD_BFIELD_LOBBY: // Fight Arena (615, 715), (619, 711)
				if (player.getCache().hasKey("freed_servil")
					|| player.getQuestStage(Quests.FIGHT_ARENA) == -1
					|| player.getQuestStage(Quests.FIGHT_ARENA) == 3) {
					doDoor(obj, player);
					return;
				}
				Npc guardArenaEntrance = ifnearvisnpc(player, NpcId.GUARD_KHAZARD_ARENA_ENTRANCE.id(), 8);
				if (guardArenaEntrance != null) {
					npcsay(player, guardArenaEntrance, "and where do you think you're going?",
						"only General Khazard decides who fights in the arena",
						"so get out of here");
				}
				break;

			case DOOR_PLAGUE_UNOPENABLE: // Plague city / Biohazard - unsure the purpose of this door
				if (player.getX() > 624) {
					Npc mourner = ifnearvisnpc(player, NpcId.MOURNER_WESTARDOUGNE.id(), 8);
					player.message("The door won't open");
					player.message("You notice a black cross on the door");
					if (mourner != null) {
						npcsay(player, mourner, "I'd stand away from there",
							"That black cross means that house has been touched by the plague");
					}
				}
				break;

			case DOOR_REHNISON_FAMILY: // Plague City
				if (player.getQuestStage(Quests.PLAGUE_CITY) >= 6
					|| player.getQuestStage(Quests.PLAGUE_CITY) == -1) {
					if (player.getY() >= 569) {
						doDoor(obj, player);
						player.message("You go through the door");
					} else {
						doDoor(obj, player);
						player.message("You go through the door");
					}
					return;
				}
				Npc ted = ifnearvisnpc(player, NpcId.TED_REHNISON.id(), 8);
				if (ted != null) {
					player.message("The door won't open");
					npcsay(player, ted, "Go away we don't want any");
					if (player.getY() >= 569) {
						if (player.getCarriedItems().remove(new Item(ItemId.PLAGUE_CITY_BOOK.id())) != -1) {
							say(player, ted,
								"I have come to return a book from Jethick");
							npcsay(player, ted, "Ok I guess you can come in then");
							doDoor(obj, player);
							player.updateQuestStage(Quests.PLAGUE_CITY, 6);
						}
					}
				}
				break;

			case DOOR_INFECTED_CAPTURED_ELENA: // Plague City
				Npc mourner = ifnearvisnpc(player, NpcId.MOURNER_WESTARDOUGNE.id(), 8);
				if (player.getQuestStage(Quests.PLAGUE_CITY) == 11
					|| player.getQuestStage(Quests.PLAGUE_CITY) == -1) {
					doDoor(obj, player);
					return;
				}
				if (player.getY() <= 605 || player.getY() >= 612) {
					player.message("The door won't open");
					player.message("You notice a black cross on the door");
					if (mourner != null) {
						npcsay(player, mourner, "I'd stand away from there",
							"That black cross means that house has been touched by the plague");
						if (player.getCarriedItems().hasCatalogID(ItemId.WARRANT.id(), Optional.of(false))) {
							say(player, mourner,
								"I have a warrant from Bravek to enter here");
							npcsay(player, mourner, "this is highly irregular",
								"Please wait while I speak to the head mourner");
							player.message("You wait until the mourner's back is turned and sneak into the building");
							doDoor(obj, player);
							return;
						}
						if (player.getQuestStage(Quests.PLAGUE_CITY) == 7) {
							int menu = multi(player, mourner, false, //do not send over
								"but I think a kidnap victim is in here",
								"I fear not a mere plague",
								"thanks for the warning");
							if (menu == 0) {
								say(player, mourner, "But I think a kidnap victim is in here");
								npcsay(player, mourner, "Sounds unlikely",
									"Even kidnappers wouldn't go in there",
									"even if someone is in there",
									"They're probably dead by now");
								int menu2 = multi(player, mourner, "Good point",
									"I want to check anyway");
								if (menu2 == 0) {
									// NOTHING
								} else if (menu2 == 1) {
									npcsay(player, mourner, "You don't have clearance to go in there");
									say(player, mourner, "How do I get clearance?");
									npcsay(player,
										mourner,
										"Well you'd need to apply to the head mourner",
										"Or I suppose Bravek the city warder",
										"I wouldn't get your hopes up though");
									player.updateQuestStage(Quests.PLAGUE_CITY, 8);
								}
							} else if (menu == 1) {
								say(player, mourner, "I fear not a mere plague");
								npcsay(player, mourner, "that's irrelevant",
									"You don't have clearance to go in there");
								say(player, mourner, "How do I get clearance?");
								npcsay(player,
									mourner,
									"Well you'd need to apply to the head mourner",
									"Or I suppose Bravek the city warder",
									"I wouldn't get your hopes up though");
								player.updateQuestStage(Quests.PLAGUE_CITY, 8);

							} else if (menu == 2) {
								say(player, mourner, "thanks for the warning");
							}
						}
					}
				} else {
					doDoor(obj, player);
				}
				break;

			case DOOR_TO_BRAVEK: // Plague City
				Npc Bravek = ifnearvisnpc(player, NpcId.BRAVEK.id(), 8);
				if (player.getQuestStage(Quests.PLAGUE_CITY) >= 9
					|| player.getQuestStage(Quests.PLAGUE_CITY) == -1) {
					doDoor(obj, player);
					return;
				}
				if (player.getX() >= 648) {
					if (Bravek != null) {
						npcsay(player, Bravek, "Go away,I'm busy", "I'm", "um",
								"In a meeting");
					}
					player.message("The door won't open");
				} else {
					doDoor(obj, player);
				}
				break;

			case DOOR_KHAZARD_BATTLE_FIELD:
				doDoor(obj, player);
				break;

			/** Guild Doors */
			case DOOR_FISHING_GUILD: // Fishing Guild Door
				if (obj.getX() != 586 || obj.getY() != 524) {
					break;
				}
				if (getCurrentLevel(player, Skill.FISHING.id()) < 68) {
					Npc masterFisher = player.getWorld().getNpc(NpcId.MASTER_FISHER.id(), 582, 588,
						524, 527);
					if (masterFisher != null) {
						npcsay(player, masterFisher, "Hello only the top fishers are allowed in here");
					}
					delay(2);
					player.message("You need a fishing level of 68 to enter");
				} else {
					doDoor(obj, player);
				}
				break;

			case DOOR_MINING_GUILD: // Mining Guild Door
				if (obj.getX() != 268 || obj.getY() != 3381) {
					break;
				}
				if (player.getY() < 3381) {
					if (getCurrentLevel(player, Skill.MINING.id()) < 60) {
						Npc dwarf = player.getWorld().getNpc(NpcId.DWARF_MINING_GUILD.id(), 265, 270, 3379, 3380);
						if (dwarf != null) {
							npcsay(player, dwarf, "Sorry only the top miners are allowed in there");
						}
						delay();
						player.message("The door won't open - you need a mining of level 60 to enter");
					} else {
						doDoor(obj, player);
					}
				} else {
					doDoor(obj, player);
				}
				break;

			case DOOR_CRAFTING_GUILD: // Crafting Guild Door
				if (obj.getX() != 347 || obj.getY() != 601) {
					return;
				}
				if (getCurrentLevel(player, Skill.CRAFTING.id()) < 40) {
					Npc master = player.getWorld().getNpc(NpcId.MASTER_CRAFTER.id(), 341, 349, 599, 612);
					if (master != null) {
						npcsay(player, master, "Sorry only experienced craftsmen are allowed in here");
					}
					delay();
					player.message("You need a crafting level of 40 to enter the guild");
				} else if (!(player.getCarriedItems().getEquipment().hasEquipped(ItemId.BROWN_APRON.id())
					|| player.getCarriedItems().getEquipment().hasEquipped(ItemId.CRAFTING_CAPE.id()))) {
					Npc master = player.getWorld().getNpc(NpcId.MASTER_CRAFTER.id(), 341, 349, 599, 612);
					if (master != null) {
						npcsay(player, master, "Where's your brown apron?",
							"You can't come in here unless you're wearing a brown apron");
					}
				} else {
					doDoor(obj, player);
				}
				break;

			case DOOR_COOKING_GUILD: // Cooking Guild Door
				if (obj.getX() != 179 || obj.getY() != 488) {
					break;
				}
				if (getCurrentLevel(player, Skill.COOKING.id()) < 32) {
					Npc chef = player.getWorld().getNpc(NpcId.HEAD_CHEF.id(), 176, 181, 480, 487);
					if (chef != null) {
						npcsay(player, chef, "Sorry. Only the finest chefs are allowed in here");
					}
					delay();
					player.message("You need a cooking level of 32 to enter");
				} else if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.CHEFS_HAT.id())
				&& !player.getCarriedItems().getEquipment().hasEquipped(ItemId.COOKING_CAPE.id())) {
					Npc chef = player.getWorld().getNpc(NpcId.HEAD_CHEF.id(), 176, 181, 480, 487);
					if (chef != null) {
						npcsay(player, chef, "Where's your chef's hat",
							"You can't come in here unless you're wearing a chef's hat");
					}
				} else {
					doDoor(obj, player);
				}
				break;

			case DOOR_WIZARDS_GUILD: // Magic Guild Door
				if (obj.getX() != 599 || obj.getY() != 757) {
					break;
				}
				if (getCurrentLevel(player, Skill.MAGIC.id()) < 66) {
					Npc wizard = player.getWorld().getNpc(NpcId.HEAD_WIZARD.id(), 596, 597, 755, 758);
					if (wizard != null) {
						npcsay(player, wizard, "You need a magic level of 66 to get in here",
							"The magical energy in here is unsafe for those below that level");
					}
				} else {
					doDoor(obj, player);
				}
				break;

			case ODD_LOOKING_WALL: // Odd looking wall (545, 3283) & (219, 3282)
				player.playSound("secretdoor");
				doDoor(obj, player, BoundaryId.NOTHING.id());
				player.message("You just went through a secret door");
				break;

			case DOOR_BLACK_KNIGHT_GUARD_ENTRANCE: // Black Knight Guard Door
				if (obj.getX() != 271 || obj.getY() != 441) {
					return;
				}
				if (player.getX() <= 270) {
					if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.IRON_CHAIN_MAIL_BODY.id())
						|| !player.getCarriedItems().getEquipment().hasEquipped(ItemId.MEDIUM_BRONZE_HELMET.id())) {
						player.message(
							"Only guards are allowed in there!");
						return;
					}
					doDoor(obj, player);
				}
				break;

			case DOOR_DRAYNOR_MANOR_LOBBY: // Draynor mansion front door
				if (obj.getX() != 210 || obj.getY() != 553) {
					return;
				}
				if (player.getY() >= 553) {
					doDoor(obj, player);
				} else {
					player.message("The door won't open");
				}
				break;

			case DOOR_DRAYNOR_MANOR_BACK_DOOR: // Draynor mansion back door
				if (obj.getX() != 199 || obj.getY() != 551) {
					return;
				}
				doDoor(obj, player);
				player.message("You go through the door");
				break;

			case DOOR_ERNEST_CENTER_MIDDLE_NORTH_OR_LOCKED: // Locked Doors
				player.message("The door is locked");
				break;

			case DOOR_CHAMPIONS_GUILD: // champs guild door
				if (obj.getX() != 150 && obj.getY() != 554) {// champs guild door
					return;
				}
				if ((!player.getConfig().INFLUENCE_INSTEAD_QP && player.getQuestPoints() < 32)
					|| (player.getConfig().INFLUENCE_INSTEAD_QP && player.getSkills().getLevel(Skill.INFLUENCE.id()) < 20)) {
					final Npc champy = ifnearvisnpc(player, NpcId.GUILDMASTER.id(), 20);
					if (champy != null) {
						npcsay(player, champy,
							"You have not proven yourself worthy to enter here yet");
						if (!player.getConfig().INFLUENCE_INSTEAD_QP && player.getQuestPoints() < 32) {
							mes("The door won't open - you need at least 32 quest points");
						} else {
							mes("The door won't open - you need the highest available influence");
						}
						delay(3);
					}
					return;
				}
				doDoor(obj, player);
				break;

			case DOOR_LOST_CITY_MARKETPLACE: // Lost City Market Door (117, 3539), (116, 3537) NPC: 221
				if (player.getLocation().getX() == 115 || player.getLocation().getY() == 3539) {
					Npc n = player.getWorld().getNpc(NpcId.DOORMAN.id(), 105, 116, 3536, 3547);
					if (n != null) {
						npcsay(player, n,
							"You cannot go through this door without paying the trading tax");
						say(player, n, "What do I need to pay?");
						npcsay(player, n, "One diamond");
						int m = multi(player, n, false, //do not send over
							"Okay", "A diamond, are you crazy?",
							"I haven't brought my diamonds with me");
						if (m == 0) {
							say(player, n, "Okay");
							if (!player.getCarriedItems().hasCatalogID(ItemId.DIAMOND.id(), Optional.of(false))) {
								say(player, n,
									"I haven't brought my diamonds with me");
							} else {
								player.message("You give the doorman a diamond");
								player.getCarriedItems().remove(new Item(ItemId.DIAMOND.id()));
								doDoor(obj, player);
							}
						} else if (m == 1) {
							say(player, n, "A diamond?", "are you crazy?");
							npcsay(player, n, "Nope those are the rules");
						} else if (m == 2) {
							say(player, n, "I haven't brought my diamonds with me");
						}
					}
					break;
				}
				if (player.getLocation().getX() == 116 || player.getLocation().getY() == 3538) {
					Npc n = player.getWorld().getNpc(NpcId.DOORMAN.id(), 117, 125, 3531, 3538);

					if (n != null) {
						npcsay(player, n,
							"You cannot go through this door without paying the trading tax");
						say(player, n, "What do I need to pay?");
						npcsay(player, n, "One diamond");
						int m = multi(player, n, false, //do not send over
							"Okay", "A diamond, are you crazy?",
							"I haven't brought my diamonds with me");
						if (m == 0) {
							say(player, n, "Okay");
							if (!player.getCarriedItems().hasCatalogID(ItemId.DIAMOND.id(), Optional.of(false))) {
								say(player, n,
									"I haven't brought my diamonds with me");
							} else {
								player.message("You give the doorman a diamond");
								player.getCarriedItems().remove(new Item(ItemId.DIAMOND.id()));
								doDoor(obj, player);
							}
						} else if (m == 1) {
							say(player, n, "A diamond?", "are you crazy?");
							npcsay(player, n, "Nope those are the rules");
						} else if (m == 2) {
							say(player, n, "I haven't brought my diamonds with me");
						}
					}
				}
				break;

			case DOOR_TRAINING_OGRES_AND_PRACTICE_ZOMBIES: // Fight Arena: Ogre Cage (?)
				doDoor(obj, player);
				break;

			case DOOR_MOURNER_HEADQUARTERS: // Biohazard
				if (!player.getCache().hasKey("rotten_apples") && player.getQuestStage(Quests.BIOHAZARD) == 4) {
					mes("the door is locked");
					delay(3);
					mes("inside you can hear the mourners eating");
					delay(3);
					mes("you need to distract them from their stew");
					delay(3);
				} else if (player.getCache().hasKey("rotten_apples") || player.getQuestStage(Quests.BIOHAZARD) == 5) {
					if (player.getY() <= 572) {
						doDoor(obj, player);
						player.playerServerMessage(MessageType.QUEST, "you open the door");
						player.playerServerMessage(MessageType.QUEST, "You go through the door");
						return;
					}
					Npc DOOR_MOURNER = ifnearvisnpc(player, NpcId.MOURNER_DOOR.id(), 10);
					if (DOOR_MOURNER != null) {
						if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.DOCTORS_GOWN.id())) {
							npcsay(player, DOOR_MOURNER, "in you go doc");
							doDoor(obj, player);
							player.playerServerMessage(MessageType.QUEST, "You go through the door");
						} else {
							npcsay(player, DOOR_MOURNER, "keep away from there");
							say(player, DOOR_MOURNER, "why?");
							npcsay(player, DOOR_MOURNER, "several mourners are ill with food poisoning",
								"we're waiting for a doctor");
						}
					}
				} else if ((player.getQuestStage(Quests.BIOHAZARD) > 5 || player.getQuestStage(Quests.BIOHAZARD) == -1) &&
					player.getCarriedItems().getEquipment().hasEquipped(ItemId.DOCTORS_GOWN.id())) {
					doDoor(obj, player);
				} else {
					player.message("the door is locked");
				}
				break;

			case DOOR_SICK_MOURNER: // Biohazard
				if (player.getY() >= 1513) {
					doDoor(obj, player);
					player.playerServerMessage(MessageType.QUEST, "You go through the door");
					return;
				}
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.DOCTORS_GOWN.id())) {
					doDoor(obj, player);
					player.playerServerMessage(MessageType.QUEST, "You go through the door");
				} else {
					player.message("the mourner is refusing to open the door");
				}
				break;

			case DOOR_GUIDOR_ROOM: // Biohazard
				if (player.getQuestStage(Quests.BIOHAZARD) == 7 || player.getQuestStage(Quests.BIOHAZARD) == 8 || player.getQuestStage(Quests.BIOHAZARD) == 9 || player.getQuestStage(Quests.BIOHAZARD) == -1) {
					if (player.getX() <= 82) {
						player.playerServerMessage(MessageType.QUEST, "You go through the door");
						doDoor(obj, player);
						return;
					}
					if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.PRIEST_ROBE.id()) && player.getCarriedItems().getEquipment().hasEquipped(ItemId.PRIEST_GOWN.id())) {
						player.message("guidors wife allows you to go in");
						player.playerServerMessage(MessageType.QUEST, "You go through the door");
						doDoor(obj, player);
					} else {
						player.message("guildors wife refuses to let you enter");
					}
				} else {
					player.message("the door is locked");
				}
				break;
		}
	}

	private void sendToPortSarim(Player player, Npc n, int path) {
		if (path == 0) {
			npcsay(player, n,
				"You are to be transported to a maximum security prison in Port Sarim.",
				"I hope you've learnt an important lesson from this.");
		} else if (path == 1) {
			npcsay(player, n,
				"Very well, I grow tired of you, you'll be taken to a new jail in Port Sarim.");
		}
		player.teleport(281, 665, false);
		player.getCache().remove("shantay_jail");
	}

	public boolean blockInvUseOnWallObject(GameObject obj, Item item,
										   Player player) {

		/* Ernest the Chicken */
		if (obj.getID() == BoundaryId.DOOR_DRAYNOR_MANOR_CLOSET_KEY.id() && obj.getY() == 545) {
			return true;
		}
		if (obj.getID() == BoundaryId.DOOR_SHINY_KEY.id()) {
			return true;
		}
		/* Dragon Slayer Maze Doors */
		if (obj.getID() >= BoundaryId.DOOR_MELZAR_RED_KEY.id()
			&& obj.getID() <= BoundaryId.DOOR_MELZAR_MAGENTA_KEY.id()
			|| obj.getID() == BoundaryId.DOOR_MAZE_KEY.id()) {
			return true;
		}
		/* Shield of arrav */
		if (obj.getID() == BoundaryId.DOOR_PHOENIX_WEAPON_KEY.id()) {
			return true;
		}
		/* witches house door */
		if (obj.getID() == BoundaryId.DOOR_WITCHS_FRONT_KEY.id()) {
			return true;
		}
		/* varrocks shortcut to edgeville dung*/
		if (obj.getID() == BoundaryId.DOOR_BRASS_KEY.id()) {
			return true;
		}
		/* taverly dungeon door near jailer */
		if ((obj.getID() == BoundaryId.DOOR_JAIL_KEY.id() && obj.getX() == 360 && obj.getY() == 3428)
			|| (obj.getID() == BoundaryId.DOOR_JAIL_KEY.id() && obj.getX() == 360
			&& obj.getY() == 3425)) {
			return true;
		}
		/* Door to enter blue dragons in taverly dungeon */
		if (obj.getID() == BoundaryId.DOOR_DUSTY_KEY.id() && obj.getX() == 355 && obj.getY() == 3353) {
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
		switch (BoundaryId.getById(obj.getID())) {
			//Brass key
			case DOOR_BRASS_KEY:
				keyItem = ItemId.BRASS_KEY.id();
				showsBubble = true;
				break;
			/* Ernest the Chicken */
			case DOOR_DRAYNOR_MANOR_CLOSET_KEY:
				keyItem = ItemId.CLOSET_KEY.id();
				showsBubble = true;
				break;

			case DOOR_WITCHS_FRONT_KEY: // Witches house
				keyItem = ItemId.FRONT_DOOR_KEY.id();
				if (player.getCache().hasKey("witch_spawned")) {
					player.getCache().remove("witch_spawned");
				}
				showsBubble = false;
				break;
			//Jail door in Taverley Dungeon
			case DOOR_JAIL_KEY:
				keyItem = ItemId.JAIL_KEYS.id();
				showsBubble = true;
				break;
			//Dusty key
			case DOOR_DUSTY_KEY:
				keyItem = ItemId.DUSTY_KEY.id();
				showsBubble = true;
				break;

			/* Dragon Slayer maze doors */
			case DOOR_MELZAR_RED_KEY: /* Red door */
				keyItem = ItemId.RED_KEY.id();
				remove = true;
				showsBubble = false;
				break;
			case DOOR_MELZAR_ORANGE_KEY: /* Orange door */
				keyItem = ItemId.ORANGE_KEY.id();
				remove = true;
				showsBubble = false;
				break;
			case DOOR_MELZAR_YELLOW_KEY: /* Yellow door */
				keyItem = ItemId.YELLOW_KEY.id();
				remove = true;
				showsBubble = false;
				break;
			case DOOR_MELZAR_BLUE_KEY: /* Blue door */
				keyItem = ItemId.BLUE_KEY.id();
				remove = true;
				showsBubble = false;
				break;
			case DOOR_MELZAR_MAGENTA_KEY: /* Magenta door */
				keyItem = ItemId.MAGENTA_KEY.id();
				remove = true;
				showsBubble = false;
				break;
			case DOOR_MELZAR_BLACK_KEY: /* Black door */
				keyItem = ItemId.BLACK_KEY.id();
				remove = true;
				showsBubble = false;
				break;
			case DOOR_MAZE_KEY: /* Maze entrace */
				keyItem = ItemId.MAZE_KEY.id();
				remove = false;
				showsBubble = false;
				break;
			/* End of dragon slayer maze */
			// Temple of Ikov
			case DOOR_SHINY_KEY:
				keyItem = ItemId.SHINY_KEY.id();
				remove = false;
				showsBubble = true;
				break;
		}
		if (player.getCarriedItems().hasCatalogID(keyItem) && item.getCatalogId() == keyItem) {
			if (keyItem == ItemId.FRONT_DOOR_KEY.id() && player.getQuestStage(Quests.WITCHS_HOUSE) == 0) {
				say(player, null, "It'd be rude to break into this house");
				return;
			}
			if (showsBubble) {
				thinkbubble(item);
			}
			player.message("you unlock the door");
			doDoor(obj, player);
			player.message("you go through the door");
			if (remove) {
				player.message("Your " + item.getDef(player.getWorld()).getName().toLowerCase() + " has gone!");
				player.getCarriedItems().remove(new Item(keyItem));
				if (obj.getID() == BoundaryId.DOOR_MELZAR_BLACK_KEY.id()) {
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

		switch (SceneryId.getById(obj.getID())) {
			case BLACK_KNIGHTS_FORTRESS_DOORS: // Black Knight Big Door
				player.message("the doors are locked");
				break;
			/* Regular Doors */
			case CHEST_GENERIC_CLOSED:
				replaceGameObject(SceneryId.CHEST_GENERIC_OPEN.id(), true, player, obj);
				break;
			case CHEST_GENERIC_OPEN:
				replaceGameObject(SceneryId.CHEST_GENERIC_CLOSED.id(), false, player, obj);
				break;
			case GATE_METAL_GENERIC_OPEN:
				replaceGameObject(SceneryId.GATE_METAL_GENERIC_CLOSED.id(), false, player, obj);
				break;

			case GATE_METAL_GENERIC_CLOSED:
				replaceGameObject(SceneryId.GATE_METAL_GENERIC_OPEN.id(), true, player, obj);
				break;

			case DOOR_BANK_OPEN:
				replaceGameObject(SceneryId.DOOR_BANK_CLOSED.id(), false, player, obj);
				break;

			case DOOR_BANK_CLOSED:
				if (obj.getX() == 467 && obj.getY() == 518) {
					player.message("The doors are locked");
					break;
				} else if (obj.getX() == 558 && obj.getY() == 587) {
					player.message("The doors are locked");
					break;
				} else {
					replaceGameObject(SceneryId.DOOR_BANK_OPEN.id(), true, player, obj);
				}
				break;

			case MANHOLE_OPEN:
				replaceGameObject(SceneryId.MANHOLE_CLOSED.id(), false, player, obj);
				break;

			case MANHOLE_CLOSED:
				replaceGameObject(SceneryId.MANHOLE_OPEN.id(), true, player, obj);
				break;

			case COFFIN_DRAYNOR_MANOR_CLOSED:
				replaceGameObject(SceneryId.COFFIN_DRAYNOR_MANOR_OPEN.id(), true, player, obj);
				break;

			case COFFIN_DRAYNOR_MANOR_OPEN:
				replaceGameObject(SceneryId.COFFIN_DRAYNOR_MANOR_CLOSED.id(), false, player, obj);
				break;

			default:
				player.message(
					"Nothing interesting happens");
		}
	}

	private void handleGates(GameObject obj, Player player) {
		boolean members = false;

		switch (SceneryId.getById(obj.getID())) {
			case GATE_WOODEN_GENERIC_CLOSED:
				replaceGameObject(SceneryId.GATE_WOODEN_GENERIC_OPEN.id(), true, player, obj);
				return;

			case GATE_WOODEN_GENERIC_OPEN:
				replaceGameObject(SceneryId.GATE_WOODEN_GENERIC_CLOSED.id(), false, player, obj);
				return;

			case GATE_METAL_GENERIC_CLOSED:
				replaceGameObject(obj, player, SceneryId.GATE_METAL_GENERIC_OPEN.id(), true);
				return;

			case GATE_METAL_GENERIC_OPEN:
				replaceGameObject(obj, player, SceneryId.GATE_METAL_GENERIC_CLOSED.id(), false);
				return;

			/** Gnome glider Karamja gate **/
			case GATE_WOODEN_KARAMJA_GLIDER_CLOSED: // (387, 760)
				if (obj.getX() != 387 || obj.getY() != 760) {
					return;
				}
				player.message("you open the gate");
				doGate(player, obj, SceneryId.GATE_WOODEN_FISHING_CONTEST_KARAMJA_GLIDER_OPEN.id());
				delay(2);
				player.message("and walk through");
				return;

			case GATE_MCGRUBORS_WOOD: // McGrouber's Wood (560, 472)
				if (obj.getX() != 560 || obj.getY() != 472) {
					return;
				}
				if (player.getY() <= 472) {
					if (!config().WANT_WOODCUTTING_GUILD) {
						player.playerServerMessage(MessageType.QUEST, "the gate is locked");
					} else {
						doGate(player, obj);
					}
				} else {
					if (config().WANT_WOODCUTTING_GUILD) {
						WoodcuttingGuild.frontGate(player);
						return;
					}
					// 8 is the authentic radius for ifnearvisnpc, however this could have used ifnearnpc which has a radius of 16
					final Npc forester = ifnearvisnpc(player, NpcId.FORESTER.id(), 8);
					if (forester != null) {
						npcsay(player, forester, "Hey you can't come through here", "This is private land");
						delay(2);
						player.playerServerMessage(MessageType.QUEST, "You will need to find another way in");
					} else {
						player.playerServerMessage(MessageType.QUEST, "You will need to find another way in");
						delay(2);
						player.playerServerMessage(MessageType.QUEST, "the gate is locked");
					}
				}
				return;

			case WOODEN_GATE_SHILO_VILLAGE_CLOSED: // Shilo inside gate
				if (obj.getX() != 394 || obj.getY() != 851) {
					return;
				}
				if (player.getX() >= 394) {
					mes("The gate opens smoothly");
					delay(3);
					player.teleport(381, 851);
					player.message("You make your way out of Shilo Village.");
				} else {
					player.message("The gate won't open");
				}
				return;

			case METAL_GATE_SHILO_VILLAGE_CLOSED: // Shilo outside gate
				if (obj.getX() != 388 || obj.getY() != 851) {
					return;
				}
				if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
					player.teleport(395, 851);
					player.message("You open the gates and make your way through into");
					player.message("the village.");
					return;
				}
				mes("The gate feels very cold to your touch!");
				delay(3);
				player.message("Are you sure you want to go through?");
				int menu = multi(player,
					"Yes, I am fearless!",
					"No, actually, I have a bad feeling about this!");
				if (menu == 0) {
					changeloc(obj, 3000, SceneryId.METAL_GATE_SHILO_VILLAGE_OPEN.id());
					if (player.getX() >= 388) {
						mes("The gates open very slowly.");
						delay(3);
						player.teleport(387, 852);
						player.message("You manage to drag your battered body back through the gates.");
					} else {
						mes("The gates open very slowly...");
						delay(3);
						player.teleport(389, 852);
						mes("As soon as the gates open, the Zombies grab you and start dragging you inside!");
						delay(3);
						player.teleport(391, 852);
						say(player, null, "Oh no, I'm done for!");
					}
				} else if (menu == 1) {
					mes("You drag your quivering body  away from the gates.");
					delay(3);
					player.message("You look around, but you don't think anyone saw you.");
				}
				return;

			case GATE_VARROCK_BIOHAZARD_CLOSED: // eastern Varrock gate for family crest or biohazard or just wanna go in :)
				if (obj.getX() != 93 || obj.getY() != 521) {
					return;
				}
				if (!config().MEMBER_WORLD) {
					player.message(
						"You need to be a member to use this gate");
					return;
				}
				if (player.getX() >= 94) {
					if (player.getQuestStage(Quests.BIOHAZARD) >= 6
						&& player.getQuestStage(Quests.BIOHAZARD) < 8) {
						Npc guard = ifnearvisnpc(player, NpcId.GUARD_VARROCKGATE.id(), 10);
						if (guard != null) {
							npcsay(player, guard, "Halt. I need to conduct a search on you",
								"There have been reports of a someone bringing a virus into Varrock");
						}
						if (player.getCarriedItems().hasCatalogID(ItemId.ETHENEA.id(), Optional.of(false))) {
							player.getCarriedItems().remove(new Item(ItemId.ETHENEA.id()));
							player.message("He takes the vial of ethenea from you");
						}
						if (player.getCarriedItems().hasCatalogID(ItemId.SULPHURIC_BROLINE.id(), Optional.of(false))) {
							player.getCarriedItems().remove(new Item(ItemId.SULPHURIC_BROLINE.id()));
							player.message("He takes the vial of sulphuric broline from you");
						}
						if (player.getCarriedItems().hasCatalogID(ItemId.LIQUID_HONEY.id(), Optional.of(false))) {
							player.getCarriedItems().remove(new Item(ItemId.LIQUID_HONEY.id()));
							player.message("He takes the vial of liquid honey from you");
						}
					}
					player.message("you open the gate and pass through");
					doGate(player, obj, SceneryId.GATE_VARROCK_BIOHAZARD_OPEN.id(), new Point(92, 522));
				} else {
					player.message("you open the gate and pass through");
					doGate(player, obj, SceneryId.GATE_VARROCK_BIOHAZARD_OPEN.id());
				}
				return;

			case GATE_MEMBERS_RED_DRAGONS_AND_RETRO_PLAYER_HOUSES: // Red dragon gate (140, 180)
				members = true;
				break;

			case GATE_MEMBERS_TAVERLY_AND_RETRO_ASGARNIA: // Members Gate near Doric (341, 487)
				members = true;
				break;

			case GATE_MEMBERS_WILDERNESS_ICE_GIANT: // Members Gate of Frozen Waste Plateau (331,142)
				members = true;
				break;

			case GATE_MEMBERS_NE_WILDERNESS: // Members Gate NW of Greaters (111,142)
				members = true;
				break;

			case GATE_MEMBERS_CRAFTING_GUILD_RETRO_BANK_VAULT: // Members Gate near Crafting Guild (343, 581)
				members = true;
				break;

			case GATE_MEMBERS_KARAMJA: // Members Gate near Brimhaven (434, 682)
				members = true;
				break;

			case GATE_COMBAT_CAMP: // King Lathas Training Area (660, 551)
				Npc lathasGuard = ifnearvisnpc(player, NpcId.GUARD_GATE_TRAINING_CAMP.id(), 10);
				if (player.getQuestStage(Quests.BIOHAZARD) == -1) {
					if (player.getY() <= 551) {
						doGate(player, obj);
					} else {
						if (lathasGuard != null) {
							npcsay(player, lathasGuard, "the king has granted you access to this training area",
								"make good use of it, soon all you strength will be needed");
						}
						doGate(player, obj);
					}
					player.message("you open the gate");
				} else {
					npcsay(player, lathasGuard, "this is a restricted area",
						"you can only enter under the authority of king lathas");
				}
				return;

			case GNOME_STRONGHOLD_GATE: // Gnome Stronghold Gate (703, 531)
				if (player.getY() <= 531 && player.getQuestStage(Quests.GRAND_TREE) == 8) {
					boolean spawned = false;
					Npc n = ifnearvisnpc(player, NpcId.GNOME_GUARD.id(), 15);
					if (n == null) {
						n = addnpc(player.getWorld(), NpcId.GNOME_GUARD.id(), 705, 530, 30000);
						n.setBusy(true);
						spawned = true;
					}
					npcsay(player, n, "halt human");
					say(player, n, "what?, why?");
					npcsay(player, n, "from order of the head tree guardian...",
						"..you cannot leave");
					say(player, n, "that's crazy, why?");
					npcsay(player, n, "humans are planning to attack our stronghold",
						"you could be a spy");
					say(player, n, "that's ridiculous");
					npcsay(player, n, "maybe, but that's the orders, I'm sorry");
					mes("the gnome refuses to open the gate");
					delay(3);
					if (spawned) {
						n.setBusy(false);
						n.remove();
					}
					return;

				} else if (player.getY() >= 532 && player.getQuestStage(Quests.GRAND_TREE) == 10) {
					Npc n = ifnearvisnpc(player, NpcId.GNOME_GUARD.id(), 15);
					npcsay(player, n, "i'm afraid that we have orders not to let you in");
					say(player, n, "orders from who?");
					npcsay(player, n, "the head tree guardian, he say's you're a spy");
					say(player, n, "glough!");
					npcsay(player, n, "i'm sorry but you'll have to leave");
					return;
				}
				members = true;
				break;

			case GATE_BANK_VAULT: // Bank Vault Gate
				members = false;
				player.playerServerMessage(MessageType.QUEST, "the gate is locked");
				return;

			case GATE_MEMBERS_EDGEVILLE_DUNGEON: // Members Gate in Edgeville Dungeon (196, 1266)
				members = true;
				break;

			case GATE_MEMBERS_DIGSITE: // Members Gate near Dig Site (59, 573)
				members = true;
				break;

			case GATE_MEMBERS_WILDERNESS_KBD: // Members Gate near King Black Dragon ladder (285, 185)
				members = true;
				break;

			case GATE_MEMBERS_WILDERNESS_BLACK_DRAGON: // Members Gate near Lava Maze Dungeon ladder (243, 178)
				members = true;
				break;

			default:
				player.message(
					"Nothing interesting happens");

				return;
		}
		if (members && !config().MEMBER_WORLD) {
			player.message(
				"You need to be a member to use this gate");
			return;
		}
		player.message("you go through the gate");
		doGate(player, obj);
		/*if(player.getY() >= 141 && obj.getID() == 347) { // Not authentic
			// Unwield so they cannot be stationary with it
			player.unwieldMembersItems();
		}*/
	}

	// replaces but does not notify the player of the action
	private void replaceGameObject(final int newID, final boolean open,
								   final Player player, final GameObject object) {
		player.getWorld().replaceGameObject(object,
			new GameObject(object.getWorld(), object.getLocation(), newID, object
				.getDirection(), object.getType()));
		player.playSound(open ? "opendoor" : "closedoor");
	}

	// replaces and notifies player on action taken
	private void replaceGameObject(GameObject obj, Player owner, int newID, boolean open) {
		if (open) {
			owner.message("The " + (obj.getGameObjectDef().getName().equalsIgnoreCase("gate") ? "gate" : "door") + " swings open");
		} else {
			owner.message("The " + (obj.getGameObjectDef().getName().equalsIgnoreCase("gate") ? "gate" : "door") + " creaks shut");
		}
		owner.playSound(open ? "opendoor" : "closedoor");
		owner.getWorld().replaceGameObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), newID, obj.getDirection(), obj.getType()));
	}
}
