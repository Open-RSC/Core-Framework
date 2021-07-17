package com.openrsc.server.plugins.authentic.defaults;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
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
		if (obj.getID() == 125 && obj.getX() == 222 && obj.getY() == 743) {
			return true;
		} else if (obj.getID() == 143 && obj.getX() == 224 && obj.getY() == 737) {
			return true;
		} else if (obj.getID() == 130 && obj.getX() == 220 && obj.getY() == 727) {
			return true;
		} else if (obj.getID() == 129 && obj.getX() == 212 && obj.getY() == 729) {
			return true;
		} else if (obj.getID() == 134 && obj.getX() == 206 && obj.getY() == 730) {
			return true;
		} else if (obj.getID() == 131 && obj.getX() == 201 && obj.getY() == 734) {
			return true;
		} else if (obj.getID() == 132 && obj.getX() == 198 && obj.getY() == 746) {
			return true;
		} else if (obj.getID() == 133 && obj.getX() == 204 && obj.getY() == 752) {
			return true;
		} else if (obj.getID() == 136 && obj.getX() == 209 && obj.getY() == 754) {
			return true;
		} else if (obj.getID() == 139 && obj.getX() == 217 && obj.getY() == 760) {
			return true;
		} else if (obj.getID() == 140 && obj.getX() == 222 && obj.getY() == 760) {
			return true;
		} else if (obj.getID() == 213 && obj.getX() == 226 && obj.getY() == 760) {
			return true;
		} else if (obj.getID() == 142 && obj.getX() == 230 && obj.getY() == 759) {
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
		if (obj.getID() == 125 && obj.getX() == 222 && obj.getY() == 743) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 10) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to a guide before going through this door");
			}
		} else if (obj.getID() == 143 && obj.getX() == 224 && obj.getY() == 737) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 15) {
				doDoor(obj, player);
			} else {
				player.message("Speak to the controls guide before going through this door");
			}
		} else if (obj.getID() == 130 && obj.getX() == 220 && obj.getY() == 727) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 25) {
				doDoor(obj, player);
			} else {
				player.message("Speak to the combat instructor before going through this door");
			}
		} else if (obj.getID() == 129 && obj.getX() == 212 && obj.getY() == 729) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 35) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to a cooking instructor before going through this door");
			}
		} else if (obj.getID() == 134 && obj.getX() == 206 && obj.getY() == 730) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 40) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to a finance advisor before going through this door");
			}
		} else if (obj.getID() == 131 && obj.getX() == 201 && obj.getY() == 734) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 45) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to the fishing instructor before going through this door");
			}
		} else if (obj.getID() == 132 && obj.getX() == 198 && obj.getY() == 746) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 55) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to the mining instructor before going through this door");
			}
		} else if (obj.getID() == 133 && obj.getX() == 204 && obj.getY() == 752) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 60) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to a bank assistant before going through this door");
			}
		} else if (obj.getID() == 136 && obj.getX() == 209 && obj.getY() == 754) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 65) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to the quest advisor before going through this door");
			}
		} else if (obj.getID() == 139 && obj.getX() == 217 && obj.getY() == 760) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 70) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to the wilderness guide before going through this door");
			}
		} else if (obj.getID() == 140 && obj.getX() == 222 && obj.getY() == 760) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 80) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to a magic instructor before going through this door");
			}
		} else if (obj.getID() == 213 && obj.getX() == 226 && obj.getY() == 760) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 90) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to a fatigue expert before going through this door");
			}
		} else if (obj.getID() == 142 && obj.getX() == 230 && obj.getY() == 759) {
			if (player.getCache().hasKey("tutorial")
				&& player.getCache().getInt("tutorial") >= 100) {
				doDoor(obj, player);
			} else {
				player.message("You should speak to the community instructor before going through this door");
			}
		}


		switch (obj.getID()) {

			case 54: // Dragon Slayer: Door out of maze near entrance
				// + escape doors of basement
				// all are openable from east (inside room) and locked on west side (outside room)
				if (player.getX() == obj.getX() - 1) {
					doDoor(obj, player);
				} else {
					player.message("this door is locked");
				}
				break;

			case 154: // Grand Tree: main door (outside)
				mes("you open the door");
				delay(3);
				player.teleport(703, 455);
				player.message("and walk through");
				break;

			case 153: // Grand Tree: main door (inside)
				mes("you open the door");
				delay(3);
				player.teleport(416, 165);
				player.message("and walk through");
				break;

			case 161: // Karamja: shipyard gate (401, 762)
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

			case 74: // Hero's Guild: main door
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

			case 20:
				replaceGameObject(obj, player, 1, true);
				break;

			case 1:
				replaceGameObject(obj, player, 2, false);
				break;

			case 2:
				replaceGameObject(obj, player, 1, true);
				break;

			case 176: // Alkharid: Shantay Pass jail door
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
											player.message("As an Ultimate Iron Man, you cannot use the bank.");
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
												player.message("As an Ultimate Iron Man, you cannot use the bank.");
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
			case 109: //
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

			case 108: // Temple of Ikov: Jail door near Fire Warrior (546, 3302)
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

			case 107: // Temple of Ikov: Door deeper into tunnel (545, 3307)
				if (player.getCache().hasKey("completeLever") || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -1 || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -2) {
					player.message("You go through the door");
					doDoor(obj, player);
				} else {
					player.message("The door won't open");
				}
				break;

			case 106: // Temple of Ikov: Door to Ice Spiders (536, 3349)
				if (player.getCache().hasKey("openSpiderDoor") || player.getX() >= 536 || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -1 || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -2) {
					player.message("You go through the door");
					doDoor(obj, player);
				} else {
					player.message("The door won't open");
				}
				break;

			case 104: // Temple of Ikov: First door (533, 3342)
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

			case 105: // Temple of Ikov: Bridge door (546, 3328)
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

			case 9:
				replaceGameObject(obj, player, 8, false);
				break;

			case 8:
				replaceGameObject(obj, player, 9, true);
				break;

			case 94:
			case 23:
				player.message("The door is locked");
				break;

			case 113: // Fight Arena (621, 699), (603, 717)
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

			case 114: // Fight Arena (615, 715), (619, 711)
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

			case 120: // Plague city / Biohazard - unsure the purpose of this door
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

			case 122: // Plague City
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

			case 123: // Plague City
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

			case 121: // Plague City
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

			case 115:
				doDoor(obj, player);
				break;

			/** Guild Doors */
			case 112: // Fishing Guild Door
				if (obj.getX() != 586 || obj.getY() != 524) {
					break;
				}
				if (player.getY() > 523) {
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
				} else {
					doDoor(obj, player);
				}
				break;

			case 55: // Mining Guild Door
				if (obj.getX() != 268 || obj.getY() != 3381) {
					break;
				}
				if (getCurrentLevel(player, Skill.MINING.id()) < 60) {
					Npc dwarf = player.getWorld().getNpc(NpcId.DWARF_MINING_GUILD.id(), 265, 270, 3379, 3380);
					if (dwarf != null) {
						npcsay(player, dwarf, "Sorry only the top miners are allowed in there");
					}
					delay();
					player.message("You need a mining of level 60 to enter");
				} else {
					doDoor(obj, player);
				}
				break;

			case 68: // Crafting Guild Door
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

			case 43: // Cooking Guild Door
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
				} else if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.CHEFS_HAT.id())) {
					Npc chef = player.getWorld().getNpc(NpcId.HEAD_CHEF.id(), 176, 181, 480, 487);
					if (chef != null) {
						npcsay(player, chef, "Where's your chef's hat",
							"You can't come in here unless you're wearing a chef's hat");
					}
				} else {
					doDoor(obj, player);
				}
				break;

			case 146: // Magic Guild Door
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

			case 22: // Odd looking wall (545, 3283) & (219, 3282)
				player.playSound("secretdoor");
				doDoor(obj, player, -1);
				player.message("You just went through a secret door");
				break;

			case 38: // Black Knight Guard Door
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

			case 36: // Draynor mansion front door
				if (obj.getX() != 210 || obj.getY() != 553) {
					return;
				}
				if (player.getY() >= 553) {
					doDoor(obj, player);
				} else {
					player.message("The door won't open");
				}
				break;

			case 37: // Draynor mansion back door
				if (obj.getX() != 199 || obj.getY() != 551) {
					return;
				}
				doDoor(obj, player);
				player.message("You go through the door");
				break;

			case 30: // Locked Doors
				player.message("The door is locked");
				break;

			case 44: // champs guild door
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

			case 67: // Lost City Market Door (117, 3539), (116, 3537) NPC: 221
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

			case 150: // Fight Arena: Ogre Cage (?)
				doDoor(obj, player);
				break;

			case 138: // Biohazard
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

			case 141: // Biohazard
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

			case 145: // Biohazard
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
					if (!config().MEMBER_WORLD) {
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
				delay(2);
				player.message("and walk through");
				return;

			case 356: // McGrouber's Wood / Woodcutting Guild Gate (560, 472)
				if (obj.getX() != 560 || obj.getY() != 472) {
					return;
				}
				if (player.getY() <= 472) {
					if (config().WANT_WOODCUTTING_GUILD) {
						doGate(player, obj);
					} else { // deny exit if not woodcut guild
						player.playerServerMessage(MessageType.QUEST, "the gate is locked");
					}
				} else {
					if (config().WANT_WOODCUTTING_GUILD) {
						if (getCurrentLevel(player, Skill.WOODCUTTING.id()) < 70) {
							final Npc forester = player.getWorld().getNpc(NpcId.FORESTER.id(), 562, 565,
								468, 472);
							if (forester != null) {
								npcsay(player, forester, "Hello only the top woodcutters are allowed in here");
							}
							delay(2);
							player.message("You need a woodcutting level of 70 to enter");
						} else {
							doGate(player, obj);
						}
					} else { // Deny Entry
						final Npc forester = player.getWorld().getNpc(NpcId.FORESTER.id(), 562, 565,
							468, 472);
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
				}
				return;

			case 712: // Shilo inside gate
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

			case 611: // Shilo outside gate
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
					changeloc(obj, 3000, 612);
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

			case 513: // eastern Varrock gate for family crest or biohazard or just wanna go in :)
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
					doGate(player, obj, 514, new Point(92, 522));
				} else {
					player.message("you open the gate and pass through");
					doGate(player, obj, 514);
				}
				return;

			case 93: // Red dragon gate (140, 180)
				members = true;
				break;

			case 137: // Members Gate near Doric (341, 487)
				members = true;
				break;

			case 346: // Members Gate of Frozen Waste Plateau (331,142)
				members = true;
				break;

			case 347: // Members Gate NW of Greaters (111,142)
				members = true;
				break;

			case 138: // Members Gate near Crafting Guild (343, 581)
				members = true;
				break;

			case 254: // Members Gate near Brimhaven (434, 682)
				members = true;
				break;

			case 563: // King Lathas Training Area (660, 551)
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

			case 626: // Gnome Stronghold Gate (703, 531)
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

			case 260: // Bank Vault Gate
				members = false;
				player.playerServerMessage(MessageType.QUEST, "the gate is locked");
				return;

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

	private void replaceGameObject(final int newID, final boolean open,
								   final Player player, final GameObject object) {
		player.getWorld().replaceGameObject(object,
			new GameObject(object.getWorld(), object.getLocation(), newID, object
				.getDirection(), object.getType()));
		player.playSound(open ? "opendoor" : "closedoor");
	}

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
