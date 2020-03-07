package com.openrsc.server.plugins.listeners;

import com.openrsc.server.external.SpellDef;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerMageObjectListener {
	void onPlayerMageObject(Player player, GameObject obj, SpellDef spell);
	boolean blockPlayerMageObject(Player player, GameObject obj, SpellDef spell);
}
