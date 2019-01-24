package com.openrsc.server.plugins.quests.members.digsite;

import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.DropListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class DigsiteMiscs implements DropListener, DropExecutiveListener {

	private static int UNINDENTIFIED_LIQUID = 1232;
	private static int MIXED_CHEMICALS = 1178;
	private static int MIXED_CHEMICALS2 = 1180;
	private static int NITROGLYCERIN = 1161;
	private static int EXPLOSIVE_COMPOUND = 1176;

	@Override
	public boolean blockDrop(Player p, Item i) {
		return i.getID() == UNINDENTIFIED_LIQUID || i.getID() == NITROGLYCERIN || i.getID() == MIXED_CHEMICALS || i.getID() == MIXED_CHEMICALS2 || i.getID() == EXPLOSIVE_COMPOUND;
	}

	@Override
	public void onDrop(Player p, Item i) {
		if (i.getID() == UNINDENTIFIED_LIQUID) {
			p.message("bang!");
			removeItem(p, UNINDENTIFIED_LIQUID, 1);
			p.damage((int) (getCurrentLevel(p, Skills.HITPOINTS) * 0.3D + 5));
			playerTalk(p, null, "Ow!");
			p.message("The liquid exploded!");
			p.message("You were injured by the burning liquid");
		}
		if (i.getID() == MIXED_CHEMICALS || i.getID() == MIXED_CHEMICALS2) {
			p.message("bang!");
			removeItem(p, i.getID(), 1);
			p.damage((int) (getCurrentLevel(p, Skills.HITPOINTS) / 2 + 6));
			playerTalk(p, null, "Ow!");
			p.message("The chemicals exploded!");
			p.message("You were injured by the exploding liquid");
		}
		if (i.getID() == NITROGLYCERIN) {
			p.message("bang!");
			removeItem(p, NITROGLYCERIN, 1);
			p.damage((int) (getCurrentLevel(p, Skills.HITPOINTS) / 2 - 3));
			playerTalk(p, null, "Ow!");
			p.message("The nitroglycerin exploded!");
			p.message("You were injured by the exploding liquid");
		}
		if (i.getID() == EXPLOSIVE_COMPOUND) {
			message(p, "bang!");
			removeItem(p, EXPLOSIVE_COMPOUND, 1);
			p.damage(61);
			playerTalk(p, null, "Ow!");
			p.message("The compound exploded!");
			p.message("You were badly injured by the exploding liquid");
		}
	}
}
