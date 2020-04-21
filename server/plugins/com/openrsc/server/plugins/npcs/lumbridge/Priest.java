package com.openrsc.server.plugins.npcs.lumbridge;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Priest implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {// that could work
		if (player.getQuestStage(Quests.THE_RESTLESS_GHOST) == 1) {
			npcsay(player, n, "Have you got rid of the ghost yet?");
			say(player, n, "I can't find father Urhney at the moment");
			npcsay(player,
				n,
				"Well to get to the swamp he is in",
				"you need to go round the back of the castle",
				"The swamp is on the otherside of the fence to the south",
				"You'll have to go through the wood to the west to get round the fence",
				"Then you'll have to go right into the eastern depths of the swamp");
			return;
		}
		if (player.getQuestStage(Quests.THE_RESTLESS_GHOST) >= 2) {
			npcsay(player, n, "Have you got rid of the ghost yet?");
			if (player.getQuestStage(Quests.THE_RESTLESS_GHOST) == 2) {
				say(player, n, "I had a talk with father Urhney",
					"He has given me this funny amulet to talk to the ghost with");
				npcsay(player, n, "I always wondered what that amulet was",
					"Well I hope it's useful. Tell me if you get rid of the ghost");
			} else if (player.getQuestStage(Quests.THE_RESTLESS_GHOST) == 3
				&& !player.getCarriedItems().hasCatalogID(ItemId.QUEST_SKULL.id(), Optional.of(false))) {
				say(
					player,
					n,
					"I've found out that the ghost's corpse has lost its skull",
					"If I can find the skull the ghost will go");
				npcsay(player, n, "That would explain it",
					"Well I haven't seen any skulls");
				say(player, n, "Yes I think a warlock has stolen it");
				npcsay(player, n, "I hate warlocks", "Ah well good luck");
			} else if (player.getQuestStage(Quests.THE_RESTLESS_GHOST) == 3
				&& player.getCarriedItems().hasCatalogID(ItemId.QUEST_SKULL.id(), Optional.of(false))) {
				say(player, n, "I've finally found the ghost's skull");
				npcsay(player, n,
					"Great. Put it in the ghost's coffin and see what happens!");
			}
			return;
		}
		npcsay(player, n, "Welcome to the church of holy Saradomin");
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("Who's Saradomin?") {
			@Override
			public void action() {
				npcsay(player,
					n,
					"Surely you have heard of the God, Saradomin?",
					"He who creates the forces of goodness and purity in this world?",
					"I cannot believe your ignorance!",
					"This is the God with more followers than any other!",
					"At least in these parts!",
					"He who along with his brothers Guthix and Zamorak created this world");
				new Menu().addOptions(new Option("Oh that Saradomin") {
					@Override
					public void action() {
						npcsay(player, n, "There is only one Saradomin");
					}
				}, new Option("Oh sorry I'm not from this world") {
					@Override
					public void action() {
						npcsay(player, n, "That's strange",
							"I thought things not from this world were all slime and tenticles");
						new Menu()
							.addOptions(
								new Option(
									"You don't understand. This is a computer game") {
									@Override
									public void action() {
										npcsay(player, n,
											"I beg your pardon?");
										say(player, n, "Never mind");
									}
								},
								new Option(
									"I am - do you like my disguise?") {
									@Override
									public void action() {
										npcsay(player, n,
											"Aargh begone foul creature from another dimension");
										say(player, n,
											"Ok, Ok, It was a joke");
									}
								}).showMenu(player);
					}
				}).showMenu(player);
			}
		});
		defaultMenu.addOption(new Option("Nice place you've got here") {
			@Override
			public void action() {
				npcsay(player, n, "It is, isn't it?", "It was built 230 years ago");
			}
		});
		if (player.getQuestStage(Quests.THE_RESTLESS_GHOST) <= 0) {
			defaultMenu.addOption(new Option("I'm looking for a quest") {
				@Override
				public void action() {
					if (player.getQuestStage(Quests.THE_RESTLESS_GHOST) == 0) {
						npcsay(player, n,
							"That's lucky, I need someone to do a quest for me");
						say(player, n, "Ok I'll help");
						npcsay(player,
							n,
							"Ok the problem is, there is a ghost in the church graveyard",
							"I would like you to get rid of it",
							"If you need any help",
							"My friend father Urhney is an expert on ghosts",
							"I believe he is currently living as a hermit",
							"He has a little shack somewhere in the swamps south of here",
							"I'm sure if you told him that I sent you he'd be willing to help",
							"My name is father Aereck by the way",
							"Be careful going through the swamps",
							"I have heard they can be quite dangerous");
						player.updateQuestStage(Quests.THE_RESTLESS_GHOST, 1);
					} else {
						npcsay(player, n, "Sorry I only had the one quest");
					}
				}
			});
		}

		defaultMenu.showMenu(player);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.PRIEST.id();
	}
}
