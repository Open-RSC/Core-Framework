package com.openrsc.server.event.rsc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameNotifyEvent {

	GameStateEvent parentEvent = null;
	public Vector<Object> returnValues = new Vector<>();
	int returnState;
	int returnDelay;
	boolean triggered = false;

	public GameNotifyEvent(GameStateEvent parent) {
		this.parentEvent = parent;
		this.parentEvent.setNotifyEvent(this);
	}

	public void setTriggered(boolean val) {
		triggered = val;
	}

	public void restoreParent() {
		parentEvent.setState(returnState);
		parentEvent.setDelayTicks(returnDelay);
	}

	public boolean getTriggered() { return this.triggered; }

	public void addReturn(Object item) {
		returnValues.add(item);
	}
}
