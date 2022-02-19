package com.openrsc.server.plugins.authentic.npcs.falador;

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

public final class HerquinGems extends AbstractShop {

	private Shop shop = null;

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.HERQUIN.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{getShop(world)};
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public Shop getShop() {
		return shop;
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		int option = multi(player, n, false, //do not send over
			"Do you wish to trade?", "Sorry i don't want to talk to you actually");
		if (option == 0) {
			say(player, n, "Do you wish to trade?");
			npcsay(player, n, "Why yes this a jewel shop after all");
			player.setAccessingShop(getShop(player.getWorld()));
			ActionSender.showShop(player, getShop(player.getWorld()));
		} else if (option == 1) {
			say(player, n, "Sorry I don't want to talk to you actually");
		}
	}

	public Shop getShop(World world) {
		if(shop == null) {
			shop = (world.getServer().getConfig().BASED_CONFIG_DATA >= 24 ?
				new Shop(false, 60000 * 10, 100, 70, 3, new Item(ItemId.UNCUT_SAPPHIRE.id(),
					1), new Item(ItemId.UNCUT_EMERALD.id(), 0), new Item(ItemId.UNCUT_RUBY.id(), 0), new Item(ItemId.UNCUT_DIAMOND.id(), 0),
					new Item(ItemId.SAPPHIRE.id(), 1), new Item(ItemId.EMERALD.id(), 0), new Item(ItemId.RUBY.id(), 0),
					new Item(ItemId.DIAMOND.id(), 0)) :
				new Shop(false, 60000 * 10, 100, 70, 3, new Item(ItemId.UNCUT_SAPPHIRE.id(),
					0), new Item(ItemId.UNCUT_EMERALD.id(), 0), new Item(ItemId.UNCUT_RUBY.id(), 0), new Item(ItemId.UNCUT_DIAMOND.id(), 0),
					new Item(ItemId.SAPPHIRE.id(), 0), new Item(ItemId.EMERALD.id(), 0), new Item(ItemId.RUBY.id(), 0),
					new Item(ItemId.DIAMOND.id(), 0)));
		}
		return shop;
	}
}
