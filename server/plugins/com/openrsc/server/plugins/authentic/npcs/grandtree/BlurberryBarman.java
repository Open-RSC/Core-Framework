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

public final class BlurberryBarman extends AbstractShop {

	private final Shop shop = new Shop(false, 3000, 100, 25, 1, new Item(ItemId.BLURBERRY_BARMAN_FRUIT_BLAST.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_BLURBERRY_SPECIAL.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_WIZARD_BLIZZARD.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_PINEAPPLE_PUNCH.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_SGG.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_CHOCOLATE_SATURDAY.id(), 10), new Item(ItemId.BLURBERRY_BARMAN_DRUNK_DRAGON.id(), 10));

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		npcsay(player, n, "good day to you", "can i get you drink?");
		int opt = multi(player, n, false, //do not send over
			"what do you have?", "no thanks");
		if (opt == 0) {
			say(player, n, "what do you have");
			npcsay(player, n, "take a look");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (opt == 1) {
			say(player, n, "no thanks");
			npcsay(player, n, "ok, take it easy");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BLURBERRY_BARMAN.id();
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
