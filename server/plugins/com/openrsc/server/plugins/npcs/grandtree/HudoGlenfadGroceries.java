package com.openrsc.server.plugins.npcs.grandtree;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public final class HudoGlenfadGroceries implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 55, 1,
		new Item(ItemId.GIANNE_DOUGH.id(), 8), new Item(ItemId.EQUA_LEAVES.id(), 5), new Item(ItemId.POT_OF_FLOUR.id(), 5),
		new Item(ItemId.GNOME_SPICE.id(), 5), new Item(ItemId.ONION.id(), 5), new Item(ItemId.POTATO.id(), 3),
		new Item(ItemId.CABBAGE.id(), 3), new Item(ItemId.TOMATO.id(), 5), new Item(ItemId.CHEESE.id(), 5),
		new Item(ItemId.LIME.id(), 5), new Item(ItemId.ORANGE.id(), 5), new Item(ItemId.LEMON.id(), 5),
		new Item(ItemId.FRESH_PINEAPPLE.id(), 5), new Item(ItemId.DWELLBERRIES.id(), 3), new Item(ItemId.COCKTAIL_SHAKER.id(), 5),
		new Item(ItemId.CHOCOLATE_BAR.id(), 8), new Item(ItemId.CHOCOLATE_DUST.id(), 5), new Item(ItemId.CREAM.id(), 5),
		new Item(ItemId.MILK.id(), 5), new Item(ItemId.KNIFE.id(), 5), new Item(ItemId.GIANNE_COOK_BOOK.id(), 5));

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		playerTalk(p, n, "hello there");
		npcTalk(p, n, "good day ..and a beautiful one at that",
			"would you like some groceries? i have a large selection");

		int option = showMenu(p, n, "no thankyou", "i'll have a look");
		switch (option) {
			case 0:
				npcTalk(p, n, "ahh well, all the best to you");
				break;

			case 1:
				npcTalk(p, n, "great stuff");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
				break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.HUDO_GLENFAD.id();
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
