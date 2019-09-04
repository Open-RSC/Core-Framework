package com.openrsc.server.event.rsc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameNotifyEvent {

	private GameStateEvent parentEvent;
	private final Map<String, Object> inObjects = new ConcurrentHashMap<>();
	private final Map<String, Object> outObjects = new ConcurrentHashMap<>();
	private int returnState;
	private int returnDelay;
	private boolean triggered = false;

	public void setParentEvent(GameStateEvent event) {
		this.parentEvent = event;
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
		getReturnValues().add(item);
	}

	public Vector<Object> getReturnValues() { return new Vector<Object> (returnValues); }

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
