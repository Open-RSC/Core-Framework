package com.loader.openrsc.core;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class GameEngine {
	private static volatile boolean shutdown;
	private static GameEngine instance;
	private ExecutorService BOSS_EXECUTER;
	private ExecutorService WORKER_EXECUTER;
	private Timer fastExecutor;
	private ScheduledExecutorService slowExecutor;

	private GameEngine() {
		this.bossExecutor(Executors.newSingleThreadExecutor(new DecoderThreadFactory()));
		this.workerExecutor(Executors.newSingleThreadExecutor(new DecoderThreadFactory()));
		this.fastExecutor(new Timer());
		this.slowExecuter(Executors.newSingleThreadScheduledExecutor(new SlowThreadFactory()));
		System.out.println("Started Engine and Executors.");
	}

	public static GameEngine get() {
		if (GameEngine.instance == null) {
			GameEngine.instance = new GameEngine();
		}
		return GameEngine.instance;
	}

	public GameEngine shutdown() {
		this.bossExecutor().shutdown();
		this.workerExecutor().shutdown();
		this.fastExecutor().cancel();
		this.slowExecutor().shutdown();
		GameEngine.shutdown = true;
		return this;
	}

	private ExecutorService bossExecutor() {
		return this.BOSS_EXECUTER;
	}

	private void bossExecutor(final ExecutorService executor) {
		this.BOSS_EXECUTER = executor;
	}

	private ExecutorService workerExecutor() {
		return this.WORKER_EXECUTER;
	}

	private void workerExecutor(final ExecutorService executor) {
		this.WORKER_EXECUTER = executor;
	}

	private Timer fastExecutor() {
		return this.fastExecutor;
	}

	private void fastExecutor(final Timer fastExecutor) {
		this.fastExecutor = fastExecutor;
	}

	private ScheduledExecutorService slowExecutor() {
		return this.slowExecutor;
	}

	private void slowExecuter(final ScheduledExecutorService slowExecuter) {
		this.slowExecutor = slowExecuter;
	}
}
