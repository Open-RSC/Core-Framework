package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.*;

public class Curator implements TalkToNpcExecutiveListener, TalkToNpcListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener {
	public boolean blockTalkToNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.CURATOR.id();
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Welcome to the museum of Varrock");
		if (p.getInventory().hasItemId(ItemId.BROKEN_SHIELD_ARRAV_1.id()) && p.getInventory().hasItemId(ItemId.BROKEN_SHIELD_ARRAV_2.id())) {
			// curator authentically does not check if you already have a certificate in your inventory before triggering this
			if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 5) {
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
				removeItem(p, ItemId.BROKEN_SHIELD_ARRAV_1.id(), 1);
				removeItem(p, ItemId.BROKEN_SHIELD_ARRAV_2.id(), 1);
				message(p, "The curator writes out two certificates");
				addItem(p, ItemId.CERTIFICATE.id(), 1);
				addItem(p, ItemId.CERTIFICATE.id(), 1);
				npcTalk(p, n, "Take these to the king",
					"And he'll pay you both handsomely");

				return;
			}
		} else if (p.getInventory().hasItemId(ItemId.BROKEN_SHIELD_ARRAV_1.id()) || p.getInventory().hasItemId(ItemId.BROKEN_SHIELD_ARRAV_2.id())) {
			if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 5 || p.getQuestStage(Quests.SHIELD_OF_ARRAV) < 0) {
				// possible this triggers always, but confirmed that it does occur authentically after the quest is complete. (state < 0)
				playerTalk(p, n,
						"I have half the shield of Arrav here",
						"Can I get a reward");
				npcTalk(p, n, "Well it might be worth a small reward",
						"The entire shield would me worth much much more");
				playerTalk(p, n,
						"Ok I'll hang onto it",
						"And see if I can find the other half");
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
		if (n.getID() == NpcId.CURATOR.id() && (item.getID() == ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id()
			|| item.getID() == ItemId.LEVEL_1_CERTIFICATE.id()
			|| item.getID() == ItemId.LEVEL_2_CERTIFICATE.id()
			|| item.getID() == ItemId.LEVEL_3_CERTIFICATE.id())) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc n, Item item) {
		if (n.getID() == NpcId.CURATOR.id()) {
			if (item.getID() == ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id()) {
				playerTalk(p, n, "I have been given this by the examiner at the digsite",
					"Can you stamp this for me ?");
				npcTalk(p, n, "What have we here ?",
					"A letter of recommendation indeed",
					"Normally I wouldn't do this",
					"But in this instance I don't see why not",
					"There you go, good luck student...");
				removeItem(p, ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id(), 1);
				addItem(p, ItemId.STAMPED_LETTER_OF_RECOMMENDATION.id(), 1);
				npcTalk(p, n, "Be sure to come back and show me your certificates",
					"I would like to see how you get on");
				playerTalk(p, n, "Okay, I will, thanks, see you later");
			} else if (item.getID() == ItemId.STAMPED_LETTER_OF_RECOMMENDATION.id()) {
				npcTalk(p, n, "No, I don't want it back, thankyou");
			} else if (item.getID() == ItemId.LEVEL_1_CERTIFICATE.id()) {
				playerTalk(p, n, "Look what I have been awarded");
				removeItem(p, ItemId.LEVEL_1_CERTIFICATE.id(), 1);
				npcTalk(p, n, "Well that's great, well done",
					"I'll take that for safekeeping",
					"Come and tell me when you are the next level");
			} else if (item.getID() == ItemId.LEVEL_2_CERTIFICATE.id()) {
				playerTalk(p, n, "Look, I am level 2 now...");
				npcTalk(p, n, "Excellent work!");
				removeItem(p, ItemId.LEVEL_2_CERTIFICATE.id(), 1);
				npcTalk(p, n, "I'll take that for safekeeping",
					"Remember to come and see me when you have graduated");
			} else if (item.getID() == ItemId.LEVEL_3_CERTIFICATE.id()) {
				playerTalk(p, n, "Look at this certificate, curator...");
				npcTalk(p, n, "Well well, a level 3 graduate!",
					"I'll keep your certificate safe for you",
					"I feel I must reward you for your work...",
					"What would you prefer, something to eat or drink ?");
				int menu = showMenu(p, n,
					"Something to eat please",
					"Something to drink please");
				if (menu == 0) {
					removeItem(p, ItemId.LEVEL_3_CERTIFICATE.id(), 1);
					npcTalk(p, n, "Very good, come and eat this cake I baked");
					playerTalk(p, n, "Yum, thanks!");
					addItem(p, ItemId.CHOCOLATE_CAKE.id(), 1);
				} else if (menu == 1) {
					removeItem(p, ItemId.LEVEL_3_CERTIFICATE.id(), 1);
					npcTalk(p, n, "Certainly, have this...");
					addItem(p, ItemId.FRUIT_BLAST.id(), 1);
					playerTalk(p, n, "A cocktail ?");
					npcTalk(p, n, "It's a new recipie from the gnome kingdom",
						"You'll like it I'm sure");
					playerTalk(p, n, "Cheers!");
					npcTalk(p, n, "Cheers!");
				}
			}
		}
	}
}
