package com.openrsc.server.plugins.quests.members.legendsquest.npcs.shop;

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

public final class SiegfriedErkel implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 60000, 150, 50, 2,
			new Item(796, 6), new Item(596, 5), new Item(52, 4),
			new Item(421, 3), new Item(1276, 1), new Item(1288, 3));

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if(p.getQuestStage(Constants.Quests.LEGENDS_QUEST) != -1) {
			npcTalk(p, n, "I'm sorry but the services of this shop are only for ",
					"the pleasure of those who are rightfull members of the ",
					"Legends Guild. I would get into serious trouble if I sold ",
					"a non-member an item from this store.");
		} else {
			npcTalk(p, n, "Hello there and welcome to the shop of useful items.",
					"Can I help you at all?");
			int option = showMenu(p, n, "Yes please. What are you selling?",
					"No thanks");
			switch (option) {
			case 0:
				npcTalk(p, n, "Take a look");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
				break;
			case 1:
				npcTalk(p, n, "Ok, well, if you change your mind, do pop back.");
				break;
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 779;
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
