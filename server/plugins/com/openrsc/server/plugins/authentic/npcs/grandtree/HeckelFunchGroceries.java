package com.openrsc.server.plugins.authentic.npcs.grandtree;

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

public final class HeckelFunchGroceries extends AbstractShop {

	private final Shop shop = new Shop(false, 30000, 100, 55, 1,
		new Item(ItemId.BRANDY.id(), 5), new Item(ItemId.GIN.id(), 5), new Item(ItemId.VODKA.id(), 5),
		new Item(ItemId.WHISKY.id(), 5), new Item(ItemId.FRESH_PINEAPPLE.id(), 5), new Item(ItemId.EQUA_LEAVES.id(), 3),
		new Item(ItemId.ORANGE.id(), 5), new Item(ItemId.LEMON.id(), 5), new Item(ItemId.LIME.id(), 5),
		new Item(ItemId.DWELLBERRIES.id(), 3), new Item(ItemId.COCKTAIL_SHAKER.id(), 5), new Item(ItemId.CHOCOLATE_BAR.id(), 5),
		new Item(ItemId.CHOCOLATE_DUST.id(), 5), new Item(ItemId.CREAM.id(), 5), new Item(ItemId.MILK.id(), 5),
		new Item(ItemId.KNIFE.id(), 5), new Item(ItemId.GNOME_COCKTAIL_GUIDE.id(), 5));

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		say(player, n, "hello there");
		npcsay(player, n, "good day to you my friend ..and a beautiful one at that",
			"would you like some groceries? i have all sorts",
			"alcohol also, if your partial to a drink");

		int option = multi(player, n, "no thank you", "i'll have a look");
		switch (option) {
			case 0:
				npcsay(player, n, "ahh well, all the best to you");
				break;

			case 1:
				npcsay(player, n, "there's a good human");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.HECKEL_FUNCH.id();
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
