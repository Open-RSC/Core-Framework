package com.openrsc.server.plugins.npcs.falador;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class WysonTheGardener implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 116;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p,n, "i am the gardener round here", 
                          "do you have any gardening that needs doing?");
		int option = showMenu(p,n, "I'm looking for woad leaves", "Not right now thanks");
		if(option == 0) {
			npcTalk(p,n, "well luckily for you i may have some around here somewhere");
			playerTalk(p,n, "can i buy one please?");
			npcTalk(p,n, "how much are you willing to pay?");
			int sub_option = showMenu(p,n, "How about 5 coins?", "How about 10 coins?",
                                   "How about 15 coins?", "How about 20 coins?");
			if(sub_option == 2) {
				npcTalk(p,n, "mmmm ok that sounds fair.");
				if(removeItem(p, 10, 15)) {
					addItem(p, 281, 1);
					p.message("you give wyson 15 coins");
					p.message("wyson the gardener gives you some woad leaves");
				} else 
					playerTalk(p,n, "i dont have enough coins to buy the leaves. i'll come back later");
			}
			else if(sub_option == 3) {
				npcTalk(p,n, "i used to have plenty but someone kept stealing them off me");
				if(removeItem(p, 10, 20)) {
					addItem(p, 281, 2);
					p.message("you give wyson 20 coins");
					p.message("wyson the gardener gives you some woad leaves");
				} else 
					playerTalk(p,n, "i dont have enough coins to buy the leaves. i'll come back later");
			}
		}
	}

}
