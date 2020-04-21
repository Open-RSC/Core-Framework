package com.openrsc.server.plugins.npcs.gutanoth;

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

public class GrudsHerblawStall implements ShopInterface,
	TalkNpcTrigger {

	private final Shop shop = new Shop(false, 3000, 100, 70, 2,
		new Item(ItemId.EMPTY_VIAL.id(), 50), new Item(ItemId.PESTLE_AND_MORTAR.id(), 3), new Item(ItemId.EYE_OF_NEWT.id(), 50));

	@Override
	public void onTalkNpc(Player p, Npc n) {
		npcsay(p, n, "Does The little creature want to buy sumfin'");
		int menu = multi(p, n,
			"Yes I do",
			"No I don't");
		if (menu == 0) {
			npcsay(p, n, "Welcome to Grud's herblaw stall");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (menu == 1) {
			npcsay(p, n, "Suit yourself");
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.OGRE_MERCHANT.id();
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
