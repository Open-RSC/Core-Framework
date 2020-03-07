package com.openrsc.server.plugins.npcs.entrana;

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

public class FrincosVialShopEntrana implements ShopInterface,
	TalkNpcTrigger {

	private final Shop shop = new Shop(false, 3000, 100, 70, 2,
		new Item(ItemId.EMPTY_VIAL.id(), 50), new Item(ItemId.PESTLE_AND_MORTAR.id(), 3), new Item(ItemId.EYE_OF_NEWT.id(), 50));

	@Override
	public void onTalkNpc(Player p, Npc n) {
		npcsay(p, n, "Hello how can I help you?");
		int menu = Functions.multi(p, n,
			"What are you selling?",
			"You can't, I'm beyond help",
			"I'm okay, thankyou");
		if (menu == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
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
}
