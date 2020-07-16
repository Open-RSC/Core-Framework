package com.openrsc.server.plugins.authentic.minigames.blurberrysbar;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DrinkMixing implements UseInvTrigger, OpInvTrigger {

	private boolean canMix(Item itemOne, Item itemTwo) {
		for (DrinkMix dm : DrinkMix.values()) {
			if (dm.isValid(itemOne.getCatalogId(), itemTwo.getCatalogId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return canMix(item1, item2);
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		DrinkMix dm = null;
		for (DrinkMix mix : DrinkMix.values()) {
			if (mix.isValid(item1.getCatalogId(), item2.getCatalogId())) {
				dm = mix;
			}
		}
		if ((player.getCarriedItems().hasCatalogID(ItemId.FULL_COCKTAIL_GLASS.id(), Optional.of(false))
			|| player.getCarriedItems().hasCatalogID(ItemId.ODD_LOOKING_COCKTAIL.id(), Optional.of(false)))
				&& dm.itemID == ItemId.COCKTAIL_SHAKER.id()) {
			player.message("you need to finish, drink or drop your unfished cocktail");
			player.message("before you can start another - blurberry's rules");
			return;
		}
		if (!player.getCache().hasKey(dm.cacheName)) {
			player.getCache().set(dm.cacheName, 1);
		} else {
			int next = player.getCache().getInt(dm.cacheName);
			player.getCache().set(dm.cacheName, (next + 1));
		}
		if (player.getCarriedItems().hasCatalogID(dm.itemIDOther, Optional.of(false))) {
			mes(dm.messages[0]);
			delay(3);
			if (dm.itemIDOther == ItemId.MILK.id()) {
				player.getCarriedItems().remove(new Item(ItemId.MILK.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.BUCKET.id()));
			} else {
				player.getCarriedItems().remove(new Item(dm.itemIDOther));
			}
			if (dm.messages.length > 1) {
				player.message(dm.messages[1]);
			}
			if (player.getCache().hasKey("fruit_blast_base")) { // fruit blast
				if (dm.itemIDOther == ItemId.LEMON_SLICES.id()) {
					player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.FRUIT_BLAST.id()));
				} else {
					player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.ODD_LOOKING_COCKTAIL.id()));
				}
				checkAndRemoveBlurberry(player, true);
			}
			if (player.getCache().hasKey("drunk_dragon_base")) {
				if (dm.itemIDOther != ItemId.CREAM.id() && dm.itemIDOther != ItemId.PINEAPPLE_CHUNKS.id()) { // heat to finish drunk dragon
					player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.ODD_LOOKING_COCKTAIL.id()));
					checkAndRemoveBlurberry(player, true);
				}
			}
			if (player.getCache().hasKey("sgg_base")) {
				if (dm.itemIDOther != ItemId.EQUA_LEAVES.id() && dm.itemIDOther != ItemId.LIME_SLICES.id()) { // SGG
					player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.ODD_LOOKING_COCKTAIL.id()));
					checkAndRemoveBlurberry(player, true);
				} else {
					if (player.getCache().hasKey("leaves_into_drink") && player.getCache().hasKey("lime_slices_to_drink")) {
						player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.SGG.id()));
						checkAndRemoveBlurberry(player, true);
					}
				}
			}
			if (player.getCache().hasKey("chocolate_saturday_base")) {
				if (dm.itemIDOther != ItemId.CHOCOLATE_BAR.id()) { // heat for range - chocolate saturday
					player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.ODD_LOOKING_COCKTAIL.id()));
					checkAndRemoveBlurberry(player, true);
				}
			}
			if (player.getCache().hasKey("heated_choco_saturday")) {
				if (dm.itemIDOther != ItemId.CREAM.id() && dm.itemIDOther != ItemId.CHOCOLATE_DUST.id()) { // finish chocolate saturday
					player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.ODD_LOOKING_COCKTAIL.id()));
					checkAndRemoveBlurberry(player, true);
				} else {
					if (player.getCache().hasKey("cream_into_drink") && player.getCache().hasKey("choco_dust_into_drink")) {
						player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.CHOCOLATE_SATURDAY.id()));
						checkAndRemoveBlurberry(player, true);
					}
				}
			}
			if (player.getCache().hasKey("blurberry_special_base")) {
				if (dm.itemIDOther != ItemId.DICED_ORANGE.id() && dm.itemIDOther != ItemId.DICED_LEMON.id()
						&& dm.itemIDOther != ItemId.LIME_SLICES.id() && dm.itemIDOther != ItemId.EQUA_LEAVES.id()) { // blurberry special finish
					player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.ODD_LOOKING_COCKTAIL.id()));
					checkAndRemoveBlurberry(player, true);
				} else {
					if (player.getCache().hasKey("diced_orange_in_drink")
						&& player.getCache().hasKey("diced_lemon_in_drink")
						&& player.getCache().hasKey("lime_slices_to_drink")
						&& player.getCache().hasKey("leaves_into_drink")) {
						player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.BLURBERRY_SPECIAL.id()));
						checkAndRemoveBlurberry(player, true);
					}
				}
			}
			if (player.getCache().hasKey("pineapple_punch_base")) { // finish pineapple punch
				if (dm.itemIDOther != ItemId.PINEAPPLE_CHUNKS.id() && dm.itemIDOther != ItemId.LIME_CHUNKS.id()
						&& dm.itemIDOther != ItemId.LIME_SLICES.id()) {
					player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.ODD_LOOKING_COCKTAIL.id()));
					checkAndRemoveBlurberry(player, true);
				} else {
					if (player.getCache().hasKey("diced_pa_to_drink")
						&& player.getCache().hasKey("diced_lime_in_drink")
						&& player.getCache().hasKey("lime_slices_to_drink")) {
						player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.PINEAPPLE_PUNCH.id()));
						checkAndRemoveBlurberry(player, true);
					}
				}
			}
			if (player.getCache().hasKey("wizard_blizzard_base")) { // finish wizard blizzard
				if (dm.itemIDOther != ItemId.PINEAPPLE_CHUNKS.id() && dm.itemIDOther != ItemId.LIME_SLICES.id()) {
					player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.ODD_LOOKING_COCKTAIL.id()));
					checkAndRemoveBlurberry(player, true);
				} else {
					if (player.getCache().hasKey("diced_pa_to_drink")
						&& player.getCache().hasKey("lime_slices_to_drink")) {
						player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.WIZARD_BLIZZARD.id()));
						checkAndRemoveBlurberry(player, true);
					}
				}
			}
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.COCKTAIL_SHAKER.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.COCKTAIL_SHAKER.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.COCKTAIL_GLASS.id(), Optional.of(false))) {
				boolean complete = false;
				String nextCache = null;
				if (player.getCache().hasKey("lemon_in_shaker")
					&& player.getCache().hasKey("orange_in_shaker")
					&& player.getCache().hasKey("pineapple_in_shaker") && player.getCache().getInt("pineapple_in_shaker") == 1) { // fruit blast base
					complete = true;
					nextCache = "fruit_blast_base";
				}
				if (player.getCache().hasKey("vodka_in_shaker")
					&& player.getCache().hasKey("gin_in_shaker")
					&& player.getCache().hasKey("dwell_in_shaker")) { // drunk dragon base
					complete = true;
					nextCache = "drunk_dragon_base";
				}
				if (player.getCache().hasKey("vodka_in_shaker")
					&& player.getCache().hasKey("lime_in_shaker") && player.getCache().getInt("lime_in_shaker") >= 3) { // SGG base.
					complete = true;
					nextCache = "sgg_base";
				}
				if (player.getCache().hasKey("whisky_in_shaker")
					&& player.getCache().hasKey("milk_in_shaker")
					&& player.getCache().hasKey("leaves_in_shaker")) { // choco saturday base
					complete = true;
					nextCache = "chocolate_saturday_base";
				}
				if (player.getCache().hasKey("vodka_in_shaker")
					&& player.getCache().hasKey("gin_in_shaker")
					&& player.getCache().hasKey("brandy_in_shaker")
					&& player.getCache().hasKey("lemon_in_shaker") && player.getCache().getInt("lemon_in_shaker") >= 2
					&& player.getCache().hasKey("orange_in_shaker")) { // blurberry special base
					complete = true;
					nextCache = "blurberry_special_base";
				}
				if (player.getCache().hasKey("lemon_in_shaker")
					&& player.getCache().hasKey("orange_in_shaker")
					&& player.getCache().hasKey("pineapple_in_shaker") && player.getCache().getInt("pineapple_in_shaker") >= 2) { // pineapple_punch base
					complete = true;
					nextCache = "pineapple_punch_base";
				}
				if (player.getCache().hasKey("pineapple_in_shaker")
					&& player.getCache().hasKey("orange_in_shaker")
					&& player.getCache().hasKey("lemon_in_shaker")
					&& player.getCache().hasKey("lime_in_shaker")
					&& player.getCache().hasKey("vodka_in_shaker") && player.getCache().getInt("vodka_in_shaker") >= 2
					&& player.getCache().hasKey("gin_in_shaker")) { // wizzard blizzard base
					complete = true;
					nextCache = "wizard_blizzard_base";
				}
				if (checkAndRemoveBlurberry(player, false)) {
					checkAndRemoveBlurberry(player, true);
					if (complete) {
						player.getCarriedItems().remove(new Item(ItemId.COCKTAIL_GLASS.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
						if (!player.getCache().hasKey(nextCache) && nextCache != null)
							player.getCache().store(nextCache, true);
					} else {
						player.getCarriedItems().remove(new Item(ItemId.COCKTAIL_GLASS.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.HALF_COCKTAIL_GLASS.id()));
					}
				} else {
					// ??
					player.getCarriedItems().remove(new Item(ItemId.COCKTAIL_GLASS.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.COCKTAIL_GLASS.id()));
				}
				mes("you pour the contents into a glass");
				delay();
			} else {
				player.message("first you'll need a glass to pour the drink into");
			}
		}
	}

	enum DrinkMix {
		LEMON_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.LEMON.id(), "lemon_in_shaker",
			"you squeeze the juice from the lemon...",
			"....into your cocktail shaker and shake well"),
		ORANGE_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.ORANGE.id(), "orange_in_shaker",
			"you squeeze the juice from the orange...",
			"....into your cocktail shaker and shake well"),
		PINE_APPLE_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.FRESH_PINEAPPLE.id(), "pineapple_in_shaker",
			"you squeeze the juice from the pineapple...",
			"....into your cocktail shaker and shake well"),
		LEMON_SLICES_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.LEMON_SLICES.id(), "lemon_slices_to_drink",
			"you place the lemon slices on the edge of the glass"),
		VODKA_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.VODKA.id(), "vodka_in_shaker",
			"you pour the vodka into the cocktail shaker",
			"you shake the container"),
		GIN_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.GIN.id(), "gin_in_shaker",
			"you pour the gin into the cocktail shaker",
			"you shake the container"),
		DWELLBERRIES_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.DWELLBERRIES.id(), "dwell_in_shaker",
			"you squeeze the juice from the dwellberries...",
			"....into your cocktail shaker and shake well"),
		DICED_PINE_APPLE_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.PINEAPPLE_CHUNKS.id(), "diced_pa_to_drink",
			"you add the pineapple chunks to the drink"),
		CREAM_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.CREAM.id(), "cream_into_drink",
			"you pour the thick cream into the drink"),
		LIME_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.LIME.id(), "lime_in_shaker",
			"you squeeze the juice from the lime...",
			"....into your cocktail shaker and shake well"),
		LEAVES_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.EQUA_LEAVES.id(), "leaves_into_drink",
			"you sprinkle the leaves over the drink"),
		LIME_SLICES_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.LIME_SLICES.id(), "lime_slices_to_drink",
			"you place the lime slices on the edge of the glass"),
		WHISKY_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.WHISKY.id(), "whisky_in_shaker",
			"you pour the whisky into the cocktail shaker",
			"you shake the container"),
		MILK_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.MILK.id(), "milk_in_shaker",
			"you pour the milk into the cocktail shaker",
			"and shake thoroughly"),
		LEAVES_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.EQUA_LEAVES.id(), "leaves_in_shaker",
			"you sprinkle the equa leaves into the shaker",
			"and shake thoroughly"),
		CHOCOLATE_BAR_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.CHOCOLATE_BAR.id(), "choco_bar_in_drink",
			"you crumble the chocolate into the drink"),
		CHOCOLATE_DUST_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.CHOCOLATE_DUST.id(), "choco_dust_into_drink",
			"you sprinkle the chocolate dust over the drink"),
		BRANDY_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.BRANDY.id(), "brandy_in_shaker",
			"you pour the brandy into the cocktail shaker",
			"you shake the container"),
		DICED_ORANGE_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.DICED_ORANGE.id(), "diced_orange_in_drink",
			"you add the diced orange to the drink"),
		DICED_LEMON_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.DICED_LEMON.id(), "diced_lemon_in_drink",
			"you add the diced lemon to the drink"),
		DICED_LIME_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.LIME_CHUNKS.id(), "diced_lime_in_drink",
			"you add the lime chunks to the drink");


		private int itemID;
		private int itemIDOther;
		private String cacheName;
		private String[] messages;

		DrinkMix(int itemOne, int itemTwo, String cacheName, String... messages) {
			this.itemID = itemOne;
			this.itemIDOther = itemTwo;
			this.cacheName = cacheName;
			this.messages = messages;
		}

		public boolean isValid(int i, int is) {
			return compareItemsIds(new Item(itemID), new Item(itemIDOther), i, is);
		}
	}
}
