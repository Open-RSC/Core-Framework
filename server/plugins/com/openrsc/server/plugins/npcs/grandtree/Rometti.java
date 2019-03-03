package com.openrsc.server.plugins.npcs.grandtree;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public final class Rometti implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 3000, 100, 55, 1,
		new Item(ItemId.GNOME_ROBE_PINK.id(), 5), new Item(ItemId.GNOME_ROBE_GREEN.id(), 5), new Item(ItemId.GNOME_ROBE_PURPLE.id(), 5),
		new Item(ItemId.GNOME_ROBE_CREAM.id(), 5), new Item(ItemId.GNOME_ROBE_BLUE.id(), 5), new Item(ItemId.GNOMESHAT_PINK.id(), 5),
		new Item(ItemId.GNOMESHAT_GREEN.id(), 5), new Item(ItemId.GNOMESHAT_PURPLE.id(), 5), new Item(ItemId.GNOMESHAT_CREAM.id(), 5),
		new Item(ItemId.GNOMESHAT_BLUE.id(), 5), new Item(ItemId.GNOME_TOP_PINK.id(), 5), new Item(ItemId.GNOME_TOP_GREEN.id(), 5),
		new Item(ItemId.GNOME_TOP_PURPLE.id(), 5), new Item(ItemId.GNOME_TOP_CREAM.id(), 5), new Item(ItemId.GNOME_TOP_BLUE.id(), 5),
		new Item(ItemId.BOOTS_PINK.id(), 5), new Item(ItemId.BOOTS_GREEN.id(), 5), new Item(ItemId.BOOTS_PURPLE.id(), 5),
		new Item(ItemId.BOOTS_CREAM.id(), 5), new Item(ItemId.BOOTS_BLUE.id(), 5));

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		playerTalk(p, n, "hello");
		npcTalk(p, n, "hello traveller",
			"have a look at my latest range of gnome fashion",
			"rometti is the ultimate label in gnome high society");
		playerTalk(p, n, "really");
		npcTalk(p, n, "pastels are all the rage this season");
		int option = showMenu(p, n, false, //do not send over
			"i've no time for fashion", "ok then let's have a look");
		switch (option) {
			case 0:
				playerTalk(p, n, "i've no time for fashion");
				npcTalk(p, n, "hmm...i did wonder");
				break;
			case 1:
				playerTalk(p, n, "ok then, let's have a look");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
				break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.ROMETTI.id();
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
