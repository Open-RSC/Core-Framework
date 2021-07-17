package com.openrsc.server.plugins.authentic.npcs.portsarim;

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

public final class BriansBattleAxes extends AbstractShop {

	private final Shop shop = new Shop(false, 15000, 100, 55, 1, new Item(ItemId.BRONZE_BATTLE_AXE.id(),
		4), new Item(ItemId.IRON_BATTLE_AXE.id(), 3), new Item(ItemId.STEEL_BATTLE_AXE.id(), 2), new Item(ItemId.BLACK_BATTLE_AXE.id(), 1),
		new Item(ItemId.MITHRIL_BATTLE_AXE.id(), 1), new Item(ItemId.ADAMANTITE_BATTLE_AXE.id(), 1));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.BRIAN.id();
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
	public Shop getShop() {
		return shop;
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "ello");
		int option = multi(player, n, false, //do not send over
				"So are you selling something?", "ello");
		switch (option) {
			case 0:
				say(player, n, "So are you selling something?");
				npcsay(player, n, "Yep take a look at these great axes");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;
			case 1:
				say(player, n, "Ello");
				break;
		}
	}
}
