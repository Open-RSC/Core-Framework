package com.openrsc.server.plugins.authentic.quests.members.grandtree;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.custom.minigames.CombatOdyssey;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.openrsc.server.plugins.Functions.*;

public class GrandTree implements QuestInterface, TalkNpcTrigger, OpLocTrigger, AttackNpcTrigger, KillNpcTrigger, UseLocTrigger {

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
		return Quests.GRAND_TREE;
	}

	@Override
	public String getQuestName() {
		return "Grand tree (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.GRAND_TREE.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("well done you have completed the grand tree quest");
		final QuestReward reward = Quest.GRAND_TREE.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.KING_NARNODE_SHAREEN.id(), NpcId.KING_NARNODE_SHAREEN_UNDERGROUND.id(),
				NpcId.HAZELMERE.id(), NpcId.GLOUGH.id(), NpcId.CHARLIE.id(), NpcId.SHIPYARD_WORKER_WHITE.id(), NpcId.SHIPYARD_WORKER_BLACK.id(),
				NpcId.SHIPYARD_FOREMAN.id(), NpcId.SHIPYARD_FOREMAN_HUT.id(), NpcId.FEMI.id(), NpcId.FEMI_STRONGHOLD.id(), NpcId.ANITA.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.KING_NARNODE_SHAREEN.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello there");
					npcsay(player, n, "hello traveller, i'm king shareem, welcome",
						"it's nice to see an outsider");
					say(player, n, "it seems to be quite a busy settlement");
					npcsay(player, n, "for now it is, thankfully");
					player.message("King shareem seems troubled");
					int option = multi(player, n, "you seem worried, what's wrong?",
						"well, i'll be on my way");
					if (option == 0) {
						npcsay(player, n, "adventurer, can i speak to you in the strictest confidence");
						say(player, n, "of course narnode");
						npcsay(player, n, "not here, follow me");
						player.message("king shareem bends down and places his hands on the stone tile");
						mes("you here a creak as he turns the tile clockwise");
						delay(3);
						mes("the tile slides away, revealing a small tunnel");
						delay(3);
						mes("you follow king shareem down");
						delay(3);
						teleport(player, 703, 3284);
						n = ifnearvisnpc(player, NpcId.KING_NARNODE_SHAREEN_UNDERGROUND.id(), 15);
						if (n != null) {
							say(player, n, "so what is this place?");
							npcsay(player, n, "these my friend, are the foundations of the stronghold");
							say(player, n, "they just look like roots");
							npcsay(player, n, "not any roots traveller",
								"these were conjured in the past age by gnome mages",
								"since then, they have grown into our mighty stronghold");
							say(player, n, "impressive, but what exactly is the problem?");
							npcsay(player, n, "in the last two months our tree guardians have reported...",
								"...continuing deterioration of the grand trees health",
								"i've never seen this before, it could mean the end for all of us");
							say(player, n, "you mean the tree is ill");
							npcsay(player, n, "in a magical sense yes",
								"would you be willing to help us discover the cause of this illness");
							int op = multi(player, n, "i'm sorry i don't want to get involved",
								"i'd be happy to help");
							if (op == 0) {
								npcsay(player, n, "i understand traveller",
									"please keep this to yourself");
								say(player, n, "of course");
								npcsay(player, n, "i'll show you the way back up");
								player.message("you follow king shareem up the ladder");
								player.teleport(415, 163);
							} else if (op == 1) {
								npcsay(player, n, "thank guthix for you arrival",
									"the first task is to find out what's killing my tree");
								say(player, n, "have you any ideas?");
								npcsay(player, n,
									"my top tree guardian, glough, believes it's human sabotage",
									"i'm not so sure",
									"the only way to really know, is to talk to Hazelmere");
								say(player, n, "who's hazelmere?");
								npcsay(player, n, "a once all powerful mage who created the grand tree",
									"one of the only survivors of the old age",
									"take this bark sample to him, he should be able to help",
									"the mage only talks in the old tongue, you'll need this");
								say(player, n, "what is it?");
								npcsay(player, n, "a translation book, translate carefully, his words may save us all",
									"you'll find his dwellings high upon a towering hill..",
									"..on a island south of the khazard fight arena");
								player.message("king shareem gives you a book and a bark sample");
								give(player, ItemId.TREE_GNOME_TRANSLATION.id(), 1);
								give(player, ItemId.BARK_SAMPLE.id(), 1);
								npcsay(player, n, "i'll show you the way back up");
								player.message("you follow king shareem up the ladder");
								teleport(player, 415, 163);
								player.updateQuestStage(this, 1);
							}
						}
					} else if (option == 1) {
						npcsay(player, n, "ok then, enjoy your stay with us",
							"there's many shops and sights to see");
					}
					break;
				case 1:
					say(player, n, "hello king shareem");
					npcsay(player, n, "traveller, you've returned",
						"any word from hazelmere?");
					say(player, n, "not yet i'm afraid");
					if (!player.getCarriedItems().hasCatalogID(ItemId.TREE_GNOME_TRANSLATION.id(), Optional.of(false))
						|| !player.getCarriedItems().hasCatalogID(ItemId.BARK_SAMPLE.id(), Optional.of(false))) {
						if (!player.getCarriedItems().hasCatalogID(ItemId.BARK_SAMPLE.id(), Optional.of(false))) {
							say(player, n, "but i've lost the bark sample");
							npcsay(player, n, "here take another and try to hang on to it");
							player.message("king shareem gives you another bark sample");
							give(player, ItemId.BARK_SAMPLE.id(), 1);
						}
						if (!player.getCarriedItems().hasCatalogID(ItemId.TREE_GNOME_TRANSLATION.id(), Optional.of(false))) {
							say(player, n, "but i've lost the book you gave me");
							npcsay(player, n, "don't worry i have more",
								"here you go");
							player.message("king shareem gives you a translation book");
							give(player, ItemId.TREE_GNOME_TRANSLATION.id(), 1);
						}
					} else {
						npcsay(player, n, "hazalmere lives on a island just south of the fight arena",
							"give him the sample and translate his reply",
							"i just hope he can help in our hour of need");
					}
					break;
				case 2:
					say(player, n, "hello again king shareem");
					npcsay(player, n, "well hello traveller, did you speak to hazelmere?");
					say(player, n, "yes, i managed to find him");
					npcsay(player, n, "and do you know what he said?");
					int menu = multi(player, n,
						"i think so",
						"no, i need to go back");
					if (menu == 0) {
						npcsay(player, n, "so what did he say?");
						int qmenu = multi(player,
							"hello there traveller",
							"king shareem must be stopped",
							"praise to the great zamorak",
							"have you any bread",
							"none of the above");
						if (qmenu == 0) {
							questionMenu2(player, n);
						} else if (qmenu == 1) {
							questionMenu2(player, n);
						} else if (qmenu == 2) {
							questionMenu2(player, n);
						} else if (qmenu == 3) {
							questionMenu2(player, n);
						} else if (qmenu == 4) {
							if (!player.getCache().hasKey("gt_q1")) {
								player.getCache().store("gt_q1", true);
							}
							questionMenu2(player, n);
						}
					} else if (menu == 1) {
						if (!player.getCarriedItems().hasCatalogID(ItemId.TREE_GNOME_TRANSLATION.id(), Optional.of(false))
							|| !player.getCarriedItems().hasCatalogID(ItemId.BARK_SAMPLE.id(), Optional.of(false))) {
							if (!player.getCarriedItems().hasCatalogID(ItemId.BARK_SAMPLE.id(), Optional.of(false))) {
								say(player, n, "but i've lost the bark sample");
								npcsay(player, n, "here take another and try to hang on to it");
								player.message("king shareem gives you another bark sample");
								give(player, ItemId.BARK_SAMPLE.id(), 1);
							}
							if (!player.getCarriedItems().hasCatalogID(ItemId.TREE_GNOME_TRANSLATION.id(), Optional.of(false))) {
								say(player, n, "but i've lost the book you gave me");
								npcsay(player, n, "don't worry i have more",
									"here you go");
								player.message("king shareem gives you a translation book");
								give(player, ItemId.TREE_GNOME_TRANSLATION.id(), 1);
							}
						}
						npcsay(player, n, "time is of the essence adventurer");
					}
					break;
				case 3:
					say(player, n, "hello narnode");
					npcsay(player, n, "hello traveller, did you speak to glough?");
					say(player, n, "not yet");
					npcsay(player, n, "ok, he lives just in front of the grand tree",
						"let me know once you've spoken to him");
					break;
				case 4:
					say(player, n, "hello king shareem",
						"have you any news on the daconia stones?");
					npcsay(player, n, "it's ok traveller, thank's to glough",
						"he found a human sneaking around...",
						"...with three daconia stones in his satchel");
					say(player, n, "i'm amazed that you retrieved them so easily");
					npcsay(player, n, "yes, glough must really know what he's doing",
						"the human has been detained until we know who's involved",
						"maybe glough was right, maybe humans are invading");
					say(player, n, "i doubt it, can i speak to the prisoner");
					npcsay(player, n, "certainly, he's on the top level of the grand tree",
						"be careful up there, it's a long way down");
					player.updateQuestStage(this, 5);
					break;
				case 5:
					say(player, n, "hi narnode");
					npcsay(player, n, "hello traveller",
						"if you wish to talk to the prisoner",
						"go to the top tree level",
						"you'll find him there");
					say(player, n, "thanks");
					break;
				case 6:
					say(player, n, "king shareem");
					npcsay(player, n, "hello adventurer, so did you speak to the culprit?");
					say(player, n, "yes i did and something's not right");
					npcsay(player, n, "what do you mean?");
					say(player, n, "the prisoner claims he was paid by glough to get the stones");
					npcsay(player, n, "that's an absurd story, he's just trying to save himself",
						"since glough's wife died he has been a little strange",
						"but he would never wrongly imprison someone",
						"now the culprit's locked up we can all relax",
						"it's sad but i think glough was right",
						"humans are planning to invade and wipe us tree gnomes out");
					say(player, n, "but why?");
					npcsay(player, n, "who knows? but you may have to leave soon adventurer",
						"i trust you, but the local gnomes are getting paranoid");
					say(player, n, "that's a shame");
					npcsay(player, n, "hopefully i can keep my people calm, we'll see");
					break;
				case 7:
					say(player, n, "king shareem, i'm concerned about glough");
					npcsay(player, n, "why, don't worry yourself about him",
						"now the culprit has been caught...",
						"..i'm sure glough's resentment of humans will die away");
					say(player, n, "i'm not so sure");
					npcsay(player, n, "he just has an active imagination",
						"if your really concerned, speak to him");
					break;
				case 8:
				case 9:
					say(player, n, "hello narnode");
					npcsay(player, n, "traveller, haven't you heard",
						"glough has set a warrant for your arrest",
						"he has guards at the exit",
						"i shouldn't have told you this",
						"but i can see your a good person",
						"please take the glider and leave before it's too late");
					say(player, n, "all the best narnode");
					break;
				case 10:
					say(player, n, "king shareem,i need to talk");
					npcsay(player, n, "traveller, what are you doing here?",
						"the stronghold has been put on full alert",
						"it's not safe for you here");
					say(player, n, "narnode, i believe glough is killing the trees",
						"in order to make a mass fleet of warships");
					npcsay(player, n, "that's an absurd accusation");
					say(player, n, "his hatred for humanity is stronger than you know");
					npcsay(player, n, "that's enough traveller, you sound as paranoid as him",
						"traveller please leave",
						"it's bad enough having one human locked up");
					break;
				case 11:
					say(player, n, "hello narnode");
					npcsay(player, n, "please traveller, if the gnomes see me talking to you",
						"they'll revolt against me");
					say(player, n, "that's crazy");
					npcsay(player, n, "glough's scared the whole town",
						"he expects the humans to attack any day",
						"he's even began to recuit hundreds of gnome soldiers");
					say(player, n, "don't you understand he's creating his own army");
					npcsay(player, n, "please traveller, just leave before it's too late");
					break;
				case 12:
					say(player, n, "hi narnode, did you think about what i said?");
					npcsay(player, n, "look, if you're right about glough i would have him arrested",
						"but there's no reason for me to think he's lying");
					if (player.getCarriedItems().hasCatalogID(ItemId.GLOUGHS_NOTES.id(), Optional.of(false))) {
						say(player, n, "look, i found this at glough's home");
						mes("you give the king the strategic notes");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.GLOUGHS_NOTES.id()));
						npcsay(player, n, "hmmm, these are interesting",
							"but it's not proof, any one could have made these",
							"traveller, i understand your concern",
							"i had guards search glough's house",
							"but they found nothing suspicious",
							"just these old pebbles");
						mes("narnode gives you four old pebbles");
						delay(3);
						give(player, ItemId.PEBBLE_3.id(), 1);
						give(player, ItemId.PEBBLE_2.id(), 1);
						give(player, ItemId.PEBBLE_4.id(), 1);
						give(player, ItemId.PEBBLE_1.id(), 1);
						npcsay(player, n, "on the other hand, if glough's right about the humans",
							"we will need an army of gnomes to protect ourselves",
							"so i've decided to allow glough to raise a mighty gnome army",
							"the grand tree's still slowly dying, if it is human sabotage",
							"we must respond");
						player.updateQuestStage(this, 13);
					}
					break;
				case 13:
					say(player, n, "hello again narnode");
					npcsay(player, n, "please traveller, take my advice and leave");
					if (!player.getCarriedItems().hasCatalogID(ItemId.PEBBLE_3.id(), Optional.of(false))
						|| !player.getCarriedItems().hasCatalogID(ItemId.PEBBLE_2.id(), Optional.of(false))
						|| !player.getCarriedItems().hasCatalogID(ItemId.PEBBLE_4.id(), Optional.of(false))
						|| !player.getCarriedItems().hasCatalogID(ItemId.PEBBLE_1.id(), Optional.of(false))) {
						say(player, n, "have you any more of those pebbles");
						npcsay(player, n, "well, yes as it goes, why?");
						say(player, n, "i lost some");
						npcsay(player, n, "here take these, i don't see how it will help though");
						mes("narnode replaces your lost pebbles");
						delay(3);
						if (player.getCache().hasKey("pebble_1")) {
							player.getCache().remove("pebble_1");
						}
						if (player.getCache().hasKey("pebble_2")) {
							player.getCache().remove("pebble_2");
						}
						if (player.getCache().hasKey("pebble_3")) {
							player.getCache().remove("pebble_3");
						}
						if (player.getCache().hasKey("pebble_4")) {
							player.getCache().remove("pebble_4");
						}
						if (!player.getCarriedItems().hasCatalogID(ItemId.PEBBLE_3.id(), Optional.of(false))) {
							give(player, ItemId.PEBBLE_3.id(), 1);
						}
						if (!player.getCarriedItems().hasCatalogID(ItemId.PEBBLE_2.id(), Optional.of(false))) {
							give(player, ItemId.PEBBLE_2.id(), 1);
						}
						if (!player.getCarriedItems().hasCatalogID(ItemId.PEBBLE_4.id(), Optional.of(false))) {
							give(player, ItemId.PEBBLE_4.id(), 1);
						}
						if (!player.getCarriedItems().hasCatalogID(ItemId.PEBBLE_1.id(), Optional.of(false))) {
							give(player, ItemId.PEBBLE_1.id(), 1);
						}
					} else if (player.getCarriedItems().hasCatalogID(ItemId.PEBBLE_3.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.PEBBLE_2.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.PEBBLE_4.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.PEBBLE_1.id(), Optional.of(false))) {
						npcsay(player, n, "it's not safe for you here");
					}
					break;
				case 14:
					say(player, n, "narnode, it's true about glough i tell you",
						"he's planning to take over runescape");
					npcsay(player, n, "i'm sorry traveller but it's just not realistic",
						"how could glough- even with a gnome army- take over?");
					say(player, n, DataConversions.getRandom().nextBoolean() ? "he plans to make a fleet of warships from the grand trees' wood"
						: "he plans to make a fleet of warships from the grand tree's wood");
					npcsay(player, n, "that's enough traveller, i've no time for make believe",
						"the tree's still dying, i must get to the truth of this");
					break;
				case -1:
					say(player, n, "hello narnode");
					npcsay(player, n, "well hello again adventurer",
						"how are you?");
					say(player, n, "i'm good thanks, how's the tree?");
					npcsay(player, n, "better than ever, thanks for asking");
					if (config().CAN_RETRIEVE_POST_QUEST_ITEMS && !player.getCarriedItems().hasCatalogID(ItemId.TREE_GNOME_TRANSLATION.id(), Optional.of(false))) {
						say(player, n, "i've lost the book you gave me");
						npcsay(player, n, "don't worry i have more",
							"here you go");
						player.message("king shareem gives you a translation book");
						give(player, ItemId.TREE_GNOME_TRANSLATION.id(), 1);
					}
					break;
			}
		}
		else if (n.getID() == NpcId.HAZELMERE.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					mes("the mage mumbles in an ancient tounge");
					delay(3);
					mes("you can't understand a word");
					delay(3);
					break;
				case 1:
					say(player, n, "hello");
					if (player.getCarriedItems().hasCatalogID(ItemId.BARK_SAMPLE.id(), Optional.of(false))) {
						mes("you give the mage the bark sample");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.BARK_SAMPLE.id()));
						mes("the mage speaks in a strange ancient tongue");
						delay(3);
						mes("he says....");
						delay(3);
						strangeTranslationBox(player);
						player.updateQuestStage(this, 2);
					} else {
						mes("the mage mumbles in an ancient tounge");
						delay(3);
						mes("you can't understand a word");
						delay(3);
						mes("you need to give him the bark sample");
						delay(3);
					}
					break;
				case 2:
					mes("the mage speaks in a strange ancient tongue");
					delay(3);
					mes("he says....");
					delay(3);
					strangeTranslationBox(player);
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
					if (config().WANT_COMBAT_ODYSSEY) {
						int currentTier = CombatOdyssey.getCurrentTier(player);
						if (currentTier == 5 && CombatOdyssey.isTierCompleted(player)) {
							if (CombatOdyssey.biggumMissing()) return;
							if (player.getCarriedItems().hasCatalogID(ItemId.TREE_GNOME_TRANSLATION.id())) {
								int newTier = 6;
								CombatOdyssey.assignNewTier(player, newTier);
								npcsay(player, n, "qaxahblat");
								CombatOdyssey.giveRewards(player, n);
								npcsay(player, n, "voxavava latxxahaqasol");
								npcsay(player, n, hazelmereTranslate(player.getWorld().getCombatOdyssey().getTier(newTier).getTasksAndCounts()));
								npcsay(player, n, "xc:vzavo latho xasolva:vhaqe");
								CombatOdyssey.biggumSay(player, "Biggum knows to keep enemies close",
									"Biggum understand gnomespeak");
							} else {
								mes("The mage mumbles in an ancient tongue",
									"You can't understand a word");
								delay(3);
								say(player, "I should probably get a tree gnome translation for this");
							}
							return;
						} else if (currentTier == 6 && CombatOdyssey.isTierCompleted(player)) {
							if (CombatOdyssey.biggumMissing()) return;
							if (player.getCarriedItems().hasCatalogID(ItemId.TREE_GNOME_TRANSLATION.id())) {
								int newTier = 7;
								CombatOdyssey.assignNewTier(player, newTier);
								npcsay(player, n, "qaxahblat");
								CombatOdyssey.giveRewards(player, n);
								npcsay(player, n, "hahoh voxavava");
								String[] npcsAndCounts = player.getWorld().getCombatOdyssey().getTier(newTier).getTasksAndCounts();
								npcsay(player, n, hazelmereTranslate(npcsAndCounts));
								npcsay(player, n, "qaho latho solxaqax::::qilat latx::: h::vqiqixahoqi");
								CombatOdyssey.biggumSay(player, "Human go kill");
								CombatOdyssey.biggumSay(player, npcsAndCounts);
								CombatOdyssey.biggumSay(player, "And then go see Sigbert adventure man");
							} else {
								mes("The mage mumbles in an ancient tongue",
									"You can't understand a word");
								delay(3);
								say(player, "I should probably get a tree gnome translation for this");
							}
							return;
						}
					}
					player.message("the mage mumbles in an ancient tounge");
					player.message("you can't understand a word");
					break;
			}
		}
		else if (n.getID() == NpcId.GLOUGH.id()) {
			switch (player.getQuestStage(this)) {
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
					say(player, n, "hello there");
					npcsay(player, n, "you shouldn't be here human");
					say(player, n, "what do you mean?");
					npcsay(player, n, "the gnome stronghold is for gnomes alone");
					say(player, n, "surely not!");
					npcsay(player, n, "we don't need you're sort around here");
					mes("he doesn't seem very nice");
					delay(3);
					break;
				case 3:
					say(player, n, "hello");
					mes("the gnome is munching on a worm hole");
					delay(3);
					npcsay(player, n, "can i help human, can't you see i'm eating?",
						"these are my favourite");
					mes("the gnome continues to eat");
					delay(3);
					say(player, n, "the king asked me to inform you...",
						"that the daconia rocks have been taken");
					npcsay(player, n, "surley not!");
					say(player, n, "apparently a human took them from hazelmere",
						"he had a permission note with the king's seal");
					npcsay(player, n, "i should have known, the humans are going to invade");
					say(player, n, "never");
					npcsay(player, n, "your type can't be trusted",
						"i'll take care of this, you go back to the king");
					player.updateQuestStage(this, 4);
					break;
				case 7:
					say(player, n, "glough, i don't know what you're up to...",
						"...but i know you paid charlie to get those rocks");
					npcsay(player, n, "you're a fool human",
						"you have no idea whats going on");
					say(player, n, "i know the grand tree's dying",
						"and i think you're part of the reason");
					npcsay(player, n, "how dare you accuse me, i'm the head tree guardian",
						"guards...guards");
					player.message("gnome guards hurry up the ladder");
					// 418, 2992
					Npc gnome_guard = addnpc(player.getWorld(), NpcId.GNOME_GUARD_PRISON.id(), 714, 1421, (int)TimeUnit.SECONDS.toMillis(12));
					npcsay(player, n, "take him away");
					player.face(gnome_guard);
					gnome_guard.face(player);
					say(player, n, "what for?");
					npcsay(player, n, "grand treason against his majesty king shareem",
						"this man is a human spy");
					gnome_guard.remove();
					npcsay(player, n, "lock him up");
					mes("the gnome guards take you to the top of the grand tree");
					delay(3);
					player.teleport(419, 2992);
					delay(8);
					Npc jailCharlie = ifnearvisnpc(player, NpcId.CHARLIE.id(), 5);
					if (jailCharlie != null) {
						npcsay(player, jailCharlie, "so, they've got you as well");
						say(player, jailCharlie, "it's glough, he's trying to cover something up");
						npcsay(player, jailCharlie, "i shouldn't tell you this adventurer",
							"but if you want to get to the bottom of this",
							"you should go and talk to the karamja foreman");
						say(player, jailCharlie, "why?");
						npcsay(player, jailCharlie, "glough sent me to karamja to meet him",
							"i delivered a large amount of gold",
							"for what i do not know",
							"but he may be able to tell you what glough's up to",
							"that's if you can get out of here",
							"you'll find him in a ship yard south of birmhaven",
							"be careful, if he discovers that you're not...",
							"...working for glough there'll be trouble",
							"the sea men use the pass word ka-lu-min");
						say(player, jailCharlie, "thanks charlie");
						delay(8);
						Npc narnode = addnpc(player.getWorld(), NpcId.KING_NARNODE_SHAREEN.id(), 419, 2993, (int)TimeUnit.SECONDS.toMillis(36));
						npcsay(player, narnode, "adventurer please accept my apologies",
							"glough had no right to arrest you",
							"i just think he's scared of humans",
							"let me get you out of there");
						mes("king shareem opens the cage");
						delay(3);
						player.teleport(418, 2993);
						say(player, narnode, "i don't think you can trust glough, narnode",
							"he seems to have a unatural hatred for humans");
						npcsay(player, narnode, "i know he can seem a little extreme at times",
							"but he's the best tree guardian i have",
							"he has however caused much fear towards humans",
							"i'm afraid he's placed guards on the front gate...",
							"...to stop you escaping",
							"let my glider pilot fly you away",
							"untill things calm down around here");
						say(player, narnode, "well, if that's how you feel");
						narnode.remove();
						player.updateQuestStage(this, 8);
					}
					break;
				case 10:
					say(player, n, "I know what you're up to glough");
					npcsay(player, n, "you have no idea human");
					say(player, n, "you may be able to make a fleet",
						"but the tree gnomes will never follow you into battle");
					npcsay(player, n, "so, you know more than i thought, i'm impressed",
						"the gnomes fear humanity more than any other race",
						"i just need to give them a push in the right direction",
						"there's nothing you can do traveller",
						"leave before it's too late",
						"soon all of runescape will feel the wrath of glough");
					say(player, n, "king shareem won't allow it");
					npcsay(player, n, "the king's a fool and a coward, he'll soon bow to me",
						"and you'll soon be back in that cage");
					break;
				case 11:
					say(player, n, "i'm going to stop you glough");
					npcsay(player, n, "you're becoming quite annoying traveller");
					mes("glough is searching his pockets");
					delay(3);
					mes("he seems very uptight");
					delay(3);
					npcsay(player, n, "damn keys",
						"leave human, before i have you put in the cage");
					break;
			}
		}
		else if (n.getID() == NpcId.CHARLIE.id()) {
			switch (player.getQuestStage(this)) {
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
					player.message("the prisoner is in no mood to talk");
					break;
				case 5:
					say(player, n, "tell me,why would you want to kill the grand tree?");
					npcsay(player, n, "what do you mean?");
					say(player, n, "don't tell me, you just happened to be caught carrying daconia rocks!");
					npcsay(player, n, "all i know, is that i did what i was asked");
					say(player, n, "i don't understand?");
					npcsay(player, n, "glough paid me to go see this gnome on a hill",
						"i gave the gnome a letter glough gave me",
						"and he gave me some rocks to give glough",
						"i've been doing it for weeks, it's just this time..",
						"...when i returned glough locked me up here",
						"i just don't understand it");
					say(player, n, "sounds like glough's hiding something");
					npcsay(player, n, "i don't know what he's up to",
						"but if you want to find out...",
						"..you better search his home");
					say(player, n, "ok, thanks charlie");
					npcsay(player, n, "good luck");
					player.updateQuestStage(this, 6);
					break;
				case 6:
				case 7:
					say(player, n, "hello charlie");
					npcsay(player, n, "hello adventurer, have you figured out what's going on?");
					say(player, n, "no idea");
					npcsay(player, n, "to get to the bottom of this you'll need to search glough's home");
					break;
				case 8:
				case 9:
					say(player, n, "i can't figure this out charlie");
					npcsay(player, n, "go and see a forman in west karamja",
						"there's a shipyard there,you might find some clues",
						"don't forget the password's ka-lu-min",
						"if they realise that you're not working for glough...",
						"...there'll be trouble");
					break;
				case 10:
				case 11:
					say(player, n, "how are you doing charlie");
					npcsay(player, n, "i've been better");
					say(player, n, "glough has some plan to rule runescape");
					npcsay(player, n, "i wouldn't put it past him, the gnome's crazy");
					say(player, n, "i need some proof to convince the king");
					npcsay(player, n, "hmmm, you could be in luck",
						"before glough had me locked up i heard him mention..",
						"..that he'd left his chest lock keys at his girlfriends");
					say(player, n, "where does she live?");
					npcsay(player, n, "just west of the toad swamp");
					say(player, n, "okay, i'll see what i can find");
					if (player.getQuestStage(this) == 10) {
						player.updateQuestStage(this, 11);
					}
					break;
			}
		}
		else if (n.getID() == NpcId.SHIPYARD_WORKER_WHITE.id() || n.getID() == NpcId.SHIPYARD_WORKER_BLACK.id()) {
			int selected = DataConversions.getRandom().nextInt(14);
			boolean isAlternative = n.getID() == NpcId.SHIPYARD_WORKER_BLACK.id();
			say(player, n, "hello");
			if (selected == 0) {
				npcsay(player, n, "ouch");
				say(player, n, "what's wrong?");
				npcsay(player, n, "i cut my finger",
					"do you have a bandage?");
				say(player, n, "i'm afraid not");
				npcsay(player, n, "that's ok, i'll use my shirt");
			} else if (selected == 1) {
				say(player, n, "you look busy");
				npcsay(player, n, "we need double the men to get..",
					"...this order out on time");
			} else if (selected == 2) {
				npcsay(player, n, "hello matey");
				say(player, n, "how are you?");
				npcsay(player, n, "tired");
				say(player, n, "you shouldn't work so hard");
			} else if (selected == 3) {
				say(player, n, "what are you building?");
				npcsay(player, n, "are you serious?");
				say(player, n, "of course not",
					"you're obviously building a boat");
			} else if (selected == 4) {
				say(player, n, "looks like hard work");
				npcsay(player, n, "i like to keep busy");
			} else if (selected == 5) {
				npcsay(player, n, "no time to talk",
					"we've a fleet to build");
			} else if (selected == 6) {
				say(player, n, "quite an impressive set up");
				npcsay(player, n, "it needs to be...",
					"..there's no other way to build a fleet of this size");
			} else if (selected == 7) {
				say(player, n, "quite a few ships you're building");
				npcsay(player, n, "this is just the start",
					"the completed fleet will be awesome");
			} else if (selected == 8) {
				say(player, n, "so where are you sailing?");
				npcsay(player, n, "what do you mean?");
				say(player, n, "don't worry, just kidding!");
			} else if (selected == 9) {
				say(player, n, "how are you?");
				npcsay(player, n, "too busy to waste time gossiping");
				say(player, n, "touchy");
			} else if (selected == 10) {
				npcsay(player, n, "can i help you");
				say(player, n, "i'm just looking around");
				npcsay(player, n, "well there's plenty of work to be done",
					"so if you don't mind...");
				say(player, n, "of course, sorry to have disturbed you");
			} else if (selected == 11) {
				npcsay(player, n, "hello there",
					"are you too lazy to work as well");
				say(player, n, "something like that");
				npcsay(player, n, "i'm just sun bathing");
			} else if (selected == 12) {
				npcsay(player, n, "hello there");
				npcsay(player, n, "i haven't seen you before");
				say(player, n, "i'm new");
				npcsay(player, n, "well it's hard work, but the pay is good");
			} else if (selected == 13) {
				npcsay(player, n, "what do you want?");
				say(player, n, isAlternative ? "is that anyway to talk to your new superior?"
					: "is that any way to talk to your new superior?");
				npcsay(player, n, "oh, i'm sorry, i didn't realise");
			}
		}
		else if (n.getID() == NpcId.SHIPYARD_FOREMAN.id()) {
			if (player.getQuestStage(this) >= 10) {
				player.message("the forman is too busy to talk");
				return;
			}
			say(player, n, "hello, are you in charge?");
			npcsay(player, n, "that's right, and you are?");
			say(player, n, "glough sent me to check up on things");
			npcsay(player, n, "is that right, glough sent a human");
			say(player, n, "his gnomes were all busy");
			npcsay(player, n, "ok, we had better go inside, follow me");
			player.teleport(408, 753);
			if (player.getQuestStage(this) == 8) {
				player.message("you follow the foreman into the wooden hut");
				Npc HUT_FOREMAN = ifnearvisnpc(player, NpcId.SHIPYARD_FOREMAN_HUT.id(), 4);
				if (HUT_FOREMAN == null) return;
				npcsay(player, HUT_FOREMAN, "so tell me again why you're here");
				say(player, HUT_FOREMAN, "erm...glough sent me?");
				npcsay(player, HUT_FOREMAN, "ok and how is glough..still with his wife?");
				int menu = multi(player, HUT_FOREMAN,
					"yes, they're both getting on great",
					"always arguing as usual",
					"his wife is no longer with us");
				if (menu == 0 || menu == 1) {
					player.updateQuestStage(this, 9);
					npcsay(player, HUT_FOREMAN, "really...",
						"..that's strange, considering she died last year",
						"die imposter");
					HUT_FOREMAN.setChasing(player);
				} else if (menu == 2) {
					npcsay(player, HUT_FOREMAN, "right answear, i have to watch out for imposters", "if really know glough...", "you know his favourite gnome dish");
					int menu2 = multi(player, HUT_FOREMAN, "he loves tangled toads legs", "he loves worm holes", "he loves choc bombs");
					if (menu2 == 0 || menu2 == 2) {
						player.updateQuestStage(this, 9);
						npcsay(player, HUT_FOREMAN, "he hates them",
							"die imposter");
						HUT_FOREMAN.setChasing(player);
					} else if (menu2 == 1) {
						npcsay(player, HUT_FOREMAN, "ok, one more question", "what's the name of his new girlfriend");
						int menu3 = multi(player, HUT_FOREMAN, false, //do not send over
							"Alia", "Anita", "Elena");
						if (menu3 == 0 || menu3 == 2) {
							player.updateQuestStage(this, 9);
							if (menu3 == 0) {
								say(player, HUT_FOREMAN, "alia");
							} else if (menu3 == 2) {
								say(player, HUT_FOREMAN, "elena");
							}
							npcsay(player, HUT_FOREMAN, "you almost fooled me",
								"die imposter");
							HUT_FOREMAN.setChasing(player);
						} else if (menu3 == 1) {
							say(player, HUT_FOREMAN, "anita");
							npcsay(player, HUT_FOREMAN, "well, well ,well, you do know glough",
								"sorry for the interrogation but i'm sure you understand");
							say(player, HUT_FOREMAN, "of course, security is paramount");
							npcsay(player, HUT_FOREMAN, "as you can see the ship builders are ready");
							say(player, HUT_FOREMAN, "indeed");
							npcsay(player, HUT_FOREMAN, "when i was asked to build a fleet large enough...",
								"..to invade port sarim and carry 300 gnome troops...",
								"..i said if anyone can, i can");
							say(player, HUT_FOREMAN, "that's a lot of troops");
							npcsay(player, HUT_FOREMAN, "true but if the gnomes are really going to..",
								"..take over runescape, they'll need at least that");
							say(player, HUT_FOREMAN, "take over?");
							npcsay(player, HUT_FOREMAN, "of course, why else would glough want 30 battleships",
								"between you and me, i don't think he stands a chance");
							say(player, HUT_FOREMAN, "no");
							npcsay(player, HUT_FOREMAN, "i mean, for the kind of battleships glough's ordered..",
								"..i'll need ton's and ton's of timber",
								"more than any forest i can think of could supply",
								"still, if he say's he can supply the wood i'm sure he can",
								"any way, here's the invoice");
							say(player, HUT_FOREMAN, "ok, thanks");
							npcsay(player, HUT_FOREMAN, "i'll need the wood as soon as possible",
								"if the orders going to be finished in time");
							say(player, HUT_FOREMAN, "ok i'll tell glough");
							mes("the foreman hands you the invoice");
							delay(3);
							give(player, ItemId.INVOICE.id(), 1);
							player.updateQuestStage(this, 10);
						}
					}
				}
			} else if (player.getQuestStage(this) == 9) {
				Npc HUT_FOREMAN = ifnearvisnpc(player, NpcId.SHIPYARD_FOREMAN_HUT.id(), 4);
				if (HUT_FOREMAN == null) return;
				npcYell(player, n, "die imposter");
				HUT_FOREMAN.setChasing(player);
			}
		}
		else if (n.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id()) {
			if (player.getQuestStage(this) == 10) {
				player.message("the forman is too busy to talk");
				return;
			}
			npcYell(player, n, "die imposter");
			n.setChasing(player);
		}
		else if (n.getID() == NpcId.FEMI.id()) {
			switch (player.getQuestStage(this)) {
				case 10:
					boolean smuggled = false;
					boolean favor = false;
					say(player, n, "i can't believe they won't let me in");
					npcsay(player, n, "i don't believe all this rubbish about an invasion",
						"if mankind wanted to, they could have invaded before now");
					say(player, n, "i really need to see king shareem",
						"could you help sneak me in");
					if(player.getCache().hasKey("helped_femi") && player.getCache().getBoolean("helped_femi")) {
						npcsay(player, n, "well, as you helped me i suppose i could",
							"we'll have to be careful",
							"if i get caught i'll be in the cage");
						say(player, n, "ok, what should i do");
						npcsay(player, n, "jump in the back of the cart",
							"it's a food delivery, we should be fine");
						mes("you hide in the cart");
						delay(3);
						mes("femi covers you with a sheet...");
						delay(3);
						mes("...and drags the cart to the gate");
						delay(3);
						mes("femi pulls you into the stronghold");
						delay(3);
						smuggled = true;
						favor = true;
					} else {
						npcsay(player, n, "why should i help you, you wouldn't help me");
						say(player, n, "erm i know, but this is an emergency");
						npcsay(player, n, "so was lifting that barrel",
							"tell you what, let's call it a round 1000 gold piece's");
						say(player, n, "1000 gold pieces");
						npcsay(player, n, "that's right 1000 and i'll sneak you in");
						int option = multi(player, n, "no chance",
							"ok then, here you go");
						if (option == 0) {
							// No extra dialogue
						} else if (option == 1) {
							if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 1000) {
								npcsay(player, n, "alright, jump in the back of the cart",
									"it's a food delivery, we should be fine");
								mes("you hide in the cart");
								delay(3);
								mes("femi covers you with a sheet...");
								delay(3);
								mes("...and drags the cart to the gate");
								delay(3);
								mes("you give femi 1000 gold coins");
								delay(3);
								player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 1000));
								mes("femi pulls you into the stronghold");
								delay(3);
								smuggled = true;
							} else {
								// TODO: Authentic behavior here not known
								say(player, n, "Oh dear I don't seem to have enough money");
							}
						}
					}
					if (smuggled) {
						player.teleport(708, 510);
						Npc femi = ifnearvisnpc(player, NpcId.FEMI_STRONGHOLD.id(), 2);
						if (femi != null) {
							npcsay(player, femi, "ok traveller, you'd better get going");
							if (favor) {
								say(player, femi, "thanks again femi");
								npcsay(player, femi, "that's ok, all the best");
							}
						}
					}
					break;
				default:
					player.message("the little gnome is too busy to talk");
					break;
			}
		}
		else if (n.getID() == NpcId.FEMI_STRONGHOLD.id()) {
			player.message("the little gnome is too busy to talk");
		}
		else if (n.getID() == NpcId.ANITA.id()) {
			switch (player.getQuestStage(this)) {
				case 11:
					say(player, n, "hello there");
					npcsay(player, n, "oh hello, i've seen you with the king");
					say(player, n, "yes, i'm helping him with a problem");
					npcsay(player, n, "you must know my boy friend glough then");
					say(player, n, "indeed!");
					npcsay(player, n, "could you do me a favour?");
					say(player, n, "i suppose so");
					npcsay(player, n, "give this key to glough",
						"he left it here last night");
					mes("anita gives you a key");
					delay(3);
					give(player, ItemId.GLOUGHS_KEY.id(), 1);
					npcsay(player, n, "thanks a lot");
					say(player, n, "no, thankyou");
					break;
				default:
					player.message("anita is to busy cleaning to talk");
					break;
			}
		}
		else if (n.getID() == NpcId.KING_NARNODE_SHAREEN_UNDERGROUND.id()) { // FINALE COMPLETION
			switch (player.getQuestStage(this)) {
				case 15:
					npcsay(player, n, "traveller you're wounded, what happened?");
					say(player, n, "it's glough, he set a demon on me");
					npcsay(player, n, "what, glough, with a demon?");
					say(player, n, "glough has a store of daconia rocks further up the passage way",
						"he's been accessing the roots from a secret passage at his home");
					npcsay(player, n, "never, not glough, he's a good gnome at heart",
						"guard, go and check out that passage way");
					mes("one of the king's guards runs of up the passage");
					delay(3);
					npcsay(player, n, "look, maybe it's stress playing with your mind");
					mes("the gnome guard returns");
					delay(3);
					mes("and talks to the king");
					delay(3);
					npcsay(player, n, "what?, never, why that little...",
						"they found glough hiding under a horde of daconia rocks..");
					say(player, n, "that's what i've been trying to tell you",
						"glough's been fooling you");
					npcsay(player, n, "i..i don't know what to say",
						"how could i have been so blind");
					mes("king shareem calls out to another guard");
					delay(3);
					npcsay(player, n, "guard, call off the military training",
						"the humans are not attacking",
						"you have my full apologies traveller, and my gratitude",
						"a reward will have to wait though, the tree is still dying",
						"the guards are clearing glough's rock supply now",
						"but there must be more daconia hidden somewhere in the roots",
						"please traveller help us search, we have little time");
					player.updateQuestStage(this, 16);
					break;
				case 16:
					npcsay(player, n, "traveller, have you managed to find the rock",
						"i think there's only one");
					if (player.getCarriedItems().hasCatalogID(ItemId.DACONIA_ROCK.id(), Optional.of(false))) {
						say(player, n, "is this it?");
						npcsay(player, n, "yes, excellent, well done");
						mes("you give king shareem the daconia rock");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.DACONIA_ROCK.id()));
						npcsay(player, n, "it's incredible, the tree's health is improving already",
							"i don't what to say, we owe you so much",
							"to think glough had me fooled all along");
						say(player, n, "all that matters now is that man...",
							"...and gnome can live together in peace");
						npcsay(player, n, "i'll drink to that");
						player.sendQuestComplete(Quests.GRAND_TREE);
						npcsay(player, n, "from now on i vow to make this stronghold",
							"a welcome place for all no matter what their creed",
							"i'll grant you access to all our facilities");
						say(player, n, "thanks, i think");
						npcsay(player, n, "it should make your stay here easier",
							"you can use the spirit tree to transport yourself",
							"..as well as the gnome glider",
							"i also give you access to our mine");
						say(player, n, "mine?");
						npcsay(player, n, "very few know of the secret mine under the grand tree",
							"if you push on the roots just to my north",
							"the grand tree will take you there");
						say(player, n, "strange");
						npcsay(player, n, "that's magic trees for you",
							"all the best traveller and thanks again");
						say(player, n, "you too narnode");
					} else {
						say(player, n, "no sign of it so far");
						npcsay(player, n, "the tree will still die if we don't find it",
							"it could be anywhere");
						say(player, n, "don't worry narnode, we'll find it");
					}
					break;
				case -1:
					say(player, n, "hello narnode");
					npcsay(player, n, "well hello again adventurer",
						"how are you?");
					say(player, n, "i'm good thanks, how's the tree?");
					if (DataConversions.getRandom().nextBoolean()) {
						npcsay(player, n, "better than ever, thanks to you");
					} else {
						npcsay(player, n, "better than ever, thanks for asking");
					}
					break;
				default:
					break;
			}
		}
	}

	private String[] hazelmereTranslate(String... messages) {
		String[] translatedMessages = new String[messages.length];
		for (int i = 0; i < messages.length; ++i) {
			StringBuilder newString = new StringBuilder();
			for (char c : messages[i].toCharArray()) {
				// Just leave everything that isn't a letter the same
				if (Character.isLetter(c)) {
					c = Character.toLowerCase(c);
				}

				switch (c) {
					case 'a':
						newString.append(":v");
						break;
					case 'b':
						newString.append("x:");
						break;
					case 'c':
						newString.append("za");
						break;
					case 'd':
						newString.append("qe");
						break;
					case 'e':
						newString.append(":::");
						break;
					case 'f':
						newString.append("hb");
						break;
					case 'g':
						newString.append("qa");
						break;
					case 'h':
						newString.append("x");
						break;
					case 'i':
						newString.append("xa");
						break;
					case 'j':
						newString.append("ve");
						break;
					case 'k':
						newString.append("vo");
						break;
					case 'l':
						newString.append("va");
						break;
					case 'm':
						newString.append("ql");
						break;
					case 'n':
						newString.append("ha");
						break;
					case 'o':
						newString.append("ho");
						break;
					case 'p':
						newString.append("ni");
						break;
					case 'q':
						newString.append("na");
						break;
					case 'r':
						newString.append("qi");
						break;
					case 's':
						newString.append("sol");
						break;
					case 't':
						newString.append("lat");
						break;
					case 'u':
						newString.append("z");
						break;
					case 'v':
						newString.append("::");
						break;
					case 'w':
						newString.append("h:");
						break;
					case 'x':
						newString.append(":i:");
						break;
					case 'y':
						newString.append("im");
						break;
					case 'z':
						newString.append("dim");
						break;
					default:
						newString.append(c);
						break;
				}
			}
			translatedMessages[i] = newString.toString();
		}
		return translatedMessages;
	}

	private void questionMenu2(final Player player, final Npc n) {
		int menu = multi(player,
			"you must warn the gnomes",
			"soon the eternal night will come",
			"the seven must reunite",
			"only one the fifth night",
			"none of the above");
		if (menu == 0) {
			questionMenu3(player, n);
		} else if (menu == 1) {
			questionMenu3(player, n);
		} else if (menu == 2) {
			questionMenu3(player, n);
		} else if (menu == 3) {
			questionMenu3(player, n);
		} else if (menu == 4) {
			if (!player.getCache().hasKey("gt_q2") && player.getCache().hasKey("gt_q1")) {
				player.getCache().store("gt_q2", true);
			}
			questionMenu3(player, n);
		}
	}

	private void questionMenu3(final Player player, final Npc n) {
		int menu = multi(player,
			"all shall peril",
			"chicken, it must be chicken",
			"and then you will know",
			"the tree will live",
			"none of the above");
		if (menu == 0) {
			wrongQuestionMenu(player, n);
		} else if (menu == 1) {
			wrongQuestionMenu(player, n);
		} else if (menu == 2) {
			wrongQuestionMenu(player, n);
		} else if (menu == 3) {
			wrongQuestionMenu(player, n);
		} else if (menu == 4) {
			if (player.getCache().hasKey("gt_q1") && player.getCache().hasKey("gt_q2")) {
				// remove to keep efficiency in cache.
				player.getCache().remove("gt_q1");
				player.getCache().remove("gt_q2");
				// Continue the last three menus.
				int realQuestionMenu = multi(player,
					"monster came with king's sword",
					"giant left with tree stone",
					"ogre came with king's head",
					"human came with king's seal",
					"fairy came with eternal flower");
				if (realQuestionMenu == 0) {
					wrongQuestionMenu(player, n);
				} else if (realQuestionMenu == 1) {
					wrongQuestionMenu(player, n);
				} else if (realQuestionMenu == 2) {
					wrongQuestionMenu(player, n);
				} else if (realQuestionMenu == 3) {
					int realQuestionMenu2 = multi(player,
						"gave the ever-light to human",
						"gave human daconia rock",
						"gave human rock to daconia",
						"human attacked by daconia",
						"human destroyed daconia rock");
					if (realQuestionMenu2 == 0) {
						wrongQuestionMenu(player, n);
					} else if (realQuestionMenu2 == 1) {
						int realQuestionMenu3 = multi(player,
							"daconia rocks will save tree",
							"daconia will fall to gnome kingdom",
							"gnome kingdom will fall to daconia",
							"daconia rocks will kill tree",
							"daconia rocks killed human");
						if (realQuestionMenu3 == 0) {
							wrongQuestionMenu(player, n);
						} else if (realQuestionMenu3 == 1) {
							wrongQuestionMenu(player, n);
						} else if (realQuestionMenu3 == 2) {
							wrongQuestionMenu(player, n);
						} else if (realQuestionMenu3 == 3) {
							say(player, n, "he said a human came to him with the king's seal",
								"hazelmere gave the man daconia rocks",
								"and daconia rocks will kill the tree");
							npcsay(player, n, "of course, i should have known",
								"some one must have forged my royal seal",
								"and convinced hazelmere that i sent for the daconia stones");
							say(player, n, "what are daconia stones?");
							npcsay(player, n, "hazelmere created the daconia stones",
								"they were a safty measure, in case the tree grew out of control",
								"they're the only thing that can kill the tree",
								"this is terrible, those stones must be retrieved");
							say(player, n, "can i help?");
							npcsay(player, n, "first i must warn the tree guardians",
								"please, could you tell the chief tree guardian glough",
								"he lives in a tree house just in front of the grand tree",
								"if he's not there he will be at anita's, his girlfriend",
								"meet me back here once you've told him");
							say(player, n, "ok, i'll be back soon");
							player.updateQuestStage(this, 3);
						} else if (realQuestionMenu3 == 4) {
							wrongQuestionMenu(player, n);
						}
					} else if (realQuestionMenu2 == 2) {
						wrongQuestionMenu(player, n);
					} else if (realQuestionMenu2 == 3) {
						wrongQuestionMenu(player, n);
					} else if (realQuestionMenu2 == 4) {
						wrongQuestionMenu(player, n);
					}
				} else if (realQuestionMenu == 4) {
					wrongQuestionMenu(player, n);
				}
			} else {
				wrongQuestionMenu(player, n);
			}
		}
	}

	private void wrongQuestionMenu(final Player player, final Npc n) {
		npcsay(player, n, "wait a minute, that doesn't sound like hazelmere",
			"are you sure you translated correctly?");
		say(player, n, "erm...i think so");
		npcsay(player, n, "i'm sorry traveller but this is no good",
			"the translation must be perfect or the infomation's no use",
			"please come back when you know exactly what hazelmere said");

		/** Remove the cache if they fail on the third question **/
		if (player.getCache().hasKey("gt_q1")) {
			player.getCache().remove("gt_q1");
		}
		if (player.getCache().hasKey("gt_q2")) {
			player.getCache().remove("gt_q2");
		}
	}

	private void shipyardPasswordMenu2(final Player player, final Npc worker) {
		int menu = multi(player,
			"lo",
			"lu",
			"le");
		if (menu == 0) {
			shipyardPasswordMenu3(player, worker);
		} else if (menu == 1) {
			if (!player.getCache().hasKey("gt_shipyard_q2") && player.getCache().hasKey("gt_shipyard_q1")) {
				player.getCache().store("gt_shipyard_q2", true);
			}
			shipyardPasswordMenu3(player, worker);
		} else if (menu == 2) {
			shipyardPasswordMenu3(player, worker);
		}
	}

	private void wrongShipyardPassword(final Player player, final Npc worker) {
		npcsay(player, worker, "you have no idea");
		worker.setChasing(player);
		/** Remove the cache if they fail on the third question **/
		if (player.getCache().hasKey("gt_shipyard_q1")) {
			player.getCache().remove("gt_shipyard_q1");
		}
		if (player.getCache().hasKey("gt_shipyard_q2")) {
			player.getCache().remove("gt_shipyard_q2");
		}
	}

	private void shipyardPasswordMenu3(final Player player, final Npc worker) {
		int menu = multi(player,
			"mon",
			"min",
			"men");
		if (menu == 0) {
			wrongShipyardPassword(player, worker);
		} else if (menu == 1) {
			if (player.getCache().hasKey("gt_shipyard_q1") && player.getCache().hasKey("gt_shipyard_q2")) {
				// remove to keep efficiency in cache.
				player.getCache().remove("gt_q1");
				player.getCache().remove("gt_q2");
				//continue
				say(player, worker, "ka lu min");
				npcsay(player, worker, "i'm sorry to have kept you",
					"but obviously high security is essential");
				player.teleport(402, 760);
				player.message("the worker opens the gate");
				player.message("you walk through");
				npcsay(player, worker, "you'll need to speak to the foreman",
					"he's on the pier, it'll give you a chance..",
					"...to see the fleet");
			}
		} else if (menu == 2) {
			wrongShipyardPassword(player, worker);
		}
	}

	private void strangeTranslationBox(Player player) {
		ActionSender.sendBox(player,
			"@yel@x@red@z@yel@ql@red@:v@yel@ha @red@za@yel@:v@red@ql@yel@::: @red@h:@yel@xa@red@lat@yel@x @red@vo@yel@xa@red@ha@yel@qa@red@sol @yel@sol@red@:::@yel@:v@red@va% %"
				+
				"@yel@qa@red@:v@yel@::@red@::: @yel@x@red@z@yel@ql@red@:v@yel@ha @red@qe@yel@:v@red@ha @yel@qe@red@:v@yel@za@red@ho@yel@ha@red@xa@yel@:v @red@qi@yel@ho@red@za@yel@vo% %"
				+
				"@red@qe@yel@:v@red@za@yel@ho@red@ha@yel@xa@red@:v @yel@qi@red@ho@yel@za@red@vo@yel@sol @red@h:@yel@xa@red@va@yel@va @red@vo@yel@xa@red@va@yel@va @yel@lat@red@qi@yel@:::@red@:::"
			, true);
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return DataConversions.inArray(new int[] {GLOUGHS_CUPBOARD_OPEN, GLOUGHS_CUPBOARD_CLOSED, TREE_LADDER_UP, TREE_LADDER_DOWN, SHIPYARD_GATE,
				GLOUGH_CHEST_CLOSED, WATCH_TOWER_UP, WATCH_TOWER_DOWN, WATCH_TOWER_STONE_STAND, ROOT_ONE, ROOT_TWO, ROOT_THREE, PUSH_ROOT, PUSH_ROOT_BACK}, obj.getID())
			|| (obj.getID() == STRONGHOLD_GATE && player.getQuestStage(this) == 0);
	}

	@Override
	public void onOpLoc(final Player player, GameObject obj, String command) {
		if (obj.getID() == GLOUGHS_CUPBOARD_OPEN || obj.getID() == GLOUGHS_CUPBOARD_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, player, GLOUGHS_CUPBOARD_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, player, GLOUGHS_CUPBOARD_CLOSED);
			} else {
				mes("you search the cupboard");
				delay(3);
				if (player.getQuestStage(this) == 6) {
					mes("inside you find glough's journal");
					delay(3);
					give(player, ItemId.GLOUGHS_JOURNAL.id(), 1);
					player.updateQuestStage(this, 7);
				} else {
					mes("but find nothing of interest");
					delay(3);
				}
			}
		}
		else if (obj.getID() == TREE_LADDER_UP) {
			player.message("you climb up the ladder");
			player.teleport(417, 2994, false);
		}
		else if (obj.getID() == TREE_LADDER_DOWN) {
			player.message("you climb down the ladder");
			player.teleport(415, 2051, false);
		}
		else if (obj.getID() == SHIPYARD_GATE) {
			if (player.getY() >= 762) {
				if (player.getQuestStage(this) >= 8 && player.getQuestStage(this) <= 9) {
					mes("the gate is locked");
					delay(3);
					final Npc worker = ifnearvisnpc(player, NpcId.SHIPYARD_WORKER_ENTRANCE.id(), 5);
					//Continue
					if (worker != null) {
						npcsay(player, worker, "hey you, what are you up to?");
						say(player, worker, "i'm trying to open the gate");
						npcsay(player, worker, "i can see that, but why?");

						ArrayList<String> options = new ArrayList<>();

						String optionCheckWorking = "i've come to check that you're working safley";
						options.add(optionCheckWorking);

						String optionQuest = "glough sent me";
						if (player.getQuestStage(this) == 8)
							options.add(optionQuest);

						String optionJustLooking = "i just fancied looking around";
						options.add(optionJustLooking);

						String finalOptions[] = new String[options.size()];
						int option = multi(player, worker, options.toArray(finalOptions));
						if (option == -1) return;
						if (options.get(option).equalsIgnoreCase(optionCheckWorking)) {
							npcsay(player, worker, "what business is that of yours?");
							say(player, worker, "as a runescape resident i have a right to know");
							npcsay(player, worker, "get out of here before you get a beating");
							say(player, worker, "that's not very friendly");
							npcsay(player, worker, "right, i'll show you friendly");
							worker.setChasing(player);
						} else if (options.get(option).equalsIgnoreCase(optionQuest)) {
							npcsay(player, worker, "hmm, really, what for?");
							say(player, worker, "your wasting my time, take me to your superior");
							npcsay(player, worker, "ok, i can let you in but i need the password");
							int menu = multi(player,
								"Ka",
								"ko",
								"ke");
							if (menu == 0) {
								if (!player.getCache().hasKey("gt_shipyard_q1")) {
									player.getCache().store("gt_shipyard_q1", true);
								}
								shipyardPasswordMenu2(player, worker);
							} else if (menu == 1) {
								shipyardPasswordMenu2(player, worker);
							} else if (menu == 2) {
								shipyardPasswordMenu2(player, worker);
							}
						} else if (options.get(option).equalsIgnoreCase(optionJustLooking)) {
							npcsay(player, worker, "this isn't a museum",
								"leave now");
							say(player, worker, "i'll leave when i choose");
							npcsay(player, worker, "we'll see");
							worker.setChasing(player);
						}

					} else {
						mes("the gate is locked");
						delay(3);
					}

				} else {
					mes("the gate is locked");
					delay(3);
				}
			} else {
				player.message("you open the gate");
				player.message("and walk through");
				doGate(player, obj, 623);
				player.teleport(401, 763);
			}
		}
		else if (obj.getID() == STRONGHOLD_GATE) {
			if (player.getY() <= 531 || player.getQuestStage(this) != 0) {
				doGate(player, obj, 181);
			}
			else {
				if(player.getCache().hasKey("helped_femi")) {
					doGate(player, obj, 181);
				}
				else {
					Npc femi = ifnearvisnpc(player, NpcId.FEMI.id(), 10);
					if (femi != null) {
						npcsay(player, femi, "hello there");
						say(player, femi, "hi");
						npcsay(player, femi, "could you help me lift this barrel",
								"it's really heavy");
						int menu = multi(player, femi, "sorry i'm a bit busy", "ok then");
						if (menu == 0) {
							npcsay(player, femi, "oh, ok, i'll do it myself");
							player.getCache().store("helped_femi", false);
						}
						else if (menu == 1) {
							npcsay(player, femi, "thanks traveller");
							mes("you help the gnome lift the barrel");
							delay(3);
							mes("it's very heavy and quite hard work");
							delay(3);
							npcsay(player, femi, "thanks again friend");
							player.getCache().store("helped_femi", true);
						}
					} else {
						player.message("the little gnome is busy at the moment");
					}
				}
			}
		}
		else if (obj.getID() == GLOUGH_CHEST_CLOSED) {
			mes("the chest is locked...");
			delay(3);
			mes("...you need a key");
			delay(3);
		}
		else if (obj.getID() == WATCH_TOWER_UP) {
			if (config().WANT_FATIGUE && config().STOP_SKILLING_FATIGUED >= 1 &&
					player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("you are too tired to climb up the tower");
			} else if (getCurrentLevel(player, Skill.AGILITY.id()) >= 25) {
				player.message("you jump up and grab hold of the platform");
				player.teleport(710, 2364);
				player.incExp(Skill.AGILITY.id(), 30, true);
				delay(5);
				player.message("and pull yourself up");
			} else {
				player.message("You need an agility level of 25 to climb up the platform");
			}
		}
		else if (obj.getID() == WATCH_TOWER_DOWN) {
			mes("you climb down the tower");
			delay(3);
			player.teleport(712, 1420);
			mes("and drop to the platform below");
			delay(3);
		}
		// 711  3306 me.
		// 709 3306 glough.
		else if (obj.getID() == WATCH_TOWER_STONE_STAND) {
			if (player.getQuestStage(this) == 15 || player.getQuestStage(this) == 16 || player.getQuestStage(this) == -1) {
				mes("you squeeze down the inner of the tree trunk");
				delay(3);
				mes("you drop out of the bottom onto a mud floor");
				delay(3);
				player.teleport(711, 3306);
				return;
			}
			mes("you push down on the pillar");
			delay(3);
			player.message("you feel it shift downwards slightly");
			delay(4);
			if ((player.getCache().hasKey("pebble_1") && player.getCache().hasKey("pebble_2") && player.getCache().hasKey("pebble_3") && player.getCache().hasKey("pebble_4")) || player.getQuestStage(this) == 14) {
				if (player.getCache().hasKey("pebble_1")) {
					player.getCache().remove("pebble_1");
				}
				if (player.getCache().hasKey("pebble_2")) {
					player.getCache().remove("pebble_2");
				}
				if (player.getCache().hasKey("pebble_3")) {
					player.getCache().remove("pebble_3");
				}
				if (player.getCache().hasKey("pebble_4")) {
					player.getCache().remove("pebble_4");
				}
				if (player.getQuestStage(this) == 13) {
					player.updateQuestStage(this, 14);
				}
				player.message("the pillar shifts back revealing a ladder");
				delay(4);
				mes("it seems to lead down through the tree trunk");
				delay(3);
				int menu = multi(player, "climb down", "come back later");
				if (menu == 0) {
					mes("you squeeze down the inner of the tree trunk");
					delay(3);
					mes("you drop out of the bottom onto a mud floor");
					delay(3);
					player.teleport(711, 3306);
					mes("around you, you can see piles of strange looking rocks");
					delay(3);
					mes("you here the sound of small footsteps coming from the darkness");
					delay(3);
					//glough despawns in almost 1 minute
					Npc n = addnpc(player.getWorld(), NpcId.GLOUGH_UNDERGROUND.id(), 709, 3306, (int)TimeUnit.SECONDS.toMillis(63));
					npcsay(player, n, "you really are becoming a headache",
						"well, at least now you can die knowing you were right",
						"it will save me having to hunt you down",
						"like all the over human filth of runescape");
					say(player, n, "you're crazy glough");
					npcsay(player, n, "i'm angry, you think you're so special",
						"well, soon you'll see, the gnome's are ready to fight",
						"in three weeks this tree will be dead wood",
						"in ten weeks it will be 30 battleships",
						"ready to finally rid the world of the disease called humanity");
					say(player, n, "what makes you think i'll let you get away with it?");
					npcsay(player, n, "ha, do you think i would challange you humans alone",
						"fool.....meet my little friend");
					mes("from the darkness you hear a deep growl");
					delay(3);
					mes("and the sound of heavy footsteps");
					delay(3);
					Npc demon = addnpc(player.getWorld(), NpcId.BLACK_DEMON_GRANDTREE.id(), 709, 3306, (int)TimeUnit.SECONDS.toMillis(250));
					if (demon != null) {
						npcYell(player, demon, "grrrrr");
						demon.setChasing(player);
					}
				} else if (menu == 1) {
					player.message("you decide to come back later");
				}
			} else {
				player.message("you here some noise below the pillar...");
				player.message("...but nothing seems to happen");

			}
		}
		else if (obj.getID() == ROOT_ONE || obj.getID() == ROOT_TWO || obj.getID() == ROOT_THREE) {
			mes("you search the root...");
			delay(3);
			if (obj.getID() == ROOT_THREE) {
				if (player.getQuestStage(this) == 16) {
					if (!player.getCarriedItems().hasCatalogID(ItemId.DACONIA_ROCK.id(), Optional.empty())) {
						mes("and find a small glowing rock");
						delay(3);
						give(player, ItemId.DACONIA_ROCK.id(), 1);
					} else {
						mes("but find nothing");
						delay(3);
					}
				} else {
					mes("...but find nothing");
					delay(3);
				}
			} else {
				mes("...but find nothing");
				delay(3);
			}
		}
		else if (obj.getID() == PUSH_ROOT || obj.getID() == PUSH_ROOT_BACK) { // ACCESS TO GNOME MINE
			mes("you push the roots");
			delay(3);
			if (player.getQuestStage(this) == -1) {
				mes("they wrap around your arms");
				delay(3);
				player.message("and drag you deeper forwards");
				if (obj.getID() == PUSH_ROOT_BACK) {
					player.teleport(700, 3280);
				} else {
					player.teleport(701, 3278);
				}
			} else {
				mes("they don't seem to mind");
				delay(3);
			}
		}
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		return n.getID() == NpcId.CHARLIE.id() || (n.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id() && player.getQuestStage(this) == 10);
	}

	@Override
	public void onAttackNpc(Player player, Npc affectedmob) {
		if (affectedmob.getID() == NpcId.CHARLIE.id()) {
			player.message("you can't attack through the bars");
		}
		else if (affectedmob.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id() && player.getQuestStage(this) == 10) {
			player.message("the forman is too busy to talk");
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id() || n.getID() == NpcId.BLACK_DEMON_GRANDTREE.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SHIPYARD_FOREMAN_HUT.id()) {
			if (player.getQuestStage(this) == 9) {
				mes("you kill the foreman");
				delay(3);
				mes("inside his pocket you find an invoice..");
				delay(3);
				mes("it seems to be an order for timber");
				delay(3);
				give(player, ItemId.INVOICE.id(), 1);
				player.updateQuestStage(this, 10);
			}
		}
		else if (n.getID() == NpcId.BLACK_DEMON_GRANDTREE.id()) {
			if (player.getQuestStage(this) == 14) {
				mes("the beast slumps to the floor");
				delay(3);
				mes("glough has fled");
				delay(3);
				player.updateQuestStage(this, 15);
				Npc fleeGlough = ifnearvisnpc(player, NpcId.GLOUGH_UNDERGROUND.id(), 15);
				if (fleeGlough != null) {
					fleeGlough.remove();
				}
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (obj.getID() == GLOUGH_CHEST_CLOSED && item.getCatalogId() == ItemId.GLOUGHS_KEY.id())
				|| (obj.getID() == WATCH_TOWER_STONE_STAND && (item.getCatalogId() == ItemId.PEBBLE_3.id()
				|| item.getCatalogId() == ItemId.PEBBLE_2.id() || item.getCatalogId() == ItemId.PEBBLE_4.id() || item.getCatalogId() == ItemId.PEBBLE_1.id()));
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == GLOUGH_CHEST_CLOSED && item.getCatalogId() == ItemId.GLOUGHS_KEY.id()) {
			mes("the key fits the chest");
			delay(3);
			player.message("you open the chest");
			player.message("and search it...");
			changeloc(obj, config().GAME_TICK * 5, GLOUGH_CHEST_OPEN);
			mes("inside you find some paper work");
			delay(3);
			player.message("and an old gnome tongue translation book");
			give(player, ItemId.GLOUGHS_NOTES.id(), 1);
			give(player, ItemId.TREE_GNOME_TRANSLATION.id(), 1);
			if (player.getQuestStage(this) == 11) {
				player.updateQuestStage(this, 12);
			}
			mes("you close the chest");
			delay(3);
			if (player.getCache().hasKey("helped_femi") && player.getQuestStage(this) > 10) {
				//no longer needed
				player.getCache().remove("helped_femi");
			}
		}
		else if (obj.getID() == WATCH_TOWER_STONE_STAND && (item.getCatalogId() == ItemId.PEBBLE_3.id()
				|| item.getCatalogId() == ItemId.PEBBLE_2.id() || item.getCatalogId() == ItemId.PEBBLE_4.id() || item.getCatalogId() == ItemId.PEBBLE_1.id())) {
			mes("on top are four pebble size indents");
			delay(3);
			mes("they span from left to right");
			delay(3);
			mes("you place the pebble...");
			delay(3);
			int menu = multi(player, "To the far left", "Centre left", "Centre right", "To the far right");
			if (menu == 0) {
				mes("you place the pebble in the indent");
				delay(3);
				mes("it crumbles into dust");
				delay(3);
				player.getCarriedItems().remove(new Item(item.getCatalogId()));
				if (item.getCatalogId() == ItemId.PEBBLE_1.id()) { // HO
					if (!player.getCache().hasKey("pebble_1")) {
						player.getCache().store("pebble_1", true);
					}
				}
			} else if (menu == 1) {
				mes("you place the pebble in the indent");
				delay(3);
				mes("it crumbles into dust");
				delay(3);
				player.getCarriedItems().remove(new Item(item.getCatalogId()));
				if (item.getCatalogId() == ItemId.PEBBLE_2.id()) { // NI
					if (!player.getCache().hasKey("pebble_2")) {
						player.getCache().store("pebble_2", true);
					}
				}
			} else if (menu == 2) {
				mes("you place the pebble in the indent");
				delay(3);
				mes("it crumbles into dust");
				delay(3);
				player.getCarriedItems().remove(new Item(item.getCatalogId()));
				if (item.getCatalogId() == ItemId.PEBBLE_3.id()) { // :::
					if (!player.getCache().hasKey("pebble_3")) {
						player.getCache().store("pebble_3", true);
					}
				}
			} else if (menu == 3) {
				mes("you place the pebble in the indent");
				delay(3);
				mes("it crumbles into dust");
				delay(3);
				player.getCarriedItems().remove(new Item(item.getCatalogId()));
				if (item.getCatalogId() == ItemId.PEBBLE_4.id()) { // HA
					if (!player.getCache().hasKey("pebble_4")) {
						player.getCache().store("pebble_4", true);
					}
				}
			}
		}
	}

}
