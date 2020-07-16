package com.openrsc.server.plugins.authentic.npcs.taverly;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;

import static com.openrsc.server.plugins.Functions.*;

public class GaiusTwoHandlerShop extends AbstractShop {

	private final Shop shop = new Shop(false, 30000, 100, 60, 2,
		new Item(ItemId.BRONZE_2_HANDED_SWORD.id(), 4), new Item(ItemId.IRON_2_HANDED_SWORD.id(), 3), new Item(ItemId.STEEL_2_HANDED_SWORD.id(), 2),
		new Item(ItemId.BLACK_2_HANDED_SWORD.id(), 1), new Item(ItemId.MITHRIL_2_HANDED_SWORD.id(), 1), new Item(ItemId.ADAMANTITE_2_HANDED_SWORD.id(), 1));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.GAIUS.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public Shop getShop() {
		return shop;
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "Welcome to my two handed sword shop");
		final int option = multi(player, n, false, //do not send over
			"Let's trade", "thankyou");
		if (option == 0) {
			say(player, n, "Let's trade");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (option == 1) {
			say(player, n, "Thankyou");
		}
	}
}
