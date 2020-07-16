package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassDwarfs implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.KAMEN.id() || n.getID() == NpcId.NILOOF.id() || n.getID() == NpcId.KLANK.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.KAMEN.id()) {
			switch (player.getQuestStage(Quests.UNDERGROUND_PASS)) {
				case 5:
				case 6:
				case 7:
				case 8:
				case -1:
					mes("the dwarf is leaning on a barrel of home made brew");
					delay(3);
					player.message("he looks a little drunk");
					say(player, n, "hi there, you ok?");
					npcsay(player, n, "ooooh, my head ...im gone");
					say(player, n, "what's wrong?");
					npcsay(player, n, "to much of this home brew my friend",
						"we make from plant roots",
						"but it blows your head off",
						"you don't wanna put it near any naked flames",
						"want some?");
					int menu = multi(player, n,
						"ok then",
						"no thanks");
					if (menu == 0) {
						npcsay(player, n, "here you go");
						player.message("you take a sip of brew from kamens glass");
						player.damage(5);
						say(player, n, "aaarrgghh");
						player.message("it tastse horrific and burns your throat");
						npcsay(player, n, "ha ha",
							"i warned you that it's strong stuff");
					} else if (menu == 1) {
						npcsay(player, n, "your losh");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.NILOOF.id()) {
			switch (player.getQuestStage(Quests.UNDERGROUND_PASS)) {
				case 5:
					npcsay(player, n, "back away..back away..wait..",
						"..you're human!");
					say(player, n, "that's right, i'm on a quest for king lathas",
						"we need to find a way through these caverns");
					npcsay(player, n, "ha ha, listen up, we came here as miners decades ago",
						"completely unaware of the evil that lurked in the caverns",
						"there's no way through, not while iban still rules",
						"he controls the gateway,the only way to the other side");
					say(player, n, "what gateway?");
					npcsay(player, n, "it once stood as the the 'well of voyage'",
						"a gateway to west runescape",
						"now ibans moulded it into a pit of the damned",
						"a portal to zamoraks darkest realms",
						"he sends his followers there, never to return",
						"only once iban is destroyed can the well be restored");
					say(player, n, "but how?");
					npcsay(player, n, "if i knew, i would have slain him already",
						"seek out the witch, his guide , his only confidante",
						"only she knows how to rid us of iban",
						"she lives on the platforms above, we dare not go there",
						"here, take some food to aid your journey");
					player.message("Niloof give you some food");
					give(player, ItemId.MEAT_PIE.id(), 2);
					give(player, ItemId.CHOCOLATE_BOMB.id(), 1);
					give(player, ItemId.MEAT_PIZZA.id(), 1);
					say(player, n, "thanks niloof, take care");
					npcsay(player, n, "you too");
					player.updateQuestStage(Quests.UNDERGROUND_PASS, 6);
					break;
				case 6:
					if (player.getCache().hasKey("doll_of_iban")) {
						say(player, n, "niloof, i found the witch's house");
						npcsay(player, n, "and...?");
						if (player.getCarriedItems().hasCatalogID(ItemId.A_DOLL_OF_IBAN.id(), Optional.of(false))
							&& !player.getCarriedItems().hasCatalogID(ItemId.OLD_JOURNAL.id(), Optional.of(false))) {
							npcsay(player, n, "i found this old book",
								"i'm not sure if it's of any use to you traveller");
							give(player, ItemId.OLD_JOURNAL.id(), 1);
							return;
						} else if (!player.getCarriedItems().hasCatalogID(ItemId.A_DOLL_OF_IBAN.id(), Optional.of(false))) {
							say(player, n, "i found a strange doll and a book",
									"but i've lost the doll");
							npcsay(player, n, "well it's a good job i found it");
							give(player, ItemId.A_DOLL_OF_IBAN.id(), 1);
							npcsay(player, n, "the witches rag doll, this here be black magic traveller",
									"mixed with the right ingredients the doll can inflict serious harm",
									"these four elements of being are guarded somewhere in this cave",
									"his shadow, his flesh, his conscience and his blood",
									"if you can retrieve these,combined with the doll...",
									"you will be able destroy iban...",
									"and ressurect the 'well of voyage'");
							if (!player.getCarriedItems().hasCatalogID(ItemId.OLD_JOURNAL.id(), Optional.of(false))) {
								npcsay(player, n, "i found this old book",
										"i'm not sure if it's of any use to you traveller");
								give(player, ItemId.OLD_JOURNAL.id(), 1);
							}
							return;
						}
						say(player, n, "i found a strange book and this..");
						player.message("you show niloof the strange doll");
						npcsay(player, n, "the witches rag doll, this here be black magic traveller");
						npcsay(player, n, "iban was magically conjured in that very item");
						npcsay(player, n, "his four elements of bieng are guarded somewhere in this cave");
						npcsay(player, n, "his shadow, his flesh, his conscience and his blood");
						npcsay(player, n, "if you can retrieve these, with the flask...");
						npcsay(player, n, "you will be able destroy iban...");
						npcsay(player, n, "and ressurect the 'well of voyage'");
					} else {
						say(player, n, "hello niloof");
						npcsay(player, n, "so you still live, not many survive down here");
						say(player, n, "as i can see");
						npcsay(player, n, "don't stay too long traveller",
							"ibans calls will soon penetrate your delicate human mind",
							"and you'll also become one of his minions",
							"you must go above and find the witch kardia",
							"she holds the secret to ibans destruction");
					}
					break;
				case 7:
					say(player, n, "hi niloof");
					npcsay(player, n, "traveller, thank the stars you're still around",
							"i thought your time had come");
					say(player, n, "i've still a few years in me yet");
					if (!player.getCarriedItems().hasCatalogID(ItemId.A_DOLL_OF_IBAN.id(), Optional.of(false))) {
						npcsay(player, n, "i found something i think you need traveller");
						say(player, n, "the doll?");
						npcsay(player, n, "i found it while slaying some of the souless, here");
						player.message("niloof gives you the doll of iban");
						give(player, ItemId.A_DOLL_OF_IBAN.id(), 1);
					}
					say(player, n, "it's about time i delt with iban");
					npcsay(player, n, "good luck to you, you'll need it",
							"may the strength of the elders be with you");
					say(player, n, "take care niloof");
					break;
				case 8:
				case -1:
					player.message("the dwarf seems to be busy");
					break;
			}
		}
		else if (n.getID() == NpcId.KLANK.id()) {
			switch (player.getQuestStage(Quests.UNDERGROUND_PASS)) {
				case 5:
				case 6:
					if (player.getCache().hasKey("doll_of_iban")) {
						say(player, n, "hi klank");
						npcsay(player, n, "traveller,I hear you plan to destroy iban");
						say(player, n, "that's right");
						npcsay(player, n, "i have a gift for you, they may help",
							"i crafted these long ago to protect myself...",
							"from the teeth of the souless, their bite is vicous",
							"i haven't seen a another pair which can with stand their jaws");
						player.message("klank gives you a pair of gaunlets");
						give(player, ItemId.KLANKS_GAUNTLETS.id(), 1);
						player.message("and a tinderbox");
						give(player, ItemId.TINDERBOX.id(), 1);
						say(player, n, "thanks klank");
						npcsay(player, n, "good luck traveller, give iban a slap for me");
					} else {
						say(player, n, "hello my good man");
						npcsay(player, n, "Good day to you outsider",
							"i'm klank, i'm the only blacksmith still alive down here",
							"infact we're the only ones that haven't yet turned",
							"if you're not carefull you'll become one of them too");
						say(player, n, "who?.. ibans followers");
						npcsay(player, n, "they're not followers, they're slaves, they're the souless");
						int menu = multi(player, n, false, //do not send over
							"what happened to them?",
							"no wonder their breath was soo bad");
						if (menu == 0) {
							say(player, n, "what happened to them?");
							npcsay(player, n, "they were normal once, adventurers, treasure hunters",
								"but men are weak, they couldn't ignore the vocies",
								"now they all seem to think with one conscience..",
								"as if they're being controlled by one being");
							say(player, n, "iban?");
							npcsay(player, n, "maybe?... maybe zamorak himself",
								"those who try and fight it...",
								"iban locks in cages, until their minds are too weak to resist",
								"eventually they all fall to his control",
								"here take this, i don't need it");
							player.message("klank gives you a tinderbox");
							give(player, ItemId.TINDERBOX.id(), 1);
						} else if (menu == 1) {
							say(player, n, "no wonder they're breath was soo bad");
							npcsay(player, n, "you think this is funny.. eh");
							say(player, n, "not really, just trying to lighten up the conversation");
							npcsay(player, n, "here take this, i don't need it");
							player.message("klank gives you a tinderbox");
							give(player, ItemId.TINDERBOX.id(), 1);
						}
					}
					break;
				case 7:
				case 8:
				case -1:
					say(player, n, "hello klank");
					npcsay(player, n, "hello again adventurer, so you're still around");
					say(player, n, "still here!");
					int menu = multi(player, n,
						"have you anymore gauntlets?",
						"take care klank");
					if (menu == 0) {
						npcsay(player, n, "well..yes, but they're not cheap to make",
							"i'll have to sell you a pair");
						say(player, n, "how much?");
						npcsay(player, n, "5000 coins");
						int menu2 = multi(player, n,
							"5000, you must be joking",
							"ok then, i'll take a pair");
						if (menu2 == 0) {
							npcsay(player, n, "we don't joke down here, friend");
						} else if (menu2 == 1) {
							if (ifheld(player, ItemId.COINS.id(), 5000)) {
								player.message("you give klank 5000 coins...");
								player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5000));
								player.message("...and klank gives you a pair of guanletts");
								give(player, ItemId.KLANKS_GAUNTLETS.id(), 1);
								npcsay(player, n, "there you go..i hope they help");
								say(player, n, "i'll see you around klank");
							} else {
								say(player, n, "oh dear, i haven't enough money");
								npcsay(player, n, "sorry, i can't sell them any cheaper than that");
							}
						}
					} else if (menu == 1) {
						npcsay(player, n, "you too adventurer");
					}
					break;
			}
		}
	}
}
