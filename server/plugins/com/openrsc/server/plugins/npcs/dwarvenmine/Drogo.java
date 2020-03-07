package com.openrsc.server.plugins.npcs.dwarvenmine;

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

public class Drogo implements ShopInterface,
	TalkNpcTrigger {

	private final Shop shop = new Shop(false, 30000, 100, 70, 2, new Item(ItemId.HAMMER.id(),
		4), new Item(ItemId.BRONZE_PICKAXE.id(), 4), new Item(ItemId.COPPER_ORE.id(), 0), new Item(ItemId.TIN_ORE.id(), 0),
		new Item(ItemId.IRON_ORE.id(), 0), new Item(ItemId.COAL.id(), 0), new Item(ItemId.BRONZE_BAR.id(), 0),
		new Item(ItemId.IRON_BAR.id(), 0), new Item(ItemId.GOLD_BAR.id(), 0));

	@Override
	public boolean blockTalkNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.DROGO.id();
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
	public void onTalkNpc(Player p, Npc n) {
		npcsay(p, n, "Ello");
		int m = multi(p, n, false, //do not send over
			"Do you want to trade?", "Hello shorty",
			"Why don't you ever restock ores and bars?");
		if (m == 0) {
			say(p, n, "Do you want to trade?");
			npcsay(p, n, "Yeah sure, I run a mining store.");
			ActionSender.showShop(p, shop);
		} else if (m == 1) {
			say(p, n, "Hello Shorty.");
			npcsay(p, n, "I may be short, but at least I've got manners");
		} else if (m == 2) {
			say(p, n, "Why don't you ever restock ores and bars?");
			npcsay(p, n, "The only ores and bars I sell are those sold to me");
		}
	}
}
