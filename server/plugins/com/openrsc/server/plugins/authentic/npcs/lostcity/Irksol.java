package com.openrsc.server.plugins.authentic.npcs.lostcity;

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

public final class Irksol extends AbstractShop {

	private final Shop shop = new Shop(false, 3000, 50, 30, 2,
		new Item(ItemId.RUBY_RING.id(), 5));

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		if (n.getID() == NpcId.IRKSOL.id()) {
			npcsay(player, n, "selling ruby rings",
				"The best deals in all the planes of existance");
			int option = multi(player, n, false, //do not send over
				"I'm interested in these deals",
				"No thankyou");
			switch (option) {
				case 0:
					say(player, n, "I'm interested in these deals");
					npcsay(player, n, "Take a look at these beauties");
					player.setAccessingShop(shop);
					ActionSender.showShop(player, shop);
					break;
				case 1:
					say(player, n, "no thankyou");
					break;
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.IRKSOL.id();
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
