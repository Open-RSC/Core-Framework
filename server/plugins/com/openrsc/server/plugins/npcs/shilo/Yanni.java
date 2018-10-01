package com.openrsc.server.plugins.npcs.shilo;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Yanni implements TalkToNpcListener, TalkToNpcExecutiveListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	public static final int YANNI = 624;

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == YANNI) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == YANNI) {
			playerTalk(p, n, "Hello there!");
			npcTalk(p, n, "Greetings Bwana!",
					"My name is Yanni and I buy and sell antiques ",
					"and other interesting items.",
					"If you have any interesting items that you might",
					"want to sell me, please let me see them and I'll",
					"offer you a fair price.",
					"Would you like me to have a look at your items",
					"and give you a quote?");
			int menu = showMenu(p, n,
					"Yes please!",
					"Maybe some other time?");
			if(menu == 0) {
				npcTalk(p, n, "Great Bwana!");
				if(hasItem(p, 959)) {
					npcTalk(p, n, "I'll give you 100 Gold for your tattered scroll");
				}
				if(hasItem(p, 960)) {
					npcTalk(p, n, "I'll give you 100 Gold for your crumpled scroll");
				}
				if(hasItem(p, 961)) {
					npcTalk(p, n, "I'll give you 100 Gold for your Bervirius Tomb Notes.");
				}
				if(hasItem(p, 852)) {
					npcTalk(p, n, "Great I'll give you 1000 Gold for your Beads of the Dead.");
				}
				if(hasItem(p, 959) || hasItem(p, 960) || hasItem(p, 961) || hasItem(p, 852)) {
					npcTalk(p, n, "Those are the items I am interested in Bwana.",
							"If you want to sell me those items, simply show them to me.");
				} else {
					npcTalk(p, n, "Sorry Bwana, you have nothing I am interested in.");
				}

			} else if(menu == 1) {
				npcTalk(p, n, "Sure thing.",
						"Have a nice day Bwana.");
			}
		}
	}

	@Override
	public boolean blockInvUseOnNpc(Player p, Npc npc, Item item) {
		if(npc.getID() == YANNI) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc npc, Item item) {
		if(npc.getID() == YANNI) {
			switch(item.getID()) {
			case 972:
				npcTalk(p, npc, "Great item, here's 500 Gold for it.");
				removeItem(p, 972, 1);
				addItem(p, 10, 500);
				p.message("You sell the Locating Crystal.");
				break;
			case 959:
				npcTalk(p, npc, "Great item, here's 100 Gold for it.");
				removeItem(p, 959, 1);
				addItem(p, 10, 100);
				p.message("You sell the Tattered Scroll.");
				break;
			case 960:
				npcTalk(p, npc, "Great item, here's 100 Gold for it.");
				removeItem(p, 960, 1);
				addItem(p, 10, 100);
				p.message("You sell the crumpled Scroll.");
				break;
			case 961:
				npcTalk(p, npc, "Great item, here's 100 Gold for it.");
				removeItem(p, 961, 1);
				addItem(p, 10, 100);
				p.message("You sell the Bervirius Tomb Notes.");
				break;
			case 852:
				npcTalk(p, npc, "Great item, here's 1000 Gold for it.");
				removeItem(p, 852, 1);
				addItem(p, 10, 1000);
				p.message("You sell Beads of the Dead.");
				break;
			default:
				p.message("Nothing interesting happens");
				break;
			}
		}
	}
}
