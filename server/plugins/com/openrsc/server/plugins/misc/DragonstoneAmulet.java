package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class DragonstoneAmulet implements OpInvTrigger, UseLocTrigger {

	/**
	 * RE-CHARGE AMULET
	 **/
	private static int FOUNTAIN_OF_HEROES = 282;

	@Override
	public boolean blockOpInv(Item item, Player p, String command) {
		return item.getCatalogId() == ItemId.CHARGED_DRAGONSTONE_AMULET.id();
	}

	@Override
	public void onOpInv(Item item, Player p, String command) {
		if (item.getCatalogId() == ItemId.CHARGED_DRAGONSTONE_AMULET.id()) {
			p.message("You rub the amulet");
			delay(p.getWorld().getServer().getConfig().GAME_TICK);
			p.message("Where would you like to teleport to?");
			int menu = multi(p, "Edgeville", "Karamja", "Draynor village", "Al Kharid", "Nowhere");
			//if(p.getLocation().inWilderness() && System.currentTimeMillis() - p.getCombatTimer() < 10000) {
			//	p.message("You need to stay out of combat for 10 seconds before using a teleport.");
			//	return;
			//}
			if (p.getLocation().wildernessLevel() >= Constants.GLORY_TELEPORT_LIMIT || p.getLocation().isInFisherKingRealm()
					|| p.getLocation().isInsideGrandTreeGround()
					|| (p.getLocation().inModRoom() && !p.isAdmin())) {
				p.message("A mysterious force blocks your teleport!");
				p.message("You can't use this teleport after level 30 wilderness");
				return;
			}
			if (p.getCarriedItems().getInventory().countId(ItemId.ANA_IN_A_BARREL.id()) > 0) {
				Functions.mes(p, "You can't teleport while holding Ana,",
					"It's just too difficult to concentrate.");
				return;
			}
			if (p.getCarriedItems().hasCatalogID(ItemId.PLAGUE_SAMPLE.id())) {
				p.message("the plague sample is too delicate...");
				p.message("it disintegrates in the crossing");
				while (p.getCarriedItems().getInventory().countId(ItemId.PLAGUE_SAMPLE.id()) > 0) {
					p.getCarriedItems().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
				}
			}
			if (menu != -1) {
				if (menu == 0) { // Edgeville
					p.teleport(226, 447, true);
				} else if (menu == 1) { // Karamja
					p.teleport(360, 696, true);
				} else if (menu == 2) { // Draynor Village
					p.teleport(214, 632, true);
				} else if (menu == 3) { // Al Kharid
					p.teleport(72, 696, true);
				} else if (menu == 4) { // nothing
					p.message("Nothing interesting happens");
					return;
				}
				if (!p.getCache().hasKey("charged_ds_amulet")) {
					p.getCache().set("charged_ds_amulet", 1);
				} else {
					int rubs = p.getCache().getInt("charged_ds_amulet");
					if (rubs >= 3) {
						p.getCarriedItems().getInventory().replace(ItemId.CHARGED_DRAGONSTONE_AMULET.id(), ItemId.DRAGONSTONE_AMULET.id());
						p.getCache().remove("charged_ds_amulet");
					} else {
						p.getCache().put("charged_ds_amulet", rubs + 1);
					}
				}
			}
		}
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player p) {
		return obj.getID() == FOUNTAIN_OF_HEROES && item.getCatalogId() == ItemId.DRAGONSTONE_AMULET.id();
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == FOUNTAIN_OF_HEROES && item.getCatalogId() == ItemId.DRAGONSTONE_AMULET.id()) {
			p.setBusy(true);
			p.message("You dip the amulet in the fountain");
			delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
			p.setBatchEvent(new BatchEvent(p.getWorld(), p, p.getWorld().getServer().getConfig().GAME_TICK, "Charge Dragonstone Ammy", p.getCarriedItems().getInventory().countId(item.getCatalogId()), false) {

				@Override
				public void action() {
					if (!p.getCarriedItems().hasCatalogID(item.getCatalogId())) {
						stop();
						return;
					}
					if (p.getCarriedItems().remove(item) > -1) {
						p.getCarriedItems().getInventory().add(new Item(ItemId.CHARGED_DRAGONSTONE_AMULET.id()));
					} else
						interrupt();
				}
			});
			Functions.mes(p, "You feel more power emanating from it than before",
				"you can now rub this amulet to teleport",
				"Though using it to much means you will need to recharge it");
			p.message("It now also means you can find more gems when mining");
			p.setBusy(false);
		}
	}
}
