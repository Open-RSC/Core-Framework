package com.openrsc.server.event.custom;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;

public abstract class BatchEvent extends DelayedEvent {

	private long repeatFor;
	private int repeated;
	private boolean gathering;
	private boolean batchProgression;

	public BatchEvent(World world, Player owner, int delay, String descriptor, int repeatFor, boolean gathering) {
		super(world, owner, delay, descriptor);
		owner.resetPath();
		owner.setBusyTimer(delay + 200);
		this.gathering = gathering;
		this.batchProgression = getWorld().getServer().getConfig().BATCH_PROGRESSION;
		if (this.batchProgression) this.repeatFor = repeatFor;
		else if (repeatFor > 1000) this.repeatFor = repeatFor - 1000; // Mining default
		else this.repeatFor = 1; // Always 1, otherwise.

		if (this.batchProgression) ActionSender.sendProgressBar(owner, delay, repeatFor);
	}

	public BatchEvent(World world, Player owner, int delay, String descriptor, int repeatFor) {
		this(world, owner, delay, descriptor, repeatFor, true);
	}

	@Override
	public void run() {
		if (getOwner().hasMoved()) { // If the player walks away, stop batching
			getOwner().setStatus(Action.IDLE);
			interrupt();
			return;
		}
		if (repeated < repeatFor) {
			//owner.setBusyTimer(delay + 200); // This was locking the player until all batching completed
			action();
			if (++repeated >= repeatFor) {
				getOwner().setStatus(Action.IDLE);
				interrupt();
				return;
			}
			if (this.batchProgression) ActionSender.sendUpdateProgressBar(getOwner(), repeated);
			/*if (owner.getInventory().full() && gathering) { // this is a PITA to have to drop inventory items too keep going so Marwolf comments this out
				interrupt();
				if (getServer().getConfig().BATCH_PROGRESSION) owner.message("Your Inventory is too full to continue.");
			}*/
		}
	}

	public abstract void action();

	public boolean isCompleted() {
		return (repeated + 1) >= repeatFor || !super.running;
	}

	public void interrupt() {
		if (this.batchProgression) ActionSender.sendRemoveProgressBar(getOwner());
		getOwner().setBusyTimer(0);
		getOwner().setBatchEvent(null);
		super.stop();
	}

	protected long getRepeatFor() {
		return repeatFor;
	}

	public void setRepeatFor(int i) {
		repeatFor = i;
	}

	public int getRepeated() { return this.repeated; }
}
