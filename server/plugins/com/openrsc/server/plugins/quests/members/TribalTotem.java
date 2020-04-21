package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
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
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("Well done you have completed the tribal totem quest");
		incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.TRIBAL_TOTEM), true);
		p.message("@gre@You haved gained 1 quest point!");
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.KANGAI_MAU.id(), NpcId.HORACIO.id(),
				NpcId.WIZARD_CROMPERTY.id(), NpcId.RPDT_EMPLOYEE.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.KANGAI_MAU.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					npcsay(p, n, "Hello I Kangai Mau", "Of the Rantuki tribe");
					int opt = multi(p, n,
						"And what are you doing in Brimhaven?",
						"I'm in search of adventure",
						"Who are the Rantuki tribe?");
					if (opt == 0) {
						npcsay(p, n, "I looking for someone brave",
							"To go on important mission for me",
							"Someone skilled in thievery and sneaking about",
							"I am told I can find such people in Brimhaven");
						int sub_opt = multi(p, n, "Tell me of this mission",
							"Yep I have heard there are many of that type here");
						if (sub_opt == 0) {
							say(p, n, "I may be able to help");
							npcsay(p,
								n,
								"I need someone to go on a mission",
								"To the city of Ardougne",
								"There you will need to find the house of Lord Handelmort",
								"In his house he has our tribal totem",
								"We need it back");
							int sub_menu = multi(p, n, false, //do not send over
								"Ok I will get it back",
								"Why does he have it?",
								"How can I find Handelmort's house?");
							if (sub_menu == 0) {
								say(p, n, "Ok I will get it back");
								p.updateQuestStage(this, 1);
							} else if (sub_menu == 1) {
								say(p, n, "Why does he have it?");
								npcsay(p,
									n,
									"Lord Handelmort is an Ardougnese explorer",
									"Which mean he think he allowed to come and steal our stuff",
									"To put in his private museum");
								int sub_opt1 = multi(p, n, false, //do not send over
									"Ok I will get it back",
									"How can I find Handlemort's house?");
								if (sub_opt1 == 0) {
									say(p, n, "Ok I will get it back");
									p.updateQuestStage(this, 1);
								} else if (sub_opt1 == 1) {
									say(p, n, "How can I find Handelmort's house",
										"Ardougne is a big place");
									npcsay(p, n, "I don't know Ardougne");
								}

							} else if (sub_menu == 2) {
								say(p, n, "How can I find Handelmort's house",
									"Ardougne is a big place");
								npcsay(p, n, "I don't know Ardougne");
							}

						}
					} else if (opt == 1) {
						npcsay(p,
							n,
							"Adventure is something I may be able to give",
							"I need someone to go on a mission",
							"To the city of Ardougne",
							"There you will need to find the house of Lord Handelmort",
							"In his house he has our tribal totem",
							"We need it back");
						int sub_opt = multi(p, n, false, //do not send over
							"Ok I will get it back",
							"Why does he have it?",
							"How can I find Handelmort's house?");
						if (sub_opt == 0) {
							say(p, n, "Ok I will get it back");
							p.updateQuestStage(this, 1);
						} else if (sub_opt == 1) {
							say(p, n, "Why does he have it?");
							npcsay(p,
								n,
								"Lord Handelmort is an Ardougnese explorer",
								"Which mean he think he allowed to come and steal our stuff",
								"To put in his private museum");
							int sub_opt1 = multi(p, n, false, //do not send over
								"Ok I will get it back",
								"How can I find Handelmort's house?");
							if (sub_opt1 == 0) {
								say(p, n, "Ok I will get it back");
								p.updateQuestStage(this, 1);
							} else if (sub_opt1 == 1) {
								say(p, n, "How can I find Handelmort's house",
									"Ardougne is a big place");
								npcsay(p, n, "I don't know Ardougne");
							}

						} else if (sub_opt == 2) {
							say(p, n, "How can I find Handelmort's house",
								"Ardougne is a big place");
							npcsay(p, n, "I don't know Ardougne");
						}
					} else if (opt == 2) {
						npcsay(p, n, "A proud and noble tribe of Karamja",
							"Now we are few", "Men come from across sea",
							"And settle on our hunting grounds");
					}
					break;
				case 1:
				case 2:
					npcsay(p, n, "Have you got our totem back?");
					if (p.getCarriedItems().getInventory().countId(ItemId.TRIBAL_TOTEM.id()) >= 1) {
						say(p, n, "Yes I have");
						npcsay(p, n, "Thank you brave adventurer");
						p.sendQuestComplete(Quests.TRIBAL_TOTEM);
						npcsay(p, n, "Here have some freshly cooked Karamja fish",
							"Caught specially by our people");
						p.getCarriedItems().remove(new Item(ItemId.TRIBAL_TOTEM.id()));
						give(p, ItemId.SWORDFISH.id(), 5);
					} else {
						say(p, n, "No it's not that easy");
						npcsay(p, n, "Bah, you no good");
					}
					break;
				case -1:
					npcsay(p, n, "greetings esteemed thief");
					break;
			}
		}
		else if (n.getID() == NpcId.HORACIO.id()) {
			npcsay(p, n, "It's a fine day to be out in the garden isn't it?");
			int menu = multi(p, n, false, //do not send over
				"Yes, it's very nice", "So who are you?");
			if (menu == 0) {
				say(p, n, "Yes, it's very nice");
			} else if (menu == 1) {
				say(p, n, "So who are you");
				npcsay(p, n, "My name is Horacio Dobson",
					"I am the gardener to Lord Handelmort",
					"All this around you is my handywork");
				if (p.getQuestStage(this) != 0) {
					int sub_menu = multi(p, n,
						"So do you garden round the back too?",
						"Do you need any help?");
					if (sub_menu == 0) {
						npcsay(p, n, "That I do");
						say(p, n, "Doesn't all this security in this house",
							"get in your way?");
						npcsay(p,
							n,
							"Ah, I'm used to all that",
							"I have my keys, the dogs knows me",
							"And I know by heart the combination to the door lock",
							"It's rather easy, it's his middle name");
						say(p, n, "Who's middle name?");
						npcsay(p, n, "Hmm I shouldn't have said that",
							"Forget I said it");
					} else if (sub_menu == 1) {
						npcsay(p, n, "Trying to muscle in on my job ehh?",
							"I'm happy to do this all myself");
					}
				}
			}
		}
		else if (n.getID() == NpcId.WIZARD_CROMPERTY.id()) {
			npcsay(p, n, "Hello there", "My name is Cromperty",
				"I am a wizard and an inventor");
			int menu = multi(p, n, "Two jobs, thats got to be tough",
				"So what have you invented?");
			if (menu == 0) {
				npcsay(p, n, "Not when you combine them it isn't",
					"I invent magic things");
				int sub_menu = multi(p, n, "So what have you invented?",
					"Well I shall leave you to your inventing");
				if (sub_menu == 0) {
					inventedDialogue(p, n);
				}
			} else if (menu == 1) {
				inventedDialogue(p, n);
			}
		}
		else if (n.getID() == NpcId.RPDT_EMPLOYEE.id()) {
			npcsay(p, n, "Welcome to RPDT");
			if (p.getCache().hasKey("label") && p.getQuestStage(this) == 1) {
				int menu = multi(p, n,
					"So when are you going to deliver this crate?",
					"Thank you, it's interesting in here");
				if (menu == 0) {
					npcsay(p, n, "I suppose I could do it now");
					n.teleport(558, 616);
					GameObject obj = p.getViewArea().getGameObject(
						Point.location(558, 617));
					p.getWorld().unregisterGameObject(obj);
					p.getWorld().delayedSpawnObject(obj.getLoc(), 30000);
					mes(p, "The employee picks up the crate");
					n.teleport(559, 612);
					mes(p, "And takes it out to be delivered");
					p.getCache().remove("label");
					p.updateQuestStage(this, 2);
				}
			} else {
				say(p, n, "Thank you very much");
			}
		}
	}

	private void inventedDialogue(Player p, Npc n) {
		npcsay(p, n,
			"My latest inevention is my patent pending teleport block",
			"Stand on this block here",
			"I do a bit of the old hocus pocus",
			"And abracadabra you end up on the other teleport block");
		int sub_menu1 = multi(p, n, "So where is the other block?",
			"Can I be teleported please?", "Well done, that's very clever");
		if (sub_menu1 == 0) {
			npcsay(p,
				n,
				"I would guess somewhere between here and the wizards tower in Misthalin",
				"All I know is it hasn't got there yet",
				"Or the wizards there would have contacted me",
				"I am using the RPDT to deliver it");
			int menu3 = multi(p, n, "Can I be teleported please?",
				"Who are the RPDT?");
			if (menu3 == 0) {
				npcsay(p, n, "By all means",
					"Though I don't know where you will come out",
					"Wherever the other teleport block is I suppose");
				int tp = multi(p, n, false, //do not send over
					"Yes, that sounds good teleport me",
					"That sounds dangerous leave me here");
				if (tp == 0) {
					say(p, n, "Yes, that sounds good",
						"teleport me");
					p.teleport(545, 577, false);
					mes(p, "Cromperty takes out a small box",
						"Cromperty presses a switch on the box");
					if (p.getQuestStage(this) == 2 || p.getQuestStage(this) == -1) {
						p.teleport(560, 588, true);
					} else {
						p.teleport(558, 617, true);
					}
				} else if (tp == 1) {
					say(p, n, "That sounds dangerous leave me here");
				}
			} else if (menu3 == 1) {
				npcsay(p, n, "The runescape parcel delivery team");
			}
		} else if (sub_menu1 == 1) {
			npcsay(p, n, "By all means",
				"Though I don't know where you will come out",
				"Wherever the other teleport block is I suppose");
			int tp = multi(p, n, false, //do not send over
				"Yes, that sounds good teleport me",
				"That sounds dangerous leave me here");
			if (tp == 0) {
				say(p, n, "Yes, that sounds good",
					"teleport me");
				p.teleport(545, 577, false);
				mes(p, "Cromperty takes out a small box",
					"Cromperty presses a switch on the box");
				if (p.getQuestStage(this) == 2 || p.getQuestStage(this) == -1) {
					p.teleport(560, 588, true);
				} else {
					p.teleport(558, 617, true);
				}
			} else if (tp == 1) {
				say(p, n, "That sounds dangerous leave me here");
			}
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command,
							  Player player) {
		return (obj.getID() == 290 && obj.getX() == 557 && obj.getY() == 615)
				|| (obj.getID() == 290 && obj.getX() == 557 && obj.getY() == 614)
				|| (obj.getID() == 329 && obj.getX() == 559 && obj.getY() == 617)
				|| (obj.getID() == 328 && obj.getX() == 558 && obj.getY() == 617)
				|| (obj.getID() == 331 && obj.getX() == 563 && obj.getY() == 587)
				|| ((obj.getID() == 332 || obj.getID() == 333) && obj.getX() == 560 && obj.getY() == 1531);
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == 290 && obj.getX() == 557 && obj.getY() == 615
			|| obj.getID() == 290 && obj.getX() == 557 && obj.getY() == 614) {
			p.message("The crate is empty");
		}
		else if (obj.getID() == 329 && obj.getX() == 559 && obj.getY() == 617) {
			mes(p, "There is a label on this crate", "It says",
				"to Lord Handelmort", "Handelmort Mansion", "Ardougne");
			if (p.getCarriedItems().hasCatalogID(ItemId.ADDRESS_LABEL.id(), Optional.empty()) || p.getCache().hasKey("label")) {
				mes(p, "It doesn't seem possible to open the crate");
			} else {
				mes(p, "You take the label");
				give(p, ItemId.ADDRESS_LABEL.id(), 1);
			}
		}
		else if (obj.getID() == 328 && obj.getX() == 558 && obj.getY() == 617) {
			if (p.getCache().hasKey("label")) {
				mes(p, "There is a label on this crate", "It says",
					"to Lord Handelmort", "Handelmort Mansion", "Ardougne");
				return;
			}
			mes(p, "Its ready to be delivered",
				"To the wizard's tower in Misthalin",
				"It doesn't seem possible to open the crate");
		}
		else if (obj.getID() == 331 && obj.getX() == 563 && obj.getY() == 587) {
			if (command.equalsIgnoreCase("Search for traps")) {
				if (getCurrentLevel(p, Skills.THIEVING) < 21) {
					mes(p, "You don't find anything interesting");
				} else {
					mes(p, "You find a trap in the stairs",
						"You make a note of the trap's location",
						"Ready for next time you go up the stairs");
					p.getCache().store("trapy", true);
				}
			} else if (command.equalsIgnoreCase("Go up")) {
				if (p.getCache().hasKey("trapy")) {
					p.message("You go up the stairs");
					p.getCache().remove("trapy");
					p.teleport(563, 1534, false);
				} else {
					mes(p, "You here a click beneath you",
						"You feel yourself falling",
						"You have fallen through a trap");
					p.teleport(563, 3418, false);
					p.damage(7);
				}
			}
		}
		else if ((obj.getID() == HANDELMORT_CHEST_OPEN || obj.getID() == HANDELMORT_CHEST_CLOSED) && obj.getX() == 560 && obj.getY() == 1531) {
			if (command.equalsIgnoreCase("open")) {
				openGenericObject(obj, p, HANDELMORT_CHEST_OPEN, "You open the chest");
			} else if (command.equalsIgnoreCase("close")) {
				closeGenericObject(obj, p, HANDELMORT_CHEST_CLOSED, "You close the chest");
			} else {
				p.message("You search the chest");
				if (p.getCarriedItems().hasCatalogID(ItemId.TRIBAL_TOTEM.id(), Optional.empty())) {
					p.message("The chest is empty");
				} else {
					p.message("You find a tribal totem which you take");
					give(p, ItemId.TRIBAL_TOTEM.id(), 1);
				}
			}
		}
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item,
							   Player player) {
		return obj.getID() == 328 && item.getCatalogId() == ItemId.ADDRESS_LABEL.id();
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == 328 && item.getCatalogId() == ItemId.ADDRESS_LABEL.id()) {
			if (p.getQuestStage(this) == -1) {
				p.message("You've already done this!");
			} else {
				p.message("You stick the label on the crate");
				say(p, null, "Now I just need someone to deliver it for me");
				p.getCarriedItems().remove(new Item(ItemId.ADDRESS_LABEL.id()));
				p.getCache().store("label", true);
			}
		}

	}

	@Override
	public boolean blockOpBound(GameObject obj, Integer click,
								Player player) {
		return obj.getID() == 98 && obj.getX() == 561 && obj.getY() == 586;
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 98 && obj.getX() == 561 && obj.getY() == 586
			&& click == 0) {
			p.message("choose a position for dial 1");
			int dial = multi(p, "postition A", "position B",
				"Position C", "Position D");
			if (dial >= 0 || dial <= 3) {
				if (dial == 1) {
					firstOpt = true;
				}
				p.message("choose a position for dial 2");
				int dial2 = multi(p, "position R", "position S",
					"position T", "Position U");
				if (dial2 >= 0 || dial2 <= 3) {
					if (dial2 == 0) {
						secondOpt = true;
					}
					p.message("choose a position for dial 3");
					int dial3 = multi(p, "postition A", "position B",
						"Position C", "Position D");
					if (dial3 >= 0 || dial3 <= 3) {
						if (dial3 == 0) {
							thirdOpt = true;
						}
						p.message("choose a position for dial 4");
						int dial4 = multi(p, "postition A",
							"position B", "Position C", "Position D");
						if (dial4 >= 0 || dial4 <= 3) {
							if (firstOpt && secondOpt && thirdOpt && dial4 == 3) {
								p.message("You here a satisfying click");
								p.message("You go through the door");
								doDoor(obj, p);
							} else {
								p.message("The door fails to open");
							}
						}
					}
				}
			}
		}
	}
}
