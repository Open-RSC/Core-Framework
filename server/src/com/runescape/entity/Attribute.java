package com.runescape.entity;

public abstract class Attribute<T> {

	protected final T obj;
	
	protected Attribute(T obj) {
		this.obj = obj;
	}
	
}