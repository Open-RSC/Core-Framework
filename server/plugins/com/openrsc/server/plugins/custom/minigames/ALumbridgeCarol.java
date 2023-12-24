package com.openrsc.server.plugins.custom.minigames;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;
import java.util.HashMap;

import static com.openrsc.server.plugins.RuneScript.*;

public class ALumbridgeCarol implements OpInvTrigger, TalkNpcTrigger, UseInvTrigger {

	public static final int NOT_STARTED = 0;
	public static final int GHOST_STORY = 1;
	public static final int READ_BOOK = 2;
	public static final int RECEIVED_PARCHMENT = 3;
	public static final int LETTER_DELIVERY = 4;
	public static final int DELIVERED_LETTER = 5;
	public static final int FIND_TRAMP = 6;
	public static final int FOUND_TRAMP = 7;
	public static final int GET_CLOTHES = 8;
	public static final int HELPED_TRAMP = 9;
	public static final int FIND_SHILOP = 10;
	public static final int GET_DAGGER = 11;
	public static final int GET_LONGSWORD = 12;
	public static final int GET_SWORD = 13;
	public static final int HELPED_SHILOP = 14;
	public static final int PARTY_TIME = 15;
	public static final int TAKING_CREDIT = 16;
	public static final int COMPLETED = -1;

	// Colored capes
	private static final String[] capeColors = new String[]{ "yellow", "orange", "green", "purple" };
	private static final int[] capeIds = new int[]{
		ItemId.YELLOW_CAPE.id(),
		ItemId.ORANGE_CAPE.id(),
		ItemId.GREEN_CAPE.id(),
		ItemId.PURPLE_CAPE.id()
	};

	private static final int[] sweaterIds = new int[]{
		ItemId.RED_CHRISTMAS_SWEATER.id(),
		ItemId.YELLOW_CHRISTMAS_SWEATER.id(),
		ItemId.BLUE_CHRISTMAS_SWEATER.id(),
		ItemId.ORANGE_CHRISTMAS_SWEATER.id(),
		ItemId.PURPLE_CHRISTMAS_SWEATER.id(),
		ItemId.GREEN_CHRISTMAS_SWEATER.id(),
		ItemId.FEMALE_RED_CHRISTMAS_SWEATER.id(),
		ItemId.FEMALE_YELLOW_CHRISTMAS_SWEATER.id(),
		ItemId.FEMALE_BLUE_CHRISTMAS_SWEATER.id(),
		ItemId.FEMALE_ORANGE_CHRISTMAS_SWEATER.id(),
		ItemId.FEMALE_PURPLE_CHRISTMAS_SWEATER.id(),
		ItemId.FEMALE_GREEN_CHRISTMAS_SWEATER.id()
	};

	private static final int[] dyeIds = new int[]{
		ItemId.REDDYE.id(),
		ItemId.YELLOWDYE.id(),
		ItemId.BLUEDYE.id(),
		ItemId.ORANGEDYE.id(),
		ItemId.PURPLEDYE.id(),
		ItemId.GREENDYE.id()
	};

	private static final HashMap<Integer, Integer> dyeToSweater = new HashMap<Integer, Integer>() {{
		put(ItemId.REDDYE.id(), ItemId.RED_CHRISTMAS_SWEATER.id());
		put(ItemId.YELLOWDYE.id(), ItemId.YELLOW_CHRISTMAS_SWEATER.id());
		put(ItemId.BLUEDYE.id(), ItemId.BLUE_CHRISTMAS_SWEATER.id());
		put(ItemId.ORANGEDYE.id(), ItemId.ORANGE_CHRISTMAS_SWEATER.id());
		put(ItemId.PURPLEDYE.id(), ItemId.PURPLE_CHRISTMAS_SWEATER.id());
		put(ItemId.GREENDYE.id(), ItemId.GREEN_CHRISTMAS_SWEATER.id());
	}};

