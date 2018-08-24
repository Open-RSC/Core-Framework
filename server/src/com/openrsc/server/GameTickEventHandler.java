package com.openrsc.server;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.openrsc.server.event.rsc.GameTickEvent;

public class GameTickEventHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	private Queue<GameTickEvent> events = new ConcurrentLinkedQueue<GameTickEvent>();
	private Queue<GameTickEvent> toAdd = new ConcurrentLinkedQueue<GameTickEvent>();

	public void add(GameTickEvent event) {
		if (!events.contains(event)) {
			events.add(event);
		}
	}

	public boolean contains(GameTickEvent event) {
		return events.contains(event);
	}

	public void doGameEvents() {
		if (toAdd.size() > 0) {
			events.addAll(toAdd);
			toAdd.clear();
		}
		Iterator<GameTickEvent> iterator = events.iterator();
		while (iterator.hasNext()) {
			GameTickEvent event = iterator.next();
			if (event == null || event.getOwner() != null && event.getOwner().isUnregistering()) {
				iterator.remove();
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
				iterator.remove();
			}
		}
	}

	public Queue<GameTickEvent> getEvents() {
		return events;
	}

	public void remove(GameTickEvent event) {
		events.remove(event);
	}
}
