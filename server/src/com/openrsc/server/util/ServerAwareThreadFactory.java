package com.openrsc.server.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.ServerConfiguration;

import java.util.concurrent.ThreadFactory;

public class ServerAwareThreadFactory implements ThreadFactory {
    private final ThreadFactory threadFactory;
    private final ServerConfiguration configuration;
    public ServerAwareThreadFactory(String nameFormat, ServerConfiguration configuration) {
        threadFactory = new ThreadFactoryBuilder().setNameFormat(nameFormat).build();
        this.configuration = configuration;
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        return threadFactory.newThread(() -> {
            LogUtil.populateThreadContext(configuration);
            runnable.run();
        });
    }
}
