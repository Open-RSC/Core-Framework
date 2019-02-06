package com.openrsc.server.util;

public interface SimpleSubscriber<E> {
	
	public void update(E ctx);
}
