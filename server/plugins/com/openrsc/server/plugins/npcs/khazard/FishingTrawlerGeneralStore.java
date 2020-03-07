package com.openrsc.server.plugins.npcs.khazard;

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

public final class FishingTrawlerGeneralStore implements ShopInterface,
	TalkNpcTrigger {

	private final Shop shop = new Shop(true, 3000, 130, 40, 3,
		new Item(ItemId.BRONZE_PICKAXE.id(), 5), new Item(ItemId.POT.id(), 3), new Item(ItemId.JUG.id(), 2),
		new Item(ItemId.SHEARS.id(), 2), new Item(ItemId.BUCKET.id(), 2), new Item(ItemId.TINDERBOX.id(), 2),
		new Item(ItemId.CHISEL.id(), 2), new Item(ItemId.HAMMER.id(), 5), new Item(ItemId.ROPE.id(), 30),
		new Item(ItemId.POT_OF_FLOUR.id(), 30), new Item(ItemId.BAILING_BUCKET.id(), 30), new Item(ItemId.SWAMP_PASTE.id(), 30));

	@Override
	public void onTalkNpc(Player p, final Npc n) {

		npcsay(p, n, "Can I help you at all?");

		int option = Functions.multi(p, n, "Yes please. What are you selling?",
				"No thanks");
		switch (option) {
			case 0:
				npcsay(p, n, "Take a look");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
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

}
