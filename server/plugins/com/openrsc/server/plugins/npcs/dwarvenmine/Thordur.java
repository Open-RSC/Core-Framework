package com.openrsc.server.plugins.npcs.dwarvenmine;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.*;

public class Thordur implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		playerTalk(p, n, "Hello");
		npcTalk(p,n, "Hello adventurer",
				"I run a tourist attraction",
				"called the Black Hole",
				"I sell a handy disk for 10 coins",
				"to get you there",
				"Would you like to buy one?");
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("Yes please") {
			@Override
			public void action() {
				if(!hasItem(p, 10, 10)) {
					playerTalk(p, n,
							"Oh dear I don't actually seem to have enough money");
				}
				else {
					p.getInventory().remove(10, 10);
					addItem(p, 387, 1);
				}
			}
		});
		defaultMenu.addOption(new Option("No thankyou") {
			@Override
			public void action() {
				//NOTHING
			}
		});
		defaultMenu.showMenu(p);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 175;
	}

}
