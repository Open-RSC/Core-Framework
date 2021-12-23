package com.openrsc.server.event.rsc.impl.combat;

import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.world.World;

public class AggroEvent extends GameTickEvent {
	private final Npc npc;
	private final Mob mob;

	public AggroEvent(World world, Npc npc, Mob mob) {
		super(world,null, 0, "Aggro Event", DuplicationStrategy.ALLOW_MULTIPLE);
		this.npc = npc;
		this.mob = mob;
		mob.getWorld().getServer().getCombatScriptLoader().checkAndExecuteCombatAggroScript(npc, mob);
		npc.setExecutedAggroScript(true);
	}

	@Override
	public void run() {
		/*setDelayTicks(2);*/
	}
}
