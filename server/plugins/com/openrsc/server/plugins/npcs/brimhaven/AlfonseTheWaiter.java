package com.openrsc.server.plugins.npcs.brimhaven;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isBlackArmGang;

public class AlfonseTheWaiter implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 10000, 110, 75, 2,
		new Item(ItemId.HERRING.id(), 5), new Item(ItemId.COD.id(), 5),
		new Item(ItemId.TUNA.id(), 5), new Item(ItemId.LOBSTER.id(), 3), new Item(ItemId.SWORDFISH.id(), 2));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.ALFONSE_THE_WAITER.id()) {
			npcTalk(p, n, "Welcome to the shrimp and parrot",
				"Would you like to order sir?");
			int menu;
			if (isBlackArmGang(p) || (p.getQuestStage(Constants.Quests.HEROS_QUEST) != 1 && p.getQuestStage(Constants.Quests.HEROS_QUEST) != 2 && !p.getCache().hasKey("pheonix_mission") && !p.getCache().hasKey("pheonix_alf"))) {
				menu = showMenu(p, n,
					"Yes please",
					"No thankyou");
			} else {
				menu = showMenu(p, n,
					"Yes please",
					"No thankyou",
					"Do you sell Gherkins?");
			}
			if (menu == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			} else if (menu == 2) {
				npcTalk(p, n, "Hmm ask Charlie the cook round the back",
					"He may have some Gherkins for you");
				message(p, "Alfonse winks");
				p.getCache().store("talked_alf", true);
				p.getCache().remove("pheonix_alf");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.ALFONSE_THE_WAITER.id();
	}

	@Override
	public Shop[] getShops() {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

}
