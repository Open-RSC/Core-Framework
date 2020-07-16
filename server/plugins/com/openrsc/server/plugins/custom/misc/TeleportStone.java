package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class TeleportStone implements OpInvTrigger {

	// Pretty sure this item doesn't even exist.
	private final int TELEPORT_STONE = 2107;

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == TELEPORT_STONE;
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == TELEPORT_STONE) {
			mes("the stone starts shaking...");
			delay(3);
			player.message("a magical portal opens up, where would you like to go?");
			delay(4);
			String[] teleLoc = {"Lumbridge", "Draynor", "Falador", "Edgeville", "Varrock", "Alkharid", "Karamja", "Yanille", "Ardougne", "Catherby", "Seers", "Gnome Stronghold", "Stay here"};
			int menu = multi(player, teleLoc);
			//if (p.getLocation().inWilderness() && System.currentTimeMillis() - p.getCombatTimer() < 10000) {
			//	p.message("You need to stay out of combat for 10 seconds before using a teleport.");
			//	return;
			//}
			if (player.getLocation().wildernessLevel() >= 30 || player.getLocation().isInFisherKingRealm()
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
			switch (menu) {
				case -1:// stop them.
					return;
				case 0: // lumb
					player.teleport(125, 648);
					break;
				case 1: // dray
					player.teleport(214, 632);
					break;
				case 2: // falla
					player.teleport(304, 542);
					break;
				case 3: // edge
					player.teleport(223, 447);
					break;
				case 4: // varrock
					player.teleport(122, 509);
					break;
				case 5: // alkharid
					player.teleport(85, 691);
					break;
				case 6: // Karamja
					player.teleport(372, 706);
					break;
				case 7: // Yanille
					player.teleport(583, 747);
					break;
				case 8: // Ardougne
					player.teleport(557, 606);
					break;
				case 9: // Catherby
					player.teleport(442, 503);
					break;
				case 10: // Seers
					player.teleport(493, 456);
					break;
				case 11: // Gnome Stronghold
					player.teleport(703, 481);
					break;
				case 12:
					return;
			}
			player.getCarriedItems().remove(new Item(TELEPORT_STONE));
			delay();
			player.message("You landed in " + teleLoc[menu]);
		}
	}
}
