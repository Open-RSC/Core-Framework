package com.openrsc.server.plugins.authentic.npcs.wilderness.banditcamp;

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

public class Noterazzo extends AbstractShop {

	private final Shop shop = new Shop(true, 12400, 90, 60, 3,
		new Item(ItemId.POT.id(), 3), new Item(ItemId.JUG.id(), 2), new Item(ItemId.TINDERBOX.id(), 2),
		new Item(ItemId.CHISEL.id(), 2), new Item(ItemId.HAMMER.id(), 5), new Item(ItemId.BRONZE_PICKAXE.id(), 5),
		new Item(ItemId.BRONZE_AXE.id(), 10));

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.NOTERAZZO.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.NOTERAZZO.id()) {

			npcsay(player, n, "Hey wanna trade?, I'll give the best deals you can find");
			int menu = multi(player, n, "Yes please", "No thankyou", "How can you afford to give such good deals?");
			if (menu == 0) {
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			} else if (menu == 1) {
				//NOTHING
			} else if (menu == 2) {
				npcsay(player, n, "The general stores in Asgarnia and Misthalin are heavily taxed",
					"It really makes it hard for them to run an effective buisness",
					"For some reason taxmen don't visit my store");
				player.message("Noterazzo winks at you");
			}
		}
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
}
