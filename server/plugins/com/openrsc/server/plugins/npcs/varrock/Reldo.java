package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.*;

public final class Reldo implements TalkToNpcListener,
	TalkToNpcExecutiveListener {
	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.RELDO.id();
	}

	/**
	 * Man, this is the whole reldo with shield of arrav. dont tell me that this
	 * is bad choice.
	 */
	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		Menu defaultMenu = new Menu();
		if (p.getCache().hasKey("read_arrav")
			&& p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 1 || p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 2) {
			playerTalk(p, n, "OK I've read the book",
				"Do you know where I can find the Phoenix Gang");
			npcTalk(p, n, "No I don't",
				"I think I know someone who will though",
				"Talk to Baraek, the fur trader in the market place",
				"I've heard he has connections with the Phoenix Gang");
			playerTalk(p, n, "Thanks, I'll try that");
			if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 1) {
				p.updateQuestStage(Quests.SHIELD_OF_ARRAV, 2);
			}
			return;
		}
		playerTalk(p, n, "Hello");
		npcTalk(p, n, "Hello stranger");
		if (p.getQuestStage(Constants.Quests.SHIELD_OF_ARRAV) == 0) {
			defaultMenu.addOption(new Option("I'm in search of a quest") {
				@Override
				public void action() {
					npcTalk(p, n, "I don't think there's any here");
					sleep(600);
					npcTalk(p, n, "Let me think actually",
						"If you look in a book",
						"called the shield of Arrav",
						"You'll find a quest in there",
						"I'm not sure where the book is mind you",
						"I'm sure it's somewhere in here");
					playerTalk(p, n, "Thankyou");
					p.updateQuestStage(Constants.Quests.SHIELD_OF_ARRAV, 1);
				}
			});
		}
		defaultMenu.addOption(new Option("Do you have anything to trade?") {
			@Override
			public void action() {
				npcTalk(p, n, "No, sorry. I'm not the trading type");
				playerTalk(p, n, "ah well");
			}
		});
		defaultMenu.addOption(new Option("What do you do?") {
			@Override
			public void action() {
				npcTalk(p, n, "I'm the palace librarian");
				playerTalk(p, n, "Ah that's why you're in the library then");
				npcTalk(p, n, "Yes",
					"Though I might be in here even if I didn't work here",
					"I like reading");
			}
		});
		if (p.getQuestStage(Quests.THE_KNIGHTS_SWORD) == 1) {
			defaultMenu.addOption(new Option(
				"What do you know about the Imcando dwarves?") {
				@Override
				public void action() {
					npcTalk(p,
						n,
						"The Imcando Dwarves, you say?",
						"They were the world's most skilled smiths about a hundred years ago",
						"They used secret knowledge",
						"Which they passed down from generation to generation",
						"Unfortunatly about a century ago the once thriving race",
						"Was wiped out during the barbarian invasions of that time");
					playerTalk(p, n, "So are there any Imcando left at all?");
					npcTalk(p,
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
					p.updateQuestStage(Quests.THE_KNIGHTS_SWORD, 2);
				}
			});
		}

		defaultMenu.showMenu(p);
	}
}
