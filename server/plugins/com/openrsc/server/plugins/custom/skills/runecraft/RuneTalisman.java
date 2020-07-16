package com.openrsc.server.plugins.custom.skills.runecraft;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;

public class RuneTalisman implements OpInvTrigger {

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		switch(ItemId.getById(item.getCatalogId())) {
			case AIR_TALISMAN:
			case CURSED_AIR_TALISMAN:
			case ENFEEBLED_AIR_TALISMAN:
			case MIND_TALISMAN:
			case CURSED_MIND_TALISMAN:
			case ENFEEBLED_MIND_TALISMAN:
			case WATER_TALISMAN:
			case CURSED_WATER_TALISMAN:
			case ENFEEBLED_WATER_TALISMAN:
			case EARTH_TALISMAN:
			case CURSED_EARTH_TALISMAN:
			case ENFEEBLED_EARTH_TALISMAN:
			case FIRE_TALISMAN:
			case CURSED_FIRE_TALISMAN:
			case ENFEEBLED_FIRE_TALISMAN:
			case BODY_TALISMAN:
			case CURSED_BODY_TALISMAN:
			case ENFEEBLED_BODY_TALISMAN:
			case COSMIC_TALISMAN:
			case CURSED_COSMIC_TALISMAN:
			case ENFEEBLED_COSMIC_TALISMAN:
			case CHAOS_TALISMAN:
			case CURSED_CHAOS_TALISMAN:
			case ENFEEBLED_CHAOS_TALISMAN:
			case NATURE_TALISMAN:
			case CURSED_NATURE_TALISMAN:
			case ENFEEBLED_NATURE_TALISMAN:
			case LAW_TALISMAN:
			case CURSED_LAW_TALISMAN:
			case ENFEEBLED_LAW_TALISMAN:
			case DEATH_TALISMAN:
			case CURSED_DEATH_TALISMAN:
			case ENFEEBLED_DEATH_TALISMAN:
			case BLOOD_TALISMAN:
			case CURSED_BLOOD_TALISMAN:
			case ENFEEBLED_BLOOD_TALISMAN:
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
						case CURSED_AIR_TALISMAN:
						case ENFEEBLED_AIR_TALISMAN:
							altarX = 306;
							altarY = 593;
							break;
						case MIND_TALISMAN:
						case CURSED_MIND_TALISMAN:
						case ENFEEBLED_MIND_TALISMAN:
							altarX = 297;
							altarY = 438;
							break;
						case WATER_TALISMAN:
						case CURSED_WATER_TALISMAN:
						case ENFEEBLED_WATER_TALISMAN:
							altarX = 447;
							altarY = 684;
							break;
						case EARTH_TALISMAN:
						case CURSED_EARTH_TALISMAN:
						case ENFEEBLED_EARTH_TALISMAN:
							altarX = 62;
							altarY = 464;
							break;
						case FIRE_TALISMAN:
						case CURSED_FIRE_TALISMAN:
						case ENFEEBLED_FIRE_TALISMAN:
							altarX = 50;
							altarY = 633;
							break;
						case BODY_TALISMAN:
						case CURSED_BODY_TALISMAN:
						case ENFEEBLED_BODY_TALISMAN:
							altarX = 259;
							altarY = 503;
							break;
						case COSMIC_TALISMAN:
						case CURSED_COSMIC_TALISMAN:
						case ENFEEBLED_COSMIC_TALISMAN:
							altarX = 106;
							altarY = 3565;
							break;
						case CHAOS_TALISMAN:
						case CURSED_CHAOS_TALISMAN:
						case ENFEEBLED_CHAOS_TALISMAN:
							altarX = 232;
							altarY = 375;
							break;
						case NATURE_TALISMAN:
						case CURSED_NATURE_TALISMAN:
						case ENFEEBLED_NATURE_TALISMAN:
							altarX = 392;
							altarY = 804;
							break;
						case LAW_TALISMAN:
						case CURSED_LAW_TALISMAN:
						case ENFEEBLED_LAW_TALISMAN:
							altarX = 409;
							altarY = 534;
							break;
						case DEATH_TALISMAN:
						case CURSED_DEATH_TALISMAN:
						case ENFEEBLED_DEATH_TALISMAN:
							altarX = 0;
							altarY = 0;
							break;
						case BLOOD_TALISMAN:
						case CURSED_BLOOD_TALISMAN:
						case ENFEEBLED_BLOOD_TALISMAN:
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
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.AIR_TALISMAN.id()
			|| item.getCatalogId() == ItemId.CURSED_AIR_TALISMAN.id()
			|| item.getCatalogId() == ItemId.ENFEEBLED_AIR_TALISMAN.id()
			|| item.getCatalogId() == ItemId.MIND_TALISMAN.id()
			|| item.getCatalogId() == ItemId.CURSED_MIND_TALISMAN.id()
			|| item.getCatalogId() == ItemId.ENFEEBLED_MIND_TALISMAN.id()
			|| item.getCatalogId() == ItemId.WATER_TALISMAN.id()
			|| item.getCatalogId() == ItemId.CURSED_WATER_TALISMAN.id()
			|| item.getCatalogId() == ItemId.ENFEEBLED_WATER_TALISMAN.id()
			|| item.getCatalogId() == ItemId.EARTH_TALISMAN.id()
			|| item.getCatalogId() == ItemId.CURSED_EARTH_TALISMAN.id()
			|| item.getCatalogId() == ItemId.ENFEEBLED_EARTH_TALISMAN.id()
			|| item.getCatalogId() == ItemId.FIRE_TALISMAN.id()
			|| item.getCatalogId() == ItemId.CURSED_FIRE_TALISMAN.id()
			|| item.getCatalogId() == ItemId.ENFEEBLED_FIRE_TALISMAN.id()
			|| item.getCatalogId() == ItemId.BODY_TALISMAN.id()
			|| item.getCatalogId() == ItemId.CURSED_BODY_TALISMAN.id()
			|| item.getCatalogId() == ItemId.ENFEEBLED_BODY_TALISMAN.id()
			|| item.getCatalogId() == ItemId.COSMIC_TALISMAN.id()
			|| item.getCatalogId() == ItemId.CURSED_COSMIC_TALISMAN.id()
			|| item.getCatalogId() == ItemId.ENFEEBLED_COSMIC_TALISMAN.id()
			|| item.getCatalogId() == ItemId.CHAOS_TALISMAN.id()
			|| item.getCatalogId() == ItemId.CURSED_CHAOS_TALISMAN.id()
			|| item.getCatalogId() == ItemId.ENFEEBLED_CHAOS_TALISMAN.id()
			|| item.getCatalogId() == ItemId.NATURE_TALISMAN.id()
			|| item.getCatalogId() == ItemId.CURSED_NATURE_TALISMAN.id()
			|| item.getCatalogId() == ItemId.ENFEEBLED_NATURE_TALISMAN.id()
			|| item.getCatalogId() == ItemId.LAW_TALISMAN.id()
			|| item.getCatalogId() == ItemId.CURSED_LAW_TALISMAN.id()
			|| item.getCatalogId() == ItemId.ENFEEBLED_LAW_TALISMAN.id()
			|| item.getCatalogId() == ItemId.DEATH_TALISMAN.id()
			|| item.getCatalogId() == ItemId.CURSED_DEATH_TALISMAN.id()
			|| item.getCatalogId() == ItemId.ENFEEBLED_DEATH_TALISMAN.id()
			|| item.getCatalogId() == ItemId.BLOOD_TALISMAN.id()
			|| item.getCatalogId() == ItemId.CURSED_BLOOD_TALISMAN.id()
			|| item.getCatalogId() == ItemId.ENFEEBLED_BLOOD_TALISMAN.id();
	}
}
