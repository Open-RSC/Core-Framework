package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;
import com.openrsc.server.plugins.quests.members.digsite.DigsiteExaminer;

import static com.openrsc.server.plugins.Functions.*;

public class Curator implements TalkToNpcExecutiveListener, TalkToNpcListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener {
	public boolean blockTalkToNpc(final Player player, final Npc npc) {
		if (npc.getID() == 39) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Welcome to the museum of Varrock");
		if (p.getInventory().hasItemId(53) && p.getInventory().hasItemId(54)) {
			if (p.getQuestStage(Constants.Quests.SHIELD_OF_ARRAV) == 5) {
				playerTalk(p, n,
						"I have retrieved the shield of Arrav and I would like to claim my reward");
				npcTalk(p, n, "The shield of Arrav?", "Let me see that");
				message(p, "The curator peers at the shield");
				npcTalk(p,
						n,
						"This is incredible",
						"That shield has been missing for about twenty five years",
						"Well give me the shield",
						"And I'll write you out a certificate",
						"Saying you have returned the shield",
						"So you can claim your reward from the king");
				playerTalk(
						p,
						n,
						"Can I have two certificates?",
						"I needed significant help from a friend to get the shield",
						"We'll split the reward");
				npcTalk(p, n, "Oh ok");
				message(p, "You hand over the shield parts");
				removeItem(p, 53, 1);
				removeItem(p, 54, 1);
				message(p, "The curator writes out two certificates");
				addItem(p, 61, 1);
				addItem(p, 61, 1);
				npcTalk(p, n, "Take these to the king",
						"And he'll pay you both handsomely");

				return;
			}
		}
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("Have you any interesting news?") {
			@Override
			public void action() {
				npcTalk(p, n, "No, I'm only interested in old stuff");
			}
		});
		defaultMenu.addOption(new Option(
				"Do you know where I could find any treasure?") {
			@Override
			public void action() {
				npcTalk(p, n, "This museum is full of treasures");
				playerTalk(p, n, "No, I meant treasures for me");
				npcTalk(p, n, "Any treasures this museum knows about",
						"It aquires");
			}
		});
		defaultMenu.showMenu(p);
	}

	@Override
	public boolean blockInvUseOnNpc(Player p, Npc n, Item item) {
		if(n.getID() == 39 && (item.getID() == DigsiteExaminer.UNSTAMPED_LETTER 
				|| item.getID() == DigsiteExaminer.LEVEL_1_CERTIFICATE
				|| item.getID() == DigsiteExaminer.LEVEL_2_CERTIFICATE
				|| item.getID() == DigsiteExaminer.LEVEL_3_CERTIFICATE)) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc n, Item item) {
		if(n.getID() == 39) {
			if(item.getID() == DigsiteExaminer.UNSTAMPED_LETTER) {
				playerTalk(p, n, "I have been given this by the examiner at the digsite",
						"Can you stamp this for me ?");
				npcTalk(p, n, "What have we here ?",
						"A letter of recommendation indeed",
						"Normally I wouldn't do this",
						"But in this instance I don't see why not",
						"There you go, good luck student...");
				removeItem(p, DigsiteExaminer.UNSTAMPED_LETTER, 1);
				addItem(p, DigsiteExaminer.STAMPED_LETTER, 1);
				npcTalk(p, n, "Be sure to come back and show me your certificates",
						"I would like to see how you get on");
				playerTalk(p, n, "Okay, I will, thanks, see you later");
			}
			if(item.getID() == DigsiteExaminer.LEVEL_1_CERTIFICATE) {
				playerTalk(p, n, "Look what I have been awarded");
				removeItem(p, DigsiteExaminer.LEVEL_1_CERTIFICATE, 1);
				npcTalk(p, n, "Well that's great, well done",
						"I'll take that for safekeeping",
						"Come and tell me when you are the next level");
			}
			if(item.getID() == DigsiteExaminer.LEVEL_2_CERTIFICATE) {
				npcTalk(p, n, "Excellent work!");
				removeItem(p, DigsiteExaminer.LEVEL_2_CERTIFICATE, 1);
				npcTalk(p, n, "I'll take that for safekeeping",
						"Remember to come and see me when you have graduated");
			}
			if(item.getID() == DigsiteExaminer.LEVEL_3_CERTIFICATE) {
				playerTalk(p, n, "Look at this certificate, curator...");
				npcTalk(p, n, "Well well, a level 3 graduate!",
						"I'll keep your certificate safe for you",
						"I feel I must reward you for your work...",
						"What would you prefer, something to eat or drink ?");
				int menu = showMenu(p, n,
						"Something to eat please",
						"Something to drink please");
				if(menu == 0) {
					removeItem(p, DigsiteExaminer.LEVEL_3_CERTIFICATE, 1);
					npcTalk(p, n, "Very good, come and eat this cake I baked");
					playerTalk(p, n, "Yum, thanks!");
					addItem(p, 332, 1);
				} else if(menu == 1) {
					removeItem(p, DigsiteExaminer.LEVEL_3_CERTIFICATE, 1);
					npcTalk(p, n, "Certainly, have this...");
					addItem(p, 866, 1);
					playerTalk(p, n, "A cocktail?");
					npcTalk(p, n, "It's a new recipie from the gnome kingdom",
							"You'll like it I'm sure");
					playerTalk(p, n, "Cheers!");
					npcTalk(p, n, "Cheers!");
				}
			}
		}
	}
}
