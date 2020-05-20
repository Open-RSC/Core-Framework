package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;

import static com.openrsc.server.plugins.Functions.multi;
import static com.openrsc.server.plugins.Functions.npcsay;

public class Scavvo extends AbstractShop {

	private final Shop scavvosShop = new Shop(false, 300000, 100, 60, 2,
		new Item(ItemId.RUNE_SKIRT.id(), 1), new Item(ItemId.RUNE_PLATE_MAIL_LEGS.id(), 1), new Item(ItemId.RUNE_MACE.id(), 1),
		new Item(ItemId.RUNE_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.RUNE_LONG_SWORD.id(), 1), new Item(ItemId.RUNE_SHORT_SWORD.id(), 1));

	@Override
	public Shop[] getShops(World world) { return new Shop[]{scavvosShop}; }

	@Override
	public boolean isMembers() { return false; }

	@Override
	public Shop getShop() { return scavvosShop; }

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "Ello matey", "Want to buy some exciting new toys?");
		int options = multi(player, n, "No, toys are for kids", "Lets have a look then", "Ooh goody goody toys");
		if (options == 1 || options == 2) {
			player.setAccessingShop(scavvosShop);
			ActionSender.showShop(player, scavvosShop);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.SCAVVO.id();
	}
}
