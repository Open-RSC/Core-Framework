package com.openrsc.server.plugins.authentic.npcs.varrock;

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

public final class VarrockSwords extends AbstractShop {

	private final Shop shop = new Shop(false, 30000, 100, 60, 2,
		new Item(ItemId.BRONZE_SHORT_SWORD.id(), 5), new Item(ItemId.IRON_SHORT_SWORD.id(), 4), new Item(ItemId.STEEL_SHORT_SWORD.id(), 4),
		new Item(ItemId.BLACK_SHORT_SWORD.id(), 3), new Item(ItemId.MITHRIL_SHORT_SWORD.id(), 3), new Item(ItemId.ADAMANTITE_SHORT_SWORD.id(), 2),
		new Item(ItemId.BRONZE_LONG_SWORD.id(), 4), new Item(ItemId.IRON_LONG_SWORD.id(), 3), new Item(ItemId.STEEL_LONG_SWORD.id(), 3),
		new Item(ItemId.BLACK_LONG_SWORD.id(), 2), new Item(ItemId.MITHRIL_LONG_SWORD.id(), 2), new Item(ItemId.ADAMANTITE_LONG_SWORD.id(), 1),
		new Item(ItemId.BRONZE_DAGGER.id(), 10), new Item(ItemId.IRON_DAGGER.id(), 6), new Item(ItemId.STEEL_DAGGER.id(), 5),
		new Item(ItemId.BLACK_DAGGER.id(), 4), new Item(ItemId.MITHRIL_DAGGER.id(), 3), new Item(ItemId.ADAMANTITE_DAGGER.id(), 2));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return (n.getID() == NpcId.SHOPKEEPER_VARROCK_SWORD.id() || n.getID() == NpcId.SHOP_ASSISTANT_VARROCK_SWORD.id());
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
		if (n.getID() == NpcId.SHOPKEEPER_VARROCK_SWORD.id() || n.getID() == NpcId.SHOP_ASSISTANT_VARROCK_SWORD.id()) {
			npcsay(player, n, "Hello bold adventurer",
				"Can I interest you in some swords?");

			final String[] options = new String[]{"Yes please",
				"No, I'm OK for swords right now"};
			int option = multi(player, n, options);
			switch (option) {
				case 0:
					player.setAccessingShop(shop);
					ActionSender.showShop(player, shop);
					break;
				case 1:
					npcsay(player, n, "Come back if you need any");
					break;
			}
		}
	}
}
