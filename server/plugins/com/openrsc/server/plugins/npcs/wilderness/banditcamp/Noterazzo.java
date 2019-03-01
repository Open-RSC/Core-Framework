package com.openrsc.server.plugins.npcs.wilderness.banditcamp;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class Noterazzo implements ShopInterface, TalkToNpcListener, TalkToNpcExecutiveListener {

	private final Shop shop = new Shop(true, 12400, 90, 60, 3,
		new Item(ItemId.POT.id(), 3), new Item(ItemId.JUG.id(), 2), new Item(ItemId.TINDERBOX.id(), 2),
		new Item(ItemId.CHISEL.id(), 2), new Item(ItemId.HAMMER.id(), 5), new Item(ItemId.BRONZE_PICKAXE.id(), 5),
		new Item(ItemId.BRONZE_AXE.id(), 10));

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.NOTERAZZO.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.NOTERAZZO.id()) {

			npcTalk(p, n, "Hey wanna trade?, I'll give the best deals you can find");
			int menu = showMenu(p, n, "Yes please", "No thankyou", "How can you afford to give such good deals?");
			if (menu == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			} else if (menu == 1) {
				//NOTHING
			} else if (menu == 2) {
				npcTalk(p, n, "The general stores in Asgarnia and Misthalin are heavily taxed",
					"It really makes it hard for them to run an effective buisness",
					"For some reason taxmen don't visit my store");
				p.message("Noterazzo winks at you");
			}
		}
	}

	@Override
	public Shop[] getShops() {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return false;
	}
}
