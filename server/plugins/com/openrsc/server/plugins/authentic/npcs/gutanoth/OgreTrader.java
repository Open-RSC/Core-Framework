package com.openrsc.server.plugins.authentic.npcs.gutanoth;

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

public class OgreTrader extends AbstractShop {

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
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.OGRE_TRADER_GENSTORE.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "What the human be wantin'");
		int menu = multi(player, n,
			"Can I see what you are selling ?",
			"I don't need anything");
		if (menu == 0) {
			npcsay(player, n, "I suppose so...");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (menu == 1) {
			npcsay(player, n, "As you wish");
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

	@Override
	public Shop getShop() {
		return shop;
	}
}
