package com.openrsc.server.plugins.npcs.falador;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.List;

import static com.openrsc.server.plugins.Functions.*;

public final class WaynesChains implements ShopInterface,
	TalkNpcTrigger {

	private final Shop shop = new Shop(false, 25000, 100, 65, 1, new Item(ItemId.BRONZE_CHAIN_MAIL_BODY.id(),
		3), new Item(ItemId.IRON_CHAIN_MAIL_BODY.id(), 2), new Item(ItemId.STEEL_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.BLACK_CHAIN_MAIL_BODY.id(), 1),
		new Item(ItemId.MITHRIL_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.ADAMANTITE_CHAIN_MAIL_BODY.id(), 1));

	@Override
	public boolean blockTalkNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.WAYNE.id();
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
	public void onTalkNpc(final Player p, final Npc n) {
		if (n.getID() == NpcId.WAYNE.id()) {
			npcsay(p, n, "Welcome to Wayne's chains",
				"Do you wanna buy or sell some chain mail?");

			List<String> options = new ArrayList<>();
			options.add("Yes please");
			options.add("No thanks");
			if (p.getCache().hasKey("miniquest_dwarf_youth_rescue")
			&& p.getCache().getInt("miniquest_dwarf_youth_rescue") == 2)
				options.add("I need your help with a special armour");
			String[] optionsArray = new String[options.size()];
			optionsArray = options.toArray(optionsArray);
			int option = multi(p, n, false, //do not send over
				optionsArray);
			if (option == 0) {
				say(p, n, "Yes Please");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			} else if (option == 1) {
				say(p, n, "No thanks");
			} else if (option == 2) {
				say(p, n, "I need your help with a special armor");
				npcsay(p, n, "special you say? how can I help?");
				say(p, n, "Gramat told me you can make dragon scale mail");
				npcsay(p, n, "ah yes. I am able, but it's very difficult",
					"first, you need to bring me the materials",
					"500 dragon metal chains",
					"and 150 chipped dragon scales");
				boolean hasChains = false;
				boolean hasScales = false;
				if (p.getCarriedItems().getInventory().countId(ItemId.CHIPPED_DRAGON_SCALE.id()) >= 150) {
					hasScales = true;
					say(p, n, "i have the scales here");
				} else
					say(p, n, "i don't seem to have enough scales");

				if (p.getCarriedItems().getInventory().countId(ItemId.DRAGON_METAL_CHAIN.id()) >= 500) {
					hasChains = true;
					say(p, n, "i have the chains here");
				} else
					say(p, n, "i don't seem to have enough chains");

				if (hasChains && hasScales) {
					npcsay(p, n, "great, you have the materials",
						"for my time I also require compensation",
						"how does 500,000 gold pieces sound");
					int option2 = multi(p, n, false,
						"sounds fair", "no way");
					if (option2 == 0) {
						say(p, n, "sounds fair");
						if (p.getCarriedItems().getInventory().countId(ItemId.COINS.id()) < 500000) {
							say(p, n, "but I'm short at the moment");
							npcsay(p, n, "get the money and return to me");
							return;
						}
						if (p.getCarriedItems().remove(new Item(ItemId.CHIPPED_DRAGON_SCALE.id(), 150)) > -1) {
							if (p.getCarriedItems().remove(new Item(ItemId.DRAGON_METAL_CHAIN.id() ,500)) > -1) {
								if (p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 500000)) > -1) {
									p.message("you hand over the materials and money");
									delay(p.getWorld().getServer().getConfig().GAME_TICK * 4);
									p.message("Wayne flashes a smile");
									delay(p.getWorld().getServer().getConfig().GAME_TICK * 4);
									npcsay(p, n, "i happen to have one made already",
										"so there's no need for you to wait");
									p.message("Wayne hands you a dragon scale mail");
									give(p, ItemId.DRAGON_SCALE_MAIL.id(), 1);
									say(p, n, "thanks");
									npcsay(p, n, "my pleasure " + p.getUsername(),
										"if you need my help again",
										"i'm always open");
									p.message("Congratulations! You have received a dragon scale mail");
								}
							}
						}
					} else if (option2 == 1) {
						say(p, n, "no way");
						npcsay(p, n, "suit yourself");
					}
				} else {
					npcsay(p, n, "if you're able to gather them",
						"come see me again");
				}
			}
		}
	}

}
