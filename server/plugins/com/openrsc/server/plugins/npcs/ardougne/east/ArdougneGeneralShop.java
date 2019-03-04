package com.openrsc.server.plugins.npcs.ardougne.east;

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

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class ArdougneGeneralShop implements ShopInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener {

	private final Shop shop = new Shop(true, 15000, 130, 40, 3, new Item(ItemId.VIAL.id(),
		10), new Item(ItemId.BRONZE_PICKAXE.id(), 2), new Item(ItemId.IRON_AXE.id(), 2), new Item(ItemId.COOKEDMEAT.id(), 2),
		new Item(ItemId.TINDERBOX.id(), 2), new Item(ItemId.BALL_OF_WOOL.id(), 2), new Item(ItemId.BRONZE_ARROWS.id(), 30),
		new Item(ItemId.ROPE.id(), 1), new Item(ItemId.PAPYRUS.id(), 50), new Item(ItemId.SLEEPING_BAG.id(), 10));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.KORTAN.id() || n.getID() == NpcId.AEMAD.id();
	}

	@Override
	public Shop[] getShops() {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Hello you look like a bold adventurer",
			"You've come to the right place for adventurer's equipment");
		final int option = showMenu(p, n, false, //do not send over
			"Oh that sounds intersting",
			"No I've come to the wrong place");
		if (option == 0) {
			playerTalk(p, n, "Oh that sounds interesting");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (option == 1) {
			playerTalk(p, n, "No I've come to the wrong place");
			npcTalk(p, n, "Hmph");
		}
	}

}
