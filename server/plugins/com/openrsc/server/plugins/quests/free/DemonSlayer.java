package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class DemonSlayer implements QuestInterface,
	PlayerAttackNpcExecutiveListener, PlayerKilledNpcExecutiveListener,
	PlayerKilledNpcListener, TalkToNpcListener, ObjectActionListener,
	ObjectActionExecutiveListener, TalkToNpcExecutiveListener,
	InvUseOnObjectListener, InvUseOnObjectExecutiveListener, PlayerRangeNpcListener, PlayerRangeNpcExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.DEMON_SLAYER;
	}

	@Override
	public String getQuestName() {
		return "Demon slayer";
	}
	
	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player p) {
		p.message("You have completed the demonslayer quest");
		incQuestReward(p, Quests.questData.get(Quests.DEMON_SLAYER), true);
		p.message("@gre@You haved gained 3 quest points!");
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
									   Player player) {
		return obj.getID() == 77;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
		if (obj.getID() == 77) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
					message(player, "I have no reason to do that.");
					break;
				case 2:
				case 3:
				case 4:
				case -1:
					//even post-quest was the same thing
					if (item.getID() == ItemId.BUCKET_OF_WATER.id()) {
						message(player,
							"You pour the liquid down the drain");
						message(player, "Ok I think I've washed the key down into the sewer",
							"I'd better go down and get it before someone else finds it");
						player.getInventory().replace(ItemId.BUCKET_OF_WATER.id(), ItemId.BUCKET.id());
						World.getWorld().registerItem(
							new GroundItem(ItemId.SILVERLIGHT_KEY_3.id(), 117, 3294, 1, player));
					}
					break;
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] { NpcId.GYPSY.id(), NpcId.SIR_PRYSIN.id(), 
				NpcId.TRAIBORN_THE_WIZARD.id(), NpcId.CAPTAIN_ROVIN.id() }, n.getID());
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
									 Player player) {
		return obj.getID() == 77;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if (obj.getID() == 77 && obj.getY() == 461) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
					message(player, "I can see a key but can't quite reach it...");
					break;
				case 2:
				case 3:
				case 4:
					message(player, "This is the drainpipe",
						"Running from the kitchen sink to the sewer",
						"I can see a key just inside the drain",
						"That must be the key Sir Prysin dropped",
						"I don't seem to be able to quite reach it",
						"It's stuck part way down",
						"I wonder if I can dislodge it somehow",
						"And knock it down into the sewers");
					break;
				case -1:
					message(player, "This is the drainpipe",
						"Running from the kitchen sink to the sewer",
						"I can see a key just inside the drain",
						"I don't seem to be able to quite reach it",
						"It's stuck part way down",
						"I wonder if I can dislodge it somehow",
						"And knock it down into the sewers");
					break;
			}
		}
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GYPSY.id()) {
			gypsyDialogue(p, n, -1);
		} else if (n.getID() == NpcId.SIR_PRYSIN.id()) {
			sirPrysinDialogue(p, n, -1);
		} else if (n.getID() == NpcId.TRAIBORN_THE_WIZARD.id()) {
			traibornTheWizDialogue(p, n, -1);
		} else if (n.getID() == NpcId.CAPTAIN_ROVIN.id()) {
			captainRovinDialogue(p, n, -1);
		}
	}

	private void captainRovinDialogue(Player p, Npc n, int cID) {
		int questStage = p.getQuestStage(this);
		if (cID == -1) {
			npcTalk(p, n, "What are you doing up here?",
				"Only the palace guards are allowed up here");
			String[] choices = new String[]{
				"I am one of the palace guards",
				"What about the king?"
			};
			if (questStage >= 2)
				choices = new String[]{"I am one of the palace guards",
					"What about the king?",
					"Yes I know but this important"
				};

			int choice = showMenu(p, n, false, choices); // Do not send choice to client
			if (choice == 0) {
				playerTalk(p, n, "I am one of the palace guard");
				captainRovinDialogue(p, n, CaptainRovin.PALACE);
			} else if (choice == 1) {
				playerTalk(p, n, "What about the king?");
				captainRovinDialogue(p, n, CaptainRovin.KING);
			} else if (choice == 2 && questStage > 1) {
				playerTalk(p, n, "Yes I know but this important");
				captainRovinDialogue(p, n, CaptainRovin.IMPORTANT);
			}
		}

		switch (cID) {
			case CaptainRovin.PALACE:
				npcTalk(p, n, "No, you're not. I know all the palace guard");
				int choice2 = showMenu(p, n, "I'm a new recruit",
					"I've had extensive plastic surgery");
				if (choice2 == 0) {
					captainRovinDialogue(p, n, CaptainRovin.RECRUIT);
				} else if (choice2 == 1) {
					captainRovinDialogue(p, n, CaptainRovin.SURGERY);
				}
				break;

			case CaptainRovin.SURGERY:
				npcTalk(p, n, "What sort of surgery is that?", "Never heard of it",
					"Besides, you look reasonably healthy",
					"Why is this relevant anyway?",
					"You still shouldn't be here");
				break;
			case CaptainRovin.KING:
				playerTalk(p, n, "Surely you'd let him up here?");
				npcTalk(p, n, "Well, yes, I suppose we'd let him up",
					"He doesn't generally want to come up here",
					"But if he did want to", "He could come up",
					"Anyway, you're not the king either",
					"So get out of my sight");
				break;
			case CaptainRovin.IMPORTANT:
				npcTalk(p, n, "Ok, I'm listening", "Tell me what's so important");
				int choice = showMenu(p, n,
					"There's a demon who wants to invade this city",
					"Erm I forgot",
					"The castle has just received it's ale delivery");
				if (choice == 0) {
					captainRovinDialogue(p, n, CaptainRovin.DEMON_INVASION);
				} else if (choice == 1) {
					captainRovinDialogue(p, n, CaptainRovin.I_FORGOT);
				} else if (choice == 2) {
					captainRovinDialogue(p, n, CaptainRovin.DELIVERY);
				}
				break;
			case CaptainRovin.DEMON_INVASION:
				npcTalk(p, n, "Is it a powerful demon?");
				playerTalk(p, n, "Yes, very");
				npcTalk(p, n, "Well as good as the palace guards are",
					"I don't think they're up to taking on a very powerful demon");
				playerTalk(p, n,
					"No no, it's not them who's going to fight the demon",
					"It's me");
				npcTalk(p, n, "What all by yourself?");
				playerTalk(p, n,
					"Well I am going to use the powerful sword silverlight",
					"Which I believe you have one of the keys for");
				npcTalk(p, n, "Yes you're right", "Here you go");
				message(p, "Captain Rovin hands you a key");
				addItem(p, ItemId.SILVERLIGHT_KEY_2.id(), 1);
				break;
			case CaptainRovin.I_FORGOT:
				npcTalk(p, n, "Well it can't be that important then");
				playerTalk(p, n, "How do you know?");
				npcTalk(p, n, "Just go away");
				break;
			case CaptainRovin.DELIVERY:
				npcTalk(p, n, "Now that is important",
					"However, I'm the wrong person to speak to about it",
					"Go talk to the kitchen staff");
				break;
			case CaptainRovin.RECRUIT:
				npcTalk(p, n, "I interview all the new recruits",
					"I'd know if you were one of them");
				playerTalk(p, n, "That blows that story out of the window then");
				npcTalk(p, n, "Get out of my sight");
				break;
		}
	}

	private void traibornTheWizDialogue(Player p, Npc n, int cID) {

		if (cID == -1) {
			switch (p.getQuestStage(this)) {
				case -1:
				case 0:
				case 1:
					npcTalk(p, n, "Ello young thingummywut");
					int choice = showMenu(p, n,
						"Whats a thingummywut?",
						"Teach me to be a mighty and powerful wizard");
					if (choice == 0) {
						traibornTheWizDialogue(p, n, Traiborn.THINGWUT);
						int choice2 = showMenu(p, n,
							"Err you just called me thingummywut",
							"Tell me what they look like and I'll mash 'em");
						if (choice2 == 0) {
							traibornTheWizDialogue(p, n, Traiborn.CALLEDME);
							int choice3 = showMenu(p, n,
								"Err I'd better be off really",
								"They're right, you are mad");
							if (choice3 == 0) {
								traibornTheWizDialogue(p, n, Traiborn.BEOFF);
							} else if (choice3 == 1) {
								traibornTheWizDialogue(p, n, Traiborn.MAD);
							}
						} else if (choice2 == 1) {
							traibornTheWizDialogue(p, n, Traiborn.MASHEM);
						}
					} else if (choice == 1) {
						traibornTheWizDialogue(p, n, Traiborn.TEACHME);
					}
					break;
				//stage of not having obtained silverlight and having obtained it
				case 3:
				case 4:
					//key obtained, present in inventory
					if (p.getInventory().hasItemId(ItemId.SILVERLIGHT_KEY_1.id())) {
						npcTalk(p, n, "Ello young thingummywut");
						int choice3 = showMenu(p, n,
							"Whats a thingummywut?",
							"Teach me to be a mighty and powerful wizard");
						if (choice3 == 0) {
							traibornTheWizDialogue(p, n, Traiborn.THINGWUT);
						} else if (choice3 == 1) {
							traibornTheWizDialogue(p, n, Traiborn.TEACHME);
						}
					}
					//cache of bone task done
					else if (p.getCache().hasKey("done_bone_task")) {
						playerTalk(p, n, "I've lost the key you gave me");
						npcTalk(p, n, "Yes I know", "It was returned to me",
							"If you want it back",
							"you're going to have to collect another 25 sets of bones");
						//player needs to redo bone task
						p.getCache().remove("done_bone_task");
					} else {
						npcTalk(p, n, "How are you doing finding bones?");
						if (p.getInventory().countId(ItemId.BONES.id()) <= 0) {
							playerTalk(p, n, "I haven't got any at the moment");
							npcTalk(p, n, "Never mind. Keep working on it");
							return;
						}
						playerTalk(p, n, "I have some bones");
						npcTalk(p, n, "Give 'em here then");
						int boneCount = 0;
						if (!p.getCache().hasKey("traiborn_bones"))
							p.getCache().set("traiborn_bones", boneCount);
						else
							boneCount = p.getCache().getInt("traiborn_bones");

						while (p.getInventory().countId(ItemId.BONES.id()) > 0) {
							p.getInventory().remove(new Item(ItemId.BONES.id()));
							p.message("You give Traiborn a set of bones");
							boneCount++;
							sleep(600);
							if (boneCount >= 25)
								break;
						}
						p.getCache().set("traiborn_bones", boneCount);
						if (boneCount >= 25) {
							npcTalk(p, n, "Hurrah! That's all 25 sets of bones");
							message(p,
								"Traiborn places the bones in a circle on the floor",
								"Traiborn waves his arms about");
							npcTalk(p, n, "Wings of dark and colour too",
								"Spreading in the morning dew");
							message(p, "The wizard waves his arms some more");
							npcTalk(p, n, "Locked away I have a key",
								"Return it now unto me");
							message(p, "Traiborn smiles",
								"Traiborn hands you a key");
							p.getInventory().add(new Item(ItemId.SILVERLIGHT_KEY_1.id(), 1));
							p.getCache().store("done_bone_task", true);
							playerTalk(p, n, "Thank you very much");
							npcTalk(p, n,
								"Not a problem for a friend of sir what's-his-face");
							p.getCache().remove("traiborn_bones");
						} else {
							playerTalk(p, n, "That's all of them");
							npcTalk(p, n, "I still need more");
							playerTalk(p, n, "Ok, I'll look for some more");
						}
					}
					break;
				case 2:
					npcTalk(p, n, "Ello young thingummywut");
					int choice4;
					if (!p.getInventory().hasItemId(ItemId.SILVERLIGHT_KEY_1.id())) {
						choice4 = showMenu(p, n,
							"Whats a thingummywut?",
							"Teach me to be a mighty and powerful wizard",
							"I need to get a key given to you by Sir Prysin");
					} else {
						choice4 = showMenu(p, n,
							"Whats a thingummywut?",
							"Teach me to be a mighty and powerful wizard");
					}
					if (choice4 == 0) {
						traibornTheWizDialogue(p, n, Traiborn.THINGWUT);
					} else if (choice4 == 1) {
						traibornTheWizDialogue(p, n, Traiborn.TEACHME);
					} else if (choice4 == 2) {
						npcTalk(p, n, "Sir Prysin?  Who's that?",
							"What would I want his key for?");
						int choice5 = showMenu(p, n,
							"He told me you were looking after it for him",
							"He's one of the king's knights",
							"Well, have you got any keys knocking around?");
						if (choice5 == 0) {
							npcTalk(p,
								n,
								"That wasn't very clever of him",
								"I'd lose my head if it wasn't screwed on properly",
								"Go tell him to find someone else",
								"to look after his valuables in future");
							int choice6 = showMenu(p, n,
								"Ok, I'll go and tell him that",
								"Well, have you got any keys knocking around?");
							if (choice6 == 0) {
								npcTalk(p, n, "Oh that's great",
									"If it wouldn't be too much trouble");
								int choice7 = showMenu(p, n,
									"Err I'd better be off really",
									"Well, have you got any keys knocking around?");
								if (choice7 == 0) {
									traibornTheWizDialogue(p, n, Traiborn.BEOFF);
								} else if (choice7 == 1) {
									traibornTheWizDialogue(p, n, Traiborn.KNOCKING);
								}
							} else if (choice6 == 1) {
								traibornTheWizDialogue(p, n, Traiborn.KNOCKING);
							}
						} else if (choice5 == 1) {
							npcTalk(p, n, "Say, I remember a knight with a key",
								"He had nice shoes",
								"and didn't like my homemade spinach rolls",
								"Would you like a spinach roll?");
							int choice8 = showMenu(p, n,
								"Yes Please",
								"Just tell me if you have the key");
							if (choice8 == 0) {
								message(p,
									"Traiborn digs around in the pockets of his robes",
									"Traiborn hands you a spinach roll");
								addItem(p, ItemId.SPINACH_ROLL.id(), 1);
								playerTalk(p, n, "Thank you very much");
								int choice9 = showMenu(p, n,
									"Err I'd better be off really",
									"Well, have you got any keys knocking around?");
								if (choice9 == 0) {
									traibornTheWizDialogue(p, n, Traiborn.BEOFF);
								} else if (choice9 == 1) {
									traibornTheWizDialogue(p, n, Traiborn.KNOCKING);
								}
							} else if (choice8 == 1) {
								traibornTheWizDialogue(p, n, Traiborn.KNOCKING);
							}
						} else if (choice5 == 2) {
							traibornTheWizDialogue(p, n, Traiborn.KNOCKING);
						}
					}
					break;
			}
		}

		switch (cID) {
			case Traiborn.TEACHME:
				npcTalk(p, n, "Wizard, Eh?",
					"You don't want any truck with that sort",
					"They're not to be trusted",
					"That's what I've heard anyways");
				int choice4 = showMenu(p, n, false,
					"So aren't you a wizard?",
					"Oh I'd better stop talking to you then"); // Don't send to client
				if (choice4 == 0) {
					playerTalk(p, n, "So aren't you a wizard");
					npcTalk(p, n, "How dare you?", "Of course I'm a wizard",
						"Now don't be so cheeky or I'll turn you into a frog");
				} else if (choice4 == 1) {
					playerTalk(p, n, "Oh I'd better stop talking to you then");
					npcTalk(p, n, "Cheerio then", "Was nice chatting to you");
				}
				break;
			case Traiborn.THINGWUT:
				npcTalk(p, n, "A thingummywut?", "Where? , Where?",
					"Those pesky thingummywuts", "They get everywhere",
					"They leave a terrible mess too");
				break;
			case Traiborn.CALLEDME:
				npcTalk(p, n, "You're a thingummywut?",
					"I've never seen one up close before",
					"They said I was mad", "Now you are my proof",
					"There ARE thingummywuts in this tower",
					"Now where can I find a cage big enough to keep you?");
				break;
			case Traiborn.MASHEM:
				npcTalk(p, n, "Don't be ridiculous", "No-one has ever seen one",
					"They're invisible", "Or a myth",
					"Or a figment of my imagination",
					"Can't remember which right now");
				break;
			case Traiborn.BEOFF:
				npcTalk(p, n, "Oh ok have a good time", "and watch out for sheep!",
					"They're more cunning than they look");
				break;
			case Traiborn.MAD:
				npcTalk(p, n, "That's a pity",
					"I thought maybe they were winding me up");
				break;
			case Traiborn.KNOCKING:
				npcTalk(p, n, "Now you come to mention it - yes I do have a key",
					"Its in my special closet of valuable stuff",
					"Now how do I get into that?");
				message(p, "The wizard scratches his head");
				npcTalk(p, n, "I sealed it using one of my magic rituals",
					"so it would make sense that another ritual",
					"Would open it again");
				message(p, "The wizard beams");
				playerTalk(p, n, "So do you know what ritual to use?");
				npcTalk(p, n, "Let me think a second");
				sleep(800);
				npcTalk(p, n, "Yes a simple drazier style ritual should suffice",
					"Hmm",
					"Main problem with that is I'll need 25 sets of bones",
					"Now where am I going to get hold of something like that");
				int choices = showMenu(p, n,
					"Hmm, that's too bad. I really need that key",
					"I'll get the bones for you");
				if (choices == 0) {
					npcTalk(p, n, "Ah well sorry I couldn't be any more help");
				} else if (choices == 1) {
					traibornTheWizDialogue(p, n, Traiborn.BONES);
				}
				break;
			case Traiborn.BONES:
				npcTalk(p, n, "Ooh that would very good of you");
				playerTalk(p, n, "Ok I'll speak to you when I've got some bones");
				p.updateQuestStage(this, 3);
				break;
		}
	}

	private void sirPrysinDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			int questStage = p.getQuestStage(this);
			int choice;
			switch (questStage) {
				case 0:
				case 1:
					npcTalk(p, n, "Hello, who are you");
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

					choice = showMenu(p, n, choices);
					if (choice == 0) {
						npcTalk(p, n, "I am Sir Prysin",
							"A bold and famous knight of the realm");
					} else if (choice == 1) {
						npcTalk(p, n, "Well I've never met you before");
					} else if (choice == 2 && questStage > 0) {
						sirPrysinDialogue(p, n, SirPrysin.GYPSY);
					}
					break;
				case 2:
				case 3:
				case 4:
					//if silverlight is lost, player needs to regain keys
					if (!p.getInventory().hasItemId(ItemId.SILVERLIGHT.id())) {
						npcTalk(p, n, "So how are you doing with getting the keys?");
						if (p.getInventory().hasItemId(ItemId.SILVERLIGHT_KEY_2.id())
							&& p.getInventory().hasItemId(ItemId.SILVERLIGHT_KEY_1.id())
							&& p.getInventory().hasItemId(ItemId.SILVERLIGHT_KEY_3.id())) {
							playerTalk(p, n, "I've got them all");
							sirPrysinDialogue(p, n, SirPrysin.GOT_THEM);
							return;
						} else if (!p.getInventory().hasItemId(ItemId.SILVERLIGHT_KEY_2.id())
							&& !p.getInventory().hasItemId(ItemId.SILVERLIGHT_KEY_1.id())
							&& !p.getInventory().hasItemId(ItemId.SILVERLIGHT_KEY_3.id())) {
							playerTalk(p, n, "I've not found any of them yet");
						} else {
							playerTalk(p, n, "I've made a start");
						}
						if (p.getInventory().hasItemId(ItemId.SILVERLIGHT_KEY_2.id())) {
							playerTalk(p, n, "I've got the key off Wizard Traiborn");
						}
						if (p.getInventory().hasItemId(ItemId.SILVERLIGHT_KEY_1.id())) {
							playerTalk(p, n, "I've got the key off Captain Rovin");
						}
						if (p.getInventory().hasItemId(ItemId.SILVERLIGHT_KEY_3.id())) {
							playerTalk(p, n,
								"I've got the key You dropped down the drain");
						}
						choice = showMenu(p, n, false,
							"Can you remind me where all the keys were again?",
							"I'm still looking");
						if (choice == 0) {
							playerTalk(p, n, "Can you remind me where all the keys were again");
							npcTalk(p, n, "I kept one of the keys", "I gave the other two",
								"To other people for safe keeping", "One I gave to Rovin",
								"who is captain of the palace guard", "I gave the other to the wizard Traiborn");
							choice = showMenu(p, n,
								"Can you give me your key?",
								"Where can I find Captain Rovin?",
								"Where does the wizard live?");
							if (choice == 0) {
								sirPrysinDialogue(p, n, SirPrysin.YOUR_KEY);
							} else if (choice == 1) {
								sirPrysinDialogue(p, n, SirPrysin.ROVIN);
							} else if (choice == 2) {
								sirPrysinDialogue(p, n, SirPrysin.WIZARD);
							}
						} else if (choice == 1) {
							playerTalk(p, n, "I'm still looking");
							npcTalk(p, n, "Ok, tell me when you've got them all");
						}
					} else {
						npcTalk(p, n, "You sorted that demon yet?");
						playerTalk(p, n, "No, not yet");
						npcTalk(p, n, "Well get on with it",
							"He'll be pretty powerful when he gets to full strength");
					}
					break;
				case -1:
					npcTalk(p, n,
						"Hello. I've heard you stopped the demon well done");
					playerTalk(p, n, "Yes, that's right");
					npcTalk(p, n, "A good job well done then");
					playerTalk(p, n, "Thank you");
					break;
			}
		}
		switch (cID) {
			case SirPrysin.GOT_THEM:
				npcTalk(p, n, "Excellent. Now I can give you Silverlight");
				message(p, "You give all three keys to Sir Prysin");
				removeItem(p, ItemId.SILVERLIGHT_KEY_1.id(), 1);
				removeItem(p, ItemId.SILVERLIGHT_KEY_2.id(), 1);
				removeItem(p, ItemId.SILVERLIGHT_KEY_3.id(), 1);
				message(p, "Sir Prysin unlocks a long thin box",
					"Prysin hands you an impressive looking sword");
				addItem(p, ItemId.SILVERLIGHT.id(), 1);
				p.updateQuestStage(this, 4);
				break;
			case SirPrysin.GYPSY:
				npcTalk(p, n, "Gypsy Aris?  Is she still alive?",
					"I remember her from when I was pretty young",
					"Well what do you need to talk to me about?");
				int choice = showMenu(p, n,
					"I need to find Silverlight",
					"Yes, she is still alive");
				if (choice == 0) {
				} else if (choice == 1) {
					npcTalk(p, n,
						"I would have thought she would have died by now",
						"She was pretty old, when I was a lad",
						"Anyway, what can I do for you?");
					playerTalk(p, n, "I need to find Silverlight");
				} else break;
				sirPrysinDialogue(p, n, SirPrysin.SILVERLIGHT);
				break;
			case SirPrysin.SILVERLIGHT:
				npcTalk(p, n, "What do you need to find that for?");
				playerTalk(p, n, "I need it to fight Delrith");
				npcTalk(p, n, "Delrith?", "I thought the world was rid of him");
				choice = showMenu(p, n,
					"Well, the gypsy's crystal ball seems to think otherwise",
					"He's back and unfortunatly I've got to deal with him");
				if (choice == 0) {
					npcTalk(p, n, "Well if the ball says so, I'd better help you");
				} else if (choice == 1) {
					npcTalk(p, n, "You don't look up to much",
						"I suppose Silverlight may be good enough to carry you through though");
				} else break;
				npcTalk(p, n, "The problem is getting silverlight");
				playerTalk(p, n, "You mean you don't have it?");
				npcTalk(p, n, "Oh I do have it", "But it is so powerful",
					"That I have put it in a special box",
					"Which needs three different keys to open it",
					"That way, it won't fall into the wrong hands");
				choice = showMenu(p, n, "So give me the keys",
					"And why is this a problem?");
				if (choice == 0) {
					npcTalk(p, n, "Um", "Well, It's not so easy");
				} else if (choice == 1) {
				} else break;
				sirPrysinDialogue(p, n, SirPrysin.PROBLEM);
				break;
			case SirPrysin.PROBLEM:
				npcTalk(p, n,
					"I kept one of the keys", "I gave the other two",
					"To other people for safe keeping", "One I gave to Rovin",
					"who is captain of the palace guard",
					"I gave the other to the wizard Traiborn");
				p.updateQuestStage(this, 2);
				int problemMenu = showMenu(p, n,
					"Can you give me your key?",
					"Where can I find Captain Rovin?",
					"Where does the wizard live?");
				if (problemMenu == 0) {
					sirPrysinDialogue(p, n, SirPrysin.YOUR_KEY);
				} else if (problemMenu == 1) {
					sirPrysinDialogue(p, n, SirPrysin.ROVIN);
				} else if (problemMenu == 2) {
					sirPrysinDialogue(p, n, SirPrysin.WIZARD);
				}
				break;
			case SirPrysin.YOUR_KEY:
				npcTalk(p, n, "Um", "Ah", "Well there's a problem there as well",
					"I managed to drop the key in the drain",
					"Just outside the palace kitchen",
					"It is just inside and I can't reach it");
				int yourKey = showMenu(p, n,
					"So what does the drain connect to?",
					"Where can I find Captain Rovin?",
					"Where does the wizard live?");
				if (yourKey == 0) {
					sirPrysinDialogue(p, n, SirPrysin.DRAIN);
				} else if (yourKey == 1) {
					sirPrysinDialogue(p, n, SirPrysin.ROVIN);
				} else if (yourKey == 2) {
					sirPrysinDialogue(p, n, SirPrysin.WIZARD);
				}
				break;
			case SirPrysin.DRAIN:
				npcTalk(p, n, "It is the drain",
					"For the drainpipe running from the sink in the kitchen",
					"Down to the palace sewers");
				int drainMenu = showMenu(p, n,
					"Where can I find Captain Rovin?",
					"Where does the wizard live?",
					"Well I'd better go key hunting");
				if (drainMenu == 0) {
					sirPrysinDialogue(p, n, SirPrysin.ROVIN);
				} else if (drainMenu == 1) {
					sirPrysinDialogue(p, n, SirPrysin.WIZARD);
				} else if (drainMenu == 2) {
					sirPrysinDialogue(p, n, SirPrysin.Stage2);
				}
				break;
			case SirPrysin.Stage2:
				npcTalk(p, n, "Ok goodbye");
				break;
			case SirPrysin.ROVIN:
				npcTalk(p, n,
					"Captain Rovin lives at the top of the guards quarters",
					"in the northwest wing of this palace");
				int rovinMenu = showMenu(p, n,
					"Can you give me your key", "Where does the wizard live?",
					"Well I'd better go key hunting");
				if (rovinMenu == 0) {
					sirPrysinDialogue(p, n, SirPrysin.YOUR_KEY);
				} else if (rovinMenu == 1) {
					sirPrysinDialogue(p, n, SirPrysin.WIZARD);
				} else if (rovinMenu == 2) {
					sirPrysinDialogue(p, n, SirPrysin.Stage2);
				}
				break;
			case SirPrysin.WIZARD:
				npcTalk(p, n, "Wizard Traiborn?",
					"He is one of the wizards who lives in the tower",
					"On the little island just off the south coast",
					"I believe his quarters are on the first floor of the tower");
				int wizardMenu = showMenu(p, n,
					"Can you give me your key?",
					"Where can I find Captain Rovin?",
					"Well I'd better go key hunting");
				if (wizardMenu == 0) {
					sirPrysinDialogue(p, n, SirPrysin.YOUR_KEY);
				} else if (wizardMenu == 1) {
					sirPrysinDialogue(p, n, SirPrysin.ROVIN);
				} else if (wizardMenu == 2) {
					sirPrysinDialogue(p, n, SirPrysin.Stage2);
				}
				break;
		}
	}

	private void gypsyDialogue(Player p, Npc n, int conversationID) {
		if (conversationID == -1) {
			switch (p.getQuestStage(this)) {
				case 0:
					gypsyDialogue(p, n, GypsyConversation.INTRO);
					break;
				case 1:
					npcTalk(p, n, "Greetings how goes thy quest?");
					playerTalk(p, n, "I'm still working on it");
					npcTalk(p, n,
						"Well if you need any advice I'm always here young one");
					int choice = showMenu(p, n,
						"What is the magical incantation?",
						"Where can I find Silverlight?",
						"Well I'd better press on with it",
						"Stop calling me that");
					if (choice == 0) {
						gypsyDialogue(p, n, GypsyConversation.INCANTATION);
					} else if (choice == 1) {
						gypsyDialogue(p, n, GypsyConversation.SILVERLIGHT);
					} else if (choice == 2) {
						npcTalk(p, n, "See you anon");
					} else if (choice == 4) {
						npcTalk(p, n, "In the scheme of things you are very young");
						int choice2 = showMenu(p, n,
							"Ok but how old are you",
							"Oh if its in the scheme of things that's ok");
						if (choice2 == 0) {
							gypsyDialogue(p, n, GypsyConversation.HOW_OLD_TWO);
						} else if (choice2 == 1) {
							npcTalk(p, n, "You show wisdom for one so young");
						}
					}
					break;
				case 2:
				case 3:
				case 4:
					npcTalk(p, n, "How goes the quest?");
					if (!p.getInventory().hasItemId(ItemId.SILVERLIGHT.id())) {
						playerTalk(p, n,
							"I found Sir Prysin. Unfortunately, I haven't got the sword yet");
						playerTalk(p, n, "He's made it complicated for me!");
						npcTalk(p, n, "Ok, hurry, we haven't much time");
					} else {
						playerTalk(p, n,
							"I have the sword, now. I just need to kill the demon I think");
						npcTalk(p, n, "Yep, that's right");
					}
					break;
				case -1:
					npcTalk(p, n, "Greetings young one", "You're a hero now",
						"That was a good bit of demonslaying");
					int choice3 = showMenu(p, n,
						"How do you know I killed it?", "Thanks",
						"Stop calling me that");
					if (choice3 == 0) {
						npcTalk(p, n, "You forget",
							"I'm good at knowing these things");
					} else if (choice3 == 2) {
						npcTalk(p, n, "In the scheme of things you are very young");
						int choice2 = showMenu(p, n,
							"Ok but how old are you",
							"Oh if its in the scheme of things that's ok");
						if (choice2 == 0) {
							gypsyDialogue(p, n, GypsyConversation.HOW_OLD);
						} else if (choice2 == 1) {
							npcTalk(p, n, "You show wisdom for one so young");
						}
					}
					break;
			}
		}
		switch (conversationID) {
			case GypsyConversation.INTRO:// Intro
				npcTalk(p, n, "Hello, young one",
					"Cross my palm with silver and the future will be revealed to you");
				int introduceMenu = showMenu(p, n, "Ok, here you go",
					"Who are you calling young one?!",
					"No, I don't believe in that stuff");
				if (introduceMenu == 0) {
					gypsyDialogue(p, n, GypsyConversation.QUEST_START);
				} else if (introduceMenu == 1) {
					npcTalk(p, n, "You have been on this world",
						"A relatively short time", "At least compared to me",
						"So do you want your fortune told or not?");
					int choice = showMenu(p, n, "Yes please",
						"No, I don't believe in that stuff",
						"Ooh how old are you then?");
					if (choice == 0) {
						gypsyDialogue(p, n, GypsyConversation.QUEST_START);
					} else if (choice == 1) {
						npcTalk(p, n, "Ok suit yourself");
					} else if (choice == 2) {
						gypsyDialogue(p, n, GypsyConversation.HOW_OLD);
					}
				} else if (introduceMenu == 2) {
					npcTalk(p, n, "Ok suit yourself");
				}
				break;
			case GypsyConversation.HOW_OLD:
				npcTalk(p, n, "Older than you imagine");
				int choice = showMenu(p, n,
					"Believe me, I have a good imagination",
					"How do you know how old I think you are?",
					"Oh pretty old then");
				if (choice == 0) {
					// Believe me, I have a good imagination
					npcTalk(p, n, "You seem like just the sort of person",
						"Who would want their fortune told then");
					choice = showMenu(p, n, "No, I don't believe in that stuff",
						"Yes please");
					if (choice == 0) {
						// No I don't believe in that stuff
						npcTalk(p, n, "Ok suit yourself");
					} else if (choice == 1) {
						// Yes please
						gypsyDialogue(p, n, GypsyConversation.QUEST_START);
					}
				} else if (choice == 1) {
					// How do you know how old I think you are?
					npcTalk(p, n, "I have the power to know",
						"Just as I have the power to foresee the future");
					choice = showMenu(p, n, "Ok what am I thinking now?",
						"Ok but how old are you?",
						"Go on then, what's my future?");
					if (choice == 0) {
						// Ok what am I thinking now?
						npcTalk(p, n, "You are thinking that I'll never guess what you are thinking");
					} else if (choice == 1) {
						// Ok but how old are you?
						gypsyDialogue(p, n, GypsyConversation.HOW_OLD_TWO);
					} else if (choice == 2) {
						// Go on then, what's my future?
						gypsyDialogue(p, n, GypsyConversation.QUEST_START);
					}
				} else if (choice == 2) {
					// Oh pretty old then
					npcTalk(p, n, "Yes I'm old", "Don't rub it in");
				}
				break;
			case GypsyConversation.HOW_OLD_TWO:
				npcTalk(p, n,
					"Count the number of legs of the chairs in the blue moon inn",
					"And multiply that number by seven");
				playerTalk(p, n, "Err yeah whatever");
				break;
			case GypsyConversation.QUEST_START:// Quest Start
				if (p.getInventory().hasItemId(ItemId.COINS.id()))
					p.getInventory().remove(ItemId.COINS.id(), 1);
				else {
					playerTalk(p, n, "Oh dear. I don't have any money");
					break;
				}
				npcTalk(p, n, "Come closer",
					"And listen carefully to what the future holds for you",
					"As I peer into the swirling mists of the crystal ball",
					"I can see images forming", "I can see you",
					"You are holding a very impressive looking sword",
					"I'm sure I recognise that sword",
					"There is a big dark shadow appearing now", "Aaargh");
				int aargh = showMenu(p, n,
					"Very interesting what does the Aaargh bit mean?",
					"Are you alright?", "Aaargh?");
				if (aargh > -1) {
					npcTalk(p, n, "Aaargh its Delrith", "Delrith is coming");
					choice = showMenu(p, n, "Who's Delrith?",
						"Get a grip!");
					if (choice == 0) {
						gypsyDialogue(p, n, GypsyConversation.WHO_IS_DELRITH);
					} else if (choice == 1) {
						npcTalk(p, n, "Sorry. I didn't expect to see Delrith",
							"I had to break away quickly in case he detected me");
						playerTalk(p, n, "Who's Delrith?");
						gypsyDialogue(p, n, GypsyConversation.WHO_IS_DELRITH);
					}
					p.updateQuestStage(this, 1);
				}
				break;

			case GypsyConversation.WHO_IS_DELRITH:
				npcTalk(p,
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
				choice = showMenu(p, n,
					"How am I meant to fight a demon who can destroy cities?",
					"Ok where is he? I'll kill him for you",
					"Wally doesn't sound like a very heroic name");
				if (choice == 0) {
					npcTalk(p, n, "I admit it won't be easy");
					gypsyDialogue(p, n, GypsyConversation.DEFEATING_DELRITH);
				} else if (choice == 1) {
					npcTalk(p, n, "Well you can't just go and fight",
						"He can't be harmed by ordinary weapons");
					gypsyDialogue(p, n, GypsyConversation.DEFEATING_DELRITH);
				} else if (choice == 2) {
					gypsyDialogue(p, n, GypsyConversation.WALLY);
				}
				break;
			case GypsyConversation.DEFEATING_DELRITH:
				npcTalk(p,
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
				gypsyDialogue(p, n, GypsyConversation.INCANTATION_SILVERLIGHT_MENU);
				break;

			case GypsyConversation.INCANTATION:
				npcTalk(p, n, "Oh yes let me think a second");
				message(p, "The gypsy is thinking");
				sleep(2000);
				npcTalk(p, n, "Alright I've got it now I think", "It goes",
					"Carlem", "Aber", "Camerinthum", "Purchai", "Gabindo",
					"Have you got that?");
				playerTalk(p, n, "I think so, yes");
				choice = showMenu(p, n,
					"Ok thanks. I'll do my best to stop the Demon",
					"Where can I find Silverlight?");
				if (choice == 0) {
					gypsyDialogue(p, n, GypsyConversation.ILL_DO_MY_BEST);
				} else if (choice == 1) {
					gypsyDialogue(p, n, GypsyConversation.SILVERLIGHT);
				}
				break;

			case GypsyConversation.INCANTATION_SILVERLIGHT_MENU:
				choice = showMenu(p, n,
					"What is the magical incantation?",
					"Where can I find Silverlight?");
				if (choice == 0) {
					npcTalk(p, n, "Oh yes let me think a second");
					message(p, "The gypsy is thinking");
					sleep(2000);
					npcTalk(p, n, "Alright I've got it now I think", "It goes",
						"Carlem", "Aber", "Camerinthum", "Purchai", "Gabindo",
						"Have you got that?");
					playerTalk(p, n, "I think so, yes");
					choice = showMenu(p, n,
						"Ok thanks. I'll do my best to stop the Demon",
						"Where can I find Silverlight?");
					if (choice == 0) {
						gypsyDialogue(p, n, GypsyConversation.ILL_DO_MY_BEST);
					} else if (choice == 1) {
						gypsyDialogue(p, n, GypsyConversation.SILVERLIGHT);
					}
				}
				if (choice == 1) {
					npcTalk(p,
						n,
						"Silverlight has been passed down through Wally's descendents",
						"I believe it is currently in the care of one of the king's knights",
						"called Sir Prysin",
						"He shouldn't be to hard to find he lives in the royal palace in this city",
						"Tell him Gypsy Aris sent you");
					choice = showMenu(p, n,
						"Ok thanks. I'll do my best to stop the Demon",
						"What is the magical incantation?");
					if (choice == 0) {
						gypsyDialogue(p, n, GypsyConversation.ILL_DO_MY_BEST);
					} else if (choice == 1) {
						gypsyDialogue(p, n, GypsyConversation.INCANTATION);
					}
				}
				break;

			case GypsyConversation.WALLY:
				npcTalk(p,
					n,
					"Yes I know. Maybe that is why history doesn't remember him",
					"However he was a very great hero.",
					"Who knows how much pain and suffering",
					"Delrith would have brought forth without Wally to stop him",
					"It looks like you are going to need to perform similar heroics");
				choice = showMenu(p, n,
					"How am I meant to fight a demon who can destroy cities?",
					"Ok where is he? I'll kill him for you");
				if (choice == 0) {
					npcTalk(p, n, "I admit it won't be easy");
					gypsyDialogue(p, n, GypsyConversation.DEFEATING_DELRITH);
				} else if (choice == 1) {
					npcTalk(p, n, "Well you can't just go and fight",
						"He can't be harmed by ordinary weapons");
					gypsyDialogue(p, n, GypsyConversation.DEFEATING_DELRITH);
				}
				break;

			case GypsyConversation.SILVERLIGHT:
				npcTalk(p,
					n,
					"Silverlight has been passed down through Wally's descendents",
					"I believe it is currently in the care of one of the king's knights",
					"called Sir Prysin",
					"He shouldn't be to hard to find he lives in the royal palace in this city",
					"Tell him Gypsy Aris sent you");
				choice = showMenu(p, n,
					"Ok thanks. I'll do my best to stop the Demon",
					"What is the magical incantation?");
				if (choice == 0) {
					gypsyDialogue(p, n, GypsyConversation.ILL_DO_MY_BEST);
				} else if (choice == 1) {
					gypsyDialogue(p, n, GypsyConversation.INCANTATION);
				}
				break;
			case GypsyConversation.ILL_DO_MY_BEST:
				npcTalk(p, n, "Good luck, may Guthix be with you");
				break;
		}
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		if (n.getID() == NpcId.DELRITH.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
				case 3:
					playerTalk(p, null, "I'd rather not. He looks scary");
					return true;
				case 4:
					if (!p.getInventory().wielding(ItemId.SILVERLIGHT.id())) {
						playerTalk(p, null, "Maybe I'd better wield silverlight first");
						return true;
					} else {
						// silverlight effect shared in its own file
						n.getSkills().setLevel(SKILLS.HITS.id(), n.getDef().getHits());
						p.resetMenuHandler();
						p.setOption(-1);
						p.setAttribute("delrith", false);
					}
					break;
				case -1:
					p.message("You've already done that quest");
					return true;
			}
		}
		return false;
	}

	public void onPlayerKilledNpc(Player p, Npc n) {
		n.getSkills().setLevel(SKILLS.HITS.id(), n.getDef().getHits());
		if (p.getMenuHandler() == null && !p.getAttribute("delrith", false)) {
			p.setAttribute("delrith", true);
			message(p, "As you strike Delrith a vortex opens up");
			playerTalk(p, n, "Now what was that incantation again");
			if (p.inCombat()) {
				int choice = showMenu(p, n,
					"Carlem Gabindo Purchai Zaree Camerinthum",
					"Purchai Zaree Gabindo Carlem Camerinthum",
					"Purchai Camerinthum Aber Gabindo Carlem",
					"Carlem Aber Camerinthum Purchai Gabindo");
				if (choice != -1) {
					if (choice == 3) {
						message(p, 1300, "Delrith is sucked back into the dark demension from which he came");
						n.killedBy(p);
						n.remove();
						if (p.getQuestStage(Constants.Quests.DEMON_SLAYER) != -1) {
							//remove flags in case they are present with drop trick
							p.getCache().remove("done_bone_task");
							p.getCache().remove("traiborn_bones");
							p.sendQuestComplete(getQuestId());
						}
					} else {
						message(p, 1300, "As you chant, Delrith is sucked towards the vortex", "Suddenly the vortex closes");
						p.message("And Delrith is still here");
						p.message("That was the wrong incantation");
					}
				}
				p.setAttribute("delrith", false);
			}
		}
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == NpcId.DELRITH.id();
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		return n.getID() == NpcId.DELRITH.id();
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc n) {
		if (n.getID() == NpcId.DELRITH.id()) {
			p.message("You cannot attack Delrith without the silverlight sword");
		}
	}

	class GypsyConversation {
		static final int INTRO = 0;
		static final int QUEST_START = 1;
		static final int DEFEATING_DELRITH = 2;
		static final int WHO_IS_DELRITH = 3;
		static final int WALLY = 4;
		static final int INCANTATION_SILVERLIGHT_MENU = 5;
		static final int SILVERLIGHT = 6;
		static final int ILL_DO_MY_BEST = 7;
		static final int INCANTATION = 8;
		static final int HOW_OLD = 9;
		static final int HOW_OLD_TWO = 10;
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

