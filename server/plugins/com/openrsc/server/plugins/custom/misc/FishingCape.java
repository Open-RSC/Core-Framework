package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class FishingCape implements OpInvTrigger, UseInvTrigger {

	public static final int MAX_CHARGES = 10;


	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.FISHING_CAPE.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.FISHING_CAPE.id()) {
			mes("@dcy@You think deeply of your experiences with the fish...");
			delay(3);

			if (player.getCarriedItems().getInventory().countId(ItemId.ANA_IN_A_BARREL.id()) > 0) {
				mes("@yel@Ana: stop thinking about fish!!!!");
				delay(3);
				mes("It's too difficult to concentrate on fish rn lol");
				return;
			}

			if (player.getLocation().wildernessLevel() >= Constants.GLORY_TELEPORT_LIMIT || player.getLocation().isInFisherKingRealm()
				|| player.getLocation().isInsideGrandTreeGround()
				|| (player.getLocation().inModRoom() && !player.isAdmin())) {
				player.message("A mysterious force blocks your teleport!");
				player.message("You can't use this teleport after level 30 wilderness");
				return;
			}

			if (!player.getCache().hasKey("fishing_cape_charges")) {
				// free fully charged cape first time using it.
				player.getCache().set("fishing_cape_charges", MAX_CHARGES);
			}

			int charges = player.getCache().getInt("fishing_cape_charges");
			if (charges >= 1) {
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

				charges--;
				player.getCache().put("fishing_cape_charges", charges);
				if (charges > 0) {
					mes("one of the sharks falls out of your cape, but there's still " + charges + " left.");
				} else {
					mes("one of the sharks falls out of your cape");
				}
				delay(3);
				player.teleport(586, 522, true);
				if (charges == 0) {
					mes("@red@Ah!! that was the last shark!!");
				} else {
					mes("@dcy@so many fish...");
				}
			} else {
				player.message("You can't seem to concentrate enough on fish for anything to happen...");
				player.message("Maybe if you stored some sharks in your cape, your bond with the fish would be stronger...");
			}
		}
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item, Item usedWith) {
		if (!player.getCache().hasKey("fishing_cape_charges")) {
			// free 10 charges first time using cape
			player.getCache().set("fishing_cape_charges", MAX_CHARGES);
		}
		int charges = player.getCache().getInt("fishing_cape_charges");
		if (charges >= MAX_CHARGES) {
			player.message("Your cape is already fully charged.");
			return;
		}

		if (item.getCatalogId() == ItemId.RAW_SHARK.id() || usedWith.getCatalogId() == ItemId.RAW_SHARK.id()) {
			Item removeShark = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(ItemId.RAW_SHARK.id(), Optional.of(false)));
			if (removeShark == null) return;

			// Use all sharks eligible to be used
			int maxSharks = player.getCarriedItems().getInventory().countId(ItemId.RAW_SHARK.id());
			if (maxSharks == 1) {
				player.getCarriedItems().remove(removeShark);
				if (charges > 0) {
					mes("You add 1 charge to your cape for a total of " + (charges + 1) + " sharks.");
				} else {
					mes("You now have 1 shark in your cape. Enjoy that.");
				}
				player.getCache().set("fishing_cape_charges", charges + 1);
			} else {
				int sharks = 1;
				player.getCarriedItems().remove(removeShark);
				while (sharks + charges < MAX_CHARGES && sharks < maxSharks) {
					removeShark = player.getCarriedItems().getInventory().get(
						player.getCarriedItems().getInventory().getLastIndexById(ItemId.RAW_SHARK.id(), Optional.of(false)));
					if (removeShark == null) break;
					player.getCarriedItems().remove(removeShark);
					sharks++;
				}
				if (charges > 0) {
					mes("You add " + sharks + " charges to your cape for a total of " + (charges + sharks) + " sharks.");
				} else {
					mes("Your cape now has " + (charges + sharks) + " sharks in it.");
				}
				player.getCache().set("fishing_cape_charges", charges + sharks);
			}
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item, Item usedWith) {
		return item.getCatalogId() == ItemId.FISHING_CAPE.id() || usedWith.getCatalogId() == ItemId.FISHING_CAPE.id();
	}
}
