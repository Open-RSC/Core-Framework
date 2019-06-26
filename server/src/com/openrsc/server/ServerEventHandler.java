package com.openrsc.server;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class ServerEventHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private LinkedHashMap<String, DelayedEvent> events = new LinkedHashMap<String, DelayedEvent>();
	private LinkedHashMap<String, DelayedEvent> toAdd = new LinkedHashMap<String, DelayedEvent>();

	public void add(DelayedEvent event) {
		String className = String.valueOf(event.getClass());
		if (event.isUniqueEvent() || !event.hasOwner()) {
			String u;
			do {
				u = UUID.randomUUID().toString();
			} while (toAdd.containsKey(className + u));
			toAdd.put(className + u, event);
		} else {
			if (event.getOwner().isPlayer())
				toAdd.put(className + event.getOwner().getUUID() + "p", event);
			else
				toAdd.put(className + event.getOwner().getUUID() + "n", event);
		}
	}

	public boolean contains(DelayedEvent event) {
		if (event.getOwner() != null)
			return events.containsKey(event.getOwner().getID());
		return false;
	}

	void doEvents() {
		if (toAdd.size() > 0) {
			for (Map.Entry<String, DelayedEvent> e : toAdd.entrySet())
				events.put(e.getKey(), e.getValue());
			toAdd.clear();
		}
		for (Iterator<Map.Entry<String, DelayedEvent>> it = events.entrySet().iterator(); it.hasNext(); ) {
			DelayedEvent event = it.next().getValue();
			if (event == null || event.getOwner() != null && event.getOwner().isUnregistering()) {
				it.remove();
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
				it.remove();
			}
		}
	}

	public LinkedHashMap<String, DelayedEvent> getEvents() {
		return events;
	}

	public void remove(DelayedEvent event) {
		events.remove(event);
	}

	public void removePlayersEvents(Player player) {
		try {
			Iterator<Map.Entry<String, DelayedEvent>> iterator = events.entrySet().iterator();
			while (iterator.hasNext()) {
				DelayedEvent event = iterator.next().getValue();
				if (event.belongsTo(player)) {
					iterator.remove();
				}
			}
		} catch (Exception e) {
			LOGGER.catching(e);
		}

	}
}
