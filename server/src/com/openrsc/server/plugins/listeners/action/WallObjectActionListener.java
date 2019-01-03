package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface WallObjectActionListener {

	public void onWallObjectAction(GameObject obj, Integer click, Player p);

}
