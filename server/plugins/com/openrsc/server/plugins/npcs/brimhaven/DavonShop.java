package com.openrsc.server.plugins.npcs.brimhaven;

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

public class DavonShop implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 900000000, 120, 90, 2, new Item(ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(), 0),
			new Item(ItemId.SAPPHIRE_AMULET_OF_MAGIC.id(), 1), new Item(ItemId.EMERALD_AMULET_OF_PROTECTION.id(), 0), new Item(ItemId.RUBY_AMULET_OF_STRENGTH.id(), 0), new Item(ItemId.DIAMOND_AMULET_OF_POWER.id(), 0));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Pssst come here if you want to do some amulet trading");
		int menu = showMenu(p, n, "What are you selling?", "What do you mean pssst?", "Why don't you ever restock some types of amulets?");
		if (menu == 0) {
			p.message("Davon opens up his jacket to reveal some amulets");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (menu == 1) {
			npcTalk(p, n, "I was clearing my throat");
		} else if (menu == 2) {
			npcTalk(p, n, "Some of these amulets are very hard to get",
				"I have to wait until an adventurer supplies me");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.DAVON.id();
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
