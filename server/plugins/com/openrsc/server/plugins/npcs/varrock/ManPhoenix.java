package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.*;

public class ManPhoenix implements TalkToNpcExecutiveListener,
	TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		Npc man = getNearestNpc(p, NpcId.STRAVEN.id(), 20);
		if (isBlackArmGang(p)) {
			if (man != null) {
				npcTalk(p, man, "hey get away from there",
					"Black arm dog");
				man.setChasing(p);
			}
		} else if (p.getQuestStage(Quests.HEROS_QUEST) >= 1 && isPhoenixGang(p)) {
			if (!hasItem(p, ItemId.MASTER_THIEF_ARMBAND.id()) && p.getCache().hasKey("armband")) {
				playerTalk(p, n, "I have lost my master thief armband");
				npcTalk(p, n, "You need to be more careful", "Ah well", "Have this spare");
				addItem(p, ItemId.MASTER_THIEF_ARMBAND.id(), 1);
				return;
			} else if (hasItem(p, ItemId.CANDLESTICK.id()) && !p.getCache().hasKey("armband")) {
				playerTalk(p, n, "I have retrieved a candlestick");
				npcTalk(p, n, "Hmm not a bad job",
					"Let's see it, make sure it's genuine");
				p.message("You hand Straven the candlestick");
				removeItem(p, ItemId.CANDLESTICK.id(), 1);
				playerTalk(p, n, "So is this enough to get me a master thieves armband?");
				npcTalk(p, n, "Hmm I dunno",
					"I suppose I'm in a generous mood today");
				p.message("Straven hands you a master thief armband");
				addItem(p, ItemId.MASTER_THIEF_ARMBAND.id(), 1);
				p.getCache().store("armband", true);
				return;
			}
			playerTalk(p, n, "How would I go about getting a master thieves armband?");
			npcTalk(p, n, "Ooh tricky stuff, took me years to get that rank",
				"Well what some of aspiring thieves in our gang are working on right now",
				"Is to steal some very valuable rare candlesticks",
				"From scarface Pete - the pirate leader on Karamja",
				"His security is good enough and the target valuable enough",
				"That might be enough to get you the rank",
				"Go talk to our man Alfonse the waiter in the shrimp and parrot",
				"Use the secret word gherkin to show you're one of us");
			p.getCache().store("pheonix_mission", true);
			p.getCache().store("pheonix_alf", true);
		} else if (!p.getBank().hasItemId(ItemId.PHOENIX_GANG_WEAPON_KEY.id()) && !hasItem(p, ItemId.PHOENIX_GANG_WEAPON_KEY.id()) &&
				(p.getQuestStage(Quests.SHIELD_OF_ARRAV) >= 5 || p.getQuestStage(Quests.SHIELD_OF_ARRAV) < 0)
				&& isPhoenixGang(p)) {
			npcTalk(p, n, "Greetings fellow gang member");
			playerTalk(p, n, "I have lost the key you gave me");
			npcTalk(p, n, "You need to be more careful",
				"We don't want that key falling into the wrong hands",
				"Ah well",
				"Have this spare");
			addItem(p, ItemId.PHOENIX_GANG_WEAPON_KEY.id(), 1);
			message(p, "Straven hands you a key");
		} else if ((p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 4 && isPhoenixGang(p))
				|| (p.getCache().hasKey("arrav_mission") && (p.getCache().getInt("arrav_mission") & 2) == PHOENIX_MISSION)) {
			npcTalk(p, n, "Hows your little mission going?");
			if (p.getInventory().hasItemId(ItemId.SCROLL.id())) {
				playerTalk(p, n, "I have the intelligence report");
				npcTalk(p, n, "Lets see it then");
				message(p, "You hand over the report");
				removeItem(p, ItemId.SCROLL.id(), 1);
				message(p, "The man reads the report");
				npcTalk(p, n, "Yes this is very good",
					"Ok you can join the phoenix gang",
					"I am Straven, one of the gang leaders");
				playerTalk(p, n, "Nice to meet you");
				npcTalk(p, n, "Here is a key");
				message(p, "Straven hands you a key");
				addItem(p, ItemId.PHOENIX_GANG_WEAPON_KEY.id(), 1);
				npcTalk(p, n, "It will let you enter our weapon supply area",
					"Round the front of this building");
				p.updateQuestStage(Quests.SHIELD_OF_ARRAV, 5);
				if (p.getCache().hasKey("arrav_mission")) {
					p.getCache().remove("arrav_mission");
				}
				if (p.getCache().hasKey("spoken_tramp")) {
					p.getCache().remove("spoken_tramp");
				}
			} else {
				playerTalk(p, n, "I haven't managed to find the report yet");
				npcTalk(p, n,
					"You need to kill Jonny the beard",
					"Who should be in the blue moon inn");
			}

		} else if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 5
			|| p.getQuestStage(Quests.SHIELD_OF_ARRAV) < 0 || p.getQuestStage(Quests.HEROS_QUEST) == -1) {
			memberOfPhoenixConversation(p, n);
		} else if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) <= 3) {
			defaultConverstation(p, n);
		}
	}

	private void memberOfPhoenixConversation(final Player p, final Npc n) {
		Menu defaultMenu = new Menu();
		if (isPhoenixGang(p)) {
			npcTalk(p, n, "Greetings fellow gang member");
			defaultMenu.addOption(new Option(
				"I've heard you've got some cool treasures in this place") {
				public void action() {
					npcTalk(p, n,
						"Oh yeah, we've all stolen some stuff in our time",
						"The candlesticks down here",
						"Were quite a challenge to get out the palace");
					playerTalk(p, n, "And the shield of Arrav",
						"I heard you got that");
					npcTalk(p, n, "hmm", "That was a while ago",
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
			});
			defaultMenu.addOption(new Option(
				"Any suggestions for where I can go thieving?") {
				public void action() {
					npcTalk(p, n, "You can always try the market",
						"Lots of opportunity there");
				}
			});
			defaultMenu.addOption(new Option("Where's the Blackarm gang hideout?") {
				public void action() {
					playerTalk(p, n, "I wanna go sabotage em");
					npcTalk(p, n, "That would be a little tricky",
						"Their security is pretty good",
						"Not as good as ours obviously", "But still good",
						"If you really want to go there",
						"It is in the alleyway",
						"To the west as you come in the south gate",
						"One of our operatives is often near the alley",
						"A red haired tramp",
						"He may be able to give you some ideas");
					playerTalk(p, n, "Thanks for the help");
				}
			});
			defaultMenu.showMenu(p);
		}
	}

	private void defaultConverstation(final Player p, final Npc n) {
		Menu defaultMenu = new Menu();
		playerTalk(p, n, "What's through that door?");
		npcTalk(p,
			n,
			"Heh you can't go in there",
			"Only authorised personnel of the VTAM corporation are allowed beyond this point");
		if (p.getQuestStage(Constants.Quests.SHIELD_OF_ARRAV) == 3) {
			defaultMenu.addOption(new Option("I know who you are") {
				public void action() {
					npcTalk(p, n, "I see", "Carry on");
					playerTalk(p, n,
						"This is the headquarters of the Phoenix Gang",
						"The most powerful crime gang this city has seen");
					npcTalk(p, n, "And supposing we were this crime gang",
						"What would you want with us?");
					new Menu().addOptions(
						new Option("I'd like to offer you my services") {
							public void action() {
								npcTalk(p,
									n,
									"You mean you'd like to join the phoenix gang?",
									"Well the phoenix gang doesn't let people join just like that",
									"You can't be too careful, you understand",
									"Generally someone has to prove their loyalty before they can join");
								playerTalk(p, n,
									"How would I go about this?");
								npcTalk(p,
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
								if (p.getCache().hasKey("arrav_mission")) {
									p.getCache().set("arrav_mission", ANY_MISSION);
								} else {
									p.getCache().set("arrav_mission", PHOENIX_MISSION);
								}
								playerTalk(p, n, "Ok, I'll get on it");
							}
						},
						new Option(
							"I want nothing. I was just making sure you were them") {
							@Override
							public void action() {
								npcTalk(p, n, "Well stop wasting my time");
							}

						}).showMenu(p);

				}
			});
		}
		defaultMenu.addOption(new Option(
			"How do I get a job with the VTAM corporation?") {
			public void action() {
				npcTalk(p, n, "Get a copy of the Varrock Herald",
					"If we have any positions right now",
					"They'll be advertised in there");
			}
		});
		defaultMenu.addOption(new Option("Why not?") {
			public void action() {
				npcTalk(p, n, "Sorry that is classified information");
			}
		});
		defaultMenu.showMenu(p);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.STRAVEN.id();
	}
}
