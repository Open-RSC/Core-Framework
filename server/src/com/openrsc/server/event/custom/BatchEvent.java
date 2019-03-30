package com.openrsc.server.event.custom;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;

import static com.openrsc.server.Constants.GameServer.BATCH_PROGRESSION;

public abstract class BatchEvent extends DelayedEvent {

	private long repeatFor;
	private long repeated;

	public BatchEvent(Player owner, int delay, int repeatFor) {
		super(owner, delay);
		if (BATCH_PROGRESSION) this.repeatFor = repeatFor;
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
				if (BATCH_PROGRESSION) owner.message("Your Inventory is too full to continue.");
			}
			if (BATCH_PROGRESSION && owner.hasMoved()) { // If the player walks away, stop batching
				interrupt();
			}
			if (BATCH_PROGRESSION && owner.getFatigue() == 100) { // If the player's fatigue is 100%, stop batching
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
