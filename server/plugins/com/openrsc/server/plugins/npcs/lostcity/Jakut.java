package com.openrsc.server.plugins.npcs.lostcity;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public final class Jakut implements ShopInterface, TalkToNpcExecutiveListener,
	TalkToNpcListener {

	private final Shop shop = new Shop(false, 3000, 100, 60, 2,
		new Item(ItemId.DRAGON_SWORD.id(), 2));

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		npcTalk(p, n, "Dragon swords, get your Dragon swords",
			"Straight from the plane of frenaskrae");

		int option = showMenu(p, n, false, //do not send over
			"Yes please", "No thankyou, I'm just browsing the marketplace");
		switch (option) {
			case 0:
				playerTalk(p, n, "Yes Please");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
				break;
			case 1:
				playerTalk(p, n, "No thankyou, I'm just browsing the marketplace");
				break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.JAKUT.id();
	}

	@Override
	public Shop[] getShops() {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

}
