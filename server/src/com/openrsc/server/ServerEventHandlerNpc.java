package com.openrsc.server;

import com.openrsc.server.event.DelayedEventNpc;
import com.openrsc.server.model.entity.Mob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ServerEventHandlerNpc {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private ConcurrentHashMap<String, DelayedEventNpc> events = new ConcurrentHashMap<String, DelayedEventNpc>();
	private ConcurrentHashMap<String, DelayedEventNpc> toAdd = new ConcurrentHashMap<String, DelayedEventNpc>();

	public void add(DelayedEventNpc event) {
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

	public boolean contains(DelayedEventNpc event) {
		if (event.getOwner() != null)
			return events.containsKey(event.getOwner().getID());
		return false;
	}

	void doEvents() {
		if (toAdd.size() > 0) {
			for(Iterator<Map.Entry<String, DelayedEventNpc>> iter = toAdd.entrySet().iterator(); iter.hasNext(); ) {
			    Map.Entry<String, DelayedEventNpc> e = iter.next();
			    events.putIfAbsent(e.getKey(), e.getValue());
			    iter.remove();
			}
		}
		for (Iterator<Map.Entry<String, DelayedEventNpc>> it = events.entrySet().iterator(); it.hasNext(); ) {
			DelayedEventNpc event = it.next().getValue();
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

	public HashMap<String, DelayedEventNpc> getEvents() {
		return new LinkedHashMap<String, DelayedEventNpc>(events);
	}

	public void remove(DelayedEventNpc event) {
		events.remove(event);
	}

	public void removePlayersEvents(Mob mob) {
		try {
			Iterator<Map.Entry<String, DelayedEventNpc>> iterator = events.entrySet().iterator();
			while (iterator.hasNext()) {
				DelayedEventNpc event = iterator.next().getValue();
				if (event.belongsTo(mob)) {
					iterator.remove();
				}
			}
		} catch (Exception e) {
			LOGGER.catching(e);
		}

	}
}
