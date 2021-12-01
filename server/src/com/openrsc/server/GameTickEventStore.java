package com.openrsc.server;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Key;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.player.Player;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

class GameTickEventStore {
    private final Object LOCK = new Object();

    /**
     * Tracks whether the event should be added using the criteria determined by the key
     */
    private final Map<GameTickKey, GameTickEvent> events = new LinkedHashMap<>();

    /**
     * Indexes events by username for fast-lookup during individual player tick processing
     */
    private final Multimap<String, GameTickEvent> byUsername = ArrayListMultimap.create();

    /**
     * Keep the non player events ready
     */
    private final Map<GameTickKey, GameTickEvent> nonPlayerEvents = new LinkedHashMap<>();

    /**
     * Index by event type to quickly know if a certain event type exists (i.e. instanceof)
     */
    private final Multimap<Key<? extends GameTickEvent>, GameTickEvent> byType = LinkedHashMultimap.create();

    public boolean add(GameTickEvent event) {
        synchronized (LOCK) {
            final GameTickKey eventKey = getKey(event);

            if (events.containsKey(eventKey)) {
                // We already have an instance of this event
                return false;
            }

            events.put(eventKey, event);
            byType.put(Key.get(event.getClass()), event);
            if (isPlayerOwner(event)) {
                byUsername.put(((Player) event.getOwner()).getUsername(), event);
            } else {
                nonPlayerEvents.put(eventKey, event);
            }
            return true;
        }
    }

    public void remove(GameTickEvent event) {
        synchronized (LOCK) {
            final GameTickKey eventKey = getKey(event);

            if(!events.containsKey(eventKey)) {
                // Event does not exist
                return;
            }

            events.remove(eventKey);
            byType.remove(Key.get(event.getClass()), event);
            if(isPlayerOwner(event)) {
                byUsername.remove(((Player) event.getOwner()).getUsername(), event);
            } else {
                nonPlayerEvents.remove(eventKey);
            }
        }
    }

    public Collection<GameTickEvent> getPlayerEvents(String username) {
        synchronized (LOCK) {
            return new ArrayList<>(byUsername.get(username));
        }
    }

    public Collection<GameTickEvent> getNonPlayerEvents() {
        synchronized (LOCK) {
            return new ArrayList<>(nonPlayerEvents.values());
        }
    }

    public Collection<GameTickEvent> getEvents(Class<? extends GameTickEvent> type) {
        synchronized (LOCK) {
            return byType.get(Key.get(type));
        }
    }

    public boolean hasEvent(Class<? extends GameTickEvent> eventType) {
        synchronized (LOCK) {
            return byType.containsKey(Key.get(eventType));
        }
    }

    public Collection<GameTickEvent> getTrackedEvents() {
        synchronized (LOCK) {
            return new ArrayList<>(events.values());
        }
    }

    private boolean isPlayerOwner(GameTickEvent event) {
        return event.getOwner() != null && event.getOwner() instanceof Player;
    }

    private GameTickKey getKey(GameTickEvent event) {
        return new GameTickKey(event);
    }

    class GameTickKey {
        private final String className;
        private final Boolean isPlayerEvent;
        private final UUID uuid;

        private GameTickKey(GameTickEvent event) {
            this.className = String.valueOf(event.getClass());
            this.isPlayerEvent = isPlayerOwner(event);
            this.uuid = event.isNotUniqueEvent() ? UUID.randomUUID() : event.getOwner().getUUID();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GameTickKey that = (GameTickKey) o;
            return new EqualsBuilder()
                    .append(className, that.className)
                    .append(isPlayerEvent, that.isPlayerEvent)
                    .append(uuid, that.uuid).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(className)
                    .append(isPlayerEvent)
                    .append(uuid)
                    .toHashCode();
        }
    }
}
