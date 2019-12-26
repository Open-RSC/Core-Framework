package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GameNotifyEvent extends GameStateEvent {

	private GameStateEvent parentEvent;
	private final Map<String, Object> inObjects = new ConcurrentHashMap<>();
	private final Map<String, Object> outObjects = new ConcurrentHashMap<>();
	private int returnState;
	private int returnDelay;
	private boolean triggered = false;

	public GameNotifyEvent(final World world, final Mob owner, final int ticks, final String descriptor) {
		super(world, owner, ticks, descriptor);
	}

	@Override
	public void stop() {
		super.stop();
		trigger();
	}

	public void setParentEvent(final GameStateEvent event) {
		this.parentEvent = event;
	}

	public void trigger() {
		if(!isTriggered()) {
			triggered = true;
			restoreParent();
			onTriggered();
		}
	}

	public void onTriggered() {}

	private void restoreParent() {
		getParentEvent().setState(getReturnState());
		getParentEvent().setDelayTicks(getReturnDelay());
	}

	public boolean isTriggered() { return triggered; }

	public void addObjectOut(final String name, final Object item) {
		outObjects.put(name, item);
	}

	public void addObjectIn(final String name, final Object item) {
		inObjects.put(name, item);
	}

	public Object getObjectOut(final String name) {
		return outObjects.get(name);
	}

	public boolean hasObjectOut(final String name) {
		return outObjects.containsKey(name);
	}

	public Object getObjectIn(final String name) {
		return inObjects.get(name);
	}

	public boolean hasObjectIn(final String name) {
		return inObjects.containsKey(name);
	}

	public int getReturnState() {
		return returnState;
	}

	public void setReturnState(final int returnState) {
		this.returnState = returnState;
	}

	public int getReturnDelay() {
		return returnDelay;
	}

	public void setReturnDelay(final int returnDelay) {
		this.returnDelay = returnDelay;
	}

	public GameStateEvent getParentEvent() {
		return parentEvent;
	}
}
