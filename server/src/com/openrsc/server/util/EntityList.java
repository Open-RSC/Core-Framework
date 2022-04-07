package com.openrsc.server.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.openrsc.server.model.entity.Entity;

import java.text.MessageFormat;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("ConstantConditions")
public class EntityList<T extends Entity> extends AbstractCollection<T> {

    public static final int DEFAULT_CAPACITY = 2000;
    private Queue<Integer> priorityIdPool;
    private ConcurrentHashMap<Integer, Object> occupiedIndices;
    private final int capacity;
    private Object[] entities;

    public EntityList() {
        this(DEFAULT_CAPACITY);
    }

    public EntityList(final int capacity) {
		this.capacity = capacity;
		initialize();
    }

	private void initialize() {
		this.priorityIdPool = IntStream.range(0, capacity)
			.boxed()
			.collect(Collectors.toCollection(() -> new PriorityQueue<>(capacity)));
		this.occupiedIndices = new ConcurrentHashMap<>();
		this.entities = new Object[capacity];
	}

    public synchronized boolean add(final T entity) {
        if (size() >= capacity) {
            throw new IllegalStateException(
                    MessageFormat.format(
                            "Attempt to add entity would exceed capacity of {0}",
                            capacity
                    )
            );
        }

        int nextId = priorityIdPool.poll();
        entities[nextId] = entity;
        entity.setIndex(nextId);
        occupiedIndices.put(nextId, new Object());
        return true;
    }

    public boolean contains(final T entity) {
        return indexOf(entity) > -1;
    }

    public int count() {
        return getOccupiedIndices().size();
    }

    public int size() {
        return getOccupiedIndices().size();
    }

    @SuppressWarnings("unchecked")
    public T get(final int index) {
        if(entities[index] != null) {
            return (T) entities[index];
        }
        return null;
    }

    private int indexOf(final T entity) {
        // Check if the entity at the index provided by the entity matches first
        final int candidateIndex = entity.getIndex();
        if (candidateIndex >= 0 && candidateIndex < capacity) {
            if (entity.equals(entities[candidateIndex])) {
                return candidateIndex;
            }
        }

        // If it wasn't a match, iterate and find it
        for (int index : getOccupiedIndices()) {
            if (entities[index].equals(entity)) {
                return index;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public Iterator<T> iterator() {
        return Arrays.stream(entities)
                .filter(Objects::nonNull)
                .map(entity -> (T) entity)
                .collect(ImmutableList.toImmutableList())
                .iterator();
    }

	public void clear() {
		initialize();
	}

    @SuppressWarnings("unchecked")
    public synchronized T remove(final int index) {
        if(index >= 0) {
            T entity = (T) entities[index];
            if (entity != null) {
				// regression check, the below code was added back
				// to see if removing it was the cause of login 4 for uranium
				entity.setIndex(-1);
			}
            entities[index] = null;
            occupiedIndices.remove(index);
            priorityIdPool.offer(index);
            return entity;
        }
        return null;
    }

    public synchronized void remove(final T entity) {
        final int index = entity.getIndex();
        remove(index);
    }

    private ConcurrentHashMap.KeySetView<Integer, Object> getOccupiedIndices() {
        return occupiedIndices.keySet();
    }

    public Set<Integer> indices() {
        return ImmutableSet.copyOf(getOccupiedIndices());
    }
}
