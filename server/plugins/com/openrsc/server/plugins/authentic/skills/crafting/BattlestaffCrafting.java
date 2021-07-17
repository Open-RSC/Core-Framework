package com.openrsc.server.plugins.authentic.skills.crafting;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class BattlestaffCrafting implements UseInvTrigger {

	private boolean canCraft(Item itemOne, Item itemTwo) {
		for (Battlestaff c : Battlestaff.values()) {
			if (c.isValid(itemOne.getCatalogId(), itemTwo.getCatalogId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		Battlestaff combine = null;
		for (Battlestaff c : Battlestaff.values()) {
			if (c.isValid(item1.getCatalogId(), item2.getCatalogId())) {
				combine = c;
			}
		}
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < combine.requiredLevel) {
			player.playerServerMessage(MessageType.QUEST, "You need a crafting level of " + combine.requiredLevel + " to make " + resultItemString(combine));
			return;
		}
		item1 = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(combine.itemID, Optional.of(false))
		);
		item2 = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(combine.itemIDOther, Optional.of(false))
		);
		if (item1 == null || item2 == null) return;

		player.getCarriedItems().remove(item1);
		player.getCarriedItems().remove(item2);
		delay();
		if (combine.messages.length > 1) {
			mes(combine.messages[0]);
			delay(3);
		}
		else {
			player.message(combine.messages[0]);
		}

		give(player, combine.resultItem, 1);
		player.incExp(Skill.CRAFTING.id(), combine.experience, true);

		if (combine.messages.length > 1) {
			player.message(combine.messages[1]);
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
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
