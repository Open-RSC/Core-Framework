package com.openrsc.server.model.action;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;

public abstract class WalkToAction {

	private Player player;
	private Point location;
	private volatile boolean executed;

	public WalkToAction(final Player player, final Point location) {
		this.player = player;
		this.location = location;
		this.executed = false;
	}

	public void execute() {
		executeInternal();
		finishExecution();
	}

	public void finishExecution() {
		setExecuted(true);
		player.setLastExecutedWalkToAction(this);
	}

	public final boolean shouldExecute() {
		return !isExecuted() && shouldExecuteInternal();
	}

	protected abstract void executeInternal();

	protected abstract boolean shouldExecuteInternal();

	public Player getPlayer() {
		return player;
	}

	public Point getLocation() {
		return location;
	}

	public synchronized boolean isExecuted() {
		return executed;
	}

	protected synchronized void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public boolean isPvPAttack() {
		return false;
	}
}
