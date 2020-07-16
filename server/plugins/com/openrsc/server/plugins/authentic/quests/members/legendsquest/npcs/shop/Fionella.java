package com.openrsc.server.plugins.authentic.quests.members.legendsquest.npcs.shop;

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

public class Fionella extends AbstractShop {

	private final Shop shop = new Shop(true, 20000, 155, 55, 13,
		new Item(ItemId.SWORDFISH.id(), 2), new Item(ItemId.APPLE_PIE.id(), 5), new Item(ItemId.SLEEPING_BAG.id(), 1),
		new Item(ItemId.FULL_ATTACK_POTION.id(), 3), new Item(ItemId.STEEL_ARROWS.id(), 50));

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.FIONELLA.id()) {
			npcsay(player, n, "Can I help you at all?");
			int menu = multi(player, n,
				"Yes please. What are you selling?",
				"No thanks");
			if (menu == 0) {
				npcsay(player, n, "Take a look");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.FIONELLA.id();
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
