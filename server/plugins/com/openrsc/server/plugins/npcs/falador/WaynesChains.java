package com.openrsc.server.plugins.npcs.falador;

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

public final class WaynesChains implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 25000, 100, 65, 1, new Item(ItemId.BRONZE_CHAIN_MAIL_BODY.id(),
		3), new Item(ItemId.IRON_CHAIN_MAIL_BODY.id(), 2), new Item(ItemId.STEEL_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.BLACK_CHAIN_MAIL_BODY.id(), 1),
		new Item(ItemId.MITHRIL_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.ADAMANTITE_CHAIN_MAIL_BODY.id(), 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.WAYNE.id();
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
		if (n.getID() == NpcId.WAYNE.id()) {
			npcTalk(p, n, "Welcome to Wayne's chains",
				"Do you wanna buy or sell some chain mail?");

			int option = showMenu(p, n, false, //do not send over
				"Yes please", "No thanks");
			if (option == 0) {
				playerTalk(p, n, "Yes Please");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			} else if (option == 1) {
				playerTalk(p, n, "No thanks");
			}
		}
	}

}
