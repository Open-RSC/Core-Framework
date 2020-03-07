package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface WallObjectActionListener {
	void onWallObjectAction(GameObject obj, Integer click, Player p);
	boolean blockWallObjectAction(GameObject obj, Integer click, Player player);
}
