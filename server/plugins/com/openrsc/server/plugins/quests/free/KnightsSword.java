package com.openrsc.server.plugins.quests.free;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class KnightsSword implements QuestInterface,TalkToNpcListener,
		TalkToNpcExecutiveListener, ObjectActionListener,
		ObjectActionExecutiveListener {
	private static final int QUEST_POINTS = 1;
	private static final int NPC_SQUIRE = 132;
	private static final int NPC_DWARF = 134;
	private static final int NPC_VYVIN = 138;
	private static final int CUPBOARD_ID = 175;
	private static final int CUPBOARD_Y = 2454;
	private static final int BLUERITE_ORE_ID = 266;
	private static final int SWORD_ID = 265;
	private static final int PICTURE_ID = 264;

	// Thrugo coords: 290 716

	@Override
	public int getQuestId() {
		return Constants.Quests.THE_KNIGHTS_SWORD;
	}

	@Override
	public String getQuestName() {
		return "The Knight's sword";
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the knight's sword quest");
		player.incQuestExp(13, player.getSkills().getMaxStat(13) * 1500 + 1400);
		player.incQuestPoints(QUEST_POINTS);
		player.message("@gre@You have gained 1 quest point!");
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NPC_DWARF || n.getID() == NPC_SQUIRE
				|| n.getID() == NPC_VYVIN;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NPC_DWARF) {
			dwarfDialogue(p, n);
		} else if (n.getID() == NPC_SQUIRE) {
			squireDialogue(p, n, -1);
		} else if (n.getID() == NPC_VYVIN) {
			vyvinDialogue(p, n);
		}
	}

	private void vyvinDialogue(final Player p, final Npc n) {
		playerTalk(p, n, "Hello");
		npcTalk(p, n, "Greetings traveller");
		int option = showMenu(p, n, "Do you have anything to trade?",
				"Why are there so many knights in this city?");
		if (option == 0) {
			npcTalk(p, n, "No I'm sorry");
		} else if (option == 1) {
			npcTalk(p, n, "We are the white knights of falador",
					"We are the most powerfull order of knights in the land",
					"We are helping the king vallance rule the kingdom",
					"As he is getting old and tired");
		}
	}

	public void dwarfDialogue(final Player p, final Npc n) {
		switch (p.getQuestStage(this)) {
		case -1:
			playerTalk(p, n, "Thanks for your help getting the sword for me");
			npcTalk(p, n, "No worries mate");
			break;
		case 0:
		case 1:
			npcTalk(p, n, "Go away!");
			break;
		case 2:
			int option = showMenu(p, n, "Hello are you an incando Dwarf?",
					"Would you like some redberry pie?");
			if (option == 0) {
				npcTalk(p, n, "Yeah what about it?");
				option = showMenu(p, n, "Would you like some redberry pie?",
						"Can you make me a special sword?");
				if (option == 0) {
					givePie(p, n);
				} else {
					npcTalk(p, n, "No i don't do that anymore",
							"I'm getting old");
				}
			} else {
				givePie(p, n);
			}
			break;
		case 3:
			playerTalk(p, n, "Can you make me a special sword?");
			npcTalk(p, n, "Well after you've brought me such a great pie",
					"I guess i should give it a go",
					"What sort of sword is it?");
			playerTalk(
					p,
					n,
					"I need you to make a sword for one of falador's knights",
					"He had one which was passed down through five generations",
					"But his squire has lost it");
			npcTalk(p,
					n,
					"A knight's sword eh?",
					"Well i'd need to know exactly how it looked",
					"Before i could make a new one",
					"All the faladian knights used to have swords with different designs",
					"Could you bring me a picture or something?");
			playerTalk(p, n, "I'll see if i can find one",
					"I'll go and ask his squire");
			p.updateQuestStage(this, 4);
			break;
		case 5:
		case 4:
			if (hasItem(p, PICTURE_ID)) {
				playerTalk(p, n,
						"I have found a picture of the sword i would like you to make");
				p.message("You hand Thurgo the portrait");
				removeItem(p, PICTURE_ID, 1);
				message(p, "Thurgo examines the picture for a moment");
				npcTalk(p,
						n,
						"Ok you'll need to get me some stuff for me to make this",
						"I'll need two iron bars to make the sword to start with",
						"I'll also need an ore called blurite",
						"It's useless for making actual weapons for fighting with",
						"But i'll need some as decoration for the hilt",
						"It is a fairly rare sort of ore",
						"The only place i know where to get it",
						"Is under this cliff here",
						"But it is guarded by a very powerful ice giant",
						"Most the rocks in that cliff are pretty useless",
						"Don't contain much of anything",
						"But there's definitly some blurite in there",
						"You'll need a little bit of mining experience",
						"To be able to find it");
				playerTalk(p, n, "Ok i'll go and find them");
				p.updateQuestStage(this, 6);
			} else {
				npcTalk(p, n, "Have you got a picture of the sword for me yet?");
				playerTalk(p, n, "No sorry, not yet");
			}
			break;
		case 6:
			if (hasItem(p, 265)) {
				playerTalk(p, n,
						"Thanks for your help getting the sword for me");
				npcTalk(p, n, "No worries mate");
				return;
			}
			if (hasItem(p, 170, 2) && hasItem(p, BLUERITE_ORE_ID)) {
				npcTalk(p, n, "How are you doing finding sword materials?");
				playerTalk(p, n, "I have them all");
				message(p, "You hand Thurgo the items");

				removeItem(p, 170, 1);
				removeItem(p, 170, 1);
				removeItem(p, BLUERITE_ORE_ID, 1);
				message(p, "Thurgo hammers the materials into a metal sword");

				addItem(p, SWORD_ID, 1);
				npcTalk(p, n, "Here you go");
				playerTalk(p, n, "Thank you very much");
				npcTalk(p, n,
						"Just remember to call in with more pie some time");
			} else {
				npcTalk(p, n, "How are you doing finding sword materials?");
				playerTalk(p, n, "I haven't found everything yet");
				npcTalk(p, n, "Well come back when you do",
						"Remember i need blurite ore and two iron bars");
			}
			break;
		}
	}

	private void givePie(Player p, Npc n) {
		message(p, "Thurgo's eyes light up");
		npcTalk(p, n, "I'd never say no to a redberry pie");
		if (!hasItem(p, 258)) {
			playerTalk(p, n, "Well that's too bad, because I don't have any");
			message(p, "Thurgo does not look impressed");
		} else {
			message(p, "You hand over the pie");
			removeItem(p, 258, 1);
			p.updateQuestStage(this, 3);
			npcTalk(p, n, "It's great stuff");
			message(p, "Thurgo eats the pie", "He pats his stomach");
			npcTalk(p, n, "By guthix that was good pie",
					"Anyone who makes pie like that has gotta be alright");
		}
	}

	public void squireDialogue(final Player p, final Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
			case -1:
				npcTalk(p, n, "Hello friend", "Thanks for your help before",
						"Vyvin never even realised it was a different sword");
				break;
			case 0:
				npcTalk(p, n, "Hello, i am the squire to sir vyvin");
				int option = showMenu(p, n, "And how is life as a squire?",
						"Wouldn't you prefer to be a squire for me?");
				if (option == 0) {
					npcTalk(p, n, "Well sir vyvin is a good guy to work for",
							"However i'm in a spot of trouble today",
							"I've gone and lost sir vyvin's sword");
					option = showMenu(p, n, "Do you know where you lost it?",
							"I can make a new sword if you like",
							"Is he angry?");
					if (option == 0) {
						npcTalk(p, n, "Well now if i knew that",
								"It wouldn't be lost, now would it?");

						squireDialogue(p, n, Squire.MAIN);
					} else if (option == 1) {
						squireDialogue(p, n, Squire.MAIN);
					} else {
						npcTalk(p,
								n,
								"He doesn't know yet",
								"I was hoping i could think of something to do",
								"Before he does find out",
								"But i find myself at a loss");
						squireDialogue(p, n, Squire.MAIN);
					}
				} else {
					npcTalk(p, n, "No, sorry i'm loyal to vyvin");
				}
				break;
			case 1:
			case 2:
			case 3:
				npcTalk(p, n, "So how are you doing getting a sword?");
				playerTalk(p, n, "I'm still looking for incando dwarves");
				break;
			case 4:
				npcTalk(p, n, "So how are you doing getting a sword?");
				playerTalk(p, n, "I've found an incando dwarf",
						"But he needs a picture of the sword before he can make it");
				npcTalk(p,
						n,
						"A picture eh?",
						"The only one i can think of is in a small portrait of sir vyvin's father",
						"He's holding the sword in it",
						"Sir vyvin keeps it in a cupboard in his room i think");
				p.updateQuestStage(this, 5);
				break;
			case 6:
			case 5:
				if (hasItem(p, SWORD_ID)) {
					playerTalk(p, n, "I have retrieved your sword for you");
					npcTalk(p, n, "Thankyou, thankyou",
							"I was seriously worried i'd have to own up to sir vyvin");
					p.message("You give the sword to the squire");
					removeItem(p, SWORD_ID, 1);
					p.sendQuestComplete(getQuestId());
				} else if (hasItem(p, PICTURE_ID)) {
					npcTalk(p, n, "So how are you doing getting a sword?");
					playerTalk(p, n, "I haven't got it from Thurgo yet",
							"Please let me know when you do");
					return;
				} else {
					npcTalk(p, n, "So how are you doing getting a sword?");
					playerTalk(p, n, "I've found an incando dwarf",
							"But he needs a picture of the sword before he can make it");
					npcTalk(p,
							n,
							"A picture eh?",
							"The only one i can think of is in a small portrait of sir vyvin's father",
							"He's holding the sword in it",
							"Sir vyvin keeps it in a cupboard in his room i think");
					p.updateQuestStage(this, 5);
				}
				break;
			}
		}
		switch (cID) {
		case Squire.MAIN:
			int option = showMenu(p, n,
					"Well do you know the vague area you lost it?",
					"I can make a new sword if you like",
					"Well the kingdom is fairly abundant with swords",
					"Well I hope you find it soon");
			if (option == 0) {
				squireDialogue(p, n, Squire.LOST_IT);
			} else if (option == 1) {
				squireDialogue(p, n, Squire.NEW_SWORD);
			} else if (option == 2) {
				squireDialogue(p, n, Squire.ABUDANT);
			} else {
				squireDialogue(p, n, Squire.FIND_IT);
			}
			break;
		case Squire.LOST_IT:
			npcTalk(p,
					n,
					"No i was carrying it for him all the way from where he had it stored in Varrock",
					"It must have slipped from my pack during the trip",
					"And you know what people are like these days",
					"Someone will have just picked it up and kept it for themselves");
			int option1 = showMenu(p, n, "I can make a new sword if you like",
					"Well the kingdom is fairly abundant with swords",
					"Well I hope you find it soon");
			if (option1 == 0) {
				squireDialogue(p, n, Squire.NEW_SWORD);
			} else if (option1 == 1) {
				squireDialogue(p, n, Squire.ABUDANT);
			} else if (option1 == 2) {
				squireDialogue(p, n, Squire.FIND_IT);
			}
			break;
		case Squire.NEW_SWORD:
			npcTalk(p, n, "Thanks for the offer",
					"I'd be surprised if you could though");
			squireDialogue(p, n, Squire.DWARF_CHAT);
			break;
		case Squire.FIND_IT:
			npcTalk(p, n, "Yes me too",
					"I'm not looking forward to telling vyvin i've lost it",
					"He's going to want it for the parade next week as well");
			break;
		case Squire.DWARF_CHAT:
			npcTalk(p,
					n,
					"The thing is, this sword is a family heirloom",
					"It has been passed down through vyvin's family for five generations",
					"It was originally made by the incando dwarves",
					"Who were a particularly skilled tribe of dwarven smiths",
					"I doubt anyone could make it in the style they do");

			int option11 = showMenu(p, n,
					"So would these dwarves make another one?",
					"Well I hope you find it soon");
			if (option11 == 0) {
				npcTalk(p,
						n,
						"I'm not a hundred percent sure the incando tribe exists anymore",
						"I should think reldo the palace librarian in varrock will know",
						"He has done a lot of research on the races of these lands",
						"I don't suppose you could try and track down the incando dwarves for me?",
						"I've got so much work to do");

				int option2 = showMenu(p, n, "Ok I'll give it a go",
						"No I've got lots of mining work to do");
				if (option2 == 0) {
					npcTalk(p, n, "Thankyou very much",
							"As i say the best place to start should be with reldo");
					p.updateQuestStage(this, 1);
				}
			} else if (option11 == 1) {
				squireDialogue(p, n, Squire.FIND_IT);
			}
			break;
		}
	}

	class Squire {
		public static final int DWARF_CHAT = 5;
		public static final int ABUDANT = 4;
		public static final int FIND_IT = 3;
		public static final int NEW_SWORD = 2;
		public static final int LOST_IT = 1;
		public static final int MAIN = 0;

	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == CUPBOARD_ID && obj.getY() == CUPBOARD_Y
				&& obj.getX() == 318) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		final Npc n = World.getWorld().getNpc(138, 316, 320, 2454, 2459);
		if (obj.getID() == CUPBOARD_ID && obj.getY() == CUPBOARD_Y
				&& obj.getX() == 318) {
			if (n != null) {
				if (!n.isBusy()) {
					n.face(p);
					p.face(n);
					npcTalk(p, n, "Hey what are you doing?",
							"That's my cupboard");
					message(p,
							"Maybe you need someone to distract Sir Vivyn for you");
				} else {
					message(p, "You search through the cupboard");
					if (hasItem(p, PICTURE_ID) || p.getQuestStage(this) < 4) {
						p.message("The cupboard is just full of junk");
						return;
					}
					p.message("You find a small portrait in there which you take");
					addItem(p, PICTURE_ID, 1);
				}
			}
		}
	}

}
