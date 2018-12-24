package com.openrsc.server.plugins.npcs.alkharid;

import com.openrsc.server.external.ItemId;
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

public final class LouieLegs implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int npcid = 85;

	private final Shop shop = new Shop(false, 25000, 100, 65, 1,
		new Item(ItemId.BRONZE_PLATE_MAIL_LEGS.id(), 5),
		new Item(ItemId.IRON_PLATE_MAIL_LEGS.id(), 3),
		new Item(ItemId.STEEL_PLATE_MAIL_LEGS.id(), 2),
		new Item(ItemId.BLACK_PLATE_MAIL_LEGS.id(), 1),
		new Item(ItemId.MITHRIL_PLATE_MAIL_LEGS.id(), 1),
		new Item(ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id(), 1)
	);

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == npcid;
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
		npcTalk(p, n, "Hey, wanna buy some armour?");

		int option = showMenu(p, n, "What have you got?", "No, thank you");
		if (option == 0) {
			npcTalk(p, n, "Take a look, see");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}
