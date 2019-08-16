package com.openrsc.server.event.rsc.impl.combat;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

public class AggroEvent extends GameTickEvent {
	private final Npc npc;
	private final Player player;
	
	public AggroEvent(World world, Npc npc, Player player) {
		super(world,null, 0, "Aggro Event");
		this.npc = npc;
		this.player = player;
		player.getWorld().getServer().getCombatScriptLoader().checkAndExecuteCombatAggroScript(npc, player);
		npc.setExecutedAggroScript(true);
	}

	@Override
	public void run() {
		setDelayTicks(2);
	}
}
