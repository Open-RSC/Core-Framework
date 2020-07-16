package com.openrsc.server.plugins.authentic.npcs.wilderness.banditcamp;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;

import static com.openrsc.server.plugins.Functions.*;

public class FatTony extends AbstractShop {

	private final Shop shop = new Shop(false, 5000, 100, 60, 2, new Item(ItemId.PIZZA_BASE.id(), 30));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.FAT_TONY.id();
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
		npcsay(player, n, "Go away I'm very busy");
		final int option = multi(player, n,
			"Sorry to disturb you", "What are you busy doing?",
			"Have you anything to sell?");
		if (option == 1) {
			npcsay(player, n, "I'm cooking pizzas for the people in this camp",
				"Not that these louts appreciate my gourmet cooking");
			final int sub_option = multi(player, n,
				"So what is a gourmet chef doing cooking for bandits?",
				"Can I have some pizza too?", "Okay, I'll leave you to it");
			if (sub_option == 0) {
				npcsay(player,
					n,
					"Well I'm an outlaw",
					"I was accused of giving the king food poisoning",
					"The thought of it - I think he just drank to much wine that night",
					"I had to flee the kingdom of Misthalin",
					"The bandits give me refuge here as long as I cook for them");
				final int remaining_option = multi(player, n,
					 "Can I have some pizza too?",
						"Okay, I'll leave you to it");
				if (remaining_option == 0) {
					wantsPizza(n, player);
				} else if (remaining_option == 1) {
					//nothing
				}
			} else if (sub_option == 1) {
				wantsPizza(n, player);
			}
		} else if (option == 2) {
			npcsay(player, n, "Well I guess I can sell you some half made pizzas");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}
	}

	private void wantsPizza(final Npc n, final Player player) {
		npcsay(player, n, "Well this pizza is really meant to be for the bandits",
				"I guess I could sell you some pizza bases though");
		final int next_option = multi(player, n, "Yes, Okay",
			"Oh if I have to pay I don't want any");
		if (next_option == 0) {
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}
	}
}
