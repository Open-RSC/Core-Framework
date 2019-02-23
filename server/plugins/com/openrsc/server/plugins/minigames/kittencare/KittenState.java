package com.openrsc.server.plugins.minigames.kittencare;

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
	
	public void loadState(Player p) {
		if (p.getAttribute("kitten_events", -1) == -1 && p.getCache().hasKey("kitten_events")) {
			events = p.getCache().getInt("kitten_events");
		} else {
			events = p.getAttribute("kitten_events", 0);
		}
		if (p.getAttribute("kitten_hunger", -1) == -1 && p.getCache().hasKey("kitten_hunger")) {
			hunger = p.getCache().getInt("kitten_hunger");
		} else {
			hunger = p.getAttribute("kitten_hunger", 0);
		}
		if (p.getAttribute("kitten_loneliness", -1) == -1 && p.getCache().hasKey("kitten_loneliness")) {
			loneliness = p.getCache().getInt("kitten_loneliness");
		} else {
			loneliness = p.getAttribute("kitten_loneliness", 0);
		}
	}
	
	public void saveState(Player p) {
		p.setAttribute("kitten_events", events);
		p.setAttribute("kitten_hunger", hunger);
		p.setAttribute("kitten_loneliness", loneliness);
	}
	
}
