package com.openrsc.server.plugins.npcs.lumbridge;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Urhney implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		Menu defaultMenu = new Menu();
		npcsay(player, n, "Go away, I'm meditating");
		if (player.getQuestStage(Quests.THE_RESTLESS_GHOST) == 1 && !player.getCarriedItems().hasCatalogID(ItemId.AMULET_OF_GHOSTSPEAK.id(), Optional.empty())) {
			defaultMenu.addOption(new Option(
				"Father Aereck sent me to talk to you") {
				@Override
				public void action() {
					npcsay(player, n, "I suppose I'd better talk to you then",
						"What problems has he got himself into this time?");
					new Menu().addOptions(
						new Option(
							"He's got a ghost haunting his graveyard") {
							@Override
							public void action() {
								npcsay(player,
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
								mes(player,
									"Father Urhney hands you an amulet");
								give(player, ItemId.AMULET_OF_GHOSTSPEAK.id(), 1); // AMULET OF GHOST SPEAK.
								npcsay(player,
									n,
									"It is an amulet of Ghostspeak",
									"So called because when you wear it you can speak to ghosts",
									"A lot of ghosts are doomed to be ghosts",
									"Because they have left some task uncompleted",
									"Maybe if you know what this task is",
									"You can get rid of the ghost",
									"I'm not making any guarantees mind you",
									"But it is the best I can do right now");
								say(player, n,
									"Thank you, I'll give it a try");
								player.updateQuestStage(Quests.THE_RESTLESS_GHOST,
									2);
							}
						},
						new Option(
							"You mean he gets himself into lots of problems?") {
							@Override
							public void action() {
								npcsay(player,
									n,
									"Yeah. For example when we were trainee priests",
									"He kept on getting stuck up bell ropes",
									"Anyway I don't have time for chitchat",
									"What's his problem this time?");
								say(player, n,
									"He's got a ghost haunting his graveyard");
								npcsay(player,
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
								mes(player,
									"Father Urhney hands you an amulet");
								give(player, ItemId.AMULET_OF_GHOSTSPEAK.id(), 1); // AMULET OF GHOST SPEAK.
								npcsay(player,
									n,
									"It is an amulet of Ghostspeak",
									"So called because when you wear it you can speak to ghosts",
									"A lot of ghosts are doomed to be ghosts",
									"Because they have left some task uncompleted",
									"Maybe if you know what this task is",
									"You can get rid of the ghost",
									"I'm not making any guarantees mind you",
									"But it is the best I can do right now");
								say(player, n,
									"Thank you, I'll give it a try");
								player.updateQuestStage(Quests.THE_RESTLESS_GHOST,
									2);
							}
						}).showMenu(player);
				}
			});
		}
		if (player.getQuestStage(Quests.THE_RESTLESS_GHOST) >= 2 && !player.getCarriedItems().hasCatalogID(ItemId.AMULET_OF_GHOSTSPEAK.id(), Optional.empty())) {
			defaultMenu.addOption(new Option(
				"I've lost the amulet") {
				@Override
				public void action() {
					mes(player, "Father Urhney sighs");
					npcsay(player, n, "How careless can you get",
						"Those things aren't easy to come by you know",
						"It's a good job I've got a spare");
					give(player, ItemId.AMULET_OF_GHOSTSPEAK.id(), 1);
					mes(player, "Father Urhney hands you an amulet");
					npcsay(player, n, "Be more careful this time");
					say(player, n, "Ok I'll try to be");
				}
			});
		}
		defaultMenu.addOption(new Option("Well that's friendly") {
			@Override
			public void action() {
				npcsay(player, n, "I said go away!");
				say(player, n, "Ok, ok");
			}
		});
		defaultMenu.addOption(new Option("I've come to repossess your house") {
			@Override
			public void action() {
				npcsay(player, n, "Under what grounds?");
				new Menu().addOptions(new Option("Repeated failure on mortgage payments") {
					@Override
					public void action() {
						npcsay(player, n, "I don't have a mortgage", "I built this house myself");
						say(player, n, "Sorry I must have got the wrong address", "All the houses look the same around here");
					}
				}, new Option("I don't know, I just wanted this house") {
					@Override
					public void action() {
						npcsay(player, n, "Oh go away and stop wasting my time");
					}
				}).showMenu(player);
			}
		});
		defaultMenu.showMenu(player);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.URHNEY.id();
	}

}
