package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class LeafyPalmTree implements ObjectActionListener, ObjectActionExecutiveListener {

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == 1176;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 1176) {
			message(p, 1300, "You give the palm tree a good shake.");
			message(p, 0, "A palm leaf falls down.");
			createGroundItem(ItemId.PALM_TREE_LEAF.id(), 1, obj.getX(), obj.getY(), p);
			replaceObjectDelayed(obj, 15000, 33);
		}
	}
}
