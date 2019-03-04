package com.openrsc.server.plugins.npcs.alkharid;

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

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.Constants.Quests;

public final class RanaelSkirt implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 25000, 100, 65, 1,
		new Item(ItemId.BRONZE_PLATED_SKIRT.id(), 5),
		new Item(ItemId.IRON_PLATED_SKIRT.id(), 3),
		new Item(ItemId.STEEL_PLATED_SKIRT.id(), 2),
		new Item(ItemId.BLACK_PLATED_SKIRT.id(), 1),
		new Item(ItemId.MITHRIL_PLATED_SKIRT.id(), 1),
		new Item(ItemId.ADAMANTITE_PLATED_SKIRT.id(), 1)
	);

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.RANAEL.id();
	}

	@Override
	public Shop[] getShops() {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		final String[] options;
		npcTalk(p, n, "Do you want to buy any armoured skirts?",
			"Designed especially for ladies who like to fight");
		if (p.getQuestStage(Quests.FAMILY_CREST) <= 2 || p.getQuestStage(Quests.FAMILY_CREST) >= 5) {
			options = new String[]{
				"Yes please",
				"No thank you that's not my scene"
			};
		} else {
			options = new String[]{
				"Yes please",
				"No thank you that's not my scene",
				"I'm in search of a man named adam fitzharmon"
			};
		}
		int option = showMenu(p, n, false, options);

		if (option == 0) {
			playerTalk(p, n, "Yes Please");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (option == 1) {
			playerTalk(p, n, "No thank you that's not my scene");
		} else if (option == 2) {
			playerTalk(p, n, "I'm in search of a man named adam fitzharmon");
			npcTalk(p, n, "I haven't seen him",
					"I'm sure if he's been to Al Kharid recently",
					"Someone around here will have seen him though");
		}
	}

}
