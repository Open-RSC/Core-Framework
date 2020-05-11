package com.openrsc.server.plugins;

import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;

public class BatchBar {

	private Player player;
	private int current;
	private int totalBatch;
	private int delay;

	/**
	 * Creates a new instance of a Batch bar.
	 * @param player The player the bar belongs to
	 */
	public BatchBar(Player player) {
		this.player = player;
	}

	/**
	 * Creates a new batch bar. Call start() to send to client
	 * @param delay The interval between actions.
	 * @param totalBatch The total repetitions of a task
	 */
	public void initialize(int delay, int totalBatch) {
		this.current = 0;
		this.delay = delay;
		this.totalBatch = totalBatch;
	}

	/**
	 * Displays the batch bar to the client
	 */
	public void start() {
		ActionSender.sendProgressBar(getPlayer(), getDelay(), getTotalBatch());
	}

	/**
	 * Stops displaying the batch bar to the client
	 */
	public void stop() {
		getPlayer().getWorld().getServer().getGameEventHandler().add(
			new SingleEvent(getPlayer().getWorld(), null, getDelay(), "Close Batch Bar") {
				@Override
				public void action() {
					ActionSender.sendRemoveProgressBar(getPlayer());
				}
			}
		);
	}

	/**
	 * Increments the current batch's progress by 1.
	 */
	public void update() {
		ActionSender.sendUpdateProgressBar(getPlayer(), ++current);
	}

	private Player getPlayer() { return player; }
	private int getDelay() { return delay; }
	private int getTotalBatch() { return totalBatch; }
}
