package com.openrsc.server.plugins;

import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.npc.NpcInteraction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;

public class Batch {

	private Player player;
	private int current;
	private int totalBatch;
	private int delay;
	private boolean showingBar = false;
	private boolean completed;
	private Point location;

	/**
	 * Creates a new instance of a Batch bar.
	 * @param player The player the bar belongs to
	 */
	public Batch(Player player) {
		this.player = player;
		this.location = player.getLocation();
	}

	/**
	 * Creates a new batch bar. Call start() to send to client
	 * @param totalBatch The total repetitions of a task
	 */
	public void initialize(int totalBatch) {
		this.current = 0;
		this.delay = getPlayer().getConfig().GAME_TICK * 3;
		this.totalBatch = totalBatch;
		this.completed = false;
	}

	/**
	 * Displays the batch bar to the client
	 */
	public void start() {
		if (wantBatching() && getTotalBatch() > 1) {
			ActionSender.sendProgressBar(getPlayer(), getDelay(), getTotalBatch());
			this.showingBar = true;
		}
	}

	/**
	 * Stops displaying the batch bar to the client.
	 * Gives it 3 ticks to close
	 */
	public void stop() {
		if (wantBatching() && isShowingBar()) {
			getPlayer().getWorld().getServer().getGameEventHandler().add(
				new SingleEvent(getPlayer().getWorld(), null, getDelay(), "Close Batch Bar") {
					@Override
					public void action() {
						ActionSender.sendRemoveProgressBar(getPlayer());
					}
				}
			);
			this.showingBar = false;
		}
		this.completed = true;
	}

	/**
	 * Increments the current batch's progress by 1.
	 * @return Returns false when the batch is complete
	 */
	public void update() {
		int xDiff = Math.abs(this.location.getX() - getPlayer().getLocation().getX());
		int yDiff = Math.abs(this.location.getY() - getPlayer().getLocation().getY());
		/*
		Because some actions (like thieving) can take place one extra tile away from their target before the player gets close,
		we will give them one tile worth of wiggle room on the first increment before we cancel their batch.
		*/
		if (getPlayer().getNpcInteraction() == NpcInteraction.NPC_OP && current == 0 && xDiff <= 1 && yDiff <= 1) {
			this.location = getPlayer().getLocation();
		}
		if (!getPlayer().getLocation().equals(this.location)) {
			stop();
			return;
		}
		incrementBatch();
		if (wantBatching() && isShowingBar()) ActionSender.sendUpdateProgressBar(getPlayer(), getCurrentBatchProgress());
		if (getCurrentBatchProgress() == getTotalBatch()) {
			stop();
		}
	}

	public Player getPlayer() { return player; }
	private int getDelay() { return delay; }
	private int getTotalBatch() { return totalBatch; }
	private void incrementBatch() { current++; }
	private int getCurrentBatchProgress() { return current; }
	private boolean wantBatching() { return getPlayer().getConfig().BATCH_PROGRESSION; }
	public boolean isFirstInBatch() { return current == 0; }
	public boolean isShowingBar() { return showingBar; }
	public boolean isComplete() { return completed; }

	public void setLocation(Point location) {
		this.location = location;
	}
}
