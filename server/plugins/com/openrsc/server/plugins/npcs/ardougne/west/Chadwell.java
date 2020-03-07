package com.openrsc.server.plugins.npcs.ardougne.west;

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

public final class Chadwell implements ShopInterface, TalkNpcTrigger {

	private final Shop shop = new Shop(true, 3000, 130, 40, 3, new Item(ItemId.ROPE.id(), 7), new Item(ItemId.BRONZE_PICKAXE.id(), 10), new Item(ItemId.SALMON.id(), 2), new Item(ItemId.BUCKET.id(), 2), new Item(ItemId.TINDERBOX.id(), 10), new Item(ItemId.MEAT_PIE.id(), 2), new Item(ItemId.HAMMER.id(), 5), new Item(ItemId.BREAD.id(), 10), new Item(ItemId.BOOTS.id(), 10), new Item(ItemId.POT.id(), 3), new Item(ItemId.COOKEDMEAT.id(), 2), new Item(ItemId.LONGBOW.id(), 2), new Item(ItemId.BRONZE_ARROWS.id(), 200), new Item(ItemId.SLEEPING_BAG.id(), 10));

	@Override
	public void onTalkNpc(Player p, final Npc n) {
		say(p, n, "hello there");
		npcsay(p, n, "good day, what can i get you?");
		int options = multi(p, n, false, //do not send over
				"nothing thanks, just browsing", "lets see what you've got");
		if (options == 0) {
			say(p, n, "nothing thanks");
			npcsay(p, n, "ok then");
		}
		if (options == 1) {
			say(p, n, "let's see what you've got then");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}

	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.CHADWELL.id();
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
