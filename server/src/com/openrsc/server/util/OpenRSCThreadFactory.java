package com.openrsc.server.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class OpenRSCThreadFactory implements ThreadFactory {

	private final String name;

	private final AtomicInteger threadCount = new AtomicInteger();

	public OpenRSCThreadFactory(String name) {
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, new StringBuilder(name).append("-")
			.append(threadCount.getAndIncrement()).toString());
	}
}
