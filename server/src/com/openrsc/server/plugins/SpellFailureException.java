package com.openrsc.server.plugins;

public class SpellFailureException extends RuntimeException {

	public SpellFailureException(String reason) {
		super(reason);
	}

}
