package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.CarriedItems;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DragonstoneAmulet implements OpInvTrigger, UseLocTrigger {
	private static final int FOUNTAIN_OF_HEROES = 282;

	@Override
	public boolean blockOpInv(final Player player, final Integer invIndex, final Item item, final String command) {
		return item.getCatalogId() == ItemId.CHARGED_DRAGONSTONE_AMULET.id();
	}

	@Override
	public void onOpInv(final Player player, final Integer invIndex, final Item item, final String command) {
		if (item.getCatalogId() != ItemId.CHARGED_DRAGONSTONE_AMULET.id()) return;

		int chargesRemaining = -1;

		player.message("You rub the amulet");
		delay();
		if (config().IMPROVED_ITEM_OBJECT_NAMES) {
			if (!player.getCache().hasKey("charged_ds_amulet")) {
				chargesRemaining = 4;
			} else {
				chargesRemaining = 4 - player.getCache().getInt("charged_ds_amulet");
			}
			mes("Your amulet has " + chargesRemaining + (chargesRemaining == 1 ? " charge" : " charges") + " remaining.");
			delay();
		}
		player.message("Where would you like to teleport to?");

		final String[] menuOptions = new String[]{"Edgeville", "Karamja", "Draynor village", "Al Kharid", "Nowhere"};
		final int menuOption = multi(player, menuOptions); // Show menu and wait for selection
		if (menuOption < 0 || menuOption >= menuOptions.length) return;

		if (player.getLocation().wildernessLevel() >= Constants.GLORY_TELEPORT_LIMIT || player.getLocation().isInFisherKingRealm()
			|| player.getLocation().isInsideGrandTreeGround()
			|| (player.getLocation().inModRoom() && !player.isAdmin())) {
			player.message("A mysterious force blocks your teleport!");
			player.message("You can't use this teleport after level 30 wilderness");
			return;
		}

		if (player.getCarriedItems().getInventory().countId(ItemId.ANA_IN_A_BARREL.id()) > 0) {
			mes("You can't teleport while holding Ana,");
			delay(3);
			mes("It's just too difficult to concentrate.");
			delay(3);
			return;
		}

		if (menuOption == 4) { // Nowhere
			player.message("Nothing interesting happens");
			return;
		}

		final CarriedItems carriedItems = player.getCarriedItems();

		if (player.getLocation().inKaramja() && carriedItems.hasCatalogID(ItemId.KARAMJA_RUM.id())) {
			final Item karamjaRum = new Item(ItemId.KARAMJA_RUM.id());
			do {
				if (carriedItems.remove(karamjaRum) == -1) break;
			} while (carriedItems.hasCatalogID(ItemId.KARAMJA_RUM.id()));
		}

		if (carriedItems.hasCatalogID(ItemId.PLAGUE_SAMPLE.id())) {
			player.message("the plague sample is too delicate...");
			player.message("it disintegrates in the crossing");
			final Item plagueSample = new Item(ItemId.PLAGUE_SAMPLE.id());
			do {
				if (carriedItems.remove(plagueSample) == -1) break;
			} while (carriedItems.hasCatalogID(ItemId.PLAGUE_SAMPLE.id()));
		}

		switch (menuOption) {
			case 0: // Edgeville
				player.teleport(226, 447, true);
				break;
			case 1: // Karamja
				player.teleport(360, 696, true);
				break;
			case 2: // Draynor Village
				player.teleport(214, 632, true);
				break;
			case 3: // Al Kharid
				player.teleport(72, 696, true);
				break;
			default:
				assert false : "DragonstoneAmulet menu option out of range: " + menuOption;
		}

		if (config().IMPROVED_ITEM_OBJECT_NAMES) {
			chargesRemaining--;
			mes("You rub your amulet and teleport to " + menuOptions[menuOption]);
			delay();
			if (chargesRemaining > 1) {
				mes("Your amulet now has " + chargesRemaining + " charges remaining");
			} else if (chargesRemaining == 1) {
				mes("Your amulet now has 1 charge remaining");
			} else {
				mes("You feel the power leave your amulet as it reverts to its uncharged state.");
			}
			delay();
		}

		if (!player.getCache().hasKey("charged_ds_amulet")) {
			player.getCache().set("charged_ds_amulet", 1);
			return;
		}

		final int rubCount = player.getCache().getInt("charged_ds_amulet") + 1;

		if (rubCount < 4) {
			player.getCache().put("charged_ds_amulet", rubCount);
			return;
		}

		player.getCache().remove("charged_ds_amulet");

		final ItemDefinition itemDef = item.getDef(player.getWorld());

		if (player.getConfig().WANT_EQUIPMENT_TAB) {
			if (carriedItems.getEquipment().hasEquipped(item.getCatalogId())) {
				final Equipment equipment = carriedItems.getEquipment();
				final Item neckItem = equipment.getNeckItem();

				if (neckItem != null && neckItem.getCatalogId() == ItemId.CHARGED_DRAGONSTONE_AMULET.id()) {
					if (equipment.remove(neckItem, 1) != -1) {
						equipment.add(new Item(ItemId.DRAGONSTONE_AMULET.id()));
						player.updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(), itemDef.getWearableId(),
							true);
					}
				}
			} else if (carriedItems.remove(new Item(ItemId.CHARGED_DRAGONSTONE_AMULET.id())) != -1) {
				carriedItems.getInventory().add(new Item(ItemId.DRAGONSTONE_AMULET.id()));
			}
		} else {
			if (item.isWielded()) {
				item.setWielded(false);
				player.updateWornItems(itemDef.getWieldPosition(),
					player.getSettings().getAppearance().getSprite(itemDef.getWieldPosition()));
			}

			if (carriedItems.remove(new Item(ItemId.CHARGED_DRAGONSTONE_AMULET.id())) != -1) {
				carriedItems.getInventory().add(new Item(ItemId.DRAGONSTONE_AMULET.id()));
			}
		}

		ActionSender.sendEquipmentStats(player, itemDef.getWieldPosition());
		player.getUpdateFlags().setAppearanceChanged(true);
	}

	@Override
	public boolean blockUseLoc(final Player player, final GameObject obj, final Item item) {
		return obj.getID() == FOUNTAIN_OF_HEROES && item.getCatalogId() == ItemId.DRAGONSTONE_AMULET.id();
	}

	@Override
	public void onUseLoc(final Player player, final GameObject obj, final Item item) {
		if (obj.getID() == FOUNTAIN_OF_HEROES && item.getCatalogId() == ItemId.DRAGONSTONE_AMULET.id()) {
			int repeat = 1;
			if (player.getConfig().BATCH_PROGRESSION) {
				repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId());
			}
			startbatch(repeat);
			batchAmuletCharge(player, item);
		}
	}

	private void batchAmuletCharge(final Player player, Item item) {
		final Inventory inventory = player.getCarriedItems().getInventory();

		item = inventory.get(inventory.getLastIndexById(item.getCatalogId(), Optional.of(false)));

		player.message("You dip the amulet in the fountain");
		delay(2);

		if (player.getCarriedItems().remove(item) != -1) {
			inventory.add(new Item(ItemId.CHARGED_DRAGONSTONE_AMULET.id()));
		}

		mes("You feel more power emanating from it than before");
		delay(3);
		mes("you can now rub this amulet to teleport");
		delay(3);
		mes("Though using it to much means you will need to recharge it");
		delay(3);
		player.message("It now also means you can find more gems when mining");

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			batchAmuletCharge(player, item);
		}
	}
}
