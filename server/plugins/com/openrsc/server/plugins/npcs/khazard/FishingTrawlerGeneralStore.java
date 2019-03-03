package com.openrsc.server.plugins.npcs.khazard;

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

public final class FishingTrawlerGeneralStore implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(true, 3000, 130, 40, 3,
		new Item(ItemId.BRONZE_PICKAXE.id(), 5), new Item(ItemId.POT.id(), 3), new Item(ItemId.JUG.id(), 2),
		new Item(ItemId.SHEARS.id(), 2), new Item(ItemId.BUCKET.id(), 2), new Item(ItemId.TINDERBOX.id(), 2),
		new Item(ItemId.CHISEL.id(), 2), new Item(ItemId.HAMMER.id(), 5), new Item(ItemId.ROPE.id(), 30),
		new Item(ItemId.POT_OF_FLOUR.id(), 30), new Item(ItemId.BAILING_BUCKET.id(), 30), new Item(ItemId.SWAMP_PASTE.id(), 30));

	@Override
	public void onTalkToNpc(Player p, final Npc n) {

		npcTalk(p, n, "Can I help you at all?");

		int option = showMenu(p, n, "Yes please. What are you selling?",
				"No thanks");
		switch (option) {
			case 0:
				npcTalk(p, n, "Take a look");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
				break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.SHOPKEEPER_PORTKHAZARD.id();
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
