package com.openrsc.server.plugins.npcs.ardougne.east;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import java.time.Instant;

import static com.openrsc.server.plugins.Functions.*;

public class GemMerchant implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 60000 * 5, 150, 80, 3, new Item(164,
			2), new Item(163, 1), new Item(162, 1), new Item(161,
					0));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(p.getCache().hasKey("gemStolen") && (Instant.now().getEpochSecond() < p.getCache().getLong("gemStolen") + 1200)) {
			npcTalk(p, n, "Do you really think I'm going to buy something",
					"That you have just stolen from me",
					"guards guards");

			Npc attacker = getNearestNpc(p, 324, 5); // Hero first
			if (attacker == null)
				attacker = getNearestNpc(p, 323, 5); // Paladin second
			if (attacker == null)
				attacker = getNearestNpc(p, 322, 5); // Knight third
			if (attacker == null)
				attacker = getNearestNpc(p, 321, 5); // Guard fourth

			if (attacker != null)
				attacker.setChasing(p);

		} else {
			npcTalk(p, n, "Here, look at my lovely gems");
			int menu = showMenu(p, n, "Ok show them to me", "I'm not interested thankyou");
			if(menu == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			} 
		}
	}
	
	// WHEN STEALING AND CAUGHT BY A MERCHANT ("Hey thats mine");
	// Delay player busy (3000); after stealing and Npc shout out to you.

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 330;
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