	private static final int PARTY_ROOM_MIN_X = 316;
	private static final int PARTY_ROOM_MIN_Y = 1487;
	private static final int PARTY_ROOM_MAX_X = 323;
	private static final int PARTY_ROOM_MAX_Y = 1494;

	public static void dukeDialogue(Player player, Npc npc, String dialogue) {
		int option;
		say(dialogue);
		switch (getStage(player)) {
			case NOT_STARTED:
				npcsay("I haven't",
					"I've been up all night");

				if (multi("What happened?", "That's too bad") != 0) return;

				npcsay("The strangest thing happened",
					"Three spirits came to visit me during the night",
					"I'm not even sure if it was real or a bad dream",
					"Can you help me figure this out?");

				if (multi("Sure, what did they want?", "Spirits? That's too scary for me") != 0) return;

				npcsay("I don't know what they want exactly",
					"But they did show me things",
					"Each ghost had a different vision for me",
					"I wrote down my experiences in this journal",
					"Take a look and talk to me again when you've read it");
				give(ItemId.DUKES_JOURNAL.id(), 1);
				mes("The Duke hands you a journal");
				updateStage(player, GHOST_STORY);
				break;
			case GHOST_STORY:
				npcsay("I thought I gave you my journal to read so we could figure that out?");
				if (!ifheld(ItemId.DUKES_JOURNAL.id(), 1)) {
					npcsay("But it looks like you've lost it",
						"Luckily I keep multiple copies of my journal for situations such as this");
					give(ItemId.DUKES_JOURNAL.id(), 1);
					mes("The Duke hands you a journal");
				}
				npcsay("Take a look and talk to me again when you've read it");
				break;
			case READ_BOOK:
				npcsay("So what do you think?");

				if (multi("I think the spirits want you to make amends", "I don't know I'll think about it some more") != 0) return;

				npcsay("Now that you mention that I think you're right",
					"I guess I had forgotten to write this down after my long night",
					"But the third spirit did show me another vision",
					"He showed me a headstone",
					"Cracked and overgrown",
					"I looked at the name on the stone...",
					"...And it was my own",
					"The spirit told me I will live out my days alone",
					"And die alone",
					"I do not want my life to turn out like that",
					"I guess I have not been so kind to some of the people in my life",
					"It would be best to make amends",
					"Would you help me?");

				if (multi("Sure", "I don't have the time") != 0) return;

				npcsay("Excellent thank you",
					"We can work through this one vision at a time",
					"Firstly, I need to make amends for the past",
					"I treated my love so poorly");

				if (multi("What did you have in mind?", "Yeah sounds like you were a jerk") == -1) return;

				npcsay("I know",
					"We can write a letter");

				option = multi("We?", "That's a good idea");

				if (option == -1) {
					return;
				} else if (option == 0) {
					npcsay("You're right",
						"You should do it");
				}

				npcsay("Here is some parchment");
				mes("The Duke hands you some parchment");
				give(ItemId.DUKE_PARCHMENT.id(), 1);
				delay(5);
				mes("He then continues talking before you have a chance to interrupt");
				delay(5);

				npcsay("When you're done, talk to me again",
					"I can tell you where she lives so you can deliver it");
				updateStage(player, RECEIVED_PARCHMENT);
				break;
			case RECEIVED_PARCHMENT:
				if (ifheld(ItemId.APOLOGY_LETTER.id(), 1)) {
					npcsay("Excellent",
						"Can you go deliver it for me?");

					if (multi("Don't you want to read it over?", "I can't right now") != 0) return;

					npcsay("Do I need to?",
						"I'm sure you've done just fine",
						"The woman you're looking for still lives here in Lumbridge",
						"She actually lives just outside the castle courtyard",
						"Her house is right next to Bob's Axes");
					updateStage(player, LETTER_DELIVERY);
				} else if (!ifheld(ItemId.DUKE_PARCHMENT.id(), 1)) {
					npcsay("Here, take another");
					mes("The Duke hands you some parchment");
					give(ItemId.DUKE_PARCHMENT.id(), 1);
				}
				break;
			case LETTER_DELIVERY:
				ArrayList<String> options = new ArrayList<String>();
				options.add("Where can I find the recepient again?");
				options.add("Wait a minute...");
				if (!ifheld(ItemId.APOLOGY_LETTER.id(), 1)) {
					options.add("I dropped the letter");
				}
				options.add("Nevermind");

				option = multi(options.toArray(new String[0]));
				if (option == -1 || option == (options.size() - 1)) {
					return;
				} else if (option == 0) {
					npcsay("The woman you're looking for still lives here in Lumbridge",
						"She actually lives just outside the castle courtyard",
						"Her house is right next to Bob's Axes");
				} else if (option == 1) {
					say("The house you're describing...",
						"...Is my mum's house!",
						"Did you date my mom?");

					mes("The Duke's face turns red, but he doesn't say anything");
				} else if (option == 2) {
					npcsay("Luckily Hans found it on the ground and brought it back to me",
						"Here you go");
					mes("The Duke hands you the apology letter");
					give(ItemId.APOLOGY_LETTER.id(), 1);
					delay(5);
					npcsay("Please take it straight to her",
						"And don't lose it this time!");
				}
				break;
			case DELIVERED_LETTER:
				npcsay("Excellent",
					"What did she say?");
				say("She said that she appreciated the gesture",
					"But she doesn't think she could get back together with you");
				npcsay("That's fine",
					"I am trying to make amends",
					"Not court your mother",
					"I'm glad she liked my letter though");

				option = multi("Well I was the one that wrote the letter",
					"What's next?");

				if (option == -1) {
					return;
				} else if (option == 0) {
					npcsay("Oh yes of course",
						"Anyway");
				}

				npcsay("Next we should help the old cook",
					"I guess I did sack him rather hastily",
					"You should go find him in Varrock and offer him his old job back",
					"I wouldn't mind having two cooks around here",
					"Unfortunately, you'll probably find him in some alleyway begging for money",
					"I heard he's living as a tramp now");
				updateStage(player, FIND_TRAMP);
				break;
			case FIND_TRAMP:
				npcsay("You need to go to Varrock and find my old cook",
					"Offer him his old job back",
					"Unfortunately, you'll probably find him in some alleyway begging for money",
					"I heard he's living as a tramp now");
				break;
			case FOUND_TRAMP:
				npcsay("Oh good",
					"So will he be coming back here to cook?");
				say("No",
					"He said that he is fine where he is at");
				npcsay("Well did you help him with something else then?");
				say("What?",
					"No",
					"Is that my job?");
				npcsay("Go back and see if you can help him with something");
				break;
			case HELPED_TRAMP:
				npcsay("Oh good",
					"So will he be coming back here to cook?");
				say("No",
					"He said that he is fine where he is at",
					"I did help him get ready for a job interview he has coming up");
				npcsay("Okay good",
					"So we were able to help him",
					"That should make the second spirit happy");
				mes("Before you can interrupt, the Duke continues");
				delay(5);
				npcsay("Now there's only one more person to help out",
					"Can you head back to Varrock and find Shilop?",
					"Perhaps there is something you--",
					"I mean, we--can help him with");
				updateStage(player, FIND_SHILOP);
				break;
			case FIND_SHILOP:
				npcsay("You need to head to Varrock and see if we can help Shilop with anything");
				break;
			case HELPED_SHILOP:
				npcsay("Excellent!",
					"Then it sounds like I'm done",
					"I'm sure those spirits will be happy now");
				do {
					option = multi("Don't you want to know what happened?", "You didn't do anything", "So what now?");
					if (option == -1) {
						return;
					} else if (option == 0) {
						npcsay("No",
							"I'm sure you helped him out just fine");
					} else if (option == 1) {
						npcsay("All the people that the spirits mentioned have been helped",
							"I don't really think it matters who actually did the helping",
							"Besides, I was the one that was telling you to do it");
					}
				} while (option != 2);

				npcsay("This whole ordeal has really gotten me in the Christmas spirit",
					"Ha ha, \"spirit,\" get it?",
					"Anyway, I think I want to throw a big Christmas party",
					"I think I'll invite everyone we helped today",
					"From what the spirits showed me",
					"It looks like the lot of them could use the Christmas cheer",
					"And with the look that you're giving me",
					"It looks like you could, too",
					"So you're invited as well",
					"The party will be at the Rising Sun Inn in Falador on the 1st floor",
					"I hope to see you there!");
				updateStage(player, PARTY_TIME);
				break;
			case PARTY_TIME:
				npcsay("Head to Falador",
					"The Rising Sun Inn is located right in the center of the city",
					"Everyone will be on the 1st floor");
		}
	}

