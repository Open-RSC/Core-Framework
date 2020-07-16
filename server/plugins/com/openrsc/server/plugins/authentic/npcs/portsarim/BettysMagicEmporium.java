package com.openrsc.server.plugins.authentic.npcs.portsarim;

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

public final class BettysMagicEmporium extends AbstractShop {

	private final Shop shop = new Shop(false, 6000, 100, 75, 2, new Item(ItemId.FIRE_RUNE.id(),
		30), new Item(ItemId.WATER_RUNE.id(), 30), new Item(ItemId.AIR_RUNE.id(), 30), new Item(ItemId.EARTH_RUNE.id(),
		30), new Item(ItemId.MIND_RUNE.id(), 30), new Item(ItemId.BODY_RUNE.id(), 30), new Item(ItemId.EYE_OF_NEWT.id(),
		30), new Item(ItemId.BLUE_WIZARDSHAT.id(), 1), new Item(ItemId.BLACK_WIZARDSHAT.id(), 1));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.BETTY.id();
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
	public Shop getShop() {
		return shop;
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (n.getID() == NpcId.BETTY.id()) {
			npcsay(player, n, "Welcome to the magic emporium");
			int opt = multi(player, n, "Can I see your wares?",
				"Sorry I'm not into magic");
			if (opt == 0) {
				npcsay(player, n, "Yes");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			}
			if (opt == 1) {
				npcsay(player, n, "Send anyone my way who is");
			}
		}
	}

}
