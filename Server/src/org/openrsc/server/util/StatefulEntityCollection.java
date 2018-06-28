package org.openrsc.server.util;

import java.util.*;

import org.openrsc.server.model.Entity;

public class StatefulEntityCollection<T extends Entity> {
	private Set<T> newEntities = new HashSet<T>();
	private Set<T> knownEntities = new HashSet<T>();
	private Set<T> entitiesToRemove = new HashSet<T>();

	public void add(T entity) {
		newEntities.add(entity);
	}

	public void add(Collection<T> entities) {
		newEntities.addAll(entities);
	}

	public boolean contains(T entity) {
		return newEntities.contains(entity) || knownEntities.contains(entity);
	}
	public void clear()
	{
		newEntities.clear();
		knownEntities.clear();
		entitiesToRemove.clear();
	}
	public void remove(T entity) {
		entitiesToRemove.add(entity);
	}

	public boolean isRemoving(T entity) {
		return entitiesToRemove.contains(entity);
	}
	
	public void update() {
		knownEntities.removeAll(entitiesToRemove);
		knownEntities.addAll(newEntities);	
		newEntities.clear();
		entitiesToRemove.clear();
	}
	
	public boolean changed() {
		return !entitiesToRemove.isEmpty() || !newEntities.isEmpty();
	}
	
	public Collection<T> getRemovingEntities() {
		return entitiesToRemove;
	}

	public Collection<T> getNewEntities() {
		return newEntities;
	}

	public Collection<T> getKnownEntities() {
		return knownEntities;
	}
	
	public Collection<T> getAllEntities() {
		Set<T> temp = new HashSet<T>();
		temp.addAll(newEntities);
		temp.addAll(knownEntities);
		return temp;
	}									 
}