	public static void mumDialogue(Player player, Npc npc) {
		mes("Mum's face turns red");
		delay(5);
		npcsay("Yes",
			"Many years ago while you were off adventuring",
			"I was lonely and he kept me company",
			"But I left when I realized that he cared more for his title and riches than me",
			"I haven't spoken to him since");

		if (!ifheld(ItemId.APOLOGY_LETTER.id(), 1)) return;

		if (multi("I have a letter for you", "I need to go think about this") != 0) return;

		mes("You hand the letter to your mother");
		remove(ItemId.APOLOGY_LETTER.id(), 1);
		delay(5);
		mes("She reads it...");
		delay(5);
		mes("And starts to cry");
		delay(5);
		npcsay("This is very sweet",
			"I don't think I could ever get back together with the Duke",
			"But this is a very thoughtful gesture and I appreciate it",
			"Will you tell him that for me?");
		updateStage(player, DELIVERED_LETTER);
	}

	public static void trampDialogue(Player player, Npc npc) {
		switch (getStage(player)) {
			case FIND_TRAMP:
				npcsay("A job, eh?",
					"Do you need me to \"take care\" of someone for you?",
					"Because that'll cost you quite a lot");
				say("What?",
					"No",
					"Why would you assume that's what I meant?");
				npcsay("I dunno");
				say("The Duke of Lumbridge wants to offer you your old job back");
				npcsay("That ol' git?",
					"Why the bloody 'ell would he want to do that?",
					"Practically threw me out the door, he did");
				int option = multi("A spirit told him to make amends for wrongs he's done",
					"Do you want the job or not?",
					"Nevermind then");
				if (option == -1 || option == 2) {
					return;
				} else if (option == 0) {
					npcsay("Oh did he now?",
						"Well forget about him",
						"He can stay doomed for all I care");
				}

				npcsay("No way I'm going back to that old job",
					"You can tell the Duke to take his offer and stick it",
					"Especially after what I found out after I left",
					"I wouldn't've lasted much longer anyways",
					"You ever wonder why those goblins are always holdin' on to so many chef's hats?",
					"I actually feel sorry for the poor bloke that's working there now",
					"Hope he keeps an eye out while walkin' home",
					"Yeah, it's way safer here in this alleyway");

				say("Well alright then");
				updateStage(player, FOUND_TRAMP);

				int capeColor = DataConversions.random(0, 3);

				npcsay("Say you know what though",
					"I could actually use your help",
					"I've got me a job interview coming up and I need help to get some new clothes",
					"My boots are all worn out so I'll need a new pair of those",
					"And I also think a " + capeColors[capeColor] + " cape would look good",
					"Could you find me those?");

				if (multi("Sure", "No way") != 0) return;

				npcsay("Thanks mate");
				player.getCache().set("arc_cape_color", capeColor);
				updateStage(player, GET_CLOTHES);
				break;

			case GET_CLOTHES:
				int cape = player.getCache().getInt("arc_cape_color");
				if (ifheld(ItemId.BOOTS.id(), 1) && ifheld(capeIds[cape], 1)) {
					say("I have what you asked for");
					npcsay("Good stuff, mate",
						"Give 'em here if you would");
					mes("You hand the tramp the clothes");
					remove(ItemId.BOOTS.id(), 1);
					remove(capeIds[cape], 1);
					delay(5);
					npcsay("You've done me a great service, you have");
					updateStage(player, HELPED_TRAMP);
					player.getCache().remove("arc_cape_color");
				} else {
					say("What did you need again?");
					npcsay("How thick are you?",
						"I need a new pair of boots",
						"And a " + capeColors[cape] + " cape");
				}
				break;
		}
	}

