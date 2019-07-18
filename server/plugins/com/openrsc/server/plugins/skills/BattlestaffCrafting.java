package com.openrsc.server.plugins.skills;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.removeItem;

public class BattlestaffCrafting implements InvUseOnItemListener,
	InvUseOnItemExecutiveListener {

	private boolean canCraft(Item itemOne, Item itemTwo) {
		for (Battlestaff c : Battlestaff.values()) {
			if (c.isValid(itemOne.getID(), itemTwo.getID())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		Battlestaff combine = null;
		for (Battlestaff c : Battlestaff.values()) {
			if (c.isValid(item1.getID(), item2.getID())) {
				combine = c;
			}
		}
		if (p.getSkills().getLevel(SKILLS.CRAFTING.id()) < combine.requiredLevel) {
			p.message("You need a crafting level of " + combine.requiredLevel + " to make " + resultItemString(combine));
			return;
		}
		if (removeItem(p, combine.itemID, 1) && removeItem(p, combine.itemIDOther, 1)) {
			if (combine.messages.length > 1)
				message(p, combine.messages[0]);
			else
				p.message(combine.messages[0]);

			addItem(p, combine.resultItem, 1);
			p.incExp(SKILLS.CRAFTING.id(), combine.experience, true);

			if (combine.messages.length > 1)
				p.message(combine.messages[1]);
		}
	}

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		return canCraft(item1, item2);
	}

	private String resultItemString(Battlestaff combinedItem) {
		String name;
		switch (combinedItem) {
			case WATER_BATTLESTAFF:
				name = "a water battlestaff";
				break;
			case EARTH_BATTLESTAFF:
				// kosher: didn't say "an earth"
				name = "a earth battlestaff";
				break;
			case FIRE_BATTLESTAFF:
				name = "a fire battlestaff";
				break;
			case AIR_BATTLESTAFF:
				name = "an air battlestaff";
				break;
			default:
				// unimplemented battlestaff or not known
				name = "this";
		}
		return name;
	}

	enum Battlestaff {
		WATER_BATTLESTAFF(ItemId.BATTLESTAFF.id(), ItemId.WATER_ORB.id(), ItemId.BATTLESTAFF_OF_WATER.id(), 400, 54, ""),
		EARTH_BATTLESTAFF(ItemId.BATTLESTAFF.id(), ItemId.EARTH_ORB.id(), ItemId.BATTLESTAFF_OF_EARTH.id(), 450, 58, ""),
		FIRE_BATTLESTAFF(ItemId.BATTLESTAFF.id(), ItemId.FIRE_ORB.id(), ItemId.BATTLESTAFF_OF_FIRE.id(), 500, 62, ""),
		AIR_BATTLESTAFF(ItemId.BATTLESTAFF.id(), ItemId.AIR_ORB.id(), ItemId.BATTLESTAFF_OF_AIR.id(), 550, 66, "");

		private int itemID;
		private int itemIDOther;
		private int resultItem;
		private int experience;
		private int requiredLevel;
		private String[] messages;

		Battlestaff(int itemOne, int itemTwo, int resultItem, int experience, int level, String... messages) {
			this.itemID = itemOne;
			this.itemIDOther = itemTwo;
			this.resultItem = resultItem;
			this.experience = experience;
			this.requiredLevel = level;
			this.messages = messages;
		}

		public boolean isValid(int i, int is) {
			return itemID == i && itemIDOther == is || itemIDOther == i && itemID == is;
		}
	}
}
