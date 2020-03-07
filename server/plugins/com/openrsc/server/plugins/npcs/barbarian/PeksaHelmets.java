package com.openrsc.server.plugins.npcs.barbarian;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
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

public final class PeksaHelmets implements ShopInterface,
	TalkNpcTrigger {

	private final Shop shop = new Shop(false, 25000, 100, 60, 1, new Item(ItemId.MEDIUM_BRONZE_HELMET.id(),
		5), new Item(ItemId.MEDIUM_IRON_HELMET.id(), 3), new Item(ItemId.MEDIUM_STEEL_HELMET.id(), 3), new Item(ItemId.MEDIUM_MITHRIL_HELMET.id(), 1),
		new Item(ItemId.MEDIUM_ADAMANTITE_HELMET.id(), 1), new Item(ItemId.LARGE_BRONZE_HELMET.id(), 4), new Item(ItemId.LARGE_IRON_HELMET.id(), 3),
		new Item(ItemId.LARGE_STEEL_HELMET.id(), 2), new Item(ItemId.LARGE_MITHRIL_HELMET.id(), 1), new Item(ItemId.LARGE_ADAMANTITE_HELMET.id(), 1));

	@Override
	public boolean blockTalkNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.PEKSA.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void onTalkNpc(final Player p, final Npc n) {
		npcsay(p, n, "Are you interested in buying or selling a helmet?");

		int option = Functions.multi(p, n, "I could be, yes", "No, I'll pass on that");
		if (option == 0) {
			npcsay(p, n, "Well look at all these great helmets!");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (option == 1) {
			npcsay(p, n, "Well come back if you change your mind");
		}
	}

}
