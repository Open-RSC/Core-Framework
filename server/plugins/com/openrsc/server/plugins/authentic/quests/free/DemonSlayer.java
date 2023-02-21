package com.openrsc.server.plugins.authentic.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;
import java.util.List;

import static com.openrsc.server.plugins.Functions.*;

public class DemonSlayer implements QuestInterface,
	KillNpcTrigger, TalkNpcTrigger, OpLocTrigger,
	UseLocTrigger, PlayerRangeNpcTrigger, AttackNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.DEMON_SLAYER;
	}

	@Override
	public String getQuestName() {
		return "Demon slayer";
	}

	@Override
	public int getQuestPoints() {
		return Quest.DEMON_SLAYER.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("You have completed the demonslayer quest");
		final QuestReward reward = Quest.DEMON_SLAYER.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 77;
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == 77) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
					mes("I have no reason to do that.");
					delay(3);
					break;
				case 2:
				case 3:
				case 4:
				case -1:
					//even post-quest was the same thing
					if (item.getCatalogId() == ItemId.BUCKET_OF_WATER.id()) {
						mes("You pour the liquid down the drain");
						delay(3);
						mes("Ok I think I've washed the key down into the sewer");
						delay(3);
						mes("I'd better go down and get it before someone else finds it");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.BUCKET_OF_WATER.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.BUCKET.id()));
						player.getWorld().registerItem(
							new GroundItem(player.getWorld(), ItemId.SILVERLIGHT_KEY_3.id(), 117, 3294, 1, player));
					}
					break;
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] { NpcId.GYPSY.id(), NpcId.SIR_PRYSIN.id(),
				NpcId.TRAIBORN_THE_WIZARD.id(), NpcId.CAPTAIN_ROVIN.id() }, n.getID());
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 77;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 77 && obj.getY() == 461) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
					mes("I can see a key but can't quite reach it...");
					delay(3);
					break;
				case 2:
				case 3:
				case 4:
					mes("This is the drainpipe");
					delay(3);
					mes("Running from the kitchen sink to the sewer");
					delay(3);
					mes("I can see a key just inside the drain");
					delay(3);
					mes("That must be the key Sir Prysin dropped");
					delay(3);
					mes("I don't seem to be able to quite reach it");
					delay(3);
					mes("It's stuck part way down");
					delay(3);
					mes("I wonder if I can dislodge it somehow");
					delay(3);
					mes("And knock it down into the sewers");
					delay(3);
					break;
				case -1:
					mes("This is the drainpipe");
					delay(3);
					mes("Running from the kitchen sink to the sewer");
					delay(3);
					mes("I can see a key just inside the drain");
					delay(3);
					mes("I don't seem to be able to quite reach it");
					delay(3);
					mes("It's stuck part way down");
					delay(3);
					mes("I wonder if I can dislodge it somehow");
					delay(3);
					mes("And knock it down into the sewers");
					delay(3);
					break;
			}
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GYPSY.id()) {
			gypsyDialogue(player, n, -1);
		} else if (n.getID() == NpcId.SIR_PRYSIN.id()) {
			sirPrysinDialogue(player, n, -1);
		} else if (n.getID() == NpcId.TRAIBORN_THE_WIZARD.id()) {
			traibornTheWizDialogue(player, n, -1);
		} else if (n.getID() == NpcId.CAPTAIN_ROVIN.id()) {
			captainRovinDialogue(player, n, -1);
		}
	}

	private void captainRovinDialogue(Player player, Npc n, int cID) {
		int questStage = player.getQuestStage(this);
		if (cID == -1) {
			List<String> choices = new ArrayList<>();
			npcsay(player, n, "What are you doing up here?",
				"Only the palace guards are allowed up here");
			choices.add("I am one of the palace guards");
			choices.add("What about the king?");

			if (questStage >= 2)
				choices.add("Yes I know but this important");

			if (config().WANT_CUSTOM_QUESTS
				&& getMaxLevel(player, Skill.ATTACK.id()) >= 99)
				choices.add("Attack Skillcape");

			int choice = multi(player, n, false, choices.toArray(new String[0])); // Do not send choice to client
			if (choice == 0) {
				say(player, n, "I am one of the palace guard");
				captainRovinDialogue(player, n, CaptainRovin.PALACE);
			} else if (choice == 1) {
				say(player, n, "What about the king?");
				captainRovinDialogue(player, n, CaptainRovin.KING);
			} else {
				if (choice != -1 && choices.get(choice).equalsIgnoreCase("Attack Skillcape")) {
					if (config().WANT_CUSTOM_SPRITES && getMaxLevel(player, Skill.ATTACK.id()) >= 99) {
						npcsay(player, n, "I see you too are a master of attack",
							"You are worthy to wield the Attack Skillcape",
							"The cost is 99,000 coins");
						int choice2 = multi(player, n, true, "I'll buy one", "Not at the moment");
						if (choice2 == 0) {
							if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 99000) {
								if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 99000)) > -1) {
									give(player, ItemId.ATTACK_CAPE.id(), 1);
									npcsay(player, n, "Congratulations",
										"Wearing this cape in battle",
										"will increase to your accuracy");
								}
							} else {
								npcsay(player, n, "You don't have the money?",
									"Get lost");
							}
						}
					}
				} else if (choices.get(choice).equalsIgnoreCase("Yes I know but this important")) {
					if (questStage > 1) {
						say(player, n, "Yes, I know, but this is important");
						captainRovinDialogue(player, n, CaptainRovin.IMPORTANT);
					}
				}
			}
		}

		switch (cID) {
			case CaptainRovin.PALACE:
				npcsay(player, n, "No, you're not. I know all the palace guard");
				int choice2 = multi(player, n, "I'm a new recruit",
					"I've had extensive plastic surgery");
				if (choice2 == 0) {
					captainRovinDialogue(player, n, CaptainRovin.RECRUIT);
				} else if (choice2 == 1) {
					captainRovinDialogue(player, n, CaptainRovin.SURGERY);
				}
				break;

			case CaptainRovin.SURGERY:
				npcsay(player, n, "What sort of surgery is that?", "Never heard of it",
					"Besides, you look reasonably healthy",
					"Why is this relevant anyway?",
					"You still shouldn't be here");
				break;
			case CaptainRovin.KING:
				say(player, n, "Surely you'd let him up here?");
				npcsay(player, n, "Well, yes, I suppose we'd let him up",
					"He doesn't generally want to come up here",
					"But if he did want to", "He could come up",
					"Anyway, you're not the king either",
					"So get out of my sight");
				break;
			case CaptainRovin.IMPORTANT:
				npcsay(player, n, "Ok, I'm listening", "Tell me what's so important");
				int choice = multi(player, n,
					"There's a demon who wants to invade this city",
					"Erm I forgot",
					"The castle has just received it's ale delivery");
				if (choice == 0) {
					captainRovinDialogue(player, n, CaptainRovin.DEMON_INVASION);
				} else if (choice == 1) {
					captainRovinDialogue(player, n, CaptainRovin.I_FORGOT);
				} else if (choice == 2) {
					captainRovinDialogue(player, n, CaptainRovin.DELIVERY);
				}
				break;
			case CaptainRovin.DEMON_INVASION:
				npcsay(player, n, "Is it a powerful demon?");
				say(player, n, "Yes, very");
				npcsay(player, n, "Well as good as the palace guards are",
					"I don't think they're up to taking on a very powerful demon");
				say(player, n,
					"No no, it's not them who's going to fight the demon",
					"It's me");
				npcsay(player, n, "What all by yourself?");
				say(player, n,
					"Well I am going to use the powerful sword silverlight",
					"Which I believe you have one of the keys for");
				npcsay(player, n, "Yes you're right", "Here you go");
				mes("Captain Rovin hands you a key");
				delay(3);
				give(player, ItemId.SILVERLIGHT_KEY_2.id(), 1);
				break;
			case CaptainRovin.I_FORGOT:
				npcsay(player, n, "Well it can't be that important then");
				say(player, n, "How do you know?");
				npcsay(player, n, "Just go away");
				break;
			case CaptainRovin.DELIVERY:
				npcsay(player, n, "Now that is important",
					"However, I'm the wrong person to speak to about it",
					"Go talk to the kitchen staff");
				break;
			case CaptainRovin.RECRUIT:
				npcsay(player, n, "I interview all the new recruits",
					"I'd know if you were one of them");
				say(player, n, "That blows that story out of the window then");
				npcsay(player, n, "Get out of my sight");
				break;
		}
	}

	private void traibornTheWizDialogue(Player player, Npc n, int cID) {

		if (cID == -1) {
			switch (player.getQuestStage(this)) {
				case -1:
				case 0:
				case 1:
					npcsay(player, n, "Ello young thingummywut");
					int choice = multi(player, n,
						"Whats a thingummywut?",
						"Teach me to be a mighty and powerful wizard");
					if (choice == 0) {
						traibornTheWizDialogue(player, n, Traiborn.THINGWUT);
						int choice2 = multi(player, n,
							"Err you just called me thingummywut",
							"Tell me what they look like and I'll mash 'em");
						if (choice2 == 0) {
							traibornTheWizDialogue(player, n, Traiborn.CALLEDME);
							int choice3 = multi(player, n,
								"Err I'd better be off really",
								"They're right, you are mad");
							if (choice3 == 0) {
								traibornTheWizDialogue(player, n, Traiborn.BEOFF);
							} else if (choice3 == 1) {
								traibornTheWizDialogue(player, n, Traiborn.MAD);
							}
						} else if (choice2 == 1) {
							traibornTheWizDialogue(player, n, Traiborn.MASHEM);
						}
					} else if (choice == 1) {
						traibornTheWizDialogue(player, n, Traiborn.TEACHME);
					}
					break;
				//stage of not having obtained silverlight and having obtained it
				case 3:
				case 4:
					//key obtained, present in inventory
					if (player.getCarriedItems().hasCatalogID(ItemId.SILVERLIGHT_KEY_1.id())) {
						npcsay(player, n, "Ello young thingummywut");
						int choice3 = multi(player, n,
							"Whats a thingummywut?",
							"Teach me to be a mighty and powerful wizard");
						if (choice3 == 0) {
							traibornTheWizDialogue(player, n, Traiborn.THINGWUT);
						} else if (choice3 == 1) {
							traibornTheWizDialogue(player, n, Traiborn.TEACHME);
						}
					}
					//cache of bone task done
					else if (player.getCache().hasKey("done_bone_task")) {
						say(player, n, "I've lost the key you gave me");
						npcsay(player, n, "Yes I know", "It was returned to me",
							"If you want it back",
							"you're going to have to collect another 25 sets of bones");
						//player needs to redo bone task
						player.getCache().remove("done_bone_task");
					} else {
						npcsay(player, n, "How are you doing finding bones?");
						if (player.getCarriedItems().getInventory().countId(ItemId.BONES.id()) <= 0) {
							say(player, n, "I haven't got any at the moment");
							npcsay(player, n, "Never mind. Keep working on it");
							return;
						}
						say(player, n, "I have some bones");
						npcsay(player, n, "Give 'em here then");
						int boneCount = 0;
						if (!player.getCache().hasKey("traiborn_bones"))
							player.getCache().set("traiborn_bones", boneCount);
						else
							boneCount = player.getCache().getInt("traiborn_bones");

						while (player.getCarriedItems().getInventory().countId(ItemId.BONES.id()) > 0) {
							player.getCarriedItems().remove(new Item(ItemId.BONES.id()));
							player.message("You give Traiborn a set of bones");
							boneCount++;
							delay();
							if (boneCount >= 25)
								break;
						}
						player.getCache().set("traiborn_bones", boneCount);
						if (boneCount >= 25) {
							npcsay(player, n, "Hurrah! That's all 25 sets of bones");
							mes("Traiborn places the bones in a circle on the floor");
							delay(3);
							mes("Traiborn waves his arms about");
							delay(3);
							npcsay(player, n, "Wings of dark and colour too",
								"Spreading in the morning dew");
							mes("The wizard waves his arms some more");
							delay(3);
							npcsay(player, n, "Locked away I have a key",
								"Return it now unto me");
							mes("Traiborn smiles");
							delay(3);
							mes("Traiborn hands you a key");
							delay(3);
							player.getCarriedItems().getInventory().add(new Item(ItemId.SILVERLIGHT_KEY_1.id(), 1));
							player.getCache().store("done_bone_task", true);
							say(player, n, "Thank you very much");
							npcsay(player, n,
								"Not a problem for a friend of sir what's-his-face");
							player.getCache().remove("traiborn_bones");
						} else {
							say(player, n, "That's all of them");
							npcsay(player, n, "I still need more");
							say(player, n, "Ok, I'll look for some more");
						}
					}
					break;
				case 2:
					npcsay(player, n, "Ello young thingummywut");
					int choice4;
					if (!player.getCarriedItems().hasCatalogID(ItemId.SILVERLIGHT_KEY_1.id())) {
						choice4 = multi(player, n,
							"Whats a thingummywut?",
							"Teach me to be a mighty and powerful wizard",
							"I need to get a key given to you by Sir Prysin");
					} else {
						choice4 = multi(player, n,
							"Whats a thingummywut?",
							"Teach me to be a mighty and powerful wizard");
					}
					if (choice4 == 0) {
						traibornTheWizDialogue(player, n, Traiborn.THINGWUT);
					} else if (choice4 == 1) {
						traibornTheWizDialogue(player, n, Traiborn.TEACHME);
					} else if (choice4 == 2) {
						npcsay(player, n, "Sir Prysin?  Who's that?",
							"What would I want his key for?");
						int choice5 = multi(player, n,
							"He told me you were looking after it for him",
							"He's one of the king's knights",
							"Well, have you got any keys knocking around?");
						if (choice5 == 0) {
							npcsay(player,
								n,
								"That wasn't very clever of him",
								"I'd lose my head if it wasn't screwed on properly",
								"Go tell him to find someone else",
								"to look after his valuables in future");
							int choice6 = multi(player, n,
								"Ok, I'll go and tell him that",
								"Well, have you got any keys knocking around?");
							if (choice6 == 0) {
								npcsay(player, n, "Oh that's great",
									"If it wouldn't be too much trouble");
								int choice7 = multi(player, n,
									"Err I'd better be off really",
									"Well, have you got any keys knocking around?");
								if (choice7 == 0) {
									traibornTheWizDialogue(player, n, Traiborn.BEOFF);
								} else if (choice7 == 1) {
									traibornTheWizDialogue(player, n, Traiborn.KNOCKING);
								}
							} else if (choice6 == 1) {
								traibornTheWizDialogue(player, n, Traiborn.KNOCKING);
							}
						} else if (choice5 == 1) {
							npcsay(player, n, "Say, I remember a knight with a key",
								"He had nice shoes",
								"and didn't like my homemade spinach rolls",
								"Would you like a spinach roll?");
							int choice8 = multi(player, n,
								"Yes Please",
								"Just tell me if you have the key");
							if (choice8 == 0) {
								mes("Traiborn digs around in the pockets of his robes");
								delay(3);
								mes("Traiborn hands you a spinach roll");
								delay(3);
								give(player, ItemId.SPINACH_ROLL.id(), 1);
								say(player, n, "Thank you very much");
								int choice9 = multi(player, n,
									"Err I'd better be off really",
									"Well, have you got any keys knocking around?");
								if (choice9 == 0) {
									traibornTheWizDialogue(player, n, Traiborn.BEOFF);
								} else if (choice9 == 1) {
									traibornTheWizDialogue(player, n, Traiborn.KNOCKING);
								}
							} else if (choice8 == 1) {
								npcsay(player, n, "The key?", "The key to what?",
									"There's more than one key in the world, don't you know?",
									"Would be a bit odd if there was only one");
								int choice9a = multi(player, n, false, //do not send over
									"It's the key to get a sword called Silverlight",
									"You've lost it, haven't you?");
								if (choice9a == 0) {
									say(player, n, "Its the key to get a sword called Silverlight");
									npcsay(player, n, "Silverlight? Never heard of that",
										"Sounds a good name for a ship",
										"Are you sure it's not the name of a ship, rather than a sword?");
									int choice9b = multi(player, n,
										"Yeah, pretty sure",
										"Well, have you got any keys knocking around?");
									if (choice9b == 0) {
										npcsay(player, n, "That's a pity",
											"Waste of a name");
										int opts = multi(player, n,
											"Err I'd better be off really",
											"Well, have you got any keys knocking around?");
										if (opts == 0) {
											traibornTheWizDialogue(player, n, Traiborn.BEOFF);
										} else if (opts == 1) {
											traibornTheWizDialogue(player, n, Traiborn.KNOCKING);
										}
									} else if (choice9b == 1) {
										traibornTheWizDialogue(player, n, Traiborn.KNOCKING);
									}
								} else if (choice9a == 1) {
									say(player, n, "You've lost it, haven't you?");
									npcsay(player, n, "Me?  Lose things?",
										"Thats a nasty accusation");
									say(player, n, "Well, have you got any keys knocking around?");
									traibornTheWizDialogue(player, n, Traiborn.KNOCKING);
								}
							}
						} else if (choice5 == 2) {
							traibornTheWizDialogue(player, n, Traiborn.KNOCKING);
						}
					}
					break;
			}
		}

		switch (cID) {
			case Traiborn.TEACHME:
				npcsay(player, n, "Wizard, Eh?",
					"You don't want any truck with that sort",
					"They're not to be trusted",
					"That's what I've heard anyways");
				int choice4 = multi(player, n, false,
					"So aren't you a wizard?",
					"Oh I'd better stop talking to you then"); // Don't send to client
				if (choice4 == 0) {
					say(player, n, "So aren't you a wizard");
					npcsay(player, n, "How dare you?", "Of course I'm a wizard",
						"Now don't be so cheeky or I'll turn you into a frog");
				} else if (choice4 == 1) {
					say(player, n, "Oh I'd better stop talking to you then");
					npcsay(player, n, "Cheerio then", "Was nice chatting to you");
				}
				break;
			case Traiborn.THINGWUT:
				npcsay(player, n, "A thingummywut?", "Where? , Where?",
					"Those pesky thingummywuts", "They get everywhere",
					"They leave a terrible mess too");
				break;
			case Traiborn.CALLEDME:
				npcsay(player, n, "You're a thingummywut?",
					"I've never seen one up close before",
					"They said I was mad", "Now you are my proof",
					"There ARE thingummywuts in this tower",
					"Now where can I find a cage big enough to keep you?");
				break;
			case Traiborn.MASHEM:
				npcsay(player, n, "Don't be ridiculous", "No-one has ever seen one",
					"They're invisible", "Or a myth",
					"Or a figment of my imagination",
					"Can't remember which right now");
				break;
			case Traiborn.BEOFF:
				npcsay(player, n, "Oh ok have a good time", "and watch out for sheep!",
					"They're more cunning than they look");
				break;
			case Traiborn.MAD:
				npcsay(player, n, "That's a pity",
					"I thought maybe they were winding me up");
				break;
			case Traiborn.KNOCKING:
				npcsay(player, n, "Now you come to mention it - yes I do have a key",
					"Its in my special closet of valuable stuff",
					"Now how do I get into that?");
				mes("The wizard scratches his head");
				delay(3);
				npcsay(player, n, "I sealed it using one of my magic rituals",
					"so it would make sense that another ritual",
					"Would open it again");
				mes("The wizard beams");
				delay(3);
				say(player, n, "So do you know what ritual to use?");
				npcsay(player, n, "Let me think a second");
				delay(2);
				npcsay(player, n, "Yes a simple drazier style ritual should suffice",
					"Hmm",
					"Main problem with that is I'll need 25 sets of bones",
					"Now where am I going to get hold of something like that");
				int choices = multi(player, n, false,
					"Hmm, that's too bad. I really need that key",
					"I'll get the bones for you");
				if (choices == 0) {
					say(player, n, "Hmm, thats too bad. I really need that key");
					npcsay(player, n, "Ah well sorry I couldn't be any more help");
				} else if (choices == 1) {
					say(player, n, "I'll get the bones for you");
					traibornTheWizDialogue(player, n, Traiborn.BONES);
				}
				break;
			case Traiborn.BONES:
				npcsay(player, n, "Ooh that would very good of you");
				say(player, n, "Ok I'll speak to you when I've got some bones");
				player.updateQuestStage(this, 3);
				break;
		}
	}

	private void sirPrysinDialogue(Player player, Npc n, int cID) {
		if (cID == -1) {
			int questStage = player.getQuestStage(this);
			int choice;
			switch (questStage) {
				case 0:
				case 1:
					npcsay(player, n, "Hello, who are you");
					String[] choices = new String[]{
						"I am a mighty adventurer. Who are you?",
						"I'm not sure, I was hoping you could tell me"
					};
					if (questStage > 0)
						choices = new String[]{
							"I am a mighty adventurer. Who are you?",
							"I'm not sure, I was hoping you could tell me",
							"Gypsy Aris said I should come and talk to you"
						};

					choice = multi(player, n, false, //do not send over
						choices);
					if (choice == 0) {
						say(player, n, "I am a mighty adventurer. Who are you?");
						npcsay(player, n, "I am Sir Prysin",
							"A bold and famous knight of the realm");
					} else if (choice == 1) {
						say(player, n, "I'm not sure. I was hoping you could tell me");
						npcsay(player, n, "Well I've never met you before");
					} else if (choice == 2 && questStage > 0) {
						say(player, n, "Gypsy Aris said I should come and talk to you");
						sirPrysinDialogue(player, n, SirPrysin.GYPSY);
					}
					break;
				case 2:
				case 3:
				case 4:
					//if silverlight is lost, player needs to regain keys
					if (!player.getCarriedItems().hasCatalogID(ItemId.SILVERLIGHT.id())) {
						npcsay(player, n, "So how are you doing with getting the keys?");
						if (player.getCarriedItems().hasCatalogID(ItemId.SILVERLIGHT_KEY_2.id())
							&& player.getCarriedItems().hasCatalogID(ItemId.SILVERLIGHT_KEY_1.id())
							&& player.getCarriedItems().hasCatalogID(ItemId.SILVERLIGHT_KEY_3.id())) {
							say(player, n, "I've got them all");
							sirPrysinDialogue(player, n, SirPrysin.GOT_THEM);
							return;
						} else if (!player.getCarriedItems().hasCatalogID(ItemId.SILVERLIGHT_KEY_2.id())
							&& !player.getCarriedItems().hasCatalogID(ItemId.SILVERLIGHT_KEY_1.id())
							&& !player.getCarriedItems().hasCatalogID(ItemId.SILVERLIGHT_KEY_3.id())) {
							say(player, n, "I've not found any of them yet");
						} else {
							say(player, n, "I've made a start");
						}
						if (player.getCarriedItems().hasCatalogID(ItemId.SILVERLIGHT_KEY_2.id())) {
							say(player, n, "I've got the key off Wizard Traiborn");
						}
						if (player.getCarriedItems().hasCatalogID(ItemId.SILVERLIGHT_KEY_1.id())) {
							say(player, n, "I've got the key off Captain Rovin");
						}
						if (player.getCarriedItems().hasCatalogID(ItemId.SILVERLIGHT_KEY_3.id())) {
							say(player, n,
								"I've got the key You dropped down the drain");
						}
						choice = multi(player, n, false,
							"Can you remind me where all the keys were again?",
							"I'm still looking");
						if (choice == 0) {
							say(player, n, "Can you remind me where all the keys were again");
							npcsay(player, n, "I kept one of the keys", "I gave the other two",
								"To other people for safe keeping", "One I gave to Rovin",
								"who is captain of the palace guard", "I gave the other to the wizard Traiborn");
							choice = multi(player, n,
								"Can you give me your key?",
								"Where can I find Captain Rovin?",
								"Where does the wizard live?");
							if (choice == 0) {
								sirPrysinDialogue(player, n, SirPrysin.YOUR_KEY);
							} else if (choice == 1) {
								sirPrysinDialogue(player, n, SirPrysin.ROVIN);
							} else if (choice == 2) {
								sirPrysinDialogue(player, n, SirPrysin.WIZARD);
							}
						} else if (choice == 1) {
							say(player, n, "I'm still looking");
							npcsay(player, n, "Ok, tell me when you've got them all");
						}
					} else {
						npcsay(player, n, "You sorted that demon yet?");
						say(player, n, "No, not yet");
						npcsay(player, n, "Well get on with it",
							"He'll be pretty powerful when he gets to full strength");
					}
					break;
				case -1:
					npcsay(player, n,
						"Hello. I've heard you stopped the demon well done");
					say(player, n, "Yes, that's right");
					npcsay(player, n, "A good job well done then");
					say(player, n, "Thank you");
					break;
			}
		}
		switch (cID) {
			case SirPrysin.GOT_THEM:
				npcsay(player, n, "Excellent. Now I can give you Silverlight");
				mes("You give all three keys to Sir Prysin");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.SILVERLIGHT_KEY_1.id()));
				player.getCarriedItems().remove(new Item(ItemId.SILVERLIGHT_KEY_2.id()));
				player.getCarriedItems().remove(new Item(ItemId.SILVERLIGHT_KEY_3.id()));
				mes("Sir Prysin unlocks a long thin box");
				delay(3);
				mes("Prysin hands you an impressive looking sword");
				delay(3);
				give(player, ItemId.SILVERLIGHT.id(), 1);
				player.updateQuestStage(this, 4);
				break;
			case SirPrysin.GYPSY:
				npcsay(player, n, "Gypsy Aris?  Is she still alive?",
					"I remember her from when I was pretty young",
					"Well what do you need to talk to me about?");
				int choice = multi(player, n,
					"I need to find Silverlight",
					"Yes, she is still alive");
				if (choice == 0) {
				} else if (choice == 1) {
					npcsay(player, n,
						"I would have thought she would have died by now",
						"She was pretty old, when I was a lad",
						"Anyway, what can I do for you?");
					say(player, n, "I need to find Silverlight");
				} else break;
				sirPrysinDialogue(player, n, SirPrysin.SILVERLIGHT);
				break;
			case SirPrysin.SILVERLIGHT:
				npcsay(player, n, "What do you need to find that for?");
				say(player, n, "I need it to fight Delrith");
				npcsay(player, n, "Delrith?", "I thought the world was rid of him");
				choice = multi(player, n,
					"Well, the gypsy's crystal ball seems to think otherwise",
					"He's back and unfortunatly I've got to deal with him");
				if (choice == 0) {
					npcsay(player, n, "Well if the ball says so, I'd better help you");
				} else if (choice == 1) {
					npcsay(player, n, "You don't look up to much",
						"I suppose Silverlight may be good enough to carry you through though");
				} else break;
				npcsay(player, n, "The problem is getting silverlight");
				say(player, n, "You mean you don't have it?");
				npcsay(player, n, "Oh I do have it", "But it is so powerful",
					"That I have put it in a special box",
					"Which needs three different keys to open it",
					"That way, it won't fall into the wrong hands");
				choice = multi(player, n, "So give me the keys",
					"And why is this a problem?");
				if (choice == 0) {
					npcsay(player, n, "Um", "Well, It's not so easy");
				} else if (choice == 1) {
				} else break;
				sirPrysinDialogue(player, n, SirPrysin.PROBLEM);
				break;
			case SirPrysin.PROBLEM:
				npcsay(player, n,
					"I kept one of the keys", "I gave the other two",
					"To other people for safe keeping", "One I gave to Rovin",
					"who is captain of the palace guard",
					"I gave the other to the wizard Traiborn");
				player.updateQuestStage(this, 2);
				int problemMenu = multi(player, n,
					"Can you give me your key?",
					"Where can I find Captain Rovin?",
					"Where does the wizard live?");
				if (problemMenu == 0) {
					sirPrysinDialogue(player, n, SirPrysin.YOUR_KEY);
				} else if (problemMenu == 1) {
					sirPrysinDialogue(player, n, SirPrysin.ROVIN);
				} else if (problemMenu == 2) {
					sirPrysinDialogue(player, n, SirPrysin.WIZARD);
				}
				break;
			case SirPrysin.YOUR_KEY:
				npcsay(player, n, "Um", "Ah", "Well there's a problem there as well",
					"I managed to drop the key in the drain",
					"Just outside the palace kitchen",
					"It is just inside and I can't reach it");
				int yourKey = multi(player, n, false, //do not send over
					"So what does the drain lead to?",
					"Where can I find Captain Rovin?",
					"Where does the wizard live?");
				if (yourKey == 0) {
					say(player, n, "So what does the drain connect to?");
					sirPrysinDialogue(player, n, SirPrysin.DRAIN);
				} else if (yourKey == 1) {
					say(player, n, "Where can I find Captain Rovin?");
					sirPrysinDialogue(player, n, SirPrysin.ROVIN);
				} else if (yourKey == 2) {
					say(player, n, "Where does the wizard live?");
					sirPrysinDialogue(player, n, SirPrysin.WIZARD);
				}
				break;
			case SirPrysin.DRAIN:
				npcsay(player, n, "It is the drain",
					"For the drainpipe running from the sink in the kitchen",
					"Down to the palace sewers");
				int drainMenu = multi(player, n,
					"Where can I find Captain Rovin?",
					"Where does the wizard live?",
					"Well I'd better go key hunting");
				if (drainMenu == 0) {
					sirPrysinDialogue(player, n, SirPrysin.ROVIN);
				} else if (drainMenu == 1) {
					sirPrysinDialogue(player, n, SirPrysin.WIZARD);
				} else if (drainMenu == 2) {
					sirPrysinDialogue(player, n, SirPrysin.Stage2);
				}
				break;
			case SirPrysin.Stage2:
				npcsay(player, n, "Ok goodbye");
				break;
			case SirPrysin.ROVIN:
				npcsay(player, n,
					"Captain Rovin lives at the top of the guards quarters",
					"in the northwest wing of this palace");
				int rovinMenu = multi(player, n,
					"Can you give me your key", "Where does the wizard live?",
					"Well I'd better go key hunting");
				if (rovinMenu == 0) {
					sirPrysinDialogue(player, n, SirPrysin.YOUR_KEY);
				} else if (rovinMenu == 1) {
					sirPrysinDialogue(player, n, SirPrysin.WIZARD);
				} else if (rovinMenu == 2) {
					sirPrysinDialogue(player, n, SirPrysin.Stage2);
				}
				break;
			case SirPrysin.WIZARD:
				npcsay(player, n, "Wizard Traiborn?",
					"He is one of the wizards who lives in the tower",
					"On the little island just off the south coast",
					"I believe his quarters are on the first floor of the tower");
				int wizardMenu = multi(player, n,
					"Can you give me your key?",
					"Where can I find Captain Rovin?",
					"Well I'd better go key hunting");
				if (wizardMenu == 0) {
					sirPrysinDialogue(player, n, SirPrysin.YOUR_KEY);
				} else if (wizardMenu == 1) {
					sirPrysinDialogue(player, n, SirPrysin.ROVIN);
				} else if (wizardMenu == 2) {
					sirPrysinDialogue(player, n, SirPrysin.Stage2);
				}
				break;
		}
	}

	private void gypsyDialogue(Player player, Npc n, int conversationID) {
		if (conversationID == -1) {
			switch (player.getQuestStage(this)) {
				case 0:
					gypsyDialogue(player, n, GypsyConversation.INTRO);
					break;
				case 1:
					npcsay(player, n, "Greetings how goes thy quest?");
					say(player, n, "I'm still working on it");
					npcsay(player, n,
						"Well if you need any advice I'm always here young one");
					int choice = multi(player, n,
						"What is the magical incantation?",
						"Where can I find Silverlight?",
						"Well I'd better press on with it",
						"Stop calling me that");
					if (choice == 0) {
						gypsyDialogue(player, n, GypsyConversation.INCANTATION);
					} else if (choice == 1) {
						gypsyDialogue(player, n, GypsyConversation.SILVERLIGHT);
					} else if (choice == 2) {
						npcsay(player, n, "See you anon");
					} else if (choice == 4) {
						npcsay(player, n, "In the scheme of things you are very young");
						int choice2 = multi(player, n,
							"Ok but how old are you",
							"Oh if its in the scheme of things that's ok");
						if (choice2 == 0) {
							gypsyDialogue(player, n, GypsyConversation.HOW_OLD_TWO);
						} else if (choice2 == 1) {
							npcsay(player, n, "You show wisdom for one so young");
						}
					}
					break;
				case 2:
				case 3:
				case 4:
					npcsay(player, n, "How goes the quest?");
					if (!player.getCarriedItems().hasCatalogID(ItemId.SILVERLIGHT.id())) {
						say(player, n,
							"I found Sir Prysin. Unfortunately, I haven't got the sword yet");
						say(player, n, "He's made it complicated for me!");
						npcsay(player, n, "Ok, hurry, we haven't much time");
					} else {
						say(player, n,
							"I have the sword, now. I just need to kill the demon I think");
						npcsay(player, n, "Yep, that's right");
					}
					break;
				case -1:
					npcsay(player, n, "Greetings young one", "You're a hero now",
						"That was a good bit of demonslaying");
					int choice3 = multi(player, n,
						"How do you know I killed it?", "Thanks",
						"Stop calling me that");
					if (choice3 == 0) {
						npcsay(player, n, "You forget",
							"I'm good at knowing these things");
					} else if (choice3 == 2) {
						npcsay(player, n, "In the scheme of things you are very young");
						int choice2 = multi(player, n, false, //do not send over
							"Ok but how old are you",
							"Oh if its in the scheme of things that's ok");
						if (choice2 == 0) {
							say(player, n, "Ok but how old are you?");
							// goes to count the number of legs ...
							gypsyDialogue(player, n, GypsyConversation.HOW_OLD_TWO);
						} else if (choice2 == 1) {
							say(player, n, "Oh if its in the scheme of things that's ok");
							npcsay(player, n, "You show wisdom for one so young");
						}
					}
					break;
			}
		}
		switch (conversationID) {
			case GypsyConversation.INTRO:// Intro
				npcsay(player, n, "Hello, young one",
					"Cross my palm with silver and the future will be revealed to you");
				int introduceMenu = multi(player, n, "Ok, here you go",
					"Who are you calling young one?!",
					"No, I don't believe in that stuff");
				if (introduceMenu == 0) {
					gypsyDialogue(player, n, GypsyConversation.QUEST_START);
				} else if (introduceMenu == 1) {
					npcsay(player, n, "You have been on this world",
						"A relatively short time", "At least compared to me",
						"So do you want your fortune told or not?");
					int choice = multi(player, n, "Yes please",
						"No, I don't believe in that stuff",
						"Ooh how old are you then?");
					if (choice == 0) {
						gypsyDialogue(player, n, GypsyConversation.YES_PLEASE);
					} else if (choice == 1) {
						npcsay(player, n, "Ok suit yourself");
					} else if (choice == 2) {
						gypsyDialogue(player, n, GypsyConversation.HOW_OLD);
					}
				} else if (introduceMenu == 2) {
					npcsay(player, n, "Ok suit yourself");
				}
				break;
			case GypsyConversation.HOW_OLD:
				npcsay(player, n, "Older than you imagine");
				int choice = multi(player, n, false, //do not send over
					"Believe me, I have a good imagination",
					"How do you know how old I think you are?",
					"Oh pretty old then");
				if (choice == 0) {
					// Believe me, I have a good imagination
					say(player, n, "Believe me, I have a good imagination");
					npcsay(player, n, "You seem like just the sort of person",
						"Who would want their fortune told then");
					choice = multi(player, n, "No, I don't believe in that stuff",
						"Yes please");
					if (choice == 0) {
						// No I don't believe in that stuff
						npcsay(player, n, "Ok suit yourself");
					} else if (choice == 1) {
						// Yes please
						gypsyDialogue(player, n, GypsyConversation.YES_PLEASE);
					}
				} else if (choice == 1) {
					// How do you know how old I think you are?
					say(player, n, "How do you know how old I think you are");
					npcsay(player, n, "I have the power to know",
						"Just as I have the power to foresee the future");
					choice = multi(player, n, false, //do not send over
						"Ok what am I thinking now?",
						"Ok but how old are you?",
						"Go on then, what's my future?");
					if (choice == 0) {
						say(player, n, "Ok what am I thinking now?");
						npcsay(player, n, "You are thinking that I'll never guess what you are thinking");
					} else if (choice == 1) {
						say(player, n, "Ok but how old are you?");
						gypsyDialogue(player, n, GypsyConversation.HOW_OLD_TWO);
					} else if (choice == 2) {
						say(player, n, "Go on then what's my future");
						npcsay(player, n, "Cross my palm with silver and I'll tell you");
						int crosspalm = multi(player, n, false, //do not send over
							"Ok here you go", "Oh you want me to pay. No thanks");
						if (crosspalm == 0) {
							say(player, n, "Ok, here you go");
							gypsyDialogue(player, n, GypsyConversation.QUEST_START);
						} else if (crosspalm == 1) {
							say(player, n, "Oh you want me to pay. No thanks");
							npcsay(player, n, "Go away then");
						}
					}
				} else if (choice == 2) {
					// Oh pretty old then
					say(player, n, "Oh pretty old then");
					npcsay(player, n, "Yes I'm old", "Don't rub it in");
				}
				break;
			case GypsyConversation.HOW_OLD_TWO:
				npcsay(player, n,
					"Count the number of legs of the chairs in the blue moon inn",
					"And multiply that number by seven");
				say(player, n, "Errr yeah whatever");
				break;
			case GypsyConversation.YES_PLEASE:
				npcsay(player, n,
					"Cross my palm with silver then");
				int crosspalm = multi(player, n, false, //do not send over
					"Ok here you go", "Oh you want me to pay. No thanks");
				if (crosspalm == 0) {
					say(player, n, "Ok, here you go");
					gypsyDialogue(player, n, GypsyConversation.QUEST_START);
				} else if (crosspalm == 1) {
					say(player, n, "Oh you want me to pay. No thanks");
					npcsay(player, n, "Go away then");
				}
				break;
			case GypsyConversation.QUEST_START:// Quest Start
				if (player.getCarriedItems().hasCatalogID(ItemId.COINS.id()))
					player.getCarriedItems().remove(new Item(ItemId.COINS.id()));
				else {
					say(player, n, "Oh dear. I don't have any money");
					break;
				}
				npcsay(player, n, "Come closer",
					"And listen carefully to what the future holds for you",
					"As I peer into the swirling mists of the crystal ball",
					"I can see images forming", "I can see you",
					"You are holding a very impressive looking sword",
					"I'm sure I recognise that sword",
					"There is a big dark shadow appearing now", "Aaargh");
				int aargh = multi(player, n,
					"Very interesting what does the Aaargh bit mean?",
					"Are you alright?", "Aaargh?");
				if (aargh > -1) {
					npcsay(player, n, "Aaargh its Delrith", "Delrith is coming");
					choice = multi(player, n, "Who's Delrith?",
						"Get a grip!");
					if (choice == 0) {
						gypsyDialogue(player, n, GypsyConversation.WHO_IS_DELRITH);
					} else if (choice == 1) {
						npcsay(player, n, "Sorry. I didn't expect to see Delrith",
							"I had to break away quickly in case he detected me");
						say(player, n, "Who's Delrith?");
						gypsyDialogue(player, n, GypsyConversation.WHO_IS_DELRITH);
					}
					player.updateQuestStage(this, 1);
				}
				break;

			case GypsyConversation.WHO_IS_DELRITH:
				npcsay(player,
					n,
					"Delrith",
					"Delrtih is a powerfull demon",
					"Oh I really hope he didn't see me",
					"Looking at him through my crystal ball",
					"He tried to destroy this city 150 years ago",
					"He was stopped just in time, by the great hero Wally",
					"Wally managed to trap the demon",
					"In the stone circle just south of this city",
					"Using his magic sword silverlight",
					"Ye Gods",
					"Silverlight was the sword you were holding in the ball vision",
					"You are the one destined to try and stop the demon this time");
				choice = multi(player, n, false, //do not send over
					"How am I meant to fight a demon who can destroy cities?",
					"Ok where is he? I'll kill him for you",
					"Wally doesn't sound like a very heroic name");
				if (choice == 0) {
					say(player, n, "How am I meant to fight a demon who can destroy cities");
					npcsay(player, n, "I admit it won't be easy");
					gypsyDialogue(player, n, GypsyConversation.DEFEATING_DELRITH);
				} else if (choice == 1) {
					say(player, n, "Ok where is he? I'll kill him for you");
					npcsay(player, n, "Well you can't just go and fight",
						"He can't be harmed by ordinary weapons");
					gypsyDialogue(player, n, GypsyConversation.DEFEATING_DELRITH);
				} else if (choice == 2) {
					say(player, n, "Wally doesn't sound a very heroic name");
					gypsyDialogue(player, n, GypsyConversation.WALLY);
				}
				break;
			case GypsyConversation.DEFEATING_DELRITH:
				npcsay(player,
					n,
					"Wally managed to arrive at the stone circle",
					"Just as Delrith was summoned by a cult of chaos druids",
					"By reciting the correct magical incantation",
					"and thrusting Silverlight into Delrith, while he was newly summoned",
					"Wally was able to imprison Delrith",
					"in the stone block in the centre of the circle",
					"Delrith will come forth from the stone circle again",
					"I would imagine an evil sorcerer is already starting on the rituals",
					"To summon Delrith as we speak");
				gypsyDialogue(player, n, GypsyConversation.INCANTATION_SILVERLIGHT_MENU);
				break;

			case GypsyConversation.INCANTATION:
				npcsay(player, n, "Oh yes let me think a second");
				mes("The gypsy is thinking");
				delay(3);
				npcsay(player, n, "Alright I've got it now I think", "It goes",
					"Carlem", "Aber", "Camerinthum", "Purchai", "Gabindo",
					"Have you got that?");
				say(player, n, "I think so, yes");
				choice = multi(player, n,
					"Ok thanks. I'll do my best to stop the Demon",
					"Where can I find Silverlight?");
				if (choice == 0) {
					gypsyDialogue(player, n, GypsyConversation.ILL_DO_MY_BEST);
				} else if (choice == 1) {
					gypsyDialogue(player, n, GypsyConversation.SILVERLIGHT);
				}
				break;

			case GypsyConversation.INCANTATION_SILVERLIGHT_MENU:
				choice = multi(player, n,
					"What is the magical incantation?",
					"Where can I find Silverlight?");
				if (choice == 0) {
					npcsay(player, n, "Oh yes let me think a second");
					mes("The gypsy is thinking");
					delay(3);
					npcsay(player, n, "Alright I've got it now I think", "It goes",
						"Carlem", "Aber", "Camerinthum", "Purchai", "Gabindo",
						"Have you got that?");
					say(player, n, "I think so, yes");
					choice = multi(player, n,
						"Ok thanks. I'll do my best to stop the Demon",
						"Where can I find Silverlight?");
					if (choice == 0) {
						gypsyDialogue(player, n, GypsyConversation.ILL_DO_MY_BEST);
					} else if (choice == 1) {
						gypsyDialogue(player, n, GypsyConversation.SILVERLIGHT);
					}
				} else if (choice == 1) {
					npcsay(player,
						n,
						"Silverlight has been passed down through Wally's descendents",
						"I believe it is currently in the care of one of the king's knights",
						"called Sir Prysin",
						"He shouldn't be to hard to find he lives in the royal palace in this city",
						"Tell him Gypsy Aris sent you");
					choice = multi(player, n,
						"Ok thanks. I'll do my best to stop the Demon",
						"What is the magical incantation?");
					if (choice == 0) {
						gypsyDialogue(player, n, GypsyConversation.ILL_DO_MY_BEST);
					} else if (choice == 1) {
						gypsyDialogue(player, n, GypsyConversation.INCANTATION);
					}
				}
				break;

			case GypsyConversation.WALLY:
				npcsay(player,
					n,
					"Yes I know. Maybe that is why history doesn't remember him",
					"However he was a very great hero.",
					"Who knows how much pain and suffering",
					"Delrith would have brought forth without Wally to stop him",
					"It looks like you are going to need to perform similar heroics");
				choice = multi(player, n,
					"How am I meant to fight a demon who can destroy cities?",
					"Ok where is he? I'll kill him for you");
				if (choice == 0) {
					npcsay(player, n, "I admit it won't be easy");
					gypsyDialogue(player, n, GypsyConversation.DEFEATING_DELRITH);
				} else if (choice == 1) {
					npcsay(player, n, "Well you can't just go and fight",
						"He can't be harmed by ordinary weapons");
					gypsyDialogue(player, n, GypsyConversation.DEFEATING_DELRITH);
				}
				break;

			case GypsyConversation.SILVERLIGHT:
				npcsay(player,
					n,
					"Silverlight has been passed down through Wally's descendents",
					"I believe it is currently in the care of one of the king's knights",
					"called Sir Prysin",
					"He shouldn't be to hard to find he lives in the royal palace in this city",
					"Tell him Gypsy Aris sent you");
				choice = multi(player, n,
					"Ok thanks. I'll do my best to stop the Demon",
					"What is the magical incantation?");
				if (choice == 0) {
					gypsyDialogue(player, n, GypsyConversation.ILL_DO_MY_BEST);
				} else if (choice == 1) {
					gypsyDialogue(player, n, GypsyConversation.INCANTATION);
				}
				break;
			case GypsyConversation.ILL_DO_MY_BEST:
				npcsay(player, n, "Good luck, may Guthix be with you");
				break;
		}
	}

	@Override
	public void onAttackNpc(Player player, Npc affectedmob) {
		if (affectedmob.getID() == NpcId.DELRITH.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
				case 3:
					say(player, null, "I'd rather not. He looks scary");
				case 4:
					if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.SILVERLIGHT.id())) {
						say(player, null, "Maybe I'd better wield silverlight first");
					} else {
						// silverlight effect shared in its own file
						affectedmob.getSkills().setLevel(Skill.HITS.id(), affectedmob.getDef().getHits());
						player.resetMenuHandler();
						player.setOption(-1);
					}
					break;
				case -1:
					player.message("You've already done that quest");
			}
		}
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		if (n.getID() == NpcId.DELRITH.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
				case 3:
					return true;
				case 4:
					if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.SILVERLIGHT.id())) {
						return true;
					}
					break;
				case -1:
					return true;
			}
		}
		return false;
	}

	public void onKillNpc(Player player, Npc npc) {
		npc.getSkills().setLevel(Skill.HITS.id(), npc.getDef().getHits());

		if (player.getMenuHandler() == null
			&& player.getCarriedItems().getEquipment().hasEquipped(ItemId.SILVERLIGHT.id())
			&& player.inCombat()) {

			mes("As you strike Delrith a vortex opens up");
			delay(3);
			if (!player.inCombat()) {
				npc.killed = false;
				return;
			}
			say(player, npc, "Now what was that incantation again");
			int choice = multi(player, npc,
				"Carlem Gabindo Purchai Zaree Camerinthum",
				"Purchai Zaree Gabindo Carlem Camerinthum",
				"Purchai Camerinthum Aber Gabindo Carlem",
				"Carlem Aber Camerinthum Purchai Gabindo");
			if (choice == -1) {
				npc.killed = false;
			} else if (choice == 3) {
				mes("Delrith is sucked back into the dark dimension from which he came");
				delay(2);
				npc.remove();
				if (player.getQuestStage(Quests.DEMON_SLAYER) != -1) {
					//remove flags in case they are present with drop trick
					player.getCache().remove("done_bone_task");
					player.getCache().remove("traiborn_bones");
					player.sendQuestComplete(getQuestId());
				}
				return;
			} else {
				mes("As you chant, Delrith is sucked towards the vortex");
				delay(2);
				mes("Suddenly the vortex closes");
				delay(2);
				player.message("And Delrith is still here");
				player.message("That was the wrong incantation");
			}
		}
		npc.killed = false;
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.DELRITH.id();
	}

	@Override
	public boolean blockPlayerRangeNpc(Player player, Npc n) {
		return n.getID() == NpcId.DELRITH.id();
	}

	@Override
	public void onPlayerRangeNpc(Player player, Npc n) {
		if (n.getID() == NpcId.DELRITH.id()) {
			player.message("You cannot attack Delrith without the silverlight sword");
		}
	}

	class GypsyConversation {
		static final int INTRO = 0;
		static final int YES_PLEASE = 1;
		static final int QUEST_START = 2;
		static final int DEFEATING_DELRITH = 3;
		static final int WHO_IS_DELRITH = 4;
		static final int WALLY = 5;
		static final int INCANTATION_SILVERLIGHT_MENU = 6;
		static final int SILVERLIGHT = 7;
		static final int ILL_DO_MY_BEST = 8;
		static final int INCANTATION = 9;
		static final int HOW_OLD = 10;
		static final int HOW_OLD_TWO = 11;
	}

	class SirPrysin {
		static final int GOT_THEM = 9;
		static final int ALIVE = 8;
		static final int DRAIN = 7;
		static final int PROBLEM = 6;
		static final int WIZARD = 5;
		static final int ROVIN = 4;
		static final int YOUR_KEY = 3;
		static final int Stage2 = 2;
		static final int SILVERLIGHT = 1;
		static final int GYPSY = 0;
	}

	class Traiborn {
		static final int TEACHME = 0;
		static final int THINGWUT = 1;
		static final int CALLEDME = 2;
		static final int MASHEM = 3;
		static final int BEOFF = 4;
		static final int MAD = 5;
		static final int KNOCKING = 6;
		static final int BONES = 7;
	}

	class CaptainRovin {
		static final int PALACE = 7;
		static final int RECRUIT = 0;
		static final int DELIVERY = 1;
		static final int I_FORGOT = 2;
		static final int DEMON_INVASION = 3;
		static final int IMPORTANT = 4;
		static final int KING = 5;
		static final int SURGERY = 6;
	}
}

