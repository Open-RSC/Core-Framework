package com.openrsc.server.plugins.quests.members.digsite;

import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.DropListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;

public class DigsiteMiscs implements DropListener, DropExecutiveListener {

	@Override
	public boolean blockDrop(Player p, Item i) {
		return DataConversions.inArray(new int[] {ItemId.UNIDENTIFIED_LIQUID.id(), ItemId.NITROGLYCERIN.id(),
				ItemId.MIXED_CHEMICALS_1.id(), ItemId.MIXED_CHEMICALS_2.id(), ItemId.EXPLOSIVE_COMPOUND.id()}, i.getID());
	}

	@Override
	public void onDrop(Player p, Item i) {
		if (i.getID() == ItemId.UNIDENTIFIED_LIQUID.id()) {
			p.message("bang!");
			removeItem(p, ItemId.UNIDENTIFIED_LIQUID.id(), 1);
			p.damage((int) (getCurrentLevel(p, SKILLS.HITS.id()) * 0.3D + 5));
			playerTalk(p, null, "Ow!");
			p.message("The liquid exploded!");
			p.message("You were injured by the burning liquid");
		}
		else if (i.getID() == ItemId.MIXED_CHEMICALS_1.id() || i.getID() == ItemId.MIXED_CHEMICALS_2.id()) {
			p.message("bang!");
			removeItem(p, i.getID(), 1);
			p.damage((int) (getCurrentLevel(p, SKILLS.HITS.id()) / 2 + 6));
			playerTalk(p, null, "Ow!");
			p.message("The chemicals exploded!");
			p.message("You were injured by the exploding liquid");
		}
		else if (i.getID() == ItemId.NITROGLYCERIN.id()) {
			p.message("bang!");
			removeItem(p, ItemId.NITROGLYCERIN.id(), 1);
			p.damage((int) (getCurrentLevel(p, SKILLS.HITS.id()) / 2 - 3));
			playerTalk(p, null, "Ow!");
			p.message("The nitroglycerin exploded!");
			p.message("You were injured by the exploding liquid");
		}
		else if (i.getID() == ItemId.EXPLOSIVE_COMPOUND.id()) {
			message(p, "bang!");
			removeItem(p, ItemId.EXPLOSIVE_COMPOUND.id(), 1);
			p.damage(61);
			playerTalk(p, null, "Ow!");
			p.message("The compound exploded!");
			p.message("You were badly injured by the exploding liquid");
		}
	}
}
