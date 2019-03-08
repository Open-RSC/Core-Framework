package com.openrsc.server.plugins.npcs.portsarim;

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

public class Bartender implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.BARTENDER_PORTSARIM.id();
	}
	
	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("Could i buy a beer please?") {
			@Override
			public void action() {
				npcTalk(p, n, "Sure that will be 2 gold coins please");
				if (hasItem(p, ItemId.COINS.id(), 2)) {
					playerTalk(p, n, "Ok here you go thanks");
					p.getInventory().remove(ItemId.COINS.id(), 2);
					p.message("you buy a pint of beer");
					addItem(p, ItemId.BEER.id(), 1);
				} else {
					p.message("You dont have enough coins for the beer");
				}
			}
		});
		if (p.getQuestStage(Quests.GOBLIN_DIPLOMACY) == 0) {
			defaultMenu.addOption(new Option(
				"Not very busy in here today is it?") {
				@Override
				public void action() {
					npcTalk(p,
						n,
						"No it was earlier",
						"There was a guy in here saying the goblins up by the mountain are arguing again",
						"Of all things about the colour of their armour",
						"Knowing the goblins, it could easily turn into a full blown war",
						"Which wouldn't be good",
						"Goblin wars make such a mess of the countryside");
					playerTalk(p, n,
						"Well if I have time I'll see if I can go and knock some sense into them");
					p.updateQuestStage(Quests.GOBLIN_DIPLOMACY, 1); // remember
					// quest
					// starts
					// here.
				}
			});
		} else if (p.getQuestStage(Quests.GOBLIN_DIPLOMACY) >= 1
			|| p.getQuestStage(Quests.GOBLIN_DIPLOMACY) == -1) { // TODO
			defaultMenu.addOption(new Option(
				"Have you heard any more rumours in here?") {
				@Override
				public void action() {
					npcTalk(p, n, "No it hasn't been very busy lately");
				}
			});
		}
		if (p.getCache().hasKey("barcrawl") && !p.getCache().hasKey("barsix")) {
			defaultMenu.addOption(new Option(
				"I'm doing Alfred Grimhand's barcrawl") {
				@Override
				public void action() {
					npcTalk(p, n, "Are you sure you look a bit skinny for that");
					playerTalk(p, n,
						"Just give me whatever drink I need to drink here");
					npcTalk(p, n,
						"Ok one black skull ale coming up, 8 coins please");
					if (hasItem(p, ItemId.COINS.id(), 8)) {
						p.getInventory().remove(ItemId.COINS.id(), 8);
						message(p, "You buy a black skull ale",
							"You drink your black skull ale",
							"Your vision blurs",
							"The bartender signs your card");
						p.getCache().store("barsix", true);
						playerTalk(p, n, "hiccup", "hiccup");
					} else {
						playerTalk(p, n, "I don't have 8 coins with me");
					}
				}
			});
		}
		defaultMenu.showMenu(p);
	}

}
