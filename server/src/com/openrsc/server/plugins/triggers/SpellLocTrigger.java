package com.openrsc.server.plugins.triggers;

import com.openrsc.server.external.SpellDef;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface SpellLocTrigger {
	void onSpellLoc(Player player, GameObject gameObject, SpellDef spell);
	boolean blockSpellLoc(Player player, GameObject gameObject, SpellDef spell);
}
