package com.openrsc.server;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.entity.player.Player;

public final class ServerEventHandler {
	
	/**
     * The asynchronous logger.
     */
    private static final Logger LOGGER = LogManager.getLogger();

	private Queue<DelayedEvent> events = new ConcurrentLinkedQueue<DelayedEvent>();
	private Queue<DelayedEvent> toAdd = new ConcurrentLinkedQueue<DelayedEvent>();

	public void add(DelayedEvent event) {
		if (!events.contains(event)) {
			toAdd.add(event);
		}
	}

	public boolean contains(DelayedEvent event) {
		return events.contains(event);
	}

	public void doEvents() {
		if (toAdd.size() > 0) {
			events.addAll(toAdd);
			toAdd.clear();
		}

		Iterator<DelayedEvent> iterator = events.iterator();
		while (iterator.hasNext()) {
			DelayedEvent event = iterator.next();
			if (event == null || event.getOwner() != null && event.getOwner().isUnregistering()) {
				iterator.remove();
				continue;
			}
			try {
				if (event.shouldRun()) {
					event.run();
					event.updateLastRun();
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

	public Queue<DelayedEvent> getEvents() {
		return events;
	}

	public void remove(DelayedEvent event) {
		events.remove(event);
	}

	public void removePlayersEvents(Player player) {
		try {
			Iterator<DelayedEvent> iterator = events.iterator();
			while (iterator.hasNext()) {
				DelayedEvent event = iterator.next();
				if (event.belongsTo(player)) {
					iterator.remove();
				}
			}
		} catch (Exception e) {
			LOGGER.catching(e);
		}

	}
}
