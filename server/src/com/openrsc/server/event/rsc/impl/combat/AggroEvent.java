package com.openrsc.server.event.rsc.impl.combat;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.impl.combat.scripts.CombatScriptLoader;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public class AggroEvent extends GameTickEvent {
	private final Npc npc;
	private final Player player;
	
	public AggroEvent(Npc npc, Player player) {
		super(null, 0);
		this.npc = npc;
		this.player = player;
		CombatScriptLoader.checkAndExecuteCombatAggroScript(npc, player);
		npc.setExecutedAggroScript(true);
	}

	@Override
	public void run() {
		setDelayTicks(2);
	}
}
