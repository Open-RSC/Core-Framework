package com.openrsc.server.plugins.authentic.npcs.brimhaven;

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
import static com.openrsc.server.plugins.authentic.quests.free.ShieldOfArrav.isBlackArmGang;

public class AlfonseTheWaiter extends AbstractShop {

	private final Shop shop = new Shop(false, 10000, 110, 75, 2,
		new Item(ItemId.HERRING.id(), 5), new Item(ItemId.COD.id(), 5),
		new Item(ItemId.TUNA.id(), 5), new Item(ItemId.LOBSTER.id(), 3), new Item(ItemId.SWORDFISH.id(), 2));

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.ALFONSE_THE_WAITER.id()) {
			npcsay(player, n, "Welcome to the shrimp and parrot",
				"Would you like to order sir?");
			int menu;
			if (isBlackArmGang(player) || (player.getQuestStage(Quests.HEROS_QUEST) != 1 && player.getQuestStage(Quests.HEROS_QUEST) != 2 && !player.getCache().hasKey("pheonix_mission") && !player.getCache().hasKey("pheonix_alf"))) {
				menu = multi(player, n,
					"Yes please",
					"No thankyou");
			} else {
				menu = multi(player, n,
					"Yes please",
					"No thankyou",
					"Do you sell Gherkins?");
			}
			if (menu == 0) {
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			} else if (menu == 2) {
				npcsay(player, n, "Hmm ask Charlie the cook round the back",
					"He may have some Gherkins for you");
				mes("Alfonse winks");
				delay(3);
				player.getCache().store("talked_alf", true);
				player.getCache().remove("pheonix_alf");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.ALFONSE_THE_WAITER.id();
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
