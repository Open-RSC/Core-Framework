package com.loader.openrsc.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class FastThreadFactory implements ThreadFactory {
	private static final AtomicInteger poolNumber;

	static {
		poolNumber = new AtomicInteger(1);
	}

	private final ThreadGroup group;
	private final AtomicInteger threadNumber;
	private final String namePrefix;

	public FastThreadFactory() {
		this.threadNumber = new AtomicInteger(1);
		final SecurityManager s = System.getSecurityManager();
		this.group = ((s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
		this.namePrefix = "Fast Pool-" + poolNumber.getAndIncrement() + "-thread-";
	}

	@Override
	public Thread newThread(final Runnable r) {
		final Thread t = new Thread(this.group, r, String.valueOf(this.namePrefix) + this.threadNumber.getAndIncrement(), 0L);
		if (t.isDaemon()) {
			t.setDaemon(false);
		}
		if (t.getPriority() != 10) {
			t.setPriority(10);
		}
		return t;
	}
}
