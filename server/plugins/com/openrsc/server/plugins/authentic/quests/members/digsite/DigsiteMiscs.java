package com.openrsc.server.plugins.authentic.quests.members.digsite;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.DropObjTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class DigsiteMiscs implements DropObjTrigger {

	@Override
	public boolean blockDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		return DataConversions.inArray(new int[] {ItemId.UNIDENTIFIED_LIQUID.id(), ItemId.NITROGLYCERIN.id(),
				ItemId.MIXED_CHEMICALS_1.id(), ItemId.MIXED_CHEMICALS_2.id(), ItemId.EXPLOSIVE_COMPOUND.id()}, item.getCatalogId());
	}

	@Override
	public void onDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		if (item.getCatalogId() == ItemId.UNIDENTIFIED_LIQUID.id()) {
			player.message("bang!");
			player.getCarriedItems().remove(new Item(ItemId.UNIDENTIFIED_LIQUID.id()));
			player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) * 0.3D + 5));
			say(player, null, "Ow!");
			player.message("The liquid exploded!");
			player.message("You were injured by the burning liquid");
		}
		else if (item.getCatalogId() == ItemId.MIXED_CHEMICALS_1.id() || item.getCatalogId() == ItemId.MIXED_CHEMICALS_2.id()) {
			player.message("bang!");
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
			player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) / 2 + 6));
			say(player, null, "Ow!");
			player.message("The chemicals exploded!");
			player.message("You were injured by the exploding liquid");
		}
		else if (item.getCatalogId() == ItemId.NITROGLYCERIN.id()) {
			player.message("bang!");
			player.getCarriedItems().remove(new Item(ItemId.NITROGLYCERIN.id()));
			player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) / 2 - 3));
			say(player, null, "Ow!");
			player.message("The nitroglycerin exploded!");
			player.message("You were injured by the exploding liquid");
		}
		else if (item.getCatalogId() == ItemId.EXPLOSIVE_COMPOUND.id()) {
			mes("bang!");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.EXPLOSIVE_COMPOUND.id()));
			player.damage(61);
			say(player, null, "Ow!");
			player.message("The compound exploded!");
			player.message("You were badly injured by the exploding liquid");
		}
	}
}
