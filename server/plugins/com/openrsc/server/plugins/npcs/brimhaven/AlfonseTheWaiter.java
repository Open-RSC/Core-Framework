package com.openrsc.server.plugins.npcs.brimhaven;

import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.Constants;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class AlfonseTheWaiter implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 10000, 110, 75, 2,
			new Item(362, 5), new Item(551, 5), new Item(367, 5), new Item(373, 3), new Item(370, 2));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == 260) {
			npcTalk(p,n, "Welcome to the shrimp and parrot",
					"Would you like to order sir?");
			int menu;
			if(p.getQuestStage(Constants.Quests.HEROS_QUEST) != 1 && p.getQuestStage(Constants.Quests.HEROS_QUEST) != 2 && !p.getCache().hasKey("pheonix_mission") && !p.getCache().hasKey("pheonix_alf")) {
				menu = showMenu(p,n,
						"Yes please",
						"No thankyou");
			} else {
				menu = showMenu(p,n,
						"Yes please",
						"No thankyou",
						"Do you sell Gherkins?");
			}
			if(menu == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			} else if(menu == 2) {
				npcTalk(p,n, "Hmm ask Charlie the cook round the back",
						"He may have some Gherkins for you");
				message(p, "Alfonse winks");
				p.getCache().store("talked_alf", true);
				p.getCache().remove("pheonix_alf");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == 260) {
			return true;
		}
		return false;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return true;
	}

}
