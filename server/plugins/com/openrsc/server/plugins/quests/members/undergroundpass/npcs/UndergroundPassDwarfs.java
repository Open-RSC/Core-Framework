package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassDwarfs implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.KAMEN.id() || n.getID() == NpcId.NILOOF.id() || n.getID() == NpcId.KLANK.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.KAMEN.id()) {
			switch (p.getQuestStage(Quests.UNDERGROUND_PASS)) {
				case 5:
				case 6:
				case 7:
				case 8:
				case -1:
					Functions.mes(p, "the dwarf is leaning on a barrel of home made brew");
					p.message("he looks a little drunk");
					say(p, n, "hi there, you ok?");
					npcsay(p, n, "ooooh, my head ...im gone");
					say(p, n, "what's wrong?");
					npcsay(p, n, "to much of this home brew my friend",
						"we make from plant roots",
						"but it blows your head off",
						"you don't wanna put it near any naked flames",
						"want some?");
					int menu = multi(p, n,
						"ok then",
						"no thanks");
					if (menu == 0) {
						npcsay(p, n, "here you go");
						p.message("you take a sip of brew from kamens glass");
						p.damage(5);
						say(p, n, "aaarrgghh");
						p.message("it tastse horrific and burns your throat");
						npcsay(p, n, "ha ha",
							"i warned you that it's strong stuff");
					} else if (menu == 1) {
						npcsay(p, n, "your losh");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.NILOOF.id()) {
			switch (p.getQuestStage(Quests.UNDERGROUND_PASS)) {
				case 5:
					npcsay(p, n, "back away..back away..wait..",
						"..you're human!");
					say(p, n, "that's right, i'm on a quest for king lathas",
						"we need to find a way through these caverns");
					npcsay(p, n, "ha ha, listen up, we came here as miners decades ago",
						"completely unaware of the evil that lurked in the caverns",
						"there's no way through, not while iban still rules",
						"he controls the gateway,the only way to the other side");
					say(p, n, "what gateway?");
					npcsay(p, n, "it once stood as the the 'well of voyage'",
						"a gateway to west runescape",
						"now ibans moulded it into a pit of the damned",
						"a portal to zamoraks darkest realms",
						"he sends his followers there, never to return",
						"only once iban is destroyed can the well be restored");
					say(p, n, "but how?");
					npcsay(p, n, "if i knew, i would have slain him already",
						"seek out the witch, his guide , his only confidante",
						"only she knows how to rid us of iban",
						"she lives on the platforms above, we dare not go there",
						"here, take some food to aid your journey");
					p.message("Niloof give you some food");
					give(p, ItemId.MEAT_PIE.id(), 2);
					give(p, ItemId.CHOCOLATE_BOMB.id(), 1);
					give(p, ItemId.MEAT_PIZZA.id(), 1);
					say(p, n, "thanks niloof, take care");
					npcsay(p, n, "you too");
					p.updateQuestStage(Quests.UNDERGROUND_PASS, 6);
					break;
				case 6:
					if (p.getCache().hasKey("doll_of_iban")) {
						say(p, n, "niloof, i found the witch's house");
						npcsay(p, n, "and...?");
						if (p.getCarriedItems().hasCatalogID(ItemId.A_DOLL_OF_IBAN.id(), Optional.of(false))
							&& !p.getCarriedItems().hasCatalogID(ItemId.OLD_JOURNAL.id(), Optional.of(false))) {
							npcsay(p, n, "i found this old book",
								"i'm not sure if it's of any use to you traveller");
							give(p, ItemId.OLD_JOURNAL.id(), 1);
							return;
						} else if (!p.getCarriedItems().hasCatalogID(ItemId.A_DOLL_OF_IBAN.id(), Optional.of(false))) {
							say(p, n, "i found a strange doll and a book",
									"but i've lost the doll");
							npcsay(p, n, "well it's a good job i found it");
							give(p, ItemId.A_DOLL_OF_IBAN.id(), 1);
							npcsay(p, n, "the witches rag doll, this here be black magic traveller",
									"mixed with the right ingredients the doll can inflict serious harm",
									"these four elements of being are guarded somewhere in this cave",
									"his shadow, his flesh, his conscience and his blood",
									"if you can retrieve these,combined with the doll...",
									"you will be able destroy iban...",
									"and ressurect the 'well of voyage'");
							if (!p.getCarriedItems().hasCatalogID(ItemId.OLD_JOURNAL.id(), Optional.of(false))) {
								npcsay(p, n, "i found this old book",
										"i'm not sure if it's of any use to you traveller");
								give(p, ItemId.OLD_JOURNAL.id(), 1);
							}
							return;
						}
						say(p, n, "i found a strange book and this..");
						p.message("you show niloof the strange doll");
						npcsay(p, n, "the witches rag doll, this here be black magic traveller");
						npcsay(p, n, "iban was magically conjured in that very item");
						npcsay(p, n, "his four elements of bieng are guarded somewhere in this cave");
						npcsay(p, n, "his shadow, his flesh, his conscience and his blood");
						npcsay(p, n, "if you can retrieve these, with the flask...");
						npcsay(p, n, "you will be able destroy iban...");
						npcsay(p, n, "and ressurect the 'well of voyage'");
					} else {
						say(p, n, "hello niloof");
						npcsay(p, n, "so you still live, not many survive down here");
						say(p, n, "as i can see");
						npcsay(p, n, "don't stay too long traveller",
							"ibans calls will soon penetrate your delicate human mind",
							"and you'll also become one of his minions",
							"you must go above and find the witch kardia",
							"she holds the secret to ibans destruction");
					}
					break;
				case 7:
					say(p, n, "hi niloof");
					npcsay(p, n, "traveller, thank the stars you're still around",
							"i thought your time had come");
					say(p, n, "i've still a few years in me yet");
					if (!p.getCarriedItems().hasCatalogID(ItemId.A_DOLL_OF_IBAN.id(), Optional.of(false))) {
						npcsay(p, n, "i found something i think you need traveller");
						say(p, n, "the doll?");
						npcsay(p, n, "i found it while slaying some of the souless, here");
						p.message("niloof gives you the doll of iban");
						give(p, ItemId.A_DOLL_OF_IBAN.id(), 1);
					}
					say(p, n, "it's about time i delt with iban");
					npcsay(p, n, "good luck to you, you'll need it",
							"may the strength of the elders be with you");
					say(p, n, "take care niloof");
					break;
				case 8:
				case -1:
					p.message("the dwarf seems to be busy");
					break;
			}
		}
		else if (n.getID() == NpcId.KLANK.id()) {
			switch (p.getQuestStage(Quests.UNDERGROUND_PASS)) {
				case 5:
				case 6:
					if (p.getCache().hasKey("doll_of_iban")) {
						say(p, n, "hi klank");
						npcsay(p, n, "traveller,I hear you plan to destroy iban");
						say(p, n, "that's right");
						npcsay(p, n, "i have a gift for you, they may help",
							"i crafted these long ago to protect myself...",
							"from the teeth of the souless, their bite is vicous",
							"i haven't seen a another pair which can with stand their jaws");
						p.message("klank gives you a pair of gaunlets");
						give(p, ItemId.KLANKS_GAUNTLETS.id(), 1);
						p.message("and a tinderbox");
						give(p, ItemId.TINDERBOX.id(), 1);
						say(p, n, "thanks klank");
						npcsay(p, n, "good luck traveller, give iban a slap for me");
					} else {
						say(p, n, "hello my good man");
						npcsay(p, n, "Good day to you outsider",
							"i'm klank, i'm the only blacksmith still alive down here",
							"infact we're the only ones that haven't yet turned",
							"if you're not carefull you'll become one of them too");
						say(p, n, "who?.. ibans followers");
						npcsay(p, n, "they're not followers, they're slaves, they're the souless");
						int menu = multi(p, n, false, //do not send over
							"what happened to them?",
							"no wonder their breath was soo bad");
						if (menu == 0) {
							say(p, n, "what happened to them?");
							npcsay(p, n, "they were normal once, adventurers, treasure hunters",
								"but men are weak, they couldn't ignore the vocies",
								"now they all seem to think with one conscience..",
								"as if they're being controlled by one being");
							say(p, n, "iban?");
							npcsay(p, n, "maybe?... maybe zamorak himself",
								"those who try and fight it...",
								"iban locks in cages, until their minds are too weak to resist",
								"eventually they all fall to his control",
								"here take this, i don't need it");
							p.message("klank gives you a tinderbox");
							give(p, ItemId.TINDERBOX.id(), 1);
						} else if (menu == 1) {
							say(p, n, "no wonder they're breath was soo bad");
							npcsay(p, n, "you think this is funny.. eh");
							say(p, n, "not really, just trying to lighten up the conversation");
							npcsay(p, n, "here take this, i don't need it");
							p.message("klank gives you a tinderbox");
							give(p, ItemId.TINDERBOX.id(), 1);
						}
					}
					break;
				case 7:
				case 8:
				case -1:
					say(p, n, "hello klank");
					npcsay(p, n, "hello again adventurer, so you're still around");
					say(p, n, "still here!");
					int menu = multi(p, n,
						"have you anymore gauntlets?",
						"take care klank");
					if (menu == 0) {
						npcsay(p, n, "well..yes, but they're not cheap to make",
							"i'll have to sell you a pair");
						say(p, n, "how much?");
						npcsay(p, n, "5000 coins");
						int menu2 = multi(p, n,
							"5000, you must be joking",
							"ok then, i'll take a pair");
						if (menu2 == 0) {
							npcsay(p, n, "we don't joke down here, friend");
						} else if (menu2 == 1) {
							if (ifheld(p, ItemId.COINS.id(), 5000)) {
								p.message("you give klank 5000 coins...");
								remove(p, ItemId.COINS.id(), 5000);
								p.message("...and klank gives you a pair of guanletts");
								give(p, ItemId.KLANKS_GAUNTLETS.id(), 1);
								npcsay(p, n, "there you go..i hope they help");
								say(p, n, "i'll see you around klank");
							} else {
								say(p, n, "oh dear, i haven't enough money");
								npcsay(p, n, "sorry, i can't sell them any cheaper than that");
							}
						}
					} else if (menu == 1) {
						npcsay(p, n, "you too adventurer");
					}
					break;
			}
		}
	}
}
