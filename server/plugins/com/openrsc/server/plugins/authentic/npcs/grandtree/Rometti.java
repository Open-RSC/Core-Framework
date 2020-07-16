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

public final class Rometti extends AbstractShop {

	private final Shop shop = new Shop(false, 3000, 100, 55, 1,
		new Item(ItemId.GNOME_ROBE_PINK.id(), 5), new Item(ItemId.GNOME_ROBE_GREEN.id(), 5), new Item(ItemId.GNOME_ROBE_PURPLE.id(), 5),
		new Item(ItemId.GNOME_ROBE_CREAM.id(), 5), new Item(ItemId.GNOME_ROBE_BLUE.id(), 5), new Item(ItemId.GNOMESHAT_PINK.id(), 5),
		new Item(ItemId.GNOMESHAT_GREEN.id(), 5), new Item(ItemId.GNOMESHAT_PURPLE.id(), 5), new Item(ItemId.GNOMESHAT_CREAM.id(), 5),
		new Item(ItemId.GNOMESHAT_BLUE.id(), 5), new Item(ItemId.GNOME_TOP_PINK.id(), 5), new Item(ItemId.GNOME_TOP_GREEN.id(), 5),
		new Item(ItemId.GNOME_TOP_PURPLE.id(), 5), new Item(ItemId.GNOME_TOP_CREAM.id(), 5), new Item(ItemId.GNOME_TOP_BLUE.id(), 5),
		new Item(ItemId.BOOTS_PINK.id(), 5), new Item(ItemId.BOOTS_GREEN.id(), 5), new Item(ItemId.BOOTS_PURPLE.id(), 5),
		new Item(ItemId.BOOTS_CREAM.id(), 5), new Item(ItemId.BOOTS_BLUE.id(), 5));

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		say(player, n, "hello");
		npcsay(player, n, "hello traveller",
			"have a look at my latest range of gnome fashion",
			"rometti is the ultimate label in gnome high society");
		say(player, n, "really");
		npcsay(player, n, "pastels are all the rage this season");
		int option = multi(player, n, false, //do not send over
			"i've no time for fashion", "ok then let's have a look");
		switch (option) {
			case 0:
				say(player, n, "i've no time for fashion");
				npcsay(player, n, "hmm...i did wonder");
				break;
			case 1:
				say(player, n, "ok then, let's have a look");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.ROMETTI.id();
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
