package com.openrsc.server.event.custom;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;

public abstract class BatchEvent extends DelayedEvent {

	private long repeatFor;
	private int repeated;
	private boolean gathering;

	public BatchEvent(World world, Player owner, int delay, String descriptor, int repeatFor, boolean gathering) {
		super(world, owner, delay, descriptor);
		this.gathering = gathering;
		if (getWorld().getServer().getConfig().BATCH_PROGRESSION) this.repeatFor = repeatFor;
		else if (repeatFor > 1000) this.repeatFor = repeatFor - 1000; // Mining default
		else this.repeatFor = 1; // Always 1, otherwise.
		ActionSender.sendProgressBar(owner, delay, repeatFor);
		owner.setBusyTimer(delay + 200);
	}
	
	public BatchEvent(World world, Player owner, int delay, String descriptor, int repeatFor) {
		this(world, owner, delay, descriptor, repeatFor, true);
	}

	@Override
	public void run() {
		if (repeated < getRepeatFor()) {
			//owner.setBusyTimer(delay + 200); // This was locking the player until all batching completed
			action();
			repeated++;
			if (repeated < getRepeatFor()) {
				ActionSender.sendUpdateProgressBar(getOwner(), repeated);
			} else {
				interrupt();
			}
			/*if (owner.getInventory().full() && gathering) { // this is a PITA to have to drop inventory items too keep going so Marwolf comments this out
				interrupt();
				if (getServer().getConfig().BATCH_PROGRESSION) owner.message("Your Inventory is too full to continue.");
			}*/
			if (getOwner().hasMoved()) { // If the player walks away, stop batching
				//this.stop();
				//owner.setStatus(Action.IDLE);
				interrupt();
			}
		}
	}

	public abstract void action();

	public boolean isCompleted() {
		return (repeated + 1) >= getRepeatFor() || !running;
	}

	public void interrupt() {
		ActionSender.sendRemoveProgressBar(getOwner());
		getOwner().setBusyTimer(0);
		getOwner().setBatchEvent(null);
		running = false;
	}

	protected long getRepeatFor() {
		return repeatFor;
	}

	public void setRepeatFor(int i) {
		repeatFor = i;
	}

	public int getRepeated() { return this.repeated; }
}
