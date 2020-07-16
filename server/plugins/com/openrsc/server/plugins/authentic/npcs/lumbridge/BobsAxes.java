package com.openrsc.server.plugins.authentic.npcs.lumbridge;

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

public final class BobsAxes extends AbstractShop {

	private final Shop shop = new Shop(false, 15000, 100, 60, 2, new Item(ItemId.BRONZE_PICKAXE.id(),
		5), new Item(ItemId.BRONZE_AXE.id(), 10), new Item(ItemId.IRON_AXE.id(), 5), new Item(ItemId.STEEL_AXE.id(), 3),
		new Item(ItemId.IRON_BATTLE_AXE.id(), 5), new Item(ItemId.STEEL_BATTLE_AXE.id(), 2), new Item(ItemId.MITHRIL_BATTLE_AXE.id(), 1));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.BOB.id();
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
		npcsay(player, n, "Hello. How can I help you?");
		int option = multi(player, n, "Give me a quest!",
			"Have you anything to sell?");
		switch (option) {
			case 0:
				npcsay(player, n, "Get yer own!");
				break;
			case 1:
				npcsay(player, n, "Yes, I buy and sell axes, take your pick! (or axe)");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;
		}
	}
}
