package com.openrsc.server.plugins.authentic.npcs.catherby;

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

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public class CandleMakerShop extends AbstractShop {

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
	public Shop getShop() {
		return shop;
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

		ArrayList<String> options = new ArrayList<>();

		npcsay(player, n, "Hi would you be interested in some of my fine candles");

		String questOption = "Have you got any black candles?";
		if (player.getQuestStage(Quests.MERLINS_CRYSTAL) == 3) {
			options.add(questOption);
		}

		String optionYes = "Yes please";
		options.add(optionYes);

		options.add("No thankyou");

		String[] finalOptions = new String[options.size()];
		int option = multi(player, n, options.toArray(finalOptions));

		if (option == -1) return;
		if (options.get(option).equalsIgnoreCase(questOption)) {
			npcsay(player, n, "Black candles hmm?",
				"It's very bad luck to make black candles");
			say(player, n, "I can pay well for one");
			npcsay(player, n, "I still dunno",
				"Tell you what, I'll supply with you with a black candle",
				"If you can bring me a bucket full of wax");
			player.getCache().store("candlemaker", true);
		} else if (options.get(option).equalsIgnoreCase(optionYes)) {
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}
	}
}
