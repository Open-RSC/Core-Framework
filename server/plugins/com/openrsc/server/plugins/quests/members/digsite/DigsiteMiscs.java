package com.openrsc.server.plugins.quests.members.digsite;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.DropListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class DigsiteMiscs implements DropListener {

	@Override
	public boolean blockDrop(Player p, Item i, Boolean fromInventory) {
		return DataConversions.inArray(new int[] {ItemId.UNIDENTIFIED_LIQUID.id(), ItemId.NITROGLYCERIN.id(),
				ItemId.MIXED_CHEMICALS_1.id(), ItemId.MIXED_CHEMICALS_2.id(), ItemId.EXPLOSIVE_COMPOUND.id()}, i.getCatalogId());
	}

	@Override
	public void onDrop(Player p, Item i, Boolean fromInventory) {
		if (i.getCatalogId() == ItemId.UNIDENTIFIED_LIQUID.id()) {
			p.message("bang!");
			removeItem(p, ItemId.UNIDENTIFIED_LIQUID.id(), 1);
			p.damage((int) (getCurrentLevel(p, Skills.HITS) * 0.3D + 5));
			playerTalk(p, null, "Ow!");
			p.message("The liquid exploded!");
			p.message("You were injured by the burning liquid");
		}
		else if (i.getCatalogId() == ItemId.MIXED_CHEMICALS_1.id() || i.getCatalogId() == ItemId.MIXED_CHEMICALS_2.id()) {
			p.message("bang!");
			removeItem(p, i.getCatalogId(), 1);
			p.damage((int) (getCurrentLevel(p, Skills.HITS) / 2 + 6));
			playerTalk(p, null, "Ow!");
			p.message("The chemicals exploded!");
			p.message("You were injured by the exploding liquid");
		}
		else if (i.getCatalogId() == ItemId.NITROGLYCERIN.id()) {
			p.message("bang!");
			removeItem(p, ItemId.NITROGLYCERIN.id(), 1);
			p.damage((int) (getCurrentLevel(p, Skills.HITS) / 2 - 3));
			playerTalk(p, null, "Ow!");
			p.message("The nitroglycerin exploded!");
			p.message("You were injured by the exploding liquid");
		}
		else if (i.getCatalogId() == ItemId.EXPLOSIVE_COMPOUND.id()) {
			message(p, "bang!");
			removeItem(p, ItemId.EXPLOSIVE_COMPOUND.id(), 1);
			p.damage(61);
			playerTalk(p, null, "Ow!");
			p.message("The compound exploded!");
			p.message("You were badly injured by the exploding liquid");
		}
	}
}
