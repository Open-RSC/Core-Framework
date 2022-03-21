package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public final class Reldo implements TalkNpcTrigger {
	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.RELDO.id();
	}

	@Override
	public void onTalkNpc(final Player player, final Npc npc) {
		if (player.getCache().hasKey("superchiselquest")) {
			int chiselqueststate = player.getCache().getInt("superchiselquest");
			if (chiselqueststate >= 2) {
				superchiselQuestDialogue(player, npc, chiselqueststate);
				return;
			}
		}

		if (player.getCache().hasKey("read_arrav")
			&& player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 1 || player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 2) {
			if (player.getQuestStage(Quests.THE_KNIGHTS_SWORD) != 1) {
				// player doesn't need to know on the Imcando dwarves
				shieldArravDialogue(player, npc);
			} else {
				ArrayList<String> menuOptions = new ArrayList<>();
				menuOptions.add("Do you know where I can find the Phoenix Gang?");
				menuOptions.add("What do you know about the Imcando dwarves?");

				String[] choiceOptions = new String[menuOptions.size()];
				int option = multi(player, npc, false, //do not send over
					menuOptions.toArray(choiceOptions));

				if (option == 0) {
					shieldArravDialogue(player, npc);
				} else if (option == 1) {
					say(player, npc, "What do you know about the Imcando Dwarves?");
					knightsSwordDialogue(player, npc);
				}
			}
			return;
		}

		say(player, npc, "Hello");
		npcsay(player, npc, "Hello stranger");

		ArrayList<String> options = new ArrayList<>();
		if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 0) {
			options.add("I'm in search of a quest");
		}
		options.add("Do you have anything to trade?");
		options.add("What do you do?");
		if (player.getQuestStage(Quests.THE_KNIGHTS_SWORD) == 1) {
			options.add("What do you know about the Imcando dwarves?");
		}
		String[] finalOptions = new String[options.size()];
		int option = multi(player, npc, false, //do not send over
			options.toArray(finalOptions));

		if (option == 3) {
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 0
				&& player.getQuestStage(Quests.THE_KNIGHTS_SWORD) == 1) {
				say(player, npc, "What do you know about the Imcando Dwarves?");
				knightsSwordDialogue(player, npc);
			}
		}

		else if (option == 2) {
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 0) {
				say(player, npc, "What do you do?");
				npcsay(player, npc, "I'm the palace librarian");
				say(player, npc, "Ah that's why you're in the library then");
				npcsay(player, npc, "Yes",
					"Though I might be in here even if I didn't work here",
					"I like reading");
			}
			else if (player.getQuestStage(Quests.THE_KNIGHTS_SWORD) == 1) {
				say(player, npc, "What do you know about the Imcando Dwarves?");
				knightsSwordDialogue(player, npc);
			}
		}

		else if (option == 1) {
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 0) {
				say(player, npc, "Do you have anything to trade?");
				npcsay(player, npc, "No, sorry. I'm not the trading type");
				say(player, npc, "ah well");
			}
			else {
				say(player, npc, "What do you do?");
				npcsay(player, npc, "I'm the palace librarian");
				say(player, npc, "Ah that's why you're in the library then");
				npcsay(player, npc, "Yes",
					"Though I might be in here even if I didn't work here",
					"I like reading");
			}
		}

		else if (option == 0) {
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 0) {
				say(player, npc, "I'm in search of a quest");
				shieldOfArravStartDialogue(player, npc);
			}

			else {
				say(player, npc, "Do you have anything to trade?");
				npcsay(player, npc, "No, sorry. I'm not the trading type");
				say(player, npc, "ah well");
			}
		}
	}

	public static void superchiselQuestDialogue(Player player, Npc npc, int state) {
		switch (state) {
			case 2:
				say(player, npc, "Hey Reldo, it's me, " + player.getUsername() + ",", "the one talking to you through the superchisel earlier.");
				npcsay(player, npc, "Right, the superchisel!", "I'm definitely excited to examine that artifact", "Could I please see it?");
				int choice = multi(player, npc, "I think I'd rather hold onto it for now", "Sure, that's why I'm here");
				if (choice == 0) {
					say(player, npc, "I really just, feel the need to keep twiddling it...");
					npcsay(player, npc, "Bizarre. It seems to have an entrancing effect", "on those who come in contact with it...");
					delay(1);
					npcsay(player, npc, "I hope you'll reconsider. I need it for my research.");
				} else if (choice == 1) {
					npcsay(player, npc, "Excellent!");
					if (!(player.getCarriedItems().remove(new Item(ItemId.SUPERCHISEL.id())) > -1)) {
						npcsay(player, npc, "Erm. Actually do you not have it?");
						say(player, npc, "Uhhm... I'll try to find it again... one minute", "I'm sure it can't have gone far");
						npcsay(player, npc, "That would be really irresponsible to have lost it.");
						say(player, npc, "No, I'm sure it's somewhere...");
						return;
					}
					npcsay(player, npc, "Hmmm...");
					delay(2);
					npcsay(player, npc, "This is a real mystery.", "and... I really feel the need to...", "twiddle it", "for some reason...");
					delay(2);
					npcsay(player, npc, "Please give me some time to research this.");
					player.getCache().set("superchiselquest", 3);
				}
				break;
			case 3:
				say(player, npc, "Did you find anything out yet?");
				mes("Reldo is busy twiddling the superchisel");
				player.getCache().set("superchiselquest", 4);
				break;
			case 4:
				say(player, npc, "Reldo?");
				npcsay(player, npc, "Ah, " + player.getUsername() + ",", "I've made some discoveries regarding the superchisel.");
				say(player, npc, "Great!");
				npcsay(player, npc,
					"It seems to date back to the First Age",
					"Around the time that mankind was first learning",
					"how to make jewelry out of gold.");
				delay(3);
				npcsay(player, npc,
					"It's enchanted with some kind of Reality bending magicks",
					"Which when combined with the Body Essence of Man");
				delay();
				npcsay(player, npc,
					"through a \"twiddling\" action,",
					"lets the user access powers that until now",
					"I did not know were possible.");
				int powerchoice = -2;
				while (powerchoice != -1 && powerchoice != 2) {
					powerchoice = multi(player, npc, "What kind of powers?", "Reality bending magicks?", "Errr, wow!!! So can I have it back then?");
					switch (powerchoice) {
						case 0:
							npcsay(player, npc, "Well, truthfully,",
								"I'm not entirely sure.");
							mes("Reldo looks sheepish");
							delay(4);
							npcsay(player, npc, "It seems that many of the powers I tried",
								"whispered back a kind of,",
								"ominous message in my ear",
								"\"" + config().BAD_SYNTAX_PREFIX,
								"@whi@Only administrators can use this command@yel@\"");
							delay(2);
							npcsay(player, npc,
								"Which, I'm not exactly sure what that means",
								"Or how to become an \"administrator\"",
								"But from some of the thoughts",
								"panging in my head as I twiddled",
								"I felt that in the right hands,",
								"That of an \"administrator\",");
							delay(2);
							npcsay(player, npc,
								"This could be a truly terrifying object of power.");
							break;
						case 1:
							npcsay(player, npc, "Yes, Reality bending.",
								"In one of my many chats with Traiborn the wizard",
								"We were discussing old mythology from the First Age",
								"And the topic of \"Reality-runes\" arose.",
								"I believe this object bears the signature of that type of power.");
							delay(3);
							npcsay(player, npc, "Until now, I had not been aware of any surviving evidence",
								"that Reality bending magicks ever really existed.",
								"It has only been observed in historical accounts, myths.");
							break;
						case 2:
							mes("Reldo hesitates a moment");
							delay(4);
							npcsay(player, npc, "Yes, I suppose it is yours.",
								"Please take good care of this artifact.");
							if (player.hasElevatedPriveledges()) {
								give(player, ItemId.SUPERCHISEL.id(), 1);
							}
							say(player, npc, "And about that census information...?");
							npcsay(player, npc, "Yes, about that.",
								"This is to be kept strictly between you and me");
							say(player, npc, "Naturally.");
							npcsay(player, npc,
								"For the past several decades now,",
								"the King has ordered a census to be conducted",
								"of all citizens living in Varrock.",
								"This is public knowledge.",
								"However recently, he has also asked that I conduct a census",
								"in kingdoms outside of Varrock",
								"To keep tabs on our neighbours.");
							delay();
							say(player, npc, "That sounds like an awful lot of work");
							npcsay(player, npc,
								"Yes. It has been monumental task for me",
								"Requiring a lot of time and effort",
								"But actually, thanks to you,",
								"and with some help from Traiborn,",
								"We've been able to reverse-engineer",
								"some of the Reality bending magicks",
								"behind the Super chisel.");
							int youdidwhat = multi(player, npc, "You did what?!", "How does that help collect census information?");
							if (youdidwhat == 0) {
								npcsay(player, npc, "You'll have to forgive me for taking it to him without asking...!",
									"Traiborn is an odd one,",
									"but a very knowledgable and close friend of mine",
									"Who we at the Royal Palace trust completely.");
								delay(2);
								npcsay(player, npc,
									"Afterall, if he weren't trustworthy,",
									"would we really entrust him in the care of one of the keys",
									"to the chest containing the Legendary Silverlight?");
								say(player, npc, "Right, okay,",
									"I suppose that's fine then.",
									"But how does reverse-engineering the superchisel",
									"help to collect census information?");
							}
							npcsay(player, npc, "As it turns out,",
								"though many of its powers seemed to be",
								"gated behind some mysterious",
								"\"administrator\" identity,",
								"There were many powers",
								"that were still accessible to us",
								"Including an @gre@::onlinelist@yel@ option",
								"Which lists the names of all players currently online.",
								"This invisible method of census collection",
								"and immediate real-time results",
								"are an amazing power",
								"that should not fall into the wrong hands.");
							delay(2);
							npcsay(player, npc,
								"I was only going to be able provide",
								"what I've been able to collect slowly over years",
								"But now, if you do decide to contact me",
								"I'll pass along those real-time results",
								"at no inconvenience to you");
							delay(2);
							player.getCache().set("superchiselquest", -1);
							mes("Well done.You have completed the superchisel quest");
							if (config().INFLUENCE_INSTEAD_QP) {
								player.message("@gre@You just advanced 0 influence level!");
							} else {
								player.message("@gre@You haved gained 0 quest point!");
							}
					}
				}
				break;
		}
	}

	private void knightsSwordDialogue(Player player, Npc npc) {
		npcsay(player,
			npc,
			"The Imcando Dwarves, you say?",
			"They were the world's most skilled smiths about a hundred years ago",
			"They used secret knowledge",
			"Which they passed down from generation to generation",
			"Unfortunatly about a century ago the once thriving race",
			"Was wiped out during the barbarian invasions of that time");
		say(player, npc, "So are there any Imcando left at all?");
		npcsay(player,
			npc,
			"A few of them survived",
			"But with the bulk of their population destroyed",
			"Their numbers have dwindled even further",
			"Last I knew there were a couple living in Asgarnia",
			"Near the cliffs on the Asgarnian southern peninsula",
			"They tend to keep to themselves",
			"They don't tend to tell people that they're the descendants of the Imcando",
			"Which is why people think that the tribe has died out totally",
			"you may have more luck talking to them if you bring them some red berry pie",
			"They really like red berry pie");
		player.updateQuestStage(Quests.THE_KNIGHTS_SWORD, 2);
	}

	private void shieldArravDialogue(Player player, Npc npc) {
		say(player, npc, "OK I've read the book",
			"Do you know where I can find the Phoenix Gang");
		npcsay(player, npc, "No I don't",
			"I think I know someone who will though",
			"Talk to Baraek, the fur trader in the market place",
			"I've heard he has connections with the Phoenix Gang");
		say(player, npc, "Thanks, I'll try that");
		if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 1) {
			player.updateQuestStage(Quests.SHIELD_OF_ARRAV, 2);
		}
	}

	private void shieldOfArravStartDialogue(Player player, Npc npc) {
		npcsay(player, npc, "I don't think there's any here");
		delay();
		npcsay(player, npc, "Let me think actually",
			"If you look in a book",
			"called the shield of Arrav",
			"You'll find a quest in there",
			"I'm not sure where the book is mind you",
			"I'm sure it's somewhere in here");
		say(player, npc, "Thankyou");
		player.updateQuestStage(Quests.SHIELD_OF_ARRAV, 1);
	}
}
