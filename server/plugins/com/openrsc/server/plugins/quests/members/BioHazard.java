package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
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
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("@gre@You haved gained 3 quest points!");
		incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.BIOHAZARD), true);
		p.message("you have completed the biohazard quest");
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.ELENA_HOUSE.id(), NpcId.OMART.id(), NpcId.JERICO.id(), NpcId.KILRON.id(), NpcId.NURSE_SARAH.id(),
				NpcId.CHEMIST.id(), NpcId.CHANCY.id(), NpcId.HOPS.id(), NpcId.DEVINCI.id(), NpcId.KING_LATHAS.id(), NpcId.CHANCY_BAR.id(),
				NpcId.HOPS_BAR.id(), NpcId.DEVINCI_BAR.id(), NpcId.GUIDORS_WIFE.id(), NpcId.GUIDOR.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.ELENA_HOUSE.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					say(p, n, "good to see you, elena");
					npcsay(p, n, "you too, thanks for freeing me",
						"it's just a shame the mourners confiscated my equipment");
					say(p, n, "what did they take?");
					npcsay(p, n, "my distillator, I can't test any plague samples without it",
						"they're holding it in the mourner quarters in west ardounge",
						"i must somehow retrieve that distillator",
						"if i am to find a cure for this awful affliction");
					int menu = multi(p, n,
						"i'll try to retrieve it for you",
						"well, good luck");
					if (menu == 0) {
						/***************************/
						/** START BIOHAZARD QUEST! **/
						/***************************/
						npcsay(p, n, "i was hoping you would say that",
							"unfortunately they discovered the tunnel and filled it in",
							"we need another way over the wall");
						say(p, n, "any ideas?");
						npcsay(p, n, "my father's friend jerico is in communication with west ardounge",
							"he might be able to help",
							"he lives next to the chapel");
						p.updateQuestStage(this, 1);
					} else if (menu == 1) {
						npcsay(p, n, "thanks traveller");
					}
					break;
				case 1:
					say(p, n, "hello elena");
					npcsay(p, n, "hello brave adventurer",
						"any luck finding the distillator");
					say(p, n, "no i'm afraid not");
					npcsay(p, n, "speak to jerico, he will help you to cross the wall",
						"he lives next to the chapel");
					break;
				case 2:
					say(p, n, "hello elena, i've spoken to jerico");
					npcsay(p, n, "was he able to help?");
					say(p, n, "he has two friends who will help me cross the wall",
						"but first i need to distract the watch tower");
					npcsay(p, n, "hmmm, could be tricky");
					break;
				case 3:
					say(p, n, "elena i've distracted the guards at the watch tower");
					npcsay(p, n, "yes, i saw",
						"quickly meet with jerico's friends and cross the wall",
						"before the pigeons fly off");
					break;
				case 4:
					say(p, n, "hello again");
					npcsay(p, n, "you're back, did you find the distillator?");
					say(p, n, "i'm afraid not");
					npcsay(p, n, "i can't test the samples without the distillator",
						"please don't give up until you find it");
					break;
				case 5:
					npcsay(p, n, "so, have you managed to retrieve my distillator?");
					if (p.getCarriedItems().hasCatalogID(ItemId.DISTILLATOR.id(), Optional.of(false))) {
						npcsay(p, n, "You have - that's great!",
							"Now can you pass me those refraction agents please?");
						Functions.mes(p, "You hand Elena the distillator and an assortment of vials");
						p.getCarriedItems().remove(new Item(ItemId.DISTILLATOR.id()));
						say(p, n, "These look pretty fancy");
						npcsay(p, n, "Well, yes and no. The liquid honey isn't worth so much",
							"But the others are- especially this colourless ethenea",
							"And be careful with the sulphuric broline- it's highly poisonous");
						say(p, n, "You're not kidding- I can smell it from here");
						Functions.mes(p, "Elena puts the agents through the distillator");
						npcsay(p, n, "I don't understand...the touch paper hasn't changed colour at all",
							"You'll need to go and see my old mentor Guidor. He lives in Varrock",
							"Take these vials and this sample to him");
						Functions.mes(p, "elena gives you three vials and a sample in a tin container");
						give(p, ItemId.LIQUID_HONEY.id(), 1);
						give(p, ItemId.ETHENEA.id(), 1);
						give(p, ItemId.SULPHURIC_BROLINE.id(), 1);
						give(p, ItemId.PLAGUE_SAMPLE.id(), 1);
						npcsay(p, n, "But first you'll need some more touch-paper. Go and see the chemist in Rimmington",
							"Just don't get into any fights, and be careful who you speak to",
							"Those vials are fragile, and plague carriers don't tend to be too popular");
						p.updateQuestStage(this, 6);
					} else {
						say(p, n, "i'm afraid not");
						npcsay(p, n, "Oh, you haven't",
							"People may be dying even as we speak");
					}
					break;
				case 6:
				case 7:
					npcsay(p, n, "what are you doing back here");
					int menu6 = multi(p, n, false, //do not send over
						"I just find it hard to say goodbye sometimes",
						"I'm afraid I've lost some of the stuff that you gave me...",
						"i've forgotten what i need to do");
					if (menu6 == 0) {
						say(p, n, "I just find it hard to say goodbye sometimes");
						npcsay(p, n, "Yes...I have feelings for you too...",
							"Now get to work!");
					} else if (menu6 == 1) {
						say(p, n, "I'm afraid I've you lost some of the stuff that you gave me");
						npcsay(p, n, "That's alright, I've got plenty");
						Functions.mes(p, "Elena replaces your items");
						p.getCarriedItems().getInventory().replace(ItemId.LIQUID_HONEY.id(), ItemId.LIQUID_HONEY.id());
						p.getCarriedItems().getInventory().replace(ItemId.ETHENEA.id(), ItemId.ETHENEA.id());
						p.getCarriedItems().getInventory().replace(ItemId.SULPHURIC_BROLINE.id(), ItemId.SULPHURIC_BROLINE.id());
						p.getCarriedItems().getInventory().replace(ItemId.PLAGUE_SAMPLE.id(), ItemId.PLAGUE_SAMPLE.id());
						npcsay(p, n, "OK so that's the colourless ethenea...",
							"Some highly toxic sulphuric broline...",
							"And some bog-standard liquid honey...");
						say(p, n, "Great. I'll be on my way");
					} else if (menu6 == 2) {
						say(p, n, "i've forgotten what i need to do");
						npcsay(p, n, "go to rimmington and get some touch paper from the chemist",
							"use his errand boys to smuggle the vials into varrock",
							"then go to varrock and take the sample to guidor, my old mentor");
						say(p, n, "ok, i'll get to it");
					}
					break;
				case 8:
					npcsay(p, n, "You're back! So what did Guidor say?");
					say(p, n, "Nothing");
					npcsay(p, n, "What?");
					say(p, n, "He said that there is no plague");
					npcsay(p, n, "So what, this thing has all been a big hoax?");
					say(p, n, "Or maybe we're about to uncover something huge");
					npcsay(p, n, "Then I think this thing may be bigger than both of us");
					say(p, n, "What do you mean?");
					npcsay(p, n, "I mean that you need to go right to the top",
						"You need to see the King of east Ardougne");
					p.updateQuestStage(this, 9);
					break;
				case 9:
					say(p, n, "hello elena");
					npcsay(p, n, "you must go to king lathas immediately");
					break;
				case -1:
					say(p, n, "hello elena");
					npcsay(p, n, "hey, how are you?");
					say(p, n, "good thanks, yourself?");
					npcsay(p, n, "not bad, let me know when you hear from king lathas again");
					say(p, n, "will do");
					break;
			}
		}
		else if (n.getID() == NpcId.OMART.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
				case 1:
					say(p, n, "hello there");
					npcsay(p, n, "hello");
					say(p, n, "how are you?");
					npcsay(p, n, "fine thanks");
					break;
				case 2:
					say(p, n, "omart, jerico said you might be able to help me");
					npcsay(p, n, "he informed me of your problem traveller",
						"i would be glad to help, i have a rope ladder",
						"and my associate, kilron, is waiting on the other side");
					say(p, n, "good stuff");
					npcsay(p, n, "unfortunately we can't risk it with the watch tower so close",
						"so first we need to distract the guards in the tower");
					say(p, n, "how?");
					npcsay(p, n, "try asking jerico, if he's not too busy with his pigeons",
						"I'll be waiting here for you");
					break;
				case 3:
					npcsay(p, n, "well done, the guards are having real trouble with those birds",
						"you must go now traveller, it's your only chance");
					Functions.mes(p, "Omart calls to his associate");
					npcsay(p, n, "Kilron!");
					Functions.mes(p, "he throws one end of the rope ladder over the wall");
					npcsay(p, n, "go now traveller");
					int menu = multi(p, n,
						"ok lets do it",
						"I'll be back soon");
					if (menu == 0) {
						ropeLadderInFunction(p);
						p.updateQuestStage(this, 4);
					} else if (menu == 1) {
						npcsay(p, n, "don't take long",
							"the mourners will soon be rid of those birds");
					}
					break;
				case 4:
				case 5:
					say(p, n, "hello omart");
					npcsay(p, n, "hello traveller",
						"the guards are still distracted if you wish to cross the wall");
					int OverAgain = multi(p, n, false, //do not send over
						"ok lets do it",
						"i'll be back soon");
					if (OverAgain == 0) {
						say(p, n, "ok lets do it");
						ropeLadderInFunction(p);
					} else if (OverAgain == 1) {
						say(p, n, "I'll be back soon");
						npcsay(p, n, "don't take long",
							"the mourners will soon be rid of those birds");
					}
					break;
				case 6:
				case 7:
				case 8:
				case 9:
				case -1:
					say(p, n, "hello omart");
					npcsay(p, n, "hello adventurer",
						"i'm afraid it's too risky to use the ladder again",
						"but I believe that edmond's working on another tunnel");
					break;
			}
		}
		else if (n.getID() == NpcId.JERICO.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					say(p, n, "hello");
					npcsay(p, n, "can i help you?");
					say(p, n, "just passing by");
					break;
				case 1:
					say(p, n, "hello jerico");
					npcsay(p, n, "hello, i've been expecting you",
						"elena tells me you need to cross the wall");
					say(p, n, "that's right");
					npcsay(p, n, "my messenger pigeons help me communicate with friends over the wall",
						"i have arranged for two friends to aid you with a rope ladder",
						"omart is waiting for you at the southend of the wall",
						"be careful, if the mourners catch you the punishment will be severe");
					say(p, n, "thanks jerico");
					p.updateQuestStage(this, 2);
					break;
				case 2:
					say(p, n, "hello jerico");
					npcsay(p, n, "hello again",
						"you'll need someway to distract the watch tower",
						"otherwise you'll be caught for sure");
					say(p, n, "any ideas?");
					npcsay(p, n, "sorry, try asking omart",
						"i really must get back to feeding the messenger birds");
					break;
				case 3:
					say(p, n, "hello there");
					npcsay(p, n, "the guards are distracted by the birds",
						"you must go now",
						"quickly traveller");
					break;
				case 4:
					say(p, n, "hello again jerico");
					npcsay(p, n, "so you've returned traveller",
						"did you get what you wanted");
					say(p, n, "not yet");
					npcsay(p, n, "omart will be waiting by the wall",
						"In case you need to cross again");
					break;
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case -1:
					p.message("jerico is busy looking for his bird feed");
					break;
			}
		}
		else if (n.getID() == NpcId.KILRON.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
				case 3:
					say(p, n, "hello there");
					npcsay(p, n, "hello");
					say(p, n, "how are you?");
					npcsay(p, n, "busy");
					break;
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case -1:
					say(p, n, "hello kilron");
					npcsay(p, n, "hello traveller",
						"do you need to go back over?");
					int menu = multi(p, n,
						"not yet kilron",
						"yes i do");
					if (menu == 0) {
						npcsay(p, n, "okay, just give me the word");
					} else if (menu == 1) {
						npcsay(p, n, "okay, quickly now");
						ropeLadderBackFunction(p);
					}
					break;
			}
		}
		else if (n.getID() == NpcId.NURSE_SARAH.id()) {
			switch (p.getQuestStage(this)) {
				case 4:
				case 5:
					if (p.getCache().hasKey("rotten_apples")) {
						say(p, n, "hello nurse");
						npcsay(p, n, "oh hello there");
						npcsay(p, n, "im afraid i can't stop and talk",
							"a group of mourners have became ill with food poisoning",
							"i need to go over and see what i can do");
						say(p, n, "hmmm, strange that!");
					} else {
						say(p, n, "hello nurse");
						npcsay(p, n, "i don't know how much longer i can cope here");
						say(p, n, "what? is the plague getting to you?");
						npcsay(p, n, "no, strangely enough the people here don't seem to be affected",
							"it's just the awful living conditions that are making people ill");
						say(p, n, "i was under the impression that every one here was affected");
						npcsay(p, n, "me too, but it doesn't seem to be the case");
					}
					break;
				default:
					p.message("nurse sarah doesn't feel like talking");
					break;
			}
		}
		else if (n.getID() == NpcId.HOPS_BAR.id()) {
			if (p.getQuestStage(this) == 7) {
				if (p.getCache().hasKey("wrong_vial_hops")) {
					say(p, n, "Hello. How was your journey?");
					npcsay(p, n, "Pretty thirst-inducing actually...");
					say(p, n, "Please tell me that you haven't drunk the contents");
					npcsay(p, n, "Of course I can tell you that I haven't drunk the contents",
						"But I'd be lying",
						"Sorry about that me old mucker- can I get you a drink?");
					say(p, n, "No, I think you've done enough for now");
					p.getCache().remove("wrong_vial_hops");
				} else if (p.getCache().hasKey("vial_hops")) {
					say(p, n, "Hello. How was your journey?");
					npcsay(p, n, "Pretty thirst-inducing actually...");
					say(p, n, "Please tell me that you haven't drunk the contents");
					npcsay(p, n, "Oh the gods no! What do you take me for?",
						"Besides, the smell kind of put me off ",
						"Here's your vial anyway");
					p.message("He gives you the vial of sulphuric broline");
					give(p, ItemId.SULPHURIC_BROLINE.id(), 1);
					say(p, n, "Thanks. I'll leave you to your drink now");
					p.getCache().remove("vial_hops");
				}
			} else {
				p.message("Hops doesn't feel like talking");
			}
		}
		else if (n.getID() == NpcId.DEVINCI_BAR.id()) {
			if (p.getQuestStage(this) == 7) {
				if (p.getCache().hasKey("wrong_vial_vinci")) {
					npcsay(p, n, "Hello again",
						"I hope your journey was as pleasant as mine");
					say(p, n, "Yep. Anyway, I'll take the package off you now");
					npcsay(p, n, "Package? That's a funny way to describe a liquid of such exquisite beauty");
					int menu = multi(p, n,
						"I'm getting a bad feeling about this",
						"Just give me the stuff now please");
					if (menu == 0) {
						say(p, n, "You do still have it don't you?");
						npcsay(p, n, "Absolutely",
							"Its' just not stored in a vial anymore");
						say(p, n, "What?");
						npcsay(p, n, "Instead it has been liberated",
							"And it now gleams from the canvas of my latest epic:",
							"The Majesty of Varrock");
						say(p, n, "That's great",
							"Thanks to you I'll have to walk back to East Ardougne to get another vial");
						npcsay(p, n, "Well you can't put a price on art");
						p.getCache().remove("wrong_vial_vinci");
					} else if (menu == 1) {
						say(p, n, "You do still have it don't you?");
						npcsay(p, n, "Absolutely",
							"Its' just not stored in a vial anymore");
						say(p, n, "What?");
						npcsay(p, n, "Instead it has been liberated",
							"And it now gleams from the canvas of my latest epic:",
							"The Majesty of Varrock");
						say(p, n, "That's great",
							"Thanks to you I'll have to walk back to East Ardougne to get another vial");
						npcsay(p, n, "Well you can't put a price on art");
						p.getCache().remove("wrong_vial_vinci");
					}
				} else if (p.getCache().hasKey("vial_vinci")) {
					npcsay(p, n, "Hello again",
						"I hope your journey was as pleasant as mine");
					say(p, n, "Well, it's always sunny in Runescape, as they say");
					npcsay(p, n, "OK. Here it is");
					Functions.mes(p, "He gives you the vial of ethenea");
					give(p, ItemId.ETHENEA.id(), 1);
					say(p, n, "Thanks. You've been a big help");
					p.getCache().remove("vial_vinci");
				}
			} else {
				p.message("devinci doesn't feel like talking");

			}
		}
		else if (n.getID() == NpcId.CHANCY_BAR.id()) {
			if (p.getQuestStage(this) == 7) {
				if (p.getCache().hasKey("wrong_vial_chancy")) {
					say(p, n, "Hi.Thanks for doing that");
					npcsay(p, n, "No problem. I've got some money for you actually");
					say(p, n, "What do you mean?");
					npcsay(p, n, "Well it turns out that that potion you gave me was quite valuable...");
					say(p, n, "What?");
					npcsay(p, n, "And I know that I probably shouldn't have sold it...",
						"But some friends and I were having a little wager- the odds were just too good");
					say(p, n, "You sold my vial and gambled with the money?");
					npcsay(p, n, "Actually, yes... but praise be to Saradomin, because I won!",
						"So all's well that ends well right?");
					int menu = multi(p, n,
						"No. Nothing could be further from the truth",
						"You have no idea of what you have just done");
					if (menu == 0) {
						npcsay(p, n, "Well there's no pleasing some people");
						p.getCache().remove("wrong_vial_chancy");
					} else if (menu == 1) {
						npcsay(p, n, "Ignorance is bliss I'm afraid");
						p.getCache().remove("wrong_vial_chancy");
					}
				} else if (p.getCache().hasKey("vial_chancy")) {
					say(p, n, "Hi.Thanks for doing that");
					npcsay(p, n, "No problem");
					p.message("He gives you the vial of liquid honey");
					give(p, ItemId.LIQUID_HONEY.id(), 1);
					npcsay(p, n, "Next time give me something more valuable",
						"I couldn't get anything for this on the blackmarket");
					say(p, n, "That was the idea");
					p.getCache().remove("vial_chancy");
				}
			} else {
				p.message("chancy doesn't feel like talking");

			}
		}
		else if (n.getID() == NpcId.HOPS.id()) {
			if (p.getQuestStage(this) == 7) {
				if (p.getCache().hasKey("vial_hops") || p.getCache().hasKey("wrong_vial_hops")) {
					npcsay(p, n, "I suppose I'd better get going",
						"I'll meet you at the The dancing donkey inn");
					return;
				}
				say(p, n, "Hi,I've got something for you to take to Varrock");
				npcsay(p, n, "Sounds like pretty thirsty work");
				say(p, n, "Well, there's a pub in Varrock if you're desperate");
				npcsay(p, n, "Don't worry, I'm a pretty resourceful fellow you know");
				int menu = multi(p, n, false, //do not send over
					"You give him the vial of ethenea",
					"You give him the vial of liquid honey",
					"You give him the vial of sulphuric broline");
				if (menu == 0) {
					if (p.getCarriedItems().hasCatalogID(ItemId.ETHENEA.id(), Optional.of(false))) {
						if (!p.getCache().hasKey("wrong_vial_hops")) {
							p.getCache().store("wrong_vial_hops", true);
							p.getCarriedItems().remove(new Item(ItemId.ETHENEA.id()));
							p.message("You give him the vial of ethenea");
							say(p, n, "OK. I'll see you in Varrock");
							npcsay(p, n, "Sure. I'm a regular at the The dancing donkey inn as it happens");
						}
					} else {
						p.message("You have no ethenea to give");
					}
				} else if (menu == 1) {
					if (p.getCarriedItems().hasCatalogID(ItemId.LIQUID_HONEY.id(), Optional.of(false))) {
						if (!p.getCache().hasKey("wrong_vial_hops")) {
							p.getCache().store("wrong_vial_hops", true);
							p.getCarriedItems().remove(new Item(ItemId.LIQUID_HONEY.id()));
							p.message("You give him the vial of liquid honey");
							say(p, n, "OK. I'll see you in Varrock");
							npcsay(p, n, "Sure. I'm a regular at the The dancing donkey inn as it happens");
						}
					} else {
						p.message("You have no liquid honey to give");
					}
				} else if (menu == 2) {
					if (p.getCarriedItems().hasCatalogID(ItemId.SULPHURIC_BROLINE.id(), Optional.of(false))) {
						if (!p.getCache().hasKey("vial_hops")) {
							p.getCache().store("vial_hops", true);
							p.getCarriedItems().remove(new Item(ItemId.SULPHURIC_BROLINE.id()));
							p.message("You give him the vial of sulphuric broline");
							say(p, n, "OK. I'll see you in Varrock");
							npcsay(p, n, "Sure. I'm a regular at the The dancing donkey inn as it happens");
						}
					} else {
						p.message("You have no sulphuric broline to give");
					}
				}
			} else {
				p.message("He is not in a fit state to talk");
			}
		}
		else if (n.getID() == NpcId.CHANCY.id()) {
			if (p.getQuestStage(this) == 7) {
				if (p.getCache().hasKey("vial_chancy") || p.getCache().hasKey("wrong_vial_chancy")) {
					npcsay(p, n, "look, I've got your vial, but I'm not taking two",
						"I always like to play the percentages");
					return;
				}
				say(p, n, "Hello, I've got a vial for you to take to Varrock");
				npcsay(p, n, "Tssch... that chemist asks a lot for the wages he pays");
				say(p, n, "Maybe you should ask him for more money");
				npcsay(p, n, "Nah...I just use my initiative here and there");
				int menu = multi(p, n, false, //do not send over
					"You give him the vial of ethenea",
					"You give him the vial of liquid honey",
					"You give him the vial of sulphuric broline");
				if (menu == 0) {
					if (p.getCarriedItems().hasCatalogID(ItemId.ETHENEA.id(), Optional.of(false))) {
						if (!p.getCache().hasKey("wrong_vial_chancy")) {
							p.getCache().store("wrong_vial_chancy", true);
							p.getCarriedItems().remove(new Item(ItemId.ETHENEA.id()));
							Functions.mes(p, "You give him the vial of ethenea");
							say(p, n, "Right.I'll see you later in the dancing donkey inn");
							npcsay(p, n, "Be lucky");
						}
					} else {
						p.message("You can't give him what you don't have");
					}
				} else if (menu == 1) {
					if (p.getCarriedItems().hasCatalogID(ItemId.LIQUID_HONEY.id(), Optional.of(false))) {
						if (!p.getCache().hasKey("vial_chancy")) {
							p.getCache().store("vial_chancy", true);
							p.getCarriedItems().remove(new Item(ItemId.LIQUID_HONEY.id()));
							Functions.mes(p, "You give him the vial of liquid honey");
							say(p, n, "Right.I'll see you later in the dancing donkey inn");
							npcsay(p, n, "Be lucky");
						}
					} else {
						p.message("You can't give him what you don't have");
					}
				} else if (menu == 2) {
					if (p.getCarriedItems().hasCatalogID(ItemId.SULPHURIC_BROLINE.id(), Optional.of(false))) {
						if (!p.getCache().hasKey("wrong_vial_chancy")) {
							p.getCache().store("wrong_vial_chancy", true);
							p.getCarriedItems().remove(new Item(ItemId.SULPHURIC_BROLINE.id()));
							Functions.mes(p, "You give him the vial of sulphuric broline");
							say(p, n, "Right.I'll see you later in the dancing donkey inn");
							npcsay(p, n, "Be lucky");
						}
					} else {
						p.message("You can't give him what you don't have");
					}
				}
			} else {
				p.message("Chancy doesn't feel like talking");
			}
		}
		else if (n.getID() == NpcId.CHEMIST.id()) {
			if (p.getQuestStage(this) == 7) {
				say(p, n, "hello again");
				npcsay(p, n, "oh hello, do you need more touch paper?");
				if (!p.getCarriedItems().hasCatalogID(ItemId.TOUCH_PAPER.id(), Optional.empty())) {
					say(p, n, "yes please");
					npcsay(p, n, "ok there you go");
					p.message("the chemist gives you some touch paper");
					give(p, ItemId.TOUCH_PAPER.id(), 1);
				} else {
					say(p, n, "no i just wanted to say hello");
					npcsay(p, n, "oh, ok then ... hello");
					say(p, n, "hi");
				}
				return;
			} else if (p.getCarriedItems().hasCatalogID(ItemId.PLAGUE_SAMPLE.id(), Optional.of(false))
				&& p.getCarriedItems().hasCatalogID(ItemId.LIQUID_HONEY.id(), Optional.of(false))
				&& p.getCarriedItems().hasCatalogID(ItemId.SULPHURIC_BROLINE.id(), Optional.of(false))
				&& p.getCarriedItems().hasCatalogID(ItemId.ETHENEA.id(), Optional.of(false)) && p.getQuestStage(this) == 6) {
				npcsay(p, n, "Sorry, I'm afraid we're just closing now, you'll have to come back another time");
				int menu = multi(p, n, "This can't wait,I'm carrying a plague sample that desperately needs analysis",
					"It's OK I'm Elena's friend");
				if (menu == 0) {
					npcsay(p, n, "You idiot! A plague sample should be confined to a lab",
						"I'm taking it off you- I'm afraid it's the only responsible thing to do");
					p.message("He takes the plague sample from you");
					p.getCarriedItems().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
				} else if (menu == 1) {
					npcsay(p, n, "Oh, well that's different then. Must be pretty important to come all this way",
						"How's everyone doing there anyway? Wasn't there was some plague scare");
					int lastMenu = multi(p, n,
						"that's why I'm here: I need some more touch paper for this plague sample",
						"Who knows... I just need some touch paper for a guy called Guidor");
					if (lastMenu == 0) {
						npcsay(p, n, "You idiot! A plague sample should be confined to a lab",
							"I'm taking it off you- I'm afraid it's the only responsible thing to do");
						p.message("He takes the plague sample from you");
						p.getCarriedItems().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
					} else if (lastMenu == 1) {
						npcsay(p, n, "Guidor? This one's on me then- the poor guy. Sorry about the interrogation",
							"It's just that there's been rumours of a man travelling with a plague on him",
							"They're even doing spot checks in Varrock: it's a pharmeceutical disaster");
						say(p, n, "Oh right...so am I going to be OK carrying these three vials with me?");
						npcsay(p, n, "With touch paper as well? You're asking for trouble",
							"You'd be better using my errand boys outside- give them a vial each",
							"They're not the most reliable people in the world",
							"One's a painter, one's a gambler, and one's a drunk",
							"Still, if you pay peanuts you'll get monkeys, right?",
							"And it's better than entering Varrock with half a laborotory in your napsack");
						say(p, n, "OK- thanks for your help, I know that Elena appreciates it");
						npcsay(p, n, "Yes well don't stand around here gassing",
							"You'd better hurry if you want to see Guidor",
							"He won't be around for much longer");
						p.message("He gives you the touch paper");
						give(p, ItemId.TOUCH_PAPER.id(), 1);
						p.updateQuestStage(this, 7);

					}
				}
			} else {
				p.message("The chemist is busy at the moment");
			}
		}
		else if (n.getID() == NpcId.DEVINCI.id()) {
			if (p.getQuestStage(this) == 7) {
				if (p.getCache().hasKey("vial_vinci") || p.getCache().hasKey("wrong_vial_vinci")) {
					npcsay(p, n, "Oh, it's you again",
						"Please don't distract me now, I'm contemplating the sublime");
					return;
				}
				say(p, n, "Hello.i hear you're an errand boy for the chemist");
				npcsay(p, n, "Well that's my day job yes",
					"But I don't necessarily define my identity in such black and white terms");
				say(p, n, "Good for you",
					"Now can you take a vial to Varrock for me?");
				npcsay(p, n, "Go on then");
				int menu = multi(p, n, false, //do not send over
					"You give him the vial of ethenea",
					"You give him the vial of liquid honey",
					"You give him the vial of sulphuric broline");
				if (menu == 0) {
					if (p.getCarriedItems().hasCatalogID(ItemId.ETHENEA.id(), Optional.of(false))) {
						if (!p.getCache().hasKey("vial_vinci")) {
							p.getCache().store("vial_vinci", true);
							p.getCarriedItems().remove(new Item(ItemId.ETHENEA.id()));
							Functions.mes(p, "You give him the vial of ethenea");
							npcsay(p, n, "OK. We're meeting at the dancing donkey in Varrock right?");
							say(p, n, "That's right.");
						}
					} else {
						p.message("You can't give him what you don't have");
					}
				} else if (menu == 1) {
					if (p.getCarriedItems().hasCatalogID(ItemId.LIQUID_HONEY.id(), Optional.of(false))) {
						if (!p.getCache().hasKey("wrong_vial_vinci")) {
							p.getCache().store("wrong_vial_vinci", true);
							p.getCarriedItems().remove(new Item(ItemId.LIQUID_HONEY.id()));
							Functions.mes(p, "You give him the vial of liquid honey");
							npcsay(p, n, "OK. We're meeting at the dancing donkey in Varrock right?");
							say(p, n, "That's right.");
						}
					} else {
						p.message("You can't give him what you don't have");
					}
				} else if (menu == 2) {
					if (p.getCarriedItems().hasCatalogID(ItemId.SULPHURIC_BROLINE.id(), Optional.of(false))) {
						if (!p.getCache().hasKey("wrong_vial_vinci")) {
							p.getCache().store("wrong_vial_vinci", true);
							p.getCarriedItems().remove(new Item(ItemId.SULPHURIC_BROLINE.id()));
							Functions.mes(p, "You give him the vial of sulphuric broline");
							npcsay(p, n, "OK. We're meeting at the dancing donkey in Varrock right?");
							say(p, n, "That's right.");
						}
					} else {
						p.message("You can't give him what you don't have");
					}
				}
			} else {
				p.message("Devinci does not feel sufficiently moved to talk");
			}
		}
		else if (n.getID() == NpcId.KING_LATHAS.id()) {
			/** START UNDERGROUND PASS QUEST!!! **/
			if (p.getQuestStage(this) == -1) {
				switch (p.getQuestStage(Quests.UNDERGROUND_PASS)) {
					case 0:
					case 1:
					case 2:
						say(p, n, "hello king lathas");
						npcsay(p, n, "adventurer, thank saradomin for your arrival");
						say(p, n, "have your scouts found a way though the mountains");
						npcsay(p, n, "Not quite, we found a path to where we expected..",
							"..to find the 'well of voyage' an ancient portal to west runescape",
							"however over the past era's a cluster of cultists",
							"have settled there, run by a madman named iban");
						say(p, n, "iban?");
						npcsay(p, n, "a crazy loon who claims to be the son of zamorok",
							"go meet my main tracker koftik, he will help you",
							"he waits for you at the west side of west ardounge",
							"we must find a way through these caverns..",
							"if we are to stop my brother tyras");
						say(p, n, "i'll do my best lathas");
						npcsay(p, n, "a warning traveller the ungerground pass..",
							"is lethal, we lost many men exploring those caverns",
							"go preparred with food and armour or you won't last long");
						if (p.getQuestStage(Quests.UNDERGROUND_PASS) == 0) {
							p.updateQuestStage(Quests.UNDERGROUND_PASS, 1);
						}
						break;
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
						say(p, n, "hello king lanthas");
						npcsay(p, n, "traveller, how are you managing down there?");
						say(p, n, "it's a pretty nasty place but i'm ok");
						npcsay(p, n, "well keep up the good work");
						break;
					case 8:
						npcsay(p, n, "the traveller returns..any news?");
						say(p, n, "indeed, the quest is complete lathas",
							"i have defeated iban and his undead minions");
						npcsay(p, n, "incrediable, you are a truly awesome warrior",
							"now we can begin to restore the well of voyage",
							"once our mages have re-summoned the well",
							"i will send a band of troops led by yourself",
							"to head into west runescape and stop tryas");
						say(p, n, "i will be ready and waiting");
						npcsay(p, n, "your loyalty is appreiciated traveller");
						p.sendQuestComplete(Quests.UNDERGROUND_PASS);
						break;
					case -1:
						say(p, n, "hello king lathas");
						npcsay(p, n, "well hello there traveller",
							"the mages are still ressurecting the well of voyage",
							"but i'll have word sent to you as soon as its ready");
						say(p, n, "ok then, take care");
						npcsay(p, n, "you too");
						break;
				}
				return;
			} else if (p.getQuestStage(this) == 9) {
				say(p, n, "I assume that you are the King of east Ardougne?");
				npcsay(p, n, "You assume correctly- but where do you get such impertinence?");
				say(p, n, "I get it from finding out that the plague is a hoax");
				npcsay(p, n, "A hoax, I've never heard such a ridiculous thing...");
				say(p, n, "I have evidence- from Guidor in Varrock");
				npcsay(p, n, "Ah... I see. Well then you are right about the plague",
					"But I did it for the good of my people");
				say(p, n, "When is it ever good to lie to people like that?");
				npcsay(p, n, "When it protects them from a far greater danger- a fear too big to fathom");
				int menu = multi(p, n,
					"I don't understand...",
					"Well I've wasted enough of my time here");
				if (menu == 0) {
					npcsay(p, n, "Their King, tyras, journeyed out to the West, on a voyage of discovery",
						"But he was captured by the Dark Lord",
						"The Dark Lord agreed to spare his life, but only on one condition...",
						"That he would drink from the chalice of eternity");
					say(p, n, "So what happened?");
					npcsay(p, n, "The chalice corrupted him. He joined forces with the Dark Lord...",
						"...The embodiment of pure evil, banished all those years ago...",
						"And so I erected this wall, not just to protect my people",
						"But to protect all the people of Runescape",
						"Because now, with the King of West Ardougne...",
						"...The dark lord has an ally on the inside",
						"So I'm sorry that I lied about the plague",
						"I just hope that you can understand my reasons");
					say(p, n, "Well at least I know now. But what can we do about it?");
					npcsay(p, n, "Nothing at the moment",
						"I'm waiting for my scouts to come back",
						"They will tell us how we can get through the mountains",
						"When this happens, can I count on your support?");
					say(p, n, "Absolutely");
					npcsay(p, n, "Thank the gods. Let me give you this amulet",
						"Think of it as a thank you, for all that you have done",
						"...but know that one day it may turn red",
						"...Be ready for this moment",
						"And to help, I give you permission to use my training area",
						"It's located just to the north west of ardounge",
						"There you can prepare for the challenge ahead");
					say(p, n, "OK. There's just one thing I don't understand");
					say(p, n, "How do you know so much about King Tyras");
					npcsay(p, n, "How could I not do?",
						"He was my brother");
					p.message("king lathas gives you a magic amulet");
					give(p, ItemId.KING_LATHAS_AMULET.id(), 1);
					p.sendQuestComplete(Quests.BIOHAZARD);
				} else if (menu == 1) {
					npcsay(p, n, "No time is ever wasted- thanks for all you've done");
				}
				return;
			}
			p.message("the king is too busy to talk");
		}
		else if (n.getID() == NpcId.GUIDORS_WIFE.id()) {
			if (p.getQuestStage(this) == 9 || p.getQuestStage(this) == -1) {
				say(p, n, "hello");
				npcsay(p, n, "oh hello, i can't chat now",
					"i have to keep an eye on my husband",
					"he's very ill");
				say(p, n, "i'm sorry to hear that");
				return;
			}
			if (p.getQuestStage(this) == 8) {
				say(p, n, "hello again");
				npcsay(p, n, "hello there",
					"i fear guidor may not be long for this world");
				return;
			}
			if (p.getQuestStage(this) == 7) {
				if (p.getCarriedItems().getEquipment().hasEquipped(ItemId.PRIEST_ROBE.id()) && p.getCarriedItems().getEquipment().hasEquipped(ItemId.PRIEST_GOWN.id())) {
					npcsay(p, n, "Father, thank heavens you're here. My husband is very ill",
						"Perhaps you could go and perform his final ceremony");
					say(p, n, "I'll see what I can do");
				} else {
					say(p, n, "Hello, I'm a friend of Elena, here to see Guidor");
					npcsay(p, n, "I'm afraid...(she sobs)... that Guidor is not long for this world",
						"So I'm not letting people see him now");
					say(p, n, "I'm really sorry to hear about Guidor...",
						"but I do have some very important business to attend to");
					npcsay(p, n, "You heartless rogue. What could be more important than Guidor's life?",
						"...A life spent well, if not always wisely...",
						"I just hope that Saradomin shows mercy on his soul");
					say(p, n, "Guidor is a religious man?");
					npcsay(p, n, "Oh god no. But I am",
						"if only i could get him to see a priest");
				}
			}
		}
		else if (n.getID() == NpcId.GUIDOR.id()) {
			if (p.getQuestStage(this) == 8 || p.getQuestStage(this) == 9 || p.getQuestStage(this) == -1) {
				say(p, n, "hello again guidor");
				npcsay(p, n, "well hello traveller",
					"i still can't understand why they would lie about the plague");
				say(p, n, "it's strange, anyway how are you doing?");
				npcsay(p, n, "i'm hanging in there");
				say(p, n, "good for you");
				return;
			}
			say(p, n, "Hello,you must be Guidor. I understand that you are unwell");
			npcsay(p, n, "Is my wife asking priests to visit me now?",
				"I'm a man of science, for god's sake!",
				"Ever since she heard rumours of a plague carrier travelling from Ardougne",
				"she's kept me under house arrest",
				"Of course she means well, and I am quite frail now...",
				"So what brings you here?");
			int menu = multi(p, n, false, //do not send over
				"I've come to ask your assistance in stopping a plague that could kill thousands",
				"Oh,nothing,I was just going to bless your room and I've done that now  Goodbye");
			if (menu == 0) {
				say(p, n, "Well it's funny you should ask actually...",
					"I've come to ask your assistance in stopping a plague that could kill thousands");
				npcsay(p, n, "So you're the plague carrier!");
				int menu2 = multi(p, n,
					"No! Well, yes... but not exactly. It's contained in a sealed unit from elena",
					"I've been sent by your old pupil Elena, she's trying to halt the virus");
				//both lead to the EXACT same dialogue
				if (menu2 == 0 || menu2 == 1) {
					npcsay(p, n, "Elena eh?");
					say(p, n, "Yes. She wants you to analyse it",
						"You might be the only one that can help");
					npcsay(p, n, "Right then. Sounds like we'd better get to work!");
					if (p.getCarriedItems().hasCatalogID(ItemId.PLAGUE_SAMPLE.id(), Optional.of(false))) {
						say(p, n, "I have the plague sample");
						npcsay(p, n, "Now I'll be needing some liquid honey,some sulphuric broline,and then...");
						say(p, n, "...some ethenea?");
						npcsay(p, n, "Indeed!");
						if (p.getCarriedItems().hasCatalogID(ItemId.ETHENEA.id(), Optional.of(false))
							&& p.getCarriedItems().hasCatalogID(ItemId.SULPHURIC_BROLINE.id(), Optional.of(false))
							&& p.getCarriedItems().hasCatalogID(ItemId.LIQUID_HONEY.id(), Optional.of(false))) {
							if (p.getCarriedItems().hasCatalogID(ItemId.TOUCH_PAPER.id(), Optional.of(false))) {
								p.message("You give him the vials and the touch paper");
								p.getCarriedItems().remove(new Item(ItemId.TOUCH_PAPER.id()));
								p.getCarriedItems().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
								p.getCarriedItems().remove(new Item(ItemId.ETHENEA.id()));
								p.getCarriedItems().remove(new Item(ItemId.LIQUID_HONEY.id()));
								p.getCarriedItems().remove(new Item(ItemId.SULPHURIC_BROLINE.id()));
								npcsay(p, n, "Now I'll just apply these to the sample and...",
									"I don't get it...the touch paper has remained the same");
								p.updateQuestStage(this, 8);
								int menu3 = multi(p, n,
									"That's why Elena wanted you to do it- because she wasn't sure what was happening",
									"So what does that mean exactly?");
								if (menu3 == 0) {
									npcsay(p, n, "Well that's just it. Nothing has happened",
										"I don't know what this sample is, but it certainly isn't toxic");
									say(p, n, "So what about the plague?");
									npcsay(p, n, "Don't you understand, there is no plague!",
										"I'm very sorry, I can see that you've worked hard for this...",
										"...but it seems that someone has been lying to you",
										"The only question is...",
										"...why?");
								} else if (menu3 == 1) {
									say(p, n, "That's why Elena wanted you to do it- because she wasn't sure what was happening");
									npcsay(p, n, "Well that's just it. Nothing has happened",
										"I don't know what this sample is, but it certainly isn't toxic");
									say(p, n, "So what about the plague?");
									npcsay(p, n, "Don't you understand, there is no plague!",
										"I'm very sorry, I can see that you've worked hard for this...",
										"...but it seems that someone has been lying to you",
										"The only question is...",
										"...why?");
								}

							} else {
								npcsay(p, n, "Oh. You don't have any touch-paper",
									"And so I won't be able to help you after all");
							}
						} else {
							npcsay(p, n, "Look,I need all three reagents to test the plague sample",
								"Come back when you've got them");
						}
					} else {
						npcsay(p, n, "Seems like you don't actually HAVE the plague sample",
							"It's a long way to come empty-handed...",
							"And quite a long way back too");
						return;
					}
				}
			} else if (menu == 1) {
				say(p, n, "Oh, nothing, I was just going to bless your room, and I've done that now. Goodbye");
			}
		}
	}

	private void ropeLadderBackFunction(final Player p) {
		GameObject ropeLadder = new GameObject(p.getWorld(), Point.location(622, 611), VISUAL_ROPELADDER, 0, 0);
		p.getWorld().registerGameObject(ropeLadder);
		Functions.mes(p, "you climb up the rope ladder");
		p.teleport(622, 611);
		Functions.mes(p, "and drop down on the other side");
		delay(2000);
		p.getWorld().unregisterGameObject(ropeLadder);
	}

	private void ropeLadderInFunction(final Player p) {
		GameObject ropeLadder = new GameObject(p.getWorld(), Point.location(622, 611), VISUAL_ROPELADDER, 0, 0);
		p.getWorld().registerGameObject(ropeLadder);
		Functions.mes(p, "you climb up the rope ladder");
		p.teleport(624, 606);
		Functions.mes(p, "and drop down on the other side");
		delay(2000);
		p.getWorld().unregisterGameObject(ropeLadder);
	}

	@Override
	public boolean blockOpBound(GameObject obj, Integer click, Player p) {
		return obj.getID() == ELENAS_DOOR;
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, Player p) {
		if (obj.getID() == ELENAS_DOOR) {
			if (p.getQuestStage(Quests.PLAGUE_CITY) == -1) {
				doDoor(obj, p);
				p.message("You go through the door");
			} else {
				p.message("the door is locked");
			}
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return DataConversions.inArray(new int[] {JERICOS_CUPBOARD_ONE_OPEN, JERICOS_CUPBOARD_ONE_CLOSED, JERICOS_CUPBOARD_TWO_OPEN, JERICOS_CUPBOARD_TWO_CLOSED,
				WATCH_TOWER, NURSE_SARAHS_CUPBOARD_OPEN, NURSE_SARAHS_CUPBOARD_CLOSED, GET_INTO_CRATES_GATE, DISTILLATOR_CRATE, OTHER_CRATE}, obj.getID());
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == JERICOS_CUPBOARD_ONE_OPEN || obj.getID() == JERICOS_CUPBOARD_ONE_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, p, JERICOS_CUPBOARD_ONE_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, p, JERICOS_CUPBOARD_ONE_CLOSED);
			} else {
				p.message("You search the cupboard, but find nothing");
			}
		}
		else if (obj.getID() == JERICOS_CUPBOARD_TWO_OPEN || obj.getID() == JERICOS_CUPBOARD_TWO_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, p, JERICOS_CUPBOARD_TWO_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, p, JERICOS_CUPBOARD_TWO_CLOSED);
			} else {
				p.message("you search the cupboard");
				if (!p.getCarriedItems().hasCatalogID(ItemId.BIRD_FEED.id(), Optional.empty())) {
					p.message("and find some pigeon feed");
					give(p, ItemId.BIRD_FEED.id(), 1);
				} else {
					p.message("but find nothing of interest");
				}
			}
		}
		else if (obj.getID() == NURSE_SARAHS_CUPBOARD_OPEN || obj.getID() == NURSE_SARAHS_CUPBOARD_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, p, NURSE_SARAHS_CUPBOARD_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, p, NURSE_SARAHS_CUPBOARD_CLOSED);
			} else {
				p.message("you search the cupboard");
				if ((!p.getCarriedItems().hasCatalogID(ItemId.DOCTORS_GOWN.id(), Optional.empty())) && (p.getQuestStage(this) == 4 || p.getQuestStage(this) == 5)) {
					p.message("inside you find a doctor's gown");
					give(p, ItemId.DOCTORS_GOWN.id(), 1);
				} else {
					p.message("but find nothing of interest");
				}
			}
		}
		else if (obj.getID() == WATCH_TOWER) {
			if (command.equalsIgnoreCase("approach")) {
				Npc mournerGuard = ifnearvisnpc(p, NpcId.MOURNER_BYENTRANCE2.id(), 15);
				if (mournerGuard != null) {
					npcsay(p, mournerGuard, "keep away civilian");
					say(p, mournerGuard, "what's it to you?");
					npcsay(p, mournerGuard, "the tower's here for your protection");
				}
			}
		}
		else if (obj.getID() == GET_INTO_CRATES_GATE) {
			if (p.getX() <= 630) {
				doGate(p, obj);
				p.message("you open the gate and pass through");
			} else {
				Functions.mes(p, "the gate is locked");
				p.message("you need a key");
			}
		}
		else if (obj.getID() == DISTILLATOR_CRATE || obj.getID() == OTHER_CRATE) {
			if (obj.getID() == DISTILLATOR_CRATE) {
				Functions.mes(p, "you search the crate");
				if (!p.getCarriedItems().hasCatalogID(ItemId.DISTILLATOR.id(), Optional.empty())) {
					Functions.mes(p, "and find elena's distillator");
					give(p, ItemId.DISTILLATOR.id(), 1);
					if (p.getCache().hasKey("rotten_apples")) {
						p.getCache().remove("rotten_apples");
						p.updateQuestStage(this, 5);
					}
				} else {
					Functions.mes(p, "it's empty");
				}
			} else {
				p.message("The crate is empty");
			}
		}
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player p) {
		return (item.getCatalogId() == ItemId.BIRD_FEED.id() && obj.getID() == WATCH_TOWER)
				|| (item.getCatalogId() == ItemId.ROTTEN_APPLES.id() && obj.getID() == COOKING_POT)
				|| (item.getCatalogId() == ItemId.BIOHAZARD_BRONZE_KEY.id() && obj.getID() == GET_INTO_CRATES_GATE);
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (item.getCatalogId() == ItemId.BIRD_FEED.id() && obj.getID() == WATCH_TOWER) {
			if (p.getQuestStage(this) == 2) {
				Functions.mes(p, "you throw a hand full of seeds onto the watch tower",
					"the mourners do not seem to notice");
				p.getCarriedItems().remove(new Item(ItemId.BIRD_FEED.id()));
				if (!p.getCache().hasKey("bird_feed")) {
					p.getCache().store("bird_feed", true);
				}
			} else {
				p.message("nothing interesting happens");
			}
		}
		else if (item.getCatalogId() == ItemId.ROTTEN_APPLES.id() && obj.getID() == COOKING_POT) {
			if (p.getQuestStage(this) == 4 || p.getQuestStage(this) == 5) {
				Functions.mes(p, "you place the rotten apples in the pot",
					"they quickly dissolve into the stew",
					"that wasn't very nice");
				if (!p.getCache().hasKey("rotten_apples")) {
					p.getCache().store("rotten_apples", true);
				}
				p.getCarriedItems().remove(new Item(ItemId.ROTTEN_APPLES.id()));
				return;
			}
			Functions.mes(p, "you place the rotten apples in the pot",
				"that wasn't very nice");
			p.getCarriedItems().remove(new Item(ItemId.ROTTEN_APPLES.id()));
		}
		else if (item.getCatalogId() == ItemId.BIOHAZARD_BRONZE_KEY.id() && obj.getID() == GET_INTO_CRATES_GATE) {
			Functions.mes(p, "the key fits the gate");
			p.message("you open it and pass through");
			doGate(p, obj);
		}
	}

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
		return n.getID() == NpcId.MOURNER_ILL.id();
	}

	@Override
	public void onKillNpc(Player p, Npc n) {
		if (n.getID() == NpcId.MOURNER_ILL.id()) {
			if (!p.getCarriedItems().hasCatalogID(ItemId.BIOHAZARD_BRONZE_KEY.id(), Optional.empty())) {
				Functions.mes(p, "you search the mourner");
				give(p, ItemId.BIOHAZARD_BRONZE_KEY.id(), 1);
				if (n != null) {
					n.getBehavior().retreat();
					mes(p, 0, "and find a key");
					n.remove();
				}
			} else {
				n.killedBy(p);
			}
		}
	}
}
