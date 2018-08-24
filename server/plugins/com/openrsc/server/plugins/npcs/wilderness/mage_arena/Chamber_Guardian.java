package com.openrsc.server.plugins.npcs.wilderness.mage_arena;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Chamber_Guardian implements ShopInterface, TalkToNpcExecutiveListener,
		TalkToNpcListener {
	
	private final Shop shop = new Shop(true, 60000 * 5, 100, 60, 2,
			new Item(1216, 5), new Item(1218, 5), new Item(1217, 5));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (p.getCache().hasKey("mage_arena")
				&& p.getCache().getInt("mage_arena") == 2) {
			playerTalk(p, n, "hello my friend, kolodion sent me down");
			npcTalk(p,
					n,
					"sssshhh...the gods are talking..i can hear their whispers",
					"..can you hear them adventurer...they're calling you");
			playerTalk(p, n, "erm...ok!");
			npcTalk(p, n,
					"go and chant to the sacred stone of your chosen god",
					"you will be rewarded");
			playerTalk(p, n, "ok?");
			npcTalk(p, n, "once you're done come back to me...",
					"...and i'll supply you with a mage staff ready for battle");
			p.getCache().set("mage_arena", 3);
		} else if ((p.getCache().hasKey("mage_arena") && p.getCache().getInt("mage_arena") == 3) && (hasItem(p, 1213) || hasItem(p, 1214) || hasItem(p, 1215))) {
			npcTalk(p, n, "hello adventurer, have you made your choice?");
			playerTalk(p, n, "i have");
			npcTalk(p, n, "good, good .. i hope you chose well",
					"you will have been rewarded with a magic cape",
					"now i will give you a magic staff",
					"these are all the weapons and armour you'll need here");
			p.message("the mage guardian gives you a magic staff");
			if(hasItem(p, 1213, 1)) {
				addItem(p, 1216, 1);
			}
			else if(hasItem(p, 1214, 1)) {
				addItem(p, 1218, 1);
			}
			else if(hasItem(p, 1215, 1)) {
				addItem(p, 1217, 1);
			}
			p.getCache().set("mage_arena", 4);
		} else if(p.getCache().hasKey("mage_arena") && p.getCache().getInt("mage_arena") == 4) {
			playerTalk(p, n, "hello again");
			npcTalk(p,n, "hello adventurer, are you looking for another staff");
			int choice = showMenu(p, n, "what do you have to offer?", "no thanks", "tell me what you know about the charge spell?");
			if(choice == 0) {
				npcTalk(p,n, "take a look");
				ActionSender.showShop(p, shop);
			} else if(choice == 1) {
				npcTalk(p,n,"well, let me know if you need one");
			}
			else if(choice == 2) {
				npcTalk(p,n, "we believe the spells are gifts from the gods",
						"the charge spell draws even more power from the cosmos",
						"while wearing a matching cape and staff",
						"it will double the damage caused by ...",
						"battle mage spells for several minutes");
				playerTalk(p,n, "good stuff");
			}
		} else {
			npcTalk(p, n, "hello adventurer, have you made your choice?");
			playerTalk(p, n, "no, not yet.");
			npcTalk(p, n, "once you're done come back to me...",
					"...and i'll supply you with a mage staff ready for battle");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 784;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return true;
	}
}
