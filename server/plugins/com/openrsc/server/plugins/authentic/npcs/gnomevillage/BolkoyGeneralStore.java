package com.openrsc.server.plugins.authentic.npcs.gnomevillage;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.Functions.say;

public final class BolkoyGeneralStore extends AbstractShop {

	private final Shop shop = new Shop(true, 15000, 130, 40, 3, new Item(ItemId.BRONZE_PICKAXE.id(),
		5), new Item(ItemId.POT.id(), 3), new Item(ItemId.JUG.id(), 2), new Item(ItemId.SHEARS.id(), 2), new Item(ItemId.BUCKET.id(),
		2), new Item(ItemId.TINDERBOX.id(), 2), new Item(ItemId.CHISEL.id(), 2), new Item(ItemId.HAMMER.id(), 5),
		new Item(ItemId.BRONZE_ARROWS.id(), 30), new Item(ItemId.COOKEDMEAT.id(), 2));

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		switch (player.getQuestStage(Quests.TREE_GNOME_VILLAGE)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				say(player, n, "hello there");
				npcsay(player, n, "hello stranger, are you",
					"new to these parts?",
					"i'm bolkoy by the way",
					"i'm the village shop keeper",
					"would you like to buy something?");
				dialogueShop(player, n);
				break;
			case 5:
				say(player, n, "hello");
				npcsay(player, n, "hello traveller",
					"amazing, you recovered the orb",
					"well i am impressed",
					"would you like to buy something?");
				dialogueShop(player, n);
				break;
			case 6:
				if (!player.getCache().hasKey("looted_orbs_protect")) {
					say(player, n, "hi");
					npcsay(player, n, "oh, hello there",
						"have you heard? they took",
						"the other orbs, it's terrible",
						"i suppose the show must go on",
						"would you like to buy something?");
				} else {
					say(player, n, "hello");
					npcsay(player, n, "hello there",
						"you're that hero who saved the orbs",
						"soon we will perform the ritual",
						"and the village will be safe again",
						"anyway, would you like anything from my shop?");
				}
				dialogueShop(player, n);
				break;
			case -1:
				say(player, n, "welcome, welcome");
				npcsay(player, n, "it's good to see you again",
					"the village is much safer now",
					"by the way i'm the village shop keeper",
					"would you like to buy something?");
				dialogueShop(player, n);
				break;
		}
	}

	private void dialogueShop(Player player, Npc bolkoy) {
		int options = multi(player, bolkoy, false, //do not send over
			"What have you got?", "No thankyou");
		if (options == 0) {
			say(player, bolkoy, "what have you got?");
			npcsay(player, bolkoy, "take a look");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}
		else if (options == 1) {
			say(player, bolkoy, "no thankyou");
			npcsay(player, bolkoy, "ok maybe later");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BOLKOY.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public Shop getShop() {
		return shop;
	}

}
