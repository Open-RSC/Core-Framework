package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;

import static com.openrsc.server.plugins.Functions.*;

public final class WydinsGrocery extends AbstractShop implements OpBoundTrigger {

	private final Shop shop = new Shop(false, 12500, 100, 70, 1, new Item(ItemId.POT_OF_FLOUR.id(),
		3), new Item(ItemId.RAW_CHICKEN.id(), 1), new Item(ItemId.CABBAGE.id(), 3), new Item(ItemId.BANANA.id(), 3),
		new Item(ItemId.REDBERRIES.id(), 1), new Item(ItemId.BREAD.id(), 0), new Item(ItemId.CHOCOLATE_BAR.id(), 1),
		new Item(ItemId.CHEESE.id(), 3), new Item(ItemId.TOMATO.id(), 3), new Item(ItemId.POTATO.id(), 1));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.WYDIN.id();
	}

	@Override
	public boolean blockOpBound(final GameObject obj,
								final Integer click, final Player player) {
		return obj.getID() == 47 && obj.getX() == 277 && obj.getY() == 658;
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public Shop getShop() {
		return shop;
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "Welcome to my foodstore",
			"Would you like to buy anything");

		int option = multi(player, n, false, //do not send over
				"yes please", "No thankyou", "what can you recommend?");
		switch (option) {
			case 0:
				say(player, n, "Yes please");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;
			case 2:
				say(player, n, "What can you recommend?");
				npcsay(player, n, "We have this really exotic fruit",
					"All the way from Karamja", "It's called a banana");
				break;
		}

	}

	@Override
	public void onOpBound(final GameObject obj, final Integer click,
						  final Player player) {
		if (obj.getID() == 47 && obj.getX() == 277 && obj.getY() == 658) {
			final Npc n = player.getWorld().getNpcById(NpcId.WYDIN.id());

			if (n != null && !player.getCache().hasKey("job_wydin")) {
				n.face(player);
				player.face(n);
				npcsay(player, n, "Heh you can't go in there",
					"Only employees of the grocery store can go in");

				int option = multi(player, n, false, //do not send over
					"Well can I get a job here?", "Sorry I didn't realise");
				if (option == 0) {
					say(player, n, "Can I get a job here?");
					npcsay(player, n, "Well you're keen I'll give you that",
						"Ok I'll give you a go",
						"Have you got your own apron?");
					if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.WHITE_APRON.id())) {
						say(player, n, "Yes I have one right here");
						npcsay(player, n,
							"Wow you are well prepared, you're hired",
							"Go through to the back and tidy up for me please");
						player.getCache().store("job_wydin", true);
					} else {
						say(player, n, "No");
						npcsay(player, n,
							"Well you can't work here unless you have an apron",
							"Health and safety regulations, you understand");
					}
				} else if (option == 1) {
					say(player, n, "Sorry I didn't realise");
				}
			} else {
				if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.WHITE_APRON.id())) {
					npcsay(player, n, "Can you put your apron on before going in there please");
				} else {
					if (player.getX() < 277) {
						doDoor(obj, player);
						player.teleport(277, 658, false);
					} else {
						doDoor(obj, player);
						player.teleport(276, 658, false);
					}
				}
			}
		}
	}
}
