package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.*;

public class Bartender implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.BARTENDER_PORTSARIM.id();
	}

	@Override
	public void onTalkNpc(final Player p, final Npc n) {
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("Could i buy a beer please?") {
			@Override
			public void action() {
				npcsay(p, n, "Sure that will be 2 gold coins please");
				if (ifheld(p, ItemId.COINS.id(), 2)) {
					say(p, n, "Ok here you go thanks");
					p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 2));
					p.message("you buy a pint of beer");
					give(p, ItemId.BEER.id(), 1);
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
					npcsay(p,
						n,
						"No it was earlier",
						"There was a guy in here saying the goblins up by the mountain are arguing again",
						"Of all things about the colour of their armour",
						"Knowing the goblins, it could easily turn into a full blown war",
						"Which wouldn't be good",
						"Goblin wars make such a mess of the countryside");
					say(p, n,
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
					npcsay(p, n, "No it hasn't been very busy lately");
				}
			});
		}
		if (p.getCache().hasKey("barcrawl") && !p.getCache().hasKey("barsix")) {
			defaultMenu.addOption(new Option(
				"I'm doing Alfred Grimhand's barcrawl") {
				@Override
				public void action() {
					npcsay(p, n, "Are you sure you look a bit skinny for that");
					say(p, n,
						"Just give me whatever drink I need to drink here");
					npcsay(p, n,
						"Ok one black skull ale coming up, 8 coins please");
					if (ifheld(p, ItemId.COINS.id(), 8)) {
						p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 8));
						Functions.mes(p, "You buy a black skull ale",
							"You drink your black skull ale",
							"Your vision blurs",
							"The bartender signs your card");
						p.getCache().store("barsix", true);
						say(p, n, "hiccup", "hiccup");
					} else {
						say(p, n, "I don't have 8 coins with me");
					}
				}
			});
		}
		defaultMenu.showMenu(p);
	}

}
