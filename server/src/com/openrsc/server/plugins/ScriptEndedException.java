package com.openrsc.server.plugins;

public class ScriptEndedException extends RuntimeException {
	public ScriptEndedException(final String message) {
		super(message);
	}

	public ScriptEndedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ScriptEndedException(final Throwable cause) {
		super(cause);
	}
}
