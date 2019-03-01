package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public final class TeaSeller implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener, PickupExecutiveListener,
	PickupListener {

	private final Shop shop = new Shop(false, 30000, 100, 60, 2, new Item(ItemId.CUP_OF_TEA.id(),
		20));

	@Override
	public boolean blockPickup(final Player p, final GroundItem i) {
		return i.getID() == ItemId.DISPLAY_TEA.id();
	}

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.TEA_SELLER.id();
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
	public void onPickup(final Player p, final GroundItem i) {
		if (i.getID() == ItemId.DISPLAY_TEA.id()) {
			final Npc n = World.getWorld().getNpcById(NpcId.TEA_SELLER.id());
			if (n == null) {
				return;
			}
			n.face(p);
			npcTalk(p, n, "Hey ! get your hands off that tea !",
				"That's for display purposes only",
				"Im not running a charity here !");
		}
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Greetings!",
			"Are you in need of refreshment ?");

		final String[] options = new String[]{"Yes please", "No thanks",
			"What are you selling ?"};
		int option = showMenu(p, n, options);
		switch (option) {
			case 0:
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
				break;
			case 1:
				npcTalk(p, n, "Well, if you're sure",
					"You know where to come if you do !");
				break;
			case 2:
				npcTalk(p, n, "Only the most delicious infusion",
					"Of the leaves of the tea plant",
					"Grown in the exotic regions of this world...",
					"Buy yourself a cup !");
				break;
		}
	}

}
