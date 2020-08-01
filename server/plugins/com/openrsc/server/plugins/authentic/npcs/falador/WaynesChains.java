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

import java.util.ArrayList;
import java.util.List;

import static com.openrsc.server.plugins.Functions.*;

public final class WaynesChains extends AbstractShop {

	private Shop shop = null;

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.WAYNE.id();
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

	public Shop getShop(World world) {
		if(shop == null) {
			shop = (world.getServer().getConfig().WANT_CHAIN_LEGS) ?
				new Shop(false, 25000, 100, 65, 1, new Item(ItemId.BRONZE_CHAIN_MAIL_LEGS.id(),
					3), new Item(ItemId.IRON_CHAIN_MAIL_LEGS.id(), 2), new Item(ItemId.STEEL_CHAIN_MAIL_LEGS.id(), 1), new Item(ItemId.BLACK_CHAIN_MAIL_LEGS.id(), 1),
					new Item(ItemId.MITHRIL_CHAIN_MAIL_LEGS.id(), 1), new Item(ItemId.ADAMANTITE_CHAIN_MAIL_LEGS.id(), 1), new Item(ItemId.BRONZE_CHAIN_MAIL_BODY.id(), 3),
					new Item(ItemId.IRON_CHAIN_MAIL_BODY.id(), 2), new Item(ItemId.STEEL_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.BLACK_CHAIN_MAIL_BODY.id(), 1),
					new Item(ItemId.MITHRIL_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.ADAMANTITE_CHAIN_MAIL_BODY.id(), 1)) :
				new Shop(false, 25000, 100, 65, 1, new Item(ItemId.BRONZE_CHAIN_MAIL_BODY.id(),
					3), new Item(ItemId.IRON_CHAIN_MAIL_BODY.id(), 2), new Item(ItemId.STEEL_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.BLACK_CHAIN_MAIL_BODY.id(), 1),
					new Item(ItemId.MITHRIL_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.ADAMANTITE_CHAIN_MAIL_BODY.id(), 1));
		}

		return shop;
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (n.getID() == NpcId.WAYNE.id()) {
			npcsay(player, n, "Welcome to Wayne's chains",
				"Do you wanna buy or sell some chain mail?");

			List<String> options = new ArrayList<>();
			options.add("Yes please");
			options.add("No thanks");
			if (player.getCache().hasKey("miniquest_dwarf_youth_rescue")
			&& player.getCache().getInt("miniquest_dwarf_youth_rescue") == 2)
				options.add("I need your help with a special armour");
			String[] optionsArray = new String[options.size()];
			optionsArray = options.toArray(optionsArray);
			int option = multi(player, n, false, //do not send over
				optionsArray);
			if (option == 0) {
				say(player, n, "Yes Please");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			} else if (option == 1) {
				say(player, n, "No thanks");
			} else if (option == 2) {
				say(player, n, "I need your help with a special armor");
				npcsay(player, n, "special you say? how can I help?");
				say(player, n, "Gramat told me you can make dragon scale mail");
				npcsay(player, n, "ah yes. I am able, but it's very difficult",
					"first, you need to bring me the materials",
					"500 dragon metal chains",
					"and 150 chipped dragon scales");
				boolean hasChains = false;
				boolean hasScales = false;
				if (player.getCarriedItems().getInventory().countId(ItemId.CHIPPED_DRAGON_SCALE.id()) >= 150) {
					hasScales = true;
					say(player, n, "i have the scales here");
				} else
					say(player, n, "i don't seem to have enough scales");

				if (player.getCarriedItems().getInventory().countId(ItemId.DRAGON_METAL_CHAIN.id()) >= 500) {
					hasChains = true;
					say(player, n, "i have the chains here");
				} else
					say(player, n, "i don't seem to have enough chains");

				if (hasChains && hasScales) {
					npcsay(player, n, "great, you have the materials",
						"for my time I also require compensation",
						"how does 500,000 gold pieces sound");
					int option2 = multi(player, n, false,
						"sounds fair", "no way");
					if (option2 == 0) {
						say(player, n, "sounds fair");
						if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) < 500000) {
							say(player, n, "but I'm short at the moment");
							npcsay(player, n, "get the money and return to me");
							return;
						}
						if (player.getCarriedItems().remove(new Item(ItemId.CHIPPED_DRAGON_SCALE.id(), 150)) > -1) {
							if (player.getCarriedItems().remove(new Item(ItemId.DRAGON_METAL_CHAIN.id() ,500)) > -1) {
								if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 500000)) > -1) {
									player.message("you hand over the materials and money");
									delay(4);
									player.message("Wayne flashes a smile");
									delay(4);
									npcsay(player, n, "i happen to have one made already",
										"so there's no need for you to wait");
									player.message("Wayne hands you a dragon scale mail");
									give(player, ItemId.DRAGON_SCALE_MAIL.id(), 1);
									say(player, n, "thanks");
									npcsay(player, n, "my pleasure " + player.getUsername(),
										"if you need my help again",
										"i'm always open");
									player.message("Congratulations! You have received a dragon scale mail");
								}
							}
						}
					} else if (option2 == 1) {
						say(player, n, "no way");
						npcsay(player, n, "suit yourself");
					}
				} else {
					npcsay(player, n, "if you're able to gather them",
						"come see me again");
				}
			}
		}
	}
}
