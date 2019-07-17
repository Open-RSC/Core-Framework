package com.openrsc.server.plugins.quests.members.grandtree;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.closeCupboard;
import static com.openrsc.server.plugins.Functions.doGate;
import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.incQuestReward;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.movePlayer;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.npcYell;
import static com.openrsc.server.plugins.Functions.openCupboard;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.replaceObjectDelayed;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.spawnNpc;

public class GrandTree implements QuestInterface, TalkToNpcListener, TalkToNpcExecutiveListener, ObjectActionListener, ObjectActionExecutiveListener, PlayerAttackNpcListener, PlayerAttackNpcExecutiveListener, PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	/***********************************
	 * Hazelmere coords: 535, 754       *
	 * King narnode coords: 416, 165    *
	 ***********************************/
	//the CAGE (prison) on top of the grand tree has open option but has no effect

	private static final int GLOUGHS_CUPBOARD_OPEN = 620;
	private static final int GLOUGHS_CUPBOARD_CLOSED = 619;
	private static final int GLOUGH_CHEST_OPEN = 631;
	private static final int GLOUGH_CHEST_CLOSED = 632;

	private static final int TREE_LADDER_UP = 585;
	private static final int TREE_LADDER_DOWN = 586;
	private static final int SHIPYARD_GATE = 624;
	private static final int STRONGHOLD_GATE = 626;

	private static final int WATCH_TOWER_UP = 635;
	private static final int WATCH_TOWER_DOWN = 646;
	private static final int WATCH_TOWER_STONE_STAND = 634;

	private static final int ROOT_ONE = 609;
	private static final int ROOT_TWO = 610;
	private static final int ROOT_THREE = 637;
	private static final int PUSH_ROOT = 638;
	private static final int PUSH_ROOT_BACK = 639;

	@Override
	public int getQuestId() {
		return Constants.Quests.GRAND_TREE;
	}

	@Override
	public String getQuestName() {
		return "Grand tree (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("well done you have completed the grand tree quest");
		int[] questData = Quests.questData.get(Quests.GRAND_TREE);
		//keep order kosher
		int[] skillIDs = {SKILLS.AGILITY.id(), SKILLS.ATTACK.id(), SKILLS.MAGIC.id()};
		//1600 for agility, 1600 for attack, 600 for magic
		int[] baseAmounts = {1600, 1600, 600};
		//1200 for agility, 1200 for attack, 200 for magic
		int[] varAmounts = {1200, 1200, 200};
		for (int i = 0; i < skillIDs.length; i++) {
			questData[Quests.MAPIDX_SKILL] = skillIDs[i];
			questData[Quests.MAPIDX_BASE] = baseAmounts[i];
			questData[Quests.MAPIDX_VAR] = varAmounts[i];
			incQuestReward(p, questData, i == (skillIDs.length - 1));
		}
		p.message("@gre@You haved gained 5 quest points!");
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.KING_NARNODE_SHAREEN.id(), NpcId.KING_NARNODE_SHAREEN_UNDERGROUND.id(),
				NpcId.HAZELMERE.id(), NpcId.GLOUGH.id(), NpcId.CHARLIE.id(), NpcId.SHIPYARD_WORKER_WHITE.id(), NpcId.SHIPYARD_WORKER_BLACK.id(),
				NpcId.SHIPYARD_FOREMAN.id(), NpcId.SHIPYARD_FOREMAN_HUT.id(), NpcId.FEMI.id(), NpcId.FEMI_STRONGHOLD.id(), NpcId.ANITA.id()}, n.getID());
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.KING_NARNODE_SHAREEN.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					playerTalk(p, n, "hello there");
					npcTalk(p, n, "hello traveller, i'm king shareem, welcome",
						"it's nice to see an outsider");
					playerTalk(p, n, "it seems to be quite a busy settlement");
					npcTalk(p, n, "for now it is, thankfully");
					p.message("King shareem seems troubled");
					int option = showMenu(p, n, "you seem worried, what's wrong?",
						"well, i'll be on my way");
					if (option == 0) {
						npcTalk(p, n, "adventurer, can i speak to you in the strictest confidence");
						playerTalk(p, n, "of course narnode");
						npcTalk(p, n, "not here, follow me");
						p.message("king shareem bends down and places his hands on the stone tile");
						message(p, "you here a creak as he turns the tile clockwise",
							"the tile slides away, revealing a small tunnel",
							"you follow king shareem down");
						movePlayer(p, 703, 3284);
						n = getNearestNpc(p, NpcId.KING_NARNODE_SHAREEN_UNDERGROUND.id(), 15);
						if (n != null) {
							playerTalk(p, n, "so what is this place?");
							npcTalk(p, n, "these my friend, are the foundations of the stronghold");
							playerTalk(p, n, "they just look like roots");
							npcTalk(p, n, "not any roots traveller",
								"these were conjured in the past age by gnome mages",
								"since then, they have grown into our mighty stronghold");
							playerTalk(p, n, "impressive, but what exactly is the problem?");
							npcTalk(p, n, "in the last two months our tree guardians have reported...",
								"...continuing deterioration of the grand trees health",
								"i've never seen this before, it could mean the end for all of us");
							playerTalk(p, n, "you mean the tree is ill");
							npcTalk(p, n, "in a magical sense yes",
								"would you be willing to help us discover the cause of this illness");
							int op = showMenu(p, n, "i'm sorry i don't want to get involved",
								"i'd be happy to help");
							if (op == 0) {
								npcTalk(p, n, "i understand traveller",
									"please keep this to yourself");
								playerTalk(p, n, "of course");
								npcTalk(p, n, "i'll show you the way back up");
								p.message("you follow king shareem up the ladder");
								p.teleport(415, 163);
							} else if (op == 1) {
								npcTalk(p, n, "thank guthix for you arrival",
									"the first task is to find out what's killing my tree");
								playerTalk(p, n, "have you any ideas?");
								npcTalk(p, n,
									"my top tree guardian, glough, believes it's human sabotage",
									"i'm not so sure",
									"the only way to really know, is to talk to Hazelmere");
								playerTalk(p, n, "who's hazelmere?");
								npcTalk(p, n, "a once all powerful mage who created the grand tree",
									"one of the only survivors of the old age",
									"take this bark sample to him, he should be able to help",
									"the mage only talks in the old tongue, you'll need this");
								playerTalk(p, n, "what is it?");
								npcTalk(p, n, "a translation book, translate carefully, his words may save us all",
									"you'll find his dwellings high upon a towering hill..",
									"..on a island south of the khazard fight arena");
								p.message("king shareem gives you a book and a bark sample");
								addItem(p, ItemId.TREE_GNOME_TRANSLATION.id(), 1);
								addItem(p, ItemId.BARK_SAMPLE.id(), 1);
								npcTalk(p, n, "i'll show you the way back up");
								p.message("you follow king shareem up the ladder");
								movePlayer(p, 415, 163);
								p.updateQuestStage(this, 1);
								//no longer needed
								p.getCache().remove("helped_femi");
							}
						}
					} else if (option == 1) {
						npcTalk(p, n, "ok then, enjoy your stay with us",
							"there's many shops and sights to see");
					}
					break;
				case 1:
					playerTalk(p, n, "hello king shareem");
					npcTalk(p, n, "traveller, you've returned",
						"any word from hazelmere?");
					playerTalk(p, n, "not yet i'm afraid");
					if (!hasItem(p, ItemId.TREE_GNOME_TRANSLATION.id()) || !hasItem(p, ItemId.BARK_SAMPLE.id())) {
						if (!hasItem(p, ItemId.BARK_SAMPLE.id())) {
							playerTalk(p, n, "but i've lost the bark sample");
							npcTalk(p, n, "here take another and try to hang on to it");
							p.message("king shareem gives you another bark sample");
							addItem(p, ItemId.BARK_SAMPLE.id(), 1);
						}
						if (!hasItem(p, ItemId.TREE_GNOME_TRANSLATION.id())) {
							playerTalk(p, n, "but i've lost the book you gave me");
							npcTalk(p, n, "don't worry i have more",
								"here you go");
							p.message("king shareem gives you a translation book");
							addItem(p, ItemId.TREE_GNOME_TRANSLATION.id(), 1);
						}
					} else {
						npcTalk(p, n, "hazalmere lives on a island just south of the fight arena",
							"give him the sample and translate his reply",
							"i just hope he can help in our hour of need");
					}
					break;
				case 2:
					playerTalk(p, n, "hello again king shareem");
					npcTalk(p, n, "well hello traveller, did you speak to hazelmere?");
					playerTalk(p, n, "yes, i managed to find him");
					npcTalk(p, n, "and do you know what he said?");
					int menu = showMenu(p, n,
						"i think so",
						"no, i need to go back");
					if (menu == 0) {
						npcTalk(p, n, "so what did he say?");
						int qmenu = showMenu(p,
							"hello there traveller",
							"king shareem must be stopped",
							"praise to the great zamorak",
							"have you any bread",
							"none of the above");
						if (qmenu == 0) {
							questionMenu2(p, n);
						} else if (qmenu == 1) {
							questionMenu2(p, n);
						} else if (qmenu == 2) {
							questionMenu2(p, n);
						} else if (qmenu == 3) {
							questionMenu2(p, n);
						} else if (qmenu == 4) {
							questionMenu2(p, n);
							if (!p.getCache().hasKey("gt_q1")) {
								p.getCache().store("gt_q1", true);
							}
						}
					} else if (menu == 1) {
						if (!hasItem(p, ItemId.TREE_GNOME_TRANSLATION.id()) || !hasItem(p, ItemId.BARK_SAMPLE.id())) {
							if (!hasItem(p, ItemId.BARK_SAMPLE.id())) {
								playerTalk(p, n, "but i've lost the bark sample");
								npcTalk(p, n, "here take another and try to hang on to it");
								p.message("king shareem gives you another bark sample");
								addItem(p, ItemId.BARK_SAMPLE.id(), 1);
							}
							if (!hasItem(p, ItemId.TREE_GNOME_TRANSLATION.id())) {
								playerTalk(p, n, "but i've lost the book you gave me");
								npcTalk(p, n, "don't worry i have more",
									"here you go");
								p.message("king shareem gives you a translation book");
								addItem(p, ItemId.TREE_GNOME_TRANSLATION.id(), 1);
							}
						}
						npcTalk(p, n, "time is of the essence adventurer");
					}
					break;
				case 3:
					playerTalk(p, n, "hello narnode");
					npcTalk(p, n, "hello traveller, did you speak to glough?");
					playerTalk(p, n, "not yet");
					npcTalk(p, n, "ok, he lives just in front of the grand tree",
						"let me know once you've spoken to him");
					break;
				case 4:
					playerTalk(p, n, "hello king shareem",
						"have you any news on the daconia stones?");
					npcTalk(p, n, "it's ok traveller, thank's to glough",
						"he found a human sneaking around...",
						"...with three daconia stones in his satchel");
					playerTalk(p, n, "i'm amazed that you retrieved them so easily");
					npcTalk(p, n, "yes, glough must really know what he's doing",
						"the human has been detained until we know who's involved",
						"maybe glough was right, maybe humans are invading");
					playerTalk(p, n, "i doubt it, can i speak to the prisoner");
					npcTalk(p, n, "certainly, he's on the top level of the grand tree",
						"be careful up there, it's a long way down");
					p.updateQuestStage(this, 5);
					break;
				case 5:
					playerTalk(p, n, "hi narnode");
					npcTalk(p, n, "hello traveller",
						"if you wish to talk to the prisoner",
						"go to the top tree level",
						"you'll find him there");
					playerTalk(p, n, "thanks");
					break;
				case 6:
					playerTalk(p, n, "king shareem");
					npcTalk(p, n, "hello adventurer, so did you speak to the culprit?");
					playerTalk(p, n, "yes i did and something's not right");
					npcTalk(p, n, "what do you mean?");
					playerTalk(p, n, "the prisoner claims he was paid by glough to get the stones");
					npcTalk(p, n, "that's an absurd story, he's just trying to save himself",
						"since glough's wife died he has been a little strange",
						"but he would never wrongly imprison someone",
						"now the culprit's locked up we can all relax",
						"it's sad but i think glough was right",
						"humans are planning to invade and wipe us tree gnomes out");
					playerTalk(p, n, "but why?");
					npcTalk(p, n, "who knows? but you may have to leave soon adventurer",
						"i trust you, but the local gnomes are getting paranoid");
					playerTalk(p, n, "that's a shame");
					npcTalk(p, n, "hopefully i can keep my people calm, we'll see");
					break;
				case 7:
					playerTalk(p, n, "king shareem, i'm concerned about glough");
					npcTalk(p, n, "why, don't worry yourself about him",
						"now the culprit has been caught...",
						"..i'm sure glough's resentment of humans will die away");
					playerTalk(p, n, "i'm not so sure");
					npcTalk(p, n, "he just has an active imagination",
						"if your really concerned, speak to him");
					break;
				case 8:
				case 9:
					playerTalk(p, n, "hello narnode");
					npcTalk(p, n, "traveller, haven't you heard",
						"glough has set a warrant for your arrest",
						"he has guards at the exit",
						"i shouldn't have told you this",
						"but i can see your a good person",
						"please take the glider and leave before it's too late");
					playerTalk(p, n, "all the best narnode");
					break;
				case 10:
					playerTalk(p, n, "king shareem,i need to talk");
					npcTalk(p, n, "traveller, what are you doing here?",
						"the stronghold has been put on full alert",
						"it's not safe for you here");
					playerTalk(p, n, "narnode, i believe glough is killing the trees",
						"in order to make a mass fleet of warships");
					npcTalk(p, n, "that's an absurd accusation");
					playerTalk(p, n, "his hatred for humanity is stronger than you know");
					npcTalk(p, n, "that's enough traveller, you sound as paranoid as him",
						"traveller please leave",
						"it's bad enough having one human locked up");
					break;
				case 11:
					playerTalk(p, n, "hello narnode");
					npcTalk(p, n, "please traveller, if the gnomes see me talking to you",
						"they'll revolt against me");
					playerTalk(p, n, "that's crazy");
					npcTalk(p, n, "glough's scared the whole town",
						"he expects the humans to attack any day",
						"he's even began to recuit hundreds of gnome soldiers");
					playerTalk(p, n, "don't you understand he's creating his own army");
					npcTalk(p, n, "please traveller, just leave before it's too late");
					break;
				case 12:
					playerTalk(p, n, "look, i found this at glough's home");
					message(p, "you give the king the strategic notes");
					removeItem(p, ItemId.GLOUGHS_NOTES.id(), 1);
					npcTalk(p, n, "hmmm, these are interesting",
						"but it's not proof, any one could have made these",
						"traveller, i understand your concern",
						"i had guards search glough's house",
						"but they found nothing suspicious",
						"just these old pebbles");
					message(p, "narnode gives you four old pebbles");
					addItem(p, ItemId.PEBBLE_3.id(), 1);
					addItem(p, ItemId.PEBBLE_2.id(), 1);
					addItem(p, ItemId.PEBBLE_4.id(), 1);
					addItem(p, ItemId.PEBBLE_1.id(), 1);
					npcTalk(p, n, "on the other hand, if glough's right about the humans",
						"we will need an army of gnomes to protect ourselves",
						"so i've decided to allow glough to raise a mighty gnome army",
						"the grand tree's still slowly dying, if it is human sabotage",
						"we must respond");
					p.updateQuestStage(this, 13);
					break;
				case 13:
					playerTalk(p, n, "hello again narnode");
					npcTalk(p, n, "please traveller, take my advice and leave");
					if (!hasItem(p, ItemId.PEBBLE_3.id()) || !hasItem(p, ItemId.PEBBLE_2.id())
							|| !hasItem(p, ItemId.PEBBLE_4.id()) || !hasItem(p, ItemId.PEBBLE_1.id())) {
						playerTalk(p, n, "have you any more of those pebbles");
						npcTalk(p, n, "well, yes as it goes, why?");
						playerTalk(p, n, "i lost some");
						npcTalk(p, n, "here take these, i don't see how it will help though");
						message(p, "narnode replaces your lost pebbles");
						if (p.getCache().hasKey("pebble_1")) {
							p.getCache().remove("pebble_1");
						}
						if (p.getCache().hasKey("pebble_2")) {
							p.getCache().remove("pebble_2");
						}
						if (p.getCache().hasKey("pebble_3")) {
							p.getCache().remove("pebble_3");
						}
						if (p.getCache().hasKey("pebble_4")) {
							p.getCache().remove("pebble_4");
						}
						if (!hasItem(p, ItemId.PEBBLE_3.id())) {
							addItem(p, ItemId.PEBBLE_3.id(), 1);
						}
						if (!hasItem(p, ItemId.PEBBLE_2.id())) {
							addItem(p, ItemId.PEBBLE_2.id(), 1);
						}
						if (!hasItem(p, ItemId.PEBBLE_4.id())) {
							addItem(p, ItemId.PEBBLE_4.id(), 1);
						}
						if (!hasItem(p, ItemId.PEBBLE_1.id())) {
							addItem(p, ItemId.PEBBLE_1.id(), 1);
						}
					} else if (hasItem(p, ItemId.PEBBLE_3.id()) && hasItem(p, ItemId.PEBBLE_2.id())
							&& hasItem(p, ItemId.PEBBLE_4.id()) && hasItem(p, ItemId.PEBBLE_1.id())) {
						npcTalk(p, n, "it's not safe for you here");
					}
					break;
				case 14:
					playerTalk(p, n, "narnode, it's true about glough i tell you",
						"he's planning to take over runescape");
					npcTalk(p, n, "i'm sorry traveller but it's just not realistic",
						"how could glough- even with a gnome army- take over?");
					playerTalk(p, n, "he plans to make a fleet of warships from the grand tree's wood");
					npcTalk(p, n, "that's enough traveller, i've no time for make believe",
						"the tree's still dying, i must get to the truth of this");
					break;
				case -1:
					playerTalk(p, n, "hello narnode");
					npcTalk(p, n, "well hello again adventurer",
						"how are you?");
					playerTalk(p, n, "i'm good thanks, how's the tree?");
					npcTalk(p, n, "better than ever, thanks for asking");
					break;
			}
		}
		else if (n.getID() == NpcId.HAZELMERE.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					message(p, "the mage mumbles in an ancient tounge",
						"you can't understand a word");
					break;
				case 1:
					playerTalk(p, n, "hello");
					if (hasItem(p, ItemId.BARK_SAMPLE.id())) {
						message(p, "you give the mage the bark sample");
						removeItem(p, ItemId.BARK_SAMPLE.id(), 1);
						message(p, "the mage speaks in a strange ancient tongue",
							"he says....");
						strangeTranslationBox(p);
						p.updateQuestStage(this, 2);
					} else {
						message(p, "the mage mumbles in an ancient tounge",
							"you can't understand a word");
						message(p, "you need to give him the bark sample");
					}
					break;
				case 2:
					message(p, "the mage speaks in a strange ancient tongue",
						"he says....");
					strangeTranslationBox(p);
					break;
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case -1:
					p.message("the mage mumbles in an ancient tounge");
					p.message("you can't understand a word");
					break;
			}
		}
		else if (n.getID() == NpcId.GLOUGH.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
				case 4:
				case 5:
				case 6:
				case 8:
				case 9:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case -1:
					playerTalk(p, n, "hello there");
					npcTalk(p, n, "you shouldn't be here human");
					playerTalk(p, n, "what do you mean?");
					npcTalk(p, n, "the gnome stronghold is for gnomes alone");
					playerTalk(p, n, "surely not!");
					npcTalk(p, n, "we don't need you're sort around here");
					message(p, "he doesn't seem very nice");
					break;
				case 3:
					playerTalk(p, n, "hello");
					message(p, "the gnome is munching on a worm hole");
					npcTalk(p, n, "can i help human, can't you see i'm eating?",
						"these are my favourite");
					message(p, "the gnome continues to eat");
					playerTalk(p, n, "the king asked me to inform you...",
						"that the daconia rocks have been taken");
					npcTalk(p, n, "surley not!");
					playerTalk(p, n, "apparently a human took them from hazelmere",
						"he had a permission note with the king's seal");
					npcTalk(p, n, "i should have known, the humans are going to invade");
					playerTalk(p, n, "never");
					npcTalk(p, n, "your type can't be trusted",
						"i'll take care of this, you go back to the king");
					p.updateQuestStage(this, 4);
					break;
				case 7:
					playerTalk(p, n, "glough, i don't know what you're up to...",
						"...but i know you paid charlie to get those rocks");
					npcTalk(p, n, "you're a fool human",
						"you have no idea whats going on");
					playerTalk(p, n, "i know the grand tree's dying",
						"and i think you're part of the reason");
					npcTalk(p, n, "how dare you accuse me, i'm the head tree guardian",
						"guards...guards");
					p.message("gnome guards hurry up the ladder");
					// 418, 2992
					Npc gnome_guard = spawnNpc(NpcId.GNOME_GUARD_PRISON.id(), 714, 1421, 12000);
					npcTalk(p, n, "take him away");
					p.face(gnome_guard);
					gnome_guard.face(p);
					playerTalk(p, n, "what for?");
					npcTalk(p, n, "grand treason against his majesty king shareem",
						"this man is a human spy");
					gnome_guard.remove();
					npcTalk(p, n, "lock him up");
					message(p, "the gnome guards take you to the top of the grand tree");
					p.teleport(419, 2992);
					sleep(5000);
					Npc jailCharlie = getNearestNpc(p, NpcId.CHARLIE.id(), 5);
					npcTalk(p, jailCharlie, "so, they've got you as well");
					playerTalk(p, jailCharlie, "it's glough, he's trying to cover something up");
					npcTalk(p, jailCharlie, "i shouldn't tell you this adventurer",
						"but if you want to get to the bottom of this",
						"you should go and talk to the karamja foreman");
					playerTalk(p, jailCharlie, "why?");
					npcTalk(p, jailCharlie, "glough sent me to karamja to meet him",
						"i delivered a large amount of gold",
						"for what i do not know",
						"but he may be able to tell you what glough's up to",
						"that's if you can get out of here",
						"you'll find him in a ship yard south of birmhaven",
						"be careful, if he discovers that you're not...",
						"...working for glough there'll be trouble",
						"the sea men use the pass word ka-lu-min");
					playerTalk(p, jailCharlie, "thanks charlie");
					sleep(5000);
					Npc narnode = spawnNpc(NpcId.KING_NARNODE_SHAREEN.id(), 419, 2993, 36000);
					npcTalk(p, narnode, "adventurer please accept my apologies",
						"glough had no right to arrest you",
						"i just think he's scared of humans",
						"let me get you out of there");
					message(p, "king shareem opens the cage");
					p.teleport(418, 2993);
					playerTalk(p, narnode, "i don't think you can trust glough, narnode",
						"he seems to have a unatural hatred for humans");
					npcTalk(p, narnode, "i know he can seem a little extreme at times",
						"but he's the best tree guardian i have",
						"he has however caused much fear towards humans",
						"i'm afraid he's placed guards on the front gate...",
						"...to stop you escaping",
						"let my glider pilot fly you away",
						"untill things calm down around here");
					playerTalk(p, narnode, "well, if that's how you feel");
					narnode.remove();
					p.updateQuestStage(this, 8);
					break;
				case 10:
					playerTalk(p, n, "I know what you're up to glough");
					npcTalk(p, n, "you have no idea human");
					playerTalk(p, n, "you may be able to make a fleet",
						"but the tree gnomes will never follow you into battle");
					npcTalk(p, n, "so, you know more than i thought, i'm impressed",
						"the gnomes fear humanity more than any other race",
						"i just need to give them a push in the right direction",
						"there's nothing you can do traveller",
						"leave before it's too late",
						"soon all of runescape will feel the wrath of glough");
					playerTalk(p, n, "king shareem won't allow it");
					npcTalk(p, n, "the king's a fool and a coward, he'll soon bow to me",
						"and you'll soon be back in that cage");
					break;
				case 11:
					playerTalk(p, n, "i'm going to stop you glough");
					npcTalk(p, n, "you're becoming quite annoying traveller");
					message(p, "glough is searching his pockets",
						"he seems very uptight");
					npcTalk(p, n, "damn keys",
						"leave human, before i have you put in the cage");
					break;
			}
		}
		else if (n.getID() == NpcId.CHARLIE.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case -1:
					p.message("the prisoner is in no mood to talk");
					break;
				case 5:
					playerTalk(p, n, "tell me,why would you want to kill the grand tree?");
					npcTalk(p, n, "what do you mean?");
					playerTalk(p, n, "don't tell me, you just happened to be caught carrying daconia rocks!");
					npcTalk(p, n, "all i know, is that i did what i was asked");
					playerTalk(p, n, "i don't understand?");
					npcTalk(p, n, "glough paid me to go see this gnome on a hill",
						"i gave the gnome a letter glough gave me",
						"and he gave me some rocks to give glough",
						"i've been doing it for weeks, it's just this time..",
						"...when i returned glough locked me up here",
						"i just don't understand it");
					playerTalk(p, n, "sounds like glough's hiding something");
					npcTalk(p, n, "i don't know what he's up to",
						"but if you want to find out...",
						"..you better search his home");
					playerTalk(p, n, "ok, thanks charlie");
					npcTalk(p, n, "good luck");
					p.updateQuestStage(this, 6);
					break;
				case 6:
				case 7:
					playerTalk(p, n, "hello charlie");
					npcTalk(p, n, "hello adventurer, have you figured out what's going on?");
					playerTalk(p, n, "no idea");
					npcTalk(p, n, "to get to the bottom of this you'll need to search glough's home");
					break;
				case 8:
				case 9:
					playerTalk(p, n, "i can't figure this out charlie");
					npcTalk(p, n, "go and see a forman in west karamja",
						"there's a shipyard there,you might find some clues",
						"don't forget the password's ka-lu-min",
						"if they realise that you're not working for glough...",
						"...there'll be trouble");
					break;
				case 10:
				case 11:
					playerTalk(p, n, "how are you doing charlie");
					npcTalk(p, n, "i've been better");
					playerTalk(p, n, "glough has some plan to rule runescape");
					npcTalk(p, n, "i wouldn't put it past him, the gnome's crazy");
					playerTalk(p, n, "i need some proof to convince the king");
					npcTalk(p, n, "hmmm, you could be in luck",
						"before glough had me locked up i heard him mention..",
						"..that he'd left his chest lock keys at his girlfriends");
					playerTalk(p, n, "where does she live?");
					npcTalk(p, n, "just west of the toad swamp");
					playerTalk(p, n, "okay, i'll see what i can find");
					if (p.getQuestStage(this) == 10) {
						p.updateQuestStage(this, 11);
					}
					break;
			}
		}
		else if (n.getID() == NpcId.SHIPYARD_WORKER_WHITE.id() || n.getID() == NpcId.SHIPYARD_WORKER_BLACK.id()) {
			int selected = p.getRandom().nextInt(14);
			playerTalk(p, n, "hello");
			if (selected == 0) {
				npcTalk(p, n, "ouch");
				playerTalk(p, n, "what's wrong?");
				npcTalk(p, n, "i cut my finger",
					"do you have a bandage?");
				playerTalk(p, n, "i'm afraid not");
				npcTalk(p, n, "that's ok, i'll use my shirt");
			} else if (selected == 1) {
				playerTalk(p, n, "you look busy");
				npcTalk(p, n, "we need double the men to get..",
					"...this order out on time");
			} else if (selected == 2) {
				npcTalk(p, n, "hello matey");
				playerTalk(p, n, "how are you?");
				npcTalk(p, n, "tired");
				playerTalk(p, n, "you shouldn't work so hard");
			} else if (selected == 3) {
				playerTalk(p, n, "what are you building?");
				npcTalk(p, n, "are you serious?");
				playerTalk(p, n, "of course not",
					"you're obviously building a boat");
			} else if (selected == 4) {
				playerTalk(p, n, "looks like hard work");
				npcTalk(p, n, "i like to keep busy");
			} else if (selected == 5) {
				npcTalk(p, n, "no time to talk",
					"we've a fleet to build");
			} else if (selected == 6) {
				playerTalk(p, n, "quite an impressive set up");
				npcTalk(p, n, "it needs to be...",
					"..there's no other way to build a fleet of this size");
			} else if (selected == 7) {
				playerTalk(p, n, "quite a few ships you're building");
				npcTalk(p, n, "this is just the start",
					"the completed fleet will be awesome");
			} else if (selected == 8) {
				playerTalk(p, n, "so where are you sailing?");
				npcTalk(p, n, "what do you mean?");
				playerTalk(p, n, "don't worry, just kidding!");
			} else if (selected == 9) {
				playerTalk(p, n, "how are you?");
				npcTalk(p, n, "too busy to waste time gossiping");
				playerTalk(p, n, "touchy");
			} else if (selected == 10) {
				npcTalk(p, n, "can i help you");
				playerTalk(p, n, "i'm just looking around");
				npcTalk(p, n, "well there's plenty of work to be done",
					"so if you don't mind...");
				playerTalk(p, n, "of course, sorry to have disturbed you");
			} else if (selected == 11) {
				npcTalk(p, n, "hello there",
					"are you too lazy to work as well");
				playerTalk(p, n, "something like that");
				npcTalk(p, n, "i'm just sun bathing");
			} else if (selected == 12) {
				npcTalk(p, n, "hello there");
				npcTalk(p, n, "i haven't seen you before");
				playerTalk(p, n, "i'm new");
				npcTalk(p, n, "well it's hard work, but the pay is good");
			} else if (selected == 13) {
				npcTalk(p, n, "what do you want?");
				playerTalk(p, n, "is that any way to talk to your new superior?");
				npcTalk(p, n, "oh, i'm sorry, i didn't realise");
			}
		}
		else if (n.getID() == NpcId.SHIPYARD_FOREMAN.id()) {
			if (p.getQuestStage(this) >= 10) {
				p.message("the forman is too busy to talk");
				return;
			}
			playerTalk(p, n, "hello, are you in charge?");
			npcTalk(p, n, "that's right, and you are?");
			playerTalk(p, n, "glough sent me to check up on things");
			npcTalk(p, n, "is that right, glough sent a human");
			playerTalk(p, n, "his gnomes were all busy");
			npcTalk(p, n, "ok, we had better go inside, follow me");
			p.teleport(408, 753);
			if (p.getQuestStage(this) == 8) {
				p.message("you follow the foreman into the wooden hut");
				Npc HUT_FOREMAN = getNearestNpc(p, NpcId.SHIPYARD_FOREMAN_HUT.id(), 4);
				npcTalk(p, HUT_FOREMAN, "so tell me again why you're here");
				playerTalk(p, HUT_FOREMAN, "erm...glough sent me?");
				npcTalk(p, HUT_FOREMAN, "ok and how is glough..still with his wife?");
				int menu = showMenu(p, HUT_FOREMAN,
					"yes, they're both getting on great",
					"always arguing as usual",
					"his wife is no longer with us");
				if (menu == 0 || menu == 1) {
					p.updateQuestStage(this, 9);
					npcTalk(p, HUT_FOREMAN, "really...",
						"..that's strange, considering she died last year",
						"die imposter");
					HUT_FOREMAN.setChasing(p);
				} else if (menu == 2) {
					npcTalk(p, HUT_FOREMAN, "right answear, i have to watch out for imposters", "if really know glough...", "you know his favourite gnome dish");
					int menu2 = showMenu(p, HUT_FOREMAN, "he loves tangled toads legs", "he loves worm holes", "he loves choc bombs");
					if (menu2 == 0 || menu2 == 2) {
						p.updateQuestStage(this, 9);
						npcTalk(p, HUT_FOREMAN, "he hates them",
							"die imposter");
						HUT_FOREMAN.setChasing(p);
					} else if (menu2 == 1) {
						npcTalk(p, HUT_FOREMAN, "ok, one more question", "what's the name of his new girlfriend");
						int menu3 = showMenu(p, HUT_FOREMAN, false, //do not send over
							"Alia", "Anita", "Elena");
						if (menu3 == 0 || menu3 == 2) {
							p.updateQuestStage(this, 9);
							if (menu3 == 0) {
								playerTalk(p, HUT_FOREMAN, "alia");
							} else if (menu3 == 2) {
								playerTalk(p, HUT_FOREMAN, "elena");
							}
							npcTalk(p, HUT_FOREMAN, "you almost fooled me",
								"die imposter");
							HUT_FOREMAN.setChasing(p);
						} else if (menu3 == 1) {
							playerTalk(p, HUT_FOREMAN, "anita");
							npcTalk(p, HUT_FOREMAN, "well, well ,well, you do know glough",
								"sorry for the interrogation but i'm sure you understand");
							playerTalk(p, HUT_FOREMAN, "of course, security is paramount");
							npcTalk(p, HUT_FOREMAN, "as you can see the ship builders are ready");
							playerTalk(p, HUT_FOREMAN, "indeed");
							npcTalk(p, HUT_FOREMAN, "when i was asked to build a fleet large enough...",
								"..to invade port sarim and carry 300 gnome troops...",
								"..i said if anyone can, i can");
							playerTalk(p, HUT_FOREMAN, "that's a lot of troops");
							npcTalk(p, HUT_FOREMAN, "true but if the gnomes are really going to..",
								"..take over runescape, they'll need at least that");
							playerTalk(p, HUT_FOREMAN, "take over?");
							npcTalk(p, HUT_FOREMAN, "of course, why else would glough want 30 battleships",
								"between you and me, i don't think he stands a chance");
							playerTalk(p, HUT_FOREMAN, "no");
							npcTalk(p, HUT_FOREMAN, "i mean, for the kind of battleships glough's ordered..",
								"..i'll need ton's and ton's of timber",
								"more than any forest i can think of could supply",
								"still, if he say's he can supply the wood i'm sure he can",
								"any way, here's the invoice");
							playerTalk(p, HUT_FOREMAN, "ok, thanks");
							npcTalk(p, HUT_FOREMAN, "i'll need the wood as soon as possible",
								"if the orders going to be finished in time");
							playerTalk(p, HUT_FOREMAN, "ok i'll tell glough");
							message(p, "the foreman hands you the invoice");
							addItem(p, 922, 1);
							p.updateQuestStage(this, 10);
						}
					}
				}
			} else if (p.getQuestStage(this) == 9) {
				Npc HUT_FOREMAN = getNearestNpc(p, NpcId.SHIPYARD_FOREMAN_HUT.id(), 4);
				npcYell(p, n, "die imposter");
				HUT_FOREMAN.setChasing(p);
			}
		}
		else if (n.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id()) {
			if (p.getQuestStage(this) == 10) {
				p.message("the forman is too busy to talk");
				return;
			}
			npcYell(p, n, "die imposter");
			n.setChasing(p);
		}
		else if (n.getID() == NpcId.FEMI.id()) {
			switch (p.getQuestStage(this)) {
				case 10:
					playerTalk(p, n, "i can't believe they won't let me in");
					npcTalk(p, n, "i don't believe all this rubbish about an invasion",
						"if mankind wanted to, they could have invaded before now");
					playerTalk(p, n, "i really need to see king shareem",
						"could you help sneak me in");
					npcTalk(p, n, "well, as you helped me i suppose i could",
						"we'll have to be careful",
						"if i get caught i'll be in the cage");
					playerTalk(p, n, "ok, what should i do");
					npcTalk(p, n, "jump in the back of the cart",
						"it's a food delivery, we should be fine");
					message(p, "you hide in the cart",
						"femi covers you with a sheet...",
						"...and drags the cart to the gate",
						"femi pulls you into the stronghold");
					p.teleport(708, 510);
					Npc femi = getNearestNpc(p, NpcId.FEMI_STRONGHOLD.id(), 2);
					npcTalk(p, femi, "ok traveller, you'd better get going");
					playerTalk(p, femi, "thanks again femi");
					npcTalk(p, femi, "that's ok, all the best");
					break;
				default:
					p.message("the little gnome is too busy to talk");
					break;
			}
		}
		else if (n.getID() == NpcId.FEMI_STRONGHOLD.id()) {
			p.message("the little gnome is too busy to talk");
		}
		else if (n.getID() == NpcId.ANITA.id()) {
			switch (p.getQuestStage(this)) {
				case 11:
					playerTalk(p, n, "hello there");
					npcTalk(p, n, "oh hello, i've seen you with the king");
					playerTalk(p, n, "yes, i'm helping him with a problem");
					npcTalk(p, n, "you must know my boy friend glough then");
					playerTalk(p, n, "indeed!");
					npcTalk(p, n, "could you do me a favour?");
					playerTalk(p, n, "i suppose so");
					npcTalk(p, n, "give this key to glough",
						"he left it here last night");
					message(p, "anita gives you a key");
					addItem(p, ItemId.GLOUGHS_KEY.id(), 1);
					npcTalk(p, n, "thanks a lot");
					playerTalk(p, n, "no, thankyou");
					break;
				default:
					p.message("anita is to busy cleaning to talk");
					break;
			}
		}
		else if (n.getID() == NpcId.KING_NARNODE_SHAREEN_UNDERGROUND.id()) { // FINALE COMPLETION
			switch (p.getQuestStage(this)) {
				case 15:
					npcTalk(p, n, "traveller you're wounded, what happened?");
					playerTalk(p, n, "it's glough, he set a demon on me");
					npcTalk(p, n, "what, glough, with a demon?");
					playerTalk(p, n, "glough has a store of daconia rocks further up the passage way",
						"he's been accessing the roots from a secret passage at his home");
					npcTalk(p, n, "never, not glough, he's a good gnome at heart",
						"guard, go and check out that passage way");
					message(p, "one of the king's guards runs of up the passage");
					npcTalk(p, n, "look, maybe it's stress playing with your mind");
					message(p, "the gnome guard returns",
						"and talks to the king");
					npcTalk(p, n, "what?, never, why that little...",
						"they found glough hiding under a horde of daconia rocks..");
					playerTalk(p, n, "that's what i've been trying to tell you",
						"glough's been fooling you");
					npcTalk(p, n, "i..i don't know what to say",
						"how could i have been so blind");
					message(p, "king shareem calls out to another guard");
					npcTalk(p, n, "guard, call off the military training",
						"the humans are not attacking",
						"you have my full apologies traveller, and my gratitude",
						"a reward will have to wait though, the tree is still dying",
						"the guards are clearing glough's rock supply now",
						"but there must be more daconia hidden somewhere in the roots",
						"please traveller help us search, we have little time");
					p.updateQuestStage(this, 16);
					break;
				case 16:
					npcTalk(p, n, "traveller, have you managed to find the rock",
						"i think there's only one");
					if (hasItem(p, ItemId.DACONIA_ROCK.id())) {
						playerTalk(p, n, "is this it?");
						npcTalk(p, n, "yes, excellent, well done");
						message(p, "you give king shareem the daconia rock");
						removeItem(p, ItemId.DACONIA_ROCK.id(), 1);
						npcTalk(p, n, "it's incredible, the tree's health is improving already",
							"i don't what to say, we owe you so much",
							"to think glough had me fooled all along");
						playerTalk(p, n, "all that matters now is that man...",
							"...and gnome can live together in peace");
						npcTalk(p, n, "i'll drink to that");
						p.sendQuestComplete(Constants.Quests.GRAND_TREE);
						npcTalk(p, n, "from now on i vow to make this stronghold",
							"a welcome place for all no matter what their creed",
							"i'll grant you access to all our facilities");
						playerTalk(p, n, "thanks, i think");
						npcTalk(p, n, "it should make your stay here easier",
							"you can use the spirit tree to transport yourself",
							"..as well as the gnome glider",
							"i also give you access to our mine");
						playerTalk(p, n, "mine?");
						npcTalk(p, n, "very few know of the secret mine under the grand tree",
							"if you push on the roots just to my north",
							"the grand tree will take you there");
						playerTalk(p, n, "strange");
						npcTalk(p, n, "that's magic trees for you",
							"all the best traveller and thanks again");
						playerTalk(p, n, "you too narnode");
					} else {
						playerTalk(p, n, "no sign of it so far");
						npcTalk(p, n, "the tree will still die if we don't find it",
							"it could be anywhere");
						playerTalk(p, n, "don't worry narnode, we'll find it");
					}
					break;
				case -1:
					playerTalk(p, n, "hello narnode");
					npcTalk(p, n, "well hello again adventurer",
						"how are you?");
					playerTalk(p, n, "i'm good thanks, how's the tree?");
					npcTalk(p, n, "better than ever, thanks for asking");
					break;
				default:
					break;
			}
		}
	}

	private void questionMenu2(final Player p, final Npc n) {
		int menu = showMenu(p,
			"you must warn the gnomes",
			"soon the eternal night will come",
			"the seven must reunite",
			"only one the fifth night",
			"none of the above");
		if (menu == 0) {
			questionMenu3(p, n);
		} else if (menu == 1) {
			questionMenu3(p, n);
		} else if (menu == 2) {
			questionMenu3(p, n);
		} else if (menu == 3) {
			questionMenu3(p, n);
		} else if (menu == 4) {
			if (!p.getCache().hasKey("gt_q2") && p.getCache().hasKey("gt_q1")) {
				p.getCache().store("gt_q2", true);
			}
			questionMenu3(p, n);
		}
	}

	private void questionMenu3(final Player p, final Npc n) {
		int menu = showMenu(p,
			"all shall peril",
			"chicken, it must be chicken",
			"and then you will know",
			"the tree will live",
			"none of the above");
		if (menu == 0) {
			wrongQuestionMenu(p, n);
		} else if (menu == 1) {
			wrongQuestionMenu(p, n);
		} else if (menu == 2) {
			wrongQuestionMenu(p, n);
		} else if (menu == 3) {
			wrongQuestionMenu(p, n);
		} else if (menu == 4) {
			if (p.getCache().hasKey("gt_q1") && p.getCache().hasKey("gt_q2")) {
				// remove to keep efficiency in cache.
				p.getCache().remove("gt_q1");
				p.getCache().remove("gt_q2");
				// Continue the last three menus.
				int realQuestionMenu = showMenu(p,
					"monster came with king's sword",
					"giant left with tree stone",
					"ogre came with king's head",
					"human came with king's seal",
					"fairy came with eternal flower");
				if (realQuestionMenu == 0) {
					wrongQuestionMenu(p, n);
				} else if (realQuestionMenu == 1) {
					wrongQuestionMenu(p, n);
				} else if (realQuestionMenu == 2) {
					wrongQuestionMenu(p, n);
				} else if (realQuestionMenu == 3) {
					int realQuestionMenu2 = showMenu(p,
						"gave the ever-light to human",
						"gave human daconia rock",
						"gave human rock to daconia",
						"human attacked by daconia",
						"human destroyed daconia rock");
					if (realQuestionMenu2 == 0) {
						wrongQuestionMenu(p, n);
					} else if (realQuestionMenu2 == 1) {
						int realQuestionMenu3 = showMenu(p,
							"daconia rocks will save tree",
							"daconia will fall to gnome kingdom",
							"gnome kingdom will fall to daconia",
							"daconia rocks will kill tree",
							"daconia rocks killed human");
						if (realQuestionMenu3 == 0) {
							wrongQuestionMenu(p, n);
						} else if (realQuestionMenu3 == 1) {
							wrongQuestionMenu(p, n);
						} else if (realQuestionMenu3 == 2) {
							wrongQuestionMenu(p, n);
						} else if (realQuestionMenu3 == 3) {
							playerTalk(p, n, "he said a human came to him with the king's seal",
								"hazelmere gave the man daconia rocks",
								"and daconia rocks will kill the tree");
							npcTalk(p, n, "of course, i should have known",
								"some one must have forged my royal seal",
								"and convinced hazelmere that i sent for the daconia stones");
							playerTalk(p, n, "what are daconia stones?");
							npcTalk(p, n, "hazelmere created the daconia stones",
								"they were a safty measure, in case the tree grew out of control",
								"they're the only thing that can kill the tree",
								"this is terrible, those stones must be retrieved");
							playerTalk(p, n, "can i help?");
							npcTalk(p, n, "first i must warn the tree guardians",
								"please, could you tell the chief tree guardian glough",
								"he lives in a tree house just in front of the grand tree",
								"if he's not there he will be at anita's, his girlfriend",
								"meet me back here once you've told him");
							playerTalk(p, n, "ok, i'll be back soon");
							p.updateQuestStage(this, 3);
						} else if (realQuestionMenu3 == 4) {
							wrongQuestionMenu(p, n);
						}
					} else if (realQuestionMenu2 == 2) {
						wrongQuestionMenu(p, n);
					} else if (realQuestionMenu2 == 3) {
						wrongQuestionMenu(p, n);
					} else if (realQuestionMenu2 == 4) {
						wrongQuestionMenu(p, n);
					}
				} else if (realQuestionMenu == 4) {
					wrongQuestionMenu(p, n);
				}
			} else {
				wrongQuestionMenu(p, n);
			}
		}
	}

	private void wrongQuestionMenu(final Player p, final Npc n) {
		npcTalk(p, n, "wait a minute, that doesn't sound like hazelmere",
			"are you sure you translated correctly?");
		playerTalk(p, n, "erm...i think so");
		npcTalk(p, n, "i'm sorry traveller but this is no good",
			"the translation must be perfect or the infomation's no use",
			"please come back when you know exactly what hazelmere said");

		/** Remove the cache if they fail on the third question **/
		if (p.getCache().hasKey("gt_q1")) {
			p.getCache().remove("gt_q1");
		}
		if (p.getCache().hasKey("gt_q2")) {
			p.getCache().remove("gt_q2");
		}
	}

	private void shipyardPasswordMenu2(final Player p, final Npc worker) {
		int menu = showMenu(p,
			"lo",
			"lu",
			"le");
		if (menu == 0) {
			shipyardPasswordMenu3(p, worker);
		} else if (menu == 1) {
			if (!p.getCache().hasKey("gt_shipyard_q2") && p.getCache().hasKey("gt_shipyard_q1")) {
				p.getCache().store("gt_shipyard_q2", true);
			}
			shipyardPasswordMenu3(p, worker);
		} else if (menu == 2) {
			shipyardPasswordMenu3(p, worker);
		}
	}

	private void wrongShipyardPassword(final Player p, final Npc worker) {
		npcTalk(p, worker, "you have no idea");
		worker.setChasing(p);
		/** Remove the cache if they fail on the third question **/
		if (p.getCache().hasKey("gt_shipyard_q1")) {
			p.getCache().remove("gt_shipyard_q1");
		}
		if (p.getCache().hasKey("gt_shipyard_q2")) {
			p.getCache().remove("gt_shipyard_q2");
		}
	}

	private void shipyardPasswordMenu3(final Player p, final Npc worker) {
		int menu = showMenu(p,
			"mon",
			"min",
			"men");
		if (menu == 0) {
			wrongShipyardPassword(p, worker);
		} else if (menu == 1) {
			if (p.getCache().hasKey("gt_shipyard_q1") && p.getCache().hasKey("gt_shipyard_q2")) {
				// remove to keep efficiency in cache.
				p.getCache().remove("gt_q1");
				p.getCache().remove("gt_q2");
				//continue
				playerTalk(p, worker, "ka lu min");
				npcTalk(p, worker, "i'm sorry to have kept you",
					"but obviously high security is essential");
				p.teleport(402, 760);
				p.message("the worker opens the gate");
				p.message("you walk through");
				npcTalk(p, worker, "you'll need to speak to the foreman",
					"he's on the pier, it'll give you a chance..",
					"...to see the fleet");
			}
		} else if (menu == 2) {
			wrongShipyardPassword(p, worker);
		}
	}

	private void strangeTranslationBox(Player p) {
		ActionSender.sendBox(p,
			"@yel@x@red@z@yel@ql@red@:v@yel@ha @red@za@yel@:v@red@ql@yel@::: @red@h:@yel@xa@red@lat@yel@x @red@vo@yel@xa@red@ha@yel@qa@red@sol @yel@sol@red@:::@yel@:v@red@va% %"
				+
				"@yel@qa@red@:v@yel@::@red@::: @yel@x@red@z@yel@ql@red@:v@yel@ha @red@qe@yel@:v@red@ha @yel@qe@red@:v@yel@za@red@ho@yel@ha@red@xa@yel@:v @red@qi@yel@ho@red@za@yel@vo% %"
				+
				"@red@qe@yel@:v@red@za@yel@ho@red@ha@yel@xa@red@:v @yel@qi@red@ho@yel@za@red@vo@yel@sol @red@h:@yel@xa@red@va@yel@va @red@vo@yel@xa@red@va@yel@va @yel@lat@red@qi@yel@:::@red@:::"
			, true);
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return DataConversions.inArray(new int[] {GLOUGHS_CUPBOARD_OPEN, GLOUGHS_CUPBOARD_CLOSED, TREE_LADDER_UP, TREE_LADDER_DOWN, SHIPYARD_GATE, STRONGHOLD_GATE,
				GLOUGH_CHEST_CLOSED, WATCH_TOWER_UP, WATCH_TOWER_DOWN, WATCH_TOWER_STONE_STAND, ROOT_ONE, ROOT_TWO, ROOT_THREE, PUSH_ROOT, PUSH_ROOT_BACK}, obj.getID());
	}

	@Override
	public void onObjectAction(GameObject obj, String command, final Player p) {
		if (obj.getID() == GLOUGHS_CUPBOARD_OPEN || obj.getID() == GLOUGHS_CUPBOARD_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, p, GLOUGHS_CUPBOARD_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, p, GLOUGHS_CUPBOARD_CLOSED);
			} else {
				message(p, "you search the cupboard");
				if (p.getQuestStage(this) == 6) {
					message(p, "inside you find glough's journal");
					addItem(p, ItemId.GLOUGHS_JOURNAL.id(), 1);
					p.updateQuestStage(this, 7);
				} else {
					message(p, "but find nothing of interest");
				}
			}
		}
		else if (obj.getID() == TREE_LADDER_UP) {
			p.message("you climb up the ladder");
			p.teleport(417, 2994, false);
		}
		else if (obj.getID() == TREE_LADDER_DOWN) {
			p.message("you climb down the ladder");
			p.teleport(415, 2051, false);
		}
		else if (obj.getID() == SHIPYARD_GATE) {
			if (p.getY() >= 762) {
				if (p.getQuestStage(this) >= 8 && p.getQuestStage(this) <= 9) {
					message(p, "the gate is locked");
					final Npc worker = getNearestNpc(p, NpcId.SHIPYARD_WORKER_ENTRANCE.id(), 5);
					//Continue
					if (worker != null) {
						npcTalk(p, worker, "hey you, what are you up to?");
						playerTalk(p, worker, "i'm trying to open the gate");
						npcTalk(p, worker, "i can see that, but why?");
						Menu defaultMenu = new Menu();
						defaultMenu.addOption(new Option("i've come to check that you're working safley") {
							@Override
							public void action() {
								npcTalk(p, worker, "what business is that of yours?");
								playerTalk(p, worker, "as a runescape resident i have a right to know");
								npcTalk(p, worker, "get out of here before you get a beating");
								playerTalk(p, worker, "that's not very friendly");
								npcTalk(p, worker, "right, i'll show you friendly");
								worker.setChasing(p);
							}
						});
						if (p.getQuestStage(this) == 8) {
							defaultMenu.addOption(new Option("glough sent me") {
								@Override
								public void action() {
									npcTalk(p, worker, "hmm, really, what for?");
									playerTalk(p, worker, "your wasting my time, take me to your superior");
									npcTalk(p, worker, "ok, i can let you in but i need the password");
									int menu = showMenu(p,
										"Ka",
										"ko",
										"ke");
									if (menu == 0) {
										if (!p.getCache().hasKey("gt_shipyard_q1")) {
											p.getCache().store("gt_shipyard_q1", true);
										}
										shipyardPasswordMenu2(p, worker);
									} else if (menu == 1) {
										shipyardPasswordMenu2(p, worker);
									} else if (menu == 2) {
										shipyardPasswordMenu2(p, worker);
									}
								}
							});
						}
						defaultMenu.addOption(new Option("i just fancied looking around") {
							@Override
							public void action() {
								npcTalk(p, worker, "this isn't a museum",
									"leave now");
								playerTalk(p, worker, "i'll leave when i choose");
								npcTalk(p, worker, "we'll see");
								worker.setChasing(p);
							}
						});
						defaultMenu.showMenu(p);
					} else {
						message(p, "the gate is locked");
					}

				} else {
					message(p, "the gate is locked");
				}
			} else {
				p.message("you open the gate");
				p.message("and walk through");
				doGate(p, obj, 623);
				p.teleport(401, 763);
			}
		}
		else if (obj.getID() == STRONGHOLD_GATE) {
			if (p.getY() <= 531 || p.getQuestStage(this) != 0) {
				doGate(p, obj, 181);
			}
			else {
				if(p.getCache().hasKey("helped_femi")) {
					doGate(p, obj, 181);
				}
				else {
					Npc femi = getNearestNpc(p, NpcId.FEMI.id(), 10);
					if (femi != null) {
						npcTalk(p, femi, "hello there");
						playerTalk(p, femi, "hi");
						npcTalk(p, femi, "could you help me lift this barrel",
								"it's really heavy");
						int menu = showMenu(p, femi, "sorry i'm a bit busy", "ok then");
						if (menu == 0) {
							npcTalk(p, femi, "oh, ok, i'll do it myself");
							p.getCache().store("helped_femi", false);
						}
						else if (menu == 1) {
							npcTalk(p, femi, "thanks traveller");
							message(p, "you help the gnome lift the barrel",
									"it's very heavy and quite hard work");
							npcTalk(p, femi, "thanks again friend");
							p.getCache().store("helped_femi", true);
						}
					} else {
						p.message("the little gnome is busy at the moment");
					}
				}
			}
		}
		else if (obj.getID() == GLOUGH_CHEST_CLOSED) {
			message(p, "the chest is locked...",
				"...you need a key");
		}
		else if (obj.getID() == WATCH_TOWER_UP) {
			if (getCurrentLevel(p, SKILLS.AGILITY.id()) >= 25) {
				p.message("you jump up and grab hold of the platform");
				p.teleport(710, 2364);
				p.incExp(SKILLS.AGILITY.id(), 30, true);
				sleep(3000);
				p.message("and pull yourself up");
			} else {
				p.message("You need an agility level of 25 to climb up the platform");
			}
		}
		else if (obj.getID() == WATCH_TOWER_DOWN) {
			message(p, "you climb down the tower");
			p.teleport(712, 1420);
			message(p, "and drop to the platform below");
		}
		// 711  3306 me.
		// 709 3306 glough.
		else if (obj.getID() == WATCH_TOWER_STONE_STAND) {
			if (p.getQuestStage(this) == 15 || p.getQuestStage(this) == 16 || p.getQuestStage(this) == -1) {
				message(p, "you squeeze down the inner of the tree trunk",
					"you drop out of the bottom onto a mud floor");
				p.teleport(711, 3306);
				return;
			}
			message(p, "you push down on the pillar");
			p.message("you feel it shift downwards slightly");
			if ((p.getCache().hasKey("pebble_1") && p.getCache().hasKey("pebble_2") && p.getCache().hasKey("pebble_3") && p.getCache().hasKey("pebble_4")) || p.getQuestStage(this) == 14) {
				if (p.getCache().hasKey("pebble_1")) {
					p.getCache().remove("pebble_1");
				}
				if (p.getCache().hasKey("pebble_2")) {
					p.getCache().remove("pebble_2");
				}
				if (p.getCache().hasKey("pebble_3")) {
					p.getCache().remove("pebble_3");
				}
				if (p.getCache().hasKey("pebble_4")) {
					p.getCache().remove("pebble_4");
				}
				if (p.getQuestStage(this) == 13) {
					p.updateQuestStage(this, 14);
				}
				p.message("the pillar shifts back revealing a ladder");
				message(p, "it seems to lead down through the tree trunk");
				int menu = showMenu(p, "climb down", "come back later");
				if (menu == 0) {
					message(p, "you squeeze down the inner of the tree trunk",
						"you drop out of the bottom onto a mud floor");
					p.teleport(711, 3306);
					message(p, "around you, you can see piles of strange looking rocks",
						"you here the sound of small footsteps coming from the darkness");
					//glough despawns in 1 minute
					Npc n = spawnNpc(NpcId.GLOUGH_UNDERGROUND.id(), 709, 3306, 60000);
					npcTalk(p, n, "you really are becoming a headache",
						"well, at least now you can die knowing you were right",
						"it will save me having to hunt you down",
						"like all the over human filth of runescape");
					playerTalk(p, n, "you're crazy glough");
					npcTalk(p, n, "i'm angry, you think you're so special",
						"well, soon you'll see, the gnome's are ready to fight",
						"in three weeks this tree will be dead wood",
						"in ten weeks it will be 30 battleships",
						"ready to finally rid the world of the disease called humanity");
					playerTalk(p, n, "what makes you think i'll let you get away with it?");
					npcTalk(p, n, "ha, do you think i would challange you humans alone",
						"fool.....meet my little friend");
					message(p, "from the darkness you hear a deep growl",
						"and the sound of heavy footsteps");
					Npc demon = spawnNpc(NpcId.BLACK_DEMON_GRANDTREE.id(), 707, 3306, 60000 * 10);
					if (demon != null) {
						npcYell(p, demon, "grrrrr");
						demon.setChasing(p);
					}
				} else if (menu == 1) {
					p.message("you decide to come back later");
				}
			} else {
				p.message("you here some noise below the pillar...");
				p.message("...but nothing seems to happen");

			}
		}
		else if (obj.getID() == ROOT_ONE || obj.getID() == ROOT_TWO || obj.getID() == ROOT_THREE) {
			message(p, "you search the root...");
			if (obj.getID() == ROOT_THREE) {
				if (p.getQuestStage(this) == 16) {
					if (!hasItem(p, ItemId.DACONIA_ROCK.id())) {
						message(p, "and find a small glowing rock");
						addItem(p, ItemId.DACONIA_ROCK.id(), 1);
					} else {
						message(p, "but find nothing");
					}
				} else {
					message(p, "...but find nothing");
				}
			} else {
				message(p, "...but find nothing");
			}
		}
		else if (obj.getID() == PUSH_ROOT || obj.getID() == PUSH_ROOT_BACK) { // ACCESS TO GNOME MINE
			message(p, "you push the roots");
			if (p.getQuestStage(this) == -1) {
				message(p, "they wrap around your arms");
				p.message("and drag you deeper forwards");
				if (obj.getID() == PUSH_ROOT_BACK) {
					p.teleport(700, 3280);
				} else {
					p.teleport(701, 3278);
				}
			} else {
				message(p, "they don't seem to mind");
			}
		}
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		return n.getID() == NpcId.CHARLIE.id() || (n.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id() && p.getQuestStage(this) == 10);
	}

	@Override
	public void onPlayerAttackNpc(Player p, Npc affectedmob) {
		if (affectedmob.getID() == NpcId.CHARLIE.id()) {
			p.message("you can't attack through the bars");
		}
		else if (affectedmob.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id() && p.getQuestStage(this) == 10) {
			p.message("the forman is too busy to talk");
		}
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id() || n.getID() == NpcId.BLACK_DEMON_GRANDTREE.id();
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id()) {
			if (p.getQuestStage(this) == 9) {
				n.killedBy(p);
				message(p, "you kill the foreman",
					"inside his pocket you find an invoice..",
					"it seems to be an order for timber");
				addItem(p, ItemId.INVOICE.id(), 1);
				p.updateQuestStage(this, 10);
			}
		}
		else if (n.getID() == NpcId.BLACK_DEMON_GRANDTREE.id()) {
			if (p.getQuestStage(this) == 14) {
				n.killedBy(p);
				message(p, "the beast slumps to the floor",
					"glough has fled");
				p.updateQuestStage(this, 15);
				Npc fleeGlough = getNearestNpc(p, NpcId.GLOUGH_UNDERGROUND.id(), 15);
				if (fleeGlough != null) {
					fleeGlough.remove();
				}
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		return (obj.getID() == GLOUGH_CHEST_CLOSED && item.getID() == ItemId.GLOUGHS_KEY.id())
				|| (obj.getID() == WATCH_TOWER_STONE_STAND && (item.getID() == ItemId.PEBBLE_3.id()
				|| item.getID() == ItemId.PEBBLE_2.id() || item.getID() == ItemId.PEBBLE_4.id() || item.getID() == ItemId.PEBBLE_1.id()));
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
		if (obj.getID() == GLOUGH_CHEST_CLOSED && item.getID() == ItemId.GLOUGHS_KEY.id()) {
			message(player, "the key fits the chest");
			player.message("you open the chest");
			player.message("and search it...");
			replaceObjectDelayed(obj, 3000, GLOUGH_CHEST_OPEN);
			message(player, "inside you find some paper work");
			player.message("and an old gnome tongue translation book");
			addItem(player, ItemId.GLOUGHS_NOTES.id(), 1);
			addItem(player, ItemId.TREE_GNOME_TRANSLATION.id(), 1);
			if (player.getQuestStage(this) == 11) {
				player.updateQuestStage(this, 12);
			}
			message(player, "you close the chest");
		}
		else if (obj.getID() == WATCH_TOWER_STONE_STAND && (item.getID() == ItemId.PEBBLE_3.id()
				|| item.getID() == ItemId.PEBBLE_2.id() || item.getID() == ItemId.PEBBLE_4.id() || item.getID() == ItemId.PEBBLE_1.id())) {
			message(player, "on top are four pebble size indents",
				"they span from left to right",
				"you place the pebble...");
			int menu = showMenu(player, "To the far left", "Centre left", "Centre right", "To the far right");
			if (menu == 0) {
				message(player, "you place the pebble in the indent", "it crumbles into dust");
				removeItem(player, item.getID(), 1);
				if (item.getID() == ItemId.PEBBLE_1.id()) { // HO
					if (!player.getCache().hasKey("pebble_1")) {
						player.getCache().store("pebble_1", true);
					}
				}
			} else if (menu == 1) {
				message(player, "you place the pebble in the indent", "it crumbles into dust");
				removeItem(player, item.getID(), 1);
				if (item.getID() == ItemId.PEBBLE_2.id()) { // NI
					if (!player.getCache().hasKey("pebble_2")) {
						player.getCache().store("pebble_2", true);
					}
				}
			} else if (menu == 2) {
				message(player, "you place the pebble in the indent", "it crumbles into dust");
				removeItem(player, item.getID(), 1);
				if (item.getID() == ItemId.PEBBLE_3.id()) { // :::
					if (!player.getCache().hasKey("pebble_3")) {
						player.getCache().store("pebble_3", true);
					}
				}
			} else if (menu == 3) {
				message(player, "you place the pebble in the indent", "it crumbles into dust");
				removeItem(player, item.getID(), 1);
				if (item.getID() == ItemId.PEBBLE_4.id()) { // HA
					if (!player.getCache().hasKey("pebble_4")) {
						player.getCache().store("pebble_4", true);
					}
				}
			}
		}
	}

}
