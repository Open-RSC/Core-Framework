package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class TribalTotem implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	UseLocTrigger,
	OpBoundTrigger {

	boolean firstOpt = false;
	boolean secondOpt = false;
	boolean thirdOpt = false;
	private static final int HANDELMORT_CHEST_OPEN = 332;
	private static final int HANDELMORT_CHEST_CLOSED = 333;

	@Override
	public int getQuestId() {
		return Quests.TRIBAL_TOTEM;
	}

	@Override
	public String getQuestName() {
		return "Tribal totem (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.TRIBAL_TOTEM.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the tribal totem quest");
		final QuestReward reward = Quest.TRIBAL_TOTEM.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.KANGAI_MAU.id(), NpcId.HORACIO.id(),
				NpcId.WIZARD_CROMPERTY.id(), NpcId.RPDT_EMPLOYEE.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.KANGAI_MAU.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay(player, n, "Hello I Kangai Mau", "Of the Rantuki tribe");
					int opt = multi(player, n,
						"And what are you doing in Brimhaven?",
						"I'm in search of adventure",
						"Who are the Rantuki tribe?");
					if (opt == 0) {
						npcsay(player, n, "I looking for someone brave",
							"To go on important mission for me",
							"Someone skilled in thievery and sneaking about",
							"I am told I can find such people in Brimhaven");
						int sub_opt = multi(player, n, "Tell me of this mission",
							"Yep I have heard there are many of that type here");
						if (sub_opt == 0) {
							say(player, n, "I may be able to help");
							npcsay(player,
								n,
								"I need someone to go on a mission",
								"To the city of Ardougne",
								"There you will need to find the house of Lord Handelmort",
								"In his house he has our tribal totem",
								"We need it back");
							int sub_menu = multi(player, n, false, //do not send over
								"Ok I will get it back",
								"Why does he have it?",
								"How can I find Handelmort's house?");
							if (sub_menu == 0) {
								say(player, n, "Ok I will get it back");
								player.updateQuestStage(this, 1);
							} else if (sub_menu == 1) {
								say(player, n, "Why does he have it?");
								npcsay(player,
									n,
									"Lord Handelmort is an Ardougnese explorer",
									"Which mean he think he allowed to come and steal our stuff",
									"To put in his private museum");
								int sub_opt1 = multi(player, n, false, //do not send over
									"Ok I will get it back",
									"How can I find Handlemort's house?");
								if (sub_opt1 == 0) {
									say(player, n, "Ok I will get it back");
									player.updateQuestStage(this, 1);
								} else if (sub_opt1 == 1) {
									say(player, n, "How can I find Handelmort's house",
										"Ardougne is a big place");
									npcsay(player, n, "I don't know Ardougne");
								}

							} else if (sub_menu == 2) {
								say(player, n, "How can I find Handelmort's house",
									"Ardougne is a big place");
								npcsay(player, n, "I don't know Ardougne");
							}

						}
					} else if (opt == 1) {
						npcsay(player,
							n,
							"Adventure is something I may be able to give",
							"I need someone to go on a mission",
							"To the city of Ardougne",
							"There you will need to find the house of Lord Handelmort",
							"In his house he has our tribal totem",
							"We need it back");
						int sub_opt = multi(player, n, false, //do not send over
							"Ok I will get it back",
							"Why does he have it?",
							"How can I find Handelmort's house?");
						if (sub_opt == 0) {
							say(player, n, "Ok I will get it back");
							player.updateQuestStage(this, 1);
						} else if (sub_opt == 1) {
							say(player, n, "Why does he have it?");
							npcsay(player,
								n,
								"Lord Handelmort is an Ardougnese explorer",
								"Which mean he think he allowed to come and steal our stuff",
								"To put in his private museum");
							int sub_opt1 = multi(player, n, false, //do not send over
								"Ok I will get it back",
								"How can I find Handelmort's house?");
							if (sub_opt1 == 0) {
								say(player, n, "Ok I will get it back");
								player.updateQuestStage(this, 1);
							} else if (sub_opt1 == 1) {
								say(player, n, "How can I find Handelmort's house",
									"Ardougne is a big place");
								npcsay(player, n, "I don't know Ardougne");
							}

						} else if (sub_opt == 2) {
							say(player, n, "How can I find Handelmort's house",
								"Ardougne is a big place");
							npcsay(player, n, "I don't know Ardougne");
						}
					} else if (opt == 2) {
						npcsay(player, n, "A proud and noble tribe of Karamja",
							"Now we are few", "Men come from across sea",
							"And settle on our hunting grounds");
					}
					break;
				case 1:
				case 2:
					npcsay(player, n, "Have you got our totem back?");
					if (player.getCarriedItems().getInventory().countId(ItemId.TRIBAL_TOTEM.id()) >= 1) {
						say(player, n, "Yes I have");
						npcsay(player, n, "Thank you brave adventurer");
						player.sendQuestComplete(Quests.TRIBAL_TOTEM);
						npcsay(player, n, "Here have some freshly cooked Karamja fish",
							"Caught specially by our people");
						player.getCarriedItems().remove(new Item(ItemId.TRIBAL_TOTEM.id()));
						give(player, ItemId.SWORDFISH.id(), 5);
					} else {
						say(player, n, "No it's not that easy");
						npcsay(player, n, "Bah, you no good");
					}
					break;
				case -1:
					npcsay(player, n, "greetings esteemed thief");
					break;
			}
		}
		else if (n.getID() == NpcId.HORACIO.id()) {
			npcsay(player, n, "It's a fine day to be out in the garden isn't it?");
			int menu = multi(player, n, false, //do not send over
				"Yes, it's very nice", "So who are you?");
			if (menu == 0) {
				say(player, n, "Yes, it's very nice");
			} else if (menu == 1) {
				say(player, n, "So who are you");
				npcsay(player, n, "My name is Horacio Dobson",
					"I am the gardener to Lord Handelmort",
					"All this around you is my handywork");
				if (player.getQuestStage(this) != 0) {
					int sub_menu = multi(player, n,
						"So do you garden round the back too?",
						"Do you need any help?");
					if (sub_menu == 0) {
						npcsay(player, n, "That I do");
						say(player, n, "Doesn't all this security in this house",
							"get in your way?");
						npcsay(player,
							n,
							"Ah, I'm used to all that",
							"I have my keys, the dogs knows me",
							"And I know by heart the combination to the door lock",
							"It's rather easy, it's his middle name");
						say(player, n, "Who's middle name?");
						npcsay(player, n, "Hmm I shouldn't have said that",
							"Forget I said it");
					} else if (sub_menu == 1) {
						npcsay(player, n, "Trying to muscle in on my job ehh?",
							"I'm happy to do this all myself");
					}
				}
			}
		}
		else if (n.getID() == NpcId.WIZARD_CROMPERTY.id()) {
			npcsay(player, n, "Hello there", "My name is Cromperty",
				"I am a wizard and an inventor");
			int menu = multi(player, n, "Two jobs, thats got to be tough",
				"So what have you invented?");
			if (menu == 0) {
				npcsay(player, n, "Not when you combine them it isn't",
					"I invent magic things");
				int sub_menu = multi(player, n, "So what have you invented?",
					"Well I shall leave you to your inventing");
				if (sub_menu == 0) {
					inventedDialogue(player, n);
				}
			} else if (menu == 1) {
				inventedDialogue(player, n);
			}
		}
		else if (n.getID() == NpcId.RPDT_EMPLOYEE.id()) {
			npcsay(player, n, "Welcome to RPDT");
			if (player.getCache().hasKey("label") && player.getQuestStage(this) == 1) {
				int menu = multi(player, n,
					"So when are you going to deliver this crate?",
					"Thank you, it's interesting in here");
				if (menu == 0) {
					npcsay(player, n, "I suppose I could do it now");
					n.teleport(558, 616);
					GameObject obj = player.getViewArea().getGameObject(
						Point.location(558, 617));
					player.getWorld().unregisterGameObject(obj);
					player.getWorld().delayedSpawnObject(obj.getLoc(), 30000);
					mes("The employee picks up the crate");
					delay(3);
					n.teleport(559, 612);
					mes("And takes it out to be delivered");
					delay(3);
					player.getCache().remove("label");
					player.updateQuestStage(this, 2);
				}
			} else {
				say(player, n, "Thank you very much");
			}
		}
	}

	private void inventedDialogue(Player player, Npc n) {
		npcsay(player, n,
			"My latest inevention is my patent pending teleport block",
			"Stand on this block here",
			"I do a bit of the old hocus pocus",
			"And abracadabra you end up on the other teleport block");
		int sub_menu1 = multi(player, n, "So where is the other block?",
			"Can I be teleported please?", "Well done, that's very clever");
		if (sub_menu1 == 0) {
			npcsay(player,
				n,
				"I would guess somewhere between here and the wizards tower in Misthalin",
				"All I know is it hasn't got there yet",
				"Or the wizards there would have contacted me",
				"I am using the RPDT to deliver it");
			int menu3 = multi(player, n, "Can I be teleported please?",
				"Who are the RPDT?");
			if (menu3 == 0) {
				npcsay(player, n, "By all means",
					"Though I don't know where you will come out",
					"Wherever the other teleport block is I suppose");
				int tp = multi(player, n, false, //do not send over
					"Yes, that sounds good teleport me",
					"That sounds dangerous leave me here");
				if (tp == 0) {
					say(player, n, "Yes, that sounds good",
						"teleport me");
					player.teleport(545, 577, false);
					mes("Cromperty takes out a small box");
					delay(3);
					mes("Cromperty presses a switch on the box");
					delay(3);
					if (player.getQuestStage(this) == 2 || player.getQuestStage(this) == -1) {
						player.teleport(560, 588, true);
					} else {
						player.teleport(558, 617, true);
					}
				} else if (tp == 1) {
					say(player, n, "That sounds dangerous leave me here");
				}
			} else if (menu3 == 1) {
				npcsay(player, n, "The runescape parcel delivery team");
			}
		} else if (sub_menu1 == 1) {
			npcsay(player, n, "By all means",
				"Though I don't know where you will come out",
				"Wherever the other teleport block is I suppose");
			int tp = multi(player, n, false, //do not send over
				"Yes, that sounds good teleport me",
				"That sounds dangerous leave me here");
			if (tp == 0) {
				say(player, n, "Yes, that sounds good",
					"teleport me");
				player.teleport(545, 577, false);
				mes("Cromperty takes out a small box");
				delay(3);
				mes("Cromperty presses a switch on the box");
				delay(3);
				if (player.getQuestStage(this) == 2 || player.getQuestStage(this) == -1) {
					player.teleport(560, 588, true);
				} else {
					player.teleport(558, 617, true);
				}
			} else if (tp == 1) {
				say(player, n, "That sounds dangerous leave me here");
			}
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return (obj.getID() == 290 && obj.getX() == 557 && obj.getY() == 615)
				|| (obj.getID() == 290 && obj.getX() == 557 && obj.getY() == 614)
				|| (obj.getID() == 329 && obj.getX() == 559 && obj.getY() == 617)
				|| (obj.getID() == 328 && obj.getX() == 558 && obj.getY() == 617)
				|| (obj.getID() == 331 && obj.getX() == 563 && obj.getY() == 587)
				|| ((obj.getID() == 332 || obj.getID() == 333) && obj.getX() == 560 && obj.getY() == 1531);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 290 && obj.getX() == 557 && obj.getY() == 615
			|| obj.getID() == 290 && obj.getX() == 557 && obj.getY() == 614) {
			player.message("The crate is empty");
		}
		else if (obj.getID() == 329 && obj.getX() == 559 && obj.getY() == 617) {
			mes("There is a label on this crate");
			delay(3);
			mes("It says");
			delay(3);
			mes("to Lord Handelmort");
			delay(3);
			mes("Handelmort Mansion");
			delay(3);
			mes("Ardougne");
			delay(3);
			if (player.getCarriedItems().hasCatalogID(ItemId.ADDRESS_LABEL.id(), Optional.empty()) || player.getCache().hasKey("label")) {
				mes("It doesn't seem possible to open the crate");
				delay(3);
			} else {
				mes("You take the label");
				delay(3);
				give(player, ItemId.ADDRESS_LABEL.id(), 1);
			}
		}
		else if (obj.getID() == 328 && obj.getX() == 558 && obj.getY() == 617) {
			if (player.getCache().hasKey("label")) {
				mes("There is a label on this crate");
				delay(3);
				mes("It says");
				delay(3);
				mes("to Lord Handelmort");
				delay(3);
				mes("Handelmort Mansion");
				delay(3);
				mes("Ardougne");
				delay(3);
				return;
			}
			mes("Its ready to be delivered");
			delay(3);
			mes("To the wizard's tower in Misthalin");
			delay(3);
			mes("It doesn't seem possible to open the crate");
			delay(3);
		}
		else if (obj.getID() == 331 && obj.getX() == 563 && obj.getY() == 587) {
			if (command.equalsIgnoreCase("Search for traps")) {
				if (getCurrentLevel(player, Skill.THIEVING.id()) < 21) {
					mes("You don't find anything interesting");
					delay(3);
				} else {
					mes("You find a trap in the stairs");
					delay(3);
					mes("You make a note of the trap's location");
					delay(3);
					mes("Ready for next time you go up the stairs");
					delay(3);
					player.getCache().store("trapy", true);
				}
			} else if (command.equalsIgnoreCase("Go up")) {
				if (player.getCache().hasKey("trapy")) {
					player.message("You go up the stairs");
					player.getCache().remove("trapy");
					player.teleport(563, 1534, false);
				} else {
					mes("You here a click beneath you");
					delay(3);
					mes("You feel yourself falling");
					delay(3);
					mes("You have fallen through a trap");
					delay(3);
					player.teleport(563, 3418, false);
					player.damage(7);
				}
			}
		}
		else if ((obj.getID() == HANDELMORT_CHEST_OPEN || obj.getID() == HANDELMORT_CHEST_CLOSED) && obj.getX() == 560 && obj.getY() == 1531) {
			if (command.equalsIgnoreCase("open")) {
				openGenericObject(obj, player, HANDELMORT_CHEST_OPEN, "You open the chest");
			} else if (command.equalsIgnoreCase("close")) {
				closeGenericObject(obj, player, HANDELMORT_CHEST_CLOSED, "You close the chest");
			} else {
				player.message("You search the chest");
				delay(4);
				if (player.getCarriedItems().hasCatalogID(ItemId.TRIBAL_TOTEM.id(), Optional.empty())) {
					player.message("The chest is empty");
					delay(4);
				} else {
					player.message("You find a tribal totem which you take");
					delay(4);
					give(player, ItemId.TRIBAL_TOTEM.id(), 1);
				}
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 328 && item.getCatalogId() == ItemId.ADDRESS_LABEL.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == 328 && item.getCatalogId() == ItemId.ADDRESS_LABEL.id()) {
			if (player.getQuestStage(this) == -1) {
				player.message("You've already done this!");
			} else {
				player.message("You stick the label on the crate");
				say(player, null, "Now I just need someone to deliver it for me");
				player.getCarriedItems().remove(new Item(ItemId.ADDRESS_LABEL.id()));
				player.getCache().store("label", true);
			}
		}

	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == 98 && obj.getX() == 561 && obj.getY() == 586;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 98 && obj.getX() == 561 && obj.getY() == 586
			&& click == 0) {
			player.message("choose a position for dial 1");
			int dial = multi(player, "postition A", "position B",
				"Position C", "Position D");
			if (dial >= 0 || dial <= 3) {
				if (dial == 1) {
					firstOpt = true;
				}
				player.message("choose a position for dial 2");
				int dial2 = multi(player, "position R", "position S",
					"position T", "Position U");
				if (dial2 >= 0 || dial2 <= 3) {
					if (dial2 == 0) {
						secondOpt = true;
					}
					player.message("choose a position for dial 3");
					int dial3 = multi(player, "postition A", "position B",
						"Position C", "Position D");
					if (dial3 >= 0 || dial3 <= 3) {
						if (dial3 == 0) {
							thirdOpt = true;
						}
						player.message("choose a position for dial 4");
						int dial4 = multi(player, "postition A",
							"position B", "Position C", "Position D");
						if (dial4 >= 0 || dial4 <= 3) {
							if (firstOpt && secondOpt && thirdOpt && dial4 == 3) {
								player.message("You here a satisfying click");
								player.message("You go through the door");
								doDoor(obj, player);
							} else {
								player.message("The door fails to open");
							}
						}
					}
				}
			}
		}
	}
}
