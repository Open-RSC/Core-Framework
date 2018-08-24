package com.openrsc.server.plugins.npcs.catherby;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;

import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

public class CandleMakerShop  implements ShopInterface,
TalkToNpcListener, TalkToNpcExecutiveListener {

	private static final int CANDLEMAKER = 282;
	private final Shop shop = new Shop(false, 1000, 100, 80,2, new Item(599, 10));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == CANDLEMAKER;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if(p.getCache().hasKey("candlemaker")) {
			npcTalk(p,n, "Have you got any wax yet?");
			if(p.getInventory().hasItemId(605)) {
				playerTalk(p,n, "Yes I have some now");
				p.message("You exchange the wax with the candle maker for a black candle");
				addItem(p, 600, 1);
				p.getCache().remove("candlemaker");
			} else {
				//NOTHING HAPPENS
			}
			return;
		}
		Menu defaultMenu = new Menu();
		npcTalk(p,n, "Hi would you be interested in some of my fine candles");
		if(p.getQuestStage(Quests.MERLINS_CRYSTAL) == 3) {
			defaultMenu.addOption(new Option("Have you got any black candles?") {
				@Override
				public void action() {
					npcTalk(p,n, "Black candles hmm?",
							"It's very bad luck to make black candles");
					playerTalk(p,n, "I can pay well for one");
					npcTalk(p,n, "I still dunno",
							"Tell you what, I'll supply with you with a black candle",
							"If you can bring me a bucket full of wax");
					p.getCache().store("candlemaker", true);
				}
			});

		}
		defaultMenu.addOption(new Option("Yes please") {
			@Override
			public void action() {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}
		});
		defaultMenu.addOption(new Option("No thankyou") {
			@Override
			public void action() {

			}
		});
		defaultMenu.showMenu(p);
	}

}
