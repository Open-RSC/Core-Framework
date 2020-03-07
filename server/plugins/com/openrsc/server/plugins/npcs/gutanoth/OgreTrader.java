package com.openrsc.server.plugins.npcs.gutanoth;

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

public class OgreTrader implements ShopInterface, TalkNpcTrigger {

	private final Shop shop = new Shop(false, 15000, 130, 40, 3,
		new Item(ItemId.POT.id(), 3),
		new Item(ItemId.JUG.id(), 2),
		new Item(ItemId.KNIFE.id(), 2),
		new Item(ItemId.BUCKET.id(), 2),
		new Item(ItemId.TINDERBOX.id(), 2),
		new Item(ItemId.CHISEL.id(), 2),
		new Item(ItemId.HAMMER.id(), 5),
		new Item(ItemId.SLEEPING_BAG.id(), 10));

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.OGRE_TRADER_GENSTORE.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		npcsay(p, n, "What the human be wantin'");
		int menu = Functions.multi(p, n,
			"Can I see what you are selling ?",
			"I don't need anything");
		if (menu == 0) {
			npcsay(p, n, "I suppose so...");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (menu == 1) {
			npcsay(p, n, "As you wish");
		}
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
