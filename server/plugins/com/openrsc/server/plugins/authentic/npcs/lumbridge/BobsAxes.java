package com.openrsc.server.plugins.authentic.npcs.lumbridge;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;
import com.openrsc.server.plugins.Functions;

import java.util.ArrayList;

import static com.openrsc.server.plugins.RuneScript.*;

public final class BobsAxes extends AbstractShop {

	private final Shop shop = new Shop(false, 15000, 100, 60, 2, new Item(ItemId.BRONZE_PICKAXE.id(),
		5), new Item(ItemId.BRONZE_AXE.id(), 10), new Item(ItemId.IRON_AXE.id(), 5), new Item(ItemId.STEEL_AXE.id(), 3),
		new Item(ItemId.IRON_BATTLE_AXE.id(), 5), new Item(ItemId.STEEL_BATTLE_AXE.id(), 2), new Item(ItemId.MITHRIL_BATTLE_AXE.id(), 1));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.BOB.id();
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
	public void onTalkNpc(final Player player, final Npc npc) {
		npcsay("Hello. How can I help you?");

		ArrayList<String> options = new ArrayList<String>();
		options.add("Give me a quest!");
		options.add("Have you anything to sell?");

		if (Functions.config().WANT_CUSTOM_SPRITES) {
			options.add("Give me a cape like yours!");
		}

		int option = multi(options.toArray(new String[0]));
		switch (option) {
			case 0:
				npcsay("Get yer own!");
				break;
			case 1:
				npcsay("Yes, I buy and sell axes, take your pick! (or axe)");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;
			case 2:
				if (!Functions.config().WANT_CUSTOM_SPRITES) break;
				npcsay("Get yer own!");
				if (player.getSkills().getMaxStat(Skill.WOODCUTTING.id()) >= 99) {
					say("I'm trying to!");
					npcsay("Oh",
						"Well it'll cost ya 99,000 coins",
						"Nothing is free after all");
					if (multi("Alright", "No way") == 0) {
						mes("Bob holds out his hand for the coins");
						delay(3);
						if (ifheld(ItemId.COINS.id(), 99000)) {
							remove(ItemId.COINS.id(), 99000);
							mes("You hand Bob the coins");
							delay(3);
							mes("Bob hands you a woodcutting cape");
							give(ItemId.WOODCUTTING_CAPE.id(), 1);
							delay(3);
							npcsay("Oh yeah",
								"I guess I should tell ya what it does",
								"This cape'll help you woodcut better",
								"You'll be able to get more logs from trees");
						} else {
							npcsay("Well?");
							say("Uh, it looks like I don't actually have the money");
							npcsay("What are ya tryin' to do, con me?",
								"Come back when you've got the coins");
						}
					}
				}
				break;
		}
	}
}
