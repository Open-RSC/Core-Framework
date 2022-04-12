package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.authentic.quests.free.ShieldOfArrav.*;

public class ManPhoenix implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		stravenDialogue(player, n, true);
	}

	public static void indirectTalkToStraven(final Player player, final Npc n) {
		Npc man = ifnearvisnpc(player, NpcId.STRAVEN.id(), 20);
		if (isBlackArmGang(player)) {
			if (man != null) {
				npcsay(player, man, "hey get away from there",
					"Black arm dog");
				man.setChasing(player);
			}
		} else if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) >= 0 && player.getQuestStage(Quests.SHIELD_OF_ARRAV) < 5) {
			stravenDialogue(player, n, false);
		}
		//any other condition inexistent, should open door
	}

	public static void stravenDialogue(Player player, Npc n, final boolean directTalk) {
		Npc man = ifnearvisnpc(player, NpcId.STRAVEN.id(), 20);
		if (isBlackArmGang(player)) {
			if (man != null) {
				npcsay(player, man, "hey get away from there",
					"Black arm dog");
				man.setChasing(player);
			}
		} else if (player.getQuestStage(Quests.HEROS_QUEST) >= 1 && isPhoenixGang(player)) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.MASTER_THIEF_ARMBAND.id(), Optional.empty()) && player.getCache().hasKey("armband")) {
				say(player, n, "I have lost my master thief armband");
				npcsay(player, n, "You need to be more careful", "Ah well", "Have this spare");
				give(player, ItemId.MASTER_THIEF_ARMBAND.id(), 1);
				return;
			} else if (player.getCarriedItems().hasCatalogID(ItemId.CANDLESTICK.id(), Optional.of(false)) && player.getCache().hasKey("killed_grip")
				&& !player.getCache().hasKey("armband")) {
				say(player, n, "I have retrieved a candlestick");
				npcsay(player, n, "Hmm not a bad job",
					"Let's see it, make sure it's genuine");
				player.message("You hand Straven the candlestick");
				player.getCarriedItems().remove(new Item(ItemId.CANDLESTICK.id()));
				say(player, n, "So is this enough to get me a master thieves armband?");
				npcsay(player, n, "Hmm I dunno",
					"I suppose I'm in a generous mood today");
				player.message("Straven hands you a master thief armband");
				give(player, ItemId.MASTER_THIEF_ARMBAND.id(), 1);
				player.getCache().store("armband", true);
				return;
			}
			say(player, n, "How would I go about getting a master thieves armband?");
			npcsay(player, n, "Ooh tricky stuff, took me years to get that rank",
				"Well what some of aspiring thieves in our gang are working on right now",
				"Is to steal some very valuable rare candlesticks",
				"From scarface Pete - the pirate leader on Karamja",
				"His security is good enough and the target valuable enough",
				"That might be enough to get you the rank",
				"Go talk to our man Alfonse the waiter in the shrimp and parrot",
				"Use the secret word gherkin to show you're one of us");
			if (!player.getCache().hasKey("pheonix_mission")) {
				player.getCache().store("pheonix_mission", true);
			}
			if (!player.getCache().hasKey("pheonix_alf")) {
				player.getCache().store("pheonix_alf", true);
			}
		} else if (!player.getBank().hasItemId(ItemId.PHOENIX_GANG_WEAPON_KEY.id()) && !player.getCarriedItems().hasCatalogID(ItemId.PHOENIX_GANG_WEAPON_KEY.id(), Optional.of(false)) &&
			(player.getQuestStage(Quests.SHIELD_OF_ARRAV) >= 5 || player.getQuestStage(Quests.SHIELD_OF_ARRAV) < 0)
			&& isPhoenixGang(player)) {
			npcsay(player, n, "Greetings fellow gang member");
			say(player, n, "I have lost the key you gave me");
			npcsay(player, n, "You need to be more careful",
				"We don't want that key falling into the wrong hands",
				"Ah well",
				"Have this spare");
			give(player, ItemId.PHOENIX_GANG_WEAPON_KEY.id(), 1);
			mes("Straven hands you a key");
			delay(3);
		} else if (player.getConfig().OLD_QUEST_MECHANICS && !player.getBank().hasItemId(ItemId.PHOENIX_GANG_KEY.id()) && !player.getCarriedItems().hasCatalogID(ItemId.PHOENIX_GANG_KEY.id(), Optional.of(false)) &&
			(player.getQuestStage(Quests.SHIELD_OF_ARRAV) >= 5 || player.getQuestStage(Quests.SHIELD_OF_ARRAV) < 0)
			&& isPhoenixGang(player)) {
			// unknown real dialogue for reclaiming
			// placed here similar to how is for retrieving weapon key
			npcsay(player, n, "Greetings fellow gang member");
			say(player, n, "I have lost the hideout key you gave me");
			npcsay(player, n, "You need to be more careful",
				"We don't want that key falling into the wrong hands",
				"Ah well",
				"Have this spare");
			give(player, ItemId.PHOENIX_GANG_KEY.id(), 1);
			mes("Straven hands you a key");
			delay(3);
		} else if ((player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 4 && isPhoenixGang(player))
			|| (player.getCache().hasKey("arrav_mission") && (player.getCache().getInt("arrav_mission") & 2) == PHOENIX_MISSION)) {
			npcsay(player, n, "Hows your little mission going?");
			if (player.getCarriedItems().hasCatalogID(ItemId.SCROLL.id())) {
				say(player, n, "I have the intelligence report");
				npcsay(player, n, "Lets see it then");
				mes("You hand over the report");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.SCROLL.id()));
				mes("The man reads the report");
				delay(3);
				npcsay(player, n, "Yes this is very good",
					"Ok you can join the phoenix gang",
					"I am Straven, one of the gang leaders");
				say(player, n, "Nice to meet you");
				npcsay(player, n, "Here is a key");
				mes("Straven hands you a key");
				delay(3);
				give(player, ItemId.PHOENIX_GANG_WEAPON_KEY.id(), 1);
				npcsay(player, n, "It will let you enter our weapon supply area",
					"Round the front of this building");
				if (player.getConfig().OLD_QUEST_MECHANICS) {
					// Recreated RSC retro mechanic
					npcsay(player, n, "And here is another key");
					mes("Straven hands you another key");
					delay(3);
					give(player, ItemId.PHOENIX_GANG_KEY.id(), 1);
					npcsay(player, n, "It will let you enter our hideout");
				}
				player.updateQuestStage(Quests.SHIELD_OF_ARRAV, 5);
				if (!player.getCache().hasKey("arrav_gang")) {
					// player got traded the report or had it before starting mission
					player.getCache().set("arrav_gang", PHOENIX_GANG);
				}
				if (player.getCache().hasKey("arrav_mission")) {
					player.getCache().remove("arrav_mission");
				}
				if (player.getCache().hasKey("spoken_tramp")) {
					player.getCache().remove("spoken_tramp");
				}
			} else {
				say(player, n, "I haven't managed to find the report yet");
				npcsay(player, n,
					"You need to kill Jonny the beard",
					"Who should be in the blue moon inn");
			}

		} else if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 5
			|| player.getQuestStage(Quests.SHIELD_OF_ARRAV) < 0 || player.getQuestStage(Quests.HEROS_QUEST) == -1) {
			memberOfPhoenixConversation(player, n);
		} else if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) <= 3) {
			defaultConversation(player, n, directTalk);
		}
	}

	private static void memberOfPhoenixConversation(final Player player, final Npc n) {
		if (isPhoenixGang(player)) {
			npcsay(player, n, "Greetings fellow gang member");

			ArrayList<String> options = new ArrayList<>();
			options.add("I've heard you've got some cool treasures in this place");
			options.add("Any suggestions for where I can go thieving?");
			options.add("Where's the Blackarm gang hideout?");
			String[] finalOptions = new String[options.size()];
			int option = multi(player, n, options.toArray(finalOptions));

			if (option == 0) {
				npcsay(player, n,
					"Oh yeah, we've all stolen some stuff in our time",
					"The candlesticks down here",
					"Were quite a challenge to get out the palace");
				say(player, n, "And the shield of Arrav",
					"I heard you got that");
				npcsay(player, n, "hmm", "That was a while ago",
					"We don't even have all the shield anymore",
					"About 5 years ago",
					"We had a massive fight in our gang",
					"The shield got broken in half during the fight",
					"Shortly after the fight", "Some gang members decided",
					"They didn't want to be part of our gang anymore",
					"So they split off to form their own gang",
					"The black arm gang", "On their way out",
					"They looted what treasures they could from us",
					"Which included one of the halves of the shield",
					"We've been rivals with the black arms ever since");
			}

			else if (option == 1) {
				npcsay(player, n, "You can always try the market",
					"Lots of opportunity there");
			}

			else if (option == 2) {
				say(player, n, "I wanna go sabotage em");
				npcsay(player, n, "That would be a little tricky",
					"Their security is pretty good",
					"Not as good as ours obviously", "But still good",
					"If you really want to go there",
					"It is in the alleyway",
					"To the west as you come in the south gate",
					"One of our operatives is often near the alley",
					"A red haired tramp",
					"He may be able to give you some ideas");
				say(player, n, "Thanks for the help");
			}
		}
	}

	private static void defaultConversation(final Player player, final Npc n, final boolean directTalk) {
		if (directTalk) {
			say(player, n, "What's through that door?");
		}
		npcsay(player,
			n,
			"Heh you can't go in there",
			"Only authorised personnel of the VTAM corporation are allowed beyond this point");

		ArrayList<String> options = new ArrayList<>();
		if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 3) {
			options.add("I know who you are");
		}
		options.add("How do I get a job with the VTAM corporation?");
		options.add("Why not?");
		String[] finalOptions = new String[options.size()];
		int option = multi(player, n, options.toArray(finalOptions));

		if (option == 2) {
			npcsay(player, n, "Sorry that is classified information");
		}

		else if (option == 1) {
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 3) {
				npcsay(player, n, "Get a copy of the Varrock Herald",
					"If we have any positions right now",
					"They'll be advertised in there");
			}

			else {
				npcsay(player, n, "Sorry that is classified information");
			}
		}

		else if (option == 0) {
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 3) {
				npcsay(player, n, "I see", "Carry on");
				say(player, n,
					"This is the headquarters of the Phoenix Gang",
					"The most powerful crime gang this city has seen");
				npcsay(player, n, "And supposing we were this crime gang",
					"What would you want with us?");
				option = multi(player, n,
					"I'd like to offer you my services",
					"I want nothing. I was just making sure you were them"
				);
				if (option == 0) {
					npcsay(player,
						n,
						"You mean you'd like to join the phoenix gang?",
						"Well the phoenix gang doesn't let people join just like that",
						"You can't be too careful, you understand",
						"Generally someone has to prove their loyalty before they can join");
					say(player, n,
						"How would I go about this?");
					npcsay(player,
						n,
						"Let me think",
						"I have an idea",
						"A rival gang of ours",
						"Called the black arm gang",
						"Is meant to be meeting their contact from Port Sarim today",
						"In the blue moon inn",
						"By the south entrance to this city",
						"The name of the contact is Jonny the beard",
						"Kill him and bring back his intelligence report");
					if (player.getCache().hasKey("arrav_mission") && ((player.getCache().getInt("arrav_mission") & 2) != PHOENIX_MISSION)) {
						player.getCache().set("arrav_mission", ANY_MISSION);
					}

					else if (!player.getCache().hasKey("arrav_mission")) {
						player.getCache().set("arrav_mission", PHOENIX_MISSION);
					}
					say(player, n, "Ok, I'll get on it");
				}

				else if (option == 1) {
					npcsay(player, n, "Well stop wasting my time");
				}
			}

			else {
				npcsay(player, n, "Get a copy of the Varrock Herald",
					"If we have any positions right now",
					"They'll be advertised in there");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.STRAVEN.id();
	}
}
