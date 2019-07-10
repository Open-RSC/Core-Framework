package com.openrsc.server.event;

public abstract class PluginsUseThisEvent extends DelayedEvent {

	protected PluginsUseThisEvent(String descriptor) {
		super(null, 0, descriptor);
	}

	public PluginsUseThisEvent(int delay, String descriptor) {
		super(null, delay, descriptor);
	}

	@Override
	public void run() {
		action();
		stop();
	}

	public abstract void action();
}
