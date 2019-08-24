package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.sleep;

public class DeadTree implements ObjectActionListener, ObjectActionExecutiveListener {

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 88;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0,"Dead Tree") {
			public void init() {
				addState(0, () -> {
					getPlayerOwner().setBusy(true);
					getPlayerOwner().message("The tree seems to lash out at you!");
					return nextState(1);
				});
				addState(1, () -> {
					getPlayerOwner().damage((int) (getPlayerOwner().getSkills().getLevel(Skills.HITS) * 0.2D));
					getPlayerOwner().message("You are badly scratched by the tree");
					getPlayerOwner().setBusy(false);
					return null;
				});
			}
		});
	}
}
