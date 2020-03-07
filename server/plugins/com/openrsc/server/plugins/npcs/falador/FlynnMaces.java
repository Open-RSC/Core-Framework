package com.openrsc.server.plugins.npcs.falador;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public final class FlynnMaces implements ShopInterface,
	TalkNpcTrigger {

	private final Shop shop = new Shop(false, 25000, 100, 60, 1,
		new Item(ItemId.BRONZE_MACE.id(), 5), new Item(ItemId.IRON_MACE.id(), 4), new Item(ItemId.STEEL_MACE.id(), 4),
		new Item(ItemId.MITHRIL_MACE.id(), 3), new Item(ItemId.ADAMANTITE_MACE.id(), 2));

	@Override
	public boolean blockTalkNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.FLYNN.id();
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
		npcsay(p, n, "Hello do you want to buy or sell any maces?");

		int opt = multi(p, n, false, //do not send over
			"No thanks", "Well I'll have a look anyway");
		if (opt == 0) {
			say(p, n, "no thanks");
		} else if (opt == 1) {
			say(p, n, "Well I'll have a look anyway");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}

	}

}
