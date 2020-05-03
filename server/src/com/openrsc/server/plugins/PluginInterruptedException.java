package com.openrsc.server.plugins;

public class PluginInterruptedException extends RuntimeException {
	public PluginInterruptedException(final String message) {
		super(message);
	}

	public PluginInterruptedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public PluginInterruptedException(final Throwable cause) {
		super(cause);
	}
}
