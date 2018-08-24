package com.openrsc.server.plugins.npcs.yanille;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

public final class BartenderDragonInn implements TalkToNpcExecutiveListener,
		TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if (n.getID() == 529) {
			npcTalk(p, n, "What can I get you?");
			playerTalk(p, n, "What's on the menu?");
			npcTalk(p, n, "Dragon bitter and Greenmans ale");
			Menu defaultMenu = new Menu();
			defaultMenu.addOption(new Option("I'll give it a miss I think") {
				@Override
				public void action() {
					npcTalk(p, n, "Come back when you're a little thirstier");
				}
			});
			defaultMenu.addOption(new Option("I'll try the dragon bitter") {
				@Override
				public void action() {
					npcTalk(p, n, "Ok, that'll be two coins");
					if (hasItem(p, 10, 2)) {
						p.message("You buy a pint of dragon bitter");
						addItem(p, 829, 1);
						removeItem(p, 10, 2);
					} else {
						playerTalk(p, n,"Oh dear. I don't seem to have enough money");
					}
				}
			});
			defaultMenu.addOption(new Option("Can I have some greenmans ale?") {
					@Override
					public void action() {
						npcTalk(p, n, "Ok, that'll be ten coins");
						if (hasItem(p, 10, 10)) {
							p.message("You buy a pint of ale");
							addItem(p, 830, 1);
							removeItem(p, 10, 10);
						} else {
							playerTalk(p, n, "Oh dear. I don't seem to have enough money");
						}
					}
				});
			defaultMenu.showMenu(p);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 529) {
			return true;
		}
		return false;
	}

}
