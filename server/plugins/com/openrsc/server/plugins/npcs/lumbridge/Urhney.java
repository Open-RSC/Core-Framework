package com.openrsc.server.plugins.npcs.lumbridge;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;

public class Urhney implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		Menu defaultMenu = new Menu();
		npcTalk(p, n, "Go away, I'm meditating");
		if (p.getQuestStage(Quests.THE_RESTLESS_GHOST) == 1 && !hasItem(p, ItemId.AMULET_OF_GHOSTSPEAK.id())) {
			defaultMenu.addOption(new Option(
				"Father Aereck sent me to talk to you") {
				@Override
				public void action() {
					npcTalk(p, n, "I suppose I'd better talk to you then",
						"What problems has he got himself into this time?");
					new Menu().addOptions(
						new Option(
							"He's got a ghost haunting his graveyard") {
							@Override
							public void action() {
								npcTalk(p,
									n,
									"Oh the silly fool",
									"I leave town for just five months",
									"and already he can't manage",
									"Sigh",
									"Well I can't go back and exorcise it",
									"I vowed not to leave this place",
									"Until I had done a full two years of prayer and meditation",
									"Tell you what I can do though",
									"Take this amulet");
								message(p,
									"Father Urhney hands you an amulet");
								addItem(p, ItemId.AMULET_OF_GHOSTSPEAK.id(), 1); // AMULET OF GHOST SPEAK.
								npcTalk(p,
									n,
									"It is an amulet of Ghostspeak",
									"So called because when you wear it you can speak to ghosts",
									"A lot of ghosts are doomed to be ghosts",
									"Because they have left some task uncompleted",
									"Maybe if you know what this task is",
									"You can get rid of the ghost",
									"I'm not making any guarantees mind you",
									"But it is the best I can do right now");
								playerTalk(p, n,
									"Thank you, I'll give it a try");
								p.updateQuestStage(Quests.THE_RESTLESS_GHOST,
									2);
							}
						},
						new Option(
							"You mean he gets himself into lots of problems?") {
							@Override
							public void action() {
								npcTalk(p,
									n,
									"Yeah. For example when we were trainee priests",
									"He kept on getting stuck up bell ropes",
									"Anyway I don't have time for chitchat",
									"What's his problem this time?");
								playerTalk(p, n,
									"He's got a ghost haunting his graveyard");
								npcTalk(p,
									n,
									"Oh the silly fool",
									"I leave town for just five months",
									"and already he can't manage",
									"Sigh",
									"Well I can't go back and exorcise it",
									"I vowed not to leave this place",
									"Until I had done a full two years of prayer and meditation",
									"Tell you what I can do though",
									"Take this amulet");
								message(p,
									"Father Urhney hands you an amulet");
								addItem(p, ItemId.AMULET_OF_GHOSTSPEAK.id(), 1); // AMULET OF GHOST SPEAK.
								npcTalk(p,
									n,
									"It is an amulet of Ghostspeak",
									"So called because when you wear it you can speak to ghosts",
									"A lot of ghosts are doomed to be ghosts",
									"Because they have left some task uncompleted",
									"Maybe if you know what this task is",
									"You can get rid of the ghost",
									"I'm not making any guarantees mind you",
									"But it is the best I can do right now");
								playerTalk(p, n,
									"Thank you, I'll give it a try");
								p.updateQuestStage(Quests.THE_RESTLESS_GHOST,
									2);
							}
						}).showMenu(p);
				}
			});
		}
		if (p.getQuestStage(Quests.THE_RESTLESS_GHOST) >= 2 && !hasItem(p, ItemId.AMULET_OF_GHOSTSPEAK.id())) {
			defaultMenu.addOption(new Option(
				"I've lost the amulet") {
				@Override
				public void action() {
					message(p, "Father Urhney sighs");
					npcTalk(p, n, "How careless can you get",
						"Those things aren't easy to come by you know",
						"It's a good job I've got a spare");
					addItem(p, ItemId.AMULET_OF_GHOSTSPEAK.id(), 1);
					message(p, "Father Urhney hands you an amulet");
					npcTalk(p, n, "Be more careful this time");
					playerTalk(p, n, "Ok I'll try to be");
				}
			});
		}
		defaultMenu.addOption(new Option("Well that's friendly") {
			@Override
			public void action() {
				npcTalk(p, n, "I said go away!");
				playerTalk(p, n, "Ok, ok");
			}
		});
		defaultMenu.addOption(new Option("I've come to repossess your house") {
			@Override
			public void action() {
				npcTalk(p, n, "Under what grounds?");
				new Menu().addOptions(new Option("Repeated failure on mortgage payments") {
					@Override
					public void action() {
						npcTalk(p, n, "I don't have a mortgage", "I built this house myself");
						playerTalk(p, n, "Sorry I must have got the wrong address", "All the houses look the same around here");
					}
				}, new Option("I don't know, I just wanted this house") {
					@Override
					public void action() {
						npcTalk(p, n, "Oh go away and stop wasting my time");
					}
				}).showMenu(p);
			}
		});
		defaultMenu.showMenu(p);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.URHNEY.id();
	}

}
