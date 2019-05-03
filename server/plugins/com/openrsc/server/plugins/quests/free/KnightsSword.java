package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class KnightsSword implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener, ObjectActionListener,
	ObjectActionExecutiveListener {
	private static final int VYVINS_CUPBOARD_OPEN = 175;
	private static final int VYVINS_CUPBOARD_CLOSED = 174;
	private static final int CUPBOARD_Y = 2454;

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
		incQuestReward(player, Quests.questData.get(Quests.THE_KNIGHTS_SWORD), true);
		player.message("@gre@You haved gained 1 quest point!");
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.THURGO.id() || n.getID() == NpcId.SQUIRE.id()
			|| n.getID() == NpcId.SIR_VYVIN.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.THURGO.id()) {
			dwarfDialogue(p, n);
		} else if (n.getID() == NpcId.SQUIRE.id()) {
			squireDialogue(p, n, -1);
		} else if (n.getID() == NpcId.SIR_VYVIN.id()) {
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
			npcTalk(p, n, "We are the White Knights of Falador",
				"We are the most powerfull order of knights in the land",
				"We are helping the king Vallance rule the kingdom",
				"As he is getting old and tired");
		}
	}

	public void dwarfDialogue(final Player p, final Npc n) {
		switch (p.getQuestStage(this)) {
			case -1:
				playerTalk(p, n, "Thanks for your help in getting the sword for me");
				npcTalk(p, n, "No worries mate");
				break;
			case 0:
				p.message("Thurgo doesn't appear to be interested in talking");
				break;
			case 1:
				playerTalk(p, n, "Hello are you are an Imcando Dwarf?");
				npcTalk(p, n, "Yeah what about it?");
				break;
			case 2:
				if (!hasItem(p, ItemId.REDBERRY_PIE.id())) {
					playerTalk(p, n, "Hello are you are an Imcando Dwarf?");
					npcTalk(p, n, "Yeah what about it?");
				} else {
					int option = showMenu(p, n, "Hello are you an Imcando Dwarf?",
						"Would you like some redberry pie?");
					if (option == 0) {
						npcTalk(p, n, "Yeah what about it?");
						option = showMenu(p, n, "Would you like some redberry  Pie?",
							"Can you make me a special sword?");
						if (option == 0) {
							givePie(p, n);
						} else if (option == 1) {
							npcTalk(p, n, "no I don't do that anymore",
								"I'm getting old");
						}
					} else if (option == 1) {
						givePie(p, n);
					}
				}
				break;
			case 3:
				playerTalk(p, n, "Can you make me a special sword?");
				npcTalk(p, n, "Well after you've brought me such a great pie",
					"I guess I should give it a go",
					"What sort of sword is it?");
				playerTalk(
					p,
					n,
					"I need you to make a sword for one of Falador's knights",
					"He had one which was passed down through five generations",
					"But his squire has lost it",
					"So we need an identical one to replace it");
				npcTalk(p,
					n,
					"A Knight's sword eh?",
					"Well I'd need to know exactly how it looked",
					"Before I could make a new one",
					"All the Faladian knights used to have swords with different designs",
					"could you bring me a picture or something?");
				playerTalk(p, n, "I'll see if I can find one",
					"I'll go and ask his squire");
				p.updateQuestStage(this, 4);
				break;
			case 4:
				if (hasItem(p, ItemId.PORTRAIT.id())) {
					playerTalk(p, n,
						"I have found a picture of the sword I would like you to make");
					p.message("You give the portrait to Thurgo");
					removeItem(p, ItemId.PORTRAIT.id(), 1);
					message(p, "Thurgo studies the portrait");
					p.updateQuestStage(this, 5);
					npcTalk(p,
						n,
						"Ok you'll need to get me some stuff for me to make this",
						"I'll need two Iron bars to make the sword to start with",
						"I'll also need an ore called blurite",
						"It's useless for making actual weapons for fighting with",
						"But I'll need some as decoration for the hilt",
						"It is a fairly rare sort of ore",
						"The only place I know where to get it",
						"Is under this cliff here",
						"But it is guarded by a very powerful ice giant",
						"Most the rocks in that clif are pretty useless",
						"Don't contain much of anything",
						"But there's definitly some blurite in there",
						"You'll need a little bit of mining experience",
						"TO be able to find it");
					playerTalk(p, n, "Ok I'll go and find them");
				} else {
					npcTalk(p, n, "Have you got a picture of the sword for me yet?");
					playerTalk(p, n, "Sorry not yet");
				}
				break;
			case 5:
			case 6:
				if (hasItem(p, ItemId.FALADIAN_KNIGHTS_SWORD.id())) {
					playerTalk(p, n,
						"Thanks for your help in getting the sword for me");
					npcTalk(p, n, "No worries mate");
					return;
				}
				if (hasItem(p, ItemId.IRON_BAR.id(), 2) && hasItem(p, ItemId.BLURITE_ORE.id())) {
					npcTalk(p, n, "How are you doing finding sword materials?");
					playerTalk(p, n, "I have them all");
					message(p, "You give some blurite ore and two iron bars to Thurgo");

					removeItem(p, ItemId.IRON_BAR.id(), 1);
					removeItem(p, ItemId.IRON_BAR.id(), 1);
					removeItem(p, ItemId.BLURITE_ORE.id(), 1);
					message(p, "Thurgo starts making a sword",
						"Thurgo hammers away",
						"Thurgo hammers some more",
						"Thurgo hands you a sword");

					addItem(p, ItemId.FALADIAN_KNIGHTS_SWORD.id(), 1);
					playerTalk(p, n, "Thank you very much");
					npcTalk(p, n, "Just remember to call in with more pie some time");
					p.updateQuestStage(this, 6);
				} else {
					npcTalk(p, n, "How are you doing finding sword materials?");
					playerTalk(p, n, "I haven't found everything yet");
					npcTalk(p, n, "Well come back when you do",
						"Remember I need blurite ore and two iron bars");
				}
				break;
		}
	}

	private void givePie(Player p, Npc n) {
		message(p, "Thurgo's eyes light up");
		npcTalk(p, n, "I'd never say no to a redberry pie");
		npcTalk(p, n, "It's great stuff");
		if (!hasItem(p, ItemId.REDBERRY_PIE.id())) { //should not happen here
			playerTalk(p, n, "Well that's too bad, because I don't have any");
			message(p, "Thurgo does not look impressed");
		} else {
			message(p, "You hand over the pie");
			removeItem(p, ItemId.REDBERRY_PIE.id(), 1);
			p.updateQuestStage(this, 3);
			message(p, "Thurgo eats the pie", "Thurgo pats his stomach");
			npcTalk(p, n, "By Guthix that was good pie",
				"Anyone who makes pie like that has gotta be alright");
		}
	}

	public void squireDialogue(final Player p, final Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
				case -1:
					npcTalk(p, n, "Hello friend", "thanks for your help before",
						"Vyvin never even realised it was a different sword");
					break;
				case 0:
					npcTalk(p, n, "Hello I am the squire to Sir Vyvin");
					int option = showMenu(p, n, "And how is life as a squire?",
						"Wouldn't you prefer to be a squire for me?");
					if (option == 0) {
						npcTalk(p, n, "Well Sir Vyvin is a good guy to work for",
							"However I'm in a spot of trouble today",
							"I've gone and lost Sir Vyvin's sword");
						option = showMenu(p, n, "Do you know where you lost it?",
							"I can make a new sword if you like",
							"Is he angry?");
						if (option == 0) {
							npcTalk(p, n, "Well now if I knew that",
								"It wouldn't be lost,now would it?");

							squireDialogue(p, n, Squire.MAIN);
						} else if (option == 1) {
							squireDialogue(p, n, Squire.NEW_SWORD);
						} else if (option == 2) {
							squireDialogue(p, n, Squire.ANGRY);
						}
					} else if (option == 1) {
						npcTalk(p, n, "No, sorry I'm loyal to Vyvin");
					}
					break;
				case 1:
				case 2:
				case 3:
					npcTalk(p, n, "So how are you doing getting a sword?");
					playerTalk(p, n, "I'm still looking for Imcando dwarves");
					break;
				case 4:
					npcTalk(p, n, "So how are you doing getting a sword?");
					playerTalk(p, n, "I've found an Imcando dwarf",
						"But he needs a picture of the sword before he can make it");
					npcTalk(p,
						n,
						"A picture eh?",
						"The only one I can think of is in a small portrait of Sir Vyvin's father",
						"Sir Vyvin keeps it in a cupboard in his room I think");
					break;
				case 5:
				case 6:
					if (hasItem(p, ItemId.FALADIAN_KNIGHTS_SWORD.id())) {
						playerTalk(p, n, "I have retrieved your sword for you");
						npcTalk(p, n, "Thankyou, Thankyou",
							"I was seriously worried I'd have to own up to Sir Vyvin");
						p.message("You give the sword to the squire");
						removeItem(p, ItemId.FALADIAN_KNIGHTS_SWORD.id(), 1);
						p.sendQuestComplete(getQuestId());
					} else {
						npcTalk(p, n, "So how are you doing getting a sword?");
						playerTalk(p, n, "I've found a dwarf who will make the sword",
							"I've just got to find the materials for it now");
					}
					break;
			}
		}
		switch (cID) {
			case Squire.MAIN:
				int option = showMenu(p, n, false, //do not send over
					"Well do you know the vague area you lost it?",
					"I can make a new sword if you like",
					"Well the kingdom is fairly abundant with swords",
					"Is he angry?");
				if (option == 0) {
					playerTalk(p, n, "Well do you know the vague area you lost it in?");
					squireDialogue(p, n, Squire.LOST_IT);
				} else if (option == 1) {
					playerTalk(p, n, "I can make a new sword if you like");
					squireDialogue(p, n, Squire.NEW_SWORD);
				} else if (option == 2) {
					playerTalk(p, n, "Well the kingdom is fairly abundant with swords");
					squireDialogue(p, n, Squire.ABUNDANT);
				} else if (option == 3) {
					playerTalk(p, n, "Is he angry?");
					squireDialogue(p, n, Squire.ANGRY);
				}
				break;
			case Squire.LOST_IT:
				npcTalk(p,
					n,
					"No I was carrying it for him all the way from where he had it stored in Lumbridge",
					"It must have slipped from my pack during the trip",
					"And you know what people are like these days",
					"Someone will have just picked it up and kept it for themselves");
				int option1 = showMenu(p, n, "I can make a new sword if you like",
					"Well the kingdom is fairly abundant with swords",
					"Well I hope you find it soon");
				if (option1 == 0) {
					squireDialogue(p, n, Squire.NEW_SWORD);
				} else if (option1 == 1) {
					squireDialogue(p, n, Squire.ABUNDANT);
				} else if (option1 == 2) {
					squireDialogue(p, n, Squire.FIND_IT);
				}
				break;
			case Squire.NEW_SWORD:
				npcTalk(p, n, "Thanks for the offer",
					"I'd be surprised if you could though");
				squireDialogue(p, n, Squire.DWARF_CHAT);
				break;
			case Squire.ABUNDANT:
				npcTalk(p, n, "Yes you can get bronze swords anywhere",
					"But this isn't any old sword");
				squireDialogue(p, n, Squire.DWARF_CHAT);
				break;
			case Squire.ANGRY:
				npcTalk(p, n, "He doesn't know yet",
					"I was hoping I could think of something to do",
					"Before he does find out",
					"But I find myself at a loss");
				squireDialogue(p, n, Squire.MAIN);
				break;
			case Squire.FIND_IT:
				npcTalk(p, n, "Yes me too",
					"I'm not looking forward to telling Vyvin I've lost it",
					"He's going to want it for the parade next week as well");
				break;
			case Squire.DWARF_CHAT:
				npcTalk(p,
					n,
					"The thing is,this sword is a family heirloom",
					"It has been passed down through Vyvin's family for five generations",
					"It was originally made by the Imcando Dwarves",
					"Who were a particularly skilled tribe of dwarven smiths",
					"I doubt anyone could make it in the style they do");

				int option11 = showMenu(p, n,
					"So would these dwarves make another one?",
					"Well I hope you find it soon");
				if (option11 == 0) {
					npcTalk(p,
						n,
						"I'm not a hundred percent sure the Imcando tribe exists anymore",
						"I should think Reldo the palace librarian in Varrock will know",
						"He has done a lot of research on the races of Runescape",
						"I don't suppose you could try and track down the Imcando dwarves for me?",
						"I've got so much work to do");

					int option2 = showMenu(p, n, "Ok I'll give it a go",
						"No I've got lots of mining work to do");
					if (option2 == 0) {
						npcTalk(p, n, "Thankyou very much",
							"As I say the best place to start should be with Reldo");
						p.updateQuestStage(this, 1);
					}
				} else if (option11 == 1) {
					squireDialogue(p, n, Squire.FIND_IT);
				}
				break;
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return (obj.getID() == VYVINS_CUPBOARD_OPEN || obj.getID() == VYVINS_CUPBOARD_CLOSED) && obj.getY() == CUPBOARD_Y
				&& obj.getX() == 318;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		final Npc n = World.getWorld().getNpc(138, 316, 320, 2454, 2459);
		if ((obj.getID() == VYVINS_CUPBOARD_OPEN || obj.getID() == VYVINS_CUPBOARD_CLOSED) && obj.getY() == CUPBOARD_Y
			&& obj.getX() == 318) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, p, VYVINS_CUPBOARD_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, p, VYVINS_CUPBOARD_CLOSED);
			} else {
				if (n != null) {
					if (!n.isBusy()) {
						n.face(p);
						p.face(n);
						npcTalk(p, n, "Hey what are you doing?",
							"That's my cupboard");
						message(p,
							"Maybe you need to get someone to distract Sir Vyvin for you");
					} else {
						if (hasItem(p, ItemId.PORTRAIT.id()) || p.getQuestStage(this) < 4) {
							p.message("There is just a load of junk in here");
							return;
						}
						p.message("You find a small portrait in here which you take");
						addItem(p, ItemId.PORTRAIT.id(), 1);
					}
				}
			}
		}
	}

	class Squire {
		public static final int ANGRY = 6;
		public static final int DWARF_CHAT = 5;
		public static final int ABUNDANT = 4;
		public static final int FIND_IT = 3;
		public static final int NEW_SWORD = 2;
		public static final int LOST_IT = 1;
		public static final int MAIN = 0;

	}

}
