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
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import java.util.ArrayList;
import java.util.List;

import static com.openrsc.server.plugins.Functions.*;

public final class WaynesChains implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 25000, 100, 65, 1, new Item(ItemId.BRONZE_CHAIN_MAIL_BODY.id(),
		3), new Item(ItemId.IRON_CHAIN_MAIL_BODY.id(), 2), new Item(ItemId.STEEL_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.BLACK_CHAIN_MAIL_BODY.id(), 1),
		new Item(ItemId.MITHRIL_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.ADAMANTITE_CHAIN_MAIL_BODY.id(), 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
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
	public void onTalkToNpc(final Player p, final Npc n) {
		if (n.getID() == NpcId.WAYNE.id()) {
			npcTalk(p, n, "Welcome to Wayne's chains",
				"Do you wanna buy or sell some chain mail?");

			List<String> options = new ArrayList<>();
			options.add("Yes please");
			options.add("No thanks");
			if (p.getCache().hasKey("miniquest_dwarf_youth_rescue")
			&& p.getCache().getInt("miniquest_dwarf_youth_rescue") == 2)
				options.add("I need your help with a special armour");
			String[] optionsArray = new String[options.size()];
			optionsArray = options.toArray(optionsArray);
			int option = showMenu(p, n, false, //do not send over
				optionsArray);
			if (option == 0) {
				playerTalk(p, n, "Yes Please");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			} else if (option == 1) {
				playerTalk(p, n, "No thanks");
			} else if (option == 2) {
				playerTalk(p, n, "I need your help with a special armor");
				npcTalk(p, n, "special you say? how can I help?");
				playerTalk(p, n, "Gramat told me you can make dragon scale mail");
				npcTalk(p, n, "ah yes. I am able, but it's very difficult",
					"first, you need to bring me the materials",
					"500 dragon metal chains",
					"and 150 chipped dragon scales");
				boolean hasChains = false;
				boolean hasScales = false;
				if (p.getCarriedItems().getInventory().countId(ItemId.CHIPPED_DRAGON_SCALE.id()) >= 150) {
					hasScales = true;
					playerTalk(p, n, "i have the scales here");
				} else
					playerTalk(p, n, "i don't seem to have enough scales");

				if (p.getCarriedItems().getInventory().countId(ItemId.DRAGON_METAL_CHAIN.id()) >= 500) {
					hasChains = true;
					playerTalk(p, n, "i have the chains here");
				} else
					playerTalk(p, n, "i don't seem to have enough chains");

				if (hasChains && hasScales) {
					npcTalk(p, n, "great, you have the materials",
						"for my time I also require compensation",
						"how does 500,000 gold pieces sound");
					int option2 = showMenu(p, n, false,
						"sounds fair", "no way");
					if (option2 == 0) {
						playerTalk(p, n, "sounds fair");
						if (p.getCarriedItems().getInventory().countId(ItemId.COINS.id()) < 500000) {
							playerTalk(p, n, "but I'm short at the moment");
							npcTalk(p, n, "get the money and return to me");
							return;
						}
						if (p.getCarriedItems().remove(ItemId.CHIPPED_DRAGON_SCALE.id(), 150) > -1) {
							if (p.getCarriedItems().remove(ItemId.DRAGON_METAL_CHAIN.id() ,500) > -1) {
								if (p.getCarriedItems().remove(ItemId.COINS.id(), 500000) > -1) {
									p.message("you hand over the materials and money");
									sleep(p.getWorld().getServer().getConfig().GAME_TICK * 4);
									p.message("Wayne flashes a smile");
									sleep(p.getWorld().getServer().getConfig().GAME_TICK * 4);
									npcTalk(p, n, "i happen to have one made already",
										"so there's no need for you to wait");
									p.message("Wayne hands you a dragon scale mail");
									addItem(p, ItemId.DRAGON_SCALE_MAIL.id(), 1);
									playerTalk(p, n, "thanks");
									npcTalk(p, n, "my pleasure " + p.getUsername(),
										"if you need my help again",
										"i'm always open");
									p.message("Congratulations! You have received a dragon scale mail");
								}
							}
						}
					} else if (option2 == 1) {
						playerTalk(p, n, "no way");
						npcTalk(p, n, "suit yourself");
					}
				} else {
					npcTalk(p, n, "if you're able to gather them",
						"come see me again");
				}
			}
		}
	}

}
