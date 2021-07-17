package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class TempleOfIkov implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger, TakeObjTrigger, UseLocTrigger, SpellNpcTrigger, KillNpcTrigger, AttackNpcTrigger, PlayerRangeNpcTrigger {

	/**
	 * Quest Objects
	 **/
	private static int STAIR_DOWN = 370;
	private static int STAIR_UP = 369;
	private static int LEVER = 361;
	private static int LEVER_BRACKET = 367;
	private static int COMPLETE_LEVER = 368;

	@Override
	public int getQuestId() {
		return Quests.TEMPLE_OF_IKOV;
	}

	@Override
	public String getQuestName() {
		return "Temple of Ikov (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.TEMPLE_OF_IKOV.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.getCache().remove("openSpiderDoor");
		player.getCache().remove("completeLever");
		player.getCache().remove("killedLesarkus");
		final QuestReward reward = Quest.TEMPLE_OF_IKOV.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		player.message("Well done you have completed the temple of Ikov quest");
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.LUCIEN.id(), NpcId.WINELDA.id(), NpcId.GUARDIAN_OF_ARMADYL_FEMALE.id(),
				NpcId.GUARDIAN_OF_ARMADYL_MALE.id(), NpcId.LUCIEN_EDGE.id()}, n.getID());
	}

	private void lucienDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.LUCIEN.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(this)) {
					case 0:
						npcsay(player, n, "I come here seeking a hero who can help me");
						int menu = multi(player, n,
							"I am a hero",
							"Yep lots of heroes about here");
						if (menu == 0) {
							npcsay(player, n, "I need someone who can enter the tunnels under the deserted temple of Ikov",
								"Near Hemenster, to the north of here",
								"Kill the fire warrior of Lesarkus",
								"And retrieve the staff of Armardyl");
							int newMenu = multi(player, n, false, //do not send over
								"Why can't you do it yourself?",
								"That sounds like fun",
								"That sounds too dangerous for me",
								"How much will you pay me?");
							if (newMenu == 0) {
								say(player, n, "Why can't you do that yourself?");
								npcsay(player, n, "The guardians of the staff of Armardyl fear me",
									"They know my kind is powerful",
									"So they have set up magical wards against are race");
								int newMenu2 = multi(player, n, false, //do not send over
									"How much will you pay me?",
									"That sounds like fun",
									"Who are your kind?",
									"That sounds too dangerous for me");
								if (newMenu2 == 0) {
									say(player, n, "How much will you pay me");
									lucienDialogue(player, n, Lucien.PAYME);
								} else if (newMenu2 == 1) {
									say(player, n, "That sounds like fun");
									lucienDialogue(player, n, Lucien.SOUNDSFUN);
								} else if (newMenu2 == 2) {
									say(player, n, "Who are your kind?");
									npcsay(player, n, "An ancient and powerful race",
										"Back in the second age we held great influence in this world",
										"There are few of us left now");
								} else if (newMenu2 == 3) {
									say(player, n, "That sounds too dangerous for me");
									npcsay(player, n, "Fortune favours the bold");
								}

							} else if (newMenu == 1) {
								say(player, n, "That sounds like fun");
								lucienDialogue(player, n, Lucien.SOUNDSFUN);
							} else if (newMenu == 2) {
								say(player, n, "That sounds too dangerous for me");
								npcsay(player, n, "Fortune favours the bold");
							} else if (newMenu == 3) {
								say(player, n, "How much will you pay me");
								lucienDialogue(player, n, Lucien.PAYME);
							}
						}
						break;
					case 1:
					case 2:
					case -1:
					case -2:
						npcsay(player, n, "I thought I told you not to meet me here again");
						if (player.getCarriedItems().hasCatalogID(ItemId.PENDANT_OF_LUCIEN.id(), Optional.empty())) {
							say(player, n, "Yes you did, sorry");
						} else {
							int lostAmuletMenu = multi(player, n,
								"I lost that pendant you gave me",
								"Yes you did sorry");
							if (lostAmuletMenu == 0) {
								npcsay(player, n, "Hmm",
									"Imbecile");
								player.message("Lucien gives you another pendant");
								give(player, ItemId.PENDANT_OF_LUCIEN.id(), 1);
							}
						}
						break;
				}
			}
			switch (cID) {
				case Lucien.SOUNDSFUN:
					npcsay(player, n, "Well it's not that easy",
						"The fire warrior can only be killed with a weapon of ice",
						"And there are many other traps and hazards in those tunnels");
					say(player, n, "Well I am brave I shall give it a go");
					npcsay(player, n, "Take this pendant you will need it to get through the chamber of fear");
					give(player, ItemId.PENDANT_OF_LUCIEN.id(), 1);
					npcsay(player, n, "It is not safe for me to linger here much longer",
						"When you have done meet me in the forest north of Varrock",
						"I have a small holding up there");
					player.updateQuestStage(this, 1);
					break;
				case Lucien.PAYME:
					npcsay(player, n, "Ah the mercenary type I see");
					say(player, n, "It's a living");
					npcsay(player, n, "I shall adequately reward you",
						"With both money and power");
					say(player, n, "Sounds rather too vague for me");
					break;
			}
		}
	}

	private void wineldaDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.WINELDA.id()) {
			if (cID == -1) {
				if (ifheld(player, ItemId.LIMPWURT_ROOT.id(), 20)) {
					say(player, n, "I have the 20 limpwurt roots, now transport me please");
					npcsay(player, n, "Oh marverlous",
						"Brace yourself then");
					for (int i = 0; i < 20; i++) {
						player.getCarriedItems().remove(new Item(ItemId.LIMPWURT_ROOT.id()));
					}
					player.teleport(557, 3290);
					delay();
					ActionSender.sendTeleBubble(player, player.getX(), player.getY(), false);
				} else {
					npcsay(player, n, "Hehe in a bit of a pickle are we?",
						"Want to be getting over the nasty lava stream do we?");
					int menu = multi(player, n,
						"Not really, no",
						"Yes we do",
						"Yes I do");
					if (menu == 0) {
						npcsay(player, n, "Hehe ye'll come back later",
							"They always come back later");
					} else if (menu == 1) {
						wineldaDialogue(player, n, Winelda.YES);
					} else if (menu == 2) {
						wineldaDialogue(player, n, Winelda.YES);
					}
				}
			}
			switch (cID) {
				case Winelda.YES:
					npcsay(player, n, "Well keep it under your helmet",
						"But I'm knowing some useful magic tricks",
						"I could get you over there easy as that");
					say(player, n, "Okay get me over there");
					npcsay(player, n, "Okay brace yourself",
						"Actually no no",
						"Why should I do it for free",
						"Bring me a bite to eat and I'll be a touch more helpful",
						"How about some nice tasty limpwurt roots to chew on",
						"Yes yes that's good, bring me 20 limpwurt roots and over you go");
					break;
			}
		}
	}

	private void guardianDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.GUARDIAN_OF_ARMADYL_FEMALE.id() || n.getID() == NpcId.GUARDIAN_OF_ARMADYL_MALE.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(this)) {
					case 1:
						if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.PENDANT_OF_LUCIEN.id())
							&& !player.getCarriedItems().hasCatalogID(ItemId.STAFF_OF_ARMADYL.id(), Optional.of(false))) {
							npcsay(player, n, "Ahh tis a foul agent of Lucien",
								"Get ye from our master's house");
							if (n != null) {
								n.startCombat(player);
							}
							return;
						}
						if (player.getCarriedItems().hasCatalogID(ItemId.STAFF_OF_ARMADYL.id(), Optional.of(false))) {
							npcsay(player, n, "Stop",
								"You cannot take the staff of Armadyl");
							n.setChasing(player);
							return;
						}
						npcsay(player, n, "Thou dost venture deep in the tunnels",
							"It has been many a year since someone has passed thus far");
						int menu = multi(player, n,
							"I seek the staff of Armadyl",
							"Out of my way fool",
							"Who are you?");
						if (menu == 0) {
							npcsay(player, n, "We guard that here",
								"As did our fathers",
								"And our father's fathers",
								"Why dost thou seeketh it?");
							int seekMenu = multi(player, n, false, //do not send over
								"A guy named Lucien is paying me",
								"Just give it to me",
								"I am a collector of rare and powerful artifacts");
							if (seekMenu == 0) {
								say(player, n, "A guy named Lucien is paying me");
								guardianDialogue(player, n, Guardian.WORKINGFORLUCIEN);
							} else if (seekMenu == 1) {
								say(player, n, "Just give it to me");
								npcsay(player, n, "The staff is a sacred object",
									"Not to be given away to anyone who asks");
							} else if (seekMenu == 2) {
								say(player, n, "I am a collector of rare and powerful objects");
								npcsay(player, n, "The staff is not yours to collect");
							}

						} else if (menu == 1) {
							npcsay(player, n, "I may be a fool, but I will not step aside");
							int foolMenu = multi(player, n,
								"Why not?",
								"Then I must strike you down",
								"Then I guess I will turn back");
							if (foolMenu == 0) {
								npcsay(player, n, "Only members of our order are allowed further");
							} else if (foolMenu == 1) {
								n.startCombat(player);
							}
						} else if (menu == 2) {
							npcsay(player, n, "I am a guardian of Armadyl",
								"We have kept this place safe and holy",
								"For many generations",
								"Many evil souls would like to get their hands on what lies here",
								"Especially the Mahjarrat");
							int whoMenu = multi(player, n,
								"What is an Armadyl?",
								"Who are the Mahjarrat?",
								"Wow you must be old");
							if (whoMenu == 0) {
								npcsay(player, n, "Armadyl is our God",
									"We are his servants",
									"Who have the honour to stay here",
									"And guard his artifacts",
									"Till he needs them to smite his enemies");
								int bla = multi(player, n, false, //do not send over
									"Ok that's nice to know",
									"Someone told me there were only three gods");
								if (bla == 0) {
									say(player, n, "I am a collector of rare and powerful objects");
									npcsay(player, n, "The staff is not yours to collect");
								} else if (bla == 1) {
									say(player, n, "Someone told me there were only three gods",
										"Saradomin, Zamorak and Guthix");
									npcsay(player, n, "Was that someone a Saradominist?",
										"I hear Saradominism is the principle doctrine",
										"Out in the world currently",
										"They only Acknowledge those three gods",
										"They are wrong",
										"Depending on what you define as a god",
										"We are aware of at least twenty");
								}
							} else if (whoMenu == 1) {
								npcsay(player, n, "Ancient powerful beings",
									"They are very evil",
									"They were said to once dominate this plane of existance",
									"Zamorak was said to once have been of their stock",
									"They are few in number and have less power these days",
									"Some still have presence in this world in their liche forms",
									"Mahjarrat such as Lucien and Azzanadra would become extremely powerful",
									"If they got their hands on the staff of Armadyl");
								int maj = multi(player, n, false, //do not send over
									"Did you say Lucien?",
									"You had better guard it well then");
								if (maj == 0) {
									say(player, n, "Did you say Lucien?",
										"He's the one who sent me to fetch the staff");
									guardianDialogue(player, n, Guardian.WORKINGFORLUCIEN);
								} else if (maj == 1) {
									say(player, n, "You had better guard it well them");
									npcsay(player, n, "Don't fret, for we shall");
								}
							} else if (whoMenu == 2) {
								npcsay(player, n, "No no, I have not guarded here for all those generations",
									"Many generations of my family have though");
							}
						}
						break;
					case 2:
						npcsay(player, n, "Any luck against Lucien?");
						if (!player.getCarriedItems().hasCatalogID(ItemId.PENDANT_OF_ARMADYL.id(), Optional.empty())) {
							int option = multi(player, n, "Not yet", "No I've lost the pendant you gave me");
							if (option == 0) {
								npcsay(player, n, "Well good luck on your quest");
							} else if (option == 1) {
								npcsay(player, n, "Thou art a careless buffoon",
									"Have another one");
								player.message("The guardian gives you a pendant");
								give(player, ItemId.PENDANT_OF_ARMADYL.id(), 1);
							}
						} else {
							say(player, n, "Not yet");
							npcsay(player, n, "Well good luck on your quest");
						}
						break;
					case -1:
						say(player, n, "I have defeated Lucien");
						npcsay(player, n, "Well done",
							"We can only hope that will keep him quiet for a while");
						break;
					case -2:
						npcsay(player, n, "Get away from here", "Thou evil agent of Lucien");
						break;
				}
			}
			switch (cID) {
				case Guardian.WORKINGFORLUCIEN:
					npcsay(player, n, "Thou art working for him?",
						"Thy fool",
						"Quick you must be cleansed to save your soul");
					int menu = multi(player, n, false, //do not send over
						"How dare you call me a fool?",
						"Erm I think I'll be leaving now",
						"Yes I could do with a bath");
					if (menu == 0) {
						say(player, n, "How dare you call me a fool",
							"I will work for who I please");
						npcsay(player, n, "This one is too far gone",
							"He must be cut down to stop the spread of the blight");
						n.startCombat(player);
					} else if (menu == 1) {
						say(player, n, "Erm, I think I'll be leaving now");
						npcsay(player, n, "We cannot allow an agent of Lucien to roam free");
						n.startCombat(player);
					} else if (menu == 2) {
						say(player, n, "Yes I could do with a bath");
						player.message("The guardian splashes holy water over you");
						npcsay(player, n, "That should do the trick",
							"Now you say that Lucien sent you to retrieve the staff",
							"He must not get a hold of it",
							"He would become too powerful with the staff",
							"Hast thou heard of the undead necromancer?",
							"Who raised an undead army against Varrock a few years past",
							"That was Lucien",
							"If thou knowest where to find him maybe you can help us against him");
						int lastMenu = multi(player, n,
							"Ok I will help",
							"No I shan't turn against my employer",
							"I need time to consider this");
						if (lastMenu == 0) {
							npcsay(player, n, "So you know where he lurks?");
							say(player, n, "Yes");
							npcsay(player, n, "He must be growing in power again if he is after the staff",
								"If you can defeat him, it may weaken him for a time",
								"You will need to use this pendant to even be able to attack him");
							player.message("The guardian gives you a pendant");
							give(player, ItemId.PENDANT_OF_ARMADYL.id(), 1);
							player.updateQuestStage(this, 2);
						} else if (lastMenu == 1) {
							npcsay(player, n, "This one is too far gone",
								"He must be cut down to stop the spread of the blight");
							n.startCombat(player);
						} else if (lastMenu == 2) {
							npcsay(player, n, "Come back when you have made your choice");
						}
					}
					break;
			}
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.LUCIEN_EDGE.id()) {
			if (player.getQuestStage(this) == -1 || player.getQuestStage(this) == -2) {
				player.message("You have already completed this quest");
				return;
			}
			npcsay(player, n, "Have you got the staff of Armadyl yet?");
			if (player.getCarriedItems().hasCatalogID(ItemId.STAFF_OF_ARMADYL.id(), Optional.of(false))) {
				int menu = multi(player, n,
					"Yes here it is",
					"No not yet");
				if (menu == 0) {
					mes("You give the staff to Lucien");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.STAFF_OF_ARMADYL.id()));
					npcsay(player, n, "Muhahahaha",
						"Already I can feel the power of this staff running through my limbs",
						"Soon I shall be exceedingly powerful",
						"I suppose you would like a reward now",
						"I shall grant you much power");
					player.message("A glow eminates from Lucien's helmet");
					player.sendQuestComplete(Quests.TEMPLE_OF_IKOV);
					player.updateQuestStage(this, -2);
					npcsay(player, n, "I must be away now to make preparations for my conquest",
						"Muhahahaha");
					n.remove();

				}
			} else {
				say(player, n, "No not yet");
			}
		}
		else if (n.getID() == NpcId.LUCIEN.id()) {
			lucienDialogue(player, n, -1);
		}
		else if (n.getID() == NpcId.WINELDA.id()) {
			wineldaDialogue(player, n, -1);

		}
		else if (n.getID() == NpcId.GUARDIAN_OF_ARMADYL_FEMALE.id() || n.getID() == NpcId.GUARDIAN_OF_ARMADYL_MALE.id()) {
			guardianDialogue(player, n, -1);
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return (obj.getID() == STAIR_DOWN || obj.getID() == STAIR_UP) || (obj.getID() == LEVER || obj.getID() == COMPLETE_LEVER);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == STAIR_DOWN) {
			if (player.getCarriedItems().hasCatalogID(ItemId.LIT_CANDLE.id(), Optional.of(false))
				|| player.getCarriedItems().hasCatalogID(ItemId.LIT_BLACK_CANDLE.id(), Optional.of(false))
				|| player.getCarriedItems().hasCatalogID(ItemId.LIT_TORCH.id(), Optional.of(false))) {
				player.message("Your flame lights up the room");
				player.teleport(537, 3372);
			} else {
				player.message("You cannot see any further into the room");
				player.teleport(537, 3394);
				delay(3);
				player.message("It is too dark");
			}
		}
		else if (obj.getID() == STAIR_UP) {
			player.teleport(536, 3338);
		}
		else if (obj.getID() == LEVER) {
			if (command.equals("pull")) {
				if (!player.getCache().hasKey("ikovLever")) {
					player.message("You have activated a trap on the lever");
					player.damage(DataConversions.roundUp(player.getSkills().getLevel(Skill.HITS.id()) / 5));
				} else {
					mes("You pull the lever");
					delay(3);
					mes("You hear a clunk");
					delay(3);
					mes("The trap on the lever resets");
					delay(3);
					if (player.getCache().hasKey("ikovLever")) {
						player.getCache().remove("ikovLever");
					}
					if (!player.getCache().hasKey("openSpiderDoor") && (player.getQuestStage(this) != -1 || player.getQuestStage(this) != -2)) {
						player.getCache().store("openSpiderDoor", true);
					}
				}
			} else if (command.equals("searchfortraps")) {
				player.message("You search the lever for traps");
				if (getCurrentLevel(player, Skill.THIEVING.id()) < 42) {
					player.message("You have not high thieving enough to disable this trap");
					return;
				}
				mes("You find a trap on the lever");
				delay(3);
				mes("You disable the trap");
				delay(3);
				if (!player.getCache().hasKey("ikovLever")) {
					player.getCache().store("ikovLever", true);
				}
			}
		}
		else if (obj.getID() == COMPLETE_LEVER) {
			mes("You pull the lever");
			delay(3);
			mes("You hear the door next to you make a clunking noise");
			delay(3);
			if (!player.getCache().hasKey("completeLever") && (player.getQuestStage(this) != -1 || player.getQuestStage(this) != -2)) {
				player.getCache().store("completeLever", true);
			}
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.ICE_ARROWS.id()) {
			return true;
		}
		if (i.getID() == ItemId.STAFF_OF_ARMADYL.id()) {
			Npc guardian = ifnearvisnpc(player, 5, NpcId.GUARDIAN_OF_ARMADYL_FEMALE.id(), NpcId.GUARDIAN_OF_ARMADYL_MALE.id());
			if (guardian == null)
				return false;
			else
				return true;
		}
		return false;
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.ICE_ARROWS.id()) {
			if (i.getX() == 560 && i.getY() == 3352 || i.getX() == 563 && i.getY() == 3354) {
				give(player, ItemId.ICE_ARROWS.id(), 1);
				player.teleport(538, 3348);
				delay();
				ActionSender.sendTeleBubble(player, player.getX(), player.getY(), false);
				delay(2);
				player.message("Suddenly your surroundings change");
			} else {
				mes("You can only take ice arrows from the cave of ice spiders");
				delay(3);
				mes("In the temple of Ikov");
				delay(3);
			}
		}
		else if (i.getID() == ItemId.STAFF_OF_ARMADYL.id()) {
			if (player.getQuestStage(this) == 2 || player.getQuestStage(this) == -1 || player.getQuestStage(this) == -2) {
				player.message("I shouldn't steal this");
				return;
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.STAFF_OF_ARMADYL.id(), Optional.of(false))) {
				player.message("I already have one of those");
				return;
			}
			Npc n = ifnearvisnpc(player, 5, NpcId.GUARDIAN_OF_ARMADYL_FEMALE.id(), NpcId.GUARDIAN_OF_ARMADYL_MALE.id());
			if (n != null) {
				npcsay(player, n, "That is not thine to take");
				n.setChasing(player);
				return;
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return item.getCatalogId() == ItemId.LEVER.id() && obj.getID() == LEVER_BRACKET;
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (item.getCatalogId() == ItemId.LEVER.id() && obj.getID() == LEVER_BRACKET) {
			player.message("You fit the lever into the bracket");
			player.getCarriedItems().remove(new Item(ItemId.LEVER.id()));
			player.getWorld().replaceGameObject(obj,
				new GameObject(obj.getWorld(), obj.getLocation(), COMPLETE_LEVER, obj.getDirection(), obj
					.getType()));
			player.getWorld().delayedSpawnObject(obj.getLoc(), 15000);
		}
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id();
	}

	@Override
	public void onSpellNpc(Player player, Npc n) {
		if (n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id()) {
			if (player.getCache().hasKey("killedLesarkus") || player.getQuestStage(this) == -1 || player.getQuestStage(this) == -2) {
				player.message("You have already killed the fire warrior");
				return;
			}
			player.message("You need to kill the fire warrior with ice arrows");
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id() || n.getID() == NpcId.LUCIEN_EDGE.id();
	}

	@Override
	public void onKillNpc(Player player, Npc npc) {
		if (npc.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id()) {
			if (!player.getCache().hasKey("killedLesarkus")) {
				player.getCache().store("killedLesarkus", true);
			}
		}
		else if (npc.getID() == NpcId.LUCIEN_EDGE.id()) {
			if (player.getQuestStage(this) == -1 || player.getQuestStage(this) == -2) {
				player.message("You have already completed this quest");
				npc.getSkills().setLevel(Skill.HITS.id(), npc.getSkills().getMaxStat(Skill.HITS.id()));
				npc.killed = false;
				return;
			}
			npc.getSkills().setLevel(Skill.HITS.id(), npc.getSkills().getMaxStat(Skill.HITS.id()));
			npcsay(player, npc, "You may have defeated me for now",
				"But I will be back");
			player.sendQuestComplete(Quests.TEMPLE_OF_IKOV);
			npc.displayNpcTeleportBubble(npc.getX(), npc.getY());
			npc.remove();
		}
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		return n.getID() == NpcId.LUCIEN_EDGE.id();
	}

	@Override
	public void onAttackNpc(Player player, Npc n) {
		if (n.getID() == NpcId.LUCIEN_EDGE.id()) {
			if (player.getQuestStage(this) == -1 || player.getQuestStage(this) == -2) {
				player.message("You have already completed this quest");
			} else {
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.PENDANT_OF_ARMADYL.id())) {
					player.startCombat(n);
				} else {
					npcsay(player, n, "I'm sure you don't want to attack me really",
						"I am your friend");
					mes("You decide you don't want to attack Lucien really");
					delay(3);
					mes("He is your friend");
					delay(3);
				}
			}
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player player, Npc n) {
		if (n.getID() == NpcId.LUCIEN_EDGE.id() && (player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -1 || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -2 || !player.getCarriedItems().getEquipment().hasEquipped(ItemId.PENDANT_OF_ARMADYL.id()))) {
			return true;
		}
		if (n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id() && (player.getCache().hasKey("killedLesarkus") || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -1 || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -2)) {
			return true;
		} else {
			if (n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id() && ((player.getCarriedItems().hasCatalogID(ItemId.ICE_ARROWS.id()) && hasGoodBow(player)) || player.getCache().hasKey("shot_ice"))) {
				player.getCache().store("shot_ice", true);
				return false;
			}
		}
		if (n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id() && !player.getCache().hasKey("shot_ice")) {
			return true;
		}
		return false;
	}

	public boolean hasGoodBow(Player player) {

		int[] allowedBowsIce = {ItemId.YEW_LONGBOW.id(), ItemId.YEW_SHORTBOW.id(), ItemId.MAGIC_LONGBOW.id(), ItemId.MAGIC_SHORTBOW.id()};
		boolean hasGoodBow = false;

		for (int bow : allowedBowsIce) {
			hasGoodBow |= (player.getCarriedItems().hasCatalogID(bow) && player.getCarriedItems().getEquipment().hasEquipped(bow));
		}

		return hasGoodBow;
	}

	@Override
	public void onPlayerRangeNpc(Player player, Npc n) {
		if (n.getID() == NpcId.LUCIEN_EDGE.id()) {
			if (player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -1 || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -2) {
				player.message("You have already completed this quest");
				return;
			}
			if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.PENDANT_OF_ARMADYL.id())) {
				npcsay(player, n, "I'm sure you don't want to attack me really",
					"I am your friend");
				mes("You decide you don't want to attack Lucien really");
				delay(3);
				mes("He is your friend");
				delay(3);
				return;
			}
		}
		else if (n.getID() == NpcId.THE_FIRE_WARRIOR_OF_LESARKUS.id()) {
			if ((player.getCache().hasKey("killedLesarkus") || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -1 || player.getQuestStage(Quests.TEMPLE_OF_IKOV) == -2)) {
				player.message("You have already killed the fire warrior");
				return;
			}
			if (!player.getCache().hasKey("shot_ice")) {
				player.message("You need to kill the fire warrior with ice arrows");
				return;
			}
		}
	}

	class Lucien {
		public static final int SOUNDSFUN = 0;
		public static final int PAYME = 1;
	}

	class Winelda {
		public static final int YES = 0;
	}

	class Guardian {
		public static final int WORKINGFORLUCIEN = 0;
	}
}
