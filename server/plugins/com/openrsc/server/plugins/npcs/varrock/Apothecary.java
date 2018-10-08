package com.openrsc.server.plugins.npcs.varrock;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.Constants;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

public final class Apothecary implements TalkToNpcExecutiveListener,
		TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (p.getQuestStage(Constants.Quests.ROMEO_N_JULIET) == 4) {
			playerTalk(p, n, "Apothecary. Father Lawrence sent me",
					"I need some Cadava potion to help Romeo and Juliet");
			npcTalk(p, n, "Cadava potion. Its pretty nasty. And hard to make",
					"Wing of Rat, Tail of frog. Ear of snake and horn of dog",
					"I have all that, but i need some cadavaberries",
					"You will have to find them while i get the rest ready",
					"Bring them here when you have them. But be careful. They are nasty");
			p.updateQuestStage(Constants.Quests.ROMEO_N_JULIET, 5);
			return;
		} else if (p.getQuestStage(Constants.Quests.ROMEO_N_JULIET) == 5) {
			if (!p.getInventory().hasItemId(55)) {
				npcTalk(p, n, "Keep searching for the berries",
						"they are needed for the potion");
			} else {
				npcTalk(p, n, "Well done. You have the berries");
				message(p, "You hand over the berries");
				p.getInventory().remove(p.getInventory().getLastIndexById(55));
				p.message("Which the apothecary shakes up in vial of strange liquid");
				npcTalk(p, n, "Here is what you need");
				p.message("The apothecary gives you a Cadava potion");
				p.getInventory().add(new Item(57));
				p.message("I'm meant to give this to Juliet");
				p.updateQuestStage(Constants.Quests.ROMEO_N_JULIET, 6);
			}
			return;
		}
		npcTalk(p,n, "I am the apothecary", "I have potions to brew. Do you need anything specific?");
		int option;
		if (!Constants.GameServer.WANT_EXPERIENCE_ELIXIRS)
      option = showMenu(p,n, "Can you make a strength potion?",
          "Do you know a potion to make hair fall out?",
          "Have you got any good potions to give away?");
		else
			option = showMenu(p,n, "Can you make a strength potion?",
					"Do you know a potion to make hair fall out?",
					"Have you got any good potions to give away?",
					"Do you have any experience elixir?");

		if(option == 0) {
			if (hasItem(p, 10, 5)
					&& hasItem(p, 220, 1)
					&& hasItem(p, 219, 1)) {
				playerTalk(p,n,  "I have the root and spider eggs needed to make it",
						"Well give me them and 5 gold and I'll make you your potion");

				message(p, "Apothecary: starts brewing and fixes to a strength potion");
				p.setBatchEvent(new BatchEvent(p, 600, 14) {
					@Override
					public void action() {
						if (p.getInventory().countId(10) < 5) {
							p.message("You don't have all the ingredients");
							interrupt();
							return;
						}
						if (p.getInventory().countId(220) < 1 || p.getInventory().countId(219) < 1) {
							p.message("You don't have all the ingredients");
							interrupt();
							return;
						}
						p.message("Apothecary gives you a strength potion.");
						removeItem(p, 10, 5);
						removeItem(p, 220, 1);
						removeItem(p, 219, 1);
						addItem(p, 221, 1);
					}
				});
			} else {
				npcTalk(p, n,
						"Yes. But the ingredients are a little hard to find",
						"If you ever get them I will make it for you... for a cost",
						"You'll need to find the eggs of the deadly red spider",
						"And a limpwurt root",
						"Oh and you'll have to pay me 5 coins");
				playerTalk(p, n, "Ok, I'll look out for them");

			}
		} 
		else if(option == 1) {
			npcTalk(p,n, "I do indeed. I gave it to my mother. That's why I now live alone");
		}
		else if(option == 2) {
			npcTalk(p,n, "Sorry, charity is not my strongest point");
		}
		else if(option == 3 && Constants.GameServer.WANT_EXPERIENCE_ELIXIRS) {
			npcTalk(p, n, "Yes, it's my most mysterious and special elixir",
					"It has a strange taste and sure does give you a rush",
					"I would know..",
					"I sell it for 5,000gp");
			int menu = showMenu(p, n, "Yes please", "No thankyou");
			if(menu == 0) {
				long lastElixir = 0;
				if(p.getCache().hasKey("buy_elixir")) {
					lastElixir = p.getCache().getLong("buy_elixir");
				}
				if (System.currentTimeMillis() - lastElixir < 24 * 60 * 60 * 1000) {
					npcTalk(p, n, "Wait.. it's you, I recently made an elixir for you",
							"I don't want to poison my customers",
							"You'll need to wait before I make you a new one");
					int time = (int) (86400 - ((System.currentTimeMillis() - lastElixir) / 1000));
					p.message("You need to wait: " + DataConversions.getDateFromMsec(time * 1000));
					return;
				}
				if(hasItem(p, 10, 5000)) {
					playerTalk(p, n, "I have the 5,000 gold with me");
					p.message("you give Apothecary 5,000 gold");
					removeItem(p, 10, 5000);
					message(p, "Apothecary: starts brewing and fixes to a elixir");
					p.message("Apothecary gives you a mysterious experience elixir.");
					addItem(p, 2106, 1);
					p.getCache().store("buy_elixir", System.currentTimeMillis());
				} else {
					playerTalk(p, n, "Oops, I don't have enough coins");
					npcTalk(p, n, "Ok. I need my money, the ingredients are hard to find");
				}
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 33;
	}
}
