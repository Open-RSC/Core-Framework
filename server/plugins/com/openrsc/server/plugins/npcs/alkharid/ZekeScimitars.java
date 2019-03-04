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
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.Constants.Quests;

public final class ZekeScimitars implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 25000, 100, 55, 2,
		new Item(ItemId.BRONZE_SCIMITAR.id(), 5),
		new Item(ItemId.IRON_SCIMITAR.id(), 3),
		new Item(ItemId.STEEL_SCIMITAR.id(), 2),
		new Item(ItemId.MITHRIL_SCIMITAR.id(), 1)
	);

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.ZEKE.id();
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
		npcTalk(p, n, "A thousand greetings " + ((p.isMale()) ? "sir" : "madam"));
		if (p.getQuestStage(Quests.FAMILY_CREST) <= 2 || p.getQuestStage(Quests.FAMILY_CREST) >= 5) {
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

		int option = showMenu(p, n, options);
		if (option == 0) {
			npcTalk(p, n, "Yes, certainly", "I deal in scimitars");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (option == 1) {
			npcTalk(p, n, "Thank you");
		} else if (option == 2) {
			npcTalk(p, n, "I haven't seen him",
					"I'm sure if he's been to Al Kharid recently",
					"Someone around here will have seen him though");
		}
	}

}
