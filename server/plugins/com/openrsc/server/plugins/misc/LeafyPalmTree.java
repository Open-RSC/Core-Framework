package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class LeafyPalmTree implements OpLocTrigger {

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return obj.getID() == 1176;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == 1176) {
			mes(p, 1300, "You give the palm tree a good shake.");
			mes(p, 0, "A palm leaf falls down.");
			addobject(ItemId.PALM_TREE_LEAF.id(), 1, obj.getX(), obj.getY(), p);
			Functions.changeloc(obj, 15000, 33);
		}
	}
}
