package com.openrsc.server.plugins.minigames.blurberrysbar;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;

public class DrinkMixing implements InvUseOnItemListener, InvUseOnItemExecutiveListener, InvActionListener, InvActionExecutiveListener {

	private boolean canMix(Item itemOne, Item itemTwo) {
		for (DrinkMix dm : DrinkMix.values()) {
			if (dm.isValid(itemOne.getID(), itemTwo.getID())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean blockInvUseOnItem(Player p, Item item1, Item item2) {
		return canMix(item1, item2);
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		DrinkMix dm = null;
		for (DrinkMix mix : DrinkMix.values()) {
			if (mix.isValid(item1.getID(), item2.getID())) {
				dm = mix;
			}
		}
		if ((hasItem(p, ItemId.FULL_COCKTAIL_GLASS.id()) || hasItem(p, ItemId.ODD_LOOKING_COCKTAIL.id())) 
				&& dm.itemID == ItemId.COCKTAIL_SHAKER.id()) {
			p.message("you need to finish, drink or drop your unfished cocktail");
			p.message("before you can start another - blurberry's rules");
			return;
		}
		if (!p.getCache().hasKey(dm.cacheName)) {
			p.getCache().set(dm.cacheName, 1);
		} else {
			int next = p.getCache().getInt(dm.cacheName);
			p.getCache().set(dm.cacheName, (next + 1));
		}
		if (hasItem(p, dm.itemIDOther)) {
			p.setBusy(true);
			message(p, 1900, dm.messages[0]);
			if (dm.itemIDOther == ItemId.MILK.id()) {
				p.getInventory().replace(ItemId.MILK.id(), ItemId.BUCKET.id());
			} else {
				removeItem(p, dm.itemIDOther, 1);
			}
			if (dm.messages.length > 1) {
				p.message(dm.messages[1]);
			}
			if (p.getCache().hasKey("fruit_blast_base")) { // fruit blast
				if (dm.itemIDOther == ItemId.LEMON_SLICES.id()) {
					p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.FRUIT_BLAST.id());
				} else {
					p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.ODD_LOOKING_COCKTAIL.id());
				}
				p.setBusy(false);
				checkAndRemoveBlurberry(p, true);
			}
			if (p.getCache().hasKey("drunk_dragon_base")) {
				if (dm.itemIDOther != ItemId.CREAM.id() && dm.itemIDOther != ItemId.PINEAPPLE_CHUNKS.id()) { // heat to finish drunk dragon
					p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.ODD_LOOKING_COCKTAIL.id());
					p.setBusy(false);
					checkAndRemoveBlurberry(p, true);
				}
			}
			if (p.getCache().hasKey("sgg_base")) {
				if (dm.itemIDOther != ItemId.EQUA_LEAVES.id() && dm.itemIDOther != ItemId.LIME_SLICES.id()) { // SGG
					p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.ODD_LOOKING_COCKTAIL.id());
					p.setBusy(false);
					checkAndRemoveBlurberry(p, true);
				} else {
					if (p.getCache().hasKey("leaves_into_drink") && p.getCache().hasKey("lime_slices_to_drink")) {
						p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.SGG.id());
						p.setBusy(false);
						checkAndRemoveBlurberry(p, true);
					}
				}
			}
			if (p.getCache().hasKey("chocolate_saturday_base")) {
				if (dm.itemIDOther != ItemId.CHOCOLATE_BAR.id()) { // heat for range - chocolate saturday
					p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.ODD_LOOKING_COCKTAIL.id());
					p.setBusy(false);
					checkAndRemoveBlurberry(p, true);
				}
			}
			if (p.getCache().hasKey("heated_choco_saturday")) {
				if (dm.itemIDOther != ItemId.CREAM.id() && dm.itemIDOther != ItemId.CHOCOLATE_DUST.id()) { // finish chocolate saturday
					p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.ODD_LOOKING_COCKTAIL.id());
					p.setBusy(false);
					checkAndRemoveBlurberry(p, true);
				} else {
					if (p.getCache().hasKey("cream_into_drink") && p.getCache().hasKey("choco_dust_into_drink")) {
						p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.CHOCOLATE_SATURDAY.id());
						p.setBusy(false);
						checkAndRemoveBlurberry(p, true);
					}
				}
			}
			if (p.getCache().hasKey("blurberry_special_base")) {
				if (dm.itemIDOther != ItemId.DICED_ORANGE.id() && dm.itemIDOther != ItemId.DICED_LEMON.id()
						&& dm.itemIDOther != ItemId.LIME_SLICES.id() && dm.itemIDOther != ItemId.EQUA_LEAVES.id()) { // blurberry special finish
					p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.ODD_LOOKING_COCKTAIL.id());
					p.setBusy(false);
					checkAndRemoveBlurberry(p, true);
				} else {
					if (p.getCache().hasKey("diced_orange_in_drink")
						&& p.getCache().hasKey("diced_lemon_in_drink")
						&& p.getCache().hasKey("lime_slices_to_drink")
						&& p.getCache().hasKey("leaves_into_drink")) {
						p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.BLURBERRY_SPECIAL.id());
						p.setBusy(false);
						checkAndRemoveBlurberry(p, true);
					}
				}
			}
			if (p.getCache().hasKey("pineapple_punch_base")) { // finish pineapple punch
				if (dm.itemIDOther != ItemId.PINEAPPLE_CHUNKS.id() && dm.itemIDOther != ItemId.LIME_CHUNKS.id()
						&& dm.itemIDOther != ItemId.LIME_SLICES.id()) {
					p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.ODD_LOOKING_COCKTAIL.id());
					p.setBusy(false);
					checkAndRemoveBlurberry(p, true);
				} else {
					if (p.getCache().hasKey("diced_pa_to_drink")
						&& p.getCache().hasKey("diced_lime_in_drink")
						&& p.getCache().hasKey("lime_slices_to_drink")) {
						p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.PINEAPPLE_PUNCH.id());
						p.setBusy(false);
						checkAndRemoveBlurberry(p, true);
					}
				}
			}
			if (p.getCache().hasKey("wizard_blizzard_base")) { // finish wizard blizzard
				if (dm.itemIDOther != ItemId.PINEAPPLE_CHUNKS.id() && dm.itemIDOther != ItemId.LIME_SLICES.id()) {
					p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.ODD_LOOKING_COCKTAIL.id());
					p.setBusy(false);
					checkAndRemoveBlurberry(p, true);
				} else {
					if (p.getCache().hasKey("diced_pa_to_drink")
						&& p.getCache().hasKey("lime_slices_to_drink")) {
						p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.WIZARD_BLIZZARD.id());
						p.setBusy(false);
						checkAndRemoveBlurberry(p, true);
					}
				}
			}
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player p, String command) {
		return item.getID() == ItemId.COCKTAIL_SHAKER.id();
	}

	@Override
	public void onInvAction(Item item, Player p, String command) {
		if (item.getID() == ItemId.COCKTAIL_SHAKER.id()) {
			if (hasItem(p, ItemId.COCKTAIL_GLASS.id())) {
				boolean complete = false;
				String nextCache = null;
				if (p.getCache().hasKey("lemon_in_shaker")
					&& p.getCache().hasKey("orange_in_shaker")
					&& p.getCache().hasKey("pineapple_in_shaker") && p.getCache().getInt("pineapple_in_shaker") == 1) { // fruit blast base
					complete = true;
					nextCache = "fruit_blast_base";
				}
				if (p.getCache().hasKey("vodka_in_shaker")
					&& p.getCache().hasKey("gin_in_shaker")
					&& p.getCache().hasKey("dwell_in_shaker")) { // drunk dragon base
					complete = true;
					nextCache = "drunk_dragon_base";
				}
				if (p.getCache().hasKey("vodka_in_shaker")
					&& p.getCache().hasKey("lime_in_shaker") && p.getCache().getInt("lime_in_shaker") >= 3) { // SGG base.
					complete = true;
					nextCache = "sgg_base";
				}
				if (p.getCache().hasKey("whisky_in_shaker")
					&& p.getCache().hasKey("milk_in_shaker")
					&& p.getCache().hasKey("leaves_in_shaker")) { // choco saturday base
					complete = true;
					nextCache = "chocolate_saturday_base";
				}
				if (p.getCache().hasKey("vodka_in_shaker")
					&& p.getCache().hasKey("gin_in_shaker")
					&& p.getCache().hasKey("brandy_in_shaker")
					&& p.getCache().hasKey("lemon_in_shaker") && p.getCache().getInt("lemon_in_shaker") >= 2
					&& p.getCache().hasKey("orange_in_shaker")) { // blurberry special base
					complete = true;
					nextCache = "blurberry_special_base";
				}
				if (p.getCache().hasKey("lemon_in_shaker")
					&& p.getCache().hasKey("orange_in_shaker")
					&& p.getCache().hasKey("pineapple_in_shaker") && p.getCache().getInt("pineapple_in_shaker") >= 2) { // pineapple_punch base
					complete = true;
					nextCache = "pineapple_punch_base";
				}
				if (p.getCache().hasKey("pineapple_in_shaker")
					&& p.getCache().hasKey("orange_in_shaker")
					&& p.getCache().hasKey("lemon_in_shaker")
					&& p.getCache().hasKey("lime_in_shaker")
					&& p.getCache().hasKey("vodka_in_shaker") && p.getCache().getInt("vodka_in_shaker") >= 2
					&& p.getCache().hasKey("gin_in_shaker")) { // wizzard blizzard base
					complete = true;
					nextCache = "wizard_blizzard_base";
				}
				if (checkAndRemoveBlurberry(p, false)) {
					checkAndRemoveBlurberry(p, true);
					if (complete) {
						p.getInventory().replace(ItemId.COCKTAIL_GLASS.id(), ItemId.FULL_COCKTAIL_GLASS.id());
						if (!p.getCache().hasKey(nextCache) && nextCache != null)
							p.getCache().store(nextCache, true);
					} else {
						p.getInventory().replace(ItemId.COCKTAIL_GLASS.id(), ItemId.HALF_COCKTAIL_GLASS.id());
					}
				} else {
					// ??
					p.getInventory().replace(ItemId.COCKTAIL_GLASS.id(), ItemId.COCKTAIL_GLASS.id());
				}
				message(p, 600, "you pour the contents into a glass");
			} else {
				p.message("first you'll need a glass to pour the drink into");
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
