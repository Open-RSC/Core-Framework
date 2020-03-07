package com.openrsc.server.plugins.npcs.grandtree;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.multi;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public final class Blurberry implements ShopInterface, TalkNpcTrigger {

	private final Shop shop = new Shop(false, 3000, 100, 25, 1, new Item(ItemId.BLURBERRY_BARMAN_FRUIT_BLAST.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_BLURBERRY_SPECIAL.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_WIZARD_BLIZZARD.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_PINEAPPLE_PUNCH.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_SGG.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_CHOCOLATE_SATURDAY.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_DRUNK_DRAGON.id(), 10));

	@Override
	public void onTalkNpc(Player p, final Npc n) {
		npcsay(p, n, "good day to you", "can i get you drink");
		int opt = Functions.multi(p, n, "what do you have", "no thanks");
		if (opt == 0) {
			npcsay(p, n, "take a look");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (opt == 1) {
			npcsay(p, n, "ok, take it easy");
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.BLURBERRY_BARMAN.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}


}
