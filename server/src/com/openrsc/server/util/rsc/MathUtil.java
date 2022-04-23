package com.openrsc.server.util.rsc;

public class MathUtil {

	public static int maxUnsigned(int a, int b) {
		return Integer.compareUnsigned(a, b) >= 0 ? a : b;
	}

	public static int boundedNumber(int number, int lowerBounds, int upperBounds) {
		return Math.max(lowerBounds, Math.min(number, upperBounds));
	}

	public static boolean isKthBitSet(int number, int kBit)
	{
		return (((number >> (kBit - 1)) & 1) > 0);
	}

	public static int toggleKthBit(int number, int kBit)
	{
		return (number ^ (1 << (kBit - 1)));
	}
}
