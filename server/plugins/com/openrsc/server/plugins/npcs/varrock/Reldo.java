package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public final class Reldo implements TalkNpcTrigger {
	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.RELDO.id();
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (player.getCache().hasKey("read_arrav")
			&& player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 1 || player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 2) {
			say(player, n, "OK I've read the book",
				"Do you know where I can find the Phoenix Gang");
			npcsay(player, n, "No I don't",
				"I think I know someone who will though",
				"Talk to Baraek, the fur trader in the market place",
				"I've heard he has connections with the Phoenix Gang");
			say(player, n, "Thanks, I'll try that");
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 1) {
				player.updateQuestStage(Quests.SHIELD_OF_ARRAV, 2);
			}
			return;
		}

		say(player, n, "Hello");
		npcsay(player, n, "Hello stranger");

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
		int option = multi(player, n, options.toArray(finalOptions));

		if (option == 3) {
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 0
				&& player.getQuestStage(Quests.THE_KNIGHTS_SWORD) == 1) {
				knightsSwordDialog(player, n);
			}
		}

		else if (option == 2) {
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 0) {
				npcsay(player, n, "I'm the palace librarian");
				say(player, n, "Ah that's why you're in the library then");
				npcsay(player, n, "Yes",
					"Though I might be in here even if I didn't work here",
					"I like reading");
			}
			else if (player.getQuestStage(Quests.THE_KNIGHTS_SWORD) == 1) {
				knightsSwordDialog(player, n);
			}
		}

		else if (option == 1) {
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 0) {
				npcsay(player, n, "No, sorry. I'm not the trading type");
				say(player, n, "ah well");
			}
			else {
				npcsay(player, n, "I'm the palace librarian");
				say(player, n, "Ah that's why you're in the library then");
				npcsay(player, n, "Yes",
					"Though I might be in here even if I didn't work here",
					"I like reading");
			}
		}

		else if (option == 0) {
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 0) {
				shieldOfArravDialog(player, n);
			}

			else {
				npcsay(player, n, "No, sorry. I'm not the trading type");
				say(player, n, "ah well");
			}
		}
	}

	private void knightsSwordDialog(Player player, Npc n) {
		npcsay(player,
			n,
			"The Imcando Dwarves, you say?",
			"They were the world's most skilled smiths about a hundred years ago",
			"They used secret knowledge",
			"Which they passed down from generation to generation",
			"Unfortunatly about a century ago the once thriving race",
			"Was wiped out during the barbarian invasions of that time");
		say(player, n, "So are there any Imcando left at all?");
		npcsay(player,
			n,
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

	private void shieldOfArravDialog(Player player, Npc n) {
		npcsay(player, n, "I don't think there's any here");
		delay(config().GAME_TICK);
		npcsay(player, n, "Let me think actually",
			"If you look in a book",
			"called the shield of Arrav",
			"You'll find a quest in there",
			"I'm not sure where the book is mind you",
			"I'm sure it's somewhere in here");
		say(player, n, "Thankyou");
		player.updateQuestStage(Quests.SHIELD_OF_ARRAV, 1);
	}
}
