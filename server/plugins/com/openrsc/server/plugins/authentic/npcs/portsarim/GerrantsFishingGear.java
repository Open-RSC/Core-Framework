package com.openrsc.server.plugins.authentic.npcs.portsarim;

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

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class GerrantsFishingGear extends AbstractShop {

	private final Shop shop = new Shop(false, 12000, 100, 70, 3, new Item(ItemId.NET.id(),
		5), new Item(ItemId.FISHING_ROD.id(), 5), new Item(ItemId.FLY_FISHING_ROD.id(), 5), new Item(ItemId.HARPOON.id(), 2),
		new Item(ItemId.LOBSTER_POT.id(), 2), new Item(ItemId.FISHING_BAIT.id(), 200), new Item(ItemId.FEATHER.id(), 200),
		new Item(ItemId.RAW_SHRIMP.id(), 0), new Item(ItemId.RAW_SARDINE.id(), 0), new Item(ItemId.RAW_HERRING.id(), 0),
		new Item(ItemId.RAW_ANCHOVIES.id(), 0), new Item(ItemId.RAW_TROUT.id(), 0), new Item(ItemId.RAW_PIKE.id(), 0),
		new Item(ItemId.RAW_SALMON.id(), 0), new Item(ItemId.RAW_TUNA.id(), 0), new Item(ItemId.RAW_LOBSTER.id(), 0),
		new Item(ItemId.RAW_SWORDFISH.id(), 0));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.GERRANT.id();
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
		npcsay(player, n, "Welcome you can buy any fishing equipment at my store",
			"We'll also buy anything you catch off you");

		String[] options;
		if (player.getQuestStage(Quests.HEROS_QUEST) >= 1
			&& player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			options = new String[]{"Let's see what you've got then",
				"Sorry, I'm not interested",
				"I want to find out how to catch a lava eel"};
		} else {
			options = new String[]{"Let's see what you've got then",
				"Sorry, I'm not interested"};
		}
		int option = multi(player, n, false, options);
		if (option == 0) {
			say(player, n, "Let's see what you've got then");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (option == 1) {
			say(player, n, "Sorry,I'm not interested");
		} else if (option == 2) {
			say(player, n, "I want to find out how to catch a lava eel");
			npcsay(player,
				n,
				"Lava eels eh?",
				"That's a tricky one that is",
				"I wouldn't even know where find them myself",
				"Probably in some lava somewhere",
				"You'll also need a lava proof fishing line",
				"The method for this would be take an ordinary fishing rod",
				"And cover it with fire proof blamish oil");
			// check no Blaimish snail slime, oil and rod to re-issue
			if (!player.getCarriedItems().hasCatalogID(ItemId.BLAMISH_SNAIL_SLIME.id(), Optional.empty())
				&& !player.getCarriedItems().hasCatalogID(ItemId.BLAMISH_OIL.id(), Optional.empty())
				&& !player.getCarriedItems().hasCatalogID(ItemId.OILY_FISHING_ROD.id(), Optional.empty())) {
				npcsay(player, n, "Now I may have a jar of Blamish snail slime",
					"I wonder where I put it");
				player.message("Gerrant searches about a bit");
				npcsay(player, n, "Aha here it is");
				player.message("Gerrant passes you a small jar");
				give(player, ItemId.BLAMISH_SNAIL_SLIME.id(), 1);
				npcsay(player, n,
					"You'll need to mix this with some of the Harralander herb and water");
			}
		}
	}
}
