package com.loader.openrsc.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Timer;
import java.util.concurrent.ExecutorService;

public final class GameEngine
{
    protected static volatile boolean shutdown;
    private static GameEngine instance;
    private ExecutorService BOSS_EXECUTER;
    private ExecutorService WORKER_EXECUTER;
    private Timer fastExecutor;
    private ScheduledExecutorService slowExecutor;
    
    public GameEngine() {
        this.bossExecutor(Executors.newSingleThreadExecutor(new DecoderThreadFactory()));
        this.workerExecutor(Executors.newSingleThreadExecutor(new DecoderThreadFactory()));
        this.fastExecutor(new Timer());
        this.slowExecuter(Executors.newSingleThreadScheduledExecutor(new SlowThreadFactory()));
        System.out.println("Started Engine and Executors.");
    }
    
    public GameEngine shutdown() {
        this.bossExecutor().shutdown();
        this.workerExecutor().shutdown();
        this.fastExecutor().cancel();
        this.slowExecutor().shutdown();
        GameEngine.shutdown = true;
        return this;
    }
    
    public ExecutorService bossExecutor() {
        return this.BOSS_EXECUTER;
    }
    
    public GameEngine bossExecutor(final ExecutorService executor) {
        this.BOSS_EXECUTER = executor;
        return this;
    }
    
    public ExecutorService workerExecutor() {
        return this.WORKER_EXECUTER;
    }
    
    public GameEngine workerExecutor(final ExecutorService executor) {
        this.WORKER_EXECUTER = executor;
        return this;
    }
    
    public Timer fastExecutor() {
        return this.fastExecutor;
    }
    
    public GameEngine fastExecutor(final Timer fastExecutor) {
        this.fastExecutor = fastExecutor;
        return this;
    }
    
    public ScheduledExecutorService slowExecutor() {
        return this.slowExecutor;
    }
    
    public GameEngine slowExecuter(final ScheduledExecutorService slowExecuter) {
        this.slowExecutor = slowExecuter;
        return this;
    }
    
    public static GameEngine get() {
        if (GameEngine.instance == null) {
            GameEngine.instance = new GameEngine();
        }
        return GameEngine.instance;
    }
}
