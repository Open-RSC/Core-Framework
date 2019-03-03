package com.openrsc.server.plugins.npcs.grandtree;

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

public final class BlurberryBarman implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 3000, 100, 25, 1, new Item(ItemId.BLURBERRY_BARMAN_FRUIT_BLAST.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_BLURBERRY_SPECIAL.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_WIZARD_BLIZZARD.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_PINEAPPLE_PUNCH.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_SGG.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_CHOCOLATE_SATURDAY.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_DRUNK_DRAGON.id(), 10));

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		npcTalk(p, n, "good day to you", "can i get you drink?");
		int opt = showMenu(p, n, false, //do not send over
			"what do you have?", "no thanks");
		if (opt == 0) {
			playerTalk(p, n, "what do you have");
			npcTalk(p, n, "take a look");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (opt == 1) {
			playerTalk(p, n, "no thanks");
			npcTalk(p, n, "ok, take it easy");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.BLURBERRY_BARMAN.id();
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
