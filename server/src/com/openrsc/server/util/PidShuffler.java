package com.openrsc.server.util;

import java.util.Random;

public class PidShuffler {
	private static Random random;

	public static int[] pidProcessingOrder;

	public static void init() {
		pidProcessingOrder = new int[EntityList.DEFAULT_CAPACITY];
		for (int i = 0; i < pidProcessingOrder.length; i++) {
			pidProcessingOrder[i] = i;
		}
		shuffle();
	}

	public static void shuffle() {
		if (random == null) random = new Random();
		for (int i = pidProcessingOrder.length; i > 1; i--) {
			swap(pidProcessingOrder, i - 1, random.nextInt(i));
		}
	}

	private static void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
}
