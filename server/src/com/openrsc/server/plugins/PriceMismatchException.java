package com.openrsc.server.plugins;

public class PriceMismatchException extends Exception {
	private int desiredPrice;
	private int effectivePrice;
	public PriceMismatchException(final int desiredPrice, final int effectivePrice, String message) {
		super(message);
		this.desiredPrice = desiredPrice;
		this.effectivePrice = effectivePrice;
	}

	public PriceMismatchException(final int desiredPrice, final int effectivePrice, final String message, final Throwable cause) {
		super(message, cause);
		this.desiredPrice = desiredPrice;
		this.effectivePrice = effectivePrice;
	}

	public PriceMismatchException(final int desiredPrice, final int effectivePrice, final Throwable cause) {
		super(cause);
		this.desiredPrice = desiredPrice;
		this.effectivePrice = effectivePrice;
	}

	public int getDesiredPrice() {
		return desiredPrice;
	}

	public int getEffectivePrice() {
		return effectivePrice;
	}
}
