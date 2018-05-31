package org.rscemulation.server.event;

import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Path;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;
import org.rscemulation.server.states.Action;

public class NpcAggressionEvent extends DelayedEvent {
	private Npc npc;
	
	public Npc getNpc() {
		return npc;
	}
	
	public NpcAggressionEvent(Player player, Npc npc) {
		super(player, 600);
		this.npc = npc;
	}

	public void run() {
		if (npc.isBusy() || npc == null || owner == null || (owner.isBusy() && owner.getStatus() != Action.EATING && owner.getStatus() != Action.DRINKING) || owner.isRemoved()) {
			npc.resetAggression();
			super.running = false;
		} else {
			if (System.currentTimeMillis() - npc.getCombatTimer() > 3000 && !npc.isRemoved()) {
				if (npc.findUniqueVictim(owner) && !npc.isFighting())
				{
					FightEvent fe = new FightEvent(owner, npc, true);
					npc.setFightEvent(fe);
					owner.setFightEvent(fe);
					World.getDelayedEventHandler().add(fe);			
				}
				else {
					if (playerWithinRange())
						npc.setPath(new Path(npc.getX(), npc.getY(), owner.getX(), owner.getY()));
					else {
						npc.resetAggression();
						super.running = false;
					}
				}
			}
		}
	}
	
	private boolean playerWithinRange() {
		return owner.getLocation().inBounds(npc.getLoc().minX, npc.getLoc().minY, npc.getLoc().maxX, npc.getLoc().maxY);
	}
}