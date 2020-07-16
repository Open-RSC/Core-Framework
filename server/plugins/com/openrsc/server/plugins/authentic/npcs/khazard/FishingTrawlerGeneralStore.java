package com.openrsc.server.plugins.authentic.npcs.khazard;

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

public final class FishingTrawlerGeneralStore extends AbstractShop {

	private final Shop shop = new Shop(true, 3000, 130, 40, 3,
		new Item(ItemId.BRONZE_PICKAXE.id(), 5), new Item(ItemId.POT.id(), 3), new Item(ItemId.JUG.id(), 2),
		new Item(ItemId.SHEARS.id(), 2), new Item(ItemId.BUCKET.id(), 2), new Item(ItemId.TINDERBOX.id(), 2),
		new Item(ItemId.CHISEL.id(), 2), new Item(ItemId.HAMMER.id(), 5), new Item(ItemId.ROPE.id(), 30),
		new Item(ItemId.POT_OF_FLOUR.id(), 30), new Item(ItemId.BAILING_BUCKET.id(), 30), new Item(ItemId.SWAMP_PASTE.id(), 30));

	@Override
	public void onTalkNpc(Player player, final Npc n) {

		npcsay(player, n, "Can I help you at all?");

		int option = multi(player, n, "Yes please. What are you selling?",
				"No thanks");
		switch (option) {
			case 0:
				npcsay(player, n, "Take a look");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.SHOPKEEPER_PORTKHAZARD.id();
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