	public static void shilopDialogue(Player player, Npc npc, int stage) {
		int option;
		switch (stage) {
			case FIND_SHILOP:
				say("Hello there, youngster");
				npcsay("I don't know what you want from me",
					"I didn't do nothing");
				say("The Duke of Lumbridge asked me to come find you",
					"He felt bad for sending you away the other day");
				npcsay("Oh that guy?",
					"Yeah he's a jerk",
					"All I wanted was some stuff to get started adventuring",
					"But he just had some grown up bring me back here");

				if (multi("Well maybe I could help you out", "That's too bad") != 0) return;

				say("What do you need to start adventuring?");
				npcsay("What I really need is something to slay monsters with");
				say("Are you sure your mum is okay with that?");
				npcsay("Yeah she says it's fine",
					"I'm away from home so much that it'll be good if I can protect myself");

				option = multi("Well alright if you say so", "No way I'm getting you a weapon");
				if (option == -1) {
					return;
				} else if (option == 1) {
					npcsay("Fine suit yourself",
						"But I thought you wanted to help me");
				}

				say("What did you have in mind?");
				npcsay("Could you bring me a bronze dagger?",
					"I think that might work");
				updateStage(player, GET_DAGGER);
				break;
			case GET_DAGGER:
				npcsay("Do you have the bronze dagger yet?");
				if (ifheld(ItemId.BRONZE_DAGGER.id(), 1)) {
					say("Yes, I have it right here");
					mes("You hand Shilop the bronze dagger");
					remove(ItemId.BRONZE_DAGGER.id(), 1);
					delay(5);
					npcsay("Hurray!");
					mes("Shilop holds the bronze dagger and swings it around for a bit");
					delay(5);
					mes("He suddenly doesn't seem as pleased");
					delay(5);
					say("What's wrong?");
					npcsay("This is much too small",
						"I won't be able to protect myself with this",
						"You should bring me a bronze longsword instead",
						"That would be much better");
					updateStage(player, GET_LONGSWORD);
					do {
						option = multi("Alright, I'll be back",
							"There's no way I'm doing that",
							"Can I have my dagger back?");
						if (option == -1) {
							return;
						} else if (option == 2) {
							npcsay("Oh I'll hang on to it",
								"I needed something to spread jam on my bread");
						}
					} while (option == 2);
					npcsay("Well, I'll be waiting here");
				} else {
					say("No, not yet");
				}
				break;
			case GET_LONGSWORD:
				npcsay("Do you have the bronze longsword yet?");
				if (ifheld(ItemId.BRONZE_LONG_SWORD.id(), 1)) {
					say("Yes, I have it right here");
					mes("You hand Shilop the bronze longsword");
					remove(ItemId.BRONZE_LONG_SWORD.id(), 1);
					delay(5);
					npcsay("Hurray!");
					mes("Shilop tries to lift the longsword to swing it but it is too heavy");
					delay(5);
					mes("He suddenly doesn't seem as pleased");
					delay(5);
					say("What's wrong?");
					npcsay("This is too heavy",
						"I can't even swing this",
						"You should bring me a bronze short sword instead",
						"That would be much better");
					updateStage(player, GET_SWORD);
					do {
						option = multi("Alright, I'll be back",
							"There's no way I'm doing that",
							"Can I have my longsword back?");
						if (option == -1) {
							return;
						} else if (option == 2) {
							npcsay("Oh I'll hang on to it",
								"I'll grow into it some day");
						}
					} while (option == 2);
					npcsay("Well, I'll be waiting here");
				} else {
					say("No, not yet");
				}
				break;
			case GET_SWORD:
				npcsay("Do you have the bronze short sword yet?");
				if (ifheld(ItemId.BRONZE_SHORT_SWORD.id(), 1)) {
					say("Yes, I have it right here");
					mes("You hand Shilop the bronze short sword");
					remove(ItemId.BRONZE_SHORT_SWORD.id(), 1);
					delay(5);
					npcsay("Hurray!");
					mes("Shilop holds the bronze short sword and swings it around for a bit");
					delay(5);
					mes("He looks very pleased!");
					delay(5);
					npcsay("This is just right",
						"I'll be able to slay tons of monsters with this",
						"Thanks a lot adventurer!");
					updateStage(player, HELPED_SHILOP);
					say("I'm glad you like it",
						"Don't poke your eye out!");
				} else {
					say("No, not yet");
				}
				break;
		}
	}

