package com.openrsc.server.plugins.authentic.npcs.ardougne.east;

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

public class ArdougneGeneralShop extends AbstractShop {

	private final Shop shop = new Shop(true, 15000, 130, 40, 3, new Item(ItemId.VIAL.id(),
		10), new Item(ItemId.BRONZE_PICKAXE.id(), 2), new Item(ItemId.IRON_AXE.id(), 2), new Item(ItemId.COOKEDMEAT.id(), 2),
		new Item(ItemId.TINDERBOX.id(), 2), new Item(ItemId.BALL_OF_WOOL.id(), 2), new Item(ItemId.BRONZE_ARROWS.id(), 30),
		new Item(ItemId.ROPE.id(), 1), new Item(ItemId.PAPYRUS.id(), 50), new Item(ItemId.SLEEPING_BAG.id(), 10));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.KORTAN.id() || n.getID() == NpcId.AEMAD.id();
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
		npcsay(player, n, "Hello you look like a bold adventurer",
			"You've come to the right place for adventurer's equipment");
		final int option = multi(player, n, false, //do not send over
			"Oh that sounds intersting",
			"No I've come to the wrong place");
		if (option == 0) {
			say(player, n, "Oh that sounds interesting");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (option == 1) {
			say(player, n, "No I've come to the wrong place");
			npcsay(player, n, "Hmph");
		}
	}
}
