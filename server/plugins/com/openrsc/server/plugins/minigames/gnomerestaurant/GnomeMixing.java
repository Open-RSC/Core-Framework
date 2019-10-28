package com.openrsc.server.plugins.minigames.gnomerestaurant;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeMixing implements InvUseOnItemListener, InvUseOnItemExecutiveListener {

	private boolean canMix(World world, Item itemOne, Item itemTwo) {
		for (GnomeMix gm : GnomeMix.values()) {
			if (gm.isValid(world, itemOne.getID(), itemTwo.getID())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean blockInvUseOnItem(Player p, Item item1, Item item2) {
		return canMix(p.getWorld(), item1, item2);
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		GnomeMix gm = null;
		for (GnomeMix mix : GnomeMix.values()) {
			if (mix.isValid(p.getWorld(), item1.getID(), item2.getID())) {
				gm = mix;
			}
		}
		if (!p.getCache().hasKey(gm.cacheName)) {
			p.getCache().set(gm.cacheName, 1);
		} else {
			int next = p.getCache().getInt(gm.cacheName);
			p.getCache().set(gm.cacheName, (next + 1));
		}
		if (hasItem(p, gm.itemIDOther)) {
			p.setBusy(true);
			if (gm.itemIDOther != ItemId.GNOME_SPICE.id())
				removeItem(p, gm.itemIDOther, 1);
			if (p.getCache().hasKey("cheese_on_batta")
				&& p.getCache().hasKey("tomato_on_batta")
				&& p.getCache().hasKey("tomato_cheese_batta")
				&& p.getCache().hasKey("leaves_on_batta")) { // tomato cheese batta
				p.getCache().set("complete_dish", ItemId.CHEESE_AND_TOMATO_BATTA.id());
			}
			if (p.getCache().hasKey("chocolate_on_bowl") && p.getCache().getInt("chocolate_on_bowl") >= 4
				&& p.getCache().hasKey("leaves_on_bowl")
				&& p.getCache().hasKey("chocolate_bomb")
				&& p.getCache().hasKey("cream_on_bowl") && p.getCache().getInt("cream_on_bowl") >= 2
				&& p.getCache().hasKey("choco_dust_on_bowl")) { // chocolate bomb
				p.getCache().set("complete_dish", ItemId.CHOCOLATE_BOMB.id());
			}
			if (p.getCache().hasKey("kingworms_on_bowl") && p.getCache().getInt("kingworms_on_bowl") >= 6
				&& p.getCache().hasKey("onions_on_bowl") && p.getCache().getInt("onions_on_bowl") >= 2
				&& p.getCache().hasKey("gnomespice_on_bowl")
				&& p.getCache().hasKey("wormhole")
				&& p.getCache().hasKey("leaves_on_bowl")) { // wormhole
				p.getCache().set("complete_dish", ItemId.WORM_HOLE.id());
			}
			if (p.getCache().hasKey("gnomespice_on_dough")
				&& p.getCache().hasKey("toadlegs_on_dough") && p.getCache().getInt("toadlegs_on_dough") >= 2
				&& p.getCache().hasKey("leaves_on_crunchies")
				&& p.getCache().hasKey("gnomecrunchie_dough")
				&& p.getCache().hasKey("gnome_crunchie_cooked")) { // toad crunchies
				p.getCache().set("complete_dish", ItemId.TOAD_CRUNCHIES.id());
			}
			if (p.getCache().hasKey("gnomespice_on_worm")
				&& p.getCache().hasKey("worm_on_batta")
				&& p.getCache().hasKey("cheese_on_batta")
				&& p.getCache().hasKey("worm_batta")
				&& p.getCache().hasKey("leaves_on_batta")) { // worm batta
				p.getCache().set("complete_dish", ItemId.WORM_BATTA.id());
			}
			if (p.getCache().hasKey("onion_on_batta")
				&& p.getCache().hasKey("tomato_on_batta") && p.getCache().getInt("tomato_on_batta") >= 2
				&& p.getCache().hasKey("cabbage_on_batta")
				&& p.getCache().hasKey("dwell_on_batta")
				&& p.getCache().hasKey("veg_batta_no_cheese")
				&& p.getCache().hasKey("veg_batta_with_cheese")
				&& p.getCache().hasKey("leaves_on_batta")) { // veg batta
				p.getCache().set("complete_dish", ItemId.VEG_BATTA.id());
			}
			if (p.getCache().hasKey("gnomespice_on_dough")
				&& p.getCache().hasKey("chocolate_on_dough") && p.getCache().getInt("chocolate_on_dough") >= 2
				&& p.getCache().hasKey("choco_dust_on_crunchies")
				&& p.getCache().hasKey("gnomecrunchie_dough")
				&& p.getCache().hasKey("gnome_crunchie_cooked")) { // choc crunchies
				p.getCache().set("complete_dish", ItemId.CHOC_CRUNCHIES.id());
			}
			if (p.getCache().hasKey("onions_on_bowl") && p.getCache().getInt("onions_on_bowl") >= 2
				&& p.getCache().hasKey("potato_on_bowl") && p.getCache().getInt("potato_on_bowl") >= 2
				&& p.getCache().hasKey("gnomespice_on_bowl")
				&& p.getCache().hasKey("vegball")
				&& p.getCache().hasKey("leaves_on_bowl")) { // vegball
				p.getCache().set("complete_dish", ItemId.VEGBALL.id());
			}
			if (p.getCache().hasKey("gnomespice_on_dough")
				&& p.getCache().hasKey("kingworm_on_dough") && p.getCache().getInt("kingworm_on_dough") >= 2
				&& p.getCache().hasKey("leaves_on_dough")
				&& p.getCache().hasKey("gnomecrunchie_dough")
				&& p.getCache().hasKey("gnome_crunchie_cooked")
				&& p.getCache().hasKey("spice_over_crunchies")) { // worm crunchies
				p.getCache().set("complete_dish", ItemId.WORM_CRUNCHIES.id());
			}
			if (p.getCache().hasKey("gnomespice_on_dough") && p.getCache().getInt("gnomespice_on_dough") >= 3
				&& p.getCache().hasKey("leaves_on_dough") && p.getCache().getInt("leaves_on_dough") >= 2
				&& p.getCache().hasKey("gnomecrunchie_dough")
				&& p.getCache().hasKey("gnome_crunchie_cooked")
				&& p.getCache().hasKey("spice_over_crunchies")) { // spice crunchies
				p.getCache().set("complete_dish", ItemId.SPICE_CRUNCHIES.id());
			}
			if (p.getCache().hasKey("leaves_on_batta") && p.getCache().getInt("leaves_on_batta") >= 4
				&& p.getCache().hasKey("batta_cooked_leaves")
				&& p.getCache().hasKey("diced_orange_on_batta")
				&& p.getCache().hasKey("lime_on_batta")
				&& p.getCache().hasKey("pine_apple_batta")
				&& p.getCache().hasKey("spice_over_batta")) { // fruit batta
				p.getCache().set("complete_dish", ItemId.FRUIT_BATTA.id());
			}

			if (p.getCache().hasKey("complete_dish")) {
				removeItem(p, gm.itemID, 1);
				addItem(p, p.getCache().getInt("complete_dish"), 1);
				resetGnomeCooking(p);
			}
			p.message(gm.messages[0]);
			p.setBusy(false);
		}
	}

	enum GnomeMix {
		CHEESE_ON_BATTA(ItemId.GNOMEBATTA.id(), ItemId.CHEESE.id(), "cheese_on_batta",
				"you crumble the cheese over the gnome batta"),
		TOMATO_ON_BATTA(ItemId.GNOMEBATTA.id(), ItemId.TOMATO.id(), "tomato_on_batta", 
				"you add the tomato to the gnome batta"),
		SPRINKLE_LEAVE_ON_BATTA(ItemId.GNOMEBATTA.id(), ItemId.EQUA_LEAVES.id(), "leaves_on_batta", 
				"you sprinkle the equa leaves over the gnome batta"),
		CHOCOLATE_ON_BOWL(ItemId.GNOMEBOWL.id(), ItemId.CHOCOLATE_BAR.id(), "chocolate_on_bowl", 
				"you add the chocolate to the dough bowl"),
		LEAVES_ON_BOWL(ItemId.GNOMEBOWL.id(), ItemId.EQUA_LEAVES.id(), "leaves_on_bowl", 
				"you add the equa leaves to the dough bowl"),
		CREAM_ON_BOWL(ItemId.GNOMEBOWL.id(), ItemId.CREAM.id(), "cream_on_bowl", 
				"you pour thick cream over the gnome bowl"),
		SPRINKLE_CHOCO_DUST_ON_BOWL(ItemId.GNOMEBOWL.id(), ItemId.CHOCOLATE_DUST.id(), "choco_dust_on_bowl", 
				"you sprinkle the chocolate dust over the gnome bowl"),
		AQUA_LEAVES_ON_TOADLEGS(ItemId.TOAD_LEGS.id(), ItemId.EQUA_LEAVES.id(), "aqua_toad_legs", 
				"you mix the equa leaves with your toads legs"),
		SPRINKLE_SPICE_ON_TOADLEGS(ItemId.TOAD_LEGS.id(), ItemId.GNOME_SPICE.id(), "gnomespice_toad_legs", 
				"you sprinkle the spice over the toads legs"),
		SEASONED_TOADLEGS_ON_BATTA(ItemId.GNOMEBATTA.id(), ItemId.TOAD_LEGS.id(), "toadlegs_on_batta", 
				"you add the toads legs to the gnome batta"),
		KINGWORMS_ON_BOWL(ItemId.GNOMEBOWL.id(), ItemId.KING_WORM.id(), "kingworms_on_bowl", 
				"you add the worm to the dough bowl"),
		ONIONS_ON_BOWL(ItemId.GNOMEBOWL.id(), ItemId.ONION.id(), "onions_on_bowl", 
				"you add the onion to the dough bowl"),
		GNOMESPICE_ON_BOWL(ItemId.GNOMEBOWL.id(), ItemId.GNOME_SPICE.id(), "gnomespice_on_bowl", 
				"you sprinkle some gnome spice over the dough bowl"),
		SPRINKLE_SPICE_ON_DOUGH(ItemId.GIANNE_DOUGH.id(), ItemId.GNOME_SPICE.id(), "gnomespice_on_dough", 
				"you sprinkle the spice into the dough"),
		TOADLEGS_ON_DOUGH(ItemId.GIANNE_DOUGH.id(), ItemId.TOAD_LEGS.id(), "toadlegs_on_dough", 
				"you mix the toad's legs into the dough"),
		SPRINKLE_LEAVE_ON_CRUNCHIES(ItemId.GNOMECRUNCHIE.id(), ItemId.EQUA_LEAVES.id(), "leaves_on_crunchies", 
				"you sprinkle some leaves over the crunchies"),
		GNOMESPICE_ON_WORM(ItemId.KING_WORM.id(), ItemId.GNOME_SPICE.id(), "gnomespice_on_worm", 
				"you sprinkle some gnome spice over your worm"),
		KINGWORM_ON_BATTA(ItemId.GNOMEBATTA.id(), ItemId.KING_WORM.id(), "worm_on_batta", 
				"you add the king worms to the gnome batta"),
		ONION_ON_BATTA(ItemId.GNOMEBATTA.id(), ItemId.ONION.id(), "onion_on_batta", 
				"you add the onion to the gnome batta"),
		CABBAGE_ON_BATTA(ItemId.GNOMEBATTA.id(), ItemId.CABBAGE.id(), "cabbage_on_batta", 
				"you add the Cabbage to the gnome batta"),
		DWELLBERRIES_ON_BATTA(ItemId.GNOMEBATTA.id(), ItemId.DWELLBERRIES.id(), "dwell_on_batta", 
				"you add the dwell berries to the gnome batta"),
		COCOLATE_ON_DOUGH(ItemId.GIANNE_DOUGH.id(), ItemId.CHOCOLATE_BAR.id(), "chocolate_on_dough", 
				"you crumble the chocolate into the dough"),
		CHOCO_DUST_ON_CRUNCHIES(ItemId.GNOMECRUNCHIE.id(), ItemId.CHOCOLATE_DUST.id(), "choco_dust_on_crunchies", 
				"you sprinkle the chocolate dust over the crunchie"),
		POTATOES_ON_BOWL(ItemId.GNOMEBOWL.id(), ItemId.POTATO.id(), "potato_on_bowl", 
				"you add the potato to the dough bowl"),
		TOADLEGS_ON_BOWL(ItemId.GNOMEBOWL.id(), ItemId.TOAD_LEGS.id(), "toadlegs_on_bowl", 
				"you add the taods legs to the gnome bowl"),
		ADD_CHEESE_TO_BOWL(ItemId.GNOMEBOWL.id(), ItemId.CHEESE.id(), "cheese_on_bowl", 
				"you add the cheese to the dough bowl"),
		DWELLBERRIES_ON_BOWL(ItemId.GNOMEBOWL.id(), ItemId.DWELLBERRIES.id(), "dwell_on_bowl", 
				"you add the dwell berries to the dough bowl"),
		LEAVES_ON_DOUGH(ItemId.GIANNE_DOUGH.id(), ItemId.EQUA_LEAVES.id(), "leaves_on_dough", 
				"you mix the equaleaves into the dough"),
		KINGWORMS_ON_DOUGH(ItemId.GIANNE_DOUGH.id(), ItemId.KING_WORM.id(), "kingworm_on_dough", 
				"you mix the worm into the dough"),
		SPRINKLE_SPICE_OVER_CRUNCHIES(ItemId.GNOMECRUNCHIE.id(), ItemId.GNOME_SPICE.id(), "spice_over_crunchies", 
				"you sprinkle some spice over the crunchies"),
		DICED_ORANGE_ON_BATTA(ItemId.GNOMEBATTA.id(), ItemId.DICED_ORANGE.id(), "diced_orange_on_batta", 
				"you sprinkle the orange chunks over the gnome batta"),
		LIME_ON_BATTA(ItemId.GNOMEBATTA.id(), ItemId.LIME_CHUNKS.id(), "lime_on_batta", 
				"you sprinkle the lime chunks over the gnome batta"),
		PINE_APPLE_ON_BATTA(ItemId.GNOMEBATTA.id(), ItemId.PINEAPPLE_CHUNKS.id(), "pine_apple_batta", 
				"you sprinkle the pineapple chunks over the gnome batta"),
		SPRINKLE_SPICE_OVER_BATTA(ItemId.GNOMEBATTA.id(), ItemId.GNOME_SPICE.id(), "spice_over_batta", 
				"you sprinkle the gnome spice over the gnome batta");

		private int itemID;
		private int itemIDOther;
		private String cacheName;
		private String[] messages;

		GnomeMix(int itemOne, int itemTwo, String cacheName, String... messages) {
			this.itemID = itemOne;
			this.itemIDOther = itemTwo;
			this.cacheName = cacheName;
			this.messages = messages;
		}

		public boolean isValid(World world, int i, int is) {
			return compareItemsIds(new Item(itemID), new Item(itemIDOther), i, is);
		}
	}
}
