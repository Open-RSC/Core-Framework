package com.openrsc.server.model;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Player cache
 *
 * @author Peeter
 * <p/>
 * This system enables a programmer to store key/value pairs in the
 * database with ease.
 */
public class Cache {

	/**
	 * Where we store the data.
	 */
	private ConcurrentMap<String, Object> storage = new ConcurrentHashMap<String, Object>();

	public Map<String, Object> getCacheMap() {
		return storage;
	}

	/**
	 * Determines if the selected key already exists in the cache
	 *
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key) {
		return storage.containsKey(key);
	}

	/**
	 * Store an integer in the cache
	 *
	 * @param key
	 * @param i
	 * @throws IllegalArgumentException when the key is already used in the database
	 */
	public void set(String key, int i) {
		storage.put(key, i);
	}

	/**
	 * Store a String in the cache
	 *
	 * @param key
	 * @param s
	 * @throws IllegalArgumentException when the key is already used in the database
	 */
	public void store(String key, String s) {
		storage.put(key, s);
	}

	/**
	 * Store a Boolean in the cache
	 *
	 * @param key
	 * @param b
	 * @throws IllegalArgumentException when the key is already used in the database
	 */
	public void store(String key, Boolean b) {
		storage.put(key, b);
	}

	/**
	 * Store a long in the cache
	 *
	 * @param key
	 * @param l
	 * @throws IllegalArgumentException when the key is already used in the database
	 */
	public void store(String key, long l) {
		storage.put(key, l);
	}

	/**
	 * Get an Integer from the cache
	 *
	 * @param key
	 * @return
	 * @throws NoSuchElementException - When no object is found for the key
	 *                                IllegalArgumentException - When object is found, but is not
	 *                                an Integer
	 */
	public int getInt(String key) {
		if (!storage.containsKey(key))
			throw new NoSuchElementException("No object found for that key");

		Object value = storage.get(key);
		if (!(value instanceof Integer)) {
			throw new IllegalArgumentException(
				"Object found, but not an Integer");
		}
		return (Integer) value;
	}

	/**
	 * Get a String from the cache
	 *
	 * @param key
	 * @return
	 * @throws NoSuchElementException   When no object is found for the key
	 * @throws IllegalArgumentException When object is found, but is not a string
	 */
	public String getString(String key) {
		if (!storage.containsKey(key))
			throw new NoSuchElementException("No object found for that key");
		if (!(storage.get(key) instanceof String)) {
			throw new IllegalArgumentException(
				"Object found, but not an String");
		}
		return (String) (storage.get(key));
	}

	/**
	 * Get a Boolean from the cache
	 *
	 * @param key
	 * @return
	 * @throws NoSuchElementException   When no object is found for the key
	 * @throws IllegalArgumentException When object is found, but is not a Boolean
	 */
	public Boolean getBoolean(String key) {
		if (!storage.containsKey(key))
			throw new NoSuchElementException("No object found for that key");
		if (!(storage.get(key) instanceof Boolean)) {
			throw new IllegalArgumentException(
				"Object found, but not a Boolean");
		}
		return (Boolean) (storage.get(key));
	}

	/**
	 * Get a Long from the cache
	 *
	 * @param key
	 * @return
	 * @throws NoSuchElementException   When no object is found for the key
	 * @throws IllegalArgumentException When object is found, but is not a Long
	 */
	public long getLong(String key) {
		if (!storage.containsKey(key))
			throw new NoSuchElementException("No object found for that key");

		Object value = storage.get(key);
		if (!(value instanceof Long)) {
			throw new IllegalArgumentException("Object found, but not a Long");
		}
		return (Long) value;
	}

	/**
	 * Remove a value from the cache.
	 *
	 * @param key
	 */
	public void remove(String key) {
		storage.remove(key);
	}

	public void remove(String... key) {
		for (String s : key) {
			if (storage.containsKey(s))
				storage.remove(s);
		}
	}

	public void put(String key, Object o) {
		storage.put(key, o);
	}
}
