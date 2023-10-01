package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class BioHazard implements QuestInterface, TalkNpcTrigger,
	OpBoundTrigger, OpLocTrigger, UseLocTrigger, KillNpcTrigger {

	/**
	 * 1.Decided to add the door into Elena for starting the Biohazard quest in this template,
	 * instead of doors class.
	 **/

	/**
	 * BIG NOTE: START UNDERGROUND PASS ON KING LATHAS NPC IN THIS CLASS!
	 **/

	// OBJECTS
	private static final int ELENAS_DOOR = 152;
	private static final int JERICOS_CUPBOARD_ONE_OPEN = 71;
	private static final int JERICOS_CUPBOARD_ONE_CLOSED = 56;
	private static final int JERICOS_CUPBOARD_TWO_OPEN = 500;
	private static final int JERICOS_CUPBOARD_TWO_CLOSED = 499;
	private static final int WATCH_TOWER = 494;
	private static final int VISUAL_ROPELADDER = 498;
	private static final int COOKING_POT = 502;
	private static final int NURSE_SARAHS_CUPBOARD_OPEN = 510;
	private static final int NURSE_SARAHS_CUPBOARD_CLOSED = 509;
	private static final int GET_INTO_CRATES_GATE = 504;
	private static final int DISTILLATOR_CRATE = 505;
	private static final int OTHER_CRATE = 290;

	@Override
	public int getQuestId() {
		return Quests.BIOHAZARD;
	}

	@Override
	public String getQuestName() {
		return "Biohazard (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.BIOHAZARD.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		final QuestReward reward = Quest.BIOHAZARD.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP(), true);
		}
		player.message("you have completed the biohazard quest");
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.ELENA_HOUSE.id(), NpcId.OMART.id(), NpcId.JERICO.id(), NpcId.KILRON.id(), NpcId.NURSE_SARAH.id(),
				NpcId.CHEMIST.id(), NpcId.CHANCY.id(), NpcId.HOPS.id(), NpcId.DEVINCI.id(), NpcId.KING_LATHAS.id(), NpcId.CHANCY_BAR.id(),
				NpcId.HOPS_BAR.id(), NpcId.DEVINCI_BAR.id(), NpcId.GUIDORS_WIFE.id(), NpcId.GUIDOR.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			freePlayerDialogue(player, n);
			return;
		}
		if (n.getID() == NpcId.ELENA_HOUSE.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "good to see you, elena");
					npcsay(player, n, "you too, thanks for freeing me",
						"it's just a shame the mourners confiscated my equipment");
					say(player, n, "what did they take?");
					npcsay(player, n, "my distillator, I can't test any plague samples without it",
						"they're holding it in the mourner quarters in west ardounge",
						"i must somehow retrieve that distillator",
						"if i am to find a cure for this awful affliction");
					int menu = multi(player, n,
						"i'll try to retrieve it for you",
						"well, good luck");
					if (menu == 0) {
						/***************************/
						/** START BIOHAZARD QUEST! **/
						/***************************/
						npcsay(player, n, "i was hoping you would say that",
							"unfortunately they discovered the tunnel and filled it in",
							"we need another way over the wall");
						say(player, n, "any ideas?");
						npcsay(player, n, "my father's friend jerico is in communication with west ardounge",
							"he might be able to help",
							"he lives next to the chapel");
						player.updateQuestStage(this, 1);
					} else if (menu == 1) {
						npcsay(player, n, "thanks traveller");
					}
					break;
				case 1:
					say(player, n, "hello elena");
					npcsay(player, n, "hello brave adventurer",
						"any luck finding the distillator");
					say(player, n, "no i'm afraid not");
					npcsay(player, n, "speak to jerico, he will help you to cross the wall",
						"he lives next to the chapel");
					break;
				case 2:
					say(player, n, "hello elena, i've spoken to jerico");
					npcsay(player, n, "was he able to help?");
					say(player, n, "he has two friends who will help me cross the wall",
						"but first i need to distract the watch tower");
					npcsay(player, n, "hmmm, could be tricky");
					break;
				case 3:
					say(player, n, "elena i've distracted the guards at the watch tower");
					npcsay(player, n, "yes, i saw",
						"quickly meet with jerico's friends and cross the wall",
						"before the pigeons fly off");
					break;
				case 4:
					say(player, n, "hello again");
					npcsay(player, n, "you're back, did you find the distillator?");
					say(player, n, "i'm afraid not");
					npcsay(player, n, "i can't test the samples without the distillator",
						"please don't give up until you find it");
					break;
				case 5:
					npcsay(player, n, "so, have you managed to retrieve my distillator?");
					if (player.getCarriedItems().hasCatalogID(ItemId.DISTILLATOR.id(), Optional.of(false))) {
						npcsay(player, n, "You have - that's great!",
							"Now can you pass me those refraction agents please?");
						mes("You hand Elena the distillator and an assortment of vials");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.DISTILLATOR.id()));
						say(player, n, "These look pretty fancy");
						npcsay(player, n, "Well, yes and no. The liquid honey isn't worth so much",
							"But the others are- especially this colourless ethenea",
							"And be careful with the sulphuric broline- it's highly poisonous");
						say(player, n, "You're not kidding- I can smell it from here");
						mes("Elena puts the agents through the distillator");
						delay(3);
						npcsay(player, n, "I don't understand...the touch paper hasn't changed colour at all",
							"You'll need to go and see my old mentor Guidor. He lives in Varrock",
							"Take these vials and this sample to him");
						mes("elena gives you three vials and a sample in a tin container");
						delay(3);
						give(player, ItemId.LIQUID_HONEY.id(), 1);
						give(player, ItemId.ETHENEA.id(), 1);
						give(player, ItemId.SULPHURIC_BROLINE.id(), 1);
						give(player, ItemId.PLAGUE_SAMPLE.id(), 1);
						npcsay(player, n, "But first you'll need some more touch-paper. Go and see the chemist in Rimmington",
							"Just don't get into any fights, and be careful who you speak to",
							"Those vials are fragile, and plague carriers don't tend to be too popular");
						player.updateQuestStage(this, 6);
					} else {
						say(player, n, "i'm afraid not");
						npcsay(player, n, "Oh, you haven't",
							"People may be dying even as we speak");
					}
					break;
				case 6:
				case 7:
					npcsay(player, n, "what are you doing back here");
					int menu6 = multi(player, n, false, //do not send over
						"I just find it hard to say goodbye sometimes",
						"I'm afraid I've lost some of the stuff that you gave me...",
						"i've forgotten what i need to do");
					if (menu6 == 0) {
						say(player, n, "I just find it hard to say goodbye sometimes");
						npcsay(player, n, "Yes...I have feelings for you too...",
							"Now get to work!");
					} else if (menu6 == 1) {
						say(player, n, "I'm afraid I've you lost some of the stuff that you gave me");
						npcsay(player, n, "That's alright, I've got plenty");
						mes("Elena replaces your items");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.LIQUID_HONEY.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.LIQUID_HONEY.id()));
						player.getCarriedItems().remove(new Item(ItemId.ETHENEA.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.ETHENEA.id()));
						player.getCarriedItems().remove(new Item(ItemId.SULPHURIC_BROLINE.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.SULPHURIC_BROLINE.id()));
						player.getCarriedItems().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.PLAGUE_SAMPLE.id()));
						npcsay(player, n, "OK so that's the colourless ethenea...",
							"Some highly toxic sulphuric broline...",
							"And some bog-standard liquid honey...");
						say(player, n, "Great. I'll be on my way");
					} else if (menu6 == 2) {
						say(player, n, "i've forgotten what i need to do");
						npcsay(player, n, "go to rimmington and get some touch paper from the chemist",
							"use his errand boys to smuggle the vials into varrock",
							"then go to varrock and take the sample to guidor, my old mentor");
						say(player, n, "ok, i'll get to it");
					}
					break;
				case 8:
					npcsay(player, n, "You're back! So what did Guidor say?");
					say(player, n, "Nothing");
					npcsay(player, n, "What?");
					say(player, n, "He said that there is no plague");
					npcsay(player, n, "So what, this thing has all been a big hoax?");
					say(player, n, "Or maybe we're about to uncover something huge");
					npcsay(player, n, "Then I think this thing may be bigger than both of us");
					say(player, n, "What do you mean?");
					npcsay(player, n, "I mean that you need to go right to the top",
						"You need to see the King of east Ardougne");
					player.updateQuestStage(this, 9);
					break;
				case 9:
					say(player, n, "hello elena");
					npcsay(player, n, "you must go to king lathas immediately");
					break;
				case -1:
					say(player, n, "hello elena");
					npcsay(player, n, "hey, how are you?");
					say(player, n, "good thanks, yourself?");
					npcsay(player, n, "not bad, let me know when you hear from king lathas again");
					say(player, n, "will do");
					break;
			}
		}
		else if (n.getID() == NpcId.OMART.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
					say(player, n, "hello there");
					npcsay(player, n, "hello");
					say(player, n, "how are you?");
					npcsay(player, n, "fine thanks");
					break;
				case 2:
					say(player, n, "omart, jerico said you might be able to help me");
					npcsay(player, n, "he informed me of your problem traveller",
						"i would be glad to help, i have a rope ladder",
						"and my associate, kilron, is waiting on the other side");
					say(player, n, "good stuff");
					npcsay(player, n, "unfortunately we can't risk it with the watch tower so close",
						"so first we need to distract the guards in the tower");
					say(player, n, "how?");
					npcsay(player, n, "try asking jerico, if he's not too busy with his pigeons",
						"I'll be waiting here for you");
					break;
				case 3:
					npcsay(player, n, "well done, the guards are having real trouble with those birds",
						"you must go now traveller, it's your only chance");
					mes("Omart calls to his associate");
					delay(3);
					npcsay(player, n, "Kilron!");
					mes("he throws one end of the rope ladder over the wall");
					delay(3);
					npcsay(player, n, "go now traveller");
					int menu = multi(player, n,
						"ok lets do it",
						"I'll be back soon");
					if (menu == 0) {
						ropeLadderInFunction(player);
						player.updateQuestStage(this, 4);
					} else if (menu == 1) {
						npcsay(player, n, "don't take long",
							"the mourners will soon be rid of those birds");
					}
					break;
				case 4:
				case 5:
					say(player, n, "hello omart");
					npcsay(player, n, "hello traveller",
						"the guards are still distracted if you wish to cross the wall");
					int OverAgain = multi(player, n, false, //do not send over
						"ok lets do it",
						"i'll be back soon");
					if (OverAgain == 0) {
						say(player, n, "ok lets do it");
						ropeLadderInFunction(player);
					} else if (OverAgain == 1) {
						say(player, n, "I'll be back soon");
						npcsay(player, n, "don't take long",
							"the mourners will soon be rid of those birds");
					}
					break;
				case 6:
				case 7:
				case 8:
				case 9:
				case -1:
					say(player, n, "hello omart");
					npcsay(player, n, "hello adventurer",
						"i'm afraid it's too risky to use the ladder again",
						"but I believe that edmond's working on another tunnel");
					break;
			}
		}
		else if (n.getID() == NpcId.JERICO.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello");
					npcsay(player, n, "can i help you?");
					say(player, n, "just passing by");
					break;
				case 1:
					say(player, n, "hello jerico");
					npcsay(player, n, "hello, i've been expecting you",
						"elena tells me you need to cross the wall");
					say(player, n, "that's right");
					npcsay(player, n, "my messenger pigeons help me communicate with friends over the wall",
						"i have arranged for two friends to aid you with a rope ladder",
						"omart is waiting for you at the southend of the wall",
						"be careful, if the mourners catch you the punishment will be severe");
					say(player, n, "thanks jerico");
					player.updateQuestStage(this, 2);
					break;
				case 2:
					say(player, n, "hello jerico");
					npcsay(player, n, "hello again",
						"you'll need someway to distract the watch tower",
						"otherwise you'll be caught for sure");
					say(player, n, "any ideas?");
					npcsay(player, n, "sorry, try asking omart",
						"i really must get back to feeding the messenger birds");
					break;
				case 3:
					say(player, n, "hello there");
					npcsay(player, n, "the guards are distracted by the birds",
						"you must go now",
						"quickly traveller");
					break;
				case 4:
					say(player, n, "hello again jerico");
					npcsay(player, n, "so you've returned traveller",
						"did you get what you wanted");
					say(player, n, "not yet");
					npcsay(player, n, "omart will be waiting by the wall",
						"In case you need to cross again");
					break;
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case -1:
					player.message("jerico is busy looking for his bird feed");
					break;
			}
		}
		else if (n.getID() == NpcId.KILRON.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
				case 3:
					say(player, n, "hello there");
					npcsay(player, n, "hello");
					say(player, n, "how are you?");
					npcsay(player, n, "busy");
					break;
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case -1:
					say(player, n, "hello kilron");
					npcsay(player, n, "hello traveller",
						"do you need to go back over?");
					int menu = multi(player, n,
						"not yet kilron",
						"yes i do");
					if (menu == 0) {
						npcsay(player, n, "okay, just give me the word");
					} else if (menu == 1) {
						npcsay(player, n, "okay, quickly now");
						ropeLadderBackFunction(player);
					}
					break;
			}
		}
		else if (n.getID() == NpcId.NURSE_SARAH.id()) {
			switch (player.getQuestStage(this)) {
				case 4:
				case 5:
					if (player.getCache().hasKey("rotten_apples")) {
						say(player, n, "hello nurse");
						npcsay(player, n, "oh hello there");
						npcsay(player, n, "im afraid i can't stop and talk",
							"a group of mourners have became ill with food poisoning",
							"i need to go over and see what i can do");
						say(player, n, "hmmm, strange that!");
					} else {
						say(player, n, "hello nurse");
						npcsay(player, n, "i don't know how much longer i can cope here");
						say(player, n, "what? is the plague getting to you?");
						npcsay(player, n, "no, strangely enough the people here don't seem to be affected",
							"it's just the awful living conditions that are making people ill");
						say(player, n, "i was under the impression that every one here was affected");
						npcsay(player, n, "me too, but it doesn't seem to be the case");
					}
					break;
				default:
					player.message("nurse sarah doesn't feel like talking");
					break;
			}
		}
		else if (n.getID() == NpcId.HOPS_BAR.id()) {
			if (player.getQuestStage(this) == 7) {
				if (player.getCache().hasKey("wrong_vial_hops")) {
					say(player, n, "Hello. How was your journey?");
					npcsay(player, n, "Pretty thirst-inducing actually...");
					say(player, n, "Please tell me that you haven't drunk the contents");
					npcsay(player, n, "Of course I can tell you that I haven't drunk the contents",
						"But I'd be lying",
						"Sorry about that me old mucker- can I get you a drink?");
					say(player, n, "No, I think you've done enough for now");
					player.getCache().remove("wrong_vial_hops");
				} else if (player.getCache().hasKey("vial_hops")) {
					say(player, n, "Hello. How was your journey?");
					npcsay(player, n, "Pretty thirst-inducing actually...");
					say(player, n, "Please tell me that you haven't drunk the contents");
					npcsay(player, n, "Oh the gods no! What do you take me for?",
						"Besides, the smell kind of put me off ",
						"Here's your vial anyway");
					player.message("He gives you the vial of sulphuric broline");
					give(player, ItemId.SULPHURIC_BROLINE.id(), 1);
					say(player, n, "Thanks. I'll leave you to your drink now");
					player.getCache().remove("vial_hops");
				}
			} else {
				player.message("Hops doesn't feel like talking");
			}
		}
		else if (n.getID() == NpcId.DEVINCI_BAR.id()) {
			if (player.getQuestStage(this) == 7) {
				if (player.getCache().hasKey("wrong_vial_vinci")) {
					npcsay(player, n, "Hello again",
						"I hope your journey was as pleasant as mine");
					say(player, n, "Yep. Anyway, I'll take the package off you now");
					npcsay(player, n, "Package? That's a funny way to describe a liquid of such exquisite beauty");
					int menu = multi(player, n,
						"I'm getting a bad feeling about this",
						"Just give me the stuff now please");
					if (menu == 0) {
						say(player, n, "You do still have it don't you?");
						npcsay(player, n, "Absolutely",
							"Its' just not stored in a vial anymore");
						say(player, n, "What?");
						npcsay(player, n, "Instead it has been liberated",
							"And it now gleams from the canvas of my latest epic:",
							"The Majesty of Varrock");
						say(player, n, "That's great",
							"Thanks to you I'll have to walk back to East Ardougne to get another vial");
						npcsay(player, n, "Well you can't put a price on art");
						player.getCache().remove("wrong_vial_vinci");
					} else if (menu == 1) {
						say(player, n, "You do still have it don't you?");
						npcsay(player, n, "Absolutely",
							"Its' just not stored in a vial anymore");
						say(player, n, "What?");
						npcsay(player, n, "Instead it has been liberated",
							"And it now gleams from the canvas of my latest epic:",
							"The Majesty of Varrock");
						say(player, n, "That's great",
							"Now I'll have to walk all the way back to East Ardougne to get another vial");
						npcsay(player, n, "Well you can't put a price on art");
						player.getCache().remove("wrong_vial_vinci");
					}
				} else if (player.getCache().hasKey("vial_vinci")) {
					npcsay(player, n, "Hello again",
						"I hope your journey was as pleasant as mine");
					say(player, n, "Well, it's always sunny in Runescape, as they say");
					npcsay(player, n, "OK. Here it is");
					mes("He gives you the vial of ethenea");
					delay(3);
					give(player, ItemId.ETHENEA.id(), 1);
					say(player, n, "Thanks. You've been a big help");
					player.getCache().remove("vial_vinci");
				}
			} else {
				player.message("devinci doesn't feel like talking");

			}
		}
		else if (n.getID() == NpcId.CHANCY_BAR.id()) {
			if (player.getQuestStage(this) == 7) {
				if (player.getCache().hasKey("wrong_vial_chancy")) {
					say(player, n, "Hi.Thanks for doing that");
					npcsay(player, n, "No problem. I've got some money for you actually");
					say(player, n, "What do you mean?");
					npcsay(player, n, "Well it turns out that that potion you gave me was quite valuable...");
					say(player, n, "What?");
					npcsay(player, n, "And I know that I probably shouldn't have sold it...",
						"But some friends and I were having a little wager- the odds were just too good");
					say(player, n, "You sold my vial and gambled with the money?");
					npcsay(player, n, "Actually, yes... but praise be to Saradomin, because I won!",
						"So all's well that ends well right?");
					int menu = multi(player, n,
						"No. Nothing could be further from the truth",
						"You have no idea of what you have just done");
					if (menu == 0) {
						npcsay(player, n, "Well there's no pleasing some people");
						player.getCache().remove("wrong_vial_chancy");
					} else if (menu == 1) {
						npcsay(player, n, "Ignorance is bliss I'm afraid");
						player.getCache().remove("wrong_vial_chancy");
					}
				} else if (player.getCache().hasKey("vial_chancy")) {
					say(player, n, "Hi.Thanks for doing that");
					npcsay(player, n, "No problem");
					player.message("He gives you the vial of liquid honey");
					give(player, ItemId.LIQUID_HONEY.id(), 1);
					npcsay(player, n, "Next time give me something more valuable",
						"I couldn't get anything for this on the blackmarket");
					say(player, n, "That was the idea");
					player.getCache().remove("vial_chancy");
				}
			} else {
				player.message("chancy doesn't feel like talking");

			}
		}
		else if (n.getID() == NpcId.HOPS.id()) {
			if (player.getQuestStage(this) == 7) {
				if (player.getCache().hasKey("vial_hops") || player.getCache().hasKey("wrong_vial_hops")) {
					npcsay(player, n, "I suppose I'd better get going",
						"I'll meet you at the The dancing donkey inn");
					return;
				}
				say(player, n, "Hi,I've got something for you to take to Varrock");
				npcsay(player, n, "Sounds like pretty thirsty work");
				say(player, n, "Well, there's a pub in Varrock if you're desperate");
				npcsay(player, n, "Don't worry, I'm a pretty resourceful fellow you know");
				int menu = multi(player, n, false, //do not send over
					"You give him the vial of ethenea",
					"You give him the vial of liquid honey",
					"You give him the vial of sulphuric broline");
				if (menu == 0) {
					if (player.getCarriedItems().hasCatalogID(ItemId.ETHENEA.id(), Optional.of(false))) {
						if (!player.getCache().hasKey("wrong_vial_hops")) {
							player.getCache().store("wrong_vial_hops", true);
							player.getCarriedItems().remove(new Item(ItemId.ETHENEA.id()));
							player.message("You give him the vial of ethenea");
							say(player, n, "OK. I'll see you in Varrock");
							npcsay(player, n, "Sure. I'm a regular at the The dancing donkey inn as it happens");
						}
					} else {
						player.message("You have no ethenea to give");
					}
				} else if (menu == 1) {
					if (player.getCarriedItems().hasCatalogID(ItemId.LIQUID_HONEY.id(), Optional.of(false))) {
						if (!player.getCache().hasKey("wrong_vial_hops")) {
							player.getCache().store("wrong_vial_hops", true);
							player.getCarriedItems().remove(new Item(ItemId.LIQUID_HONEY.id()));
							player.message("You give him the vial of liquid honey");
							say(player, n, "OK. I'll see you in Varrock");
							npcsay(player, n, "Sure. I'm a regular at the The dancing donkey inn as it happens");
						}
					} else {
						player.message("You have no liquid honey to give");
					}
				} else if (menu == 2) {
					if (player.getCarriedItems().hasCatalogID(ItemId.SULPHURIC_BROLINE.id(), Optional.of(false))) {
						if (!player.getCache().hasKey("vial_hops")) {
							player.getCache().store("vial_hops", true);
							player.getCarriedItems().remove(new Item(ItemId.SULPHURIC_BROLINE.id()));
							player.message("You give him the vial of sulphuric broline");
							say(player, n, "OK. I'll see you in Varrock");
							npcsay(player, n, "Sure. I'm a regular at the The dancing donkey inn as it happens");
						}
					} else {
						player.message("You have no sulphuric broline to give");
					}
				}
			} else {
				player.message("He is not in a fit state to talk");
			}
		}
		else if (n.getID() == NpcId.CHANCY.id()) {
			if (player.getQuestStage(this) == 7) {
				if (player.getCache().hasKey("vial_chancy") || player.getCache().hasKey("wrong_vial_chancy")) {
					npcsay(player, n, "look, I've got your vial, but I'm not taking two",
						"I always like to play the percentages");
					return;
				}
				say(player, n, "Hello, I've got a vial for you to take to Varrock");
				npcsay(player, n, "Tssch... that chemist asks a lot for the wages he pays");
				say(player, n, "Maybe you should ask him for more money");
				npcsay(player, n, "Nah...I just use my initiative here and there");
				int menu = multi(player, n, false, //do not send over
					"You give him the vial of ethenea",
					"You give him the vial of liquid honey",
					"You give him the vial of sulphuric broline");
				if (menu == 0) {
					if (player.getCarriedItems().hasCatalogID(ItemId.ETHENEA.id(), Optional.of(false))) {
						if (!player.getCache().hasKey("wrong_vial_chancy")) {
							player.getCache().store("wrong_vial_chancy", true);
							player.getCarriedItems().remove(new Item(ItemId.ETHENEA.id()));
							mes("You give him the vial of ethenea");
							delay(3);
							say(player, n, "Right. I'll see you later in the dancing donkey inn");
							npcsay(player, n, "Be lucky");
						}
					} else {
						player.message("You can't give him what you don't have");
					}
				} else if (menu == 1) {
					if (player.getCarriedItems().hasCatalogID(ItemId.LIQUID_HONEY.id(), Optional.of(false))) {
						if (!player.getCache().hasKey("vial_chancy")) {
							player.getCache().store("vial_chancy", true);
							player.getCarriedItems().remove(new Item(ItemId.LIQUID_HONEY.id()));
							mes("You give him the vial of liquid honey");
							delay(3);
							say(player, n, "Right. I'll see you later in the dancing donkey inn");
							npcsay(player, n, "Be lucky");
						}
					} else {
						player.message("You can't give him what you don't have");
					}
				} else if (menu == 2) {
					if (player.getCarriedItems().hasCatalogID(ItemId.SULPHURIC_BROLINE.id(), Optional.of(false))) {
						if (!player.getCache().hasKey("wrong_vial_chancy")) {
							player.getCache().store("wrong_vial_chancy", true);
							player.getCarriedItems().remove(new Item(ItemId.SULPHURIC_BROLINE.id()));
							mes("You give him the vial of sulphuric broline");
							delay(3);
							say(player, n, "Right.I'll see you later in the dancing donkey inn");
							npcsay(player, n, "Be lucky");
						}
					} else {
						player.message("You can't give him what you don't have");
					}
				}
			} else {
				player.message("Chancy doesn't feel like talking");
			}
		}
		else if (n.getID() == NpcId.CHEMIST.id()) {
			if (player.getQuestStage(this) == 7) {
				say(player, n, "hello again");
				npcsay(player, n, "oh hello, do you need more touch paper?");
				if (!player.getCarriedItems().hasCatalogID(ItemId.TOUCH_PAPER.id(), Optional.empty())) {
					say(player, n, "yes please");
					npcsay(player, n, "ok there you go");
					player.message("the chemist gives you some touch paper");
					give(player, ItemId.TOUCH_PAPER.id(), 1);
				} else {
					say(player, n, "no i just wanted to say hello");
					npcsay(player, n, "oh, ok then ... hello");
					say(player, n, "hi");
				}
				return;
			} else if (player.getCarriedItems().hasCatalogID(ItemId.PLAGUE_SAMPLE.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.LIQUID_HONEY.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.SULPHURIC_BROLINE.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.ETHENEA.id(), Optional.of(false)) && player.getQuestStage(this) == 6) {
				npcsay(player, n, "Sorry, I'm afraid we're just closing now, you'll have to come back another time");
				int menu = multi(player, n, "This can't wait,I'm carrying a plague sample that desperately needs analysis",
					"It's OK I'm Elena's friend");
				if (menu == 0) {
					npcsay(player, n, "You idiot! A plague sample should be confined to a lab",
						"I'm taking it off you- I'm afraid it's the only responsible thing to do");
					player.message("He takes the plague sample from you");
					player.getCarriedItems().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
				} else if (menu == 1) {
					npcsay(player, n, "Oh, well that's different then. Must be pretty important to come all this way",
						"How's everyone doing there anyway? Wasn't there was some plague scare");
					int lastMenu = multi(player, n,
						"that's why I'm here: I need some more touch paper for this plague sample",
						"Who knows... I just need some touch paper for a guy called Guidor");
					if (lastMenu == 0) {
						npcsay(player, n, "You idiot! A plague sample should be confined to a lab",
							"I'm taking it off you- I'm afraid it's the only responsible thing to do");
						player.message("He takes the plague sample from you");
						player.getCarriedItems().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
					} else if (lastMenu == 1) {
						npcsay(player, n, "Guidor? This one's on me then- the poor guy. Sorry about the interrogation",
							"It's just that there's been rumours of a man travelling with a plague on him",
							"They're even doing spot checks in Varrock: it's a pharmeceutical disaster");
						say(player, n, "Oh right...so am I going to be OK carrying these three vials with me?");
						npcsay(player, n, "With touch paper as well? You're asking for trouble",
							"You'd be better using my errand boys outside- give them a vial each",
							"They're not the most reliable people in the world",
							"One's a painter, one's a gambler, and one's a drunk",
							"Still, if you pay peanuts you'll get monkeys, right?",
							"And it's better than entering Varrock with half a laborotory in your napsack");
						say(player, n, "OK- thanks for your help, I know that Elena appreciates it");
						npcsay(player, n, "Yes well don't stand around here gassing",
							"You'd better hurry if you want to see Guidor",
							"He won't be around for much longer");
						player.message("He gives you the touch paper");
						give(player, ItemId.TOUCH_PAPER.id(), 1);
						player.updateQuestStage(this, 7);

					}
				}
			} else {
				player.message("The chemist is busy at the moment");
			}
		}
		else if (n.getID() == NpcId.DEVINCI.id()) {
			if (player.getQuestStage(this) == 7) {
				if (player.getCache().hasKey("vial_vinci") || player.getCache().hasKey("wrong_vial_vinci")) {
					npcsay(player, n, "Oh, it's you again",
						"Please don't distract me now, I'm contemplating the sublime");
					return;
				}
				say(player, n, "Hello.i hear you're an errand boy for the chemist");
				npcsay(player, n, "Well that's my day job yes",
					"But I don't necessarily define my identity in such black and white terms");
				say(player, n, "Good for you",
					"Now can you take a vial to Varrock for me?");
				npcsay(player, n, "Go on then");
				int menu = multi(player, n, false, //do not send over
					"You give him the vial of ethenea",
					"You give him the vial of liquid honey",
					"You give him the vial of sulphuric broline");
				if (menu == 0) {
					if (player.getCarriedItems().hasCatalogID(ItemId.ETHENEA.id(), Optional.of(false))) {
						if (!player.getCache().hasKey("vial_vinci")) {
							player.getCache().store("vial_vinci", true);
							player.getCarriedItems().remove(new Item(ItemId.ETHENEA.id()));
							mes("You give him the vial of ethenea");
							delay(3);
							npcsay(player, n, "OK. We're meeting at the dancing donkey in Varrock right?");
							say(player, n, "That's right.");
						}
					} else {
						player.message("You can't give him what you don't have");
					}
				} else if (menu == 1) {
					if (player.getCarriedItems().hasCatalogID(ItemId.LIQUID_HONEY.id(), Optional.of(false))) {
						if (!player.getCache().hasKey("wrong_vial_vinci")) {
							player.getCache().store("wrong_vial_vinci", true);
							player.getCarriedItems().remove(new Item(ItemId.LIQUID_HONEY.id()));
							mes("You give him the vial of liquid honey");
							delay(3);
							npcsay(player, n, "OK. We're meeting at the dancing donkey in Varrock right?");
							say(player, n, "That's right.");
						}
					} else {
						player.message("You can't give him what you don't have");
					}
				} else if (menu == 2) {
					if (player.getCarriedItems().hasCatalogID(ItemId.SULPHURIC_BROLINE.id(), Optional.of(false))) {
						if (!player.getCache().hasKey("wrong_vial_vinci")) {
							player.getCache().store("wrong_vial_vinci", true);
							player.getCarriedItems().remove(new Item(ItemId.SULPHURIC_BROLINE.id()));
							mes("You give him the vial of sulphuric broline");
							delay(3);
							npcsay(player, n, "OK. We're meeting at the dancing donkey in Varrock right?");
							say(player, n, "That's right.");
						}
					} else {
						player.message("You can't give him what you don't have");
					}
				}
			} else {
				player.message("Devinci does not feel sufficiently moved to talk");
			}
		}
		else if (n.getID() == NpcId.KING_LATHAS.id()) {
			/** START UNDERGROUND PASS QUEST!!! **/
			if (player.getQuestStage(this) == -1) {
				switch (player.getQuestStage(Quests.UNDERGROUND_PASS)) {
					case 0:
					case 1:
					case 2:
						say(player, n, "hello king lathas");
						npcsay(player, n, "adventurer, thank saradomin for your arrival");
						say(player, n, "have your scouts found a way though the mountains");
						npcsay(player, n, "Not quite, we found a path to where we expected..",
							"..to find the 'well of voyage' an ancient portal to west runescape",
							"however over the past era's a cluster of cultists",
							"have settled there, run by a madman named iban");
						say(player, n, "iban?");
						npcsay(player, n, "a crazy loon who claims to be the son of zamorok",
							"go meet my main tracker koftik, he will help you",
							"he waits for you at the west side of west ardounge",
							"we must find a way through these caverns..",
							"if we are to stop my brother tyras");
						say(player, n, "i'll do my best lathas");
						npcsay(player, n, "a warning traveller the ungerground pass..",
							"is lethal, we lost many men exploring those caverns",
							"go preparred with food and armour or you won't last long");
						if (player.getQuestStage(Quests.UNDERGROUND_PASS) == 0) {
							player.updateQuestStage(Quests.UNDERGROUND_PASS, 1);
						}
						break;
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
						say(player, n, "hello king lanthas");
						npcsay(player, n, "traveller, how are you managing down there?");
						say(player, n, "it's a pretty nasty place but i'm ok");
						npcsay(player, n, "well keep up the good work");
						break;
					case 8:
						npcsay(player, n, "the traveller returns..any news?");
						say(player, n, "indeed, the quest is complete lathas",
							"i have defeated iban and his undead minions");
						npcsay(player, n, "incrediable, you are a truly awesome warrior",
							"now we can begin to restore the well of voyage",
							"once our mages have re-summoned the well",
							"i will send a band of troops led by yourself",
							"to head into west runescape and stop tryas");
						say(player, n, "i will be ready and waiting");
						npcsay(player, n, "your loyalty is appreiciated traveller");
						player.sendQuestComplete(Quests.UNDERGROUND_PASS);
						break;
					case -1:
						say(player, n, "hello king lathas");
						npcsay(player, n, "well hello there traveller",
							"the mages are still ressurecting the well of voyage",
							"but i'll have word sent to you as soon as its ready");
						say(player, n, "ok then, take care");
						npcsay(player, n, "you too");
						break;
				}
				return;
			} else if (player.getQuestStage(this) == 9) {
				say(player, n, "I assume that you are the King of east Ardougne?");
				npcsay(player, n, "You assume correctly- but where do you get such impertinence?");
				say(player, n, "I get it from finding out that the plague is a hoax");
				npcsay(player, n, "A hoax, I've never heard such a ridiculous thing...");
				say(player, n, "I have evidence- from Guidor in Varrock");
				npcsay(player, n, "Ah... I see. Well then you are right about the plague",
					"But I did it for the good of my people");
				say(player, n, "When is it ever good to lie to people like that?");
				npcsay(player, n, "When it protects them from a far greater danger- a fear too big to fathom");
				int menu = multi(player, n,
					"I don't understand...",
					"Well I've wasted enough of my time here");
				if (menu == 0) {
					npcsay(player, n, "Their King, tyras, journeyed out to the West, on a voyage of discovery",
						"But he was captured by the Dark Lord",
						"The Dark Lord agreed to spare his life, but only on one condition...",
						"That he would drink from the chalice of eternity");
					say(player, n, "So what happened?");
					npcsay(player, n, "The chalice corrupted him. He joined forces with the Dark Lord...",
						"...The embodiment of pure evil, banished all those years ago...",
						"And so I erected this wall, not just to protect my people",
						"But to protect all the people of Runescape",
						"Because now, with the King of West Ardougne...",
						"...The dark lord has an ally on the inside",
						"So I'm sorry that I lied about the plague",
						"I just hope that you can understand my reasons");
					say(player, n, "Well at least I know now. But what can we do about it?");
					npcsay(player, n, "Nothing at the moment",
						"I'm waiting for my scouts to come back",
						"They will tell us how we can get through the mountains",
						"When this happens, can I count on your support?");
					say(player, n, "Absolutely");
					npcsay(player, n, "Thank the gods. Let me give you this amulet",
						"Think of it as a thank you, for all that you have done",
						"...but know that one day it may turn red",
						"...Be ready for this moment",
						"And to help, I give you permission to use my training area",
						"It's located just to the north west of ardounge",
						"There you can prepare for the challenge ahead");
					say(player, n, "OK. There's just one thing I don't understand");
					say(player, n, "How do you know so much about King Tyras");
					npcsay(player, n, "How could I not do?",
						"He was my brother");
					player.message("king lathas gives you a magic amulet");
					give(player, ItemId.KING_LATHAS_AMULET.id(), 1);
					player.sendQuestComplete(Quests.BIOHAZARD);
				} else if (menu == 1) {
					npcsay(player, n, "No time is ever wasted- thanks for all you've done");
				}
				return;
			}
			player.message("the king is too busy to talk");
		}
		else if (n.getID() == NpcId.GUIDORS_WIFE.id()) {
			if (player.getQuestStage(this) == 9 || player.getQuestStage(this) == -1) {
				say(player, n, "hello");
				npcsay(player, n, "oh hello, i can't chat now",
					"i have to keep an eye on my husband",
					"he's very ill");
				say(player, n, "i'm sorry to hear that");
				return;
			}
			if (player.getQuestStage(this) == 8) {
				say(player, n, "hello again");
				npcsay(player, n, "hello there",
					"i fear guidor may not be long for this world");
				return;
			}
			if (player.getQuestStage(this) == 7) {
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.PRIEST_ROBE.id()) && player.getCarriedItems().getEquipment().hasEquipped(ItemId.PRIEST_GOWN.id())) {
					npcsay(player, n, "Father, thank heavens you're here. My husband is very ill",
						"Perhaps you could go and perform his final ceremony");
					say(player, n, "I'll see what I can do");
				} else {
					say(player, n, "Hello, I'm a friend of Elena, here to see Guidor");
					npcsay(player, n, "I'm afraid...(she sobs)... that Guidor is not long for this world",
						"So I'm not letting people see him now");
					say(player, n, "I'm really sorry to hear about Guidor...",
						"but I do have some very important business to attend to");
					npcsay(player, n, "You heartless rogue. What could be more important than Guidor's life?",
						"...A life spent well, if not always wisely...",
						"I just hope that Saradomin shows mercy on his soul");
					say(player, n, "Guidor is a religious man?");
					npcsay(player, n, "Oh god no. But I am",
						"if only i could get him to see a priest");
				}
			}
		}
		else if (n.getID() == NpcId.GUIDOR.id()) {
			if (player.getQuestStage(this) == 8 || player.getQuestStage(this) == 9 || player.getQuestStage(this) == -1) {
				say(player, n, "hello again guidor");
				npcsay(player, n, "well hello traveller",
					"i still can't understand why they would lie about the plague");
				say(player, n, "it's strange, anyway how are you doing?");
				npcsay(player, n, "i'm hanging in there");
				say(player, n, "good for you");
				return;
			}
			say(player, n, "Hello,you must be Guidor. I understand that you are unwell");
			npcsay(player, n, "Is my wife asking priests to visit me now?",
				"I'm a man of science, for god's sake!",
				"Ever since she heard rumours of a plague carrier travelling from Ardougne",
				"she's kept me under house arrest",
				"Of course she means well, and I am quite frail now...",
				"So what brings you here?");
			int menu = multi(player, n, false, //do not send over
				"I've come to ask your assistance in stopping a plague that could kill thousands",
				"Oh,nothing,I was just going to bless your room and I've done that now  Goodbye");
			if (menu == 0) {
				say(player, n, "Well it's funny you should ask actually...",
					"I've come to ask your assistance in stopping a plague that could kill thousands");
				npcsay(player, n, "So you're the plague carrier!");
				int menu2 = multi(player, n,
					"No! Well, yes... but not exactly. It's contained in a sealed unit from elena",
					"I've been sent by your old pupil Elena, she's trying to halt the virus");
				//both lead to the EXACT same dialogue
				if (menu2 == 0 || menu2 == 1) {
					npcsay(player, n, "Elena eh?");
					say(player, n, "Yes. She wants you to analyse it",
						"You might be the only one that can help");
					npcsay(player, n, "Right then. Sounds like we'd better get to work!");
					if (player.getCarriedItems().hasCatalogID(ItemId.PLAGUE_SAMPLE.id(), Optional.of(false))) {
						say(player, n, "I have the plague sample");
						npcsay(player, n, "Now I'll be needing some liquid honey,some sulphuric broline,and then...");
						say(player, n, "...some ethenea?");
						npcsay(player, n, "Indeed!");
						if (player.getCarriedItems().hasCatalogID(ItemId.ETHENEA.id(), Optional.of(false))
							&& player.getCarriedItems().hasCatalogID(ItemId.SULPHURIC_BROLINE.id(), Optional.of(false))
							&& player.getCarriedItems().hasCatalogID(ItemId.LIQUID_HONEY.id(), Optional.of(false))) {
							if (player.getCarriedItems().hasCatalogID(ItemId.TOUCH_PAPER.id(), Optional.of(false))) {
								player.message("You give him the vials and the touch paper");
								player.getCarriedItems().remove(new Item(ItemId.TOUCH_PAPER.id()));
								player.getCarriedItems().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
								player.getCarriedItems().remove(new Item(ItemId.ETHENEA.id()));
								player.getCarriedItems().remove(new Item(ItemId.LIQUID_HONEY.id()));
								player.getCarriedItems().remove(new Item(ItemId.SULPHURIC_BROLINE.id()));
								npcsay(player, n, "Now I'll just apply these to the sample and...",
									"I don't get it...the touch paper has remained the same");
								player.updateQuestStage(this, 8);
								int menu3 = multi(player, n,
									"That's why Elena wanted you to do it- because she wasn't sure what was happening",
									"So what does that mean exactly?");
								if (menu3 == 0) {
									npcsay(player, n, "Well that's just it.Nothing has happened",
										"I don't know what this sample is, but it certainly isn't toxic");
									say(player, n, "So what about the plague?");
									npcsay(player, n, "Don't you understand, there is no plague!",
										"I'm very sorry, I can see that you've worked hard for this...",
										"...but it seems that someone has been lying to you",
										"The only question is...",
										"...why?");
								} else if (menu3 == 1) {
									say(player, n, "That's why Elena wanted you to do it- because she wasn't sure what was happening");
									npcsay(player, n, "Well that's just it. Nothing has happened",
										"I don't know what this sample is, but it certainly isn't toxic");
									say(player, n, "So what about the plague?");
									npcsay(player, n, "Don't you understand, there is no plague!",
										"I'm very sorry, I can see that you've worked hard for this...",
										"...but it seems that someone has been lying to you",
										"The only question is...",
										"...why?");
								}

							} else {
								npcsay(player, n, "Oh. You don't have any touch-paper",
									"And so I won't be able to help you after all");
							}
						} else {
							npcsay(player, n, "Look,I need all three reagents to test the plague sample",
								"Come back when you've got them");
						}
					} else {
						npcsay(player, n, "Seems like you don't actually HAVE the plague sample",
							"It's a long way to come empty-handed...",
							"And quite a long way back too");
						return;
					}
				}
			} else if (menu == 1) {
				say(player, n, "Oh, nothing, I was just going to bless your room, and I've done that now. Goodbye");
			}
		}
	}

	// All recreated/reconstructed
	private void freePlayerDialogue(Player player, Npc n) {
		if (n.getID() == NpcId.CHEMIST.id()) {
			npcsay(player, n, "It's very nice that you've come",
				"all the way down here",
				"but I really don't have time to talk at the moment",
				"Maybe if you come back later I'll be able to help");
		} else if (n.getID() == NpcId.DEVINCI.id() || n.getID() == NpcId.DEVINCI_BAR.id()) {
			npcsay(player, n, "Bah!",
				"A great artist such as myself should not have to",
				"suffer the HUMILIATION of spending time where the",
				"likes of you wander everywhere!");
		} else if (n.getID() == NpcId.HOPS.id() || n.getID() == NpcId.HOPS_BAR.id()) {
			npcsay(player, n, "Hops don't wanna talk now");
		} else if (n.getID() == NpcId.CHANCY.id() || n.getID() == NpcId.CHANCY_BAR.id()) {
			say(player, n, "Hello!",
				"Playing solitaire?");
			npcsay(player, n, "Hush",
				"I'm trying to perfect the art of",
				"dealing off the bottom of the deck",
				"Whatever you want",
				"come back later and I'll speak to you then");
		} else if (n.getID() == NpcId.GUIDORS_WIFE.id()) {
			npcsay(player, n, "Oh dear! Oh dear!",
				"I don't have time to chat!");
		}
	}

	private void ropeLadderBackFunction(final Player player) {
		GameObject ropeLadder = new GameObject(player.getWorld(), Point.location(622, 611), VISUAL_ROPELADDER, 0, 0);
		player.getWorld().registerGameObject(ropeLadder);
		mes("you climb up the rope ladder");
		delay(3);
		player.teleport(622, 611);
		mes("and drop down on the other side");
		delay(3);
		player.getWorld().unregisterGameObject(ropeLadder);
	}

	private void ropeLadderInFunction(final Player player) {
		GameObject ropeLadder = new GameObject(player.getWorld(), Point.location(622, 611), VISUAL_ROPELADDER, 0, 0);
		player.getWorld().registerGameObject(ropeLadder);
		mes("you climb up the rope ladder");
		delay(3);
		player.teleport(624, 606);
		mes("and drop down on the other side");
		delay(3);
		player.getWorld().unregisterGameObject(ropeLadder);
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == ELENAS_DOOR;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == ELENAS_DOOR) {
			if (player.getQuestStage(Quests.PLAGUE_CITY) == -1) {
				doDoor(obj, player);
				player.message("You go through the door");
			} else {
				player.message("the door is locked");
			}
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return DataConversions.inArray(new int[] {JERICOS_CUPBOARD_ONE_OPEN, JERICOS_CUPBOARD_ONE_CLOSED, JERICOS_CUPBOARD_TWO_OPEN, JERICOS_CUPBOARD_TWO_CLOSED,
				WATCH_TOWER, NURSE_SARAHS_CUPBOARD_OPEN, NURSE_SARAHS_CUPBOARD_CLOSED, GET_INTO_CRATES_GATE, DISTILLATOR_CRATE, OTHER_CRATE}, obj.getID());
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == JERICOS_CUPBOARD_ONE_OPEN || obj.getID() == JERICOS_CUPBOARD_ONE_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, player, JERICOS_CUPBOARD_ONE_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, player, JERICOS_CUPBOARD_ONE_CLOSED);
			} else {
				player.message("You search the cupboard, but find nothing");
			}
		}
		else if (obj.getID() == JERICOS_CUPBOARD_TWO_OPEN || obj.getID() == JERICOS_CUPBOARD_TWO_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, player, JERICOS_CUPBOARD_TWO_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, player, JERICOS_CUPBOARD_TWO_CLOSED);
			} else {
				player.message("you search the cupboard");
				if (!player.getCarriedItems().hasCatalogID(ItemId.BIRD_FEED.id(), Optional.empty())) {
					player.message("and find some pigeon feed");
					give(player, ItemId.BIRD_FEED.id(), 1);
				} else {
					player.message("but find nothing of interest");
				}
			}
		}
		else if (obj.getID() == NURSE_SARAHS_CUPBOARD_OPEN || obj.getID() == NURSE_SARAHS_CUPBOARD_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, player, NURSE_SARAHS_CUPBOARD_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, player, NURSE_SARAHS_CUPBOARD_CLOSED);
			} else {
				player.message("you search the cupboard");
				if ((!player.getCarriedItems().hasCatalogID(ItemId.DOCTORS_GOWN.id(), Optional.empty()))
					&& ((player.getQuestStage(this) == 4 || player.getQuestStage(this) == 5)
					|| config().CAN_RETRIEVE_POST_QUEST_ITEMS)) {
					player.message("inside you find a doctor's gown");
					give(player, ItemId.DOCTORS_GOWN.id(), 1);
				} else {
					player.message("but find nothing of interest");
				}
			}
		}
		else if (obj.getID() == WATCH_TOWER) {
			if (command.equalsIgnoreCase("approach")) {
				Npc mournerGuard = ifnearvisnpc(player, NpcId.MOURNER_BYENTRANCE2.id(), 15);
				if (mournerGuard != null) {
					npcsay(player, mournerGuard, "keep away civilian");
					say(player, mournerGuard, "what's it to you?");
					npcsay(player, mournerGuard, "the tower's here for your protection");
				}
			}
		}
		else if (obj.getID() == GET_INTO_CRATES_GATE) {
			if (player.getX() <= 630) {
				doGate(player, obj);
				player.message("you open the gate and pass through");
			} else {
				mes("the gate is locked");
				delay(3);
				player.message("you need a key");
			}
		}
		else if (obj.getID() == DISTILLATOR_CRATE || obj.getID() == OTHER_CRATE) {
			if (obj.getID() == DISTILLATOR_CRATE) {
				mes("you search the crate");
				delay(3);
				if (!player.getCarriedItems().hasCatalogID(ItemId.DISTILLATOR.id(), Optional.empty())) {
					mes("and find elena's distillator");
					delay(3);
					give(player, ItemId.DISTILLATOR.id(), 1);
					if (player.getCache().hasKey("rotten_apples")) {
						player.getCache().remove("rotten_apples");
						player.updateQuestStage(this, 5);
					}
				} else {
					mes("it's empty");
					delay(3);
				}
			} else {
				player.message("The crate is empty");
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (item.getCatalogId() == ItemId.BIRD_FEED.id() && obj.getID() == WATCH_TOWER)
				|| (item.getCatalogId() == ItemId.ROTTEN_APPLES.id() && obj.getID() == COOKING_POT)
				|| (item.getCatalogId() == ItemId.BIOHAZARD_BRONZE_KEY.id() && obj.getID() == GET_INTO_CRATES_GATE);
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (item.getCatalogId() == ItemId.BIRD_FEED.id() && obj.getID() == WATCH_TOWER) {
			if (player.getQuestStage(this) == 2) {
				mes("you throw a hand full of seeds onto the watch tower");
				delay(3);
				mes("the mourners do not seem to notice");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.BIRD_FEED.id()));
				if (!player.getCache().hasKey("bird_feed")) {
					player.getCache().store("bird_feed", true);
				}
			} else {
				player.message("nothing interesting happens");
			}
		}
		else if (item.getCatalogId() == ItemId.ROTTEN_APPLES.id() && obj.getID() == COOKING_POT) {
			if (player.getQuestStage(this) == 4 || player.getQuestStage(this) == 5) {
				mes("you place the rotten apples in the pot");
				delay(3);
				mes("they quickly dissolve into the stew");
				delay(3);
				mes("that wasn't very nice");
				delay(3);
				if (!player.getCache().hasKey("rotten_apples")) {
					player.getCache().store("rotten_apples", true);
				}
				player.getCarriedItems().remove(new Item(ItemId.ROTTEN_APPLES.id()));
				return;
			}
			mes("you place the rotten apples in the pot");
			delay(3);
			mes("that wasn't very nice");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.ROTTEN_APPLES.id()));
		}
		else if (item.getCatalogId() == ItemId.BIOHAZARD_BRONZE_KEY.id() && obj.getID() == GET_INTO_CRATES_GATE) {
			mes("the key fits the gate");
			delay(3);
			player.message("you open it and pass through");
			doGate(player, obj);
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.MOURNER_ILL.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.MOURNER_ILL.id()) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.BIOHAZARD_BRONZE_KEY.id(), Optional.empty())) {
				mes("you search the mourner");
				delay(3);
				give(player, ItemId.BIOHAZARD_BRONZE_KEY.id(), 1);
				if (n != null) {
					n.getBehavior().retreat();
					mes("and find a key");
					delay();
					n.remove();
				}
			}
		}
	}
}
