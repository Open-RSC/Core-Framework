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

public final class WydinsGrocery implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener,
		WallObjectActionExecutiveListener, WallObjectActionListener {

	private final Shop shop = new Shop(false, 12500, 100, 70, 1, new Item(136,
			3), new Item(133, 1), new Item(18, 3), new Item(249, 3),
			new Item(236, 1), new Item(138, 0), new Item(337, 1),
			new Item(319, 3), new Item(320, 3), new Item(348, 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == 129;
	}

	@Override
	public boolean blockWallObjectAction(final GameObject obj,
			final Integer click, final Player player) {
		return obj.getID() == 47 && obj.getX() == 277 && obj.getY() == 658;
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
		npcTalk(p, n, "welcome to my foodstore",
				"would you like to buy anything");

		final String[] options = new String[] { "yes please", "No thankyou",
				"what can you recommend?" };
		int option = showMenu(p, n, options);
		switch (option) {
		case 0:
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
			break;
		case 2:
			npcTalk(p, n, "we have this really exotic fruit",
					"all the way from Karamja", "it's called a banana");
			break;
		}

	}

	@Override
	public void onWallObjectAction(final GameObject obj, final Integer click,
			final Player p) {
		if (obj.getID() == 47 && obj.getX() == 277 && obj.getY() == 658) {
			final Npc n = World.getWorld().getNpcById(129);

			if (n != null && !p.getCache().hasKey("job_wydin")) {
				n.face(p);
				p.face(n);
				npcTalk(p, n, "heh you can't go in there",
						"only employees of the grocery store can go in");

				final String[] options = new String[] {
						"Well can I get a job here?", "Sorry I didn't realise" };
				int option = showMenu(p, n, options);
				if (option == 0) {
					npcTalk(p, n, "Well you're keen I'll give you that",
							"Ok I'll give you a go",
							"Have you got your own apron?");
					if (p.getInventory().wielding(182)) {
						playerTalk(p, n, "Yes I have one right here");
						npcTalk(p, n,
								"Wow you are well prepared, you're hired",
								"Go through to the back and tidy up for me please");
						p.getCache().store("job_wydin", true);
					} else {
						npcTalk(p, n,
								"well you can't work here unless you have an apron",
								"health and safety regulations, you understand");
					}
				}
			} else {
				if (!p.getInventory().wielding(182)) {
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
