package com.openrsc.server.event.rsc;

import java.util.Vector;

public class GameNotifyEvent {

	private final GameStateEvent parentEvent;
	private final Vector<Object> returnValues = new Vector<>();
	private int returnState;
	private int returnDelay;
	private boolean triggered = false;

	public GameNotifyEvent(GameStateEvent parent) {
		this.parentEvent = parent;
		this.getParentEvent().setNotifyEvent(this);
	}

	public void setTriggered(boolean val) {
		triggered = val;
	}

	public void restoreParent() {
		getParentEvent().setState(getReturnState());
		getParentEvent().setDelayTicks(getReturnDelay());
	}

	public boolean isTriggered() { return this.triggered; }

	public void addReturn(Object item) {
		returnValues.add(item);
	}

	public Object getReturnValue(int index) { return returnValues.get(index); }

	public boolean hasReturnValues() { return !returnValues.isEmpty(); }

	public int getReturnState() {
		return returnState;
	}

	public void setReturnState(int returnState) {
		this.returnState = returnState;
	}

	public int getReturnDelay() {
		return returnDelay;
	}

	public void setReturnDelay(int returnDelay) {
		this.returnDelay = returnDelay;
	}

	public GameStateEvent getParentEvent() {
		return parentEvent;
	}
}
