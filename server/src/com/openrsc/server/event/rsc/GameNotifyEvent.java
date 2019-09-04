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

	public void addObjectOut(String name, Object item) {
		outObjects.put(name, item);
	}

	public void addObjectIn(String name, Object item) {
		inObjects.put(name, item);
	}

	public Object getObjectOut(String name) {
		return this.outObjects.get(name);
	}

	public Object getObjectIn(String name) {
		return this.inObjects.get(name);
	}

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
