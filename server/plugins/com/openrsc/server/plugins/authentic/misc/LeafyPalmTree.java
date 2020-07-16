package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class LeafyPalmTree implements OpLocTrigger {

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 1176;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 1176) {
			mes("You give the palm tree a good shake.");
			delay(2);
			mes("A palm leaf falls down.");
			delay();
			addobject(ItemId.PALM_TREE_LEAF.id(), 1, obj.getX(), obj.getY(), player);
			changeloc(obj, 15000, 33);
		}
	}
}
