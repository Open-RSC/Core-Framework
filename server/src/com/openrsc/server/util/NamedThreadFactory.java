package com.openrsc.server.util;

import com.openrsc.server.ServerConfiguration;

import java.util.concurrent.atomic.AtomicInteger;

public final class NamedThreadFactory extends ServerAwareThreadFactory {

	/**
	 * The unique name.
	 */
	private final String name;

	/**
	 * Creates the named thread factory.
	 *
	 * @param name The unique name.
	 */
	public NamedThreadFactory(String name, ServerConfiguration configuration) {
		super(name + "-%d", configuration);
		this.name = name;
	}

}
