package com.openrsc.server.plugins.minigames.gnomerestaurant;

import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;

public class GnomeCooking implements InvActionListener, InvActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	private boolean canCook(Item item, GameObject object) {
		for (GnomeCook c : GnomeCook.values()) {
			if (item.getID() == c.uncookedID && inArray(object.getID(), 119)) {
				return true;
			}
		}
		return false;
	}

	private void handleGnomeCooking(final Item item, Player p, final GameObject object) {
		GnomeCook gc = null;
		for (GnomeCook c : GnomeCook.values()) {
			if (item.getID() == c.uncookedID && inArray(object.getID(), 119)) {
				gc = c;
			}
		}
		// NOTE: THERE ARE NO REQUIREMENT TO COOK THE DOUGH ONLY TO MOULD IT.
		p.setBusy(true);
		showBubble(p, item);
		p.playSound("cooking");
		if (p.getInventory().remove(item) > -1) {
			message(p, 3000, gc.messages[0]);
			if (!burnFood(p, gc.requiredLevel, p.getSkills().getLevel(SKILLS.COOKING.id()))) {
				if (gc.cookedID == ItemId.GNOMEBATTA.id()) {
					// stop tomato cheese batta when doing veg batta.
					if (p.getCache().hasKey("cheese_on_batta") && p.getCache().hasKey("tomato_on_batta") && !p.getCache().hasKey("onion_on_batta")) {
						p.getCache().store("tomato_cheese_batta", true);
						p.message(gc.messages[1]);
						p.incExp(SKILLS.COOKING.id(), gc.experience, true);
					} else if (p.getCache().hasKey("cheese_on_batta")
						&& p.getCache().hasKey("aqua_toad_legs")
						&& p.getCache().hasKey("gnomespice_toad_legs")
						&& p.getCache().hasKey("toadlegs_on_batta")) { // Makes toad batta
						p.message(gc.messages[1]);
						p.incExp(SKILLS.COOKING.id(), gc.experience, true);
						addItem(p, ItemId.TOAD_BATTA.id(), 1);
						resetGnomeCooking(p);
						return;
					} else if (p.getCache().hasKey("gnomespice_on_worm")
						&& p.getCache().hasKey("worm_on_batta")
						&& p.getCache().hasKey("cheese_on_batta")) { // makes worm batta
						p.getCache().store("worm_batta", true);
						p.message(gc.messages[1]);
					} else if (p.getCache().hasKey("onion_on_batta")
						&& p.getCache().hasKey("tomato_on_batta") && p.getCache().getInt("tomato_on_batta") >= 2
						&& p.getCache().hasKey("cabbage_on_batta")
						&& p.getCache().hasKey("dwell_on_batta")) { // makes veg batta
						if (!p.getCache().hasKey("cheese_on_batta")) {
							p.getCache().store("veg_batta_no_cheese", true);
						} else {
							p.getCache().store("veg_batta_with_cheese", true);
						}
						p.message(gc.messages[1]);
					} else if (p.getCache().hasKey("leaves_on_batta") && p.getCache().getInt("leaves_on_batta") >= 4) { // makes fruit batta
						p.getCache().store("batta_cooked_leaves", true);
						p.message(gc.messages[1]);
					}
				} else if (gc.cookedID == ItemId.GNOMEBOWL.id()) {
					p.message(gc.messages[1]);
					if (p.getCache().hasKey("chocolate_on_bowl") && p.getCache().getInt("chocolate_on_bowl") >= 4 && p.getCache().hasKey("leaves_on_bowl")) {
						p.getCache().store("chocolate_bomb", true);
					} else if (p.getCache().hasKey("kingworms_on_bowl") && p.getCache().getInt("kingworms_on_bowl") >= 6
						&& p.getCache().hasKey("onions_on_bowl") && p.getCache().getInt("onions_on_bowl") >= 2
						&& p.getCache().hasKey("gnomespice_on_bowl")) {
						p.getCache().store("wormhole", true);
						p.incExp(SKILLS.COOKING.id(), gc.experience, true);
					} else if (p.getCache().hasKey("onions_on_bowl") && p.getCache().getInt("onions_on_bowl") >= 2
						&& p.getCache().hasKey("potato_on_bowl") && p.getCache().getInt("potato_on_bowl") >= 2
						&& p.getCache().hasKey("gnomespice_on_bowl")) {
						p.getCache().store("vegball", true);
						p.incExp(SKILLS.COOKING.id(), gc.experience, true);
					} else if (p.getCache().hasKey("cheese_on_bowl") && p.getCache().getInt("cheese_on_bowl") >= 2
						&& p.getCache().hasKey("toadlegs_on_bowl") && p.getCache().getInt("toadlegs_on_bowl") >= 5
						&& p.getCache().hasKey("leaves_on_bowl") && p.getCache().getInt("leaves_on_bowl") >= 2
						&& p.getCache().hasKey("dwell_on_bowl")
						&& p.getCache().hasKey("gnomespice_on_bowl") && p.getCache().getInt("gnomespice_on_bowl") >= 2) { // tangled toads legs
						p.incExp(SKILLS.COOKING.id(), gc.experience, true);
						addItem(p, ItemId.TANGLED_TOADS_LEGS.id(), 1);
						resetGnomeCooking(p);
						return;
					}
				} else if (gc.cookedID == ItemId.GNOMECRUNCHIE.id()) {
					p.message(gc.messages[1]);
					if (!p.getCache().hasKey("gnome_crunchie_cooked")) {
						p.getCache().store("gnome_crunchie_cooked", true);
					}
				} else {
					p.message(gc.messages[1]);
					p.incExp(SKILLS.COOKING.id(), gc.experience, true);
				}
				addItem(p, gc.cookedID, 1);
			} else {
				p.getInventory().add(new Item(gc.burntID));
				p.message(gc.messages[2]);
			}
		}
		p.setBusy(false);
	}

	private boolean mouldDough(Item item, Player p) {
		if (hasItem(p, ItemId.GNOMEBATTA_DOUGH.id()) || hasItem(p, ItemId.GNOMEBOWL_DOUGH.id()) 
				|| hasItem(p, ItemId.GNOMECRUNCHIE_DOUGH.id()) || hasItem(p, ItemId.GNOMEBATTA.id()) 
				|| hasItem(p, ItemId.GNOMEBOWL.id()) || hasItem(p, ItemId.GNOMECRUNCHIE.id())) {
			message(p, "you need to finish, eat or drop the unfinished dish you hold");
			p.message("before you can make another - giannes rules");
			return false;
		}
		p.message("which shape would you like to mould");
		int menu = showMenu(p,
			"gnomebatta",
			"gnomebowl",
			"gnomecrunchie");
		if (menu != -1) {
			p.setOption(-1);
			p.setBusy(true);
			if (menu == 0) {
				if (p.getSkills().getLevel(SKILLS.COOKING.id()) < 25) {
					p.message("you need a cooking level of 25 to mould dough batta's");
					p.setBusy(false);
					return false;
				}
				showBubble(p, item);
				message(p, 3000, "you attempt to mould the dough into a gnomebatta");
				p.message("You manage to make some gnome batta dough");
				p.getInventory().replace(item.getID(), ItemId.GNOMEBATTA_DOUGH.id());
			} else if (menu == 1) {
				if (p.getSkills().getLevel(SKILLS.COOKING.id()) < 30) {
					p.message("you need a cooking level of 30 to mould dough bowls");
					p.setBusy(false);
					return false;
				}
				showBubble(p, item);
				message(p, 3000, "you attempt to mould the dough into a gnome bowl");
				p.message("You manage to make some gnome bowl dough");
				p.getInventory().replace(item.getID(), ItemId.GNOMEBOWL_DOUGH.id());
			} else if (menu == 2) {
				if (p.getSkills().getLevel(SKILLS.COOKING.id()) < 15) {
					p.message("you need a cooking level of 15 to mould crunchies");
					p.setBusy(false);
					return false;
				}
				showBubble(p, item);
				message(p, 3000, "you attempt to mould the dough into gnome crunchies");
				p.message("You manage to make some gnome crunchies dough");
				p.getInventory().replace(item.getID(), ItemId.GNOMECRUNCHIE_DOUGH.id());
				if (!p.getCache().hasKey("gnomecrunchie_dough")) {
					p.getCache().store("gnomecrunchie_dough", true);
				}
			}
			p.incExp(SKILLS.COOKING.id(), 100, true);
			p.setBusy(false);
		}
		return true;

	}

	@Override
	public void onInvAction(Item item, Player p) {
		if (item.getID() == ItemId.GIANNE_DOUGH.id()) {
			mouldDough(item, p);
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player p) {
		return item.getID() == ItemId.GIANNE_DOUGH.id();
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		return canCook(item, obj);
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		handleGnomeCooking(item, p, obj);
	}

	private boolean burnFood(Player p, int reqLvl, int myCookingLvl) {
		int levelDiff;
		if (p.getInventory().wielding(ItemId.GAUNTLETS_OF_COOKING.id()))
			levelDiff = (myCookingLvl += 10) - reqLvl;
		else
			levelDiff = myCookingLvl - reqLvl;
		if (levelDiff < 0) {
			return true;
		}
		if (levelDiff >= 20) {
			return false;
		}
		return DataConversions.random(0, levelDiff - DataConversions.random(0, levelDiff) + 1) == 0;
	}

	enum GnomeCook {
		GNOME_BATTA_DOUGH(ItemId.GNOMEBATTA_DOUGH.id(), ItemId.GNOMEBATTA.id(), ItemId.BURNT_GNOMEBATTA.id(), 120, 1,
			"You cook the gnome batta in the oven...",
			"You remove the gnome batta from the oven",
			"You accidentally burn the gnome batta"),

		GNOME_BOWL_DOUGH(ItemId.GNOMEBOWL_DOUGH.id(), ItemId.GNOMEBOWL.id(), ItemId.BURNT_GNOMEBOWL.id(), 120, 1,
			"You cook the gnome bowl in the oven...",
			"You remove the gnome bowl from the oven",
			"You accidentally burn the gnome bbowl"),

		GNOME_CRUNCHIE_DOUGH(ItemId.GNOMECRUNCHIE_DOUGH.id(), ItemId.GNOMECRUNCHIE.id(), ItemId.BURNT_GNOMECRUNCHIE.id(), 120, 1,
			"You cook the gnome crunchie in the oven...",
			"You remove the gnome crunchie from the oven",
			"You accidentally burn the gnome crunchie"),

		GNOME_BATTA_ALREADY_COOKED(ItemId.GNOMEBATTA.id(), ItemId.GNOMEBATTA.id(), ItemId.BURNT_GNOMEBATTA.id(), 120, 1,
			"You cook the gnome batta in the oven...",
			"You remove the gnome batta from the oven",
			"You accidentally burn the gnome batta"),

		GNOME_BOWL_ALREADY_COOKED(ItemId.GNOMEBOWL.id(), ItemId.GNOMEBOWL.id(), ItemId.BURNT_GNOMEBOWL.id(), 120, 1,
			"You cook the gnome bowl in the oven...",
			"You remove the gnome bowl from the oven",
			"You accidentally burn the gnome bbowl");

		private int uncookedID;
		private int cookedID;
		private int burntID;
		private int experience;
		private int requiredLevel;
		private String[] messages;

		GnomeCook(int uncookedID, int cookedID, int burntID, int experience, int reqlevel, String... cookingMessages) {
			this.uncookedID = uncookedID;
			this.cookedID = cookedID;
			this.burntID = burntID;
			this.experience = experience;
			this.requiredLevel = reqlevel;
			this.messages = cookingMessages;
		}
	}
}
