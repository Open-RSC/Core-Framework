package com.openrsc.server.plugins.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

public final class Gardener implements ShopInterface,
	TalkNpcTrigger {

	private Shop shop = null;

	@Override
	public void onTalkNpc(Player p, final Npc n) {

		npcTalk(p, n, "Can I help you at all?");

		int option = showMenu(p, n, "Yes please. What are you selling?",
			"No thanks", "Do you have any tips on getting produce?");
		switch (option) {
			case 0:
				npcTalk(p, n, "Take a look");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
				break;
			case 2:
				npcTalk(p, n, "Certainly, is there anything in particular",
					"you might be wondering about?");
				int sub_option = showMenu(p, n, "Sometimes I damage the produce",
					"How can I take care for a specific harvesting spot?",
					"I don't seem to improve my harvesting skills on certain areas");
				if (sub_option == 0) {
					npcTalk(p, n, "You can get yield from fruit trees and allotments by hand",
						"but you will get better results if you use a tool",
						"such as fruit pickers or hand shovels");
				} else if (sub_option == 1) {
					npcTalk(p, n, "While collecting you may weaken the spot",
						"in such case you will know whether to soil or water it",
						"and in doing so you may end up with extra produce");
				} else if (sub_option == 2) {
					npcTalk(p, n, "Some areas have magical soil and the allotment",
						"never depletes",
						"Others are drops mysterious forces have",
						"left behind in synchronized harmony",
						"and you simply pick up instead of harvest");
				}
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
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

	public Shop getShop(World world) {
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

}
