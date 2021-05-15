package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.delay;

public class DeadTree implements OpLocTrigger {

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 88;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		player.message("The tree seems to lash out at you!");
		delay();
		player.damage((int) (player.getSkills().getLevel(Skill.HITS.id()) * 0.2D));
		player.message("You are badly scratched by the tree");
	}
}
