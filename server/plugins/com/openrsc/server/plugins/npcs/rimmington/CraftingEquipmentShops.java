package com.openrsc.server.plugins.npcs.rimmington;

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

public final class CraftingEquipmentShops implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 5000, 100, 65, 2,
		new Item(ItemId.CHISEL.id(), 2), new Item(ItemId.RING_MOULD.id(), 4), new Item(ItemId.NECKLACE_MOULD.id(), 2),
		new Item(ItemId.AMULET_MOULD.id(), 2), new Item(ItemId.NEEDLE.id(), 3), new Item(ItemId.THREAD.id(), 100),
		new Item(ItemId.HOLY_SYMBOL_MOULD.id(), 3));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.ROMMIK.id() || n.getID() == NpcId.DOMMIK.id();
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
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Would you like to buy some crafting equipment");
		int option = showMenu(p, n, "No I've got all the crafting equipment I need", "Let's see what you've got then");
		if (option == 0) {
			npcTalk(p, n, "Ok fair well on your travels");
		} else if (option == 1) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}
