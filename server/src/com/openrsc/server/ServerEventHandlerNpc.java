package com.openrsc.server;

import com.openrsc.server.event.DelayedEventNpc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.Mob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class ServerEventHandlerNpc {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private LinkedHashMap<String, DelayedEventNpc> events = new LinkedHashMap<String, DelayedEventNpc>();
	private LinkedHashMap<String, DelayedEventNpc> toAdd = new LinkedHashMap<String, DelayedEventNpc>();

	public void add(DelayedEventNpc event) {
		String className = String.valueOf(event.getClass());
		if (event.isUniqueEvent() || !event.hasOwner()) {
			String u;
			while (toAdd.containsKey(u = UUID.randomUUID().toString())) {
			}
			toAdd.put(className + u, event);
		} else {
			if (event.getOwner().isPlayer())
				toAdd.put(className + event.getOwner().getUUID() + "p", event);
			else
				toAdd.put(className + event.getOwner().getUUID() + "n", event);
		}
	}

	public boolean contains(DelayedEventNpc event) {
		if (event.getOwner() != null)
			return events.containsKey(event.getOwner().getID());
		return false;
	}

	void doEvents() {
		if (toAdd.size() > 0) {
			for (Map.Entry<String, DelayedEventNpc> e : toAdd.entrySet())
				events.put(e.getKey(), e.getValue());
			toAdd.clear();
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

	public LinkedHashMap<String, DelayedEventNpc> getEvents() {
		return events;
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
