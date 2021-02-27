package com.openrsc.server.plugins.authentic.npcs.varrock;

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

public class Scavvo extends AbstractShop {

	private Shop scavvosShop = null;

	@Override
	public Shop[] getShops(World world) { return new Shop[]{getShop(world)}; }

	@Override
	public boolean isMembers() { return false; }

	@Override
	public Shop getShop() { return scavvosShop; }

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "Ello matey", "Want to buy some exciting new toys?");
		int options = multi(player, n, false, // do not send over
			"No, toys are for kids", "Lets have a look then", "Ooh goody goody toys");
		if (options == 0) {
			say(player, n, "No toys are for kids");
		} else if (options == 1) {
			say(player, n, "Let's have a look then");
		} else if (options == 2) {
			say(player, n, "Ooh goody goody toys");
		}
		if (options == 1 || options == 2) {
			player.setAccessingShop(scavvosShop);
			ActionSender.showShop(player, scavvosShop);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.SCAVVO.id();
	}

	public Shop getShop(World world) {
		if(scavvosShop == null) {
			scavvosShop = (world.getServer().getConfig().WANT_CHAIN_LEGS) ?
				new Shop(false, 300000, 100, 60, 2, new Item(ItemId.RUNE_SKIRT.id(), 1),
					new Item(ItemId.RUNE_PLATE_MAIL_LEGS.id(), 1), new Item(ItemId.RUNE_MACE.id(), 1), new Item(ItemId.RUNE_CHAIN_MAIL_BODY.id(), 1),
					new Item(ItemId.RUNE_CHAIN_MAIL_LEGS.id(), 1), new Item(ItemId.RUNE_LONG_SWORD.id(), 1), new Item(ItemId.RUNE_SHORT_SWORD.id(), 1)) :
				new Shop(false, 300000, 100, 60, 2,
					new Item(ItemId.RUNE_SKIRT.id(), 1), new Item(ItemId.RUNE_PLATE_MAIL_LEGS.id(), 1), new Item(ItemId.RUNE_MACE.id(), 1),
					new Item(ItemId.RUNE_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.RUNE_LONG_SWORD.id(), 1), new Item(ItemId.RUNE_SHORT_SWORD.id(), 1));
		}

		return scavvosShop;
	}
}
