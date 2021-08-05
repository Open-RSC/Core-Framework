package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DragonstoneAmulet implements OpInvTrigger, UseLocTrigger {

	/**
	 * RE-CHARGE AMULET
	 **/
	private static int FOUNTAIN_OF_HEROES = 282;

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.CHARGED_DRAGONSTONE_AMULET.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.CHARGED_DRAGONSTONE_AMULET.id()) {
			player.message("You rub the amulet");
			delay();
			player.message("Where would you like to teleport to?");
			int menu = multi(player, "Edgeville", "Karamja", "Draynor village", "Al Kharid", "Nowhere");
			//if(p.getLocation().inWilderness() && System.currentTimeMillis() - p.getCombatTimer() < 10000) {
			//	p.message("You need to stay out of combat for 10 seconds before using a teleport.");
			//	return;
			//}
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

			if (menu != -1) {
				if (player.getCarriedItems().hasCatalogID(ItemId.KARAMJA_RUM.id()) && (player.getLocation().inKaramja())) {
					player.getCarriedItems().remove(new Item(ItemId.KARAMJA_RUM.id()));
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.PLAGUE_SAMPLE.id())) {
					player.message("the plague sample is too delicate...");
					player.message("it disintegrates in the crossing");
					while (player.getCarriedItems().getInventory().countId(ItemId.PLAGUE_SAMPLE.id()) > 0) {
						player.getCarriedItems().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
					}
				}

				if (menu == 0) { // Edgeville
					player.teleport(226, 447, true);
				} else if (menu == 1) { // Karamja
					player.teleport(360, 696, true);
				} else if (menu == 2) { // Draynor Village
					player.teleport(214, 632, true);
				} else if (menu == 3) { // Al Kharid
					player.teleport(72, 696, true);
				} else if (menu == 4) { // nothing
					player.message("Nothing interesting happens");
					return;
				}
				if (!player.getCache().hasKey("charged_ds_amulet")) {
					player.getCache().set("charged_ds_amulet", 1);
				} else {
					int rubs = player.getCache().getInt("charged_ds_amulet");
					if (rubs >= 3) {
						int chargedId = ItemId.CHARGED_DRAGONSTONE_AMULET.id();
						Item toAdd = new Item(ItemId.DRAGONSTONE_AMULET.id());
						Item currentNeckItem = player.getCarriedItems().getEquipment().getNeckItem();
						if (config().WANT_EQUIPMENT_TAB && currentNeckItem != null
							&& currentNeckItem.getCatalogId() == chargedId) {

							player.getCarriedItems().getEquipment().remove(currentNeckItem, 1);
							player.getCarriedItems().getEquipment().add(toAdd);
						}
						else {
							player.getCarriedItems().remove(new Item(chargedId));
							player.getCarriedItems().getInventory().add(toAdd);
						}
						ItemDefinition itemDef = item.getDef(player.getWorld());
						player.updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(), itemDef.getWearableId(), true);
						ActionSender.sendEquipmentStats(player, item.getDef(player.getWorld()).getWieldPosition());
						ActionSender.sendUpdatedPlayer(player);
						player.getCache().remove("charged_ds_amulet");
					} else {
						player.getCache().put("charged_ds_amulet", rubs + 1);
					}
				}
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == FOUNTAIN_OF_HEROES && item.getCatalogId() == ItemId.DRAGONSTONE_AMULET.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == FOUNTAIN_OF_HEROES && item.getCatalogId() == ItemId.DRAGONSTONE_AMULET.id()) {
			int repeat = 1;
			if (config().BATCH_PROGRESSION) {
				repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId());
			}
			startbatch(repeat);
			batchAmuletCharge(player, item);
		}
	}

	private void batchAmuletCharge(Player player, Item item) {
		item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		player.message("You dip the amulet in the fountain");
		delay(2);
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.CHARGED_DRAGONSTONE_AMULET.id()));

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
