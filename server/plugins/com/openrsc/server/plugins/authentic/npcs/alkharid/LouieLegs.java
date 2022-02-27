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

public final class LouieLegs extends AbstractShop {

	private Shop shop = null;

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.LOUIE_LEGS.id();
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
		final String[] options;
		npcsay(player, n, "Hey, wanna buy some armour?");
		if (player.getQuestStage(Quests.FAMILY_CREST) <= 2 || player.getQuestStage(Quests.FAMILY_CREST) >= 5) {
			options = new String[]{
				"What have you got?",
				"No, thank you"
			};
		} else {
			options = new String[]{
				"What have you got?",
				"No, thank you",
				"I'm in search of a man named adam fitzharmon"
			};
		}
		int option = multi(player, n, options);

		if (option == 0) {
			npcsay(player, n, "Take a look, see");
			player.setAccessingShop(getShop(player.getWorld()));
			ActionSender.showShop(player, getShop(player.getWorld()));
		} else if (option == 1) {
			//nothing
		} else if (option == 2) {
			npcsay(player, n, "I haven't seen him",
					"I'm sure if he's been to Al Kharid recently",
					"Someone around here will have seen him though");
		}
	}

	public Shop getShop(World world) {
		if(shop == null) {
			shop = (world.getServer().getConfig().BASED_CONFIG_DATA >= 46 ?
				new Shop(false, 25000, 100, 65, 1,
					new Item(ItemId.BRONZE_PLATE_MAIL_LEGS.id(), 5),
					new Item(ItemId.IRON_PLATE_MAIL_LEGS.id(), 3),
					new Item(ItemId.STEEL_PLATE_MAIL_LEGS.id(), 2),
					new Item(ItemId.BLACK_PLATE_MAIL_LEGS.id(), 1),
					new Item(ItemId.MITHRIL_PLATE_MAIL_LEGS.id(), 1),
					new Item(ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id(), 1)
				) :
				new Shop(false, 25000, 100, 65, 1,
					new Item(ItemId.BRONZE_PLATE_MAIL_LEGS.id(), 5),
					new Item(ItemId.IRON_PLATE_MAIL_LEGS.id(), 3),
					new Item(ItemId.STEEL_PLATE_MAIL_LEGS.id(), 2),
					new Item(ItemId.MITHRIL_PLATE_MAIL_LEGS.id(), 1),
					new Item(ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id(), 1)
				));
		}
		return shop;
	}
}
