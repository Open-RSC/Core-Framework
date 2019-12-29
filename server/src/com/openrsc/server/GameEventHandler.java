package com.openrsc.server;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.ImmediateEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.NamedThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;

public class GameEventHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final ConcurrentHashMap<String, GameTickEvent> events = new ConcurrentHashMap<String, GameTickEvent>();
	private final ConcurrentHashMap<String, GameTickEvent> eventsToAdd = new ConcurrentHashMap<String, GameTickEvent>();

	private final ConcurrentHashMap<String, Integer> eventsCounts = new ConcurrentHashMap<String, Integer>();
	private final ConcurrentHashMap<String, Long> eventsDurations = new ConcurrentHashMap<String, Long>();

	private final ThreadPoolExecutor executor;

	private final Server server;

	public GameEventHandler(final Server server) {
		this.server = server;
		final int nThreads = Runtime.getRuntime().availableProcessors();
		executor = new ThreadPoolExecutor(nThreads, nThreads * 2, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new NamedThreadFactory(getServer().getName() + " : EventHandler"));
		executor.prestartAllCoreThreads();
	}

	public void add(final GameTickEvent event) {
		final String className = String.valueOf(event.getClass());
		if (event.allowsDuplicateEvents() || !event.hasOwner()) {
			final UUID uuid = UUID.randomUUID();
			eventsToAdd.putIfAbsent(className + uuid, event);
		} else {
			if (event.getOwner().isPlayer())
				eventsToAdd.putIfAbsent(className + event.getOwner().getUUID() + "p", event);
			else
				eventsToAdd.putIfAbsent(className + event.getOwner().getUUID() + "n", event);
		}
	}

	public void submit(final Runnable r, final String descriptor) {
		add(new ImmediateEvent(getServer().getWorld(), descriptor) {
			@Override
			public void action() {
				try {
					r.run();
				} catch (Throwable e) {
					LOGGER.catching(e);
				}
			}
		});
	}

	public boolean contains(final GameTickEvent event) {
		if (event.getOwner() != null) {
			return events.containsKey(String.valueOf(event.getOwner().getID()));
		}
		return false;
	}

	private void processEvents() {
		if (eventsToAdd.size() > 0) {
			for (Iterator<Map.Entry<String, GameTickEvent>> iter = eventsToAdd.entrySet().iterator(); iter.hasNext(); ) {
				Map.Entry<String, GameTickEvent> e = iter.next();
				events.merge(e.getKey(), e.getValue(), (oldEvent, newEvent) -> newEvent);
				iter.remove();
			}
		}

		// Sorting for unused Notify and State Events
		/*ArrayList<Callable<Integer>> notifyEvents = new ArrayList<Callable<Integer>>();
		ArrayList<Callable<Integer>> stateEvents = new ArrayList<Callable<Integer>>();
		ArrayList<Callable<Integer>> tickEvents = new ArrayList<Callable<Integer>>();*/

		ArrayList<Callable<Integer>> callables = new ArrayList<Callable<Integer>>();

		for (final Iterator<Map.Entry<String, GameTickEvent>> it = events.entrySet().iterator(); it.hasNext(); ) {
			GameTickEvent event = it.next().getValue();
			if (event == null || event.getOwner() != null && event.getOwner().isUnregistering()) {
				it.remove();
			}

			callables.add(event);
			// Doing this in stages to ensure that GameNotifyEvents are processed before GameStateEvents and GameStateEvents before all other events
			/*if(event instanceof GameNotifyEvent) {
				notifyEvents.add(event);
			} else if (event instanceof GameStateEvent) {
				stateEvents.add(event);
			} else {
				tickEvents.add(event);
			}*/
		}

		try {
			executor.invokeAll(callables);
			// Sorting for unused Notify and State Events
			/*executor.invokeAll(notifyEvents);
			executor.invokeAll(stateEvents);
			executor.invokeAll(tickEvents);*/

			/*List<Future<Integer>> futures = executor.invokeAll(callables);
			for (int i = 0; i < futures.size(); i++) {
				Future<Integer> future = futures.get(i);
				final int returnCode = future.get();
			}*/
		} catch (Exception e) {
			LOGGER.catching(e);
		}

		eventsCounts.clear();
		eventsDurations.clear();

		for (final Iterator<Map.Entry<String, GameTickEvent>> it = events.entrySet().iterator(); it.hasNext(); ) {
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
	}

	public long runGameEvents() {
		final long eventsStart = System.currentTimeMillis();

		processEvents();

		final long eventsEnd = System.currentTimeMillis();
		return eventsEnd - eventsStart;
	}

	public final String buildProfilingDebugInformation(boolean forInGame) {
		int countAllEvents = 0;
		long durationAllEvents = 0;
		String newLine = forInGame ? "%" : "\r\n";

		final HashMap<String, Integer> eventsCounts = getEventsCounts();
		final HashMap<String, Long> eventsDurations = getEventsDurations();

		// Calculate Totals
		for (Map.Entry<String, Integer> eventEntry : eventsCounts.entrySet()) {
			countAllEvents += eventEntry.getValue();
		}
		for (Map.Entry<String, Long> eventEntry : eventsDurations.entrySet()) {
			durationAllEvents += eventEntry.getValue();
		}

		// Sort the Events Hashmap
		List list = new LinkedList(eventsDurations.entrySet());
		Collections.sort(list, (Object o1, Object o2) -> {
			int o1EventCount = eventsCounts.get(((Map.Entry) (o1)).getKey());
			int o2EventCount = eventsCounts.get(((Map.Entry) (o2)).getKey());
			long o1EventDuration = eventsDurations.get(((Map.Entry) (o1)).getKey());
			long o2EventDuration = eventsDurations.get(((Map.Entry) (o2)).getKey());

			if(o1EventDuration == o2EventDuration) {
				if(o1EventCount == o2EventCount) {
					return 0;
				}
				return o1EventCount < o2EventCount ? 1 : -1;
			} else {
				return o1EventDuration < o2EventDuration ? 1 : -1;
			}
		});
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		eventsDurations.clear();
		eventsDurations.putAll(sortedHashMap);

		int i = 0;
		StringBuilder s = new StringBuilder();
		for (Map.Entry<String, Long> entry : eventsDurations.entrySet()) {
			if (forInGame && i >= 17) // Only display first 17 elements of the hashmap
				break;

			String name = entry.getKey();
			Long duration = entry.getValue();
			Integer count = eventsCounts.get(entry.getKey());
			s.append(name).append(" : ").append(duration).append("ms").append(" : ").append(count).append(newLine);
			++i;
		}

		String returnString = (
			"Tick: " + getServer().getConfig().GAME_TICK + "ms, Server: " + getServer().getLastTickDuration() + "ms " + getServer().getLastIncomingPacketsDuration() + "ms " + getServer().getLastEventsDuration() + "ms " + getServer().getLastGameStateDuration() + "ms " + getServer().getLastOutgoingPacketsDuration() + "ms" + newLine +
				"Game Updater: " + getServer().getGameUpdater().getLastProcessPlayersDuration() + "ms " + getServer().getGameUpdater().getLastProcessNpcsDuration() + "ms " + getServer().getGameUpdater().getLastProcessMessageQueuesDuration() + "ms " + getServer().getGameUpdater().getLastUpdateClientsDuration() + "ms " + getServer().getGameUpdater().getLastDoCleanupDuration() + "ms " + getServer().getGameUpdater().getLastExecuteWalkToActionsDuration() + "ms " + newLine +
				"Threads: " + Thread.activeCount() + ", Events: " + countAllEvents + ", NPCs: " + getServer().getWorld().getNpcs().size() + ", Players: " + getServer().getWorld().getPlayers().size() + ", Shops: " + getServer().getWorld().getShops().size() + newLine +
				/*"Player Atk Map: " + getWorld().getPlayersUnderAttack().size() + ", NPC Atk Map: " + getWorld().getNpcsUnderAttack().size() + ", Quests: " + getWorld().getQuests().size() + ", Mini Games: " + getWorld().getMiniGames().size() + newLine +*/
				s
		);

		if(!forInGame) {
			LOGGER.info(returnString);
		}

		return returnString.substring(0, returnString.length() > 1999 ? 1999 : returnString.length()); // Limit to 2000 characters for Discord.
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

	public final Server getServer() {
		return server;
	}
}
