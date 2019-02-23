package com.openrsc.server.plugins.minigames.kittencare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KittenMessageSolver {
	
	public static List<String> messagesCombined(int hungerGauge, int lonelinessGauge) {
		List<String> messages = new ArrayList<>();
		List<String> hungerMessages = messagesHunger(hungerGauge);
		List<String> lonelinessMessages = messagesLoneliness(lonelinessGauge);
		if (hungerGauge >= 4*KittenToCat.BASE_FACTOR) {
			return hungerMessages;
		} else if (lonelinessGauge >= 4*KittenToCat.BASE_FACTOR) {
			return lonelinessMessages;
		}
		messages.add(hungerMessages.get(0));
		for(String message : lonelinessMessages) {
			messages.add(message);
		}
		for(String message : hungerMessages.subList(1, hungerMessages.size())) {
			messages.add(message);
		}
		
		return messages;
	}
	
	public static List<String> messagesHunger(int hungerGauge) {
		List<String> messages = new ArrayList<>();
		KittenIndicator hungerEnum = resolveHunger(hungerGauge);
		if (hungerEnum.getSignal().isPresent()) {
			messages.add(hungerEnum.getSignal().get());
		}
		if (hungerEnum.getInterpretation().isPresent()) {
			messages.addAll(Arrays.asList(hungerEnum.getInterpretation().get()));
		}
		return messages;
	}
	
	public static List<String> messagesLoneliness(int lonelinessGauge) {
		List<String> messages = new ArrayList<>();
		KittenIndicator lonelinessEnum = resolveLoneliness(lonelinessGauge);
		if (lonelinessEnum.getSignal().isPresent()) {
			messages.add(lonelinessEnum.getSignal().get());
		}
		if (lonelinessEnum.getInterpretation().isPresent()) {
			messages.addAll(Arrays.asList(lonelinessEnum.getInterpretation().get()));
		}
		return messages;
	}
	
	private static KittenIndicator resolveLoneliness(int lonelinessGauge) {
		KittenIndicator lonelinessEnum;
		if (lonelinessGauge < KittenToCat.BASE_FACTOR) {
			lonelinessEnum = KittenIndicator.KITTEN_NONE;
		} else if (lonelinessGauge < 2*KittenToCat.BASE_FACTOR) {
			lonelinessEnum = KittenIndicator.KITTEN_LONELINESS_1;
		} else if (lonelinessGauge < 3*KittenToCat.BASE_FACTOR) {
			lonelinessEnum = KittenIndicator.KITTEN_LONELINESS_2;
		} else if (lonelinessGauge < 4*KittenToCat.BASE_FACTOR) {
			lonelinessEnum = KittenIndicator.KITTEN_LONELINESS_3;
		} else {
			lonelinessEnum = KittenIndicator.KITTEN_LONELINESS_4;
		}
		return lonelinessEnum;
	}
	
	private static KittenIndicator resolveHunger(int hungerGauge) {
		KittenIndicator hungerEnum;
		if (hungerGauge < KittenToCat.BASE_FACTOR) {
			hungerEnum = KittenIndicator.KITTEN_NONE;
		} else if (hungerGauge < 2*KittenToCat.BASE_FACTOR) {
			hungerEnum = KittenIndicator.KITTEN_HUNGER_1;
		} else if (hungerGauge < 3*KittenToCat.BASE_FACTOR) {
			hungerEnum = KittenIndicator.KITTEN_HUNGER_2;
		} else if (hungerGauge < 4*KittenToCat.BASE_FACTOR) {
			hungerEnum = KittenIndicator.KITTEN_HUNGER_3;
		} else {
			hungerEnum = KittenIndicator.KITTEN_HUNGER_4;
		}
		return hungerEnum;
	}
	
}
