package com.openrsc.server.plugins.npcs.dwarvenmine;

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

public class Drogo implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 70, 2, new Item(ItemId.HAMMER.id(),
		4), new Item(ItemId.BRONZE_PICKAXE.id(), 4), new Item(ItemId.COPPER_ORE.id(), 0), new Item(ItemId.TIN_ORE.id(), 0),
		new Item(ItemId.IRON_ORE.id(), 0), new Item(ItemId.COAL.id(), 0), new Item(ItemId.BRONZE_BAR.id(), 0),
		new Item(ItemId.IRON_BAR.id(), 0), new Item(ItemId.GOLD_BAR.id(), 0));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.DROGO.id();
	}

	@Override
	public Shop[] getShops() {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Ello");
		int m = showMenu(p, n, false, //do not send over
			"Do you want to trade?", "Hello shorty",
			"Why don't you ever restock ores and bars?");
		if (m == 0) {
			playerTalk(p, n, "Do you want to trade?");
			npcTalk(p, n, "Yeah sure, I run a mining store.");
			ActionSender.showShop(p, shop);
		} else if (m == 1) {
			playerTalk(p, n, "Hello Shorty.");
			npcTalk(p, n, "I may be short, but at least I've got manners");
		} else if (m == 2) {
			playerTalk(p, n, "Why don't you ever restock ores and bars?");
			npcTalk(p, n, "The only ores and bars I sell are those sold to me");
		}
	}
}
