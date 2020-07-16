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

public final class Tailor extends AbstractShop {

	private final Shop shop = new Shop(false, 30000, 130, 40, 2, new Item(ItemId.CHEFS_HAT.id(),
		0), new Item(ItemId.BLUE_WIZARDSHAT.id(), 3), new Item(ItemId.YELLOW_CAPE.id(), 1), new Item(ItemId.GREY_WOLF_FUR.id(), 3),
		new Item(ItemId.FUR.id(), 3), new Item(ItemId.NEEDLE.id(), 3), new Item(ItemId.THREAD.id(), 100),
		new Item(ItemId.LEATHER_GLOVES.id(), 10), new Item(ItemId.BOOTS.id(), 10), new Item(ItemId.PRIEST_ROBE.id(), 3),
		new Item(ItemId.PRIEST_GOWN.id(), 3), new Item(ItemId.BROWN_APRON.id(), 1), new Item(ItemId.PINK_SKIRT.id(), 5),
		new Item(ItemId.BLACK_SKIRT.id(), 3), new Item(ItemId.BLUE_SKIRT.id(), 2), new Item(ItemId.RED_CAPE.id(), 4),
		new Item(ItemId.EYE_PATCH.id(), 3));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.TAILOR.id();
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

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "Now you look like someone who goes to a lot of fancy dress parties");
		say(player, n, "Errr... what are you saying exactly?");
		npcsay(player, n, "I'm just saying that perhaps you would like to peruse my selection of garments");
		int opt = multi(player, n, false, //do not send over
			"I think I might just leave the perusing for now thanks",
			"OK,lets see what you've got then");
		if (opt == 0) {
			say(player, n, "I think I might just leave the perusing for now thanks");
		} else if (opt == 1) {
			say(player, n, "OK,let's see what you've got then");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}
	}
}
