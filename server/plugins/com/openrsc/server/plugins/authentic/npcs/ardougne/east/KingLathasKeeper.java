package com.openrsc.server.plugins.authentic.npcs.ardougne.east;

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

public final class KingLathasKeeper extends AbstractShop {

	private final Shop shop = new Shop(false, 3000, 150, 50, 2, new Item(ItemId.BRONZE_ARROWS.id(),
		200), new Item(ItemId.CROSSBOW_BOLTS.id(), 150), new Item(ItemId.SHORTBOW.id(), 4),
		new Item(ItemId.LONGBOW.id(), 2), new Item(ItemId.CROSSBOW.id(), 2), new Item(ItemId.BRONZE_ARROW_HEADS.id(), 200),
		new Item(ItemId.IRON_ARROW_HEADS.id(), 180), new Item(ItemId.STEEL_ARROW_HEADS.id(), 160),
		new Item(ItemId.MITHRIL_ARROW_HEADS.id(), 140), new Item(ItemId.IRON_AXE.id(), 5), new Item(ItemId.STEEL_AXE.id(), 3),
		new Item(ItemId.IRON_BATTLE_AXE.id(), 5), new Item(ItemId.STEEL_BATTLE_AXE.id(), 2), new Item(ItemId.MITHRIL_BATTLE_AXE.id(), 1),
		new Item(ItemId.BRONZE_2_HANDED_SWORD.id(), 4), new Item(ItemId.IRON_2_HANDED_SWORD.id(), 3),
		new Item(ItemId.STEEL_2_HANDED_SWORD.id(), 2), new Item(ItemId.BLACK_2_HANDED_SWORD.id(), 1), new Item(ItemId.MITHRIL_2_HANDED_SWORD.id(), 1),
		new Item(ItemId.ADAMANTITE_2_HANDED_SWORD.id(), 1));

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		say(player, n, "hello");
		npcsay(player, n, "so are you looking to buy some weapons?",
			"king lathas keeps us very well stocked");
		int option = multi(player, n, "what do you have?", "no thanks");
		switch (option) {

			case 0:
				npcsay(player, n, "take a look");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.SHOP_KEEPER_TRAINING_CAMP.id();
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
}
