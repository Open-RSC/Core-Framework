package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public final class WydinsGrocery implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener,
	WallObjectActionExecutiveListener, WallObjectActionListener {

	private final Shop shop = new Shop(false, 12500, 100, 70, 1, new Item(ItemId.POT_OF_FLOUR.id(),
		3), new Item(ItemId.RAW_CHICKEN.id(), 1), new Item(ItemId.CABBAGE.id(), 3), new Item(ItemId.BANANA.id(), 3),
		new Item(ItemId.REDBERRIES.id(), 1), new Item(ItemId.BREAD.id(), 0), new Item(ItemId.CHOCOLATE_BAR.id(), 1),
		new Item(ItemId.CHEESE.id(), 3), new Item(ItemId.TOMATO.id(), 3), new Item(ItemId.POTATO.id(), 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.WYDIN.id();
	}

	@Override
	public boolean blockWallObjectAction(final GameObject obj,
										 final Integer click, final Player player) {
		return obj.getID() == 47 && obj.getX() == 277 && obj.getY() == 658;
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
		npcTalk(p, n, "Welcome to my foodstore",
			"Would you like to buy anything");

		int option = showMenu(p, n, false, //do not send over
				"yes please", "No thankyou", "what can you recommend?");
		switch (option) {
			case 0:
				playerTalk(p, n, "Yes please");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
				break;
			case 2:
				playerTalk(p, n, "What can you recommend?");
				npcTalk(p, n, "We have this really exotic fruit",
					"All the way from Karamja", "It's called a banana");
				break;
		}

	}

	@Override
	public void onWallObjectAction(final GameObject obj, final Integer click,
								   final Player p) {
		if (obj.getID() == 47 && obj.getX() == 277 && obj.getY() == 658) {
			final Npc n = World.getWorld().getNpcById(NpcId.WYDIN.id());

			if (n != null && !p.getCache().hasKey("job_wydin")) {
				n.face(p);
				p.face(n);
				npcTalk(p, n, "Heh you can't go in there",
					"Only employees of the grocery store can go in");

				int option = showMenu(p, n, false, //do not send over
					"Well can I get a job here?", "Sorry I didn't realise");
				if (option == 0) {
					playerTalk(p, n, "Can I get a job here?");
					npcTalk(p, n, "Well you're keen I'll give you that",
						"Ok I'll give you a go",
						"Have you got your own apron?");
					if (p.getInventory().wielding(ItemId.WHITE_APRON.id())) {
						playerTalk(p, n, "Yes I have one right here");
						npcTalk(p, n,
							"Wow you are well prepared, you're hired",
							"Go through to the back and tidy up for me please");
						p.getCache().store("job_wydin", true);
					} else {
						playerTalk(p, n, "No");
						npcTalk(p, n,
							"Well you can't work here unless you have an apron",
							"Health and safety regulations, you understand");
					}
				} else if (option == 1) {
					playerTalk(p, n, "Sorry I didn't realise");
				}
			} else {
				if (!p.getInventory().wielding(ItemId.WHITE_APRON.id())) {
					npcTalk(p, n, "Can you put your apron on before going in there please");
				} else {
					if (p.getX() < 277) {
						doDoor(obj, p);
						p.teleport(277, 658, false);
					} else {
						doDoor(obj, p);
						p.teleport(276, 658, false);
					}
				}
			}
		}
	}

}
