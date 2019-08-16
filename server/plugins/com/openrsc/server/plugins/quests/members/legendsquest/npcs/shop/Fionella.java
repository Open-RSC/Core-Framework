package com.openrsc.server.plugins.quests.members.legendsquest.npcs.shop;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

public class Fionella implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(true, 20000, 155, 55, 13,
		new Item(ItemId.SWORDFISH.id(), 2), new Item(ItemId.APPLE_PIE.id(), 5), new Item(ItemId.SLEEPING_BAG.id(), 1),
		new Item(ItemId.FULL_ATTACK_POTION.id(), 3), new Item(ItemId.STEEL_ARROWS.id(), 50));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.FIONELLA.id()) {
			npcTalk(p, n, "Can I help you at all?");
			int menu = showMenu(p, n,
				"Yes please. What are you selling?",
				"No thanks");
			if (menu == 0) {
				npcTalk(p, n, "Take a look");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.FIONELLA.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}
}
