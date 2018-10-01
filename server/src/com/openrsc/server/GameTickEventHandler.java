package com.openrsc.server;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.openrsc.server.event.rsc.GameTickEvent;

public class GameTickEventHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	private LinkedHashMap<String, GameTickEvent> events = new LinkedHashMap<String, GameTickEvent>();
	private LinkedHashMap<String, GameTickEvent> toAdd = new LinkedHashMap<String, GameTickEvent>();

	public void add(GameTickEvent event) {
		String className = event.getClass().getSimpleName();
		if (event.getOwner() == null) {
			String u;
			while (events.containsKey(u = UUID.randomUUID().toString())) {}
			toAdd.put(className + u, event);
		}
		else
			toAdd.put(className + String.valueOf(event.getOwner().getID()), event);
	}

	public boolean contains(GameTickEvent event) {
		if (event.getOwner() != null)
			return events.containsKey(String.valueOf(event.getOwner().getID()));
		return false;
	}

	public void doGameEvents() {
		if (toAdd.size() > 0) {
			for (Map.Entry<String, GameTickEvent> e : toAdd.entrySet())
				events.put(e.getKey(), e.getValue());
			toAdd.clear();
		}
		for (Iterator<Map.Entry<String, GameTickEvent>> it = events.entrySet().iterator(); it.hasNext();) {
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

	public LinkedHashMap<String, GameTickEvent> getEvents() {
		return events;
	}

	public void remove(GameTickEvent event) {
		events.remove(event);
	}
}
