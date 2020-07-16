package com.openrsc.server.plugins.authentic.minigames.kittencare;

import com.openrsc.server.model.entity.player.Player;

public class KittenState {
	
	private int events = 0;
	private int hunger = 0;
	private int loneliness = 0;
	
	KittenState() {
		this(0, 0, 0);
	}
	
	private KittenState(int events, int hunger, int loneliness) {
		this.events = events;
		this.hunger = hunger;
		this.loneliness = loneliness;
	}
	
	public int getEvents() {
		return events;
	}
	public void setEvents(int events) {
		this.events = events;
	}
	public int getHunger() {
		return hunger;
	}
	public void setHunger(int hunger) {
		this.hunger = hunger;
	}
	public int getLoneliness() {
		return loneliness;
	}
	public void setLoneliness(int loneliness) {
		this.loneliness = loneliness;
	}
	
	public void loadState(Player player) {
		if (player.getAttribute("kitten_events", -1) == -1 && player.getCache().hasKey("kitten_events")) {
			events = player.getCache().getInt("kitten_events");
		} else {
			events = player.getAttribute("kitten_events", 0);
		}
		if (player.getAttribute("kitten_hunger", -1) == -1 && player.getCache().hasKey("kitten_hunger")) {
			hunger = player.getCache().getInt("kitten_hunger");
		} else {
			hunger = player.getAttribute("kitten_hunger", 0);
		}
		if (player.getAttribute("kitten_loneliness", -1) == -1 && player.getCache().hasKey("kitten_loneliness")) {
			loneliness = player.getCache().getInt("kitten_loneliness");
		} else {
			loneliness = player.getAttribute("kitten_loneliness", 0);
		}
	}
	
	public void saveState(Player player) {
		player.setAttribute("kitten_events", events);
		player.setAttribute("kitten_hunger", hunger);
		player.setAttribute("kitten_loneliness", loneliness);
	}
	
}
