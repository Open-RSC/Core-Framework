package com.openrsc.server.plugins.authentic.npcs.lumbridge;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Urhney implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "Go away, I'm meditating");

		ArrayList<String> options = new ArrayList<>();
		boolean beforeAmulet = player.getQuestStage(Quests.THE_RESTLESS_GHOST) == 1
			&& !player.getCarriedItems().hasCatalogID(ItemId.AMULET_OF_GHOSTSPEAK.id(), Optional.empty());
		boolean afterAmulet = player.getQuestStage(Quests.THE_RESTLESS_GHOST) >= 2
			&& !player.getCarriedItems().hasCatalogID(ItemId.AMULET_OF_GHOSTSPEAK.id(), Optional.empty());
		if (beforeAmulet) {
			options.add("Father Aereck sent me to talk to you");
		}
		else if (afterAmulet) {
			options.add("I've lost the amulet");
		}
		options.add("Well that's friendly");
		options.add("I've come to repossess your house");
		String[] finalOptions = new String[options.size()];
		int option = multi(player, n, options.toArray(finalOptions));

		if (option == 2) {
			repossessDialog(player, n);
		}

		else if (option == 1) {
			if (beforeAmulet || afterAmulet) {
				npcsay(player, n, "I said go away!");
				say(player, n, "Ok, ok");
			}
			else {
				repossessDialog(player, n);
			}
		}

		else if (option == 0) {
			if (beforeAmulet) {
				beforeAmuletDialog(player, n);
			}
			else if (afterAmulet) {
				mes("Father Urhney sighs");
				delay(3);

				npcsay(player, n, "How careless can you get",
					"Those things aren't easy to come by you know",
					"It's a good job I've got a spare");
				give(player, ItemId.AMULET_OF_GHOSTSPEAK.id(), 1);
				mes("Father Urhney hands you an amulet");
				delay(3);
				npcsay(player, n, "Be more careful this time");
				say(player, n, "Ok I'll try to be");
			}
			else {
				npcsay(player, n, "I said go away!");
				say(player, n, "Ok, ok");
			}
		}
	}

	private void repossessDialog(Player player, Npc n) {
		npcsay(player, n, "Under what grounds?");
		int option = multi(player, n,
			"Repeated failure on mortgage payments",
			"I don't know, I just wanted this house"
		);
		if (option == 0) {
			npcsay(player, n, "I don't have a mortgage", "I built this house myself");
			say(player, n, "Sorry I must have got the wrong address", "All the houses look the same around here");
		}
		else if (option == 1) {
			npcsay(player, n, "Oh go away and stop wasting my time");
		}
	}

	private void beforeAmuletDialog(Player player, Npc n) {
		npcsay(player, n, "I suppose I'd better talk to you then",
			"What problems has he got himself into this time?");
		int option = multi(player, n,
			"He's got a ghost haunting his graveyard",
			"You mean he gets himself into lots of problems?"
		);

		if (option == 0) {
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
			mes("Father Urhney hands you an amulet");
			delay(3);
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
			player.updateQuestStage(Quests.THE_RESTLESS_GHOST, 2);
		}

		else if (option == 1) {
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
			mes("Father Urhney hands you an amulet");
			delay(3);
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
			player.updateQuestStage(Quests.THE_RESTLESS_GHOST, 2);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.URHNEY.id();
	}

}
