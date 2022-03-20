package com.openrsc.server.util.rsc;

public class MathUtil {

	public static int maxUnsigned(int a, int b) {
		return Integer.compareUnsigned(a, b) >= 0 ? a : b;
	}

	public static int boundedNumber(int number, int lowerBounds, int upperBounds) {
		return Math.max(lowerBounds, Math.min(number, upperBounds));
	}
}
