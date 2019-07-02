package com.openrsc.server;

import com.openrsc.server.event.rsc.GameTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameTickEventHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	private ConcurrentHashMap<String, GameTickEvent> events = new ConcurrentHashMap<String, GameTickEvent>();
	private ConcurrentHashMap<String, GameTickEvent> toAdd = new ConcurrentHashMap<String, GameTickEvent>();

	public void add(GameTickEvent event) {
		String className = String.valueOf(event.getClass());
		if (event.getOwner() == null) { // Server events, no owner.
			toAdd.merge(className + UUID.randomUUID(), event, (oldEvent, newEvent) -> newEvent);
		} else {
			if (event.getOwner().isPlayer())
				toAdd.merge(className + event.getOwner().getUUID() + "p", event, (oldEvent, newEvent) -> newEvent);
			else
				toAdd.merge(className + event.getOwner().getUUID() + "n", event, (oldEvent, newEvent) -> newEvent);
		}
	}

	public boolean contains(GameTickEvent event) {
		if (event.getOwner() != null)
			return events.containsKey(String.valueOf(event.getOwner().getID()));
		return false;
	}

	void doGameEvents() {
		if (toAdd.size() > 0) {
			for(Iterator<Map.Entry<String, GameTickEvent>> iter = toAdd.entrySet().iterator(); iter.hasNext(); ) {
			    Map.Entry<String, GameTickEvent> e = iter.next();
			    events.merge(e.getKey(), e.getValue(), (oldEvent, newEvent) -> newEvent);
			    iter.remove();
			}
		}
		for (Iterator<Map.Entry<String, GameTickEvent>> it = events.entrySet().iterator(); it.hasNext(); ) {
			GameTickEvent event = it.next().getValue();
			if (event == null || event.getOwner() != null && event.getOwner().isUnregistering()) {
				it.remove();
				continue;
			}
			try {
				event.countdown();
				if (event.shouldRun()) {
					event.run();
					event.resetCountdown();
				}
			} catch (Exception e) {
				LOGGER.catching(e);
				event.stop();
			}
			if (event.shouldRemove()) {
				it.remove();
			}
		}
	}

	public HashMap<String, GameTickEvent> getEvents() {
		return new LinkedHashMap<String, GameTickEvent>(events);
	}

	public void remove(GameTickEvent event) {
		events.remove(event);
	}
}
