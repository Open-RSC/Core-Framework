package com.openrsc.server.plugins.custom.npcs;

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

public final class VarrockSwordsOpenPk extends AbstractShop {

	private final Shop shop = new Shop(false, 30000, 100, 60, 2,
		new Item(ItemId.IRON_2_HANDED_SWORD.id(), 100),
		new Item(ItemId.STEEL_2_HANDED_SWORD.id(), 100),
		new Item(ItemId.BLACK_2_HANDED_SWORD.id(), 100),
		new Item(ItemId.MITHRIL_2_HANDED_SWORD.id(), 100),
		new Item(ItemId.ADAMANTITE_2_HANDED_SWORD.id(), 100),
		new Item(ItemId.RUNE_2_HANDED_SWORD.id(), 100),
		new Item(ItemId.IRON_BATTLE_AXE.id(), 100),
		new Item(ItemId.STEEL_BATTLE_AXE.id(), 100),
		new Item(ItemId.BLACK_BATTLE_AXE.id(), 100),
		new Item(ItemId.MITHRIL_BATTLE_AXE.id(), 100),
		new Item(ItemId.ADAMANTITE_BATTLE_AXE.id(), 100),
		new Item(ItemId.RUNE_BATTLE_AXE.id(), 100),
		new Item(ItemId.RUBY_AMULET_OF_STRENGTH.id(), 100),
		new Item(ItemId.DRAGONSTONE_AMULET.id(), 100));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return player.getConfig().WANT_OPENPK_POINTS && (n.getID() == NpcId.SHOPKEEPER_VARROCK_SWORD.id() || n.getID() == NpcId.SHOP_ASSISTANT_VARROCK_SWORD.id());
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
