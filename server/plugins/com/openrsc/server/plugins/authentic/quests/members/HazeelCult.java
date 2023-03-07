package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.NpcInteraction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.openrsc.server.plugins.Functions.*;

public class HazeelCult implements QuestInterface, TalkNpcTrigger, KillNpcTrigger, OpLocTrigger, UseLocTrigger {

	private static final int BUTLERS_CUPBOARD_OPEN = 441;
	private static final int BUTLERS_CUPBOARD_CLOSED = 440;
	private static final int BASEMENT_CRATE = 439;
	private static final int TOP_LEVEL_BOOKCASE = 436;
	private static final int CARNILLEAN_CHEST_OPEN = 437;
	private static final int CARNILLEAN_CHEST_CLOSED = 438;

	@Override
	public int getQuestId() {
		return Quests.THE_HAZEEL_CULT;
	}

	@Override
	public String getQuestName() {
		return "The Hazeel Cult (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.THE_HAZEEL_CULT.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		final QuestReward reward = Quest.THE_HAZEEL_CULT.reward();
		if (player.getCache().hasKey("good_side")) {
			player.message("Well done you have completed the Hazeel cult quest");
			for (XPReward xpReward : reward.getXpRewards()) {
				incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
			}
			incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
			player.message("ceril gives you 2000 gold coins");
			give(player, ItemId.COINS.id(), 2000);
		} else if (player.getCache().hasKey("evil_side")) {
			player.message("Hazeel gives you some coins");
			give(player, ItemId.COINS.id(), 2000);
			for (XPReward xpReward : reward.getXpRewards()) {
				incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
			}
			incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
			player.message("you have completed the hazeel cult quest");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.CLAUS.id(), NpcId.CERIL.id(), NpcId.BUTLER.id(), NpcId.HENRYETA.id(), NpcId.PHILIPE.id(),
				NpcId.CARNILLEAN_GUARD.id(), NpcId.CLIVET.id(), NpcId.CULT_MEMBER.id(), NpcId.ALOMONE.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.CERIL.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello there");
					npcsay(player, n, "blooming, thieving, wierdos",
						"why don't they leave me alone?");
					int menu = multi(player, n, false, //do not send over
						"What's wrong?",
						"You probably deserve it",
						"You seem uptight, I'll leave you alone");
					if (menu == 0) {
						say(player, n, "What's wrong?");
						npcsay(player, n, "it's those strange folk from the forest",
							"those freaks keep breaking into my house");
						say(player, n, "have they taken much?");
						npcsay(player, n, "they first broke in months ago and stole a suit of armour",
							"the strange thing is that they've broken in four times since",
							"but took nothing");
						say(player, n, "and you are...?");
						npcsay(player, n, "why, i'm ceril carnillean",
							"we really are quite a famous bloodline",
							"we've played a large part in ardounge pollitics for generations",
							"maybe you could help retrieve the armour?",
							"of course there would be a handsom cash reward for yourself");
						int option = multi(player, n, false, //do not send over
							"No thanks i've got plans",
							"yes, off course,i'd be happy to help");
						if (option == 0) {
							say(player, n, "no thanks i've got plans");
							npcsay(player, n, "no wonder i'm the one with the big house and you're on the streets");
						} else if (option == 1) {
							say(player, n, "yes of course, i'd be happy to help");
							npcsay(player, n, "that's very kind of you",
								"I caught a glimpse of the thieves leaving",
								"but due to ermm... my cold... I was unable to give chase",
								"they were dressed all in black",
								"I think they may have belonged to some sort of cult");
							say(player, n, "do you know where they are?");
							npcsay(player, n, "my old butler once followed them",
								"to a cave entrance in the forest south of here",
								"unfortunately the next night he died in his sleep");
							say(player, n, "that's awful");
							npcsay(player, n, "it's ok, a replacement arrived the next day",
								"he's been great, cooks an excellent broth");
							say(player, n, "ok ceril, i'll see what i can do");
							player.updateQuestStage(this, 1);
						}
					} else if (menu == 1) {
						say(player, n, "you probably deserve it");
						npcsay(player, n, "who are you to judge me?",
							"hmmm, you look like a peasant",
							"i'm wasting my time talking to you");
					} else if (menu == 2) {
						say(player, n, "you seem uptight,i'll leave you alone");
						npcsay(player, n, "yes, i doubt you could help");
					}
					break;
				case 1:
				case 2:
					say(player, n, "hello ceril");
					npcsay(player, n, "it's sir ceril to you",
						"and shouldn't you be out recovering my suit of armour?");
					break;
				case 3:
					npcsay(player, n, "have you had any luck yet?");
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello ceril, i've discovered the hideout");
						npcsay(player, n, "well done... and the armour?");
						say(player, n, "i'm afraid not",
							"i spoke to a cult member in the entrance of the cave",
							"but he escaped into the sewer systems",
							"seems they have a grievance with your family",
							"something to do with some bloke called hazeel");
						npcsay(player, n, "err errmm... no",
							"They're obviously all mad",
							"just find them and bring back the armour");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "i'm afraid not ceril");
						npcsay(player, n, "well that's strange",
							"the butler seemed quite sure about their location");
						return;
					}
					break;
				case 4:
					if (player.getCache().hasKey("good_side")) {
						if (player.getCarriedItems().hasCatalogID(ItemId.CARNILLEAN_ARMOUR.id(), Optional.of(false))) {
							say(player, n, "ceril, how are you?",
								"Look, I've found the armour");
							npcsay(player, n, "well done i must say i am impressed");
							mes("you give ceril the family armour");
							delay(3);
							player.getCarriedItems().remove(new Item(ItemId.CARNILLEAN_ARMOUR.id()));
							npcsay(player, n, "before we send you on your way",
								"i'll get our butler jones",
								"to whip you up some of his special broth");
							say(player, n, "i'd rather not",
								"i overheard the cult members talking",
								"the buttler is really working for them");
							Npc ceril;
							if (Formulae.getHeight(n.getLocation()) == 0) {
								// ceril of ground floor
								npcsay(player, n, "that's it, come with me",
									"we'll sort this out once and for all");
								mes("you follow ceril up to butler Jones' room");
								delay(3);
								player.teleport(613, 1562);
								ceril = ifnearvisnpc(player, NpcId.CERIL.id(), 10);
							} else {
								// ceril of 1st floor
								// unknown from OG RSC but on OSRS doesnt move locations
								// and excluding the upstairs part, has same dialogue
								npcsay(player, n, "that's it",
									"we'll sort this out once and for all");
								ceril = n;
							}
							if (ceril != null) {
								mes("ceril speaks briefly with Jones");
								delay(3);
								npcsay(player, ceril, "Well, he assures me that he's a loyal hard working man",
									"I cannot fathom, why you would believe he is a spy");
								say(player, ceril, "surely you won't take his word for it?");
								npcsay(player, ceril, "we have also decided that due to the humilliation you have caused",
									"it is only fair that Jones shall recieve your reward",
									"you shall recieve payment more suited to your low life personality");
								mes("ceril gives you 5 gold coins");
								delay(3);
								give(player, ItemId.COINS.id(), 5);
								mes("ceril gives jones 695 gold coins");
								delay(3);
								npcsay(player, ceril, "now take it and leave");
								mes("butler Jones has a slight grin");
								delay(3);
								mes("You're going to need more than just your word");
								delay(3);
								mes("To prove Jones' treachary");
								delay(3);
								player.updateQuestStage(this, 5);
							}
						} else {
							say(player, n, "ceril, how are you?");
							npcsay(player, n, "Im ok. Have you found the armour");
							say(player, n, "i'm afraid not");
							npcsay(player, n, "well i'm not paying you to see the sights");
							say(player, n, "okay, i'll go and try and retrieve it for you");
						}
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello again");
						npcsay(player, n, "oh my, the misery, the pain",
							"my son is a good boy but stupid as well",
							"i can't believe he gave his dinner to scruffy",
							"without having the servents check it for poison first",
							"how could he be so careless?");
						say(player, n, "scruffy?");
						npcsay(player, n, "he's been in the family for twenty years the poor dog",
							"what did he ever do to hurt anyone?");
						return;
					}
					break;
				case 5:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "you owe me money");
						npcsay(player, n, "i owe you nothing now go away",
							"before i have jones throw you out");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "ceril, how are you?");
						npcsay(player, n, "I'm devestated",
							"i don't know what to do with myself since i lost scruffy");
						mes("ceril bursts into tears");
						delay(3);
						return;
					}
					break;
				case 6:
					say(player, n, "hello ceril, how are you?");
					npcsay(player, n, "I think the thieves may have been back in the house");
					say(player, n, "why?");
					npcsay(player, n, "i'm not sure but it seem's as if some of my books",
						"have been re-arranged in my study",
						"it's either that or i'm losing my marbles");
					break;
				case -1:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello ceril");
						npcsay(player, n, "well hello there",
							"brave adventurer, it's good to see you again",
							"if it wasn't for you",
							"that butler jones would have poisoned me by now");
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello ceril");
						npcsay(player, n, "i maybe wrong",
							"but ever since i asked for your help",
							"thing's have gone from bad to worse",
							"i think from now on you better keep out of my way");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.BUTLER.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 2:
					say(player, n, "hello there");
					npcsay(player, n, "hello,how are you today?");
					say(player, n, "good thanks and yourself");
					npcsay(player, n, "fine and dandy");
					break;
				case 1:
					say(player, n, "hello, what is this building?");
					npcsay(player, n, "this is the property of Sir Ceril Carnillean",
						"of the noble carnillean family",
						"you're welcome to look around",
						"but i'm afraid i'll have to keep an eye on you",
						"we've been having a real problem with thieves",
						"strange cult folk coming out the forest");
					say(player, n, "that's a shame");
					npcsay(player, n, "yes well these things are bound to happen",
						"when you're as wealthy as the Varnilleans");
					int butMenu = multi(player, n, false, //do not send over
						"Have you any more info on the carnilleans?",
						"How long have you worked here?",
						"Ok then take care");
					if (butMenu == 0) {
						say(player, n, "Have you any more info on the carnilleans?");
						npcsay(player, n, "there's a lot i could tell you",
							"about the carnillean family history",
							"i'm afraid if did speak about such matter's",
							"i would lose my job and that i cannot risk");
					} else if (butMenu == 1) {
						say(player, n, "how long have you worked here?");
						npcsay(player, n, "long enough to know the carnilleans",
							"are not as innocent or noble as they seem");
					} else if (butMenu == 2) {
						say(player, n, "ok then take care");
						npcsay(player, n, "you to");
					}
					break;
				case 3:
					say(player, n, "how long have you worked here?");
					npcsay(player, n, "long enough to know the carnillean's",
						"are not as innocent or noble as they seem");
					break;
				case 4:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "jones i need to talk to you");
						npcsay(player, n, "do you need some help with your quest?");
						say(player, n, "you can stop the act jones",
							"i know you're working for the cult");
						npcsay(player, n, "what? don't be so silly");
						say(player, n, "I overheard the cult leader talking about you");
						npcsay(player, n, "look here,you may think you know something",
							"but really you have no idea");
						say(player, n, "i know once i reveal the truth",
							"you'll be locked up");
						npcsay(player, n, "you think that old fool ceril",
							"will take your word over mine",
							"he completely trust's me");
						say(player, n, "we will have to see about that");
						npcsay(player, n, "i'll warn you once more traveller",
							"don't get involved");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello there");
						npcsay(player, n, "hello friend,i heard about your handy work",
							"quite amusing really",
							"I'm sure hazeel will be pleased with you anyway",
							"keep up the good work");
						return;
					}
					break;
				case 5:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "you fool",
							"did you think you could simply accuse me and save the day?",
							"we've been working on this for years",
							"your interference is only a minor set back to our plans",
							"and when the mighty hazeel does return",
							"the likes of you and the carnilleans will be the first of many to suffer");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						npcsay(player, n, "hello again friend",
							"I see you you have the mark",
							"you should keep that covered up");
						say(player, n, "oh that's just an old family pass down");
						npcsay(player, n, "you don't have to pretend to me friend",
							"our cause is one and the same",
							"the sooner lord hazeel is avenged",
							"the better for us and this city");
						say(player, n, "have you any idea where the sacred script is");
						npcsay(player, n, "no idea i'm afraid",
							"it must be somewhere in the house",
							"but i can't find it for the life of me",
							"i've searched high and low");
						say(player, n, "doesn't ceril get suspisous");
						npcsay(player, n, "that old fool",
							"he can't can't see the forest for the tree's");
						return;
					}
					break;
				case 6:
					say(player, n, "hello jones");
					npcsay(player, n, "have you managed to find the script?");
					if (player.getCarriedItems().hasCatalogID(ItemId.SCRIPT_OF_HAZEEL.id(), Optional.of(false))) {
						say(player, n, "I have it here");
						npcsay(player, n, "incredible, we owe you a lot",
							"you better get it back to our hideout as quick as you can",
							"these our exciting times traveller",
							"once the great hazeel returns",
							"things are going to really change around here");
					} else {
						say(player, n, "i'm afraid i've lost it");
						npcsay(player, n, "how could you be so foolish",
							"the future of our people completly relys on that script",
							"you better find it again quickly");
					}
					break;
				case -1:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello stranger");
						npcsay(player, n, "why hello there");
						say(player, n, "i take it you're the new butler");
						npcsay(player, n, "that's right",
							"i think they had some problems with the last one");
						say(player, n, "you could say that");
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello jones");
						npcsay(player, n, "it's an honour to be in your presence again traveller",
							"I hope things are well");
						say(player, n, "not bad, yourself");
						npcsay(player, n, "i'm good thanks");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.HENRYETA.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
					say(player, n, "hello");
					npcsay(player, n, "oh hello",
						"if you wish to look around the carnillean family home",
						"please refraine from touching anything",
						"with those grubby hands of yours");
					break;
				case 3:
					say(player, n, "hello madam");
					if (player.getCache().hasKey("good_side")) {
						npcsay(player, n, "i hope you've found those awful holigans",
							"I can't sleep at night");
						say(player, n, "i'm working on it madam");
						npcsay(player, n, "i don't know",
							"there really are some strange folk around these parts");
					} else if (player.getCache().hasKey("evil_side")) {
						npcsay(player, n, "i hope you found those awful hooligans",
							"I can't sleep at night");
						say(player, n, "I'm afraid not");
						npcsay(player, n, "you really are useless");
						say(player, n, "thanks a lot");
					}
					break;
				case 4:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "oh, hello there adventurer",
							"i hope you were careful dealing with those nasty men");
						say(player, n, "i was fine, thanks");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello are you ok?");
						npcsay(player, n, "no i'm not ok",
							"those animals slaughtered my precious scruffy",
							"i'll never recover",
							"i'm emotionaly scarred for life");
						say(player, n, "i'm sorry to hear that");
						npcsay(player, n, "don't be sorry it's not your fault",
							"just find those animals and punish them severely",
							"before i get to them first");
						return;
					}
					break;
				case 5:
				case 6:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello henyeta");
						npcsay(player, n, "don't think you can accuse my trusted staff",
							"then be friends with me");
						say(player, n, "what i said about jones is true");
						npcsay(player, n, "don't be so ridiculous",
							"next you'll tell me he murdered our old butler");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello");
						npcsay(player, n, "i'm sorry i'm too depressed to talk to you",
							"poor scruffy...");
						say(player, n, "yeah, poor scruffy!");
						return;
					}
					break;
				case -1:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "hello again adventurer\"",
							"things really have picked up around here",
							"since you dealt with those nasty cult members",
							"good to hear");
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello");
						npcsay(player, n, "i've been instructed by my husband not to talk to you",
							"so go away and leave me alone");
						say(player, n, "charming");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.PHILIPE.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
					say(player, n, "hello there");
					npcsay(player, n, "what have you brought me?",
						"I want some more toys");
					say(player, n, "I'm afraid i don't have any");
					npcsay(player, n, "toys, i want toys");
					break;
				case 3:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "i want more toys");
						say(player, n, "sorry i don't have any");
						npcsay(player, n, "i want sweets, gimme sweets");
						say(player, n, "no sorrry i don't have sweets either");
						npcsay(player, n, "i hate you, i want my mum");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello");
						npcsay(player, n, "i want more toys");
						say(player, n, "sorry i don't have any");
						npcsay(player, n, "i want sweets, gimme sweets");
						say(player, n, "no sorrry i don't have sweets either");
						npcsay(player, n, "i hate you, i want my mum");
						return;
					}
					break;
				case 4:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "mommy said your here to",
							"kill all the nasty men",
							"that come into our house");
						say(player, n, "something like that");
						npcsay(player, n, "can i watch?");
						say(player, n, "no");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello youngster");
						mes("the boy looks very upset");
						delay(3);
						npcsay(player, n, "someone killed scruffy",
							"i liked scruffy",
							"he never told me off");
						say(player, n, "that's unfortunate");
						npcsay(player, n, "i want my mommy");
						return;
					}
					break;
				case 5:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello youngster");
						npcsay(player, n, "daddy say's you dont like Jones",
							"Jones is nice",
							"he brings me toys and sweets");
						say(player, n, "jones is a bad person philipe");
						npcsay(player, n, "you're a bad person",
							"i don't like you");
						say(player, n, "ok");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello");
						npcsay(player, n, "mommy said your here to",
							"kill all the nasty men",
							"that come into our house");
						say(player, n, "something like that");
						npcsay(player, n, "can i watch?");
						say(player, n, "no");
						return;
					}
					break;
				case 6:
					say(player, n, "hello youngster");
					npcsay(player, n, "why are you still here?");
					say(player, n, "just looking around");
					npcsay(player, n, "have you got me some toys?");
					say(player, n, "no");
					npcsay(player, n, "then i don't like you");
					say(player, n, "that's a shame");
					break;
				case -1:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello philipe");
						npcsay(player, n, "i want more toys");
						say(player, n, "sorry i don't have any");
						npcsay(player, n, "i want sweets, gimme sweets");
						say(player, n, "no sorrry",
							"I don't have any sweets either");
						npcsay(player, n, "i hate you,i want my mum");
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello philipe");
						npcsay(player, n, "i want more toys");
						say(player, n, "sorry i don't have any");
						npcsay(player, n, "i want sweets, gimme sweets");
						say(player, n, "no sorrry",
							"I don't have any sweets either");
						npcsay(player, n, "i hate you,i want my mum");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.CLAUS.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
					say(player, n, "hello");
					npcsay(player, n, "sorry i can't talk now",
						"you would be amazed how many",
						"meals this family can go through");
					break;
				case 2:
					break;
				case 3:
					say(player, n, "hello");
					npcsay(player, n, "you're that chap they've asked to help get those nasty folk",
						"that keep breaking in");
					say(player, n, "yep, that's me");
					npcsay(player, n, "well i wish the best of luck");
					break;
				case 4:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello there");
						npcsay(player, n, "hello, how are you today");
						say(player, n, "not bad thanks");
						npcsay(player, n, "good good");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						npcsay(player, n, "hello there",
							"caught any thieves yet?");
						say(player, n, "afraid not");
						npcsay(player, n, "keep at it");
						return;
					}
					break;
				case 5:
					if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello there");
						npcsay(player, n, "i don't understand it",
							"how could someone slip poison in my cooking",
							"without me even noticing",
							"I'll be lucky if the carnilleans don't fire me",
							"those animals how could they do it",
							"poor scruffy");
						return;
					} else if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello there");
						npcsay(player, n, "hello, how are you today");
						say(player, n, "not bad thanks");
						npcsay(player, n, "good good");
						return;
					}
					break;
				case 6:
					say(player, n, "hello there");
					npcsay(player, n, "those animals how could they do it",
						"poor scruffy");
					break;
				case -1:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello cook");
						npcsay(player, n, "well hello there traveller",
							"are we fit and well");
						say(player, n, "yes i'm fine");
						npcsay(player, n, "good to hear");
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello cook");
						npcsay(player, n, "get out of my kitchen",
							"can't you tell your not welcome around here");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.CARNILLEAN_GUARD.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
					say(player, n, "hello");
					npcsay(player, n, "hello,i haven't seen you before",
						"if you've come to look at the carnillean family home",
						"just make sure you behave yourself",
						"we've had enough wierdos causing trouble",
						"round here of late");
					break;
				case 3:
					say(player, n, "hello");
					npcsay(player, n, "hi i heard you're after the cult",
						"who broke in the other night",
						"blooming wierdo's");
					break;
				case 4:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "hello brave adventurer",
							"keep up the good work");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello there");
						npcsay(player, n, "oh hello, did you hear?",
							"the cult members have been back",
							"I don't know what they've done",
							"but ceril is really upset");
						return;
					}
					break;
				case 5:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "hello adventurer",
							"i heard you've accused butler jones",
							"of being involved with the cult",
							"that's right",
							"to be honest i haven't",
							"trusted him since he turned up here",
							"a day after the old butler died",
							"it seems too much of a coincidence to me");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello");
						npcsay(player, n, "hello adventurer",
							"you're still hanging around then");
						return;
					}
					break;
				case 6:
					say(player, n, "hello guard");
					npcsay(player, n, "hello there",
						"i hope you find the cult soon",
						"we think there may have been another burglary");
					say(player, n, "that's worrying");
					npcsay(player, n, "i just don't know how they do it",
						"it seems like they're right under our noses");
					break;
				case -1:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "well if it isn't our own local hero",
							"it's good to see you in these parts again");
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello");
						npcsay(player, n, "you again",
							"didn't i tell you you're not welcome around here",
							"now leave before we have to get rough with you");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.CLIVET.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello there");
					npcsay(player, n, "what do you want traveller");
					say(player, n, "just passing by");
					npcsay(player, n, "you have no business here",
						"leave...now");
					break;
				case 1:
					say(player, n, "do you know the carnilleans?");
					npcsay(player, n, "i'll mind my business you mind yours");
					say(player, n, "look i know you're hiding something",
						"i've heard there's a cult hideout down here");
					npcsay(player, n, "if you know what's best for you you'll leave now");
					say(player, n, "i have my orders");
					npcsay(player, n, "so that two faced cold hearted snob has got to you too has he?");
					say(player, n, "ceril carnillean is a decent man");
					npcsay(player, n, "there's a lot more than meets the eye to the carnilleans",
						"and none of it's decent");
					player.updateQuestStage(this, 2);
					int menu = multi(player, n, false, //do not send over
						"What do you mean?",
						"I've heard enough of your rubbish");
					if (menu == 0) {
						say(player, n, "what do you mean?");
						npcsay(player, n, "the carnillean family house does not belong to them",
							"it's original owner was lord hazeel",
							"hazeel was one of the mahjarrat followers of zamorak",
							"The carnilleans harassed hazeel and his family for decades",
							"then one night they stormed hazeel's home",
							"one by one they tortured and then butchered him and his family",
							"the next day the carnillean forefathers moved into the property",
							"they've lived there on hazeel's wealth ever since");
						say(player, n, "ardounge history and pollitics are not my concern",
							"i've been asked to do a job and i plan to carry it through");
						npcsay(player, n, "well now i'm asking you to do a job",
							"hazeel is going to return my friend",
							"those who aid his journey will gain rewards",
							"help us avenge hazeel's spirit so he may return");
						int chooseSideMenu = multi(player, n, false, //do not send over
							"You're crazy, i'd never help you",
							"So what would i have to do?");
						if (chooseSideMenu == 0) {
							//GOOD SIDE;
							say(player, n, "You're crazy, i'd never help you");
							npcsay(player, n, "then you're a fool",
								"go back to your adventures traveller");
							mes("clivet boards the raft and pushes of down the sewer system");
							delay(3);
							n.remove();
							mes("you hear him call out");
							delay(3);
							mes("@yel@clivet:you'll never find us...");
							delay(3);
							//SET GOOD SIDE;
							player.updateQuestStage(this, 3);
							player.getCache().store("good_side", true);
						} else if (chooseSideMenu == 1) {
							//EVIL SIDE;
							say(player, n, "so what would i have to do?");
							npcsay(player, n, "first you must prove your loyalty to the cause",
								"you must kill one of the carnillean family members",
								"then we will know who's side you're really on",
								"so will you do it?");
							int whichSideMenu = multi(player, n, false, //do not send over
								"No i won't do it",
								"Ok i'll do it");
							if (whichSideMenu == 0) {
								say(player, n, "no i won't do it");
								npcsay(player, n, "then you're a fool",
									"go back to your adventures traveller");
								mes("clivet boards the raft and pushes of down the sewer system");
								delay(3);
								n.remove();
								mes("you hear him call out");
								delay(3);
								mes("@yel@clivet:you'll never find us...");
								delay(3);
								//SET GOOD SIDE;
								player.updateQuestStage(this, 3);
								player.getCache().store("good_side", true);
							} else if (whichSideMenu == 1) {
								say(player, n, "ok, i'll do it");
								npcsay(player, n, "good, few see through the carnillean lies",
									"but i guessed you were of stronger character",
									"here take this poison, pour it into one of their meals",
									"once the deed is done return here");
								give(player, ItemId.POISON.id(), 1);
								player.updateQuestStage(this, 3);
								player.getCache().store("evil_side", true);
							}
						}
					} else if (menu == 1) {
						say(player, n, "I've heard enough of your rubbish");
						npcsay(player, n, "then leave, fool");
					}
					break;
				case 2:
					say(player, n, "hello");
					npcsay(player, n, "so you've returned\"",
						"now do you want to know the truth about the carnilleans?");
					int menu2 = multi(player, n, false, //do not send over
						"What do you mean?", "I've heard enough of your rubbish");
					if (menu2 == 0) {
						say(player, n, "what do you mean?");
						npcsay(player, n, "the carnillean family house does not belong to them",
							"it's original owner was lord hazeel",
							"hazeel was one of the mahjarrat followers of zamorak",
							"The carnilleans harassed hazeel and his family for decades",
							"then one night they stormed hazeel's home",
							"one by one they tortured and then butchered him and his family",
							"the next day the carnillean forefathers moved into the property",
							"they've lived there on hazeel's wealth ever since");
						say(player, n, "ardounge history and pollitics are not my concern",
							"i've been asked to do a job and i plan to carry it through");
						npcsay(player, n, "well now i'm asking you to do a job",
							"hazeel is going to return my friend",
							"those who aid his journey will gain rewards",
							"help us avenge hazeel's spirit so he may return");
						int chooseSideMenu = multi(player, n, false, //do not send over
							"You're crazy, i'd never help you",
							"So what would i have to do?");
						if (chooseSideMenu == 0) {
							//GOOD SIDE;
							say(player, n, "You're crazy, i'd never help you");
							npcsay(player, n, "then you're a fool",
								"go back to your adventures traveller");
							mes("the man jumps onto a small raft");
							delay(3);
							mes("and pushes off down the sewer system");
							delay(3);
							n.remove();
							mes("you hear him call out");
							delay(3);
							mes("@yel@clivet:you'll never find us...");
							delay(3);
							//SET GOOD SIDE;
							player.updateQuestStage(this, 3);
							player.getCache().store("good_side", true);
						} else if (chooseSideMenu == 1) {
							//EVIL SIDE;
							say(player, n, "so what would i have to do?");
							npcsay(player, n, "first you must prove your loyalty to the cause",
								"you must kill one of the carnillean family members",
								"then we will know who's side you're really on",
								"so will you do it?");
							int whichSideMenu = multi(player, n, false, //do not send over
								"No i won't do it",
								"Ok i'll do it");
							if (whichSideMenu == 0) {
								say(player, n, "no i won't do it");
								npcsay(player, n, "then you're a fool",
									"go back to your adventures traveller");
								mes("clivet boards the raft and pushes of down the sewer system");
								delay(3);
								n.remove();
								mes("you hear him call out");
								delay(3);
								mes("@yel@clivet:you'll never find us...");
								delay(3);
								//SET GOOD SIDE;
								player.updateQuestStage(this, 3);
								player.getCache().store("good_side", true);
							} else if (whichSideMenu == 1) {
								say(player, n, "ok, i'll do it");
								npcsay(player, n, "good, few see through the carnillean lies",
									"but i guessed you were of stronger character",
									"here take this poison, pour it into one of their meals",
									"once the deed is done return here");
								give(player, ItemId.POISON.id(), 1);
								player.updateQuestStage(this, 3);
								player.getCache().store("evil_side", true);
							}
						}
					} else if (menu2 == 1) {
						say(player, n, "I've heard enough of your rubbish");
						npcsay(player, n, "then leave, fool");
					}
					break;
				case 3:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello there");
						npcsay(player, n, "oh not you again");
						say(player, n, "where is the cult hideout?");
						npcsay(player, n, "you're a fool if you think you'll ever find it",
							"soon hazeel will return and you'll be punished");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello there");
						npcsay(player, n, "traveller you have a mission",
							"go to the carnillean house and poison their meal");
						return;
					}
					break;
				case 4:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "You again! I warned you to keep away",
							"hazeel will punish you for your interference");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						if (!player.getCarriedItems().hasCatalogID(ItemId.MARK_OF_HAZEEL.id(), Optional.of(false))) {
							say(player, n, "hello",
								"I poured the poison into the carnillean's meal as requested");
							npcsay(player, n, "yes we have people on the inside who informed me of your deed",
								"hazeel will reward you for your loyalty");
							say(player, n, "ok, so what's next?");
							npcsay(player, n, "first you must wear the sign of hazeel");
							mes("clivet hands you a small metal amulet");
							delay(3);
							give(player, ItemId.MARK_OF_HAZEEL.id(), 1);
							npcsay(player, n, "the amulet is proof to other cult members that you're one of us",
								"it is also the key to finding the cult hideout");
							say(player, n, "in what way?");
							npcsay(player, n, "the flow of the sewer's are controlled by 5 sewer valves above",
								"turn them correctly and the sewer will carry you to the hideout",
								"the sign of hazeel is your guide - you must begin at the tail",
								"The cult leader alomone shall be expecting you");
						} else {
							say(player, n, "hello");
							npcsay(player, n, "hello traveller",
								"have you found the cult hideout yet?");
							say(player, n, "not yet im afraid");
							npcsay(player, n, "hurry! soon hazeel will return");
						}
						return;
					}
					break;
				case 5:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "You again! I warned you to keep away",
							"hazeel will punish you for your interference");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello");
						npcsay(player, n, "hello traveller",
							"all we need now is the sacred script of hazeel",
							"once we have that Hazeel can return");
						return;
					}
					break;
				case 6:
					say(player, n, "hello again");
					npcsay(player, n, "have you managed to find the script of hazeel?");
					if (player.getCarriedItems().hasCatalogID(ItemId.SCRIPT_OF_HAZEEL.id(), Optional.of(false))) {
						say(player, n, "yes, i found it in the house");
						npcsay(player, n, "amazing, the last piece",
							"now the time has come to change history and avenge lord hazeel",
							"take the script to alomone as quick as you can");
					} else {
						say(player, n, "errm, no, i misplaced it");
						npcsay(player, n, "go to the house and don't return until you have the script");
					}
					break;
				case -1:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "You again! I warned you to keep away",
							"bother someone else",
							"go find some goblins to hack up");
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello");
						npcsay(player, n, "It's good to see you again",
							"i am patiently waiting for for hazeel to call upon me");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.CULT_MEMBER.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
					say(player, n, "hello");
					npcsay(player, n, "what how did you get in here?",
						"leave now traveller");
					break;
				case 3:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello there");
						npcsay(player, n, "what, how did you get in here",
							"leave now traveller");
						n.setChasing(player);
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello there");
						npcsay(player, n, "can't you see i'm busy",
							"the great hazeel shall return soon");

						return;
					}
					break;
				case 4:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "what, an outsider",
							"how did you get in here",
							"you must leave, now");
						n.setChasing(player);
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hi");
						npcsay(player, n, "hello, oh, are you new");
						say(player, n, "that's right");
						npcsay(player, n, "well it's good to have you on board",
							"soon we should retrieved the sacred hazeel script",
							"then at last we can bring are lord back from the dead");
						return;
					}
					break;
				case 5:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "what, an outsider",
							"how did you get in here",
							"you must leave, now");
						n.setChasing(player);
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello");
						npcsay(player, n, "hello there",
							"untill we have the hazeel script",
							"we cannot summon our master");
						return;
					}
					break;
				case 6:
					say(player, n, "hello");
					npcsay(player, n, "you truly are our savior",
						"you found the script of hazeel",
						"now his injustice can be resolved");
					break;
				case -1:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "An outsider!",
							"how did you get in here?",
							"leave now fool or die");
						n.setChasing(player);
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello");
						npcsay(player, n, "the traveller returns",
							"we are forever in your dept brave adventurer",
							"i bow before you");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.ALOMONE.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
					say(player, n, "hello");
					npcsay(player, n, "what, an intruder",
						"kill him");
					Npc cults = ifnearvisnpc(player, NpcId.CULT_MEMBER.id(), 20);
					if (cults != null) {
						cults.setChasing(player);
					}
					break;
				case 3:
					if (player.getCache().hasKey("good_side")) {
						npcsay(player, n, "How did get you get in here?");
						say(player, n, "I've come for the carnillean family armour");
						npcsay(player, n, "I thought I told the butler to get rid of you",
							"he must be going soft");
						say(player, n, "so the butler is working for you too?",
							"Why's it always the Butler? I should have guessed");
						delay(3);
						n.setChasing(player);
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello");
						npcsay(player, n, "Can't you see I'm busy?");
						return;
					}
					break;
				case 4:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello alomone");
						npcsay(player, n, "out of my way",
							"can't you see we're busy here?");
						n.setChasing(player);
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hi there");
						npcsay(player, n, "well well, we have a new recruit",
							"Clivet told me about your willingness to prove yourself",
							"we must retrieve the sacred script of hazeel",
							"From the Carnillean house",
							"an ancient spell which if read over Hazeel's grave",
							"will bring him back to this world",
							"the Carnilleans aren't aware of it's existence",
							"we have eyes in the house",
							"Butler Jones is one of us",
							"go back to the house and try to find the script");
						player.updateQuestStage(this, 5);
						return;
					}
					break;
				case 5:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello");
						npcsay(player, n, "out of my way",
							"can't you see we're busy here?");
						return;
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello alomone");
						npcsay(player, n, "hazeel has waited long enough traveller",
							"the sooner you find the hazeel script the better");
						return;
					}
					break;
				case 6:
					/** COMPLETE EVIL SIDE **/
					say(player, n, "hello");
					npcsay(player, n, "Do you have the sacred script of hazeel?");
					if (player.getCarriedItems().hasCatalogID(ItemId.SCRIPT_OF_HAZEEL.id(), Optional.of(false))) {
						say(player, n, "yes I have it here");
						npcsay(player, n, "finally our lord hazeel can return");
						mes("alomone takes the hazeel script");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.SCRIPT_OF_HAZEEL.id()));
						npcsay(player, n, "with these words our lord will return and save us all",
							"come with me adventurer and let the ceromony begin");
						player.teleport(580, 3419);
						n.teleport(580, 3419);
						npcsay(player, n, "I do this for you lord hazeel and all followers of zamorak");
						mes("alomone kneels down infront of the shrine");
						delay(3);
						mes("he begins to read the script");
						delay(3);
						player.message("the language is something you have never heard");
						mes("alomone reads on");
						delay(3);
						mes("Alomone finishes the script");
						delay(3);
						mes("the room is silent");
						delay(3);
						mes("suddenly a shrill scream comes from the coffin of hazeel");
						delay(3);
						mes("A shadowy figure appears");
						delay(3);
						Npc lord_hazeel = addnpc(player.getWorld(), NpcId.LORD_HAZEEL.id(), 580, 3420, (int)TimeUnit.SECONDS.toMillis(120));
						ActionSender.sendTeleBubble(player, 580,
							3420, true);
						for (Player pe : player.getViewArea().getPlayersInView()) {
							ActionSender.sendTeleBubble(pe, 580,
								3420, true);
						}
						NpcInteraction interaction = NpcInteraction.NPC_TALK_TO;
						NpcInteraction.setInteractions(lord_hazeel, player, interaction);
						mes("the cult begin to chant");
						delay(3);
						npcsay(player, lord_hazeel, "my followers i am proud of you all",
							"I never expected to retun to these lands",
							"I can see I have much to attend to",
							"In due time you will all be rewarded for your part",
							"brave adventurer, i believe your contribution was the most critical",
							"i owe you much, you may not be a follower of the great zamorak",
							"but you understand injustice and anger",
							"for this I certainly shall call upon your help in the future",
							"my people gain strength day to day",
							"you would be wise to join us while you can");
						say(player, lord_hazeel, "I fight for myself");
						npcsay(player, lord_hazeel, "hmm, fair enough for now",
							"I shall reward you with money",
							"But your reward of Zamorak's approval is far greater");
						player.sendQuestComplete(Quests.THE_HAZEEL_CULT);
						npcsay(player, lord_hazeel, "now i must leave you",
							"i have much business to attend to with my brothers in the north",
							"i will see you all again but be aware",
							"soon much blood will be spilt over runescape");
						if (lord_hazeel != null) {
							lord_hazeel.remove();
						}
						ActionSender.sendTeleBubble(player, 580,
							3420, true);
						for (Player pe : player.getViewArea().getPlayersInView()) {
							ActionSender.sendTeleBubble(pe, 580,
								3420, true);
						}
					} else {
						say(player, n, "i'm afraid not");
						npcsay(player, n, "we need the script if hazeel is to return");
					}
					break;
				case -1:
					if (player.getCache().hasKey("good_side")) {
						say(player, n, "hello again");
						npcsay(player, n, "leave here now intruder",
							"before i loose my patience");
					} else if (player.getCache().hasKey("evil_side")) {
						say(player, n, "hello again");
						npcsay(player, n, "we wait patiently for lord hazeel's calling");
						say(player, n, "ok, take care");
					}
					break;
			}
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.ALOMONE.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.ALOMONE.id()) {
			if (player.getCache().hasKey("good_side")) {
				if (!player.getCarriedItems().hasCatalogID(ItemId.CARNILLEAN_ARMOUR.id(), Optional.empty())) {
					mes("you have killed alomone");
					delay(3);
					mes("lying behind his corpse");
					delay(3);
					mes("you see the carnillean family armour");
					delay(3);
					mes("you place it in your bag");
					delay(3);
					give(player, ItemId.CARNILLEAN_ARMOUR.id(), 1);
					if (player.getQuestStage(this) == 3) {
						player.updateQuestStage(this, 4);
					}
				}
			}
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return DataConversions.inArray(new int[] {BUTLERS_CUPBOARD_OPEN, BUTLERS_CUPBOARD_CLOSED, BASEMENT_CRATE,
				TOP_LEVEL_BOOKCASE, CARNILLEAN_CHEST_CLOSED}, obj.getID());
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == BUTLERS_CUPBOARD_OPEN || obj.getID() == BUTLERS_CUPBOARD_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, player, BUTLERS_CUPBOARD_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, player, BUTLERS_CUPBOARD_CLOSED);
			} else {
				mes("you search the cupboard");
				delay(3);
				if (player.getQuestStage(this) == 5 && player.getCache().hasKey("good_side")) {
					mes("you find a bottle of poison");
					delay(3);
					mes("and a strange amulet");
					delay(3);
					Npc ceril = ifnearvisnpc(player, NpcId.CERIL.id(), 10);
					if (ceril != null) {
						mes("you pass your finds to ceril");
						delay(3);
						say(player, ceril, "look what i've found?");
						npcsay(player, ceril, "what's this for jones?");
						mes("ceril takes the bottle");
						delay(3);
						npcsay(player, ceril, "i don't believe it, it's poison");
						Npc butler = ifnearvisnpc(player, NpcId.BUTLER.id(), 10);
						npcsay(player, butler, "mr carnillean, it's for the rats",
							"i'm just a loyal servent");
						npcsay(player, ceril, "i've seen this amulet before",
							"the thieves that broke in",
							"one of them  was wearing exactly the same amulet",
							"jones i don't believe it",
							"we trusted you");
						npcsay(player, butler, "that's because you're an old fool ceril",
							"I should have got rid of you and your family weeks ago");
						mes("ceril calls for the guards");
						delay(3);
						npcsay(player, butler, "don't worry ceril",
							"we'll make sure you and your family pay");
						npcsay(player, ceril, "looks like i owe you an apology traveller");
						say(player, ceril, "that's ok, we all make mistakes");
						npcsay(player, ceril, "if it wasn't for you he could have poisoned my whole family",
							"i'm sorry for the way i spoke to you",
							"the least i can do is give you a proper reward");
						player.sendQuestComplete(Quests.THE_HAZEEL_CULT);
						say(player, ceril, "thanks ceril");
						npcsay(player, ceril, "thankyou, you're welcome here any time traveller");
					}
				} else {
					mes("but find nothing");
					delay(3);
				}
			}
		}
		else if (obj.getID() == BASEMENT_CRATE) {
			mes("you search the crate");
			delay(3);
			if (player.getQuestStage(this) == 5 && player.getCache().hasKey("evil_side")) {
				if (!player.getCarriedItems().hasCatalogID(ItemId.CARNILLEAN_KEY.id(), Optional.of(false))) {
					player.message("under the food packages");
					player.message("you find an old rusty key");
					give(player, ItemId.CARNILLEAN_KEY.id(), 1);
				} else {
					player.message("but find nothing");
				}
			} else {
				player.message("but find nothing");
			}
		}
		else if (obj.getID() == TOP_LEVEL_BOOKCASE) {
			mes("you search the book case");
			delay(3);
			if (player.getQuestStage(this) == 5 && player.getCache().hasKey("evil_side")) {
				mes("as you pull out one of the books");
				delay(3);
				mes("the shelves slide to the side");
				delay(3);
				mes("revealing a secret passage");
				delay(3);
				mes("you walk through");
				delay(3);
				player.teleport(614, 2504);
				mes("the passage leads upwards");
				delay(3);
				mes("to an empty room");
				delay(3);
			} else {
				player.message("but find nothing interesting");
			}
		}
		else if (obj.getID() == CARNILLEAN_CHEST_CLOSED) {
			player.message("the chest is locked");
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == CARNILLEAN_CHEST_CLOSED && item.getCatalogId() == ItemId.CARNILLEAN_KEY.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == CARNILLEAN_CHEST_CLOSED && item.getCatalogId() == ItemId.CARNILLEAN_KEY.id()) {
			player.message("you use the key to open");
			player.message("the chest");
			changeloc(obj, CARNILLEAN_CHEST_OPEN, 437);
			player.message("inside the chest you find the sacred script of hazeel");
			give(player, ItemId.SCRIPT_OF_HAZEEL.id(), 1);
			if (player.getQuestStage(this) == 5) {
				player.updateQuestStage(this, 6);
			}
		}
	}
}
