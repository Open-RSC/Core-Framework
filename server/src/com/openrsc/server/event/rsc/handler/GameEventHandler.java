package com.openrsc.server.event.rsc.handler;

import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.ImmediateEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.NamedThreadFactory;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class GameEventHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final GameTickEventStore eventStore = new GameTickEventStore();
	private final ConcurrentHashMap<String, Integer> eventsCounts = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Long> eventsDurations = new ConcurrentHashMap<>();
	private final Server server;
	private ThreadPoolExecutor executor;

	public GameEventHandler(final Server server) {
		this.server = server;
	}

	public void load() {
		final int maxThreads;
		if (getServer().getConfig().WANT_THREADING__BREAK_PID_PRIORITY) {
			// can be slightly faster if we don't care which order events are done in (you always should care!)
			// TODO: currently also causes issues with scenery breaking from having two players accessing it
			maxThreads = (Runtime.getRuntime().availableProcessors() * 2) / (Server.serversList.size() > 0 ? Server.serversList.size() : 1);
		} else {
			// single thread events so that PID order is always respected.
			maxThreads = 1;
		}
		executor = new ThreadPoolExecutor(Math.max(1, maxThreads / 2), maxThreads, Long.MAX_VALUE, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new NamedThreadFactory(getServer().getName() + " : EventHandler", getServer().getConfig()));
		executor.prestartAllCoreThreads();
	}

	public final Server getServer() {
		return server;
	}

	public void unload() {
		// Process any events still in the queue.
		processEvents();

		executor.shutdown();
		try {
			final boolean terminationResult = executor.awaitTermination(1, TimeUnit.MINUTES);
			if (!terminationResult) {
				LOGGER.error("GameEventHandler thread pool termination failed");
			}
		} catch (final InterruptedException e) {
			LOGGER.catching(e);
		}

		cleanupEvents();
	}

	private void processEvents() {
		processNonPlayerEvents();
		getServer().getWorld().getPlayers().forEach(this::runPlayerEvents);
	}

	public void cleanupEvents() {
		eventStore.getTrackedEvents().forEach(event -> {
			incrementCounts(event);
			if (event.shouldRemove()) {
				eventStore.remove(event);
			}
		});
		eventsCounts.clear();
		eventsDurations.clear();
	}

	public long processNonPlayerEvents() {
		return getServer().bench(() -> {
			try {
				executor.invokeAll(eventStore.getNonPlayerEvents());
			} catch (final Exception e) {
				LOGGER.catching(e);
			}
		});
	}

	public long runPlayerEvents(final Player player) {
		return getServer().bench(() -> processEvents(player));
	}

	private void incrementCounts(GameTickEvent event) {
		eventsCounts.put(event.getDescriptor(),
			eventsCounts.containsKey(event.getDescriptor()) ?
				eventsCounts.get(event.getDescriptor()) + 1 :
				1);
		eventsDurations.put(event.getDescriptor(),
			eventsDurations.containsKey(event.getDescriptor()) ?
				eventsDurations.get(event.getDescriptor()) + event.getLastEventDuration() :
				event.getLastEventDuration());
	}

	public void processEvents(final Player player) {
		try {
			executor.invokeAll(eventStore.getPlayerEvents(player.getUsernameHash()));
		} catch (final Exception e) {
			LOGGER.catching(e);
		}
	}

	public void submit(final Runnable r, final String descriptor) {
		add(new ImmediateEvent(getServer().getWorld(), descriptor) {
			@Override
			public void action() {
				try {
					r.run();
				} catch (final Throwable e) {
					LOGGER.catching(e);
				}
			}
		});
	}

	public boolean add(final GameTickEvent event) {
		return eventStore.add(event);
	}

	public boolean addOrUpdate(final GameTickEvent event) {
		return eventStore.addOrUpdate(event);
	}

	public boolean has(final GameTickEvent event) {
		return eventStore.eventIsContained(event);
	}

	public final String buildProfilingDebugInformation(final boolean forInGame) {
		int countAllEvents = 0;
		long durationAllEvents = 0;
		String newLine = forInGame ? "%" : "\r\n";

		final HashMap<String, Integer> eventsCounts = getEventsCounts();
		final HashMap<String, Long> eventsDurations = getEventsDurations();

		// Calculate Totals
		for (Map.Entry<String, Integer> eventEntry : eventsCounts.entrySet())
			countAllEvents += eventEntry.getValue();
		//for (Map.Entry<String, Long> eventEntry : eventsDurations.entrySet())
		//	durationAllEvents += eventEntry.getValue();

		// Sort the Events Hashmap
		List<Map.Entry<String, Long>> mapEntries = new LinkedList<>(eventsDurations.entrySet());
		mapEntries.sort((prev, next) -> {
			long prevDuration = eventsDurations.get(prev.getKey());
			long nextDuration = eventsDurations.get(next.getKey());

			if (prevDuration == nextDuration) {
				int prevCount = eventsCounts.get(prev.getKey());
				int nextCount = eventsCounts.get(next.getKey());

				if (prevCount == nextCount)
					return 0;
				return prevCount < nextCount ? 1 : -1;
			}
			return prevDuration < nextDuration ? 1 : -1;
		});
		eventsDurations.clear();
		//HashMap<String, Long> sortedHashMap = new LinkedHashMap<>();
		for (Map.Entry<String, Long> entry : mapEntries)
			eventsDurations.put(entry.getKey(), entry.getValue());
		//eventsDurations.clear();
		//eventsDurations.putAll(sortedHashMap);

		StringBuilder s = new StringBuilder();
		int idx = 0;
		if (!forInGame) {
			s.append("========================").append(newLine);
			s.append("===     Events       ===").append(newLine);
			s.append("========================").append(newLine);
		}
		for (Map.Entry<String, Long> entry : eventsDurations.entrySet()) {
			// Only display first few elements of the hashmap
			if (forInGame && idx++ >= 15) {
				break;
			}
			final String eventName = entry.getKey();
			final long eventTime = entry.getValue();
			final int eventCount = eventsCounts.get(entry.getKey());
			s.append(eventName).append(" : ")
				.append(eventTime / 1000000).append("ms").append(" : ")
				.append(eventTime / 1000).append("us").append(" : ")
				.append(eventCount).append(newLine);
		}

		if (!forInGame) {
			s.append("========================").append(newLine);
			s.append("=== Incoming Packets ===").append(newLine);
			s.append("========================").append(newLine);
			for (Map.Entry<Integer, Integer> entry : getServer().getIncomingCountPerPacketOpcode().entrySet()) {
				final int incomingPacketId = entry.getKey();
				final int incomingCount = entry.getValue();
				final long incomingTime = getServer().getIncomingTimePerPacketOpcode().get(incomingPacketId);
				s.append("Packet ID: ").append(incomingPacketId).append(" : ")
					.append(incomingTime / 1000000).append("ms").append(" : ")
					.append(incomingTime / 1000).append("us").append(" : ")
					.append(incomingCount).append(newLine);
			}
		}

		// Running GC before grabbing memory usage in order to get the actual used and referenced memory amount.
		System.gc();
		final String totalMemory = DataConversions.formatBytes(Runtime.getRuntime().totalMemory());
		final String freeMemory = DataConversions.formatBytes(Runtime.getRuntime().freeMemory());
		final String usedMemory = DataConversions.formatBytes(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());

		final String returnString = (
			"Tick: " + getServer().getConfig().GAME_TICK + "ms, Server: " + (getServer().getLastTickDuration() / 1000000) + "ms " + (getServer().getLastIncomingPacketsDuration() / 1000000) + "ms " + (getServer().getLastEventsDuration() / 1000000) + "ms " + (getServer().getLastOutgoingPacketsDuration() / 1000000) + "ms" + newLine +
				"Game Updater: " + (getServer().getLastWorldUpdateDuration() / 1000000) + "ms " + (getServer().getLastProcessPlayersDuration() / 1000000) + "ms " + (getServer().getLastProcessNpcsDuration() / 1000000) + "ms " + (getServer().getLastProcessMessageQueuesDuration() / 1000000) + "ms " + (getServer().getLastUpdateClientsDuration() / 1000000) + "ms " + (getServer().getLastDoCleanupDuration() / 1000000) + "ms " + (getServer().getLastExecuteWalkToActionsDuration() / 1000000) + "ms " + newLine +
				"Events: " + countAllEvents + ", NPCs: " + getServer().getWorld().getNpcs().size() + ", Players: " + getServer().getWorld().getPlayers().size() + ", Shops: " + getServer().getWorld().getShops().size() + newLine +
				"Threads: " + Thread.activeCount() + ", Total: " + totalMemory + ", Free: " + freeMemory + ", Used: " + usedMemory + newLine +
				/*"Player Atk Map: " + getWorld().getPlayersUnderAttack().size() + ", NPC Atk Map: " + getWorld().getNpcsUnderAttack().size() + ", Quests: " + getWorld().getQuests().size() + ", Mini Games: " + getWorld().getMiniGames().size() + newLine +*/
				s.toString()
		);

		if (!forInGame) {
			LOGGER.info(returnString);
		}

		return returnString.substring(0, Math.min(returnString.length(), 1999)); // Limit to 2000 characters for Discord.
	}

	public HashMap<String, Integer> getEventsCounts() {
		return new LinkedHashMap<>(eventsCounts);
	}

	public HashMap<String, Long> getEventsDurations() {
		return new LinkedHashMap<>(eventsDurations);
	}

	public List<GameTickEvent> getEvents() {
		return new ArrayList<>(eventStore.getTrackedEvents());
	}

	public boolean hasEvent(Class<? extends GameTickEvent> type) {
		return eventStore.hasEvent(type);
	}

	public Collection<GameTickEvent> getEvents(Class<? extends GameTickEvent> type) {
		return eventStore.getEvents(type);
	}

	public Collection<GameTickEvent> getPlayerEvents(final Player player) {
		return eventStore.getPlayerEvents(player);
	}

	public void remove(final GameTickEvent event) {
		eventStore.remove(event);
	}
}
