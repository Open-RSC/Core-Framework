package com.openrsc.server.plugins.quests.members;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnWallObjectListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnWallObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

public class ScorpionCatcher implements QuestInterface,TalkToNpcListener,
TalkToNpcExecutiveListener, InvUseOnNpcListener,
InvUseOnNpcExecutiveListener, InvUseOnWallObjectListener,
InvUseOnWallObjectExecutiveListener, WallObjectActionListener,
WallObjectActionExecutiveListener {

	public static int THORMAC = 300;
	public static int SEER = 301;
	public static int VELRAK_THE_EXPLORER = 272;

	@Override
	public int getQuestId() {
		return Constants.Quests.SCORPION_CATCHER;
	}

	@Override
	public String getQuestName() {
		return "Scorpion catcher (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("Well done you have completed the scorpion catcher quest");
		p.incQuestPoints(1);
		p.message("@gre@You have gained 1 quest point!");
		p.incQuestExp(2, p.getSkills().getMaxStat(2) * 500 + 1500);

	}

	class SEER_NPC {
		private static final int LOCATE_SCORPIONS = 0;
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == THORMAC) {
			return true;
		}
		if (n.getID() == SEER) {
			return true;
		}
		if (n.getID() == VELRAK_THE_EXPLORER) {
			return true;
		}
		return false;
	}

	private void seerDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
			case 0:
			case -1:
				npcTalk(p,n, "Many greetings");
				int menu = showMenu(p, n, "Many greetings", "I seek knowledge and power");
				if(menu == 1) {
					npcTalk(p, n, "Knowledge comes from experience, power comes from battleaxes");
				}
				break;
			case 1:
				npcTalk(p, n, "Many greetings");
				int first = showMenu(p, n, 
						"I need to locate some scorpions",
						"Your friend Thormac sent me to speak to you",
						"I seek knowledge and power");
				if (first == 0) {
					seerDialogue(p, n, SEER_NPC.LOCATE_SCORPIONS);
				} else if (first == 1) {
					npcTalk(p, n, "What does the old fellow want");
					playerTalk(p, n, "He's lost his valuable lesser kharid scorpions");
					seerDialogue(p, n, SEER_NPC.LOCATE_SCORPIONS);
				} else if (first == 2) {
					npcTalk(p, n, "Knowledge comes from experience, power comes from battleaxes");
				}
				break;
			case 2:
				if (hasItem(p, 686)) {
					playerTalk(p, n, "I need to locate some scorpions");
					seerDialogue(p, n, SEER_NPC.LOCATE_SCORPIONS);
					return;
				}
				npcTalk(p, n, "Many greetings");
				playerTalk(p, n, "Where did you say that scorpion was again?");
				npcTalk(p, n, "Let me look into my looking glass");
				message(p, "The seer produces a small mirror",
						"The seer gazes into the mirror",
						"The seer smoothes his hair with his hand");
				npcTalk(p,
						n,
						"I can see a scorpion that you seek",
						"It would appear to be near some  nasty looking spiders",
						"I can see two coffins there as well",
						"The scorpion seems to be going through some crack in the wall",
						"He's gone into some sort of secret room",
						"Well see if you can find that scorpion then",
						"And I'll try and get you some information on the others");
				break;
			}

		}
		switch (cID) {
		case SEER_NPC.LOCATE_SCORPIONS:
			npcTalk(p, n, "Well you have come to the right place",
					"I am a master of animal detection",
					"Do you need to locate any particular scorpion",
					"Scorpions are a creature somewhat in abundance");
			playerTalk(p, n, "I'm looking for some lesser kharid scorpions",
					"They belong to Thormac the sorceror");
			npcTalk(p, n, "Let me look into my looking glass");
			message(p, "The seer produces a small mirror",
					"The seer gazes into the mirror",
					"The seer smoothes his hair with his hand");
			npcTalk(p,
					n,
					"I can see a scorpion that you seek",
					"It would appear to be near some  nasty looking spiders",
					"I can see two coffins there as well",
					"The scorpion seems to be going through some crack in the wall",
					"He's gone into some sort of secret room",
					"Well see if you can find that scorpion then",
					"And I'll try and get you some information on the others");
			if(p.getQuestStage(this) == 1) {
				p.updateQuestStage(getQuestId(), 2);
			}
			break;
		}
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == SEER) {
			seerDialogue(p, n, -1);
		}
		if (n.getID() == VELRAK_THE_EXPLORER) {
			if (hasItem(p, 596)) {
				playerTalk(p, n, "Are you still here?");
				npcTalk(p, n, "Yes, I'm still plucking up courage",
						"To run out past those black knights");
				return;
			}
			npcTalk(p, n, "Thankyou for rescuing me",
					"It isn't comfy in this cell");
			int first = showMenu(p, n,
					"So do you know anywhere good to explore?",
					"Do I get a reward?");
			if (first == 0) {
				npcTalk(p, n, "Well this dungeon was quite good to explore",
						"Till I got captured",
						"I got given a key to an inner part of this dungeon",
						"By a mysterious cloaked stranger",
						"It's rather to tough for me to get that far though",
						"I keep getting captured",
						"Would you like to give it a go");
				int second = showMenu(p, n, "Yes please",
						"No it's too dangerous for me");
				if (second == 0) {
					message(p,
							"Velrak reaches inside his boot and passes you a key");
					addItem(p, 596, 1);
				} 
			} else if (first == 1) {
				npcTalk(p,
						n,
						"Well not really the black knights took all my stuff before throwing me in here");
			}
		}
		if (n.getID() == THORMAC) {
			switch (p.getQuestStage(this)) {
			case 0:
				npcTalk(p, n, "Hello I am Thormac the sorceror",
						"I don't suppose you could be of assistance to me?");
				int first = showMenu(p, n,
						"What do you need assistance with?",
						"I'm a little busy");
				if (first == 0) {
					npcTalk(p,
							n,
							"I've lost my pet scorpions",
							"They're lesser kharid scorpions, a very rare breed",
							"I left there cage door open",
							"now I don't know where they have gone",
							"There's 3 of them and they're quick little beasties",
							"They're all over runescape");
					int second = showMenu(p, n,
							"So how would I go about catching them then?",
							"What's in it for me?", "I'm not interested then");
					if (second == 0) {
						npcTalk(p, n, "Well I have a scorpion cage here",
								"Which you can use to catch them in");
						addItem(p, 678, 1);
						message(p, "Thormac gives you a cage");
						npcTalk(p,
								n,
								"If you go up to the village of seers to the north of here",
								"One of them will be able to tell you where the scorpions are now");
						int third = showMenu(p, n, "What's in it for me?",
								"Ok I will do it then");
						if (third == 0) {
							npcTalk(p,
									n,
									"Well I suppose I can aid you with my skills as a staff sorcerer",
									"Most the battlestaffs around here are pretty puny",
									"I can beef them up for you a bit");
						} else if (third == 1) {
							p.updateQuestStage(getQuestId(), 1); // STARTED QUEST
						}
					} else if (second == 1) {
						npcTalk(p,
								n,
								"Well I suppose I can aid you with my skills as a staff sorcerer",
								"Most the battlestaffs around here are pretty puny",
								"I can beef them up for you a bit");
					} else if (second == 2) {
						npcTalk(p, n,
								"Blast, I suppose I will have to have find someone else then");
					}
				}
				break;
			case 1:
			case 2:
				npcTalk(p, n, "How goes your quest?");
				if(!hasItem(p, 678) && !hasItem(p, 681)) {
					int menu = showMenu(p,n,
							"I've lost my cage",
							"I've not caught all the scorpions yet");
					if(menu == 0 ) {
						npcTalk(p,n, "Ok here is another cage",
								"You're almost as bad at loosing things as me");
						addItem(p, 678, 1);
					} else if(menu == 1) {
						npcTalk(p, n, "Well remember, go speak to the seers north of here if you need any help");
					}
				}
				else if (hasItem(p, 681)) {
					playerTalk(p, n, "I have retrieved all your scorpions");
					npcTalk(p, n, "aha my little scorpions home at last");
					p.sendQuestComplete(Constants.Quests.SCORPION_CATCHER);
				} else {
					playerTalk(p, n, "I've not caught all the scorpions yet");
					npcTalk(p, n, "Well remember, go speak to the seers north of here if you need any help");
				}
				break;
			case -1:
				npcTalk(p, n, "Thankyou for rescuing my scorpions");
				int four = showMenu(p, n, "That's ok",
						"You said you'd enchant my battlestaff for me");
				if (four == 1) {
					npcTalk(p, n,
							"Yes it'll cost you 40000 coins for the materials needed mind you",
							"Which sort of staff did you want enchanting?");
					int five = showMenu(p, n,
							"battlestaff of fire", "battlestaff of water",
							"battlestaff of air", "battlestaff of earth",
					"I won't bother yet actually");
					if (five == 0) {
						if(!hasItem(p, 615)) {
							playerTalk(p, n, "I don't have a battlestaff of fire yet though");
							return;
						}
						if(!hasItem(p, 10, 40000)) {
							playerTalk(p, n, "I'll just get the money for you");
							return;
						}
						if(removeItem(p, new Item(10, 40000), new Item(615,1)))  {
							addItem(p, 682, 1);
							p.message("Thormac enchants your staff");
						}
					} else if (five == 1) {
						if(!hasItem(p, 616)) {
							playerTalk(p, n, "I don't have a battlestaff of water yet though");
							return;
						}
						if(!hasItem(p, 10, 40000)) {
							playerTalk(p, n, "I'll just get the money for you");
							return;
						}
						if(removeItem(p, new Item(10, 40000), new Item(616,1)))  {
							addItem(p, 683, 1);
							p.message("Thormac enchants your staff");
						}
					} else if (five == 2) {
						if(!hasItem(p, 617)) {
							playerTalk(p, n, "I don't have a battlestaff of air yet though");
							return;
						}
						if(!hasItem(p, 10, 40000)) {
							playerTalk(p, n, "I'll just get the money for you");
							return;
						}
						if(removeItem(p, new Item(10, 40000), new Item(617,1)))  {
							addItem(p, 684, 1);
							p.message("Thormac enchants your staff");
						}
					} else if (five == 3) {
						if(!hasItem(p, 618)) {
							playerTalk(p, n, "I don't have a battlestaff of earth yet though");
							return;
						}
						if(!hasItem(p, 10, 40000)) {
							playerTalk(p, n, "I'll just get the money for you");
							return;
						}
						if(removeItem(p, new Item(10, 40000), new Item(618,1)))  {
							addItem(p, 685, 1);
							p.message("Thormac enchants your staff");
						}
					}
				}
				break;
			}
		}
	}

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc n, Item i) {
		if (n.getID() == 303 && i.getID() == 678) {
			return true;
		}
		if (n.getID() == 304 && i.getID() == 686) {
			return true;
		}
		if (n.getID() == 302 && i.getID() == 689) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc n, Item i) {
		/** Barb course scorpion **/
		if (n.getID() == 303 && i.getID() == 678) {
			p.message("You catch a scorpion");
			removeItem(p, 678, 1);
			addItem(p, 686, 1);
			temporaryRemoveNpc(n);
		}
		/** Prayer scorpion **/
		if (n.getID() == 304 && i.getID() == 686) {
			p.message("You catch a scorpion");
			removeItem(p, 686, 1);
			addItem(p, 689, 1);
			temporaryRemoveNpc(n);
		}
		/** Taverly dungeon scorpion **/
		if (n.getID() == 302 && i.getID() == 689) {
			if (p.getQuestStage(this) == 2) {
				p.message("You catch a scorpion");
				removeItem(p, 689, 1);
				addItem(p, 681, 1);
				temporaryRemoveNpc(n);
			} else {
				p.message("Talk to Seer before you attempt catching this scorpion");
				return;
			}
		}

	}

	@Override
	public boolean blockInvUseOnWallObject(GameObject obj, Item item,
			Player player) {
		if (obj.getID() == 83 && obj.getY() == 3428 && item.getID() == 595) {
			return true;
		}
		if (obj.getID() == 83 && obj.getY() == 3425 && item.getID() == 595) {
			return true;
		}
		if (obj.getID() == 84 && obj.getY() == 3353 && item.getID() == 596) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnWallObject(GameObject obj, Item item, Player player) {
		/*
		 * Velrak cell door
		 */
		if (obj.getID() == 83 && obj.getY() == 3428 && item.getID() == 595) {
			showBubble(player, item);
			doDoor(obj, player);
		}
		/*
		 * Below door infront of Velrak cell has nothing todo with quest or
		 * anything important at all - replicated it anyway.
		 */
		if (obj.getID() == 83 && obj.getY() == 3425 && item.getID() == 595) {
			showBubble(player, item);
			doDoor(obj, player);
		}
		/*
		 * Dusty key door into blue dragons lair in Taverly dungeon
		 */
		if (obj.getID() == 84 && obj.getY() == 3353 && item.getID() == 596) {
			showBubble(player, item);
			doDoor(obj, player);
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
			Player player) {
		if (obj.getID() == 87 && obj.getY() == 3353) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 87 && obj.getY() == 3353) {
			doDoor(obj, p);
			p.message("You just went through a secret door");
		}
	}
}
