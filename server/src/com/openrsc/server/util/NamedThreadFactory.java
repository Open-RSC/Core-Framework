package com.openrsc.server.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class NamedThreadFactory implements ThreadFactory {

	/**
	 * The unique name.
	 */
	private final String name;
	/**
	 * The next id.
	 */
	private AtomicInteger id = new AtomicInteger(0);

	/**
	 * Creates the named thread factory.
	 *
	 * @param name The unique name.
	 */
	public NamedThreadFactory(String name) {
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable runnable) {
		int currentId = id.getAndIncrement();
		return new Thread(runnable, name + "-" + currentId);
	}

}
