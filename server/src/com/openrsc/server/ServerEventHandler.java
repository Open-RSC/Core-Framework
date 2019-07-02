package com.openrsc.server;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ServerEventHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private ConcurrentHashMap<String, DelayedEvent> events = new ConcurrentHashMap<String, DelayedEvent>();
	private ConcurrentHashMap<String, DelayedEvent> toAdd = new ConcurrentHashMap<String, DelayedEvent>();
	
	public void add(DelayedEvent event) {
		String className = String.valueOf(event.getClass());
		if (event.isUniqueEvent() || !event.hasOwner()) {
			toAdd.putIfAbsent(className + event.getUUID(), event);
		} else {
			if (event.getOwner().isPlayer())
				toAdd.putIfAbsent(className + event.getOwner().getUUID() + "p", event);
			else
				toAdd.putIfAbsent(className + event.getOwner().getUUID() + "n", event);
		}
	}

	public boolean contains(DelayedEvent event) {
		if (event.getOwner() != null)
			return events.containsKey(event.getOwner().getID());
		return false;
	}

	void doEvents() {
		if (toAdd.size() > 0) {
			for(Iterator<Map.Entry<String, DelayedEvent>> iter = toAdd.entrySet().iterator(); iter.hasNext(); ) {
			    Map.Entry<String, DelayedEvent> e = iter.next();
			    events.putIfAbsent(e.getKey(), e.getValue());
			    iter.remove();
			}
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

	public HashMap<String, DelayedEvent> getEvents() {
		return new LinkedHashMap<String, DelayedEvent>(events);
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
