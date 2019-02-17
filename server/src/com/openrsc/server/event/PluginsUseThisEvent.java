package com.openrsc.server.event;

public abstract class PluginsUseThisEvent extends DelayedEvent {

	protected PluginsUseThisEvent() {
		super(null, 0);
	}

	public PluginsUseThisEvent(int delay) {
		super(null, delay);
	}

	@Override
	public void run() {
		action();
		stop();
	}

	public abstract void action();
}
