package com.openrsc.server.plugins.authentic.npcs.alkharid;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;

import static com.openrsc.server.plugins.Functions.*;

public final class ZekeScimitars extends AbstractShop {

	private final Shop shop = new Shop(false, 25000, 100, 55, 2,
		new Item(ItemId.BRONZE_SCIMITAR.id(), 5),
		new Item(ItemId.IRON_SCIMITAR.id(), 3),
		new Item(ItemId.STEEL_SCIMITAR.id(), 2),
		new Item(ItemId.MITHRIL_SCIMITAR.id(), 1)
	);

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.ZEKE.id();
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
		final String[] options;
		npcsay(player, n, player.getText("ZekeAThousandGreetings"));
		if (player.getQuestStage(Quests.FAMILY_CREST) <= 2 || player.getQuestStage(Quests.FAMILY_CREST) >= 5) {
			options = new String[]{
				"Do you want to trade?",
				"Nice cloak"
			};
		} else {
			options = new String[]{
				"Do you want to trade?",
				"Nice cloak",
				"I'm in search of a man named adam fitzharmon"
			};
		}

		int option = multi(player, n, options);
		if (option == 0) {
			npcsay(player, n, "Yes, certainly", "I deal in scimitars");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (option == 1) {
			npcsay(player, n, "Thank you");
		} else if (option == 2) {
			npcsay(player, n, "I haven't seen him",
					"I'm sure if he's been to Al Kharid recently",
					"Someone around here will have seen him though");
		}
	}
}
