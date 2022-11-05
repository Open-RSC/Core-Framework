package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public final class Gardener extends AbstractShop {

	Shop shop = new Shop(false, 3000, 130, 40, 3,
		new Item(ItemId.FRUIT_PICKER.id(), 10), new Item(ItemId.HAND_SHOVEL.id(), 5),
		new Item(ItemId.HERB_CLIPPERS.id(), 3), new Item(ItemId.WATERING_CAN.id(), 2),
		new Item(ItemId.SOIL.id(), 1));

	@Override
	public void onTalkNpc(Player player, final Npc n) {

		npcsay(player, n, "Can I help you at all?");

		int option = multi(player, n, "Yes please. What are you selling?",
			"No thanks", "Do you have any tips on getting produce?");
		switch (option) {
			case 0:
				npcsay(player, n, "Take a look");
				player.setAccessingShop(getShop(player.getWorld()));
				ActionSender.showShop(player, getShop(player.getWorld()));
				break;
			case 2:
				npcsay(player, n, "Certainly, is there anything in particular",
					"you might be wondering about?");
				int sub_option = multi(player, n, "Sometimes I damage the produce",
					"How can I take care for a specific harvesting spot?",
					"I don't seem to improve my harvesting skills on certain areas");
				if (sub_option == 0) {
					npcsay(player, n, "You can get yield from fruit trees and allotments by hand",
						"but you will get better results if you use a tool",
						"such as fruit pickers or hand shovels");
				} else if (sub_option == 1) {
					npcsay(player, n, "While collecting you may weaken the spot",
						"in such case you will know whether to soil or water it",
						"and in doing so you may end up with extra produce");
				} else if (sub_option == 2) {
					npcsay(player, n, "Some areas have magical soil and the allotment",
						"never depletes",
						"Others are drops mysterious forces have",
						"left behind in synchronized harmony",
						"and you simply pick up instead of harvest");
				}
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.GARDENER.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{getShop(world)};
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public Shop getShop() {
		return shop;
	}

	private Shop getShop(World world) {
		if(shop == null) {
			shop = (world.getServer().getConfig().MEMBER_WORLD) ?
				new Shop(false, 3000, 130, 40, 3,
					new Item(ItemId.FRUIT_PICKER.id(), 10), new Item(ItemId.HAND_SHOVEL.id(), 5),
					new Item(ItemId.HERB_CLIPPERS.id(), 3), new Item(ItemId.WATERING_CAN.id(), 2),
					new Item(ItemId.SOIL.id(), 1)) :
				new Shop(false, 3000, 130, 40, 3,
					new Item(ItemId.FRUIT_PICKER.id(), 10), new Item(ItemId.HAND_SHOVEL.id(), 5),
					new Item(ItemId.WATERING_CAN.id(), 2));
		}

		return shop;
	}

	@Override
	public void onOpNpc(Player player, Npc n, String command) {
		Npc gardener = player.getWorld().getNpc(n.getID(),
			player.getX() - 2, player.getX() + 2,
			player.getY() - 2, player.getY() + 2);
		if (gardener == null) return;
		if (command.equalsIgnoreCase("Trade") && config().RIGHT_CLICK_TRADE) {
			if (!player.getQolOptOut()) {
				Shop shop = getShop(player.getWorld());
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			} else {
				player.playerServerMessage(MessageType.QUEST, "Right click trading is a QoL feature which you are opted out of.");
				player.playerServerMessage(MessageType.QUEST, "Consider using an original RSC client so that you don't see the option.");
			}
		}
	}

	@Override
	public boolean blockOpNpc(Player player, Npc n, String command) {
		return n.getID() == NpcId.GARDENER.id() && command.equalsIgnoreCase("Trade");
	}
}
