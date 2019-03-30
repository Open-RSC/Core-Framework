package com.openrsc.server.event.custom;

import com.openrsc.server.Constants;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;

public abstract class BatchEvent extends DelayedEvent {

	private long repeatFor;
	private long repeated;

	public BatchEvent(Player owner, int delay, int repeatFor) {
		super(owner, delay);
		if (Constants.GameServer.BATCH_PROGRESSION) this.repeatFor = repeatFor;
		else if (repeatFor > 1000) this.repeatFor = repeatFor - 1000; // Mining default
		else this.repeatFor = 1; // Always 1, otherwise.
		ActionSender.sendProgressBar(owner, delay, repeatFor);
		owner.setBusyTimer(delay + 200);
	}

	@Override
	public void run() {
		if (repeated < getRepeatFor()) {
			//owner.setBusyTimer(delay + 200); // This was locking the player until all batching completed
			action();
			repeated++;
			if (repeated < getRepeatFor()) {
				ActionSender.sendProgress(owner, repeated);
			} else {
				interrupt();
			}
			if (owner.getInventory().full()) {
				interrupt();
				if (Constants.GameServer.BATCH_PROGRESSION) owner.message("Your Inventory is too full to continue.");
			}
			if (owner.hasMoved()) { // If the player walks away, stop batching
				//this.stop();
				//owner.setStatus(Action.IDLE);
				interrupt();
			}
		}
	}

	public abstract void action();

	public boolean isCompleted() {
		return (repeated + 1) >= getRepeatFor() || !matchRunning;
	}

	public void interrupt() {
		ActionSender.sendRemoveProgressBar(owner);
		owner.setBusyTimer(0);
		owner.setBatchEvent(null);
		matchRunning = false;
	}

	protected long getRepeatFor() {
		return repeatFor;
	}

	public void setRepeatFor(int i) {
		repeatFor = i;
	}
}
