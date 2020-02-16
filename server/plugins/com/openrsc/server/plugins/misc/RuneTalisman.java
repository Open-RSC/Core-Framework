package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;

public class RuneTalisman implements InvActionListener, InvActionExecutiveListener {

	@Override
	public void onInvAction(Item item, Player player, String command) {
		switch(ItemId.getById(item.getCatalogId())) {
			case AIR_TALISMAN:
			case MIND_TALISMAN:
			case WATER_TALISMAN:
			case EARTH_TALISMAN:
			case FIRE_TALISMAN:
			case BODY_TALISMAN:
			case COSMIC_TALISMAN:
			case CHAOS_TALISMAN:
			case NATURE_TALISMAN:
			case LAW_TALISMAN:
			case DEATH_TALISMAN:
			case BLOOD_TALISMAN:
				if (command.equalsIgnoreCase("locate")) {
					if (player.getQuestStage(Quests.RUNE_MYSTERIES) != -1) {
						player.message("You can't understand what the talisman is trying to tell you.");
						return;
					}
					String northORsouth = "", eastORwest = "";
					int playerX, playerY, altarX, altarY;
					playerX = player.getX();
					playerY = player.getY();

					switch (ItemId.getById(item.getCatalogId())) {
						case AIR_TALISMAN:
							altarX = 306;
							altarY = 593;
							break;
						case MIND_TALISMAN:
							altarX = 297;
							altarY = 438;
							break;
						case WATER_TALISMAN:
							altarX = 447;
							altarY = 684;
							break;
						case EARTH_TALISMAN:
							altarX = 62;
							altarY = 464;
							break;
						case FIRE_TALISMAN:
							altarX = 50;
							altarY = 633;
							break;
						case BODY_TALISMAN:
							altarX = 259;
							altarY = 503;
							break;
						case COSMIC_TALISMAN:
							altarX = 106;
							altarY = 3565;
							break;
						case CHAOS_TALISMAN:
							altarX = 232;
							altarY = 375;
							break;
						case NATURE_TALISMAN:
							altarX = 392;
							altarY = 804;
							break;
						case LAW_TALISMAN:
							altarX = 409;
							altarY = 534;
							break;
						case DEATH_TALISMAN:
							altarX = 0;
							altarY = 0;
							break;
						case BLOOD_TALISMAN:
							altarX = 0;
							altarY = 0;
							break;
						default:
							altarX = 0;
							altarY = 0;
							break;
					}
					int diffX = altarX - playerX;
					int diffY = altarY - playerY;

					if (diffX != 0)
						eastORwest = diffX > 0 ? "west" : "east";

					if (diffY != 0)
						northORsouth = diffY > 0 ? "south" : "north";

					player.message("The talisman pulls towards the " + northORsouth + eastORwest + ".");
				}
				break;
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player player, String command) {
		return item.getCatalogId() == ItemId.AIR_TALISMAN.id() || item.getCatalogId() == ItemId.MIND_TALISMAN.id() ||
			item.getCatalogId() == ItemId.WATER_TALISMAN.id() || item.getCatalogId() == ItemId.EARTH_TALISMAN.id() ||
			item.getCatalogId() == ItemId.FIRE_TALISMAN.id() || item.getCatalogId() == ItemId.BODY_TALISMAN.id() ||
			item.getCatalogId() == ItemId.COSMIC_TALISMAN.id() || item.getCatalogId() == ItemId.CHAOS_TALISMAN.id() ||
			item.getCatalogId() == ItemId.NATURE_TALISMAN.id() || item.getCatalogId() == ItemId.LAW_TALISMAN.id() ||
			item.getCatalogId() == ItemId.DEATH_TALISMAN.id() || item.getCatalogId() == ItemId.BLOOD_TALISMAN.id();
	}
}
