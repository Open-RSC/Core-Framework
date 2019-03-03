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

public final class Gulluck implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 3000, 100, 25, 1, new Item(ItemId.BRONZE_ARROWS.id(),
		200), new Item(ItemId.CROSSBOW_BOLTS.id(), 150), new Item(ItemId.OYSTER_PEARL_BOLTS.id(), 1), new Item(ItemId.SHORTBOW.id(),
		4), new Item(ItemId.LONGBOW.id(), 2), new Item(ItemId.CROSSBOW.id(), 2), new Item(ItemId.BRONZE_ARROW_HEADS.id(), 200),
		new Item(ItemId.IRON_ARROW_HEADS.id(), 180), new Item(ItemId.STEEL_ARROW_HEADS.id(), 160),
		new Item(ItemId.MITHRIL_ARROW_HEADS.id(), 140), new Item(ItemId.IRON_AXE.id(), 5), new Item(ItemId.STEEL_AXE.id(), 3),
		new Item(ItemId.IRON_BATTLE_AXE.id(), 5), new Item(ItemId.STEEL_BATTLE_AXE.id(), 2), new Item(ItemId.MITHRIL_BATTLE_AXE.id(), 1),
		new Item(ItemId.BRONZE_2_HANDED_SWORD.id(), 4), new Item(ItemId.IRON_2_HANDED_SWORD.id(), 3), new Item(ItemId.STEEL_2_HANDED_SWORD.id(), 2),
		new Item(ItemId.BLACK_2_HANDED_SWORD.id(), 1), new Item(ItemId.MITHRIL_2_HANDED_SWORD.id(), 1), new Item(ItemId.ADAMANTITE_2_HANDED_SWORD.id(), 1));

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == NpcId.GULLUCK.id()) {
			playerTalk(p, n, "hello");
			npcTalk(p, n, "good day brave adventurer",
				"could i interest you in my fine selection of weapons?");

			int option = showMenu(p, n, "i'll take a look", "no thanks");
			switch (option) {
				case 0:
					p.setAccessingShop(shop);
					ActionSender.showShop(p, shop);
					break;
				case 1:
					npcTalk(p, n, "grrrr");
					break;

			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.GULLUCK.id();
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
