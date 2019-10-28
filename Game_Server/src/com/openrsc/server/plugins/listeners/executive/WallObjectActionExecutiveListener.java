package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface WallObjectActionExecutiveListener {

	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player);

}
