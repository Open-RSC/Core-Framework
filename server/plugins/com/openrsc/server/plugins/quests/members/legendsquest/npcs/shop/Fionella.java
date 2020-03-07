package com.openrsc.server.plugins.quests.members.legendsquest.npcs.shop;

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

public class Fionella implements ShopInterface, TalkNpcTrigger {

	private final Shop shop = new Shop(true, 20000, 155, 55, 13,
		new Item(ItemId.SWORDFISH.id(), 2), new Item(ItemId.APPLE_PIE.id(), 5), new Item(ItemId.SLEEPING_BAG.id(), 1),
		new Item(ItemId.FULL_ATTACK_POTION.id(), 3), new Item(ItemId.STEEL_ARROWS.id(), 50));

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.FIONELLA.id()) {
			npcsay(p, n, "Can I help you at all?");
			int menu = Functions.multi(p, n,
				"Yes please. What are you selling?",
				"No thanks");
			if (menu == 0) {
				npcsay(p, n, "Take a look");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
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
}
