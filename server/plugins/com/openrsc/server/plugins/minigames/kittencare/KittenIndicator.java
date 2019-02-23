package com.openrsc.server.plugins.minigames.kittencare;

import java.util.Optional;

public enum KittenIndicator {
	
	KITTEN_NONE(Optional.empty(), Optional.empty()),
	KITTEN_LONELINESS_1(Optional.of("@yel@kitten: miaow!"), 
			Optional.empty()),
	KITTEN_LONELINESS_2(Optional.of("@yel@kitten: miaow!"), 
			Optional.of(new String[]{"your kitten wants some attention"})),
	KITTEN_LONELINESS_3(Optional.of("@yel@kitten: miaaaaow!"), 
			Optional.of(new String[]{"your kitten is feeling lonely"})),
	KITTEN_LONELINESS_4(Optional.empty(), 
			Optional.of(new String[]{"your kitten has ran off",
					"your kitten was feeling lonely"})),
	KITTEN_HUNGER_1(Optional.of("you hear a purring"), 
			Optional.empty()),
	KITTEN_HUNGER_2(Optional.of("you hear a purring"), 
			Optional.of(new String[]{"your kitten is hungry"})),
	KITTEN_HUNGER_3(Optional.of("you hear a loud meow"), 
			Optional.of(new String[]{"your kitten is really hungry"})),
	KITTEN_HUNGER_4(Optional.of("your kitten has ran off to look for food"), 
			Optional.of(new String[]{"to find some food"}));
	
	
	private Optional<String> signal;
	private Optional<String[]> interpretation;
	
	KittenIndicator(Optional<String> signal, Optional<String[]> interpretation) {
		this.signal = signal;
		this.interpretation = interpretation;
	}
	
	public Optional<String> getSignal() {
		return signal;
	}
	
	public Optional<String[]> getInterpretation() {
		return interpretation;
	}
	
}
