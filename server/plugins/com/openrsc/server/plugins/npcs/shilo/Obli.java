package com.openrsc.server.plugins.npcs.shilo;

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

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class Obli implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(true, 15000, 150, 50, 2,
		new Item(ItemId.TINDERBOX.id(), 2), new Item(ItemId.EMPTY_VIAL.id(), 20), new Item(ItemId.PESTLE_AND_MORTAR.id(), 3),
		new Item(ItemId.POT.id(), 3), new Item(ItemId.BRONZE_AXE.id(), 3), new Item(ItemId.BRONZE_PICKAXE.id(), 2),
		new Item(ItemId.IRON_AXE.id(), 5), new Item(ItemId.LEATHER_ARMOUR.id(), 12), new Item(ItemId.LEATHER_GLOVES.id(), 10),
		new Item(ItemId.BOOTS.id(), 10), new Item(ItemId.COOKEDMEAT.id(), 2), new Item(ItemId.BREAD.id(), 10),
		new Item(ItemId.BRONZE_BAR.id(), 10), new Item(ItemId.SPADE.id(), 10), new Item(ItemId.UNLIT_CANDLE.id(), 10),
		new Item(ItemId.UNLIT_TORCH.id(), 10), new Item(ItemId.CHISEL.id(), 10), new Item(ItemId.HAMMER.id(), 10),
		new Item(ItemId.PAPYRUS.id(), 50), new Item(ItemId.A_LUMP_OF_CHARCOAL.id(), 50), new Item(ItemId.SLEEPING_BAG.id(), 10),
		new Item(ItemId.MACHETTE.id(), 50));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.OBLI.id()) {
			npcTalk(p, n, "Welcome to Obli's General Store Bwana!",
				"Would you like to see my items?");
			int menu = showMenu(p, n,
				"Yes please!",
				"No, but thanks for the offer.");
			if (menu == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			} else if (menu == 1) {
				npcTalk(p, n, "That's fine and thanks for your interest.");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.OBLI.id();
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
