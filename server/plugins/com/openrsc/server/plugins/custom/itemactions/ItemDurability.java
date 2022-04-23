package com.openrsc.server.plugins.custom.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.content.EnchantedCrowns;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.MathUtil;

import static com.openrsc.server.plugins.Functions.*;

public class ItemDurability implements OpInvTrigger {
	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return (inArray(item.getCatalogId(), ItemId.RING_OF_RECOIL.id(), ItemId.RING_OF_FORGING.id(), ItemId.DWARVEN_RING.id(),
			ItemId.CROWN_OF_DEW.id(), ItemId.CROWN_OF_MIMICRY.id(), ItemId.CROWN_OF_THE_ARTISAN.id(), ItemId.CROWN_OF_THE_ITEMS.id(),
			ItemId.CROWN_OF_THE_HERBALIST.id(), ItemId.CROWN_OF_THE_OCCULT.id())
			&& (command.equalsIgnoreCase("check") || command.equalsIgnoreCase("break") || command.equalsIgnoreCase("configure")));
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (command.equalsIgnoreCase("check")) {
			int charges;
			int totalCharges;
			if (item.getCatalogId() == ItemId.RING_OF_RECOIL.id()) {
				totalCharges = config().RING_OF_RECOIL_LIMIT;
				charges = player.getCache().hasKey("ringofrecoil")
					? totalCharges - player.getCache().getInt("ringofrecoil")
					: totalCharges;
				player.message("Your Ring of Recoil has " + charges + "/" +
					totalCharges + " charges remaining.");
			} else if (item.getCatalogId() == ItemId.RING_OF_FORGING.id()) {
				totalCharges = config().RING_OF_FORGING_USES;
				charges = player.getCache().hasKey("ringofforging")
					? totalCharges - player.getCache().getInt("ringofforging")
					: totalCharges;
				player.message("Your Ring of Forging has " + charges + "/" +
					totalCharges + " charges remaining.");
			} else if (item.getCatalogId() == ItemId.DWARVEN_RING.id()) {
				totalCharges = config().DWARVEN_RING_USES;
				charges = player.getCache().hasKey("dwarvenring")
					? totalCharges - player.getCache().getInt("dwarvenring")
					: totalCharges;
				player.message("Your Dwarven Ring has " + charges + "/" +
					totalCharges + " charges remaining.");
			} else if (item.getCatalogId() == ItemId.CROWN_OF_DEW.id()) {
				totalCharges = EnchantedCrowns.DEW_CROWN_USES;
				charges = player.getCache().hasKey("dewcrown")
					? totalCharges - player.getCache().getInt("dewcrown")
					: totalCharges;
				player.message("Your Crown of Dew has " + charges + "/" +
					totalCharges + " charges remaining.");
			} else if (item.getCatalogId() == ItemId.CROWN_OF_MIMICRY.id()) {
				totalCharges = EnchantedCrowns.MIMICRY_CROWN_USES;
				charges = player.getCache().hasKey("mimicrycrown")
					? totalCharges - player.getCache().getInt("mimicrycrown")
					: totalCharges;
				player.message("Your Crown of Mimicry has " + charges + "/" +
					totalCharges + " charges remaining.");
			} else if (item.getCatalogId() == ItemId.CROWN_OF_THE_ARTISAN.id()) {
				totalCharges = EnchantedCrowns.ARTISAN_CROWN_USES;
				charges = player.getCache().hasKey("artisancrown")
					? totalCharges - player.getCache().getInt("artisancrown")
					: totalCharges;
				player.message("Your Crown of the Artisan has " + charges + "/" +
					totalCharges + " charges remaining.");
			} else if (item.getCatalogId() == ItemId.CROWN_OF_THE_ITEMS.id()) {
				totalCharges = EnchantedCrowns.ITEMS_CROWN_USES;
				charges = player.getCache().hasKey("itemscrown")
					? totalCharges - player.getCache().getInt("itemscrown")
					: totalCharges;
				player.message("Your Crown of the Items has " + charges + "/" +
					totalCharges + " charges remaining.");
			} else if (item.getCatalogId() == ItemId.CROWN_OF_THE_HERBALIST.id()) {
				totalCharges = EnchantedCrowns.HERBALIST_CROWN_USES;
				charges = player.getCache().hasKey("herbalistcrown")
					? totalCharges - player.getCache().getInt("herbalistcrown")
					: 0;
				player.message("Your Crown of the Herbalist has " + charges + "/" +
					totalCharges + " charges remaining.");
			} else if (item.getCatalogId() == ItemId.CROWN_OF_THE_OCCULT.id()) {
				totalCharges = EnchantedCrowns.OCCULT_CROWN_USES;
				charges = player.getCache().hasKey("occultcrown")
					? totalCharges - player.getCache().getInt("occultcrown")
					: 0;
				player.message("Your Crown of the Occult has " + charges + "/" +
					totalCharges + " charges remaining.");
			}
		} else if (command.equalsIgnoreCase("break")) {
			player.message("Are you sure you want to break your " + item.getDef(player.getWorld()).getName() + "?");
			delay();
			int choice = multi(player, "Yes", "No");
			if (choice != 0) return;
			if (item.getCatalogId() == ItemId.RING_OF_RECOIL.id()) {
				player.getCache().remove("ringofrecoil");
				player.getCarriedItems().shatter(item);
			} else if (item.getCatalogId() == ItemId.RING_OF_FORGING.id()) {
				player.getCache().remove("ringofforging");
				player.getCarriedItems().shatter(item);
			} else if (item.getCatalogId() == ItemId.DWARVEN_RING.id()) {
				player.getCache().remove("dwarvenring");
				player.getCarriedItems().shatter(item);
			} else if (item.getCatalogId() == ItemId.CROWN_OF_DEW.id()) {
				player.getCache().remove("dewcrown");
				player.getCarriedItems().shatter(item);
			} else if (item.getCatalogId() == ItemId.CROWN_OF_MIMICRY.id()) {
				player.getCache().remove("mimicrycrown");
				player.getCarriedItems().shatter(item);
			} else if (item.getCatalogId() == ItemId.CROWN_OF_THE_ARTISAN.id()) {
				player.getCache().remove("artisancrown");
				player.getCarriedItems().shatter(item);
			} else if (item.getCatalogId() == ItemId.CROWN_OF_THE_ITEMS.id()) {
				player.getCache().remove("itemscrown");
				player.getCarriedItems().shatter(item);
			} else if (item.getCatalogId() == ItemId.CROWN_OF_THE_HERBALIST.id()) {
				player.getCache().remove("herbalistcrown");
				player.message("The power of the crown prevents you from breaking it");
				player.message("but you manage to clear its charges");
			} else if (item.getCatalogId() == ItemId.CROWN_OF_THE_OCCULT.id()) {
				player.getCache().remove("occultcrown");
				player.message("The power of the crown prevents you from breaking it");
				player.message("but you manage to clear its charges");
			}
		} else if (command.equalsIgnoreCase("configure")) {
			if (item.getCatalogId() == ItemId.CROWN_OF_DEW.id()) {
				player.message("Select which dough your crown should make when activated");
				delay();
				int choice = multi(player, "bread dough", "pastry dough", "pizza dough", "pitta dough");
				if (choice < 0 || choice > 3) return;
				player.message("Dough selection set successfully");
				player.getCache().set("dough_conf", choice);
			} else if (item.getCatalogId() == ItemId.CROWN_OF_THE_HERBALIST.id()) {
				int conf = player.getCache().hasKey("herb_conf") ? player.getCache().getInt("herb_conf") : 7;
				player.message("Select which herb tiers your crown should keep or destroy when activated");
				delay();
				int choice;
				do {
					choice = multi(player, action("low tier", conf, 1), action("medium tier", conf, 2), action("high tier", conf, 3), "cancel");
					if (choice >= 0 && choice <= 2) {
						conf = MathUtil.toggleKthBit(conf, choice + 1);
						player.getCache().set("herb_conf", conf);
					}
				} while (choice >= 0 && choice <= 2);
			} else if (item.getCatalogId() == ItemId.CROWN_OF_THE_OCCULT.id()) {
				int conf = player.getCache().hasKey("bone_conf") ? player.getCache().getInt("bone_conf") : 7;
				player.message("Select which bone tiers your crown should keep or destroy when activated");
				delay();
				int choice;
				do {
					choice = multi(player, action("low tier", conf, 1), action("medium tier", conf, 2), action("high tier", conf, 3), "cancel");
					if (choice >= 0 && choice <= 2) {
						conf = MathUtil.toggleKthBit(conf, choice + 1);
						player.getCache().set("bone_conf", conf);
					}
				} while (choice >= 0 && choice <= 2);
			}
		}
	}

	private String action(String type, int number, int bit) {
		boolean isSet = MathUtil.isKthBitSet(number, bit);
		return (!isSet ? "destroy" : "keep") + " " + type;
	}
}
