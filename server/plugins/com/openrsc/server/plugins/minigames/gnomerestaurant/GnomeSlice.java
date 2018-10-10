package com.openrsc.server.plugins.minigames.gnomerestaurant;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeSlice implements InvUseOnItemListener, InvUseOnItemExecutiveListener {

	public final int KNIFE = 13;
	public final int LIME = 863;
	public final int ORANGE = 857;
	public final int ORANGE_DICE = 859;
	public final int ORANGE_SLICE = 858;
	public final int LIME_SLICE = 865;
	public final int LIME_DICE = 864;
	public final int PINE_APPLE = 861;
	public final int PINE_APPLE_RINGS = 749;
	public final int PINE_APPLE_DICE = 862;
	public final int LEMON = 855;
	public final int LEMON_SLICE = 856;
	public final int LEMON_DICE = 860;
	@Override
	public boolean blockInvUseOnItem(Player p, Item item1, Item item2) {
		if(item1.getID() == KNIFE && item2.getID() == ORANGE || item2.getID() == KNIFE && item1.getID() == ORANGE) {
			return true;
		}
		if(item1.getID() == KNIFE && item2.getID() == LIME || item2.getID() == KNIFE && item1.getID() == LIME) {
			return true;
		}
		if(item1.getID() == KNIFE && item2.getID() == PINE_APPLE || item2.getID() == KNIFE && item1.getID() == PINE_APPLE) {
			return true;
		}
		if(item1.getID() == KNIFE && item2.getID() == LEMON || item2.getID() == KNIFE && item1.getID() == LEMON) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if(item1.getID() == KNIFE && item2.getID() == ORANGE || item2.getID() == KNIFE && item1.getID() == ORANGE) {
			p.message("you can slice or dice the orange");
			int menu = showMenu(p,
					"slice orange",
					"dice orange");
			if(menu == 0) {
				p.message("you slice the orange");
				p.getInventory().replace(ORANGE, ORANGE_SLICE);
			} else if(menu == 1) {
				p.message("you cut the orange into chunks");
				p.getInventory().replace(ORANGE, ORANGE_DICE);
			}
		}
		if(item1.getID() == KNIFE && item2.getID() == LIME || item2.getID() == KNIFE && item1.getID() == LIME) {
			p.message("you can slice or dice the lime");
			int menu = showMenu(p,
					"slice lime",
					"dice lime");
			if(menu == 0) {
				p.message("you slice the lime");
				p.getInventory().replace(LIME, LIME_SLICE);
			} else if(menu == 1) {
				p.message("you cut the lime into chunks");
				p.getInventory().replace(LIME, LIME_DICE);
			}
		}
		if(item1.getID() == KNIFE && item2.getID() == PINE_APPLE || item2.getID() == KNIFE && item1.getID() == PINE_APPLE) {
			p.message("you can slice or dice the pineapple");
			int menu = showMenu(p,
					"slice pineapple",
					"dice pineapple");
			if(menu == 0) {
				p.message("you slice the pineapple into rings");
				p.getInventory().replace(PINE_APPLE, PINE_APPLE_RINGS);
				addItem(p, PINE_APPLE_RINGS, 3);
			} else if(menu == 1) {
				p.message("you cut the pineapple into chunks");
				p.getInventory().replace(PINE_APPLE, PINE_APPLE_DICE);
			}
		}
		if(item1.getID() == KNIFE && item2.getID() == LEMON || item2.getID() == KNIFE && item1.getID() == LEMON) {
			p.message("you can slice or dice the lemon");
			int menu = showMenu(p,
					"slice lemon",
					"dice lemon");
			if(menu == 0) {
				p.message("you slice the lemon");
				p.getInventory().replace(LEMON, LEMON_SLICE);
			} else if(menu == 1) {
				p.message("you cut the lemon into chunks");
				p.getInventory().replace(LEMON, LEMON_DICE);
			}
		}
	}
}
