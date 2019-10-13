package com.openrsc.server;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.GameNotifyEvent;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameTickEventHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	private final Vector<GameNotifyEvent> notifiers = new Vector<>();
	private final Vector<GameNotifyEvent> addNotifier = new Vector<>();

	private final ConcurrentHashMap<String, GameTickEvent> events = new ConcurrentHashMap<String, GameTickEvent>();
	private final ConcurrentHashMap<String, GameTickEvent> toAdd = new ConcurrentHashMap<String, GameTickEvent>();


	private final HashMap<String, Integer> eventsCounts = new HashMap<String, Integer>();
	private final HashMap<String, Long> eventsDurations = new HashMap<String, Long>();

	private final Server server;

	public final Server getServer() {
		return server;
	}

	public GameTickEventHandler(Server server) {
		this.server = server;
	}

	public void add(GameNotifyEvent event) {
		addNotifier.add(event);
	}

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

	public void add(DelayedEvent event) {
		String className = String.valueOf(event.getClass());
		UUID uuid = UUID.randomUUID();
		if (event.isUniqueEvent() || !event.hasOwner()) {
			toAdd.putIfAbsent(className + uuid, event);
		} else {
			if (event.getOwner().isPlayer())
				toAdd.putIfAbsent(className + event.getOwner().getUUID() + "p", event);
			else
				toAdd.putIfAbsent(className + event.getOwner().getUUID() + "n", event);
		}
	}

	public boolean contains(GameTickEvent event) {
		if (event.getOwner() != null)
			return events.containsKey(String.valueOf(event.getOwner().getID()));
		return false;
	}

	private void checkNotifiers() {
		if (addNotifier.size() > 0) {
			for (Iterator<GameNotifyEvent> iter = addNotifier.iterator(); iter.hasNext(); ) {
				GameNotifyEvent e = iter.next();
				notifiers.add(e);
				iter.remove();
			}
		}

		Iterator<GameNotifyEvent> it = notifiers.iterator();
		while (it.hasNext()) {
			GameNotifyEvent next = it.next();
			if (next.isTriggered()) {
				next.restoreParent();
				it.remove();
			}
		}
	}

	public long doGameEvents() {
		checkNotifiers();

		final long eventsStart = System.currentTimeMillis();


		if (toAdd.size() > 0) {
			for (Iterator<Map.Entry<String, GameTickEvent>> iter = toAdd.entrySet().iterator(); iter.hasNext(); ) {
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
					event.doRun();
					event.resetCountdown();
				}
			} catch (Exception e) {
				LOGGER.catching(e);
				event.stop();
			}
		}
		
		eventsCounts.clear();
		eventsDurations.clear();
		
		for (Iterator<Map.Entry<String, GameTickEvent>> it = events.entrySet().iterator(); it.hasNext(); ) {
			GameTickEvent event = it.next().getValue();
			if (!eventsCounts.containsKey(event.getDescriptor())) {
				eventsCounts.put(event.getDescriptor(), 1);
			} else {
				eventsCounts.put(event.getDescriptor(), eventsCounts.get(event.getDescriptor()) + 1);
			}

			if (!eventsDurations.containsKey(event.getDescriptor())) {
				eventsDurations.put(event.getDescriptor(), event.getLastEventDuration());
			} else {
				eventsDurations.put(event.getDescriptor(), eventsDurations.get(event.getDescriptor()) + event.getLastEventDuration());
			}

			if (event.shouldRemove()) {
				it.remove();
			}
		}
		final long eventsEnd = System.currentTimeMillis();
		return eventsEnd - eventsStart;
	}

	public HashMap<String, GameTickEvent> getEvents() {
		return new LinkedHashMap<String, GameTickEvent>(events);
	}

	public void remove(GameTickEvent event) {
		events.remove(event);
	}

	public void removePlayersEvents(Player player) {
		try {
			Iterator<Map.Entry<String, GameTickEvent>> iterator = events.entrySet().iterator();
			while (iterator.hasNext()) {
				GameTickEvent event = iterator.next().getValue();
				if (event.belongsTo(player)) {
					iterator.remove();
				}
			}
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	public HashMap<String, Integer> getEventsCounts() {
		return new LinkedHashMap<String, Integer>(eventsCounts);
	}

	public HashMap<String, Long> getEventsDurations() {
		return new LinkedHashMap<String, Long>(eventsDurations);
	}
}
