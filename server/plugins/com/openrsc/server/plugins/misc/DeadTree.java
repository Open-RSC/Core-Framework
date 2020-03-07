package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.ObjectActionListener;

public class DeadTree implements ObjectActionListener {

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 88;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		player.setBusy(true);
		player.message("The tree seems to lash out at you!");
		Functions.sleep(640);
		player.damage((int) (player.getSkills().getLevel(Skills.HITS) * 0.2D));
		player.message("You are badly scratched by the tree");
		player.setBusy(false);
	}
}
