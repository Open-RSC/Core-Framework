package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.external.SpellDef;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerMageObjectExecutiveListener {
	public boolean blockPlayerMageObject(Player player, GameObject obj, SpellDef spell);
}

