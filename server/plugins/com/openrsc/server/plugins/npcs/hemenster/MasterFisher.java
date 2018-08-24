package com.openrsc.server.plugins.npcs.hemenster;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class MasterFisher implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 368;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(p.getLocation().inEdgeville()) {
			if(p.isIronMan(1) || p.isIronMan(2) || p.isIronMan(3)) {
				p.message("You are an Iron Man. You stand alone.");
				return;
			}
			if (!p.canUsePool()) {
				p.message("You have just died, you must wait for "+ p.secondsUntillPool()+ " seconds before you refill for food");
				return;
			}
			npcTalk(p, n, "Would you like some trouts?");
			int menu = showMenu(p, n, "Yes Please", "No Thanks");
			if(menu == 0) {
				while(!p.getInventory().full()) {
					p.getInventory().add(new Item(359, 1));
				}
				p.message("Your inventory is now filled with trouts");
			} else if(menu == 1) {
				npcTalk(p, n, "Maybe another time then", 
						"you can visit our facilities at fishing guild located in Kandarin perhaps");
			}
		} else {
			npcTalk(p, n, "Hello, welcome to the fishing guild.",
					"Please feel free to make use of any of our facilities");
		}
	}
}