	public static void partyDialogue(Player player, Npc npc) {
		if (npc.getID() == NpcId.DUKE_OF_LUMBRIDGE.id()) {
			npcsay("Hello and welcome to my Christmas party!");
			if (getStage(player) == PARTY_TIME) {
				npcsay("Listen",
					"On my way over here I was thinking about how much you helped me today",
					"And so I wanted to repay your kindness");
				int option = multi("You don't have to do that",
					"It's about time");
				if (option == -1) {
					return;
				} else if (option == 0) {
					npcsay("No I insist");
				}
				npcsay("So I thought I would make sure you got something nice",
					"When I sent your mother her invite I asked her to bring you something special",
					"She says she would have brought it anyways",
					"But I'm pretty sure she would have forgot if it weren't for me",
					"So you're welcome!");
				say("Thanks...");
				updateStage(player, TAKING_CREDIT);
			}
		} else if (npc.getID() == NpcId.MUM.id()) {
			npcsay("Hi sweetie!");
			int stage = getStage(player);
			if (stage == PARTY_TIME) {
				npcsay("I think the Duke has something he wants to say to you");
			} else if (stage == TAKING_CREDIT) {
				npcsay("I hope you're having a lovely time",
					"I've heard from these people that you did a lot of really nice things today",
					"So I made you a Christmas present!",
					"Here you go");
				mes("Your mum hands you a hand-knitted Christmas sweater");
				if (player.isMale()) {
					give(ItemId.RED_CHRISTMAS_SWEATER.id(), 1);
				} else {
					give(ItemId.FEMALE_RED_CHRISTMAS_SWEATER.id(), 1);
				}
				delay(5);
				npcsay("I hope to see you wearing it for the rest of the party",
					"It'll keep you nice and warm");
				player.playerServerMessage(MessageType.QUEST, "@gre@Congratulations! You have completed A RuneScape Carol!");
				updateStage(player, COMPLETED);
			} else {
				boolean wearingSweater = false;
				for (int id : sweaterIds) {
					if (ifworn(id)) {
						wearingSweater = true;
						break;
					}
				}
				if (wearingSweater) {
					npcsay("I see you're wearing the Christmas sweater I knitted for you",
						"I hope it's keeping you nice and warm!",
						"I forgot to tell you earlier, but the material I used is great for dyeing",
						"So if you wanted a different colour you could redye it yourself");
				} else {
					npcsay("You aren't wearing your Christmas sweater?",
						"Oh, I knew I should have made you something else");
					mes("Your mum looks a bit sad");
				}
			}
		} else if (npc.getID() == NpcId.TRAMP.id()) {
			npcsay("Oi mate",
				"Thanks again for the help with those clothes",
				"Unfortunately on my way to the interview",
				"I was jumped by some mugger and he took my cape",
				"And then the interviewer told me I didn't dress professionally enough for the job",
				"So you'll probably see me around the alleyways",
				"But that's alright",
				"It's not a bad life, it is");
		} else if (npc.getID() == NpcId.SHILOP.id()) {
			npcsay("Hello!",
				"If you're wondering where my sword is my mom took it",
				"I might have accidentally poked one of her cats",
				"She said I could have it back after New Year's though");
		}
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.DUKES_JOURNAL.id()) {
			mes("You open the Duke's journal");
			mes("Which page would you like to turn to?");
			int page = multi("page 1", "page 2", "page 3");
			if (page == 0) {
				ActionSender.sendBox(player, " %Tonight I was visited by three spirits %"
					+ "The first spirit told me that his name was Praeteritum %"
					+ "He then showed me a vision of a Christmas from my past %"
					+ "I remember it well %"
					+ "It was the Christmas that I lost the love of my life %"
					+ "I was young and stupid %"
					+ "She wanted me to spend time with her and her family %"
					+ "But I was too concerned with my dukedom and riches %"
					+ "And I turned her away %"
					+ "I broke her heart %"
					+ "And she never came around again", true);
			} else if (page == 1) {
				ActionSender.sendBox(player, " %The second spirit said his name was Praesens %"
					+ "He showed me a vision of a man that I recently banished from Lumbridge %"
					+ "I kicked him out because he ruined Thanksgiving dinner %"
					+ "He forgot to buy ingredients for the pie %"
					+ "Apparently he is now living as a tramp in Varrock", true);
			} else if (page == 2) {
				ActionSender.sendBox(player, "The third and final spirit told me his name was Futurum %"
					+ "The vision he had for me was the strangest of all %"
					+ "It seemed to be a vision of the future %"
					+ "In the vision, it was Christmas time like it is now %"
					+ "The ghost showed me a man %"
					+ "He was living at home, still with his elderly mother and her cats %"
					+ "You could tell they did not have much money %"
					+ "I didn't recognize the man, so I asked the spirit who he was %"
					+ "The ghost told me that this man was Shilop %"
					+ "I was astonished to see that the young lad had grown up to be so miserable %"
					+ "Why just the other day the young lad had come all the way down from Varrock %"
					+ "He asked me for a quest and some gear to get him started with adventuring %"
					+ "Of course I told him that he was being ridiculous %"
					+ "I told him that adventuring was not a life he should be persuing %"
					+ "I had Hans take him back home", true);
			}
			if (getStage(player) == GHOST_STORY) {
				updateStage(player, READ_BOOK);
			}
		} else if (item.getCatalogId() == ItemId.DUKE_PARCHMENT.id()) {
			mes("You find a quill and ink bottle nearby on the ground");
			mes("That's lucky!");
			delay(5);
			mes("You begin to write an apology letter as if it were from the Duke");
			delay(5);
			mes("Oh, rarely have words poured from your penny pencil--");
			delay(5);
			mes("err, quill--");
			delay(5);
			mes("with such feverish fluidity");
			delay(5);
			mes("Before long, you have written a beautiful and heartfelt apology letter");
			remove(ItemId.DUKE_PARCHMENT.id(), 1);
			give(ItemId.APOLOGY_LETTER.id(), 1);
		} else if (item.getCatalogId() == ItemId.APOLOGY_LETTER.id()) {
			ActionSender.sendBox(player, "My Love, % %"
				+ "I hope you're doing well. I've been reflecting on our past and the "
				+ "choices I made, and I want to sincerely apologize for my actions. I "
				+ "realize I made a terrible mistake by prioritizing my career over our "
				+ "relationship, especially during important moments like Christmas. % %"
				+ "I deeply regret the pain I caused you, and I now understand the "
				+ "importance of love and connection in life. I've made changes to my "
				+ "priorities, and I hope you can find it in your heart to forgive me. I "
				+ "miss you and would love the chance to make amends and rebuild what we once had. % %"
				+ "Horacio", true);
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc npc) {
		int option = multi("Who are you?", "Is the Duke still doomed?");
		if (option == 0) {
			if (npc.getID() == NpcId.PRAETERITUM.id()) {
				npcsay("I am the ghost of Christmas past",
					"People do not throw Christmas parties like they used to",
					"Now the Christmas parties in the second age?",
					"Those were where it was at!");
			} else if (npc.getID() == NpcId.PRAESENS.id()) {
				npcsay("I am the ghost of Christmas present",
					"That's \"present\" as in the current time",
					"Not \"presents\" as in gifts",
					"People get that mixed up all the time");
			} else if (npc.getID() == NpcId.FUTURUM.id()) {
				npcsay("I am the ghost of Christmas future",
					"Would you mind getting me some eggnog?");
				say("What's eggnog?");
				npcsay("Oh yeah",
					"They haven't invented that yet",
					"Well, you'll have something to look forward to on future Christmases!");
			}
		} else if (option == 1) {
			npcsay("Well he was",
				"He didn't really seem to learn anything",
				"And you did all the work of course",
				"But then he invited us to this party",
				"So we'll let him slide",
				"The friends he makes here today will stick around for a long time");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.PRAETERITUM.id()
			|| npc.getID() == NpcId.PRAESENS.id()
			|| npc.getID() == NpcId.FUTURUM.id();
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return ((item.getCatalogId() == ItemId.DUKES_JOURNAL.id() || item.getCatalogId() == ItemId.APOLOGY_LETTER.id()) && command.equalsIgnoreCase("read")) || (item.getCatalogId() == ItemId.DUKE_PARCHMENT.id() && command.equals("write"));
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		Item sweater;
		Item dye;
		if (DataConversions.inArray(sweaterIds, item1.getCatalogId())) {
			sweater = item1;
			dye = item2;
		} else {
			sweater = item2;
			dye = item1;
		}

		int newSweaterId = dyeToSweater.get(dye.getCatalogId());
		if (!player.isMale()) {
			int sweaterOffset = ItemId.FEMALE_RED_CHRISTMAS_SWEATER.id() - ItemId.RED_CHRISTMAS_SWEATER.id();
			newSweaterId += sweaterOffset;
		}

		mes("You dye the sweater");
		remove(sweater.getCatalogId(), 1);
		remove(dye.getCatalogId(), 1);
		give(newSweaterId, 1);
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		boolean sweater = false;
		boolean dye = false;

		for (int id : sweaterIds) {
			if (item1.getCatalogId() == id) {
				sweater = true;
				break;
			}
			if (item2.getCatalogId() == id) {
				sweater = true;
				break;
			}
		}

		for (int id : dyeIds) {
			if (item1.getCatalogId() == id) {
				dye = true;
				break;
			}
			if (item2.getCatalogId() == id) {
				dye = true;
				break;
			}
		}

		return sweater && dye;
	}

	public static boolean inPartyRoom(Npc npc) {
		return npc.getX() >= PARTY_ROOM_MIN_X && npc.getX() <= PARTY_ROOM_MAX_X
			&& npc.getY() >= PARTY_ROOM_MIN_Y && npc.getY() <= PARTY_ROOM_MAX_Y;
	}

	public static boolean hasSweater(Player player) {
		for (int id : sweaterIds) {
			if (ifheld(id, 1)) {
				return true;
			}
			if (ifworn(id)) {
				return true;
			}
			if (player.getBank().hasItemId(id)) {
				return true;
			}
		}
		return false;
	}

	private static void updateStage(final Player player, final int newStage) {
		player.getCache().set("a_lumbridge_carol", newStage);
	}

	public static int getStage(final Player player) {
		return player.getCache().hasKey("a_lumbridge_carol") ? player.getCache().getInt("a_lumbridge_carol") : 0;
	}
}
