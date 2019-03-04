package com.openrsc.server.plugins.npcs.ardougne.east;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
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

public class SilverMerchant implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 60000 * 2, 100, 70, 2, new Item(ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(),
		2), new Item(ItemId.SILVER.id(), 1), new Item(ItemId.SILVER_BAR.id(), 1));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (p.getCache().hasKey("silverStolen") && (Instant.now().getEpochSecond() < p.getCache().getLong("silverStolen") + 1200)) {
			npcTalk(p, n, "Do you really think I'm going to buy something",
				"That you have just stolen from me",
				"guards guards");

			Npc attacker = getNearestNpc(p, NpcId.PALADIN.id(), 5); // Paladin first
			if (attacker == null)
				attacker = getNearestNpc(p, NpcId.KNIGHT.id(), 5); // Knight second
			if (attacker == null)
				attacker = getNearestNpc(p, NpcId.GUARD_ARDOUGNE.id(), 5); // Guard third

			if (attacker != null)
				attacker.setChasing(p);
		} else {
			npcTalk(p, n, "Silver! Silver!", "Best prices for buying and selling in all Kandarin!");
			int menu = showMenu(p, n, "Yes please", "No thankyou");
			if (menu == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}
		}
	}

	// WHEN STEALING AND CAUGHT BY A MERCHANT ("Hey thats mine");
	// Delay player busy (3000); after stealing and Npc shout out to you.

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.SILVER_MERCHANT.id();
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
