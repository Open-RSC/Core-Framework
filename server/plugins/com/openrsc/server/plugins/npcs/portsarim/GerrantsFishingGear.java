package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.Constants;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public final class GerrantsFishingGear implements
		ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 12000, 100, 70, 3, new Item(376,
			5), new Item(377, 5), new Item(378, 5), new Item(379, 2),
			new Item(375, 2), new Item(380, 200), new Item(381, 200),
			new Item(349, 30), new Item(354, 0), new Item(361, 0),
			new Item(351, 0), new Item(358, 0), new Item(363, 0),
			new Item(356, 0), new Item(366, 0), new Item(372, 0),
			new Item(369, 0));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == 167;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Welcome you can buy any fishing equipment at my store",
				"We'll also buy anything you catch off you");

		String[] options;
		if (p.getQuestStage(Constants.Quests.HEROS_QUEST) >= 1) {
			options = new String[] { "Let's see what you've got then",
					"Sorry, I'm not interested",
					"I want to find out how to catch a lava eel" };
		} else {
			options = new String[] { "Let's see what you've got then",
					"Sorry, I'm not interested" };
		}
		int option = showMenu(p, n, options);
		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (option == 2) {
			npcTalk(p,
					n,
					"Lava eels eh?",
					"That's a tricky one that is",
					"I wouldn't even know where find them myself",
					"Probably in some lava somewhere",
					"You'll also need a lava proof fishing line",
					"The method for this would be take an ordinary fishing rod",
					"And cover it with fire proof blamish oil");
			if (!hasItem(p, 587)) {
				npcTalk(p, n, "Now I may have a jar of Blamish snail slime",
						"I wonder where I put it");
				p.message("Gerrant searches about a bit");
				npcTalk(p, n, "Aha here it is");
				p.message("Gerrant passes you a small jar");
				addItem(p, 587, 1);
				npcTalk(p, n,
						"You'll need to mix this with some of the Harralander herb and water");
			}
		}
	}

}
