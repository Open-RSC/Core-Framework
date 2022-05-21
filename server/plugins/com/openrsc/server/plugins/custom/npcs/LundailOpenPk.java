package com.openrsc.server.plugins.custom.npcs;

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

public final class LundailOpenPk extends AbstractShop {

	private final Shop shop = new Shop(false, 6000, 190, 60, 10, new Item(ItemId.AIR_RUNE.id(),
		500), new Item(ItemId.FIRE_RUNE.id(), 500), new Item(ItemId.WATER_RUNE.id(), 500), new Item(ItemId.EARTH_RUNE.id(),
		500), new Item(ItemId.MIND_RUNE.id(), 500), new Item(ItemId.BODY_RUNE.id(), 500), new Item(ItemId.CHAOS_RUNE.id(), 500), new Item(ItemId.NATURE_RUNE.id(), 500), new Item(ItemId.DEATH_RUNE.id(), 500), new Item(ItemId.BLOOD_RUNE.id(), 500));

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		say(player, n, "well hello sir");
		npcsay(player, n, "hello brave adventurer",
			"how can i help you?");

		int option = multi(player, n, "what are you selling?",
			"what's that big old building behind us?");
		switch (option) {
			case 0:
				npcsay(player, n, "why, i sell rune stones",
					"i've got some good stuff, real powerful little rocks",
					"take a look");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;

			case 1:
				npcsay(player, n, "why that my friend...",
					"...is the mage battle arena",
					"top mages come from all over to compete in the arena",
					"few return back, most get fried...hence the smell");
				npcsay(player, n, "hmmm.. i did notice");
				break;

		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return player.getConfig().WANT_OPENPK_POINTS && n.getID() == NpcId.LUNDAIL.id();
	}

	@Override
	public Shop[] getShops(World word) {
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
