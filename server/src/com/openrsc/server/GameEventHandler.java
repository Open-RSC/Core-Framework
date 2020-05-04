package com.openrsc.server;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.ImmediateEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.NamedThreadFactory;
import com.openrsc.server.util.rsc.DataConversions;
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
		if (event.isUniqueEvent() || !event.hasOwner()) {
			final UUID uuid = UUID.randomUUID();
			eventsToAdd.putIfAbsent(className + uuid, event);
		} else {
			eventsToAdd.putIfAbsent(className + event.getOwner().getUUID() + (event.getOwner().isPlayer() ? "p" : "n"), event);
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
			events.putAll(eventsToAdd);
			eventsToAdd.clear();
		}

		try {
			executor.invokeAll(events.values());
		} catch (Exception e) {
			LOGGER.catching(e);
		}

		eventsCounts.clear();
		eventsDurations.clear();

		events.entrySet().removeIf((eventBox) -> {
			GameTickEvent event = eventBox.getValue();
			eventsCounts.put(event.getDescriptor(),
				eventsCounts.containsKey(event.getDescriptor()) ?
					eventsCounts.get(event.getDescriptor()) + 1 :
					1);
			eventsDurations.put(event.getDescriptor(),
				eventsDurations.containsKey(event.getDescriptor()) ?
					eventsDurations.get(event.getDescriptor()) + event.getLastEventDuration() :
					event.getLastEventDuration());

			return event.shouldRemove();
		});
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
		for (Map.Entry<String, Integer> eventEntry : eventsCounts.entrySet())
			countAllEvents += eventEntry.getValue();
//		for (Map.Entry<String, Long> eventEntry : eventsDurations.entrySet())
//			durationAllEvents += eventEntry.getValue();

		// Sort the Events Hashmap
		List<Map.Entry<String,Long>> mapEntries = new LinkedList<>(eventsDurations.entrySet());
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
//		HashMap<String, Long> sortedHashMap = new LinkedHashMap<>();
		for (Map.Entry<String, Long> entry : mapEntries)
			eventsDurations.put(entry.getKey(), entry.getValue());
//		eventsDurations.clear();
//		eventsDurations.putAll(sortedHashMap);

		StringBuilder s = new StringBuilder();
		int idx = 0;
		for (Map.Entry<String, Long> entry : eventsDurations.entrySet()) {
			if (forInGame && idx++ >= 16) // Only display first 17 elements of the hashmap
				break;
			s.append(entry.getKey()).append(" : ");
			s.append(entry.getValue()).append("ms").append(" : ");
			s.append(eventsCounts.get(entry.getKey())).append(newLine);
		}

		String returnString = (
			"Tick: " + getServer().getConfig().GAME_TICK + "ms, Server: " + getServer().getLastTickDuration() + "ms " + getServer().getLastIncomingPacketsDuration() + "ms " + getServer().getLastEventsDuration() + "ms " + getServer().getLastGameStateDuration() + "ms " + getServer().getLastOutgoingPacketsDuration() + "ms" + newLine +
				"Game Updater: " + getServer().getGameUpdater().getLastProcessPlayersDuration() + "ms " + getServer().getGameUpdater().getLastProcessNpcsDuration() + "ms " + getServer().getGameUpdater().getLastProcessMessageQueuesDuration() + "ms " + getServer().getGameUpdater().getLastUpdateClientsDuration() + "ms " + getServer().getGameUpdater().getLastDoCleanupDuration() + "ms " + getServer().getGameUpdater().getLastExecuteWalkToActionsDuration() + "ms " + newLine +
				"Events: " + countAllEvents + ", NPCs: " + getServer().getWorld().getNpcs().size() + ", Players: " + getServer().getWorld().getPlayers().size() + ", Shops: " + getServer().getWorld().getShops().size() + newLine +
				"Threads: " + Thread.activeCount() + ", Total: " + DataConversions.formatBytes(Runtime.getRuntime().totalMemory()) + ", Free: " +  DataConversions.formatBytes(Runtime.getRuntime().freeMemory()) + ", Used: " + DataConversions.formatBytes(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + newLine +
				/*"Player Atk Map: " + getWorld().getPlayersUnderAttack().size() + ", NPC Atk Map: " + getWorld().getNpcsUnderAttack().size() + ", Quests: " + getWorld().getQuests().size() + ", Mini Games: " + getWorld().getMiniGames().size() + newLine +*/
				s.toString()
		);

		if(!forInGame)
			LOGGER.info(returnString);

		return returnString.substring(0, Math.min(returnString.length(), 1999)); // Limit to 2000 characters for Discord.
	}

	public HashMap<String, GameTickEvent> getEvents() {
		return new LinkedHashMap<>(events);
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
		return new LinkedHashMap<>(eventsCounts);
	}

	public HashMap<String, Long> getEventsDurations() {
		return new LinkedHashMap<>(eventsDurations);
	}

	public final Server getServer() {
		return server;
	}
}
