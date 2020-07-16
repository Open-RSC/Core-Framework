package com.openrsc.server.plugins.authentic.npcs.entrana;

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

public class FrincosVialShopEntrana extends AbstractShop {

	private final Shop shop = new Shop(false, 3000, 100, 70, 2,
		new Item(ItemId.EMPTY_VIAL.id(), 50), new Item(ItemId.PESTLE_AND_MORTAR.id(), 3), new Item(ItemId.EYE_OF_NEWT.id(), 50));

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "Hello how can I help you?");
		int menu = multi(player, n,
			"What are you selling?",
			"You can't, I'm beyond help",
			"I'm okay, thankyou");
		if (menu == 0) {
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.FRINCOS.id();
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
