package com.openrsc.server.plugins.npcs.catherby;

import com.openrsc.server.constants.Quests;
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
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.*;

public class CandleMakerShop implements ShopInterface,
	TalkNpcTrigger {

	private final Shop shop = new Shop(false, 1000, 100, 80, 2, new Item(ItemId.UNLIT_CANDLE.id(), 10));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.CANDLEMAKER.id();
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
	public void onTalkNpc(final Player player, final Npc n) {
		if (player.getCache().hasKey("candlemaker")) {
			npcsay(player, n, "Have you got any wax yet?");
			if (player.getCarriedItems().hasCatalogID(ItemId.WAX_BUCKET.id())) {
				say(player, n, "Yes I have some now");
				player.getCarriedItems().remove(new Item(ItemId.WAX_BUCKET.id()));
				player.message("You exchange the wax with the candle maker for a black candle");
				give(player, ItemId.UNLIT_BLACK_CANDLE.id(), 1);
				player.getCache().remove("candlemaker");
			} else {
				//NOTHING HAPPENS
			}
			return;
		}
		Menu defaultMenu = new Menu();
		npcsay(player, n, "Hi would you be interested in some of my fine candles");
		if (player.getQuestStage(Quests.MERLINS_CRYSTAL) == 3) {
			defaultMenu.addOption(new Option("Have you got any black candles?") {
				@Override
				public void action() {
					npcsay(player, n, "Black candles hmm?",
						"It's very bad luck to make black candles");
					say(player, n, "I can pay well for one");
					npcsay(player, n, "I still dunno",
						"Tell you what, I'll supply with you with a black candle",
						"If you can bring me a bucket full of wax");
					player.getCache().store("candlemaker", true);
				}
			});

		}
		defaultMenu.addOption(new Option("Yes please") {
			@Override
			public void action() {
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			}
		});
		defaultMenu.addOption(new Option("No thankyou") {
			@Override
			public void action() {

			}
		});
		defaultMenu.showMenu(player);
	}

}
